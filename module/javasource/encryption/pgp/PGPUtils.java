package encryption.pgp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.PublicKeyAlgorithmTags;
import org.bouncycastle.bcpg.sig.KeyFlags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPSignatureSubpacketVector;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.bc.BcPGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;

public class PGPUtils {

	private static final int BUFFER_SIZE = 1 << 16; // should always be power of 2
	private static final int KEY_FLAGS = 27;
	private static final int[] MASTER_KEY_CERTIFICATION_TYPES = new int[] {
			PGPSignature.POSITIVE_CERTIFICATION,
			PGPSignature.CASUAL_CERTIFICATION,
			PGPSignature.NO_CERTIFICATION,
			PGPSignature.DEFAULT_CERTIFICATION
	};

	@SuppressWarnings("unchecked")
	public static PGPPublicKey readPublicKey( InputStream in )
			throws IOException, PGPException
	{

		PGPPublicKeyRingCollection keyRingCollection = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(in), new BcKeyFingerprintCalculator());

		//
		// we just loop through the collection till we find a key suitable for encryption, in the real
		// world you would probably want to be a bit smarter about this.
		//
		PGPPublicKey publicKey = null;

		//
		// iterate through the key rings.
		//
		Iterator<PGPPublicKeyRing> rIt = keyRingCollection.getKeyRings();

		while( publicKey == null && rIt.hasNext() ) {
			PGPPublicKeyRing kRing = rIt.next();
			Iterator<PGPPublicKey> kIt = kRing.getPublicKeys();
			while( publicKey == null && kIt.hasNext() ) {
				PGPPublicKey key = kIt.next();
				if ( key.isEncryptionKey() ) {
					publicKey = key;
				}
			}
		}

		if ( publicKey == null ) {
			throw new IllegalArgumentException("Can't find public key in the key ring.");
		}
		if ( !isForEncryption(publicKey) ) {
			throw new IllegalArgumentException("KeyID " + publicKey.getKeyID() + " not flagged for encryption.");
		}

		return publicKey;
	}

	@SuppressWarnings("unchecked")
	public static PGPSecretKey readSecretKey( InputStream in )
			throws IOException, PGPException
	{

		PGPSecretKeyRingCollection keyRingCollection = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(in), new BcKeyFingerprintCalculator());

		//
		// We just loop through the collection till we find a key suitable for signing.
		// In the real world you would probably want to be a bit smarter about this.
		//
		PGPSecretKey secretKey = null;

		Iterator<PGPSecretKeyRing> rIt = keyRingCollection.getKeyRings();
		while( secretKey == null && rIt.hasNext() ) {
			PGPSecretKeyRing keyRing = rIt.next();
			Iterator<PGPSecretKey> kIt = keyRing.getSecretKeys();
			while( secretKey == null && kIt.hasNext() ) {
				PGPSecretKey key = kIt.next();
				if ( key.isSigningKey() ) {
					secretKey = key;
				}
			}
		}

		// Validate secret key
		if ( secretKey == null ) {
			throw new IllegalArgumentException("Can't find private key in the key ring.");
		}
		if ( !secretKey.isSigningKey() ) {
			throw new IllegalArgumentException("Private key does not allow signing.");
		}
		if ( secretKey.getPublicKey().hasRevocation() ) {
			throw new IllegalArgumentException("Private key has been revoked.");
		}
		if ( !hasKeyFlags(secretKey.getPublicKey(), KeyFlags.SIGN_DATA) ) {
			throw new IllegalArgumentException("Key cannot be used for signing.");
		}

		return secretKey;
	}

	/**
	 * Load a secret key ring collection from keyIn and find the private key corresponding to
	 * keyID if it exists.
	 *
	 * @param keyIn input stream representing a key ring collection.
	 * @param keyID keyID we want.
	 * @param pass passphrase to decrypt secret key with.
	 * @return
	 * @throws IOException
	 * @throws PGPException
	 * @throws NoSuchProviderException
	 */
	public static PGPPrivateKey findPrivateKey( InputStream keyIn, long keyID, char[] pass )
			throws IOException, PGPException, NoSuchProviderException
	{
		PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(keyIn), new BcKeyFingerprintCalculator());
		return findPrivateKey(pgpSec.getSecretKey(keyID), pass);

	}

	/**
	 * Load a secret key and find the private key in it
	 *
	 * @param pgpSecKey The secret key
	 * @param pass passphrase to decrypt secret key with
	 * @return
	 * @throws PGPException
	 */
	public static PGPPrivateKey findPrivateKey( PGPSecretKey pgpSecKey, char[] pass )
			throws PGPException
	{
		if ( pgpSecKey == null )
			return null;

		PBESecretKeyDecryptor decryptor = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider()).build(pass);
		return pgpSecKey.extractPrivateKey(decryptor);
	}

	/**
	 * decrypt the passed in message stream
	 */
	@SuppressWarnings("unchecked")
	public static void decryptFile( InputStream in, OutputStream out, InputStream keyIn, char[] passwd )
			throws Exception
	{
		Security.addProvider(new BouncyCastleProvider());

		in = org.bouncycastle.openpgp.PGPUtil.getDecoderStream(in);

		PGPObjectFactory pgpF = new PGPObjectFactory(in, new BcKeyFingerprintCalculator());
		PGPEncryptedDataList enc;

		Object o = pgpF.nextObject();
		//
		// the first object might be a PGP marker packet.
		//
		if ( o instanceof PGPEncryptedDataList ) {
			enc = (PGPEncryptedDataList) o;
		}
		else {
			enc = (PGPEncryptedDataList) pgpF.nextObject();
		}

		//
		// find the secret key
		//
		Iterator<PGPEncryptedData> it = enc.getEncryptedDataObjects();
		PGPPrivateKey sKey = null;
		PGPPublicKeyEncryptedData pbe = null;

		while( sKey == null && it.hasNext() ) {
			PGPEncryptedData data = it.next();
			if (data instanceof PGPPublicKeyEncryptedData) {
				pbe = (PGPPublicKeyEncryptedData) data;

				sKey = findPrivateKey(keyIn, pbe.getKeyID(), passwd);
			}
		}

		if ( sKey == null ) {
			throw new IllegalArgumentException("Secret key for message not found. (Looking for key:" + pbe.getKeyID() + ")");
		}

		InputStream clear = pbe.getDataStream(new BcPublicKeyDataDecryptorFactory(sKey));

		PGPObjectFactory plainFact = new PGPObjectFactory(clear, new BcKeyFingerprintCalculator());

		Object message = plainFact.nextObject();

		if ( message instanceof PGPCompressedData ) {
			PGPCompressedData cData = (PGPCompressedData) message;
			PGPObjectFactory pgpFact = new PGPObjectFactory(cData.getDataStream(), new BcKeyFingerprintCalculator());

			message = pgpFact.nextObject();
		}

		if ( message instanceof PGPLiteralData ) {
			PGPLiteralData ld = (PGPLiteralData) message;

			InputStream unc = ld.getInputStream();
			int ch;

			while( (ch = unc.read()) >= 0 ) {
				out.write(ch);
			}
		}
		else if ( message instanceof PGPOnePassSignatureList ) {
			throw new PGPException("Encrypted message contains a signed message - not literal data.");
		}
		else {
			throw new PGPException("Message is not a simple encrypted file - type unknown.");
		}

		if ( pbe.isIntegrityProtected() ) {
			if ( !pbe.verify() ) {
				throw new PGPException("Message failed integrity check");
			}
		}
	}

	public static void encryptFile(
			OutputStream out,
			String fileName,
			PGPPublicKey encKey,
			boolean armor,
			boolean withIntegrityCheck )
			throws IOException, NoSuchProviderException, PGPException
	{
		Security.addProvider(new BouncyCastleProvider());

		if ( armor ) {
			out = new ArmoredOutputStream(out);
		}

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(PGPCompressedData.ZIP);

		PGPUtil.writeFileToLiteralData(
				comData.open(bOut),
				PGPLiteralData.BINARY,
				new File(fileName));

		comData.close();

		BcPGPDataEncryptorBuilder dataEncryptor = new BcPGPDataEncryptorBuilder(PGPEncryptedData.TRIPLE_DES);
		dataEncryptor.setWithIntegrityPacket(withIntegrityCheck);
		dataEncryptor.setSecureRandom(new SecureRandom());

		PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(dataEncryptor);
		encryptedDataGenerator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(encKey));

		byte[] bytes = bOut.toByteArray();
		OutputStream cOut = encryptedDataGenerator.open(out, bytes.length);
		cOut.write(bytes);
		cOut.close();

		encryptedDataGenerator.close();
		bOut.close();


		if( armor ) {
			out.close();
		}
	}

	@SuppressWarnings("unchecked")
	public static void signEncryptFile(
			OutputStream out,
			String fileName,
			PGPPublicKey publicKey,
			PGPSecretKey secretKey,
			String password,
			boolean armor,
			boolean withIntegrityCheck )
			throws Exception
	{

		// Initialize Bouncy Castle security provider
		Provider provider = new BouncyCastleProvider();
		Security.addProvider(provider);

		if ( armor ) {
			out = new ArmoredOutputStream(out);
		}

		BcPGPDataEncryptorBuilder dataEncryptor = new BcPGPDataEncryptorBuilder(PGPEncryptedData.TRIPLE_DES);
		dataEncryptor.setWithIntegrityPacket(withIntegrityCheck);
		dataEncryptor.setSecureRandom(new SecureRandom());

		PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(dataEncryptor);
		encryptedDataGenerator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(publicKey));

		OutputStream encryptedOut = encryptedDataGenerator.open(out, new byte[PGPUtils.BUFFER_SIZE]);

		// Initialize compressed data generator
		PGPCompressedDataGenerator compressedDataGenerator = new PGPCompressedDataGenerator(PGPCompressedData.ZIP);
		OutputStream compressedOut = compressedDataGenerator.open(encryptedOut, new byte[PGPUtils.BUFFER_SIZE]);

		// Initialize signature generator
		PGPPrivateKey privateKey = findPrivateKey(secretKey, password.toCharArray());

		PGPContentSignerBuilder signerBuilder = new BcPGPContentSignerBuilder(secretKey.getPublicKey().getAlgorithm(),
				HashAlgorithmTags.SHA1);

		PGPSignatureGenerator signatureGenerator = new PGPSignatureGenerator(signerBuilder);
		signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, privateKey);

		boolean firstTime = true;
		Iterator<String> it = secretKey.getPublicKey().getUserIDs();
		while( it.hasNext() && firstTime ) {
			PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();
			spGen.addSignerUserID(false, it.next());
			signatureGenerator.setHashedSubpackets(spGen.generate());
			// Exit the loop after the first iteration
			firstTime = false;
		}
		signatureGenerator.generateOnePassVersion(false).encode(compressedOut);

		// Initialize literal data generator
		PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
		OutputStream literalOut = literalDataGenerator.open(
				compressedOut,
				PGPLiteralData.BINARY,
				fileName,
				new Date(),
				new byte[PGPUtils.BUFFER_SIZE]);

		// Main loop - read the "in" stream, compress, encrypt and write to the "out" stream
		FileInputStream in = new FileInputStream(fileName);
		byte[] buf = new byte[PGPUtils.BUFFER_SIZE];
		int len;
		while( (len = in.read(buf)) > 0 ) {
			literalOut.write(buf, 0, len);
			signatureGenerator.update(buf, 0, len);
		}

		literalOut.close();
		in.close();
		literalDataGenerator.close();
		// Generate the signature, compress, encrypt and write to the "out" stream
		signatureGenerator.generate().encode(compressedOut);
		compressedDataGenerator.close();
		encryptedDataGenerator.close();
		if ( armor ) {
			out.close();
		}
	}

	public static boolean verifyFile(
			InputStream in,
			InputStream keyIn,
			String extractContentFile )
			throws Exception
	{
		in = PGPUtil.getDecoderStream(in);

		PGPObjectFactory pgpFact = new PGPObjectFactory(in, new BcKeyFingerprintCalculator());
		PGPCompressedData c1 = (PGPCompressedData) pgpFact.nextObject();

		pgpFact = new PGPObjectFactory(c1.getDataStream(), new BcKeyFingerprintCalculator());

		PGPOnePassSignatureList p1 = (PGPOnePassSignatureList) pgpFact.nextObject();

		PGPOnePassSignature ops = p1.get(0);

		PGPLiteralData p2 = (PGPLiteralData) pgpFact.nextObject();

		InputStream dIn = p2.getInputStream();

		IOUtils.copy(dIn, new FileOutputStream(extractContentFile));

		int ch;
		PGPPublicKeyRingCollection pgpRing = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(keyIn), new BcKeyFingerprintCalculator());

		PGPPublicKey key = pgpRing.getPublicKey(ops.getKeyID());

		FileOutputStream out = new FileOutputStream(p2.getFileName());

		ops.init(new BcPGPContentVerifierBuilderProvider(), key);

		while( (ch = dIn.read()) >= 0 )
		{
			ops.update((byte) ch);
			out.write(ch);
		}

		out.close();

		PGPSignatureList p3 = (PGPSignatureList) pgpFact.nextObject();
		return ops.verify(p3.get(0));
	}

	/**
	 * From LockBox Lobs PGP Encryption tools.
	 * http://www.lockboxlabs.org/content/downloads
	 *
	 * I didn't think it was worth having to import a 4meg lib for three methods
	 *
	 * @param key
	 * @return
	 */
	public static boolean isForEncryption( PGPPublicKey key )
	{
		if ( key.getAlgorithm() == PublicKeyAlgorithmTags.RSA_SIGN
				|| key.getAlgorithm() == PublicKeyAlgorithmTags.DSA
				|| key.getAlgorithm() == PublicKeyAlgorithmTags.ECDH
				|| key.getAlgorithm() == PublicKeyAlgorithmTags.ECDSA )
		{
			return false;
		}

		return hasKeyFlags(key, KeyFlags.ENCRYPT_COMMS | KeyFlags.ENCRYPT_STORAGE);
	}

	/**
	 * From LockBox Lobs PGP Encryption tools.
	 * http://www.lockboxlabs.org/content/downloads
	 *
	 * I didn't think it was worth having to import a 4meg lib for three methods
	 *
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static boolean hasKeyFlags( PGPPublicKey encKey, int keyUsage ) {
		if ( encKey.isMasterKey() ) {
			for( int i = 0; i != PGPUtils.MASTER_KEY_CERTIFICATION_TYPES.length; i++ ) {
				for( Iterator<PGPSignature> eIt = encKey.getSignaturesOfType(PGPUtils.MASTER_KEY_CERTIFICATION_TYPES[i]); eIt.hasNext(); ) {
					PGPSignature sig = eIt.next();
					if ( !isMatchingUsage(sig, keyUsage) ) {
						return false;
					}
				}
			}
		}
		else {
			for( Iterator<PGPSignature> eIt = encKey.getSignaturesOfType(PGPSignature.SUBKEY_BINDING); eIt.hasNext(); ) {
				PGPSignature sig = eIt.next();
				if ( !isMatchingUsage(sig, keyUsage) ) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * From LockBox Lobs PGP Encryption tools.
	 * http://www.lockboxlabs.org/content/downloads
	 *
	 * I didn't think it was worth having to import a 4meg lib for three methods
	 *
	 * @param key
	 * @return
	 */
	private static boolean isMatchingUsage( PGPSignature sig, int keyUsage ) {
		if ( sig.hasSubpackets() ) {
			PGPSignatureSubpacketVector sv = sig.getHashedSubPackets();
			if ( sv.hasSubpacket(PGPUtils.KEY_FLAGS) ) {
				if ( (sv.getKeyFlags() == 0 && keyUsage == 0) ) {
					return false;
				}
			}
		}
		return true;
	}

}
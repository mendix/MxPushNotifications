package encryption.pgp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;

import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;

public class PGPFileProcessor {

	private String passphrase;
	private IMendixObject publicKeyFileDocument;
	private IMendixObject secretKeyFileDocument;
	private IMendixObject inputFileDocument;
	private IMendixObject outputFileDocument;
	private boolean asciiArmored = false;
	private boolean integrityCheck = true;

	/**
	 * Encrypt the InputDocument.
	 * 
	 * This function requires the publicKey, OutputFile and InputFile to be specified.
	 *
	 * @return true or exception
	 */
	public boolean encrypt( IContext context ) throws Exception {

		if ( this.publicKeyFileDocument == null )
			throw new CoreException("Please provide a public key document");
		if ( this.outputFileDocument == null )
			throw new CoreException("Please provide an output document");

		try (InputStream publicKeyIn = Core.getFileDocumentContent(context, this.publicKeyFileDocument)) {

			String tempOutputFile = getNewTempFile("out");
			try (FileOutputStream out = new FileOutputStream(tempOutputFile)) {

				File tmpIn = writeInputDocumentToTempFile(context);

				PGPUtils.encryptFile(out, tmpIn.getAbsolutePath(), PGPUtils.readPublicKey(publicKeyIn), this.isAsciiArmored(), this.isIntegrityCheck());

				tmpIn.delete();
			}

			storeOutput(context, tempOutputFile);
		}

		return true;
	}

	/**
	 * Encrypt and sign the InputDocument.
	 * 
	 * This function requires the publicKey, privateKey (for signing), OutputFile and InputFile to be specified.
	 *
	 * @return true or exception
	 */
	public boolean signEncrypt( IContext context ) throws Exception {

		if ( this.publicKeyFileDocument == null )
			throw new CoreException("Please provide a public key document");
		if ( this.secretKeyFileDocument == null )
			throw new CoreException("Please provide a secret key document");
		if ( this.passphrase == null )
			throw new CoreException("Please provide a pass phrase");
		if ( this.outputFileDocument == null )
			throw new CoreException("Please provide an output document");

		String tempOutputFile = getNewTempFile("out");
		try (FileOutputStream out = new FileOutputStream(tempOutputFile)) {
			try (InputStream publicKeyIn = Core.getFileDocumentContent(context, this.publicKeyFileDocument)) {
				try (InputStream secretKeyIn = Core.getFileDocumentContent(context, this.secretKeyFileDocument)) {

					PGPPublicKey publicKey = PGPUtils.readPublicKey(publicKeyIn);
					PGPSecretKey secretKey = PGPUtils.readSecretKey(secretKeyIn);

					File tmpIn = writeInputDocumentToTempFile(context);

					PGPUtils.signEncryptFile(
							out,
							tmpIn.getAbsolutePath(),
							publicKey,
							secretKey,
							this.getPassphrase(),
							this.isAsciiArmored(),
							this.isIntegrityCheck());

					storeOutput(context, tempOutputFile);
				}
			}
		}
		finally {
			new File(tempOutputFile).delete();
		}

		return true;
	}

	public boolean decrypt( IContext context ) throws Exception {
		try (InputStream in = Core.getFileDocumentContent(context, this.inputFileDocument)) {
			try (InputStream keyIn = Core.getFileDocumentContent(context, this.secretKeyFileDocument)) {

				String tempOutputFile = getNewTempFile("out");
				try (FileOutputStream out = new FileOutputStream(tempOutputFile)) {
					PGPUtils.decryptFile(in, out, keyIn, this.passphrase.toCharArray());

					storeOutput(context, tempOutputFile);
				}
				finally {
					new File(tempOutputFile).delete();
				}
			}
		}

		return true;
	}

	/**
	 * Generate a new random file name in the Mx Temp folder.
	 *
	 * @return full path to a file in deployment/data/tmp/
	 */
	public static String getNewTempFile( String suffix ) {
		return Core.getConfiguration().getTempPath().getAbsolutePath() + File.separator + UUID.randomUUID() + "-" + suffix + ".pgp";
	}

	/** Evaluate and store the content of the output file in the output file document (with the same name as input file). */
	private void storeOutput( IContext context, String tempOutputFile ) throws IOException {
		try (FileInputStream in = new FileInputStream(tempOutputFile)) {
			Core.storeFileDocumentContent(context, this.outputFileDocument, this.inputFileDocument.getValue(context, "Name"), in);
		}
	}

	private File writeInputDocumentToTempFile( IContext context ) throws IOException
	{
		File tmpFile = new File(getNewTempFile("in"));
		try (FileOutputStream outStream = new FileOutputStream(tmpFile)) {
			try (InputStream inStream = Core.getFileDocumentContent(context, this.inputFileDocument)) {

				int content;
				while( (content = inStream.read()) != -1 )
				{
					outStream.write(content);
				}
			}
		}

		return tmpFile;
	}

	/** Should we use ASCII encoded output for the encryption or decryption action. */
	public boolean isAsciiArmored() {
		return this.asciiArmored;
	}

	/** Should we use ASCII encoded output for the encryption or decryption action. */
	public void setAsciiArmored( boolean asciiArmored ) {
		this.asciiArmored = asciiArmored;
	}

	public boolean isIntegrityCheck() {
		return this.integrityCheck;
	}

	public void setIntegrityCheck( boolean integrityCheck ) {
		this.integrityCheck = integrityCheck;
	}

	public String getPassphrase() {
		return this.passphrase;
	}

	public void setPassphrase( String passphrase ) {
		this.passphrase = passphrase;
	}

	public IMendixObject getPublicKeyFileName() {
		return this.publicKeyFileDocument;
	}

	public void setPublicKeyFileName( IMendixObject publicKeyFileDocument ) {
		this.publicKeyFileDocument = publicKeyFileDocument;
	}

	public IMendixObject getSecretKeyFileName() {
		return this.secretKeyFileDocument;
	}

	public void setSecretKeyFileName( IMendixObject secretKeyFileDocument ) {
		this.secretKeyFileDocument = secretKeyFileDocument;
	}

	public void setInputFileDocument( IMendixObject inputFileDocument ) {
		this.inputFileDocument = inputFileDocument;
	}

	public void setOutputFileDocument( IMendixObject outputFileDocument ) {
		this.outputFileDocument = outputFileDocument;
	}
}
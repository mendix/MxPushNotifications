// This file was generated by Mendix Modeler.
//
// WARNING: Code you write here will be lost the next time you deploy the project.

package encryption.proxies;

/**
 * This entity is used to hold the public and secret key ring for PGP encryption and decryption
 */
public class PGPCertificate extends system.proxies.FileDocument
{
	/**
	 * Internal name of this entity
	 */
	public static final java.lang.String entityName = "Encryption.PGPCertificate";

	/**
	 * Enum describing members of this entity
	 */
	public enum MemberNames
	{
		CertificateType("CertificateType"),
		PassPhrase_Plain("PassPhrase_Plain"),
		PassPhrase_Encrypted("PassPhrase_Encrypted"),
		Reference("Reference"),
		EmailAddress("EmailAddress"),
		FileID("FileID"),
		Name("Name"),
		DeleteAfterDownload("DeleteAfterDownload"),
		Contents("Contents"),
		HasContents("HasContents"),
		SecretKey_PublicKey("Encryption.SecretKey_PublicKey");

		private java.lang.String metaName;

		MemberNames(java.lang.String s)
		{
			metaName = s;
		}

		@Override
		public java.lang.String toString()
		{
			return metaName;
		}
	}

	public PGPCertificate(com.mendix.systemwideinterfaces.core.IContext context)
	{
		this(context, com.mendix.core.Core.instantiate(context, "Encryption.PGPCertificate"));
	}

	protected PGPCertificate(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixObject pGPCertificateMendixObject)
	{
		super(context, pGPCertificateMendixObject);
		if (!com.mendix.core.Core.isSubClassOf("Encryption.PGPCertificate", pGPCertificateMendixObject.getType()))
			throw new java.lang.IllegalArgumentException("The given object is not a Encryption.PGPCertificate");
	}

	/**
	 * @deprecated Use 'PGPCertificate.load(IContext, IMendixIdentifier)' instead.
	 */
	@Deprecated
	public static encryption.proxies.PGPCertificate initialize(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixIdentifier mendixIdentifier) throws com.mendix.core.CoreException
	{
		return encryption.proxies.PGPCertificate.load(context, mendixIdentifier);
	}

	/**
	 * Initialize a proxy using context (recommended). This context will be used for security checking when the get- and set-methods without context parameters are called.
	 * The get- and set-methods with context parameter should be used when for instance sudo access is necessary (IContext.getSudoContext() can be used to obtain sudo access).
	 */
	public static encryption.proxies.PGPCertificate initialize(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixObject mendixObject)
	{
		return new encryption.proxies.PGPCertificate(context, mendixObject);
	}

	public static encryption.proxies.PGPCertificate load(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixIdentifier mendixIdentifier) throws com.mendix.core.CoreException
	{
		com.mendix.systemwideinterfaces.core.IMendixObject mendixObject = com.mendix.core.Core.retrieveId(context, mendixIdentifier);
		return encryption.proxies.PGPCertificate.initialize(context, mendixObject);
	}

	public static java.util.List<encryption.proxies.PGPCertificate> load(com.mendix.systemwideinterfaces.core.IContext context, java.lang.String xpathConstraint) throws com.mendix.core.CoreException
	{
		java.util.List<encryption.proxies.PGPCertificate> result = new java.util.ArrayList<encryption.proxies.PGPCertificate>();
		for (com.mendix.systemwideinterfaces.core.IMendixObject obj : com.mendix.core.Core.retrieveXPathQuery(context, "//Encryption.PGPCertificate" + xpathConstraint))
			result.add(encryption.proxies.PGPCertificate.initialize(context, obj));
		return result;
	}

	/**
	 * Set value of CertificateType
	 * @param certificatetype
	 */
	public final encryption.proxies.CertificateType getCertificateType()
	{
		return getCertificateType(getContext());
	}

	/**
	 * @param context
	 * @return value of CertificateType
	 */
	public final encryption.proxies.CertificateType getCertificateType(com.mendix.systemwideinterfaces.core.IContext context)
	{
		Object obj = getMendixObject().getValue(context, MemberNames.CertificateType.toString());
		if (obj == null)
			return null;

		return encryption.proxies.CertificateType.valueOf((java.lang.String) obj);
	}

	/**
	 * Set value of CertificateType
	 * @param certificatetype
	 */
	public final void setCertificateType(encryption.proxies.CertificateType certificatetype)
	{
		setCertificateType(getContext(), certificatetype);
	}

	/**
	 * Set value of CertificateType
	 * @param context
	 * @param certificatetype
	 */
	public final void setCertificateType(com.mendix.systemwideinterfaces.core.IContext context, encryption.proxies.CertificateType certificatetype)
	{
		if (certificatetype != null)
			getMendixObject().setValue(context, MemberNames.CertificateType.toString(), certificatetype.toString());
		else
			getMendixObject().setValue(context, MemberNames.CertificateType.toString(), null);
	}

	/**
	 * @return value of PassPhrase_Plain
	 */
	public final String getPassPhrase_Plain()
	{
		return getPassPhrase_Plain(getContext());
	}

	/**
	 * @param context
	 * @return value of PassPhrase_Plain
	 */
	public final String getPassPhrase_Plain(com.mendix.systemwideinterfaces.core.IContext context)
	{
		return (String) getMendixObject().getValue(context, MemberNames.PassPhrase_Plain.toString());
	}

	/**
	 * Set value of PassPhrase_Plain
	 * @param passphrase_plain
	 */
	public final void setPassPhrase_Plain(String passphrase_plain)
	{
		setPassPhrase_Plain(getContext(), passphrase_plain);
	}

	/**
	 * Set value of PassPhrase_Plain
	 * @param context
	 * @param passphrase_plain
	 */
	public final void setPassPhrase_Plain(com.mendix.systemwideinterfaces.core.IContext context, String passphrase_plain)
	{
		getMendixObject().setValue(context, MemberNames.PassPhrase_Plain.toString(), passphrase_plain);
	}

	/**
	 * @return value of PassPhrase_Encrypted
	 */
	public final String getPassPhrase_Encrypted()
	{
		return getPassPhrase_Encrypted(getContext());
	}

	/**
	 * @param context
	 * @return value of PassPhrase_Encrypted
	 */
	public final String getPassPhrase_Encrypted(com.mendix.systemwideinterfaces.core.IContext context)
	{
		return (String) getMendixObject().getValue(context, MemberNames.PassPhrase_Encrypted.toString());
	}

	/**
	 * Set value of PassPhrase_Encrypted
	 * @param passphrase_encrypted
	 */
	public final void setPassPhrase_Encrypted(String passphrase_encrypted)
	{
		setPassPhrase_Encrypted(getContext(), passphrase_encrypted);
	}

	/**
	 * Set value of PassPhrase_Encrypted
	 * @param context
	 * @param passphrase_encrypted
	 */
	public final void setPassPhrase_Encrypted(com.mendix.systemwideinterfaces.core.IContext context, String passphrase_encrypted)
	{
		getMendixObject().setValue(context, MemberNames.PassPhrase_Encrypted.toString(), passphrase_encrypted);
	}

	/**
	 * @return value of Reference
	 */
	public final String getReference()
	{
		return getReference(getContext());
	}

	/**
	 * @param context
	 * @return value of Reference
	 */
	public final String getReference(com.mendix.systemwideinterfaces.core.IContext context)
	{
		return (String) getMendixObject().getValue(context, MemberNames.Reference.toString());
	}

	/**
	 * Set value of Reference
	 * @param reference
	 */
	public final void setReference(String reference)
	{
		setReference(getContext(), reference);
	}

	/**
	 * Set value of Reference
	 * @param context
	 * @param reference
	 */
	public final void setReference(com.mendix.systemwideinterfaces.core.IContext context, String reference)
	{
		getMendixObject().setValue(context, MemberNames.Reference.toString(), reference);
	}

	/**
	 * @return value of EmailAddress
	 */
	public final String getEmailAddress()
	{
		return getEmailAddress(getContext());
	}

	/**
	 * @param context
	 * @return value of EmailAddress
	 */
	public final String getEmailAddress(com.mendix.systemwideinterfaces.core.IContext context)
	{
		return (String) getMendixObject().getValue(context, MemberNames.EmailAddress.toString());
	}

	/**
	 * Set value of EmailAddress
	 * @param emailaddress
	 */
	public final void setEmailAddress(String emailaddress)
	{
		setEmailAddress(getContext(), emailaddress);
	}

	/**
	 * Set value of EmailAddress
	 * @param context
	 * @param emailaddress
	 */
	public final void setEmailAddress(com.mendix.systemwideinterfaces.core.IContext context, String emailaddress)
	{
		getMendixObject().setValue(context, MemberNames.EmailAddress.toString(), emailaddress);
	}

	/**
	 * @return value of SecretKey_PublicKey
	 */
	public final encryption.proxies.PGPCertificate getSecretKey_PublicKey() throws com.mendix.core.CoreException
	{
		return getSecretKey_PublicKey(getContext());
	}

	/**
	 * @param context
	 * @return value of SecretKey_PublicKey
	 */
	public final encryption.proxies.PGPCertificate getSecretKey_PublicKey(com.mendix.systemwideinterfaces.core.IContext context) throws com.mendix.core.CoreException
	{
		encryption.proxies.PGPCertificate result = null;
		com.mendix.systemwideinterfaces.core.IMendixIdentifier identifier = getMendixObject().getValue(context, MemberNames.SecretKey_PublicKey.toString());
		if (identifier != null)
			result = encryption.proxies.PGPCertificate.load(context, identifier);
		return result;
	}

	/**
	 * Set value of SecretKey_PublicKey
	 * @param secretkey_publickey
	 */
	public final void setSecretKey_PublicKey(encryption.proxies.PGPCertificate secretkey_publickey)
	{
		setSecretKey_PublicKey(getContext(), secretkey_publickey);
	}

	/**
	 * Set value of SecretKey_PublicKey
	 * @param context
	 * @param secretkey_publickey
	 */
	public final void setSecretKey_PublicKey(com.mendix.systemwideinterfaces.core.IContext context, encryption.proxies.PGPCertificate secretkey_publickey)
	{
		if (secretkey_publickey == null)
			getMendixObject().setValue(context, MemberNames.SecretKey_PublicKey.toString(), null);
		else
			getMendixObject().setValue(context, MemberNames.SecretKey_PublicKey.toString(), secretkey_publickey.getMendixObject().getId());
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;

		if (obj != null && getClass().equals(obj.getClass()))
		{
			final encryption.proxies.PGPCertificate that = (encryption.proxies.PGPCertificate) obj;
			return getMendixObject().equals(that.getMendixObject());
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return getMendixObject().hashCode();
	}

	/**
	 * @return String name of this class
	 */
	public static java.lang.String getType()
	{
		return "Encryption.PGPCertificate";
	}

	/**
	 * @return String GUID from this object, format: ID_0000000000
	 * @deprecated Use getMendixObject().getId().toLong() to get a unique identifier for this object.
	 */
	@Override
	@Deprecated
	public java.lang.String getGUID()
	{
		return "ID_" + getMendixObject().getId().toLong();
	}
}
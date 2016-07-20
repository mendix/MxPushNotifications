package encryption.pgp;


public class PGPTester {

	private static final String PASSPHRASE = "test";

	private static final String DE_INPUT = "src/test/x.pgp";
	private static final String DE_OUTPUT = "src/test/x.txt";
	private static final String DE_KEY_FILE = "src/test/secring.skr";

	private static final String E_INPUT = "src/test/x.txt";
	private static final String E_OUTPUT = "src/test/x.pgp";
	private static final String E_KEY_FILE = "src/test/pubring.pkr";

	public static void testDecrypt() throws Exception {
//		PGPFileProcessor p = new PGPFileProcessor();
//		p.setInputFileName(DE_INPUT);
//		p.setOutputFileName(DE_OUTPUT);
//		p.setPassphrase(PASSPHRASE);
//		p.setSecretKeyFileName(DE_KEY_FILE);
//		System.out.println(p.decrypt());
	}

	public static void testEncrypt() throws Exception {
//		PGPFileProcessor p = new PGPFileProcessor();
//		p.setInputFileName(E_INPUT);
//		p.setOutputFileName(E_OUTPUT);
//		p.setPassphrase(PASSPHRASE);
//		p.setPublicKeyFileName(E_KEY_FILE);
//		System.out.println(p.encrypt());
	}
}
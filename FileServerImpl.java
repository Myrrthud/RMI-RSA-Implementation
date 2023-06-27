import java.io.File;
import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.cert.CertificateFactory;
import java.security.cert.Certificate;
import java.io.InputStream;

public class FileServerImpl extends UnicastRemoteObject implements FileServerInterface {
    private static final String FILE_DIRECTORY = "C:\\Users\\user\\Documents\\G2\\G2S";
    private PrivateKey privateKey;

    protected FileServerImpl() throws RemoteException {
        super();
        privateKey = loadPrivateKey(); // Load the private key from the keystore
    }

    private PrivateKey loadPrivateKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream("keystore.p12");
            char[] password = "Group2".toCharArray();
            keyStore.load(fis, password);
            fis.close();

            return (PrivateKey) keyStore.getKey("alias", password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void authenticate(PublicKey publicKey, byte[] encryptedKey, byte[] signature, PrivateKey privateKey) throws RemoteException {
        try {
            // Verify digital signature
            Signature verifySignature = Signature.getInstance("SHA256withRSA");
            verifySignature.initVerify(publicKey);
            verifySignature.update(encryptedKey);
            boolean signatureValid = verifySignature.verify(signature);

            if (signatureValid) {
                // Decrypt the secret key with the private key
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] decryptedKey = cipher.doFinal(encryptedKey);

                // Use the decrypted secret key for further communication
                SecretKey secretKey = new SecretKeySpec(decryptedKey, "AES");
                // Perform additional authentication or authorization checks here
            } else {
                throw new RemoteException("Invalid digital signature. Client authentication failed.");
            }
        } catch (Exception e) {
            throw new RemoteException("Client authentication failed.", e);
        }
    }

    @Override
    public byte[] getFile(String filename) throws RemoteException {
        File file = new File(FILE_DIRECTORY, filename);

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] fileData = new byte[(int) file.length()];
            fis.read(fileData);
            return fileData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

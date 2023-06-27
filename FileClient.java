import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.security.*;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.io.FileInputStream;

public class FileClient {
    private static final String TRUSTSTORE_PATH = "C:\\Users\\user\\Documents\\G2\\G2S\\truststore.jks"; //path to truststore.jks
    private static final String TRUSTSTORE_PASSWORD = "Group2"; //truststore password

    public static void main(String[] args) {
        try {
            // Set the truststore for secure communication
            System.setProperty("javax.net.ssl.trustStore", TRUSTSTORE_PATH);
            System.setProperty("javax.net.ssl.trustStorePassword", TRUSTSTORE_PASSWORD);

            // Connect to the remote server
            FileServerInterface fileServer = (FileServerInterface) Naming.lookup("rmi://localhost/FileServer");

            // Generate a key pair for RSA encryption
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();



            // Generate a symmetric key for file encryption
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();

        

            // Encrypt the secret key with the server's public key
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedKey = cipher.doFinal(secretKey.getEncoded());

        

            // Generate a digital signature
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(encryptedKey);
            byte[] digitalSignature = signature.sign();


            // Perform client authentication
            fileServer.authenticate(publicKey, encryptedKey, digitalSignature, privateKey);

            // Retrieve the file from the server
            String filename = "file.txt";
            byte[] fileData = fileServer.getFile(filename);

            if (fileData != null) {
                // Save the file to disk
                Path outputPath = Paths.get("output.txt");
                Files.write(outputPath, fileData);

                System.out.println("File retrieved and saved as output.txt");
            } else {
                System.out.println("File not found on the server.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

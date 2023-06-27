import java.io.FileInputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class FileServer {
    public static void main(String[] args) {
        try {
            // Load the certificate from the file
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            FileInputStream certFile = new FileInputStream("exported_certificate.cer");
            X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(certFile);
            certFile.close();

            // load the keystore
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream keyStoreFile = new FileInputStream("keystore.p12");
            char[] password = "Group2".toCharArray();
            keyStore.load(keyStoreFile, password);
            keyStoreFile.close();

            // Set the system properties
            System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
            System.setProperty("javax.net.ssl.keyStore", "keystore.p12");
            System.setProperty("javax.net.ssl.keyStorePassword", "Group2");
            System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");
            System.setProperty("javax.net.ssl.trustStore", "truststore.p12");
            System.setProperty("javax.net.ssl.trustStorePassword", "Group2");

            // Start the RMI registry
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);

            // Create and bind the remote object
            FileServerInterface fileServer = new FileServerImpl();
            Naming.rebind("rmi://localhost/FileServer", fileServer);

            System.out.println("File Server is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

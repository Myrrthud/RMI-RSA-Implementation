import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.security.PrivateKey;

public interface FileServerInterface extends Remote {
    void authenticate(PublicKey publicKey, byte[] encryptedKey, byte[] signature, PrivateKey privateKey) throws RemoteException;
    byte[] getFile(String filename) throws RemoteException;
}

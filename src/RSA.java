import javax.crypto.Cipher;
import java.security.*;
import java.util.Base64;

public class RSA {

    //generates key pairs
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    //encrypts the message using the public key
    public static String encrypt(String text, PublicKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] bytes = cipher.doFinal(text.getBytes());
        return Base64.getEncoder().encodeToString(bytes);
    }

    //decrypts the message using the private key
    public static String decrypt(String text, PrivateKey key) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(text);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(bytes));
    }

    //converts the key to a string
    public static String keyToString(PublicKey key) throws Exception {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    //converts a string to a key
    public static PublicKey stringToKey(String key) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(key);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(new java.security.spec.X509EncodedKeySpec(bytes));
    }

}

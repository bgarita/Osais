package logica.utilitarios;

import com.lowagie.text.pdf.codec.Base64;
import java.math.BigInteger;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author bgarita, 24/04/2021
 */
public class Encripcion {

    private static final String TEXTFORMD5 = "Garita3112*-+@#$|";
    private static String encryptKey;
    

    public static String encript(String text) throws Exception {
        encryptKey = getMd5(TEXTFORMD5);
        Key aesKey = new SecretKeySpec(encryptKey.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);

        byte[] encrypted = cipher.doFinal(text.getBytes());

        return Base64.encodeBytes(encrypted);
    } // end encript

    public static String decrypt(String encrypted) throws Exception {
        encryptKey = getMd5(TEXTFORMD5);
        byte[] encryptedBytes = Base64.decode(encrypted.replace("\n", ""));

        Key aesKey = new SecretKeySpec(encryptKey.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);

        String decrypted = new String(cipher.doFinal(encryptedBytes));

        return decrypted;
    }

    private static String getMd5(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger number = new BigInteger(1, messageDigest);
        String hashtext = number.toString(16);

        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }
} // end class

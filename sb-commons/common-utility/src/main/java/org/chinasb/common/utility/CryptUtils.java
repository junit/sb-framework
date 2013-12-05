package org.chinasb.common.utility;

import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * 加密工具类
 * @version 1.0.0
 * @author zhujuan
 * @created 2013-12-4
 */
public class CryptUtils {
    private static final String PASSWORD_CRYPT_KEY = "Org_Chinasb";
    private static final String DES = "DES";
    private static final String[] UPPERCASE = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
            "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "A", "S", "D", "F", "G", "H", "J",
            "K", "L", "Z", "X", "C", "V", "B", "N", "M"};

    private static final String[] LOWERCASE = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
            "q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j",
            "k", "l", "z", "x", "c", "v", "b", "n", "m"};

    public static byte[] md5(byte[] src) throws Exception {
        MessageDigest alg = MessageDigest.getInstance("MD5");
        return alg.digest(src);
    }

    public static String md5(String src) throws Exception {
        return byte2hex(md5(src.getBytes()));
    }

    public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();

        DESKeySpec dks = new DESKeySpec(key);

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance(DES);

        cipher.init(1, securekey, sr);

        return cipher.doFinal(src);
    }

    public static byte[] hex2byte(byte[] bytes) {
        if (bytes.length % 2 != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }
        byte[] b2 = new byte[bytes.length / 2];
        for (int n = 0; n < bytes.length; n += 2) {
            String item = new String(bytes, n, 2);
            b2[(n / 2)] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();

        DESKeySpec dks = new DESKeySpec(key);

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance(DES);

        cipher.init(2, securekey, sr);

        return cipher.doFinal(src);
    }

    public static final String decryptPassword(String data) {
        if (data != null) try {
            return new String(decrypt(hex2byte(data.getBytes()), PASSWORD_CRYPT_KEY.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final String encryptPassword(String password) {
        if (password != null) try {
            return byte2hex(encrypt(password.getBytes(), PASSWORD_CRYPT_KEY.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String byte2hex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        String stmp = "";
        for (int n = 0; (bytes != null) && (n < bytes.length); ++n) {
            stmp = Integer.toHexString(bytes[n] & 0xFF);
            if (stmp.length() == 1)
                builder.append("0").append(stmp);
            else {
                builder.append(stmp);
            }
        }
        return builder.toString().toUpperCase();
    }

    public static String byte2webhex(byte[] b) {
        return byte2hex(b, "%");
    }

    public static String byte2hex(byte[] bytes, String elide) {
        StringBuilder sb = new StringBuilder();
        String stmp = "";
        elide = (elide == null) ? "" : elide;
        for (int n = 0; (bytes != null) && (n < bytes.length); ++n) {
            stmp = Integer.toHexString(bytes[n] & 0xFF);
            if (stmp.length() == 1)
                sb.append(elide).append("0").append(stmp);
            else
                sb.append(elide).append(stmp);
        }
        return sb.toString().toUpperCase();
    }

    public static String getMD5(byte[] source) {
        String src = null;

        char[] hexDigits =
                {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            byte[] tmp = md.digest();
            char[] str = new char[32];
            int k = 0;
            for (int i = 0; i < 16; ++i) {
                byte byte0 = tmp[i];
                str[(k++)] = hexDigits[(byte0 >>> 4 & 0xF)];
                str[(k++)] = hexDigits[(byte0 & 0xF)];
            }
            src = new String(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return src;
    }
}

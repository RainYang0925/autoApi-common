package com.lj.autotest.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Locale;

/**
 * Created by lijing on 16/10/21.
 * Description:
 */
public class Encrypt3Des {
    private static final String KEY = "meqnr5viyy1ppoqqzh43angr";

    /**
     * 对字符串进行DES3加密
     *
     * @param str
     * @return 加密后的字符串，若失败则返回null
     */
    public static String encode(String str) {
        if(str.isEmpty())
            return str;
        try {
            byte[] key = KEY.getBytes("UTF-8");
            byte[] data = str.getBytes("UTF-8");
            byte[] encodedData = des3EncodeECB(key, data);
            return byte2HexString(encodedData);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String decode(String str) {
        if(str.isEmpty())
            return str;
        try {
            byte[] key = KEY.getBytes("UTF-8");
            byte[] data = hexString2Byte(str);
            byte[] decodedData = des3DecodeECB(key, data);
            return new String(decodedData, "UTF-8");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * ECB加密,不要IV
     *
     * @param key
     *            密钥
     * @param data
     *            明文
     * @return Base64编码的密文
     * @throws Exception
     */
    public static byte[] des3EncodeECB(byte[] key, byte[] data)
            throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, deskey);
        byte[] bOut = cipher.doFinal(data);
        return bOut;
    }

    /**
     * ECB解密,不要IV
     *
     * @param key
     *            密钥
     * @param data
     *            Base64编码的密文
     * @return 明文
     * @throws Exception
     */
    public static byte[] ees3DecodeECB(byte[] key, byte[] data)
            throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, deskey);
        byte[] bOut = cipher.doFinal(data);
        return bOut;
    }

    /**
     * CBC加密
     *
     * @param key
     *            密钥
     * @param keyiv
     *            IV
     * @param data
     *            明文
     * @return Base64编码的密文
     * @throws Exception
     */
    public static byte[] des3EncodeCBC(byte[] key, byte[] keyiv, byte[] data)
            throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(keyiv);
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        byte[] bOut = cipher.doFinal(data);
        return bOut;
    }

    /**
     * CBC解密
     *
     * @param key
     *            密钥
     * @param keyiv
     *            IV
     * @param data
     *            Base64编码的密文
     * @return 明文
     * @throws Exception
     */
    public static byte[] des3DecodeCBC(byte[] key, byte[] keyiv, byte[] data)
            throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(keyiv);
        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
        byte[] bOut = cipher.doFinal(data);
        return bOut;
    }

    public static byte[] des3DecodeECB(byte[] key, byte[] data)
            throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, deskey);
        byte[] bOut = cipher.doFinal(data);
        return bOut;
    }

    private static String byte2HexString(byte[] b) {
        String a = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF)
                    .toUpperCase(Locale.US);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            a = a + hex;
        }
        return a;
    }

    private static byte[] hexString2Byte(String str) {
        if (str == null)
            return null;
        str = str.trim();
        int len = str.length();
        if (len == 0 || len % 2 == 1)
            return null;
        byte[] b = new byte[len / 2];
        try {
            for (int i = 0; i < str.length(); i += 2) {
                b[i / 2] = (byte) Integer.decode("0x" + str.substring(i, i + 2)).intValue();
            }
            return b;
        } catch (Exception e) {
            return null;
        }
    }

//    public static void main(String[] args) throws Exception {
//        String name="13012340001";
//        String encryptStr = encode(name);
//        System.out.println(encryptStr);
//
//    }
}

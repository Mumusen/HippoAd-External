/*
 * Copyright (c) 2019 Transsion Corporation. All rights reserved.
 * Created on 2019-12-16.
 */

package com.transmartx.hippo.utils;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 安全工具类.
 *
 */
public final class SecurityHelper {

    public static final String ALGORITHM__DES = "DES";
    public static final String ALGORITHM__DESEDE = "DESede";
    public static final String ALGORITHM__AES = "AES";
    public static final String ALGORITHM__RSA = "RSA";

    public static final String TRANSFORM__AES_CBC_PKCS5Padding = "AES/CBC/PKCS5Padding"; // JAVA
    public static final String TRANSFORM__AES_ECB_PKCS7Padding = "AES/ECB/PKCS7Padding"; // IOS

    public static final String TRANSFORM__AES_ECB_NoPadding = "AES/ECB/NoPadding";

    public static final String TRANSFORM__RSA_None_PKCS1Padding = "RSA/None/PKCS1Padding"; // JAVA
    public static final String TRANSFORM__RSA_ECB_PKCS1Padding = "RSA/ECB/PKCS1Padding"; // IOS

    public static final int KEY_SIZE__DES = 56; // Keysize must be equal to 56.
    public static final int KEY_SIZE__DESEDE = 168; // Keysize must be equal to 112 or 168.
    public static final int KEY_SIZE__AES = 256; // Keysize must be equal to 128, 192, or 256.
    public static final int KEY_SIZE__RSA = 2048; // Keysize ranges from 384 bits to 16,384 bits (depending on the underlying Microsoft Windows cryptographic service provider).

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private SecurityHelper() {
    }

    public static final String aesEncryptBase64(String aesKey, String plain) {
        return aesEncryptBase64(TRANSFORM__AES_ECB_PKCS7Padding, aesKey, plain);
    }

    public static final String aesDecryptBase64(String aesKey, String base64) {
        return aesDecryptBase64(TRANSFORM__AES_ECB_PKCS7Padding, aesKey, base64);
    }

    public static final String aesEncryptHex(String aesKey, String plain) {
        return aesEncryptHex(TRANSFORM__AES_ECB_PKCS7Padding, aesKey, plain);
    }

    public static final String aesDecryptHex(String aesKey, String hexString) {
        return aesDecryptHex(TRANSFORM__AES_ECB_PKCS7Padding, aesKey, hexString);
    }

    public static final String aesEncryptBase64(String transform, String aesKey, String plain) {
        try {
            byte[] bytes = aesEncrypt(transform, aesKey.getBytes(StandardCharsets.UTF_8), plain.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(bytes);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }

    public static final String aesDecryptBase64(String transform, String aesKey, String base64) {
        try {
            byte[] bytes = aesDecrypt(transform, aesKey.getBytes(StandardCharsets.UTF_8), Base64.decodeBase64(base64));
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }

    public static final String aesEncryptHex(String transform, String aesKey, String plain) {
        try {
            byte[] bytes = aesEncrypt(transform, aesKey.getBytes(StandardCharsets.UTF_8), plain.getBytes(StandardCharsets.UTF_8));
            return bytes2HexString(bytes);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }

    public static final String aesDecryptHex(String transform, String aesKey, String hexString) {
        try {
            byte[] bytes = aesDecrypt(transform, aesKey.getBytes(StandardCharsets.UTF_8), hexString2Bytes(hexString));
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }

    /**
     * byte转16进制String.
     *
     * @param bytes
     * @return
     */
    public static final String bytes2HexString(byte[] bytes) {
        int len = bytes.length;
        StringBuilder sb = new StringBuilder(len * 2);
        String tmp;
        for (int i = 0; i < len; i++) {
            tmp = Integer.toHexString(bytes[i] & 0xFF);
            if (tmp.length() < 2) {
                sb.append(0);
            }
            sb.append(tmp);
        }
        return sb.toString();
    }

    /**
     * 16进制String转byte.
     *
     * @param hexString
     * @return
     */
    public static final byte[] hexString2Bytes(String hexString) {
        int len = hexString.length() / 2;
        byte[] out = new byte[len];
        int pos = 0;
        for (int i = 0; i < len; i++) {
            out[i] = (byte) (Character.digit(hexString.charAt(pos++), 16) << 4 | Character.digit(hexString.charAt(pos++), 16));
        }
        return out;
    }

    public static final byte[] aesEncrypt(String transform, byte[] aesKey, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(transform);
            Key key = new SecretKeySpec(aesKey, ALGORITHM__AES);
            if (TRANSFORM__AES_CBC_PKCS5Padding.equalsIgnoreCase(transform)) {
                AlgorithmParameterSpec iv = new IvParameterSpec(aesKey);
                cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            } else if (TRANSFORM__AES_ECB_PKCS7Padding.equalsIgnoreCase(transform)) {
                cipher.init(Cipher.ENCRYPT_MODE, key);
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, key);
            }
            return cipher.doFinal(data);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }

    public static final byte[] aesDecrypt(String transform, byte[] aesKey, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(transform);
            Key key = new SecretKeySpec(aesKey, ALGORITHM__AES);
            if (TRANSFORM__AES_CBC_PKCS5Padding.equalsIgnoreCase(transform)) {
                AlgorithmParameterSpec iv = new IvParameterSpec(aesKey);
                cipher.init(Cipher.DECRYPT_MODE, key, iv);
            } else if (TRANSFORM__AES_ECB_PKCS7Padding.equalsIgnoreCase(transform)) {
                cipher.init(Cipher.DECRYPT_MODE, key);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, key);
            }
            return cipher.doFinal(data);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }

    public static final String rsaEncryptBase64(String rsaPublicKey, String plain) {
        return rsaEncryptBase64(TRANSFORM__RSA_ECB_PKCS1Padding, rsaPublicKey, plain);
    }

    public static final String rsaDecryptBase64(String rsaPrivateKey, String base64) {
        return rsaDecryptBase64(TRANSFORM__RSA_ECB_PKCS1Padding, rsaPrivateKey, base64);
    }

    public static final String rsaEncryptBase64(String transform, String rsaPublicKey, String plain) {
        try {
            Cipher cipher = Cipher.getInstance(transform);
            X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(rsaPublicKey));
            KeyFactory factory = KeyFactory.getInstance(ALGORITHM__RSA);
            PublicKey publicKey = factory.generatePublic(encodedKeySpec);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] bytes = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(bytes);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }

    public static final String rsaDecryptBase64(String transform, String rsaPrivateKey, String base64) {
        try {
            Cipher cipher = Cipher.getInstance(transform);
            PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(rsaPrivateKey));
            KeyFactory factory = KeyFactory.getInstance(ALGORITHM__RSA);
            PrivateKey privateKey = factory.generatePrivate(encodedKeySpec);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] bytes = cipher.doFinal(Base64.decodeBase64(base64));
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }

    public static final SecretKey generateSecretKey(String algorithm, int keySize) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
            keyGen.init(keySize, new SecureRandom());
            return keyGen.generateKey();
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }

    public static final KeyPair generateKeyPair(String algorithm, int keySize) {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(algorithm);
            keyPairGen.initialize(keySize, new SecureRandom());
            return keyPairGen.generateKeyPair();
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }

    public static final RSAPublicKey generateRSAPublicKey(KeyPair keyPair) {
        return (RSAPublicKey) keyPair.getPublic();
    }

    public static final RSAPrivateKey generateRSAPrivateKey(KeyPair keyPair) {
        return (RSAPrivateKey) keyPair.getPrivate();
    }

    public static void main(String[] args) {
        String signOut = SecurityHelper.aesEncryptHex("hippo@2024010101", "prd.oo2o22");
        String source = SecurityHelper.aesDecryptHex("hippo@2024010101", signOut);
        System.out.println("sign out: " + signOut);
        System.out.println("source: " + source);
    }

}

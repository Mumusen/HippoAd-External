package com.transmartx.hippo.utils.sign;
  
import java.io.BufferedReader;  
import java.io.BufferedWriter;  
import java.io.ByteArrayOutputStream;
import java.io.FileReader;  
import java.io.FileWriter;  
import java.io.IOException;  
import java.security.InvalidKeyException;  
import java.security.KeyFactory;  
import java.security.KeyPair;  
import java.security.KeyPairGenerator;  
import java.security.NoSuchAlgorithmException;  
import java.security.SecureRandom;  
  
import java.security.interfaces.RSAPrivateKey;  
import java.security.interfaces.RSAPublicKey;  
import java.security.spec.InvalidKeySpecException;  
import java.security.spec.PKCS8EncodedKeySpec;  
import java.security.spec.X509EncodedKeySpec;  
  

import javax.crypto.BadPaddingException;  
import javax.crypto.Cipher;  
import javax.crypto.IllegalBlockSizeException;  
import javax.crypto.NoSuchPaddingException;  
  
//import com.fcplay.Base64;  

  
public class RSAEncrypt {  
	
    private static final int MAX_ENCRYPT_BLOCK = 234;

    private static final int MAX_DECRYPT_BLOCK = 256;
	
    private static final char[] HEX_CHAR = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };  
  
    public static void genKeyPair(String filePath) {
        KeyPairGenerator keyPairGen = null;
        try {  
            keyPairGen = KeyPairGenerator.getInstance("RSA");  
        } catch (NoSuchAlgorithmException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        keyPairGen.initialize(2048,new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // �õ�˽Կ  
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  
        // �õ���Կ  
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  
        try {  
            // �õ���Կ�ַ���  
            String publicKeyString = new String(Base64.encode(publicKey.getEncoded()));  
            // �õ�˽Կ�ַ���  
            String privateKeyString = new String(Base64.encode(privateKey.getEncoded()));  
            // ����Կ��д�뵽�ļ�  
            FileWriter pubfw = new FileWriter(filePath + "/publicKey.keystore");  
            FileWriter prifw = new FileWriter(filePath + "/privateKey.keystore");  
            BufferedWriter pubbw = new BufferedWriter(pubfw);  
            BufferedWriter pribw = new BufferedWriter(prifw);  
            pubbw.write(publicKeyString);  
            pribw.write(privateKeyString);  
            pubbw.flush();  
            pubbw.close();  
            pubfw.close();  
            pribw.flush();  
            pribw.close();  
            prifw.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    public static String loadPublicKeyByFile(String path) throws Exception {
        try {  
            BufferedReader br = new BufferedReader(new FileReader(path  
                    + "/publicKey.keystore"));  
            String readLine = null;  
            StringBuilder sb = new StringBuilder();  
            while ((readLine = br.readLine()) != null) {  
                sb.append(readLine);  
            }  
            br.close();  
            return sb.toString();  
        } catch (IOException e) {  
            throw new Exception("��Կ��������ȡ����");  
        } catch (NullPointerException e) {  
            throw new Exception("��Կ������Ϊ��");  
        }  
    }  
  
    /** 
     * ���ַ����м��ع�Կ 
     *  
     * @param publicKeyStr 
     *            ��Կ�����ַ��� 
     * @throws Exception 
     *             ���ع�Կʱ�������쳣 
     */  
    public static RSAPublicKey loadPublicKeyByStr(String publicKeyStr)  
            throws Exception {  
        try {  
            byte[] buffer =Base64.decode(publicKeyStr);  
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);  
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("�޴��㷨");  
        } catch (InvalidKeySpecException e) {  
            throw new Exception("��Կ�Ƿ�");  
        } catch (NullPointerException e) {  
            throw new Exception("��Կ����Ϊ��");  
        }  
    }  
  
    /** 
     * ���ļ��м���˽Կ 
     *  
     * @param keyFileName 
     *            ˽Կ�ļ��� 
     * @return �Ƿ�ɹ� 
     * @throws Exception 
     */  
    public static String loadPrivateKeyByFile(String path) throws Exception {  
        try {  
            BufferedReader br = new BufferedReader(new FileReader(path  
                    + "/privateKey.keystore"));  
            String readLine = null;  
            StringBuilder sb = new StringBuilder();  
            while ((readLine = br.readLine()) != null) {  
                sb.append(readLine);  
            }  
            br.close();  
            return sb.toString();  
        } catch (IOException e) {  
            throw new Exception("˽Կ���ݶ�ȡ����");  
        } catch (NullPointerException e) {  
            throw new Exception("˽Կ������Ϊ��");  
        }  
    }  
  
    public static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr)  
            throws Exception {  
        try {  
            byte[] buffer = Base64.decode(privateKeyStr);  
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);  
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("�޴��㷨");  
        } catch (InvalidKeySpecException e) {  
            throw new Exception("˽Կ�Ƿ�");  
        } catch (NullPointerException e) {  
            throw new Exception("˽Կ����Ϊ��");  
        }  
    }  
  
    /** 
     * ��Կ���ܹ��� 
     *  
     * @param publicKey 
     *            ��Կ 
     * @param plainTextData 
     *            �������� 
     * @return 
     * @throws Exception 
     *             ���ܹ����е��쳣��Ϣ 
     */  
    public static byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData)  
            throws Exception {  
        if (publicKey == null) {  
            throw new Exception("���ܹ�ԿΪ��, ������");  
        }  
        Cipher cipher = null;  
        try {  
            // ʹ��Ĭ��RSA  
            cipher = Cipher.getInstance("RSA");  
//             cipher= Cipher.getInstance("RSA", new BouncyCastleProvider());  
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
//            byte[] output = cipher.doFinal(plainTextData);  
            int inputLen = plainTextData.length;  
            ByteArrayOutputStream out = new ByteArrayOutputStream();  
            int offSet = 0;  
            byte[] cache;  
            int i = 0;  
            // �����ݷֶμ���  
            while (inputLen - offSet > 0) {  
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {  
                    cache = cipher.doFinal(plainTextData, offSet, MAX_ENCRYPT_BLOCK);  
                } else {  
                    cache = cipher.doFinal(plainTextData, offSet, inputLen - offSet);  
                }  
                out.write(cache, 0, cache.length);  
                i++;  
                offSet = i * MAX_ENCRYPT_BLOCK;  
            } 
            byte[] encryptedData = out.toByteArray();  
            out.close();  
            return encryptedData;  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("�޴˼����㷨");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        } catch (InvalidKeyException e) {  
            throw new Exception("���ܹ�Կ�Ƿ�,����");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("���ĳ��ȷǷ�");  
        } catch (BadPaddingException e) {  
            throw new Exception("������������");  
        }  
    }  
  
    /** 
     * ˽Կ���ܹ��� 
     *  
     * @param privateKey 
     *            ˽Կ 
     * @param plainTextData 
     *            �������� 
     * @return 
     * @throws Exception 
     *             ���ܹ����е��쳣��Ϣ 
     */  
    public static byte[] encrypt(RSAPrivateKey privateKey, byte[] plainTextData)  
            throws Exception {  
        if (privateKey == null) {  
            throw new Exception("����˽ԿΪ��, ������");  
        }  
        Cipher cipher = null;  
        try {  
            // ʹ��Ĭ��RSA  
            cipher = Cipher.getInstance("RSA");  
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);  
//            byte[] output = cipher.doFinal(plainTextData);  
            int inputLen = plainTextData.length;  
            ByteArrayOutputStream out = new ByteArrayOutputStream();  
            int offSet = 0;  
            byte[] cache;  
            int i = 0;  
            // �����ݷֶμ���  
            while (inputLen - offSet > 0) {  
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {  
                    cache = cipher.doFinal(plainTextData, offSet, MAX_ENCRYPT_BLOCK);  
                } else {  
                    cache = cipher.doFinal(plainTextData, offSet, inputLen - offSet);  
                }  
                out.write(cache, 0, cache.length);  
                i++;  
                offSet = i * MAX_ENCRYPT_BLOCK;  
            } 
            byte[] encryptedData = out.toByteArray();  
            out.close();  
            return encryptedData;  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("�޴˼����㷨");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        } catch (InvalidKeyException e) {  
            throw new Exception("����˽Կ�Ƿ�,����");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("���ĳ��ȷǷ�");  
        } catch (BadPaddingException e) {  
            throw new Exception("������������");  
        }  
    }  
  
    /** 
     * ˽Կ���ܹ��� 
     *  
     * @param privateKey 
     *            ˽Կ 
     * @param cipherData 
     *            �������� 
     * @return ���� 
     * @throws Exception 
     *             ���ܹ����е��쳣��Ϣ 
     */  
    public static byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData)  
            throws Exception {  
        if (privateKey == null) {  
            throw new Exception("����˽ԿΪ��, ������");  
        }  
        Cipher cipher = null;  
        try {  
            // ʹ��Ĭ��RSA  
            cipher = Cipher.getInstance("RSA");  
            cipher.init(Cipher.DECRYPT_MODE, privateKey);  
            int inputLen = cipherData.length;  
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // �����ݷֶν���  
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(cipherData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(cipherData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();
            return decryptedData;
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("�޴˽����㷨");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        } catch (InvalidKeyException e) {  
            throw new Exception("����˽Կ�Ƿ�,����");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("���ĳ��ȷǷ�");  
        } catch (BadPaddingException e) {  
            throw new Exception("������������");  
        }  
    }  
  
    /** 
     * ��Կ���ܹ��� 
     *  
     * @param publicKey 
     *            ��Կ 
     * @param cipherData 
     *            �������� 
     * @return ���� 
     * @throws Exception 
     *             ���ܹ����е��쳣��Ϣ 
     */  
    public static byte[] decrypt(RSAPublicKey publicKey, byte[] cipherData)  
            throws Exception {  
        if (publicKey == null) {  
            throw new Exception("���ܹ�ԿΪ��, ������");  
        }  
        Cipher cipher = null;  
        try {  
            // ʹ��Ĭ��RSA  
            cipher = Cipher.getInstance("RSA");  
            cipher.init(Cipher.DECRYPT_MODE, publicKey);  
            int inputLen = cipherData.length;  
            ByteArrayOutputStream out = new ByteArrayOutputStream();  
            int offSet = 0;  
            byte[] cache;  
            int i = 0;  
            // �����ݷֶμ���  
            while (inputLen - offSet > 0) {  
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {  
                    cache = cipher.doFinal(cipherData, offSet, MAX_DECRYPT_BLOCK);  
                } else {  
                    cache = cipher.doFinal(cipherData, offSet, inputLen - offSet);  
                }  
                out.write(cache, 0, cache.length);  
                i++;  
                offSet = i * MAX_DECRYPT_BLOCK;  
            } 
            byte[] decryptedData = out.toByteArray();  
            out.close();  
            return decryptedData;
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("�޴˽����㷨");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        } catch (InvalidKeyException e) {  
            throw new Exception("���ܹ�Կ�Ƿ�,����");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("���ĳ��ȷǷ�");  
        } catch (BadPaddingException e) {  
            throw new Exception("������������");  
        }  
    }  
  
    /** 
     * �ֽ�����תʮ�������ַ��� 
     *  
     * @param data 
     *            �������� 
     * @return ʮ���������� 
     */  
    public static String byteArrayToString(byte[] data) {  
        StringBuilder stringBuilder = new StringBuilder();  
        for (int i = 0; i < data.length; i++) {  
            // ȡ���ֽڵĸ���λ ��Ϊ�����õ���Ӧ��ʮ�����Ʊ�ʶ�� ע���޷�������  
            stringBuilder.append(HEX_CHAR[(data[i] & 0xf0) >>> 4]);  
            // ȡ���ֽڵĵ���λ ��Ϊ�����õ���Ӧ��ʮ�����Ʊ�ʶ��  
            stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);  
            if (i < data.length - 1) {  
                stringBuilder.append(' ');  
            }  
        }  
        return stringBuilder.toString();  
    }  
}  

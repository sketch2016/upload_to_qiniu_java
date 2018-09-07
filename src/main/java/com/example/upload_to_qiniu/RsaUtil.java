package com.example.upload_to_qiniu;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RsaUtil {

    public static final String RSA = "RSA";

    private static final int ENCRYPT_BLOCK = 100;

    //生成公钥+私钥
    public static Map<String, String> createKey(int keyLength) {
        KeyPairGenerator keyPairGenerator = null;

        try {
            keyPairGenerator = keyPairGenerator.getInstance(RSA);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //初始化KeyPairGenerator对象,密钥长度
        keyPairGenerator.initialize(keyLength);
        //生成密钥对
        KeyPair pair = keyPairGenerator.generateKeyPair();
        //生成公钥　+　base64编码
        Key publicKey = pair.getPublic();
        String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded());
        //生成公私钥　+　base64编码
        Key privateKey = pair.getPrivate();
        String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());

        Map<String, String> pairMap = new HashMap<>();
        pairMap.put("publicKey", publicKeyStr);
        pairMap.put("privateKey", privateKeyStr);

        return pairMap;
    }

    //经过base64解码得到公钥
    public static RSAPublicKey getPublicKey(String publicKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyStr));
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(x509EncodedKeySpec);

        return rsaPublicKey;
    }

    //经过base64解码得到私钥
    public static RSAPrivateKey getPrivateKey(String privateKeyStr) throws Exception {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyStr));
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8EncodedKeySpec);

        return rsaPrivateKey;
    }

    /**
     * @param data   需要加密的数据
     * @param publicKey 　公钥
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static byte[] publicEncrypt(byte[] data, RSAPublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return rsaProcessData(cipher, Cipher.ENCRYPT_MODE, data, publicKey.getModulus().bitLength());
    }

    /**
     *
     * @param data    公钥加密过的数据
     * @param privateKey　私钥
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static byte[] privateDecrypt(byte[] data, RSAPrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return rsaProcessData(cipher, Cipher.DECRYPT_MODE, data, privateKey.getModulus().bitLength());
    }

    /**
     *
     * @param data  未加密的数据
     * @param privateKey　私钥
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static byte[] privateEncrypt(byte[] data, RSAPrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException{
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        return rsaProcessData(cipher, Cipher.ENCRYPT_MODE, data, privateKey.getModulus().bitLength());
    }

    /**
     *
     * @param data  私钥加密过的数据
     * @param publicKey　公钥
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static byte[] publicDecrypt(byte[] data, RSAPublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        return rsaProcessData(cipher, Cipher.DECRYPT_MODE, data, publicKey.getModulus().bitLength());
    }

    /**
     * 加解密过程－核心算法
     * @param cipher　　加解密工具
     * @param mode　　　模式－加密/解密
     * @param data　　　加密数据/未加密数据
     * @param keyLength　密钥长度
     * @return
     */
    public static byte[] rsaProcessData(Cipher cipher, int mode, byte[] data, int keyLength) {
        int max_block = 0;
        if (mode == Cipher.DECRYPT_MODE) {
            max_block = keyLength / 8;
        } else {
            max_block = keyLength / 8 - 11;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int totaLength = data.length;
        int offset = 0;
        byte[] buff = null;

        try {
            while (totaLength > offset) {
                if (totaLength - offset > max_block) {
                    buff = cipher.doFinal(data, offset, max_block);
                } else {
                    buff = cipher.doFinal(data, offset, totaLength - offset);
                }
                out.write(buff, 0, buff.length);

                offset += max_block;
            }
            byte[] result = out.toByteArray();
            out.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();

        }

        return null;
    }

    public static void test() throws Exception {
        Map<String, String> rsaMap = createKey(1024);
        //String publicKeyStr = rsaMap.get("publicKey");
        //String privateKeyStr = rsaMap.get("privateKey");

        String publicKeyStr = WebMvcConf.PUBLIC_KEY;
        String privateKeyStr = WebMvcConf.PRIVATE_KEY;

        System.out.println("公钥: \n\r" + publicKeyStr);
        System.out.println("私钥： \n\r" + privateKeyStr);

        //testString(publicKeyStr, privateKeyStr);

        //testFile(publicKeyStr, privateKeyStr);


    }

    private static void testString(String publicKeyStr, String privateKeyStr) throws Exception {
        String testStr = "hello world";
        /**
         * 测试公钥加密－私钥解密@String
         */
        System.out.println("--------------------------------------------------");
        byte[] publicEncryptData = publicEncrypt(testStr.getBytes(), getPublicKey(publicKeyStr));
        String publicEncryptResult = new String(publicEncryptData);

        System.out.println("明文： \n\r" + testStr);
        System.out.println("公钥加密： \n\r" + publicEncryptResult);

        byte[] privateDectyptData = privateDecrypt(publicEncryptData, getPrivateKey(privateKeyStr));
        String privateDecryptResult = new String(privateDectyptData);
        System.out.println("私钥解密： \n\r" + privateDecryptResult);

        System.out.println("--------------------------------------------------");
        /**
         * 测试私钥加密－公钥解密@String
         */
        byte[] privateEncryptData = privateEncrypt(testStr.getBytes(), getPrivateKey(privateKeyStr));
        String privateEncryptResult = new String(privateEncryptData);

        System.out.println("明文： \n\r" + testStr);
        System.out.println("私钥加密： \n\r" + privateEncryptResult);

        byte[] publicDecryptData = publicDecrypt(privateEncryptData, getPublicKey(publicKeyStr));
        String publicDecryptResult = new String(publicDecryptData);

        System.out.println("公钥解密： \n\r" + publicDecryptResult);
    }

    private static void testFile(String publicKeyStr, String privateKeyStr) throws Exception {
        System.out.println("--------------------------------------------------");
        File file = new File(WebMvcConf.UPLOAD_DIR);
        File picture = null;
        if (file.exists() && file.isDirectory()) {
            picture =file.listFiles()[0];

            System.out.println("get picture: picture=" + picture);
        }

        if (picture == null) {
            return;
        }
        byte[] data = null;

//        FileInputStream fis = new FileInputStream(picture);
//        BufferedInputStream bis = new BufferedInputStream(fis);
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//        byte[] buff = new byte[1024 * 8];
//        int n = -1;
//        while ((n = bis.read(buff)) != -1) {
//            out.write(buff, 0, n);
//        }
//        data = out.toByteArray();
//        bis.close();
//        fis.close();
//        out.close();
        publicEncryptFile(picture, publicKeyStr);

        privateDecryptFile(picture, privateKeyStr);
    }

    private static void privateDecryptFile(File picture, String privateKeyStr) throws Exception {
        byte[] data = file2Byte(new File(WebMvcConf.UPLOAD_DIR, "public_enctypt_" + picture.getName()));
        byte[] privateDecryptData = RsaUtil.privateDecrypt(data, getPrivateKey(privateKeyStr));
        FileOutputStream fos = new FileOutputStream(new File(WebMvcConf.UPLOAD_DIR, "private_dectypt_" + picture.getName()));
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bos.write(privateDecryptData);
        fos.close();
        bos.close();
        System.out.println("file decrypt success!");
    }

    private static void publicEncryptFile(File file, String publicKeyStr) throws Exception {
        byte[] data = file2Byte(file);

        byte[] publicEncryptData = RsaUtil.publicEncrypt(data, getPublicKey(publicKeyStr));
        FileOutputStream fos = new FileOutputStream(new File(WebMvcConf.UPLOAD_DIR, "public_enctypt_" + file.getName()));
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bos.write(publicEncryptData);
        bos.close();
        fos.close();
        System.out.println("file encrypt success!");
    }

    public static byte[] file2Byte(File file) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        byte[] buff = new byte[1024 * 8];
        int n = -1;

        while ((n = bis.read(buff)) != -1) {
            bos.write(buff, 0, n);
        }
        byte[] result = bos.toByteArray();
        bos.close();
        bis.close();
        fis.close();

        return result;
    }

}

package com.jz.bigdata.util;

import com.jz.bigdata.common.license.CreateLicense;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: yiyang
 * @Date: 2021/3/16 14:09
 * @Description:
 */
@Slf4j
public class RSAUtil {

    private static String privateKey = "";//私钥
    private static String publicKey = "";//公钥
    private static final String RSAKeyFile = "RSAKey.properties";
    private static Map<Integer, String> keyMap = new HashMap<Integer, String>();  //用于封装产生的公钥与私钥
    public static void main(String[] args) throws Exception {
        System.out.println(getPublicKey());
        //生成公钥和私钥
        //genKeyPair();
        //加密字符串
        String message = "123456";
//        System.out.println("随机生成的公钥为:" + keyMap.get(0));
//        System.out.println("随机生成的私钥为:" + keyMap.get(1));
        String messageEn = encrypt(message,publicKey);

        System.out.println(message + "\t加密后的字符串为:" + messageEn);
        String string = "m42lJJkkaaIqHk6T2+K7JFbR51mOt58ncwxnOSqJpVbkFY8eIKF2JCmoXuK/7ONxBlA/mibjJ2fRLjyfKI8JV4FxRffYICYu0OowMNdrIzzX5hIs4GtbG/wp9a0R64neg8U7jpa0cA4/npqMG6hDW/isTURAxnUKcXtERp/GGtU=";
        String messageDe = decrypt(string,privateKey);
        System.out.println("还原后的字符串为:" + messageDe);

    }

    /**
     * 获取公钥
     * @return
     */
    public static String getPublicKey(){
        if(Strings.isNullOrEmpty(publicKey)){
            getKeyPair();
        }
        return publicKey;
    }
    /**
     * 获取私钥
     * @return
     */
    public static String getPrivateKey(){
        if(Strings.isNullOrEmpty(privateKey)){
            getKeyPair();
        }
        return privateKey;
    }
    /**
     * 读取文件，获取公钥和私钥信息，写入变量中，
     */
    private static void getKeyPair(){
        Properties prop = new Properties();
        InputStream in = CreateLicense.class.getClassLoader().getResourceAsStream(RSAKeyFile);
        try {
            prop.load(in);
        } catch (IOException e) {
            log.error("获取RSA文件失败！"+e.getMessage());
        }
        privateKey = prop.getProperty("private");
        publicKey = prop.getProperty("public");
    }
    /**
     * 随机生成密钥对
     * @throws NoSuchAlgorithmException
     */
    public static void genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024,new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
        // 将公钥和私钥保存到Map
        keyMap.put(0,publicKeyString);  //0表示公钥
        keyMap.put(1,privateKeyString);  //1表示私钥
    }
    /**
     * RSA公钥加密
     *
     * @param str
     *            加密字符串
     * @param publicKey
     *            公钥
     * @return 密文
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static String encrypt( String str, String publicKey ) throws Exception{
        //base64编码的公钥
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param str
     *            加密字符串
     * @param privateKey
     *            私钥
     * @return 铭文
     * @throws Exception
     *             解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception{
        //64位解码加密后的字符串
        byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
        //base64编码的私钥
        byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }
}

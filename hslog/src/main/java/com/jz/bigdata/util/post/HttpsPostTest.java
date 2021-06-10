package com.jz.bigdata.util.post;

import org.apache.commons.httpclient.HttpStatus;

import javax.net.ssl.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

/**
 * @program: idea
 * @description:
 * @author: Savilio
 * @create: 2021-04-25 14:38
 **/

public class HttpsPostTest {

    /**
     * 获得KeyStore.
     * @param keyStorePath
     *            密钥库路径
     * @param password
     *            密码
     * @return 密钥库
     * @throws Exception
     */
    public static KeyStore getKeyStore(String password, String keyStorePath)
            throws Exception {
        // 实例化密钥库
        KeyStore ks = KeyStore.getInstance("JKS");
        // 获得密钥库文件流
        FileInputStream is = new FileInputStream(keyStorePath);
        // 加载密钥库
        ks.load(is, password.toCharArray());
        // 关闭密钥库文件流
        is.close();
        return ks;
    }

    /**
     * 获得SSLSocketFactory.
     * @param password
     *            密码
     * @param keyStorePath
     *            密钥库路径
     * @param trustStorePath
     *            信任库路径
     * @return SSLSocketFactory
     * @throws Exception
     */
    public static SSLContext getSSLContext(String password,
                                           String keyStorePath, String trustStorePath) throws Exception {
        // 实例化密钥库
        KeyManagerFactory keyManagerFactory = KeyManagerFactory
                .getInstance(KeyManagerFactory.getDefaultAlgorithm());
        // 获得密钥库
        KeyStore keyStore = getKeyStore(password, keyStorePath);
        // 初始化密钥工厂
        keyManagerFactory.init(keyStore, password.toCharArray());

        // 实例化信任库
        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        // 获得信任库
        KeyStore trustStore = getKeyStore(password, trustStorePath);
        // 初始化信任库
        trustManagerFactory.init(trustStore);
        // 实例化SSL上下文
        SSLContext ctx = SSLContext.getInstance("TLS");
        // 初始化SSL上下文
        ctx.init(keyManagerFactory.getKeyManagers(),
                trustManagerFactory.getTrustManagers(), null);
        // 获得SSLSocketFactory
        return ctx;
    }

    /**
     * 初始化HttpsURLConnection.
     * @param password
     *            密码
     * @param keyStorePath
     *            密钥库路径
     * @param trustStorePath
     *            信任库路径
     * @throws Exception
     */
    public static void initHttpsURLConnection(String password,
                                              String keyStorePath, String trustStorePath) throws Exception {
        // 声明SSL上下文
        SSLContext sslContext = null;
        // 实例化主机名验证接口
        HostnameVerifier hnv = new MyHostnameVerifier();
        try {
            sslContext = getSSLContext(password, keyStorePath, trustStorePath);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        if (sslContext != null) {
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
                    .getSocketFactory());
        }
        HttpsURLConnection.setDefaultHostnameVerifier(hnv);
    }

    /**
     * 发送请求.
     * @param httpsUrl
     *            请求的地址
     * @param xmlStr
     *            请求的数据
     */
    public static void post(String httpsUrl, String xmlStr) {
        HttpsURLConnection urlCon = null;
        try {
            urlCon = (HttpsURLConnection) (new URL(httpsUrl)).openConnection();
            urlCon.setDoInput(true);
            urlCon.setDoOutput(true);
            urlCon.setRequestMethod("POST");
            urlCon.setRequestProperty("Content-Length",
                    String.valueOf(xmlStr.getBytes().length));
            urlCon.setUseCaches(false);
            //设置为gbk可以解决服务器接收时读取的数据中文乱码问题
            urlCon.getOutputStream().write(xmlStr.getBytes("gbk"));
            urlCon.getOutputStream().flush();
            urlCon.getOutputStream().close();

//            BufferedReader in = new BufferedReader(new InputStreamReader(
//                    urlCon.getInputStream()));
            InputStream is = null;
            int status = urlCon.getResponseCode();
            if(status>= HttpStatus.SC_BAD_REQUEST){  //此处一定要根据返回的状态码state来初始化输入流。如果为错误
                is = urlCon.getErrorStream();
                System.out.println("失败:"+status);
            }else{
                //如果正确
                is = urlCon.getInputStream();
                System.out.println("成功");
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void post(String httpsUrl) {
        HttpsURLConnection urlCon = null;
        try {
            urlCon = (HttpsURLConnection) (new URL(httpsUrl)).openConnection();
            urlCon.setDoInput(true);
            urlCon.setDoOutput(true);
            urlCon.setRequestMethod("POST");

            urlCon.setUseCaches(false);
            //设置为gbk可以解决服务器接收时读取的数据中文乱码问题
//            urlCon.getOutputStream().write(null);
            urlCon.getOutputStream().flush();
            urlCon.getOutputStream().close();

//            BufferedReader in = new BufferedReader(new InputStreamReader(
//                    urlCon.getInputStream()));

            InputStream is = null;
            int status = urlCon.getResponseCode();
            if(status>= HttpStatus.SC_BAD_REQUEST){  //此处一定要根据返回的状态码state来初始化输入流。如果为错误
                is = urlCon.getErrorStream();
                System.out.println("失败:"+status);
            }else{
                //如果正确
                is = urlCon.getInputStream();
                System.out.println("成功");
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试方法.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // 密码
        //String password = "changeit";
        //String password = "cnblogs";
        String password = "hsdata.123";

        // 密钥库
        String keyStorePath = "C:\\work\\https\\hstomcat.keystore";
        // 信任库
        String trustStorePath = "C:\\work\\https\\hstomcat.keystore";
        // 本地起的https服务
//        String httpsUrl = "https://192.168.2.181/collector/stopSyslogKafkaListener.do";
//        String httpsUrl = "https://192.168.2.181/hslog/collector/stopSyslogKafkaListener.do";
        String httpsUrl = "https://192.168.2.181/hslog/user/login.do";
        // 传输文本
        String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><fruitShop><fruits><fruit><kind>萝卜</kind></fruit><fruit><kind>菠萝</kind></fruit></fruits></fruitShop>";
        HttpsPostTest.initHttpsURLConnection(password, keyStorePath, trustStorePath);
        // 发起请求
        HttpsPostTest.post(httpsUrl, xmlStr);
//        HttpsPostTest.post(httpsUrl);
    }
}
package com.jz.bigdata.util.post;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.util.StringUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/4/26 9:19
 * @Description: 发送http/https请求
 */
public class UrlPost {
    private String cookies;//发送请求时的cookie信息，如果有cookie信息，请求时附加，如果没有cookie信息，请求成功后尝试获取。
    private Map<String, String> params;//参数
    private String url;//请求的url
    private String contentType;// 内容类型
    private String method;//请求方式  post/get等

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
    //添加主机名验证程序类，设置不验证主机
    private final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    /**
     * 发送https请求
     * @return
     */
    public String sendHttpsPost(){
        //返回信息
        StringBuffer sb=new StringBuffer();
        DataOutputStream out = null;
        HttpsURLConnection conn = null;
        try {
            //添加信任主机
            trustAllHosts();
            // 创建url资源
            URL url = new URL(this.url);
            // 建立https连接
            conn = (HttpsURLConnection) url.openConnection();
            //如果cookie不为空，添加到请求信息中
            if(this.cookies!=null&&this.cookies!=""){
                conn.setRequestProperty("Cookie", cookies);
            }
            conn.setHostnameVerifier(DO_NOT_VERIFY);
            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置允许输出
            conn.setDoOutput(true);
            // 设置允许输入
            conn.setDoInput(true);
            // 设置传递方式
            conn.setRequestMethod(this.method);
            System.out.println(conn.getRequestMethod());
            // 设置维持长连接
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 设置文件字符集:
            conn.setRequestProperty("Charset", "UTF-8");
            // 设置文件类型:
            conn.setRequestProperty("Content-Type", this.contentType);

            // 开始连接请求
            conn.connect();
            out = new DataOutputStream(conn.getOutputStream());
            // 写入请求的字符串
            out.writeBytes(map2Url(params));
            out.flush();

            System.out.println("请求返回code："+conn.getResponseCode());

            InputStream is = null;
            int status = conn.getResponseCode();
            if(status>= HttpStatus.SC_BAD_REQUEST){  //此处一定要根据返回的状态码state来初始化输入流。如果为错误
                is = conn.getErrorStream();
                //System.out.println("失败:"+status);
            }else{
                //如果正确
                is = conn.getInputStream();
                //System.out.println("成功");
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println("发送请求失败");
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch(Exception e) {
                System.out.println("请求输出流关闭失败");
                e.printStackTrace();

            }
            try{
                conn.disconnect();
//                cookies = getCookies(conn);
                //请求不包含cookie，尝试请求完成后获取
                if(this.cookies==null||this.cookies==""){
                    String header_cookie = conn.getHeaderField("Set-Cookie");
                    if(header_cookie!=null){
                        cookies = conn.getHeaderField("Set-Cookie").split(";")[0];
                    }
                }
            }catch(Exception e) {
                System.out.println("链接关闭失败|获取cookie失败");
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
    /**
     * 发送http请求
     * @return
     */
    public String sendHttpPost(){
        //返回信息
        StringBuffer sb=new StringBuffer();
        DataOutputStream out = null;
        HttpURLConnection conn = null;
        try {
            //添加信任主机
            trustAllHosts();
            // 创建url资源
            URL url = new URL(this.url);
            // 建立https连接
            conn = (HttpURLConnection) url.openConnection();
            //如果cookie不为空，添加到请求信息中
            if(this.cookies!=null&&this.cookies!=""){
                conn.setRequestProperty("Cookie", cookies);
            }
            //conn.setHostnameVerifier(DO_NOT_VERIFY);
            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置允许输出
            conn.setDoOutput(true);
            // 设置允许输入
            conn.setDoInput(true);
            // 设置传递方式
            conn.setRequestMethod(this.method);
            System.out.println(conn.getRequestMethod());
            // 设置维持长连接
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 设置文件字符集:
            conn.setRequestProperty("Charset", "UTF-8");
            // 设置文件类型:
            conn.setRequestProperty("Content-Type", this.contentType);

            // 开始连接请求
            conn.connect();
            out = new DataOutputStream(conn.getOutputStream());
            // 写入请求的字符串
            out.writeBytes(map2Url(params));
            out.flush();

            System.out.println("请求返回code："+conn.getResponseCode());

            InputStream is = null;
            int status = conn.getResponseCode();
            if(status>= HttpStatus.SC_BAD_REQUEST){  //此处一定要根据返回的状态码state来初始化输入流。如果为错误
                is = conn.getErrorStream();
                //System.out.println("失败:"+status);
            }else{
                //如果正确
                is = conn.getInputStream();
                //System.out.println("成功");
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println("发送请求失败");
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch(Exception e) {
                System.out.println("请求输出流关闭失败");
                e.printStackTrace();
            }
            try{
                conn.disconnect();
//                cookies = getCookies(conn);
                //请求不包含cookie，尝试请求完成后获取
                if(this.cookies==null||this.cookies==""){
                    String header_cookie = conn.getHeaderField("Set-Cookie");
                    if(header_cookie!=null){
                        cookies = conn.getHeaderField("Set-Cookie").split(";")[0];
                    }
                }
            }catch(Exception e) {
                System.out.println("链接关闭失败|获取cookie失败");
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
    /**
     * 添加信任主机
     */
    private void trustAllHosts() {
        // 创建不验证证书链的信任管理器 这里使用的是x509证书
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        }};
        // 安装所有信任的信任管理器
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            //HttpsURLConnection通过SSLSocket来建立与HTTPS的安全连接，SSLSocket对象是由SSLSocketFactory生成的。
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            System.out.println("添加信任主机失败");
            e.printStackTrace();
        }
    }
    /**
     * map转url参数    a=111&b=222&c=333
     */
    public static String map2Url(Map<String, String> paramToMap) {
        if (null == paramToMap || paramToMap.isEmpty()) {
            return "";
        }
        StringBuffer url  = new StringBuffer();
        boolean isfist = true;
        for (Map.Entry<String, String> entry : paramToMap.entrySet()) {
            if (isfist) {
                isfist = false;
            } else {
                url.append("&");
            }
            url.append(entry.getKey()).append("=");
            String value = entry.getValue();
            if (!StringUtils.isEmpty(value)) {
                try {
                    url.append(URLEncoder.encode(value, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return url.toString();
    }
    public static void main(String[] args) {
//        String login_url = "http://192.168.200.101:8080/jzLog/users/login.do";
//        String phone = "15069109870";
//        String password = "123456";
//        Map<String, String> params = new HashMap<>();
//        params.put("phone",phone);
//        params.put("password", password);
//        UrlPost httpsPost = new UrlPost();
//        httpsPost.setUrl(login_url);
//        httpsPost.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
//        httpsPost.setParams(params);
//        httpsPost.setMethod("POST");
//        httpsPost.sendHttpPost();
//        String stop_url = "http://192.168.200.101:8080/jzLog/collector/stopKafkaCollector.do";
//        httpsPost.setUrl(stop_url);
//        httpsPost.sendHttpPost();
//

        String protocol = "";
        String phone = "";
        String password = "";
        String login_url = "";
        if(args.length>=4){
            //参数
            //0.协议
            protocol = args[0];
            //1.账号
            phone = args[1];
            //2.密码
            password = args[2];
            //3.登录url
            login_url = args[3];
            //4-n. 执行的请求，可以为多个
            //组装账号密码
            Map<String, String> params = new HashMap<>();
            params.put("phone",phone);
            params.put("password", password);
            //判断协议类型
            if("https".equals(protocol)){
                UrlPost httpsPost = new UrlPost();
                httpsPost.setUrl(login_url);
                httpsPost.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
                httpsPost.setParams(params);
                httpsPost.setMethod("POST");
                httpsPost.sendHttpsPost();
                //执行的请求，可以为多个
                for(int i=3;i<args.length;i++){
                    httpsPost.setUrl(args[i]);
                    httpsPost.sendHttpsPost();
                }
            }else{
                UrlPost httpPost = new UrlPost();
                httpPost.setUrl(login_url);
                httpPost.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
                httpPost.setParams(params);
                httpPost.setMethod("POST");
                httpPost.sendHttpPost();
                //执行的请求，可以为多个
                for(int i=3;i<args.length;i++){
                    httpPost.setUrl(args[i]);
                    httpPost.sendHttpPost();
                }
            }


        }else{
            System.out.println("参数错误,参数格式如下：");
            System.out.println("0.协议 http/https");
            System.out.println("1.账号");
            System.out.println("2.密码");
            System.out.println("3.登录的url");
            System.out.println("4-N.需调用的url");

        }




    }
}

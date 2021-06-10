package com.jz.bigdata.util.post;

import com.jz.bigdata.util.MapUtil;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @program: idea
 * @description:
 * @author: Savilio
 * @create: 2021-04-25 15:45
 **/

public class HttpsPostTest2 {

    static String cookies;


    public void test() {
        Map<String, String> params = new HashMap<>();
        params.put("firstName", "Mickey");
        params.put("lastName", "Mouse");
        //要发送的POST请求url?Key=Value&amp;Key2=Value2&amp;Key3=Value3的形式
        //application/json;charset=UTF-8  application/x-www-form-urlencoded;charset=UTF-8
//        sendPost("https://localhost:8888/tests", MapUtil.map2Url(params),"application/x-www-form-urlencoded;charset=UTF-8","POST");
    }

    //添加主机名验证程序类，设置不验证主机
    private final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    //添加信任主机
    private static void trustAllHosts() {
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
            e.printStackTrace();
        }
    }

    //这里可以用main方法测试
    public static void main(String[] args) {
        Map<String, String> params = new HashMap<>();
        params.put("phone", "GW48a9i8vnhscLpRjHofK1EaX8d2qz3ZAi69bQslY47Umu/fgZBZxShgARpD+p8ktGK+AGuI+H1uPh85TBNBfqsc5ArLr+s/jIrJIkjB04INLPF5zAmKX8F/F+8bfpHTxgShwvYigPu7acm3ctl+BVqbA64Ak/ynXPxFReewGS4=");
        params.put("password", "fU+jtD/k15+quew56OA0n8Rua8bIxeBRjmbYV4GRGPsKh6jyF98zWIpWKDWF9kJazDSHDaa7SYVWOSRlWTNUUtmLYowyQk1kL79+BBhw99W7PTFVmGftfEx1ZFoxgZAxDvxNt79Sh4L2y1g6lfxtEIbGprc+ZJbsaXFkVQFoBLs=");
        //要发送的POST请求url?Key=Value&amp;Key2=Value2&amp;Key3=Value3的形式
        //application/json;charset=UTF-8  application/x-www-form-urlencoded;charset=UTF-8
//        sendPost("https://192.168.2.181/hslog/user/login.do",MapUtil.map2Url(params),"application/x-www-form-urlencoded;charset=UTF-8","POST");

//        String cookie = cookies;
//        sendPostWithCookie("https://192.168.2.181/hslog/collector/startSyslogKafkaListener.do",MapUtil.map2Url(null),"application/x-www-form-urlencoded;charset=UTF-8","POST",cookies);
    }

    public static String sendPostWithCookie(String urls, String param, String contentType, String method,String cookies) {
        StringBuffer sb=new StringBuffer();
        DataOutputStream out = null;
        BufferedReader responseReader = null;
        InputStream in1 = null;
        HttpsURLConnection conn = null;
        try {
            trustAllHosts();
            // 创建url资源
            URL url = new URL(urls);
            // 建立http连接


            conn = (HttpsURLConnection) url.openConnection();
            System.out.println(cookies.length());
            conn.setRequestProperty("Cookie", cookies);
            conn.setHostnameVerifier(DO_NOT_VERIFY);
            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置允许输出
            conn.setDoOutput(true);
            // 设置允许输入
            conn.setDoInput(true);
            // 设置传递方式
            conn.setRequestMethod(method);
            System.out.println(conn.getRequestMethod());
            // 设置维持长连接
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 设置文件字符集:
            conn.setRequestProperty("Charset", "UTF-8");
            // 转换为字节数组
//            byte[] data = (param).getBytes();
//            // 设置文件长度
//            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            // 设置文件类型:
            conn.setRequestProperty("Content-Type", contentType);

//            conn.setRequestProperty("Cookie", cookies);

            // 开始连接请求
            conn.connect();
            out = new DataOutputStream(conn.getOutputStream());
            // 写入请求的字符串
            out.writeBytes(param);
            out.flush();

            System.out.println(conn.getResponseCode());

            // 请求返回的状态
//            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
//                System.out.println("连接成功");
//                // 请求返回的数据
//                in1 = conn.getInputStream();
//                String readLine;
//                responseReader = new BufferedReader(new InputStreamReader(in1,"UTF-8"));
//                while((readLine=responseReader.readLine()) != null){
//                    sb.append(readLine).append("\n");
//                }
//
//
//            } else {
//                System.out.println("error++");
//            }
            InputStream is = null;
            int status = conn.getResponseCode();
            if(status>= HttpStatus.SC_BAD_REQUEST){  //此处一定要根据返回的状态码state来初始化输入流。如果为错误
                is = conn.getErrorStream();
                System.out.println("失败:"+status);
            }else{
                //如果正确
                is = conn.getInputStream();
                System.out.println("成功");
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {

        } finally {
            try {
                if (null != responseReader)
                    responseReader.close();
                if (null != in1)
                    in1.close();
            } catch(Exception e) {}
            try {
                out.close();
            } catch(Exception e) {}
            try{
                conn.disconnect();
//                cookies = getCookies(conn);
                String cookies_value = conn.getHeaderField("Set-Cookie");
                cookies = cookies_value.split(";")[0];
            }catch(Exception e) {}
        }

        return sb.toString();
    }

    /**
     *  发送post 数据
     * @param urls
     * @return
     */
    public static String sendPost(String urls, String param, String contentType, String method) {
        StringBuffer sb=new StringBuffer();
        DataOutputStream out = null;
        BufferedReader responseReader = null;
        InputStream in1 = null;
        HttpsURLConnection conn = null;
        try {
            trustAllHosts();
            // 创建url资源
            URL url = new URL(urls);
            // 建立http连接
            conn = (HttpsURLConnection) url.openConnection();
            conn.setHostnameVerifier(DO_NOT_VERIFY);
            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置允许输出
            conn.setDoOutput(true);
            // 设置允许输入
            conn.setDoInput(true);
            // 设置传递方式
            conn.setRequestMethod(method);
            System.out.println(conn.getRequestMethod());
            // 设置维持长连接
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 设置文件字符集:
            conn.setRequestProperty("Charset", "UTF-8");
            // 转换为字节数组
//            byte[] data = (param).getBytes();
//            // 设置文件长度
//            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            // 设置文件类型:
            conn.setRequestProperty("Content-Type", contentType);
            // 开始连接请求
                conn.connect();
            out = new DataOutputStream(conn.getOutputStream());
            // 写入请求的字符串
            out.writeBytes(param);
            out.flush();

            System.out.println(conn.getResponseCode());

            // 请求返回的状态
//            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
//                System.out.println("连接成功");
//                // 请求返回的数据
//                in1 = conn.getInputStream();
//                String readLine;
//                responseReader = new BufferedReader(new InputStreamReader(in1,"UTF-8"));
//                while((readLine=responseReader.readLine()) != null){
//                    sb.append(readLine).append("\n");
//                }
//
//
//            } else {
//                System.out.println("error++");
//            }
            InputStream is = null;
            int status = conn.getResponseCode();
            if(status>= HttpStatus.SC_BAD_REQUEST){  //此处一定要根据返回的状态码state来初始化输入流。如果为错误
                is = conn.getErrorStream();
                System.out.println("失败:"+status);
            }else{
                //如果正确
                is = conn.getInputStream();
                System.out.println("成功");
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {

        } finally {
            try {
                if (null != responseReader)
                    responseReader.close();
                if (null != in1)
                    in1.close();
            } catch(Exception e) {}
            try {
                out.close();
            } catch(Exception e) {}
            try{
                conn.disconnect();
//                cookies = getCookies(conn);
                String cookies_value = conn.getHeaderField("Set-Cookie");
                cookies = cookies_value.split(";")[0];
            }catch(Exception e) {}
        }

        return sb.toString();

    }



    @RequestMapping(value="/tests",method = RequestMethod.POST)
    @ResponseBody
    //如果post发送的参数过多，这里提供一个思路，就是序列化或者转成一个指定格式发送后，这边用一个参数解析
    public void tests(HttpServletRequest request, @RequestParam(required = false) String firstName,@RequestParam(required = false) String lastName){
        try {
//            //接收请求参数 如果是application/json;charset=UTF-8，用这种方式接收参数
//            InputStreamReader reader=new InputStreamReader(request.getInputStream());
//            BufferedReader buffer=new BufferedReader(reader);
//            String data = buffer.readLine();
//            System.out.println(data);

            //如果是application/x-www-form-urlencoded，直接在参数上指定map的键即可
            System.out.println(firstName+"  "+lastName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCookies(HttpURLConnection conn) {
        StringBuffer cookies = new StringBuffer();
        String headName;
        for (int i = 7; (headName = conn.getHeaderField(i)) != null; i++) {
            StringTokenizer st = new StringTokenizer(headName, "; ");
            while (st.hasMoreTokens()) {
                cookies.append(st.nextToken() + "; ");
            }
        }
        return cookies.toString();
    }
}

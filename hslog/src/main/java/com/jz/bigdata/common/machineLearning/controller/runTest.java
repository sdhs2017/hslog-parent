package com.jz.bigdata.common.machineLearning.controller;

import java.io.*;
import java.util.concurrent.TimeoutException;

/**
 * @Author: yiyang
 * @Date: 2021/3/15 15:53
 * @Description:
 */
public class runTest {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        /******执行结果一次全部打印*******/
        try {
            //String a=getPara("car").substring(1),b="D34567",c="LJeff34",d="iqngfao";
            //String[] args1=new String[]{ "python", "D:\\pyworkpeace\\9_30_1.py", a, b, c, d };
            //Process pr=Runtime.getRuntime().exec(args1);
            String url="http://blog.csdn.net/thorny_v/article/details/61417386";
            String[] args1 = new String[] { "E:\\work\\hs\\MachineLearning\\ADLTI_py\\dist\\hello.exe", url};

            //windows调用命令
            Process pr = Runtime.getRuntime().exec("D:\\Anaconda3\\Scripts\\activate.bat && conda activate detection_anomalies_env && python -u E:\\work\\hs\\MachineLearning\\ADLTI_py\\detectAnomalies.py E:/work/hs/MachineLearning/files/review.yaml train");
            //Process pr=Runtime.getRuntime().exec(args1);

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    pr.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            pr.waitFor();
            System.out.println("java-print-end");
        }catch (Exception e) {
            e.printStackTrace();
        }
        /************************************************************/
        // 参数1 exe的绝对路径 参数2 -i  参数3 las文件目录  23执行exe程序必须的参数
//        ProcessBuilder processBuilder = new ProcessBuilder(
//                "E:\\work\\hs\\MachineLearning\\ADLTI_py\\dist\\hello.exe","12344","111111","23456");
//        processBuilder.redirectErrorStream(true);
//        Process process = processBuilder.start();
//        System.out.println("start: " + process.isAlive());
//        StringBuilder processOutput = new StringBuilder();
//        try (BufferedReader processOutputReader = new BufferedReader(
//                new InputStreamReader(process.getInputStream()));) {
//            String readLine;
//            while ((readLine = processOutputReader.readLine()) != null) {
//                System.out.println(readLine);
//                processOutput.append(readLine + System.lineSeparator());//System.lineSeparator 行分隔符
//            }
//            //process.waitFor();
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//        //catch (InterruptedException e) {
//        //  System.out.println(e.getMessage());
//        //}
//        finally {
//            if (process != null) {
//                process.destroy();
//            }
//        }
        /************************************************************/
//        Connection conn = new Connection("192.168.2.181");//参数传服务器的地址
//        conn.connect();
//        boolean isAuthenticated=conn.authenticateWithPassword("root", "jzdata.123");//返回true代表可正常登录服务器
//        if(!isAuthenticated) {
//            System.out.println("用户名或密码错误");
//            throw new IOException("Authentication failed!");
//        }
//
//        Session session=conn.openSession();
//        session.execCommand("tail -200f /home/log/tomcat/catalina.out");
//        InputStream stdOut=new StreamGobbler(session.getStdout());
//        StringBuffer sb=new StringBuffer();
//        byte[] bys=new byte[1024];
//        int len=0;
//        while((len=stdOut.read(bys))!=-1) {
//            sb.append(new String(bys,0,len));
//        }
//        String res=sb.toString();
//        System.out.println(res);
//
//        //这是接收错误信息
//        InputStream stdErr=new StreamGobbler(session.getStderr());
//        sb=new StringBuffer();
//        bys=new byte[1024];
//        len=0;
//        while((len=stdErr.read(bys))!=-1) sb.append(new String(bys,0,len));
//        String err=sb.toString();
//        System.out.println("错误是:"+err);
//
////session.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.EXIT_STATUS , 30000);
//// 等待，除非1.连接关闭；2.输出数据传送完毕；3.进程状态为退出；4.超时
////调用这个方法在执行linux命令时，会避免环境变量读取不全的问题，这里有许多标识可以用，比如当exit命令执行后或者超过了timeout时间,则session关闭
//        session.waitForCondition(ChannelCondition.EXIT_STATUS, 30000);//在调用getExitStatus时，要先调用WaitForCondition方法
//
////一般情况下shell脚本正常执行完毕，getExitStatus方法返回0。
////此方法通过远程命令取得Exit Code/status。
////但并不是每个server设计时都会返回这个值，如果没有则会返回null。
////getExitStatus的返回值，可以认为是此次执行是否OK的标准。
//        int ret=session.getExitStatus()==null?0:session.getExitStatus();
//
//
////        new ProcessExecutor().command("E:\\work\\hs\\MachineLearning\\ADLTI_py\\dist\\hello.exe", "")
////                .redirectOutput(new LogOutputStream() {
////                    @Override
////                    protected void processLine(String line) {
////                        System.out.println(line);
////                    }
////                })
////                .execute();
    }
}

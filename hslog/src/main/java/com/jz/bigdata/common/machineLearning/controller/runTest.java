package com.jz.bigdata.common.machineLearning.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Author: yiyang
 * @Date: 2021/3/15 15:53
 * @Description:
 */
public class runTest {
    public static void main(String[] args) throws IOException {
        /******执行结果一次全部打印*******/
//        try {
//            //String a=getPara("car").substring(1),b="D34567",c="LJeff34",d="iqngfao";
//            //String[] args1=new String[]{ "python", "D:\\pyworkpeace\\9_30_1.py", a, b, c, d };
//            //Process pr=Runtime.getRuntime().exec(args1);
//            String url="http://blog.csdn.net/thorny_v/article/details/61417386";
//            String[] args1 = new String[] { "E:\\work\\hs\\MachineLearning\\ADLTI_py\\dist\\hello.exe", url};
//            Process pr=Runtime.getRuntime().exec(args1);
//            BufferedReader in = new BufferedReader(new InputStreamReader(
//                    pr.getInputStream()));
//            String line;
//            while ((line = in.readLine()) != null) {
//                System.out.println("python-print:"+line);
//            }
//            in.close();
//            pr.waitFor();
//            System.out.println("java-print-end");
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
        // 参数1 exe的绝对路径 参数2 -i  参数3 las文件目录  23执行exe程序必须的参数
        ProcessBuilder processBuilder = new ProcessBuilder(
                "E:\\work\\hs\\MachineLearning\\ADLTI_py\\dist\\hello.exe","12344","111111","23456");
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        System.out.println("start: " + process.isAlive());
        StringBuilder processOutput = new StringBuilder();
        try (BufferedReader processOutputReader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));) {
            String readLine;
            while ((readLine = processOutputReader.readLine()) != null) {
                System.out.println(readLine);
                processOutput.append(readLine + System.lineSeparator());//System.lineSeparator 行分隔符
            }
            //process.waitFor();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        //catch (InterruptedException e) {
        //  System.out.println(e.getMessage());
        //}
        finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
}

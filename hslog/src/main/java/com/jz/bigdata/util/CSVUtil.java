package com.jz.bigdata.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CSVUtil {

    /**
     * CSV文件生成方法
     * @param head
     * @param dataList
     * @param outPutPath
     * @param filename
     * @return
     */
    public static File createCSVFile(List<Object> head, List<List<Object>> dataList,
            String outPutPath, String filename) {

        File csvFile = null;
        BufferedWriter csvWtriter = null;
        try {
            csvFile = new File(outPutPath + File.separator + filename + ".csv");
            File parent = csvFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            csvFile.createNewFile();

            // GB2312使正确读取分隔符","
            csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    csvFile), "GB2312"), 1024);
            // 写入文件头部
            writeRow(head, csvWtriter);

            // 写入文件内容
            for (List<Object> row : dataList) {
                writeRow(row, csvWtriter);
            }
            csvWtriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                csvWtriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return csvFile;
    }

    public static File createCSVFile(List<Object> head, List<Map<String, Object>> dataList,
            String outPutPath, String filename,String ssss) {

        File csvFile = null;
        BufferedWriter csvWtriter = null;
        try {
            csvFile = new File(outPutPath + File.separator + filename + ".csv");
            File parent = csvFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            csvFile.createNewFile();

            // GB2312使正确读取分隔符","
            csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    csvFile), "GB2312"), 1024);
            // 写入文件头部
            writeRow(head, csvWtriter);

            // 写入文件内容
            for (Map<String, Object> row : dataList) {
                writeRow(row, csvWtriter);
            }
            csvWtriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                csvWtriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return csvFile;
    }

    /**
     *
     * @param headKey  显示的列名对应的字段，用于数据获取
     * @param headValue 文件中显示的列名
     * @param dataList 数据
     * @param outPutPath 输出路径
     * @param filename 文件名
     * @return
     */
    public static File createCSVFile(List<String> headKey,List<String> headValue, List<Map<String, Object>> dataList,
                                     String outPutPath, String filename) {

        File csvFile = null;
        BufferedWriter csvWtriter = null;
        try {
            csvFile = new File(outPutPath + File.separator + filename + ".csv");
            File parent = csvFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            csvFile.createNewFile();

            // GB2312使正确读取分隔符","
            csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    csvFile), "GB2312"), 1024);
            // 写入文件头部
            String head = String.join(",",  headValue);
            csvWtriter.write(head);
            csvWtriter.newLine();
            // 写入文件内容
            for (Map<String, Object> row : dataList) {
                //
                writeRowByKey(headKey,row, csvWtriter);
            }
            csvWtriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                csvWtriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return csvFile;
    }
    /**
     * 写一行数据方法
     * @param row
     * @param csvWriter
     * @throws IOException
     */
    private static void writeRow(List<Object> row, BufferedWriter csvWriter) throws IOException {
        // 写入文件头部
        for (Object data : row) {
            StringBuffer sb = new StringBuffer();
            String rowStr = sb.append("\"").append(data).append("\",").toString();
            csvWriter.write(rowStr);
        }
        csvWriter.newLine();
    }
    /**
     * 写一行数据方法，数据通过源数据getkey获取
     * 类似处理前端显示5个字段，请求返回的数据是>5个字段的
     * @param row
     * @param csvWriter
     * @throws IOException
     */
    private static void writeRowByKey(List<String> headKey,Map<String, Object> row, BufferedWriter csvWriter) throws IOException {
        // 写入文件头部
        for (int i=0;i<headKey.size() ;i++) {
            StringBuffer sb = new StringBuffer();
            String rowStr;
            //最后一列不加逗号
            if(i==headKey.size()-1){
                rowStr = sb.append("\"").append(row.get(headKey.get(i))).append("\"").toString();
            }else{
                rowStr = sb.append("\"").append(row.get(headKey.get(i))).append("\",").toString();
            }
            csvWriter.write(rowStr);
        }
        csvWriter.newLine();
    }
    /**
     * 写一行数据方法
     * @param row
     * @param csvWriter
     * @throws IOException
     */
	private static void writeRow(Map<String, Object> row, BufferedWriter csvWriter) throws IOException {
    	// 写入文件头部
    	/*for (Object data : row) {
    		StringBuffer sb = new StringBuffer();
    		String rowStr = sb.append("\"").append(data).append("\",").toString();
    		csvWriter.write(rowStr);
    	}
		Object[] head = {"@timestamp", "type", "operation_level", "equipmentname", "ip", "operation_des" };
		for (Object data : head) {
    		StringBuffer sb = new StringBuffer();
    		String rowStr = sb.append("\"").append(row.get(data)).append("\",").toString();
    		csvWriter.write(rowStr);
    	}
		*/
		//时间
        csvWriter.write(new StringBuffer().append("\"").append(row.get("@timestamp")).append("\",").toString());
        //日志类型
        csvWriter.write(new StringBuffer().append("\"").append(row.get("agent")!=null?(((Map<String,Object>)row.get("agent")).get("type")!=null?((Map<String,Object>)row.get("agent")).get("type").toString():""):"").append("\",").toString());
        //日志级别
        csvWriter.write(new StringBuffer().append("\"").append(row.get("log")!=null?(((Map<String,Object>)row.get("log")).get("level")!=null?((Map<String,Object>)row.get("log")).get("level").toString():""):"").append("\",").toString());
        //资产名称
        csvWriter.write(new StringBuffer().append("\"").append(row.get("fields")!=null?(((Map<String,Object>)row.get("fields")).get("equipmentname")!=null?((Map<String,Object>)row.get("fields")).get("equipmentname").toString():""):"").append("\",").toString());
        //资产IP
        csvWriter.write(new StringBuffer().append("\"").append(row.get("fields")!=null?(((Map<String,Object>)row.get("fields")).get("ip")!=null?((Map<String,Object>)row.get("fields")).get("ip").toString():""):"").append("\",").toString());
        //日志内容
        csvWriter.write(new StringBuffer().append("\"").append(row.get("message")).append("\",").toString());
    	csvWriter.newLine();
    }
    
    
   /* public static void main(String [] agrs) {
		Client client = new Client();
		List<Map<String, Object>> list = client.getlist("eslog-analysis", "syslog");
		
		for(Map<String, Object> map : list){
			System.out.println(map);
		}
		
		// 设置表格头
        Object[] head = {"时间", "日志类型", "日志级别", "资产名称", "资产IP", "日志内容" };
        List<Object> headList = Arrays.asList(head);
		
        createCSVFile(headList, list, "D:\\Computer_Science", "test",null);
		
		
	}*/


}

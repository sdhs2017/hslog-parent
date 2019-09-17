package com.jz.bigdata.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
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
    	}*/
		Object[] head = {"logtime", "type", "operation_level", "equipmentname", "ip", "operation_des" };
		for (Object data : head) {
    		StringBuffer sb = new StringBuffer();
    		String rowStr = sb.append("\"").append(row.get(data)).append("\",").toString();
    		csvWriter.write(rowStr);
    	}
		
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

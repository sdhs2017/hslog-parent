package com.jz.bigdata.common.machineLearning.service.impl;

import com.csvreader.CsvReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.jz.bigdata.common.machineLearning.service.IMachineLearningService;
import net.sf.json.JSONArray;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * @Author: yiyang
 * @Date: 2021/3/17 16:56
 * @Description: AI service实现
 */
@Service(value="machineLearningService")
public class MachineLearningServiceImpl implements IMachineLearningService {
    @Autowired
    protected ILogCrudDao logCurdDao;
    //json转化工具
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    //日志格式
    private final DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private final DateTimeFormatter ml_dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    //template 名称，存放检测结果
    private final String template = "hs_ml_result_";
    /**
     * 获取检测结果
     * @return key：timestamp 时间轴 real_value 日志数 color 颜色 size 点的大小
     * @throws Exception
     */
    @Override
    public Map<String,Object> getDetectResult() throws Exception {

        //1.数据入ES
        String now_date_time = LocalDateTime.now().toString(dtf);
        String indexName = template+DateTime.now().toString("yyyy.MM.dd");
        List<Map<String,String>> result = new LinkedList<>();

        //1.1 待检测数据
        //String testFilePath = "E:/work/hs/MachineLearning/files/test.csv";//windows
        String testFilePath = "/opt/ml/files_linux/test.csv";//linux
        // 创建CSV读对象
        CsvReader testCsvReader = new CsvReader(testFilePath);
        // 读表头
        testCsvReader.readHeaders();
        while (testCsvReader.readRecord()){
            Map<String,String> row = new HashMap<>();
            row.put("@timestamp",ml_dtf.parseDateTime(testCsvReader.get("xAxis")).toString(dtf));//时间戳
            row.put("real_value",testCsvReader.get("Count"));//真实值
            row.put("detect_time",now_date_time);//检测时间
            result.add(row);
            // 读一整行
//            System.out.println(testCsvReader.getRawRecord());
            // 读这行的某一列
//            System.out.println(testCsvReader.get("xAxis"));
//            System.out.println(testCsvReader.get("Count"));
        }
        //1.2 检测结果数据

        //String detectResultFilePath = "E:/work/hs/MachineLearning/files/res.csv";//windows
        String detectResultFilePath = "/opt/ml/files_linux/res.csv";//linux
        // 创建CSV读对象
        CsvReader detectResultCsvReader = new CsvReader(detectResultFilePath);
        // 读表头
        detectResultCsvReader.readHeaders();
        while (detectResultCsvReader.readRecord()){
            Map<String,String> row = result.get((int)detectResultCsvReader.getCurrentRecord());
            //row.put("@timestamp",detectResultCsvReader.get("timestamp"));
            row.put("anomaly_value",detectResultCsvReader.get("A-score"));//异常值
        }
//        IndexRequest request = new IndexRequest();
//        request.index(indexName);
//        request.source(gson.toJson(result), XContentType.JSON);
//        logCurdDao.bulkProcessor_add(request);
//        System.out.println(gson.toJson(result));
        //2.返回结果
        //重新组装前端需要的数据结构
        Map<String,Object> chartResult = new HashMap<>();
        List<String> timestamp = new ArrayList<>();
        List<String> real_value = new ArrayList<>();
        List<String> color = new ArrayList<>();
        List<Integer> size = new ArrayList<>();

        for(Map<String,String> row : result){
            //入库
            IndexRequest request = new IndexRequest();
            request.index(indexName);
            request.source(gson.toJson(row), XContentType.JSON);
            logCurdDao.bulkProcessor_add(request);
            //组装新的返回数据
            timestamp.add(row.get("@timestamp"));//时间
            real_value.add(row.get("real_value"));//实际值
            //异常值
            String anomaly_value = row.get("anomaly_value");
            //根据异常值的大小对图表显示效果进行设置
            Double value = Double.parseDouble(anomaly_value);
            //对异常值进行分类，>0.9（节点显示明显）     0.5-0.9（节点显示较明显）   <0.5   （正常节点）
            if(value>=0.9){
                color.add("#FE2D46");//颜色
                size.add(15);//点的大小
            }else if(value>=0.5){
                color.add("#FAA90E");
                size.add(10);
            }else{
                color.add("#5bc0de");
                size.add(5);
            }
        }
        chartResult.put("timestamp",timestamp);
        chartResult.put("real_value",real_value);
        chartResult.put("color",color);
        chartResult.put("size",size);
        return chartResult;
    }
    /**
     * 获取训练数据
     * @return key timestamp: x轴  时间    value ：y轴 日志数
     * @throws Exception
     */
    @Override
    public Map<String, Object> getTrainData() throws Exception {
        //String detectResultFilePath = "E:/work/hs/MachineLearning/files/train0901.csv";//windows
        String detectResultFilePath = "/opt/ml/files_linux/train0901.csv";//linux
        // 创建CSV读对象
        CsvReader detectResultCsvReader = new CsvReader(detectResultFilePath);
        // 读表头
        detectResultCsvReader.readHeaders();
        Map<String, Object> result = new HashMap<>();
        List<String> timestamp = new ArrayList<>();
        List<String> value = new ArrayList<>();
        while (detectResultCsvReader.readRecord()){
            timestamp.add(detectResultCsvReader.get("xAxis"));//时间
            value.add(detectResultCsvReader.get("Count"));//日志数
        }
        result.put("timestamp",timestamp);
        result.put("value",value);
        return result;
    }
    /**
     * 获取待检测数据
     * @return key timestamp: x轴  时间    value ：y轴 日志数
     * @throws Exception
     */
    @Override
    public Map<String, Object> getTestData() throws Exception {
        //String detectResultFilePath = "E:/work/hs/MachineLearning/files/test.csv";//windows
        String detectResultFilePath = "/opt/ml/files_linux/test.csv";//linux
        // 创建CSV读对象
        CsvReader detectResultCsvReader = new CsvReader(detectResultFilePath);
        // 读表头
        detectResultCsvReader.readHeaders();
        Map<String, Object> result = new HashMap<>();
        List<String> timestamp = new ArrayList<>();
        List<String> value = new ArrayList<>();
        while (detectResultCsvReader.readRecord()){
            timestamp.add(detectResultCsvReader.get("xAxis"));//时间
            value.add(detectResultCsvReader.get("Count"));//日志数
        }
        result.put("timestamp",timestamp);
        result.put("value",value);
        return result;
    }

}

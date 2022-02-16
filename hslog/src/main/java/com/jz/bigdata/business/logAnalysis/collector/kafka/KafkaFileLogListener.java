package com.jz.bigdata.business.logAnalysis.collector.kafka;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.jz.bigdata.business.logAnalysis.ecs.cn2en.Cn2En;
import com.jz.bigdata.business.logAnalysis.log.LogType;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.asset.entity.Asset;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.fileLog.dao.IFileLogDao;
import com.jz.bigdata.common.fileLog.entity.FileLogField;
import com.jz.bigdata.common.fileLog.service.IFileLogService;
import com.jz.bigdata.common.start_execution.cache.AssetCache;
import com.jz.bigdata.common.start_execution.cache.FileLogCache;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.ContextRoles;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.Acknowledgment;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class KafkaFileLogListener {
    @Resource(name ="configProperty")
    private ConfigProperty configProperty;
    @Autowired
    protected ILogCrudDao logCurdDao;
    @Autowired
    private KafkaListenerEndpointRegistry registry;

    private static final DateTimeFormatter dtf_time = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final DateTimeFormatter dtf_fileLog = DateTimeFormatter.ofPattern("yyyy.MM");

    //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private Equipment equipment;//虚拟资产
    private String ipadress;//ip地址
    private String module;// 所属模块
    private Asset asset;//逻辑资产
    @Resource(name = "FileLogService")
    private IFileLogService fileLogService;

    @KafkaListener(id = "filelog",group = "jd-group",topics = "filelog", containerFactory = "kafkaListenerContainerFactory")
    public void kafkaListenBatch(List<ConsumerRecord<?, String>> records, Acknowledgment ack){
        IndexRequest request;
        try{
            for(ConsumerRecord<?, String> record:records){
                request = new IndexRequest();
                try {
                    String logs = record.value();
                    /**
                     * 将采集到的log转为jsonObject格式
                     */
                    JsonElement jsonElement = new JsonParser().parse(logs);
                    JsonObject jsonObject= jsonElement.getAsJsonObject();

                    //获取数据IP
                    try{
                        ipadress = jsonObject.getAsJsonObject("fields").get("ip").getAsString().replaceAll("\"","");
                    }catch (Exception e){
                        ipadress = null;
                    }

                    //判断IP是否在虚拟资产中

                    //3.0资产识别，打上资产标签
                    //判断是否在已识别资产里————日志类型可识别
                    equipment = AssetCache.INSTANCE.getEquipmentMap().get(ipadress + LogType.LOGTYPE_FILE);
                    if (equipment!=null) {

                        //获取日志类型
                        String beat_type;
                        try{
                            beat_type = jsonObject.getAsJsonObject("@metadata").get("beat").getAsString().replaceAll("\"","");
                        }catch (Exception e){
                            beat_type = null;
                        }
                        if(beat_type.equals(ContextRoles.FILEBEAT)){
                            //CSV文件处理
                            String message = jsonObject.get("message").getAsString();
                            //双引号内的逗号不分割  双引号外的逗号进行分割
                            String[] strArr = message.trim().split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1);

                            //组装将要写入ES的数据
                            Map<String,Object> dataMap = new HashMap<>();
                            //系统自带日期字段，默认值为当前时间
                            dataMap.put("@timestamp",dtf_time.format(LocalDateTime.now()));
                            //查看该数据是否在字段信息中
                            String templateKey = jsonObject.getAsJsonObject("fields").get("template_key").getAsString();
                            List<FileLogField> fieldList = FileLogCache.INSTANCE.getFileLogList().get(templateKey);
                            if(null!=fieldList){
                                //字段信息中，第一个为日期字段，不参于与数据的对应
                                for(int i=1;i<fieldList.size();i++){
                                    //字段为timestamp字段
                                    if("true".equals(fieldList.get(i).getFile_log_is_timestamp())){
                                        //将该日期字段写入到timestamp
                                        dataMap.put("@timestamp",dtf_time.format(LocalDateTime.parse(strArr[i-1].replaceAll("\"",""),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
                                    }
                                    dataMap.put(fieldList.get(i).getFile_log_field(),strArr[i-1].replaceAll("\"",""));
                                }
                            }else{
                                List<FileLogField> newList = new ArrayList<>();
                                //字段信息   @timestamp（与其他beat数据保持一致），type：date，序号为0
                                FileLogField fileLogField_timestamp = new FileLogField(templateKey,templateKey,"@timestamp","日期","date",null,null,0);
                                newList.add(fileLogField_timestamp);
                                int length = strArr.length;
                                //最后一列如果为空，不计算该列的信息
                                if("".equals(strArr[strArr.length-1])){
                                    length = length-1;
                                }
                                for(int i=1;i<=length;i++){
                                    //dataMap.put("field"+String.format("%0" + String.valueOf(strArr.length).length() + "d", i),strArr[i-1].replaceAll("\"",""));
                                    dataMap.put("field"+ i,strArr[i-1].replaceAll("\"",""));
                                    FileLogField fileLogField = new FileLogField(templateKey,templateKey,"field"+ i,"field"+ i,"text",null,null,i);
                                    newList.add(fileLogField);
                                }
                                //更新数据库
                                fileLogService.insert(newList);
                            }
                            //获取当前月份
                            String year_month = dtf_fileLog.format(LocalDateTime.now());
                            //添加资产信息 id
                            Map<String,String> fieldsMap = new HashMap<>();
                            fieldsMap.put("equipmentid",equipment.getId());
                            fieldsMap.put("ip",equipment.getIp());
                            fieldsMap.put("quipmentname",equipment.getName());
                            dataMap.put("fields",fieldsMap);
                            //写入ES
                            request.index((configProperty.getEs_filelog_pre()+templateKey+"_"+year_month).toLowerCase());
                            request.source(JSONObject.fromObject(dataMap).toString(), XContentType.JSON);
                            logCurdDao.bulkProcessor_add(request);
                            //判断是否在已识别资产里————日志类型可识别
                            //equipment = AssetCache.INSTANCE.getEquipmentMap().get(ipadress + LogType.LOGTYPE_FILE);
                            //如果该资产在虚拟资产列表中
                            //if(null != equipment){
                                //TODO 数据中是否写入资产信息
                            //}else{
                                //虚拟资产中改ip对应资产不包含filebeat，数据暂时不入库
                            //}
                        }else{
                            log.info("该条数据不属于file Beat！");
                        }
                    }else{
                        log.info("数据不在资产池中，IP:"+ipadress+"--"+logs);
                    }

                }catch (NullPointerException e){
                    e.printStackTrace();
                    log.error("kafka范式化：获取数据出现空指针情况");
                    continue;
                }catch (Exception e){
                    e.printStackTrace();
                    log.error("范式化失败！！！！");
                    continue;
                }
            }

        }catch(Exception e){
            e.printStackTrace();
            log.error("kafka—beats消费数据处理异常："+e.getMessage());
        }
        ack.acknowledge();//提交偏移量
    }
}
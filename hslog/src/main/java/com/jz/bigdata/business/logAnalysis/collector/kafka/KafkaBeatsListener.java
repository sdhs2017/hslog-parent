package com.jz.bigdata.business.logAnalysis.collector.kafka;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.jz.bigdata.business.logAnalysis.ecs.cn2en.Cn2En;
import com.jz.bigdata.business.logAnalysis.log.LogType;
import com.jz.bigdata.common.asset.cache.AssetCache;
import com.jz.bigdata.common.asset.entity.Asset;
import com.jz.bigdata.common.configuration.cache.ConfigurationCache;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.ContextRoles;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.Acknowledgment;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class KafkaBeatsListener {
    private static Logger logger = Logger.getLogger(KafkaConfig.class);
    @Resource(name ="configProperty")
    private ConfigProperty configProperty;
    @Autowired
    protected ILogCrudDao logCurdDao;
    @Autowired
    private KafkaListenerEndpointRegistry registry;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private Equipment equipment;//虚拟资产
    private String ipadress;//ip地址
    private String module;// 所属模块
    private Asset asset;//逻辑资产

    @KafkaListener(id = "beats",group = "jd-group",topics = "beats", containerFactory = "kafkaListenerContainerFactory")
    public void kafkaListenBatch(List<ConsumerRecord<?, String>> records, Acknowledgment ack){
        IndexRequest request;
        try{
            for(ConsumerRecord<?, String> record:records){
                request = new IndexRequest();
                try {
                    String log = record.value();
                    /**
                     * 将采集到的log转为jsonObject格式
                     */
                    JsonElement jsonElement = new JsonParser().parse(log);
                    JsonObject jsonObject= jsonElement.getAsJsonObject();

                    //获取数据IP
                    try{
                        ipadress = jsonObject.getAsJsonObject("fields").get("ip").getAsString().replaceAll("\"","");
                    }catch (Exception e){
                        ipadress = null;
                    }

                    //判断IP是否在虚拟资产中
                    //TODO 目前资产对外都是通过虚拟资产功能进行录入，逻辑资产还未体现。后续资产的判定条件还会根据资产关联性进行调整。
                    if (AssetCache.INSTANCE.getIpAddressSet().contains(ipadress)) {

                        //获取日志类型
                        String beat_type;
                        try{
                            beat_type = jsonObject.getAsJsonObject("@metadata").get("beat").getAsString().replaceAll("\"","");
                        }catch (Exception e){
                            beat_type = null;
                        }
                        //日志类型的判定
                        if(beat_type.equals(ContextRoles.WINLOGBEAT)){
                            //判断是否在已识别资产里————日志类型可识别
                            equipment = AssetCache.INSTANCE.getEquipmentMap().get(ipadress + LogType.LOGTYPE_WINLOG);
                            //如果该资产在虚拟资产列表中
                            if(null != equipment){

                                // 判断winlogbeat的版本
                                String beat_version;
                                try{
                                    beat_version = jsonObject.getAsJsonObject("@metadata").get("version").getAsString().replaceAll("\"","");
                                }catch (Exception e){
                                    beat_version = null;
                                }

                                String loglevel;
                                // 6.4.2版本单独处理数据
                                if (beat_version.equals("6.4.2")) {
                                    loglevel = jsonObject.get("level").toString().replaceAll("\"", "").toLowerCase();
                                    /**
                                     * 判断日志级别是否是中文，如果是中文改成英文
                                     */
                                    switch (loglevel) {
                                        case ContextRoles.LOG_LEVEL_INFO:
                                            loglevel = "information";
                                            break;
                                        case ContextRoles.LOG_LEVEL_WARN:
                                            loglevel = "warning";
                                            break;
                                        case ContextRoles.LOG_LEVEL_ERROR:
                                            loglevel = "error";
                                            break;
                                    }
                                    // 判断该日志级别的数据是否需要收集
                                    if (AssetCache.INSTANCE.getEquipmentLogLevel().get(equipment.getId()).indexOf(loglevel)!=-1) {
                                        JsonObject jsonObject_6 = new JsonObject();
                                        /**  时间数据 */
                                        String beatdate = jsonObject.get("@timestamp").getAsString();
                                        try {
                                            Date date = format.parse(beatdate);
                                            Calendar cal = Calendar.getInstance();
                                            cal.setTime(date);
                                            cal.add(Calendar.HOUR_OF_DAY, +8);
                                            date = cal.getTime();
                                            jsonObject_6.addProperty("@timestamp", format.format(date));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        /** @metadata数据 */
                                        jsonObject_6.add("@metadata", jsonObject.getAsJsonObject("@metadata"));
                                        /**  log-level数据  */
                                        JsonObject log_level = new JsonObject();
                                        log_level.addProperty("level", loglevel);
                                        jsonObject_6.add("log", log_level);
                                        /**  fields数据  */
                                        JsonObject fields = new JsonObject();
                                        fields.addProperty("ip", ipadress);
                                        //补全虚拟资产信息
                                        fields.addProperty("equipmentname", equipment.getName());
                                        fields.addProperty("equipmentid", equipment.getId());
                                        fields.addProperty("userid", equipment.getUserId());
                                        fields.addProperty("deptid", equipment.getDepartmentId());
                                        /**
                                         * 打标签，认为beats的数据都是正常的，设置fields.failure=false
                                         */
                                        fields.addProperty("failure",false);
                                        //如果IP在逻辑资产列表中，加上逻辑资产标签
                                        asset = AssetCache.INSTANCE.getAssetMap().get(ipadress);
                                        if(asset!=null){
                                            fields.addProperty("assetid", asset.getId());
                                            fields.addProperty("assetname", asset.getName());
                                        }
                                        jsonObject_6.add("fields", fields);
                                        /** event数据 */
                                        JsonObject event = new JsonObject();
                                        try {
                                            event.addProperty("code", jsonObject.get("event_id").getAsString().replaceAll("\"", ""));
                                            event.addProperty("module", jsonObject.get("log_name").getAsString().replaceAll("\"", ""));
                                        } catch (Exception e) {

                                        }
                                        jsonObject_6.add("event", event);
                                        /** user **/
                                        JsonObject user = new JsonObject();
                                        try {
                                            user.addProperty("id", jsonObject.getAsJsonObject("user").get("identifier").getAsString().replaceAll("\"", ""));
                                            user.addProperty("name", jsonObject.getAsJsonObject("user").get("name").getAsString().replaceAll("\"", ""));
                                            user.addProperty("domain", jsonObject.getAsJsonObject("user").get("domain").getAsString().replaceAll("\"", ""));
                                        } catch (Exception e) {

                                        }
                                        jsonObject_6.add("user", user);

                                        /** agent **/
                                        JsonObject agent = new JsonObject();
                                        agent.addProperty("type", beat_type);
                                        jsonObject_6.add("agent", agent);

                                        /** agent **/
                                        JsonObject host = new JsonObject();
                                        String hostname = jsonObject.getAsJsonObject("host").get("name").getAsString().replaceAll("\"", "");
                                        host.addProperty("name", hostname);
                                        host.addProperty("hostname", hostname);
                                        host.addProperty("architecture", "-");
                                        JsonObject os = new JsonObject();
                                        os.addProperty("family", "windows");
                                        os.addProperty("name", "windows XP");
                                        os.addProperty("kernel", "-");
                                        os.addProperty("build", "-");
                                        os.addProperty("platform", "windows");
                                        os.addProperty("version", "-");
                                        host.add("os",os);
                                        jsonObject_6.add("host", host);
                                        /** message，存在message为空的情况获取message_error */
                                        jsonObject_6.add("message", jsonObject.get("message")!=null?jsonObject.get("message"):jsonObject.get("message_error"));

                                        /**
                                         * index名称设置
                                         */
                                        String date = jsonObject.get("@timestamp").getAsString().substring(0,jsonObject.get("@timestamp").getAsString().indexOf("T")).replaceAll("-",".");
                                        //获取所属模块，该字段信息不一定存在
                                        try {
                                            module = jsonObject.getAsJsonObject("fields").get("module").getAsString().replaceAll("\"","");
                                        }catch (Exception e){
                                            module = null;
                                        }
                                        //组装index信息,存在module字段时，index名称中插入module
                                        String index = Strings.isNullOrEmpty(module)?(beat_type+"-"+date):(beat_type+"-"+module+"-"+date);
                                        //批量入库
                                        request.index(index);
                                        request.source(jsonObject_6.toString(), XContentType.JSON);
                                        logCurdDao.bulkProcessor_add(request);
                                    }else {
                                        //数据的日志级别不在需收集的级别列表中，直接舍弃。
                                    }
                                    // 7.6.2版本的winlogbeat
                                }else {
                                    // 判定应收集的日志级别，通过日志级别进行日志过滤
                                    loglevel = jsonObject.getAsJsonObject("log").get("level").toString().replaceAll("\"","");
                                    /**
                                     * 判断日志级别是否是中文，如果是中文改成英文
                                     */
                                    switch (loglevel){
                                        case ContextRoles.LOG_LEVEL_INFO:
                                            loglevel = "information";
                                            jsonObject.getAsJsonObject("log").addProperty("level","information");
                                            break;
                                        case ContextRoles.LOG_LEVEL_WARN:
                                            loglevel = "warning";
                                            jsonObject.getAsJsonObject("log").addProperty("level","warning");
                                            break;
                                        case ContextRoles.LOG_LEVEL_ERROR:
                                            loglevel = "error";
                                            jsonObject.getAsJsonObject("log").addProperty("level","error");
                                            break;
                                    }
                                    if (AssetCache.INSTANCE.getEquipmentLogLevel().get(equipment.getId()).indexOf(loglevel)!=-1) {
                                        //补全虚拟资产信息
                                        jsonObject.getAsJsonObject("fields").addProperty("equipmentname", equipment.getName());
                                        jsonObject.getAsJsonObject("fields").addProperty("equipmentid", equipment.getId());
                                        jsonObject.getAsJsonObject("fields").addProperty("userid", equipment.getUserId());
                                        jsonObject.getAsJsonObject("fields").addProperty("deptid", equipment.getDepartmentId());
                                        //如果IP在逻辑资产列表中，加上逻辑资产标签
                                        asset = AssetCache.INSTANCE.getAssetMap().get(ipadress);
                                        if(asset!=null){
                                            jsonObject.getAsJsonObject("fields").addProperty("assetid", asset.getId());
                                            jsonObject.getAsJsonObject("fields").addProperty("assetname", asset.getName());
                                        }

                                        /**
                                         * 打标签，认为beats的数据都是正常的，设置fields.failure=false
                                         */
                                        jsonObject.getAsJsonObject("fields").addProperty("failure",false);
                                        /**
                                         * 判断windows日志事件event.action字段是否是中文，如果是中文改成英文
                                         */
                                        String event_action = jsonObject.getAsJsonObject("event").get("action").toString().replaceAll("\"","");
                                        if (Cn2En.event_cn.get(event_action)!=null){
                                            jsonObject.getAsJsonObject("event").addProperty("action",Cn2En.event_cn.get(event_action));
                                        }
                                        /**
                                         * 增加event.action_cn字段，作为event.action的中文对照,没有找到的情况下存""
                                         */
                                        jsonObject.getAsJsonObject("event").addProperty("action_cn",(Cn2En.event_en.get(event_action)!=null?Cn2En.event_en.get(event_action):""));
                                        /**
                                         * winlogbeat发送的日志时间为UTC/GMT 0 (零时区)，需要在原时间上加8小时
                                         */
                                        String beatdate = jsonObject.get("@timestamp").getAsString();
                                        try {
                                            Date date = format.parse(beatdate);
                                            Calendar cal = Calendar.getInstance();
                                            cal.setTime(date);
                                            cal.add(Calendar.HOUR_OF_DAY, +8);
                                            date = cal.getTime();
                                            jsonObject.addProperty("@timestamp",format.format(date));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        /**
                                         * index名称设置
                                         */
                                        String date = jsonObject.get("@timestamp").getAsString().substring(0,jsonObject.get("@timestamp").getAsString().indexOf("T")).replaceAll("-",".");
                                        //获取所属模块，该字段信息不一定存在
                                        try {
                                            module = jsonObject.getAsJsonObject("fields").get("module").getAsString().replaceAll("\"","");
                                        }catch (Exception e){
                                            module = null;
                                        }
                                        //组装index信息,存在module字段时，index名称中插入module
                                        String index = Strings.isNullOrEmpty(module)?(beat_type+"-"+date):(beat_type+"-"+module+"-"+date);
                                        //将index名称和json 直接写入到IndexRequest，将原有的list替换成原生的BulkRequest
                                        //批量入库
                                        //indicesrequests.add(logCurdDao.insertNotCommit(index, null, jsonObject.toString()));
                                        request.index(index);
                                        request.source(jsonObject.toString(), XContentType.JSON);
                                        //bulkRequest.add(request);
                                        // 将bulkrequest替换为bulkprocessor方式
                                        logCurdDao.bulkProcessor_add(request);
                                    }else {
                                        //数据的日志级别不在需收集的级别列表中，直接舍弃。
                                    }
                                }
                            }else{
                                //虚拟资产中改ip对应资产不包含winlogbeat，数据暂时不入库
                            }
                        }else if(beat_type.equals(ContextRoles.PACKETBEAT)){
                            //判断是否在已识别资产里————日志类型可识别
                            equipment = AssetCache.INSTANCE.getEquipmentMap().get(ipadress + LogType.LOGTYPE_PACKET);
                            //如果该资产在虚拟资产列表中
                            if(null != equipment){
                                //补全虚拟资产信息
                                jsonObject.getAsJsonObject("fields").addProperty("equipmentname", equipment.getName());
                                jsonObject.getAsJsonObject("fields").addProperty("equipmentid", equipment.getId());
                                jsonObject.getAsJsonObject("fields").addProperty("userid", equipment.getUserId());
                                jsonObject.getAsJsonObject("fields").addProperty("deptid", equipment.getDepartmentId());
                                //如果IP在逻辑资产列表中，加上逻辑资产标签
                                asset = AssetCache.INSTANCE.getAssetMap().get(ipadress);
                                if(asset!=null){
                                    jsonObject.getAsJsonObject("fields").addProperty("assetid", asset.getId());
                                    jsonObject.getAsJsonObject("fields").addProperty("assetname", asset.getName());
                                }
                                /**
                                 * 打标签，认为beats的数据都是正常的，设置fields.failure=false
                                 */
                                jsonObject.getAsJsonObject("fields").addProperty("failure",false);
                                /**
                                 * winlogbeat发送的日志时间为UTC/GMT 0 (零时区)，需要在原时间上加8小时
                                 */
                                String beatdate = jsonObject.get("@timestamp").getAsString();
                                try {
                                    Date date = format.parse(beatdate);
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(date);
                                    cal.add(Calendar.HOUR_OF_DAY, +8);
                                    date = cal.getTime();
                                    jsonObject.addProperty("@timestamp",format.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                /**
                                 * index名称设置
                                 */
                                String date = jsonObject.get("@timestamp").getAsString().substring(0,jsonObject.get("@timestamp").getAsString().indexOf("T")).replaceAll("-",".");
                                //获取所属模块，该字段信息不一定存在
                                try {
                                    module = jsonObject.getAsJsonObject("fields").get("module").getAsString().replaceAll("\"","");
                                }catch (Exception e){
                                    module = null;
                                }
                                //组装index信息,存在module字段时，index名称中插入module
                                String index = Strings.isNullOrEmpty(module)?(beat_type+"-"+date):(beat_type+"-"+module+"-"+date);
                                //批量入库
                                //indicesrequests.add(logCurdDao.insertNotCommit(index, null, jsonObject.toString()));
                                request.index(index);
                                request.source(jsonObject.toString(), XContentType.JSON);
                                //bulkRequest.add(request);
                                // 将bulkrequest替换为bulkprocessor方式
                                logCurdDao.bulkProcessor_add(request);
                            }else{
                                //虚拟资产中改ip对应资产不包含packetbeat，数据暂时不入库
                            }
                        }else if(beat_type.equals(ContextRoles.METRICBEAT)){
                            //判断是否在已识别资产里————日志类型可识别
                            equipment = AssetCache.INSTANCE.getEquipmentMap().get(ipadress + LogType.LOGTYPE_METRIC);
                            //如果该资产在虚拟资产列表中
                            if(null != equipment){
                                //补全虚拟资产信息
                                jsonObject.getAsJsonObject("fields").addProperty("equipmentname", equipment.getName());
                                jsonObject.getAsJsonObject("fields").addProperty("equipmentid", equipment.getId());
                                jsonObject.getAsJsonObject("fields").addProperty("userid", equipment.getUserId());
                                jsonObject.getAsJsonObject("fields").addProperty("deptid", equipment.getDepartmentId());
                                //如果IP在逻辑资产列表中，加上逻辑资产标签
                                asset = AssetCache.INSTANCE.getAssetMap().get(ipadress);
                                if(asset!=null){
                                    jsonObject.getAsJsonObject("fields").addProperty("assetid", asset.getId());
                                    jsonObject.getAsJsonObject("fields").addProperty("assetname", asset.getName());
                                }
                                /**
                                 * 打标签，认为beats的数据都是正常的，设置fields.failure=false
                                 */
                                jsonObject.getAsJsonObject("fields").addProperty("failure",false);
                                /**
                                 * winlogbeat发送的日志时间为UTC/GMT 0 (零时区)，需要在原时间上加8小时
                                 */
                                String beatdate = jsonObject.get("@timestamp").getAsString();
                                try {
                                    Date date = format.parse(beatdate);
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(date);
                                    cal.add(Calendar.HOUR_OF_DAY, +8);
                                    date = cal.getTime();
                                    jsonObject.addProperty("@timestamp",format.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                /**
                                 * index名称设置
                                 */
                                String date = jsonObject.get("@timestamp").getAsString().substring(0,jsonObject.get("@timestamp").getAsString().indexOf("T")).replaceAll("-",".");
                                //获取所属模块，该字段信息不一定存在
                                try {
                                    module = jsonObject.getAsJsonObject("fields").get("module").getAsString().replaceAll("\"","");
                                }catch (Exception e){
                                    module = null;
                                }
                                //组装index信息,存在module字段时，index名称中插入module
                                String index = Strings.isNullOrEmpty(module)?(beat_type+"-"+date):(beat_type+"-"+module+"-"+date);
                                //批量入库
                                //indicesrequests.add(logCurdDao.insertNotCommit(index, null, jsonObject.toString()));
                                request.index(index);
                                request.source(jsonObject.toString(), XContentType.JSON);
                                //bulkRequest.add(request);
                                // 将bulkrequest替换为bulkprocessor方式
                                logCurdDao.bulkProcessor_add(request);
                            }else{
                                //虚拟资产中改ip对应资产不包含packetbeat，数据暂时不入库
                            }
                        }else{
                            logger.info("该跳数据不属于packet/metric/winlog Beat！");
                        }
                    }else{
                        logger.info("数据不在资产池中，IP:"+ipadress+"--"+log);
                    }

                }catch (NullPointerException e){
                    e.printStackTrace();
                    logger.error("kafka范式化：获取数据出现空指针情况");
                    continue;
                }catch (Exception e){
                    e.printStackTrace();
                    logger.error("范式化失败！！！！");
                    continue;
                }
            }

        }catch(Exception e){
            e.printStackTrace();
            logger.error("kafka—beats消费数据处理异常："+e.getMessage());
        }
        ack.acknowledge();//提交偏移量
    }
}
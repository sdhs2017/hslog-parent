package com.jz.bigdata.business.logAnalysis.collector.kafka;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.jz.bigdata.business.logAnalysis.ecs.cn2en.Cn2En;
import com.jz.bigdata.business.logAnalysis.log.LogType;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.start_execution.cache.AssetCache;
import com.jz.bigdata.common.asset.entity.Asset;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.ContextRoles;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.hadoop.util.hash.Hash;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.Acknowledgment;

import javax.annotation.Resource;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class KafkaBeatsListener {
    @Resource(name ="configProperty")
    private ConfigProperty configProperty;
    @Autowired
    protected ILogCrudDao logCurdDao;
    @Autowired
    private KafkaListenerEndpointRegistry registry;

    private static final DateTimeFormatter dtf_time = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final DateTimeFormatter dtf_time_ymd = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private Equipment equipment;//虚拟资产
    private String ipadress;//ip地址
    private String module;// 所属模块
    private Asset asset;//逻辑资产

    /*
    filebeat-mysql-audit变量
     */
    private String file_type;//文件类型，eg:db（数据库）
    private String db_type;//数据库类型，eg:mysql oracle
    private String log_type;//日志类型，eg:mysql-audit 插件日志，或存在其他审计插件
    //druid 解析sql语句所用到的对象
    private static final SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
    JsonObject message;//用于存放filebeat收取的日志信息，解析成json对象
    JsonElement mysql_audit_date;//mysql-audit中的时间
    LocalDateTime timestamp;//时间转化成localDateTime类型
    String msg_type;//mysql-audit 日志类型，分两种 header和activity。header为mysql 启动信息 。activity为正常sql语句信息
    Set<String> db_names;//存放执行sql涉及到的数据库，方便转成json写入入库数据中
    Set<String> table_names;//表
    Set<String> field_names;//字段
    String query;//执行的sql语句
    List<SQLStatement> stmtList;//sql语句转成druid解析后的对象，原始借口支持多个sql，返回的是一个list
    SQLStatement stmt;//sql语句解析后的对象
    JsonArray audit_objects;//mysql-audit 日志中存放objects对象内容，值为数组，一个对象为一个表的信息（库、表、类型）
    String[] column_info;//druid解析后的字段信息
    String mysql_audit_index_name ;//mysql-audit日志要写入的index
    //存放表信息，key为表名，value为objects数组中一个对象的信息，包括库名、表名、类型
    Map<String,JsonElement> tables;
    TableStat.Column first_column;//通过druid解析出的sql 字段数组中的第一个元素。用来判定是“库.表.字段”还是“表.字段”

    @KafkaListener(id = "beats",group = "jd-group",topics = "beats", containerFactory = "kafkaListenerContainerFactory")
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
                                        //时间+8处理
                                        LocalDateTime ldt = LocalDateTime.parse(beatdate,dtf_time);
                                        jsonObject_6.addProperty("@timestamp", ldt.plusHours(8).format(dtf_time));
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
                                        LocalDateTime ldt = LocalDateTime.parse(beatdate,dtf_time);
                                        jsonObject.addProperty("@timestamp", ldt.plusHours(8).format(dtf_time));
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
                                LocalDateTime ldt = LocalDateTime.parse(beatdate,dtf_time);
                                jsonObject.addProperty("@timestamp", ldt.plusHours(8).format(dtf_time));
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
                                LocalDateTime ldt = LocalDateTime.parse(beatdate,dtf_time);
                                jsonObject.addProperty("@timestamp", ldt.plusHours(8).format(dtf_time));
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
                        }else if(beat_type.equals(ContextRoles.FILEBEAT)){

                            //TODO 变量提出来？
                            //1.判断是否是数据库日志 file_type=db
                            file_type = jsonObject.getAsJsonObject("fields").get("file_type")!=null?jsonObject.getAsJsonObject("fields").get("file_type").getAsString():"";
                            if(Constant.FILEBEAT_FILE_TYPE_DB.equals(file_type)){
                                //2.判断是否是mysql日志 db_type=mysql
                                db_type = jsonObject.getAsJsonObject("fields").get("db_type")!=null?jsonObject.getAsJsonObject("fields").get("db_type").getAsString():"";
                                if(Constant.FILEBEAT_DB_TYPE_MYSQL.equals(db_type)){
                                    //3.判断是否是mysql-audit类型日志
                                    log_type = jsonObject.getAsJsonObject("fields").get("log_type")!=null?jsonObject.getAsJsonObject("fields").get("log_type").getAsString():"";
                                    if(Constant.FILEBEAT_LOG_TYPE_MYSQL_AUDIT.equals(log_type)){
                                        //3.0资产识别，打上资产标签
                                        //判断是否在已识别资产里————日志类型可识别
                                        equipment = AssetCache.INSTANCE.getEquipmentMap().get(ipadress + LogType.LOGTYPE_MYSQLLOG);
                                        if(equipment!=null){

                                            //获取mysql-audit原始数据，并转成json对象，该数据作为入ES的原始数据格式，
                                            message = new JsonParser().parse(jsonObject.get("message").getAsString().replace("\\\"","\"")).getAsJsonObject();
                                            //将fields信息写入message中，message为最终入库的json对象
                                            message.add("fields",jsonObject.getAsJsonObject("fields"));
                                            //补全虚拟资产信息
                                            message.getAsJsonObject("fields").addProperty("equipmentname", equipment.getName());
                                            message.getAsJsonObject("fields").addProperty("equipmentid", equipment.getId());
                                            message.getAsJsonObject("fields").addProperty("userid", equipment.getUserId());
                                            message.getAsJsonObject("fields").addProperty("deptid", equipment.getDepartmentId());
                                            //获取时间戳，并转换格式
                                            mysql_audit_date = message.get("date");
                                            if(mysql_audit_date!=null){
                                                timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(mysql_audit_date.getAsString())), ZoneId.systemDefault());
                                                //将时间戳写入message-json
                                                message.addProperty("@timestamp",timestamp.format(dtf_time));
                                                message.remove("date");//移除原属性
                                                //判断日志内容类型
                                                msg_type = message.get("msg-type").getAsString();
                                                if(Constant.FILEBEAT_MYSQL_AUDIT_MSGTYPE_HEADER.equals(msg_type)){
                                                    //header类型日志
                                                    //header要写入index的内容：msg-type，@timestamp，info，其中info内容为filebeat获取的message信息
                                                    //message
                                                    message = new JsonObject();
                                                    message.addProperty("msg-type",msg_type);
                                                    message.addProperty("@timestamp",timestamp.format(dtf_time));
                                                    message.addProperty("info",jsonObject.getAsJsonObject("message").toString());
                                                }else if(Constant.FILEBEAT_MYSQL_AUDIT_MSGTYPE_ACT.equals(msg_type)){
                                                    //存放数据库名称的集合
                                                    db_names = new HashSet<>();
                                                    //存放 数据库.表名的集合
                                                    table_names = new HashSet<>();
                                                    //存放 数据库.表.字段 的就和
                                                    field_names = new HashSet<>();
                                                    //activity类型日志
                                                    //重新设置获取库、表、字段信息
                                                    //sql语句格式化，使用ali-druid提供的工具,sql语句中可能会存在大量换行符，导致druid无法解析，进行清理。
                                                    query = message.get("query").getAsString().replaceAll("\\\\n","").replaceAll("\\\\t","");
                                                    stmtList = SQLUtils.parseStatements(query, JdbcConstants.MYSQL);
                                                    stmt = stmtList.get(0);

                                                    stmt.accept(statVisitor);

                                                    //通过audit日志中objects对象的数据获取库、表信息
                                                    audit_objects = message.getAsJsonArray("objects");
                                                    //sql语句未解析出 字段信息，eg:SELECT 1
                                                    /**
                                                     * 判定标准：
                                                     * A.SQL语句解析出字段信息，eg: 未解析出的情况 SELECT 1
                                                     * B.mysql-audit解析出库、表信息，eg: 未解析出的情况SELECT
                                                     * 情況1：!A&!B 不进行处理，直接入库  eg:SELECT 1
                                                     * 情况2：A&!B 不处理，直接入库 eg：@@version.readonly这条sql大量出现
                                                     * 情况3：!A&B 需要处理，eg: delete from tablename
                                                     * 情况4：A&B 正常处理，eg：基本的增 改 查
                                                     */
                                                    //情况1 !A&!B
                                                    if(statVisitor.getColumns().size()==0&&audit_objects==null){
                                                        //continue;
                                                    }else{
                                                        //sql语句能解析出字段信息 符合标准A
                                                        if(statVisitor.getColumns().size()!=0){
                                                            //获取字段数组的第一个元素，
                                                            first_column = statVisitor.getColumns().iterator().next();
                                                            // 进行split("[.]")，如果长度为3，即格式为database.table.field，则直接对库、表、字段信息进行处理
                                                            //split对于特殊符号的拆分有特殊的写法：
                                                            //关于点的问题是用string.split("[.]") 解决。
                                                            //关于竖线的问题用 string.split("\\|")解决。
                                                            //关于星号的问题用 string.split("\\*")解决。
                                                            //关于斜线的问题用 sring.split("\\\\")解决。
                                                            //关于中括号的问题用 sring.split("\\[\\]")解决。
                                                            if(first_column.getFullName().split("[.]").length==3){

                                                                //进行遍历
                                                                for(TableStat.Column column : statVisitor.getColumns()){
                                                                    column_info = column.getFullName().split("[.]");
                                                                    db_names.add(column_info[0]);//数据库
                                                                    table_names.add(column_info[0]+"."+column_info[1]);//数据库.表
                                                                    field_names.add(column.getFullName());//数据库.表.字段
                                                                }
                                                            }else if(first_column.getFullName().split("[.]").length==2){
                                                                //存放表信息，key为表名，value为objects数组中一个对象的信息，包括库名、表名、类型
                                                                tables = new HashMap<>();
                                                                //objects不为空，有库、表的信息，再进行遍历，否则报错
                                                                //情况4：A&B
                                                                if(audit_objects!=null){
                                                                    //遍历 objects中的数组
                                                                    for(int i=0;i<audit_objects.size();i++) {
                                                                        //object中数据格式为：
                                                                        //[{
                                                                        //			"db": "jzLogAnalysis",
                                                                        //			"name": "HS_User",
                                                                        //			"obj_type": "TABLE"
                                                                        //},......]
                                                                        //obj_type为table类型的为 库 表 信息，其他情况未发现
                                                                        if("TABLE".equals(audit_objects.get(i).getAsJsonObject().get("obj_type").getAsString())){
                                                                            //写入map，用于后面字段信息与其对应
                                                                            tables.put(audit_objects.get(i).getAsJsonObject().get("name").getAsString(),audit_objects.get(i));
                                                                            db_names.add(audit_objects.get(i).getAsJsonObject().get("db").getAsString());//数据库
                                                                            table_names.add(audit_objects.get(i).getAsJsonObject().get("db").getAsString()+"."+audit_objects.get(i).getAsJsonObject().get("name").getAsString());//数据库.表
                                                                        }else{
                                                                            //continue;
                                                                        }
                                                                    }
                                                                }else{
                                                                    //情况2：A&!B 不处理
                                                                    //continue;
                                                                }

                                                                //遍历sql语句解析后的字段数组
                                                                for(TableStat.Column column : statVisitor.getColumns()){
                                                                    column_info = column.getFullName().split("[.]");
                                                                    //如果存在该表对应的 库、表信息
                                                                    if(tables.get(column_info[0])!=null){
                                                                        //数据库.表.字段写入列表
                                                                        field_names.add(tables.get(column_info[0]).getAsJsonObject().get("db").getAsString()+"."+column.getFullName());
                                                                    }else{
                                                                        //如果找不到，不进行入库，eg:select @@session.readonly。该语句执行特别频繁，不进行统计
                                                                        //field_names.add(column.getFullName());
                                                                        //log.info("未找到该字段（"+column.getFullName()+"）的库名,日志信息："+jsonObject.getAsString());
                                                                    }
                                                                }
                                                            }else{
                                                                log.info("druid解析sql语句异常，字段信息split(.)不是2和3，实际内容为："+first_column.getFullName());
                                                            }
                                                        }else{
                                                            //情况3：!A&B
                                                            //仅mysql-audit可以解析出库、表信息，eg :delete from tablename
                                                            if(audit_objects!=null){
                                                                for(int i=0;i<audit_objects.size();i++) {
                                                                    //object中数据格式为：
                                                                    //[{
                                                                    //			"db": "jzLogAnalysis",
                                                                    //			"name": "HS_User",
                                                                    //			"obj_type": "TABLE"
                                                                    //},......]
                                                                    //obj_type为table类型的为 库 表 信息，其他情况未发现
                                                                    if("TABLE".equals(audit_objects.get(i).getAsJsonObject().get("obj_type").getAsString())){
                                                                        db_names.add(audit_objects.get(i).getAsJsonObject().get("db").getAsString());//数据库
                                                                        table_names.add(audit_objects.get(i).getAsJsonObject().get("db").getAsString()+"."+audit_objects.get(i).getAsJsonObject().get("name").getAsString());//数据库.表
                                                                    }else{
                                                                        //continue;
                                                                    }
                                                                }
                                                            }
                                                        }

                                                    }

                                                    //放入json对象中
                                                    if(db_names.size()!=0){//数组长度为0，不写入K/V
                                                        message.add("db_names",new JsonParser().parse(JSONArray.fromObject(db_names).toString()));
                                                    }else{

                                                    }
                                                    if(table_names.size()!=0){//数组长度为0，不写入K/V
                                                        message.add("table_names",new JsonParser().parse(JSONArray.fromObject(table_names).toString()));
                                                    }else{

                                                    }
                                                    if(field_names.size()!=0){//数组长度为0，不写入K/V
                                                        message.add("field_names",new JsonParser().parse(JSONArray.fromObject(field_names).toString()));
                                                    }else{

                                                    }
                                                    message.remove("objects");//移除原始日志json中带有的该key的内容
                                                    //index名称 eg:hs_db_mysql_log-2021.05.26
                                                    mysql_audit_index_name = Constant.MYSQL_AUDIT_INDEX_NAME.replace("*",timestamp.format(dtf_time_ymd));
                                                    message.addProperty("index",mysql_audit_index_name);
                                                    //批量入库
                                                    request.index(mysql_audit_index_name);
                                                    request.source(message.toString(), XContentType.JSON);
                                                    logCurdDao.bulkProcessor_add(request);
                                                }else{
                                                    log.info("filebeat mysql-audit 其他类型日志(msg-type)："+msg_type);
                                                }
                                            }else{
                                                //未发现对应资产，不入库
                                                log.info("filebeat-mysql-audit未发现date字段，日志内容："+jsonObject.toString());
                                            }


                                        }else{
                                            //未发现对应资产，不入库
                                            log.info("filebeat-mysql-audit未发现对应资产，日志内容："+jsonObject.toString());
                                        }
                                    }else{
                                        log.info("filebeat-mysql，非mysql-audit类型日志，日志内容："+jsonObject.toString());
                                    }
                                }else{
                                    log.info("filebeat，db类型，非mysql日志，日志内容："+jsonObject.toString());
                                }
                            }else{
                                log.info("filebeat，非db类型日志，日志内容："+jsonObject.toString());
                            }
                        }else{
                            log.info("该跳数据不属于packet/metric/winlog/file Beat！");
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
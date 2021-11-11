package com.jz.bigdata.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jz.bigdata.business.logAnalysis.log.entity.Syslog;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.start_execution.cache.AssetCache;
import com.mysql.jdbc.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.json.Json;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/6/18 10:59
 * @Description: 处理kafka消费后的syslog日志数据
 *
 */
@Slf4j
public class SyslogUtil {
    //日志级别
    public static final Map<Integer, String> Severity = new HashMap<>();
    static{
        //rsyslog过来的是数字，对应成英文
        Severity.put(0, "emergency"); 		//system is unusable
        Severity.put(1, "alert");	 		//action must be taken immediately
        Severity.put(2, "critical");	 		//critical conditions
        Severity.put(3, "error");			//error conditions
        Severity.put(4, "warning");			//warning conditions
        Severity.put(5, "notice");			//normal but significant condition
        Severity.put(6, "information");	//informational messages
        Severity.put(7, "debug");			//debug-level messages
    }
    //资产类型，key为资产类型在数据库中存储的code，value为该资产对应的标记名
    private static final Map<String, String> EQUIPMENT_TYPES = new HashMap<>();
    static {
        EQUIPMENT_TYPES.put("0201","ips");
        EQUIPMENT_TYPES.put("0204","firewall");
    }
    //日志界别对应的字段名，符合rsyslog标准
    private static final String HS_SYSLOG_SEVERITY = "severity";
    //防火墙日志index前缀
    private static final String HS_SYSLOG_INDEX_PRE = "hs_syslog_";
    //范式化防火墙message信息后，不管是创建1级还是2级数据结构，定义的标签名称
    //如果是1级：字段名添加前缀 firewall_
    //如果是2级：firewall_message定义为一级字段，message范式化后的数据在2级
    //private static final String HS_SYSLOG_FIREWALL_2LEVEL_TAG = "firewall_message";
    //private static final String HS_SYSLOG_FIREWALL_1LEVEL_TAG = "firewall_";
    //logstash syslog 日志转换
    private static final DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    //防火墙日志类型存储的字段名称
    private static final String SYSLOG_FIREWALL_TYPE_KEY = "program";
    //日志存储时间的字段key
    private static final String SYSLOG_DATETIME_FIELD = "@timestamp";
    //防火墙日志默认类型---default
    private static final String SYSLOG_FIREWALL_TYPE_DEFAULT = "default";
    //syslog-message信息对应的字段名
    private static final String SYSLOG_MESSAGE_FIELD = "message";
    /*************自定义标签字段名称*********************/
    //
    private static final String CUSTOM_FIELDS = "fields";
    //fields.ip 资产ip
    private static final String CUSTOM_FIELDS_IP = "ip";
    //fields.equipmenttype 资产类型
    private static final String CUSTOM_FIELDS_EQUIPMENT_TYPE = "equipmenttype";
    //fields.equipmentname 资产名称
    private static final String CUSTOM_FIELDS_EQUIPMENT_NAME = "equipmentname";
    //fields.equipmentid 资产ID
    private static final String CUSTOM_FIELDS_EQUIPMENT_ID = "equipmentid";
    //fields.userid 用户ID
    private static final String CUSTOM_FIELDS_USER_ID = "userid";
    //fields.deptid 部门ID
    private static final String CUSTOM_FIELDS_DEPARTMENT_ID = "deptid";
    //fields.failure 是否正常范式化
    private static final String CUSTOM_FIELDS_FAILURE = "failure";

    //logstash转化成syslog json时，如果转化失败，会带有tag标签，内容为失败说明
    private static final String SYSLOG_TAG = "tags";
    //是否为转换失败的内容匹配字段，匹配成功，会在自定义字段fields.failure=true
    private static final String SYSLOG_TAG_FAILURE_TEXT = "failure";
    //message信息拆分时，split的方式  空格   等号（=），需要忽略双引号里的内容
    //eg：a=1 b="c=1 d=1" 以空格拆分时时，需要忽略双引号内的空格，同样，以=拆分b="c=1 d=1"时，需要忽略双引号内的=
    //因此需要在正则中引入忽略双引号内容的正则，最终的正则表达式为 拆分符号正则+忽略双引号内容正则，
    //例如：以空格拆分的正则为 [](?=([^\"]*\"[^\"]*\")*[^\"]*$)
    //将后面的这部分常量，即忽略双引号内容正则 作为常量进行定义
    private static final String MESSAGE_IGNORE_REGEX = "(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";
    private static final String SPACE_REGEX = "[ ]";
    private static final String EQUAL_REGEX = "=";
    /**
     * 处理 kafka消费的syslog日志信息
     * 1.将syslog转化为json对象
     * 2.判断日志ip是否在资产库中
     *    T 在资产库中
     *      2.1 ip+日志类型是否匹配
     *          T 匹配成功
     *          2.1.1 根据时间戳，生成index日期后缀。eg:2021.06.18
     *          2.1.2日志级别筛选
     *              T 需入库
     *              2.1.2.1 判断资产类型
     *                  T 防火墙
     *                       a.获取message信息，进行范式化处理
     *                          message eg：
     *                          英文：SerialNum=100016002002852106021883 GenTime="2021-06-17 11:17:45" Content="interface ge0/4 link down" EvtCount=1
     *                          中文：SerialNum=100016002002852106021883 GenTime="2021-06-17 09:27:48" 源地址=192.168.1.10 用户名=admin 操作="mod fw_policy_table 配置" 管理方式=WEB 内容="操作成功" EvtCount=1
     *                          message数据格式为以“空格”隔开   k=v的数据格式
     *                          将范式化后的数据转化为json对象
     *                       b.将message范式化后的json obj写入到原syslog转化后的json obj中，与其他字段并列（打平）。
     *                       c.根据数据生成index：hs_syslog_firewall_[program类型]-2021.06.18
     *                  F 其他
     *                      a.范式化成ecs格式数据
     *                      b.生成index：winlogbeat-2021.06.18
     *               2.1.2.2创建request对象，写入index和json（json对象转化成string） 并返回。
     *              F 不需入库
     *              return null
     *          F 匹配失败
     *          打上资产ip，其他资产信息为unknown，
     *    F 不在资产库中
     *      return null(不入库)
     * @param syslogJson kafka消费的string类型的syslog数据
     * @return es bulk提交所需要的request对象
     */
//    public static IndexRequest SyslogHandler(String syslogJson){
//        return null;
//    }

    /**
     * 对自定义标签内容进行处理。
     * 包含两部分
     * 1.打资产标签 (fields.ip...)
     * 2.是否正常范式化标签 （fields.failure）
     * @param syslogJsonObj syslog json对象
     * @param equipment 资产对象
     */
    public static void fieldsTagHandler(JsonObject syslogJsonObj, Equipment equipment){
        //1.根据资产信息生成资产标签json obj，写入到syslogJsonObj中
        //fields的value
        JsonObject fieldsValue = new JsonObject();
        //用户信息
        fieldsValue.addProperty(CUSTOM_FIELDS_USER_ID,equipment.getUserId());
        //部门id
        fieldsValue.addProperty(CUSTOM_FIELDS_DEPARTMENT_ID,equipment.getDepartmentId());
        //资产名称
        fieldsValue.addProperty(CUSTOM_FIELDS_EQUIPMENT_NAME,equipment.getName());
        //资产id
        fieldsValue.addProperty(CUSTOM_FIELDS_EQUIPMENT_ID,equipment.getId());
        //资产ip
        fieldsValue.addProperty(CUSTOM_FIELDS_IP,equipment.getIp());
        //资产类型
        fieldsValue.addProperty(CUSTOM_FIELDS_EQUIPMENT_TYPE,EQUIPMENT_TYPES.get(equipment.getType()));

        syslogJsonObj.add(CUSTOM_FIELDS,fieldsValue);
        //2.syslogJsonObj打上是否范式化成功标签
        //是否正常范式化.tags标签中包含failure字符串
        if (syslogJsonObj.get(SYSLOG_TAG)!=null&&syslogJsonObj.get(SYSLOG_TAG).toString().contains(SYSLOG_TAG_FAILURE_TEXT)){
            fieldsValue.addProperty(CUSTOM_FIELDS_FAILURE,true);
        }else{
            fieldsValue.addProperty(CUSTOM_FIELDS_FAILURE,false);
        }
    }

    /**
     * 生成ES提交对象 indexRequest（防火墙类型）
     * 主要是生成index名称 eg:hs_syslog_firewall_config-2021.06.18
     * @param syslogJsonObj syslog json对象
     * @param indexRequest es提交对象
     * @return
     */
    public static IndexRequest createIndexRequest(JsonObject syslogJsonObj,IndexRequest indexRequest,String equipment_type){
        //创建index：hs_syslog_firewall_[program类型]-2021.06.18,program即防火墙存储日志类型的字段
        //资产产生日志的内部类型存储字段
        JsonElement syslog_program = syslogJsonObj.get(SYSLOG_FIREWALL_TYPE_KEY);
        //定义时间对象，获取日志时间
        DateTime dateTime;
        if(syslogJsonObj.get(SYSLOG_DATETIME_FIELD)!=null){
            //时间字段不为空，按格式转化成时间对象
            dateTime = DateTime.parse(syslogJsonObj.get(SYSLOG_DATETIME_FIELD).getAsString(), dtf);
        }else{
            //如果日志没有时间字段，取当前时间
            dateTime = DateTime.now();
        }
        //组装要写入的index的名称，如果找不到日志类型，用default代替,index名称必须小写
        //String indexName = HS_SYSLOG_INDEX_PRE+equipment_type+"_"+(syslog_program!=null?syslog_program.getAsString().toLowerCase():SYSLOG_FIREWALL_TYPE_DEFAULT)+"-"+dateTime.toString("yyyy.MM.dd");
        String indexName = HS_SYSLOG_INDEX_PRE+equipment_type+"-"+dateTime.toString("yyyy.MM.dd");
        //8.es bulk提交的IndexRequest ，写入index和转成string的jsonobj
        indexRequest.index(indexName);
        indexRequest.source(syslogJsonObj.toString(), XContentType.JSON);
        return indexRequest;
    }
    /**
     * 防火墙 日志处理流程
     * 将已经转成jsonobj的syslog日志中的message信息进行范式化处理
     * message eg：
     *  启明星辰 天清汉马防火墙（千兆）USG-FW-T-NF II型V2.6
     *   英文：SerialNum=100016002002852106021883 GenTime="2021-06-17 11:17:45" Content="interface ge0/4 link down" EvtCount=1
     *   中文：SerialNum=100016002002852106021883 GenTime="2021-06-17 09:27:48" 源地址=192.168.1.10 用户名=admin 操作="mod fw_policy_table 配置" 管理方式=WEB 内容="操作成功" EvtCount=1
     *   message数据格式为以“空格”隔开   k=v的数据格式
     *   将范式化后的数据转化为json对象
     *  1.message信息范式化(防火墙)
     *  2.自定义字段处理：打资产标签和syslog是否正常范式化标签
     *  3.日志级别筛选
     *  4.生成ES提交对象 indexRequest
     * @param syslogJsonObj 转化成jsonobj的syslog日志
     * @param  equipment  匹配后的资产基本信息
     * @param  indexRequest 需要提交到es的对象
     * @return es bulk提交所需的对象
     */
    public static IndexRequest SyslogHandler(JsonObject syslogJsonObj, Equipment equipment,IndexRequest indexRequest){
        //获取message信息
        String message = syslogJsonObj.get(SYSLOG_MESSAGE_FIELD).getAsString();
        //资产类型
        String equipment_type = EQUIPMENT_TYPES.get(equipment.getType());
        //1.message信息范式化(防火墙)
        MessageHandler(message,syslogJsonObj,equipment_type,SPACE_REGEX,EQUAL_REGEX);
        //2.自定义字段处理：打资产标签和syslog是否正常范式化标签
        fieldsTagHandler(syslogJsonObj,equipment);
        //3.日志级别筛选
        //获取日志级别 编码
        String severityCode = syslogJsonObj.get(HS_SYSLOG_SEVERITY)==null?"":syslogJsonObj.get(HS_SYSLOG_SEVERITY).getAsString();
        //获取日志界别编码对应的名称
        String severityName = NumberUtils.isNumber(severityCode)?Severity.get(Integer.parseInt(severityCode)):"";
        //如果获取不到日志数据中的日志级别，则认为日志数据未范式化，不进行级别判定，统一入库
        /**
         * severityName
         * 1.!null && !"" 有日志级别 需要对日志级别进行筛选
         * 2.null
         * 3.""
         *2-3情况不进行日志级别筛选，直接入库
         */
        if(!StringUtils.isNullOrEmpty(severityName)){
            // 判定应收集的日志级别，通过日志级别进行日志过滤
            if(AssetCache.INSTANCE.getEquipmentLogLevel().get(equipment.getId()).indexOf(severityName.toLowerCase())!=-1){
                //4.生成ES提交对象 indexRequest
                createIndexRequest(syslogJsonObj,indexRequest,equipment_type);
            }
        }else{
            //4.生成ES提交对象 indexRequest
            createIndexRequest(syslogJsonObj,indexRequest,equipment_type);
        }
        //TODO 9.返回，目前不需要返回，暂时保留
        return indexRequest;
    }

    /**
     * 启明星辰 天清汉马防火墙（千兆）USG-FW-T-NF II型V2.6   IPS V6.0
     * message信息范式化
     * * message eg：
     * *   英文：SerialNum=100016002002852106021883 GenTime="2021-06-17 11:17:45" Content="interface ge0/4 link down" EvtCount=1
     * *   中文：SerialNum=100016002002852106021883 GenTime="2021-06-17 09:27:48" 源地址=192.168.1.10 用户名=admin 操作="mod fw_policy_table 配置" 管理方式=WEB 内容="操作成功" EvtCount=1
     * *   message数据格式为以“空格”隔开   k=v的数据格式
     *    将范式化后的数据转化为json对象
     *注：该方法会将展平后的数据作为2级字段信息，一级字段使用HS_SYSLOG_FIREWALL_2LEVEL_TAG=firewall_message
     * @param message 防火墙/ips 生成的syslog日志中，message字段的数据
     * @param equipmenttype 资产类型
     * @param firstRegex 一级拆分符对应的正则（有一些特殊符号有对应的拆分正则表达式，如 空格对应的为 [ ]）
     * @param secondRegex 一级拆分后，再进行拆分，所需的拆分符号对应的正则
     * @param syslogJsonObj kafka接收到的日志json对象，通过传参处理是因为拆分后的kv没法先生成json对象再并入syslogJsonObj中，所以通过参数传过来
     *                      直接用syslogJsonObj进行add
     */
    public static void MessageHandler(String message,JsonObject syslogJsonObj,String equipmenttype,String firstRegex,String secondRegex){
        JsonObject newMessageObj = new JsonObject();
        //1.split(空格)=> 数组
        String[] msgArr = message.trim().split(firstRegex+MESSAGE_IGNORE_REGEX,-1);
        //2.遍历k=v数组 split("=")  =>  k & v
        for(String kv:msgArr){
            //拆分kv
            String[] kvArr = kv.trim().split(secondRegex+MESSAGE_IGNORE_REGEX,-1);
            //正常拆分后，长度为2
            if(kvArr.length==2){
                //TODO
                //涉密特殊处理，IPS日志中，中英文对照 message.EventLevel 字段（事件优先级） 0：低  1：中  2：高
                if("EventLevel".equals(kvArr[0])){
                    switch (kvArr[1]){
                        case "0":
                            newMessageObj.addProperty(kvArr[0],"低");
                            break;
                        case "1":
                            newMessageObj.addProperty(kvArr[0],"中");
                            break;
                        case "2":
                            newMessageObj.addProperty(kvArr[0],"高");
                            break;
                        default:
                            newMessageObj.addProperty(kvArr[0],kvArr[1]);
                            break;
                    }
                }else{
                    //3.将k v写入newMessageObj中
                    newMessageObj.addProperty(kvArr[0],kvArr[1]);
                }

            }else{
                log.error("message拆分失败："+kv+"，原始message："+message);
            }
        }
        //2级菜单,一级字段为[资产类型]_message，2级为message打平后的信息，eg:firewall_message,ips_message
        //字段可以通过[资产类型]_message.field进行访问
        syslogJsonObj.add(equipmenttype+"_"+SYSLOG_MESSAGE_FIELD,newMessageObj);
    }
    public static void main(String[] args) {
        String logs = "{\"host\":\"172.16.0.250\",\"priority\":389,\"facility\":48,\"program\":\"CONFIG\",\"severity\":5,\"severity_label\":\"Notice\",\"@timestamp\":\"2021-06-17T14:00:52.000Z\",\"logsource\":\"host\",\"@version\":\"1\",\"message\":\"SerialNum=100016002002852106021883 GenTime=\\\"2021-06-17 14:00:52\\\" 源地址=192.168.1.10 用户名=admin 操作=\\\"mod fw_policy_state 配置\\\" 管理方式=WEB 内容=\\\"操作成功\\\"\\n EvtCount=1\",\"type\":\"system-syslog\"}";
        JsonElement jsonElement = new JsonParser().parse(logs);
        JsonObject jsonObject= jsonElement.getAsJsonObject();
        Equipment equipment = new Equipment();
        equipment.setUserId("userid11111111111");
        equipment.setId("id22222222222");
        equipment.setIp("172.16.0.250");
        equipment.setName("name资产名");
        equipment.setDepartmentId(222);
        //firewallSyslogHandler(jsonObject,equipment,new IndexRequest());

    }
}

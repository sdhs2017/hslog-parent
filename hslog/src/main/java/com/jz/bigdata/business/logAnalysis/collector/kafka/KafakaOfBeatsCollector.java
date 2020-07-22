package com.jz.bigdata.business.logAnalysis.collector.kafka;

import com.google.common.base.Strings;
import com.google.gson.*;
import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.jz.bigdata.common.asset.cache.AssetCache;
import com.jz.bigdata.business.logAnalysis.ecs.cn2en.Cn2En;
import com.jz.bigdata.business.logAnalysis.log.LogType;
import com.jz.bigdata.common.asset.entity.Asset;
import com.jz.bigdata.common.configuration.cache.ConfigurationCache;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.roleauthority.user.service.IUserService;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.ContextRoles;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
import org.apache.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: hsgit
 * @description: 通过kafka收集elastic beats发送的数据
 * @author: jiyourui
 * @create: 2020-03-16 13:01
 **/
public class KafakaOfBeatsCollector implements Runnable {

    private static Logger logger = Logger.getLogger(KafakaOfBeatsCollector.class);

    private final ConsumerConnector consumer;
    Map<String, List<KafkaStream<String, String>>> consumerMap;
    // TODO 将topic 作为配置添加到hostproperties文件中
    String topic = "beats";

    private ILogCrudDao logCurdDao;
    private ConfigProperty configProperty;
    private IUserService usersService;
    public static Map<String,Object> map=new HashMap<String,Object>();

    Map<String, String> protocolmap = new HashMap<String, String>();

    public static List<Object> list=new ArrayList<Object>();


    /**
     * 线程操作
     */
    //暂停
    //TODO
    /**
     * 默认状态：关闭
     */
    boolean started = false;
    public boolean isStarted() {
        return started;
    }
    public void setStarted(boolean started) {
        this.started = started;
        if(started){
            logger.info("开启beats的数据采集器");
        }else{
            logger.info("关闭beats的数据采集器");
        }
    }


    /**
     * 资产列表（虚拟资产）
     */
    //Map<String, Equipment> equipmentMap;
    //Set<String> ipadressSet;
    //Map<String, String> equipmentLogLevel;
    /**
     * 逻辑资产
     */
    //Map<String, Asset> assetMap;
    //Set<String> assetIpAddressSet;
    /**
     *
     * @param logCurdDao
     * @param configProperty
     */
    public KafakaOfBeatsCollector(ILogCrudDao logCurdDao, ConfigProperty configProperty) {
        /**
         * kafka 消费者属性配置
         */
        Properties props = new Properties();
        //zookeeper 配置
        props.put("zookeeper.connect", configProperty.getZookeeper_path());
//		props.put("zookeeper.connect", "124.133.246.61:2181");
        //group 代表一个消费组
        props.put("group.id", "jd-group");

        //zk连接超时
        props.put("zookeeper.session.timeout.ms", "4000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "smallest");
        //序列化类
        props.put("serializer.class", "kafka.serializer.StringEncoder");

        ConsumerConfig config = new ConsumerConfig(props);

        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(config);

        /*Gson gson = new Gson();

        protocolmap = gson.fromJson(this.configProperty.getProtocol(), protocolmap.getClass());*/


        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(1));

        // 设置译码器
        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());

        consumerMap = consumer.createMessageStreams(topicCountMap,
                keyDecoder, valueDecoder);

        /**
         * 非kafka配置相关，用于对采集数据进行补全的属性
         */
        this.logCurdDao = logCurdDao;
        this.configProperty = configProperty;
        //初始化：获取设备列表、map
        //equipmentMap = equipmentService.selectAllEquipment();

        //ipadressSet = equipmentService.selectAllIPAdress();

        //equipmentLogLevel = equipmentService.selectLog_level();
        //初始化逻辑资产
        //assetMap = assetService.selectAllAsset();
        //assetIpAddressSet = assetService.selectAllIPAdress();

    }

    public static final String REGEX = "lineStartRegex";
    //	public static final String DEFAULT_REGEX = "\\s?\\d\\d\\d\\d-\\d\\d-\\d\\d\\s\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d";
    public static final String DEFAULT_REGEX = "^ java.|^   at";

    Calendar cal = Calendar.getInstance();

    /**
     * kafka流对象管理
     */


    public void closeKafkaStream(){
        consumer.shutdown();
    }
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Override
    public void run() {

        // 获取数据
        KafkaStream<String, String> stream = consumerMap.get(topic).get(0);
        ConsumerIterator<String, String> it = stream.iterator();

        try {

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();
            String json;

            /*
             * 	资产、ip地址
             */
            Equipment equipment;
            String ipadress;
            String module;// 所属模块
            Asset asset;//逻辑资产

            List<IndexRequest> indicesrequests = new ArrayList<IndexRequest>();
            /**
             * 判断迭代器中是否有数据，且kafka的状态为开启中
             */
            while (it.hasNext() && isStarted()) {

                try {
                    String log = it.next().message();
                    /**
                     * 将采集到的log转为jsonObject格式
                     */
                    JsonElement jsonElement = new JsonParser().parse(log);
                    JsonObject jsonObject= jsonElement.getAsJsonObject();
                    /**
                     * 判断日志是否为windows日志
                     */
                    String beat_type = jsonObject.getAsJsonObject("agent").get("type").getAsString().replaceAll("\"","");
                    if(beat_type.equals(ContextRoles.WINLOGBEAT)){
                        //获取数据IP
                        ipadress = jsonObject.getAsJsonObject("fields").get("ip").getAsString().replaceAll("\"","");
                        try {
                            module = jsonObject.getAsJsonObject("fields").get("module").getAsString().replaceAll("\"","");
                        }catch (Exception e){
                            module = null;
                        }
                        //如果IP在逻辑资产列表中，加上逻辑资产标签
                        asset = AssetCache.INSTANCE.getAssetMap().get(ipadress);
                        if(asset!=null){
                            jsonObject.getAsJsonObject("fields").addProperty("assetid", asset.getId());
                            jsonObject.getAsJsonObject("fields").addProperty("assetname", asset.getName());
                        }
                        //判断是否在资产ip地址池里
                        if (AssetCache.INSTANCE.getIpAddressSet().contains(ipadress)) {
                            //判断是否在已识别资产里————日志类型可识别
                            equipment = AssetCache.INSTANCE.getEquipmentMap().get(ipadress + LogType.LOGTYPE_WINLOG);
                            if (null != equipment) {
                                // TODO 资产添加的时候选择收集的日志级别需要和ECS进行对应
                                //if (equipmentLogLevel.get(equipment.getId()).indexOf(winlog.getOperation_level().toLowerCase())!=-1) {}
                                // 补全资产信息
                                jsonObject.getAsJsonObject("fields").addProperty("equipmentname", equipment.getName());
                                jsonObject.getAsJsonObject("fields").addProperty("equipmentid", equipment.getId());
                                jsonObject.getAsJsonObject("fields").addProperty("userid", equipment.getUserId());
                                jsonObject.getAsJsonObject("fields").addProperty("deptid", equipment.getDepartmentId());
                            }else {
                                // TODO 之前版本对IP地址匹配但是资产未匹配的数据设置为unknow
                                /*jsonObject.getAsJsonObject("fields").addProperty("equipmentname", equipment.getName());
                                jsonObject.getAsJsonObject("fields").addProperty("equipmentid", equipment.getId());
                                jsonObject.getAsJsonObject("fields").addProperty("userid", equipment.getUserId());
                                jsonObject.getAsJsonObject("fields").addProperty("deptid", equipment.getDepartmentId());*/
                            }
                            /**
                             * 判断日志级别是否是中文，如果是中文改成英文
                             */
                            String loglevel = jsonObject.getAsJsonObject("log").get("level").toString().replaceAll("\"","");
                            switch (loglevel){
                                case ContextRoles.LOG_LEVEL_INFO:
                                    jsonObject.getAsJsonObject("log").addProperty("level","information");
                                    break;
                                case ContextRoles.LOG_LEVEL_WARN:
                                    jsonObject.getAsJsonObject("log").addProperty("level","warning");
                                    break;
                                case ContextRoles.LOG_LEVEL_ERROR:
                                    jsonObject.getAsJsonObject("log").addProperty("level","error");
                                    break;
                                default:
                                    logger.info("日志级别符合标准");
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
                             *
                             */
                            String version = jsonObject.getAsJsonObject("agent").get("version").getAsString().replaceAll("\"","");
                            String date = jsonObject.get("@timestamp").getAsString().substring(0,jsonObject.get("@timestamp").getAsString().indexOf("T")).replaceAll("-",".");
                            /**
                             * index 名称不体现版本信息，但是版本数据依然在数据中存储
                             */
                            //String index = beat_type+"-"+version+"-"+date;
                            String index = beat_type+"-"+date;
                            if (!Strings.isNullOrEmpty(module)){
                                index = beat_type+"-"+module+"-"+date;
                            }
                            /**
                             *  打印入库数据
                             */
                            /*System.out.println(index+" : "+jsonObject.toString());*/

                            // 判定应收集的日志级别，通过日志级别进行日志过滤
                            loglevel = jsonObject.getAsJsonObject("log").get("level").toString().replaceAll("\"","");
                            if (AssetCache.INSTANCE.getEquipmentLogLevel().get(equipment.getId()).indexOf(loglevel)!=-1) {
                                /**
                                 * 批量入库
                                 */
                                indicesrequests.add(logCurdDao.insertNotCommit(index, null, jsonObject.toString()));
                            }

                        }else {
                            // 不在资产池中的日志数据不处理
                        }

                    }else if(beat_type.equals(ContextRoles.PACKETBEAT)){
                        //获取数据IP
                        ipadress = jsonObject.getAsJsonObject("fields").get("ip").getAsString().replaceAll("\"","");
                        try {
                            module = jsonObject.getAsJsonObject("fields").get("module").getAsString().replaceAll("\"","");
                        }catch (Exception e){
                            module = null;
                        }

                        //如果IP在逻辑资产列表中，加上逻辑资产标签
                        asset = AssetCache.INSTANCE.getAssetMap().get(ipadress);
                        if(asset!=null){
                            jsonObject.getAsJsonObject("fields").addProperty("assetid", asset.getId());
                            jsonObject.getAsJsonObject("fields").addProperty("assetname", asset.getName());
                        }
                        //判断是否在资产ip地址池里
                        if (AssetCache.INSTANCE.getIpAddressSet().contains(ipadress)) {
                            //判断是否在已识别资产里————日志类型可识别
                            equipment = AssetCache.INSTANCE.getEquipmentMap().get(ipadress + LogType.LOGTYPE_PACKET);
                            if (null != equipment) {
                                // TODO 资产添加的时候选择收集的日志级别需要和ECS进行对应
                                //if (equipmentLogLevel.get(equipment.getId()).indexOf(winlog.getOperation_level().toLowerCase())!=-1) {}
                                // 补全资产信息
                                jsonObject.getAsJsonObject("fields").addProperty("equipmentname", equipment.getName());
                                jsonObject.getAsJsonObject("fields").addProperty("equipmentid", equipment.getId());
                                jsonObject.getAsJsonObject("fields").addProperty("userid", equipment.getUserId());
                                jsonObject.getAsJsonObject("fields").addProperty("deptid", equipment.getDepartmentId());
                            }else {
                                // TODO 之前版本对IP地址匹配但是资产未匹配的数据设置为unknow
                                /*jsonObject.getAsJsonObject("fields").addProperty("equipmentname", equipment.getName());
                                jsonObject.getAsJsonObject("fields").addProperty("equipmentid", equipment.getId());
                                jsonObject.getAsJsonObject("fields").addProperty("userid", equipment.getUserId());
                                jsonObject.getAsJsonObject("fields").addProperty("deptid", equipment.getDepartmentId());*/
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
                             * 名称不体现版本信息，但是版本数据依然在数据中存储
                             */
                            String date = jsonObject.get("@timestamp").getAsString().substring(0,jsonObject.get("@timestamp").getAsString().indexOf("T")).replaceAll("-",".");
                            String index = beat_type+"-"+date;
                            if (!Strings.isNullOrEmpty(module)){
                                index = beat_type+"-"+module+"-"+date;
                            }
                            /**
                             * 批量入库
                             */
                            indicesrequests.add(logCurdDao.insertNotCommit(index, null, jsonObject.toString()));
                        }else {
                            // 不在资产池中的日志数据不处理
                        }
                    }else if(beat_type.equals(ContextRoles.METRICBEAT)){
                        //获取数据IP
                        ipadress = jsonObject.getAsJsonObject("fields").get("ip").getAsString().replaceAll("\"","");
                        try {
                            module = jsonObject.getAsJsonObject("fields").get("module").getAsString().replaceAll("\"","");
                        }catch (Exception e){
                            module = null;
                        }
                        //如果IP在逻辑资产列表中，加上逻辑资产标签
                        asset = AssetCache.INSTANCE.getAssetMap().get(ipadress);
                        if(asset!=null){
                            jsonObject.getAsJsonObject("fields").addProperty("assetid", asset.getId());
                            jsonObject.getAsJsonObject("fields").addProperty("assetname", asset.getName());
                        }
                        //判断是否在资产ip地址池里
                        if (AssetCache.INSTANCE.getIpAddressSet().contains(ipadress)) {
                            //判断是否在已识别资产里————日志类型可识别
                            equipment = AssetCache.INSTANCE.getEquipmentMap().get(ipadress + LogType.LOGTYPE_METRIC);
                            if (null != equipment) {
                                // TODO 资产添加的时候选择收集的日志级别需要和ECS进行对应
                                //if (equipmentLogLevel.get(equipment.getId()).indexOf(winlog.getOperation_level().toLowerCase())!=-1) {}
                                // 补全资产信息
                                jsonObject.getAsJsonObject("fields").addProperty("equipmentname", equipment.getName());
                                jsonObject.getAsJsonObject("fields").addProperty("equipmentid", equipment.getId());
                                jsonObject.getAsJsonObject("fields").addProperty("userid", equipment.getUserId());
                                jsonObject.getAsJsonObject("fields").addProperty("deptid", equipment.getDepartmentId());
                            }else {
                                // TODO 之前版本对IP地址匹配但是资产未匹配的数据设置为unknow
                                /*jsonObject.getAsJsonObject("fields").addProperty("equipmentname", equipment.getName());
                                jsonObject.getAsJsonObject("fields").addProperty("equipmentid", equipment.getId());
                                jsonObject.getAsJsonObject("fields").addProperty("userid", equipment.getUserId());
                                jsonObject.getAsJsonObject("fields").addProperty("deptid", equipment.getDepartmentId());*/
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
                             * 名称不体现版本信息，但是版本数据依然在数据中存储
                             */
                            String date = jsonObject.get("@timestamp").getAsString().substring(0,jsonObject.get("@timestamp").getAsString().indexOf("T")).replaceAll("-",".");
                            String index = beat_type+"-"+date;
                            if (!Strings.isNullOrEmpty(module)){
                                index = beat_type+"-"+module+"-"+date;
                            }
                            /**
                             * 批量入库
                             */
                            indicesrequests.add(logCurdDao.insertNotCommit(index, null, jsonObject.toString()));
                        }else {
                            // 不在资产池中的日志数据不处理
                        }
                    }else{
                        String a= "1";
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


                /**
                 * 批量入库
                 */
                /*indicesrequests.add(logCurdDao.insertNotCommit(index, null, jsonObject.toString()));*/
                /**
                 * 当 indices request中的数据大于等于 配置中设置的批量提交阈值时进行批量提交操作，并清空indicesrequests
                 */
                Object es_bulk = ConfigurationCache.INSTANCE.getConfigurationCache().getIfPresent("es_bulk");
                if (indicesrequests.size()>= (es_bulk!=null?Integer.parseInt(es_bulk.toString()):0)) {
                    logCurdDao.bulkInsert(indicesrequests);
                    indicesrequests.clear();
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error("数据入库失败："+e.getMessage());
        }

    }

}

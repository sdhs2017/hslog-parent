package com.jz.bigdata.business.logAnalysis.collector.kafka;

import com.google.gson.*;
import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.jz.bigdata.common.alarm.service.IAlarmService;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.equipment.service.IEquipmentService;
import com.jz.bigdata.roleauthority.user.service.IUserService;
import com.jz.bigdata.util.ConfigProperty;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
import org.apache.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;

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
     * 资产列表
     */
    Map<String, Equipment> equipmentMap;
    Set<String> ipadressSet;
    Map<String, String> equipmentLogLevel;

    /**
     *
     * @param equipmentService
     * @param logCurdDao
     * @param configProperty
     */
    //public KafakaOfBeatsCollector(IEquipmentService equipmentService, ILogCrudDao logCurdDao, ConfigProperty configProperty, IAlarmService alarmService, IUserService usersService) {
    public KafakaOfBeatsCollector(IEquipmentService equipmentService, ILogCrudDao logCurdDao, ConfigProperty configProperty) {
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

        /**
         * 非kafka配置相关，用于对采集数据进行补全的属性
         */
        this.logCurdDao = logCurdDao;
        this.configProperty = configProperty;
        this.usersService = usersService;
        //初始化：获取设备列表、map
        equipmentMap = equipmentService.selectAllEquipment();

        ipadressSet = equipmentService.selectAllIPAdress();

        equipmentLogLevel = equipmentService.selectLog_level();


        Gson gson = new Gson();

        protocolmap = gson.fromJson(this.configProperty.getProtocol(), protocolmap.getClass());


        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(1));

        // 设置译码器
        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());

        consumerMap = consumer.createMessageStreams(topicCountMap,
                keyDecoder, valueDecoder);

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
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


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

            List<IndexRequest> indicesrequests = new ArrayList<IndexRequest>();
            StringBuilder builder = new StringBuilder();
            /**
             * 判断迭代器中是否有数据，且kafka的状态为开启中
             */
            while (it.hasNext() && isStarted()) {
                String index = configProperty.getEs_index().replace("*",format.format(new Date()));

                String log = it.next().message();
                /**
                 *  打印采集到的数据
                 */
                System.out.println(log);
                // TODO 采集的数据为json格式，需要先补全资产信息再入库，是否需要将json转为java bean？
                JsonElement jsonElement = new JsonParser().parse(log);
                JsonObject jsonObject= jsonElement.getAsJsonObject();
                //json = gson.toJson(syslog);

                /**
                 * 批量入库
                 */
                //indicesrequests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_index(),syslog.getIndex_suffix(),syslog.getLogdate()), LogType.LOGTYPE_SYSLOG, json));
                /**
                 * 当 indices request中的数据大于等于 配置中设置的批量提交阈值时进行批量提交操作，并清空indicesrequests
                 */
                if (indicesrequests.size()>=configProperty.getEs_bulk()) {
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

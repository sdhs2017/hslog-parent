package com.jz.bigdata.business.logAnalysis.collector.kafka;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.google.gson.*;
import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.jz.bigdata.common.start_execution.cache.AssetCache;
import com.jz.bigdata.business.logAnalysis.log.entity.*;
import com.jz.bigdata.common.asset.entity.Asset;
import com.jz.bigdata.common.asset.service.IAssetService;
import com.jz.bigdata.common.start_execution.cache.ConfigurationCache;
import com.jz.bigdata.roleauthority.user.service.IUserService;
import joptsimple.internal.Strings;
import org.elasticsearch.action.index.IndexRequest;

import com.jz.bigdata.business.logAnalysis.log.LogType;
import com.jz.bigdata.common.alarm.service.IAlarmService;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.equipment.service.IEquipmentService;

//import com.jz.bigdata.framework.spring.es.elasticsearch.ClientTemplate;
//import com.hs.elsearch.template.bak.ClientTemplate;
import com.jz.bigdata.util.ConfigProperty;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
import org.elasticsearch.common.xcontent.XContentType;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaCollector implements Runnable {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private ConsumerConnector consumer = null;
	Map<String, List<KafkaStream<String, String>>> consumerMap;
	String topic = "all";
	
    //private ClientTemplate template;
	private ILogCrudDao logCurdDao;
    private ConfigProperty configProperty;
    private IUserService usersService;
	public static Map<String,Object> map=new HashMap<String,Object>();
	
	Map<String, String> protocolmap = new HashMap<String, String>();
	
	public static List<Object> list=new ArrayList<Object>();

	String json;
	/**
	 * 	虚拟资产、ip地址
	 */
	Equipment equipment;
	String ipadress;

	/**
	 * 逻辑资产
	 */
	//Map<String, Asset> assetMap;
	//Set<String> assetIpAddressSet;
	Asset asset;//逻辑资产

	// log4j日志信息过滤条件
	private Pattern facility_pattern = Pattern.compile("local3:");
	private Pattern pattern = Pattern.compile(DEFAULT_REGEX);
	private Pattern log4j_pattern = Pattern.compile("\"type\":\"log4j\"");
	// 防火墙-包过滤日志信息过滤条件
	private Pattern logtype_pattern = Pattern.compile("logtype=1");
	private Pattern dmg_pattern = Pattern.compile("包过滤日志");
	// 防火墙-日志
	private Pattern firewallsDevid_pattern = Pattern.compile("devid=");
	private Pattern firewallsType_pattern = Pattern.compile("logtype=");
	private Pattern firewallsMod_pattern = Pattern.compile("mod=");
	private Pattern firewallsMsg_pattern = Pattern.compile("dsp_msg=");
	// windows安全审计
	private Pattern win2008pattern = Pattern.compile("Security-Auditing:");
	private Pattern win2003pattern = Pattern.compile("Security:");
	// mysql日志
	private Pattern mysqlpattern = Pattern.compile("timestamp");
	// netflow日志
	private Pattern netflowpattern = Pattern.compile("\"type\":\"netflow\"");
	private Pattern netflow1pattern = Pattern.compile("\"type\" => \"netflow\"");
	// DNS日志
	private Pattern dnspattern = Pattern.compile("\\s+named");
	//dhcp
	private Pattern dhcppattern = Pattern.compile("\\s+dhcpd:");
	//filebeat
	private Pattern filebeatpattern = Pattern.compile("\"logtype\":\"app_*");
	private Pattern logstashSyslogPattern = Pattern.compile("\"type\":\"system-syslog\"");

	//logstash 发送的syslog对应winlogbeat，声明index
	private String logstashIndexName ;
	//logstash syslog 日志转换
	private final DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private DateTime dateTime;

	/**
	 * 线程操作
	 */
	//暂停
	//TODO
	//关闭
//	boolean suspended=false;
	boolean started = false;
	public boolean isStarted() {
		return started;
	}
	public void setStarted(boolean started) {
		this.started = started;
		/*
		if(started){
			System.out.println("<<<--------开启--------->>");
		}else{
			System.out.println("<<<--------关闭--------->>");
		}
		*/
	}

	
	/**
	 * 资产列表
	 */
	//Map<String, Equipment> equipmentMap;
	//Set<String> ipadressSet;
	//Map<String, String> equipmentLogLevel;
	/**
	 * 告警事件
	 */
	Set<String> eventType;
	
	//日志类型
	String logType="unknown";
	Log4j log4j;
	Winlog winlog;
	PacketFilteringFirewal packetFilteringFirewal;
	Mysql mysql;
	Syslog syslog;
	ZtsSyslog ztsSyslog;
	ZtsLog4j ztsLog4j;
	ZtsApp ztsapp;
	Netflow netflow;
	DNS dns;
	DHCP dhcp;
	App_file app_file;
	//LogstashSyslog logstashSyslog;

	/**
	 *
	 * @param equipmentService
	 * @param logCurdDao
	 * @param configProperty
	 * @param alarmService
	 * @param usersService
	 */
	public KafkaCollector(IEquipmentService equipmentService, IAssetService assetService, ILogCrudDao logCurdDao, ConfigProperty configProperty, IAlarmService alarmService, IUserService usersService) {
		Properties props = new Properties();
		//zookeeper 配置
//		props.put("zookeeper.connect", "124.133.246.61:2281");
//		props.put("zookeeper.connect", "10.29.175.201:2181");
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
		try{
			consumer = kafka.consumer.Consumer.createJavaConsumerConnector(config);
			this.logCurdDao = logCurdDao;
			this.configProperty = configProperty;
			this.usersService = usersService;

			//状态设置为开启
//		setStarted(true);

			//初始化：获取设备列表、map
			//equipmentMap = equipmentService.selectAllEquipment();

			//ipadressSet = equipmentService.selectAllIPAdress();

			//equipmentLogLevel = equipmentService.selectLog_level();

			eventType = alarmService.selectByEmailState();

			//初始化逻辑资产
			//assetMap = assetService.selectAllAsset();
			//assetIpAddressSet = assetService.selectAllIPAdress();

			Gson gson = new Gson();

			protocolmap = gson.fromJson(this.configProperty.getProtocol(), protocolmap.getClass());


			Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
			topicCountMap.put(topic, new Integer(1));

			// 设置译码器
			StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
			StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());

			consumerMap = consumer.createMessageStreams(topicCountMap,
					keyDecoder, valueDecoder);

			Object es_bulk = ConfigurationCache.INSTANCE.getConfigurationCache().getIfPresent("es_bulk");
			Object concurrent_requests = ConfigurationCache.INSTANCE.getConfigurationCache().getIfPresent("concurrent_requests");
			logCurdDao.bulkProcessor_init(Integer.parseInt(es_bulk.toString()),Integer.parseInt(concurrent_requests.toString()));
		}catch (Exception e){

			e.printStackTrace();

		}
//		equ=equipment;
		//template = clientTemplate;


	}

	public static final String REGEX = "lineStartRegex";
//	public static final String DEFAULT_REGEX = "\\s?\\d\\d\\d\\d-\\d\\d-\\d\\d\\s\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d";
	public static final String DEFAULT_REGEX = "^ java.|^   at";
	
	Calendar cal = Calendar.getInstance();

	/**
	 * kafka流对象管理
	 */
	
//	public Map<String, List<KafkaStream<String, String>>> getConsumerMap() {
//		return consumerMap;
//	}
//	public void setConsumerMap(Map<String, List<KafkaStream<String, String>>> consumerMap) {
//		this.consumerMap = consumerMap;
//	}
	
//	public void createKafkaStreamManage(String topic){
//		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
//		topicCountMap.put(topic, new Integer(1));
//		
//		// 设置译码器
//		StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
//		StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());
//
//		Map<String, List<KafkaStream<String, String>>> consumerMap = consumer.createMessageStreams(topicCountMap,
//				keyDecoder, valueDecoder);
//		
//		
//		setConsumerMap(consumerMap);
//	}
	
	public void closeKafkaStream(){
		consumer.shutdown();
	}
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


	@Override
	public void run() {
		
//		String topicName = "testFlume";
//		String topicDBName = "flume-log4j-db";
//		String topicServerName = "flume-log4j-server";
		
		
		

//		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
//		topicCountMap.put(topic, new Integer(1));
//		
//		// 设置译码器
//		StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
//		StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());
//
//		Map<String, List<KafkaStream<String, String>>> consumerMap = consumer.createMessageStreams(topicCountMap,
//				keyDecoder, valueDecoder);
		
//		Map<String, List<KafkaStream<String, String>>> consumerMap = createKafkaStreamManage(topic);
		/**
		 * 部分初始化信息
		 */
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		// 获取数据
		List<KafkaStream<String, String>> streamList = consumerMap.get(topic);
		//streamList是按照配置中的消费组生成list，目前就设置了一个组
		for(KafkaStream<String, String> stream:streamList){
			//遍历数据
			ConsumerIterator<String, String> it = stream.iterator();
			try {

				//List<IndexRequest> requests = new ArrayList<IndexRequest>();
				List<IndexRequest> newrequests = new ArrayList<IndexRequest>();
				//BulkRequest newrequests = new BulkRequest();
				IndexRequest request = null;

				StringBuilder builder = new StringBuilder();
				int count=0;
				while (it.hasNext() && isStarted()) {
					request = new IndexRequest();
					//String index = configProperty.getEs_index().replace("*",format.format(new Date()));

					String log = it.next().message();

					// 日志过滤正则
					// log4j日志信息过滤条件
					Matcher facility_matcher = facility_pattern.matcher(log);
					// log4j from logstash
					Matcher log4j_matcher = log4j_pattern.matcher(log);

					// 防火墙-包过滤日志信息过滤条件
					Matcher logtype_matcher = logtype_pattern.matcher(log);
					Matcher dmg_matcher = dmg_pattern.matcher(log);
					// 防火墙-日志
					Matcher firewallsDevid_matcher = firewallsDevid_pattern.matcher(log);
					Matcher firewallsType_matcher = firewallsType_pattern.matcher(log);
					Matcher firewallsMod_matcher = firewallsMod_pattern.matcher(log);
					Matcher firewallsMsg_matcher = firewallsMsg_pattern.matcher(log);
					// windows安全审计
					Matcher win2008matcher = win2008pattern.matcher(log);
					Matcher win2003matcher = win2003pattern.matcher(log);
					// mysql日志
					Matcher mysqlmatcher = mysqlpattern.matcher(log);
					// netflow日志
					Matcher netflowmatcher = netflowpattern.matcher(log);
					Matcher netflow1matcher = netflow1pattern.matcher(log);
					// DNS日志
					Matcher dnsmatcher = dnspattern.matcher(log);
					//dhcp
					Matcher dhcpmatcher = dhcppattern.matcher(log);
					//filebeat
					Matcher filebeatmatcher = filebeatpattern.matcher(log);
					//logstash syslog
					Matcher logstashSyslogMatcher = logstashSyslogPattern.matcher(log);
					//System.out.println("-----------"+log);
					//logstash发送的日志信息
					if(logstashSyslogMatcher.find()){
						logType = LogType.LOGTYPE_SYSLOG;
						try{
							/**
							 * 将采集到的log转为jsonObject格式
							 */
							JsonElement jsonElement = new JsonParser().parse(log);
							JsonObject jsonObject= jsonElement.getAsJsonObject();
							//-----转成ecs
							Logstash2ECS logstash2ECS = new Logstash2ECS().build(jsonObject);
							/**
							 * 判断tags确认logstash对syslog的日志范式化是否成功
							 */
							//TODO 在logstash端处理
							if (jsonObject.get("tags")!=null&&jsonObject.get("tags").toString().contains("failure")){
								logstash2ECS.getFields().setFailure(true);
							}else{
								logstash2ECS.getFields().setFailure(false);
							}

							//获取ip
							ipadress = jsonObject.get("host")==null?"":jsonObject.get("host").getAsString();
							//如果IP在逻辑资产列表中，加上逻辑资产标签
							asset = AssetCache.INSTANCE.getAssetMap().get(ipadress);
							if(asset!=null){
								logstash2ECS.getFields().setAssetname(asset.getName());
								logstash2ECS.getFields().setAssetid(asset.getId());
							}
							//IP是否在虚拟资产中
							if (AssetCache.INSTANCE.getIpAddressSet().contains(ipadress)) {
								equipment = AssetCache.INSTANCE.getEquipmentMap().get(ipadress+logType);
								if (equipment!=null) {
									logstash2ECS.getFields().setUserid(equipment.getUserId());
									logstash2ECS.getFields().setDeptid(String.valueOf(equipment.getDepartmentId()));
									logstash2ECS.getFields().setEquipmentname(equipment.getName());
									logstash2ECS.getFields().setEquipmentid(equipment.getId());
									logstash2ECS.getFields().setIp(equipment.getIp());
								}else{
									logstash2ECS.getFields().setUserid(LogType.LOGTYPE_UNKNOWN);
									logstash2ECS.getFields().setDeptid(LogType.LOGTYPE_UNKNOWN);
									logstash2ECS.getFields().setEquipmentname(LogType.LOGTYPE_UNKNOWN);
									logstash2ECS.getFields().setEquipmentid(LogType.LOGTYPE_UNKNOWN);
								}

								if(logstash2ECS.getTimestamp()!=null){
									dateTime = DateTime.parse(logstash2ECS.getTimestamp().toString(), dtf);
								}else{
									dateTime = DateTime.now();
								}


								// TODO 后期需要把module字段补全到资产信息中，nodule数据将不再从日志数据中获取
								// 判断syslog的module是否为空，不为空的情况下索引名称拼接module字段信息
								String module = jsonObject.get("module")==null?"":jsonObject.get("module").getAsString();
								if(!Strings.isNullOrEmpty(module)){
									logstashIndexName = "winlogbeat-"+module+"-"+ dateTime.toString("yyyy.MM.dd");
								}else {
									// module信息为空的情况下使用标准索引名称
									logstashIndexName = "winlogbeat-"+ dateTime.toString("yyyy.MM.dd");
								}
								//日志级别
								Object severityName = logstash2ECS.getLog().getSyslog().getSeverity().getName();
								//如果获取不到日志数据中的日志级别，则认为日志数据未范式化，不进行级别判定，统一入库
								if(severityName==null||(severityName!=null&&"".equals(severityName.toString()))){
									// 将bulkrequest替换为bulkprocessor方式
									request.index(logstashIndexName);
									request.source(gson.toJson(logstash2ECS), XContentType.JSON);
									logCurdDao.bulkProcessor_add(request);
								}else{
									// 判定应收集的日志级别，通过日志级别进行日志过滤
									if (AssetCache.INSTANCE.getEquipmentLogLevel().get(equipment.getId()).indexOf(severityName.toString().toLowerCase())!=-1) {
										request.index(logstashIndexName);
										request.source(gson.toJson(logstash2ECS), XContentType.JSON);
										logCurdDao.bulkProcessor_add(request);
									}
								}
							}else{
								//System.out.println("");
							}
						}catch (Exception e){
							e.printStackTrace();
							logger.error("范式化失败 ，日志内容："+builder.toString());
							continue;
						}
					}
					//log4j日志信息
					else if (facility_matcher.find()) {
						logType = LogType.LOGTYPE_LOG4J;
						//同步锁，其他试图访问该对象（log）的线程将被阻塞
						synchronized (log) {
							String logleft = log.substring(0, log.indexOf(facility_matcher.group(0))+facility_matcher.group(0).length());

							Matcher m = pattern.matcher(log.replace(logleft, ""));
							//判断是否符合正则表达式 如果符合，表明这是一条开始数据
							if(m.find()) {
								log = log.replace(logleft, "");
								//添加数据
								builder.append(" \\005 "+log);
							}else {
								//添加builder
								if (builder.length()!=0) {
									//进入范式化
									try {
										log4j = new Log4j(builder.toString());
										log4j.setHslog_type(logType);
										ipadress = log4j.getIp();
										//判断是否在资产ip地址池里
										if(AssetCache.INSTANCE.getIpAddressSet().contains(ipadress)){
											//判断是否在已识别资产里————日志类型可识别
											equipment=AssetCache.INSTANCE.getEquipmentMap().get(ipadress +logType);
											if(null != equipment){
												if (AssetCache.INSTANCE.getEquipmentLogLevel().get(equipment.getId()).indexOf(log4j.getOperation_level().toLowerCase())!=-1){
													log4j.setUserid(equipment.getUserId());
													log4j.setDeptid(String.valueOf(equipment.getDepartmentId()));
													log4j.setEquipmentname(equipment.getName());
													log4j.setEquipmentid(equipment.getId());
													json = gson.toJson(log4j);
													//requests.add(template.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_LOG4J, json));
													//newrequests.add(logCurdDao.insertNotCommit(index, LogType.LOGTYPE_LOG4J, json));
													//newrequests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),log4j.getIndex_suffix(),log4j.getLogdate()), LogType.LOGTYPE_LOG4J, json));
													request.index(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),log4j.getIndex_suffix(),log4j.getLogdate()));
													request.source(json, XContentType.JSON);
													logCurdDao.bulkProcessor_add(request);
												}
											}else{
												log4j.setUserid(LogType.LOGTYPE_UNKNOWN);
												log4j.setDeptid(LogType.LOGTYPE_UNKNOWN);
												log4j.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
												log4j.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
												json = gson.toJson(log4j);
												//requests.add(template.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_LOG4J, json));
												//newrequests.add(logCurdDao.insertNotCommit(index, LogType.LOGTYPE_LOG4J, json));
												//newrequests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),log4j.getIndex_suffix(),log4j.getLogdate()), LogType.LOGTYPE_LOG4J, json));
												request.index(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),log4j.getIndex_suffix(),log4j.getLogdate()));
												request.source(json, XContentType.JSON);
												logCurdDao.bulkProcessor_add(request);
											}
										}else{
											//不在资产ip池里，暂不处理
											//TODO
										}
									} catch (Exception e) {
										e.printStackTrace();
										logger.error("范式化失败 ，日志内容："+builder.toString());
										//System.out.println("范式化失败 ，日志内容："+builder.toString());
										continue;
									}

									//清空数据
									builder.delete(0, builder.length());
								}
								builder.append(log);

							}
						}
					}else if (log4j_matcher.find()) {
						logType = LogType.LOGTYPE_LOG4J;
						try {
							log4j = new Log4j(log, cal);
							log4j.setHslog_type(logType);
							ipadress = log4j.getIp();
							if (AssetCache.INSTANCE.getIpAddressSet().contains(ipadress)) {
								equipment = AssetCache.INSTANCE.getEquipmentMap().get(log4j.getIp()+logType);
								if (equipment!=null) {
									if (AssetCache.INSTANCE.getEquipmentLogLevel().get(equipment.getId()).indexOf(log4j.getOperation_level().toLowerCase())!=-1){
										log4j.setUserid(equipment.getUserId());
										log4j.setDeptid(String.valueOf(equipment.getDepartmentId()));
										log4j.setEquipmentname(equipment.getName());
										log4j.setEquipmentid(equipment.getId());
										json = gson.toJson(log4j);
										//requests.add(template.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_LOG4J, json));
										//newrequests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),log4j.getIndex_suffix(),log4j.getLogdate()), LogType.LOGTYPE_LOG4J, json));
										request.index(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),log4j.getIndex_suffix(),log4j.getLogdate()));
										request.source(json, XContentType.JSON);
										logCurdDao.bulkProcessor_add(request);
									}
								}else {
									log4j.setUserid(LogType.LOGTYPE_UNKNOWN);
									log4j.setDeptid(LogType.LOGTYPE_UNKNOWN);
									log4j.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
									log4j.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
									json = gson.toJson(log4j);
									//requests.add(template.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_LOG4J, json));
									//newrequests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),log4j.getIndex_suffix(),log4j.getLogdate()), LogType.LOGTYPE_LOG4J, json));
									request.index(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),log4j.getIndex_suffix(),log4j.getLogdate()));
									request.source(json, XContentType.JSON);
									logCurdDao.bulkProcessor_add(request);
								}
							}
						}catch (Exception e) {
							e.printStackTrace();
							logger.error("范式化失败 ，日志内容："+log);
							//System.out.println("范式化失败 ，日志内容："+log);
							continue;
						}

						// 注释掉原有的包过滤日志关键字判断方式，改用防火墙基本字段判断是否为防火墙日志
						//}else if (logtype_matcher.find()&&dmg_matcher.find()) {
					}else if (firewallsDevid_matcher.find()&&firewallsMod_matcher.find()&&firewallsType_matcher.find()&&firewallsMsg_matcher.find()) {
						logType = LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG;

						try {
							packetFilteringFirewal = new PacketFilteringFirewal(log);
							packetFilteringFirewal.setHslog_type(logType);
							ipadress = packetFilteringFirewal.getIp();
							if (AssetCache.INSTANCE.getIpAddressSet().contains(ipadress)) {
								equipment=AssetCache.INSTANCE.getEquipmentMap().get(packetFilteringFirewal.getIp() +logType);
								if (equipment!=null) {
									packetFilteringFirewal.setUserid(equipment.getUserId());
									packetFilteringFirewal.setDeptid(String.valueOf(equipment.getDepartmentId()));
									packetFilteringFirewal.setEquipmentid(equipment.getId());
									packetFilteringFirewal.setEquipmentname(equipment.getName());
									json = gson.toJson(packetFilteringFirewal);
									//requests.add(template.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG, json));
									//newrequests.add(logCurdDao.insertNotCommit(index, LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG, json));
									//newrequests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),packetFilteringFirewal.getIndex_suffix(),packetFilteringFirewal.getLogdate()), LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG, json));
									request.index(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),packetFilteringFirewal.getIndex_suffix(),packetFilteringFirewal.getLogdate()));
									request.source(json, XContentType.JSON);
									logCurdDao.bulkProcessor_add(request);
								}else {
									packetFilteringFirewal.setUserid(LogType.LOGTYPE_UNKNOWN);
									packetFilteringFirewal.setDeptid(LogType.LOGTYPE_UNKNOWN);
									packetFilteringFirewal.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
									packetFilteringFirewal.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
									json = gson.toJson(packetFilteringFirewal);
									//requests.add(template.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG, json));
									//newrequests.add(logCurdDao.insertNotCommit(index, LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG, json));
									//newrequests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),packetFilteringFirewal.getIndex_suffix(),packetFilteringFirewal.getLogdate()), LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG, json));
									request.index(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),packetFilteringFirewal.getIndex_suffix(),packetFilteringFirewal.getLogdate()));
									request.source(json, XContentType.JSON);
									logCurdDao.bulkProcessor_add(request);
								}
							}else {
								//不在资产ip池里，暂不处理
							}
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("范式化失败 ，日志内容："+log);
							//System.out.println("范式化失败 ，日志内容："+log);
							continue;
						}

					}else if(netflowmatcher.find()||netflow1matcher.find()){
						logType = LogType.LOGTYPE_NETFLOW;
						try {
							netflow = new Netflow(log, cal, protocolmap);
							netflow.setHslog_type(logType);
//						netflow=netflow.SetNetflow(log, cal);
							equipment = AssetCache.INSTANCE.getEquipmentMap().get(netflow.getIp()+logType);
							if (equipment!=null) {
								netflow.setUserid(equipment.getUserId());
								netflow.setDeptid(String.valueOf(equipment.getDepartmentId()));
								netflow.setEquipmentname(equipment.getName());
								netflow.setEquipmentid(equipment.getId());
								json = gson.toJson(netflow);
								//requests.add(template.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_NETFLOW, json));
								//requests.add(template.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_DEFAULTPACKET, json));
								//newrequests.add(logCurdDao.insertNotCommit(index, LogType.LOGTYPE_DEFAULTPACKET, json));
								//newrequests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),netflow.getIndex_suffix(),netflow.getLogdate()), LogType.LOGTYPE_DEFAULTPACKET, json));
								request.index(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),netflow.getIndex_suffix(),netflow.getLogdate()));
								request.source(json, XContentType.JSON);
								logCurdDao.bulkProcessor_add(request);
							}
						}catch (Exception e) {
							e.printStackTrace();
							logger.error("范式化失败 ，日志内容："+log);
							//System.out.println("范式化失败 ，日志内容："+log);
							continue;
						}

					}
					//es暂无防火墙包过滤日志对应的mapping，暂未入库es
				/*else if(logtotherype_matcher.find()&&dmgother_matcher.find()){
					//防火墙、不包括包过滤日志，暂不处理
					System.out.println("-------不做处理-------------");
				}*//*else if (mysqlmatcher.find()) {
					logType = LogType.LOGTYPE_MYSQLLOG;
					mysql = new Mysql(log);
					ipadress = mysql.getIp();
					if (ipadressSet.contains(ipadress)) {
						equipment=equipmentMap.get(mysql.getIp() +logType);
						if (equipment!=null) {
							mysql.setUserid(equipment.getUserId());
							mysql.setDeptid(String.valueOf(equipment.getDepartmentId()));
							mysql.setEquipmentname(equipment.getName());
							mysql.setEquipmentid(equipment.getId());
							json = gson.toJson(mysql);
							requests.add(template.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_MYSQLLOG, json));
						}else {
							mysql.setUserid(LogType.LOGTYPE_UNKNOWN);
							mysql.setDeptid(String.valueOf(LogType.LOGTYPE_UNKNOWN));
							mysql.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
							mysql.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
							json = gson.toJson(mysql);
							requests.add(template.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_MYSQLLOG, json));
						}
					}else {
						//不在资产ip池里，暂不处理
					}
				}*/
					else if(win2003matcher.find()||win2008matcher.find()){
						//windows、evtsys组件收集日志
						logType = LogType.LOGTYPE_WINLOG;
						try {
							winlog = new Winlog(log);
							winlog.setHslog_type(logType);
							ipadress = winlog.getIp();
							//判断是否在资产ip地址池里
							if(AssetCache.INSTANCE.getIpAddressSet().contains(ipadress)){
								//判断是否在已识别资产里————日志类型可识别
								equipment=AssetCache.INSTANCE.getEquipmentMap().get(winlog.getIp() +logType);
								if(equipment != null){
									if (AssetCache.INSTANCE.getEquipmentLogLevel().get(equipment.getId()).indexOf(winlog.getOperation_level().toLowerCase())!=-1) {
										winlog.setUserid(equipment.getUserId());
										winlog.setDeptid(String.valueOf(equipment.getDepartmentId()));
										winlog.setEquipmentname(equipment.getName());
										winlog.setEquipmentid(equipment.getId());
										json = gson.toJson(winlog);
										//requests.add(template.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_WINLOG, json));
										//newrequests.add(logCurdDao.insertNotCommit(index, LogType.LOGTYPE_WINLOG, json));
										//newrequests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),winlog.getIndex_suffix(),winlog.getLogdate()), LogType.LOGTYPE_WINLOG, json));
										request.index(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),winlog.getIndex_suffix(),winlog.getLogdate()));
										request.source(json, XContentType.JSON);
										logCurdDao.bulkProcessor_add(request);
									}
								}else{
									winlog.setUserid(LogType.LOGTYPE_UNKNOWN);
									winlog.setDeptid(LogType.LOGTYPE_UNKNOWN);
									winlog.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
									winlog.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
									json = gson.toJson(winlog);
									//requests.add(template.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_WINLOG, json));
									//newrequests.add(logCurdDao.insertNotCommit(index, LogType.LOGTYPE_WINLOG, json));
									//newrequests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),winlog.getIndex_suffix(),winlog.getLogdate()), LogType.LOGTYPE_WINLOG, json));
									request.index(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),winlog.getIndex_suffix(),winlog.getLogdate()));
									request.source(json, XContentType.JSON);
									logCurdDao.bulkProcessor_add(request);
								}
							}else{
								//不在资产ip池里，暂不处理
							}
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("范式化失败 ，日志内容："+log);
							//System.out.println("范式化失败 ，日志内容："+log);
							continue;
						}

					}else if (dnsmatcher.find()) {
						logType = LogType.LOGTYPE_DNS;
						try {
							dns = new DNS(log);
							dns.setHslog_type(logType);
							ipadress = dns.getIp();
							//判断是否在资产ip地址池里
							if(AssetCache.INSTANCE.getIpAddressSet().contains(ipadress)){
								//判断是否在已识别资产里————日志类型可识别
								equipment=AssetCache.INSTANCE.getEquipmentMap().get(dns.getIp() +logType);
								if(equipment != null){
									if (AssetCache.INSTANCE.getEquipmentLogLevel().get(equipment.getId()).indexOf(dns.getOperation_level().toLowerCase())!=-1) {
										dns.setUserid(equipment.getUserId());
										dns.setDeptid(String.valueOf(equipment.getDepartmentId()));
										dns.setEquipmentname(equipment.getName());
										dns.setEquipmentid(equipment.getId());
										json = gson.toJson(dns);
										//requests.add(template.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_DNS, json));
										//newrequests.add(logCurdDao.insertNotCommit(index, LogType.LOGTYPE_DNS, json));
										//newrequests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),dns.getIndex_suffix(),dns.getLogdate()), LogType.LOGTYPE_DNS, json));
										request.index(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),dns.getIndex_suffix(),dns.getLogdate()));
										request.source(json, XContentType.JSON);
										logCurdDao.bulkProcessor_add(request);
									}
								}else{
									dns.setUserid(LogType.LOGTYPE_UNKNOWN);
									dns.setDeptid(LogType.LOGTYPE_UNKNOWN);
									dns.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
									dns.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
									json = gson.toJson(dns);
									//requests.add(template.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_DNS, json));
									//newrequests.add(logCurdDao.insertNotCommit(index, LogType.LOGTYPE_DNS, json));
									//newrequests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),dns.getIndex_suffix(),dns.getLogdate()), LogType.LOGTYPE_DNS, json));
									request.index(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),dns.getIndex_suffix(),dns.getLogdate()));
									request.source(json, XContentType.JSON);
									logCurdDao.bulkProcessor_add(request);
								}
							}else{
								//不在资产ip池里，暂不处理
							}
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("范式化失败 ，日志内容："+log);
							//System.out.println("范式化失败 ，日志内容："+log);
							continue;
						}
					}else if (dhcpmatcher.find()) {
						logType = LogType.LOGTYPE_DHCP;
						try {
							dhcp = new DHCP(log);
							dhcp.setHslog_type(logType);
							ipadress = dhcp.getIp();
							//判断是否在资产ip地址池里
							if(AssetCache.INSTANCE.getIpAddressSet().contains(ipadress)){
								//判断是否在已识别资产里————日志类型可识别
								equipment=AssetCache.INSTANCE.getEquipmentMap().get(dhcp.getIp() +logType);
								if(equipment != null){
									if (AssetCache.INSTANCE.getEquipmentLogLevel().get(equipment.getId()).indexOf(dhcp.getOperation_level().toLowerCase())!=-1) {
										dhcp.setUserid(equipment.getUserId());
										dhcp.setDeptid(String.valueOf(equipment.getDepartmentId()));
										dhcp.setEquipmentname(equipment.getName());
										dhcp.setEquipmentid(equipment.getId());
										json = gson.toJson(dhcp);
										//requests.add(template.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_DHCP, json));
										//newrequests.add(logCurdDao.insertNotCommit(index, LogType.LOGTYPE_DHCP, json));
										//newrequests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),dhcp.getIndex_suffix(),dhcp.getLogdate()), LogType.LOGTYPE_DHCP, json));
										request.index(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),dhcp.getIndex_suffix(),dhcp.getLogdate()));
										request.source(json, XContentType.JSON);
										logCurdDao.bulkProcessor_add(request);
									}
								}else{
									dhcp.setUserid(LogType.LOGTYPE_UNKNOWN);
									dhcp.setDeptid(LogType.LOGTYPE_UNKNOWN);
									dhcp.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
									dhcp.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
									json = gson.toJson(dhcp);
									//requests.add(template.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_DHCP, json));
									//newrequests.add(logCurdDao.insertNotCommit(index, LogType.LOGTYPE_DHCP, json));
									//newrequests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),dhcp.getIndex_suffix(),dhcp.getLogdate()), LogType.LOGTYPE_DHCP, json));
									request.index(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),dhcp.getIndex_suffix(),dhcp.getLogdate()));
									request.source(json, XContentType.JSON);
									logCurdDao.bulkProcessor_add(request);
								}
							}else{
								//不在资产ip池里，暂不处理
							}
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("范式化失败 ，日志内容："+log);
							//System.out.println("范式化失败 ，日志内容："+log);
							continue;
						}
					}else if (filebeatmatcher.find()) {
						logType = getSubUtilSimple(log,"\"logtype\":\"(.*?)\"");
						if(logType==null) {
							logType = LogType.LOGTYPE_APP_FILE;
						}
						try {
							app_file = new App_file(log);
							app_file.setHslog_type(logType);
							ipadress = app_file.getIp();
							//判断是否在资产ip地址池里
							if(AssetCache.INSTANCE.getIpAddressSet().contains(ipadress)){
								//判断是否在已识别资产里————日志类型可识别
								equipment = AssetCache.INSTANCE.getEquipmentMap().get(app_file.getIp() +logType);
								if(null != equipment){
									if (AssetCache.INSTANCE.getEquipmentLogLevel().get(equipment.getId()).indexOf(app_file.getOperation_level().toLowerCase())!=-1) {
										app_file.setUserid(equipment.getUserId());
										app_file.setDeptid(String.valueOf(equipment.getDepartmentId()));
										app_file.setEquipmentid(equipment.getId());
										app_file.setEquipmentname(equipment.getName());

										json = gson.toJson(app_file);
										//requests.add(template.insertNo(configProperty.getEs_index(), logType, json));
										//newrequests.add(logCurdDao.insertNotCommit(index, LogType.LOGTYPE_APP_FILE, json));
										//newrequests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),app_file.getIndex_suffix(),app_file.getLogdate()), LogType.LOGTYPE_APP_FILE, json));
										request.index(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),app_file.getIndex_suffix(),app_file.getLogdate()));
										request.source(json, XContentType.JSON);
										logCurdDao.bulkProcessor_add(request);
									}

								}else{
									//在资产ip地址池里，但是无法识别日志类型
									app_file.setUserid(LogType.LOGTYPE_UNKNOWN);
									app_file.setDeptid(LogType.LOGTYPE_UNKNOWN);
									app_file.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
									app_file.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
									json = gson.toJson(app_file);
									//requests.add(template.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_UNKNOWN, json));
									//newrequests.add(logCurdDao.insertNotCommit(index, LogType.LOGTYPE_UNKNOWN, json));
									//newrequests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),app_file.getIndex_suffix(),app_file.getLogdate()), LogType.LOGTYPE_APP_FILE, json));
									request.index(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),app_file.getIndex_suffix(),app_file.getLogdate()));
									request.source(json, XContentType.JSON);
									logCurdDao.bulkProcessor_add(request);
								}
							}else{
								//不在资产ip池里，暂不处理
								//TODO
							}
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("范式化失败 ，日志内容："+log);
							//System.out.println("范式化失败 ，日志内容："+log);
							continue;
						}
					}else {
						logType = LogType.LOGTYPE_SYSLOG;
						try {
							syslog = new Syslog(log);
							syslog.setHslog_type(logType);
							ipadress = syslog.getIp();
							//判断是否在资产ip地址池里
							if(AssetCache.INSTANCE.getIpAddressSet().contains(ipadress)){
								//判断是否在已识别资产里————日志类型可识别
								equipment = AssetCache.INSTANCE.getEquipmentMap().get(syslog.getIp() +logType);
								if(null != equipment){
									if (AssetCache.INSTANCE.getEquipmentLogLevel().get(equipment.getId()).indexOf(syslog.getOperation_level().toLowerCase())!=-1) {
										syslog.setUserid(equipment.getUserId());
										syslog.setDeptid(String.valueOf(equipment.getDepartmentId()));
										syslog.setEquipmentid(equipment.getId());
										syslog.setEquipmentname(equipment.getName());

									/*if (eventType.contains(syslog.getEvent_type())) {
										Sendmail sendmail = new Sendmail(syslog.getIp(), syslog.getEquipmentname(), syslog.getEvent_des(), usersService.selectById(syslog.getUserid()).getEmail());
									}*/
										json = gson.toJson(syslog);
										//requests.add(template.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_SYSLOG, json));
										//newrequests.add(logCurdDao.insertNotCommit(configProperty.getEs_index().replace("*","_"+syslog.getHslog_type()+format.format(syslog.getLogdate())), LogType.LOGTYPE_SYSLOG, json));
										//newrequests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),syslog.getIndex_suffix(),syslog.getLogdate()), LogType.LOGTYPE_SYSLOG, json));
										request.index(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),syslog.getIndex_suffix(),syslog.getLogdate()));
										request.source(json, XContentType.JSON);
										logCurdDao.bulkProcessor_add(request);
									}

								}else{
									//在资产ip地址池里，但是无法识别日志类型
									syslog.setUserid(LogType.LOGTYPE_UNKNOWN);
									syslog.setDeptid(LogType.LOGTYPE_UNKNOWN);
									syslog.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
									syslog.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
									json = gson.toJson(syslog);
									//requests.add(template.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_UNKNOWN, json));
									//newrequests.add(logCurdDao.insertNotCommit(index, LogType.LOGTYPE_UNKNOWN, json));
									//newrequests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),syslog.getIndex_suffix(),syslog.getLogdate()), LogType.LOGTYPE_UNKNOWN, json));
									request.index(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),syslog.getIndex_suffix(),syslog.getLogdate()));
									request.source(json, XContentType.JSON);
									logCurdDao.bulkProcessor_add(request);
								}
							}else{
								//不在资产ip池里，暂不处理
								//TODO
							}
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("范式化失败 ，日志内容："+log);
							//System.out.println("范式化失败 ，日志内容："+log);
							continue;
						}

					}
				/*if (requests.size()==configProperty.getEs_bulk()) {
					template.bulk(requests);
					requests.clear();
				}*/
					/*Object es_bulk = ConfigurationCache.INSTANCE.getConfigurationCache().getIfPresent("es_bulk");
					if (newrequests.size()>= (es_bulk!=null?Integer.parseInt(es_bulk.toString()):0)) {
						logCurdDao.bulkInsert(newrequests);
						newrequests.clear();
					}*/
					if(count==10000){
						System.out.println("--------"+ DateTime.now());

						count=0;
					}
					count++;

					// Thread.sleep(2000);
				}

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				logger.error("es入库失败："+e1.getMessage());
				//System.out.println("es入库失败："+e1.getMessage());
				//System.out.println(e1);
			}
		}


	}
	
	// 正则匹配
	public static String getSubUtilSimple(String soap,String rgex){  
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式  
        Matcher m = pattern.matcher(soap);  
        while(m.find()){
            return m.group(1);  
        }  
        return null;  
    }

}

package com.jz.bigdata.components.kafka.logAnalysis;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.hs.elsearch.dao.logDao.ILogCrudDao;
import org.elasticsearch.action.index.IndexRequest;

import com.google.gson.Gson;
import com.jz.bigdata.business.logAnalysis.log.entity.Log4j;
import com.jz.bigdata.business.logAnalysis.log.entity.PacketFilteringFirewal;
import com.jz.bigdata.business.logAnalysis.log.entity.Syslog;
import com.jz.bigdata.business.logAnalysis.log.entity.Winlog;
import com.jz.bigdata.common.equipment.entity.Equipment;
//import com.jz.bigdata.framework.spring.es.elasticsearch.ClientTemplate;
//import com.hs.elsearch.template.bak.ClientTemplate;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;

public class SysLogKafkaConsumer implements Runnable {

	private final ConsumerConnector consumer;
	boolean suspended=false;
    private ILogCrudDao logCrudDao;
	public static Map<String,Object> map=new HashMap<String,Object>();
	
	public static List<Object> list=new ArrayList<Object>();
	 public SysLogKafkaConsumer(Equipment equipment,ILogCrudDao logCrudDao) {
		Properties props = new Properties();
		//zookeeper 配置
//		props.put("zookeeper.connect", "124.133.246.61:2281");
//		props.put("zookeeper.connect", "10.29.175.201:2181");
		props.put("zookeeper.connect", "10.4.2.5:2181");
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
//		equ=equipment;
//		template = clientTemplate;
	}
	
	public static final String REGEX = "lineStartRegex";
	public static final String DEFAULT_REGEX = "\\s?\\d\\d\\d\\d-\\d\\d-\\d\\d\\s\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d";
	public void run() {
		
//		String topicName = "testFlume";
//		String topicDBName = "flume-log4j-db";
//		String topicServerName = "flume-log4j-server";
		
		
		String topic = "all";

		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(1));
		
		// 设置译码器
		StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
		StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());

		Map<String, List<KafkaStream<String, String>>> consumerMap = consumer.createMessageStreams(topicCountMap,
				keyDecoder, valueDecoder);
		
		// 获取数据
		KafkaStream<String, String> stream = consumerMap.get(topic).get(0);
		ConsumerIterator<String, String> it = stream.iterator();

		
		
		try {
			
//			System.err.println(equ.getId()+equ.getUserId()+equ.getDepartmentId());
			Gson gson = new Gson();
			List<IndexRequest> requests = new ArrayList<IndexRequest>();
			StringBuilder builder = new StringBuilder();
			while (it.hasNext()) {
				//System.out.println("---中泰数据接收-----"+it.next().message().toString());
				
				String log = it.next().message().toString();
				
				// 日志过滤正则
				// log4j日志信息过滤条件
				Pattern facility_pattern = Pattern.compile("local3:");
				Matcher facility_matcher = facility_pattern.matcher(log);
				Pattern pattern = Pattern.compile(DEFAULT_REGEX);
				// 防火墙-包过滤日志信息过滤条件
				Pattern logtype_pattern = Pattern.compile("logtype=1");
				Matcher logtype_matcher = logtype_pattern.matcher(log);
				Pattern dmg_pattern = Pattern.compile("包过滤日志");
				Matcher dmg_matcher = dmg_pattern.matcher(log);
				// 防火墙-其他日志信息过滤条件
				Pattern logothertype_pattern = Pattern.compile("logtype=");
				Matcher logtotherype_matcher = logothertype_pattern.matcher(log);
				Pattern dmgother_pattern = Pattern.compile("dsp_msg=");
				Matcher dmgother_matcher = dmgother_pattern.matcher(log);
				// windows安全审计
				Pattern winpattern = Pattern.compile("Security-Auditing:");
				Matcher winmatcher = winpattern.matcher(log);
				
				String json =null;
				if (facility_matcher.find()) {
					synchronized (log) {
						Matcher m = pattern.matcher(log);
						//判断是否符合正则表达式 如果符合，表明这是一条开始数据
						if(m.find()) {
							//添加builder
							if (builder.length()!=0) {
								//进入范式化
								Log4j log4j = new Log4j(builder.toString());
								log4j.setUserid("b3286efc86434783aad60ce89f141d92");
								log4j.setDeptid("155");
								log4j.setEquipmentid("efbbf7dd70b9476590050faabf3ab3d3");
								json = gson.toJson(log4j);
								requests.add(logCrudDao.insertNotCommit("estest", "log4j", json));
								//清空数据
								builder.delete(0, builder.length());
							}
							builder.append(log);
						}else {
							String logleft = log.substring(0, log.indexOf(facility_matcher.group(0))+facility_matcher.group(0).length());
							log = log.replace(logleft, "");
							//添加数据
							builder.append(log);
						}
					}
				}else if (logtype_matcher.find()&&dmg_matcher.find()) {
					PacketFilteringFirewal firewall = new PacketFilteringFirewal(log);
					json = gson.toJson(firewall);
				}else if(logtotherype_matcher.find()&&dmgother_matcher.find()){
					//暂不处理
					System.out.println("-------不做处理-------------");
				}else if(winmatcher.find()){
					Winlog winlog = new Winlog(log);
					winlog.setUserid("13c6d9081d6f45928066d9ffa7c613f9");
					winlog.setDeptid("161");
					winlog.setEquipmentid("1b114cded1cf4512a59af58ad844c81e");
					json = gson.toJson(winlog);
					requests.add(logCrudDao.insertNotCommit("estest", "winlog", json));
				}else {
					Syslog syslog = new Syslog(log);
					syslog.setUserid("b3286efc86434783aad60ce89f141d92");
					syslog.setDeptid("155");
					syslog.setEquipmentid("5c0a4bc613eb4876ab1216fe3b594624");
					json = gson.toJson(syslog);
					System.out.println(json);
					requests.add(logCrudDao.insertNotCommit("estest", "syslog", json));
				}
				
				if (requests.size()==10) {
					logCrudDao.bulkInsert(requests);
					requests.clear();
				}
				
				
				// Thread.sleep(2000);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
//	  void suspend(){
//          suspended = true;
//      }
//	
//	  /**
//       * 继续
//       */
//      synchronized void resume(){
//          suspended = false;
//          notify();
//      }
	
	public static void main(String[] args) throws SQLException, InterruptedException {
//		KafkaConsumer m = new KafkaConsumer();
//		Thread t = new Thread(m);
//		t.start();
//		
//		t.suspend();
//		System.out.println("11");
//		Thread.sleep(2000);
//		t.resume();
//		System.out.println("22");
//		Thread.sleep(2000);
//		t.suspend();
//		System.out.println("33");
//		Thread.sleep(2000);
//		t.interrupt();
//		t.join();
//		
//		 System.out.println("线程已经退出!"); 
//		 
//		 t.resume();
		

	}


}
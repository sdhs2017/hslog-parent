package com.jz.bigdata.components.kafka.logAnalysis;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.hs.elsearch.dao.logDao.ILogCrudDao;
import org.elasticsearch.action.index.IndexRequest;

import com.google.gson.Gson;
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

public class WinLogKafkaConsumer implements Runnable {

	private final ConsumerConnector consumer;
	boolean suspended=false;
    private Equipment equ;
    private ILogCrudDao logCrudDao;
	public static Map<String,Object> map=new HashMap<String,Object>();

	public static List<Object> list=new ArrayList<Object>();
	 public WinLogKafkaConsumer(Equipment equipment,ILogCrudDao logCrudDao) {
		Properties props = new Properties();
		//zookeeper 配置
//		props.put("zookeeper.connect", "jzhadoop-h11:2181");
//		props.put("zookeeper.connect", "192.168.0.129:2181");
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
		equ=equipment;
		logCrudDao = logCrudDao;
	}
	
	
	public void run() {
		
//		String topicName = "testFlume";
//		String topicDBName = "flume-log4j-db";
//		String topicServerName = "flume-log4j-server";
		
		String topicWinlogName = "winlog";
		
		String topic = topicWinlogName;

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
			
			System.err.println(equ.getId()+equ.getUserId()+equ.getDepartmentId());
			Gson gson = new Gson();
			List<IndexRequest> requests = new ArrayList<IndexRequest>();
			while (it.hasNext()) {
				System.out.println(it.next().message().toString());
//				Log4j log4j = new Log4j(it.next().message().toString());
//				log4j.setUserid(equ.getUserId());
//				log4j.setDeptid(equ.getDepartmentId()+"");
//				log4j.setEquipmentid(equ.getId());
				
				Winlog winlog = new Winlog(it.next().message().toString());
				winlog.setUserid(equ.getUserId());
				winlog.setDeptid(equ.getDepartmentId()+"");
				winlog.setEquipmentid(equ.getId());
				
				String json = gson.toJson(winlog);
				
				requests.add(logCrudDao.insertNotCommit("estest", "winlog", json));
				if (requests.size()==2) {
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
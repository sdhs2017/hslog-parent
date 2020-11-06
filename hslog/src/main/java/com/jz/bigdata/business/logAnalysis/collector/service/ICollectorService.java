package com.jz.bigdata.business.logAnalysis.collector.service;

import com.jz.bigdata.common.assets_old.service.IAssetsService;

//import com.jz.bigdata.framework.spring.es.elasticsearch.ClientTemplate;
//import com.hs.elsearch.template.bak.ClientTemplate;
import com.jz.bigdata.util.ConfigProperty;

public interface ICollectorService {

	/**
	 * **************************************** 默认kafka的采集器管理******************************************
	 */

	/**
	 * 获取默认kafka的状态
	 * @return
	 */
	public boolean getKafkaCollectorState();

	/**
	 * 开启默认kafka采集器
	 * @return
	 */
	public boolean startKafkaCollector();

	/**
	 * 关闭默认kafka采集器
	 * @return
	 * @throws InterruptedException
	 */
	public boolean stopKafkaCollector()throws InterruptedException;

	/**
	 * 监听默认kafka采集器状态
	 * @return
	 */
	public boolean stateKafkaCollector() ;

	/**
	 * **************************************** masscan管理******************************************
	 */

	/**
	 * 开启masscan扫描，masscan扫描完成后自动关闭
	 * @param startip
	 * @param endip
	 * @param ports
	 * @param masscanipService
	 * @param configProperty
	 * @return
	 */
	public boolean startMasscanCollector(String startip,String endip,String ports,IAssetsService masscanipService,ConfigProperty configProperty);

	/**
	 * 监听masscan的状态
	 * @return
	 */
	public boolean stateMasscanCollector() ;

	/**
	 * **************************************** 流量采集器 pcap4j的管理******************************************
	 */
	/**
	 * 开启流量采集器pcap4j
	 * @param logCurdDao
	 * @param configProperty
	 * @return
	 */
	public String startPcap4jCollector();

	/**
	 * 监听pcap4j的状态
	 * @return
	 */
	public String statePcap4jCollector();

	/**
	 * 关闭pcap4j采集器
	 * @return
	 */
	public String stopPcap4jCollector();

	/**
	 * **************************************** 咖啡因 数据查询******************************************
	 */
	public String getCaffeineSize();


	/**
	 * **************************************** kafka of beats 的采集器管理******************************************
	 */
	/**
	 * 开启kafka of beats 的采集器
	 * @return
	 */
	public boolean startKafkaOfBeatsCollector();

	public boolean stopKafkaOfBeatsCollector()throws InterruptedException;

	public boolean stateKafkaOfBeatsCollector();

	/**
	 *开启、关闭 agent采集
	 * @return
	 */
	public String startAgentKafkaListener();
	public String stopAgentKafkaListener();
	public boolean getAgentKafkaListenerState();
	/**
	 *开启、关闭 syslog采集
	 * @return
	 */
	public String startSyslogKafkaListener();
	public String stopSyslogKafkaListener();
	public boolean getSyslogKafkaListenerState();

	/**
	 * ES 批量提交初始化
	 * @return
	 */
	public boolean bulkProcessorInit();
	/**
	 * 定时任务将获取的http 根域名  插入到资产表中
	 */
	public void insertDomain();
	/**
	 * 资产心跳机制
	 */
	public void assetsHeartBeat();
	/**
	 * 定时任务将获取的http url 插入到serviceInfo表中
	 * @return
	 */
	public void insertUrl();
	/**
	 * cache初始化
	 * 资产/全局配置项
	 */
	public void cacheInit();
}

package com.jz.bigdata.business.logAnalysis.collector.service;

import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.jz.bigdata.common.alarm.service.IAlarmService;
import com.jz.bigdata.common.asset.service.IAssetService;
import com.jz.bigdata.common.assets_old.service.IAssetsService;
import com.jz.bigdata.common.equipment.service.IEquipmentService;

//import com.jz.bigdata.framework.spring.es.elasticsearch.ClientTemplate;
//import com.hs.elsearch.template.bak.ClientTemplate;
import com.jz.bigdata.roleauthority.user.service.IUserService;
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
}

package com.jz.bigdata.business.logAnalysis.collector.service;

import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.jz.bigdata.common.alarm.service.IAlarmService;
import com.jz.bigdata.common.assets.service.IAssetsService;
import com.jz.bigdata.common.equipment.service.IEquipmentService;

//import com.jz.bigdata.framework.spring.es.elasticsearch.ClientTemplate;
//import com.hs.elsearch.template.bak.ClientTemplate;
import com.jz.bigdata.roleauthority.user.service.IUserService;
import com.jz.bigdata.util.ConfigProperty;

public interface ICollectorService {
	
	public boolean getKafkaCollectorState();
	
	public boolean startKafkaCollector(IEquipmentService equipmentService, ILogCrudDao logCurdDao, ConfigProperty configProperty, IAlarmService alarmService, IUserService usersService);
	
	public boolean stopKafkaCollector()throws InterruptedException;
	
	public boolean stateKafkaCollector() ;

	
	public boolean startMasscanCollector(String startip,String endip,String ports,IAssetsService masscanipService,ConfigProperty configProperty);
	public boolean stateMasscanCollector() ;

	public String startPcap4jCollector(ILogCrudDao logCurdDao,ConfigProperty configProperty);

	public String statePcap4jCollector();

	public String stopPcap4jCollector();
	public String startCaffeineTest();
	public String getCaffeineSize();
}

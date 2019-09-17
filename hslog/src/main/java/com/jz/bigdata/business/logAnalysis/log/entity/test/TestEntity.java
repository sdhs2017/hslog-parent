package com.jz.bigdata.business.logAnalysis.log.entity.test;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jz.bigdata.business.logAnalysis.log.LogType;
import com.jz.bigdata.business.logAnalysis.log.entity.App_file;
import com.jz.bigdata.business.logAnalysis.log.entity.DHCP;
import com.jz.bigdata.business.logAnalysis.log.entity.DNS;
import com.jz.bigdata.business.logAnalysis.log.entity.Log4j;
import com.jz.bigdata.business.logAnalysis.log.entity.Mysql;
import com.jz.bigdata.business.logAnalysis.log.entity.Netflow;
import com.jz.bigdata.business.logAnalysis.log.entity.PacketFilteringFirewal;
import com.jz.bigdata.business.logAnalysis.log.entity.Syslog;
import com.jz.bigdata.business.logAnalysis.log.entity.Winlog;
import com.jz.bigdata.business.logAnalysis.log.entity.ZtsApp;
import com.jz.bigdata.business.logAnalysis.log.entity.ZtsLog4j;
import com.jz.bigdata.business.logAnalysis.log.entity.ZtsSyslog;
import com.jz.bigdata.common.equipment.entity.Equipment;

public class TestEntity {
	
	
	Equipment equipment;
	String ipadress;
	
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
	
	/**
	 * 资产列表
	 */
	Map<String, Equipment> equipmentMap;
	Set<String> ipadressSet;
	Map<String, String> equipmentLogType;
	
	Gson gson = new GsonBuilder()
			 .setDateFormat("yyyy-MM-dd HH:mm:ss")  
			 .create(); 
	String json;
	
	static final String DEFAULT_REGEX = "^ java.|^   at";
	Calendar cal = Calendar.getInstance();
	Map<String, String> protocolmap = new HashMap<String, String>();
	/**
	 * 范式化测试
	 */
	public void format(String log) {

		StringBuilder builder = new StringBuilder();
		// 日志过滤正则
		// log4j日志信息过滤条件
		Pattern facility_pattern = Pattern.compile("local3:");
		Matcher facility_matcher = facility_pattern.matcher(log);
		Pattern pattern = Pattern.compile(DEFAULT_REGEX);
		// log4j from logstash
		Pattern log4j_pattern = Pattern.compile("\"type\":\"log4j\"");
		Matcher log4j_matcher = log4j_pattern.matcher(log);
		
		// 防火墙-包过滤日志信息过滤条件
		Pattern logtype_pattern = Pattern.compile("logtype=1");
		Matcher logtype_matcher = logtype_pattern.matcher(log);
		Pattern dmg_pattern = Pattern.compile("包过滤日志");
		Matcher dmg_matcher = dmg_pattern.matcher(log);
		// 防火墙-日志
		Pattern firewallsDevid_pattern = Pattern.compile("devid=");
		Matcher firewallsDevid_matcher = firewallsDevid_pattern.matcher(log);
		Pattern firewallsType_pattern = Pattern.compile("logtype=");
		Matcher firewallsType_matcher = firewallsType_pattern.matcher(log);
		Pattern firewallsMod_pattern = Pattern.compile("mod=");
		Matcher firewallsMod_matcher = firewallsMod_pattern.matcher(log);
		Pattern firewallsMsg_pattern = Pattern.compile("dsp_msg=");
		Matcher firewallsMsg_matcher = firewallsMsg_pattern.matcher(log);
		// windows安全审计
		Pattern win2008pattern = Pattern.compile("Security-Auditing:");
		Matcher win2008matcher = win2008pattern.matcher(log);
		Pattern win2003pattern = Pattern.compile("Security:");
		Matcher win2003matcher = win2003pattern.matcher(log);
		// mysql日志
		Pattern mysqlpattern = Pattern.compile("timestamp");
		Matcher mysqlmatcher = mysqlpattern.matcher(log);
		// netflow日志
		Pattern netflowpattern = Pattern.compile("\"type\":\"netflow\"");
		Matcher netflowmatcher = netflowpattern.matcher(log);
		// DNS日志
		Pattern dnspattern = Pattern.compile("\\s+named");
		Matcher dnsmatcher = dnspattern.matcher(log);
		//dhcp
		Pattern dhcppattern = Pattern.compile("\\s+dhcpd:");
		Matcher dhcpmatcher = dhcppattern.matcher(log);
		//filebeat
		Pattern filebeatpattern = Pattern.compile("\"logtype\":\"app_*");
		Matcher filebeatmatcher = filebeatpattern.matcher(log);
		if (facility_matcher.find()) {
			logType = LogType.LOGTYPE_LOG4J;
			synchronized (log) {
				String logleft = log.substring(0, log.indexOf(facility_matcher.group(0))+facility_matcher.group(0).length());
				
				Matcher m = pattern.matcher(log.replace(logleft, ""));
				//判断是否符合正则表达式 如果符合，表明这是一条开始数据
//				System.err.println(m.find());
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
							
							ipadress = log4j.getIp();
							//判断是否在资产ip地址池里
							if(ipadressSet.contains(ipadress)){
								//判断是否在已识别资产里————日志类型可识别
								equipment=equipmentMap.get(log4j.getIp() +logType);
								if(null != equipment){
									log4j.setUserid(equipment.getUserId());
									log4j.setDeptid(String.valueOf(equipment.getDepartmentId()));
									log4j.setEquipmentname(equipment.getName());
									log4j.setEquipmentid(equipment.getId());
									json = gson.toJson(log4j);
								}else{
									log4j.setUserid(LogType.LOGTYPE_UNKNOWN);
									log4j.setDeptid(LogType.LOGTYPE_UNKNOWN);
									log4j.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
									log4j.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
									json = gson.toJson(log4j);
								}
							}else{
								//不在资产ip池里，暂不处理
								//TODO
							}
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("范式化失败 ，日志内容："+builder.toString());
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
				
				ipadress = log4j.getIp();
				if (ipadressSet.contains(ipadress)) {
					equipment = equipmentMap.get(log4j.getIp()+logType);
					if (equipment!=null) {
						log4j.setUserid(equipment.getUserId());
						log4j.setDeptid(String.valueOf(equipment.getDepartmentId()));
						log4j.setEquipmentname(equipment.getName());
						log4j.setEquipmentid(equipment.getId());
						json = gson.toJson(log4j);
					}else {
						log4j.setUserid(LogType.LOGTYPE_UNKNOWN);
						log4j.setDeptid(LogType.LOGTYPE_UNKNOWN);
						log4j.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
						log4j.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
						json = gson.toJson(log4j);
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
				System.out.println("范式化失败 ，日志内容："+log);
			}
		
		// 注释掉原有的包过滤日志关键字判断方式，改用防火墙基本字段判断是否为防火墙日志
		//}else if (logtype_matcher.find()&&dmg_matcher.find()) {
		}else if (firewallsDevid_matcher.find()&&firewallsMod_matcher.find()&&firewallsType_matcher.find()&&firewallsMsg_matcher.find()) {
			logType = LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG;
			try {
				packetFilteringFirewal = new PacketFilteringFirewal(log);
				
				ipadress = packetFilteringFirewal.getIp();
				if (ipadressSet.contains(ipadress)) {
					equipment=equipmentMap.get(packetFilteringFirewal.getIp() +logType);
					if (equipment!=null) {
						packetFilteringFirewal.setUserid(equipment.getUserId());
						packetFilteringFirewal.setDeptid(String.valueOf(equipment.getDepartmentId()));
						packetFilteringFirewal.setEquipmentid(equipment.getId());
						packetFilteringFirewal.setEquipmentname(equipment.getName());
						json = gson.toJson(packetFilteringFirewal);
					}else {
						packetFilteringFirewal.setUserid(LogType.LOGTYPE_UNKNOWN);
						packetFilteringFirewal.setDeptid(LogType.LOGTYPE_UNKNOWN);
						packetFilteringFirewal.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
						packetFilteringFirewal.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
						json = gson.toJson(packetFilteringFirewal);
					}
				}else {
					//不在资产ip池里，暂不处理
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("范式化失败 ，日志内容："+log);
			}
			
		}else if(netflowmatcher.find()){
			logType = LogType.LOGTYPE_NETFLOW;
			try {
				netflow = new Netflow(log, cal, protocolmap);
//				netflow=netflow.SetNetflow(log, cal);
				equipment = equipmentMap.get(netflow.getIp()+logType);
				if (equipment!=null) {
					netflow.setUserid(equipment.getUserId());
					netflow.setDeptid(String.valueOf(equipment.getDepartmentId()));
					netflow.setEquipmentname(equipment.getName());
					netflow.setEquipmentid(equipment.getId());
					json = gson.toJson(netflow);
					//requests.add(template.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_NETFLOW, json));
				}
			}catch (Exception e) {
				e.printStackTrace();
				System.out.println("范式化失败 ，日志内容："+log);
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
				
				ipadress = winlog.getIp();
				//判断是否在资产ip地址池里
				if(ipadressSet.contains(ipadress)){
					//判断是否在已识别资产里————日志类型可识别
					equipment=equipmentMap.get(winlog.getIp() +logType);
					if(equipment != null){
						if (equipmentLogType.get(equipment.getId()).indexOf(winlog.getOperation_level().toLowerCase())!=-1) {
							winlog.setUserid(equipment.getUserId());
							winlog.setDeptid(String.valueOf(equipment.getDepartmentId()));
							winlog.setEquipmentname(equipment.getName());
							winlog.setEquipmentid(equipment.getId());
							json = gson.toJson(winlog);
						}
					}else{
						winlog.setUserid(LogType.LOGTYPE_UNKNOWN);
						winlog.setDeptid(LogType.LOGTYPE_UNKNOWN);
						winlog.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
						winlog.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
						json = gson.toJson(winlog);
					}
				}else{
					//不在资产ip池里，暂不处理
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("范式化失败 ，日志内容："+log);
			}
			
		}else if (dnsmatcher.find()) {
			logType = LogType.LOGTYPE_DNS;
			try {
				dns = new DNS(log);
				
				ipadress = dns.getIp();
				//判断是否在资产ip地址池里
				if(ipadressSet.contains(ipadress)){
					//判断是否在已识别资产里————日志类型可识别
					equipment=equipmentMap.get(dns.getIp() +logType);
					if(equipment != null){
						if (equipmentLogType.get(equipment.getId()).indexOf(dns.getOperation_level().toLowerCase())!=-1) {
							dns.setUserid(equipment.getUserId());
							dns.setDeptid(String.valueOf(equipment.getDepartmentId()));
							dns.setEquipmentname(equipment.getName());
							dns.setEquipmentid(equipment.getId());
							json = gson.toJson(dns);
						}
					}else{
						dns.setUserid(LogType.LOGTYPE_UNKNOWN);
						dns.setDeptid(LogType.LOGTYPE_UNKNOWN);
						dns.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
						dns.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
						json = gson.toJson(dns);
					}
				}else{
					//不在资产ip池里，暂不处理
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("范式化失败 ，日志内容："+log);
			}
		}else if (dhcpmatcher.find()) {
			logType = LogType.LOGTYPE_DHCP;
			try {
				dhcp = new DHCP(log);
				
				ipadress = dhcp.getIp();
				//判断是否在资产ip地址池里
				if(ipadressSet.contains(ipadress)){
					//判断是否在已识别资产里————日志类型可识别
					equipment=equipmentMap.get(dhcp.getIp() +logType);
					if(equipment != null){
						if (equipmentLogType.get(equipment.getId()).indexOf(dhcp.getOperation_level().toLowerCase())!=-1) {
							dhcp.setUserid(equipment.getUserId());
							dhcp.setDeptid(String.valueOf(equipment.getDepartmentId()));
							dhcp.setEquipmentname(equipment.getName());
							dhcp.setEquipmentid(equipment.getId());
							json = gson.toJson(dhcp);
						}
					}else{
						dhcp.setUserid(LogType.LOGTYPE_UNKNOWN);
						dhcp.setDeptid(LogType.LOGTYPE_UNKNOWN);
						dhcp.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
						dhcp.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
						json = gson.toJson(dhcp);
					}
				}else{
					//不在资产ip池里，暂不处理
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("范式化失败 ，日志内容："+log);
			}
		}else if (filebeatmatcher.find()) {
			logType = getSubUtilSimple(log,"\"logtype\":\"(.*?)\"");
			if(logType==null) {
				logType = LogType.LOGTYPE_APP_FILE;
			}
			try {
				app_file = new App_file(log);
				
				ipadress = app_file.getIp();
				//判断是否在资产ip地址池里
				if(ipadressSet.contains(ipadress)){
					//判断是否在已识别资产里————日志类型可识别
					equipment = equipmentMap.get(app_file.getIp() +logType);
					if(null != equipment){
						if (equipmentLogType.get(equipment.getId()).indexOf(app_file.getOperation_level().toLowerCase())!=-1) {
							app_file.setUserid(equipment.getUserId());
							app_file.setDeptid(String.valueOf(equipment.getDepartmentId()));
							app_file.setEquipmentid(equipment.getId());
							app_file.setEquipmentname(equipment.getName());
							
							json = gson.toJson(app_file);
						}
						
					}else{
						//在资产ip地址池里，但是无法识别日志类型
						app_file.setUserid(LogType.LOGTYPE_UNKNOWN);
						app_file.setDeptid(LogType.LOGTYPE_UNKNOWN);
						app_file.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
						app_file.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
						json = gson.toJson(app_file);
					}
				}else{
					//不在资产ip池里，暂不处理
					//TODO
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("范式化失败 ，日志内容："+log);
			}
		}else {
			logType = LogType.LOGTYPE_SYSLOG;
			try {
				syslog = new Syslog(log);
				
				ipadress = syslog.getIp();
				//判断是否在资产ip地址池里
				if(ipadressSet.contains(ipadress)){
					//判断是否在已识别资产里————日志类型可识别
					equipment = equipmentMap.get(syslog.getIp() +logType);
					if(null != equipment){
						if (equipmentLogType.get(equipment.getId()).indexOf(syslog.getOperation_level().toLowerCase())!=-1) {
							syslog.setUserid(equipment.getUserId());
							syslog.setDeptid(String.valueOf(equipment.getDepartmentId()));
							syslog.setEquipmentid(equipment.getId());
							syslog.setEquipmentname(equipment.getName());
							
							/*if (eventType.contains(syslog.getEvent_type())) {
								Sendmail sendmail = new Sendmail(syslog.getIp(), syslog.getEquipmentname(), syslog.getEvent_des(), usersService.selectById(syslog.getUserid()).getEmail());
							}*/
							json = gson.toJson(syslog);
						}
						
					}else{
						//在资产ip地址池里，但是无法识别日志类型
						syslog.setUserid(LogType.LOGTYPE_UNKNOWN);
						syslog.setDeptid(LogType.LOGTYPE_UNKNOWN);
						syslog.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
						syslog.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
						json = gson.toJson(syslog);
					}
				}else{
					//不在资产ip池里，暂不处理
					//TODO
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("范式化失败 ，日志内容："+log);
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

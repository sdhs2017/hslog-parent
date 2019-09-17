package com.jz.bigdata.business.logAnalysis.log.entity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Unknown {

	public final static Map<Integer, String> facility = new HashMap<>();
	
	public final static Map<Integer, String> Severity = new HashMap<>();
	static{
		facility.put(0, "kern");	//kernel messages
		facility.put(1, "user");	//user-level messages
		facility.put(2, "mail");	//mail system
		facility.put(3, "daemon");	//system daemons
		facility.put(4, "auth");	//security/authorization messages
		facility.put(5, "syslog");	//messages generated internally by syslogd
		facility.put(6, "lpr");		//line printer subsystem
		facility.put(7, "news");	//network news subsystem
		facility.put(8, "uucp");	//UUCP subsystem
		facility.put(9, "kern");	//clock daemon
		facility.put(10, "authpriv");	//security/authorization messages
		facility.put(11, "ftp");	//FTP daemon
		facility.put(15, "cron");	//scheduling daemon
		 	 	
		 	 	
		Severity.put(0, "Emergency"); 		//system is unusable
		Severity.put(1, "Alert");	 		//action must be taken immediately
		Severity.put(2, "Critical");	 		//critical conditions
		Severity.put(3, "Error");			//error conditions
		Severity.put(4, "Warning");			//warning conditions
		Severity.put(5, "Notice");			//normal but significant condition
		Severity.put(6, "Informational");	//informational messages
		Severity.put(7, "Debug");			//debug-level messages
	}
	
	
	/**
	 * id
	 */
	String id;
	/**
	 * userid
	 */
	String userid;
	/**
	 * deptid
	 */
	String deptid;
	/**
	 * equipmentid
	 */
	String equipmentid;
	/**
	 * equipmentname
	 */
	String equipmentname;
	/**
	 * 日志时间
	 */
	Date logdate;
	String logtime;
	/**
	 * ip地址
	 */
	String ip;
	/**
	 * 主机名
	 */
	String hostname;
	/**
	 * 日志模块
	 * (facility)
	 */
	String operation_facility;
	/**
	 * 日志级别
	 * (Severity level)
	 */
	String operation_level;
	/**
	 * 进程名
	 */
	String process;
	/**
	 * PID
	 */
	String PID;
	/**
	 * 操作描述
	 */
	String operation_des;
	/**
	 * 事件级别
	 */
	Integer event_level;
	/**
	 * 事件类型
	 */
	String event_type;
	/**
	 * 事件描述
	 */
	String event_des;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getDeptid() {
		return deptid;
	}

	public void setDeptid(String deptid) {
		this.deptid = deptid;
	}

	public String getEquipmentid() {
		return equipmentid;
	}

	public void setEquipmentid(String equipmentid) {
		this.equipmentid = equipmentid;
	}

	public String getEquipmentname() {
		return equipmentname;
	}

	public void setEquipmentname(String equipmentname) {
		this.equipmentname = equipmentname;
	}

	public String getLogtime() {
		return logtime;
	}

	public void setLogtime(String logtime) {
		this.logtime = logtime;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getOperation_facility() {
		return operation_facility;
	}

	public void setOperation_facility(String operation_facility) {
		this.operation_facility = operation_facility;
	}

	public String getOperation_level() {
		return operation_level;
	}

	public void setOperation_level(String operation_level) {
		this.operation_level = operation_level;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getPID() {
		return PID;
	}

	public void setPID(String pID) {
		PID = pID;
	}

	public String getOperation_des() {
		return operation_des;
	}

	public void setOperation_des(String operation_des) {
		this.operation_des = operation_des;
	}

	public Unknown() {
		
	}
	
	public String toMapping() {
		 String template = "{\n" 
                 +"\t\t\"properties\":{\n"
                         + "\t\t{#}\n" 
                 + "\t\t\t\t}"
             +"}";
		String fieldString =  getClassMapping(new Unknown());
		template = template.replace("{#}",fieldString);
		return template;
	}
	
	public <T> String getClassMapping(T classes) {
		
		StringBuilder fieldstring = new StringBuilder();
		
		String [] fielddata = {"userid","deptid","equipmentid","logtime","ip","hostname","operation_facility","operation_level","process","logtime_year","logtime_month","logtime_day","logtime_hour","logtime_minute","equipmentname","event_type"};
       Field[] fields = classes.getClass().getDeclaredFields();
       for (int i = 0; i < fields.length; i++) {
            fieldstring.append("\t\t\t\t\"" + fields[i].getName().toLowerCase() + "\": {\n");
            fieldstring.append("\t\t\t\t\t\t\"type\": \""
                       + getElasticSearchMappingType(fields[i].getType().getSimpleName(),fields[i].getName()) + "\n");
            if (fields[i].getName().equals("id")) {
           	 fieldstring.append("\t\t\t\t\t\t,\"index\": \""
                        + "false\"" + "\n");
			}
            if (Arrays.asList(fielddata).contains(fields[i].getName())) {
				fieldstring.append("\t\t\t\t\t\t,\"fielddata\": "
                       + "true" + "\n");
			}
            if (fields[i].getName().equals("operation_des")||fields[i].getName().equals("ip")||fields[i].getName().equals("process")||fields[i].getName().equals("hostname")||fields[i].getName().equals("equipmentname")||fields[i].getName().equals("event_des")) {
           	 fieldstring.append("\t\t\t\t\t\t,\"analyzer\": \""
           	 + "index_ansj\"" + "\n");
           	 fieldstring.append("\t\t\t\t\t\t,\"search_analyzer\": \""
           	 + "query_ansj\"" + "\n");
			}
            if (i == fields.length-1) {
                   fieldstring.append("\t\t\t\t\t}\n");
               } else {
                   fieldstring.append("\t\t\t\t\t},\n");
               }
       }
       return fieldstring.toString();
	}

	private static String getElasticSearchMappingType(String varType,String name) {
	    String es = "text";
	    switch (varType) {
	    case "Date":
	        es = "date\"\n"+"\t\t\t\t\t\t,\"format\":\"yyyy-MM-dd HH:mm:ss\"\n"+"\t\t\t\t\t\t";
	        break;
	    case "Double":
	        es = "double\"\n"+"\t\t\t\t\t\t,\"null_value\":\"NaN\"";
	        break;
	    case "Long":
	        es = "long\"";
	        break;
	    case "Integer":
	           es = "integer\"";
	           break;
	    default:
	    	if (name.equals("id")) {
	    		es = "keyword\"";
				}else {
					es = "text\"";
				}
	        
	        break;
	    }
	    return es;
	}
	
	public static void main(String [] args) throws IOException {
		System.out.println(new Unknown().toMapping());
	}
}

package com.jz.bigdata.business.logAnalysis.log.entity;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import joptsimple.internal.Strings;

public class ZtsSyslog extends BaseEntity{
	
	private static Logger logger = Logger.getLogger(ZtsSyslog.class);

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
		facility.put(9, "clock daemon");	//clock daemon
		facility.put(10, "authpriv");	//security/authorization messages
		facility.put(11, "ftp");	//FTP daemon
		facility.put(15, "cron");	//scheduling daemon
		 	 	
		 	 	
		Severity.put(0, "Emergency"); 		//system is unusable
		Severity.put(1, "Alert");	 		//action must be taken immediately
		Severity.put(2, "Critical");	 		//critical conditions
		Severity.put(3, "Error");			//error conditions
		Severity.put(4, "Warn");			//warning conditions
		Severity.put(5, "Notice");			//normal but significant condition
		Severity.put(6, "Info");	//informational messages
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
	String logtime_minute;
	String logtime_hour;
	String logtime_day;
	String logtime_month;
	String logtime_year;
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
	 *//*
	Integer event_level;
	*//**
	 * 事件类型
	 *//*
	String event_type;
	*//**
	 * 事件描述
	 *//*
	String event_des;*/
	/**
	 * 业务数据
	 */
	String devid;
	String dname;
	String logtype;
	String pri;
	String mod;
	String event;
	String act;
	String result;
	String user;
	String dsp_msg;
	String from;
	String fwlog;
	String cmd;
	/**
	 * 是否为业务日志
	 */
	Boolean is_business;
	
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
	public Date getLogdate() {
		return logdate;
	}
	public void setLogdate(Date logdate) {
		this.logdate = logdate;
	}
	public String getLogtime() {
		return logtime;
	}
	public void setLogtime(String logtime) {
		this.logtime = logtime;
	}
	public String getLogtime_minute() {
		return logtime_minute;
	}
	public void setLogtime_minute(String logtime_minute) {
		this.logtime_minute = logtime_minute;
	}
	public String getLogtime_hour() {
		return logtime_hour;
	}
	public void setLogtime_hour(String logtime_hour) {
		this.logtime_hour = logtime_hour;
	}
	public String getLogtime_day() {
		return logtime_day;
	}
	public void setLogtime_day(String logtime_day) {
		this.logtime_day = logtime_day;
	}
	public String getLogtime_month() {
		return logtime_month;
	}
	public void setLogtime_month(String logtime_month) {
		this.logtime_month = logtime_month;
	}
	public String getLogtime_year() {
		return logtime_year;
	}
	public void setLogtime_year(String logtime_year) {
		this.logtime_year = logtime_year;
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
	public String getDevid() {
		return devid;
	}
	public void setDevid(String devid) {
		this.devid = devid;
	}
	public String getDname() {
		return dname;
	}
	public void setDname(String dname) {
		this.dname = dname;
	}
	public String getLogtype() {
		return logtype;
	}
	public void setLogtype(String logtype) {
		this.logtype = logtype;
	}
	public String getPri() {
		return pri;
	}
	public void setPri(String pri) {
		this.pri = pri;
	}
	public String getMod() {
		return mod;
	}
	public void setMod(String mod) {
		this.mod = mod;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getAct() {
		return act;
	}
	public void setAct(String act) {
		this.act = act;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getDsp_msg() {
		return dsp_msg;
	}
	public void setDsp_msg(String dsp_msg) {
		this.dsp_msg = dsp_msg;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getFwlog() {
		return fwlog;
	}
	public void setFwlog(String fwlog) {
		this.fwlog = fwlog;
	}
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public Boolean getIs_business() {
		return is_business;
	}
	public void setIs_business(Boolean is_business) {
		this.is_business = is_business;
	}
	public static String getFacility(Integer value) {
		return facility.get(value);
	}
	public static String getLevel(Integer value) {
		return Severity.get(value);
	}
	
	public ZtsSyslog() {
		
	}
	
	public ZtsSyslog(String syslog) {
		// 正则：匹配PRI值
		Pattern PRIpattern = Pattern.compile("<[0-9]{1,5}>");
		Matcher PRImatcher = PRIpattern.matcher(syslog);
		// 正则：匹配时间
		Pattern datePattern = Pattern.compile("[0-9]{4}[-][0-9]{1,2}[-][0-9]{1,2}[ ][0-9]{1,2}[:][0-9]{1,2}[:][0-9]{1,2}");
		Matcher datematcher = datePattern.matcher(syslog);
		// 正则：匹配IP
		Pattern ipPattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)");  
        Matcher ipmatcher = ipPattern.matcher(syslog);
        // 正则：匹配PID
 		Pattern PIDPattern = Pattern.compile("\\[[0-9]{1,5}\\][:]");  
        Matcher PIDmatcher = PIDPattern.matcher(syslog);
        // 正则：非匹配PID
        Pattern NOPIDPattern = Pattern.compile("[A-Za-z]{3,11}[:]");  
        Matcher NOPIDmatcher = NOPIDPattern.matcher(syslog);
		
        this.is_business = true;
        
		if (PRImatcher.find()) {
			String PRI = PRImatcher.group(0);
			PRI = PRI.substring(1, PRI.length()-1);
			Integer PRIint = Integer.valueOf(PRI);
			String facility = getFacility(PRIint/8);
			this.operation_facility = facility;
			//System.out.print("日志类型："+facility+"\t");
			String level = getLevel(PRIint%8);
			this.operation_level = level;
			//System.out.print("日志级别:"+level+"\t");
		}
		
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String logtimetmp = null;
		if(datematcher.find()){
        	logtimetmp = datematcher.group(0);
        	this.logtime = datematcher.group(0);
        	String [] tmp = this.logtime.split(" ");
        	String [] date = tmp[0].split("-");
        	String [] time = tmp[1].split(":");
        	this.logtime_year = date[0];
        	this.logtime_month = date[1];
        	this.logtime_day = date[2];
        	this.logtime_hour = time[0];
        	this.logtime_minute = time[1];
        }
		
		if (!Strings.isNullOrEmpty(logtime)) {
			int a = syslog.indexOf(logtimetmp) + logtimetmp.length()+1;
			String tmp = syslog.substring(a, syslog.length());
			
			String [] logs = tmp.split(" ");
			if (logs.length>0) {
				this.hostname = logs[0];
			}
		}
		if (ipmatcher.find()) {
			this.ip = ipmatcher.group(0);
		}
		// 判断是否为PID
		if (PIDmatcher.find()) {
			String pidString = PIDmatcher.group(0);
			this.PID = pidString.replace("[", "").replace("]:", "");
			int x = syslog.indexOf(pidString)+pidString.length()+1;
			this.operation_des = syslog.substring(x);
			Gson gson = new Gson();
			String json = "{"+operation_des.replace("=", ":").replaceAll(" ", ",")+"}";
			ZtcBusiness ztcBusiness = gson.fromJson(json, ZtcBusiness.class);
			this.devid = ztcBusiness.getDevid();
			this.dname = ztcBusiness.getDname();
			this.logtype = ztcBusiness.getLogtype();
			this.pri = ztcBusiness.getPri();
			this.mod = ztcBusiness.getMod();
			this.dsp_msg = ztcBusiness.getDsp_msg();
			this.event = ztcBusiness.getEvent();
			this.act = ztcBusiness.getAct();
			this.result = ztcBusiness.getResult();
			if (ztcBusiness.getCmd()!=null) {
				this.cmd = ztcBusiness.getCmd().replaceAll(",", " ");
			}
			this.from = ztcBusiness.getFrom();
			this.user = ztcBusiness.getUser();
			this.fwlog = ztcBusiness.getFwlog();
			
			if (ztcBusiness.getDate()!=null) {
				try {
					this.logtime = ztcBusiness.getDate().replace(",", " ");
					String [] tmp = this.logtime.split(" ");
		        	String [] date = tmp[0].split("/");
		        	String [] time = tmp[1].split(":");
		        	this.logtime_year = date[0];
		        	this.logtime_month = date[1];
		        	this.logtime_day = date[2];
		        	this.logtime_hour = time[0];
		        	this.logtime_minute = time[1];
					this.logdate = dateformat.parse(this.logtime);
				} catch (ParseException e) {
					e.printStackTrace();
					logger.info(e.getMessage());
				}
			}
			
		// 判断是否为进程名，区别于进程id
		}else if (NOPIDmatcher.find()) {
			String processString = NOPIDmatcher.group(0);
			this.process = processString.replace(":", "");
			int x = syslog.indexOf(processString)+processString.length()+1;
			this.operation_des = syslog.substring(x);
			Gson gson = new Gson();
			String json = "{"+operation_des.replace("=", ":").replaceAll(" ", ",")+"}";
			ZtcBusiness ztcBusiness = gson.fromJson(json, ZtcBusiness.class);
			this.devid = ztcBusiness.getDevid();
			this.dname = ztcBusiness.getDname();
			this.logtype = ztcBusiness.getLogtype();
			this.pri = ztcBusiness.getPri();
			this.mod = ztcBusiness.getMod();
			this.dsp_msg = ztcBusiness.getDsp_msg();
			this.event = ztcBusiness.getEvent();
			this.act = ztcBusiness.getAct();
			this.cmd = ztcBusiness.getCmd().replaceAll(",", " ");
			this.from = ztcBusiness.getFrom();
			this.user = ztcBusiness.getUser();
			
			if (ztcBusiness.getDate()!=null) {
				try {
					this.logtime = ztcBusiness.getDate().replace(",", " ");
					String [] tmp = this.logtime.split(" ");
		        	String [] date = tmp[0].split("/");
		        	String [] time = tmp[1].split(":");
		        	this.logtime_year = date[0];
		        	this.logtime_month = date[1];
		        	this.logtime_day = date[2];
		        	this.logtime_hour = time[0];
		        	this.logtime_minute = time[1];
					this.logdate = dateformat.parse(this.logtime);
				} catch (ParseException e) {
					e.printStackTrace();
					logger.info(e.getMessage());
				}
			}
		}
	}
	
	// 业务类型
	class ZtcBusiness {
		String devid;
		String date;
		String dname;
		String logtype;
		String pri;
		String mod;
		String event;
		String act;
		String result;
		String user;
		String dsp_msg;
		String from;
		String fwlog;
		String cmd;
		
		public String getDevid() {
			return devid;
		}
		public void setDevid(String devid) {
			this.devid = devid;
		}
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public String getDname() {
			return dname;
		}
		public void setDname(String dname) {
			this.dname = dname;
		}
		public String getLogtype() {
			return logtype;
		}
		public void setLogtype(String logtype) {
			this.logtype = logtype;
		}
		public String getPri() {
			return pri;
		}
		public void setPri(String pri) {
			this.pri = pri;
		}
		public String getMod() {
			return mod;
		}
		public void setMod(String mod) {
			this.mod = mod;
		}
		public String getAct() {
			return act;
		}
		public void setAct(String act) {
			this.act = act;
		}
		public String getUser() {
			return user;
		}
		public void setUser(String user) {
			this.user = user;
		}
		public String getDsp_msg() {
			return dsp_msg;
		}
		public void setDsp_msg(String dsp_msg) {
			this.dsp_msg = dsp_msg;
		}
		public String getFwlog() {
			return fwlog;
		}
		public void setFwlog(String fwlog) {
			this.fwlog = fwlog;
		}
		public String getEvent() {
			return event;
		}
		public void setEvent(String event) {
			this.event = event;
		}
		public String getFrom() {
			return from;
		}
		public void setFrom(String from) {
			this.from = from;
		}
		public String getCmd() {
			return cmd;
		}
		public void setCmd(String cmd) {
			this.cmd = cmd;
		}
		public String getResult() {
			return result;
		}
		public void setResult(String result) {
			this.result = result;
		}
		
	}
	
	public String toMapping() {
		 String template = "{\n" 
                 +"\t\t\"properties\":{\n"
                         + "\t\t{#}\n" 
                 + "\t\t\t\t}"
             +"}";
		String fieldString =  getClassMapping(new ZtsSyslog());
		template = template.replace("{#}",fieldString);
		return template;
	}
	
	/*
	public <T> String getClassMapping(T classes) {
		
		StringBuilder fieldstring = new StringBuilder();
		
		String [] fielddata = {"userid","deptid","equipmentid","logtime","ip","hostname","operation_facility","operation_level","process","logtime_year","logtime_month","logtime_day","logtime_hour","logtime_minute","equipmentname","devid","dname","logtype","pri","mod","act","user","from","result"};
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
            if (fields[i].getName().equals("operation_des")||fields[i].getName().equals("ip")||fields[i].getName().equals("process")||fields[i].getName().equals("hostname")||fields[i].getName().equals("equipmentname")||fields[i].getName().equals("dsp_msg")||fields[i].getName().equals("cmd")||fields[i].getName().equals("from")||fields[i].getName().equals("event")) {
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
	    case "Boolean":
	    	es ="boolean\"";
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
	}*/
	
	
	public static void main(String []args) {
		/*String log = "<134> 2018-08-16 18:21:24 10.29.16.48 10.29.16.48 root: devid=3 date=\"2018/08/16 10:33:06\" dname=themis logtype=1 pri=6 mod=webui event=\"管理员[administrator]执行命令，设置日志服务器。\" dsp_msg=\"管理员[administrator]执行命令，设置日志服务器。\" from=\"10.29.172.70\" user=\"administrator\" act=\"配置\" cmd=\"logserver set ip 10.29.172.120 port 514 protocol udp 2>&1;\"";
		ZtsSyslog ztcSyslog = new ZtsSyslog(log);
		System.out.println(ztcSyslog.act);*/
		System.out.println(new ZtsSyslog().toMapping());
	}
}


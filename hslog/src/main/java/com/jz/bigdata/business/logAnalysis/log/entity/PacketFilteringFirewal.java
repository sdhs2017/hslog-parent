package com.jz.bigdata.business.logAnalysis.log.entity;

import java.io.*;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;


public class PacketFilteringFirewal {

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
	/*String logtime_minute;
	String logtime_hour;
	String logtime_day;
	String logtime_month;
	String logtime_year;*/
	
	
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
	
	String devid;
	
	/**
	 * 日志时间
	 */
	String date;
	/**
	 * 设备名称
	 */
	String dname;
	/**
	 * 日志消息类型
	 */
	String logtype;
	/**
	 * 版本
	 */
	String ver;
	/**
	 * 模块
	 */
	String mod;
	/**
	 * 源IP
	 */
	String sa;
	/**
	 * 源端口
	 */
	String sport;
	String type;
	/**
	 * 目的IP
	 */
	String da;
	/**
	 * 目的端口
	 */
	String dport;
	String code;
	/**
	 * ip协议
	 */
	String proto;
	/**
	 * 策略
	 */
	String policy;
	/**
	 * 周期
	 */
	String duration;
	/**
	 * 已接收
	 */
	String rcvd;
	/**
	 * 已发送
	 */
	String sent;
	/**
	 * 公开地址
	 */
	String sata;
	String pa;
	/**
	 * 端口
	 */
	String saport;
	String pport;
	String fwlog;
	/**
	 * IP来源
	 */
	String from;
	String admin;
	String act;
	String result;
	String user;
	String agent;
	/**
	 * 日志消息类型
	 */
	String dsp_msg;
	
	/**
	 * 日志级别
	 * (Severity level)
	 */
	String operation_level;
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
	/**
	 * 日志类型
	 */
	private String hslog_type;

	/**
	 * 设置index名称的后缀
	 */
	private String index_suffix;

	
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

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getMod() {
		return mod;
	}

	public void setMod(String mod) {
		this.mod = mod;
	}

	public String getSa() {
		return sa;
	}

	public void setSa(String sa) {
		this.sa = sa;
	}

	public String getSport() {
		return sport;
	}

	public void setSport(String sport) {
		this.sport = sport;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDa() {
		return da;
	}

	public void setDa(String da) {
		this.da = da;
	}

	public String getDport() {
		return dport;
	}

	public void setDport(String dport) {
		this.dport = dport;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getProto() {
		return proto;
	}

	public void setProto(String proto) {
		this.proto = proto;
	}

	public String getPolicy() {
		return policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getRcvd() {
		return rcvd;
	}

	public void setRcvd(String rcvd) {
		this.rcvd = rcvd;
	}

	public String getSent() {
		return sent;
	}

	public void setSent(String sent) {
		this.sent = sent;
	}

	public String getSata() {
		return sata;
	}

	public void setSata(String sata) {
		this.sata = sata;
	}

	public String getSaport() {
		return saport;
	}

	public void setSaport(String saport) {
		this.saport = saport;
	}

	public String getFwlog() {
		return fwlog;
	}

	public void setFwlog(String fwlog) {
		this.fwlog = fwlog;
	}

	public String getDsp_msg() {
		return dsp_msg;
	}

	public void setDsp_msg(String dsp_msg) {
		this.dsp_msg = dsp_msg;
	}

	public String getPa() {
		return pa;
	}

	public void setPa(String pa) {
		this.pa = pa;
	}

	public String getPport() {
		return pport;
	}

	public void setPport(String pport) {
		this.pport = pport;
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

	public String getOperation_level() {
		return operation_level;
	}

	public void setOperation_level(String operation_level) {
		this.operation_level = operation_level;
	}

	public String getOperation_des() {
		return operation_des;
	}

	public void setOperation_des(String operation_des) {
		this.operation_des = operation_des;
	}

	public Integer getEvent_level() {
		return event_level;
	}

	public void setEvent_level(Integer event_level) {
		this.event_level = event_level;
	}

	public String getEvent_type() {
		return event_type;
	}

	public void setEvent_type(String event_type) {
		this.event_type = event_type;
	}

	public String getEvent_des() {
		return event_des;
	}

	public void setEvent_des(String event_des) {
		this.event_des = event_des;
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

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
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

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getHslog_type() {
		return hslog_type;
	}

	public void setHslog_type(String hslog_type) {
		this.hslog_type = hslog_type;
	}

	public String getIndex_suffix() {
		return index_suffix;
	}

	public void setIndex_suffix(String index_suffix) {
		this.index_suffix = index_suffix;
	}

	public PacketFilteringFirewal() {
		
	}
	
	public PacketFilteringFirewal(String log) {
		
		/*if (!log.contains("logtype=1")||!log.contains("包过滤日志")) {
			return ;
		}*/
		Pattern pattern = Pattern.compile("<\\d{1,5}>\\s+\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\s+\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\s+\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\s+[A-Za-z]{1,10}:\\s+");
		Matcher matcher = pattern.matcher(log);
		// 去除不规则数据
		Pattern NGPattern = Pattern.compile("[A-Za-z0-9]{1,10}=[0-9]{1,10}:[0-9]{1,10}");
		Matcher NGmatcher = NGPattern.matcher(log);
		Pattern NGPatternIP = Pattern.compile("[A-Za-z]{1,10}=\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
		Matcher NGmatcherIP = NGPatternIP.matcher(log);
		Pattern NGPatternFilter = Pattern.compile("[A-Za-z]{1,10}=\\s+");
		Matcher NGmatcherFilter = NGPatternFilter.matcher(log);
		// 两个逗号之间没有冒号
		Pattern NGPatternNO = Pattern.compile("[,][A-Za-z]{1,100}[-][\u4E00-\u9FA5]{1,100}[,]");
		Matcher NGmatcherNO = NGPatternNO.matcher(log);
		
		while (NGmatcher.find()) {
			log = log.replaceAll(NGmatcher.group(0), "");
		}
		while (NGmatcherIP.find()) {
			String tmp = NGmatcherIP.group(0);
			Pattern PatternIP = Pattern.compile(":\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
			Matcher matcherIP = PatternIP.matcher(tmp);
			if (matcherIP.find()) {
				tmp = tmp.replaceAll(matcherIP.group(0), "");
				log = log.replaceAll(NGmatcherIP.group(0), tmp);
			}else{
				log = log.replaceAll(NGmatcherIP.group(0), "");
			}
		}
		
		if (NGmatcherFilter.find()) {
			log = log.replaceAll(NGmatcherFilter.group(0), "");
		}
		
		// 通过正则截取防护墙前段日志（即syslog的信息除message）
		if (matcher.find()) {
			String leftlog = matcher.group(0);
			Pattern PRIpattern = Pattern.compile("<[0-9]{1,5}>");
			Matcher PRImatcher = PRIpattern.matcher(leftlog);
			Pattern IPpattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
			Matcher IPmatcher = IPpattern.matcher(leftlog);
			Pattern ProcessPattern = Pattern.compile("[A-Za-z]{1,30}:");
			Matcher ProcessMatcher = ProcessPattern.matcher(leftlog);
			if (PRImatcher.find()) {
				String PRI = PRImatcher.group(0);
				PRI = PRI.substring(1, PRI.length()-1);
				Integer PRIint = Integer.valueOf(PRI);
				this.event_level = PRIint%8;
				String facility = getFacility(PRIint/8);
				this.operation_facility = facility;
				//System.out.print("日志类型："+facility+"\t");
				String level = getLevel(PRIint%8);
				this.operation_level = level;
				//System.out.print("日志级别:"+level+"\t");
			}
			if (IPmatcher.find()) {
				this.ip=IPmatcher.group(0);
			}
			
			log = log.replace(matcher.group(0), "");
			this.operation_des = log;
			log = "{"+log.replaceAll("=", ":")+"}";
			log = log.replaceAll(" +", ",");
			log = log.replaceAll(",,", ",");
			// 去除不规则数据
			if (ProcessMatcher.find()) {
				String tmp = ProcessMatcher.group(0).replace(":", "");
				Pattern NGPattern1 = Pattern.compile("mod:"+tmp+",");
				Matcher NGmatcher1 = NGPattern1.matcher(log);
				Pattern NGPattern2 = Pattern.compile("mod:[\u4E00-\u9FA5]{1,50},");
				Matcher NGmatcher2 = NGPattern2.matcher(log);
				if (!NGmatcher1.find()&&!NGmatcher2.find()) {
					System.out.println(NGmatcher2.group(0));
					log = log.replaceAll("mod:"+tmp, "mod:"+tmp+",");
				}
			}
			if (NGmatcherNO.find()) {
				log = log.replaceAll(NGmatcherNO.group(0), ",");
			}
			
			
			Gson gson = new Gson();
			PacketFilteringFirewal fire = gson.fromJson(log, PacketFilteringFirewal.class);
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			this.devid = fire.getDevid();
			this.date = fire.getDate().replace(",", " ");
			this.logtime = this.date.replace("/", "-");
			/*String [] tmp = this.logtime.split(" ");
        	String [] date = tmp[0].split("-");
        	String [] time = tmp[1].split(":");
        	this.logtime_year = date[0];
        	this.logtime_month = date[1];
        	this.logtime_day = date[2];
        	this.logtime_hour = time[0];
        	this.logtime_minute = time[1];*/
        	try {
				this.logdate = dateformat.parse(this.logtime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			this.dname = fire.getDname();
			this.logtype = fire.getLogtype();
			this.ver = fire.getVer();
			this.mod = fire.getMod();
			this.sa = fire.getSa();
			this.sport = fire.getSport();
			this.type = fire.getType();
			this.da = fire.getDa();
			this.dport = fire.getDport();
			this.code = fire.getCode();
			this.proto = fire.getProto();
			this.policy = fire.getPolicy();
			this.duration = fire.getDuration();
			this.rcvd = fire.getRcvd();
			this.sent = fire.getSent();
			this.agent = fire.getAgent();
			this.act = fire.getAct();
			this.result = fire.getResult();
			this.user = fire.getUser();
			this.from = fire.getFrom();
			this.admin = fire.getAdmin();
			if (fire.getSata()!=null) {
				this.sata = fire.getSata();
			}else if (fire.getPa()!=null) {
				this.sata = fire.getPa();
			}
			if (fire.getSaport()!=null) {
				this.saport = fire.getSaport();
			}else if (fire.getPport()!=null) {
				this.saport = fire.getPport();
			}
			this.fwlog = fire.getFwlog();
			this.dsp_msg = fire.getDsp_msg();
		}
		this.index_suffix = "syslog";
		
	}
	
	public static String getFacility(Integer value) {
		return facility.get(value);
	}
	public static String getLevel(Integer value) {
		return Severity.get(value);
	}
	
	public String toMapping() {
		 String template = "{\n" 
                 +"\t\t\"properties\":{\n"
                         + "\t\t{#}\n" 
                 + "\t\t\t\t}"
             +"}";
		String fieldString =  getClassMapping(new PacketFilteringFirewal());
		template = template.replace("{#}",fieldString);
		return template;
	}
	
	public <T> String getClassMapping(T classes) {
		
		StringBuilder fieldstring = new StringBuilder();
		
		String [] fielddata = {"userid","deptid","equipmentid","logtime","ip","from","hostname","operation_facility","operation_level","process","logtime_year","logtime_month","logtime_day","logtime_hour","logtime_minute","equipmentname","event_type","devid","dname","logtype","mod","act"};
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
            // 由于旧版es的防护墙dsp_msg采用包过滤，该字段过于简单，所以没有设置分词，所以该字段没有更新   ||fields[i].getName().equals("dsp_msg")
            if (fields[i].getName().equals("operation_des")||fields[i].getName().equals("ip")||fields[i].getName().equals("process")||fields[i].getName().equals("hostname")||fields[i].getName().equals("equipmentname")||fields[i].getName().equals("event_des")||fields[i].getName().equals("from")||fields[i].getName().equals("act")) {
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
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//String log = "<6> 2018-04-17 01:17:06 124.133.246.61 124.133.246.61 kernel: devid=0 date=\"2017/10/30 11:26:08\" dname=venus logtype=1 pri=5 ver=0.3.0 mod=pf sa=58.242.83.10 sport=49996 type=NULL da=124.133.246.61 dport=22 code=NULL proto=IPPROTO_TCP policy=POLICY_NAT_PERMIT duration=0 rcvd=60 sent=60 pa=192.168.2.161 pport=22 fwlog=0 dsp_msg=\"包过滤日志\"";
		//String yclog="<6> 2018-04-17 01:17:06 10.60.32.154 10.60.32.154 kernel: devid=0 date=\"2018/04/16 17:44:07\" dname=venus logtype=1 pri=5 ver=0.3.0 mod=pf sa=10.37.54.75 sport=137 type=NULL da=10.37.54.127 dport=137 code=NULL proto=IPPROTO_UDP policy=POLICY_DENY duration=0 rcvd=78 sent=78 fwlog=0 dsp_msg=\"包过滤日志\"";
		String dzlog ="<158> 2018-09-20 15:44:22 10.61.64.250 10.61.64.250 webui: devid=0 date=\"2018/09/20 15:45:44\" dname=zhuxianlu_fw logtype=9 pri=6 ver=0.3.0 mod=webui from=10.63.64.132 agent=\"Mozilla/4.0 \" admin=administrator act=登录 result=0 msg=\"成功\" dsp_msg=\"administrator 登录\"  fwlog=0";
		String configlog = "<157> 2018-09-20 15:36:43 10.61.64.250 10.61.64.250 newconfig: devid=0 date=\"2018/09/20 15:38:05\" dname=zhuxianlu_fw logtype=9 pri=5 ver=0.3.0 mod=newconfig act=保存配置 result=0 user=\"administrator\" dsp_msg=\"保存配置\" fwlog=0";
	  //Pattern PRIpattern = Pattern.compile("<\\d{1,5}>\\s+\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\s+\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\s+\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\s+[A-Za-z]{1,6}:\\s+");
		/*Pattern PRIpattern = Pattern.compile("<\\d{1,5}>\\s+\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\s+\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\s+\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\s+[A-Za-z]{1,6}:\\s+");
		Matcher PRImatcher = PRIpattern.matcher(log);
		if (PRImatcher.find()) {
			System.out.println(PRImatcher.group(0));
		}*/
		
		
		//PacketFilteringFirewal firewall = new PacketFilteringFirewal(configlog);
		System.out.println(new PacketFilteringFirewal().toMapping());
		
	}

}

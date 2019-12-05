package com.jz.bigdata.business.logAnalysis.log.entity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import joptsimple.internal.Strings;


/**
 * @author jiyourui
 *
 */
public class DNS {

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
	/**
	 * 是否为业务日志
	 */
	Boolean is_business;
	
	// ----DNS---属性
	/**
	 * dns 客户IP
	 */
	String dns_clientip;
	/**
	 * dns view
	 */
	String dns_view;
	/**
	 * dns 域名
	 */
	String dns_domain_name;
	/**
	 * dns 解析数据类型
	 */
	String dns_ana_type;
	/**
	 * dns 服务器
	 */
	String dns_server;
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

	public String getDns_clientip() {
		return dns_clientip;
	}

	public void setDns_clientip(String dns_clientip) {
		this.dns_clientip = dns_clientip;
	}

	public String getDns_view() {
		return dns_view;
	}

	public void setDns_view(String dns_view) {
		this.dns_view = dns_view;
	}

	public String getDns_domain_name() {
		return dns_domain_name;
	}

	public void setDns_domain_name(String dns_domain_name) {
		this.dns_domain_name = dns_domain_name;
	}

	public String getDns_ana_type() {
		return dns_ana_type;
	}

	public void setDns_ana_type(String dns_ana_type) {
		this.dns_ana_type = dns_ana_type;
	}

	public String getDns_server() {
		return dns_server;
	}

	public void setDns_server(String dns_server) {
		this.dns_server = dns_server;
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

	public DNS() {
		
	}
	
	public DNS(String log) {
		
		Pattern PRIpattern = Pattern.compile("<[0-9]{1,5}>");
		Matcher PRImatcher = PRIpattern.matcher(log);
		
		Pattern datePattern = Pattern.compile("[0-9]{4}[-][0-9]{1,2}[-][0-9]{1,2}[ ][0-9]{1,2}[:][0-9]{1,2}[:][0-9]{1,2}");
		Matcher datematcher = datePattern.matcher(log);
		
	    
	    /*// 截取flume时间
	 	Pattern datePattern = Pattern.compile("[0-9]{4}[-][0-9]{1,2}[-][0-9]{1,2}[ ][0-9]{1,2}[:][0-9]{1,2}[:][0-9]{1,2}[.][0-9]{2,3}");  
	    Matcher datematcher = datePattern.matcher(syslog);*/
	    // 事件定性
	    Pattern shutdownpattern = Pattern.compile("shutdown",Pattern.CASE_INSENSITIVE);
		Matcher shutdownmatcher = shutdownpattern.matcher(log);

	    Pattern networkpattern = Pattern.compile("NetworkManager",Pattern.CASE_INSENSITIVE);
		Matcher networkmatcher = networkpattern.matcher(log);
		
		Pattern usbpattern = Pattern.compile("usb",Pattern.CASE_INSENSITIVE);
		Matcher usbmatcher = usbpattern.matcher(log);
		
		Pattern sshdpattern = Pattern.compile("sshd",Pattern.CASE_INSENSITIVE);
		Matcher sshdmatcher = sshdpattern.matcher(log);
		
		Pattern loginpattern = Pattern.compile("systemd-logind",Pattern.CASE_INSENSITIVE);
		Matcher loginmatcher = loginpattern.matcher(log);
		
		Pattern supattern = Pattern.compile("su",Pattern.CASE_INSENSITIVE);
		Matcher sumatcher = supattern.matcher(log);
		
		Pattern rh7sessionpattern = Pattern.compile("Starting Session",Pattern.CASE_INSENSITIVE);
		Matcher rh7sessionmatcher = rh7sessionpattern.matcher(log);
		
		Pattern rh6sessionpattern = Pattern.compile("session opened",Pattern.CASE_INSENSITIVE);
		Matcher rh6sessionmatcher = rh6sessionpattern.matcher(log);
		
		Pattern rsyslogpattern = Pattern.compile("rsyslogd",Pattern.CASE_INSENSITIVE);
		Matcher rsyslogmatcher = rsyslogpattern.matcher(log);
		
		Pattern pcipattern = Pattern.compile("kernel: pci",Pattern.CASE_INSENSITIVE);
		Matcher pcimatcher = pcipattern.matcher(log);
		
		Pattern pcibuspattern = Pattern.compile("kernel: pci_bus",Pattern.CASE_INSENSITIVE);
		Matcher pcibusmatcher = pcibuspattern.matcher(log);
		
		Pattern ACPIpattern = Pattern.compile("kernel: ACPI",Pattern.CASE_INSENSITIVE);
		Matcher ACPImatcher = ACPIpattern.matcher(log);
		Pattern pmpattern = Pattern.compile("kernel: PM",Pattern.CASE_INSENSITIVE);
		Matcher pmmatcher = pmpattern.matcher(log);
		Pattern SRATpattern = Pattern.compile("kernel: SRAT",Pattern.CASE_INSENSITIVE);
		Matcher SRATmatcher = SRATpattern.matcher(log);
		Pattern CRONDpattern = Pattern.compile("CROND",Pattern.CASE_INSENSITIVE);
		Matcher CRONDmatcher = CRONDpattern.matcher(log);
		
        
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
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dhcpformat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS",Locale.ENGLISH);
		String logtimetmp = null;
		if(datematcher.find()){
        	logtimetmp = datematcher.group(0);
        	try {
				this.logdate = dateformat.parse(logtimetmp);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	this.logtime = datematcher.group(0);
        	/*String [] tmp = this.logtime.split(" ");
        	String [] date = tmp[0].split("-");
        	String [] time = tmp[1].split(":");
        	this.logtime_year = date[0];
        	this.logtime_month = date[1];
        	this.logtime_day = date[2];
        	this.logtime_hour = time[0];
        	this.logtime_minute = time[1];*/
        }
		
		if (!Strings.isNullOrEmpty(logtime)) {
			int a = log.indexOf(logtimetmp) + logtimetmp.length()+1;
			String tmp = log.substring(a, log.length());
			
			String [] logs = tmp.split(" ");
			if (logs.length>0) {
				this.hostname = logs[0];
			}
			if (logs.length>1) {
				this.ip = logs[1];
			}
			if (logs.length>2) {
				String logtype = logs[2];
				if (logtype.contains("[")) {
					String[] jingcheng = logtype.split("\\[");
					String jc = jingcheng[0];
					String pid = jingcheng[1].substring(0, jingcheng[1].length()-2);
					this.process = jc;
					this.PID = pid;
				}else{
					this.process = logtype.replace(":", "");
				}
			}
			if (logs.length>3) {
				String des = log.substring(log.indexOf(logs[3]), log.length());
				this.operation_des = des;
				// dns时间匹配
				Pattern DNSdatePattern = Pattern.compile("[0-9]{1,2}[-][A-Za-z]{3}[-][0-9]{4}[ ][0-9]{1,2}[:][0-9]{1,2}[:][0-9]{1,2}[.][0-9]{3}");
				Matcher DNSdatematcher = DNSdatePattern.matcher(this.operation_des);
				
				if (DNSdatematcher.find()) {
					try {
						this.logdate = dhcpformat.parse(DNSdatematcher.group(0));
						this.logtime = format.format(logdate);
						/*String [] tmptime = this.logtime.split(" ");
			        	String [] date = tmptime[0].split("-");
			        	String [] time = tmptime[1].split(":");
			        	this.logtime_year = date[0];
			        	this.logtime_month = date[1];
			        	this.logtime_day = date[2];
			        	this.logtime_hour = time[0];
			        	this.logtime_minute = time[1];*/
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				this.dns_clientip = getSubUtilSimple(des, "client\\s+(.*?)#");
				this.dns_view = getSubUtilSimple(des, "view\\s+(.*?):");
				this.dns_domain_name = getSubUtilSimple(des, "query:\\s+(.*?)\\s+IN");
				this.dns_ana_type = getSubUtilSimple(des, "IN\\s+(.*?)\\s+[+]");
				this.dns_server = getSubUtilSimple(des, "[+]\\s+\\((.*?)\\)");
				
			}else {
				this.operation_des = "";
			}
			
		}
		
		this.is_business = false;
		
		if (shutdownmatcher.find()) {
			this.event_type="poweroff";
			this.event_des="主机关机";
		}
		if (networkmatcher.find()) {
			this.event_type="NetworkManager";
			this.event_des="网络服务";
		}
		if (usbmatcher.find()) {
			this.event_type="usb";
			this.event_des="usb外接";
		}
		if (sshdmatcher.find()) {
			this.event_type="sshd";
			this.event_des="通过ssh方式进行操作";
		}
		if (loginmatcher.find()) {
			this.event_type="login";
			this.event_des="用户登录";
		}
		if (sumatcher.find()) {
			this.event_type="su";
			this.event_des="通过su方式登录";
		}
		if (rh6sessionmatcher.find()||rh7sessionmatcher.find()) {
			this.event_type="session";
			this.event_des="开启新的会话窗口";
		}
		if (rsyslogmatcher.find()) {
			this.event_type="rsyslogd";
			this.event_des="rsyslog自身日志";
		}
		if (pcimatcher.find()) {
			this.event_type="pci";
			this.event_des="pci日志";
		}
		if (pcibusmatcher.find()) {
			this.event_type="pci_bus";
			this.event_des="pci_bus日志";
		}
		if (ACPImatcher.find()) {
			this.event_type="ACPI";
			this.event_des="ACPI日志";
		}
		if (pmmatcher.find()) {
			this.event_type="PM";
			this.event_des="PM日志";
		}
		if (SRATmatcher.find()) {
			this.event_type="SRAT";
			this.event_des="SRAT日志";
		}
		if (CRONDmatcher.find()) {
			this.event_type="crond";
			this.event_des="定时任务";
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
		String fieldString =  getClassMapping(new DNS());
		template = template.replace("{#}",fieldString);
		return template;
	}

	public <T> String getClassMapping(T classes) {
		
	   StringBuilder fieldstring = new StringBuilder();
		
	   String [] fielddata = {"userid","deptid","equipmentid","logtime","ip","hostname","operation_facility","operation_level","process","logtime_year","logtime_month","logtime_day","logtime_hour","logtime_minute","equipmentname","event_type","dns_clientip","dns_view","dns_domain_name","dns_ana_type","dns_server"};
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
            if (fields[i].getName().equals("operation_des")||fields[i].getName().equals("ip")||fields[i].getName().equals("process")||fields[i].getName().equals("hostname")||fields[i].getName().equals("equipmentname")||fields[i].getName().equals("event_des")||fields[i].getName().equals("dns_clientip")||fields[i].getName().equals("dns_view")||fields[i].getName().equals("dns_domain_name")||fields[i].getName().equals("dns_ana_type")||fields[i].getName().equals("dns_server")) {
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
	
	// 正则匹配
	public static String getSubUtil(String soap,String rgex){  
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式  
        Matcher m = pattern.matcher(soap);  
        while(m.find()){
            return m.group(0);  
        }  
        return null;  
    }
	
	public static void main(String [] args) throws IOException {
		
		System.out.println(new DNS().toMapping());
		
		/*String log1 = "<30> 2018-02-09 16:26:09 ruin4 222.173.28.150 #015";
		String log2 = "<30> 2018-02-09 16:26:09 ruin4 222.173.28.150 systemd: Started Delayed Shutdown Service.";
		String log3 = "<30> 2018-02-09 16:26:09 ruin4 222.173.28.150 systemd-shutdownd: Shutting down at Fri 2018-02-09 16:27:09 CST (poweroff)...";
		String log4 = "<30> 2018-02-09 16:26:09 ruin4 222.173.28.150 systemd-shutdownd: Creating /run/nologin, blocking further logins...";
		
		String [] logs = {log1,log2,log3,log4};
		
		Gson gson = new GsonBuilder()
				 .setDateFormat("yyyy-MM-dd HH:mm:ss")  
				 .create(); 
		String json;
		
		for(String log:logs) {
			Syslog syslog = new Syslog(log);
			json = gson.toJson(syslog);
			System.out.println(json);
		}*/
		/*Gson gson = new GsonBuilder()
				 .setDateFormat("yyyy-MM-dd HH:mm:ss")  
				 .create(); 
		String json;
		String log = "<30> 2018-02-09 16:26:09 ruin4 222.173.28.150 #015";
		Syslog syslog = new Syslog(log);
		json = gson.toJson(syslog);
		System.out.println(json);*/
		
		/*SimpleDateFormat dhcpformat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS",Locale.ENGLISH);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		String logtimetmp = "07-Jan-2019 16:18:01.488";
		
		try {
			Date date = dhcpformat.parse(logtimetmp);
			
			String time = format.format(date);
			
			System.out.println(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		/*String log = "<30> 2019-01-07 16:18:01 dev 123.232.103.226 named[15850]: 07-Jan-2019 16:18:01.488 client 127.0.0.1#53260 (dev): view internal: query: dev IN AAAA + (127.0.0.1)";
		
		System.out.println(getSubUtilSimple(log, "client (.*?)#"));
		System.out.println(getSubUtilSimple(log, "view\\s+(.*?):"));
		System.out.println(getSubUtilSimple(log, "query:\\s+(.*?)\\s+IN"));
		System.out.println(getSubUtilSimple(log, "IN\\s+(.*?)\\s+[+]"));
		System.out.println(getSubUtilSimple(log, "[+]\\s+\\((.*?)\\)"));*/
		
		/*if (getSubUtil(log,"\\s+named")!=null) {
			DNS dns = new DNS(log);
			System.out.println("---------");
		}*/
		
	}
	
}

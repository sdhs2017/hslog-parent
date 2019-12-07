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
public class Syslog {

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
	private String id;
	/**
	 * userid
	 */
	private String userid;
	/**
	 * deptid
	 */
	private String deptid;
	/**
	 * equipmentid
	 */
	private String equipmentid;
	/**
	 * equipmentname
	 */
	private String equipmentname;
	/**
	 * 日志时间
	 */
	private Date logdate;
	private String logtime;

	/**
	 * ip地址
	 */
	private String ip;
	/**
	 * 主机名
	 */
	private String hostname;
	/**
	 * syslog日志PRI值或者英文字符
	 */
	private String pri;
	/**
	 * 日志模块
	 * (facility)
	 */
	private String operation_facility;
	/**
	 * 日志级别
	 * (Severity level)
	 */
	private String operation_level;
	/**
	 * 进程名
	 */
	private String process;
	/**
	 * PID
	 */
	private String PID;
	/**
	 * 操作描述
	 */
	private String operation_des;
	/**
	 * 事件级别
	 */
	private Integer event_level;
	/**
	 * 事件类型
	 */
	private String event_type;
	/**
	 * 事件描述
	 */
	private String event_des;
	/**
	 * 是否为业务日志
	 */
	private Boolean is_business;

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

	public Syslog() {
		
	}
	
	public Syslog(String syslog) {
		
		// syslog PRI值正则截取
		Pattern PRIpattern = Pattern.compile("<[0-9]{1,5}>");
		Matcher PRImatcher = PRIpattern.matcher(syslog);
		// syslog 非PRI值正则截取
		Pattern NOPRIpattern = Pattern.compile("<[A-Za-z]{1,5}>");
		Matcher NOPRImatcher = NOPRIpattern.matcher(syslog);
		
		Pattern datePattern1 = Pattern.compile("[0-9]{4}[-][0-9]{1,2}[-][0-9]{1,2}[ ][0-9]{1,2}[:][0-9]{1,2}[:][0-9]{1,2}");
		Matcher datematcher1 = datePattern1.matcher(syslog);
		
		Pattern datePattern2 = Pattern.compile("[A-Za-z]{3}\\s+[0-9]{1,2}\\s+[0-9]{1,2}[:][0-9]{1,2}[:][0-9]{1,2}");
		Matcher datematcher2 = datePattern2.matcher(syslog);
		
	    
	    // 截取flume时间
	 	Pattern datePattern = Pattern.compile("[0-9]{4}[-][0-9]{1,2}[-][0-9]{1,2}[ ][0-9]{1,2}[:][0-9]{1,2}[:][0-9]{1,2}[.][0-9]{2,3}");  
	    Matcher datematcher = datePattern.matcher(syslog);
	    // 事件定性
	    Pattern shutdownpattern = Pattern.compile("shutdown\\s+",Pattern.CASE_INSENSITIVE);
		Matcher shutdownmatcher = shutdownpattern.matcher(syslog);
		
		Pattern sshFailedpattern = Pattern.compile("Failed\\s+password\\s+for\\s+invalid\\s+user\\s+(.*?)\\s+from\\s+(\\d+\\.\\d+\\.\\d+\\.\\d+)\\s+port\\s+[0-9]{1,5}\\s+ssh2",Pattern.CASE_INSENSITIVE);
		Matcher sshFailedmatcher = sshFailedpattern.matcher(syslog);

	    Pattern networkpattern = Pattern.compile("NetworkManager",Pattern.CASE_INSENSITIVE);
		Matcher networkmatcher = networkpattern.matcher(syslog);
		
		Pattern usbpattern = Pattern.compile("usb",Pattern.CASE_INSENSITIVE);
		Matcher usbmatcher = usbpattern.matcher(syslog);
		
		Pattern sshdpattern = Pattern.compile("Accepted password for (.*?) from (\\d+\\.\\d+\\.\\d+\\.\\d+) port [0-9]{1,5} ssh2",Pattern.CASE_INSENSITIVE);
		Matcher sshdmatcher = sshdpattern.matcher(syslog);
		
		Pattern loginpattern = Pattern.compile("systemd-logind",Pattern.CASE_INSENSITIVE);
		Matcher loginmatcher = loginpattern.matcher(syslog);
		
		Pattern supattern = Pattern.compile("su:",Pattern.CASE_INSENSITIVE);
		Matcher sumatcher = supattern.matcher(syslog);
		
		Pattern rh7sessionpattern = Pattern.compile("Starting Session",Pattern.CASE_INSENSITIVE);
		Matcher rh7sessionmatcher = rh7sessionpattern.matcher(syslog);
		
		Pattern rh6sessionpattern = Pattern.compile("session opened",Pattern.CASE_INSENSITIVE);
		Matcher rh6sessionmatcher = rh6sessionpattern.matcher(syslog);
		
		Pattern rsyslogpattern = Pattern.compile("rsyslogd",Pattern.CASE_INSENSITIVE);
		Matcher rsyslogmatcher = rsyslogpattern.matcher(syslog);
		
		Pattern pcipattern = Pattern.compile("kernel: pci",Pattern.CASE_INSENSITIVE);
		Matcher pcimatcher = pcipattern.matcher(syslog);
		
		Pattern pcibuspattern = Pattern.compile("kernel: pci_bus",Pattern.CASE_INSENSITIVE);
		Matcher pcibusmatcher = pcibuspattern.matcher(syslog);
		
		Pattern ACPIpattern = Pattern.compile("kernel: ACPI",Pattern.CASE_INSENSITIVE);
		Matcher ACPImatcher = ACPIpattern.matcher(syslog);
		Pattern pmpattern = Pattern.compile("kernel: PM",Pattern.CASE_INSENSITIVE);
		Matcher pmmatcher = pmpattern.matcher(syslog);
		Pattern SRATpattern = Pattern.compile("kernel: SRAT",Pattern.CASE_INSENSITIVE);
		Matcher SRATmatcher = SRATpattern.matcher(syslog);
		Pattern CRONDpattern = Pattern.compile("CROND",Pattern.CASE_INSENSITIVE);
		Matcher CRONDmatcher = CRONDpattern.matcher(syslog);
		
        
		if (PRImatcher.find()) {
			String PRI = PRImatcher.group(0);
			PRI = PRI.substring(1, PRI.length()-1);
			this.pri = PRI;
			Integer PRIint = Integer.valueOf(PRI);
			this.event_level = PRIint%8;
			String facility = getFacility(PRIint/8);
			this.operation_facility = facility;
			//System.out.print("日志类型："+facility+"\t");
			String level = getLevel(PRIint%8);
			this.operation_level = level;
			//System.out.print("日志级别:"+level+"\t");
		} else if (NOPRImatcher.find()) {
			String PRI = NOPRImatcher.group(0);
			PRI = PRI.substring(1, PRI.length()-1);
			this.pri = PRI;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String logtimetmp = null;
		if(datematcher1.find()){
        	logtimetmp = datematcher1.group(0);
        	try {
				this.logdate = dateformat.parse(logtimetmp);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	this.logtime = datematcher1.group(0);
        	/*String [] tmp = this.logtime.split(" ");
        	String [] date = tmp[0].split("-");
        	String [] time = tmp[1].split(":");
        	this.logtime_year = date[0];
        	this.logtime_month = date[1];
        	this.logtime_day = date[2];
        	this.logtime_hour = time[0];
        	this.logtime_minute = time[1];*/
        // 该判断由于syslog的格式设置不会走到
        }else if(datematcher2.find()){
        	logtimetmp = datematcher2.group(0);
        	
        	SimpleDateFormat yearformat = new SimpleDateFormat("yyyy");
        	logtime = logtimetmp+" CST "+yearformat.format(new Date());
        	String DATE_FORMAT = "MMM ddHH:mm:ss zzz yyyy";
        	SimpleDateFormat formatter1 = new SimpleDateFormat(DATE_FORMAT,Locale.ENGLISH);
        	try {
				Date date = formatter1.parse(logtime);
				this.logtime = format.format(date);
				if (datematcher.find()) {
					String flumetime = datematcher.group(0);
					Date date2 = format.parse(flumetime);
					if (date.getTime()<=date2.getTime()) {
						this.logtime = logtime.replace(logtime.substring(logtime.length()-3, logtime.length()), flumetime.substring(flumetime.length()-3, flumetime.length()));
					}else{
						Calendar calendar = Calendar.getInstance();    
						calendar.setTime(date);    
						calendar.add(Calendar.YEAR, -1);
						date = calendar.getTime();
						this.logtime = format.format(date);
					}
					System.out.print("日志时间："+this.logtime+"\t");
					syslog = syslog.replace(flumetime, "");
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		
		if (!Strings.isNullOrEmpty(logtime)) {
			int a = syslog.indexOf(logtimetmp) + logtimetmp.length()+1;
			String tmp = syslog.substring(a, syslog.length());
			
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
				String des = syslog.substring(syslog.indexOf(logs[3]), syslog.length());
				this.operation_des = des;
			}else {
				this.operation_des = "";
			}
			
		}
		
		this.is_business = false;

		this.index_suffix = "syslog";
		
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
		if (sshFailedmatcher.find()) {
			this.event_type="ssh failed";
			this.event_des="ssh登录失败";
		}
		
		// 针对烟台烟草的防火墙日志收集、莱芜烟草的waf日志收集做定制化修改，syslog日志无PRI值，范式化之后无法获得日志级别、日志模块内容，为保证日志数据收集，添加默认级别info
		if(this.operation_level==null||this.operation_level.equals("")){
			this.operation_level = "info";
		}
		
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
		String fieldString =  getClassMapping(new Syslog());
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
	
	public static void main(String [] args) throws IOException {
		System.out.println(new Syslog().toMapping());
		
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
		//String ytyclog = "<invld> 2019-06-18 14:19:37 10.60.96.37 10.60.96.37 ????????-?? FLOW: SerialNum=0816101406139990 GenTime=\"2019-06-18 14:19:37\" SrcIP=10.60.108.94 DstIP=10.60.0.5 Protocol=UDP SrcPort=61650 DstPort=53 ProtoNum=17 AppProto=0 Starttime=\"2019-06-18 14:19:27\" Endtime=\"2019-06-18 14:19:37\" Packets=2 Bytes=584 VpnType=0 Content=\"\" EvtCount=1";
		String lwyclog = "<invld> 2019-07-22 16:03:03 10.60.145.24 10.60.145.24 waf1: DEVICE;loginip:;SerialNum=0312041408299998 GenTime=\"2019-07-22 16:03:03\" VsosCpuUsage=3 SECpuUsage=0 VSOSMemoryUsage=54.59 VSOSMEMTop3:1,guish -d  9916 2,guish -D  9092 3,vtysh -b  7716  SHMMemoryUsage=27.96 SessionNum=2 HalfSessionNum=0 ge0:RX_5_MIN=203TX_5_MIN=23 ge1:RX_5_MIN=23TX_5_MIN=203 Content=\"System running info.\"";
		String wfyclog = "<5> 2019-07-22 16:03:03 10.60.145.24 10.60.145.24 time: 2019-08-09 09:51:43 waf1: DEVICE;loginip:;SerialNum=0312041408299998 GenTime=\"2019-07-22 16:03:03\" VsosCpuUsage=3 SECpuUsage=0 VSOSMemoryUsage=54.59 VSOSMEMTop3:1,guish -d  9916 2,guish -D  9092 3,vtysh -b  7716  SHMMemoryUsage=27.96 SessionNum=2 HalfSessionNum=0 ge0:RX_5_MIN=203TX_5_MIN=23 ge1:RX_5_MIN=23TX_5_MIN=203 Content=\"System running info.\"";
		Syslog syslog = new Syslog(wfyclog);
		json = gson.toJson(syslog);
		System.out.println(json);*/
		
	}
}

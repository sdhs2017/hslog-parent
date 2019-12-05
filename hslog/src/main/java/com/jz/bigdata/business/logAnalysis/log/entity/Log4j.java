package com.jz.bigdata.business.logAnalysis.log.entity;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;


/**
 * 
 * @author jiyourui
 *  log4j日志字段模型
 */
public class Log4j {
	
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
	 * 操作类型
	 */
	String operation_level;
	/**
	 * ip地址
	 */
	String ip;
	/**
	 * 操作描述
	 */
	String operation_des;
	/**
	 * 日志类型
	 */
	private String hslog_type;

	/**
	 * 设置index名称的后缀
	 */
	private String index_suffix;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
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
	/**
	 * @return the logtime
	 */
	public String getLogtime() {
		return logtime;
	}
	/**
	 * @param logtime the logtime to set
	 */
	public void setLogtime(String logtime) {
		this.logtime = logtime;
	}
	/**
	 * @return the operation_level
	 */
	public String getOperation_level() {
		return operation_level;
	}
	/**
	 * @param operation_level the operation_level to set
	 */
	public void setOperation_level(String operation_level) {
		this.operation_level = operation_level;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	/**
	 * @return the operation_des
	 */
	public String getOperation_des() {
		return operation_des;
	}
	/**
	 * @param operation_des the operation_des to set
	 */
	public void setOperation_des(String operation_des) {
		this.operation_des = operation_des;
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

	public Log4j(){
		
	}
	
	
	/**
	 * 初始化	String2bean  针对以syslog形式发送过来的log4j日志
	 * @param log
	 * @throws ParseException 
	 */
	public Log4j(String log) throws ParseException {
		
		Pattern facility_pattern = Pattern.compile("local3:");
		Matcher facility_matcher = facility_pattern.matcher(log);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        if (facility_matcher.find()) {
			String logleft = log.substring(0, log.indexOf(facility_matcher.group(0))+facility_matcher.group(0).length());
			System.out.println(logleft);
			String logright = log.substring(log.indexOf(facility_matcher.group(0))+facility_matcher.group(0).length());
			System.out.println(logright);
			
			// 截取日志生产ip
	        //Pattern ipPattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)\\s");  
			Pattern ipPattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)");  
	        Matcher ipmatcher = ipPattern.matcher(logleft);
			// 截取时间
			Pattern datePattern = Pattern.compile("[0-9]{4}[-][0-9]{1,2}[-][0-9]{1,2}[ ][0-9]{1,2}[:][0-9]{1,2}[:][0-9]{1,2}[,][0-9]{2,3}");  
	        Matcher datematcher = datePattern.matcher(logright);
	        // 截取日志级别
	        Pattern logLevelpattern = Pattern.compile("debug|info|error|warn",Pattern.CASE_INSENSITIVE);
	        Matcher logLevelmatcher = logLevelpattern.matcher(logright);
	        
	        
	        
	        if(datematcher.find()){ 
	        	//SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,S");
	        	datematcher.group(0).replaceAll("/,", "/.");
	        	System.out.println(datematcher.group(0).replaceAll(",", "."));
	        	// 获取 时间
				this.logtime = datematcher.group(0).replaceAll(",", ".");
				this.logdate = format.parse(this.logtime);
				/*String [] tmp = this.logtime.split(" ");
	        	String [] date = tmp[0].split("-");
	        	String [] time = tmp[1].split(":");
	        	this.logtime_year = date[0];
	        	this.logtime_month = date[1];
	        	this.logtime_day = date[2];
	        	this.logtime_hour = time[0];
	        	this.logtime_minute = time[1];*/
	        }
	        if (logLevelmatcher.find()) {
	        	// 获取 日志级别
	        	this.operation_level = logLevelmatcher.group(0);
			}
	        if (ipmatcher.find()) {
	        	// 获取 日志地址
				this.ip = ipmatcher.group(0);
			}
	        
	        /*if(ip!=null) {
	        	int c = log.indexOf(ip)+ip.length();
	            // 获取日志操作
	            this.operation_des = log.substring(c, log.length());
	        }else*/ if (operation_level!=null){
	        	int c = log.indexOf(operation_level)+operation_level.length()+1;
	            // 获取日志操作
	            this.operation_des = log.substring(c, log.length());
			}else {
				this.operation_des = log;
			}
			this.index_suffix = "syslog";
		}
        
	}
	
	/**
	 * 初始化	String2bean  针对发送过来的日志是json格式的
	 * @param log
	 * @throws ParseException 
	 */
	public Log4j(String log,Calendar cal) {
		Gson gson = new Gson();
		Log4jjson log4jjson = gson.fromJson(log, Log4jjson.class);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		this.ip = log4jjson.getIp();
		// 时间处理
		this.logdate = new Date(log4jjson.getTimestamp());
		cal.setTimeInMillis(log4jjson.getTimestamp());
		this.logtime = format.format(cal.getTime());
		/*this.logtime_year = String.valueOf(cal.get(Calendar.YEAR));
		this.logtime_month = String.format("%02d",cal.get(Calendar.MONTH)+1);
		this.logtime_day = String.format("%02d",cal.get(Calendar.DAY_OF_MONTH));
		this.logtime_hour = String.format("%02d",cal.get(Calendar.HOUR_OF_DAY));
		this.logtime_minute = String.format("%02d",cal.get(Calendar.MINUTE));*/
		
		this.operation_level = log4jjson.getPriority();
		if (log4jjson.getStack_trace()!=null) {
			this.operation_des = this.logtime+" ["+operation_level+"] "+log4jjson.getLogger_name()+" "+log4jjson.getMessage()+" "+log4jjson.getStack_trace();
		}else {
			this.operation_des = this.logtime+" ["+operation_level+"] "+log4jjson.getLogger_name()+" "+log4jjson.getMessage();
		}
		this.index_suffix = "syslog";
		
	}
	
	public String toMapping() {
		 String template = "{\n" 
                   +"\t\t\"properties\":{\n"
                           + "\t\t{#}\n" 
                   + "\t\t\t\t}"
               +"}";
		String fieldString =  getClassMapping(new Log4j());
		template = template.replace("{#}",fieldString);
		return template;
	}

	public <T> String getClassMapping(T classes) {
		
		StringBuilder fieldstring = new StringBuilder();
		
        Field[] fields = classes.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
             fieldstring.append("\t\t\t\t\"" + fields[i].getName().toLowerCase() + "\": {\n");
             fieldstring.append("\t\t\t\t\t\t\"type\": \""
                        + GetElasticSearchMappingType(fields[i].getType().getSimpleName(),fields[i].getName()) + "\n");
             if (fields[i].getName().equals("id")) {
            	 fieldstring.append("\t\t\t\t\t\t,\"index\": \""
                         + "false\"" + "\n");
			}
             if (!fields[i].getName().equals("operation_des")&&!fields[i].getName().equals("id")&&!fields[i].getName().equals("logdate")) {
				fieldstring.append("\t\t\t\t\t\t,\"fielddata\": "
                        + "true" + "\n");
			}
             if (fields[i].getName().equals("operation_des")||fields[i].getName().equals("ip")||fields[i].getName().equals("equipmentname")) {
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

	private static String GetElasticSearchMappingType(String varType,String name) {
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
	
	class Log4jjson{
		
		String version;
		String file;
		String ip;
		String logger_name;
		String message;
		String method;
		String path;
		String priority;
		String stack_trace;
		long timestamp;
		String type;
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
		public String getFile() {
			return file;
		}
		public void setFile(String file) {
			this.file = file;
		}
		public String getIp() {
			return ip;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public String getLogger_name() {
			return logger_name;
		}
		public void setLogger_name(String logger_name) {
			this.logger_name = logger_name;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public String getMethod() {
			return method;
		}
		public void setMethod(String method) {
			this.method = method;
		}
		public String getPath() {
			return path;
		}
		public void setPath(String path) {
			this.path = path;
		}
		public String getPriority() {
			return priority;
		}
		public void setPriority(String priority) {
			this.priority = priority;
		}
		public String getStack_trace() {
			return stack_trace;
		}
		public void setStack_trace(String stack_trace) {
			this.stack_trace = stack_trace;
		}
		public long getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		
		
	}
	
	public static void main(String [] args) {
		System.out.println(new Log4j().toMapping());
		
		/*String log = "<155> 2017-11-23 16:04:34 192.168.4.148 192.168.4.148 local3: 2017-11-23 16:03:30,442 [ERROR]  [        at com.jz.log.test.Log4jTest.main(Log4jTest.java:37)]  [java.lang.Class] main()[32] [384576] ";
		Log4j log4j = new Log4j(log);*/
		
	}
}


package com.jz.bigdata.business.logAnalysis.log.entity;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

public class Mysql {
	
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
	 * 日志来源
	 */
	String logsource;
	/**
	 * 日志产生时间
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
	 * 日志级别
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

	public String getLogsource() {
		return logsource;
	}

	public void setLogsource(String logsource) {
		this.logsource = logsource;
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

	public Mysql() {
	}
	
	public Mysql(String log) {
		// TODO Auto-generated constructor stub
		log = log.replace("@", "");
		
		Pattern datePattern = Pattern.compile("[0-9]{1,6}[ ][0-9]{1,2}[:][0-9]{1,2}[:][0-9]{1,2}");
		
		
		Gson gson = new Gson();
		
		Mysqljson mysqljson = gson.fromJson(log, Mysqljson.class);
		
		this.logsource = mysqljson.getSource();
		
		this.hostname = mysqljson.getBeat().getHostname();
		
		this.operation_level = mysqljson.getLevel();
		
		this.ip = mysqljson.getIp();
		
		this.operation_des = mysqljson.getMessage();
		
		Matcher datematcher = datePattern.matcher(this.operation_des);
		if (datematcher.find()) {
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = "20"+datematcher.group(0);
			String [] tmp = date.split(" ");
        	this.logtime_year = tmp[0].substring(0, 4);
        	this.logtime_month = tmp[0].substring(4, 6);
        	this.logtime_day = tmp[0].substring(6, 8);
        	String [] time = tmp[1].split(":");
        	this.logtime_hour = time[0];
        	this.logtime_minute = time[1];
        	this.logtime = this.logtime_year+"-"+this.logtime_month+"-"+this.logtime_day+" "+tmp[1];
        	
        	try {
				this.logdate = dateformat.parse(this.logtime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dd = mysqljson.getTimestamp().replaceAll("T", " ").replace("Z", "");
			String [] ddd = dd.split("[.]");
			this.logtime = ddd[0];
			try {
				this.logdate = dateformat.parse(this.logtime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String [] tmp = this.logtime.split(" ");
        	String [] date = tmp[0].split("-");
        	String [] time = tmp[1].split(":");
        	this.logtime_year = date[0];
        	this.logtime_month = date[1];
        	this.logtime_day = date[2];
        	this.logtime_hour = time[0];
        	this.logtime_minute = time[1];
		}
	}
	
	class Mysqljson {
		
		private String timestamp;
		
		private Beat beat;
		
		public class Beat{
			String hostname;
			String name;
			String version;
			
			public String getHostname() {
				return hostname;
			}
			public void setHostname(String hostname) {
				this.hostname = hostname;
			}
			public String getName() {
				return name;
			}
			public void setName(String name) {
				this.name = name;
			}
			public String getVersion() {
				return version;
			}
			public void setVersion(String version) {
				this.version = version;
			}
			
		}
		
		private String level;
		
		private String ip;
		
		private String input_type;
		
		private String message;
		
		private String offset;
		
		private String source;
		
		private String type;

		public String getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(String timestamp) {
			this.timestamp = timestamp;
		}

		public Beat getBeat() {
			return beat;
		}

		public void setBeat(Beat beat) {
			this.beat = beat;
		}

		public String getLevel() {
			return level;
		}

		public void setLevel(String level) {
			this.level = level;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public String getInput_type() {
			return input_type;
		}

		public void setInput_type(String input_type) {
			this.input_type = input_type;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getOffset() {
			return offset;
		}

		public void setOffset(String offset) {
			this.offset = offset;
		}

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
		
	}
	
	public String toMapping() {
		 String template = "{\n" 
                 +"\t\t\"properties\":{\n"
                         + "\t\t{#}\n" 
                 + "\t\t\t\t}"
             +"}";
		String fieldString =  getClassMapping(new Mysql());
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
	
	public static void main(String [] args) {
		Mysql mysql = new Mysql();
		System.out.println(mysql.toMapping());
	}

}

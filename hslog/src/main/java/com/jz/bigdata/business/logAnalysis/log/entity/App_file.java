package com.jz.bigdata.business.logAnalysis.log.entity;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

public class App_file {

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
	private String logtime_minute;
	private String logtime_hour;
	private String logtime_day;
	private String logtime_month;
	private String logtime_year;
	private String timestamp;
	/**
	 * 操作类型
	 */
	private String operation_level;
	/**
	 * ip地址
	 */
	private String ip;
	/**
	 * 操作描述
	 */
	private String operation_des;

	private String host;
	private String source;
	private String message;
	private Beat beat;
	private Fields fields;
	private String hostname;
	private String name;

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

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getOperation_level() {
		return operation_level;
	}

	public void setOperation_level(String operation_level) {
		this.operation_level = operation_level;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getOperation_des() {
		return operation_des;
	}

	public void setOperation_des(String operation_des) {
		this.operation_des = operation_des;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Beat getBeat() {
		return beat;
	}

	public void setBeat(Beat beat) {
		this.beat = beat;
	}

	public Fields getFields() {
		return fields;
	}

	public void setFields(Fields fields) {
		this.fields = fields;
	}

	
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


	class Beat {
		private String hostname;
		private String name;

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
	}

	class Fields {
		private String ip;
		private String logtype;
		private String level;

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public String getLogtype() {
			return logtype;
		}

		public void setLogtype(String logtype) {
			this.logtype = logtype;
		}

		public String getLevel() {
			return level;
		}

		public void setLevel(String level) {
			this.level = level;
		}

	}

	public App_file() {}

	public App_file(String log) {

		Gson gson = new Gson();
		log = log.replace("@", "");
		App_file app_file = gson.fromJson(log, App_file.class);
	

		if (app_file.getFields()!=null) {
			this.ip = (app_file.getFields().getIp()!=null?app_file.getFields().getIp():"");
			this.operation_level = (app_file.getFields().getLevel()!=null?app_file.getFields().getLevel():"debug");
		}
		
		this.hostname=app_file.getBeat().getHostname();
		this.name=app_file.getBeat().getName();
		this.host=app_file.getHost();
		this.source=app_file.getSource();

		Pattern date_pattern = Pattern
				.compile("[0-9]{4}[/][0-9]{1,2}[/][0-9]{1,2}[ ][0-9]{1,2}[:][0-9]{1,2}[:][0-9]{1,2}");
		Matcher date_matcher = date_pattern.matcher(app_file.getMessage());

		// 判断内容中是否包含时间
		if (date_matcher.find()) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			// 获取 时间
			this.logtime = date_matcher.group(0).replaceAll(",", ".");
			try {
				this.logdate = format.parse(this.logtime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] tmp = this.logtime.split(" ");
			String[] date = tmp[0].split("/");
			String[] time = tmp[1].split(":");
			this.logtime_year = date[0];
			this.logtime_month = date[1];
			this.logtime_day = date[2];
			this.logtime_hour = time[0];
			this.logtime_minute = time[1];
			System.out.println(logdate + "   " + logtime);
			System.out.println(date.length + "   " + logtime_year + "   " + logtime_month + "    " + logtime_day
					+ "    " + logtime_hour + "   " + logtime_minute);
			System.out.println(app_file.getMessage().indexOf(logtime));
			this.operation_des = app_file.getMessage();
			System.out.println(operation_des);
		}else {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			// 获取 时间
			this.logtime = app_file.getTimestamp().replaceAll("Z", "").replaceAll("T", " ");
			System.out.println(logtime);
			try {
				this.logdate = format.parse(this.logtime);
				long tmptime = this.logdate.getTime()+(8*60*60*1000);
				this.logdate = new Date(tmptime);
				this.logtime = format.format(logdate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] tmp = this.logtime.split(" ");
			String[] date = tmp[0].split("-");
			String[] time = tmp[1].split(":");
			this.logtime_year = date[0];
			this.logtime_month = date[1];
			this.logtime_day = date[2];
			this.logtime_hour = time[0];
			this.logtime_minute = time[1];
			System.out.println(logtime);
			this.operation_des = app_file.getMessage();
		}

	}

	public String toMapping() {
		String template = "{\n" + "\t\t\"properties\":{\n" + "\t\t{#}\n" + "\t\t\t\t}" + "}";
		String fieldString = getClassMapping(new App_file());
		template = template.replace("{#}", fieldString);
		return template;
	}

	public <T> String getClassMapping(T classes) {

		StringBuilder fieldstring = new StringBuilder();

		Field[] fields = classes.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			fieldstring.append("\t\t\t\t\"" + fields[i].getName().toLowerCase() + "\": {\n");
			fieldstring.append("\t\t\t\t\t\t\"type\": \""
					+ GetElasticSearchMappingType(fields[i].getType().getSimpleName(), fields[i].getName()) + "\n");
			if (fields[i].getName().equals("id")) {
				fieldstring.append("\t\t\t\t\t\t,\"index\": \"" + "false\"" + "\n");
			}
			if (fields[i].getName().equals("userid") || fields[i].getName().equals("deptid")
					|| fields[i].getName().equals("equipmentid") || fields[i].getName().equals("equipmentname")
					|| fields[i].getName().equals("logtime") || fields[i].getName().equals("logtime_minute")
					|| fields[i].getName().equals("logtime_hour") || fields[i].getName().equals("logtime_day")
					|| fields[i].getName().equals("logtime_month") || fields[i].getName().equals("logtime_year")
					|| fields[i].getName().equals("operation_level") || fields[i].getName().equals("ip")
					|| fields[i].getName().equals("host")|| fields[i].getName().equals("hostname")) {
				fieldstring.append("\t\t\t\t\t\t,\"fielddata\": " + "true" + "\n");
			}
			if (fields[i].getName().equals("operation_des") || fields[i].getName().equals("equipmentname")
					|| fields[i].getName().equals("host") || fields[i].getName().equals("hostname")
					|| fields[i].getName().equals("name")|| fields[i].getName().equals("ip")) {
				fieldstring.append("\t\t\t\t\t\t,\"analyzer\": \"" + "index_ansj\"" + "\n");
				fieldstring.append("\t\t\t\t\t\t,\"search_analyzer\": \"" + "query_ansj\"" + "\n");
			}
			if (i == fields.length - 1) {
				fieldstring.append("\t\t\t\t\t}\n");
			} else {
				fieldstring.append("\t\t\t\t\t},\n");
			}
		}
		return fieldstring.toString();
	}

	private static String GetElasticSearchMappingType(String varType, String name) {
		String es = "text";
		switch (varType) {
		case "Date":
			es = "date\"\n" + "\t\t\t\t\t\t,\"format\":\"yyyy-MM-dd HH:mm:ss\"\n" + "\t\t\t\t\t\t";
			break;
		case "Double":
			es = "double\"\n" + "\t\t\t\t\t\t,\"null_value\":\"NaN\"";
			break;
		case "Long":
			es = "long\"";
			break;
		default:
			if (name.equals("id")) {
				es = "keyword\"";
			} else {
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

	public static void main(String[] args) {
		// String log="2018/1/28 19:29:54:
		// http://10.29.172.70:8080/ZTExplorer/server/reportServer,
		// jsonServer={\"id\":null,\"name\":\"Windows_822069551\",\"mac\":\"7f5632d1-aecd-4b60-afa2-4ec8fb779410\",\"vdiId\":\"7f5632d1-aecd-4b60-afa2-4ec8fb779410\",\"enable\":false,\"comment\":null,\"ip\":\"10.29.173.1\",\"port\":3389,\"userTag\":null,\"state\":0,\"loadInfo\":null,\"groupId\":null}\r\n"
		// +
		// "";
//			String log = "{\"@timestamp\":\"2018-08-28T03:00:43.867Z\",\"offset\":788863,\"@version\":\"1\",\"beat\":{\"hostname\":\"ZTS-AQ-USER2\",\"name\":\"ZTS-AQ-USER2\",\"version\":\"5.6.11\"},\"input_type\":\"log\",\"host\":\"ZTS-AQ-USER2\",\"source\":\"d:\\\\logs\\\\log1.txt\",\"message\":\"2018/8/24 14:55:17: ===OnStart\",\"type\":\"log\",\"fields\":{\"ip\":\"10.29.6.188\"},\"tags\":[\"beats_input_codec_plain_applied\"]}";
		String log ="{\"@timestamp\":\"2018-09-05T06:16:15.680Z\",\"host\":\"aaa-PC\",\"@version\":\"1\",\"offset\":48593,\"fields\":{\"ip\":\"10.29.6.188\"},\"beat\":{\"hostname\":\"aaa-PC\",\"name\":\"aaa-PC\",\"version\":\5.6.11\"},\"input_type\":\"log\",\"type\":\"log\",\"tags\":[\"beats_input_codec_plain_applied\"],\"message\":\" -----------Timer_Elapsed\",\"source\":\"F:\\\\filebeat\\\\filebeat-5.6.11-windows-x86_64\\\\filebeat-5.6.11-windows-x86_64\\\\log.txt\"}";
		
		//String log = "{\"@timestamp\":\"2018-09-05T06:16:15.680Z\"}";
		
		//filebeat
		/*Pattern filebeatpattern = Pattern.compile("\"logtype\":\"(.*?)\"");
		Matcher filebeatmatcher = filebeatpattern.matcher(log);
		if (filebeatmatcher.find()) {
			System.out.println();
		}*/
		/*String logtype = getSubUtilSimple(log,"\"logtype\":\"(.*?)\"");
		System.out.println(logtype);*/
		/*App_file app_file = new App_file(log);
		System.out.println(app_file.getIp());
		System.out.println(app_file.getHost());
		System.out.println(app_file.getHostname());
		System.out.println(app_file.getSource());
		System.out.println(app_file.getIp());
		System.out.println(app_file.getOperation_des());*/
		System.out.println(new App_file().toMapping());
	}
}
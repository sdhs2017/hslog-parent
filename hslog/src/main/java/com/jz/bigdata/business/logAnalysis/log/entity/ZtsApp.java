package com.jz.bigdata.business.logAnalysis.log.entity;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.Gson;

public class ZtsApp {

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
		private String type;

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		
	}

	public ZtsApp() {

	}

	public ZtsApp(String log) {

		System.out.println(log);
		Gson gson = new Gson();
//		log =log.replaceAll("\", "\\\\");
		ZtsApp ztsApp = gson.fromJson(log, ZtsApp.class);
	

		this.ip = ztsApp.getFields().getIp();
		this.hostname=ztsApp.getBeat().getHostname();
		this.name=ztsApp.getBeat().getName();
		this.host=ztsApp.getHost();
		this.source=ztsApp.getSource();
		System.err.println(ztsApp.getHost());

		Pattern date_pattern = Pattern
				.compile("[0-9]{4}[/][0-9]{1,2}[/][0-9]{1,2}[ ][0-9]{1,2}[:][0-9]{1,2}[:][0-9]{1,2}");
		Matcher date_matcher = date_pattern.matcher(ztsApp.getMessage());

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
			System.out.println(ztsApp.getMessage().indexOf(logtime));
			this.operation_des = ztsApp.getMessage();
			System.out.println(operation_des);
		}

	}

	public String toMapping() {
		String template = "{\n" + "\t\t\"properties\":{\n" + "\t\t{#}\n" + "\t\t\t\t}" + "}";
		String fieldString = getClassMapping(new ZtsApp());
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

	public static void main(String[] args) {
		// String log="2018/1/28 19:29:54:
		// http://10.29.172.70:8080/ZTExplorer/server/reportServer,
		// jsonServer={\"id\":null,\"name\":\"Windows_822069551\",\"mac\":\"7f5632d1-aecd-4b60-afa2-4ec8fb779410\",\"vdiId\":\"7f5632d1-aecd-4b60-afa2-4ec8fb779410\",\"enable\":false,\"comment\":null,\"ip\":\"10.29.173.1\",\"port\":3389,\"userTag\":null,\"state\":0,\"loadInfo\":null,\"groupId\":null}\r\n"
		// +
		// "";
//		String log = "{\"@timestamp\":\"2018-08-28T03:00:43.867Z\",\"offset\":788863,\"@version\":\"1\",\"beat\":{\"hostname\":\"ZTS-AQ-USER2\",\"name\":\"ZTS-AQ-USER2\",\"version\":\"5.6.11\"},\"input_type\":\"log\",\"host\":\"ZTS-AQ-USER2\",\"source\":\"d:\\\\logs\\\\log1.txt\",\"message\":\"2018/8/24 14:55:17: ===OnStart\",\"type\":\"log\",\"fields\":{\"ip\":\"10.29.6.188\"},\"tags\":[\"beats_input_codec_plain_applied\"]}";
		String log ="{\"@timestamp\":\"2018-09-05T06:16:15.680Z\",\"host\":\"aaa-PC\",\"@version\":\"1\",\"offset\":48593,\"fields\":{\"ip\":\"10.29.6.188\"},\"beat\":{\"hostname\":\"aaa-PC\",\"name\":\"aaa-PC\",\"version\":\5.6.11\"},\"input_type\":\"log\",\"type\":\"log\",\"tags\":[\"beats_input_codec_plain_applied\"],\"message\":\"2018/3/23 10:38:24: -----------Timer_Elapsed\",\"source\":\"F:\\\\filebeat\\\\filebeat-5.6.11-windows-x86_64\\\\filebeat-5.6.11-windows-x86_64\\\\log.txt\"}";
		ZtsApp ztsApp = new ZtsApp(log);
		System.err.println(ztsApp.getOperation_des());
		System.out.println(ztsApp.getIp());
		System.out.println(ztsApp.getHost());
		System.out.println(ztsApp.getHostname());
		System.out.println(ztsApp.getSource());
		System.out.println(ztsApp.getIp());
		System.out.println(ztsApp.getOperation_des());
		// System.out.println(ztsApp.toMapping());
	}
}

package com.jz.bigdata.business.logAnalysis.log.entity;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.google.gson.Gson;

public class ZtsLog4j {

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

	// 业务数据
	private String _links;
	private String chgPassFlag;
	private String datasource;
	private String email;
	private String empStatus;
	private String employeeNumber;
	private String gender;
	private String jobCode;
	private String jobName;
	private String jobStatus;
	private String name;
	private String orgs;
	private String partOrgs;
	private String phoneNo;
	private String properties;
	private String status;
	private String type;
	private String userName;
	private String workPlace;
	private String officePhone;
	private String jobGroupCode;
	private String jobGroupName;
	private String phoneShortNo;
	private String officeShortNo;
	private String userMaps;
	private String uid;
	private String user;

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

	public String get_links() {
		return _links;
	}

	public void set_links(String _links) {
		this._links = _links;
	}

	public String getChgPassFlag() {
		return chgPassFlag;
	}

	public void setChgPassFlag(String chgPassFlag) {
		this.chgPassFlag = chgPassFlag;
	}

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmpStatus() {
		return empStatus;
	}

	public void setEmpStatus(String empStatus) {
		this.empStatus = empStatus;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getJobCode() {
		return jobCode;
	}

	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrgs() {
		return orgs;
	}

	public void setOrgs(String orgs) {
		this.orgs = orgs;
	}

	public String getPartOrgs() {
		return partOrgs;
	}

	public void setPartOrgs(String partOrgs) {
		this.partOrgs = partOrgs;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(String workPlace) {
		this.workPlace = workPlace;
	}

	public String getOfficePhone() {
		return officePhone;
	}

	public void setOfficePhone(String officePhone) {
		this.officePhone = officePhone;
	}

	public String getJobGroupCode() {
		return jobGroupCode;
	}

	public void setJobGroupCode(String jobGroupCode) {
		this.jobGroupCode = jobGroupCode;
	}

	public String getJobGroupName() {
		return jobGroupName;
	}

	public void setJobGroupName(String jobGroupName) {
		this.jobGroupName = jobGroupName;
	}

	public String getPhoneShortNo() {
		return phoneShortNo;
	}

	public void setPhoneShortNo(String phoneShortNo) {
		this.phoneShortNo = phoneShortNo;
	}

	public String getOfficeShortNo() {
		return officeShortNo;
	}

	public void setOfficeShortNo(String officeShortNo) {
		this.officeShortNo = officeShortNo;
	}

	public String getUserMaps() {
		return userMaps;
	}

	public void setUserMaps(String userMaps) {
		this.userMaps = userMaps;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public ZtsLog4j() {
	}

	/**
	 * 初始化 String2bean
	 * 
	 * @param log
	 * @throws ParseException
	 */
	public ZtsLog4j(String log, Calendar cal) {

		if (log.indexOf("userName") > 0 && log.indexOf("userMaps") > 0) {
			
			
			Gson gson = new Gson();
			//log = gson.toJson(log);
//			log = log.replace("\"", "\\\"");
			Log4jjson log4jjson = gson.fromJson(log,Log4jjson.class);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			
			this.ip = log4jjson.getIp();
			// 时间处理
			this.logdate = new Date(log4jjson.getTimestamp());
			cal.setTimeInMillis(log4jjson.getTimestamp());
			this.logtime = format.format(cal.getTime());
			this.logtime_year = String.valueOf(cal.get(Calendar.YEAR));
			this.logtime_month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
			this.logtime_day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
			this.logtime_hour = String.format("%02d", cal.get(Calendar.HOUR_OF_DAY));
			this.logtime_minute = String.format("%02d", cal.get(Calendar.MINUTE));

			ZtsUser ztcSyslog = new ZtsUser();
			
			String teString=(String) log4jjson.getMessage();

			ztcSyslog = ztcSyslog.ZtsUsers(teString);
			this._links = String.valueOf(gson.toJson(ztcSyslog.get_links()));
			this.chgPassFlag = ztcSyslog.getChgPassFlag();
			this.datasource = ztcSyslog.getDatasource();
			this.email = ztcSyslog.getEmail();
			this.empStatus = ztcSyslog.getEmpStatus();
			this.employeeNumber = ztcSyslog.getEmployeeNumber();
			this.gender = ztcSyslog.getGender();
			this.jobCode = String.valueOf(ztcSyslog.getJobCode());
			this.jobName = String.valueOf(ztcSyslog.getJobName());
			this.jobStatus = ztcSyslog.getJobStatus();
			this.name = ztcSyslog.getName();
			this.orgs = ztcSyslog.getOrgs();
			this.phoneNo = ztcSyslog.getPhoneNo();
			this.properties = String.valueOf(gson.toJson(ztcSyslog.getProperties()));
			this.status = ztcSyslog.getStatus();
			this.type = ztcSyslog.getType();
			this.userName = ztcSyslog.getUserName();
			this.workPlace = ztcSyslog.getWorkPlace();
			this.officePhone = ztcSyslog.getOfficePhone();
			this.jobGroupCode = ztcSyslog.getJobGroupCode();

			this.jobGroupName = ztcSyslog.getJobGroupName();
			this.phoneShortNo = ztcSyslog.getPhoneShortNo();
			this.officeShortNo = ztcSyslog.getOfficeShortNo();
			this.userMaps = String.valueOf(gson.toJson(ztcSyslog.getUserMaps()));
			this.uid = ztcSyslog.getUid();
			this.user=ztcSyslog.getName();

			this.operation_level = log4jjson.getPriority();
			if (log4jjson.getStack_trace() != null) {
				this.operation_des = this.logtime + " [" + operation_level + "] " + log4jjson.getLogger_name() + " "
						+ log4jjson.getMessage() + " " + log4jjson.getStack_trace();
			} else {
				this.operation_des = this.logtime + " [" + operation_level + "] " + log4jjson.getLogger_name() + " "
						+ log4jjson.getMessage();
			}
		}else{
			
			Gson gson = new Gson();
			Log4jjson2 log4jjson2 = gson.fromJson(log, Log4jjson2.class);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			this.ip = log4jjson2.getIp();
			// 时间处理
			this.logdate = new Date(log4jjson2.getTimestamp());
			cal.setTimeInMillis(log4jjson2.getTimestamp());
			this.logtime = format.format(cal.getTime());
			this.logtime_year = String.valueOf(cal.get(Calendar.YEAR));
			this.logtime_month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
			this.logtime_day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
			this.logtime_hour = String.format("%02d", cal.get(Calendar.HOUR_OF_DAY));
			this.logtime_minute = String.format("%02d", cal.get(Calendar.MINUTE));
			
			this.operation_level = log4jjson2.getPriority();
			if (log4jjson2.getStack_trace() != null) {
				this.operation_des = this.logtime + " [" + operation_level + "] " + log4jjson2.getLogger_name() + " "
						+ log4jjson2.getMessage() + " " + log4jjson2.getStack_trace();
			} else {
				this.operation_des = this.logtime + " [" + operation_level + "] " + log4jjson2.getLogger_name() + " "
						+ log4jjson2.getMessage();
			}
		}

	}

	class Log4jjson {

		String version;
		String file;
		String ip;
		String logger_name;
		// String message;
		String method;
		String path;
		String priority;
		String stack_trace;
		long timestamp;
		String type;
		private Object message;

		// private String message_
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

		public Object getMessage() {
			return message;
		}

		public void setMessage(Object message) {
			this.message = message;
		}

	}

	class Log4jjson2 {

		String version;
		String file;
		String ip;
		String logger_name;
		// String message;
		String method;
		String path;
		String priority;
		String stack_trace;
		long timestamp;
		String type;
		private String message;

		// private String message_
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

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}

	public String toMapping() {
		String template = "{\n" + "\t\t\"properties\":{\n" + "\t\t{#}\n" + "\t\t\t\t}" + "}";
		String fieldString = getClassMapping(new ZtsLog4j());
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
					|| fields[i].getName().equals("phoneNo") || fields[i].getName().equals("jobCode")
					|| fields[i].getName().equals("jobName")|| fields[i].getName().equals("user")) {
				fieldstring.append("\t\t\t\t\t\t,\"fielddata\": " + "true" + "\n");
			}
			if (fields[i].getName().equals("operation_des") || fields[i].getName().equals("ip")
					|| fields[i].getName().equals("equipmentname") || fields[i].getName().equals("userMaps")
					|| fields[i].getName().equals("properties") || fields[i].getName().equals("name")
					|| fields[i].getName().equals("userName") || fields[i].getName().equals("phoneNo")
					|| fields[i].getName().equals("jobName") || fields[i].getName().equals("jobCode")
					|| fields[i].getName().equals("orgs") || fields[i].getName().equals("email")
					|| fields[i].getName().equals("employeeNumber") || fields[i].getName().equals("datasource")) {
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
		Calendar cal = Calendar.getInstance();
//		String log = "{\"method\":\"getJSON\",\"ip\":\"10.29.172.28\",\"thread\":\"ForkJoinPool-1-worker-0\",\"message\":{\"userName\":\"wangyx03\",\"name\":\"王怡雪\",\"email\":\"wangyx03@zts.com.cn\",\"phoneNo\":\"13954302163\",\"officePhone\":null,\"gender\":\"2\",\"status\":\"1\",\"chgPassFlag\":\"1\",\"orgs\":\"D3774:25\",\"employeeNumber\":\"91F1823868\",\"userMaps\":[{\"id\":90250,\"userId\":17259,\"convertId\":\"91F1823868\",\"appCode\":\"M0001\",\"status\":\"1\",\"employeeNumber\":\"91F1823868\"},{\"id\":91126,\"userId\":17259,\"convertId\":\"wangyx03\",\"appCode\":\"M0025\",\"status\":\"1\",\"employeeNumber\":\"91F1823868\"},{\"id\":91127,\"userId\":17259,\"convertId\":\"wangyx03\",\"appCode\":\"M0028\",\"status\":\"1\",\"employeeNumber\":\"91F1823868\"}],\"type\":1,\"jobCode\":\"J14146\",\"jobName\":\"项目承揽承做岗\",\"jobStatus\":\"2\",\"jobGroupCode\":null,\"jobGroupName\":null,\"empStatus\":\"1\",\"workPlace\":\"北京\",\"phoneShortNo\":null,\"officeShortNo\":null,\"datasource\":\"HR\",\"partOrgs\":[],\"properties\":[{\"id\":339094,\"userId\":17259,\"userKey\":\"emptype\",\"userValue\":\"1\",\"employeeNumber\":\"91F1823868\"},{\"id\":339096,\"userId\":17259,\"userKey\":\"contract\",\"userValue\":\"是\",\"employeeNumber\":\"91F1823868\"},{\"id\":339092,\"userId\":17259,\"userKey\":\"eid\",\"userValue\":\"19804\",\"employeeNumber\":\"91F1823868\"}],\"_links\":{\"self\":{\"href\":\"http://10.29.181.202:30080/v1/users/17259\"}},\"id\":17259}\",\"priority\":\"DEBUG\",\"type\":\"log4j\",\"tags\":[],\"path\":\"com.foperate.oidc.op.util.RestClientBase\",\"@timestamp\":\"2018-08-23T02:24:48.663Z\",\"file\":\"RestClientBase.java:108\",\"@version\":\"1\",\"host\":\"10.29.172.28:38980\",\"logger_name\":\"com.foperate.oidc.op.util.RestClientBase\",\"class\":\"com.foperate.oidc.op.util.RestClientBase\",\"timestamp\":1534991087323}";
		
		 String log="{\"method\":\"getJSON\",\"ip\":\"10.29.172.28\",\"thread\":\"ForkJoinPool-1-worker-0\",\"message\":11223344,\"priority\":\"DEBUG\",\"type\":\"log4j\",\"tags\":[],\"path\":\"com.foperate.oidc.op.util.RestClientBase\",\"@timestamp\":\"2018-08-23T02:24:48.663Z\",\"file\":\"RestClientBase.java:108\",\"@version\":\"1\",\"host\":\"10.29.172.28:38980\",\"logger_name\":\"com.foperate.oidc.op.util.RestClientBase\",\"class\":\"com.foperate.oidc.op.util.RestClientBase\",\"timestamp\":1534991087323}";
		
		log =log.replace("11223344", "{\"userName\":\"wangyx03\",\"name\":\"王怡雪\",\"email\":\"wangyx03@zts.com.cn\",\"phoneNo\":\"13954302163\",\"officePhone\":null,\"gender\":\"2\",\"status\":\"1\",\"chgPassFlag\":\"1\",\"orgs\":\"D3774:25\",\"employeeNumber\":\"91F1823868\",\"userMaps\":[{\"id\":90250,\"userId\":17259,\"convertId\":\"91F1823868\",\"appCode\":\"M0001\",\"status\":\"1\",\"employeeNumber\":\"91F1823868\"},{\"id\":91126,\"userId\":17259,\"convertId\":\"wangyx03\",\"appCode\":\"M0025\",\"status\":\"1\",\"employeeNumber\":\"91F1823868\"},{\"id\":91127,\"userId\":17259,\"convertId\":\"wangyx03\",\"appCode\":\"M0028\",\"status\":\"1\",\"employeeNumber\":\"91F1823868\"}],\"type\":1,\"jobCode\":\"J14146\",\"jobName\":\"项目承揽承做岗\",\"jobStatus\":\"2\",\"jobGroupCode\":null,\"jobGroupName\":null,\"empStatus\":\"1\",\"workPlace\":\"北京\",\"phoneShortNo\":null,\"officeShortNo\":null,\"datasource\":\"HR\",\"partOrgs\":[],\"properties\":[{\"id\":339094,\"userId\":17259,\"userKey\":\"emptype\",\"userValue\":\"1\",\"employeeNumber\":\"91F1823868\"},{\"id\":339096,\"userId\":17259,\"userKey\":\"contract\",\"userValue\":\"是\",\"employeeNumber\":\"91F1823868\"},{\"id\":339092,\"userId\":17259,\"userKey\":\"eid\",\"userValue\":\"19804\",\"employeeNumber\":\"91F1823868\"}],\"_links\":{\"self\":{\"href\":\"http://10.29.181.202:30080/v1/users/17259\"}},\"id\":17259}");
		System.out.println(log);
		ZtsLog4j ztsLog4j = new ZtsLog4j(log, cal);
		System.out.println(ztsLog4j.get_links());
		System.out.println(ztsLog4j.getUserMaps());
		System.out.println(ztsLog4j.getProperties());
		System.out.println(new ZtsLog4j().toMapping());
	}
}

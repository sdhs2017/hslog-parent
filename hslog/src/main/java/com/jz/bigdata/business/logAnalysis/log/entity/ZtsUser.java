package com.jz.bigdata.business.logAnalysis.log.entity;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import com.google.gson.Gson;

public class ZtsUser {
	private _links _links;
	private String chgPassFlag;
	private String datasource;
	private String email;
	private String empStatus;
	private String employeeNumber;
	private String gender;
	// private String id;
	private String jobCode;
	private String jobName;
	private String jobStatus;
	private String name;
	private String orgs;
	private List<PartOrgs> partOrgs;
	private String phoneNo;
	private List<Properties> properties;
	private String status;
	private String type;
	private String userName;
	private String workPlace;
	private String officePhone;
	private String jobGroupCode;
	private String jobGroupName;
	private String phoneShortNo;
	private String officeShortNo;
	private List<UserMaps> userMaps;
	private String uid;

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
	 * ip地址
	 */
	private String ip;
	/**
	 * 主机名
	 */
	private String hostname;
	/**
	 * 操作描述
	 */
	private String operation_des;
	/**
	 * 是否为业务日志
	 */
	private Boolean is_business;
	
	/**
	 * 操作类型
	 */
	String operation_level;

	public _links get_links() {
		return _links;
	}

	public void set_links(_links _links) {
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public List<PartOrgs> getPartOrgs() {
		return partOrgs;
	}

	public void setPartOrgs(List<PartOrgs> partOrgs) {
		this.partOrgs = partOrgs;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public List<Properties> getProperties() {
		return properties;
	}

	public void setProperties(List<Properties> properties) {
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

	public List<UserMaps> getUserMaps() {
		return userMaps;
	}

	public void setUserMaps(List<UserMaps> userMaps) {
		this.userMaps = userMaps;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
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

	public String getOperation_des() {
		return operation_des;
	}

	public void setOperation_des(String operation_des) {
		this.operation_des = operation_des;
	}

	public Boolean getIs_business() {
		return is_business;
	}

	public void setIs_business(Boolean is_business) {
		this.is_business = is_business;
	}

	
	public String getOperation_level() {
		return operation_level;
	}

	public void setOperation_level(String operation_level) {
		this.operation_level = operation_level;
	}


	class _links {
		private Self self;

		public Self getSelf() {
			return self;
		}

		public void setSelf(Self self) {
			this.self = self;
		}

	}

	class Self {
		private String href;

		public String getHref() {
			return href;
		}

		public void setHref(String href) {
			this.href = href;
		}

	}

	class PartOrgs {

	}

	class Properties {
		private String employeeNumber;
		private String id;
		private String userId;
		private String userKey;
		private String userValue;

		public String getEmployeeNumber() {
			return employeeNumber;
		}

		public void setEmployeeNumber(String employeeNumber) {
			this.employeeNumber = employeeNumber;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getUserKey() {
			return userKey;
		}

		public void setUserKey(String userKey) {
			this.userKey = userKey;
		}

		public String getUserValue() {
			return userValue;
		}

		public void setUserValue(String userValue) {
			this.userValue = userValue;
		}

	}

	class UserMaps {
		private String appCode;
		private String convertId;
		private String employeeNumber;
		private String id;
		private String status;
		private String userId;

		public String getAppCode() {
			return appCode;
		}

		public void setAppCode(String appCode) {
			this.appCode = appCode;
		}

		public String getConvertId() {
			return convertId;
		}

		public void setConvertId(String convertId) {
			this.convertId = convertId;
		}

		public String getEmployeeNumber() {
			return employeeNumber;
		}

		public void setEmployeeNumber(String employeeNumber) {
			this.employeeNumber = employeeNumber;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

	}

	public ZtsUser() {

	}

	public ZtsUser ZtsUsers(String log) {
		
//		Gson gson = new Gson();
//		Log4jjson log4jjson = gson.fromJson(log, Log4jjson.class);
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		
//		this.ip = log4jjson.getIp();
//		// 时间处理
//		this.logdate = new Date(log4jjson.getTimestamp());
//		cal.setTimeInMillis(log4jjson.getTimestamp());
//		this.logtime = format.format(cal.getTime());
//		this.logtime_year = String.valueOf(cal.get(Calendar.YEAR));
//		this.logtime_month = String.format("%02d",cal.get(Calendar.MONTH)+1);
//		this.logtime_day = String.format("%02d",cal.get(Calendar.DAY_OF_MONTH));
//		this.logtime_hour = String.format("%02d",cal.get(Calendar.HOUR_OF_DAY));
//		this.logtime_minute = String.format("%02d",cal.get(Calendar.MINUTE));
//		
//		this.operation_level = log4jjson.getPriority();
//		if (log4jjson.getStack_trace()!=null) {
//			this.operation_des = this.logtime+" ["+operation_level+"] "+log4jjson.getLogger_name()+" "+log4jjson.getMessage()+" "+log4jjson.getStack_trace();
//		}else {
//			this.operation_des = this.logtime+" ["+operation_level+"] "+log4jjson.getLogger_name()+" "+log4jjson.getMessage();
//		}
		
		Gson gson = new Gson();
		ZtsUser ztsUser = gson.fromJson(log, ZtsUser.class);
//		this.uid = ztsUser.getId();
		ztsUser.setUid(ztsUser.getId());
		ztsUser.setId(null);

		// System.out.println(ztsUser.getUserMaps().get(0).getId());
		// System.out.println(ztsUser.get_links().getSelf().getHref());
		return ztsUser;

	}

	public String toMapping() {
		String template = "{\n" + "\t\t\"properties\":{\n" + "\t\t{#}\n" + "\t\t\t\t}" + "}";
		String fieldString = getClassMapping(new ZtsUser());
		template = template.replace("{#}", fieldString);
		return template;
	}

	public <T> String getClassMapping(T classes) {

		StringBuilder fieldstring = new StringBuilder();

		String[] fielddata = { "userid", "deptid", "equipmentid", "logtime", "ip", "hostname", "uid", "jobCode",
				"employeeNumber", "logtime_year", "logtime_month", "logtime_day", "logtime_hour", "logtime_minute",
				"equipmentname" };
		Field[] fields = classes.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			fieldstring.append("\t\t\t\t\"" + fields[i].getName().toLowerCase() + "\": {\n");
			fieldstring.append("\t\t\t\t\t\t\"type\": \""
					+ getElasticSearchMappingType(fields[i].getType().getSimpleName(), fields[i].getName()) + "\n");
			if (fields[i].getName().equals("id")) {
				fieldstring.append("\t\t\t\t\t\t,\"index\": \"" + "false\"" + "\n");
			}
			if (Arrays.asList(fielddata).contains(fields[i].getName())) {
				fieldstring.append("\t\t\t\t\t\t,\"fielddata\": " + "true" + "\n");
			}
			if (fields[i].getName().equals("operation_des") || fields[i].getName().equals("ip")
					|| fields[i].getName().equals("employeeNumber") || fields[i].getName().equals("hostname")
					|| fields[i].getName().equals("equipmentname") || fields[i].getName().equals("uId")
					|| fields[i].getName().equals("userName")) {
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

	private static String getElasticSearchMappingType(String varType, String name) {
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
		case "Integer":
			es = "integer\"";
			break;
		case "Boolean":
			es = "boolean\"";
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
//		String log = "{\"userName\":\"wangyx03\",\"name\":\"王怡雪\",\"email\":\"wangyx03@zts.com.cn\",\"phoneNo\":\"13954302163\",\"officePhone\":null,\"gender\":\"2\",\"status\":\"1\",\"chgPassFlag\":\"1\",\"orgs\":\"D3774:25\",\"employeeNumber\":\"91F1823868\",\"userMaps\":[{\"id\":90250,\"userId\":17259,\"convertId\":\"91F1823868\",\"appCode\":\"M0001\",\"status\":\"1\",\"employeeNumber\":\"91F1823868\"},{\"id\":91126,\"userId\":17259,\"convertId\":\"wangyx03\",\"appCode\":\"M0025\",\"status\":\"1\",\"employeeNumber\":\"91F1823868\"},{\"id\":91127,\"userId\":17259,\"convertId\":\"wangyx03\",\"appCode\":\"M0028\",\"status\":\"1\",\"employeeNumber\":\"91F1823868\"}],\"type\":1,\"jobCode\":\"J14146\",\"jobName\":\"项目承揽承做岗\",\"jobStatus\":\"2\",\"jobGroupCode\":null,\"jobGroupName\":null,\"empStatus\":\"1\",\"workPlace\":\"北京\",\"phoneShortNo\":null,\"officeShortNo\":null,\"datasource\":\"HR\",\"partOrgs\":[],\"properties\":[{\"id\":339094,\"userId\":17259,\"userKey\":\"emptype\",\"userValue\":\"1\",\"employeeNumber\":\"91F1823868\"},{\"id\":339096,\"userId\":17259,\"userKey\":\"contract\",\"userValue\":\"是\",\"employeeNumber\":\"91F1823868\"},{\"id\":339092,\"userId\":17259,\"userKey\":\"eid\",\"userValue\":\"19804\",\"employeeNumber\":\"91F1823868\"}],\"_links\":{\"self\":{\"href\":\"http://10.29.181.202:30080/v1/users/17259\"}},\"id\":17259}";
		String log="{\"userName\":\"wangyx03\",\"name\":\"王怡雪\",\"email\":\"wangyx03@zts.com.cn\",\"phoneNo\":\"13954302163\",\"officePhone\":null,\"gender\":\"2\",\"status\":\"1\",\"chgPassFlag\":\"1\",\"orgs\":\"D3774:25\",\"employeeNumber\":\"91F1823868\",\"userMaps\":[{\"id\":90250,\"userId\":17259,\"convertId\":\"91F1823868\",\"appCode\":\"M0001\",\"status\":\"1\",\"employeeNumber\":\"91F1823868\"},{\"id\":91126,\"userId\":17259,\"convertId\":\"wangyx03\",\"appCode\":\"M0025\",\"status\":\"1\",\"employeeNumber\":\"91F1823868\"},{\"id\":91127,\"userId\":17259,\"convertId\":\"wangyx03\",\"appCode\":\"M0028\",\"status\":\"1\",\"employeeNumber\":\"91F1823868\"}],\"type\":1,\"jobCode\":\"J14146\",\"jobName\":\"项目承揽承做岗\",\"jobStatus\":\"2\",\"jobGroupCode\":null,\"jobGroupName\":null,\"empStatus\":\"1\",\"workPlace\":\"北京\",\"phoneShortNo\":null,\"officeShortNo\":null,\"datasource\":\"HR\",\"partOrgs\":[],\"properties\":[{\"id\":339094,\"userId\":17259,\"userKey\":\"emptype\",\"userValue\":\"1\",\"employeeNumber\":\"91F1823868\"},{\"id\":339096,\"userId\":17259,\"userKey\":\"contract\",\"userValue\":\"是\",\"employeeNumber\":\"91F1823868\"},{\"id\":339092,\"userId\":17259,\"userKey\":\"eid\",\"userValue\":\"19804\",\"employeeNumber\":\"91F1823868\"}],\"_links\":{\"self\":{\"href\":\"http://10.29.181.202:30080/v1/users/17259\"}},\"id\":17259}";
		ZtsUser ztcSyslog = new ZtsUser();
		ztcSyslog = ztcSyslog.ZtsUsers(log);
		System.out.println(ztcSyslog.getDatasource());
		
		System.out.println(ztcSyslog.getUid());
		System.out.println(ztcSyslog.getUserMaps().get(0).getId());
		System.out.println(ztcSyslog.get_links().getSelf().getHref());
		// System.out.println(new ZtsUser().toMapping());
	}

}

package com.jz.bigdata.business.logAnalysis.log.entity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jz.bigdata.util.Pattern_Matcher;

public class Winlog {

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
	 * 日志产生时间
	 */
	Date logdate;
	String logtime;
	/*String logtime_minute;
	String logtime_hour;
	String logtime_day;
	String logtime_month;
	String logtime_year;*/
	/**
	 * 事件ID
	 */
	String eventid;
	/**
	 * 日志级别
	 */
	String operation_level;
	/**
	 * 进程ID
	 */
	String process_id;
	/**
	 * 进程名
	 */
	String process_name;
	/**
	 * 用户域
	 */
	String user_domain;
	/**
	 * 用户名
	 */
	String user_name;
	/**
	 * 计算机名
	 */
	String hostname;
	/**
	 * 计算机IP
	 */
	String ip;
	/**
	 * 日志描述
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

	public String getLogTime() {
		return logtime;
	}

	public void setLogTime(String logtime) {
		this.logtime = logtime;
	}

	public String getEventId() {
		return eventid;
	}

	public void setEventId(String eventid) {
		this.eventid = eventid;
	}

	public String getOperation_level() {
		return operation_level;
	}

	public void setOperation_level(String operation_level) {
		this.operation_level = operation_level;
	}

	public String getProcess_id() {
		return process_id;
	}

	public void setProcess_id(String process_id) {
		this.process_id = process_id;
	}

	public String getProcess_name() {
		return process_name;
	}

	public void setProcess_name(String process_name) {
		this.process_name = process_name;
	}

	public String getUser_domain() {
		return user_domain;
	}

	public void setUser_domain(String user_domain) {
		this.user_domain = user_domain;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getHostName() {
		return hostname;
	}

	public void setHostName(String hostname) {
		this.hostname = hostname;
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

	public Winlog() {

	}

	public Winlog(String log) throws ParseException {

		// winlog日志安全审计类型
		Pattern pattern = Pattern.compile("Security-Auditing:");
		Matcher matcher = pattern.matcher(log);
		Pattern Securitypattern = Pattern.compile("Security:");
		Matcher Securitymatcher = Securitypattern.matcher(log);
		//
		Pattern PRIpattern = Pattern.compile("<[0-9]{1,5}>");
		Matcher PRImatcher = PRIpattern.matcher(log);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		if (matcher.find()) {
			if (PRImatcher.find()) {
				String PRI = PRImatcher.group(0);
				PRI = PRI.substring(1, PRI.length() - 1);
				Integer PRIint = Integer.valueOf(PRI);
				this.event_level = PRIint % 8;
				String level = Syslog.getLevel(PRIint % 8);
				this.operation_level = level;
			}
			String logleft = log.substring(0, log.indexOf(matcher.group(0)) + matcher.group(0).length());
			String[] loglefts = logleft.split(" ");
			this.logtime = loglefts[1] + " " + loglefts[2];
			this.logdate = format.parse(this.logtime);
			/*String[] tmptime = this.logtime.split(" ");
			String[] date = tmptime[0].split("-");
			String[] time = tmptime[1].split(":");
			this.logtime_year = date[0];
			this.logtime_month = date[1];
			this.logtime_day = date[2];
			this.logtime_hour = time[0];
			this.logtime_minute = time[1];*/
			this.hostname = loglefts[3];
			this.ip = loglefts[4];
			// this.operation_level = matcher.group(0);
			log = log.substring(log.indexOf(matcher.group(0)) + matcher.group(0).length() + 1);
			this.operation_des = log;
			String[] logs = log.split(" ");
			this.eventid = logs[0].replace(":", "");
			Pattern SecurityIDpattern = Pattern.compile("安全 ID:");
			Matcher SecurityIDmatcher = SecurityIDpattern.matcher(log);

			Pattern UserNamepattern = Pattern.compile("帐户名称:");
			Matcher UserNamematcher = UserNamepattern.matcher(log);

			Pattern Yupattern = Pattern.compile("帐户域:");
			Matcher Yumatcher = Yupattern.matcher(log);

			Pattern UserIDpattern = Pattern.compile("登录 ID:");
			Matcher UserIDmatcher = UserIDpattern.matcher(log);

			Pattern logintypepattern = Pattern.compile("登录类型:");
			Matcher logintypematcher = logintypepattern.matcher(log);

			Pattern limitSystempattern = Pattern.compile("受限制的管理员模式:");
			Matcher limitSystemmatcher = limitSystempattern.matcher(log);

			Pattern fictitiousAccountpattern = Pattern.compile("虚拟帐户:");
			Matcher fictitiousAccountmatcher = fictitiousAccountpattern.matcher(log);

			Pattern tokenpattern = Pattern.compile("提升的令牌:");
			Matcher tokenmatcher = tokenpattern.matcher(log);

			Pattern processidpattern = Pattern.compile("进程 ID:");
			Matcher processidmatcher = processidpattern.matcher(log);

			Pattern processnamepattern = Pattern.compile("进程名称:");
			Matcher processnamematcher = processnamepattern.matcher(log);
			// 事件定性
			Pattern loginpattern = Pattern.compile("登录成功", Pattern.CASE_INSENSITIVE);
			Matcher loginmatcher = loginpattern.matcher(log);
			Pattern addpattern = Pattern.compile("源网络地址:", Pattern.CASE_INSENSITIVE);
			Matcher addmatcher = addpattern.matcher(log);
			Pattern ipaddpattern = Pattern.compile("127.0.0.1", Pattern.CASE_INSENSITIVE);
			Matcher ipaddmatcher = ipaddpattern.matcher(log);
			Pattern winspattern = Pattern.compile("会话从 winstation 中断连接", Pattern.CASE_INSENSITIVE);
			Matcher winsmatcher = winspattern.matcher(log);
			Pattern logoffpattern = Pattern.compile("用户注销", Pattern.CASE_INSENSITIVE);
			Matcher logoffmatcher = logoffpattern.matcher(log);

			if (SecurityIDmatcher.find()) {
				String tmp = log.substring(
						log.indexOf(SecurityIDmatcher.group(0)) + SecurityIDmatcher.group(0).length() + 1,
						log.length());
				String[] tmps = tmp.split(" ");
				String serId = tmps[0];
			}
			if (UserNamematcher.find()) {
				String tmp = log.substring(
						log.indexOf(UserNamematcher.group(0)) + UserNamematcher.group(0).length() + 1, log.length());
				String[] tmps = tmp.split(" ");
				this.user_name = tmps[0];
			}
			if (Yumatcher.find()) {
				String tmp = log.substring(log.indexOf(Yumatcher.group(0)) + Yumatcher.group(0).length() + 1,
						log.length());
				String[] tmps = tmp.split(" ");
				this.user_domain = tmps[0];
			}
			if (UserIDmatcher.find()) {
				String tmp = log.substring(log.indexOf(UserIDmatcher.group(0)) + UserIDmatcher.group(0).length() + 1,
						log.length());
				String[] tmps = tmp.split(" ");
				// this.user_domain = tmps[0];
			}
			if (logintypematcher.find()) {
				String tmp = log.substring(
						log.indexOf(logintypematcher.group(0)) + logintypematcher.group(0).length() + 1, log.length());
				String[] tmps = tmp.split(" ");
				String logintype = tmps[0];
			}
			if (limitSystemmatcher.find()) {
				String tmp = log.substring(
						log.indexOf(limitSystemmatcher.group(0)) + limitSystemmatcher.group(0).length() + 1,
						log.length());
				String[] tmps = tmp.split(" ");
				String islimitSystem = tmps[0];
			}
			if (fictitiousAccountmatcher.find()) {
				String tmp = log.substring(
						log.indexOf(fictitiousAccountmatcher.group(0)) + fictitiousAccountmatcher.group(0).length() + 1,
						log.length());
				String[] tmps = tmp.split(" ");
				String fictitiousAccount = tmps[0];
			}
			if (tokenmatcher.find()) {
				String tmp = log.substring(log.indexOf(tokenmatcher.group(0)) + tokenmatcher.group(0).length() + 1,
						log.length());
				String[] tmps = tmp.split(" ");
				String istoken = tmps[0];
			}
			if (processidmatcher.find()) {
				String tmp = log.substring(
						log.indexOf(processidmatcher.group(0)) + processidmatcher.group(0).length() + 1, log.length());
				String[] tmps = tmp.split(" ");
				this.process_id = tmps[0];
			}
			if (processnamematcher.find()) {
				String tmp = log.substring(
						log.indexOf(processnamematcher.group(0)) + processnamematcher.group(0).length() + 1,
						log.length());
				String[] tmps = tmp.split(" ");
				this.process_name = tmps[0];
			}
			if (fictitiousAccountmatcher.find()) {
				String tmp = log.substring(
						log.indexOf(fictitiousAccountmatcher.group(0)) + fictitiousAccountmatcher.group(0).length() + 1,
						log.length());
				String[] tmps = tmp.split(" ");
				String fictitiousAccount = tmps[0];
			}
			if (fictitiousAccountmatcher.find()) {
				String tmp = log.substring(
						log.indexOf(fictitiousAccountmatcher.group(0)) + fictitiousAccountmatcher.group(0).length() + 1,
						log.length());
				String[] tmps = tmp.split(" ");
				String fictitiousAccount = tmps[0];
			}

			if (loginmatcher.find() && addmatcher.find() && ipaddmatcher.find()) {
				this.event_type = "login_successful";
				this.event_des = "登录成功";
			}
			if (loginmatcher.find() && addmatcher.find() && !ipaddmatcher.find()) {
				this.event_type = "mstsc_successful";
				this.event_des = "远程登录成功";
			}
			if (winsmatcher.find()) {
				this.event_type = "mstsc_interrupt";
				this.event_des = "远程连接中断";
			}
			if (logoffmatcher.find()) {
				this.event_type = "log_off";
				this.event_des = "用户注销";
			}

			// 日志过滤
			if (Pattern_Matcher.getMatchedContent(this.operation_des, "Windows 筛选平台阻止了数据包") != ""
					|| Pattern_Matcher.getMatchedContent(this.operation_des, "Windows 筛选平台已阻止连接") != "") {
				this.operation_level = "Info";
			}
			this.index_suffix = "syslog";

		} else if (Securitymatcher.find()) {
			if (PRImatcher.find()) {
				String PRI = PRImatcher.group(0);
				PRI = PRI.substring(1, PRI.length() - 1);
				Integer PRIint = Integer.valueOf(PRI);
				this.event_level = PRIint % 8;
				String level = Syslog.getLevel(PRIint % 8);
				this.operation_level = level;
			}
			// 日志时间
			Pattern datePattern = Pattern
					.compile("[0-9]{4}[-][0-9]{1,2}[-][0-9]{1,2}[ ][0-9]{1,2}[:][0-9]{1,2}[:][0-9]{1,2}");
			Matcher datematcher = datePattern.matcher(log);
			// IP
			Pattern ipPattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)");
			Matcher ipmatcher = ipPattern.matcher(log);
			// 事件定性
			Pattern loginpattern = Pattern.compile("登录成功", Pattern.CASE_INSENSITIVE);
			Matcher loginmatcher = loginpattern.matcher(log);
			Pattern addpattern = Pattern.compile("源网络地址:", Pattern.CASE_INSENSITIVE);
			Matcher addmatcher = addpattern.matcher(log);
			Pattern ipaddpattern = Pattern.compile("127.0.0.1", Pattern.CASE_INSENSITIVE);
			Matcher ipaddmatcher = ipaddpattern.matcher(log);
			Pattern winspattern = Pattern.compile("会话从 winstation 中断连接", Pattern.CASE_INSENSITIVE);
			Matcher winsmatcher = winspattern.matcher(log);
			Pattern logoffpattern = Pattern.compile("用户注销", Pattern.CASE_INSENSITIVE);
			Matcher logoffmatcher = logoffpattern.matcher(log);
			if (datematcher.find()) {
				this.logtime = datematcher.group(0);
				this.logdate = format.parse(this.logtime);
				/*String[] tmptime = this.logtime.split(" ");
				String[] date = tmptime[0].split("-");
				String[] time = tmptime[1].split(":");
				this.logtime_year = date[0];
				this.logtime_month = date[1];
				this.logtime_day = date[2];
				this.logtime_hour = time[0];
				this.logtime_minute = time[1];*/
			}
			String logleft = log.substring(0,
					log.indexOf(Securitymatcher.group(0)) + Securitymatcher.group(0).length());
			String[] loglefts = logleft.split(" ");
			this.hostname = loglefts[3];
			if (ipmatcher.find()) {
				this.ip = ipmatcher.group(0);
			}
			log = log.substring(log.indexOf(Securitymatcher.group(0)) + Securitymatcher.group(0).length() + 1);
			this.operation_des = log;
			String[] logs = log.split(" ");
			this.eventid = logs[0].replace(":", "");
			Pattern SecurityIDpattern = Pattern.compile("安全 ID:");
			Matcher SecurityIDmatcher = SecurityIDpattern.matcher(log);

			Pattern UserNamepattern = Pattern.compile("用户名:");
			Matcher UserNamematcher = UserNamepattern.matcher(log);

			Pattern Yupattern = Pattern.compile("域:");
			Matcher Yumatcher = Yupattern.matcher(log);

			Pattern UserIDpattern = Pattern.compile("登录 ID:");
			Matcher UserIDmatcher = UserIDpattern.matcher(log);

			Pattern logintypepattern = Pattern.compile("登录类型:");
			Matcher logintypematcher = logintypepattern.matcher(log);

			Pattern limitSystempattern = Pattern.compile("受限制的管理员模式:");
			Matcher limitSystemmatcher = limitSystempattern.matcher(log);

			Pattern fictitiousAccountpattern = Pattern.compile("虚拟帐户:");
			Matcher fictitiousAccountmatcher = fictitiousAccountpattern.matcher(log);

			Pattern tokenpattern = Pattern.compile("提升的令牌:");
			Matcher tokenmatcher = tokenpattern.matcher(log);

			Pattern processidpattern = Pattern.compile("进程 ID:");
			Matcher processidmatcher = processidpattern.matcher(log);

			Pattern processnamepattern = Pattern.compile("进程名称:");
			Matcher processnamematcher = processnamepattern.matcher(log);

			if (SecurityIDmatcher.find()) {
				String tmp = log.substring(
						log.indexOf(SecurityIDmatcher.group(0)) + SecurityIDmatcher.group(0).length() + 1,
						log.length());
				String[] tmps = tmp.split(" ");
				String serId = tmps[0];
			}
			if (UserNamematcher.find()) {
				String tmp = log.substring(
						log.indexOf(UserNamematcher.group(0)) + UserNamematcher.group(0).length() + 1, log.length());
				String[] tmps = tmp.split(" ");
				this.user_name = tmps[0];
			}
			if (Yumatcher.find()) {
				String tmp = log.substring(log.indexOf(Yumatcher.group(0)) + Yumatcher.group(0).length() + 1,
						log.length());
				String[] tmps = tmp.split(" ");
				String Yuname = tmps[0];
			}
			if (UserIDmatcher.find()) {
				String tmp = log.substring(log.indexOf(UserIDmatcher.group(0)) + UserIDmatcher.group(0).length() + 1,
						log.length());
				String[] tmps = tmp.split(" ");
				this.user_domain = tmps[0];
			}
			if (logintypematcher.find()) {
				String tmp = log.substring(
						log.indexOf(logintypematcher.group(0)) + logintypematcher.group(0).length() + 1, log.length());
				String[] tmps = tmp.split(" ");
				// String logintype = tmps[0];
			}
			if (limitSystemmatcher.find()) {
				String tmp = log.substring(
						log.indexOf(limitSystemmatcher.group(0)) + limitSystemmatcher.group(0).length() + 1,
						log.length());
				String[] tmps = tmp.split(" ");
				String islimitSystem = tmps[0];
			}
			if (fictitiousAccountmatcher.find()) {
				String tmp = log.substring(
						log.indexOf(fictitiousAccountmatcher.group(0)) + fictitiousAccountmatcher.group(0).length() + 1,
						log.length());
				String[] tmps = tmp.split(" ");
				String fictitiousAccount = tmps[0];
			}
			if (tokenmatcher.find()) {
				String tmp = log.substring(log.indexOf(tokenmatcher.group(0)) + tokenmatcher.group(0).length() + 1,
						log.length());
				String[] tmps = tmp.split(" ");
				String istoken = tmps[0];
			}
			if (processidmatcher.find()) {
				String tmp = log.substring(
						log.indexOf(processidmatcher.group(0)) + processidmatcher.group(0).length() + 1, log.length());
				String[] tmps = tmp.split(" ");
				this.process_id = tmps[0];
			}
			if (processnamematcher.find()) {
				String tmp = log.substring(
						log.indexOf(processnamematcher.group(0)) + processnamematcher.group(0).length() + 1,
						log.length());
				String[] tmps = tmp.split(" ");
				this.process_name = tmps[0];
			}
			if (fictitiousAccountmatcher.find()) {
				String tmp = log.substring(
						log.indexOf(fictitiousAccountmatcher.group(0)) + fictitiousAccountmatcher.group(0).length() + 1,
						log.length());
				String[] tmps = tmp.split(" ");
				String fictitiousAccount = tmps[0];
			}
			if (fictitiousAccountmatcher.find()) {
				String tmp = log.substring(
						log.indexOf(fictitiousAccountmatcher.group(0)) + fictitiousAccountmatcher.group(0).length() + 1,
						log.length());
				String[] tmps = tmp.split(" ");
				String fictitiousAccount = tmps[0];
			}

			if (loginmatcher.find() && addmatcher.find() && ipaddmatcher.find()) {
				this.event_type = "login_successful";
				this.event_des = "登录成功";
			}
			if (loginmatcher.find() && addmatcher.find() && !ipaddmatcher.find()) {
				this.event_type = "mstsc_successful";
				this.event_des = "远程登录成功";
			}
			if (winsmatcher.find()) {
				this.event_type = "mstsc_interrupt";
				this.event_des = "远程连接中断";
			}
			if (logoffmatcher.find()) {
				this.event_type = "log_off";
				this.event_des = "用户注销";
			}

			// 日志过滤
			if (Pattern_Matcher.getMatchedContent(this.operation_des, "Windows 筛选平台阻止了数据包") != ""
					|| Pattern_Matcher.getMatchedContent(this.operation_des, "Windows 筛选平台已阻止连接") != "") {
				this.operation_level = "Info";
			}
			this.index_suffix = "syslog";
		}

	}

	public String toMapping() {
		String template = "{\n" + "\t\t\"properties\":{\n" + "\t\t{#}\n" + "\t\t\t\t}" + "}";
		String fieldString = getClassMapping(new Winlog());
		template = template.replace("{#}", fieldString);
		return template;
	}

	public <T> String getClassMapping(T classes) {

		StringBuilder fieldstring = new StringBuilder();

		String[] fielddata = { "userid", "deptid", "equipmentid", "logtime", "logname", "ip", "logsource", "eventid",
				"tasktype", "operation_level", "keywords", "hostname", "logtime_year", "logtime_month", "logtime_day",
				"logtime_hour", "logtime_minute", "equipmentname", "event_type" };
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
			if (fields[i].getName().equals("operation_des") || fields[i].getName().equals("keywords")
					|| fields[i].getName().equals("hostname") || fields[i].getName().equals("ip")
					|| fields[i].getName().equals("equipmentname") || fields[i].getName().equals("event_des")) {
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

	public static void main(String[] args) throws IOException {

		// TODO Auto-generated method stub

		/*
		 * String log =
		 * "{\"@timestamp\":\"2017-09-29T01:42:29.601Z\",\"beat\":{\"hostname\":\"aaa-PC\",\"name\":\"aaa-PC\",\"version\":\"5.5.2\"},\"computer_name\":\"aaa-PC\",\"event_data\":{\"String\":\"C:\\\\Windows\\\\system32\\\\nvinitx.dll\",\"StringCount\":\"1\"},\"event_id\":11,\"level\":\"警告\",\"log_name\":\"System\",\"message\":\"正在为每个应用程序加载自定义动态链接库。系统管理员应该复查库列表以确保它们与受信任的应用程序相关。\",\"opcode\":\"信息\",\"process_id\":692,\"provider_guid\":\"{206F6DEA-D3C5-4D10-BC72-989F03C8B84B}\",\"record_number\":\"207722\",\"source_name\":\"Microsoft-Windows-Wininit\",\"thread_id\":724,\"type\":\"wineventlog\",\"user\":{\"domain\":\"NT AUTHORITY\",\"identifier\":\"S-1-5-18\",\"name\":\"SYSTEM\",\"type\":\"User\"}}"
		 * ;
		 * //"{\"@timestamp\":\"2017-09-29T01:42:22.315Z\",\"beat\":{\"hostname\":\"aaa-PC\",\"name\":\"aaa-PC\",\"version\":\"5.5.2\"},\"computer_name\":\"aaa-PC\",\"event_data\":{\"Binary\":\"4200460045002F0034000000\",\"param1\":\"Base Filtering Engine\",\"param2\":\"正在运行\"},\"event_id\":7036,\"keywords\":[\"经典\",\"经典\",\"经典\"],\"level\":\"信息\",\"log_name\":\"System\",\"message\":\"Base Filtering Engine 服务处于 正在运行 状态。\",\"process_id\":748,\"provider_guid\":\"{555908d1-a6d7-4695-8e1e-26931d2012f4}\",\"record_number\":\"207704\",\"source_name\":\"Service Control Manager\",\"thread_id\":880,\"type\":\"wineventlog\"}"
		 * ;
		 * 
		 * Winlog wmi = new Winlog(log);
		 */

		// String log = "<29> 2018-03-27 14:03:49 MCTC-ZSC 192.168.20.72 Security: 520:
		// NT AUTHORITY\\LOCAL SERVICE: 系统时间已更改。 进程 ID: 844 进程名:
		// C:\\WINDOWS\\system32\\svchost.exe 主要用户名: LOCAL SERVICE 主要域: NT AUTHORITY
		// 主要登录 ID: (0x0,0x3E5) 客户端用户名: - 客户端域: - 客户端登录: - 上一时间: 14:04:28 2018-3-28 新时间:
		// 14:03:49 2018-3-28 ";
		System.out.println(new Winlog().toMapping());

	}

}

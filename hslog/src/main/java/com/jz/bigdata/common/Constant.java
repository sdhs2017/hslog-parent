package com.jz.bigdata.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yiyang
 * @date 2016年7月29日 上午9:31:24
 * @description 系统常量
 */
public class Constant {
	public static final String ZIP_PASS = "jzdata.123";
	/**
	 * 用户角色ID
	 */
	public static final String ROLE_ADMIN = "admin";//安全保密员
	public static final String ROLE_USERADMIN = "useradmin";//系统管理员
	public static final String ROLE_AUDIT = "audit";//安全审计员

	/**
	 * license 版本信息，通过标识初始化功能菜单。
	 * #1. 日志模块  log
	 * #2. 流量模块 packet
	 * #3. 资产模块 asset
	 * #4. 可视化 visual
	 * #5. 告警模块 alert
	 * #6. 数据资产安全治理 dsg
	 * #7. 系统管理 system
	 * #目前分为4款产品：宸析日志、宸析数据可视化、宸析全维度、宸析数据资产安全治理
	 * #对应的菜单分别为：
	 * #宸析日志：system,log,asset
	 * #宸析数据可视化：system,log,packet,asset,visual
	 * #宸析全维度：system,log,packet,asset,visual,alert
	 * #宸析数据资产安全治理：system,dsg
	 */
	public static final String LICENSE_TYPE_LOG = "log";
	public static final String LICENSE_TYPE_VISUAL = "visual";
	public static final String LICENSE_TYPE_PACKET = "packet";
	public static final String LICENSE_TYPE_ASSET = "asset";
	public static final String LICENSE_TYPE_ALERT = "alert";
	public static final String LICENSE_TYPE_DSG = "dsg";
	public static final String LICENSE_TYPE_SYSTEM = "system";

	//资产类型为防火墙，在数据库中的存储代码
	public static final String EQUIPMENT_TYPE_FIREWALL_CODE = "0204";
	//资产类型为IPS，在数据库中的存储代码
	public static final String EQUIPMENT_TYPE_IPS_CODE = "0201";
	//资产类型，代码与汉字对应关系
	public static Map<String,String> EQUIPMENT_TYPE_EN = new HashMap<>();
	static{
		EQUIPMENT_TYPE_EN.put("01","网络-全部");
		EQUIPMENT_TYPE_EN.put("0101","网络-交换机");
		EQUIPMENT_TYPE_EN.put("0102","网络-路由器");
		EQUIPMENT_TYPE_EN.put("02","安全-全部");
		EQUIPMENT_TYPE_EN.put("0201","安全-IPS");
		EQUIPMENT_TYPE_EN.put("0202","安全-IDS");
		EQUIPMENT_TYPE_EN.put("0203","安全-抗DDOS");
		EQUIPMENT_TYPE_EN.put("0204","安全-防火墙");
		EQUIPMENT_TYPE_EN.put("03","主机-全部");
		EQUIPMENT_TYPE_EN.put("0301","主机-Windows");
		EQUIPMENT_TYPE_EN.put("0302","主机-Linux");
		EQUIPMENT_TYPE_EN.put("0303","主机-虚拟机");
		EQUIPMENT_TYPE_EN.put("04","应用-全部");
		EQUIPMENT_TYPE_EN.put("0401","应用-Tomcat");
		EQUIPMENT_TYPE_EN.put("0402","应用-Apache");
		EQUIPMENT_TYPE_EN.put("0403","应用-IIS");
		EQUIPMENT_TYPE_EN.put("0404","应用-Weblogic");
		EQUIPMENT_TYPE_EN.put("0405","应用-Mysql");
		EQUIPMENT_TYPE_EN.put("0406","应用-Oracle");
		EQUIPMENT_TYPE_EN.put("0407","应用-Sqlserver");
		EQUIPMENT_TYPE_EN.put("0408","应用-Db2");
	}
	/**
	 * 报告模板类型 中英文对照，日报day、周报week、月报month
	 */
	public static Map<String,String> REPORT_MODEL_TYPE = new HashMap<>();
	static{
		REPORT_MODEL_TYPE.put("day","日报");
		REPORT_MODEL_TYPE.put("week","周报");
		REPORT_MODEL_TYPE.put("month","月报");
	}
	//操作管理员角色id
	public static final String USER_ROLE_OPERATION_MANAGER = "manager";
	//filebeat file_type = db 文件类型=数据库
	public static final String FILEBEAT_FILE_TYPE_DB = "db";
	//filebeat db_type = mysql 数据库类型=mysql
	public static final String FILEBEAT_DB_TYPE_MYSQL = "mysql";
	//mysql-audit插件产生的日志
	public static final String FILEBEAT_LOG_TYPE_MYSQL_AUDIT = "mysql-audit";
	//mysql-audit 日志类型 activity 正常sql查询产生的日志类型
	public static final String FILEBEAT_MYSQL_AUDIT_MSGTYPE_ACT = "activity";
	//mysql-audit 日志类型 header mysql启动时会产生一条日志，记录mysql启动信息
	public static final String FILEBEAT_MYSQL_AUDIT_MSGTYPE_HEADER = "header";
	//mysql-audit审计日志index前缀
	public static final String MYSQL_AUDIT_INDEX_NAME = "hs_db_mysql_log-*";

	//TODO 方法提出公共类
	//数据安全治理 标签管理，发现方式  0：无规则  1：按正则发现
	public static List<Map<String,Object>> DSG_TAG_LIBRARY_DISCOVER_WAY = new ArrayList<>();
	static{
		Map<String,Object> no_rule = new HashMap<>();
		no_rule.put("label","无规则");
		no_rule.put("value",0);
		DSG_TAG_LIBRARY_DISCOVER_WAY.add(no_rule);
		Map<String,Object> regex_rule = new HashMap<>();
		regex_rule.put("label","按正则发现");
		regex_rule.put("value",1);
		DSG_TAG_LIBRARY_DISCOVER_WAY.add(regex_rule);
	}
	/**
	 * 数据源-元数据-字段敏感等级
	 */
	public static List<Map<String,String>> DATA_SOURCE_METADATA_FIELD_SensitiveLevel = new ArrayList<>();
	//TODO k/V生成map方法
	static{
		Map<String,String> level1 = new HashMap<>();
		level1.put("label","1级（低敏感级）");
		level1.put("value","1");
		DATA_SOURCE_METADATA_FIELD_SensitiveLevel.add(level1);
		Map<String,String> level2 = new HashMap<>();
		level2.put("label","2级（较高敏感级）");
		level2.put("value","2");
		DATA_SOURCE_METADATA_FIELD_SensitiveLevel.add(level2);
		Map<String,String> level3 = new HashMap<>();
		level3.put("label","3级（高敏感级）");
		level3.put("value","3");
		DATA_SOURCE_METADATA_FIELD_SensitiveLevel.add(level3);
		Map<String,String> level4 = new HashMap<>();
		level4.put("label","4级（极高敏感级）");
		level4.put("value","4");
		DATA_SOURCE_METADATA_FIELD_SensitiveLevel.add(level4);
	}
	//日志级别与数字的对应关系
	public final static Map<Integer, String> SYSLOG_SEVERITY = new HashMap<>();
	static{
		SYSLOG_SEVERITY.put(0, "emergency"); 		//system is unusable
		SYSLOG_SEVERITY.put(1, "alert");	 		//action must be taken immediately
		SYSLOG_SEVERITY.put(2, "critical");	 		//critical conditions
		SYSLOG_SEVERITY.put(3, "error");			//error conditions
		SYSLOG_SEVERITY.put(4, "warning");			//warning conditions
		SYSLOG_SEVERITY.put(5, "notice");			//normal but significant condition
		SYSLOG_SEVERITY.put(6, "information");	//informational messages
		SYSLOG_SEVERITY.put(7, "debug");			//debug-level messages
	}

	/**
	 * 数据源-元数据-表格分类
	 */
	public static List<Map<String,String>> DATA_SOURCE_METADATA_TABLE_TYPE = new ArrayList<>();
	//TODO k/V生成map方法
	static{
		Map<String,String> main_table = new HashMap<>();
		main_table.put("label","主体表");
		main_table.put("value","main");
		DATA_SOURCE_METADATA_TABLE_TYPE.add(main_table);
		Map<String,String> refer_table = new HashMap<>();
		refer_table.put("label","参考表");
		refer_table.put("value","refer");
		DATA_SOURCE_METADATA_TABLE_TYPE.add(refer_table);
		Map<String,String> statistic_table = new HashMap<>();
		statistic_table.put("label","统计表");
		statistic_table.put("value","statistic");
		DATA_SOURCE_METADATA_TABLE_TYPE.add(statistic_table);
		Map<String,String> other_table = new HashMap<>();
		other_table.put("label","其他表");
		other_table.put("value","other");
		DATA_SOURCE_METADATA_TABLE_TYPE.add(other_table);
	}
	public static final String DATA_SOURCE_ITEM_TYPE_MYSQL = "MySQL";
	public static final String DATA_SOURCE_ITEM_TYPE_SQLSERVER = "SQL Server";
	public static final String DATA_SOURCE_ITEM_TYPE_ORACLE = "Oracle";
	//多种类型数据库查询出的数据库列名不一致，通过别名统一
	//由于mysql 查询数据库的语句无法设置别名，因此向mysql的名称靠齐
	public static final String DATA_SOURCE_DATABASE_ALIAS = "Database";
	//统一表的别名
	public static final String DATA_SOURCE_TABLE_ALIAS = "TABLE_NAME";
	/**
	 * 数据源管理中，数据源类型，目前仅适配 mysql oracle sqlserver
	 */
	public static List<Map<String,String>> DATA_SOURCE_ITEM_TYPE = new ArrayList<>();
	static{
		Map<String,String> mysql = new HashMap<>();
		mysql.put("label",DATA_SOURCE_ITEM_TYPE_MYSQL);
		mysql.put("value",DATA_SOURCE_ITEM_TYPE_MYSQL);
		DATA_SOURCE_ITEM_TYPE.add(mysql);
		Map<String,String> sqlserver = new HashMap<>();
		sqlserver.put("label",DATA_SOURCE_ITEM_TYPE_SQLSERVER);
		sqlserver.put("value",DATA_SOURCE_ITEM_TYPE_SQLSERVER);
		DATA_SOURCE_ITEM_TYPE.add(sqlserver);
		Map<String,String> oracle = new HashMap<>();
		oracle.put("label",DATA_SOURCE_ITEM_TYPE_ORACLE);
		oracle.put("value",DATA_SOURCE_ITEM_TYPE_ORACLE);
		DATA_SOURCE_ITEM_TYPE.add(oracle);
	}
	/**
	 * siem界面对应选择事件下拉框，第一个下拉框，选择事件类型：linux/windows
	 */
	public static List<Map<String,String>> EVENT_TYPE = new ArrayList<>();
	static{
		Map<String,String> linux = new HashMap<>();
		linux.put("label","Linux");
		linux.put("value","0302");
		EVENT_TYPE.add(linux);
		Map<String,String> windows = new HashMap<>();
		windows.put("label","Windows");
		windows.put("value","0301");
		EVENT_TYPE.add(windows);
	}

	//批量提交数
	public static String ES_BULK_NAME = "es_bulk";
	//ES bulk提交 线程数
	public static String ES_BULK_PROCESSOR_CONCURRENT_REQUESTS_NAME = "concurrent_requests";
	//configuration 配置项，密码过期时间-key
	public static String PWD_EXPIRE_DAY_NAME = "pwd_expire_day";
	//configuration 配置项，磁盘已满百分比-key
	public static String DISK_DATA_WATERMARK_HIGH = "disk_data_watermark_high";
	//configuration 配置项，磁盘将满百分比-key
	public static String DISK_DATA_WATERMARK = "disk_data_watermark";
	//configuration 配置项，系统盘使用占比-key
	public static String DISK_SYSTEM_WATERMARK = "disk_system_watermark";
	//configuration 配置项，数据存储时间-key
	public static String ES_STORAGE_DAY = "es_storage_day";
	//configuration 配置项，密码复杂度-key
	public static String PWD_COMPLEX = "pwd_complex";
	//configuration 配置项，密码长度-key
	public static String PWD_LENGTH = "pwd_length";
	//configuration 配置项，密码错误可尝试次数-key
	public static String PWD_TRY = "pwd_try";
	//configuration 配置项，session过期时间-key
	public static String SESSION_TIMEOUT = "session_timeout";
	//configuration 配置项，IP黑名单-key
	public static String IP_BLACK = "ip_black";
	//configuration 配置项，是否包含流量模块
	public static String HAS_PACKET = "has_packet";
	//configuration 配置项，系统首页
	public static String INDEX_PAGE = "index";
	//configuration 配置项，logo路径
	public static String LOGO_PATH = "logo";
	//configuration 配置项，版本号
	public static String VERSION = "version";
	//configuration 配置项，资产对应的报表菜单，在配置文件中的key
	public static String EQUIPMENT_CHART_JSON_KEY = "equipment_chart_json_key";
	//资产对应报表菜单json在配置文件中的key /resource/database/mysql/roleauthority.properties
	public static String EQUIPMENT_CHART_DEFAULT = "equipment_chart_default";//默认报表菜单
	public static String EQUIPMENT_CHART_ALERT = "equipment_chart_alert";//有告警模块时的报表菜单
	//beats的日期字段
	public static String BEAT_DATE_FIELD = "@timestamp";
	//系统日志对应的index名称
	public static String WINLOG_BEAT_INDEX = "winlogbeat-*";
	//旧流量日期字段
	public static String PACKET_DATE_FIELD = "logdate";
	//旧流量index名称
	public static String PACKET_INDEX = "hslog_packet*";
	//alert index名称
	public static final String ALERT_SEARCH_INDEX = "alert-default*";
	public static final String ALERT_WRITE_INDEX = "alert-default";
	/**
	 * 	session信息---用户ID
	 */
	public static String SESSION_USERID = "SESSION_USERID";
//	部门id
	public static String SESSION_DEPARTMENTID = "SESSION_DEPARTMENTID";
	//username
	public static String SESSION_USERNAME = "SESSION_USERNAME";
	//部门名称
	public static String SESSION_DEPARTMENTNAME = "SESSION_DEPARTMENTNAME";
	//账号
	public static String SESSION_USERACCOUNT = "SESSION_USERACCOUNT";
	//角色
	public static String SESSION_USERROLE = "SESSION_USERROLE";
//	public static String SESSION_DEPARTMENTID = "SESSION_DEPARTMENTID";
	public static String SESSION_DEPARTMENTNODEID = "SESSION_DEPARTMENTNODEID";
	
	/**
	 * session信息---sessionid
	 */
	public static String SESSION_ID = "SESSION_ID";
	/**
	 * 返回首页路径
	 */
	public static String REDIRECTPATH="/TestRedis/Login.jsp";
	//涉密，临时添加，用于防止越权
	public static String USER_PATH = "com.jz.bigdata.roleauthority.user";//用户相关
	public static String MENU_PATH = "com.jz.bigdata.roleauthority.menu";//菜单相关
	public static String ROLE_PATH = "com.jz.bigdata.roleauthority.role";//角色相关
	public static String NOTE_PATH = "com.jz.bigdata.common.note";//系统自身审计日志相关
	public static String RSA_PATH = "com.jz.bigdata.common.rsa";//RSA加密

	public static String DEPARTMENT_PATH = "com.jz.bigdata.common.department";//组织机构

	public static String IP_PATH = "com.jz.bigdata.common.updateIp";//获取IP
	public static String DISK_USAGE = "com.jz.bigdata.common.manage.controller.ManageController.getDiskUsage";
	//组织机构 查询url
	public static String DEPARTMENT_SELECTALL = "com.jz.bigdata.common.department.controller.DepartmentController.selectAllDepartment";
	public static String DEPARTMENT_SELECTALL_SEC = "com.jz.bigdata.common.department.controller.DepartmentController.selectAll_Security";
	//密码过期时间
	public static String CONFIGURATION_PWD_DAY_INFO = "com.jz.bigdata.common.configuration.controller.ConfigurationController.selectPwdExpireDay";
	/**
	 * 获取全局配置信息
	 */
	public static String CONFIGURATION_INFO = "com.jz.bigdata.common.configuration.controller.ConfigurationController.selectAll";
	/**
	 * 登陆包路径
	 */
	public static String LOGINPATH="com.jz.bigdata.roleauthority.user.controller.UserController.login";
	/**
	 * 注册包路径
	 */
	public static String REGISTERPATH="com.jz.bigdata.roleauthority.user.controller.UserController.registerUser";
	
	/**
	 * 上传证书激活
	 */
	public static String UPLOADPATH="com.jz.bigdata.roleauthority.user.controller.FileUploadController.licenseUpload";
	/**
	 * 获取RSA公钥
	 */
	public static String RSAKEYPATH="java.lang.String com.jz.bigdata.common.rsa.controller.RSAController.getRSAPublicKey";

	/**
	 * 获取产品信息
	 */
	public static String PRODUCTPATH="java.lang.String com.jz.bigdata.common.product.controller.ProductController.getProductInfo";
	/**
	 * SYSLOG开启采集URL
	 */
	public static String COLLECTOR_START_SYSLOG_PATH = "java.lang.String com.jz.bigdata.business.logAnalysis.collector.controller.CollectorController.startSyslogKafkaListener";
	/**
	 * AGENT开启采集URL
	 */
	public static String COLLECTOR_START_AGENT_PATH = "java.lang.String com.jz.bigdata.business.logAnalysis.collector.controller.CollectorController.startAgentKafkaListener";
	/**
	 * filelog开启采集URL
	 */
	public static String COLLECTOR_START_FILELOG_PATH = "java.lang.String com.jz.bigdata.business.logAnalysis.collector.controller.CollectorController.startFileLogKafkaListener";
	/**
	 * 流量开启采集URL
	 */
	public static String COLLECTOR_START_PACKET_PATH = "java.lang.String com.jz.bigdata.business.logAnalysis.collector.controller.CollectorController.startPcap4jCollector";

	/**
	 * SYSLOG关闭采集URL
	 */
	public static String COLLECTOR_STOP_SYSLOG_PATH = "java.lang.String com.jz.bigdata.business.logAnalysis.collector.controller.CollectorController.stopSyslogKafkaListener";
	/**
	 * AGENT关闭采集URL
	 */
	public static String COLLECTOR_STOP_AGENT_PATH = "java.lang.String com.jz.bigdata.business.logAnalysis.collector.controller.CollectorController.stopAgentKafkaListener";
	/**
	 * filelog关闭采集URL
	 */
	public static String COLLECTOR_STOP_FILELOG_PATH = "java.lang.String com.jz.bigdata.business.logAnalysis.collector.controller.CollectorController.stopFileLogKafkaListener";
	/**
	 * 流量关闭采集URL
	 */
	public static String COLLECTOR_STOP_PACKET_PATH = "java.lang.String com.jz.bigdata.business.logAnalysis.collector.controller.CollectorController.stopPcap4jCollector";

	/**
	 * api数据接口
	 */
	public static String APIPATH="apiLog";

	/**
	 * 执行异常错误信息
	 */
	public static String EXCEPTION = "请求出错，请重试......";

	/*************sql运算符****************/
	public static final List<String> SQL_OPERATOR = new ArrayList<>();
	static{
		SQL_OPERATOR.add("=");
		SQL_OPERATOR.add(">");
		SQL_OPERATOR.add(">=");
		SQL_OPERATOR.add("<");
		SQL_OPERATOR.add("<=");
		SQL_OPERATOR.add("in");
		SQL_OPERATOR.add("not in");
		SQL_OPERATOR.add("between");
	}
	//前端combobox需要的两个字段名称
	public static final String COMBOBOX_VALUE="value";//传参内容
	public static final String COMBOBOX_LABEL="label";//显示内容
	//前端穿梭框需要的两个字段名称
	public static final String TRANSFER_KEY="key";//传参内容
	public static final String TRANSFER_LABEL="label";//显示内容

	//资产类别
	public static final String PHYSICAL_ASSET = "physical";//物理资产
	public static final String LOGICAL_ASSET = "logical";//逻辑资产
	public static final String VIRTUAL_ASSET = "virtual";//虚拟资产

	//定时任务变量
	public static final String QUARTZ_TRIGGER_GROUP="trigger_group";//触发器组名称
	public static final String QUARTZ_JOB_GROUP="job_group";//任务组名称
	//计划任务执行状态
	public static final String ALERT_STATE_RUNNING = "running";//执行中
	public static final String ALERT_STATE_STOP = "stop";//已停止


	/**
	 * @param tf 返回的success是true还是false
	 * @param icon 要显示的图标样式 0：叹号  ；1：对号；2：叉号；3：问号；4：锁   默认0
	 * @param message 要显示的信息
	 * @return {success:...,message:...,state:...}
	 * @description 信息返回方法
	 */
	private static String message(Boolean tf,int icon,String message){
		return "{\"success\":\""+(tf?"true":"false")+"\",\"message\":\""+message+"\",\"state\":\""+((icon>4||icon<0)?0:icon)+"\"}";
	}
	/**
	 * @param icon 图标样式 0：叹号  ；1：对号；2：叉号；3：问号；4：锁  默认0
	 * @param message 要显示的信息
	 * @return jso格式信息 {success:false,message:...state:...}
	 * @description 
	 */
	public static String failureMessage(int icon,String message){
		return message(false,icon,message);
	}
	
	/**
	 * @param icon 图标样式 0：叹号  ；1：对号；2：叉号；3：问号；4：锁  默认0
	 * @return
	 * @description 默认信息为：请求出错，请重试！
	 */
	public static String failureMessage(int icon){
		return message(false,icon,"请求出错，请重试！");
	}
	
	/**
	 * @param message 错误信息
	 * @return {success:false,message:...state:...}
	 * @description 默认图标是叉号
	 */
	public static String failureMessage(String message){
		return message(false,2,message);
	}

	/**
	 * 带有弹窗的失败信息
	 * @param message 失败信息
	 * @param alertInfo 弹窗信息
	 * @return
	 */
	public static String failureMessage(String message,String alertInfo){
		//弹窗信息中存在换行符会导致json对象无法识别。
		alertInfo = alertInfo.replaceAll("\\n","");
		return "{\"success\":\"false\",\"message\":\""+message+"\",\"state\":\"2\",\"alertInfo\":\""+alertInfo+"\"}";
	}
	
	/**
	 * @return {success:false,message:请求出错，请重试！,state:2}
	 * @description 
	 */
	public static String failureMessage(){
		return message(false,2,"请求出错，请重试！");
	}

	/**
	 * @param icon 图标样式 0：叹号  ；1：对号；2：叉号；3：问号；4：锁 
	 * @param message 成功信息
	 * @return {success:true,message:...,state:...}
	 * @description 
	 */
	public static String successMessage(int icon,String message){
		return message(true,icon,message);
	}
	/**
	 * @param icon 图标样式 0：叹号  ；1：对号；2：叉号；3：问号；4：锁 
	 * @return {success:true,message:操作成功！,state:...}
	 * @description 默认信息显示：操作成功
	 */
	public static String successMessage(int icon){
		return message(true,icon,"操作成功！");
	}
	/**
	 * @param message 返回信息
	 * @return {success:true,message:...,state:1}
	 * @description 默认图标为1号
	 */
	public static String successMessage(String message){
		return message(true,1,message);
	}
	/**
	 * @param data 返回数据
	 * @return {success:true,message:...,state:1}
	 * @description 默认图标为1号
	 */
	public static String successData(String data){
		return "{\"success\":\"true\",\"data\":"+data+"}";
	}
	/**
	 * @param message 信息
	 * @param message 客体信息，如资产名称，用户名称等
	 * @return {success:false,message:...,target:"客体信息，如资产名称，用户名称等"}
	 * @description
	 */
	public static String failureMessageWithTarget(String message,String target){
		return "{\"success\":\"false\",\"message\":\""+message+"\",\"target\":\""+target+"\"}";
	}
	/**
	 * @param message 信息
	 * @param message 客体信息，如资产名称，用户名称等
	 * @return {success:true,message:...,target:"客体信息，如资产名称，用户名称等"}
	 * @description
	 */
	public static String successMessageWithTarget(String message,String target){
		return "{\"success\":\"true\",\"message\":\""+message+"\",\"target\":\""+target+"\"}";
	}
	/**
	 * @return {success:true,message:操作成功！,state:1}
	 * @description 默认成功返回json信息
	 */
	public static String successMessage(){
		return message(true,1,"操作成功！");
	}
	/**
	 * @return {success:true,message:...,state:1}
	 * @description 默认图标为1号
	 */
	public static String repetitionMessage(){
		return message(false,2,"用户已存在，请重试输入！");
	}
	
	/**
	 * @return {success:true,message:...,state:1}
	 * @description 默认图标为1号
	 */
	public static String dataMessage(){
		return message(false,2,"数据已存在，请重试输入！");
	}
	
	/**
	 * @return {success:true,message:...,state:1}
	 * @description 默认图标为1号
	 */
	public static String updateUserPasswordMessage(){
		return message(false,2,"初始密码不正确，请重新输入！");
	}
	
	/**
	 * @return {success:true,message:...,state:1}
	 * @description 默认图标为1号
	 */
	public static String restoreMessage(){
		return message(false,2,"您没有备份，请先备份！");
	}
	
	/**
	 * @param message 返回登录信息
	 * @return {success:true,message:...,state:1}
	 * @description 默认图标为1号
	 */
	public static String loginMessage(Boolean bool,int num,String message){
		return message(bool,num,message);
	}
	/**
	 * @return {success:false,message:资产达到上限，无法添加！,state:2}
	 * @description 默认成功返回json信息
	 */
	public static String equipmentMaxInsertMessage(){
		return message(false,2,"资产达到上限，无法添加！");
	}
	/**
	 * @return {success:false,message:资产名,ip重复,无法添加!,state:2}
	 * @description 默认成功返回json信息
	 */
	public static String equipmentNameIpInsertMessage(){
		return message(false,2,"资产名,ip重复,无法添加！");
	}
	
}


package com.jz.bigdata.common;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yiyang
 * @date 2016年7月29日 上午9:31:24
 * @description 系统常量
 */
public class Constant {
	//批量提交数
	public static String ES_BULK_NAME = "es_bulk";
	//ES bulk提交 线程数
	public static String ES_BULK_PROCESSOR_CONCURRENT_REQUESTS_NAME = "concurrent_requests";
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
	 * api数据接口
	 */
	public static String APIPATH="apiLog";
	/**
	 * SYSLOG开启采集URL
	 */
	public static String COLLECTOR_START_SYSLOG_PATH = "java.lang.String com.jz.bigdata.business.logAnalysis.collector.controller.CollectorController.startSyslogKafkaListener";
	/**
	 * AGENT开启采集URL
	 */
	public static String COLLECTOR_START_AGENT_PATH = "java.lang.String com.jz.bigdata.business.logAnalysis.collector.controller.CollectorController.startAgentKafkaListener";
	/**
	 * SYSLOG关闭采集URL
	 */
	public static String COLLECTOR_STOP_SYSLOG_PATH = "java.lang.String com.jz.bigdata.business.logAnalysis.collector.controller.CollectorController.stopSyslogKafkaListener";
	/**
	 * AGENT关闭采集URL
	 */
	public static String COLLECTOR_STOP_AGENT_PATH = "java.lang.String com.jz.bigdata.business.logAnalysis.collector.controller.CollectorController.stopAgentKafkaListener";

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
	 * @return {success:true,message:操作成功！,state:1}
	 * @description 默认成功返回json信息
	 */
	public static String successMessage(){
		return message(true,1,"操作成功！");
	}
	/**
	 * @param message 返回信息
	 * @return {success:true,message:...,state:1}
	 * @description 默认图标为1号
	 */
	public static String repetitionMessage(){
		return message(false,2,"用户已存在，请重试输入！");
	}
	
	/**
	 * @param message 返回信息
	 * @return {success:true,message:...,state:1}
	 * @description 默认图标为1号
	 */
	public static String dataMessage(){
		return message(false,2,"数据已存在，请重试输入！");
	}
	
	/**
	 * @param message 返回信息
	 * @return {success:true,message:...,state:1}
	 * @description 默认图标为1号
	 */
	public static String updateUserPasswordMessage(){
		return message(false,2,"初始密码不正确，请重新输入！");
	}
	
	/**
	 * @param message 返回信息
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


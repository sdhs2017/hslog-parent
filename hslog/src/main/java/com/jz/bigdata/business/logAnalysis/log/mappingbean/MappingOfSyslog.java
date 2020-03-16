package com.jz.bigdata.business.logAnalysis.log.mappingbean;

import java.util.Date;

import com.jz.bigdata.util.Bean2Mapping;

/**
 * 
 * @author jiyourui
 *         服务于elasticsearch创建关于syslog类型index的template,对应的entity有syslog、winlog、DHCP、DNS、log4j、mysql
 */
public class MappingOfSyslog {

	/**
	 * id
	 */
	private String id;
	/**
	 * 日志类型
	 */
	private String hslog_type;
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
	 * 主机名（syslog日志发送过来的主机名称，可能与资产列表中的主机名不同）
	 */
	private String hostname;
	/**
	 * syslog日志PRI值或者英文字符
	 */
	private String pri;
	/**
	 * 日志模块 (facility)
	 */
	private String operation_facility;
	/**
	 * 日志级别 (Severity level)
	 */
	private String operation_level;
	/**
	 * 进程名
	 */
	private String process;
	/**
	 * pid
	 */
	private String pid;
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

	/* -------------- 针对windows 通过evtsys转发的syslog日志内容 ------------ */

	/**
	 * 事件ID
	 */
	private String eventid;
	/**
	 * 进程ID
	 */
	private String process_id;
	/**
	 * 进程名
	 */
	private String process_name;
	/**
	 * 用户域
	 */
	private String user_domain;
	/**
	 * 用户名
	 */
	private String user_name;

	/* -------------- 针对 DNS 属性 日志内容 ------------ */
	/**
	 * dns 客户IP
	 */
	private String dns_clientip;
	/**
	 * dns view
	 */
	private String dns_view;
	/**
	 * dns 域名
	 */
	private String dns_domain_name;
	/**
	 * dns 解析数据类型
	 */
	private String dns_ana_type;
	/**
	 * dns 服务器
	 */
	private String dns_server;

	/* -------------- 针对 dhcp 属性 日志内容 ------------ */
	/**
	 * dhcp类型
	 */
	private String dhcp_type;
	/**
	 * 终端mac地址
	 */
	private String client_mac;
	/**
	 * 终端设备名
	 */
	private String client_hostname;
	/**
	 * 设备地址
	 */
	private String relay_ip;
	/**
	 * 错误信息
	 */
	private String error_log;
	/**
	 * dhcp分配地址
	 */
	private String client_ip;
	/**
	 * 出现问题网段
	 */
	private String network_error;
	/**
	 * 设置index名称的后缀
	 */
	private String index_suffix;

	public String getIndex_suffix() {
		return index_suffix;
	}

	public void setIndex_suffix(String index_suffix) {
		this.index_suffix = index_suffix;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHslog_type() {
		return hslog_type;
	}

	public void setHslog_type(String hslog_type) {
		this.hslog_type = hslog_type;
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

	public String getPri() {
		return pri;
	}

	public void setPri(String pri) {
		this.pri = pri;
	}

	public String getOperation_facility() {
		return operation_facility;
	}

	public void setOperation_facility(String operation_facility) {
		this.operation_facility = operation_facility;
	}

	public String getOperation_level() {
		return operation_level;
	}

	public void setOperation_level(String operation_level) {
		this.operation_level = operation_level;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		pid = pid;
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

	public Boolean getIs_business() {
		return is_business;
	}

	public void setIs_business(Boolean is_business) {
		this.is_business = is_business;
	}

	public String getEventid() {
		return eventid;
	}

	public void setEventid(String eventid) {
		this.eventid = eventid;
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

	public String getDns_clientip() {
		return dns_clientip;
	}

	public void setDns_clientip(String dns_clientip) {
		this.dns_clientip = dns_clientip;
	}

	public String getDns_view() {
		return dns_view;
	}

	public void setDns_view(String dns_view) {
		this.dns_view = dns_view;
	}

	public String getDns_domain_name() {
		return dns_domain_name;
	}

	public void setDns_domain_name(String dns_domain_name) {
		this.dns_domain_name = dns_domain_name;
	}

	public String getDns_ana_type() {
		return dns_ana_type;
	}

	public void setDns_ana_type(String dns_ana_type) {
		this.dns_ana_type = dns_ana_type;
	}

	public String getDns_server() {
		return dns_server;
	}

	public void setDns_server(String dns_server) {
		this.dns_server = dns_server;
	}

	public String getDhcp_type() {
		return dhcp_type;
	}

	public void setDhcp_type(String dhcp_type) {
		this.dhcp_type = dhcp_type;
	}

	public String getClient_mac() {
		return client_mac;
	}

	public void setClient_mac(String client_mac) {
		this.client_mac = client_mac;
	}

	public String getClient_hostname() {
		return client_hostname;
	}

	public void setClient_hostname(String client_hostname) {
		this.client_hostname = client_hostname;
	}

	public String getRelay_ip() {
		return relay_ip;
	}

	public void setRelay_ip(String relay_ip) {
		this.relay_ip = relay_ip;
	}

	public String getError_log() {
		return error_log;
	}

	public void setError_log(String error_log) {
		this.error_log = error_log;
	}

	public String getClient_ip() {
		return client_ip;
	}

	public void setClient_ip(String client_ip) {
		this.client_ip = client_ip;
	}

	public String getNetwork_error() {
		return network_error;
	}

	public void setNetwork_error(String network_error) {
		this.network_error = network_error;
	}

	public String toMapping() {
		String template = "{\n" + "\t\t\"properties\":{\n" + "\t\t{#}\n" + "\t\t\t\t}" + "}";
		String fieldString = Bean2Mapping.getClassMapping(this);
		template = template.replace("{#}", fieldString);
		return template;
	}
	
	public static void main(String [] args) {
		System.out.println(new MappingOfSyslog().toMapping());
	}

}

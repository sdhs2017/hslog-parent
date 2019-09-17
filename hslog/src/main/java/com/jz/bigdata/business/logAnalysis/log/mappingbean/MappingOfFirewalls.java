package com.jz.bigdata.business.logAnalysis.log.mappingbean;

import java.util.Date;

import com.jz.bigdata.util.Bean2Mapping;

/**
 * 
 * @author jiyourui
 * 服务于elasticsearch创建关于防火墙类型index的template
 */
public class MappingOfFirewalls {
	
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
	 * 主机名
	 */
	private String hostname;
	/**
	 * 日志模块
	 * (facility)
	 */
	private String operation_facility;
	
	private String devid;
	
	/**
	 * 日志时间
	 */
	private String date;
	/**
	 * 设备名称
	 */
	private String dname;
	/**
	 * 日志消息类型
	 */
	private String logtype;
	/**
	 * 版本
	 */
	private String ver;
	/**
	 * 模块
	 */
	private String mod;
	/**
	 * 源IP
	 */
	private String sa;
	/**
	 * 源端口
	 */
	private String sport;
	
	private String type;
	/**
	 * 目的IP
	 */
	private String da;
	/**
	 * 目的端口
	 */
	private String dport;
	
	private String code;
	/**
	 * ip协议
	 */
	private String proto;
	/**
	 * 策略
	 */
	private String policy;
	/**
	 * 周期
	 */
	private String duration;
	/**
	 * 已接收
	 */
	private String rcvd;
	/**
	 * 已发送
	 */
	private String sent;
	/**
	 * 公开地址
	 */
	private String sata;
	private String pa;
	/**
	 * 端口
	 */
	private String saport;
	private String pport;
	
	private String fwlog;
	/**
	 * IP来源
	 */
	private String from;
	
	private String admin;
	private String act;
	private String result;
	private String user;
	private String agent;
	/**
	 * 日志消息类型
	 */
	private String dsp_msg;
	
	/**
	 * 日志级别
	 * (Severity level)
	 */
	private String operation_level;
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



	public String getOperation_facility() {
		return operation_facility;
	}



	public void setOperation_facility(String operation_facility) {
		this.operation_facility = operation_facility;
	}



	public String getDevid() {
		return devid;
	}



	public void setDevid(String devid) {
		this.devid = devid;
	}



	public String getDate() {
		return date;
	}



	public void setDate(String date) {
		this.date = date;
	}



	public String getDname() {
		return dname;
	}



	public void setDname(String dname) {
		this.dname = dname;
	}



	public String getLogtype() {
		return logtype;
	}



	public void setLogtype(String logtype) {
		this.logtype = logtype;
	}



	public String getVer() {
		return ver;
	}



	public void setVer(String ver) {
		this.ver = ver;
	}



	public String getMod() {
		return mod;
	}



	public void setMod(String mod) {
		this.mod = mod;
	}



	public String getSa() {
		return sa;
	}



	public void setSa(String sa) {
		this.sa = sa;
	}



	public String getSport() {
		return sport;
	}



	public void setSport(String sport) {
		this.sport = sport;
	}



	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public String getDa() {
		return da;
	}



	public void setDa(String da) {
		this.da = da;
	}



	public String getDport() {
		return dport;
	}



	public void setDport(String dport) {
		this.dport = dport;
	}



	public String getCode() {
		return code;
	}



	public void setCode(String code) {
		this.code = code;
	}



	public String getProto() {
		return proto;
	}



	public void setProto(String proto) {
		this.proto = proto;
	}



	public String getPolicy() {
		return policy;
	}



	public void setPolicy(String policy) {
		this.policy = policy;
	}



	public String getDuration() {
		return duration;
	}



	public void setDuration(String duration) {
		this.duration = duration;
	}



	public String getRcvd() {
		return rcvd;
	}



	public void setRcvd(String rcvd) {
		this.rcvd = rcvd;
	}



	public String getSent() {
		return sent;
	}



	public void setSent(String sent) {
		this.sent = sent;
	}



	public String getSata() {
		return sata;
	}



	public void setSata(String sata) {
		this.sata = sata;
	}



	public String getPa() {
		return pa;
	}



	public void setPa(String pa) {
		this.pa = pa;
	}



	public String getSaport() {
		return saport;
	}



	public void setSaport(String saport) {
		this.saport = saport;
	}



	public String getPport() {
		return pport;
	}



	public void setPport(String pport) {
		this.pport = pport;
	}



	public String getFwlog() {
		return fwlog;
	}



	public void setFwlog(String fwlog) {
		this.fwlog = fwlog;
	}



	public String getFrom() {
		return from;
	}



	public void setFrom(String from) {
		this.from = from;
	}



	public String getAdmin() {
		return admin;
	}



	public void setAdmin(String admin) {
		this.admin = admin;
	}



	public String getAct() {
		return act;
	}



	public void setAct(String act) {
		this.act = act;
	}



	public String getResult() {
		return result;
	}



	public void setResult(String result) {
		this.result = result;
	}



	public String getUser() {
		return user;
	}



	public void setUser(String user) {
		this.user = user;
	}



	public String getAgent() {
		return agent;
	}



	public void setAgent(String agent) {
		this.agent = agent;
	}



	public String getDsp_msg() {
		return dsp_msg;
	}



	public void setDsp_msg(String dsp_msg) {
		this.dsp_msg = dsp_msg;
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



	public String toMapping() {
		String template = "{\n" + "\t\t\"properties\":{\n" + "\t\t{#}\n" + "\t\t\t\t}" + "}";
		String fieldString = Bean2Mapping.getClassMapping(this);
		template = template.replace("{#}", fieldString);
		return template;
	}
	
	public static void main(String [] args) {
		System.out.println(new MappingOfFirewalls().toMapping());
	}

}

package com.jz.bigdata.common.assets_old.entity;

import com.jz.bigdata.roleauthority.user.entity.User;

public class Assets {
	private String id;//id
	private String name;//设备名称
	private String hostName;//主机名称
	private String type;//类型
	private String ip;//ip地址
	private String macAdress;//Mac地址
	private String createTime;//进入时间
	private String upDateTime;//更新时间
	private String endTime;//结束时间
	private int startUp;//是否启用
	private String userId;//用户id
	private int departmentId;//部门id
	private String logType;//日志类型
	private String system;//系统
	private String systemVersion;//系统版本号
	private String assetNum;//资产编号
	private String serialNum;//序列号
	private String describe;//描述
	private String valuation;//资产价值
	private String userName;//用户姓名
	private User user; //用户
	private String log_level;//日志级别
	private int high_risk;//高危
	private int moderate_risk;//中危
	private int low_risk;//低危
	private int departmentNodeId;//部门最上级id
	private String ports;
	private String protocol;
	private String state;
	private String responseDate;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getMacAdress() {
		return macAdress;
	}
	public void setMacAdress(String macAdress) {
		this.macAdress = macAdress;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getUpDateTime() {
		return upDateTime;
	}
	public void setUpDateTime(String upDateTime) {
		this.upDateTime = upDateTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public int getStartUp() {
		return startUp;
	}
	public void setStartUp(int startUp) {
		this.startUp = startUp;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}
	public String getLogType() {
		return logType;
	}
	public void setLogType(String logType) {
		this.logType = logType;
	}
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}
	public String getSystemVersion() {
		return systemVersion;
	}
	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}
	public String getAssetNum() {
		return assetNum;
	}
	public void setAssetNum(String assetNum) {
		this.assetNum = assetNum;
	}
	public String getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public String getValuation() {
		return valuation;
	}
	public void setValuation(String valuation) {
		this.valuation = valuation;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getLog_level() {
		return log_level;
	}
	public void setLog_level(String log_level) {
		this.log_level = log_level;
	}
	public int getHigh_risk() {
		return high_risk;
	}
	public void setHigh_risk(int high_risk) {
		this.high_risk = high_risk;
	}
	public int getModerate_risk() {
		return moderate_risk;
	}
	public void setModerate_risk(int moderate_risk) {
		this.moderate_risk = moderate_risk;
	}
	public int getLow_risk() {
		return low_risk;
	}
	public void setLow_risk(int low_risk) {
		this.low_risk = low_risk;
	}
	public int getDepartmentNodeId() {
		return departmentNodeId;
	}
	public void setDepartmentNodeId(int departmentNodeId) {
		this.departmentNodeId = departmentNodeId;
	}
	public String getPorts() {
		return ports;
	}
	public void setPorts(String ports) {
		this.ports = ports;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getResponseDate() {
		return responseDate;
	}
	public void setResponseDate(String responseDate) {
		this.responseDate = responseDate;
	}
	

}

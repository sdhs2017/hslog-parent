package com.jz.bigdata.common.serviceInfo.entity;

/**
 * @ClassName Service
 * @Description 
 * @Author shi cheng yu
 * @Date 2019年4月23日 上午10:08:04
 */
public class ServiceInfo {
	private String id;
	private String name;			//名称
	private String protocol;		//协议
	private String ip;				//ip
	private String port;			//端口
	private String url;				//路径
	private String relativeUrl;		//相对路径
	private String equipmentId;		//资产id	
	private int complementState;	//补全状态
	private String createTime;		//创建时间
	private String updateTime;		//修改时间
	private String stopTime;		//停用时间
	private int state;				//是否启用
	private String describe;		//描述
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
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getRelativeUrl() {
		return relativeUrl;
	}
	public void setRelativeUrl(String relativeUrl) {
		this.relativeUrl = relativeUrl;
	}
	public String getEquipmentId() {
		return equipmentId;
	}
	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}
	public int getComplementState() {
		return complementState;
	}
	public void setComplementState(int complementState) {
		this.complementState = complementState;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getStopTime() {
		return stopTime;
	}
	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	
	

}

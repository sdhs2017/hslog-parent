package com.jz.bigdata.common.event.entity;

import com.jz.bigdata.roleauthority.user.entity.User;

public class Event {
	private String id;
	private String name;//事件名称
	private String userId;//用户id
	private int state;//状态
	private String message;//描述
	private User user;
	private String actionId;//动作id
	private String actionName;//动作名称
	private String userName;//用户名称
	private int enabled;//是否启用
	private String dangerous_level;//危险级别
	
	private String month;//月
	private String day;//日
	private String hour;//小时
	private String minute;//分钟
	private String time;//时间
	private String safeStrategyName;//安全策略名称
	private String time_interval;//时间拼接
	private int event_classify;
	private String equipmentIds;
	private int deleteState;
	private int role;
	
	
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
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getActionId() {
		return actionId;
	}
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public int getEnabled() {
		return enabled;
	}
	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}
	public String getDangerous_level() {
		return dangerous_level;
	}
	public void setDangerous_level(String dangerous_level) {
		this.dangerous_level = dangerous_level;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getHour() {
		return hour;
	}
	public void setHour(String hour) {
		this.hour = hour;
	}
	public String getMinute() {
		return minute;
	}
	public void setMinute(String minute) {
		this.minute = minute;
	}
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getSafeStrategyName() {
		return safeStrategyName;
	}
	public void setSafeStrategyName(String safeStrategyName) {
		this.safeStrategyName = safeStrategyName;
	}
	public String getTime_interval() {
		return time_interval;
	}
	public void setTime_interval(String time_interval) {
		this.time_interval = time_interval;
	}
	public int getEvent_classify() {
		return event_classify;
	}
	public void setEvent_classify(int event_classify) {
		this.event_classify = event_classify;
	}
	public String getEquipmentIds() {
		return equipmentIds;
	}
	public void setEquipmentIds(String equipmentIds) {
		this.equipmentIds = equipmentIds;
	}
	public int getDeleteState() {
		return deleteState;
	}
	public void setDeleteState(int deleteState) {
		this.deleteState = deleteState;
	}
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	
	
	
}

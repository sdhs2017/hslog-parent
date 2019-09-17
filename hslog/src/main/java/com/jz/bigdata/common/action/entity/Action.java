package com.jz.bigdata.common.action.entity;

import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.users.entity.User;

public class Action {
	private String id;
	private String name;
	private String userId;
	private int state;
	private String type;
	private String feature;
	private User user;
	private Equipment equipment;
	private int deleteState;
	
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
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFeature() {
		return feature;
	}
	public void setFeature(String feature) {
		this.feature = feature;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Equipment getEquipment() {
		return equipment;
	}
	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}
	public int getDeleteState() {
		return deleteState;
	}
	public void setDeleteState(int deleteState) {
		this.deleteState = deleteState;
	}

	
}

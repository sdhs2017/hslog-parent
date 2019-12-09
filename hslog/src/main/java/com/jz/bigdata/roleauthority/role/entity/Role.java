package com.jz.bigdata.roleauthority.role.entity;

import java.util.List;


/**
 * @author yiyang
 * @date 2019-12-03
 * @description
 */
public class Role {
	
	private String id;
	private String role_name;//角色名称
	private String note;//说明

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRole_name() {
		return role_name;
	}

	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}

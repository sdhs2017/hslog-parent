package com.jz.bigdata.roleauthority.system.entity;

import java.util.List;


/**
 * @author yiyang
 * @date 2019-12-03
 * @description
 */
public class System {
	
	private String id;
	private String sys_name;//系统名称
	private String icon;//图标
	private String note;//说明

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSys_name() {
		return sys_name;
	}

	public void setSys_name(String sys_name) {
		this.sys_name = sys_name;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}

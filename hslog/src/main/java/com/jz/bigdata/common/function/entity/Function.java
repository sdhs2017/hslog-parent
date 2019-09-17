package com.jz.bigdata.common.function.entity;
/**
 * @author shichengyu
 * @date 2018年2月8日 上午11:24:40
 * @description
 */
public class Function {
	String id;
	String resource;//路径
	String describes;//描述
	int role;//角色
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public String getDescribes() {
		return describes;
	}
	public void setDescribes(String describes) {
		this.describes = describes;
	}
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}

}

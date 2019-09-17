package com.jz.bigdata.common.department.entity;

import java.util.List;

/**
 * @author shichengyu
 * @date 2017年8月4日 上午10:48:27
 * @description
 */
public class Department {
	private int id;//部门id	
	private String name;//部门名称
	private int level;//部门层级
	private Integer superiorId;//上级id
	private int subordinate;//是否有下级
	private Integer orderId;//位置排序
	private String comment;//简介
	private int departmentNodeId;//部门根目录id
	private int maxId;
	
	public List<Department> getMenus() {
		return menus;
	}
	public void setMenus(List<Department> menus) {
		this.menus = menus;
	}
	private List<Department> menus;
	
	

	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}


	public int getSubordinate() {
		return subordinate;
	}
	public void setSubordinate(int subordinate) {
		this.subordinate = subordinate;
	}
	public Integer getSuperiorId() {
		return superiorId;
	}
	public void setSuperiorId(Integer superiorId) {
		this.superiorId = superiorId;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public int getDepartmentNodeId() {
		return departmentNodeId;
	}
	public void setDepartmentNodeId(int departmentNodeId) {
		this.departmentNodeId = departmentNodeId;
	}
	public int getMaxId() {
		return maxId;
	}
	public void setMaxId(int maxId) {
		this.maxId = maxId;
	}
	


}

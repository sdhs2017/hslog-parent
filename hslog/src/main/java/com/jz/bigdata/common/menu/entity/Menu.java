package com.jz.bigdata.common.menu.entity;

import java.util.List;


/**
 * @author shichengyu
 * @date 2018年5月10日 下午5:53:49
 * @description
 */
public class Menu {
	
	private int id;
	private String menuName;//菜单名称
	private Integer superiorId;//上级id
	private int childId;//子节点
	private String url;//路径
	private String icon;//引用
	private int state;//状态
	private List<Menu> menus;
	
	
	
	public List<Menu> getMenus() {
		return menus;
	}
	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public Integer getSuperiorId() {
		return superiorId;
	}
	public void setSuperiorId(Integer superiorId) {
		this.superiorId = superiorId;
	}
	public int getChildId() {
		return childId;
	}
	public void setChildId(int childId) {
		this.childId = childId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
	
	
	
}

package com.jz.bigdata.roleauthority.menu.entity;

import java.util.List;
import java.util.Map;


/**
 * @author shichengyu
 * @date 2018年5月10日 下午5:53:49
 * @description
 */
public class Menu {
	
	private String id;
	private String menuName;//菜单名称
	private String superiorId;//上级id
	private int childId;//子节点
	private String url;//路径
	private String icon;//引用
	private String state;//状态
	private String systemName;//系统名称
	private String fk_system_id;//系统名称
	private String btn;
	private List<Menu> menus;

	public String getBtn() {
		return btn;
	}

	public void setBtn(String btn) {
		this.btn = btn;
	}

	public String getFk_system_id() {
		return fk_system_id;
	}

	public void setFk_system_id(String fk_system_id) {
		this.fk_system_id = fk_system_id;
	}

	public List<Menu> getMenus() {
		return menus;
	}
	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public String getSuperiorId() {
		return superiorId;
	}
	public void setSuperiorId(String superiorId) {
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
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getSystemName() {
		return systemName;
	}
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	
	
	
}

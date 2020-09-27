package com.jz.bigdata.common.assetGroup.entity;


import com.jz.bigdata.roleauthority.user.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @description
 */
public class AssetGroup {
	private String asset_group_id;//资产ID
	private String asset_group_name;//资产组名称
	private String asset_group_area;//区域
	private String asset_group_person;//负责人
	private String asset_group_note;//说明
	private String user_group_id;//用户组id
	private String create_time;//资产组创建时间
	private String create_user_id;//创建资产组的用户id
	private String update_time;//资产组更新时间
	private String update_user_id;//资产组更新用户id
	private String[] asset_ids;//资产组对应的资产id，多个，以逗号隔开
	private Integer pageIndex;//第N页
	private Integer pageSize;//每页的条数
	private Integer startRecord;//根据分页信息计算出的起始条数
	private List<AssetGroupRelations> asset_group_relations = new ArrayList<>();//资产组对应的多个资产信息

	public Integer getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getStartRecord() {
		return startRecord;
	}

	public void setStartRecord(Integer startRecord) {
		this.startRecord = startRecord;
	}

	public List<AssetGroupRelations> getAsset_group_relations() {
		return asset_group_relations;
	}

	public void setAsset_group_relations(List<AssetGroupRelations> asset_group_relations) {
		this.asset_group_relations = asset_group_relations;
	}

	public String[] getAsset_ids() {
		return asset_ids;
	}

	public void setAsset_ids(String[] asset_ids) {
		this.asset_ids = asset_ids;
	}

	public String getAsset_group_id() {
		return asset_group_id;
	}

	public void setAsset_group_id(String asset_group_id) {
		this.asset_group_id = asset_group_id;
	}

	public String getAsset_group_name() {
		return asset_group_name;
	}

	public void setAsset_group_name(String asset_group_name) {
		this.asset_group_name = asset_group_name;
	}

	public String getAsset_group_area() {
		return asset_group_area;
	}

	public void setAsset_group_area(String asset_group_area) {
		this.asset_group_area = asset_group_area;
	}

	public String getAsset_group_person() {
		return asset_group_person;
	}

	public void setAsset_group_person(String asset_group_person) {
		this.asset_group_person = asset_group_person;
	}

	public String getAsset_group_note() {
		return asset_group_note;
	}

	public void setAsset_group_note(String asset_group_note) {
		this.asset_group_note = asset_group_note;
	}

	public String getUser_group_id() {
		return user_group_id;
	}

	public void setUser_group_id(String user_group_id) {
		this.user_group_id = user_group_id;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getCreate_user_id() {
		return create_user_id;
	}

	public void setCreate_user_id(String create_user_id) {
		this.create_user_id = create_user_id;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getUpdate_user_id() {
		return update_user_id;
	}

	public void setUpdate_user_id(String update_user_id) {
		this.update_user_id = update_user_id;
	}
}

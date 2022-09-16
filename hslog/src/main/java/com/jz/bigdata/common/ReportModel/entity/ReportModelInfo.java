package com.jz.bigdata.common.ReportModel.entity;


import com.jz.bigdata.common.eventGroup.entity.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 报告模板信息表
 */
public class ReportModelInfo {
	private String reportModelInfoID;//唯一UUID
	private String reportModelID;//报告模板ID
	private String parentID;//父节点的ReportInfoID，用于标识子父关系
	private Integer orderCode;//排序编号。
	private String contentType;//内容类型：一级标题heading_1、二级标题heading_2、三级标题heading_3......、文本text、表格table、图片image
	private String content;//标题内容、文本内容
	private Integer bold;//是否加粗（0不加粗  1加粗） 默认0

	//内容位置0:left 居左 1:center 左右居中  2:right 居右 3:JUSTIFIED ？ 	4:top 居上 5:middle 上下居中 6:buttom 居下 7:baseline ？
	//8:JUSTIFIED_ALL ？
	//根据itext中对应类型设置
	private Integer alignment;
	private Integer fontSize;//字体大小
	private Integer state;//状态 0停用  1启用   默认启用
	private String remark;//备注说明
	private Integer spacingBefore;//段落居上XXX
	private String imageName;//图片名称
	private String tableName;//表格名称
	private int titleLevel;//标题级别

	public int getTitleLevel() {
		return titleLevel;
	}

	public void setTitleLevel(int titleLevel) {
		this.titleLevel = titleLevel;
	}
	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getReportModelInfoID() {
		return reportModelInfoID;
	}

	public void setReportModelInfoID(String reportModelInfoID) {
		this.reportModelInfoID = reportModelInfoID;
	}

	public String getReportModelID() {
		return reportModelID;
	}

	public void setReportModelID(String reportModelID) {
		this.reportModelID = reportModelID;
	}

	public String getParentID() {
		return parentID;
	}

	public void setParentID(String parentID) {
		this.parentID = parentID;
	}

	public Integer getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(Integer orderCode) {
		this.orderCode = orderCode;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getBold() {
		return bold;
	}

	public void setBold(Integer bold) {
		this.bold = bold;
	}

	public Integer getAlignment() {
		return alignment;
	}

	public void setAlignment(Integer alignment) {
		this.alignment = alignment;
	}

	public Integer getFontSize() {
		return fontSize;
	}

	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getSpacingBefore() {
		return spacingBefore;
	}

	public void setSpacingBefore(Integer spacingBefore) {
		this.spacingBefore = spacingBefore;
	}
}

package com.jz.bigdata.business.data_sec_govern.label.entity;

/**
 * @Author: yiyang
 * @Date: 2021/4/30 14:13
 * @Description: 标签管理，bean
 */
public class Label {
    private String label_id;//详细分类ID
    private String label_name;//标签名称
    private Integer label_is_custom;//是否自定义
    private Integer label_discover_way;//发现方式 0 无规则  1 按正则发现
    private Integer label_discover_percent;//确认比例（%）
    private String label_discover_regex;//正则表达式
    private String label_remark;//说明
    private Integer pageIndex;//第N页
    private Integer pageSize;//每页的条数
    private Integer startRecord;//根据分页信息计算出的起始条数

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

    public String getLabel_id() {
        return label_id;
    }

    public void setLabel_id(String label_id) {
        this.label_id = label_id;
    }

    public String getLabel_name() {
        return label_name;
    }

    public void setLabel_name(String label_name) {
        this.label_name = label_name;
    }

    public Integer getLabel_is_custom() {
        return label_is_custom;
    }

    public void setLabel_is_custom(Integer label_is_custom) {
        this.label_is_custom = label_is_custom;
    }

    public Integer getLabel_discover_way() {
        return label_discover_way;
    }

    public void setLabel_discover_way(Integer label_discover_way) {
        this.label_discover_way = label_discover_way;
    }

    public Integer getLabel_discover_percent() {
        return label_discover_percent;
    }

    public void setLabel_discover_percent(Integer label_discover_percent) {
        this.label_discover_percent = label_discover_percent;
    }

    public String getLabel_discover_regex() {
        return label_discover_regex;
    }

    public void setLabel_discover_regex(String label_discover_regex) {
        this.label_discover_regex = label_discover_regex;
    }

    public String getLabel_remark() {
        return label_remark;
    }

    public void setLabel_remark(String label_remark) {
        this.label_remark = label_remark;
    }
}

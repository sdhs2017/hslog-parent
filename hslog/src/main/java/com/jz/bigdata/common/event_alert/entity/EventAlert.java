package com.jz.bigdata.common.event_alert.entity;

/**
 * @Author: yiyang
 * @Date: 2021/2/3 10:29
 * @Description:
 */
public class EventAlert {
    private String event_alert_id;//UUID
    private String event_alert_name;//告警名称
    private String event_type;//事件类型，syslog/windows(日志)
    private String event_name;//事件名称
    private String event_name_cn;//事件名称，中文
    private String event_name_en_cn;//事件名称，英文+中文
    private int event_area;//事件连续的时间间隔
    private String event_filters;//筛选条件
    private int alert_count;//产生告警的事件count数值
    private String alert_equipment_id;//资产id
    private String alert_equipment_name;//资产名称
    private String alert_assetGroup_id;//资产组id
    private String alert_assetGroup_name;//资产组名称
    private Integer pageIndex;//第N页
    private Integer pageSize;//每页的条数
    private Integer startRecord;//根据分页信息计算出的起始条数
    private int event_area_num;//事件时间间隔数值
    private String event_area_unit;//事件时间间隔单位  秒/分钟/小时
    private String event_action_range;//告警作用范围
    private String event_action_range_name;//资产/资产组名称

    public String getEvent_action_range_name() {
        return event_action_range_name;
    }

    public void setEvent_action_range_name(String event_action_range_name) {
        this.event_action_range_name = event_action_range_name;
    }

    public String getEvent_action_range() {
        return event_action_range;
    }

    public void setEvent_action_range(String event_action_range) {
        this.event_action_range = event_action_range;
    }

    public String getEvent_alert_name() {
        return event_alert_name;
    }

    public void setEvent_alert_name(String event_alert_name) {
        this.event_alert_name = event_alert_name;
    }

    public int getEvent_area_num() {
        return event_area_num;
    }

    public void setEvent_area_num(int event_area_num) {
        this.event_area_num = event_area_num;
    }

    public String getEvent_area_unit() {
        return event_area_unit;
    }

    public void setEvent_area_unit(String event_area_unit) {
        this.event_area_unit = event_area_unit;
    }

    public String getEvent_name_en_cn() {
        return event_name_en_cn;
    }

    public void setEvent_name_en_cn(String event_name_en_cn) {
        this.event_name_en_cn = event_name_en_cn;
    }

    public String getAlert_equipment_name() {
        return alert_equipment_name;
    }

    public void setAlert_equipment_name(String alert_equipment_name) {
        this.alert_equipment_name = alert_equipment_name;
    }

    public String getAlert_assetGroup_name() {
        return alert_assetGroup_name;
    }

    public void setAlert_assetGroup_name(String alert_assetGroup_name) {
        this.alert_assetGroup_name = alert_assetGroup_name;
    }

    public String getEvent_name_cn() {
        return event_name_cn;
    }

    public void setEvent_name_cn(String event_name_cn) {
        this.event_name_cn = event_name_cn;
    }

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

    public String getEvent_alert_id() {
        return event_alert_id;
    }

    public void setEvent_alert_id(String event_alert_id) {
        this.event_alert_id = event_alert_id;
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public int getEvent_area() {
        return event_area;
    }

    public void setEvent_area(int event_area) {
        this.event_area = event_area;
    }

    public int getAlert_count() {
        return alert_count;
    }

    public void setAlert_count(int alert_count) {
        this.alert_count = alert_count;
    }

    public String getEvent_filters() {
        return event_filters;
    }

    public void setEvent_filters(String event_filters) {
        this.event_filters = event_filters;
    }



    public String getAlert_equipment_id() {
        return alert_equipment_id;
    }

    public void setAlert_equipment_id(String alert_equipment_id) {
        this.alert_equipment_id = alert_equipment_id;
    }

    public String getAlert_assetGroup_id() {
        return alert_assetGroup_id;
    }

    public void setAlert_assetGroup_id(String alert_assetGroup_id) {
        this.alert_assetGroup_id = alert_assetGroup_id;
    }
}

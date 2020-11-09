package com.jz.bigdata.common.alert.entity;

import com.hs.elsearch.entity.Bucket;
import com.hs.elsearch.entity.Filter;
import com.hs.elsearch.entity.Metric;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Alert {
    private String alert_id;
    private String alert_name;//名称
    private String alert_note;//说明
    private String pre_index_name;//索引前缀
    private String suffix_index_name;//索引后缀
    private String template_name;//索引模板
    private String alert_search_filters;//筛选条件
    private String alert_search_metric;//统计条件
    private String alert_search_bucket;//分组条件
    private String alert_conditions;//告警条件
    private String alert_cron;//执行周期
    private String alert_create_time;//创建时间
    private String alert_create_user;//创建人id
    private String alert_update_time;//更新时间
    private String alert_update_user;//更新人id
    private String alert_state;//状态 运行中，已停止（停止包含暂停和未运行）
    private boolean alert_state_boolean;//前端所需状态，启动/true  停止/false
    private String alert_structure;//前端数据结构
    private String alert_time;//数据查询范围
    private String alert_time_type;//时间范围类型 last range
    private String alert_exec_type;//执行周期类型 simple / complex(高级)
    private String alert_time_cycle_num;//执行周期数值
    private String alert_time_cycle_type;//执行周期时间类型： 秒 second   分钟  minute   小时  hour
    private Integer pageIndex;//第N页
    private Integer pageSize;//每页的条数
    private Integer startRecord;//根据分页信息计算出的起始条数
    private String alert_exec_cycle;//前端显示执行周期
    private String alert_time_range;//前端显示时间范围
    private String alert_assetGroup_id;//资产组id
    private String alert_equipment_id;//资产id
    private String fire_count;//产生告警的次数
    private List<Map<String,Object>> fire_list = new ArrayList<>();//产生告警的信息，存储告警快照信息
    private int current_page = 1;//前端使用，服务于分页控件

    public List<Map<String, Object>> getFire_list() {
        return fire_list;
    }

    public void setFire_list(List<Map<String, Object>> fire_list) {
        this.fire_list = fire_list;
    }

    public String getFire_count() {
        return fire_count;
    }

    public void setFire_count(String fire_count) {
        this.fire_count = fire_count;
    }

    public String getAlert_assetGroup_id() {
        return alert_assetGroup_id;
    }

    public void setAlert_assetGroup_id(String alert_assetGroup_id) {
        this.alert_assetGroup_id = alert_assetGroup_id;
    }

    public String getAlert_equipment_id() {
        return alert_equipment_id;
    }

    public void setAlert_equipment_id(String alert_equipment_id) {
        this.alert_equipment_id = alert_equipment_id;
    }

    public boolean isAlert_state_boolean() {
        return alert_state_boolean;
    }

    public void setAlert_state_boolean(boolean alert_state_boolean) {
        this.alert_state_boolean = alert_state_boolean;
    }

    public String getAlert_exec_cycle() {
        return alert_exec_cycle;
    }

    public void setAlert_exec_cycle(String alert_exec_cycle) {
        this.alert_exec_cycle = alert_exec_cycle;
    }

    public String getAlert_time_range() {
        return alert_time_range;
    }

    public void setAlert_time_range(String alert_time_range) {
        this.alert_time_range = alert_time_range;
    }

    public String getAlert_exec_type() {
        return alert_exec_type;
    }

    public void setAlert_exec_type(String alert_exec_type) {
        this.alert_exec_type = alert_exec_type;
    }

    public String getAlert_time_cycle_num() {
        return alert_time_cycle_num;
    }

    public void setAlert_time_cycle_num(String alert_time_cycle_num) {
        this.alert_time_cycle_num = alert_time_cycle_num;
    }

    public String getAlert_time_cycle_type() {
        return alert_time_cycle_type;
    }

    public void setAlert_time_cycle_type(String alert_time_cycle_type) {
        this.alert_time_cycle_type = alert_time_cycle_type;
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

    public String getAlert_time() {
        return alert_time;
    }

    public void setAlert_time(String alert_time) {
        this.alert_time = alert_time;
    }

    public String getAlert_time_type() {
        return alert_time_type;
    }

    public void setAlert_time_type(String alert_time_type) {
        this.alert_time_type = alert_time_type;
    }

    public String getAlert_structure() {
        return alert_structure;
    }

    public void setAlert_structure(String alert_structure) {
        this.alert_structure = alert_structure;
    }

    public String getAlert_id() {
        return alert_id;
    }

    public void setAlert_id(String alert_id) {
        this.alert_id = alert_id;
    }

    public String getAlert_name() {
        return alert_name;
    }

    public void setAlert_name(String alert_name) {
        this.alert_name = alert_name;
    }

    public String getAlert_note() {
        return alert_note;
    }

    public void setAlert_note(String alert_note) {
        this.alert_note = alert_note;
    }

    public String getPre_index_name() {
        return pre_index_name;
    }

    public void setPre_index_name(String pre_index_name) {
        this.pre_index_name = pre_index_name;
    }

    public String getSuffix_index_name() {
        return suffix_index_name;
    }

    public void setSuffix_index_name(String suffix_index_name) {
        this.suffix_index_name = suffix_index_name;
    }

    public String getTemplate_name() {
        return template_name;
    }

    public void setTemplate_name(String template_name) {
        this.template_name = template_name;
    }

    public String getAlert_search_filters() {
        return alert_search_filters;
    }

    public void setAlert_search_filters(String alert_search_filters) {
        this.alert_search_filters = alert_search_filters;
    }

    public String getAlert_search_metric() {
        return alert_search_metric;
    }

    public void setAlert_search_metric(String alert_search_metric) {
        this.alert_search_metric = alert_search_metric;
    }

    public String getAlert_search_bucket() {
        return alert_search_bucket;
    }

    public void setAlert_search_bucket(String alert_search_bucket) {
        this.alert_search_bucket = alert_search_bucket;
    }

    public String getAlert_conditions() {
        return alert_conditions;
    }

    public void setAlert_conditions(String alert_conditions) {
        this.alert_conditions = alert_conditions;
    }

    public String getAlert_cron() {
        return alert_cron;
    }

    public void setAlert_cron(String alert_cron) {
        this.alert_cron = alert_cron;
    }

    public String getAlert_create_time() {
        return alert_create_time;
    }

    public void setAlert_create_time(String alert_create_time) {
        this.alert_create_time = alert_create_time;
    }

    public String getAlert_create_user() {
        return alert_create_user;
    }

    public void setAlert_create_user(String alert_create_user) {
        this.alert_create_user = alert_create_user;
    }

    public String getAlert_update_time() {
        return alert_update_time;
    }

    public void setAlert_update_time(String alert_update_time) {
        this.alert_update_time = alert_update_time;
    }

    public String getAlert_update_user() {
        return alert_update_user;
    }

    public void setAlert_update_user(String alert_update_user) {
        this.alert_update_user = alert_update_user;
    }

    public String getAlert_state() {
        return alert_state;
    }

    public void setAlert_state(String alert_state) {
        this.alert_state = alert_state;
    }
}

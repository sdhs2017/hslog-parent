package com.jz.bigdata.common.alert.entity;

import com.jz.bigdata.util.Bean2Mapping;

import java.util.Date;

public class AlertSnapshot {
    private String alert_id;//告警id
    private Date start_time;//起始时间
    private Date end_time;//截至时间
    private Date run_time;//执行时间
    private String result;//告警执行结果
    private String restful_api;//执行时restful-api 快照
    private String alert_search_filters;//筛选条件
    private String alert_search_metric;//统计条件
    private String alert_search_bucket;//分组条件
    private String alert_conditions;//告警条件
    private String alert_name;//名称
    private String alert_note;//说明
    private String pre_index_name;//索引前缀
    private String suffix_index_name;//索引后缀
    private String template_name;//索引模板
    private String alert_cron;//执行周期
    private String alert_time;//数据查询范围
    private String alert_time_type;//时间范围类型 last range
    private String alert_exec_type;//执行周期类型 simple / complex(高级)
    private String alert_time_cycle_num;//执行周期数值
    private String alert_time_cycle_type;//执行周期时间类型： 秒 second   分钟  minute   小时  hour
    private Boolean alert_fire;//该条结果是否产生了告警  true/false
    private String match_result;//匹配结果，仅存储匹配告警条件的结果信息
    private String match_size;//匹配结果的数据条数

    public String getMatch_size() {
        return match_size;
    }

    public void setMatch_size(String match_size) {
        this.match_size = match_size;
    }

    public String getMatch_result() {
        return match_result;
    }

    public void setMatch_result(String match_result) {
        this.match_result = match_result;
    }

    public Date getRun_time() {
        return run_time;
    }

    public void setRun_time(Date run_time) {
        this.run_time = run_time;
    }

    public Boolean getAlert_fire() {
        return alert_fire;
    }

    public void setAlert_fire(Boolean alert_fire) {
        this.alert_fire = alert_fire;
    }

    public String getAlert_id() {
        return alert_id;
    }

    public void setAlert_id(String alert_id) {
        this.alert_id = alert_id;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRestful_api() {
        return restful_api;
    }

    public void setRestful_api(String restful_api) {
        this.restful_api = restful_api;
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

    public String getAlert_cron() {
        return alert_cron;
    }

    public void setAlert_cron(String alert_cron) {
        this.alert_cron = alert_cron;
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

    public String toMapping() {
        //String template = "\"visualization\":{\n" + "\t\t\"properties\":{\n" + "\t\t{#}\n" + "\t\t\t\t}" + "}";
        String template = "{\n" + "\t\t\"properties\":{\n" + "\t\t{#}\n" + "\t\t\t\t}" + "}";
        String fieldString = Bean2Mapping.getClassMapping(this);
        template = template.replace("{#}", fieldString);
        return template;
    }
    public static void main(String[] args){
        System.out.println(new AlertSnapshot().toMapping());
    }
}

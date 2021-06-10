package com.jz.bigdata.common.data_source.entity;

/**
 * @Author: yiyang
 * @Date: 2021/2/3 10:29
 * @Description: 数据源bean
 */
public class DataSource {
    private String data_source_id;//数据源唯一ID
    private String data_source_name;//数据源名称
    private String data_source_item;//数据源类型
    private String data_source_item_type;//数据源子分类
    private String data_source_ip;//数据源IP地址
    private String data_source_port;//数据源端口
    private String data_source_username;//数据源用户名
    private String data_source_password;//数据源密码
    private String data_source_dbname;//数据库/实例
    private String data_source_create_time;//创建时间
    private String data_source_update_time;//更新时间
    private Integer data_source_sample_num;//样本数
    private Integer pageIndex;//第N页
    private Integer pageSize;//每页的条数
    private Integer startRecord;//根据分页信息计算出的起始条数
    private String description;//描述
    private Integer data_source_is_initialized;//是否已经初始化  0:未初始化 1：已完成初始化 2:正在初始化 3：初始化失败
    private String data_source_is_initialized_label;//是否已经初始化 文字
    private String data_source_init_time;//数据源初始化完成时间
    private Integer data_source_discovery_state;//数据发现状态 0；未开始   1：敏感数据发现中...   2：敏感数据发现完成
    private String data_source_discovery_state_label;//数据发现状态 文字

    public Integer getData_source_sample_num() {
        return data_source_sample_num;
    }

    public void setData_source_sample_num(Integer data_source_sample_num) {
        this.data_source_sample_num = data_source_sample_num;
    }

    public String getData_source_is_initialized_label() {
        return data_source_is_initialized_label;
    }

    public void setData_source_is_initialized_label(String data_source_is_initialized_label) {
        this.data_source_is_initialized_label = data_source_is_initialized_label;
    }

    public String getData_source_discovery_state_label() {
        return data_source_discovery_state_label;
    }

    public void setData_source_discovery_state_label(String data_source_discovery_state_label) {
        this.data_source_discovery_state_label = data_source_discovery_state_label;
    }

    public Integer getData_source_discovery_state() {
        return data_source_discovery_state;
    }

    public void setData_source_discovery_state(Integer data_source_discovery_state) {
        this.data_source_discovery_state = data_source_discovery_state;
    }

    public Integer getData_source_is_initialized() {
        return data_source_is_initialized;
    }

    public void setData_source_is_initialized(Integer data_source_is_initialized) {
        this.data_source_is_initialized = data_source_is_initialized;
    }

    public String getData_source_init_time() {
        return data_source_init_time;
    }

    public void setData_source_init_time(String data_source_init_time) {
        this.data_source_init_time = data_source_init_time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getData_source_id() {
        return data_source_id;
    }

    public void setData_source_id(String data_source_id) {
        this.data_source_id = data_source_id;
    }

    public String getData_source_name() {
        return data_source_name;
    }

    public void setData_source_name(String data_source_name) {
        this.data_source_name = data_source_name;
    }

    public String getData_source_item() {
        return data_source_item;
    }

    public void setData_source_item(String data_source_item) {
        this.data_source_item = data_source_item;
    }

    public String getData_source_item_type() {
        return data_source_item_type;
    }

    public void setData_source_item_type(String data_source_item_type) {
        this.data_source_item_type = data_source_item_type;
    }

    public String getData_source_ip() {
        return data_source_ip;
    }

    public void setData_source_ip(String data_source_ip) {
        this.data_source_ip = data_source_ip;
    }

    public String getData_source_port() {
        return data_source_port;
    }

    public void setData_source_port(String data_source_port) {
        this.data_source_port = data_source_port;
    }

    public String getData_source_username() {
        return data_source_username;
    }

    public void setData_source_username(String data_source_username) {
        this.data_source_username = data_source_username;
    }

    public String getData_source_password() {
        return data_source_password;
    }

    public void setData_source_password(String data_source_password) {
        this.data_source_password = data_source_password;
    }

    public String getData_source_dbname() {
        return data_source_dbname;
    }

    public void setData_source_dbname(String data_source_dbname) {
        this.data_source_dbname = data_source_dbname;
    }

    public String getData_source_create_time() {
        return data_source_create_time;
    }

    public void setData_source_create_time(String data_source_create_time) {
        this.data_source_create_time = data_source_create_time;
    }

    public String getData_source_update_time() {
        return data_source_update_time;
    }

    public void setData_source_update_time(String data_source_update_time) {
        this.data_source_update_time = data_source_update_time;
    }
}

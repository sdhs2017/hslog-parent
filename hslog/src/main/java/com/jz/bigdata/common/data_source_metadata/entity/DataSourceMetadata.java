package com.jz.bigdata.common.data_source_metadata.entity;

/**
 * @Author: yiyang
 * @Date: 2021/3/1 10:51
 * @Description: 数据源对应的metadata
 */
public class DataSourceMetadata {
    private String metadata_id;//元数据id
    private String data_source_id;//数据源外键id
    private String metadata_type;//类型：数据库/表/字段
    private String metadata_database;//数据库
    private String metadata_table;//表
    private String metadata_table_type;//表格分类
    private String metadata_field;//字段
    private String metadata_field_type;//字段类型
    private String metadata_sensitiveLevel;//敏感级别
    private String metadata_remark;//描述信息
    private Integer pageIndex;//第N页
    private Integer pageSize;//每页的条数
    private Integer startRecord;//根据分页信息计算出的起始条数

    public String getMetadata_table_type() {
        return metadata_table_type;
    }

    public void setMetadata_table_type(String metadata_table_type) {
        this.metadata_table_type = metadata_table_type;
    }

    public String getMetadata_id() {
        return metadata_id;
    }

    public void setMetadata_id(String metadata_id) {
        this.metadata_id = metadata_id;
    }

    public String getData_source_id() {
        return data_source_id;
    }

    public void setData_source_id(String data_source_id) {
        this.data_source_id = data_source_id;
    }

    public String getMetadata_type() {
        return metadata_type;
    }

    public void setMetadata_type(String metadata_type) {
        this.metadata_type = metadata_type;
    }

    public String getMetadata_database() {
        return metadata_database;
    }

    public void setMetadata_database(String metadata_database) {
        this.metadata_database = metadata_database;
    }

    public String getMetadata_table() {
        return metadata_table;
    }

    public void setMetadata_table(String metadata_table) {
        this.metadata_table = metadata_table;
    }

    public String getMetadata_field() {
        return metadata_field;
    }

    public void setMetadata_field(String metadata_field) {
        this.metadata_field = metadata_field;
    }

    public String getMetadata_field_type() {
        return metadata_field_type;
    }

    public void setMetadata_field_type(String metadata_field_type) {
        this.metadata_field_type = metadata_field_type;
    }

    public String getMetadata_sensitiveLevel() {
        return metadata_sensitiveLevel;
    }

    public void setMetadata_sensitiveLevel(String metadata_sensitiveLevel) {
        this.metadata_sensitiveLevel = metadata_sensitiveLevel;
    }

    public String getMetadata_remark() {
        return metadata_remark;
    }

    public void setMetadata_remark(String metadata_remark) {
        this.metadata_remark = metadata_remark;
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
}

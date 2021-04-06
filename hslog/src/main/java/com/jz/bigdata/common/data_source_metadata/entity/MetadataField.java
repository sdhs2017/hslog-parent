package com.jz.bigdata.common.data_source_metadata.entity;

/**
 * @Author: yiyang
 * @Date: 2021/3/10 16:07
 * @Description: 元数据，描述“字段”的字段信息
 */
public class MetadataField {
    private String metadata_field_id;//字段唯一ID
    private String data_source_id;//数据源ID
    private String metadata_database_name;//数据库名称
    private String metadata_table_name;//表名
    private String metadata_field_name;//字段名称
    private String metadata_field_type;//字段类型
    private String metadata_field_length;//字段长度
    private String metadata_field_isnull;//是否为空
    private String metadata_field_comment;//字段注释
    private String metadata_field_sensitiveLevel;//字段敏感级别
    private String metadata_remark;//描述信息
    private String metadata_identify_ids;//元数据标识id，多个标识
    private String metadata_identify_names;//元数据标识名称，多个标识

    public String getMetadata_identify_names() {
        return metadata_identify_names;
    }

    public void setMetadata_identify_names(String metadata_identify_names) {
        this.metadata_identify_names = metadata_identify_names;
    }

    public String getMetadata_identify_ids() {
        return metadata_identify_ids;
    }

    public void setMetadata_identify_ids(String metadata_identify_ids) {
        this.metadata_identify_ids = metadata_identify_ids;
    }

    public String getMetadata_field_sensitiveLevel() {
        return metadata_field_sensitiveLevel;
    }

    public void setMetadata_field_sensitiveLevel(String metadata_field_sensitiveLevel) {
        this.metadata_field_sensitiveLevel = metadata_field_sensitiveLevel;
    }

    public String getMetadata_field_id() {
        return metadata_field_id;
    }

    public void setMetadata_field_id(String metadata_field_id) {
        this.metadata_field_id = metadata_field_id;
    }

    public String getData_source_id() {
        return data_source_id;
    }

    public void setData_source_id(String data_source_id) {
        this.data_source_id = data_source_id;
    }

    public String getMetadata_field_length() {
        return metadata_field_length;
    }

    public void setMetadata_field_length(String metadata_field_length) {
        this.metadata_field_length = metadata_field_length;
    }

    public String getMetadata_database_name() {
        return metadata_database_name;
    }

    public void setMetadata_database_name(String metadata_database_name) {
        this.metadata_database_name = metadata_database_name;
    }

    public String getMetadata_table_name() {
        return metadata_table_name;
    }

    public void setMetadata_table_name(String metadata_table_name) {
        this.metadata_table_name = metadata_table_name;
    }

    public String getMetadata_field_name() {
        return metadata_field_name;
    }

    public void setMetadata_field_name(String metadata_field_name) {
        this.metadata_field_name = metadata_field_name;
    }

    public String getMetadata_field_type() {
        return metadata_field_type;
    }

    public void setMetadata_field_type(String metadata_field_type) {
        this.metadata_field_type = metadata_field_type;
    }

    public String getMetadata_field_isnull() {
        return metadata_field_isnull;
    }

    public void setMetadata_field_isnull(String metadata_field_isnull) {
        this.metadata_field_isnull = metadata_field_isnull;
    }

    public String getMetadata_field_comment() {
        return metadata_field_comment;
    }

    public void setMetadata_field_comment(String metadata_field_comment) {
        this.metadata_field_comment = metadata_field_comment;
    }

    public String getMetadata_remark() {
        return metadata_remark;
    }

    public void setMetadata_remark(String metadata_remark) {
        this.metadata_remark = metadata_remark;
    }
}

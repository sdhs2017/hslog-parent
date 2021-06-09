package com.jz.bigdata.common.data_source_metadata.entity;

/**
 * @Author: yiyang
 * @Date: 2021/5/6 14:01
 * @Description: 数据库抽样数据持久化 bean 纵表
 */
public class DataSourceSample {
    private String data_source_id;//数据源id
    private String database_name;//数据库名
    private String table_name;//表名
    private String field_name;//字段名
    private String field_content;//字段内容

    public String getData_source_id() {
        return data_source_id;
    }

    public void setData_source_id(String data_source_id) {
        this.data_source_id = data_source_id;
    }

    public String getDatabase_name() {
        return database_name;
    }

    public void setDatabase_name(String database_name) {
        this.database_name = database_name;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getField_name() {
        return field_name;
    }

    public void setField_name(String field_name) {
        this.field_name = field_name;
    }

    public String getField_content() {
        return field_content;
    }

    public void setField_content(String field_content) {
        this.field_content = field_content;
    }
}

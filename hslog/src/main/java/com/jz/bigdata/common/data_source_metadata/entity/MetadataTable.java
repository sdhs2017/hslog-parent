package com.jz.bigdata.common.data_source_metadata.entity;

/**
 * @Author: yiyang
 * @Date: 2021/3/10 16:07
 * @Description:元数据，描述表格的字段信息
 */
public class MetadataTable {
    private String metadata_table_id;//表唯一ID
    private String data_source_id;//数据源ID
    private String metadata_database_name;//数据库名称
    private String metadata_table_name;//表名
    private String metadata_table_type;//表类型，主体表，参考表等
    private String metadata_remark;//描述信息

    public String getMetadata_table_id() {
        return metadata_table_id;
    }

    public void setMetadata_table_id(String metadata_table_id) {
        this.metadata_table_id = metadata_table_id;
    }

    public String getData_source_id() {
        return data_source_id;
    }

    public void setData_source_id(String data_source_id) {
        this.data_source_id = data_source_id;
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

    public String getMetadata_table_type() {
        return metadata_table_type;
    }

    public void setMetadata_table_type(String metadata_table_type) {
        this.metadata_table_type = metadata_table_type;
    }

    public String getMetadata_remark() {
        return metadata_remark;
    }

    public void setMetadata_remark(String metadata_remark) {
        this.metadata_remark = metadata_remark;
    }
}

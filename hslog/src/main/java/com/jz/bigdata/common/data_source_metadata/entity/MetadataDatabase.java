package com.jz.bigdata.common.data_source_metadata.entity;

import java.util.List;

/**
 * @Author: yiyang
 * @Date: 2021/3/10 16:06
 * @Description: 元数据，描述数据库的字段信息
 */
public class MetadataDatabase {
    private String metadata_database_id;//数据库唯一ID
    private String data_source_id;//数据源ID
    private String metadata_database_name;//数据库名称
    private String metadata_remark;//对数据库的描述信息
    private String metadata_is_auto_discovery;//是否自动发现（正则匹配）

    public String getMetadata_is_auto_discovery() {
        return metadata_is_auto_discovery;
    }

    public void setMetadata_is_auto_discovery(String metadata_is_auto_discovery) {
        this.metadata_is_auto_discovery = metadata_is_auto_discovery;
    }

    public String getMetadata_database_id() {
        return metadata_database_id;
    }

    public void setMetadata_database_id(String metadata_database_id) {
        this.metadata_database_id = metadata_database_id;
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

    public String getMetadata_remark() {
        return metadata_remark;
    }

    public void setMetadata_remark(String metadata_remark) {
        this.metadata_remark = metadata_remark;
    }
}

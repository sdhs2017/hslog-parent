package com.jz.bigdata.business.data_sec_govern.label.entity;

/**
 * @Author: yiyang
 * @Date: 2021/5/10 14:16
 * @Description: 元数据字段与label标签的关系
 */
public class FieldLabelRelation {
    private String data_source_id;//数据源id
    private String metadata_field_id;//字段id
    private String dsg_label_id;//标签id
    private String dsg_label_name;//标签名称

    public String getData_source_id() {
        return data_source_id;
    }

    public void setData_source_id(String data_source_id) {
        this.data_source_id = data_source_id;
    }

    public String getMetadata_field_id() {
        return metadata_field_id;
    }

    public void setMetadata_field_id(String metadata_field_id) {
        this.metadata_field_id = metadata_field_id;
    }

    public String getDsg_label_id() {
        return dsg_label_id;
    }

    public void setDsg_label_id(String dsg_label_id) {
        this.dsg_label_id = dsg_label_id;
    }

    public String getDsg_label_name() {
        return dsg_label_name;
    }

    public void setDsg_label_name(String dsg_label_name) {
        this.dsg_label_name = dsg_label_name;
    }
}

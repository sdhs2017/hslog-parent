package com.jz.bigdata.common.data_source_metadata_identify.entity;

/**
 * @Author: yiyang
 * @Date: 2021/3/23 14:49
 * @Description: 数据源管理，标识分类-基础类别涉及的属性
 */
public class MetadataIdentifyBasic {
    private String identify_basic_id;//基础分类id
    private String identify_basic_name;//基础分类名称
    private String identify_basic_custom;//是否自定义
    private String identify_basic_remark;//基础分类说明

    public String getIdentify_basic_id() {
        return identify_basic_id;
    }

    public void setIdentify_basic_id(String identify_basic_id) {
        this.identify_basic_id = identify_basic_id;
    }

    public String getIdentify_basic_name() {
        return identify_basic_name;
    }

    public void setIdentify_basic_name(String identify_basic_name) {
        this.identify_basic_name = identify_basic_name;
    }

    public String getIdentify_basic_custom() {
        return identify_basic_custom;
    }

    public void setIdentify_basic_custom(String identify_basic_custom) {
        this.identify_basic_custom = identify_basic_custom;
    }

    public String getIdentify_basic_remark() {
        return identify_basic_remark;
    }

    public void setIdentify_basic_remark(String identify_basic_remark) {
        this.identify_basic_remark = identify_basic_remark;
    }
}

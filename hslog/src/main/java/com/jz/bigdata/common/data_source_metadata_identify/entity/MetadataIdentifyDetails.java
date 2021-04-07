package com.jz.bigdata.common.data_source_metadata_identify.entity;

/**
 * @Author: yiyang
 * @Date: 2021/3/23 14:49
 * @Description:
 */
public class MetadataIdentifyDetails {
    private String identify_details_id;//详细分类ID
    private String identify_basic_id;//基础分类ID
    private String identify_details_name;//详细分类名称
    private String identify_details_superior;//父节点ID
    private String identify_details_custom;//是否自定义
    private String identify_details_sensitive;//敏感级别
    private String identify_details_regex;//正则匹配
    private String identify_details_remark;//说明
    private final boolean hasChildren = true;//前端需要，不确定节点是否有下级，默认都可以展开

    public boolean getHasChildren() {
        return hasChildren;
    }

    public String getIdentify_details_id() {
        return identify_details_id;
    }

    public void setIdentify_details_id(String identify_details_id) {
        this.identify_details_id = identify_details_id;
    }

    public String getIdentify_basic_id() {
        return identify_basic_id;
    }

    public void setIdentify_basic_id(String identify_basic_id) {
        this.identify_basic_id = identify_basic_id;
    }

    public String getIdentify_details_name() {
        return identify_details_name;
    }

    public void setIdentify_details_name(String identify_details_name) {
        this.identify_details_name = identify_details_name;
    }

    public String getIdentify_details_superior() {
        return identify_details_superior;
    }

    public void setIdentify_details_superior(String identify_details_superior) {
        this.identify_details_superior = identify_details_superior;
    }

    public String getIdentify_details_custom() {
        return identify_details_custom;
    }

    public void setIdentify_details_custom(String identify_details_custom) {
        this.identify_details_custom = identify_details_custom;
    }

    public String getIdentify_details_sensitive() {
        return identify_details_sensitive;
    }

    public void setIdentify_details_sensitive(String identify_details_sensitive) {
        this.identify_details_sensitive = identify_details_sensitive;
    }

    public String getIdentify_details_regex() {
        return identify_details_regex;
    }

    public void setIdentify_details_regex(String identify_details_regex) {
        this.identify_details_regex = identify_details_regex;
    }

    public String getIdentify_details_remark() {
        return identify_details_remark;
    }

    public void setIdentify_details_remark(String identify_details_remark) {
        this.identify_details_remark = identify_details_remark;
    }
}

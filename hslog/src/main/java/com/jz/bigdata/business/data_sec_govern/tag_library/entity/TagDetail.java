package com.jz.bigdata.business.data_sec_govern.tag_library.entity;

/**
 * @Author: yiyang
 * @Date: 2021/4/29 17:07
 * @Description: 数据安全治理-标签库-细分类（基础分类下的详细分类）
 */
public class TagDetail {
    private String tag_detail_id;//详细分类ID
    private String tag_basic_id;//基础分类ID
    private String tag_detail_name;//标签名称
    private String tag_detail_superior;//父节点ID
    private Integer tag_detail_is_custom;//是否自定义
    private Integer tag_detail_discover_way;//发现方式 0 无规则  1 按正则发现
    private Integer tag_detail_discover_percent;//确认比例（%）
    private String tag_detail_discover_regex;//正则表达式
    private String tag_detail_remark;//说明
    private final boolean hasChildren = true;//前端需要，不确定节点是否有下级，默认都可以展开

    public String getTag_detail_id() {
        return tag_detail_id;
    }

    public void setTag_detail_id(String tag_detail_id) {
        this.tag_detail_id = tag_detail_id;
    }

    public String getTag_basic_id() {
        return tag_basic_id;
    }

    public void setTag_basic_id(String tag_basic_id) {
        this.tag_basic_id = tag_basic_id;
    }

    public String getTag_detail_name() {
        return tag_detail_name;
    }

    public void setTag_detail_name(String tag_detail_name) {
        this.tag_detail_name = tag_detail_name;
    }

    public String getTag_detail_superior() {
        return tag_detail_superior;
    }

    public void setTag_detail_superior(String tag_detail_superior) {
        this.tag_detail_superior = tag_detail_superior;
    }

    public int getTag_detail_is_custom() {
        return tag_detail_is_custom;
    }

    public void setTag_detail_is_custom(int tag_detail_is_custom) {
        this.tag_detail_is_custom = tag_detail_is_custom;
    }

    public int getTag_detail_discover_way() {
        return tag_detail_discover_way;
    }

    public void setTag_detail_discover_way(int tag_detail_discover_way) {
        this.tag_detail_discover_way = tag_detail_discover_way;
    }

    public int getTag_detail_discover_percent() {
        return tag_detail_discover_percent;
    }

    public void setTag_detail_discover_percent(int tag_detail_discover_percent) {
        this.tag_detail_discover_percent = tag_detail_discover_percent;
    }

    public String getTag_detail_discover_regex() {
        return tag_detail_discover_regex;
    }

    public void setTag_detail_discover_regex(String tag_detail_discover_regex) {
        this.tag_detail_discover_regex = tag_detail_discover_regex;
    }

    public String getTag_detail_remark() {
        return tag_detail_remark;
    }

    public void setTag_detail_remark(String tag_detail_remark) {
        this.tag_detail_remark = tag_detail_remark;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }
}

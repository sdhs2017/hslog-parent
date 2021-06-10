package com.jz.bigdata.business.data_sec_govern.tag_library.entity;

/**
 * @Author: yiyang
 * @Date: 2021/4/30 9:21
 * @Description: 标签库基础分类，作为标签页面的左侧菜单
 */
public class TagBasic {
    private String tag_basic_id;//基础分类id
    private String tag_basic_name;//基础分类名称
    private int tag_basic_custom;//是否自定义
    private String tag_basic_remark;//基础分类说明

    public String getTag_basic_id() {
        return tag_basic_id;
    }

    public void setTag_basic_id(String tag_basic_id) {
        this.tag_basic_id = tag_basic_id;
    }

    public String getTag_basic_name() {
        return tag_basic_name;
    }

    public void setTag_basic_name(String tag_basic_name) {
        this.tag_basic_name = tag_basic_name;
    }

    public int getTag_basic_custom() {
        return tag_basic_custom;
    }

    public void setTag_basic_custom(int tag_basic_custom) {
        this.tag_basic_custom = tag_basic_custom;
    }

    public String getTag_basic_remark() {
        return tag_basic_remark;
    }

    public void setTag_basic_remark(String tag_basic_remark) {
        this.tag_basic_remark = tag_basic_remark;
    }
}

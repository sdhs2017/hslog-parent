package com.jz.bigdata.common.businessIntelligence.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jz.bigdata.util.Bean2Mapping;

/**
 * 图表数据bean
 */
public class Visualization {
    private String id;//报表的id
    private String index_id;//写入es后，es自动生成的id
    private String title;//标题
    private String description;//描述
    private String type;//图表类型 //TODO 使用枚举类定义图表类型
    private String index_name;//数据查询的索引名称
    private String template_name;//template名称
    private String pre_index_name;//index名称前缀
    private String suffix_index_name;//index名称后缀
    private String option;//图表结构
    private String params;//查询参数
    private String data;//查询结果
    private Boolean editable;//是否可编辑，默认可编辑
    private Boolean deletable;//是否可删除，默认可删除
    private String group_name;//分组
    private String source_type;//数据源类型，通过模板（template） 或 自定义index(index)

    public String getSource_type() {
        return source_type;
    }

    public void setSource_type(String source_type) {
        this.source_type = source_type;
    }

    public String getIndex_id() {
        return index_id;
    }

    public void setIndex_id(String index_id) {
        this.index_id = index_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public Boolean getDeletable() {
        return deletable;
    }

    public void setDeletable(Boolean deletable) {
        this.deletable = deletable;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public String getIndex_name() {
        return index_name;
    }

    public void setIndex_name(String index_name) {
        this.index_name = index_name;
    }

    public String getTemplate_name() {
        return template_name;
    }

    public void setTemplate_name(String template_name) {
        this.template_name = template_name;
    }

    public String getPre_index_name() {
        return pre_index_name;
    }

    public void setPre_index_name(String pre_index_name) {
        this.pre_index_name = pre_index_name;
    }

    public String getSuffix_index_name() {
        return suffix_index_name;
    }

    public void setSuffix_index_name(String suffix_index_name) {
        this.suffix_index_name = suffix_index_name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String toMapping() {
        String template = "\"visualization\":{\n" + "\t\t\"properties\":{\n" + "\t\t{#}\n" + "\t\t\t\t}" + "}";
        String fieldString = Bean2Mapping.getClassMapping(this);
        template = template.replace("{#}", fieldString);
        return template;
    }

    public static void main(String [] args) {
        System.out.println(new Visualization().toMapping());
    }

}

package com.jz.bigdata.common.businessIntelligence.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jz.bigdata.util.Bean2Mapping;

/**
 * 图表数据bean
 */
public class Visualization {
    private String id;//index _id
    private String title;//标题
    private String description;//描述
    private String type;//图表类型 //TODO 使用枚举类定义图表类型
    private String indexName;//数据查询的索引名称
    private String option;//图表结构
    private String params;//查询参数
    private String data;//查询结果

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

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
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

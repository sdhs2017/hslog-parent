package com.jz.bigdata.common.metadata.entity;

import java.util.List;

/**
 * 表头信息，通过后端生成，前端重新加载表头格式
 */
public class TableHeader {
    //列名，与数据对应的字段信息，如“字段名/fieldName”：prop为fieldName label为“字段名”
    private String prop;
    //列名 汉字
    private String label;
    //格式化，前端对数据还需要进行二次处理，使显示更友好
    private String formatter;
    //宽度
    private Integer width;
    //复合表头
    private List<TableHeader> children;

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFormatter() {
        return formatter;
    }

    public void setFormatter(String formatter) {
        this.formatter = formatter;
    }

    public List<TableHeader> getChildren() {
        return children;
    }

    public void setChildren(List<TableHeader> children) {
        this.children = children;
    }
    //构造方法实现初始化，
    public TableHeader(String prop,String label,String formatter,Integer width,List<TableHeader> children ){
        this.prop = prop;
        this.label = label;
        this.formatter = formatter;
        this.width = width;
        this.children = children;
    }
    public TableHeader(){};
}

package com.jz.bigdata.common.businessIntelligence.entity;

/**
 * sql查询列，及排序
 */
public class SqlSearchColumn {
    private String column_value;//字段（数据库字段）
    private String column_label;//字段名（汉字）
    private String sort;

    public String getColumn_value() {
        return column_value;
    }

    public void setColumn_value(String column_value) {
        this.column_value = column_value;
    }

    public String getColumn_label() {
        return column_label;
    }

    public void setColumn_label(String column_label) {
        this.column_label = column_label;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}

package com.hs.elsearch.entity;

/**
 * X轴bucket聚合涉及到的字段
 */
public class Bucket{
    private String aggType;//x轴聚合方式
    private String field;//x轴聚合字段
    private Integer size = 10;//查询结果条数，默认10条
    private String sort;//排序，正序/倒序
    private String intervalType;//间隔类型（单位）
    private Integer intervalValue;//间隔值
    private String label;//别名，暂不使用
    public Bucket(){}
    public Bucket(String aggType, String field, String intervalType, Integer intervalValue, Integer size, String sort){
        this.aggType = aggType;
        this.field = field;
        this.intervalType = intervalType;
        this.intervalValue = intervalValue;
        this.size = size;
        this.sort = sort;
    }
    public String getAggType() {
        return aggType;
    }

    public void setAggType(String aggType) {
        this.aggType = aggType;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(String intervalType) {
        this.intervalType = intervalType;
    }

    public int getIntervalValue() {
        return intervalValue;
    }

    public void setIntervalValue(int intervalValue) {
        this.intervalValue = intervalValue;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
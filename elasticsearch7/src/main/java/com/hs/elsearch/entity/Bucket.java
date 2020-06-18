package com.hs.elsearch.entity;

import java.util.LinkedList;
import java.util.Map;

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
    private LinkedList<Map<String,Object>> ranges = new LinkedList<>();//范围list，[{from: 0, to: 1000}, {from: 1000, to: 2000}]
    public Bucket(){}

    /**
     * 初始化，带range范围
     * @param aggType
     * @param field
     * @param intervalType
     * @param intervalValue
     * @param size
     * @param sort
     * @param ranges
     */
    public Bucket(String aggType, String field, String intervalType, Integer intervalValue, Integer size, String sort,LinkedList<Map<String,Object>> ranges){
        this.aggType = aggType;
        this.field = field;
        this.intervalType = intervalType;
        this.intervalValue = intervalValue;
        this.size = size;
        this.sort = sort;
        this.ranges = ranges;
    }

    /**
     * 初始化，不带range范围
     * @param aggType
     * @param field
     * @param intervalType
     * @param intervalValue
     * @param size
     * @param sort
     */
    public Bucket(String aggType, String field, String intervalType, Integer intervalValue, Integer size, String sort){
        this.aggType = aggType;
        this.field = field;
        this.intervalType = intervalType;
        this.intervalValue = intervalValue;
        this.size = size;
        this.sort = sort;
        this.ranges = ranges;
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

    public LinkedList<Map<String, Object>> getRanges() {
        return ranges;
    }

    public void setRanges(LinkedList<Map<String, Object>> ranges) {
        this.ranges = ranges;
    }
}
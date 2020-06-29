package com.hs.elsearch.entity;

/**
 * Y轴，metric指标涉及到的字段
 */
public class Metric{
    private String aggType;//y轴聚合方式
    private String field;//y轴聚合字段
    private String aliasName;//别名,如果是null，则图例中无聚合名称，“”则显示默认的聚合（count/sum等）
    public Metric(){}
    //构造方法
    public Metric(String aggType, String field,String aliasName){
        this.aggType = aggType;
        this.field = field;
        this.aliasName = aliasName;
    }
    public String getAggType() {
        return aggType;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
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
}
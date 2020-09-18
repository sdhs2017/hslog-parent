package com.jz.bigdata.common.businessIntelligence.entity;

/**
 * sql查询的where条件
 */
public class SqlSearchWhere {
    private String field;//列
    private String operator;//运算符
    private String value;//值
    private String start;//起始值
    private String end;//截至值
    private String[] values;//数组值

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }
}

package com.hs.elsearch.entity;

/**
 * 可视化模块查询条件 filters
 */
public class Filter {
    private String field;//字段名
    private String fieldType;//字段类型
    private String operator;//操作，is/is not/between/ is one of等
    private String value;//单个值 用于is/is not /
    private String[] values;//多个值 用于 is one of /is not one of
    private String start;//范围值 开始 用于 is between/is not between
    private String end;//范围值 截止
    private boolean enable;//filter是否启用
    private boolean label_status;//别名 是否启用
    private String label;//别名

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

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

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
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

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isLabel_status() {
        return label_status;
    }

    public void setLabel_status(boolean label_status) {
        this.label_status = label_status;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

package com.hs.elsearch.util;

public class MappingField {
    //mapping的列名
    private String fieldName;
    //将来可能要设置的别名
    private String aliasName;
    //字段类型
    private String fieldType;
    //字段可聚合
    private boolean fieldData;
    //字段是否可排序
    private boolean sortable;
    //是否是fields下的字段，例如：ip为一个字段，ip下有个fields，然后定义了keyword，可以用ip.keyword访问
    private boolean isFields;

    public boolean getIsFields() {
        return isFields;
    }

    public void setIsFields(boolean fields) {
        isFields = fields;
    }

    public boolean getSortable() {
        return sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public boolean getFieldData() {
        return fieldData;
    }

    public void setFieldData(boolean fieldData) {
        this.fieldData = fieldData;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }
}

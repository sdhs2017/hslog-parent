package com.jz.bigdata.common.businessIntelligence.entity;

public class MappingField {
    //mapping的列名
    private String fieldName;
    //将来可能要设置的别名
    private String aliasName;
    //字段类型
    private String fieldType;
    //字段可聚合
    private boolean fieldData;

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

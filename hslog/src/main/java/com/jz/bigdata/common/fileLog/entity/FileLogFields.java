package com.jz.bigdata.common.fileLog.entity;

/**
 * @Author: yiyang
 * @Date: 2021/1/6 16:34
 * @Description: filebeat接收数据的字段信息
 */
public class FileLogFields {
    private String fileLog_Name;//模板名称
    private String fileLog_key;//模板key（英文，为index的一部分）
    private String fileLog_field;//字段field
    private String fileLog_text;//字段中文名称（前端显示）
    private String fileLog_type;//字段类型（ES）
    private String fileLog_format;//date类型需要的format信息
    private String fileLog_is_timestamp;//是否为日期字段
    private int fileLog_order;//排序字段，cvs数据顺序一一对应

    public int getFileLog_order() {
        return fileLog_order;
    }

    public void setFileLog_order(int fileLog_order) {
        this.fileLog_order = fileLog_order;
    }

    public String getFileLog_Name() {
        return fileLog_Name;
    }

    public void setFileLog_Name(String fileLog_Name) {
        this.fileLog_Name = fileLog_Name;
    }

    public String getFileLog_key() {
        return fileLog_key;
    }

    public void setFileLog_key(String fileLog_key) {
        this.fileLog_key = fileLog_key;
    }

    public String getFileLog_field() {
        return fileLog_field;
    }

    public void setFileLog_field(String fileLog_field) {
        this.fileLog_field = fileLog_field;
    }

    public String getFileLog_text() {
        return fileLog_text;
    }

    public void setFileLog_text(String fileLog_text) {
        this.fileLog_text = fileLog_text;
    }

    public String getFileLog_type() {
        return fileLog_type;
    }

    public void setFileLog_type(String fileLog_type) {
        this.fileLog_type = fileLog_type;
    }

    public String getFileLog_format() {
        return fileLog_format;
    }

    public void setFileLog_format(String fileLog_format) {
        this.fileLog_format = fileLog_format;
    }

    public String getFileLog_is_timestamp() {
        return fileLog_is_timestamp;
    }

    public void setFileLog_is_timestamp(String fileLog_is_timestamp) {
        this.fileLog_is_timestamp = fileLog_is_timestamp;
    }
}

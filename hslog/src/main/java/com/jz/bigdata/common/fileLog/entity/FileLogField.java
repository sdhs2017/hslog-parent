package com.jz.bigdata.common.fileLog.entity;

/**
 * @Author: yiyang
 * @Date: 2021/1/6 16:34
 * @Description: filebeat接收数据的字段信息
 */
public class FileLogField {
    private String file_log_templateName;//模板名称
    private String file_log_templateKey;//模板key（英文，为index的一部分）
    private String file_log_field;//字段field
    private String file_log_text;//字段中文名称（前端显示）
    private String file_log_type;//字段类型（ES）
    private String file_log_format;//date类型需要的format信息
    private String file_log_is_timestamp;//是否为日期字段
    private int file_log_order;//排序字段，cvs数据顺序一一对应

    public String getFile_log_templateName() {
        return file_log_templateName;
    }

    public void setFile_log_templateName(String file_log_templateName) {
        this.file_log_templateName = file_log_templateName;
    }

    public String getFile_log_templateKey() {
        return file_log_templateKey;
    }

    public void setFile_log_templateKey(String file_log_templateKey) {
        this.file_log_templateKey = file_log_templateKey;
    }

    public String getFile_log_field() {
        return file_log_field;
    }

    public void setFile_log_field(String file_log_field) {
        this.file_log_field = file_log_field;
    }

    public String getFile_log_text() {
        return file_log_text;
    }

    public void setFile_log_text(String file_log_text) {
        this.file_log_text = file_log_text;
    }

    public String getFile_log_type() {
        return file_log_type;
    }

    public void setFile_log_type(String file_log_type) {
        this.file_log_type = file_log_type;
    }

    public String getFile_log_format() {
        return file_log_format;
    }

    public void setFile_log_format(String file_log_format) {
        this.file_log_format = file_log_format;
    }

    public String getFile_log_is_timestamp() {
        return file_log_is_timestamp;
    }

    public void setFile_log_is_timestamp(String file_log_is_timestamp) {
        this.file_log_is_timestamp = file_log_is_timestamp;
    }

    public int getFile_log_order() {
        return file_log_order;
    }

    public void setFile_log_order(int file_log_order) {
        this.file_log_order = file_log_order;
    }
}

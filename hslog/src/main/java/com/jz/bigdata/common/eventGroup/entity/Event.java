package com.jz.bigdata.common.eventGroup.entity;

public class Event {
    private Integer event_id;//事件id
    private String event_name_en;//事件名称（英文）
    private String event_name_cn;//事件名称（中文）
    private String event_type;//事件类型，syslog/winlog


    public Integer getEvent_id() {
        return event_id;
    }

    public void setEvent_id(Integer event_id) {
        this.event_id = event_id;
    }

    public String getEvent_name_en() {
        return event_name_en;
    }

    public void setEvent_name_en(String event_name_en) {
        this.event_name_en = event_name_en;
    }

    public String getEvent_name_cn() {
        return event_name_cn;
    }

    public void setEvent_name_cn(String event_name_cn) {
        this.event_name_cn = event_name_cn;
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }
}

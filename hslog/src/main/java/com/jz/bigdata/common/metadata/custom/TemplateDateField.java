package com.jz.bigdata.common.metadata.custom;

import java.util.HashMap;
import java.util.Map;

/**
 * 定义template与日期字段的对饮关系
 */
public enum TemplateDateField {
    INSTANCE;
    public final static Map<String, String> TemplateDateField = new HashMap<>();
    static{
        TemplateDateField.put("auditbeat-", "@timestamp");
        TemplateDateField.put("filebeat-", "@timestamp");
        TemplateDateField.put("hslog_packet", "logdate");
        TemplateDateField.put("hslog_syslog", "logdate");
        TemplateDateField.put("metricbeat-", "@timestamp");
        TemplateDateField.put("packetbeat-", "@timestamp");
        TemplateDateField.put("winlogbeat-", "@timestamp");

    }
}

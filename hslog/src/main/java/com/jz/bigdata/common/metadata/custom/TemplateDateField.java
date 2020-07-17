package com.jz.bigdata.common.metadata.custom;

import java.util.HashMap;
import java.util.Map;

/**
 * 定义template与日期字段的对饮关系
 */
public enum TemplateDateField {
    INSTANCE;
    //
    public final static Map<String, String> TemplateDateField = new HashMap<>();
    static{
        TemplateDateField.put("auditbeat-", "@timestamp");
        TemplateDateField.put("filebeat-", "@timestamp");
        TemplateDateField.put("hslog_packet", "logdate");
        TemplateDateField.put("hslog_syslog", "logdate");
        TemplateDateField.put("metricbeat-", "@timestamp");
        TemplateDateField.put("packetbeat-", "@timestamp");
        TemplateDateField.put("winlogbeat-", "@timestamp");
        TemplateDateField.put("hslog_test", "logdate");
    }

    /**
     * 使用中
     * 通过index名称获取日期字段
     * @param indexName
     * @return
     */
    public static String getDateField(String indexName){
        //以hslog开头的index，日期字段都是logdate
        if(indexName.indexOf("hslog")==0){
            return "logdate";
        }else{
            //其他情况都暂定为@timestamp
            return "@timestamp";
        }
    }
}

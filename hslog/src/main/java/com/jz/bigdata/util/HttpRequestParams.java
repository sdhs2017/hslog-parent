package com.jz.bigdata.util;

import java.util.Map;

/**
 * 请求参数bean
 */
public class HttpRequestParams {
    private String startTime;//起始时间
    private String endTime;//截止时间
    private String groupField;//聚合字段
    private Map<String,String> queryMap;//query参数

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Map<String, String> getQueryMap() {
        return queryMap;
    }

    public void setQueryMap(Map<String, String> queryMap) {
        this.queryMap = queryMap;
    }

    public String getGroupField() {
        return groupField;
    }

    public void setGroupField(String groupField) {
        this.groupField = groupField;
    }
}

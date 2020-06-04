package com.hs.elsearch.entity;

import com.hs.elsearch.util.HSDateUtil;

import java.util.Map;

/**
 * 请求参数bean
 */
public class HttpRequestParams {
    private String startTime;//起始时间
    private String endTime;//截止时间
    private String timeField;//时间范围涉及的字段
    private String groupField;//聚合字段

    private int intervalValue;//时间间隔的值
    private String intervalType;//间隔单位（seconds、minutes、hours）
    private Map<String,String> queryMap;//query参数
    private boolean hasTimeArea;//判断是否有时间参数
    private String[] indices;//索引

    public int getIntervalValue() {
        return intervalValue;
    }

    public void setIntervalValue(int intervalValue) {
        this.intervalValue = intervalValue;
    }

    public String getTimeField() {
        return timeField;
    }

    public void setTimeField(String timeField) {
        this.timeField = timeField;
    }

    public String[] getIndices() {
        return indices;
    }

    public void setIndices(String[] indices) {
        //如果有时间范围，对indices进行处理，这是对查询的一种优化
        if(null!=this.startTime&&"".equals(this.startTime)&&null!=this.endTime&&"".equals(this.endTime)){
            this.indices = HSDateUtil.dateArea2Indices(this.startTime,this.endTime,indices);
        }else{
            this.indices = indices;
        }

    }

    public boolean getHasTimeArea() {
        return hasTimeArea;
    }

    public void setHasTimeArea(boolean hasTimeArea) {
        this.hasTimeArea = hasTimeArea;
    }

    public String getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(String intervalType) {
        this.intervalType = intervalType;
    }



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

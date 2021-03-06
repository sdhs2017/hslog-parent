package com.hs.elsearch.entity;

import org.apache.commons.beanutils.BeanUtils;

import java.util.ArrayList;
import java.util.Map;

/**
 * 图表查询时的参数，用于统一管理维护
 */
public class VisualParam {
    private String x_agg;//x轴聚合方式
    private String x_field;//x轴聚合字段
    private String y_agg;//y轴聚合方式
    private String y_field;//y轴聚合字段
    private int size = 10;//查询结果条数，默认10条
    private String sort;//排序，正序/倒序
    private String intervalType;//间隔类型（单位）
    private int intervalValue;//间隔值
    private String index_name;//数据源：目前是索引，后期可能会调整
    private String pre_index_name;//索引前缀
    private String suffix_index_name;//索引后缀
    private String template_name;//
    private String startTime;
    private String endTime;
    private String dateField;//时间范围查询时对应的字段
    private Map<String,String> queryParam;//查询条件 查询框和时间范围
    private String errorInfo;//异常信息提示，用于在参数处理时产生异常时进行判定
    private ArrayList<Bucket> bucketList = new ArrayList<>();//聚合字段（X轴）
    private ArrayList<Metric> metricList = new ArrayList<>();//指标字段（Y轴）
    private ArrayList<QueryCondition> queryConditions = new ArrayList<>();//查询条件
    private String queryConnectionType;//查询条件的连接类型  should/must
    private String unit;//单位，数据换算

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getQueryConnectionType() {
        return queryConnectionType;
    }

    public void setQueryConnectionType(String queryConnectionType) {
        this.queryConnectionType = queryConnectionType;
    }

    public ArrayList<QueryCondition> getQueryConditions() {
        return queryConditions;
    }

    public void setQueryConditions(ArrayList<QueryCondition> queryConditions) {
        this.queryConditions = queryConditions;
    }

    public ArrayList<Bucket> getBucketList() {
        return bucketList;
    }

    public void setBucketList(ArrayList<Bucket> bucketList) {
        this.bucketList = bucketList;
    }

    public ArrayList<Metric> getMetricList() {
        return metricList;
    }

    public void setMetricList(ArrayList<Metric> metricList) {
        this.metricList = metricList;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
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

    public Map<String, String> getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(Map<String, String> queryParam) {
        this.queryParam = queryParam;
    }

    public String getIndex_name() {
        return index_name;
    }

    public void setIndex_name(String index_name) {
        this.index_name = index_name;
    }

    public String getPre_index_name() {
        return pre_index_name;
    }

    public void setPre_index_name(String pre_index_name) {
        this.pre_index_name = pre_index_name;
    }

    public String getSuffix_index_name() {
        return suffix_index_name;
    }

    public void setSuffix_index_name(String suffix_index_name) {
        this.suffix_index_name = suffix_index_name;
    }

    public String getTemplate_name() {
        return template_name;
    }

    public void setTemplate_name(String template_name) {
        this.template_name = template_name;
    }

    public String getDateField() {
        return dateField;
    }

    public void setDateField(String dateField) {
        this.dateField = dateField;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(String intervalType) {
        this.intervalType = intervalType;
    }

    public int getIntervalValue() {
        return intervalValue;
    }

    public void setIntervalValue(int intervalValue) {
        this.intervalValue = intervalValue;
    }

    public String getX_agg() {
        return x_agg;
    }

    public void setX_agg(String x_agg) {
        this.x_agg = x_agg;
    }

    public String getX_field() {
        return x_field;
    }

    public void setX_field(String x_field) {
        this.x_field = x_field;
    }

    public String getY_agg() {
        return y_agg;
    }

    public void setY_agg(String y_agg) {
        this.y_agg = y_agg;
    }

    public String getY_field() {
        return y_field;
    }

    public void setY_field(String y_field) {
        this.y_field = y_field;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    /**
     *  map转bean
     * @param map 参数map
     * @return
     */
    public VisualParam mapToBean(Map<String,String[]> map){
        try{
            //其他参数处理
            BeanUtils.populate(this,map);
        }catch(Exception e){
            //e.printStackTrace();
        }
        return this;
    }

    /**
     * index和日期字段初始化
     * @param dateField 日期字段
     * @param index_name 索引名称
     * @return
     */
    public VisualParam initDateFieldAndIndex(String dateField,String index_name){
        this.dateField = dateField;
        this.index_name = index_name;
        return this;
    }

}

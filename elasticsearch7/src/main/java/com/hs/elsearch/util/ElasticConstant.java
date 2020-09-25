package com.hs.elsearch.util;

import java.util.*;

/**
 * Elastic 后端涉及到的全局变量定义
 */
public class ElasticConstant {
    /**
     * BIDao层数据组装涉及到的系统常量
     */
    /**
     * 返回数据中的X轴别名
     */
    public static final String XAXIS = "xAxis";
    /**
     * 返回数据图例
     */
    public static final String DIMENSIONS = "dimensions";
    /**
     * 真实数据
     */
    public static final String SOURCE = "source";
    /**
     * 当使用metric报表，仅定义多个metric，无bucket时，需要在聚合的外层套一个聚合，该聚合的名称定义为elastic_metric
     */
    public static final String METRIC_AGG_NAME = "elastic_metric";

    /**
     * 可视化 图表保存在es中的字段名
     */
    public static final String HSDATA_VISUAL = "visualization";
    /**
     * 可视化 dashboard保存在es中的字段名
     */
    public static final String HSDATA_DASHBOARD = "dashboard";

    /**
     * sort type ，可排序字段类型
     */
    public static final Set<String> SORT_TYPES = new HashSet<>();
    static{
        SORT_TYPES.add("keyword");
        SORT_TYPES.add("date");
        SORT_TYPES.add("ip");
        SORT_TYPES.add("boolean");
        SORT_TYPES.add("long");
        SORT_TYPES.add("integer");
        SORT_TYPES.add("short");
        SORT_TYPES.add("byte");
        SORT_TYPES.add("double");
        SORT_TYPES.add("float");
        SORT_TYPES.add("half_float");
        SORT_TYPES.add("scaled_float");
    }
    /**
     * filter operator
     */
    public static final String OP_IS_MATCH = "is match";//match_phrase 查询
    public static final String OP_IS_NOT_MATCH = "is not match";
    public static final String OP_IS_TERM = "is term";// term 查询
    public static final String OP_IS_NOT_TERM = "is not term";
    public static final String OP_IS_ONE_OF_MATCH = "is one of match";
    public static final String OP_IS_NOT_ONE_OF_MATCH = "is not one of match";
    public static final String OP_IS_ONE_OF_TERM = "is one of term";
    public static final String OP_IS_NOT_ONE_OF_TERM = "is not one of term";
    public static final String OP_IS_BETWEEN = "is between";
    public static final String OP_IS_NOT_BETWEEN = "is not between";
    public static final String OP_EXISTS = "exists";
    public static final String OP_DOES_NOT_EXISTS = "does not exists";
    /**
     * index pattern field typ
     */
    public static final String INDEX_PATTERN_TYPE_NUMBER = "number";
    public static final String INDEX_PATTERN_TYPE_DATE = "date";
    public static final String INDEX_PATTERN_TYPE_STRING = "string";
    public static final String INDEX_PATTERN_TYPE_IP = "ip";
    public static final String INDEX_PATTERN_TYPE_BOOLEAN = "boolean";
    public static final String INDEX_PATTERN_TYPE_GEO_POINT = "geo_point";

    /**
     * 定义filter operator与field type的对应关系
     */
    //TODO 枚举
    public static final Map<String, List<String>> fieldTypeOperator = new HashMap<>();
    private static final List<String> numberDateIpOperator = new ArrayList<>();
    private static final List<String> DateOperator = new ArrayList<>();
    private static final List<String> stringOperator = new ArrayList<>();
    private static final List<String> booleanOperator = new ArrayList<>();

    static{
        //number date ip的operator相同
        numberDateIpOperator.add(OP_IS_MATCH);
        numberDateIpOperator.add(OP_IS_NOT_MATCH);
        //numberDateIpOperator.add(OP_IS_TERM);
        //numberDateIpOperator.add(OP_IS_NOT_TERM);
        numberDateIpOperator.add(OP_IS_ONE_OF_MATCH);
        numberDateIpOperator.add(OP_IS_NOT_ONE_OF_MATCH);
        //numberDateIpOperator.add(OP_IS_ONE_OF_TERM);
        //numberDateIpOperator.add(OP_IS_NOT_ONE_OF_TERM);
        numberDateIpOperator.add(OP_IS_BETWEEN);
        numberDateIpOperator.add(OP_IS_NOT_BETWEEN);
        numberDateIpOperator.add(OP_EXISTS);
        numberDateIpOperator.add(OP_DOES_NOT_EXISTS);
        // date 目前做特殊处理
        DateOperator.add(OP_IS_BETWEEN);
        DateOperator.add(OP_IS_NOT_BETWEEN);
        DateOperator.add(OP_EXISTS);
        DateOperator.add(OP_DOES_NOT_EXISTS);
        //string
        stringOperator.add(OP_IS_MATCH);
        stringOperator.add(OP_IS_NOT_MATCH);
        //stringOperator.add(OP_IS_TERM);
        //stringOperator.add(OP_IS_NOT_TERM);
        stringOperator.add(OP_IS_ONE_OF_MATCH);
        stringOperator.add(OP_IS_NOT_ONE_OF_MATCH);
        //stringOperator.add(OP_IS_ONE_OF_TERM);
        //stringOperator.add(OP_IS_NOT_ONE_OF_TERM);
        stringOperator.add(OP_EXISTS);
        stringOperator.add(OP_DOES_NOT_EXISTS);
        //boolean
        booleanOperator.add(OP_IS_MATCH);
        booleanOperator.add(OP_IS_NOT_MATCH);
        //booleanOperator.add(OP_IS_TERM);
        //booleanOperator.add(OP_IS_NOT_TERM);
        booleanOperator.add(OP_EXISTS);
        booleanOperator.add(OP_DOES_NOT_EXISTS);
        //number类型
        fieldTypeOperator.put("number",numberDateIpOperator);
        //date类型
        fieldTypeOperator.put("date",DateOperator);
        //ip类型
        fieldTypeOperator.put("ip",numberDateIpOperator);
        //string   （text、keyword）
        fieldTypeOperator.put("string",stringOperator);
        //boolean
        fieldTypeOperator.put("boolean",booleanOperator);
    }
    //字段类型合并机制，参考kibana index pattern
    public static final Map<String,String> IndexPatternType = new HashMap<>();
    static{
        IndexPatternType.put("text",INDEX_PATTERN_TYPE_STRING);
        IndexPatternType.put("keyword",INDEX_PATTERN_TYPE_STRING);
        IndexPatternType.put("long",INDEX_PATTERN_TYPE_NUMBER);
        IndexPatternType.put("integer",INDEX_PATTERN_TYPE_NUMBER);
        IndexPatternType.put("short",INDEX_PATTERN_TYPE_NUMBER);
        IndexPatternType.put("byte",INDEX_PATTERN_TYPE_NUMBER);
        IndexPatternType.put("double",INDEX_PATTERN_TYPE_NUMBER);
        IndexPatternType.put("float",INDEX_PATTERN_TYPE_NUMBER);
        IndexPatternType.put("half_float",INDEX_PATTERN_TYPE_NUMBER);
        IndexPatternType.put("scaled_float",INDEX_PATTERN_TYPE_NUMBER);
        IndexPatternType.put("ip",INDEX_PATTERN_TYPE_IP);
        IndexPatternType.put("geo_point",INDEX_PATTERN_TYPE_GEO_POINT);
        IndexPatternType.put("boolean",INDEX_PATTERN_TYPE_BOOLEAN);
        IndexPatternType.put("date",INDEX_PATTERN_TYPE_DATE);
    }
}

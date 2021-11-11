package com.hs.elsearch.util;

import java.util.*;

/**
 * Elastic 后端涉及到的全局变量定义
 */
public class ElasticConstant {
    /**
     * 字段类型分类
     */
    public static final String ES_FIELDS_TYPE_NUMBER = "Number";//数字
    public static final String ES_FIELDS_TYPE_NUMBER_OR_DATE = "NumberOrDate";//数字或日期
    public static final String ES_FIELDS_TYPE_TERMS = "Terms";//可
    public static final String ES_FIELDS_TYPE_DATE = "Date";//日期
    public static final String ES_FIELDS_TYPE_IP = "Ip";//IP
    public static final String ES_FIELDS_TYPE_ALL_EXCEPT_GEO = "AllExceptGeo";//除了GEO外的类型
    public static final String ES_FIELDS_TYPE_ALL = "All";//所有字段
    public static final String ES_FIELDS_TYPE_SOURCE = "Source";//可使用_source获取字段的，去除了fields下的字段（常见的如keyword）
    /*
    数据源类型
     */
    //通过模板选择数据源
    public static final String BI_SOURCE_TYPE_TEMPLATE = "template";
    //通过自定义index选择数据源
    public static final String BI_SOURCE_TYPE_INDEX = "index";

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

    /**
     * 定义alert告警条件中的operator
     */
    public static final String ALERT_OP_GT = ">";
    public static final String ALERT_OP_GTE = ">=";
    public static final String ALERT_OP_LT = "<";
    public static final String ALERT_OP_LTE = "<=";
    public static final String ALERT_OP_IS = "=";
    public static final String ALERT_OP_IS_NOT = "!=";
    public static final String ALERT_OP_IS_MATCH = "is match";
    public static final String ALERT_OP_IS_NOT_MATCH = "is not match";
    public static final String ALERT_OP_IS_ONE_OF = "is one of";
    public static final String ALERT_OP_IS_NOT_ONE_OF = "is not one of";

    public static final Map<String, List<String>> fieldTypeOperator_alert = new HashMap<>();
    private static final List<String> numberDateIpOperator_alert = new ArrayList<>();
    private static final List<String> DateOperator_alert = new ArrayList<>();
    private static final List<String> stringOperator_alert = new ArrayList<>();
    private static final List<String> booleanOperator_alert = new ArrayList<>();
    static{
        //数值类型
        numberDateIpOperator_alert.add(ALERT_OP_GT);
        numberDateIpOperator_alert.add(ALERT_OP_GTE);
        numberDateIpOperator_alert.add(ALERT_OP_LT);
        numberDateIpOperator_alert.add(ALERT_OP_LTE);
        numberDateIpOperator_alert.add(ALERT_OP_IS);
        numberDateIpOperator_alert.add(ALERT_OP_IS_NOT);
        numberDateIpOperator_alert.add(ALERT_OP_IS_ONE_OF);
        numberDateIpOperator_alert.add(ALERT_OP_IS_NOT_ONE_OF);
        //日期类型
        DateOperator_alert.add(ALERT_OP_GT);
        DateOperator_alert.add(ALERT_OP_GTE);
        DateOperator_alert.add(ALERT_OP_LT);
        DateOperator_alert.add(ALERT_OP_LTE);
        DateOperator_alert.add(ALERT_OP_IS);
        DateOperator_alert.add(ALERT_OP_IS_NOT);
//        DateOperator_alert.add(ALERT_OP_IS_ONE_OF);
//        DateOperator_alert.add(ALERT_OP_IS_NOT_ONE_OF);
        //字符串
        stringOperator_alert.add(ALERT_OP_IS);
        stringOperator_alert.add(ALERT_OP_IS_NOT);
        stringOperator_alert.add(ALERT_OP_IS_ONE_OF);
        stringOperator_alert.add(ALERT_OP_IS_NOT_ONE_OF);
        stringOperator_alert.add(ALERT_OP_IS_MATCH);
        stringOperator_alert.add(ALERT_OP_IS_NOT_MATCH);
        //bool
        booleanOperator_alert.add(ALERT_OP_IS);

        //number类型
        fieldTypeOperator_alert.put("number",numberDateIpOperator_alert);
        //date类型
        fieldTypeOperator_alert.put("date",DateOperator_alert);
        //ip类型
        fieldTypeOperator_alert.put("ip",numberDateIpOperator_alert);
        //string   （text、keyword）
        fieldTypeOperator_alert.put("string",stringOperator_alert);
        //boolean
        fieldTypeOperator_alert.put("boolean",booleanOperator_alert);
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

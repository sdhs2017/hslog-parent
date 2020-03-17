package com.jz.bigdata.common.metadata.entity;

import scala.Int;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 元数据统一化处理
 * 基于不同的数据源，其元数据会有所不同，但是需要一个统一的bean进行存储和显示
 */
public class Metadata {
    //数据源的名称，可以是mysql的表名，可以是ES的template的名称，也可以是ES的index的名称
    private String sourceName;
    //数据源的类别，MySql、Elastic。后续可以根据需要增加如mongoDB等
    private SourceTypes sourceType;
    //使用枚举类型定义
    public enum SourceTypes{
        MYSQL,ELASTIC
    }
    //列名,从ES或数据库取出的metadata的field的值，例如：domain_url、id、ip等
    private String fieldName;
    //别名，对于英文的列名，通常会用文字说明含义
    private String fieldAlisName;
    //数据类型
    private FieldType fieldType;
    public enum FieldType{
        //索引全文值的字段，对这些字段进行分析，也就是说在索引之前，会通过分词器（analyzer）转换成单个术语的列表。允许ES进行单个术语的检索。
        // 不会用于排序，很少用于聚合。如果需要为结构化内容编制索引，需要使用keyword类型
        TEXT,
        //索引结构化内容的字段，它们通常用于筛选、排序和聚合。该字段只能按照确切值进行搜索，同样，如果需要对全文进行索引，需要使用text类型
        KEYWORD,
        //IP地址（例192.168.0.1）
        IP,
        //时间，支持不同格式的时间，如Timestamp、”yyyy-mm-dd HH:mm:ss”等
        DATE,
        //32-bit integer
        INTEGER,
        //经纬度
        GEO_POINT,
        //布尔值
        BOOLEAN,
        //64-bit integer
        LONG,
        //未知类型
        UNKNOWN;

        private static final Map<String, FieldType> map;
        static {
            //将枚举转化成一个静态map，key为小写枚举值，value为枚举值
            map = Arrays.stream(values())
                    .collect(Collectors.toMap(e -> e.name().toLowerCase(), e -> e));
        }
        //向外部提供一个静态方法，传递一个参数获取对象的枚举值
        //匹配忽略大小写
        public static FieldType fromStr(String type) {
            return Optional.ofNullable(map.get(type.toLowerCase())).orElse(UNKNOWN);
        }
        public Map<String, FieldType> getMap(){
            return null;
        }
    }
    //对FieldType的文字说明
    private String fieldTypeName;
    //字段 A.raw的数据类型
    private RawType RawType;
    public enum RawType{
        KEYWORD,
        UNKNOWN;
        private static final Map<String, RawType> map;
        static {
            //将枚举转化成一个静态map，key为小写枚举值，value为枚举值
            map = Arrays.stream(values())
                    .collect(Collectors.toMap(e -> e.name().toLowerCase(), e -> e));
        }
        //向外部提供一个静态方法，传递一个参数获取对象的枚举值
        //匹配忽略大小写
        public static RawType fromStr(String type) {
            return Optional.ofNullable(map.get(type.toLowerCase())).orElse(UNKNOWN);
        }
    }
    //超过设置长度的字符串将不会被索引或存储。
    private Integer rawIgnoreAbove;
    //数据入库时候分词建立索引
    private Analyzer analyzer;
    public enum Analyzer{
        INDEX_ANSJ,
        UNKNOWN;
        private static final Map<String, Analyzer> map;
        static {
            //将枚举转化成一个静态map，key为小写枚举值，value为枚举值
            map = Arrays.stream(values())
                    .collect(Collectors.toMap(e -> e.name().toLowerCase(), e -> e));
        }
        //向外部提供一个静态方法，传递一个参数获取对象的枚举值
        //匹配忽略大小写
        public static Analyzer fromStr(String type) {
            return Optional.ofNullable(map.get(type.toLowerCase())).orElse(UNKNOWN);
        }
    }
    //数据查询时，query的内容使用search_analyzer分词
    private SearchAnalyzer searchAnalyzer;
    public enum SearchAnalyzer{
        QUERY_ANSJ,
        UNKNOWN;
        private static final Map<String, SearchAnalyzer> map;
        static {
            //将枚举转化成一个静态map，key为小写枚举值，value为枚举值
            map = Arrays.stream(values())
                    .collect(Collectors.toMap(e -> e.name().toLowerCase(), e -> e));
        }
        //向外部提供一个静态方法，传递一个参数获取对象的枚举值
        //匹配忽略大小写
        public static SearchAnalyzer fromStr(String type) {
            return Optional.ofNullable(map.get(type.toLowerCase())).orElse(UNKNOWN);
        }
    }
    //字段是否可聚合
    private boolean fieldData;
    //出现在type:date时，设置日期的格式
    private String format;

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public SourceTypes getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceTypes sourceType) {
        this.sourceType = sourceType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldAlisName() {
        return fieldAlisName;
    }

    public void setFieldAlisName(String fieldAlisName) {
        this.fieldAlisName = fieldAlisName;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldTypeName() {
        return fieldTypeName;
    }

    public void setFieldTypeName(String fieldTypeName) {
        this.fieldTypeName = fieldTypeName;
    }

    public RawType getRawType() {
        return RawType;
    }

    public void setRawType(RawType rawType) {
        RawType = rawType;
    }

    public Integer getRawIgnoreAbove() {
        return rawIgnoreAbove;
    }

    public void setRawIgnoreAbove(Integer rawIgnoreAbove) {
        this.rawIgnoreAbove = rawIgnoreAbove;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public SearchAnalyzer getSearchAnalyzer() {
        return searchAnalyzer;
    }

    public void setSearchAnalyzer(SearchAnalyzer searchAnalyzer) {
        this.searchAnalyzer = searchAnalyzer;
    }

    public boolean isFieldData() {
        return fieldData;
    }

    public void setFieldData(boolean fieldData) {
        this.fieldData = fieldData;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
    //public static void main(String[] args){

        //Metadata m = new Metadata();

        //System.out.println(FieldType.getMap());
        //System.out.println(Metadata.FieldType.getMap());
        //System.out.println(Metadata.FieldType.getMap()==Metadata.FieldType.getMap());
    //}
}

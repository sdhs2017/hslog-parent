package com.hs.elsearch.dao.biDao.impl;

import com.hs.elsearch.dao.biDao.IBIDao;
import com.hs.elsearch.template.SearchTemplate;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

public class BIDaoImpl implements IBIDao {
    @Autowired
    SearchTemplate searchTemplate;
    @Override
    public List<Map<String, Object>> getListBySumOfAggregation(String starttime, String endtime, String groupByField, String groupFieldType, String sumField, int size, String sort,Map<String, String> map, String... indices) throws Exception {
        //TODO 后期增加
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //TODO 排序字段暂时按照字符串匹配方式，后续会对此进行扩展
        boolean asc = "asc".equals(sort)?true:false;
        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder;
        //TODO 使用枚举类
        if("Date".equals(groupFieldType)){
            //TODO 对聚合方式进行更深度的扩展，可以定义时间间隔
            aggregationBuilder =
                    AggregationBuilders
                            // 聚合别名
                            .dateHistogram("aggs")
                            // 聚合字段
                            .field(groupByField)
                            // 聚合的方式，小时
                            .calendarInterval(DateHistogramInterval.HOUR);
                            // 为空的话则填充0
                            //.minDocCount(0)
                            // 需要填充0的范围
                            //.extendedBounds(extendedBounds);

                            //.order(BucketOrder.compound(BucketOrder.aggregation("sum",asc)));

        }else{
            aggregationBuilder = AggregationBuilders.terms("aggs").field(groupByField).order(BucketOrder.compound(BucketOrder.aggregation("sum",asc))).size(size);
        }
        // 在bucket上聚合metric查询sum
        SumAggregationBuilder sumBuilder = AggregationBuilders.sum("sum").field(sumField);

        aggregationBuilder.subAggregation(sumBuilder);
        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, indices);

        MultiBucketsAggregation terms  = aggregations.get("aggs");

        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

        Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();

        for(MultiBucketsAggregation.Bucket bucket:terms.getBuckets()) {
            NumericMetricsAggregation.SingleValue sum = bucket.getAggregations().get("sum");
            bucketmap.put(bucket.getKeyAsString(),sum.value());
        }
        list.add(bucketmap);
        return list;
    }

    @Override
    public List<Map<String, Object>> getListByCountOfAggregation(String starttime, String endtime, String groupByField, String groupFieldType, int size, String sort, Map<String, String> map, String... indices) throws Exception {
        //TODO 后期增加
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 聚合bucket查询group by
        //TODO 排序字段暂时按照字符串匹配方式，后续会对此进行扩展
        boolean asc = "asc".equals(sort)?true:false;
        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder;
        //TODO 使用枚举类
        if("Date".equals(groupFieldType)){
            //TODO 对聚合方式进行更深度的扩展，可以定义区间
            aggregationBuilder =
                    AggregationBuilders
                            // 聚合别名
                            .dateHistogram("aggs")
                            // 聚合字段
                            .field(groupByField)
                            // 聚合的方式，小时
                            .calendarInterval(DateHistogramInterval.HOUR);
                            // 为空的话则填充0
                            //.minDocCount(0)
                            // 需要填充0的范围
                            //.extendedBounds(extendedBounds);
                            //.order(BucketOrder.compound(BucketOrder.aggregation("count",asc)));

        }else{
            aggregationBuilder = AggregationBuilders.terms("aggs").field(groupByField).order(BucketOrder.compound(BucketOrder.aggregation("count",asc))).size(size);
        }
        // 在bucket上聚合metric查询count
        ValueCountAggregationBuilder countBuilder = AggregationBuilders.count("count").field(groupByField);

        aggregationBuilder.subAggregation(countBuilder);
        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, indices);

        MultiBucketsAggregation terms  = aggregations.get("aggs");

        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

        Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();

        for(MultiBucketsAggregation.Bucket bucket:terms.getBuckets()) {
            NumericMetricsAggregation.SingleValue count = bucket.getAggregations().get("count");
            bucketmap.put(bucket.getKeyAsString(),count.value());
        }
        list.add(bucketmap);
        return list;
    }

    @Override
    public List<Map<String, Object>> getListByAvgOfAggregation(String starttime, String endtime, String groupByField, String groupFieldType, String avgField, int size, String sort, Map<String, String> map, String... indices) throws Exception {
        //TODO 后期增加
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //TODO 排序字段暂时按照字符串匹配方式，后续会对此进行扩展
        boolean asc = "asc".equals(sort)?true:false;
        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder;
        //TODO 使用枚举类
        if("Date".equals(groupFieldType)){
            //ExtendedBounds extendedBounds = new ExtendedBounds("2020-03-06",endtime);
            //TODO 对聚合方式进行更深度的扩展，可以定义区间
            aggregationBuilder =
                    AggregationBuilders
                            // 聚合别名
                            .dateHistogram("aggs")
                            // 聚合字段
                            .field(groupByField)
                            // 聚合的方式，小时
                            .calendarInterval(DateHistogramInterval.HOUR);
                            // 为空的话则填充0
                            //.minDocCount(0)
                            // 需要填充0的范围
                            //.extendedBounds(extendedBounds)
                            //.order(BucketOrder.compound(BucketOrder.aggregation("avg",asc)));

        }else{
            aggregationBuilder = AggregationBuilders.terms("aggs").field(groupByField).order(BucketOrder.compound(BucketOrder.aggregation("avg",asc))).size(size);
        }

        // 在bucket上聚合metric查询avg
        AvgAggregationBuilder avgBuilder = AggregationBuilders.avg("avg").field(avgField);

        aggregationBuilder.subAggregation(avgBuilder);
        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, indices);

        MultiBucketsAggregation terms  = aggregations.get("aggs");

        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

        Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();

        for(MultiBucketsAggregation.Bucket bucket:terms.getBuckets()) {
            NumericMetricsAggregation.SingleValue avg = bucket.getAggregations().get("avg");
            //double有INFINITY和NaN两种特殊值，一般情况下都是数据为空时，在这里都转换成0
            //INFINITY为无穷，NaN为非数
            bucketmap.put(bucket.getKeyAsString(),(Double.isInfinite(avg.value())||Double.isNaN(avg.value()))?0:avg.value());
        }
        list.add(bucketmap);
        return list;
    }

    @Override
    public List<Map<String, Object>> getListByMaxOfAggregation(String starttime, String endtime, String groupByField, String groupFieldType, String maxField, int size, String sort, Map<String, String> map, String... indices) throws Exception {
        //TODO 后期增加
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //TODO 排序字段暂时按照字符串匹配方式，后续会对此进行扩展
        boolean asc = "asc".equals(sort)?true:false;
        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder;
        //TODO 使用枚举类
        if("Date".equals(groupFieldType)){
            //TODO 对聚合方式进行更深度的扩展，可以定义区间
            aggregationBuilder =
                    AggregationBuilders
                            // 聚合别名
                            .dateHistogram("aggs")
                            // 聚合字段
                            .field(groupByField)
                            // 聚合的方式，小时
                            .calendarInterval(DateHistogramInterval.HOUR);
                            // 为空的话则填充0
                            //.minDocCount(0)
                            // 需要填充0的范围
                            //.extendedBounds(extendedBounds);
                            //.order(BucketOrder.compound(BucketOrder.aggregation("max",asc)));

        }else{
            aggregationBuilder = AggregationBuilders.terms("aggs").field(groupByField).order(BucketOrder.compound(BucketOrder.aggregation("max",asc))).size(size);
        }
        // 在bucket上聚合metric查询max
        MaxAggregationBuilder maxBuilder = AggregationBuilders.max("max").field(maxField);

        aggregationBuilder.subAggregation(maxBuilder);
        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, indices);

        MultiBucketsAggregation terms  = aggregations.get("aggs");

        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

        Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();

        for(MultiBucketsAggregation.Bucket bucket:terms.getBuckets()) {
            NumericMetricsAggregation.SingleValue max = bucket.getAggregations().get("max");
            bucketmap.put(bucket.getKeyAsString(),(Double.isInfinite(max.value())||Double.isNaN(max.value()))?0:max.value());
        }
        list.add(bucketmap);
        return list;
    }

    @Override
    public List<Map<String, Object>> getListByMinOfAggregation(String starttime, String endtime, String groupByField, String groupFieldType, String minField, int size, String sort, Map<String, String> map, String... indices) throws Exception {
        //TODO 后期增加
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //TODO 排序字段暂时按照字符串匹配方式，后续会对此进行扩展
        boolean asc = "asc".equals(sort)?true:false;
        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder;
        //TODO 使用枚举类
        if("Date".equals(groupFieldType)){
            //TODO 对聚合方式进行更深度的扩展，可以定义区间
            aggregationBuilder =
                    AggregationBuilders
                            // 聚合别名
                            .dateHistogram("aggs")
                            // 聚合字段
                            .field(groupByField)
                            // 聚合的方式，小时
                            .calendarInterval(DateHistogramInterval.HOUR);
                            // 为空的话则填充0
                            //.minDocCount(0)
                            // 需要填充0的范围
                            //.extendedBounds(extendedBounds);
                            //.order(BucketOrder.compound(BucketOrder.aggregation("avg",asc)));

        }else{
            aggregationBuilder = AggregationBuilders.terms("aggs").field(groupByField).order(BucketOrder.compound(BucketOrder.aggregation("min",asc))).size(size);
        }
        // 在bucket上聚合metric查询min
        MinAggregationBuilder minBuilder = AggregationBuilders.min("min").field(minField);

        aggregationBuilder.subAggregation(minBuilder);
        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, indices);

        MultiBucketsAggregation terms  = aggregations.get("aggs");

        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

        Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();

        for(MultiBucketsAggregation.Bucket bucket:terms.getBuckets()) {
            NumericMetricsAggregation.SingleValue min = bucket.getAggregations().get("min");
            //bucketmap.put(bucket.getKeyAsString(),min.getValue());
            bucketmap.put(bucket.getKeyAsString(),(Double.isInfinite(min.value())||Double.isNaN(min.value()))?0:min.value());
        }
        list.add(bucketmap);
        return list;
    }

    /**
     * 根据参数创建AggregationBuilder //TODO
     * @param groupByField
     * @param groupFieldType
     * @param minField
     * @param size
     * @param sort
     * @return
     */
    private AggregationBuilder buildAggregation( String groupByField, String groupFieldType,String minField, int size, String sort){
        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder;
        return null;
    }
}

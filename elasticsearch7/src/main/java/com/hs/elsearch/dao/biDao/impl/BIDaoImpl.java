package com.hs.elsearch.dao.biDao.impl;

import com.hs.elsearch.dao.biDao.IBIDao;
import com.hs.elsearch.dao.biDao.entity.VisualParam;
import com.hs.elsearch.template.SearchTemplate;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.*;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

public class BIDaoImpl implements IBIDao {
    @Autowired
    SearchTemplate searchTemplate;
    @Override
    public List<Map<String, Object>> getListBySumOfAggregation(VisualParam params) throws Exception {
        //查询条件
        BoolQueryBuilder boolQueryBuilder = buildQuery(params);
        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder = buildAggregation(params);
        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, params.getIndex_name());

        MultiBucketsAggregation terms  = aggregations.get("aggs");

        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

        Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();

        for(MultiBucketsAggregation.Bucket bucket:terms.getBuckets()) {
            NumericMetricsAggregation.SingleValue sum = bucket.getAggregations().get(params.getY_agg());
            bucketmap.put(bucket.getKeyAsString(),sum.value());
        }
        list.add(bucketmap);
        return list;
    }

    @Override
    public List<Map<String, Object>> getListByCountOfAggregation(VisualParam params) throws Exception {
        //查询条件
        BoolQueryBuilder boolQueryBuilder = buildQuery(params);
        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder = buildAggregation(params);
        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, params.getIndex_name());

        MultiBucketsAggregation terms  = aggregations.get("aggs");

        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

        Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();

        for(MultiBucketsAggregation.Bucket bucket:terms.getBuckets()) {
            NumericMetricsAggregation.SingleValue count = bucket.getAggregations().get(params.getY_agg());
            bucketmap.put(bucket.getKeyAsString(),count.value());
        }
        list.add(bucketmap);
        return list;
    }

    @Override
    public List<Map<String, Object>> getListByAvgOfAggregation(VisualParam params) throws Exception {
        //查询条件
        BoolQueryBuilder boolQueryBuilder = buildQuery(params);
        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder = buildAggregation(params);
        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, params.getIndex_name());

        MultiBucketsAggregation terms  = aggregations.get("aggs");

        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

        Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();

        for(MultiBucketsAggregation.Bucket bucket:terms.getBuckets()) {
            NumericMetricsAggregation.SingleValue avg = bucket.getAggregations().get(params.getY_agg());
            //double有INFINITY和NaN两种特殊值，一般情况下都是数据为空时，在这里都转换成0
            //INFINITY为无穷，NaN为非数
            bucketmap.put(bucket.getKeyAsString(),(Double.isInfinite(avg.value())||Double.isNaN(avg.value()))?0:avg.value());
        }
        list.add(bucketmap);
        return list;
    }

    @Override
    public List<Map<String, Object>> getListByMaxOfAggregation(VisualParam params) throws Exception {
        //查询条件
        BoolQueryBuilder boolQueryBuilder = buildQuery(params);
        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder = buildAggregation(params);
        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, params.getIndex_name());

        MultiBucketsAggregation terms  = aggregations.get("aggs");

        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

        Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();

        for(MultiBucketsAggregation.Bucket bucket:terms.getBuckets()) {
            NumericMetricsAggregation.SingleValue max = bucket.getAggregations().get(params.getY_agg());
            bucketmap.put(bucket.getKeyAsString(),(Double.isInfinite(max.value())||Double.isNaN(max.value()))?0:max.value());
        }
        list.add(bucketmap);
        return list;
    }

    @Override
    public List<Map<String, Object>> getListByMinOfAggregation(VisualParam params) throws Exception {
        //查询条件
        BoolQueryBuilder boolQueryBuilder = buildQuery(params);
        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder = buildAggregation(params);
        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, params.getIndex_name());

        MultiBucketsAggregation terms  = aggregations.get("aggs");

        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

        Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();

        for(MultiBucketsAggregation.Bucket bucket:terms.getBuckets()) {
            NumericMetricsAggregation.SingleValue min = bucket.getAggregations().get(params.getY_agg());
            //bucketmap.put(bucket.getKeyAsString(),min.getValue());
            bucketmap.put(bucket.getKeyAsString(),(Double.isInfinite(min.value())||Double.isNaN(min.value()))?0:min.value());
        }
        list.add(bucketmap);
        return list;
    }

    /**
     * 创建查询条件
     * @param params
     * @return
     */
    private BoolQueryBuilder buildQuery(VisualParam params){
        String starttime = params.getStartTime();//起始时间
        String endtime = params.getEndTime();//截止时间
        String dateField = params.getDateField();//时间范围对应的字段名
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //存在时间类型字段，需要添加时间范围参数查询
        if(dateField!=null&&!"".equals(dateField)){
            //时间范围，起始时间和截止时间都不允许为空
            if (starttime != null && !starttime.equals("") && endtime != null && !endtime.equals("")) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery(dateField).format("yyyy-MM-dd HH:mm:ss").gte(starttime).lte(endtime));
            }
        }
        return boolQueryBuilder;
    }
    /**
     * 根据参数创建AggregationBuilder //TODO
     * @param params
     * @return
     */
    private AggregationBuilder buildAggregation(VisualParam params){

        //TODO 排序字段暂时按照字符串匹配方式，后续会对此进行扩展
        boolean asc = "asc".equals(params.getSort())?true:false;
        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder = null;
        if("Date".equals(params.getX_agg())){
            //默认1小时间隔
            DateHistogramInterval dateHis = DateHistogramInterval.hours(1);
            //TODO 对聚合方式进行更深度的扩展，可以定义时间间隔
            if(params.getIntervalType()!=null){

                switch(params.getIntervalType()){
                    case SECOND:
                        dateHis = DateHistogramInterval.seconds(params.getIntervalValue());
                        aggregationBuilder = AggregationBuilders.dateHistogram("aggs").field(params.getX_field()).fixedInterval(dateHis);
                        break;
                    case MINUTE:
                        dateHis = DateHistogramInterval.minutes(params.getIntervalValue());
                        aggregationBuilder = AggregationBuilders.dateHistogram("aggs").field(params.getX_field()).fixedInterval(dateHis);
                        break;
                    case HOURLY:
                        dateHis = DateHistogramInterval.hours(params.getIntervalValue());
                        aggregationBuilder = AggregationBuilders.dateHistogram("aggs").field(params.getX_field()).fixedInterval(dateHis);
                        break;
                    case DAILY:
                        dateHis = DateHistogramInterval.days(params.getIntervalValue());
                        aggregationBuilder = AggregationBuilders.dateHistogram("aggs").field(params.getX_field()).fixedInterval(dateHis);
                        break;
                    case WEEKLY://ES目前不支持 N周、N月、N年的间隔设置
                        dateHis = DateHistogramInterval.WEEK;
                        aggregationBuilder = AggregationBuilders.dateHistogram("aggs").field(params.getX_field()).calendarInterval(dateHis);
                        break;
                    case MONTHLY:
                        dateHis = DateHistogramInterval.MONTH;
                        aggregationBuilder = AggregationBuilders.dateHistogram("aggs").field(params.getX_field()).calendarInterval(dateHis);
                        break;
                    case YEARLY:
                        dateHis = DateHistogramInterval.YEAR;
                        aggregationBuilder = AggregationBuilders.dateHistogram("aggs").field(params.getX_field()).calendarInterval(dateHis);
                        break;
                }
            }

        }else{
            aggregationBuilder = AggregationBuilders.terms("aggs").field(params.getX_field()).order(BucketOrder.compound(BucketOrder.aggregation(params.getY_agg(),asc))).size(params.getSize());
        }
        switch(params.getY_agg()){
            case "Sum":
                // 在bucket上聚合metric查询sum
                SumAggregationBuilder sumBuilder = AggregationBuilders.sum(params.getY_agg()).field(params.getY_field());
                aggregationBuilder.subAggregation(sumBuilder);
                break;
            case "Count":
                // 在bucket上聚合metric查询count
                ValueCountAggregationBuilder countBuilder = AggregationBuilders.count(params.getY_agg()).field(params.getX_field());
                aggregationBuilder.subAggregation(countBuilder);
                break;
            case "Average":
                // 在bucket上聚合metric查询avg
                AvgAggregationBuilder avgBuilder = AggregationBuilders.avg(params.getY_agg()).field(params.getY_field());
                aggregationBuilder.subAggregation(avgBuilder);
                break;
            case "Max":
                // 在bucket上聚合metric查询min
                MaxAggregationBuilder maxBuilder = AggregationBuilders.max(params.getY_agg()).field(params.getY_field());
                aggregationBuilder.subAggregation(maxBuilder);
                break;
            case "Min":
                // 在bucket上聚合metric查询min
                MinAggregationBuilder minBuilder = AggregationBuilders.min(params.getY_agg()).field(params.getY_field());
                aggregationBuilder.subAggregation(minBuilder);
                break;
        }
        return aggregationBuilder;
    }

    /**
     * 获取某个字段不为空的数据list，用于获取图表和dashborad列表
     * @param indexName
     * @param fieldName
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getListExistsField(String indexName,String fieldName) throws Exception {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //排序
        //SortBuilder sortBuilder = SortBuilders.fieldSort("visualization.title").order(SortOrder.DESC);
        //查询条件
        boolQueryBuilder.must(QueryBuilders.constantScoreQuery(QueryBuilders.existsQuery(fieldName)));
        //TODO 分页
        List<Map<String, Object>> list = searchTemplate.getListByBuilder(boolQueryBuilder, null, 0, 999, indexName);
        //List<Map<String, Object>> list = searchTemplate.getListByBuilder(boolQueryBuilder,indexName);
        return list;
    }

    @Override
    public long getCount(Map<String, String> map, String indexName) throws Exception {
        long result = 0L;
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 其他查询条件
        if (map!=null&&!map.isEmpty()) {
            // 遍历map中查询条件
            for(Map.Entry<String, String> entry : map.entrySet()){
                // 不分词精确查询
                boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
            }
        }
        try {
            result = searchTemplate.getCountByQuery(boolQueryBuilder, indexName);
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return result;
    }
}

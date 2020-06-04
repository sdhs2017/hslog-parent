package com.hs.elsearch.dao.common.impl;

import com.hs.elsearch.dao.common.ICommonDao;
import com.hs.elsearch.entity.HttpRequestParams;
import com.hs.elsearch.template.SearchTemplate;
import com.hs.elsearch.util.HSDateUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonDaoImpl implements ICommonDao {
    @Autowired
    SearchTemplate searchTemplate;
    @Override
    public List<Map<String, Object>> getListByDateHistogramAggregation(HttpRequestParams params) throws Exception {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 时间段查询条件处理
        if (params.getHasTimeArea()) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(params.getTimeField()).format("yyyy-MM-dd HH:mm:ss").gte(params.getStartTime()).lte(params.getEndTime()));
        }else {
            //如果真的不存在时间参数，获取当前时间所谓截止时间进行处理
            boolQueryBuilder.must(QueryBuilders.rangeQuery(params.getTimeField()).format("yyyy-MM-dd HH:mm:ss").lte(DateTime.now().toString("yyyy-MM-dd HH:mm:ss")));
        }
        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder = null;
        //默认秒钟间隔
        DateHistogramInterval dateHis = DateHistogramInterval.seconds(10);
        switch(params.getIntervalType()){
            case "seconds":
                dateHis = DateHistogramInterval.seconds(params.getIntervalValue());
                aggregationBuilder = AggregationBuilders.dateHistogram("aggs").field(params.getTimeField()).fixedInterval(dateHis);
                break;
            case "minutes":
                dateHis = DateHistogramInterval.minutes(params.getIntervalValue());
                aggregationBuilder = AggregationBuilders.dateHistogram("aggs").field(params.getTimeField()).fixedInterval(dateHis);
                break;
            case "hours":
                dateHis = DateHistogramInterval.hours(params.getIntervalValue());
                aggregationBuilder = AggregationBuilders.dateHistogram("aggs").field(params.getTimeField()).fixedInterval(dateHis);
                break;
        }


        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, params.getIndices());
        Histogram agg = aggregations.get("agg");

        List<Map<String, Object>> list = new ArrayList<>();
        // For each entry
        for (Histogram.Bucket entry : agg.getBuckets()) {
            Map<String, Object> aggmap = new HashMap<>();
            //new DateTime(entry.getKey());
            ZonedDateTime key = (ZonedDateTime)entry.getKey();
            //DateTime key = (DateTime) entry.getKey();    // Key
            String keyAsString = entry.getKeyAsString(); // Key as String
            long docCount = entry.getDocCount();         // Doc count
            //aggmap.put("hour",key.hourOfDay().getAsString());
            aggmap.put("hour",key.getHour());
            aggmap.put("count",docCount);
            list.add(aggmap);

        }
        return list;
    }
}

package com.hs.elsearch.dao.flowDao.impl;

import com.hs.elsearch.dao.flowDao.IFlowSearchDao;
import com.hs.elsearch.template.IndexTemplate;
import com.hs.elsearch.template.SearchTemplate;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.*;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: hsgit
 * @description: 实现IFlowSearchDao的接口
 * @author: jiyourui
 * @create: 2019-11-05 15:09
 **/
public class FlowSearchDao implements IFlowSearchDao {

    private static Logger logger = Logger.getLogger(FlowSearchDao.class);

    // 默认排序字段
    String orderField = "logdate";
    // 默认排序方式
    SortOrder desc = SortOrder.DESC;

    @Autowired
    SearchTemplate searchTemplate;
    @Autowired
    IndexTemplate indexTemplate;

    @Override
    public long getFlowCount(Map<String, String> map, String starttime, String endtime, String[] types, String... indices) {

        long result = 0;

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 时间段处理
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (starttime!=null&&!starttime.equals("")&&endtime!=null&&!endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime).lte(endtime));
        }else if (starttime!=null&&!starttime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime));
        }else if (endtime!=null&&!endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(endtime));
        }else {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date())));
        }

        // 针对elasticsearch7版本将types转为hslog_type字段查询
        if (types!=null&&!types.equals("")){
            boolQueryBuilder.must(QueryBuilders.termsQuery("hslog_type",types));
        }
        // 遍历map中查询条件
        for(Map.Entry<String, String> entry : map.entrySet()){
            // 针对通过IP地址查询流量日志的功能，需要匹配源地址或目的地址，采用多字段查询multiMarchQuery
            if (entry.getKey().equals("multifield_ip")) {
                String[] multified = {"ipv4_dst_addr","ipv4_src_addr"};
                boolQueryBuilder.must(QueryBuilders.multiMatchQuery(entry.getValue(), multified));
            }else if(entry.getKey().equals("packet_source")){
                // 流量数据来源
                boolQueryBuilder.must(QueryBuilders.matchQuery("packet_source", entry.getValue()));
            }else if (entry.getKey().equals("domain_url")) {
                boolQueryBuilder.must(QueryBuilders.termQuery("domain_url.raw", entry.getValue()));
            }else if (entry.getKey().equals("complete_url")) {
                boolQueryBuilder.must(QueryBuilders.termQuery("complete_url.raw", entry.getValue()));
            }
            /*else if (entry.getKey().equals("domain_url")||entry.getKey().equals("complete_url")) {
                // 短语匹配
                boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(entry.getKey(), entry.getValue()));
            }*/
            else if(entry.getKey().equals("dns_domain_name")){
                boolQueryBuilder.must(QueryBuilders.matchQuery(entry.getKey(), entry.getValue()));
            }else {
                // 不分词精确查询
                boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue().toLowerCase()));
            }
        }
        try {
            result = searchTemplate.getCountByQuery(boolQueryBuilder, indices);
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getListByAggregation(String[] types, String starttime, String endtime, String groupByField, int size, Map<String, String> map, String... indices) throws Exception {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 时间段查询条件处理
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (starttime!=null&&!starttime.equals("")&&endtime!=null&&!endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime).lte(endtime));
        }else if (starttime!=null&&!starttime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime));
        }else if (endtime!=null&&!endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(endtime));
        }else {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date())));
        }

        // 针对elasticsearch7版本将types转为hslog_type字段查询
        if (types!=null&&!types.equals("")){
            boolQueryBuilder.must(QueryBuilders.termsQuery("hslog_type",types));
        }

        // 其他查询条件处理
        if (map!=null&&!map.isEmpty()) {
            for(Map.Entry<String, String> entry : map.entrySet()){
                if (entry.getKey().equals("logdate")) {
                    boolQueryBuilder.must(QueryBuilders.rangeQuery(entry.getKey()).format("yyyy-MM-dd").gte(entry.getValue()));
                }else if (entry.getKey().equals("domain_url")||entry.getKey().equals("complete_url")) {
                    // 短语匹配
                    boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey()+".raw", entry.getValue()));
                }else if (entry.getKey().equals("event_type")){
                    // 针对syslog日志的事件，该字段不为null
                    boolQueryBuilder.must(QueryBuilders.constantScoreQuery(QueryBuilders.existsQuery("event_type")));
                }/*else if (entry.getKey().equals("application_layer_protocol")) {
					queryBuilder.must(QueryBuilders.multiMatchQuery(entry.getKey(), "http"));
				}*/else {
                    boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
                }
            }
        }

        // 聚合条件处理
        String count = "aggs";
        // 聚合查询group by
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms(count).field(groupByField).order(BucketOrder.count(false)).size(size);

        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, indices);

        Terms terms  = aggregations.get(count);

        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

        Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();

        for(Terms.Bucket bucket:terms.getBuckets()) {
            bucketmap.put(bucket.getKeyAsString(), bucket.getDocCount());
        }

        list.add(bucketmap);
        return list;
    }

    @Override
    public List<List<Map<String, Object>>> getListByAggregation(String[] types, String starttime, String endtime, String[] groupByFields, int size, Map<String, String> map, String... indices) throws Exception {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 时间段查询条件处理
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (starttime!=null&&!starttime.equals("")&&endtime!=null&&!endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime).lte(endtime));
        }else if (starttime!=null&&!starttime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime));
        }else if (endtime!=null&&!endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(endtime));
        }else {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date())));
        }

        // 针对elasticsearch7版本将types转为hslog_type字段查询
        if (types!=null&&!types.equals("")){
            boolQueryBuilder.must(QueryBuilders.termsQuery("hslog_type",types));
        }

        // 其他查询条件处理
        if (map!=null&&!map.isEmpty()) {
            for(Map.Entry<String, String> entry : map.entrySet()){
                if (entry.getKey().equals("logdate")) {
                    boolQueryBuilder.must(QueryBuilders.rangeQuery(entry.getKey()).format("yyyy-MM-dd").gte(entry.getValue()));
                }else if (entry.getKey().equals("domain_url")||entry.getKey().equals("complete_url")) {
                    // 短语匹配
                    boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey()+".raw", entry.getValue()));
                }else if (entry.getKey().equals("event_type")){
                    // 针对syslog日志的事件，该字段不为null
                    boolQueryBuilder.must(QueryBuilders.constantScoreQuery(QueryBuilders.existsQuery("event_type")));
                }/*else if (entry.getKey().equals("application_layer_protocol")) {
					queryBuilder.must(QueryBuilders.multiMatchQuery(entry.getKey(), "http"));
				}*/else {
                    boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
                }
            }
        }

        AggregationBuilder aggregationBuilder = null;
        for (String groupByField : groupByFields){
            // 聚合条件处理
            String count = groupByField+"_count";
            // 聚合查询group by
            if (aggregationBuilder==null){
                aggregationBuilder = AggregationBuilders.terms(count).field(groupByField).order(BucketOrder.count(false)).size(size);
            }else {
                aggregationBuilder.subAggregation(AggregationBuilders.terms(count).field(groupByField).order(BucketOrder.count(false)).size(size).subAggregation(AggregationBuilders.topHits("top").size(1)));
            }

        }
        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, indices);

        Terms terms  = aggregations.get(groupByFields[0]+"_count");

        List<List<Map<String, Object>>> list = new ArrayList<>();


        for(Terms.Bucket bucket:terms.getBuckets()) {

            Terms terms1 = (Terms) bucket.getAggregations().asMap().get(groupByFields[1]+"_count");
            for(Terms.Bucket bucket2 : terms1.getBuckets()) {
                List<Map<String,Object>> tmplist = new ArrayList<>();
                TopHits topHits = bucket2.getAggregations().get("top");
                for (SearchHit hit : topHits.getHits().getHits()) {
                    Map<String,Object> source = new HashMap<>();
                    source.put("name",hit.getSourceAsMap().get("src_addr_city").toString().replace("\"","\'"));
                    String [] src_addr_locations =  hit.getSourceAsMap().get("src_addr_locations").toString().split(",");
                    double [] value = new double[2];
                    int j = 0;
                    for (int i=src_addr_locations.length-1;i>=0;i--){
                        value[j] = Double.parseDouble(src_addr_locations[i].toString());
                        j++;
                    }
                    source.put("value",value);
                    //source.put("count",bucket2.getDocCount());
                    tmplist.add(source);
                    Map<String,Object> dst = new HashMap<>();
                    dst.put("name",hit.getSourceAsMap().get("dst_addr_city"));
                    double [] value2 = new double[2];
                    String [] dst_addr_locations = hit.getSourceAsMap().get("dst_addr_locations").toString().split(",");
                    j = 0;
                    for (int i=dst_addr_locations.length-1;i>=0;i--){
                        value2[j] = Double.parseDouble(dst_addr_locations[i].toString());
                        j++;
                    }
                    dst.put("value",value2);
                    //dst.put("count",bucket2.getDocCount());
                    tmplist.add(dst);
                    //System.out.println("源地址："+hit.getSourceAsMap().get("src_addr_city")+" 源经纬度："+hit.getSourceAsMap().get("src_addr_locations")+"  目的地址："+hit.getSourceAsMap().get("dst_addr_city")+"  "+hit.getSourceAsMap().get("dst_addr_locations")+"");
                }
                list.add(tmplist);
            }

        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getListBySumOfAggregation(String[] types, String starttime, String endtime, String groupByField, String sumField, int size, Map<String, String> map, String... indices) throws Exception {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 时间段查询条件处理
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (starttime!=null&&!starttime.equals("")&&endtime!=null&&!endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime).lte(endtime));
        }else if (starttime!=null&&!starttime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime));
        }else if (endtime!=null&&!endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(endtime));
        }else {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date())));
        }
        // 针对elasticsearch7版本将types转为hslog_type字段查询
        if (types!=null&&!types.equals("")){
            boolQueryBuilder.must(QueryBuilders.termsQuery("hslog_type",types));
        }
        // 其他查询条件处理
        if (map!=null&&!map.isEmpty()) {
            for(Map.Entry<String, String> entry : map.entrySet()){
                if (entry.getKey().equals("logdate")) {
                    boolQueryBuilder.must(QueryBuilders.rangeQuery(entry.getKey()).format("yyyy-MM-dd").gte(entry.getValue()));
                }else if (entry.getKey().equals("domain_url")||entry.getKey().equals("complete_url")) {
                    // 短语匹配
                    boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey()+".raw", entry.getValue()));
                }else if (entry.getKey().equals("event_type")){
                    // 针对syslog日志的事件，该字段不为null
                    boolQueryBuilder.must(QueryBuilders.constantScoreQuery(QueryBuilders.existsQuery("event_type")));
                }/*else if (entry.getKey().equals("application_layer_protocol")) {
					queryBuilder.must(QueryBuilders.multiMatchQuery(entry.getKey(), "http"));
				}*/else {
                    boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
                }
            }
        }

        // 聚合bucket查询group by
        //AggregationBuilder aggregationBuilder = AggregationBuilders.terms("aggs").field(groupByField).order(Terms.Order.compound(Terms.Order.aggregation("sum",false))).size(size);
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms("aggs").field(groupByField).order(BucketOrder.compound(BucketOrder.aggregation("sum",false))).size(size);
        // 在bucket上聚合metric查询sum
        SumAggregationBuilder sumBuilder = AggregationBuilders.sum("sum").field(sumField);

        aggregationBuilder.subAggregation(sumBuilder);
        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, indices);

        Terms terms  = aggregations.get("aggs");

        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

        Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();

        for(Terms.Bucket bucket:terms.getBuckets()) {
            Sum sum = bucket.getAggregations().get("sum");
            bucketmap.put(bucket.getKeyAsString(), Math.round(sum.getValue()/1024));
        }

        list.add(bucketmap);
        return list;
    }

    @Override
    public List<Map<String, Object>> getListBySumOfMetrics(String[] types, String starttime, String endtime, String sumField, int size, Map<String, String> map, String... indices) throws Exception {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 时间段查询条件处理
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (starttime!=null&&!starttime.equals("")&&endtime!=null&&!endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime).lte(endtime));
        }else if (starttime!=null&&!starttime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime));
        }else if (endtime!=null&&!endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(endtime));
        }else {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date())));
        }
        // 针对elasticsearch7版本将types转为hslog_type字段查询
        if (types!=null&&!types.equals("")){
            boolQueryBuilder.must(QueryBuilders.termsQuery("hslog_type",types));
        }
        // 其他查询条件处理
        if (map!=null&&!map.isEmpty()) {
            for(Map.Entry<String, String> entry : map.entrySet()){
                if (entry.getKey().equals("logdate")) {
                    boolQueryBuilder.must(QueryBuilders.rangeQuery(entry.getKey()).format("yyyy-MM-dd").gte(entry.getValue()));
                }else if (entry.getKey().equals("domain_url")||entry.getKey().equals("complete_url")) {
                    // 短语匹配
                    boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey()+".raw", entry.getValue()));
                }else if (entry.getKey().equals("event_type")){
                    // 针对syslog日志的事件，该字段不为null
                    boolQueryBuilder.must(QueryBuilders.constantScoreQuery(QueryBuilders.existsQuery("event_type")));
                }else {
                    boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
                }
            }
        }

        // 聚合metric查询sum
        SumAggregationBuilder sumBuilder = AggregationBuilders.sum("agg").field(sumField);

        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, sumBuilder, indices);
        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();
        if (aggregations!=null){
            Sum sum  = aggregations.get("agg");
            Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();
            bucketmap.put(sum.getName(), Math.round(sum.getValue()/1024));
            list.add(bucketmap);
        }

        return list;
    }

    @Override
    public List<Map<String, Object>> getListByCountOfMetrics(String[] types, String starttime, String endtime, String countField, int size, Map<String, String> map, String... indices) throws Exception {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 时间段查询条件处理
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (starttime!=null&&!starttime.equals("")&&endtime!=null&&!endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime).lte(endtime));
        }else if (starttime!=null&&!starttime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime));
        }else if (endtime!=null&&!endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(endtime));
        }else {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date())));
        }
        // 针对elasticsearch7版本将types转为hslog_type字段查询
        if (types!=null&&!types.equals("")){
            boolQueryBuilder.must(QueryBuilders.termsQuery("hslog_type",types));
        }
        // 其他查询条件处理
        if (map!=null&&!map.isEmpty()) {
            for(Map.Entry<String, String> entry : map.entrySet()){
                if (entry.getKey().equals("logdate")) {
                    boolQueryBuilder.must(QueryBuilders.rangeQuery(entry.getKey()).format("yyyy-MM-dd").gte(entry.getValue()));
                }else if (entry.getKey().equals("domain_url")||entry.getKey().equals("complete_url")) {
                    // 短语匹配
                    boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey()+".raw", entry.getValue()));
                }else if (entry.getKey().equals("event_type")){
                    // 针对syslog日志的事件，该字段不为null
                    boolQueryBuilder.must(QueryBuilders.constantScoreQuery(QueryBuilders.existsQuery("event_type")));
                    // 针对packet_length的范围进行数据查询
                }else if (entry.getKey().equals("packet_length")){

                    if (entry.getValue().contains(",")){
                        String [] value = entry.getValue().split(",");
                        boolQueryBuilder.must(QueryBuilders.rangeQuery(entry.getKey()).gte(value[0]).lt(value[1]));
                    }else{
                        boolQueryBuilder.must(QueryBuilders.rangeQuery(entry.getKey()).gte(entry.getValue()));
                    }
                    // 针对组播地址的范围查询
                }else if (entry.getKey().equals("multicast")){
                    BoolQueryBuilder boolshuld = QueryBuilders.boolQuery();
                    boolshuld.should(QueryBuilders.rangeQuery(entry.getValue()).gt("224.0.0.0").lte("224.0.0.255"));
                    boolshuld.should(QueryBuilders.rangeQuery(entry.getValue()).gte("224.0.1.0").lte("224.0.1.255"));
                    boolshuld.should(QueryBuilders.rangeQuery(entry.getValue()).gte("224.0.2.0").lte("239.255.255.255"));
                    boolQueryBuilder.must(boolshuld);
                    // 针对广播地址的模糊查询
                }else if (entry.getKey().equals("broadcast")){
                    BoolQueryBuilder boolshuld = QueryBuilders.boolQuery();
                    String [] addrs = entry.getValue().split(",");
                    boolshuld.should(QueryBuilders.wildcardQuery(addrs[0],"*.255"));
                    boolshuld.should(QueryBuilders.wildcardQuery(addrs[1],"*.255"));
                    boolQueryBuilder.must(boolshuld);
                }else {
                    boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
                }
            }
        }

        // 聚合metric查询sum
        ValueCountAggregationBuilder countAggregationBuilder = AggregationBuilders.count("agg").field(countField);

        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, countAggregationBuilder, indices);
        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();
        if (aggregations!=null){
            ValueCount count  = aggregations.get("agg");
            Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();
            bucketmap.put(count.getName(), count.getValue());
            list.add(bucketmap);
        }

        return list;
    }

    @Override
    public List<Map<String, Object>> getFlowListByMap(Map<String, String> map, String starttime, String endtime, Integer from, Integer size, String[] types, String... indices) throws Exception {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 时间段查询条件处理
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (starttime!=null&&!starttime.equals("")&&endtime!=null&&!endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime).lte(endtime));
        }else if (starttime!=null&&!starttime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime));
        }else if (endtime!=null&&!endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(endtime));
        }else {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date())));
        }

        // 针对elasticsearch7版本将types转为hslog_type字段查询
        if (types!=null&&!types.equals("")){
            boolQueryBuilder.must(QueryBuilders.termsQuery("hslog_type",types));
        }

        // 遍历map中查询条件
        for(Map.Entry<String, String> entry : map.entrySet()){
            // 针对通过IP地址查询流量日志的功能，需要匹配源地址或目的地址，采用多字段查询multiMarchQuery
            if (entry.getKey().equals("multifield_ip")) {
                String[] multified = {"ipv4_dst_addr","ipv4_src_addr"};
                boolQueryBuilder.must(QueryBuilders.multiMatchQuery(entry.getValue(), multified));
            }else if(entry.getKey().equals("packet_source")){
                // 流量数据来源
                boolQueryBuilder.must(QueryBuilders.matchQuery("packet_source", entry.getValue()));
            }else if (entry.getKey().equals("domain_url")) {
                // 使用字段的raw查询，避免查询出错
                boolQueryBuilder.must(QueryBuilders.termQuery("domain_url.raw", entry.getValue()));
            }else if (entry.getKey().equals("complete_url")) {
                // 使用字段的raw查询，避免查询出错
                boolQueryBuilder.must(QueryBuilders.termQuery("complete_url.raw", entry.getValue()));
            }else if(entry.getKey().equals("dns_domain_name")){
                boolQueryBuilder.must(QueryBuilders.matchQuery(entry.getKey(), entry.getValue()));
            }else {
                // 不分词精确查询
                boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue().toLowerCase()));
            }
        }

        // 构建排序体,指定排序字段
        SortBuilder sortBuilder = SortBuilders.fieldSort(orderField).order(desc);

        return searchTemplate.getListByBuilder(boolQueryBuilder,sortBuilder,from,size,indices);
    }
}

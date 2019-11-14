package com.hs.elsearch.dao.logDao.impl;

import com.hs.elsearch.dao.logDao.ILogSearchDao;
import com.hs.elsearch.template.ESTransportIndexTemplate;
import com.hs.elsearch.template.ESTransportSearchTemplate;
import org.apache.directory.api.util.Strings;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: hsgit
 * @description: 实现ILogSearchDao
 * @author: jiyourui
 * @create: 2019-11-11 10:27
 **/
public class LogSearchDaoImpl implements ILogSearchDao {

    // 默认排序字段
    String orderField = "logdate";

    // 默认排序方式
    SortOrder desc = SortOrder.DESC;

    @Autowired
    ESTransportSearchTemplate searchTemplate;
    @Autowired
    ESTransportIndexTemplate indexTemplate;

    @Override
    public boolean createTemplateOfIndex(String tempalateName, String tempalatePattern, Map<String, Object> settings, String type, String mapping) throws Exception {
        return indexTemplate.createOrUpdateTemplateOfIndex(tempalateName,tempalatePattern,settings,type,mapping);
    }

    @Override
    public long getCount(Map<String, String> map, String starttime,String endtime, String[] types, String... indices) {

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

        // 其他查询条件
        if (map!=null&&!map.isEmpty()) {
            // 遍历map中查询条件
            for(Map.Entry<String, String> entry : map.entrySet()){
                // 针对事件查询在controller设定的固定值，保证事件查询的关键信息不为nul
                if (entry.getKey().equals("event")) {
                    // 字段不为null查询
                    boolQueryBuilder.must(QueryBuilders.constantScoreQuery(QueryBuilders.existsQuery("event_type")));
                }else if(entry.getKey().equals("event_levels")){
                    // 范围查询，根据自定义的文字描述事件级别，对应到实际的数字事件级别
                    if (entry.getValue().equals("高危")) {
                        boolQueryBuilder.must(QueryBuilders.rangeQuery("event_level").gte(0).lte(3));
                    }else if (entry.getValue().equals("中危")) {
                        boolQueryBuilder.must(QueryBuilders.rangeQuery("event_level").gte(4).lte(5));
                    }else if (entry.getValue().equals("普通")) {
                        boolQueryBuilder.must(QueryBuilders.rangeQuery("event_level").gte(6).lte(7));
                    }
                }else if (entry.getKey().equals("domain_url")||entry.getKey().equals("complete_url")) {
                    // 短语匹配
                    boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(entry.getKey(), entry.getValue()));
                }else if (entry.getKey().equals("operation_level")) {
                    // 针对日志级别为复选框，传入的参数是以逗号分隔的String，将日志级别转为数组用terms查询
                    String [] operation_level = entry.getValue().split(",");
                    boolQueryBuilder.must(QueryBuilders.termsQuery("operation_level", operation_level));
                }else if(entry.getKey().equals("dns_domain_name")){
                    // dns域名，需要用分词匹配的方式查询
                    QueryBuilder queryBuilder = QueryBuilders.matchQuery(entry.getKey(), entry.getValue());
                    boolQueryBuilder.must(queryBuilder);
                }else {
                    // 不分词精确查询
                    boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
                }
            }

        }
        try {
            result = searchTemplate.getCountByQuery(boolQueryBuilder, types, indices);
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getListByAggregation(String[] types, String starttime, String endtime, String groupByField, int size, Map<String, String> map, String... indices) {
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
        // 其他查询条件处理
        if (map!=null&&!map.isEmpty()) {
            for(Map.Entry<String, String> entry : map.entrySet()){
                if (entry.getKey().equals("logdate")) {
                    boolQueryBuilder.must(QueryBuilders.rangeQuery(entry.getKey()).format("yyyy-MM-dd").gte(entry.getValue()));
                }else if (entry.getKey().equals("domain_url")||entry.getKey().equals("complete_url")) {
                    // 短语匹配
                    boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(entry.getKey(), entry.getValue()));
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
        String count = groupByField+"_count";
        // 聚合查询group by
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms(count).field(groupByField).order(Terms.Order.count(false)).size(size);

        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, types, indices);

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
    public List<Map<String, Object>> getListByAggregation(String[] types, String starttime, String endtime, String[] groupByFields, int size, Map<String, String> map, String... indices) {
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
        // 其他查询条件处理
        if (map!=null&&!map.isEmpty()) {
            for(Map.Entry<String, String> entry : map.entrySet()){
                if (entry.getKey().equals("logdate")) {
                    boolQueryBuilder.must(QueryBuilders.rangeQuery(entry.getKey()).format("yyyy-MM-dd").gte(entry.getValue()));
                }else if (entry.getKey().equals("domain_url")||entry.getKey().equals("complete_url")) {
                    // 短语匹配
                    boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(entry.getKey(), entry.getValue()));
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
                aggregationBuilder = AggregationBuilders.terms(count).field(groupByField).order(Terms.Order.count(false)).size(size);
            }else {
                aggregationBuilder.subAggregation(AggregationBuilders.terms(count).field(groupByField).order(Terms.Order.count(false)).size(size));
            }

        }


        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, types, indices);

        Terms terms  = aggregations.get(groupByFields[0]+"_count");

        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

        /*Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();*/

        for(Terms.Bucket bucket:terms.getBuckets()) {

            Terms terms1 = (Terms) bucket.getAggregations().asMap().get(groupByFields[1]+"_count");
            for(Terms.Bucket bucket2 : terms1.getBuckets()) {
                Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();
                bucketmap.put("source", bucket.getKeyAsString());
                bucketmap.put("target",bucket2.getKeyAsString());
                bucketmap.put("count", bucket2.getDocCount());
                list.add(bucketmap);
            }

        }

        return list;
    }

    @Override
    public List<Map<String, Object>> getListByDateHistogramAggregation(String[] types, String starttime, String endtime, String dateHistogramField, Map<String, String> map, String... indices) {

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

        // 其他查询条件处理
        if (map!=null&&!map.isEmpty()) {
            for (Map.Entry<String,String> entry : map.entrySet()){
                if (entry.getKey().equals("event_type")){
                    boolQueryBuilder.must(QueryBuilders.constantScoreQuery(QueryBuilders.existsQuery("event_type")));
                }else if (entry.getKey().equals("event_level")){
                    int gte = 0;
                    int lte = 7;
                    if (Integer.valueOf(entry.getValue())==1) {
                        gte = 0;
                        lte = 3;
                    }else if (Integer.valueOf(entry.getValue())==2) {
                        gte = 4;
                        lte = 5;
                    }else if (Integer.valueOf(entry.getValue())==3) {
                        gte = 6;
                        lte = 7;
                    }
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("event_level").gte(gte).lte(lte));
                }else {
                    TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(entry.getKey(),entry.getValue());
                    boolQueryBuilder.must(termQueryBuilder);
                }

            }
        }

        AggregationBuilder aggregationBuilder =
                AggregationBuilders
                        .dateHistogram("agg")
                        .field(dateHistogramField)
                        .dateHistogramInterval(DateHistogramInterval.HOUR);

        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, types, indices);
        Histogram agg = aggregations.get("agg");

        List<Map<String, Object>> list = new ArrayList<>();
        // For each entry
        for (Histogram.Bucket entry : agg.getBuckets()) {
            Map<String, Object> aggmap = new HashMap<>();
            DateTime key = (DateTime) entry.getKey();    // Key
            String keyAsString = entry.getKeyAsString(); // Key as String
            long docCount = entry.getDocCount();         // Doc count
            aggmap.put("hour",key.hourOfDay().getAsString());
            aggmap.put("count",docCount);
            list.add(aggmap);

        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getListByContent(String content, String userid, int page, int size, String[] types, String... indices) {

        long count = 0;
        List<Map<String, Object>> list = new ArrayList<>();
        List<Map<String, Object>> arrayList = new ArrayList<>();
        // 针对全文检索需要查询的关键字段
        String [] fieldNames = {"operation_level","operation_des","ip","hostname","process","operation_facility"," equipmentname"};

        // 构建查询体
        BoolQueryBuilder QueryBuilder = QueryBuilders.boolQuery();
        // 短语匹配
        BoolQueryBuilder matchQuery = QueryBuilders.boolQuery();
        long matchCount = 0;
        // 多字段匹配
        BoolQueryBuilder multiQuery = QueryBuilders.boolQuery();
        long mutliCount = 0;
        // 模糊匹配
        BoolQueryBuilder wildcardQuery = QueryBuilders.boolQuery();

        // 查询条件-时间范围
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryBuilder dateQueryBuilder = QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date()));
        // 构建排序体,指定排序字段
        SortBuilder sortBuilder = SortBuilders.fieldSort("logdate").order(SortOrder.DESC);
        // 构建高亮体，指定包含查询条件的所有列都高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("*").requireFieldMatch(false);
        highlightBuilder.preTags("<span style=\"color:red\">");
        highlightBuilder.postTags("</span>");
        highlightBuilder.fragmentSize(500);
        // 查询条件-查询关键字
        if (content!=null&&!content.equals("")){
            // 去除查询条件前后的无用空格
            content = content.trim();
            // 针对短语、多个字段的查询条件，认为是查具体内容
            if (content.contains(" ")&&content.length()>10) {

                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("operation_des", content).operator(Operator.AND);
                matchQuery.must(matchQueryBuilder);
                // 查询条件-用户id
                if (userid!=null&&!userid.equals("")){
                    QueryBuilder userQueryBuilder = QueryBuilders.termQuery("userid", userid);
                    matchQuery.must(userQueryBuilder);
                }
                matchCount = searchTemplate.getCountByQuery(matchQuery,types,indices);
            }else{
                // 多字段匹配方式
                MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(Strings.toLowerCase(content),fieldNames);
                multiQuery.must(multiMatchQueryBuilder);
                // 查询条件-用户id
                if (userid!=null&&!userid.equals("")){
                    QueryBuilder userQueryBuilder = QueryBuilders.termQuery("userid", userid);
                    multiQuery.must(userQueryBuilder);
                }
                mutliCount = searchTemplate.getCountByQuery(multiQuery,types,indices);
            }

            if (matchCount<1&&mutliCount<1){

                // 查询条件-用户id
                if (userid!=null&&!userid.equals("")){
                    QueryBuilder userQueryBuilder = QueryBuilders.termQuery("userid", userid);
                    wildcardQuery.must(userQueryBuilder);
                }
                wildcardQuery.must(dateQueryBuilder);
                content = "*"+content.toLowerCase()+"*";
                for (String fieldname : fieldNames){
                    // 模糊查询
                    WildcardQueryBuilder wildcardqueryBuilder = QueryBuilders.wildcardQuery(fieldname, content);
                    wildcardQuery.should(wildcardqueryBuilder);
                }
                count = searchTemplate.getCountByQuery(wildcardQuery,types,indices);
                list = searchTemplate.getListByBuilder(wildcardQuery,sortBuilder,highlightBuilder,page,size,types,indices);
            }else if (matchCount>0){
                count = matchCount;
                list = searchTemplate.getListByBuilder(matchQuery,sortBuilder,highlightBuilder,page,size,types,indices);
            }else if (mutliCount>0){
                count = mutliCount;
                list = searchTemplate.getListByBuilder(multiQuery,sortBuilder,highlightBuilder,page,size,types,indices);
            }
        }else {
            // 查询条件-用户id
            if (userid!=null&&!userid.equals("")){
                QueryBuilder userQueryBuilder = QueryBuilders.termQuery("userid", userid);
                QueryBuilder.must(userQueryBuilder);
            }
            QueryBuilder.must(dateQueryBuilder);
            count = searchTemplate.getCountByQuery(QueryBuilder,types,indices);
            list = searchTemplate.getListByBuilder(QueryBuilder,sortBuilder,highlightBuilder,page,size,types,indices);
        }

        Map<String, Object> mapcount = new HashMap<String,Object>();
        //日志总量
        mapcount.put("count", count);
        arrayList.add(mapcount);
        arrayList.addAll(list);

        return arrayList;
    }

    @Override
    public List<Map<String, Object>> getListByMap(Map<String, String> map, String starttime, String endtime, String[] types, String... indices) {

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

        // 其他查询条件处理
        if (map!=null&&!map.isEmpty()) {
            for(Map.Entry<String, String> entry : map.entrySet()){
                if (entry.getKey().equals("operation_level")) {
                    // 针对日志级别为复选框，将日志级别转为数组用terms查询
                    String [] operation_level = entry.getValue().split(",");
                    boolQueryBuilder.must(QueryBuilders.termsQuery("operation_level", operation_level));
                }else if(entry.getKey().equals("dns_domain_name")){
                    QueryBuilder queryBuilder = QueryBuilders.matchQuery(entry.getKey(), entry.getValue());
                    boolQueryBuilder.must(queryBuilder);
                }else{
                    TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(entry.getKey(),entry.getValue().toLowerCase());
                    boolQueryBuilder.must(termQueryBuilder);
                }

            }
        }

        // 构建排序体,指定排序字段
        SortBuilder sortBuilder = SortBuilders.fieldSort(orderField).order(desc);

        return searchTemplate.getListByBuilder(boolQueryBuilder,sortBuilder,types,indices);
    }

    @Override
    public List<Map<String, Object>> getListByMap(Map<String, String> map, String starttime, String endtime, Integer from, Integer size, String[] types, String... indices) {
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

        // 其他查询条件处理
        if (map!=null&&!map.isEmpty()) {
            for(Map.Entry<String, String> entry : map.entrySet()){
			/*QueryBuilder matchqueryBuilder = QueryBuilders.matchQuery(entry.getKey(), entry.getValue());
			boolQueryBuilder.must(matchqueryBuilder);*/
            /*QueryBuilder wildcardqueryBuilder = QueryBuilders.wildcardQuery(entry.getKey(), "*"+entry.getValue()+"*");
            boolQueryBuilder.must(wildcardqueryBuilder);*/
                if (entry.getKey().equals("operation_level")) {
                    // 针对日志级别为复选框，将日志级别转为数组用terms查询
                    String [] operation_level = entry.getValue().split(",");
                    boolQueryBuilder.must(QueryBuilders.termsQuery("operation_level", operation_level));
                }else if(entry.getKey().equals("dns_domain_name")){
                    QueryBuilder queryBuilder = QueryBuilders.matchQuery(entry.getKey(), entry.getValue());
                    boolQueryBuilder.must(queryBuilder);
                }else{
                    TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(entry.getKey(),entry.getValue().toLowerCase());
                    boolQueryBuilder.must(termQueryBuilder);
                }
            }
        }

        // 构建排序体,指定排序字段
        SortBuilder sortBuilder = SortBuilders.fieldSort(orderField).order(desc);

        return searchTemplate.getListByBuilder(boolQueryBuilder,sortBuilder,from,size,types,indices);
    }

    @Override
    public List<Map<String, Object>> getLogListByMap(Map<String, String> map, String starttime, String endtime, Integer from, Integer size, String[] types, String... indices) {
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

        // 其他查询条件处理
        if (map!=null&&!map.isEmpty()) {
            for(Map.Entry<String, String> entry : map.entrySet()){

                if (entry.getKey().equals("operation_level")) {
                    // 针对日志级别为复选框，将日志级别转为数组用terms查询
                    String [] operation_level = entry.getValue().split(",");
                    boolQueryBuilder.must(QueryBuilders.termsQuery("operation_level", operation_level));
                }else if(entry.getKey().equals("dns_domain_name")){
                    QueryBuilder queryBuilder = QueryBuilders.matchQuery(entry.getKey(), entry.getValue());
                    boolQueryBuilder.must(queryBuilder);
                }else if(entry.getKey().equals("event")){
                    // 针对事件查询的保证事件类型不为空
                    boolQueryBuilder.must(QueryBuilders.constantScoreQuery(QueryBuilders.existsQuery("event_type")));
                }else if(entry.getKey().equals("event_levels")){
                    // 针对事件级别String转化为数字范围查询
                    if (entry.getValue().equals("高危")){
                        boolQueryBuilder.must(QueryBuilders.rangeQuery("event_level").gte(0).lte(3));
                    }else if (entry.getValue().equals("中危")) {
                        boolQueryBuilder.must(QueryBuilders.rangeQuery("event_level").gte(4).lte(5));
                    }else if (entry.getValue().equals("普通")) {
                        boolQueryBuilder.must(QueryBuilders.rangeQuery("event_level").gte(6).lte(7));
                    }
                }else if (entry.getKey().equals("equipmentname")) {
                    boolQueryBuilder.must(QueryBuilders.matchQuery("equipmentname",entry.getValue()));
                }else{
                    TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(entry.getKey(),entry.getValue().toLowerCase());
                    boolQueryBuilder.must(termQueryBuilder);
                }
            }
        }

        // 构建排序体,指定排序字段
        SortBuilder sortBuilder = SortBuilders.fieldSort(orderField).order(desc);

        return searchTemplate.getListByBuilder(boolQueryBuilder,sortBuilder,from,size,types,indices);
    }
}


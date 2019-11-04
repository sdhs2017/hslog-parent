package com.hs.elsearch.dao.impl;

import com.hs.elsearch.dao.IElasticsearchDao;
import com.hs.elsearch.template.ESTransportIndexTemplate;
import com.hs.elsearch.template.ESTransportSearchTemplate;
import com.hs.elsearch.template.bak.ClientTemplate;
import org.apache.directory.api.util.Strings;
import org.apache.log4j.Logger;
import org.elasticsearch.common.settings.Settings;
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
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: hslog-parent
 * @description: elasticsearch持久化层
 * @author: jiyourui
 * @create: 2019-09-12 14:17
 **/
public class ElasticsearchDao implements IElasticsearchDao {

    private static Logger logger = Logger.getLogger(ElasticsearchDao.class);

    /*private ClientTemplate clientTemplate;

    public ElasticsearchDao(final ClientTemplate clientTemplate){
        logger.info("module elasticsearch5 Dao层 初始化··· ···");
        this.clientTemplate = clientTemplate;
    }*/

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
                if (entry.getKey().equals("event")) {
                    // 字段不为null查询
                    boolQueryBuilder.must(QueryBuilders.constantScoreQuery(QueryBuilders.existsQuery("event_type")));
                }else if(entry.getKey().equals("event_level")){
                    // 范围查询
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("event_level").gte(0).lte(3));
                }else if (entry.getKey().equals("domain_url")||entry.getKey().equals("complete_url")) {
                    // 短语匹配
                    boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(entry.getKey(), entry.getValue()));
                }else if (entry.getKey().equals("operation_level")) {
                    // 针对日志级别为复选框，将日志级别转为数组用terms查询
                    String [] operation_level = entry.getValue().split(",");
                    boolQueryBuilder.must(QueryBuilders.termsQuery("operation_level", operation_level));
                }else if(entry.getKey().equals("dns_domain_name")){
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
    public List<Map<String, Object>> getListByAggregation(String[] types, String starttime, String endtime, String groupByField, Map<String, String> map, String... indices) {
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
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms(count).field(groupByField).order(Terms.Order.count(false));

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

        // 构建查询体
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 查询条件-用户id
        if (userid!=null&&!userid.equals("")){
            QueryBuilder userQueryBuilder = QueryBuilders.termQuery("userid", userid);
            boolQueryBuilder.must(userQueryBuilder);
        }
        // 查询条件-时间范围
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryBuilder dateQueryBuilder = QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date()));
        boolQueryBuilder.must(dateQueryBuilder);
        // 查询条件-查询关键字
        if (content!=null&&!content.equals("")){
            // 去除查询条件前后的无用空格
            content = content.trim();
            MultiMatchQueryBuilder contentQueryBuilder = QueryBuilders.multiMatchQuery(Strings.toLowerCase(content),"operation_level","operation_des","ip","hostname","process","operation_facility"," equipmentname");
            boolQueryBuilder.must(contentQueryBuilder);
        }

        // 构建排序体,指定排序字段
        SortBuilder sortBuilder = SortBuilders.fieldSort("logdate").order(SortOrder.DESC);
        // 构建高亮体，指定包含查询条件的所有列都高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("*").requireFieldMatch(false);
        highlightBuilder.preTags("<span style=\"color:red\">");
        highlightBuilder.postTags("</span>");
        highlightBuilder.fragmentSize(500);

        return searchTemplate.getListByBuilder(boolQueryBuilder,sortBuilder,highlightBuilder,page,size,types,indices);
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

        // 构建排序体,指定排序字段
        SortBuilder sortBuilder = SortBuilders.fieldSort(orderField).order(desc);

        return searchTemplate.getListByBuilder(boolQueryBuilder,sortBuilder,from,size,types,indices);
    }
}

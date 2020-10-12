package com.hs.elsearch.dao.ecsDao.impl;

import com.hs.elsearch.dao.ecsDao.IEcsSearchDao;
import com.hs.elsearch.template.CrudTemplate;
import com.hs.elsearch.template.SearchTemplate;
import com.hs.elsearch.util.HSDateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.directory.api.util.Strings;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.filter.Filters;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * @program: hsgit
 * @description: 服务于Elastic Common Schema(ECS)的具体查询事项
 * @author: jiyourui
 * @create: 2020-03-30 14:06
 **/
public class EcsSearchDao implements IEcsSearchDao {

    private static Logger logger = Logger.getLogger(EcsSearchDao.class);
    /**
     * ecs的时间字段，默认用于排序、时间范围查询
     */
    final String ECS_DATE_FIELD = "@timestamp";

    final String ECS_MATCHPHRASE_FIELD = "message";

    // 默认排序方式-倒序
    SortOrder desc = SortOrder.DESC;

    @Autowired
    SearchTemplate searchTemplate;

    @Autowired
    CrudTemplate crudTemplate;

    @Override
    public String deleteById(String index, String id) throws Exception {
        return crudTemplate.deleteById(index,id);
    }

    @Override
    public long getCount(Map<String, String> map, String starttime, String endtime, String... indices) throws Exception {
        long result = 0;

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 时间段处理
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (starttime != null && !starttime.equals("") && endtime != null && !endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").gte(starttime).lte(endtime));
            //存在时间范围时，添加对index的处理
            indices = HSDateUtil.dateArea2Indices(starttime,endtime,indices);
        } else if (starttime != null && !starttime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").gte(starttime));
        } else if (endtime != null && !endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").lte(endtime));
        } else {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date())));
        }

        // 其他查询条件
        if (map != null && !map.isEmpty()) {
            // 遍历map中查询条件
            for (Map.Entry<String, String> entry : map.entrySet()) {
               if(entry.getKey().equals("log.level")) {
                    // 针对日志级别为复选框，传入的参数是以逗号分隔的String，将日志级别转为数组用terms查询
                    String [] level = entry.getValue().split(",");
                    boolQueryBuilder.must(QueryBuilders.termsQuery(entry.getKey(), level));
                }else if (entry.getKey().equals("agent.type")) {
                    // 针对多类似为复选框，传入的参数是以逗号分隔的String，将日志级别转为数组用terms查询
                    String [] log_types = entry.getValue().split(",");
                    boolQueryBuilder.must(QueryBuilders.termsQuery(entry.getKey(), log_types));
                }else if (entry.getKey().equals("exists")){
                    // 针对事件查询的保证事件类型不为空
                    String [] fields = entry.getValue().split(",");
                    for (String field : fields){
                        boolQueryBuilder.must(QueryBuilders.existsQuery(field));
                    }
                }else if(entry.getKey().equals("log.levels")){
                    // 范围查询，根据自定义的文字描述事件级别，对应到实际的数字事件级别
                    if (entry.getValue().equals("高危")) {
                        boolQueryBuilder.must(QueryBuilders.rangeQuery("log.syslog.severity.code").gte(0).lte(3));
                    }else if (entry.getValue().equals("中危")) {
                        boolQueryBuilder.must(QueryBuilders.rangeQuery("log.syslog.severity.code").gte(4).lte(5));
                    }else if (entry.getValue().equals("普通")) {
                        boolQueryBuilder.must(QueryBuilders.rangeQuery("log.syslog.severity.code").gte(6).lte(7));
                    }
                }else if(entry.getKey().equals("event_group")){
                   String[] event_names = entry.getValue().split(",");
                   BoolQueryBuilder eventboolQueryBuilder = QueryBuilders.boolQuery();
                   for(String event_name:event_names){
                       if(!StringUtils.isEmpty(event_name)){
                           eventboolQueryBuilder.should(QueryBuilders.termQuery("event.action",event_name));
                       }
                   }
                   boolQueryBuilder.must(eventboolQueryBuilder);
               }else{
                    // 不分词精确查询
                    boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
                }

            }
        }

        result = searchTemplate.getCountByQuery(boolQueryBuilder, indices);

        return result;
    }

    @Override
    public List<Map<String, Object>> getListByContent(String starttime,String endtime,String content, String[] multiQueryField, Map<String, String> map, int page, int size, String... indices) throws Exception {
        long count = 0;
        List<Map<String, Object>> list = new ArrayList<>();
        List<Map<String, Object>> arrayList = new ArrayList<>();

        // 构建查询体
        BoolQueryBuilder QueryBuilder = QueryBuilders.boolQuery();
        // 短语匹配
        BoolQueryBuilder matchphraseQuery = QueryBuilders.boolQuery();
        long matchCount = 0;
        // 多字段匹配
        BoolQueryBuilder multiQuery = QueryBuilders.boolQuery();
        long mutliCount = 0;
        // 模糊匹配
        BoolQueryBuilder wildcardQuery = QueryBuilders.boolQuery();

        /**
         * 查询条件-时间范围
         * 当客户的服务器没有ntp时钟服务时，服务器发送的日志时间可能会比标准时间早，引起歧义，所以时间范围的结束时间为当前标准时间
         */
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //RangeQueryBuilder dateQueryBuilder = QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date()));
        //查询时间范围

        // 时间段处理
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        RangeQueryBuilder dateQueryBuilder = null;
        //如果有时间范围参数
        if (starttime != null && !starttime.equals("") && endtime != null && !endtime.equals("")) {
            dateQueryBuilder = QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").gte(starttime).lte(endtime);
            //存在时间范围时，添加对index的处理
            indices = HSDateUtil.dateArea2Indices(starttime,endtime,indices);
        }else if (starttime != null && !starttime.equals("")) {
            dateQueryBuilder = QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").gte(starttime);
        } else if (endtime != null && !endtime.equals("")) {
            dateQueryBuilder = QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").lte(endtime);
        }else{
            //默认的时间范围
            dateQueryBuilder = QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date()));
        }
        // 构建排序体,指定排序字段
        SortBuilder sortBuilder = SortBuilders.fieldSort(ECS_DATE_FIELD).order(desc);
        // 构建高亮体，指定包含查询条件的所有列都高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("*").requireFieldMatch(false);
        //highlightBuilder.preTags("<span style=\"color:red\">");
        highlightBuilder.preTags("<span style=\"background: #dea081;padding: 2px;\">");
        highlightBuilder.postTags("</span>");
        highlightBuilder.fragmentSize(500);
        /**
         * 其他查询条件构建
         */
        BoolQueryBuilder otherQueryBuilder = QueryBuilders.boolQuery();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            otherQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
        }

        // 查询条件-查询关键字
        if (content != null && !content.equals("")) {
            // 去除查询条件前后的无用空格
            content = content.trim();
            /**
             * 短语匹配的查询条件，认为是查具体内容
             * 符合短语匹配的条件：
             * 1、查询条件包含" "
             * 2、针对中文的情况长度大于3即可按照短语匹配，英文大于15按照短语匹配
             */

            if (content.contains(" ") || (content.matches("^[a-zA-Z]*") ? content.length() > 15 : content.length() > 3)) {
                MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(ECS_MATCHPHRASE_FIELD, content);
                matchphraseQuery.must(matchPhraseQueryBuilder);
                matchphraseQuery.must(otherQueryBuilder);
                matchCount = searchTemplate.getCountByQuery(matchphraseQuery, indices);
            } else {
                /**
                 * 查询内容与多字段匹配方式
                 * 英文的统一转换成小写
                 * 中文和中英文混合的不转换
                 */
                MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(content.matches("^[a-zA-Z]*") ? Strings.toLowerCase(content) : content, multiQueryField);
                multiQuery.must(multiMatchQueryBuilder);
                multiQuery.must(otherQueryBuilder);
                mutliCount = searchTemplate.getCountByQuery(multiQuery, indices);
            }

            /**
             * 如果短语匹配和多字段都没有数据进行模糊匹配
             */
            if (matchCount < 1 && mutliCount < 1) {
                //模糊查询体构建
                wildcardQuery.must(otherQueryBuilder);
                wildcardQuery.must(dateQueryBuilder);
                content = "*" + content + "*";
                BoolQueryBuilder tmpQuery = QueryBuilders.boolQuery();
                // 对多个关键字段模糊匹配
                for (String fieldname : multiQueryField) {
                    // 模糊查询
                    WildcardQueryBuilder wildcardqueryBuilder = QueryBuilders.wildcardQuery(fieldname, content);
                    tmpQuery.should(wildcardqueryBuilder);
                }
                wildcardQuery.must(tmpQuery);
                count = searchTemplate.getCountByQuery(wildcardQuery, indices);
                list = searchTemplate.getListByBuilder(wildcardQuery, sortBuilder, highlightBuilder, page, size, indices);
            } else if (matchCount > 0) {
                /**
                 * 短语匹配内容不为0时执行短语匹配的查询并返回内容
                 */
                count = matchCount;
                list = searchTemplate.getListByBuilder(matchphraseQuery, sortBuilder, highlightBuilder, page, size, indices);
            } else if (mutliCount > 0) {
                /**
                 * 多字段查询内容不为0时执行多字段的查询并返回内容
                 */
                count = mutliCount;
                list = searchTemplate.getListByBuilder(multiQuery, sortBuilder, highlightBuilder, page, size, indices);
            }
        } else {
            /**
             * 当查询内容为null时，直接查询所有
             */
            QueryBuilder.must(otherQueryBuilder);
            QueryBuilder.must(dateQueryBuilder);
            count = searchTemplate.getCountByQuery(QueryBuilder, indices);
            list = searchTemplate.getListByBuilder(QueryBuilder, sortBuilder, highlightBuilder, page, size, indices);
        }

        Map<String, Object> mapcount = new HashMap<String, Object>();
        //日志总量
        mapcount.put("count", count);
        arrayList.add(mapcount);
        arrayList.addAll(list);

        return arrayList;
    }

    @Override
    public List<Map<String, Object>> getLogListByMap(Map<String, String> map, String starttime, String endtime, Integer from, Integer size, String... indices) throws Exception {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 时间段查询条件处理
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (starttime != null && !starttime.equals("") && endtime != null && !endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").gte(starttime).lte(endtime));
            //存在时间范围时，添加对index的处理
            indices = HSDateUtil.dateArea2Indices(starttime,endtime,indices);
        } else if (starttime != null && !starttime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").gte(starttime));
        } else if (endtime != null && !endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").lte(endtime));
        } else {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date())));
        }

        // 其他查询条件处理
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getKey().equals("log.level")) {
                    // 针对日志级别为复选框，传入的参数是以逗号分隔的String，将日志级别转为数组用terms查询
                    String [] level = entry.getValue().split(",");
                    boolQueryBuilder.must(QueryBuilders.termsQuery("log.level", level));
                }else if (entry.getKey().equals("agent.type")) {
                    // 针对多类似为复选框，传入的参数是以逗号分隔的String，将日志级别转为数组用terms查询
                    String [] log_types = entry.getValue().split(",");
                    boolQueryBuilder.must(QueryBuilders.termsQuery(entry.getKey(), log_types));
                }else if (entry.getKey().equals("exists")){
                    // 针对事件查询的保证事件类型不为空
                    String [] fields = entry.getValue().split(",");
                    for (String field : fields){
                        boolQueryBuilder.must(QueryBuilders.existsQuery(field));
                    }
                }else if(entry.getKey().equals("log.levels")){
                    // 范围查询，根据自定义的文字描述事件级别，对应到实际的数字事件级别
                    if (entry.getValue().equals("高危")) {
                        boolQueryBuilder.must(QueryBuilders.rangeQuery("log.syslog.severity.code").gte(0).lte(3));
                    }else if (entry.getValue().equals("中危")) {
                        boolQueryBuilder.must(QueryBuilders.rangeQuery("log.syslog.severity.code").gte(4).lte(5));
                    }else if (entry.getValue().equals("普通")) {
                        boolQueryBuilder.must(QueryBuilders.rangeQuery("log.syslog.severity.code").gte(6).lte(7));
                    }
                }else if(entry.getKey().equals("event_group")){
                    String[] event_names = entry.getValue().split(",");
                    BoolQueryBuilder eventboolQueryBuilder = QueryBuilders.boolQuery();
                    for(String event_name:event_names){
                        if(!StringUtils.isEmpty(event_name)){
                            eventboolQueryBuilder.should(QueryBuilders.termQuery("event.action",event_name));
                        }
                    }
                    boolQueryBuilder.must(eventboolQueryBuilder);
                }else{
                    // 不分词精确查询
                    boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
                }
            }
        }

        // 构建排序体,指定排序字段
        SortBuilder sortBuilder = SortBuilders.fieldSort(ECS_DATE_FIELD).order(desc);

        return searchTemplate.getListByBuilder(boolQueryBuilder, sortBuilder, from, size, indices);
    }

    @Override
    public List<Map<String, Object>> getListByAggregation(String starttime, String endtime, String groupByField, int size, Map<String, String> map, String... indices) throws Exception {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 时间段查询条件处理
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (starttime!=null&&!starttime.equals("")&&endtime!=null&&!endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").gte(starttime).lte(endtime));
            //存在时间范围时，添加对index的处理
            indices = HSDateUtil.dateArea2Indices(starttime,endtime,indices);
        }else if (starttime!=null&&!starttime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").gte(starttime));
        }else if (endtime!=null&&!endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").lte(endtime));
        }else {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date())));
        }
        // 其他查询条件处理
        if (map!=null&&!map.isEmpty()) {
            for(Map.Entry<String, String> entry : map.entrySet()){
                if (entry.getKey().equals(ECS_DATE_FIELD)) {
                    boolQueryBuilder.must(QueryBuilders.rangeQuery(entry.getKey()).format("yyyy-MM-dd").gte(entry.getValue()));
                }else {
                    boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
                }
            }
        }

        // 聚合查询group by
        AggregationBuilder aggregationBuilder = AggregationBuilders
                .terms("agg")
                .field(groupByField)
                .order(BucketOrder.count(false))
                .size(size);

        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, indices);

        Terms terms  = aggregations.get("agg");

        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

        Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();

        for(Terms.Bucket bucket:terms.getBuckets()) {
            bucketmap.put(bucket.getKeyAsString(), bucket.getDocCount());
        }

        list.add(bucketmap);
        return list;
    }

    @Override
    public List<Map<String, Object>> getListByDateHistogramAggregation(String starttime, String endtime, String dateHistogramField, Map<String, String> map, String... indices) throws Exception {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 时间段查询条件处理
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (starttime!=null&&!starttime.equals("")&&endtime!=null&&!endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").gte(starttime).lte(endtime));
            //存在时间范围时，添加对index的处理
            indices = HSDateUtil.dateArea2Indices(starttime,endtime,indices);
        }else if (starttime!=null&&!starttime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").gte(starttime));
        }else if (endtime!=null&&!endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").lte(endtime));
        }else {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date())));
        }

        // 其他查询条件处理--不分词查询
        if (map!=null&&!map.isEmpty()) {
            for (Map.Entry<String,String> entry : map.entrySet()){
                if (entry.getKey().equals(ECS_DATE_FIELD)) {
                    boolQueryBuilder.must(QueryBuilders.rangeQuery(entry.getKey()).format("yyyy-MM-dd").gte(entry.getValue()).lte(entry.getValue()));
                }else if (entry.getKey().equals("exists")){
                    // 针对事件查询的保证事件类型不为空
                    String [] fields = entry.getValue().split(",");
                    for (String field : fields){
                        boolQueryBuilder.must(QueryBuilders.existsQuery(field));
                    }
                }else if (entry.getKey().equals("log.syslog.severity.code")){
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
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("log.syslog.severity.code").gte(gte).lte(lte));
                }else{
                    boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(),entry.getValue()));
                }

            }
        }
        ExtendedBounds extendedBounds = null;
        if (starttime!=null&&!starttime.equals("")&&endtime!=null&&!endtime.equals("")) {
            // 截取endtime的yyyy-MM-dd部分，将填充范围扩展到一整天0点-23点
            String extendedendtime = StringUtils.substringBefore(endtime," ")+" 23:59:59";
            // 当聚合结果为空时，需要填充0，设置需要填充0的范围
             extendedBounds = new ExtendedBounds(starttime,extendedendtime);
        }else if (map.get(ECS_DATE_FIELD)!=null&&!map.get(ECS_DATE_FIELD).equals("")){
            extendedBounds = new ExtendedBounds(map.get(ECS_DATE_FIELD)+" 00:00:00",map.get(ECS_DATE_FIELD)+" 23:59:59");
        }


        AggregationBuilder aggregationBuilder =
                AggregationBuilders
                        // 聚合别名
                        .dateHistogram("agg")
                        // 聚合字段
                        .field(dateHistogramField)
                        // 聚合的方式，小时
                        .calendarInterval(DateHistogramInterval.HOUR)

                        // 为空的话则填充0
                        .minDocCount(0)
                        // 需要填充0的范围
                        .extendedBounds(extendedBounds)
                        /**
                         * 7 版本中extend edBounds要求对时间格式进行定义，否则会报格式错误
                         * "type" : "parse_exception"
                         * "reason" : failed to parse date field [2020-04-03 00:00:00] with format [strict_date_optional_time||epoch_millis]
                         */
                        .format("yyyy-MM-dd HH:mm:ss");

        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, indices);
        Histogram agg = aggregations.get("agg");

        List<Map<String, Object>> list = new ArrayList<>();
        // For each entry
        for (Histogram.Bucket entry : agg.getBuckets()) {
            Map<String, Object> aggmap = new HashMap<>();
            ZonedDateTime key = (ZonedDateTime)entry.getKey();
            //String keyAsString = entry.getKeyAsString(); // Key as String
            long docCount = entry.getDocCount();// Doc count
            aggmap.put("hour",key.getHour());
            aggmap.put("count",docCount);
            list.add(aggmap);

        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getListByDateHistogramAggregation(String starttime, String endtime, String dateHistogramField, String subField, String subAggsType, Map<String, String> map, String... indices) throws Exception {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 时间段查询条件处理
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (starttime!=null&&!starttime.equals("")&&endtime!=null&&!endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").gte(starttime).lte(endtime));
            //存在时间范围时，添加对index的处理
            indices = HSDateUtil.dateArea2Indices(starttime,endtime,indices);
        }else if (starttime!=null&&!starttime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").gte(starttime));
        }else if (endtime!=null&&!endtime.equals("")) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").lte(endtime));
        }else {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(ECS_DATE_FIELD).format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date())));
        }

        // 其他查询条件处理--不分词查询
        if (map!=null&&!map.isEmpty()) {
            for (Map.Entry<String,String> entry : map.entrySet()){
                if (entry.getKey().equals(ECS_DATE_FIELD)) {
                    boolQueryBuilder.must(QueryBuilders.rangeQuery(entry.getKey()).format("yyyy-MM-dd").gte(entry.getValue()));
                }
                boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(),entry.getValue()));
            }
        }
        ExtendedBounds extendedBounds = null;
        if (starttime!=null&&!starttime.equals("")&&endtime!=null&&!endtime.equals("")) {
            // 截取endtime的yyyy-MM-dd部分，将填充范围扩展到一整天0点-23点
            String extendedendtime = StringUtils.substringBefore(endtime," ")+" 23:59:59";
            // 当聚合结果为空时，需要填充0，设置需要填充0的范围
            extendedBounds = new ExtendedBounds(starttime,extendedendtime);
        }else if (map!=null&&map.get(ECS_DATE_FIELD)!=null&&!map.get(ECS_DATE_FIELD).equals("")){
            extendedBounds = new ExtendedBounds(map.get(ECS_DATE_FIELD)+" 00:00:00",map.get(ECS_DATE_FIELD)+" 23:59:59");
        }

        AggregationBuilder aggregationBuilder =
                AggregationBuilders
                        // 聚合别名
                        .dateHistogram("agg")
                        // 聚合字段
                        .field(dateHistogramField)
                        // 聚合的方式，小时
                        .calendarInterval(DateHistogramInterval.HOUR)
                        // 为空的话则填充0
                        .minDocCount(0);
        if (extendedBounds != null) {
            // 需要填充0的范围
            ((DateHistogramAggregationBuilder) aggregationBuilder).extendedBounds(extendedBounds)
                    /**
                     * 7 版本中extend edBounds要求对时间格式进行定义，否则会报格式错误
                     * "type" : "parse_exception"
                     * "reason" : failed to parse date field [2020-04-03 00:00:00] with format [strict_date_optional_time||epoch_millis]
                     */
                    .format("yyyy-MM-dd HH:mm:ss");
        }

        switch (subAggsType) {
            case "terms":
                aggregationBuilder.subAggregation(AggregationBuilders.terms(subAggsType).field(subField));
                break;
            case "filters":
                // TODO 完善
                aggregationBuilder.subAggregation(AggregationBuilders.filter("", null));
                break;
            default:
                logger.info("ecs search dao 未找到对应的聚合参数");
        }


        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, indices);
        Histogram agg = aggregations.get("agg");

        List<Map<String, Object>> list = new ArrayList<>();
        // For each entry
        for (Histogram.Bucket entry : agg.getBuckets()) {
            Map<String, Object> aggmap = new HashMap<>();
            ZonedDateTime key = (ZonedDateTime)entry.getKey();
            long docCount = entry.getDocCount();// Doc count
            List<Map<String, Object>> sublist = new ArrayList<>();
            /**
             * 解析子聚合内容
             */
            switch (subAggsType) {
                case "terms":
                    Terms subterms = entry.getAggregations().get(subAggsType);
                    for (Terms.Bucket subentry : subterms.getBuckets()) {
                        Map<String, Object> subaggmap = new HashMap<>();
                        subaggmap.put(subentry.getKeyAsString(), subentry.getDocCount());
                        sublist.add(subaggmap);
                    }
                    break;
                case "filters":
                    Filters subfilters = entry.getAggregations().get(subAggsType);
                    for (Filters.Bucket subentry : subfilters.getBuckets()) {
                        Map<String, Object> subaggmap = new HashMap<>();
                        subaggmap.put(subentry.getKeyAsString(), subentry.getDocCount());
                        sublist.add(subaggmap);
                    }
                    break;
                default:
                    logger.info("ecs search dao 未找到对应的聚合参数");
            }

            aggmap.put("hour",key.getHour());
            aggmap.put("value",sublist);
            list.add(aggmap);

        }
        return list;
    }
}

package com.hs.elsearch.dao.biDao.impl;

import com.alibaba.fastjson.JSONObject;
import com.hs.elsearch.dao.biDao.IBIDao;
import com.hs.elsearch.entity.VisualParam;
import com.hs.elsearch.template.SearchTemplate;
import net.sf.json.JSONArray;
import org.apache.commons.collections.map.LinkedMap;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class BIDaoImpl implements IBIDao {
    @Autowired
    SearchTemplate searchTemplate;

    /**
     * bucket聚合查询后进行metric计算
     * 支持sum count avg max min
     * @param params
     * @return 返回聚合结果
     */
    @Override
    public List<Map<String, Object>> getListByAggregation(VisualParam params) throws Exception {
        //查询条件
        BoolQueryBuilder boolQueryBuilder = buildQuery(params.getQueryParam(),params.getStartTime(),params.getEndTime(),params.getDateField());
        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder = buildAggregation(params);
        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, params.getIndex_name());

        MultiBucketsAggregation terms  = aggregations.get("aggs");

        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();

        Map<String, Object> bucketmap = new HashMap<String, Object>();

        for(MultiBucketsAggregation.Bucket bucket:terms.getBuckets()) {
            NumericMetricsAggregation.SingleValue value = bucket.getAggregations().get(params.getY_agg());
            //double有INFINITY和NaN两种特殊值，一般情况下都是数据为空时，在这里都转换成0
            //INFINITY为无穷，NaN为非数
            bucketmap.put(bucket.getKeyAsString(),(Double.isInfinite(value.value())||Double.isNaN(value.value()))?0:value.value());
        }
        list.add(bucketmap);
        return list;
    }

    @Override
    public List<Map<String, Object>> getListBySumOfAggregation(VisualParam params) throws Exception {
        //查询条件
        BoolQueryBuilder boolQueryBuilder = buildQuery(params.getQueryParam(),params.getStartTime(),params.getEndTime(),params.getDateField());
        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder = buildAggregation(params);
        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, params.getIndex_name());

        MultiBucketsAggregation terms  = aggregations.get("aggs");

        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

        Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();

        for(MultiBucketsAggregation.Bucket bucket:terms.getBuckets()) {
            //Sum sum = bucket.getAggregations().get(params.getY_agg());
            //bucketmap.put(bucket.getKeyAsString(),sum.getValue());
            NumericMetricsAggregation.SingleValue sum = bucket.getAggregations().get(params.getY_agg());
            bucketmap.put(bucket.getKeyAsString(),(Double.isInfinite(sum.value())||Double.isNaN(sum.value()))?0:sum.value());
        }
        list.add(bucketmap);
        return list;
    }

    @Override
    public List<Map<String, Object>> getListByCountOfAggregation(VisualParam params) throws Exception {
        //查询条件
        BoolQueryBuilder boolQueryBuilder = buildQuery(params.getQueryParam(),params.getStartTime(),params.getEndTime(),params.getDateField());
        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder = buildAggregation(params);
        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, params.getIndex_name());

        MultiBucketsAggregation terms  = aggregations.get("aggs");

        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

        Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();

        for(MultiBucketsAggregation.Bucket bucket:terms.getBuckets()) {
            //ValueCount count = bucket.getAggregations().get(params.getY_agg());
            //bucketmap.put(bucket.getKeyAsString(),count.getValue());
            NumericMetricsAggregation.SingleValue count = bucket.getAggregations().get(params.getY_agg());
            bucketmap.put(bucket.getKeyAsString(),(Double.isInfinite(count.value())||Double.isNaN(count.value()))?0:count.value());
        }
        list.add(bucketmap);
        return list;
    }

    @Override
    public List<Map<String, Object>> getListByAvgOfAggregation(VisualParam params) throws Exception {
        //查询条件
        BoolQueryBuilder boolQueryBuilder = buildQuery(params.getQueryParam(),params.getStartTime(),params.getEndTime(),params.getDateField());
        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder = buildAggregation(params);
        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, params.getIndex_name());

        MultiBucketsAggregation terms  = aggregations.get("aggs");

        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

        Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();

        for(MultiBucketsAggregation.Bucket bucket:terms.getBuckets()) {
            //Avg avg = bucket.getAggregations().get(params.getY_agg());
            //bucketmap.put(bucket.getKeyAsString(),avg.getValue());
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
        BoolQueryBuilder boolQueryBuilder = buildQuery(params.getQueryParam(),params.getStartTime(),params.getEndTime(),params.getDateField());
        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder = buildAggregation(params);
        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, params.getIndex_name());

        MultiBucketsAggregation terms  = aggregations.get("aggs");

        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

        Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();

        for(MultiBucketsAggregation.Bucket bucket:terms.getBuckets()) {
            //Max max = bucket.getAggregations().get(params.getY_agg());
            //bucketmap.put(bucket.getKeyAsString(),max.getValue());
            NumericMetricsAggregation.SingleValue max = bucket.getAggregations().get(params.getY_agg());
            bucketmap.put(bucket.getKeyAsString(),(Double.isInfinite(max.value())||Double.isNaN(max.value()))?0:max.value());
        }
        list.add(bucketmap);
        return list;
    }

    @Override
    public List<Map<String, Object>> getListByMinOfAggregation(VisualParam params) throws Exception {
        //查询条件
        BoolQueryBuilder boolQueryBuilder = buildQuery(params.getQueryParam(),params.getStartTime(),params.getEndTime(),params.getDateField());
        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder = buildAggregation(params);
        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, params.getIndex_name());

        MultiBucketsAggregation terms  = aggregations.get("aggs");

        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

        Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();

        for(MultiBucketsAggregation.Bucket bucket:terms.getBuckets()) {
            //Min min = bucket.getAggregations().get(params.getY_agg());
            NumericMetricsAggregation.SingleValue min = bucket.getAggregations().get(params.getY_agg());
            //bucketmap.put(bucket.getKeyAsString(),min.getValue());
            bucketmap.put(bucket.getKeyAsString(),(Double.isInfinite(min.value())||Double.isNaN(min.value()))?0:min.value());
        }
        list.add(bucketmap);
        return list;
    }

    /**
     *
     * @param queryParam
     * @param startTime 起始时间
     * @param endTime 截止时间
     * @param dateField 时间范围对应的字段名
     * @return
     */
    //TODO 查询参数更好的处理方式
    private BoolQueryBuilder buildQuery(Map<String,String> queryParam,String startTime,String endTime,String dateField){


        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //存在时间类型字段，需要添加时间范围参数查询
        if(dateField!=null&&!"".equals(dateField)){
            //时间范围，起始时间和截止时间都不允许为空
            if (startTime != null && !startTime.equals("") && endTime != null && !endTime.equals("")) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery(dateField).format("yyyy-MM-dd HH:mm:ss").gte(startTime).lte(endTime));
            }
        }
        //其他查询字段  各个条件用的must
        //TODO 条件之间的关系  and or 等等
        if(null!=queryParam){
            for(Map.Entry<String,String> entry:queryParam.entrySet()){
                boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
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

                switch(params.getIntervalType().toUpperCase()){
                    case "SECOND":
                        dateHis = DateHistogramInterval.seconds(params.getIntervalValue());
                        aggregationBuilder = AggregationBuilders.dateHistogram("aggs").field(params.getX_field()).fixedInterval(dateHis);
                        break;
                    case "MINUTE":
                        dateHis = DateHistogramInterval.minutes(params.getIntervalValue());
                        aggregationBuilder = AggregationBuilders.dateHistogram("aggs").field(params.getX_field()).fixedInterval(dateHis);
                        break;
                    case "HOURLY":
                        dateHis = DateHistogramInterval.hours(params.getIntervalValue());
                        aggregationBuilder = AggregationBuilders.dateHistogram("aggs").field(params.getX_field()).fixedInterval(dateHis);
                        break;
                    case "DAILY":
                        dateHis = DateHistogramInterval.days(params.getIntervalValue());
                        aggregationBuilder = AggregationBuilders.dateHistogram("aggs").field(params.getX_field()).fixedInterval(dateHis);
                        break;
                    case "WEEKLY"://ES目前不支持 N周、N月、N年的间隔设置
                        dateHis = DateHistogramInterval.WEEK;
                        aggregationBuilder = AggregationBuilders.dateHistogram("aggs").field(params.getX_field()).calendarInterval(dateHis);
                        break;
                    case "MONTHLY":
                        dateHis = DateHistogramInterval.MONTH;
                        aggregationBuilder = AggregationBuilders.dateHistogram("aggs").field(params.getX_field()).calendarInterval(dateHis);
                        break;
                    case "YEARLY":
                        dateHis = DateHistogramInterval.YEAR;
                        aggregationBuilder = AggregationBuilders.dateHistogram("aggs").field(params.getX_field()).calendarInterval(dateHis);
                        break;
                }
            }

        }else{
            aggregationBuilder = AggregationBuilders.terms("aggs").field(params.getX_field()).order(BucketOrder.compound(BucketOrder.aggregation(params.getY_agg(),asc))).size(params.getSize());
        }
        switch(params.getY_agg().toUpperCase()){
            case "SUM":
                // 在bucket上聚合metric查询sum
                SumAggregationBuilder sumBuilder = AggregationBuilders.sum(params.getY_agg()).field(params.getY_field());
                aggregationBuilder.subAggregation(sumBuilder);
                break;
            case "COUNT":
                // 在bucket上聚合metric查询count
                ValueCountAggregationBuilder countBuilder = AggregationBuilders.count(params.getY_agg()).field(params.getX_field());
                aggregationBuilder.subAggregation(countBuilder);
                break;
            case "AVERAGE":
                // 在bucket上聚合metric查询avg
                AvgAggregationBuilder avgBuilder = AggregationBuilders.avg(params.getY_agg()).field(params.getY_field());
                aggregationBuilder.subAggregation(avgBuilder);
                break;
            case "MAX":
                // 在bucket上聚合metric查询min
                MaxAggregationBuilder maxBuilder = AggregationBuilders.max(params.getY_agg()).field(params.getY_field());
                aggregationBuilder.subAggregation(maxBuilder);
                break;
            case "MIN":
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

    /**
     * 嵌套聚合
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public Map<String,Object> getMultiAggregation4dateset(VisualParam params) throws Exception {
        String dateField = "logdate";//时间轴涉及字段
        String agg_first = "ipv4_dst_addr";//第一层聚合字段，目的IP
        String agg_second = "l4_dst_port";//第二层聚合字段，目的端口

        //查询条件
        BoolQueryBuilder boolQueryBuilder = buildQuery(params.getQueryParam(),params.getStartTime(),params.getEndTime(),dateField);

        // 当聚合结果为空时，需要填充0，设置需要填充0的范围
        ExtendedBounds extendedBounds = new ExtendedBounds(params.getStartTime(),params.getEndTime());
        //30秒时间隔，时间轴，最外层聚合builder
        DateHistogramInterval dateHis = DateHistogramInterval.seconds(30);
        AggregationBuilder aggregationBuilder = AggregationBuilders
                .dateHistogram(dateField)//别名
                .field(dateField)//进行时间间隔聚合的时间字段
                .fixedInterval(dateHis)
                .minDocCount(0)//为空时填充0
                // 需要填充0的范围
                .extendedBounds(extendedBounds);
        //第一层聚合 目的IP
        TermsAggregationBuilder termFirst = AggregationBuilders.terms(agg_first).field(agg_first).size(5);
        //第二层聚合 端口
        TermsAggregationBuilder termSecond = AggregationBuilders.terms(agg_second).field(agg_second).size(5);
        //计算count
        ValueCountAggregationBuilder countBuilder = AggregationBuilders.count("count").field(agg_second);
        //嵌套逻辑
        termSecond.subAggregation(countBuilder);
        termFirst.subAggregation(termSecond);
        aggregationBuilder.subAggregation(termFirst);
        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, params.getIndex_name());

        MultiBucketsAggregation terms  = aggregations.get(dateField);

        /**
         * 组装报表所需数据格式 eg:
         * {
         *  dimensions: ["xAxis","192.168.1.1-8080","192.168.1.1-8443"],
         *  source: [
         *   {"xAxis":"2020-02-02 11:00:00","192.168.1.1-8080":200,"192.168.1.1-8443":100},
         *   {"xAxis":"2020-02-02 11:01:00","192.168.1.1-8080":250,"192.168.1.1-8443":150}
         *   ]
         * }
         *
         */
        //可定义成全局final变量，具体值对数据无影响，保证dimensions和source中统一即可
        String xAxis = "xAxis";
        //dimensions,数据是有序的
        LinkedList<String> dimensions = new LinkedList<>();
        dimensions.add(xAxis);//作为X轴显示的信息
        //source
        //List顺序不影响显示
        //List中的Map顺序也不影响显示
        // 考虑到不确定前端会不会对有顺序的数据有更快的加载速度，后端使用其他Map也基本不影响效率，因此采用了Linked
        List<LinkedHashMap<String,Object>> source = new ArrayList<>();
        //填充数据
        for(MultiBucketsAggregation.Bucket timeBucket:terms.getBuckets()) {
            //第一级聚合的结果作为X轴显示
            String xAxisValue = timeBucket.getKeyAsString();
            LinkedHashMap<String,Object> xAxisMap = new LinkedHashMap<>();
            xAxisMap.put(xAxis,xAxisValue);
            //第2-n级
            ParsedStringTerms ipTerms = timeBucket.getAggregations().get(agg_first);
            for(MultiBucketsAggregation.Bucket ipBucket:ipTerms.getBuckets()){
                //第二层IP地址
                String ip = ipBucket.getKeyAsString();
                //一个IP下有N个端口
                ParsedStringTerms portTerms = ipBucket.getAggregations().get(agg_second);
                for(MultiBucketsAggregation.Bucket portBucket:portTerms.getBuckets()){
                    //第三层，端口
                    String portName = portBucket.getKeyAsString();
                    //metric的值
                    NumericMetricsAggregation.SingleValue value = portBucket.getAggregations().get("count");
                    //在最后一级的聚合遍历中，生成图例
                    dimensions.add(ip+"-"+portName);
                    xAxisMap.put(ip+"-"+portName,(Double.isInfinite(value.value())||Double.isNaN(value.value()))?0:value.value());
                }
            }
            source.add(xAxisMap);
        }
        Map<String,Object> result = new HashMap<>();
        result.put("dimensions",dimensions);
        result.put("source",source);
        return result;
    }

    /**
     * 嵌套聚合
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public Map<String,LinkedList<Map<String,Object>>> getMultiDateHistogramAggregation(VisualParam params) throws Exception {
        String dateField = "logdate";//时间轴涉及字段
        String agg_first = "ipv4_dst_addr";//第一层聚合字段，目的IP
        String agg_second = "l4_dst_port";//第二层聚合字段，目的端口

        //查询条件
        BoolQueryBuilder boolQueryBuilder = buildQuery(params.getQueryParam(),params.getStartTime(),params.getEndTime(),params.getDateField());

        // 当聚合结果为空时，需要填充0，设置需要填充0的范围
        ExtendedBounds extendedBounds = new ExtendedBounds(params.getStartTime(),params.getEndTime());
        //30秒时间隔，时间轴，最外层聚合builder
        DateHistogramInterval dateHis = DateHistogramInterval.seconds(30);
        AggregationBuilder aggregationBuilder = AggregationBuilders
                .dateHistogram(params.getDateField())//别名
                .field(params.getDateField())//进行时间间隔聚合的时间字段
                .fixedInterval(dateHis)
                .minDocCount(0)//为空时填充0
                // 需要填充0的范围
                .extendedBounds(extendedBounds);
        //第一层聚合 目的IP
        TermsAggregationBuilder termFirst = AggregationBuilders.terms(agg_first).field(agg_first).size(5);
        //第二层聚合 端口
        TermsAggregationBuilder termSecond = AggregationBuilders.terms(agg_second).field(agg_second).size(5);
        //计算count
        ValueCountAggregationBuilder countBuilder = AggregationBuilders.count("count").field(agg_second);
        //嵌套逻辑
        termSecond.subAggregation(countBuilder);
        termFirst.subAggregation(termSecond);
        aggregationBuilder.subAggregation(termFirst);
        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, params.getIndex_name());

        MultiBucketsAggregation terms  = aggregations.get(dateField);

        //IP+端口作为复合key，用set获取总的集合
        Set<String> keySet = new HashSet<>();
        //时间,带排序的，由于返回结果的时间是有序的，set集合也要保证是有序的
        Set<String> timeSet = new LinkedHashSet<>();

        //组装set集合
        for(MultiBucketsAggregation.Bucket timeBucket:terms.getBuckets()) {
            //时间
            timeSet.add(timeBucket.getKeyAsString());
            //一个时间下有N个IP
            ParsedStringTerms ipTerms = timeBucket.getAggregations().get(agg_first);
            for(MultiBucketsAggregation.Bucket ipBucket:ipTerms.getBuckets()){
                //第二层IP地址
                String ip = ipBucket.getKeyAsString();
                //一个IP下有N个端口
                ParsedStringTerms portTerms = ipBucket.getAggregations().get(agg_second);
                for(MultiBucketsAggregation.Bucket portBucket:portTerms.getBuckets()){
                    //第三层，端口
                    String portName = portBucket.getKeyAsString();
                    //NumericMetricsAggregation.SingleValue value = portBucket.getAggregations().get("count");
                    //"ip-端口"组成的key
                    keySet.add(ip+"-"+portName);
                    //System.out.println(time+"--"+ip+"--"+protocalName+"--"+value.value());
                }
            }
        }
        //数据返回集合
        //map的key代表聚合的字段组合成key，如IP+protocol_name，也就是图表显示的图例
        //value为该key对应的一条线上点的集合,使用linkedList保证结果有序
        //每个点为一个Map<String,Object>，存储时间和该时间对应的metric后的值
        Map<String,LinkedList<Map<String,Object>>> result = new HashMap<>();
        //将所有set组合成结果集，并初始化值
        for(String key:keySet){
            LinkedList<Map<String, Object>> line = new LinkedList<Map<String,Object>>();
            for(String time:timeSet){
                Map<String, Object> point = new HashMap<String, Object>();
                //初始化值
                point.put("time",time);
                point.put("value",0);
                line.add(point);
            }
            result.put(key,line);
        }
        //时间轴上的点在时间轴的位置,设置了时间聚合时，没有值也会置0，可以确定时间
        int i=0;
        //重新遍历并赋值
        for(MultiBucketsAggregation.Bucket timeBucket:terms.getBuckets()) {
            //时间
            String time = timeBucket.getKeyAsString();
            //一个时间下有N个IP
            ParsedStringTerms ipTerms = timeBucket.getAggregations().get(agg_first);
            for(MultiBucketsAggregation.Bucket ipBucket:ipTerms.getBuckets()){
                //第二层IP地址
                String ip = ipBucket.getKeyAsString();
                //一个IP下有N个端口
                ParsedStringTerms portTerms = ipBucket.getAggregations().get(agg_second);
                for(MultiBucketsAggregation.Bucket portBucket:portTerms.getBuckets()){
                    //第三层，端口
                    String portName = portBucket.getKeyAsString();
                    NumericMetricsAggregation.SingleValue value = portBucket.getAggregations().get("count");
                    //替换成有值的点
                    Map<String,Object> point = new HashMap<>();
                    point.put("time",time);
                    point.put("value",value.value());
                    //替换
                    result.get(ip+"-"+portName).set(i,point);
                }
            }
            i++;
        }
        return result;
    }
    public static void main(String[] args){
        LinkedList<String> dimensions = new LinkedList<>();
        dimensions.add("product");
        dimensions.add("2015");
        dimensions.add("2016");
        List<Map<String,Object>> listMap = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        map.put("product","Matcha Latte");
        map.put("2015",100);
        map.put("2016",200);
        Map<String,Object> map1 = new HashMap<>();
        map1.put("product","Matcha Latte1");
        map1.put("2015",150);
        map1.put("2016",250);
        listMap.add(map);
        listMap.add(map1);
        System.out.println(JSONArray.fromObject(dimensions).toString());
        System.out.println(JSONArray.fromObject(listMap).toString());
    }
}

package com.hs.elsearch.dao.biDao.impl;

import com.alibaba.fastjson.JSONObject;
import com.hs.elsearch.dao.biDao.IBIDao;
import com.hs.elsearch.entity.*;
import com.hs.elsearch.template.SearchTemplate;
import com.hs.elsearch.util.ElasticConstant;
import com.hs.elsearch.util.HSDateUtil;
import joptsimple.internal.Strings;
import net.sf.json.JSONArray;
import org.apache.commons.collections.map.LinkedMap;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.range.DateRangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.IpRangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.*;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import scala.collection.immutable.ListSet;

import java.text.DecimalFormat;
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

    /*@Override
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
*/
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
     *
     * @param queryConditions 除时间外的查询条件
     * @param queryConnectionType 除时间外的查询条件的链接类型
     * @param startTime 起始时间
     * @param endTime 截止时间
     * @param dateField 时间范围对应的字段名
     * @return
     */
    //TODO 查询参数更好的处理方式
    private BoolQueryBuilder buildQuery(ArrayList<QueryCondition> queryConditions,String queryConnectionType, String startTime, String endTime, String dateField){
        //时间和其他参数的连接，使用must连接
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
        if(null!=queryConditions&&queryConditions.size()>0){
            //除时间外的其他条件的query对象定义，
            BoolQueryBuilder queriesBuilder = QueryBuilders.boolQuery();
            for(QueryCondition queryCondition:queryConditions){
                switch(queryCondition.getSearchType()){
                    case "term":
                        if("should".equals(queryConnectionType)){
                            queriesBuilder.should(QueryBuilders.termQuery(queryCondition.getSearchField(), queryCondition.getSearchValue()));
                        }else{
                            queriesBuilder.must(QueryBuilders.termQuery(queryCondition.getSearchField(), queryCondition.getSearchValue()));
                        }
                        break;
                    case "range":
                        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                        ArrayList<QueryRange> searchValue = (ArrayList<QueryRange>) queryCondition.getSearchValue();
                        for(QueryRange rangeQuery : searchValue){
                            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(queryCondition.getSearchField());
                            //左侧范围
                            if(rangeQuery.getIsGtInclude()&&rangeQuery.getGt()!=null){
                                rangeQueryBuilder.gte(rangeQuery.getGt());
                            }else{
                                rangeQueryBuilder.gt(rangeQuery.getGt());
                            }
                            //右侧范围
                            if(rangeQuery.getIsLtInclude()&&rangeQuery.getLt()!=null){
                                rangeQueryBuilder.lte(rangeQuery.getLt());
                            }else{
                                rangeQueryBuilder.lt(rangeQuery.getLt());
                            }

                            //多个条件的组装方式  must/should
                            if("should".equals(queryCondition.getConntectionType())){
                                boolQuery.should(rangeQueryBuilder);
                            }else{
                                boolQuery.must(rangeQueryBuilder);
                            }

                        }
                        if("should".equals(queryConnectionType)){
                            queriesBuilder.should(boolQuery);
                        }else{
                            queriesBuilder.must(boolQuery);
                        }
                        break;
                    case "wildcard":
                        if("should".equals(queryConnectionType)){
                            queriesBuilder.should(QueryBuilders.wildcardQuery(queryCondition.getSearchField(), queryCondition.getSearchValue().toString()));
                        }else{
                            queriesBuilder.must(QueryBuilders.termQuery(queryCondition.getSearchField(), queryCondition.getSearchValue().toString()));
                        }
                        break;
                    case "exists":
                        if("should".equals(queryConnectionType)){
                            queriesBuilder.should(QueryBuilders.existsQuery(queryCondition.getSearchField()));
                        }else{
                            queriesBuilder.must(QueryBuilders.existsQuery(queryCondition.getSearchField()));
                        }
                        break;
                    default://默认与term同级
                        if("should".equals(queryConnectionType)){
                            queriesBuilder.should(QueryBuilders.termQuery(queryCondition.getSearchField(), queryCondition.getSearchValue()));
                        }else{
                            queriesBuilder.must(QueryBuilders.termQuery(queryCondition.getSearchField(), queryCondition.getSearchValue()));
                        }
                        break;
                }

            }
            boolQueryBuilder.must(queriesBuilder);
        }

        return boolQueryBuilder;
    }
    /**
     * 创建聚合对象
     * @param params
     * @return
     */
    private AggregationBuilder buildAggregations(VisualParam params){
        AggregationBuilder aggregationBuilder = null;
        AggregationBuilder orderMetric = null;
        //获取排序metric信息
        if(params.getMetricList().size()>0){
            //获取第一个metric信息作为聚合排序信息
            Metric metric = params.getMetricList().get(0);
            orderMetric = getMetricAggregation(metric);
        }
        //遍历聚合字段，进行倒序遍历，从倒数第二个聚合字段开始
        for(int i=params.getBucketList().size()-1;i>=0;i--){
            //获取最后一个聚合信息，添加metric信息
            if(i==params.getBucketList().size()-1){
                //最后一个聚合字段生成的对象
                aggregationBuilder = getBucketAggregation(params.getBucketList().get(i),aggregationBuilder,orderMetric,params);
                //生成的聚合对象不为空时
                if(null!=aggregationBuilder){
                    //如果最后一个聚合不是terms聚合，因为没有设置排序字段，因此，指标agg未添加到其下一级agg中，需要加上
                    if(!"TERMS".equals(params.getBucketList().get(i).getAggType().toUpperCase())){
                        aggregationBuilder.subAggregation(orderMetric);
                    }

                    //第一个metric信息已经通过order进行了添加，添加后续的metric
                    for(int j=1;j<params.getMetricList().size();j++){
                        AggregationBuilder metricBuilder = getMetricAggregation(params.getMetricList().get(j));
                        //不为空时添加，避免异常
                        if(metricBuilder!=null){
                            aggregationBuilder.subAggregation(metricBuilder);
                        }
                    }
                }
            }else{
                //后续聚合，进行嵌套
                aggregationBuilder = getBucketAggregation(params.getBucketList().get(i),aggregationBuilder,orderMetric,params);
            }

        }
        return aggregationBuilder;
    }

    /**
     * 递归
     * 每次生成的agg对象都是下一个agg对象的子聚合
     * @param bucket
     * @return
     */
    private AggregationBuilder getBucketAggregation(Bucket bucket,AggregationBuilder aggregationBuilderChild,AggregationBuilder orderMetric,VisualParam params){

        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder = null;
        switch(bucket.getAggType().toUpperCase()){
            case "DATE HISTOGRAM"://日期 直方图
                //默认1分钟间隔
                DateHistogramInterval dateHis = DateHistogramInterval.minutes(1);
                switch(bucket.getIntervalType().toUpperCase()){
                    case "SECOND":
                        dateHis = DateHistogramInterval.seconds(bucket.getIntervalValue());
                        break;
                    case "MINUTE":
                        dateHis = DateHistogramInterval.minutes(bucket.getIntervalValue());
                        break;
                    case "HOURLY":
                        dateHis = DateHistogramInterval.hours(bucket.getIntervalValue());
                        break;
                    case "DAILY":
                        dateHis = DateHistogramInterval.days(bucket.getIntervalValue());
                        break;
                    case "WEEKLY"://ES目前不支持 N周、N月、N年的间隔设置
                        dateHis = DateHistogramInterval.WEEK;
                        break;
                    case "MONTHLY":
                        dateHis = DateHistogramInterval.MONTH;
                        break;
                    case "YEARLY":
                        dateHis = DateHistogramInterval.YEAR;
                        break;
                }
                // 当聚合结果为空时，需要填充0，设置需要填充0的范围
                ExtendedBounds extendedBounds = new ExtendedBounds(params.getStartTime(),params.getEndTime());
                //聚合的别名使用“aggType-aggField”命名
                aggregationBuilder = AggregationBuilders
                        .dateHistogram(bucket.getAggType()+"-"+bucket.getField())
                        .field(bucket.getField())
                        .fixedInterval(dateHis)
                        .format("yyyy-MM-dd HH:mm:ss")
                        .minDocCount(0)//当数据结果为0时也会显示聚合结果，其他字段该值为1，为了保证只返回值>1的数据
                        .extendedBounds(extendedBounds);
                break;
            case "TERMS"://term级的聚合
                //term聚合的默认排序是按照count文档数倒叙，再按key正序排列
                //eg:
                //order": [{
                //		"_count": "desc"
                //		}, {
                //		"_key": "asc"
                //		}
                //	]
                //BucketOrder defaultTermAggOrder = BucketOrder.compound(BucketOrder.aggregation("_count",false),BucketOrder.aggregation("_key",true));
                //order字段不为空，并且排序metric也不为空，添加排序信息
                if(orderMetric!=null&&!Strings.isNullOrEmpty(bucket.getSort())){
                    //正序为true
                    boolean order = "asc".equals(bucket.getSort())?true:false;
                    BucketOrder termAggOrder = BucketOrder.aggregation( orderMetric.getName(),order);
                    //聚合的别名使用“aggType-aggField”命名
                    aggregationBuilder = AggregationBuilders.terms(bucket.getAggType()+"-"+bucket.getField())
                            .field(bucket.getField())
                            .order(termAggOrder)
                            .minDocCount(1)//数据量为0的不显示
                            .size(bucket.getSize());
                    //添加排序对应的metric字段
                    aggregationBuilder.subAggregation(orderMetric);
                }else{
                    aggregationBuilder = AggregationBuilders.terms(bucket.getAggType()+"-"+bucket.getField())
                            .field(bucket.getField())
                            .size(bucket.getSize());
                }
                break;
            case "RANGE"://数字范围

                RangeAggregationBuilder rangeBuilder = AggregationBuilders.range(bucket.getAggType()+"-"+bucket.getField()).field(bucket.getField());
                //遍历ranges，依次将范围数据写入聚合对象中
                for(Map<String,Object> range : bucket.getRanges()){

                    //根据from 和to参数，判定要设置的范围，from是包含，to是不包含
                    if(null!=range.get("from")&&null!=range.get("to")){
                        //key为自定义范围的别名
                        if(range.get("key")==null){
                            rangeBuilder.addRange((null==range.get("from"))?null:Double.valueOf(range.get("from").toString()),(null==range.get("to"))?null:Double.valueOf(range.get("to").toString()));
                        }else{
                            rangeBuilder.addRange(range.get("key").toString(),(null==range.get("from"))?null:Double.valueOf(range.get("from").toString()),(null==range.get("to"))?null:Double.valueOf(range.get("to").toString()));
                        }
                    }else if(null!=range.get("from")&&null==range.get("to")){
                        if(range.get("key")==null){
                            rangeBuilder.addUnboundedFrom(Double.valueOf(range.get("from").toString()));
                        }else{
                            rangeBuilder.addUnboundedFrom(range.get("key").toString(),Double.valueOf(range.get("from").toString()));
                        }
                    }else if(null==range.get("from")&&null!=range.get("to")){
                        if(range.get("key")==null){
                            rangeBuilder.addUnboundedFrom(Double.valueOf(range.get("to").toString()));
                        }else{
                            rangeBuilder.addUnboundedFrom(range.get("key").toString(),Double.valueOf(range.get("to").toString()));
                        }
                    }else{
                        //不做处理
                    }
                }
                aggregationBuilder = rangeBuilder;
                break;
            case "DATE RANGE"://日期范围

                DateRangeAggregationBuilder dateRangeBuilder = AggregationBuilders.dateRange(bucket.getAggType()+"-"+bucket.getField()).field(bucket.getField()).format("yyyy-MM-dd HH:mm:ss");

                //遍历ranges，依次将范围数据写入聚合对象中
                for(Map<String,Object> range : bucket.getRanges()){
                    //根据from 和to参数，判定要设置的范围，from是包含，to是不包含
                    if(null!=range.get("from")&&null!=range.get("to")){
                        //key为自定义范围的别名
                        if(range.get("key")==null){
                            dateRangeBuilder.addRange((null==range.get("from"))?null:range.get("from").toString(),range.get("to").toString());
                        }else{
                            dateRangeBuilder.addRange(range.get("key").toString(),range.get("from").toString(),range.get("to").toString());
                        }

                    }else if(null!=range.get("from")&&null==range.get("to")){
                        if(range.get("key")==null){
                            dateRangeBuilder.addUnboundedFrom(range.get("from").toString());
                        }else{
                            dateRangeBuilder.addUnboundedFrom(range.get("key").toString(),range.get("from").toString());
                        }

                    }else if(null==range.get("from")&&null!=range.get("to")){
                        if(range.get("key")==null){
                            dateRangeBuilder.addUnboundedFrom(range.get("to").toString());
                        }else{
                            dateRangeBuilder.addUnboundedFrom(range.get("key").toString(),range.get("to").toString());
                        }
                    }else{
                        //不做处理
                    }
                }
                aggregationBuilder = dateRangeBuilder;
                break;
            case "IPV4 RANGE"://IP地址范围
                IpRangeAggregationBuilder ipRangeBuilder = AggregationBuilders.ipRange(bucket.getAggType()+"-"+bucket.getField()).field(bucket.getField());
                //遍历ranges，依次将范围数据写入聚合对象中
                for(Map<String,Object> range : bucket.getRanges()){
                    //根据from 和to参数，判定要设置的范围，from是包含，to是不包含
                    if(null!=range.get("from")&&null!=range.get("to")){
                        //key为自定义范围的别名
                        if(range.get("key")==null){
                            ipRangeBuilder.addRange((null==range.get("from"))?null:range.get("from").toString(),range.get("to").toString());
                        }else{
                            ipRangeBuilder.addRange(range.get("key").toString(),range.get("from").toString(),range.get("to").toString());
                        }

                    }else if(null!=range.get("from")&&null==range.get("to")){
                        if(range.get("key")==null){
                            ipRangeBuilder.addUnboundedFrom(range.get("from").toString());
                        }else{
                            ipRangeBuilder.addUnboundedFrom(range.get("key").toString(),range.get("from").toString());
                        }

                    }else if(null==range.get("from")&&null!=range.get("to")){
                        if(range.get("key")==null){
                            ipRangeBuilder.addUnboundedFrom(range.get("to").toString());
                        }else{
                            ipRangeBuilder.addUnboundedFrom(range.get("key").toString(),range.get("to").toString());
                        }
                    }else{
                        //不做处理
                    }
                }
                aggregationBuilder = ipRangeBuilder;
                break;
        }
        /*
        if("Date Histogram".toUpperCase().equals(bucket.getAggType().toUpperCase())){
            //默认1分钟间隔
            DateHistogramInterval dateHis = DateHistogramInterval.minutes(1);
            if(bucket.getIntervalType()!=null){
                switch(bucket.getIntervalType().toUpperCase()){
                    case "SECOND":
                        dateHis = DateHistogramInterval.seconds(bucket.getIntervalValue());
                        break;
                    case "MINUTE":
                        dateHis = DateHistogramInterval.minutes(bucket.getIntervalValue());
                        break;
                    case "HOURLY":
                        dateHis = DateHistogramInterval.hours(bucket.getIntervalValue());
                        break;
                    case "DAILY":
                        dateHis = DateHistogramInterval.days(bucket.getIntervalValue());
                        break;
                    case "WEEKLY"://ES目前不支持 N周、N月、N年的间隔设置
                        dateHis = DateHistogramInterval.WEEK;
                        break;
                    case "MONTHLY":
                        dateHis = DateHistogramInterval.MONTH;
                        break;
                    case "YEARLY":
                        dateHis = DateHistogramInterval.YEAR;
                        break;
                }
                // 当聚合结果为空时，需要填充0，设置需要填充0的范围
                ExtendedBounds extendedBounds = new ExtendedBounds(params.getStartTime(),params.getEndTime());
                //聚合的别名使用“aggType-aggField”命名
                aggregationBuilder = AggregationBuilders
                        .dateHistogram(bucket.getAggType()+"-"+bucket.getField())
                        .field(bucket.getField())
                        .fixedInterval(dateHis)
                        .format("yyyy-MM-dd HH:mm:ss")
                        .minDocCount(0)//当数据结果为0时也会显示聚合结果，其他字段该值为1，为了保证只返回值>1的数据
                        .extendedBounds(extendedBounds);
            }

        }else if(bucket.getAggType().toUpperCase().indexOf("RANGE")>=0){
            aggregationBuilder = AggregationBuilders.range(bucket.getAggType()+"-"+bucket.getField());
        }else{
            //term聚合的默认排序是按照count文档数倒叙，再按key正序排列
            //eg:
            //order": [{
            //		"_count": "desc"
            //		}, {
            //		"_key": "asc"
            //		}
            //	]
            //BucketOrder defaultTermAggOrder = BucketOrder.compound(BucketOrder.aggregation("_count",false),BucketOrder.aggregation("_key",true));
            //order字段不为空，并且排序metric也不为空，添加排序信息
            if(orderMetric!=null&&!Strings.isNullOrEmpty(bucket.getSort())){
                //正序为true
                boolean order = "asc".equals(bucket.getSort())?true:false;
                BucketOrder termAggOrder = BucketOrder.aggregation( orderMetric.getName(),order);
                //聚合的别名使用“aggType-aggField”命名
                aggregationBuilder = AggregationBuilders.terms(bucket.getAggType()+"-"+bucket.getField())
                        .field(bucket.getField())
                        .order(termAggOrder)
                        .minDocCount(1)//数据量为0的不显示
                        .size(bucket.getSize());
                        //添加排序对应的metric字段
                aggregationBuilder.subAggregation(orderMetric);
            }else{
                aggregationBuilder = AggregationBuilders.terms(bucket.getAggType()+"-"+bucket.getField())
                        .field(bucket.getField())
                        .size(bucket.getSize());
            }

        }
         */
        //不为空时，将aggregationBuilderChild添加到新生成的bucket子聚合中。
        if(null!=aggregationBuilderChild){
            aggregationBuilder.subAggregation(aggregationBuilderChild);
        }
        return aggregationBuilder;
    }

    /**
     * 根据metric对象信息生成metric Aggregation
     * @param metric
     * @return
     */
    private AggregationBuilder getMetricAggregation(Metric metric){
        AggregationBuilder metricBuilder = null;
        String aliasName = !Strings.isNullOrEmpty(metric.getAliasName())?metric.getAliasName():(metric.getAggType()+"-"+metric.getField());//别名
        switch(metric.getAggType().toUpperCase()){
            case "SUM":
                // 在bucket上聚合metric查询sum
                metricBuilder = AggregationBuilders.sum(aliasName).field(metric.getField());
                break;
            case "COUNT":
                // 在bucket上聚合metric查询count
                metricBuilder = AggregationBuilders.count(aliasName).field(metric.getField());
                break;
            case "AVERAGE":
                // 在bucket上聚合metric查询avg
                metricBuilder = AggregationBuilders.avg(aliasName).field(metric.getField());
                break;
            case "MAX":
                // 在bucket上聚合metric查询min
                metricBuilder = AggregationBuilders.max(aliasName).field(metric.getField());
                break;
            case "MIN":
                // 在bucket上聚合metric查询min
                metricBuilder = AggregationBuilders.min(aliasName).field(metric.getField());
                break;
        }
        return metricBuilder;
    }

    /**
     * 递归方法，循环遍历聚合结果
     * @param params 请求参数
     * @param i 第i次聚合
     * @param bucket 第i次聚合结果的bucket对相同
     * @param xAxisMap 第一级聚合后的结果作为X轴，X轴上的某个点对应的Y轴结果集合
     * @param dimensions 图例，2-n次组合后的key放入到该对象中
     * @param nextKey 需要组装成的图例，多个聚合字段以“-”连接 eg：192.168.1.1-8080-count
     */
    public void getResult4DataSet(VisualParam params,int i,MultiBucketsAggregation.Bucket bucket,LinkedHashMap<String,Object> xAxisMap,LinkedHashSet<String> dimensions,String nextKey){
        //如果是最后一层bucket
        if(i==params.getBucketList().size()){
            //填充数据
            setData(params.getMetricList(),bucket,xAxisMap,dimensions,nextKey,params.getUnit());
        }else{

            //获取这次聚合结果对应的
            Bucket bucketEntity = params.getBucketList().get(i);
            //获取聚合结果
            MultiBucketsAggregation terms = bucket.getAggregations().get(bucketEntity.getAggType()+"-"+bucketEntity.getField());
            //下一次递归时  i需要+1，进入第i+1层聚合结果的处理中
            i++;
            //遍历第i层的结果集
            for(MultiBucketsAggregation.Bucket aggBucket:terms.getBuckets()){
                getResult4DataSet(params,i,aggBucket,xAxisMap,dimensions,nextKey+aggBucket.getKeyAsString()+"-");
            }
        }
    }

    /**
     * 根据metric指标信息填充数据
     * @param metricList 指标类聚合参数
     * @param bucket agg结果集
     * @param xAxisMap 数据点
     * @param dimensions 图例数据
     * @param nextKey 所有父级聚合结果组合成的key
     * @param unit 单位换算
     */
    public void setData(ArrayList<Metric> metricList,MultiBucketsAggregation.Bucket bucket,LinkedHashMap<String,Object> xAxisMap,LinkedHashSet<String> dimensions,String nextKey,String unit){
        for(Metric metric:metricList){
            NumericMetricsAggregation.SingleValue value = bucket.getAggregations().get(!Strings.isNullOrEmpty(metric.getAliasName())?metric.getAliasName():(metric.getAggType()+"-"+metric.getField()));
            //图例名称，如果别名是null 则显示聚合类型名称，否则显示别名
            //String line_name = nextKey+(Strings.isNullOrEmpty(metric.getAliasName())?metric.getAggType():(metric.getAliasName()));
            String line_name = nextKey+("".equals(metric.getAliasName())?metric.getAggType():(metric.getAliasName()==null?"":metric.getAliasName()));
            //如果图例名称最后包含“-”，则去掉
            line_name = line_name.matches(".*\\-")?line_name.substring(0,line_name.length()-1):line_name;
            //String line_name = nextKey+(Strings.isNullOrEmpty(metric.getAliasName())?metric.getAggType():metric.getAliasName());
            //在最后一级的聚合遍历中，生成图例
            dimensions.add(line_name);
            Double pointValue = (Double.isInfinite(value.value())||Double.isNaN(value.value()))?0:value.value();
            //数据换算
            xAxisMap.put(line_name,valueFormatter(pointValue,unit));
        }
    }
    /**
     * 根据参数创建AggregationBuilder
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
        List<SortBuilder> sortBuilders = new ArrayList<>();
        if(ElasticConstant.HSDATA_DASHBOARD.equals(fieldName)){
            //是否可编辑排序
            sortBuilders.add(SortBuilders.fieldSort("dashboard.editable").order(SortOrder.DESC));
        }else if(ElasticConstant.HSDATA_VISUAL.equals(fieldName)){
            sortBuilders.add(SortBuilders.fieldSort("visualization.editable").order(SortOrder.DESC));
            //图表类型排序
            sortBuilders.add(SortBuilders.fieldSort("visualization.type").order(SortOrder.DESC));
        }else{
            //其他情况无排序
        }
        //SortBuilder sortBuilder = SortBuilders.fieldSort("visualization.type").order(SortOrder.DESC);
        //查询条件
        boolQueryBuilder.must(QueryBuilders.constantScoreQuery(QueryBuilders.existsQuery(fieldName)));
        //TODO 分页
        List<Map<String, Object>> list = searchTemplate.getListByBuilder(boolQueryBuilder, sortBuilders, 0, 999, indexName);
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
     * @param params 图表参数
     * @return
     * @throws Exception
     */
    @Override
    public Map<String,Object> getMultiAggregation4dateset(VisualParam params) throws Exception {

        Map<String,Object> result = new HashMap<>();
        //params参数为null的判定
        if(null==params){
            return result;
        }else{
            /*
            params.setDateField("logdate");
            //X轴 bucket 第一层时间轴
            Bucket bucketDate = new Bucket("Date Histogram","logdate","SECOND",30,5,null);
            params.getBucketList().add(bucketDate);
            Bucket bucketIp = new Bucket("term","ipv4_dst_addr",null,null,3,"desc");
            params.getBucketList().add(bucketIp);
            Bucket bucketPort = new Bucket("term","l4_dst_port",null,null,3,"desc");
            params.getBucketList().add(bucketPort);
            //Y轴
            Metric metricCount = new Metric("count","logdate",null);
            Metric metricCount1 = new Metric("max","seqnum",null);
            params.getMetricList().add(metricCount);
            params.getMetricList().add(metricCount1);

            */
            //查询条件
            //BoolQueryBuilder boolQueryBuilder = buildQuery(params.getQueryParam(),params.getStartTime(),params.getEndTime(),params.getDateField());
            BoolQueryBuilder boolQueryBuilder = buildQuery(params.getQueryConditions(),params.getQueryConnectionType(),params.getStartTime(),params.getEndTime(),params.getDateField());
            //聚合条件
            AggregationBuilder aggregationBuilder = buildAggregations(params);

            //通过时间范围优化查询的index数量
            String [] indices =null;
            List<String> list=new ArrayList<String>();
            if(params.getDateField()!=null&&!"".equals(params.getDateField())){
                //时间范围，起始时间和截止时间都不允许为空
                if (params.getStartTime() != null && !params.getStartTime().equals("") && params.getEndTime() != null && !params.getEndTime().equals("")) {
                    indices = HSDateUtil.dateArea2Indices(params.getStartTime(),params.getEndTime(),params.getIndex_name());
                }else {
                    indices = params.getIndex_name().split(",");
                }
            }
            // 返回聚合的内容
            Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, indices);
            /**
             * 组装报表所需数据格式 eg:
             * {
             *  dimensions: ["xAxis","192.168.1.1-8080","192.168.1.1-8443"],
             *  source: [
             *   {"xAxis":"2020-02-02 11:00:00","192.168.1.1-8080":200,"192.168.1.1-8443":100},
             *   {"xAxis":"2020-02-02 11:01:00","192.168.1.1-8080":250,"192.168.1.1-8443":150}
             *   ]
             * }
             */
            if(null==aggregations) {
                //查询能够正常执行，但是存在返回结果为空的情况，为了防止出现异常，进行一次判定
            }else{

                //dimensions,数据是有序的。保证按照写入顺序排列，使用linked
                LinkedHashSet<String> dimensions = new LinkedHashSet<>();
                dimensions.add(ElasticConstant.XAXIS);//作为X轴显示的信息
                //source
                //List顺序不影响显示
                //List中的Map顺序也不影响显示
                // 考虑到不确定前端会不会对有顺序的数据有更快的加载速度，后端使用其他Map也基本不影响效率，因此采用了Linked
                List<LinkedHashMap<String,Object>> source = new ArrayList<>();
                //有聚合字段
                if(params.getBucketList().size()>0){
                    //获取第一个聚合字段信息，用以组装别名，获取对应数据
                    Bucket bucket = params.getBucketList().get(0);

                    //第一层无法使用递归，第一层和2-n层需要：声明一些对象以及相关处理
                    MultiBucketsAggregation terms  = aggregations.get(bucket.getAggType()+"-"+bucket.getField());
                    //第一层也会作为X轴的图标
                    for(MultiBucketsAggregation.Bucket xAxisBucket:terms.getBuckets()) {
                        //第一级聚合的结果作为X轴显示
                        String xAxisValue = xAxisBucket.getKeyAsString();
                        LinkedHashMap<String,Object> xAxisMap = new LinkedHashMap<>();
                        xAxisMap.put(ElasticConstant.XAXIS,xAxisValue);
                        //如果bucket聚合只有一层
                        if(params.getBucketList().size()==1){
                            setData(params.getMetricList(),xAxisBucket,xAxisMap,dimensions,"",params.getUnit());
                        }else{
                            //2-n层
                            //递归
                            getResult4DataSet(params,1,xAxisBucket,xAxisMap,dimensions,"");
                        }
                        source.add(xAxisMap);
                    }

                }else if(params.getBucketList().size()==0){
                    //TODO 无X轴的数据处理，如：Y轴计算count，返回单一的数字
                }else{

                }

                //补零
                for(LinkedHashMap<String,Object> points:source){//获取某个时间节点上对应的N条线的数据对象
                    for(String line:dimensions){//遍历N条线的名称
                        if(points.get(line)==null){//get不到数据的进行补零
                            points.put(line,0);
                        }
                    }
                }

                //如果X轴为时间(bucket第一个为X轴),且最后一个点的时间与截止时间相同（并且最后一个点的聚合值为0），去掉最后一个点
                if("Date Histogram".equals(params.getBucketList().get(0).getAggType())&&source.get(source.size()-1).get(ElasticConstant.XAXIS).equals(params.getEndTime())){
                    //将最后一个点去掉
                    source.remove(source.size()-1);
                }

                result.put(ElasticConstant.DIMENSIONS,dimensions);
                result.put(ElasticConstant.SOURCE,source);
            }
            return result;
        }

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

    /**
     * 数据换算
     * @param value 值
     * @param unit 换算单位 %/MB/GB/TB
     * @return
     */
    private Object valueFormatter(Double value,String unit){
        Object result = null;
        //保留小数点2位
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
        if(!Strings.isNullOrEmpty(unit)){
            try{
                switch (unit.toUpperCase()){
                    case "%":
                        Float folatValue = Float.parseFloat(value.toString());
                        folatValue = folatValue*100;
                        result = decimalFormat.format(folatValue);
                        break;
                    case "MB":
                        value = value/1024/1024;//byte -> MB
                        result = decimalFormat.format(value);
                        break;
                    case "GB":
                        value = value/1024/1024/1024;//byte -> GB
                        result = decimalFormat.format(value);
                        break;
                    case "TB":
                        value = value/1024/1024/1024/1024;//byte -> TB
                        result = decimalFormat.format(value);
                        break;
                    default:
                        result = value;
                        break;
                }
            }catch(Exception e){
                result = value.toString();
            }
        }else{
            result = value;
        }
        return result;
    }
    /*
    private boolean checkTimeInterval(String timeX,String timeY,String intervalType,int intervalValue){
        try{
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateX = formatter.parse(timeX);
            Date dateY = formatter.parse(timeY);
            //差值 秒 取绝对值
            long difValue = Math.abs((dateX.getTime() - dateY.getTime())/1000);
            //计算间隔 秒
            long intervalSeconds=1L;
            switch(intervalType.toUpperCase()){
                case "SECOND":
                    intervalSeconds = intervalValue*1;
                    break;
                case "MINUTE":
                    intervalSeconds = intervalValue*60;
                    break;
                case "HOURLY":
                    intervalSeconds = intervalValue*60*60;
                    break;
                case "DAILY":
                    intervalSeconds = intervalValue*60*60*24;
                    break;
                case "WEEKLY":
                    intervalSeconds = intervalValue*60*60*24*7;
                    break;
                case "MONTHLY":
                    intervalSeconds = intervalValue*60*60*24*7*30;
                    break;
                case "YEARLY":
                    intervalSeconds = intervalValue*60*60*24*7*30*365;
                    break;
                default:
                    intervalSeconds = 1L;
                    break;
            }

        }catch (Exception e){

        }




    }*/
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

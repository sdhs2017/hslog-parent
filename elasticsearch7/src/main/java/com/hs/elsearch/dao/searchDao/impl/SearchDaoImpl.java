package com.hs.elsearch.dao.searchDao.impl;

import com.hs.elsearch.dao.searchDao.ISearchDao;
import com.hs.elsearch.entity.*;
import com.hs.elsearch.template.SearchTemplate;
import com.hs.elsearch.util.ElasticConstant;
import joptsimple.internal.Strings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.range.DateRangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.IpRangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregation;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DecimalFormat;
import java.util.*;


public class SearchDaoImpl implements ISearchDao {
    @Autowired
    SearchTemplate searchTemplate;

    /**
     *
     * @param queryConditions 除时间外的查询条件
     * @param queryConnectionType 除时间外的查询条件的链接类型
     * @param startTime 起始时间
     * @param endTime 截止时间
     * @param dateField 时间范围对应的字段名
     * @return
     */
    private BoolQueryBuilder buildQuery(ArrayList<QueryCondition> queryConditions, String queryConnectionType, String startTime, String endTime, String dateField){
        //时间和其他参数的连接，使用must连接
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //存在时间类型字段，需要添加时间范围参数查询
        if(dateField!=null&&!"".equals(dateField)){
            //时间范围，起始时间和截止时间都不允许为空
            if (startTime != null && !startTime.equals("") && endTime != null && !endTime.equals("")) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery(dateField).format("yyyy-MM-dd HH:mm:ss").gte(startTime).lte(endTime));
            }
        }else{
            //时间戳在参数对象生成时已经进行了不为空的判定
            //如果真实存在，不添加条件不影响查询的执行
        }
        //其他查询字段  各个条件用的must
        //TODO 条件之间的关联，a and (b or c) 还未实现
        if(null!=queryConditions&&queryConditions.size()>0){
            //除时间外的其他条件的query对象定义，
            BoolQueryBuilder queriesBuilder = QueryBuilders.boolQuery();
            for(QueryCondition queryCondition:queryConditions){
                switch(queryCondition.getSearchType().toUpperCase()){
                    case "RANGE":
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
                            if("SHOULD".equals(queryCondition.getConntectionType().toUpperCase())){
                                boolQuery.should(rangeQueryBuilder);
                            }else{
                                boolQuery.must(rangeQueryBuilder);
                            }

                        }
                        if("SHOULD".equals(queryConnectionType.toUpperCase())){
                            queriesBuilder.should(boolQuery);
                        }else{
                            queriesBuilder.must(boolQuery);
                        }
                        break;
                    case "WILDCARD":
                        if("SHOULD".equals(queryConnectionType.toUpperCase())){
                            queriesBuilder.should(QueryBuilders.wildcardQuery(queryCondition.getSearchField(), queryCondition.getSearchValue().toString()));
                        }else{
                            queriesBuilder.must(QueryBuilders.termQuery(queryCondition.getSearchField(), queryCondition.getSearchValue().toString()));
                        }
                        break;
                    default://默认与term同级
                        if(null!=queryConnectionType&&"SHOULD".equals(queryConnectionType.toUpperCase())){
                            queriesBuilder.should(QueryBuilders.termQuery(queryCondition.getSearchField(), queryCondition.getSearchValue()));
                        }else{
                            queriesBuilder.must(QueryBuilders.termQuery(queryCondition.getSearchField(), queryCondition.getSearchValue()));
                        }
                        break;
                }

            }
            boolQueryBuilder.must(queriesBuilder);
        }else{
            //如果不存在查询条件，不添加即可，不影响查询的执行
        }

        return boolQueryBuilder;
    }
    /**
     * 创建聚合对象
     * @param conditions
     * @return
     */
    private AggregationBuilder buildAggregations(SearchConditions conditions){
        AggregationBuilder aggregationBuilder = null;
        //获取排序metric信息
        //获取第一个metric信息作为聚合排序信息,生成SearchConditions时确认了metric指标大小>=1
        Metric metric = conditions.getMetricList().get(0);
        AggregationBuilder orderMetric = getMetricAggBuilder(metric);

        //遍历聚合字段，进行倒序遍历，从倒数第二个聚合字段开始
        for(int i=conditions.getBucketList().size()-1;i>=0;i--){
            //获取最后一个聚合信息，添加metric信息
            if(i==conditions.getBucketList().size()-1){
                //最后一个聚合字段生成的对象
                aggregationBuilder = getBucketAggBuilder(conditions.getBucketList().get(i),aggregationBuilder,orderMetric,conditions);
                //生成的聚合对象不为空时
                if(null!=aggregationBuilder){
                    //如果最后一个聚合不是terms聚合，因为没有设置排序字段，因此，指标agg未添加到其下一级agg中，需要加上
                    if(!"TERMS".equals(conditions.getBucketList().get(i).getAggType().toUpperCase())){
                        aggregationBuilder.subAggregation(orderMetric);
                    }

                    //第一个metric信息已经通过order进行了添加，添加后续的metric
                    for(int j=1;j<conditions.getMetricList().size();j++){
                        AggregationBuilder metricBuilder = getMetricAggBuilder(conditions.getMetricList().get(j));
                        //不为空时添加，避免异常
                        if(metricBuilder!=null){
                            aggregationBuilder.subAggregation(metricBuilder);
                        }
                    }
                }
            }else{
                //后续聚合，进行嵌套
                aggregationBuilder = getBucketAggBuilder(conditions.getBucketList().get(i),aggregationBuilder,orderMetric,conditions);
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
    private AggregationBuilder getBucketAggBuilder(Bucket bucket,AggregationBuilder aggregationBuilderChild,AggregationBuilder orderMetric,SearchConditions conditions){

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
                ExtendedBounds extendedBounds = new ExtendedBounds(conditions.getStartTime(),conditions.getEndTime());
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
    private AggregationBuilder getMetricAggBuilder(Metric metric){
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
     * 直接返回原始数据
     * @param conditions 查询条件
     * @return
     * @throws Exception
     */
    @Override
    public Aggregations getAggregation(SearchConditions conditions) throws Exception {
        //查询条件
        BoolQueryBuilder boolQueryBuilder = buildQuery(conditions.getQueryConditions(),conditions.getQueryConnectionType(),conditions.getStartTime(),conditions.getEndTime(),conditions.getDateField());
        //聚合条件
        AggregationBuilder aggregationBuilder = buildAggregations(conditions);
        // 返回聚合的内容，boolQueryBuilder以及aggregationBuilder都为空时，可以正常进行查询。
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, conditions.getIndex_name());

        return aggregations;
    }

}

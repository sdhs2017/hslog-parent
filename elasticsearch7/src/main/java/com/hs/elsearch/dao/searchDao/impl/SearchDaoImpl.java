package com.hs.elsearch.dao.searchDao.impl;

import com.hs.elsearch.cache.SearchCache;
import com.hs.elsearch.dao.logDao.ILogIndexDao;
import com.hs.elsearch.dao.searchDao.ISearchDao;
import com.hs.elsearch.entity.*;
import com.hs.elsearch.template.SearchTemplate;
import com.hs.elsearch.util.ElasticConstant;
import com.hs.elsearch.util.ErrorInfoException;
import com.hs.elsearch.util.StringUtil;
import com.mysql.jdbc.StringUtils;
import joptsimple.internal.Strings;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.*;
import org.elasticsearch.join.aggregations.ChildrenAggregationBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.range.DateRangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.IpRangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.BucketScriptPipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.BucketSelectorPipelineAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;


public class SearchDaoImpl implements ISearchDao {
    //定义变量，用于判断match 还是term级的查询
    private final String operator_match = "match";
    private final String operator_term = "term";

    //query查询框中， and和or连接符，用来对查询框里的字符串进行解析
    private final String queryAndConnector = " and ";
    private final String queryOrConnector = " or ";
    //定义运算符
    private final String EXPRESSION_EQUALS = ":";
    private final String EXPRESSION_EXISTS = ":*";
    private final String EXPRESSION_GTE = ">=";
    private final String EXPRESSION_LTE = "<=";
    private final String EXPRESSION_GT = ">";
    private final String EXPRESSION_LT = "<";
    //通过运算符拆分表达式时，使用split，需要将特殊字符转义
    private final String EXPRESSION_EXISTS_SPLIT = ":\\*";
    private final String FIELD_TYPE_ERROR = "FIELD TYPE ERROR!";
    //date format
    private final String dateFormat = "yyyy-MM-dd HH:mm:ss";
    //sort asc desc
    private final String SORT_ASC = "asc";
    private final String SORT_DESC = "desc";

    @Autowired
    SearchTemplate searchTemplate;
    @Autowired
    protected ILogIndexDao logIndexDao;
    /**
     *
     * @param conditions 查询条件
     * @return
     */
    private BoolQueryBuilder buildQuery(SearchConditions conditions) throws Exception {
        ArrayList<QueryCondition> queryConditions = conditions.getQueryConditions();//除时间外的查询条件
        String queryConnectionType = conditions.getQueryConnectionType();//除时间外的查询条件的链接类型
        String startTime = conditions.getStartTime();//起始时间
        String endTime = conditions.getEndTime();//截止时间
        String dateField = conditions.getDateField();//时间范围对应的字段名
        List<Filter> filter_visual = conditions.getFilters_visual();//filter
        List<Filter> filter_dashboard = conditions.getFilters_dashboard();//filter
        List<Filter> filter_alert = conditions.getFilters_alert();
        List<Map<String,String>> filter_table = conditions.getFilters_table();//filter
        String queryBox = conditions.getQueryBox();//查询框
        //最外层query的定义
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

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
        //------处理查询框-------
        if(!Strings.isNullOrEmpty(queryBox)){
            //1.根据 or 拆分
            String[] queryOrConnectorSplit = queryBox.split(queryOrConnector);
            //2.遍历or的拆分结果，
            //TODO 从后往前遍历，两两组合
            BoolQueryBuilder boolQueryBuilder4Or = QueryBuilders.boolQuery();
            for(String queryOrConnectorSplitExpression:queryOrConnectorSplit){
                //3.根据 and 拆分
                String[] queryAndConnectorSplit = queryOrConnectorSplitExpression.split(queryAndConnector);
                //4.遍历and的拆分结果
                //TODO 从后往前遍历 两两组合
                BoolQueryBuilder boolQueryBuilder4And = QueryBuilders.boolQuery();
                for(String expression:queryAndConnectorSplit){
                    //and连接符使用bool-filter连接
                    boolQueryBuilder4And.filter(queryBoxExpressionBuilder(expression.trim(),conditions.getTemplate_name()));
                }
                //or 连接符使用bool-should连接
                boolQueryBuilder4Or.should(boolQueryBuilder4And);
            }
            boolQueryBuilder.filter(boolQueryBuilder4Or);
        }

        //------处理filters------
        addFilters(boolQueryBuilder,filter_visual);//图表
        addFilters(boolQueryBuilder,filter_dashboard);//dashboard
        //alert filters
        addFilters(boolQueryBuilder,filter_alert);
        //------处理table的点击事件产生的查询条件----
        addFilters4Table(boolQueryBuilder,filter_table);


        //存在时间类型字段，需要添加时间范围参数查询
        if(dateField!=null&&!"".equals(dateField)){
            //时间范围，起始时间和截止时间都不允许为空
            if (startTime != null && !startTime.equals("") && endTime != null && !endTime.equals("")) {
                boolQueryBuilder.filter(QueryBuilders.rangeQuery(dateField).format("yyyy-MM-dd HH:mm:ss").gte(startTime).lte(endTime));
            }
        }else{
            //时间戳在参数对象生成时已经进行了不为空的判定
            //如果真实存在，不添加条件不影响查询的执行
        }
        //

        return boolQueryBuilder;
    }

    /**
     * 点击table时产生的参数的处理
     * @param boolQueryBuilder
     * @param filters
     */
    private void addFilters4Table(BoolQueryBuilder boolQueryBuilder ,List<Map<String,String>> filters){
        BoolQueryBuilder tableShouldQuery = QueryBuilders.boolQuery();
        for(Map<String,String> map :filters){
            //TODO 资产表条件，暂时写死
            tableShouldQuery.should(QueryBuilders.termQuery("fields.equipmentid",map.get("asset_id")));
        }
        boolQueryBuilder.filter(tableShouldQuery);
    }
    /**
     * 处理filter
     * @param boolQueryBuilder
     * @param filters
     */
    private void addFilters(BoolQueryBuilder boolQueryBuilder ,List<Filter> filters){
        for(Filter filter : filters){
            //是否启用
            if(filter.isEnable()){
                switch(filter.getOperator()){
                    case ElasticConstant.ALERT_OP_IS:
                        boolQueryBuilder.filter(QueryBuilders.termQuery(filter.getField(),filter.getValue()));
                        break;
                    case ElasticConstant.ALERT_OP_IS_NOT:
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery(filter.getField(),filter.getValue()));
                        break;
                    case ElasticConstant.ALERT_OP_GT:
                        if("date".equals(filter.getFieldType())){
                            //date类型，添加format
                            boolQueryBuilder.filter(QueryBuilders.rangeQuery(filter.getField()).gt(filter.getValue()).format(dateFormat));
                        }else{
                            boolQueryBuilder.filter(QueryBuilders.rangeQuery(filter.getField()).gt(filter.getValue()));
                        }
                        break;
                    case ElasticConstant.ALERT_OP_GTE:
                        if("date".equals(filter.getFieldType())){
                            //date类型，添加format
                            boolQueryBuilder.filter(QueryBuilders.rangeQuery(filter.getField()).gte(filter.getValue()).format(dateFormat));
                        }else{
                            boolQueryBuilder.filter(QueryBuilders.rangeQuery(filter.getField()).gte(filter.getValue()));
                        }
                        break;
                    case ElasticConstant.ALERT_OP_LT:
                        if("date".equals(filter.getFieldType())){
                            //date类型，添加format
                            boolQueryBuilder.filter(QueryBuilders.rangeQuery(filter.getField()).lt(filter.getValue()).format(dateFormat));
                        }else{
                            boolQueryBuilder.filter(QueryBuilders.rangeQuery(filter.getField()).lt(filter.getValue()));
                        }
                        break;
                    case ElasticConstant.ALERT_OP_LTE:
                        if("date".equals(filter.getFieldType())){
                            //date类型，添加format
                            boolQueryBuilder.filter(QueryBuilders.rangeQuery(filter.getField()).lte(filter.getValue()).format(dateFormat));
                        }else{
                            boolQueryBuilder.filter(QueryBuilders.rangeQuery(filter.getField()).lte(filter.getValue()));
                        }
                        break;
                    case ElasticConstant.OP_IS_MATCH:
                        boolQueryBuilder.filter(QueryBuilders.matchPhraseQuery(filter.getField(),filter.getValue()));
                        break;
                    case ElasticConstant.OP_IS_NOT_MATCH:
                        boolQueryBuilder.mustNot(QueryBuilders.matchPhraseQuery(filter.getField(),filter.getValue()));
                        break;
                    case ElasticConstant.OP_IS_TERM:
                        boolQueryBuilder.filter(QueryBuilders.termQuery(filter.getField(),filter.getValue()));
                        break;
                    case ElasticConstant.OP_IS_NOT_TERM:
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery(filter.getField(),filter.getValue()));
                        break;
                    case ElasticConstant.OP_IS_ONE_OF_MATCH:
                        boolQueryBuilder.filter(this.buildOneOfQuery(filter,operator_match));
                        break;
                    case ElasticConstant.OP_IS_NOT_ONE_OF_MATCH:
                        boolQueryBuilder.mustNot(this.buildOneOfQuery(filter,operator_match));
                        break;
                    case ElasticConstant.OP_IS_ONE_OF_TERM:
                        boolQueryBuilder.filter(this.buildOneOfQuery(filter,operator_term));
                        break;
                    case ElasticConstant.OP_IS_NOT_ONE_OF_TERM:
                        boolQueryBuilder.mustNot(this.buildOneOfQuery(filter,operator_term));
                        break;
                    case ElasticConstant.OP_IS_BETWEEN:

                        if("date".equals(filter.getFieldType())){
                            //date类型，添加format
                            boolQueryBuilder.filter(QueryBuilders.rangeQuery(filter.getField()).gte(filter.getStart()).lt(filter.getEnd()).format(dateFormat));
                        }else{
                            boolQueryBuilder.filter(QueryBuilders.rangeQuery(filter.getField()).gte(filter.getStart()).lt(filter.getEnd()));
                        }

                        break;
                    case ElasticConstant.OP_IS_NOT_BETWEEN:
                        boolQueryBuilder.mustNot(QueryBuilders.rangeQuery(filter.getField()).gte(filter.getStart()).lt(filter.getEnd()));
                        break;
                    case ElasticConstant.OP_EXISTS:
                        boolQueryBuilder.filter(QueryBuilders.existsQuery(filter.getField()));
                        break;
                    case ElasticConstant.OP_DOES_NOT_EXISTS:
                        boolQueryBuilder.mustNot(QueryBuilders.existsQuery(filter.getField()));
                        break;
                    default:
                        break;
                }
            }
        }
    }
    /**
     * 根据表达式生成BoolQueryBuilder对象
     * @param expression
     * @param templates
     * @return
     * @throws Exception 通过抛异常方式解决 query解析失败的返回信息。
     */
    private BoolQueryBuilder queryBoxExpressionBuilder(String expression,String templates) throws Exception{
        String fieldName;
        String fieldType;
        String[] expressionArray;
        //拆分表达式 表达式格式  字段名称+运算符+值
        if(expression.contains(EXPRESSION_EXISTS)){
            expressionArray = expression.split(EXPRESSION_EXISTS_SPLIT);
            //长度为1 意味着表达式格式正常
            if(expressionArray.length==1){
                fieldName = expressionArray[0];
                //通过template获取字段类型
                fieldType = getFieldTypeByTemplates(fieldName,templates);
                return getQueryByFieldType(fieldName,fieldType,EXPRESSION_EXISTS,"");
            }else{
                throw new ErrorInfoException(expression+"表达式格式错误！");
            }
        }else if(expression.contains(EXPRESSION_EQUALS)){
            expressionArray = expression.split(EXPRESSION_EQUALS);
            //长度为2 意味着表达式格式正常
            if(expressionArray.length==2){
                fieldName = expressionArray[0];
                //通过template获取字段类型
                fieldType = getFieldTypeByTemplates(fieldName,templates);
                return getQueryByFieldType(fieldName,fieldType,EXPRESSION_EQUALS,expressionArray[1]);
            }else{
                throw new ErrorInfoException(expression+"表达式格式错误！");
            }
        }else if(expression.contains(EXPRESSION_GTE)){
            expressionArray = expression.split(EXPRESSION_GTE);
            //长度为2 意味着表达式格式正常
            if(expressionArray.length==2){
                fieldName = expressionArray[0];
                //通过template获取字段类型
                fieldType = getFieldTypeByTemplates(fieldName,templates);
                return getQueryByFieldType(fieldName,fieldType,EXPRESSION_GTE,expressionArray[1]);
            }else{
                throw new ErrorInfoException(expression+"表达式格式错误！");
            }
        }else if(expression.contains(EXPRESSION_LTE)){
            expressionArray = expression.split(EXPRESSION_LTE);
            //长度为2 意味着表达式格式正常
            if(expressionArray.length==2){
                fieldName = expressionArray[0];
                //通过template获取字段类型
                fieldType = getFieldTypeByTemplates(fieldName,templates);
                return getQueryByFieldType(fieldName,fieldType,EXPRESSION_LTE,expressionArray[1]);
            }else{
                throw new ErrorInfoException(expression+"表达式格式错误！");
            }
        }else if(expression.contains(EXPRESSION_GT)){
            expressionArray = expression.split(EXPRESSION_GT);
            //长度为2 意味着表达式格式正常
            if(expressionArray.length==2){
                fieldName = expressionArray[0];
                //通过template获取字段类型
                fieldType = getFieldTypeByTemplates(fieldName,templates);
                return getQueryByFieldType(fieldName,fieldType,EXPRESSION_GT,expressionArray[1]);
            }else{
                throw new ErrorInfoException(expression+"表达式格式错误！");
            }
        }else if(expression.contains(EXPRESSION_LT)){
            expressionArray = expression.split(EXPRESSION_LT);
            //长度为2 意味着表达式格式正常
            if(expressionArray.length==2){
                fieldName = expressionArray[0];
                //通过template获取字段类型
                fieldType = getFieldTypeByTemplates(fieldName,templates);
                return getQueryByFieldType(fieldName,fieldType,EXPRESSION_LT,expressionArray[1]);
            }else{
                throw new ErrorInfoException(expression+"表达式格式错误！");
            }
        }else{
            throw new ErrorInfoException(expression+"不存在的运算符!");
        }
    }

    /**
     * 通过字段名称，到响应的template中查询字段类型
     * 为保证查询不报类型错误，按照indexPattern将字段类型进行归类查询，
     * 最终得到的类型超过1个，则认为字段无法进行查询，返回提示错误信息。
     * @param templates 多个template以逗号隔开
     * @param fieldName
     * @return
     */
    private String getFieldTypeByTemplates(String fieldName,String templates)throws Exception{
        //处理字符串两端的引号
        fieldName = StringUtil.removeQuotation(fieldName);
        //字段类型
        String indexPatternType = null;
        Set<String> result = new HashSet<>();
        String[] templateList = templates.split(",");
        for(String template:templateList){
            Map<String,String> mapping = SearchCache.INSTANCE.getMappingCache().getIfPresent(template);
            //mapping找不到  进行一次cache初始化操作
            if(null==mapping){
                SearchCache.INSTANCE.init(template,logIndexDao);
            }
            mapping = SearchCache.INSTANCE.getMappingCache().getIfPresent(template);
            //在进行聚合查询时，cache已经进行了更新，不需要重复更新，
            //没有找到就说明template不存在
            if(null!=mapping&&null!=mapping.get(fieldName)){
                indexPatternType = ElasticConstant.IndexPatternType.get(mapping.get(fieldName));
                result.add(null==indexPatternType?mapping.get(fieldName):indexPatternType);
            }else{
                throw new ErrorInfoException(fieldName+"该字段不存在！");
            }
        }
        if(result.size()!=1){
            throw new ErrorInfoException(fieldName+"字段类型异常！");
        }else{
            return result.iterator().next();
        }
    }

    /**
     * 通过表达式的信息，生成querybuilder
     * @param fieldName 字段名称
     * @param fieldType 字段类型
     * @param operator 运算符
     * @param fieldValue 值
     * @return
     */
    private BoolQueryBuilder getQueryByFieldType(String fieldName,String fieldType,String operator,String fieldValue)throws Exception{
        //处理字符串两端的引号
        fieldName = StringUtil.removeQuotation(fieldName);
        fieldValue = StringUtil.removeQuotation(fieldValue);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        switch(operator){
            case EXPRESSION_EXISTS:
                boolQueryBuilder.should(QueryBuilders.existsQuery(fieldName));
                break;
            case EXPRESSION_GTE:
                boolQueryBuilder.should(QueryBuilders.rangeQuery(fieldName).gte(fieldValue));
                break;
            case EXPRESSION_LTE:
                boolQueryBuilder.should(QueryBuilders.rangeQuery(fieldName).lte(fieldValue));
                break;
            case EXPRESSION_GT:
                boolQueryBuilder.should(QueryBuilders.rangeQuery(fieldName).gt(fieldValue));
                break;
            case EXPRESSION_LT:
                boolQueryBuilder.should(QueryBuilders.rangeQuery(fieldName).lt(fieldValue));
                break;
            case EXPRESSION_EQUALS:
                if(ElasticConstant.INDEX_PATTERN_TYPE_STRING.equals(fieldType)){
                    boolQueryBuilder.should(QueryBuilders.matchPhraseQuery(fieldName,fieldValue));
                }else if(ElasticConstant.INDEX_PATTERN_TYPE_DATE.equals(fieldType)){
                    boolQueryBuilder.should(QueryBuilders.rangeQuery(fieldName).gte(fieldValue).lte(fieldValue));
                }else{
                    boolQueryBuilder.should(QueryBuilders.matchQuery(fieldName,fieldValue));
                }

                break;
            default:
                throw new ErrorInfoException(operator+" 运算符无法解析！");
        }
        return boolQueryBuilder;
    }

    /**
     * 供query中的filer中的 one of 使用
     * @param filter 一个filter（查询）信息
     * @param type 类型，match or term
     * @return
     */
    private QueryBuilder buildOneOfQuery(Filter filter,String type){
        BoolQueryBuilder boolShouldQuery = QueryBuilders.boolQuery();
        String[] values = filter.getValues();//one of 的数组
        //遍历
        for(String value:values){
            boolShouldQuery.should(operator_match.equals(type)?QueryBuilders.matchPhraseQuery(filter.getField(),value):QueryBuilders.termQuery(filter.getField(),value));
        }
        return boolShouldQuery;
    }
    /**
     * 创建聚合对象
     * @param conditions
     * @return
     */
    private List<AggregationBuilder> buildAggregations(SearchConditions conditions){
        List<AggregationBuilder> aggList = new ArrayList<>();
        AggregationBuilder aggregationBuilder = null;



        //bucket为空的情况，一般出现在指标报表中
        if(conditions.getBucketList().size()==0){
            //aggregationBuilder = AggregationBuilders.;
            for(Metric metric_entity : conditions.getMetricList()){
                AggregationBuilder metricBuilder = getMetricAggBuilder(metric_entity);
                //不为空时添加，避免异常
                if(metricBuilder!=null) {
                    aggList.add(metricBuilder);
                }
            }
        }else{
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
            aggList.add(aggregationBuilder);
        }
        return aggList;
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
     * 动态表格构建显示字段和排序信息
     * @param conditions
     * @param includeSource
     * @param sorts
     */
    private void buildSourceAndSort(SearchConditions conditions,String[] includeSource,List<SortBuilder> sorts){
        //要显示字段
        if(conditions.getDataTableColumns().size()>0){
            //遍历字段
            for(int i=0;i<conditions.getDataTableColumns().size();i++){
                DataTableColumn column = conditions.getDataTableColumns().get(i);
                //排序
                if(!StringUtils.isNullOrEmpty(column.getSort())){
                    sorts.add(SortBuilders.fieldSort(column.getField()).order("asc".equals(column.getSort())?SortOrder.ASC:SortOrder.DESC));
                }
                //要显示字段
                includeSource[i] = column.getField();
            }
        }
        //单独的排序字段
        if(conditions.getSortColumns().size()>0){
            for(DataTableColumn column:conditions.getSortColumns()){
                sorts.add(SortBuilders.fieldSort(column.getField()).order("asc".equals(column.getSort())?SortOrder.ASC:SortOrder.DESC));
            }
        }
    }
    /**
     * 直接返回原始数据
     * @param conditions 查询条件
     * @return
     * @throws Exception
     */
    @Override
    public Aggregations getAggregation(SearchConditions conditions) throws Exception {
        //查询条件 query(增加了filter的处理)
        BoolQueryBuilder boolQueryBuilder = buildQuery(conditions);
        //聚合条件
        List<AggregationBuilder> aggregationBuilder = buildAggregations(conditions);
        // 返回聚合的内容，boolQueryBuilder以及aggregationBuilder都为空时，可以正常进行查询。
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder,conditions.getIndex_name());

        return aggregations;
    }

    /**
     * 直接返回response原始数据
     * @param conditions 查询条件
     * @return
     * @throws Exception
     */
    @Override
    public SearchResponse getResponse(SearchConditions conditions) throws Exception {
        //查询条件 query(增加了filter的处理)
        BoolQueryBuilder boolQueryBuilder = buildQuery(conditions);
        //聚合条件
        List<AggregationBuilder> aggregationBuilder = buildAggregations(conditions);
        // 返回聚合的内容，boolQueryBuilder以及aggregationBuilder都为空时，可以正常进行查询。
        SearchResponse response = searchTemplate.getAggregationsByBuilder2Response(boolQueryBuilder, aggregationBuilder,conditions.getSize(),conditions.getIndex_name());

        return response;
    }
    /**
     * 获取查询，无聚合
     * @param conditions
     * @return
     * @throws Exception
     */
    public SearchHits getSearchHitsByBuilder(SearchConditions conditions)throws Exception {
        //查询条件 query(增加了filter的处理)
        BoolQueryBuilder boolQueryBuilder = buildQuery(conditions);
        //设置要返回的字段 _source
        String[] includeSource = new String[conditions.getDataTableColumns().size()];
        //设置排序字段 sort
        List<SortBuilder> sorts = new ArrayList<>();;
        buildSourceAndSort(conditions,includeSource,sorts);
        SearchHits searchHits = searchTemplate.getSearchHitsByBuilder(boolQueryBuilder,includeSource,sorts,conditions.getFrom(),conditions.getPage_size(),conditions.getIndex_name());
        return searchHits;
    }

    /**
     * 获取查询条件 查询出的数据量  count
     * @param conditions
     * @return
     * @throws Exception
     */
    public long getCountByConditionsQuery(SearchConditions conditions)throws Exception {
        //查询条件
        BoolQueryBuilder boolQueryBuilder = buildQuery(conditions);
        long count = searchTemplate.getCountByQuery(boolQueryBuilder,conditions.getIndex_name());
        return count;
    }

    public static void main(String[] args) {
//        Map<String,String> map = new HashMap<>();
//        map.put("usr_pct","usr_pct");
//        AggregationBuilder aggregationBuilder = AggregationBuilders.dateHistogram("day").field("@timestamp").format("yyyy-MM-dd HH:mm:ss");
//        aggregationBuilder.subAggregation(AggregationBuilders.max("usr_pct").field("system.core.user.pct"));
//        aggregationBuilder.subAggregation(new BucketSelectorPipelineAggregationBuilder("sss",map,new Script("params.usr_pct>0.5")));
//        System.out.println(aggregationBuilder.toString());
//
//        // 通过日志内容匹配进行告警
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        boolQueryBuilder.must(QueryBuilders.rangeQuery("@timestamp").format("yyyy-MM-dd HH:mm:ss").gte("2020-09-04 11:00:00").lte("2020-09-04 12:00:00"));
//        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("message","Started Session"));
//
//        AggregationBuilder aggregationBuilder1 = AggregationBuilders.terms("ip_count").field("fields.ip");
//        aggregationBuilder1.subAggregation(AggregationBuilders.count("count").field("fields.ip.raw"));
//        Map<String,String> maps = new HashMap<>();
//        maps.put("count","count");
//        aggregationBuilder1.subAggregation( new BucketSelectorPipelineAggregationBuilder("大于10次登陆",maps,new Script("params.count>10")));
//        aggregationBuilder1.subAggregation( new BucketScriptPipelineAggregationBuilder("",maps,new Script("")));
//        System.out.println(aggregationBuilder1.toString());

        MultiSearchRequest request = new MultiSearchRequest();
        SearchRequest firstSearchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("fields.ip", "192.168.200.15"));
        searchSourceBuilder.size(5);
        firstSearchRequest.source(searchSourceBuilder);
        firstSearchRequest.indices("winlogbeat-*");
        request.add(firstSearchRequest);

        SearchRequest secondSearchRequest = new SearchRequest();
        searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("message", "sshd"));
        searchSourceBuilder.size(5);
        secondSearchRequest.source(searchSourceBuilder);
        firstSearchRequest.indices("winlogbeat-*");
        request.add(secondSearchRequest);

        //MultiSearchResponse response = client.msearch(request, RequestOptions.DEFAULT);

    }
}

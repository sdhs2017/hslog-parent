package com.hs.elsearch.service.impl;

import com.hs.elsearch.dao.searchDao.ISearchDao;
import com.hs.elsearch.entity.*;
import com.hs.elsearch.service.ISearchService;
import com.hs.elsearch.util.ElasticConstant;
import com.hs.elsearch.util.ValueFormat;
import joptsimple.internal.Strings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregation;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DecimalFormat;
import java.util.*;

/**
 * 提供标准化的数据输入/输出
 */

public class SearchServiceImpl implements ISearchService {
    @Autowired
    protected ISearchDao searchDao;


    /**
     * 获取复杂查询的数据，数据返回格式为echart的dataset格式
     * @param conditions
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> getMultiAggDataSet(SearchConditions conditions) throws Exception {
        Map<String, Object> result = new HashMap<>();
        //参数为空的处理
        if(null==conditions){
            return result;
        }else{
            //数据查询的原始结果
            Aggregations aggregations = searchDao.getAggregation(conditions);
            //处理成DataSet
            result = TransformAggData2DataSet(aggregations,conditions);
            return result;
        }
    }

    /**
     * 进行数据的补零操作
     * @param conditions
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> getMultiAggDataSetWithZeroFill(SearchConditions conditions) throws Exception {
        //获取DataSet格式数据
        Map<String, Object> result = getMultiAggDataSet(conditions);
        //数据不为空
        if(result.size()>0){
            //获取source数据
            List<LinkedHashMap<String,Object>> source = (List<LinkedHashMap<String, Object>>) result.get(ElasticConstant.SOURCE);
            //获取dimensions数据
            LinkedHashSet<String> dimensions = (LinkedHashSet<String>) result.get(ElasticConstant.DIMENSIONS);
            //补零
            for(LinkedHashMap<String,Object> points:source){//获取某个时间节点上对应的N条线的数据对象
                for(String line:dimensions){//遍历N条线的名称
                    if(points.get(line)==null){//get不到数据的进行补零
                        points.put(line,0);
                    }
                }
            }

        }else{

        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getSearchHitsList(SearchConditions conditions) throws Exception {
        SearchHits searchHits = searchDao.getSearchHitsByBuilder(conditions);
        //数据处理
        SearchHit[] searchHit = searchHits.getHits();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(SearchHit hit : searchHit) {
            Map<String, Object> map = hit.getSourceAsMap();
            list.add(map);
        }
        return list;
    }

    @Override
    public long getCountByConditionsQuery(SearchConditions conditions) throws Exception {
        return searchDao.getCountByConditionsQuery(conditions);
    }


    /**
     * 将es查询获取的agg对象转化成echart的DataSet格式数据
     * {
     *  dimensions: ["xAxis","192.168.1.1-8080","192.168.1.1-8443"],
     *  source: [
     *   {"xAxis":"2020-02-02 11:00:00","192.168.1.1-8080":200,"192.168.1.1-8443":100},
     *   {"xAxis":"2020-02-02 11:01:00","192.168.1.1-8080":250,"192.168.1.1-8443":150}
     *   ]
     * }
     * @param aggregations
     * @param conditions
     * @return
     */
    private Map<String, Object> TransformAggData2DataSet(Aggregations aggregations,SearchConditions conditions){
        Map<String, Object> result = new HashMap<>();
        if(null==aggregations){
            return result;
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
            if(conditions.getBucketList().size()>0){
                //获取第一个聚合字段信息，用以组装别名，获取对应数据
                Bucket bucket = conditions.getBucketList().get(0);

                //第一层无法使用递归，第一层和2-n层需要：声明一些对象以及相关处理
                MultiBucketsAggregation terms  = aggregations.get(bucket.getAggType()+"-"+bucket.getField());
                //第一层也会作为X轴的图标
                for(MultiBucketsAggregation.Bucket xAxisBucket:terms.getBuckets()) {
                    //第一级聚合的结果作为X轴显示
                    String xAxisValue = xAxisBucket.getKeyAsString();
                    LinkedHashMap<String,Object> xAxisMap = new LinkedHashMap<>();
                    xAxisMap.put(ElasticConstant.XAXIS,xAxisValue);
                    //如果bucket聚合只有一层
                    if(conditions.getBucketList().size()==1){
                        setData(conditions.getMetricList(),xAxisBucket,xAxisMap,dimensions,"",conditions.getUnit());
                    }else{
                        //2-n层
                        //递归
                        getResult4DataSet(conditions,1,xAxisBucket,xAxisMap,dimensions,"");
                    }
                    source.add(xAxisMap);
                }
            }else if(conditions.getBucketList().size()==0){
                //TODO 无X轴的数据处理，如：Y轴计算count，返回单一的数字
            }else{

            }
            result.put(ElasticConstant.DIMENSIONS,dimensions);
            result.put(ElasticConstant.SOURCE,source);
        }
        return result;
    }
    /**
     * 递归方法，循环遍历聚合结果
     * @param conditions 请求参数
     * @param i 第i次聚合
     * @param bucket 第i次聚合结果的bucket对相同
     * @param xAxisMap 第一级聚合后的结果作为X轴，X轴上的某个点对应的Y轴结果集合
     * @param dimensions 图例，2-n次组合后的key放入到该对象中
     * @param nextKey 需要组装成的图例，多个聚合字段以“-”连接 eg：192.168.1.1-8080-count
     */
    private void getResult4DataSet(SearchConditions conditions,int i,MultiBucketsAggregation.Bucket bucket,LinkedHashMap<String,Object> xAxisMap,LinkedHashSet<String> dimensions,String nextKey){
        //如果是最后一层bucket
        if(i==conditions.getBucketList().size()){
            //填充数据
            setData(conditions.getMetricList(),bucket,xAxisMap,dimensions,nextKey,conditions.getUnit());
        }else{

            //获取这次聚合结果对应的
            Bucket bucketEntity = conditions.getBucketList().get(i);
            //获取聚合结果
            MultiBucketsAggregation terms = bucket.getAggregations().get(bucketEntity.getAggType()+"-"+bucketEntity.getField());
            //下一次递归时  i需要+1，进入第i+1层聚合结果的处理中
            i++;
            //遍历第i层的结果集
            for(MultiBucketsAggregation.Bucket aggBucket:terms.getBuckets()){
                getResult4DataSet(conditions,i,aggBucket,xAxisMap,dimensions,nextKey+aggBucket.getKeyAsString()+"-");
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
    private void setData(ArrayList<Metric> metricList,MultiBucketsAggregation.Bucket bucket,LinkedHashMap<String,Object> xAxisMap,LinkedHashSet<String> dimensions,String nextKey,String unit){
        for(Metric metric:metricList){
            NumericMetricsAggregation.SingleValue value = bucket.getAggregations().get(!Strings.isNullOrEmpty(metric.getAliasName())?metric.getAliasName():(metric.getAggType()+"-"+metric.getField()).replace(".","_"));
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
            xAxisMap.put(line_name, ValueFormat.formatter(pointValue, unit));
        }
    }

}

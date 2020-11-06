package com.jz.bigdata.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hs.elsearch.entity.Bucket;
import com.hs.elsearch.entity.Metric;
import com.hs.elsearch.entity.SearchConditions;
import com.hs.elsearch.util.ValueFormat;
import com.mysql.jdbc.StringUtils;
import joptsimple.internal.Strings;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理ES aggregation对象数据
 */
public class AggDataHandler {
    /**
     * alert数据处理
     * @param aggregations
     * @param conditions
     * @return
     */
    public static List<Map<String, Object>> toAlertAgg(Aggregations aggregations, SearchConditions conditions){
        /**
         * 数据返回示例
         *   [{value: 335, name: '192.168.2.1'},{value: 125, name: '192.168.2.2'}...]
         */
        List<Map<String,Object>> result = new ArrayList<>();
        //处理数据
        if(conditions.getBucketList().size()>0){
            //获取第一个聚合字段信息，用以组装别名，获取对应数据
            //metric 文字块 bucket最多选择一个
            Bucket bucket = conditions.getBucketList().get(0);
            MultiBucketsAggregation terms  = aggregations.get(bucket.getAggType()+"-"+bucket.getField());
            //如果无法获取结果集，直接返回空数据
            if(null==terms||(null!=terms&&null==terms.getBuckets())){
                //不需要进行其他操作
            }else{
                //遍历bucket的结果数据
                for(MultiBucketsAggregation.Bucket bucketItem:terms.getBuckets()) {
                    handleAggData4Metric(conditions,bucketItem,result);
                }
            }

        }else{
            //获取要写入结果的对象
            ArrayList<Map<String,Object>> tempResult = new ArrayList<>();
            for(Metric metric:conditions.getMetricList()){
                //定义数据点，包含name和value属性，eg:{value: 335, name: '192.168.2.1'}
                Map<String,Object> dataPoint = new HashMap<>();
                //图例名称
                String key = !Strings.isNullOrEmpty(metric.getAliasName())?metric.getAliasName():(metric.getAggType()+"-"+metric.getField());
                //写入图例名称
                dataPoint.put("name",!Strings.isNullOrEmpty(metric.getAliasName())?metric.getAliasName():metric.getAggType());
                NumericMetricsAggregation.SingleValue value = aggregations.get(key);
                //写入值
                dataPoint.put("value", (Double.isInfinite(value.value())||Double.isNaN(value.value()))?0:value.value());
                tempResult.add(dataPoint);
            }
            result.addAll(tempResult);
        }
        return result;
    }

    /**
     * 返回hits结果，仅source部分数据
     * @param response
     * @return
     */
    public static ArrayList<Object> toHitsList(SearchResponse response){
        //原始返回信息，转成json对象
        JsonElement jsonElement = new JsonParser().parse(response.toString());
        JsonObject jsonObject= jsonElement.getAsJsonObject();
        ArrayList<Object> result = new ArrayList<>();
        //查询结果的key为 hits-hits:[]
        JsonArray jsonResult = jsonObject.get("hits").getAsJsonObject().get("hits").getAsJsonArray();
        //普通查询的数据都放在了_source下。
        for(JsonElement obj:jsonResult){
            //可以获取_source中的内容时，写入list
            if(obj.getAsJsonObject().get("_source")!=null){
                result.add(obj.getAsJsonObject().get("_source"));
            }
        }
        return result;
    }

    /**
     * 递归方法，循环遍历聚合结果
     * @param conditions 请求参数
     * @param bucket 第i次聚合结果的bucket对相同
     * @return Double metric的值，饼图都是数值类型
     */
    private static void handleAggData4Metric(SearchConditions conditions, MultiBucketsAggregation.Bucket bucket,List<Map<String,Object>> result){
        //定义数据点，包含name和value属性，eg:{value: 335, name: '192.168.2.1-count'}

        for(Metric metric:conditions.getMetricList()){
            Map<String,Object> dataPoint = new HashMap<>();
            //图例名称
            String key = bucket.getKeyAsString();
            //写入图例名称
            dataPoint.put("name",key);
            //获取metric对应的值
            NumericMetricsAggregation.SingleValue value = bucket.getAggregations().get(!Strings.isNullOrEmpty(metric.getAliasName())?metric.getAliasName():(metric.getAggType()+"-"+metric.getField()));
            dataPoint.put("value",Double.isInfinite(value.value())||Double.isNaN(value.value())?0:value.value());
            result.add(dataPoint);
        }
    }
}

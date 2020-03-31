package com.jz.bigdata.common.businessIntelligence.service;

import com.jz.bigdata.common.businessIntelligence.entity.MappingField;
import com.jz.bigdata.common.metadata.entity.Metadata;

import java.util.List;
import java.util.Map;

public interface IBIService {

    /**
     * 通过X轴的聚合方式获取对应的field
     * @param templateName
     * @param indexName
     * @param agg 聚合方式
     * @return
     */
    public List<MappingField> getFieldByXAxisAggregation(String templateName, String indexName,String agg) throws Exception;
    /**
     * 通过Y轴的聚合方式获取对应的field
     * @param templateName
     * @param indexName
     * @param agg 聚合方式
     * @return
     */
    public List<MappingField> getFieldByYAxisAggregation(String templateName, String indexName,String agg) throws Exception;

    /**
     * 实现类sql的group by并进行sum求和计算的功能,包含时间范围、条件等
     * @param indices 索引或template名称
     * @param groupByField 需要进行聚合的字段
     * @param groupByFieldType 需要进行聚合方式（terms/DataHistogram）
     * @param sumField 聚合分组后进行sum计算的字段
     * @param size 聚合结果最大返回数
     * @param sort 排序
     * @param starttime 时间范围-开始时间
     * @param endtime 时间范围-结束时间
     * @param map 其他限制条件
     * @return
     */
    public String groupByThenSum(String indices,String groupByField,String groupByFieldType,String sumField, int size, String sort, String starttime, String endtime,Map<String, String> map) throws Exception;
    /**
     * 实现类sql的group by并进行sum求和计算的功能,包含时间范围、条件等
     * @param indices 索引或template名称
     * @param groupByField 需要进行聚合的字段
     * @param groupByFieldType 需要进行聚合方式（terms/DataHistogram）
     * @param sumField 聚合分组后进行sum计算的字段
     * @param size 聚合结果最大返回数
     * @param sort 排序
     * @param starttime 时间范围-开始时间
     * @param endtime 时间范围-结束时间
     * @param map 其他限制条件
     * @return
     */
    public String groupByThenCount(String indices,String groupByField,String groupByFieldType,String sumField, int size, String sort, String starttime, String endtime,Map<String, String> map) throws Exception;
    /**
     * 实现类sql的group by并进行sum求和计算的功能,包含时间范围、条件等
     * @param indices 索引或template名称
     * @param groupByField 需要进行聚合的字段
     * @param groupByFieldType 需要进行聚合方式（terms/DataHistogram）
     * @param sumField 聚合分组后进行sum计算的字段
     * @param size 聚合结果最大返回数
     * @param sort 排序
     * @param starttime 时间范围-开始时间
     * @param endtime 时间范围-结束时间
     * @param map 其他限制条件
     * @return
     */
    public String groupByThenAvg(String indices,String groupByField,String groupByFieldType,String sumField, int size, String sort, String starttime, String endtime,Map<String, String> map) throws Exception;
    /**
     * 实现类sql的group by并进行sum求和计算的功能,包含时间范围、条件等
     * @param indices 索引或template名称
     * @param groupByField 需要进行聚合的字段
     * @param groupByFieldType 需要进行聚合方式（terms/DataHistogram）
     * @param sumField 聚合分组后进行sum计算的字段
     * @param size 聚合结果最大返回数
     * @param sort 排序
     * @param starttime 时间范围-开始时间
     * @param endtime 时间范围-结束时间
     * @param map 其他限制条件
     * @return
     */
    public String groupByThenMax(String indices,String groupByField,String groupByFieldType,String sumField, int size, String sort, String starttime, String endtime,Map<String, String> map) throws Exception;
    /**
     * 实现类sql的group by并进行sum求和计算的功能,包含时间范围、条件等
     * @param indices 索引或template名称
     * @param groupByField 需要进行聚合的字段
     * @param groupByFieldType 需要进行聚合方式（terms/DataHistogram）
     * @param sumField 聚合分组后进行sum计算的字段
     * @param size 聚合结果最大返回数
     * @param sort 排序
     * @param starttime 时间范围-开始时间
     * @param endtime 时间范围-结束时间
     * @param map 其他限制条件
     * @return
     */
    public String groupByThenMin(String indices,String groupByField,String groupByFieldType,String sumField, int size, String sort, String starttime, String endtime,Map<String, String> map) throws Exception;

}

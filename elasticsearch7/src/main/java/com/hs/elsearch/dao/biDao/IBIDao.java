package com.hs.elsearch.dao.biDao;

import java.util.List;
import java.util.Map;

public interface IBIDao {
    /**
     * bucket聚合查询后进行metric的sum计算
     * @param starttime 时间范围-开始时间
     * @param endtime  时间范围-结束时间
     * @param groupByField 聚合字段
     * @param groupByFieldType 聚合方式（terms/dateHistogram）
     * @param sumField 求和字段
     * @param size 查询结果条数
     * @param sort 正序/倒序  后面可能会对此扩展
     * @param map 其他条件
     * @param indices 索引名称
     * @return 返回聚合结果
     */
    public List<Map<String, Object>> getListBySumOfAggregation(String starttime, String endtime, String groupByField, String groupByFieldType,
                                                               String  sumField, int size, String sort,Map<String, String> map, String... indices) throws Exception;
    /**
     * bucket聚合查询后进行metric的sum计算
     * @param starttime 时间范围-开始时间
     * @param endtime  时间范围-结束时间
     * @param groupByField 聚合字段
     * @param groupByFieldType 聚合方式（terms/dateHistogram）
     * @param size 查询结果条数
     * @param sort 正序/倒序  后面可能会对此扩展
     * @param map 其他条件
     * @param indices 索引名称
     * @return 返回聚合结果
     */
    public List<Map<String, Object>> getListByCountOfAggregation(String starttime, String endtime, String groupByField, String groupByFieldType,int size, String sort,Map<String, String> map, String... indices) throws Exception;
    /**
     * bucket聚合查询后进行metric的sum计算
     * @param starttime 时间范围-开始时间
     * @param endtime  时间范围-结束时间
     * @param groupByField 聚合字段
     * @param groupByFieldType 聚合方式（terms/dateHistogram）
     * @param avgField 求平均值字段
     * @param size 查询结果条数
     * @param sort 正序/倒序  后面可能会对此扩展
     * @param map 其他条件
     * @param indices 索引名称
     * @return 返回聚合结果
     */
    public List<Map<String, Object>> getListByAvgOfAggregation(String starttime, String endtime, String groupByField,String groupByFieldType,
                                                               String  avgField, int size, String sort,Map<String, String> map, String... indices) throws Exception;
    /**
     * bucket聚合查询后进行metric的sum计算
     * @param starttime 时间范围-开始时间
     * @param endtime  时间范围-结束时间
     * @param groupByField 聚合字段
     * @param groupByFieldType 聚合方式（terms/dateHistogram）
     * @param maxField 求最大值字段
     * @param size 查询结果条数
     * @param sort 正序/倒序  后面可能会对此扩展
     * @param map 其他条件
     * @param indices 索引名称
     * @return 返回聚合结果
     */
    public List<Map<String, Object>> getListByMaxOfAggregation(String starttime, String endtime, String groupByField,String groupByFieldType,
                                                               String  maxField, int size, String sort,Map<String, String> map, String... indices) throws Exception;
    /**
     * bucket聚合查询后进行metric的sum计算
     * @param starttime 时间范围-开始时间
     * @param endtime  时间范围-结束时间
     * @param groupByField 聚合字段
     * @param groupByFieldType 聚合方式（terms/dateHistogram）
     * @param minField 求最小值字段
     * @param size 查询结果条数
     * @param sort 正序/倒序  后面可能会对此扩展
     * @param map 其他条件
     * @param indices 索引名称
     * @return 返回聚合结果
     */
    public List<Map<String, Object>> getListByMinOfAggregation(String starttime, String endtime, String groupByField,String groupByFieldType,
                                                               String  minField, int size, String sort,Map<String, String> map, String... indices) throws Exception;
}

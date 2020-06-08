package com.hs.elsearch.dao.biDao;

import com.hs.elsearch.entity.VisualParam;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface IBIDao {
    /**
     * bucket聚合查询后进行metric计算
     * 支持sum count avg max min
     * @param params
     * @return 返回聚合结果
     */
    public List<Map<String, Object>> getListByAggregation(VisualParam params) throws Exception;
    /**
     * bucket聚合查询后进行metric的sum计算
     * @param params
     * @return 返回聚合结果
     */
    public List<Map<String, Object>> getListBySumOfAggregation(VisualParam params) throws Exception;
    /**
     * bucket聚合查询后进行metric的sum计算
     * @param params
     * @return 返回聚合结果
     */
    public List<Map<String, Object>> getListByCountOfAggregation(VisualParam params) throws Exception;
    /**
     * bucket聚合查询后进行metric的sum计算
     * @param params
     * @return 返回聚合结果
     */
    public List<Map<String, Object>> getListByAvgOfAggregation(VisualParam params) throws Exception;
    /**
     * bucket聚合查询后进行metric的sum计算
     * @param params
     * @return 返回聚合结果
     */
    public List<Map<String, Object>> getListByMaxOfAggregation(VisualParam params) throws Exception;
    /**
     * bucket聚合查询后进行metric的sum计算
     * @param params
     * @return 返回聚合结果
     */
    public List<Map<String, Object>> getListByMinOfAggregation(VisualParam params) throws Exception;

    /**
     * 获取某个字段不为空的数据list
     * @param indexName 索引名称
     * @param fieldName 字段名称
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getListExistsField(String indexName,String fieldName) throws Exception;

    /**
     * 根据查询条件获取count值
     * @param map 查询参数
     * @param indexName 索引名称
     * @return
     * @throws Exception
     */
    public long getCount(Map<String,String> map,String indexName)throws Exception;

    /**
     * 嵌套聚合-demo
     * @param params
     * @return
     * @throws Exception
     */
    public Map<String, Object> getMultiAggregation4dateset(VisualParam params) throws Exception;
    /**
     * 嵌套聚合 且X轴第一层为时间，其它层聚合为term
     * @param params
     * @return
     * @throws Exception
     */
    public Map<String, LinkedList<Map<String,Object>>> getMultiDateHistogramAggregation(VisualParam params) throws Exception;
}

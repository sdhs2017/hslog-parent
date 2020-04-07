package com.hs.elsearch.dao.biDao;

import com.hs.elsearch.dao.biDao.entity.VisualParam;

import java.util.List;
import java.util.Map;

public interface IBIDao {
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
}

package com.hs.elsearch.service;

import com.hs.elsearch.entity.SearchConditions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * ES的查询服务
 */
public interface ISearchService {
    /**
     * 获取复杂查询的数据，数据返回格式为echart的dataset格式
     * {
     *  dimensions: ["xAxis","192.168.1.1-8080","192.168.1.1-8443"],
     *  source: [
     *   {"xAxis":"2020-02-02 11:00:00","192.168.1.1-8080":200,"192.168.1.1-8443":100},
     *   {"xAxis":"2020-02-02 11:01:00","192.168.1.1-8080":250,"192.168.1.1-8443":150}
     *   ]
     * }
     * @param conditions
     * @return
     * @throws Exception
     */
    public Map<String,Object> getMultiAggDataSet(SearchConditions conditions) throws Exception;

    /**
     * DataSet数据且带补零操作的
     * @param conditions
     * @return
     * @throws Exception
     */
    public Map<String,Object> getMultiAggDataSetWithZeroFill(SearchConditions conditions) throws Exception;

    /**
     * 普通分页查询，无聚合的结果，适配table数据格式
     * [{"fields.ip":"192.168.1.1","fields.equipmentname":"jzlog1"},{"fields.ip":"192.168.1.2","fields.equipmentname":"jzlog2"}]
     * @param conditions
     * @return
     * @throws Exception
     */
    public List<Map<String,Object>> getSearchHitsList(SearchConditions conditions) throws Exception;

    /**
     * 通过条件中的query 查询结果的count值
     * @param conditions
     * @return
     * @throws Exception
     */
    public long getCountByConditionsQuery(SearchConditions conditions) throws Exception;

}

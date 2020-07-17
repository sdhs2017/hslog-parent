package com.hs.elsearch.service;

import com.hs.elsearch.entity.SearchConditions;

import java.util.ArrayList;
import java.util.LinkedList;
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


}

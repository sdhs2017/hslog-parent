package com.hs.elsearch.dao.common;

import com.hs.elsearch.entity.HttpRequestParams;

import java.util.List;
import java.util.Map;

public interface ICommonDao {
    /**
     * x轴位时间的聚合
     * @param params
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getListByDateHistogramAggregation(HttpRequestParams params) throws Exception;
}

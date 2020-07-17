package com.hs.elsearch.dao.searchDao;

import com.hs.elsearch.entity.SearchConditions;
import com.hs.elsearch.entity.VisualParam;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregations;

import java.util.Map;

public interface ISearchDao {

    /**
     * 直接返回原始数据的接口
     * @param conditions
     * @return
     * @throws Exception
     */
    public Aggregations getAggregation(SearchConditions conditions) throws Exception;


}

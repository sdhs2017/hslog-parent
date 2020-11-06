package com.hs.elsearch.dao.searchDao;

import com.hs.elsearch.entity.SearchConditions;
import com.hs.elsearch.entity.VisualParam;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHits;
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
    /**
     * 直接返回response原始数据
     * @param conditions 查询条件
     * @return
     * @throws Exception
     */
    public SearchResponse getResponse(SearchConditions conditions) throws Exception;
    /**
     * 普通查询，无聚合 直接返回原始数据的接口
     * @param conditions
     * @return
     * @throws Exception
     */
    public SearchHits getSearchHitsByBuilder(SearchConditions conditions) throws Exception;
    /**
     * 通过查询条件获取查询结果的数据count值
     * @param conditions
     * @return
     * @throws Exception
     */
    public long getCountByConditionsQuery(SearchConditions conditions)throws Exception;
}

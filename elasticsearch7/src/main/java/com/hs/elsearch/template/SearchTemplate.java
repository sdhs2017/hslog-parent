package com.hs.elsearch.template;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: hslog
 * @description: es-5.4 transport search template
 * @author: Savilio
 * @create: 2019-08-15 14:05
 **/

//@Component
public class SearchTemplate {

    private static Logger logger = Logger.getLogger(SearchTemplate.class);

    /*@Autowired
    restHighLevelClient restHighLevelClient;*/

    RestHighLevelClient restHighLevelClient;

    public SearchTemplate(final RestHighLevelClient restHighLevelClient){
        this.restHighLevelClient = restHighLevelClient;
    }

    /**
     * 通过查询条件获取count
     * @param queryBuilder
     * @param indices
     * @return
     * 获取index下的索引数据条数
     */
    public long getCountByQuery(QueryBuilder queryBuilder,String... indices) throws Exception {

        //SearchRequest searchRequest = new SearchRequest(indices);
        CountRequest countRequest = new CountRequest(indices);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        if (queryBuilder!=null) {
            searchSourceBuilder.query(queryBuilder);
        }
        //searchRequest.source(searchSourceBuilder);
        countRequest.source(searchSourceBuilder);

        //SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        CountResponse countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
        //long length = searchResponse.getHits().getTotalHits().value;
        long length = countResponse.getCount();
        return length;
    }

    /**
     * 带有条件的聚合查询
     * @param queryBuilder
     * @param aggregationBuilder
     * @param indices
     * @return
     */
    public Aggregations getAggregationsByBuilder(QueryBuilder queryBuilder,AggregationBuilder aggregationBuilder,String... indices) throws Exception {

        SearchRequest searchRequest = new SearchRequest(indices);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        if (queryBuilder!=null){
            searchSourceBuilder.query(queryBuilder);
        }
        if (aggregationBuilder!=null){
            searchSourceBuilder.aggregation(aggregationBuilder);
        }
        searchRequest.source(searchSourceBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        Aggregations aggregations= response.getAggregations();

        return aggregations;
    }

    /**
     * 聚合查询
     * @param aggregationBuilder
     * @param indices
     * @return
     */
    public Aggregations getAggregationsByBuilder(AggregationBuilder aggregationBuilder, String... indices) throws Exception {

        SearchRequest searchRequest = new SearchRequest(indices);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        if (aggregationBuilder!=null){
            searchSourceBuilder.aggregation(aggregationBuilder);
        }
        searchRequest.source(searchSourceBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        Aggregations aggregations= response.getAggregations();

        return aggregations;
    }

    /**
     * 通过查询体（QueryBuilder）获取数据（带排序功能）
     * 带有排序的分页查询，带有高亮
     * @param queryBuilder 查询体
     * @param sortBuilder 排序设置
     * @param highlightBuilder 高亮内容设置
     * @param from 翻页
     * @param size 每页数据条数
     * @param indices
     * @return
     */
    public List<Map<String, Object>> getListByBuilder(QueryBuilder queryBuilder, SortBuilder sortBuilder, HighlightBuilder highlightBuilder, int from, int size, String... indices) throws Exception {

        SearchRequest searchRequest = new SearchRequest(indices);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        if (queryBuilder!=null){
            sourceBuilder.query(queryBuilder);
        }
        if (sortBuilder!=null){
            sourceBuilder.sort(sortBuilder);
        }
        if (highlightBuilder!=null){
            sourceBuilder.highlighter(highlightBuilder);
        }
        if (from>=0&&size>=0){
            sourceBuilder.from(from).size(size);
        }
        searchRequest.source(sourceBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

        SearchHits searchHits = response.getHits();

        return getListBySearchHit(searchHits);
    }

    /**
     * 通过查询体（QueryBuilder）获取数据（带排序功能）
     * 带有排序的分页查询
     * @param queryBuilder 查询体
     * @param sortBuilder 排序体
     * @param from 翻页
     * @param size 每页数据条数
     * @param indices
     * @return
     */
    public List<Map<String, Object>> getListByBuilder(QueryBuilder queryBuilder, SortBuilder sortBuilder,Integer from,Integer size, String... indices) throws Exception {

        SearchRequest searchRequest = new SearchRequest(indices);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        if (queryBuilder!=null){
            sourceBuilder.query(queryBuilder);
        }
        if (sortBuilder!=null){
            sourceBuilder.sort(sortBuilder);
        }
        if (from>=0&&size>=0){
            sourceBuilder.from(from).size(size);
        }
        searchRequest.source(sourceBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

        SearchHits searchHits = response.getHits();

        return getListBySearchHit(searchHits);
    }

    /**
     * 通过查询体（QueryBuilder）获取数据（带排序功能）
     * @param queryBuilder
     * @param sortBuilder
     * @param indices
     * @return
     */
    public List<Map<String, Object>> getListByBuilder(QueryBuilder queryBuilder, SortBuilder sortBuilder, String... indices) throws Exception {

        SearchRequest searchRequest = new SearchRequest(indices);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        if (queryBuilder!=null){
            sourceBuilder.query(queryBuilder);
        }
        if (sortBuilder!=null){
            sourceBuilder.sort(sortBuilder);
        }
        searchRequest.source(sourceBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

        SearchHits searchHits = response.getHits();

        return getListBySearchHit(searchHits);
    }

    /**
     * 通过查询体（QueryBuilder）获取数据
     * @param queryBuilder
     * @param indices
     * @return
     */
    public List<Map<String, Object>> getListByBuilder(QueryBuilder queryBuilder, String... indices) throws Exception {
        SearchRequest searchRequest = new SearchRequest(indices);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        if (queryBuilder!=null){
            sourceBuilder.query(queryBuilder);
        }
        searchRequest.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        SearchHits searchHits = response.getHits();
        return getListBySearchHit(searchHits);
    }

    /**
     * 通过SearchHit数组获取数据，补全index、type、id，组成list，返回
     * TODO 确认api是否有直接返回集合的方法
     * @param searchHits
     * @return
     */
    public List<Map<String, Object>> getListBySearchHit(SearchHits searchHits){
        SearchHit[] searchHit = searchHits.getHits();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(SearchHit hit : searchHit) {
            Map<String, Object> map = hit.getSourceAsMap();
            map.put("index", hit.getIndex());
            map.put("id", hit.getId());
            map.put("type",map.get("hslog_type"));
            // 处理高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            for(Map.Entry<String, HighlightField> entry : highlightFields.entrySet()){
                if (entry.getValue()!=null){
                    Text[] texts = entry.getValue().fragments();
                    String name = "";
                    for(Text text :texts){
                        name += text;
                    }
                    map.put(entry.getKey(),name);
                }
            }
            list.add(map);
        }
        return list;
    }
    public List<Map<String, Object>> getListBySearchHit2(SearchHits searchHits){
        SearchHit[] searchHit = searchHits.getHits();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(SearchHit hit : searchHit) {
            Map<String, Object> map = hit.getSourceAsMap();
//            map.put("index", hit.getIndex());
//            map.put("type", hit.getType());
//            map.put("id", hit.getId());
            list.add(map);
        }
        return list;
    }

}

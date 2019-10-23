package com.hs.elsearch.template;

import com.hs.elsearch.template.bak.ClientTemplate;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @program: hslog
 * @description: es-5.4 transport search template
 * @author: Savilio
 * @create: 2019-08-15 14:05
 **/

//@Component
public class ESTransportSearchTemplate {

    private static Logger logger = Logger.getLogger(ESTransportSearchTemplate.class);

    /*@Autowired
    TransportClient transportClient;*/

    private  TransportClient transportClient;

    public ESTransportSearchTemplate(TransportClient transportClient){
        logger.info(" 初始化 ESTransportSearchTemplate ... ");
        this.transportClient = transportClient;
    }

    /**
     * 通过查询条件获取count
     * @param types
     * @param queryBuilder
     * @param indices
     * @return
     * 获取index+type下的索引数据条数
     */
    public long getCountByQuery(QueryBuilder queryBuilder,String [] types,String... indices) {
        SearchRequestBuilder searchRequestBuilder = transportClient.prepareSearch(indices);
        if (types!=null&&types.length>0) {
            searchRequestBuilder.setTypes(types);
        }
        if (queryBuilder!=null) {
            searchRequestBuilder.setQuery(queryBuilder);
        }
        SearchResponse response = searchRequestBuilder.get();
        long length = response.getHits().getTotalHits();
        return length;
    }

    /**
     * 带有条件的聚合查询
     * @param queryBuilder
     * @param aggregationBuilder
     * @param types
     * @param indices
     * @return
     */
    public Aggregations getAggregationsByBuilder(QueryBuilder queryBuilder,AggregationBuilder aggregationBuilder,String [] types,String... indices){

        SearchRequestBuilder requestBuilder = transportClient.prepareSearch(indices);
        if (types!=null&&types.length>0){
            requestBuilder.setTypes(types);
        }
        if (queryBuilder!=null){
            requestBuilder.setQuery(queryBuilder);
        }
        if (aggregationBuilder!=null){
            requestBuilder.addAggregation(aggregationBuilder);
        }

        SearchResponse response = requestBuilder.get();

        Aggregations aggregations= response.getAggregations();

        return aggregations;
    }

    /**
     * 聚合查询
     * @param aggregationBuilder
     * @param types
     * @param indices
     * @return
     */
    public Aggregations getAggregationsByBuilder(AggregationBuilder aggregationBuilder, String [] types, String... indices){

        SearchRequestBuilder requestBuilder = transportClient.prepareSearch(indices);
        if (types!=null&&types.length>0){
            requestBuilder.setTypes(types);
        }
        if (aggregationBuilder!=null){
            requestBuilder.addAggregation(aggregationBuilder);
        }

        SearchResponse response = requestBuilder.get();

        Aggregations aggregations = response.getAggregations();

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
     * @param types
     * @param indices
     * @return
     */
    public List<Map<String, Object>> getListByBuilder(QueryBuilder queryBuilder, SortBuilder sortBuilder, HighlightBuilder highlightBuilder, int from, int size, String [] types, String... indices){

        SearchRequestBuilder requestBuilder = transportClient.prepareSearch(indices);
        if (types!=null&&types.length>0){
            requestBuilder.setTypes(types);
        }
        if (queryBuilder!=null){
            requestBuilder.setQuery(queryBuilder);
        }
        if (sortBuilder!=null){
            requestBuilder.addSort(sortBuilder);
        }
        if (highlightBuilder!=null){
            requestBuilder.highlighter(highlightBuilder);
        }
        if (from!=-1&&size!=-1){
            requestBuilder.setFrom(from).setSize(size);
        }

        SearchResponse response = requestBuilder.get();

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
     * @param types
     * @param indices
     * @return
     */
    public List<Map<String, Object>> getListByBuilder(QueryBuilder queryBuilder, SortBuilder sortBuilder,int from,int size, String [] types, String... indices){

        SearchRequestBuilder requestBuilder = transportClient.prepareSearch(indices);
        if (types!=null&&types.length>0){
            requestBuilder.setTypes(types);
        }
        if (queryBuilder!=null){
            requestBuilder.setQuery(queryBuilder);
        }
        if (sortBuilder!=null){
            requestBuilder.addSort(sortBuilder);
        }
//        if (from!=-1&&size!=-1){
        if (from>=0&&size>=0){
            requestBuilder.setFrom(from).setSize(size);
        }

        SearchResponse response = requestBuilder.get();

        SearchHits searchHits = response.getHits();

        return getListBySearchHit(searchHits);
    }

    /**
     * 通过查询体（QueryBuilder）获取数据（带排序功能）
     * @param queryBuilder
     * @param sortBuilder
     * @param types
     * @param indices
     * @return
     */
    public List<Map<String, Object>> getListByBuilder(QueryBuilder queryBuilder, SortBuilder sortBuilder, String [] types, String... indices){

        SearchRequestBuilder requestBuilder = transportClient.prepareSearch(indices);
        if (types!=null&&types.length>0){
            requestBuilder.setTypes(types);
        }
        if (queryBuilder!=null){
            requestBuilder.setQuery(queryBuilder);
        }
        if (sortBuilder!=null){
            requestBuilder.addSort(sortBuilder);
        }

        SearchResponse response = requestBuilder.get();

        SearchHits searchHits = response.getHits();

        return getListBySearchHit(searchHits);
    }

    /**
     * 通过查询体（QueryBuilder）获取数据
     * @param queryBuilder
     * @param types
     * @param indices
     * @return
     */
    public List<Map<String, Object>> getListByBuilder(QueryBuilder queryBuilder, String [] types, String... indices){
        SearchRequestBuilder requestBuilder = transportClient.prepareSearch(indices);
        if (types!=null&&types.length>0){
            requestBuilder.setTypes(types);
        }
        if (queryBuilder!=null){
            requestBuilder.setQuery(queryBuilder);
        }
        SearchResponse response = requestBuilder.get();
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
            Map<String, Object> map = hit.getSource();
            map.put("index", hit.getIndex());
            map.put("type", hit.getType());
            map.put("id", hit.getId());
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
            Map<String, Object> map = hit.getSource();
//            map.put("index", hit.getIndex());
//            map.put("type", hit.getType());
//            map.put("id", hit.getId());
            list.add(map);
        }
        return list;
    }

}

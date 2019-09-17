package com.hs.elsearch.template;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: hslog
 * @description: es-5.4 transport search template
 * @author: Savilio
 * @create: 2019-08-15 14:05
 **/

@Component
public class ESTransportSearchTemplate {

    @Autowired
    TransportClient transportClient;

    /**
     *
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
     *
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
            list.add(map);
        }
        return list;
    }

}

package com.hs.elsearch.template;

import org.elasticsearch.action.admin.indices.close.CloseIndexResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @program: hslog
 * @description: es5.4 transport index template
 * @author: Savilio
 * @create: 2019-08-14 14:18
 **/

@Component
public class ESTransportIndexTemplate {

    @Autowired
    TransportClient transportClient;

    /**
     *
     * @param index
     * @param type
     * @param mappingproperties
     * @return
     */
    public Boolean addMapping(String index, String type,String mappingproperties) {
        boolean result = false;
        // 判断index是否存在
        if (this.indexExists(index)) {
            /*
             * 如果索引存在,则增加type，指定mapping。
             * 如果type、mapping也存在，并且mapping不一致，贼会报错
             */
            PutMappingRequest mapping = Requests.putMappingRequest(index).type(type).source(mappingproperties);
            PutMappingResponse mappingResponse = transportClient.admin().indices().putMapping(mapping).actionGet();
            result = mappingResponse.isAcknowledged();
        }else {
            //如果索引不存在，则创建索引、type，并且设置mapping
            CreateIndexResponse indexResponse = transportClient.admin().indices().prepareCreate(index).addMapping(type, mappingproperties).get();
            result = indexResponse.isAcknowledged();
        }
        return result;
    }

    /**
     * 创建指定名称的索引
     * @param index 索引名称
     * @return  成功true 失败false
     */
    public boolean createIndex(String index) {
        boolean result = false;
        CreateIndexResponse indexResponse = transportClient.admin()
                .indices()
                .prepareCreate(index)
                .get();
        result = indexResponse.isAcknowledged();
        return result;
    }

    /**
     * 删除指定index
     * @param index
     * @return
     */
    public boolean deleteByIndex(String index) {
        boolean result = false;
        DeleteIndexResponse dResponse = transportClient.admin().indices().prepareDelete(index)
                .execute().actionGet();
        result = dResponse.isAcknowledged();
        return result;
    }

    /**
     * 判断指定索引是否存在
     * @param index
     * @return
     */
    public boolean indexExists(String index) {
        boolean result = false;
        IndicesExistsRequest request = new IndicesExistsRequest(index);
        IndicesExistsResponse response = transportClient.admin().indices().exists(request).actionGet();
        result = response.isExists();
        return result;
    }

    /**
     * 查询指定索引的数据量，可指定多索引
     * @param indices   索引数组
     * @return  数据量
     * @throws IOException
     */
    public long getIndexCount(String... indices) throws IOException {
        SearchRequestBuilder searchRequestBuilder = transportClient.prepareSearch(indices);
        SearchResponse response = searchRequestBuilder.get();
        long count = response.getHits().getTotalHits();
        //es7.0以上版本:
        //long count = response.getHits().getTotalHits().value;
        return count;
    }

    /**
     * 开启指定索引
     * @param indices
     * @return
     */
    public boolean openIndex(String... indices){
        IndicesAdminClient indicesAdminClient = transportClient.admin()
                .indices();
        OpenIndexResponse response = indicesAdminClient.prepareOpen(indices).get();
        return response.isAcknowledged();
    }

    /**
     * 关闭指定索引
     * @param indices
     * @return
     */
    public boolean closeIndex(String... indices){
        IndicesAdminClient indicesAdminClient = transportClient.admin()
                .indices();
        CloseIndexResponse response = indicesAdminClient.prepareClose(indices).get();
        return response.isAcknowledged();
    }

}

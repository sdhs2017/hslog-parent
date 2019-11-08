package com.hs.elsearch.template;

import org.apache.log4j.Logger;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeRequest;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.bulk.byscroll.BulkByScrollResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @program: hslog
 * @description: es5.4 transport crud template
 * @author: Savilio
 * @create: 2019-08-14 14:17
 **/

//@Component
public class ESTransportCrudTemplate {

    private static Logger logger = Logger.getLogger(ESTransportSearchTemplate.class);

    /*@Autowired
    TransportClient transportClient;*/

    private  TransportClient transportClient;

    public ESTransportCrudTemplate(TransportClient transportClient){
        logger.info(" 初始化 ESTransportCrudTemplate ... ");
        this.transportClient = transportClient;
    }

    /*
     * <===========查询相关============>
     *     search
     * </===========查询相关============>
     */
    /**
     * 通过index，type，id查询指定数据
     * @param index
     * @param type
     * @param id
     * @return
     *
     *curl -X GET "IP:9200/index_name/_doc/ID?pretty"
     */
    public Map<String, Object> searchById(String index, String type, String id){
        GetResponse response = transportClient
                .prepareGet()   // 准备进行get操作，此时还有真正地执行get操作。（与直接get的区别）
                .setIndex(index)  // 要查询的
                .setType(type)
                .setId(id)
                .get();
        Map<String, Object> map = response.getSource();
        map.put("index", response.getIndex());
        map.put("type", response.getType());
        map.put("id", response.getId());

        return map;
    }

    /*
     * <===========添加相关============>
     *     add
     * </===========添加相关============>
     */

    /**
     * 批量提交步骤一：单一数据添加到批量提交list
     * @param index
     * @param type
     * @param json
     * @return
     */
    public IndexRequest insertNotCommit(String index, String type, String json) {

        IndexRequestBuilder response = transportClient.prepareIndex(index, type).setSource(json);
        IndexRequest request = response.request();
        return request;
    }

    /**
     * 批量提交步骤二：将步骤一的list，批量提交入库
     * @param requests
     *
     * curl -X POST "IP:9200/_bulk?pretty" -H 'Content-Type: application/json' -d'
     *     { "index" : { "_index" : "test", "_id" : "1" } }
     *     { "create" : { "_index" : "test", "_id" : "2" } }
     *     { "create" : { "_index" : "test", "_id" : "3" } }
     *     '
     */
    public void bulkInsert(List<IndexRequest> requests) {
        BulkRequestBuilder bulkRequest = transportClient.prepareBulk();
        for (IndexRequest request : requests) {
            bulkRequest.add(request);
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            System.out.println("批量提交索引文档错误！");
        }
    }

    /**
     * 添加单条数据
     * @param index
     * @param type
     * @param json
     *
     * curl -X PUT "IP:9200/index_name/_doc/id?&pretty" -H 'Content-Type: application/json' -d'
     *     {
     *     "user" : "kimchy",
     *     "logdate" : "2019-01-01 14:12:12",
     *     "message" : "trying out Elasticsearch"
     *     }
     *     '
     */
    public void insert(String index, String type, String json) {
        IndexRequestBuilder response = transportClient.prepareIndex(index, type);
        response.setSource(json);
        response.execute().actionGet();
    }


    /*
     * <===========删除相关============>
     *     delete
     * </===========删除相关============>
     */

    /**
     * 通过查询体删除指定index的指定数据
     * @param indices
     * @param queryBuilder
     * @return
     *
     * curl -X POST "IP:9200/index_name/_delete_by_query?pretty" -H 'Content-Type: application/json' -d'
     *     {
     *     "query": {
     *     "match": {
     *     "message": "some message"
     *     }
     *     }
     *     }
     *     '
     */
    public long countDeleteByQuery(QueryBuilder queryBuilder,String... indices) {
        BulkByScrollResponse response = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(transportClient)
                .filter(queryBuilder)
                .source(indices)
                .get();
        long deletecount = response.getDeleted();
        return deletecount;
    }

    /**
     * 删除后执行：强制合并，合并index后才可以释放存储空间
     * @param indices
     * @param maxNumSegments    要合并的段数
     *                          不用该参数时，指定-1
     *                          要完全合并索引，请将其设置为1.默认为只检查是否需要执行合并，如果需要，则执行它。
     *
     *                          默认-1，表示只处理删除部分的合并，不合并index
     *                          1表示合并指定索引
     *
     * @param onlyExpungeDeletes 合并进程是否只删除其中包含删除内容的段,此标志只允许合并已删除的段。默认为false。
     *                           如果要立刻释放存储，则传入true
     *                           否，则传入false
     * @return
     */
    public ForceMergeResponse indexForceMerge(int maxNumSegments, boolean onlyExpungeDeletes,String... indices) {
        ForceMergeRequest request = new ForceMergeRequest(indices);
        // 要合并的段数。 要完全合并索引，请将其设置为1.默认为只检查是否需要执行合并，如果需要，则执行它。
        if (maxNumSegments!=-1) {
            request.maxNumSegments(maxNumSegments);
        }
        // 合并进程是否只删除其中包含删除内容的段,此标志只允许合并已删除的段。默认为false。
        if (onlyExpungeDeletes) {
            request.onlyExpungeDeletes(true);
        }
        // 默认强制合并后执行刷新
        request.flush(true);
        ForceMergeResponse response = transportClient.admin().indices().forceMerge(request).actionGet();
        return response;
    }

    /**
     * 通过id删除单一数据(doc)
     * @param index
     * @param type
     * @param id
     * @return
     *
     * curl -X DELETE "IP:9200/index_name/_doc/ID?pretty"
     */
    public String deleteById(String index, String type, String id) {
        DeleteResponse response = transportClient.prepareDelete(index, type, id)
                .get();
        DocWriteResponse.Result result=response.getResult();
        return result.toString();
    }



}

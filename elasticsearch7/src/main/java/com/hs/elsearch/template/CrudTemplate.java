package com.hs.elsearch.template;

import org.apache.log4j.Logger;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeRequest;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;

import java.util.List;
import java.util.Map;

/**
 * @program: hsgit
 * @description: es7.4 rest high crud template
 * @author: jiyourui
 * @create: 2019-11-25 09:50
 **/
public class CrudTemplate {

    private static Logger logger = Logger.getLogger(CrudTemplate.class);

    RestHighLevelClient restHighLevelClient;

    public CrudTemplate(final RestHighLevelClient restHighLevelClient){
        this.restHighLevelClient = restHighLevelClient;
    }


    /**
     * 通过ID查询数据
     * @param index
     * @param id
     * @return
     * @throws Exception
     *
    curl -X GET "IP:9200/index_name/_doc/ID?pretty"
     */
    public Map<String, Object> searchById(String index, String id) throws Exception {

        GetRequest getRequest = new GetRequest(index,id);
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);

        if (getResponse.isExists()) {
            Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
            sourceAsMap.put("index",getResponse.getIndex());
            sourceAsMap.put("id",getResponse.getId());
            return  sourceAsMap;
        } else {
            return null;
        }
    }

    /**
     * 实现elasticsearch的index插入文档，id由elasticsearch自动补全
     * @param index
     * @param json
     * @throws Exception
     *
    curl -X POST "IP:9200/index_name/_doc/?pretty" -H 'Content-Type: application/json' -d'
    {
    "user" : "kimchy",
    "logdate" : "2019-01-01 14:12:12",
    "message" : "trying out Elasticsearch"
    }
    '
     */
    public String insert(String index,String json) throws Exception {
        IndexRequest request = new IndexRequest();
        request.index(index);
        request.source(json, XContentType.JSON);

        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);

        if (response.getResult() == DocWriteResponse.Result.CREATED) {
            return "插入数据成功";
        } else {
            return "插入数据失败";
        }
    }

    /**
     * 实现elasticsearch的index插入文档，id由数据段生成
     * @param index
     * @param json
     * @throws Exception
     *
    curl -X PUT "IP:9200/index_name/_doc/id?&pretty" -H 'Content-Type: application/json' -d'
    {
    "user" : "kimchy",
    "logdate" : "2019-01-01 14:12:12",
    "message" : "trying out Elasticsearch"
    }
    '
     */
    public String insert(String index,String id,String json) throws Exception {
        IndexRequest request = new IndexRequest();
        request.index(index);
        request.id(id);
        request.source(json, XContentType.JSON);

        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);

        if (response.getResult() == DocWriteResponse.Result.CREATED) {
            return "插入数据成功";
        } else if (response.getResult() == DocWriteResponse.Result.UPDATED) {
            return "更新数据成功";
        }else{
            return "数据操作失败";
        }
    }

    /**
     * 实现elasticsearch的index插入文档，id由数据段生成,但不提交
     * @param index
     * @param json
     * @return 返回一个IndexRequest，在实现时存入BulkRequest
     * @throws Exception
     */
    public IndexRequest insertNotCommit(String index,String json) throws Exception {
        IndexRequest request = new IndexRequest();
        request.index(index);
        request.source(json, XContentType.JSON);

        return request;
    }

    /**
     * 批量提交
     * @param request
     * @return
     * @throws Exception
     *
    curl -X POST "IP:9200/_bulk?pretty" -H 'Content-Type: application/json' -d'
    { "index" : { "_index" : "test", "_id" : "1" } }
    { "create" : { "_index" : "test", "_id" : "2" } }
    { "create" : { "_index" : "test", "_id" : "3" } }
    '
     */
    public boolean bulkInsert(List<IndexRequest> request) throws Exception {

        // 为了兼容5版本，参数设置为List<IndexRequest>
        BulkRequest bulkRequest = new BulkRequest();
        for (IndexRequest indexRequest : request){
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        // 判断是否存在错误提交
        if (bulkResponse.hasFailures()){
            // 如果存在报错则返回false
            return false;
        }else {
            // 正常提交返回true
            return true;
        }
    }



    /**
     * 通过ID删除数据
     * @param index
     * @param id
     * @return
     * @throws Exception
     *
    curl -X DELETE "IP:9200/index_name/_doc/ID?pretty"
     */
    public String deleteById(String index, String id) throws Exception {
        DeleteRequest request = new DeleteRequest(index,id);
        DeleteResponse deleteResponse = restHighLevelClient.delete(request, RequestOptions.DEFAULT);

        if (deleteResponse.getResult() == DocWriteResponse.Result.NOT_FOUND){
            return "未找到";
        }else if (deleteResponse.getResult() == DocWriteResponse.Result.DELETED){
            return "删除成功";
        }else {
            return "删除失败";
        }
    }


    /**
     * 实现通过查询进行批量删除
     * @param queryBuilder
     * @param indices
     * @return 删除数
     * @throws Exception
     *
    curl -X POST "IP:9200/index_name/_delete_by_query?pretty" -H 'Content-Type: application/json' -d'
    {
    "query": {
    "match": {
    "message": "some message"
    }
    }
    }
    '
     */
    public long deleteByQuery(QueryBuilder queryBuilder, String... indices) throws Exception {

        DeleteByQueryRequest request = new DeleteByQueryRequest(indices);
        // 查询体
        request.setQuery(queryBuilder);

        BulkByScrollResponse bulkResponse = restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);

        return bulkResponse.getDeleted();
    }


    /**
     * 查询并删除，然后强制合并删除段
     * @param queryBuilder
     * @param onlyExpungeDeletes
     * @param indices
     * @return
     * @throws Exception
     */
    public ForceMergeResponse DeleteAndForceMergeByQuery(QueryBuilder queryBuilder, boolean onlyExpungeDeletes, String... indices) throws Exception {

        DeleteByQueryRequest request = new DeleteByQueryRequest(indices);
        // 查询体
        request.setQuery(queryBuilder);

        BulkByScrollResponse bulkResponse = restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);

        ForceMergeRequest forceMergeRequest = null;
        // 如果索引不为空，则合并传入的索引
        if (indices!=null){
            forceMergeRequest  = new ForceMergeRequest(indices);
            // 如果索引为空，则合并所有索引
        }else {
            forceMergeRequest = new ForceMergeRequest();
        }
        // 合并进程是否只删除其中包含删除内容的段,此标志只允许合并已删除的段。默认为false。
        if (onlyExpungeDeletes){
            forceMergeRequest.onlyExpungeDeletes(onlyExpungeDeletes);
        }
        // 默认强制合并后执行刷新
        forceMergeRequest.flush(true);

        ForceMergeResponse forceMergeResponse = restHighLevelClient.indices().forcemerge(forceMergeRequest, RequestOptions.DEFAULT);
        return forceMergeResponse;
    }
}

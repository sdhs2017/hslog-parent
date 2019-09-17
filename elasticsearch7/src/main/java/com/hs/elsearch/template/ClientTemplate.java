package com.hs.elsearch.template;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeRequest;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeResponse;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indexlifecycle.*;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class ClientTemplate {


    RestHighLevelClient restHighLevelClient;

    public ClientTemplate(final RestHighLevelClient restHighLevelClient){
        this.restHighLevelClient = restHighLevelClient;
    }

    /*----------------------- elasticsearch java rest high level API 针对index的操作 -------------------------*/

    /**
     * 创建index
     * @param index
     * @param setting
     * @param mappingproperties
     * @return
     * @throws Exception
     *  对应 rest ful api curl 命令
    curl -X PUT "IP:9200/index_name?pretty" -H 'Content-Type: application/json' -d'
    {
    "settings" : {
    "index" : {
    "number_of_shards" : 1,
    "number_of_replicas" : 1
    }
    },
    "mappings" : {
    "properties" : {
    "field1" : { "type" : "text" }
    }
    }
    }'
     */
    public boolean createIndex(String index, Settings.Builder setting, String mappingproperties) throws Exception {
        CreateIndexRequest request = new CreateIndexRequest(index);
        // 设置index的属性，包括分片数、副本数，如果不设置，默认分片是1，副本数是1
        if (setting!=null){
            request.settings(setting);
        }
        // 设置mapping属性
        if (mappingproperties!=null&&!mappingproperties.equals("")){
            request.mapping(mappingproperties,XContentType.JSON);
        }

        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        return  createIndexResponse.isAcknowledged();
    }
    /**
     *
     * @param indices
     * @return 获取index的数据量long类型
     * @throws Exception
     *
     curl -X GET "IP:9200/index_name?pretty"
     */
    public long getIndexCount(String... indices) throws Exception {

        CountRequest countRequest = new CountRequest();

        countRequest.indices(indices);

        CountResponse countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);

        return countResponse.getCount();
    }

    /**
     * 判断index是否存在
     * @param index
     * @return
     *
    curl -I "IP:9200/index_name?pretty"
     *
     */
    public boolean indexExists(String index) throws Exception {

        GetIndexRequest request = new GetIndexRequest(index);
        // 返回本地信息还是从主节点检索状态，false是从主节点检索状态
        request.local(false);
        // 返回格式化后的结果
        request.humanReadable(true);
        // 返回索引的默认设置
        request.includeDefaults(false);
        //request.indicesOptions(indicesOptions);

        boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);

        return exists;
    }

    /**
     *
     * @param index
     * @param mappingproperties
     * @return
     * @throws Exception
     *
    curl -X PUT "IP:9200/index_name/_mapping?pretty" -H 'Content-Type: application/json' -d'
    {
    "properties": {
    "field1": {
    "type": "keyword"
    }
    }
    }
    '
     */
    public boolean putMapping(String index,String mappingproperties) throws Exception {

        PutMappingRequest request = new PutMappingRequest(index);

        request.source(mappingproperties,XContentType.JSON);

        AcknowledgedResponse putMappingResponse = restHighLevelClient.indices().putMapping(request, RequestOptions.DEFAULT);

        boolean acknowledged = putMappingResponse.isAcknowledged();

        return acknowledged;
    }

    /**
     * 删除索引
     * @param indices
     * @return
     * @throws Exception
     *
    curl -X DELETE "IP:9200/index_name?pretty"
     */
    public boolean deleteIndex(String... indices) throws Exception {

        DeleteIndexRequest request = new DeleteIndexRequest(indices);

        AcknowledgedResponse deleteIndexResponse = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);

        return deleteIndexResponse.isAcknowledged();
    }

    /**
     * 开启索引
     * @param indices
     * @return
     * @throws Exception
     *
    curl -X POST "IP:9200/index_name/_open?pretty"
     */
    public boolean openIndex(String... indices) throws Exception {
        OpenIndexRequest request = new OpenIndexRequest(indices);

        OpenIndexResponse openIndexResponse = restHighLevelClient.indices().open(request, RequestOptions.DEFAULT);

        boolean acknowledged = openIndexResponse.isAcknowledged();

        return acknowledged;
    }

    /**
     * 关闭索引
     * @param indices
     * @return
     * @throws Exception
     *
    curl -X POST "IP:9200/index_name/_close?pretty"
     */
    public boolean closeIndex(String...indices) throws Exception {
        CloseIndexRequest request = new CloseIndexRequest(indices);

        AcknowledgedResponse closeIndexResponse = restHighLevelClient.indices().close(request, RequestOptions.DEFAULT);

        boolean acknowledged = closeIndexResponse.isAcknowledged();

        return acknowledged;
    }

    /**
     * 强制合并索引
     * @param maxNumSegments
     * @param onlyExpungeDeletes
     * @param indices
     * @return
     * @throws Exception
     *
    curl -X POST "IP:9200/index_name/_forcemerge?only_expunge_deletes=true&max_num_segments=1&flush=true&pretty"
     */
    public ForceMergeResponse mergeIndex(int maxNumSegments, boolean onlyExpungeDeletes,String...indices) throws Exception {

        ForceMergeRequest request = null;
        // 如果索引不为空，则合并传入的索引
        if (indices!=null){
            request  = new ForceMergeRequest(indices);
        // 如果索引为空，则合并所有索引
        }else {
            request = new ForceMergeRequest();
        }
        // 要合并的段数。 要完全合并索引，请将其设置为1.默认为只检查是否需要执行合并，如果需要，则执行它。
        if (maxNumSegments!=-1){
            request.maxNumSegments(maxNumSegments);
        }
        // 合并进程是否只删除其中包含删除内容的段,此标志只允许合并已删除的段。默认为false。
        if (onlyExpungeDeletes){
            request.onlyExpungeDeletes(onlyExpungeDeletes);
        }
        // 默认强制合并后执行刷新
        request.flush(true);

        ForceMergeResponse forceMergeResponse = restHighLevelClient.indices().forcemerge(request, RequestOptions.DEFAULT);
        return forceMergeResponse;
    }

    /*----------------------- elasticsearch java rest high level API 针对索引生命周期的操作 -------------------------*/

    /**
     *
     * @param policy_name 策略名称
     * @param delete_duration 删除策略时间（单位：天）
     * @return
     * @throws Exception
     *
    curl -X PUT "IP:9200/_ilm/policy/your_policy_name?pretty" -H 'Content-Type: application/json' -d'
    {
    "policy": {
    "phases": {
    "delete": {
    "min_age": "180d",
    "actions": {
    "delete": {}
    }
    }
    }
    }
    }
    '
     */
    public boolean createLifeCycle(String policy_name,long delete_duration) throws Exception {

        Map<String, Phase> phases = new HashMap<>();

        /*Map<String, LifecycleAction> hotActions = new HashMap<>();
        hotActions.put(RolloverAction.NAME, new RolloverAction(
                new ByteSizeValue(50, ByteSizeUnit.GB), null, null));
        phases.put("hot", new Phase("hot", TimeValue.ZERO, hotActions));*/

        // 设置索引删除周期
        Map<String, LifecycleAction> deleteActions = Collections.singletonMap(DeleteAction.NAME, new DeleteAction());
        phases.put("delete", new Phase("delete",new TimeValue(delete_duration, TimeUnit.DAYS), deleteActions));

        // 创建策略
        LifecyclePolicy policy = new LifecyclePolicy(policy_name, phases);

        PutLifecyclePolicyRequest request = new PutLifecyclePolicyRequest(policy);

        org.elasticsearch.client.core.AcknowledgedResponse response = restHighLevelClient.indexLifecycle().putLifecyclePolicy(request, RequestOptions.DEFAULT);

        boolean acknowledged = response.isAcknowledged();

        return  acknowledged;
    }

    public void getLifeCycle(String... policy_name) throws Exception {
        // 建立获取policy的请求
        GetLifecyclePolicyRequest request =  new GetLifecyclePolicyRequest(policy_name);

        GetLifecyclePolicyResponse response = restHighLevelClient.indexLifecycle().getLifecyclePolicy(request, RequestOptions.DEFAULT);

        ImmutableOpenMap<String, LifecyclePolicyMetadata> policies = response.getPolicies();
        for (String policy : policy_name){
            LifecyclePolicyMetadata myPolicyMetadata = policies.get(policy);
            String myPolicyName = myPolicyMetadata.getName();
            long version = myPolicyMetadata.getVersion();
            String lastModified = myPolicyMetadata.getModifiedDateString();
            long lastModifiedDate = myPolicyMetadata.getModifiedDate();
            LifecyclePolicy myPolicy = myPolicyMetadata.getPolicy();
            for (String key : myPolicy.getPhases().keySet()){
                System.out.println(myPolicy.getPhases().get(key));

            }

        }

    }

    /*----------------------- elasticsearch java rest high level API 针对模板的操作 -------------------------*/

    /**
     * 创建或者更新template
     * @param tempalateName 模板名
     * @param tempalatePattern 通配符：匹配index名称
     * @param settings 模板设置
     * @param mapping mapping属性
     * @return
     * @throws Exception
     *
    curl -X PUT "IP:9200/_template/template_name?pretty" -H 'Content-Type: application/json' -d'
    {
    "index_patterns": ["eslog-*"],
    "settings": {
    "number_of_shards": 1
    },
    "mappings": {
    "properties": {
    "host_name": {
    "type": "keyword"
    },
    "created_at": {
    "type": "date",
    "format": "yyyy-MM-dd HH:mm:ss"
    }
    }
    }
    }
    '
     */
    public boolean createOrupdateTemplate(String tempalateName, List<String> tempalatePattern, Settings settings, String mapping) throws Exception {
        // elasticsearch 索引模板名称
        PutIndexTemplateRequest request = new PutIndexTemplateRequest(tempalateName);
        // 设置模板的匹配值，动态创建索引时索引名与模板进行匹配，例如：eslog-*可以匹配到eslog-analysis
        request.patterns(tempalatePattern);
        // 设置创建索引时的分片数、副本数等
        request.settings(settings);
        // mapping的具体设置
        request.mapping(mapping, XContentType.JSON);
        // 设置模板的优先级，参数int类型，不设置默认是0，我们默认1，值越大优先级越高
        request.order(1);
        // 设置版本，int类型
        request.version();
        // 是否覆盖原有模板，false：覆盖，true：不覆盖，会报模板已存在的错
        request.create(false);

        AcknowledgedResponse putTemplateResponse = restHighLevelClient.indices().putTemplate(request, RequestOptions.DEFAULT);

        boolean acknowledged = putTemplateResponse.isAcknowledged();
        return acknowledged;
    }

    /**
     * 获取template信息
     * @param templatename
     * @return
     * @throws Exception
     *
    curl -X GET "IP:9200/_template/temp*?pretty"
    curl -X GET "IP:9200/_template/template_1,template_2?pretty"
     */
    public List<IndexTemplateMetaData> getTemplate(String... templatename) throws Exception {
        GetIndexTemplatesRequest request = new GetIndexTemplatesRequest(templatename);

        GetIndexTemplatesResponse getTemplatesResponse = restHighLevelClient.indices().getIndexTemplate(request, RequestOptions.DEFAULT);

        List<IndexTemplateMetaData> templates = getTemplatesResponse.getIndexTemplates();
        return  templates;
    }

    /**
     * 判断模板是否存在
     * @param templatename
     * @return
     * @throws Exception
     *
    curl -I "IP:9200/_template/template_name?pretty"
     */
    public boolean templateExist(String... templatename) throws Exception {
        IndexTemplatesExistRequest request = new IndexTemplatesExistRequest(templatename);
        // 设置是否从节点本地读取template状态，true是，false从master节点读取
        request.setLocal(false);
        boolean exists = restHighLevelClient.indices().existsTemplate(request, RequestOptions.DEFAULT);

        return exists;
    }

    /**
     * 删除模板
     * @param templatename 模板名
     * @return
     * @throws Exception
     *
    curl -X DELETE "IP:9200/_template/template_name?pretty"
     */
    public boolean deleteTemplate(String templatename) throws Exception {
        DeleteIndexTemplateRequest request = new DeleteIndexTemplateRequest(templatename);

        AcknowledgedResponse deleteTemplateAcknowledge = restHighLevelClient.indices().deleteTemplate(request,RequestOptions.DEFAULT);

        return deleteTemplateAcknowledge.isAcknowledged();
    }

    /*----------------------- elasticsearch java rest high level API 针对index中数据的操作 -------------------------*/

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
        request.source(json,XContentType.JSON);

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
    public boolean bulkInsert(BulkRequest request) throws Exception {

        BulkResponse bulkResponse = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);

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
    public long deleteByQuery(QueryBuilder queryBuilder,String... indices) throws Exception {

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


    /*----------------------- elasticsearch java rest high level API 针对查询的操作 -------------------------*/

    /**
     * 通过ID查询数据
     * @param index
     * @param id
     * @return
     * @throws Exception
     *
    curl -X GET "IP:9200/index_name/_doc/ID?pretty"
     */
    public Map<String, Object> searchById(String index,String id) throws Exception {

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

    /*-------复合查询-------*/



    public void forcemergeIndex(String [] indices){
        ForceMergeRequest request = new ForceMergeRequest(indices);
        request.maxNumSegments(1);
        request.onlyExpungeDeletes(true);
        request.flush(true);



        //ForceMergeResponse forceMergeResponse = client.indices().forcemerge(request, RequestOptions.DEFAULT);
    }

}

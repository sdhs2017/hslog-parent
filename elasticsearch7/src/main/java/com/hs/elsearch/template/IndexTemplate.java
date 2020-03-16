package com.hs.elsearch.template;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.cluster.repositories.delete.DeleteRepositoryRequest;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesRequest;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesResponse;
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequest;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeRequest;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeResponse;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indexlifecycle.*;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.cluster.metadata.RepositoryMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.repositories.fs.FsRepository;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @program: hsgit
 * @description: es7.4 rest high index template
 * @author: jiyourui
 * @create: 2019-11-25 09:52
 **/
public class IndexTemplate {

    private static Logger logger = Logger.getLogger(IndexTemplate.class);

    RestHighLevelClient restHighLevelClient;

    public IndexTemplate(final RestHighLevelClient restHighLevelClient){
        this.restHighLevelClient = restHighLevelClient;
    }

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
            request.mapping(mappingproperties, XContentType.JSON);
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
     * @param indices
     * @return
     *
    curl -I "IP:9200/index_name?pretty"
     *
     */
    public boolean indexExists(String... indices) throws Exception {

        GetIndexRequest request = new GetIndexRequest(indices);
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
     * 更新index setting
     * @param indices 索引名
     * @param map setting的map方式
     * @return
     */
    public boolean updateSettings(Map<String, Object> map,String... indices) throws IOException {

        UpdateSettingsRequest request = new UpdateSettingsRequest(indices);
        request.settings(map);
        AcknowledgedResponse response =
                restHighLevelClient.indices().putSettings(request, RequestOptions.DEFAULT);

        return response.isAcknowledged();
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
    public ForceMergeResponse mergeIndex(int maxNumSegments, boolean onlyExpungeDeletes, String...indices) throws Exception {

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

    /**
     * 开启index的生命周期管理
     * @return
     * @throws Exception
     */
    public boolean startIndexLifeCycle() throws Exception {
        StartILMRequest request = new StartILMRequest();

        org.elasticsearch.client.core.AcknowledgedResponse response = restHighLevelClient.indexLifecycle().startILM(request,RequestOptions.DEFAULT);

        return response.isAcknowledged();
    }

    /**
     * 查看生命周期管理状态
     * @return
     * @throws Exception
     */
    public String getLifecycleManagementStatus() throws Exception {
        LifecycleManagementStatusRequest request = new LifecycleManagementStatusRequest();

        LifecycleManagementStatusResponse response = restHighLevelClient.indexLifecycle().lifecycleManagementStatus(request,RequestOptions.DEFAULT);

        OperationMode mode = response.getOperationMode();

        return mode.name();
    }

    /**
     * 创建或者更新template
     * @param tempalateName 模板名
     * @param templatePattern 通配符：匹配index名称
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
    public boolean createOrupdateTemplate(String tempalateName, List<String> templatePattern, Map<String,Object> settings, String mapping) throws Exception {
        // elasticsearch 索引模板名称
        PutIndexTemplateRequest request = new PutIndexTemplateRequest(tempalateName);
        // 设置模板的匹配值，动态创建索引时索引名与模板进行匹配，例如：eslog-*可以匹配到eslog-analysis
        request.patterns(templatePattern);
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
    public List<IndexTemplateMetaData> getTemplate(String... templateNames) throws Exception {
        GetIndexTemplatesRequest request = new GetIndexTemplatesRequest(templateNames);
        GetIndexTemplatesResponse getTemplatesResponse = restHighLevelClient.indices().getIndexTemplate(request, RequestOptions.DEFAULT);
        List<IndexTemplateMetaData> templates = getTemplatesResponse.getIndexTemplates();
        return  templates;
    }

    /**
     * 获取index名称 模糊查询
     * @param indexNames
     * @return
     * @throws Exception
     */
    public String[] getIndex(String... indexNames) throws Exception {
        GetIndexRequest request = new GetIndexRequest(indexNames);

        GetIndexResponse getTemplatesResponse = restHighLevelClient.indices().get(request, RequestOptions.DEFAULT);

        String[] indices = getTemplatesResponse.getIndices();
        return  indices;
    }
    /**
     * 根据index获取mapping信息
     * @param indices 索引的名称
     * @return
     * @throws Exception
     *
    GET hslog_syslog2019-12-31/_mapping
     */

    public Map<String, MappingMetaData> getIndexMapping(String... indices) throws Exception {
        GetMappingsRequest getRequest = new GetMappingsRequest();
        getRequest.indices(indices);
        GetMappingsResponse getMappingResponse = restHighLevelClient.indices().getMapping(getRequest, RequestOptions.DEFAULT);
        Map<String, MappingMetaData> mappings = getMappingResponse.mappings();
        return  mappings;
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

    /**
     * 通过名称查询备份仓库
     * @param repositories 备份仓库名称
     * @return
     */
    public List<Map<String, Object>> getRepositoriesInfo(String... repositories) throws Exception {
        GetRepositoriesRequest getRepositoriesRequest = new  GetRepositoriesRequest();
        getRepositoriesRequest.repositories(repositories);
        GetRepositoriesResponse respose = restHighLevelClient.snapshot().getRepository(getRepositoriesRequest, RequestOptions.DEFAULT);
        List<RepositoryMetaData> list = respose.repositories();
        List<Map<String, Object>> repositorieslist = new ArrayList<Map<String,Object>>();
        for(RepositoryMetaData metadata:list) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", metadata.name());
            //map.putAll(metadata.settings().getAsMap());
            //TODO 确认返回的内容和5版本有何不同
            map.put("type",metadata.type());
            map.put("settings",metadata.settings());
            repositorieslist.add(map);
        }
        return repositorieslist;
    }

    /**
     * 创建备份仓库
     * @param repositoryName 备份仓库名称
     * @param repoPath 备份仓库路径
     * @return
     */
    public Boolean createRepositories(String repositoryName,String repoPath) throws Exception {

        PutRepositoryRequest request = new PutRepositoryRequest();

        request.name(repositoryName);
        request.type(FsRepository.TYPE);

        Map<String, Object> map = new HashMap<>();
        //map.put("location", "/home/elsearch/es_backups/my_backup/");
        map.put("location", repoPath);
        map.put("compress", true);
        request.settings(map);

        AcknowledgedResponse response = restHighLevelClient.snapshot().createRepository(request, RequestOptions.DEFAULT);

        return response.isAcknowledged();

    }

    /**
     * 删除备份仓库
     * @param repositoryName
     * @return
     */
    public Boolean deleteRepositories(String repositoryName) throws Exception {
        DeleteRepositoryRequest request = new DeleteRepositoryRequest(repositoryName);
            request.timeout(TimeValue.timeValueMinutes(1));
        request.timeout("1m");

        AcknowledgedResponse response = restHighLevelClient.snapshot().deleteRepository(request, RequestOptions.DEFAULT);
        return response.isAcknowledged();
    }
}

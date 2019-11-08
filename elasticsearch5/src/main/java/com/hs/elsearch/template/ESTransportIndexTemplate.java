package com.hs.elsearch.template;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.cluster.repositories.delete.DeleteRepositoryRequest;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesRequest;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesResponse;
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequest;
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryResponse;
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeRequest;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse;
import org.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateRequest;
import org.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesRequest;
import org.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesResponse;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.IndexTemplateMetaData;
import org.elasticsearch.cluster.metadata.RepositoryMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.repositories.fs.FsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: hslog
 * @description: es5.4 transport index template
 * @author: Savilio
 * @create: 2019-08-14 14:18
 **/

//@Component
public class ESTransportIndexTemplate {

    private static Logger logger = Logger.getLogger(ESTransportSearchTemplate.class);

    /*@Autowired
    TransportClient transportClient;*/

    private  TransportClient transportClient;

    public ESTransportIndexTemplate(TransportClient transportClient){
        logger.info(" 初始化 ESTransportIndexTemplate ... ");
        this.transportClient = transportClient;
    }

    /**
     *
     * @param index
     * @param type
     * @param mappingproperties
     * @return
     * @throws Exception
     *
     * curl -X PUT "IP:9200/index_name/_mapping?pretty" -H 'Content-Type: application/json' -d'
     *     {
     *     "properties": {
     *     "field1": {
     *     "type": "keyword"
     *     }
     *     }
     *     }
     *     '
     *
     */
    public Boolean addMapping(String index, String type,String mappingproperties) throws Exception {
        boolean result = false;
        // 判断index是否存在
        if (this.indexExists(index)) {
            /*
             * 如果索引存在,则增加type，指定mapping。
             * 如果type、mapping也存在，并且mapping不一致，也会报错
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
     * 更新index setting
     * @param index 索引名
     * @param map setting的map方式
     * @return
     */
    public boolean updateSettings(String index,Map<String, Object> map) {
        boolean result = false;
		/*Builder settings = Settings.builder()
				.put("index.max_result_window", 100000000)
				//.put("index.number_of_shards", 5)
				.put("index.number_of_replicas",2);*/
        // 更新index的setting属性
        UpdateSettingsRequest request = new UpdateSettingsRequest(index);
        request.settings(map);
        UpdateSettingsResponse response = this.transportClient.admin().indices().updateSettings(request).actionGet();
        result = response.isAcknowledged();

        return result;
    }

    /**
     * 创建指定名称的索引
     * @param index 索引名称
     * @return  成功true 失败false
     *
     * 对应 rest ful api curl 命令
     *     curl -X PUT "IP:9200/index_name?pretty" -H 'Content-Type: application/json' -d'
     *     {
     *     "settings" : {
     *     "index" : {
     *     "number_of_shards" : 1,
     *     "number_of_replicas" : 1
     *     }
     *     },
     *     "mappings" : {
     *     "properties" : {
     *     "field1" : { "type" : "text" }
     *     }
     *     }
     *     }'
     *
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
     *
     * curl -X DELETE "IP:9200/index_name?pretty"
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
     * @throws Exception
     *
     * curl -I "IP:9200/index_name?pretty"
     */
    public boolean indexExists(String index) throws Exception{
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
     * @throws Exception
     *
     * curl -X GET "IP:9200/index_name?pretty"
     */
    public long getIndexCount(String... indices) throws Exception {
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
     *
     * curl -X POST "IP:9200/index_name/_open?pretty"
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
     *
     * curl -X POST "IP:9200/index_name/_close?pretty"
     */
    public boolean closeIndex(String... indices){
        IndicesAdminClient indicesAdminClient = transportClient.admin()
                .indices();
        CloseIndexResponse response = indicesAdminClient.prepareClose(indices).get();
        return response.isAcknowledged();
    }

    /*public boolean createTempalteOfIndex(){
        IndicesAdminClient indicesAdminClient = transportClient.admin()
                .indices();

        PutIndexTemplateRequest request = new
        indicesAdminClient.putTemplate();
    }*/
    /**
     * 创建或者更新template
     * @param tempalateName 模板名
     * @param tempalatePattern 通配符：匹配index名称
     * @param settings 模板设置
     * @param type index的type名称，在7版本中删除
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
    public boolean createOrUpdateTemplateOfIndex(String tempalateName, String tempalatePattern, Map<String,Object> settings, String type, String mapping) throws Exception {


        IndicesAdminClient indicesAdminClient = transportClient.admin()
                .indices();

        // elasticsearch 索引模板名称
        PutIndexTemplateRequest request = new PutIndexTemplateRequest(tempalateName);
        // 设置模板的匹配值，动态创建索引时索引名与模板进行匹配，例如：eslog-*可以匹配到eslog-analysis
        request.template(tempalatePattern);
        //request.patterns(tempalatePattern);
        // 设置创建索引时的分片数、副本数等
        request.settings(settings);
        // mapping的具体设置
        request.mapping(type,mapping, XContentType.JSON);
        //request.mapping(mapping, XContentType.JSON);
        // 设置模板的优先级，参数int类型，不设置默认是0，我们默认1，值越大优先级越高
        request.order(1);
        // 设置版本，int类型
        request.version();
        // 是否覆盖原有模板，false：覆盖，true：不覆盖，会报模板已存在的错
        request.create(true);

        PutIndexTemplateResponse putTemplateResponse = indicesAdminClient.putTemplate(request).actionGet();

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
    public List<IndexTemplateMetaData> getTemplateOfIndex(String... templatename) throws Exception {
        IndicesAdminClient indicesAdminClient = transportClient.admin()
                .indices();

        GetIndexTemplatesRequest request = new GetIndexTemplatesRequest(templatename);

        GetIndexTemplatesResponse getTemplatesResponse = indicesAdminClient.getTemplates(request).actionGet();

        List<IndexTemplateMetaData> templates = getTemplatesResponse.getIndexTemplates();
        return  templates;
    }

    /**
     * 删除模板
     * @param templatename 模板名
     * @return
     * @throws Exception
     *
    curl -X DELETE "IP:9200/_template/template_name?pretty"
     */
    public boolean deleteTemplateOfIndex(String templatename) throws Exception {
        IndicesAdminClient indicesAdminClient = transportClient.admin()
                .indices();

        DeleteIndexTemplateRequest request = new DeleteIndexTemplateRequest(templatename);

        AcknowledgedResponse deleteTemplateAcknowledge = indicesAdminClient.deleteTemplate(request).actionGet();

        return deleteTemplateAcknowledge.isAcknowledged();
    }

    /**
     * 通过名称查询备份仓库
     * @param repositories 备份仓库名称
     * @return
     */
    public List<Map<String, Object>> getRepositoriesInfo(String... repositories) {
        GetRepositoriesRequest getRepositoriesRequest = new  GetRepositoriesRequest();
        getRepositoriesRequest.repositories(repositories);
        GetRepositoriesResponse respose = transportClient.admin().cluster().getRepositories(getRepositoriesRequest).actionGet();
        List<RepositoryMetaData> list = respose.repositories();
        List<Map<String, Object>> repositorieslist = new ArrayList<Map<String,Object>>();
        for(RepositoryMetaData metadata:list) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", metadata.name());
            map.putAll(metadata.settings().getAsMap());
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
    public Boolean createRepositories(String repositoryName,String repoPath) {

        PutRepositoryRequest request = new PutRepositoryRequest();
        request.name(repositoryName);
        request.type(FsRepository.TYPE);

        Map<String, Object> map = new HashMap<>();
        //map.put("location", "/home/elsearch/es_backups/my_backup/");
        map.put("location", repoPath);
        map.put("compress", true);
        request.settings(map);

        PutRepositoryResponse response = transportClient.admin().cluster().putRepository(request).actionGet();

        return response.isAcknowledged();

    }

    /**
     * 删除备份仓库
     * @param repositoryName
     * @return
     */
    public Boolean deleteRepositories(String repositoryName) {
        DeleteRepositoryRequest request = new DeleteRepositoryRequest(repositoryName);
        request.timeout(TimeValue.timeValueMinutes(1));
        request.timeout("1m");

        AcknowledgedResponse response = transportClient.admin().cluster().deleteRepository(request).actionGet();
        return response.isAcknowledged();
    }


    /**
     *
     * @param indices 需要合并的索引
     * @param maxNumSegments 合并段数
     * @param onlyExpungeDeletes 是否仅合并删除段
     * @return
     */
    public ForceMergeResponse indexForceMerge(String [] indices,int maxNumSegments,boolean onlyExpungeDeletes) {

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
}

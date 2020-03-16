package com.hs.elsearch.dao.logDao;

import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeResponse;
import org.elasticsearch.client.indices.IndexTemplateMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ILogIndexDao {


    /**
     * 创建指定名称的索引
     * @param index 索引名称
     * @return  成功true 失败false
     */
    public boolean createIndex(String index) throws Exception;

    /**
     * 创建index下type的mapping属性
     * @param index 索引名
     * @param type index 的type字段，在7版本中移除
     * @param mappingproperties 字段熟悉，json格式
     * @return 返回是否创建成功true，失败false
     */
    public Boolean addMapping(String index, String type, Map<String, Object> settings, String mappingproperties) throws Exception;

    /**
     * 查询索引是否存在
     * @param indices 索引名称
     * @return
     * @throws Exception
     */
    public Boolean indexExists(String... indices) throws Exception;


    /**
     * 删除指定index
     * @param indices
     * @return
     */
    public boolean deleteByIndex(String... indices) throws Exception;

    /**
     * 查询备份仓库信息
     * @param repositories 仓库名称
     * @return
     */
    public List<Map<String, Object>> getRepositoriesInfo(String... repositories) throws Exception;

    /**
     * 创建备份仓库
     * @param repositoryName 仓库名称
     * @param repoPath 物理路径
     * @return
     */
    public Boolean createRepositories(String repositoryName, String repoPath) throws Exception;

    /**
     * 删除备份仓库
     * @param repositoryName 仓库名称
     * @return
     */
    public Boolean deleteRepositories(String repositoryName) throws Exception;

    /**
     * 更新inde的setting属性
     * @param index 索引名
     * @param map setting的map
     * @return
     */
    public boolean updateSettings(String index, Map<String, Object> map) throws IOException;

    /**
     * 合并索引
     * @param indices 需要合并的索引
     * @param maxNumSegments 合并段数
     * @param onlyExpungeDeletes 是否仅合并删除段
     * @return
     */
    public ForceMergeResponse indexForceMerge(String[] indices, int maxNumSegments, boolean onlyExpungeDeletes) throws Exception;


    /**
     * 用于初始化操作创建index的模板
     * @param tempalateName 模板名称
     * @param tempalatePattern index名称通配
     * @param settings 针对index模板的具体属性设置，包括分片数、副本数、最大查询值等，其中分片数设置之后是不能再修改的
     * @param type 在5.4版本中index的template需要指明mapping对应的type，在7版本后只需要指定mapping
     * @param mapping 字段属性
     * @return 创建成功返回true，失败false
     */
    public boolean createTemplateOfIndex(String tempalateName, String tempalatePattern, Map<String,Object> settings,String type, String mapping) throws Exception;

    /**
     * 用于开启服务前的验证，保证elasticsearch5版本的index必须存在或者elasticsearch7版本的template必须存在
     * @param indexOrTemplate 索引名称或者模板命名称(elasticsearch5版本传入index，elasticsearch7版本传入template)
     * @return
     */
    public boolean checkOfIndexOrTemplate(String... indexOrTemplate) throws Exception;

    /**
     * 初始化elasticsearch 7版本的template或者5版本的index
     * @param indexOrTemplate
     * @param templatePattern
     * @param type
     * @param settings
     * @param mapping
     * @throws Exception
     */
    public void initOfElasticsearch(String indexOrTemplate, String templatePattern, String type, Map<String, Object> settings, String mapping) throws Exception;

    /**
     * index 的生命周期设置
     * @param policy_name 生命周期名称
     * @param delete_duration index删除周期
     * @return
     * @throws Exception
     */
    public boolean createLifeCycle(String policy_name,long delete_duration) throws Exception;

    /**
     * 开启生命周期管理
     * @return
     * @throws Exception
     */
    public boolean startIndexLifeCycle() throws Exception;

    /**
     * 查看生命周期管理状态
     * @return
     * @throws Exception
     */
    public String getLifecycleManagementStatus() throws Exception;

    /**
     * 获取Template信息
     * @param templatename 名称，支持多个一起查询
     * @return
     * @throws Exception
     */
    public List<IndexTemplateMetaData> getTemplateData(String ...templatename) throws Exception;

    /**
     * 获取index mapping信息
     * @param indexname 索引名称，支持多个一起查询
     * @return
     * @throws Exception
     */
    public Map<String, MappingMetaData> getIndexMappingData(String ...indexname) throws Exception;

    /**
     * 模糊查询获取index的名称
     * @param indexname
     * @return
     * @throws Exception
     */
    public String[] getIndices(String indexname) throws Exception;
}

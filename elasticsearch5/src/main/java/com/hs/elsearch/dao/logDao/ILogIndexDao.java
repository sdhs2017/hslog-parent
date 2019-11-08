package com.hs.elsearch.dao.logDao;

import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeResponse;

import java.util.List;
import java.util.Map;

public interface ILogIndexDao {


    /**
     * 创建指定名称的索引
     * @param index 索引名称
     * @return  成功true 失败false
     */
    public boolean createIndex(String index);

    /**
     * 创建index下type的mapping属性
     * @param index 索引名
     * @param type index 的type字段，在7版本中移除
     * @param mappingproperties 字段熟悉，json格式
     * @return 返回是否创建成功true，失败false
     */
    public Boolean addMapping(String index, String type,String mappingproperties) throws Exception;

    /**
     * 查询索引是否存在
     * @param index 索引名称
     * @return
     * @throws Exception
     */
    public Boolean indexExists(String index) throws Exception;


    /**
     * 删除指定index
     * @param index
     * @return
     */
    public boolean deleteByIndex(String index);

    /**
     * 查询备份仓库信息
     * @param repositories 仓库名称
     * @return
     */
    public List<Map<String, Object>> getRepositoriesInfo(String... repositories);

    /**
     * 创建备份仓库
     * @param repositoryName 仓库名称
     * @param repoPath 物理路径
     * @return
     */
    public Boolean createRepositories(String repositoryName,String repoPath);

    /**
     * 删除备份仓库
     * @param repositoryName 仓库名称
     * @return
     */
    public Boolean deleteRepositories(String repositoryName);

    /**
     * 更新inde的setting属性
     * @param index 索引名
     * @param map setting的map
     * @return
     */
    public boolean updateSettings(String index,Map<String, Object> map);

    /**
     * 合并索引
     * @param indices 需要合并的索引
     * @param maxNumSegments 合并段数
     * @param onlyExpungeDeletes 是否仅合并删除段
     * @return
     */
    public ForceMergeResponse indexForceMerge(String [] indices, int maxNumSegments, boolean onlyExpungeDeletes);
}

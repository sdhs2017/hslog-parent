package com.hs.elsearch.dao.common;

import com.hs.elsearch.entity.HttpRequestParams;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.script.Script;
import org.elasticsearch.snapshots.SnapshotInfo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ICommonDao {
    /**
     * x轴位时间的聚合
     * @param params
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getListByDateHistogramAggregation(HttpRequestParams params) throws Exception;

    /**
     * reindex
     * 将源index的数据重新写入到新index中，一般用于源和目的index的mapping字段的type不同。
     * @param script 自定义数据操作
     * @param source_indices 源index，可以是多个
     * @param target_index 目的index
     * @return
     */
    public boolean reindex(Script script, String target_index, String... source_indices) throws IOException;

    /**
     * 更新index的setting信息
     * @param settings
     * @param indices
     * @return
     * @throws IOException
     */
    public boolean updateIndexSettings(Settings settings, String... indices) throws IOException;

    /**
     * 删除index 支持批量删除
     * @param indices
     * @return
     * @throws Exception
     */
    public boolean deleteIndices(String... indices) throws Exception;

    /**
     * 删除template
     * @param templateName
     * @return
     * @throws Exception
     */
    public boolean deleteTemplate(String templateName) throws Exception;

    /**
     * 创建index
     * @param indexName index名称
     * @param setting setting信息
     * @param mappingProperties mapping信息
     * @return
     * @throws Exception
     */
    public boolean createIndex(String indexName,Settings.Builder setting, String mappingProperties) throws Exception;

    /**
     * index是否存在
     * @param indices
     * @return
     * @throws Exception
     */
    public boolean indexExists(String... indices) throws Exception;
    /**
     * 关闭索引
     * @param indices 索引名称
     * @return
     * @throws Exception
     *
     */
    public boolean closeIndex(String...indices) throws Exception ;

    /**
     * 开启索引
     * @param indices 索引名称
     * @return
     * @throws Exception
     *
     */
    public boolean openIndex(String... indices) throws Exception;
    /*
     * 创建快照备份策略
     * @param indices 要进行快照备份的索引名称
     * @param policy_id 策略id
     * @param name 快照名称
     * @param schedule 定时器，cron表达式
     * @param repository 备份仓库名称
     * @return
     */
    public boolean createSLMPolicy(String indices,String policy_id,String name,String schedule,String repository) throws IOException;

    /**
     * 执行快照策略，直接调用，没有返回true/false，能否执行应该是在创建时就已经进行的检测验证。
     * 返回执行后的第一个快照名称
     * @param policy_id 快照策略ID
     * @throws IOException
     */
    public String executeSLMPolicy(String policy_id) throws IOException;

    /**
     * 根据策略id删除快照策略
     * @param policy_id 快照策略ID
     * @return
     */
    public boolean deleteSLMPolicy(String policy_id) throws IOException;
    /**
     * 根据存储仓库获取所有快照的列表
     * @param repositoryName 存储仓库名称
     * @return 快照列表
     * @throws IOException
     */
    public List<SnapshotInfo> getSnapListByPolicyId(String repositoryName) throws IOException;

    /**
     * 还原快照
     * @param repositoryName 存储仓库名称
     * @param snapshotName 快照名称
     * @return
     * @throws IOException
     */
    public boolean restoreSnapshot(String repositoryName,String snapshotName) throws IOException;
}

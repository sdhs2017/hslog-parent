package com.hs.elsearch.dao.common;

import com.hs.elsearch.entity.HttpRequestParams;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.script.Script;

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
}

package com.jz.bigdata.common.metadata.service;

import com.jz.bigdata.common.metadata.entity.Metadata;

import java.util.List;

public interface IMetadataService {
    /**
     * 通过template名称获取template信息，支持模糊查询
     * @param templateName
     * @return
     * @throws Exception
     */
    public List<Metadata> getTemplateMapping(String templateName) throws Exception;

    /**
     * 动态获取template的mapping信息
     * @param templateName 模板名称
     * @param id 节点id
     * @return
     * @throws Exception
     */
    public List<Metadata> getTemplateMappingDynamically(String templateName,String id) throws Exception;

    /**
     * 通过索引名称获取其mapping
     * @param indexName
     * @return
     * @throws Exception
     */
    public List<Metadata> getIndexMapping(String indexName) throws Exception;

    /**
     *动态获取index的mapping信息
     * @param indexName 索引
     * @param id 节点id
     * @return
     * @throws Exception
     */
    public List<Metadata> getIndexMappingDynamically(String indexName,String id) throws Exception;
    /**
     * 通过template的前缀（hslog*），获取template信息，及其下属的index
     * @param es_tempalatePattern
     * @return
     * @throws Exception
     */
    public String getIndexTree(String es_tempalatePattern)throws Exception;
}

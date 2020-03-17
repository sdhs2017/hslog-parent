package com.jz.bigdata.common.metadata.service;

import com.jz.bigdata.common.metadata.entity.Metadata;

import java.util.List;

public interface IMetadataService {
    /**
     * 通过template名称获取template信息，支持模糊查询
     * @param template
     * @return
     * @throws Exception
     */
    public List<Metadata> getTemplateMapping(String template) throws Exception;

    /**
     * 通过索引名称获取其mapping
     * @param index
     * @return
     * @throws Exception
     */
    public List<Metadata> getIndexMapping(String index) throws Exception;

    /**
     * 通过template的前缀（hslog*），获取template信息，及其下属的index
     * @param es_tempalatePattern
     * @return
     * @throws Exception
     */
    public String getIndexTree(String es_tempalatePattern)throws Exception;
}

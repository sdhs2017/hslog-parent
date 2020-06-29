package com.jz.bigdata.common.configuration.service;

import com.jz.bigdata.common.configuration.entity.Configuration;

import java.util.List;

public interface IConfigurationService {
    /**
     * 增改配置信息
     * @param configuration
     * @return
     * @throws Exception
     */
    public int update(List<Configuration> list) throws Exception;
    /**
     * 通过key查询对应的值
     * @param configuration_key
     * @return
     */
    List<Configuration> selectByKey(String configuration_key);

    /**
     * 查询所有配置项
     * @return
     */
    List<Configuration> selectAll();
}

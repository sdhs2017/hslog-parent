package com.jz.bigdata.common.configuration.dao;

import com.jz.bigdata.common.configuration.entity.Configuration;

import java.util.List;

public interface IConfigurationDao {
    /**
     * 添改配置项
     * @param configuration
     * @return
     */
    int upsert(Configuration configuration);

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

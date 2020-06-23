package com.jz.bigdata.common.configuration.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jz.bigdata.common.businessIntelligence.cache.BICache;
import com.jz.bigdata.common.businessIntelligence.entity.MappingField;
import com.jz.bigdata.common.configuration.entity.Configuration;
import com.jz.bigdata.common.configuration.service.IConfigurationService;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import java.util.List;

/**
 * 配置项缓存
 */
public enum ConfigurationCache {
    INSTANCE;
    private static Logger logger = Logger.getLogger(ConfigurationCache.class);
    //
    private Cache<String, Object> configurationCache = Caffeine.newBuilder()
            .maximumSize(100)//最大条数，
            //.expireAfterWrite(99999, TimeUnit.DAYS)//过期时间，不设置则不会过期
            .build();

    public Cache<String, Object> getConfigurationCache() {
        return configurationCache;
    }

    /**
     * 初始化
     * @return
     */
    public Cache<String, Object> init(IConfigurationService configurationService){
        //获取所有配置信息
        List<Configuration> list = configurationService.selectAll();
        for(Configuration configuration:list){
            configurationCache.put(configuration.getConfiguration_key(),configuration.getConfiguration_value());
        }
        return configurationCache;
    }

}

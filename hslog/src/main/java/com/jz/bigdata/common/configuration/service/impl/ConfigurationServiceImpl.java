package com.jz.bigdata.common.configuration.service.impl;

import com.jz.bigdata.common.configuration.dao.IConfigurationDao;
import com.jz.bigdata.common.configuration.entity.Configuration;
import com.jz.bigdata.common.configuration.service.IConfigurationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service(value = "ConfigurationService")
public class ConfigurationServiceImpl implements IConfigurationService {
    @Resource
    private IConfigurationDao configurationDao;
    @Override
    public int update(List<Configuration> list) throws Exception {
        return configurationDao.update(list);
    }

    @Override
    public List<Configuration> selectByKey(String configuration_key) {
        return configurationDao.selectByKey(configuration_key);
    }

    @Override
    public List<Configuration> selectAll() {
        return configurationDao.selectAll();
    }
}

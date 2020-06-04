package com.hs.elsearch.dao.globalDao.impl;

import com.hs.elsearch.dao.globalDao.IGlobalDao;
import com.hs.elsearch.template.GlobalTemplate;
import org.elasticsearch.action.admin.cluster.settings.ClusterGetSettingsResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class GlobalDaoImpl implements IGlobalDao {
    @Autowired
    GlobalTemplate globalTemplate;
    @Override
    public boolean updateClusterSetting(Map<String, Object> persistentMap, Map<String, Object> transientMap) throws Exception {
        return globalTemplate.updateClusterSetting(persistentMap,transientMap);
    }

    @Override
    public ClusterGetSettingsResponse getClusterSetting() throws Exception {
        return globalTemplate.getClusterInfo();
    }
}

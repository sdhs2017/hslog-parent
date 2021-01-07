package com.hs.elsearch.dao.globalDao;

import org.elasticsearch.action.admin.cluster.settings.ClusterGetSettingsResponse;

import java.util.Map;

public interface IGlobalDao {
    /**
     * 设置cluster-setting信息
     * @param persistentMap
     * @param transientMap
     * @return
     * @throws Exception
     */
    public boolean updateClusterSetting(Map<String,Object> persistentMap , Map<String,Object> transientMap) throws Exception;

    /**
     * 获取cluster-setting信息
     * @return
     * @throws Exception
     */
    public ClusterGetSettingsResponse getClusterSetting() throws Exception;


}

package com.hs.elsearch.template;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.cluster.settings.ClusterGetSettingsRequest;
import org.elasticsearch.action.admin.cluster.settings.ClusterGetSettingsResponse;
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsRequest;
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;

import java.io.IOException;
import java.util.Map;

/**
 * 全局模板设置
 */
public class GlobalTemplate {
    RestHighLevelClient restHighLevelClient;

    public GlobalTemplate(final RestHighLevelClient restHighLevelClient){
        this.restHighLevelClient = restHighLevelClient;
    }

    /**
     * 更新es-cluster-setting信息
     * cluster-setting配置生效优先级
     * 1.transient瞬变的 cluster settings
     * 2.persistent持久的 cluster settings
     * 3.settings in the elasticsearch.yml configuration file.
     * 配置后是否立即生效与配置项有关
     * @param persistentMap  es重启后依然会生效
     * @param transientMap  es重启后，配置失效
     * @return
     * @throws Exception
     */
    public boolean updateClusterSetting(Map<String,Object> persistentMap ,Map<String,Object> transientMap) throws Exception {
        ClusterUpdateSettingsRequest request = new ClusterUpdateSettingsRequest();
        //只有在map中有数据的时候才添加到配置中
        if(null!=transientMap&&transientMap.size()>0){
            request.transientSettings(transientMap);
        }
        if(null!=persistentMap&&persistentMap.size()>0){
            request.persistentSettings(persistentMap);
        }
        ClusterUpdateSettingsResponse response = restHighLevelClient.cluster().putSettings(request, RequestOptions.DEFAULT);
        return response.isAcknowledged();
        //Settings transientSettingsResponse = response.getTransientSettings();
        //Settings persistentSettingsResponse = response.getPersistentSettings();
    }

    /**
     * 获取cluster-setting设置
     * @throws Exception
     */
    public ClusterGetSettingsResponse getClusterInfo() throws Exception {
        ClusterGetSettingsRequest request = new ClusterGetSettingsRequest();
        ClusterGetSettingsResponse response = restHighLevelClient.cluster().getSettings(request,RequestOptions.DEFAULT);
        return response;
    }
}

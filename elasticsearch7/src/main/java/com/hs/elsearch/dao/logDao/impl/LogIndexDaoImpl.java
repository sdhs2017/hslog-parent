package com.hs.elsearch.dao.logDao.impl;

import com.hs.elsearch.dao.logDao.ILogIndexDao;
import com.hs.elsearch.template.IndexTemplate;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeResponse;
import org.elasticsearch.client.indices.IndexTemplateMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @program: hsgit
 * @description: 实现ILogIndexDao接口
 * @author: jiyourui
 * @create: 2019-11-07 09:39
 **/
public class LogIndexDaoImpl implements ILogIndexDao {

    @Autowired
    IndexTemplate indexTemplate;


    @Override
    public boolean createIndex(String index) throws Exception {
        return indexTemplate.createIndex(index,null,null);
    }

    @Override
    public Boolean addMapping(String index, String type, Map<String,Object> settings, String mappingproperties) throws Exception {
        // 在7版本中，type已经移除，不存在针对type再创建mapping的需求
        //return indexTemplate.addMapping(index,type,settings,mappingproperties);
        return true;
    }

    @Override
    public Boolean indexExists(String... indices) throws Exception {
        return indexTemplate.indexExists(indices);
    }

    @Override
    public boolean deleteByIndex(String... indices) throws Exception {
        return indexTemplate.deleteIndex(indices);
    }

    @Override
    public List<Map<String, Object>> getRepositoriesInfo(String... repositories) throws Exception {
        return indexTemplate.getRepositoriesInfo(repositories);
    }

    @Override
    public Boolean createRepositories(String repositoryName, String repoPath) throws Exception {
        return indexTemplate.createRepositories(repositoryName,repoPath);
    }

    @Override
    public Boolean deleteRepositories(String repositoryName) throws Exception {
        return indexTemplate.deleteRepositories(repositoryName);
    }

    @Override
    public boolean updateSettings(String index, Map<String, Object> map) throws IOException {
        return indexTemplate.updateSettings(map,index);
    }

    @Override
    public ForceMergeResponse indexForceMerge(int maxNumSegments, boolean onlyExpungeDeletes,String... indices) throws Exception {
        return indexTemplate.mergeIndex(maxNumSegments, onlyExpungeDeletes, indices);
    }

    @Override
    public boolean createTemplateOfIndex(String tempalateName, String templatePattern, Map<String, Object> settings, String type, String mapping) throws Exception {
        //准备一个String数组
        String[] strs = templatePattern.split(",");
        //String数组转List
        List<String> templatePatterns = Arrays.asList(strs);

        return indexTemplate.createOrupdateTemplate(tempalateName,templatePatterns,settings,mapping);
    }

    @Override
    public boolean checkOfIndexOrTemplate(String... indexOrTemplate) throws Exception {
        boolean result = false;

        //indexOrTemplate = indexOrTemplate.replace("*","");
        result = indexTemplate.templateExist(indexOrTemplate);

        return result;
    }

    @Override
    public void initOfElasticsearch(String indexOrTemplate, String templatePattern, String type, Map<String, Object> settings, String mapping) throws Exception {
        // 判断type字段确认传入的初始化内容是否是7版本的，如果为null是7版本
        if (type==null&&templatePattern!=null){
            String tempalateName = templatePattern.replace("*","");
            createTemplateOfIndex(tempalateName,templatePattern,settings,type,mapping);
        }
    }

    @Override
    public boolean createLifeCycle(String policy_name, long delete_duration) throws Exception {
        return indexTemplate.createLifeCycle(policy_name,delete_duration);
    }

    @Override
    public boolean startIndexLifeCycle() throws Exception {
        return indexTemplate.startIndexLifeCycle();
    }

    @Override
    public String getLifecycleManagementStatus() throws Exception {
        return indexTemplate.getLifecycleManagementStatus();
    }

    @Override
    public List<IndexTemplateMetaData> getTemplateData(String... templatename) throws Exception {
        return indexTemplate.getTemplate(templatename);
    }

    @Override
    public Map<String, MappingMetaData> getIndexMappingData(String... indexname) throws Exception {
        return indexTemplate.getIndexMapping(indexname);
    }

    @Override
    public String[] getIndices(String... indexname) throws Exception {
        return indexTemplate.getIndex(indexname);
    }


}

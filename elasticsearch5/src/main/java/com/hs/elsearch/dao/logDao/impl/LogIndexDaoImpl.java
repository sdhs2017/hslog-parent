package com.hs.elsearch.dao.logDao.impl;

import com.hs.elsearch.dao.logDao.ILogIndexDao;
import com.hs.elsearch.template.IndexTemplate;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    public boolean createIndex(String index) {
        return indexTemplate.createIndex(index);
    }

    @Override
    public Boolean addMapping(String index, String type, Map<String,Object> settings, String mappingproperties) throws Exception {
        return indexTemplate.addMapping(index,type,settings,mappingproperties);
    }

    @Override
    public Boolean indexExists(String index) throws Exception {
        return indexTemplate.indexExists(index);
    }

    @Override
    public boolean deleteByIndex(String index) {
        return indexTemplate.deleteByIndex(index);
    }

    @Override
    public List<Map<String, Object>> getRepositoriesInfo(String... repositories) {
        return indexTemplate.getRepositoriesInfo(repositories);
    }

    @Override
    public Boolean createRepositories(String repositoryName, String repoPath) {
        return indexTemplate.createRepositories(repositoryName,repoPath);
    }

    @Override
    public Boolean deleteRepositories(String repositoryName) {
        return indexTemplate.deleteRepositories(repositoryName);
    }

    @Override
    public boolean updateSettings(String index, Map<String, Object> map) {
        return indexTemplate.updateSettings(index,map);
    }

    @Override
    public ForceMergeResponse indexForceMerge(String[] indices, int maxNumSegments, boolean onlyExpungeDeletes) {
        return indexTemplate.indexForceMerge(indices,maxNumSegments,onlyExpungeDeletes);
    }

    @Override
    public boolean createTemplateOfIndex(String tempalateName, String tempalatePattern, Map<String, Object> settings, String type, String mapping) throws Exception {
        return indexTemplate.createOrUpdateTemplateOfIndex(tempalateName,tempalatePattern,settings,type,mapping);
    }

    @Override
    public boolean checkOfIndexOrTemplate(String... indexOrTemplate) throws Exception {
        boolean result = false;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String index = indexOrTemplate[0].replace("*",format.format(new Date()));
        result = indexTemplate.indexExists(index);

        return result;
    }

    @Override
    public void initOfElasticsearch(String indexOrTemplate, String templatePattern, String type, Map<String, Object> settings, String mapping) throws Exception {
        // 判断type字段确认传入的初始化内容是否是7版本的，如果为null是7版本,如果不是为5版本
        if (type!=null&&templatePattern==null){
            addMapping(indexOrTemplate,type,settings,mapping);
        }
    }
}

package com.hs.elsearch.dao.logDao.impl;

import com.hs.elsearch.dao.logDao.ILogIndexDao;
import com.hs.elsearch.template.ESTransportIndexTemplate;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeResponse;
import org.springframework.beans.factory.annotation.Autowired;

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
    ESTransportIndexTemplate indexTemplate;


    @Override
    public boolean createIndex(String index) {
        return indexTemplate.createIndex(index);
    }

    @Override
    public Boolean addMapping(String index, String type, String mappingproperties) throws Exception {
        return indexTemplate.addMapping(index,type,mappingproperties);
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
}

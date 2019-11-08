package com.hs.elsearch.dao.logDao.impl;

import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.hs.elsearch.template.ESTransportCrudTemplate;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @program: hsgit
 * @description: 实现ILogSearchDao接口
 * @author: jiyourui
 * @create: 2019-11-06 17:28
 **/
public class LogCurdDaoImpl implements ILogCrudDao {

    @Autowired
    ESTransportCrudTemplate crudTemplate;


    @Override
    public IndexRequest insertNotCommit(String index, String type, String json) {
        return crudTemplate.insertNotCommit(index,type,json);
    }

    @Override
    public void bulkInsert(List<IndexRequest> requests) {
        crudTemplate.bulkInsert(requests);
    }

    @Override
    public void insert(String index, String type, String json) {
        crudTemplate.insert(index,type,json);
    }

    @Override
    public Map<String, Object> searchById(String index, String type, String id) {
        return crudTemplate.searchById(index,type,id);
    }

    @Override
    public long countDeleteByQuery(QueryBuilder queryBuilder, String... indices) {
        return crudTemplate.countDeleteByQuery(queryBuilder,indices);
    }

    @Override
    public String deleteById(String index, String type, String id) {
        return crudTemplate.deleteById(index,type,id);
    }
}

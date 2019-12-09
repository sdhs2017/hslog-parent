package com.hs.elsearch.dao.logDao.impl;

import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.hs.elsearch.template.CrudTemplate;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    CrudTemplate crudTemplate;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

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

    @Override
    public String checkOfIndex(String index, String suffix, Date date) {
        if(suffix!=null&&!suffix.equals("")){
            index = index.replace("*","_"+suffix+format.format(date));
        }else {
            index = index.replace("*",format.format(date));
        }

        return index;
    }
}

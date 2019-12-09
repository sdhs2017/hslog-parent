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
    public IndexRequest insertNotCommit(String index, String type, String json) throws Exception {
        return crudTemplate.insertNotCommit(index,json);
    }

    @Override
    public void bulkInsert(List<IndexRequest> requests) throws Exception {
        crudTemplate.bulkInsert(requests);
    }

    @Override
    public void insert(String index, String type, String json) throws Exception {
        crudTemplate.insert(index,json);
    }

    @Override
    public Map<String, Object> searchById(String index, String type, String id) throws Exception {
        return crudTemplate.searchById(index,id);
    }

    @Override
    public long countDeleteByQuery(QueryBuilder queryBuilder, String... indices) throws Exception {
        return crudTemplate.deleteByQuery(queryBuilder,indices);
    }

    @Override
    public String deleteById(String index, String type, String id) throws Exception {
        return crudTemplate.deleteById(index,id);
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
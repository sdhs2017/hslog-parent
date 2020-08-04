package com.hs.elsearch.dao.logDao;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ILogCrudDao {

    /**
     * 插入不提交
     * @param index 索引名
     * @param type index type字段，7版本移除
     * @param json 数据内容
     * @return
     */
    public IndexRequest insertNotCommit(String index, String type, String json) throws Exception;

    /**
     * 批量提交
     * @param requests list数据
     */
    public void bulkInsert(List<IndexRequest> requests) throws Exception;

    /**
     * 批量提交
     * @param  bulkRequest 原始批量提交
     */
    public void bulkInsert(BulkRequest bulkRequest) throws Exception;

    /**
     * 添加单条数据
     * @param index 索引名称
     * @param type index 的type字段，7版本移除
     * @param json 数据内容json格式
     */
    public void insert(String index, String type, String json) throws Exception;

    /**
     * 增删单条记录
     * @param index 索引名称
     * @param id 增删的判定条件，有id则更新，没有就新增
     * @param json 数据内容json格式
     */
    public DocWriteResponse.Result upsert(String index, String id, String json) throws Exception;
    /**
     * 通过ID查询数据
     * @param index
     * @param type
     * @param id
     * @return
     */
    public Map<String, Object> searchById(String index, String type, String id) throws Exception;

    /**
     * 通过查询体删除指定index的指定数据
     * @param queryBuilder 查询体
     * @param indices 索引名
     * @return 返回删除的数据量
     */
    public long countDeleteByQuery(QueryBuilder queryBuilder, String... indices) throws Exception;

    /**
     * 通过id删除单一数据(doc)
     * @param index
     * @param type
     * @param id
     * @return
     */
    public String deleteById(String index, String type, String id) throws Exception;

    /**
     * 根据不同的版本制定index的方式不同
     * @param index
     * @param suffix
     * @param date
     * @return
     */
    public String checkOfIndex(String index, String suffix, Date date);
}

package com.hs.elsearch.dao;

import java.util.List;
import java.util.Map;

public interface IElasticsearchDao {

    /**
     * 通过查询条件获取日志数
     * @param map
     * @param types
     * @param indices
     * @return
     */
    public long getCount(Map<String,String> map,String [] types,String... indices);

    /**
     * 全文检索
     * @param content
     * @param userid
     * @param page
     * @param size
     * @param types
     * @param indices
     * @return
     */
    public List<Map<String, Object>> getListByContent(String content,String userid,int page,int size,String[] types,String... indices);
}

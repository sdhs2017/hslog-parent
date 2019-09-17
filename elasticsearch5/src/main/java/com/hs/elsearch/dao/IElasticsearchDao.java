package com.hs.elsearch.dao;

import java.util.Map;

public interface IElasticsearchDao {

    public long getCount(Map<String,String> map,String [] types,String... indices);
}

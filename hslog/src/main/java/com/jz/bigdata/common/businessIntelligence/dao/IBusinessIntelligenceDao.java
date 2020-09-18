package com.jz.bigdata.common.businessIntelligence.dao;

import java.util.List;
import java.util.Map;

public interface IBusinessIntelligenceDao {
    //查询所有表
    List<Map<String,String>> showTables();
    //查询某个表的字段信息
    List<Map<String,String>> showColumns(String tableName);
    //执行语句，获取结果集
    List<Map<String,String>> getDataBySql(String sql);
}

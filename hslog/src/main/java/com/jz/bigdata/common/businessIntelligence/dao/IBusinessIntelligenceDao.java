package com.jz.bigdata.common.businessIntelligence.dao;

import com.jz.bigdata.common.businessIntelligence.entity.SqlResultMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface IBusinessIntelligenceDao {
    //查询所有表
    List<SqlResultMap> showTables();
    //查询某个表的字段信息
    List<SqlResultMap> showColumns(String tableName);
    //执行语句，获取结果集
    List<LinkedHashMap<String, Object>> getDataBySql(String sql);
}

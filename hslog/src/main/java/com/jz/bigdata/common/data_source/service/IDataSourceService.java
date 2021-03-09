package com.jz.bigdata.common.data_source.service;


import com.jz.bigdata.common.data_source.entity.DataSource;
import com.jz.bigdata.common.event_alert.entity.EventAlert;

import java.util.List;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/1/27 10:29
 * @Description:
 */
public interface IDataSourceService {

    public boolean save(DataSource dataSource);

    public boolean delete(String[] ids);

    public boolean update(DataSource dataSource);

    public Map<String,Object> getListByCondition(DataSource dataSource);

    DataSource selectDataSourceInfoById(String data_source_id);

    /**
     * 测试连接池连接
     * @param dataSource
     * @return
     */
    boolean checkConnection(DataSource dataSource);

    /**
     * 获取数据库信息
     * @param data_source_id
     * @return
     * @throws Exception
     */
    public List<Map<String,String>> getDataBase(String data_source_id) throws Exception;

    /**
     * 通过数据库获取表信息
     * @param database
     * @return
     * @throws Exception
     */
    public List<Map<String,String>> getTablesByDatabase_tree(String database,String data_source_id) throws Exception;

    /**
     * 获取表的字段信息
     * @param database
     * @param table
     * @param data_source_id
     * @return
     * @throws Exception
     */
    public List<Map<String,Object>> getTableFieldsInfo(String database,String table,String data_source_id) throws Exception;

    /**
     * 根据data source id 进行数据初始化，将数据库、表、字段信息保存
     * @param data_source_ids
     * @return
     * @throws Exception
     */
    public boolean initByDataSourceIds(String data_source_ids) throws Exception;
}

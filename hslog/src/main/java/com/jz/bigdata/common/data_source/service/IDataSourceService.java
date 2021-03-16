package com.jz.bigdata.common.data_source.service;


import com.jz.bigdata.common.data_source.entity.DataSource;
import com.jz.bigdata.common.event_alert.entity.EventAlert;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/1/27 10:29
 * @Description:
 */
public interface IDataSourceService {
    /**
     * 数据源信息保存
     * @param dataSource 基本信息bean
     * @return
     */
    public boolean save(DataSource dataSource);

    /**
     * 数据源删除，支持批量删除
     * @param ids 数据源id，以逗号隔开
     * @return
     */
    public boolean delete(String[] ids);

    /**
     * 数据源更新，更新时需要将已存在的druid datasource对象关闭
     * @param dataSource 数据源信息
     * @return
     */
    public boolean update(DataSource dataSource);

    /**
     *  获取数据源列表详情
     * @param dataSource 查询条件
     * @return
     */
    public Map<String,Object> getListByCondition(DataSource dataSource);

    /**
     * 通过数据源唯一ID获取数据源详情
     * @param data_source_id
     * @return
     */
    DataSource selectDataSourceInfoById(String data_source_id);

    /**
     * 测试连接池连接
     * @param dataSource 数据源基本信息
     * @return
     */
    public String checkConnection(DataSource dataSource);

    /**
     * 获取数据源下的数据库信息
     * @param data_source_id 数据源id
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
     * @param database 数据库
     * @param table 表
     * @param data_source_id 数据源id
     * @return
     * @throws Exception
     */
    public List<Map<String,Object>> getTableFieldsInfo(String database,String table,String data_source_id) throws Exception;

    /**
     * 根据data source id 进行数据初始化，将数据库、表、字段信息保存
     * @param data_source_ids 数据源id
     * @return
     * @throws Exception
     */
    public boolean initByDataSourceIds(String data_source_ids) throws Exception;
    /**
     * 获取表的字段信息
     * @param database 数据库名
     * @param table 表名
     * @param data_source_item_type 数据库类型
     * @param connection 连接池,需要在外面对connection关闭
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getTableFields(String database, String table,String data_source_item_type, Connection connection) throws Exception;
}

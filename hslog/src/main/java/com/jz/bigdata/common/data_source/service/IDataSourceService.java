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
    DataSource selectDataSourceInfoById(String data_source_id) throws Exception;

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
     * 根据data source id 进行数据初始化，将数据库、表、字段信息保存,并根据抽样行数对抽样信息进行持久化
     * @param data_source_ids 数据源id
     * @return
     * @throws Exception
     */
    public boolean initByDataSourceIds(String data_source_ids) throws Exception;
    /**
     * 敏感数据发现，根据初始化后持久化的 库、表、字段以及样本数据，进行敏感数据发现，对字段打上标签
     * @param data_source_ids 数据源id
     * @return
     * @throws Exception
     */
    public boolean dataDiscovery(String data_source_ids) throws Exception;

    /**
     * 根据data source id 进行数据初始化，将数据库、表、字段信息保存,并根据条件进行自动标识
     * @param data_source_ids 数据源id
     * @param is_auto_identify 是否自动标识
     * @param sample_num 抽样行数
     * @return 返回到前端的json信息
     * @throws Exception
     */
    public String initByDataSourceIds(String data_source_ids,String is_auto_identify,String sample_num) throws Exception;
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

    /**
     * 根据数据库获取下面的表信息
     * TABLE_NAME 表名
     * @param database 数据库名称
     * @param connection 连接
     * @param data_source_item_type 数据库类型
     * @return 表的基本信息，list为条数，map的key为TABLE_NAME ，value为表名
     * @throws Exception
     */
    public List<Map<String, Object>> getTablesByDatabase(String database,Connection connection,String data_source_item_type) throws Exception;


}

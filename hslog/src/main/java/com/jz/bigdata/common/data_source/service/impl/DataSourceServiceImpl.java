package com.jz.bigdata.common.data_source.service.impl;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.data_source.dao.IDataSourceDao;
import com.jz.bigdata.common.data_source.entity.DataSource;
import com.jz.bigdata.common.data_source.service.IDataSourceService;
import com.jz.bigdata.common.data_source_metadata.dao.IDataSourceMetadataDao;
import com.jz.bigdata.common.data_source_metadata.entity.DataSourceMetadata;
import com.jz.bigdata.common.event_alert.dao.IEventAlertDao;
import com.jz.bigdata.common.event_alert.entity.EventAlert;
import com.jz.bigdata.common.event_alert.service.IEventAlertService;
import com.jz.bigdata.common.siem.entity.Interval;
import com.jz.bigdata.common.siem.service.ISIEMService;
import com.jz.bigdata.util.DruidUtil;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Author: yiyang
 * @Date: 2021/2/23 10:29
 * @Description:
 */
@Slf4j
@Service(value = "DataSourceService")
public class DataSourceServiceImpl implements IDataSourceService {
    private static final DateTimeFormatter dtf_time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Autowired
    protected IDataSourceDao dataSourceDao;
    @Autowired
    protected IDataSourceMetadataDao dataSourceMetadataDao;


    @Override
    public boolean save(DataSource dataSource) {
        dataSource.setData_source_id(UUID.randomUUID().toString());
        dataSource.setData_source_create_time(LocalDateTime.now().format(dtf_time));
        //未初始化
        dataSource.setData_source_is_initialized(0);
        int result = dataSourceDao.insert(dataSource);
        if(result>0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean delete(String[] ids) {
        int result = dataSourceDao.deletes(ids);
        if(result>0){
            return true;
        }else{
            return false;
        }

    }

    @Override
    public boolean update(DataSource dataSource) {
        dataSource.setData_source_update_time(LocalDateTime.now().format(dtf_time));
        int result = dataSourceDao.update(dataSource);
        if(result>0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Map<String,Object> getListByCondition(DataSource dataSource) {
        //获取count
        List<List<Map<String, String>>> count = dataSourceDao.getCountByCondition(dataSource);
        //分页后的结果列表
        List<DataSource> result = dataSourceDao.getListByConditionWithPage(dataSource);
        //组装前端需要的数据格式
        Map<String, Object> allMap = new HashMap<>();
        allMap.put("count",count.get(0).get(0).get("count"));
        allMap.put("list",result);
        return allMap;
    }

    @Override
    public DataSource selectDataSourceInfoById(String data_source_id) {
        return dataSourceDao.selectDataSourceInfoById(data_source_id);
    }

    @Override
    public boolean checkConnection(DataSource dataSource) {

        try{
            return DruidUtil.checkConnection(dataSource);
        } catch (Exception e) {
            log.error("测试连接池连接失败"+e.getMessage());
            return false;
        }
    }

    @Override
    public List<Map<String, String>> getDataBaseOrTable(String data_source_id) throws Exception {
        List<Map<String, String>> result = new ArrayList<>();
        //获取链接信息
        DataSource dataSource = dataSourceDao.selectDataSourceInfoById(data_source_id);
        if(dataSource!=null){
            //1.创建链接
            Connection connection = DruidUtil.getConnection(dataSource);
            //2.判断要执行的sql语句
            //TODO 数据库类型判定
            if(Strings.isNullOrEmpty(dataSource.getData_source_dbname())){
                String sql_database = "show databases";
                List<Map<String, Object>> result_table = DruidUtil.executeQuery(connection,sql_database,null);
                result = data2treeNode(result_table,"SCHEMA_NAME","","true");
            }else{
                //数据库或实例不为空,直接获取表信息
                String sql_table = "select TABLE_NAME from information_schema.`TABLES` where TABLE_SCHEMA = ? order by TABLE_NAME";
                Object[] param = {dataSource.getData_source_dbname()};
                List<Map<String, Object>> result_table = DruidUtil.executeQuery(connection,sql_table,param);
                result = data2treeNode(result_table,"TABLE_NAME",dataSource.getData_source_dbname(),"false");
            }
            connection.close();
        }
        return result;
    }

    @Override
    public List<Map<String, String>> getTablesByDatabase(String database,String data_source_id) throws Exception {
        List<Map<String, String>> result = new ArrayList<>();
        //获取链接信息
        DataSource dataSource = dataSourceDao.selectDataSourceInfoById(data_source_id);
        if(dataSource!=null){
            //1.创建链接
            Connection connection = DruidUtil.getConnection(dataSource);
            //2.判断要执行的sql语句
            //数据库或实例不为空,直接获取表信息
            String sql_table = "select TABLE_NAME from information_schema.`TABLES` where TABLE_SCHEMA = ?";
            Object[] param = {database};
            List<Map<String, Object>> result_table = DruidUtil.executeQuery(connection,sql_table,param);
            result = data2treeNode(result_table,"TABLE_NAME",database,"false");
            connection.close();
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getTableFields(String database, String table, String data_source_id) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        //获取链接信息
        DataSource dataSource = dataSourceDao.selectDataSourceInfoById(data_source_id);
        if(dataSource!=null){
            //1.创建链接
            Connection connection = DruidUtil.getConnection(dataSource);
            String sql_fields = "select * from INFORMATION_SCHEMA.COLUMNS where table_schema = ? and table_name = ?";
            Object[] param = {database,table};
            result = DruidUtil.executeQuery(connection,sql_fields,param);
            connection.close();
        }
        return result;
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
    public boolean initByDataSourceIds(String data_source_ids) throws Exception {

        String[] ids = data_source_ids.split(",");
        for(String data_source_id : ids){
            List<DataSourceMetadata> batchList = new ArrayList<>();
            //获取链接信息
            DataSource dataSource = dataSourceDao.selectDataSourceInfoById(data_source_id);
            //获取链接信息
            Connection connection = DruidUtil.getConnection(dataSource);
            if(dataSource!=null){
                //开始初始化
                //0.删除原初始化数据
                dataSourceMetadataDao.deleteByDataSourceId(data_source_id);
                //1.数据库
                if(Strings.isNullOrEmpty(dataSource.getData_source_dbname())){
                    List<Map<String, Object>> list = getDatabases(connection,dataSource.getData_source_item_type());
                    //遍历库信息，并写入数据库
                    for(Map<String,Object> database_map:list){
                        DataSourceMetadata metadata = new DataSourceMetadata();
                        metadata.setMetadata_id(UUID.randomUUID().toString());
                        metadata.setData_source_id(dataSource.getData_source_id());
                        metadata.setMetadata_type("database");//元数据类型-数据库
                        metadata.setMetadata_database(database_map.get("SCHEMA_NAME").toString());
                        batchList.add(metadata);
                        batchList.addAll(getTableAndFieldsMetadata(connection,database_map.get("SCHEMA_NAME").toString(),dataSource.getData_source_item_type(),dataSource.getData_source_id()));
                    }
                }else{
                    batchList = getTableAndFieldsMetadata(connection,dataSource.getData_source_dbname(),dataSource.getData_source_item_type(),dataSource.getData_source_id());
                    //数据源信息中有数据库，需要将数据库也写入
                    DataSourceMetadata metadata = new DataSourceMetadata();
                    metadata.setMetadata_id(UUID.randomUUID().toString());
                    metadata.setData_source_id(dataSource.getData_source_id());
                    metadata.setMetadata_type("database");//元数据类型-数据库
                    metadata.setMetadata_database(dataSource.getData_source_dbname());
                    batchList.add(metadata);
                }
                dataSourceMetadataDao.batchInsert(batchList);
                //更新初始化信息到数据源中
                dataSource.setData_source_is_initialized(1);//已经初始化
                dataSource.setData_source_init_time(LocalDateTime.now().format(dtf_time));
                dataSourceDao.update(dataSource);
            }else{
                //获取不到连接池信息
                return false;
            }
            connection.close();
        }
        return true;
    }

    /**
     * 根据数据库信息获取下属表和字段的信息
     * @param connection
     * @param database 数据库
     * @param data_source_item_type 数据库类型
     * @param data_source_id 数据源id
     * @return
     * @throws SQLException
     */
    private List<DataSourceMetadata> getTableAndFieldsMetadata(Connection connection,String database,String data_source_item_type,String data_source_id) throws SQLException {
        List<DataSourceMetadata> result = new ArrayList<>();
        //表
        List<Map<String, Object>> table_list = getTables(connection,data_source_item_type,database);
        for(Map<String,Object> table_map:table_list){
            DataSourceMetadata metadata_table = new DataSourceMetadata();
            metadata_table.setMetadata_id(UUID.randomUUID().toString());
            metadata_table.setData_source_id(data_source_id);
            metadata_table.setMetadata_type("table");//元数据类型-表
            metadata_table.setMetadata_database(database);
            metadata_table.setMetadata_table(table_map.get("TABLE_NAME").toString());
            result.add(metadata_table);
            //字段
            List<Map<String, Object>> fields_list = getFields(connection,data_source_item_type,database,table_map.get("TABLE_NAME").toString());
            for(Map<String,Object> field_map:fields_list){
                DataSourceMetadata metadata_field = new DataSourceMetadata();
                metadata_field.setMetadata_id(UUID.randomUUID().toString());
                metadata_field.setData_source_id(data_source_id);
                metadata_field.setMetadata_type("field");//元数据类型-表
                metadata_field.setMetadata_database(database);
                metadata_field.setMetadata_table(table_map.get("TABLE_NAME").toString());
                metadata_field.setMetadata_field(field_map.get("COLUMN_NAME").toString());
                metadata_field.setMetadata_field_type(field_map.get("COLUMN_TYPE").toString());
                metadata_field.setMetadata_remark(field_map.get("COLUMN_COMMENT").toString());
                result.add(metadata_field);
            }

        }
        return result;
    }

    /**
     * 将查询出的数据转化成tree结构的节点list
     * @param result_table
     * @param labelName
     * @param database
     * @return
     */
    private List<Map<String, String>> data2treeNode(List<Map<String, Object>> result_table,String labelName,String database,String isExpand){
        List<Map<String, String>> result = new ArrayList<>();
        //遍历，组装数据
        for(Map<String, Object> map:result_table){
            Map<String,String> node = new HashMap<>();
            node.put("label",map.get(labelName).toString());
            node.put("id",map.get(labelName).toString());
            node.put("child","[]");
            node.put("isExpand",isExpand);
            node.put("database",database);
            result.add(node);
        }
        return result;
    }

    /**
     * 根据数据库类型获取数据库信息
     * @param connection
     * @param data_source_item_type
     * @return
     * @throws SQLException
     */
    private List<Map<String, Object>> getDatabases(Connection connection,String data_source_item_type) throws SQLException {
        switch (data_source_item_type){
            case Constant.DATA_SOURCE_ITEM_TYPE_MYSQL:
                String sql_mysql = "show databases";
                return DruidUtil.executeQuery(connection,sql_mysql,null);
            case Constant.DATA_SOURCE_ITEM_TYPE_SQLSERVER:
                //TODO
            case Constant.DATA_SOURCE_ITEM_TYPE_ORACLE:
                //TODO
            default:
                return new ArrayList<>();
        }
    }

    /**
     * 获取数据库下的表信息
     * @param connection
     * @param data_source_item_type
     * @param database
     * @return
     * @throws SQLException
     */
    private List<Map<String, Object>> getTables(Connection connection,String data_source_item_type,String database) throws SQLException {
        switch (data_source_item_type){
            case Constant.DATA_SOURCE_ITEM_TYPE_MYSQL:
                String sql_mysql = "select * from information_schema.`TABLES` where TABLE_SCHEMA = ?";
                Object[] param = {database};
                return DruidUtil.executeQuery(connection,sql_mysql,param);
            case Constant.DATA_SOURCE_ITEM_TYPE_SQLSERVER:
                //TODO
            case Constant.DATA_SOURCE_ITEM_TYPE_ORACLE:
                //TODO
            default:
                return new ArrayList<>();
        }
    }

    /**
     * 获取表下的字段信息
     * @param connection
     * @param data_source_item_type
     * @param database
     * @param table
     * @return
     * @throws SQLException
     */
    private List<Map<String, Object>> getFields(Connection connection,String data_source_item_type,String database,String table) throws SQLException {
        switch (data_source_item_type){
            case Constant.DATA_SOURCE_ITEM_TYPE_MYSQL:
                String sql_fields = "select * from INFORMATION_SCHEMA.COLUMNS where table_schema = ? and table_name = ? order by COLUMN_NAME";
                Object[] param = {database,table};
                return DruidUtil.executeQuery(connection,sql_fields,param);
            case Constant.DATA_SOURCE_ITEM_TYPE_SQLSERVER:
                //TODO
            case Constant.DATA_SOURCE_ITEM_TYPE_ORACLE:
                //TODO
            default:
                return new ArrayList<>();
        }
    }
}

package com.jz.bigdata.common.data_source_metadata.service.impl;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.data_source.dao.IDataSourceDao;
import com.jz.bigdata.common.data_source.entity.DataSource;
import com.jz.bigdata.common.data_source.service.IDataSourceService;
import com.jz.bigdata.common.data_source_metadata.dao.IDataSourceMetadataDao;
import com.jz.bigdata.common.data_source_metadata.entity.*;
import com.jz.bigdata.common.data_source_metadata.service.IDataSourceMetadataService;
import com.jz.bigdata.roleauthority.menu.entity.Menu;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.DruidUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.*;

/**
 * @Author: yiyang
 * @Date: 2021/3/1 13:57
 * @Description:
 */
@Slf4j
@Service(value = "DataSourceMetadataService")
public class DataSourceMetadataServiceImpl implements IDataSourceMetadataService {
    @Autowired
    protected IDataSourceDao dataSourceDao;
    @Autowired
    protected IDataSourceMetadataDao dataSourceMetadataDao;
    @Resource(name ="configProperty")
    private ConfigProperty configProperty;
    @Override
    public List<DataSourceMetadataMenu> getMetadataTree() {
        List<DataSourceMetadataMenu> listMenu = new ArrayList<>();
        //1.获取数据源列表
        List<DataSource> dataSourceList = dataSourceDao.getListByCondition(new DataSource());
        for(DataSource dataSource:dataSourceList){
            DataSourceMetadataMenu menu = new DataSourceMetadataMenu();
            menu.setId(dataSource.getData_source_id());
            menu.setMenuName(dataSource.getData_source_name());
            menu.setState("1");//1级菜单标识
            menu.setData_source_id(dataSource.getData_source_id());
            //获取数据源下的数据库列表
            List<MetadataDatabase> databaseList = dataSourceMetadataDao.getMetadataDatabaseList(dataSource.getData_source_id());
            List<DataSourceMetadataMenu> databaseMenuList = new ArrayList<>();
            for(MetadataDatabase databaseMetadata:databaseList){
                DataSourceMetadataMenu databaseMenu = new DataSourceMetadataMenu();
                databaseMenu.setId(databaseMetadata.getMetadata_database_id());
                databaseMenu.setMenuName(databaseMetadata.getMetadata_database_name());
                databaseMenu.setState("2");//2级菜单标识
                databaseMenu.setData_source_id(dataSource.getData_source_id());
                databaseMenu.setDatabase(databaseMetadata.getMetadata_database_name());
                //获取数据源下的table 列表
                List<MetadataTable> tableList = dataSourceMetadataDao.getMetadataTableList(dataSource.getData_source_id(),databaseMetadata.getMetadata_database_name());
                List<DataSourceMetadataMenu> tableMenuList = new ArrayList<>();
                for(MetadataTable tableMetadata:tableList){
                    DataSourceMetadataMenu tableMenu = new DataSourceMetadataMenu();
                    tableMenu.setId(tableMetadata.getMetadata_table_id());
                    tableMenu.setMenuName(tableMetadata.getMetadata_table_name());
                    tableMenu.setState("3");//3级菜单标识
                    tableMenu.setData_source_id(dataSource.getData_source_id());
                    tableMenu.setDatabase(tableMetadata.getMetadata_database_name());
                    tableMenu.setTable(tableMetadata.getMetadata_table_name());
                    tableMenuList.add(tableMenu);

                }
                databaseMenu.setMenus(tableMenuList);
                databaseMenuList.add(databaseMenu);
            }
            menu.setMenus(databaseMenuList);
            listMenu.add(menu);
        }
        return listMenu;
    }

    @Override
    public Map<String, Object> getTableInfo(DataSourceMetadata dataSourceMetadata) {
        //获取count
        List<List<Map<String, String>>> count = dataSourceMetadataDao.getMetadataTableCount(dataSourceMetadata);
        //分页后的结果列表
        List<MetadataTable> result = dataSourceMetadataDao.getMetadataTableListWithPage(dataSourceMetadata);
        //组装前端需要的数据格式
        Map<String, Object> allMap = new HashMap<>();
        allMap.put("count",count.get(0).get(0).get("count"));
        allMap.put("list",result);
        return allMap;
    }

    @Override
    public Map<String, Object> getFieldInfo(DataSourceMetadata dataSourceMetadata) {
        //获取count
        List<List<Map<String, String>>> count = dataSourceMetadataDao.getMetadataFieldCount(dataSourceMetadata);
        //分页后的结果列表
        List<MetadataField> result = dataSourceMetadataDao.getMetadataFieldListWithPage(dataSourceMetadata);
        //组装前端需要的数据格式
        Map<String, Object> allMap = new HashMap<>();
        allMap.put("count",count.get(0).get(0).get("count"));
        allMap.put("list",result);
        return allMap;
    }

    @Override
    public int update(DataSourceMetadata dataSourceMetadata) {
        return dataSourceMetadataDao.update(dataSourceMetadata);
    }

    @Override
    public int updateMetadataTableInfo(MetadataTable metadataTable) {
        return dataSourceMetadataDao.updateMetadataTableInfo(metadataTable);
    }

    @Override
    public int updateMetadataFieldInfo(MetadataField metadataField) {
        return dataSourceMetadataDao.updateMetadataFieldInfo(metadataField);
    }

    @Override
    public Map<String,Object> getDataPreview(String databaseName, String tableName, String data_source_id) throws SQLException {
        //获取数据源信息
        DataSource dataSource = dataSourceDao.selectDataSourceInfoById(data_source_id);

        //获取字段信息
        List<DataSourceMetadata> metadataList = dataSourceMetadataDao.getFieldList(data_source_id,databaseName,tableName);
        List<Map<String,String>> fieldList = new ArrayList<>();//表头信息
        //组装表头信息
        for(DataSourceMetadata metadata:metadataList){
            Map<String,String> column = new HashMap<>();
            column.put("prop",metadata.getMetadata_field());
            column.put("label",metadata.getMetadata_field());
            column.put("width","");
            fieldList.add(column);
        }
        //表内的数据，取前N条，条数根据配置信息
        List<Map<String, String>> table_data = new ArrayList<>();
        String sql_check;
        List<Map<String,Object>> check_result;
        //要执行的查询sql
        String sql;
        //判断数据源类型
        switch (dataSource.getData_source_item_type()){
            case Constant.DATA_SOURCE_ITEM_TYPE_MYSQL:
                //先确认表是否存在
                sql_check = "select count(1)  from information_schema.`TABLES` where TABLE_SCHEMA = ? AND TABLE_NAME = ?";
                check_result = DruidUtil.executeQuery(dataSource,sql_check,new Object[]{databaseName,tableName});
                if(check_result.size()==1){
                    sql = "select * from "+databaseName+"."+tableName+" limit 0,"+configProperty.getData_source_preview_num();
                    table_data = DruidUtil.executeQuery_stringValue(dataSource,sql, null);
                }
                break;
            case Constant.DATA_SOURCE_ITEM_TYPE_SQLSERVER:
                //sqlserver 必须设置数据库/实例，所以只需要表名
                //SELECT * FROM SYSOBJECTS WHERE XTYPE='U' WHERE NAME=''
                sql_check = "SELECT COUNT(1) FROM SYSOBJECTS WHERE XTYPE='U' AND NAME=?";
                check_result = DruidUtil.executeQuery(dataSource,sql_check,new Object[]{tableName});
                if(check_result.size()==1){
                    sql = "select top "+configProperty.getData_source_preview_num()+" * from "+tableName;
                    table_data = DruidUtil.executeQuery_stringValue(dataSource,sql, null);
                }
                break;
            case Constant.DATA_SOURCE_ITEM_TYPE_ORACLE:
                //select * from all_tables WHERE OWNER='AUDSYS' AND TABLE_NAME='AUD$UNIFIED'
                sql_check = "select COUNT(1) from all_tables WHERE OWNER=? AND TABLE_NAME=?";
                check_result = DruidUtil.executeQuery(dataSource,sql_check,new Object[]{databaseName,tableName});
                if(check_result.size()==1){
                    sql = "select * from \""+databaseName+"\".\""+tableName+"\" where rownum<="+configProperty.getData_source_preview_num();
                    table_data = DruidUtil.executeQuery_stringValue(dataSource,sql, null);
                }
                break;
            default:
                table_data = new ArrayList<>();
                break;
        }
        //组装前端所需数据
        Map<String,Object> result = new HashMap<>();
        result.put("fields",fieldList);
        result.put("data",table_data);
        return result;
    }


    /** 已停用
     * 获取表中的数据
     * @param dataSource
     * @param databaseName
     * @param tableName
     * @return
     * @throws SQLException 将异常抛出，保证执行异常时能数据回滚
     */
//    private List<Map<String, Object>> getTableData(DataSource dataSource,String databaseName, String tableName) throws SQLException {
//        //表内的数据，取前N条，条数根据配置信息
//        List<Map<String, Object>> table_data;
//        //要执行的查询sql
//        String sql;
//        //判断数据源类型
//        switch (dataSource.getData_source_item_type()){
//            case Constant.DATA_SOURCE_ITEM_TYPE_MYSQL:
//                sql = "select * from "+databaseName+"."+tableName+" limit 0,"+configProperty.getData_source_preview_num();
//                table_data = DruidUtil.executeQuery(dataSource,sql,null);
//                break;
//            case Constant.DATA_SOURCE_ITEM_TYPE_SQLSERVER:
//                //sqlserver 必须设置数据库/实例，所以只需要表名
//                sql = "select top "+configProperty.getData_source_preview_num()+" * from "+tableName;
//                table_data = DruidUtil.executeQuery(dataSource,sql,null);
//                break;
//            case Constant.DATA_SOURCE_ITEM_TYPE_ORACLE:
//                sql = "select * from \""+databaseName+"\".\""+tableName+"\" where rownum<="+configProperty.getData_source_preview_num();
//                table_data = DruidUtil.executeQuery(dataSource,sql,null);
//                break;
//            default:
//                table_data = new ArrayList<>();
//                break;
//        }
//        return table_data;
//    }

}

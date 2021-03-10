package com.jz.bigdata.common.data_source_metadata.service.impl;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.data_source.dao.IDataSourceDao;
import com.jz.bigdata.common.data_source.entity.DataSource;
import com.jz.bigdata.common.data_source.service.IDataSourceService;
import com.jz.bigdata.common.data_source_metadata.dao.IDataSourceMetadataDao;
import com.jz.bigdata.common.data_source_metadata.entity.DataSourceMetadata;
import com.jz.bigdata.common.data_source_metadata.entity.DataSourceMetadataMenu;
import com.jz.bigdata.common.data_source_metadata.service.IDataSourceMetadataService;
import com.jz.bigdata.roleauthority.menu.entity.Menu;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.DruidUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            List<DataSourceMetadata> databaseList = dataSourceMetadataDao.getDatabaseList(dataSource.getData_source_id());
            List<DataSourceMetadataMenu> databaseMenuList = new ArrayList<>();
            for(DataSourceMetadata databaseMetadata:databaseList){
                DataSourceMetadataMenu databaseMenu = new DataSourceMetadataMenu();
                databaseMenu.setId(databaseMetadata.getMetadata_id());
                databaseMenu.setMenuName(databaseMetadata.getMetadata_database());
                databaseMenu.setState("2");//1级菜单标识
                databaseMenu.setData_source_id(dataSource.getData_source_id());
                databaseMenu.setDatabase(databaseMetadata.getMetadata_database());
                //获取数据源下的table 列表
                List<DataSourceMetadata> tableList = dataSourceMetadataDao.getTableList(dataSource.getData_source_id(),databaseMetadata.getMetadata_database());
                List<DataSourceMetadataMenu> tableMenuList = new ArrayList<>();
                for(DataSourceMetadata tableMetadata:tableList){
                    DataSourceMetadataMenu tableMenu = new DataSourceMetadataMenu();
                    tableMenu.setId(databaseMetadata.getMetadata_id());
                    tableMenu.setMenuName(tableMetadata.getMetadata_table());
                    tableMenu.setState("3");//1级菜单标识
                    tableMenu.setData_source_id(dataSource.getData_source_id());
                    tableMenu.setDatabase(databaseMetadata.getMetadata_database());
                    tableMenu.setTable(tableMetadata.getMetadata_table());
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
        List<List<Map<String, String>>> count = dataSourceMetadataDao.getTableListCount(dataSourceMetadata);
        //分页后的结果列表
        List<DataSourceMetadata> result = dataSourceMetadataDao.getTableListWithPage(dataSourceMetadata);
        //组装前端需要的数据格式
        Map<String, Object> allMap = new HashMap<>();
        allMap.put("count",count.get(0).get(0).get("count"));
        allMap.put("list",result);
        return allMap;
    }

    @Override
    public Map<String, Object> getFieldInfo(DataSourceMetadata dataSourceMetadata) {
        //获取count
        List<List<Map<String, String>>> count = dataSourceMetadataDao.getFieldListCount(dataSourceMetadata);
        //分页后的结果列表
        List<DataSourceMetadata> result = dataSourceMetadataDao.getFieldListWithPage(dataSourceMetadata);
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
    public Map<String,Object> getDataPreview(String databaseName, String tableName, String data_source_id) {
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
        List<Map<String, Object>> table_data;
        //要执行的查询sql
        String sql;
        //判断数据源类型
        switch (dataSource.getData_source_item_type()){
            case Constant.DATA_SOURCE_ITEM_TYPE_MYSQL:
                sql = "select * from "+databaseName+"."+tableName+" limit 0,"+configProperty.getData_source_preview_num();
                table_data = DruidUtil.executeQuery(dataSource,sql,null);
                break;
            case Constant.DATA_SOURCE_ITEM_TYPE_SQLSERVER:
                //sqlserver 必须设置数据库/实例，所以只需要表名
                sql = "select top "+configProperty.getData_source_preview_num()+" * from "+tableName;
                table_data = DruidUtil.executeQuery(dataSource,sql,null);
                break;
            case Constant.DATA_SOURCE_ITEM_TYPE_ORACLE:
                sql = "select * from \""+databaseName+"\".\""+tableName+"\" where rownum<="+configProperty.getData_source_preview_num();
                table_data = DruidUtil.executeQuery(dataSource,sql,null);
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

    @Override
    public Map<String, Object> getDataPreviewNotInited(String databaseName, String tableName, String data_source_id) {
        //获取数据源信息
        DataSource dataSource = dataSourceDao.selectDataSourceInfoById(data_source_id);

        return null;
    }

    /**
     * 获取表中的数据
     * @param dataSource
     * @param databaseName
     * @param tableName
     * @return
     */
    private List<Map<String, Object>> getTableData(DataSource dataSource,String databaseName, String tableName){
        //表内的数据，取前N条，条数根据配置信息
        List<Map<String, Object>> table_data;
        //要执行的查询sql
        String sql;
        //判断数据源类型
        switch (dataSource.getData_source_item_type()){
            case Constant.DATA_SOURCE_ITEM_TYPE_MYSQL:
                sql = "select * from "+databaseName+"."+tableName+" limit 0,"+configProperty.getData_source_preview_num();
                table_data = DruidUtil.executeQuery(dataSource,sql,null);
                break;
            case Constant.DATA_SOURCE_ITEM_TYPE_SQLSERVER:
                //sqlserver 必须设置数据库/实例，所以只需要表名
                sql = "select top "+configProperty.getData_source_preview_num()+" * from "+tableName;
                table_data = DruidUtil.executeQuery(dataSource,sql,null);
                break;
            case Constant.DATA_SOURCE_ITEM_TYPE_ORACLE:
                sql = "select * from \""+databaseName+"\".\""+tableName+"\" where rownum<="+configProperty.getData_source_preview_num();
                table_data = DruidUtil.executeQuery(dataSource,sql,null);
                break;
            default:
                table_data = new ArrayList<>();
                break;
        }
        return table_data;
    }

}

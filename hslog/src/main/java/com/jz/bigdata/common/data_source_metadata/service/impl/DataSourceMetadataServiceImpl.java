package com.jz.bigdata.common.data_source_metadata.service.impl;

import com.jz.bigdata.common.data_source.dao.IDataSourceDao;
import com.jz.bigdata.common.data_source.entity.DataSource;
import com.jz.bigdata.common.data_source.service.IDataSourceService;
import com.jz.bigdata.common.data_source_metadata.dao.IDataSourceMetadataDao;
import com.jz.bigdata.common.data_source_metadata.entity.DataSourceMetadata;
import com.jz.bigdata.common.data_source_metadata.entity.DataSourceMetadataMenu;
import com.jz.bigdata.common.data_source_metadata.service.IDataSourceMetadataService;
import com.jz.bigdata.roleauthority.menu.entity.Menu;
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


}

package com.jz.bigdata.common.data_source_metadata.service;

import com.jz.bigdata.common.data_source_metadata.entity.DataSourceMetadata;
import com.jz.bigdata.common.data_source_metadata.entity.DataSourceMetadataMenu;
import com.jz.bigdata.roleauthority.menu.entity.Menu;

import java.util.List;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/3/1 13:57
 * @Description:
 */
public interface IDataSourceMetadataService {
    /**
     * 获取元数据tree
     * @return
     */
    public List<DataSourceMetadataMenu> getMetadataTree();

    /**
     * 获取表格信息
     * @param dataSourceMetadata
     * @return
     */
    public Map<String, Object> getTableInfo(DataSourceMetadata dataSourceMetadata);
    /**
     * 获取字段信息
     * @param dataSourceMetadata
     * @return
     */
    public Map<String, Object> getFieldInfo(DataSourceMetadata dataSourceMetadata);
    /**
     * 更新数据
     * @param dataSourceMetadata
     * @return
     */
    public int update(DataSourceMetadata dataSourceMetadata);

    /**
     * 数据预览功能
     * @param databaseName
     * @param tableName
     * @param data_source_id
     * @return
     */
    public Map<String,Object> getDataPreview(String databaseName,String tableName,String data_source_id);
    /**
     * 数据预览功能，数据未初始化，在数据源管理模块
     * @param databaseName
     * @param tableName
     * @param data_source_id
     * @return
     */
    public Map<String,Object> getDataPreviewNotInited(String databaseName,String tableName,String data_source_id);

}

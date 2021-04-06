package com.jz.bigdata.common.data_source_metadata.service;

import com.jz.bigdata.common.data_source_metadata.entity.DataSourceMetadata;
import com.jz.bigdata.common.data_source_metadata.entity.DataSourceMetadataMenu;
import com.jz.bigdata.common.data_source_metadata.entity.MetadataField;
import com.jz.bigdata.common.data_source_metadata.entity.MetadataTable;
import com.jz.bigdata.roleauthority.menu.entity.Menu;

import java.sql.SQLException;
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
     * 获取表信息
     * @param dataSourceMetadata 查询条件对象
     * @return 表的基本信息，由于返回需要count和数据list，因此通过map存放，key：count value：总条数。key：list value：表的信息（list<MetadataTable>）
     */
    public Map<String, Object> getTableInfo(DataSourceMetadata dataSourceMetadata);
    /**
     * 获取字段信息
     * @param dataSourceMetadata 查询条件对象
     * @return 字段信息，key为字段属性（字段名、字段类型、长度等），value为对应的值
     */
    public Map<String, Object> getFieldInfo(DataSourceMetadata dataSourceMetadata);
    /**
     * 更新数据
     * @param dataSourceMetadata 更新对象的信息
     * @return 更新影响的条数
     */
    public int update(DataSourceMetadata dataSourceMetadata);

    /**
     * 更新元数据表信息
     * @param metadataTable
     * @return
     */
    public int updateMetadataTableInfo(MetadataTable metadataTable);

    /**
     * 更新元数据字段信息
     * @param metadataField
     * @return
     */
    public int updateMetadataFieldInfo(MetadataField metadataField);
    /**
     * 数据预览功能
     * @param databaseName
     * @param tableName
     * @param data_source_id
     * @return
     */
    public Map<String,Object> getDataPreview(String databaseName,String tableName,String data_source_id) throws SQLException;

    /**
     * 通过元数据字段id获取详细信息
     * @param metadata_field_id 字段id
     * @return
     */
    public Map<String,String> getMetadataFieldInfo(String metadata_field_id);
}

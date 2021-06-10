package com.jz.bigdata.common.data_source_metadata.service;

import com.jz.bigdata.common.data_source.entity.DataSource;
import com.jz.bigdata.common.data_source_metadata.entity.*;
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
     * 获取库信息
     * @param dataSourceMetadata 查询条件对象
     * @return 库的基本信息，由于返回需要count和数据list，因此通过map存放，key：count value：总条数。key：list value：表的信息（list<MetadataTable>）
     */
    public Map<String, Object> getDatabaseInfo(DataSourceMetadata dataSourceMetadata);
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
     * 更新元数据数据库信息
     * @param metadataDatabase
     * @return
     */
    public int updateMetadataDatabaseInfo(MetadataDatabase metadataDatabase);
    /**
     * 数据预览功能
     * @param databaseName
     * @param tableName
     * @param data_source_id
     * @return
     */
    public Map<String,Object> getDataPreview(String databaseName,String tableName,String data_source_id) throws Exception;

    /**
     * 通过元数据字段id获取详细信息
     * @param metadata_field_id 字段id
     * @return
     */
    public Map<String,String> getMetadataFieldInfo(String metadata_field_id);
    /**
     * 通过元数据数据库id获取详细信息
     * @param metadata_database_id 数据库id
     * @return
     */
    public MetadataDatabase getMetadataDatabaseInfo(String metadata_database_id);
    /**
     * 通过元数据表id获取详细信息
     * @param metadata_table_id  表id
     * @return
     */
    public MetadataTable getMetadataTableInfo(String metadata_table_id);
    /**
     * 获取表 top n 数据
     * @param dataSource 数据源基本信息
     * @param databaseName 数据库名称
     * @param tableName 表名
     * @param sample_num 前n条数据
     * @return List<Map<String, String>> 一个map为一行数据 map key为字段名 value为字段值
     */
    public List<Map<String, String>> getTableData(DataSource dataSource, String databaseName, String tableName, int sample_num ) throws Exception;
    /**
     * 获取表样本数据
     * @param dataSource 数据源基本信息
     * @param databaseName 数据库名称
     * @param tableName 表名
     * @param sample_num 样本数
     * @return List<Map<String, String>> 一个map为一行数据 map key为字段名 value为字段值
     */
    public List<Map<String, String>> getTableSampleData(DataSource dataSource, String databaseName, String tableName, int sample_num ) throws Exception;
}

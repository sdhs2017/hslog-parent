package com.jz.bigdata.common.data_source_metadata.dao;

import com.jz.bigdata.common.data_source_metadata.entity.DataSourceMetadata;
import com.jz.bigdata.common.data_source_metadata.entity.MetadataDatabase;
import com.jz.bigdata.common.data_source_metadata.entity.MetadataField;
import com.jz.bigdata.common.data_source_metadata.entity.MetadataTable;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/3/1 13:59
 * @Description:
 */
public interface IDataSourceMetadataDao {
    /**
     * 新增
     * @param dataSourceMetadata
     * @return
     */
    public int insert(DataSourceMetadata dataSourceMetadata);

    /**
     * 更新信息
     * @param dataSourceMetadata
     * @return
     */
    public int update(DataSourceMetadata dataSourceMetadata);
    /**
     * 更新源数据表信息
     * @param metadataTable
     * @return
     */
    public int updateMetadataTableInfo(MetadataTable metadataTable);
    /**
     * 更新源数据字段信息
     * @param metadataField
     * @return
     */
    public int updateMetadataFieldInfo(MetadataField metadataField);
    /**
     * 更新信息
     * @param data_source_id
     * @return
     */
    public int deleteByDataSourceId(@Param("data_source_id")String data_source_id);
    /**
     * 通过数据源id，删除初始化后的metadata信息，三条delete sql语句
     * @param data_source_id
     * @return
     */
    public int deleteMetadataByDataSourceId(@Param("data_source_id")String data_source_id);
    /**
     * 批量insert
     * @param list
     * @return
     */
    public int batchInsert(List<DataSourceMetadata> list);

    /**
     * 元数据，数据库信息批量插入
     * @param list 数据库基本信息
     * @return
     */
    public int batchInsertDatabase(List<MetadataDatabase> list);
    /**
     * 元数据，表信息批量插入
     * @param list 表基本信息
     * @return
     */
    public int batchInsertTable(List<MetadataTable> list);
    /**
     * 元数据，字段信息批量插入
     * @param list 字段基本信息
     * @return
     */
    public int batchInsertField(List<MetadataField> list);

    /**
     * 根据数据源id获取数据库信息列表
     * @param data_source_id 数据源id
     * @return
     */
    public List<MetadataDatabase> getMetadataDatabaseList(@Param("data_source_id")String data_source_id);

    /**
     * 根据数据源id及数据库名称获取表信息
     * @param data_source_id 数据源id
     * @param database 数据库名称
     * @return
     */
    public List<MetadataTable> getMetadataTableList(@Param("data_source_id")String data_source_id,@Param("metadata_database_name")String metadata_database_name);
    /**
     * 获取数据库-表下的字段  count
     * @param data_source_id 数据源id
     * @param database 数据库
     * @param table 表
     * @return
     */
    public List<List<Map<String,String>>> getMetadataFieldCount(DataSourceMetadata dataSourceMetadata);

    /**
     * 获取分页的字段详情
     * @param data_source_id 数据源id
     * @param database 数据库
     * @param table 表
     * @param startRecord 起始行
     * @param pageSize 条数
     * @return
     */
    public List<MetadataField> getMetadataFieldListWithPage(DataSourceMetadata dataSourceMetadata);

    /**
     * 获取数据库下的表 count
     * @param data_source_id 数据源id
     * @param database 数据库
     * @return
     */
    public List<List<Map<String,String>>> getMetadataTableCount(DataSourceMetadata dataSourceMetadata);

    /**
     * 获取分页的表详情
     * @param data_source_id 数据源id
     * @param database 数据库
     * @param startRecord 起始行
     * @param pageSize 条数
     * @return
     */
    public List<MetadataTable> getMetadataTableListWithPage(DataSourceMetadata dataSourceMetadata);

    /**
     * 根据数据源id，数据库名称，表名，获取其下字段信息
     * @param data_source_id 数据源id
     * @param database 数据库名称
     * @param table 表名
     * @return
     */
    public List<MetadataField> getMetadataFieldList(@Param("data_source_id")String data_source_id,@Param("database")String database,@Param("table")String table);
    /**
     * 获取数据源下的数据库信息
     * @param data_source_id 数据源id
     * @return
     */
    public List<DataSourceMetadata> getDatabaseList(@Param("data_source_id")String data_source_id);

    /**
     * 获取数据库下的表信息
     * @param data_source_id 数据源id
     * @param database 数据库名称
     * @return
     */
    public List<DataSourceMetadata> getTableList(@Param("data_source_id")String data_source_id,@Param("database")String database);
    /**
     * 获取数据库下的表信息
     * @param dataSourceMetadata 元数据信息
     * @return
     */
    public List<DataSourceMetadata> getTableListWithPage(DataSourceMetadata dataSourceMetadata);
    /**
     * 获取数据库下的表信息 count
     * @param dataSourceMetadata 元数据信息
     * @return
     */
    public List<List<Map<String,String>>> getTableListCount(DataSourceMetadata dataSourceMetadata);

    /**
     * 获取表的字段信息
     * @param data_source_id 数据源id
     * @param database 数据库名称
     * @param table 表名
     * @return
     */
    public List<DataSourceMetadata> getFieldList(@Param("data_source_id")String data_source_id,@Param("database")String database,@Param("table")String table);

    /**
     * 获取表下的字段信息，带分页
     * @param dataSourceMetadata
     * @return
     */
    public List<DataSourceMetadata> getFieldListWithPage(DataSourceMetadata dataSourceMetadata);
    /**
     * 获取表下的字段信息count
     * @param dataSourceMetadata
     * @return
     */
    public List<List<Map<String,String>>> getFieldListCount(DataSourceMetadata dataSourceMetadata);

}

package com.jz.bigdata.common.data_source_metadata.dao;

import com.jz.bigdata.common.data_source.entity.DataSource;
import com.jz.bigdata.common.data_source_metadata.entity.*;
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
     * 更新源数据数据库信息
     * @param metadataDatabase
     * @return
     */
    public int updateMetadataDatabaseInfo(MetadataDatabase metadataDatabase);
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
     * 样本数据批量写入
     * @param list 样本数据基本信息
     * @return
     */
    public int batchInsertSample(List<DataSourceSample> list);

    /**
     * 根据数据源id获取数据库信息列表
     * @param data_source_id 数据源id
     * @return
     */
    public List<MetadataDatabase> getMetadataDatabaseList(@Param("data_source_id")String data_source_id);
    /**
     * 自动发现模块，查询可自动发现的数据库，根据其“是否自动发现”查询
     * @param data_source_id 数据源id
     * @return
     */
    public List<MetadataDatabase> getMetadataDatabaseList_discovery(@Param("data_source_id")String data_source_id);

    /**
     * 根据数据源id及数据库名称获取表信息
     * @param data_source_id 数据源id
     * @param metadata_database_name 数据库名称
     * @return
     */
    public List<MetadataTable> getMetadataTableList(@Param("data_source_id")String data_source_id,@Param("metadata_database_name")String metadata_database_name);
    /**
     * 根自动发现模块，查询可自动发现的表，根据其“是否自动发现”查询
     * @param data_source_id 数据源id
     * @param metadata_database_name 数据库名称
     * @return
     */
    public List<MetadataTable> getMetadataTableList_discovery(@Param("data_source_id")String data_source_id,@Param("metadata_database_name")String metadata_database_name);

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
    public List<List<Map<String,String>>> getMetadataFieldListWithPage(DataSourceMetadata dataSourceMetadata);

    /**
     * 获取数据库下的表 count
     * @param data_source_id 数据源id
     * @param database 数据库
     * @return
     */
    public List<List<Map<String,String>>> getMetadataTableCount(DataSourceMetadata dataSourceMetadata);

    /**
     * 获取分页的表详情
     * @param dataSourceMetadata 数据源基本信息
     * @return
     */
    public List<MetadataTable> getMetadataTableListWithPage(DataSourceMetadata dataSourceMetadata);
    /**
     * 获取数据库下的表 count
     * @param data_source_id 数据源id
     * @param database 数据库
     * @return
     */
    public List<List<Map<String,String>>> getMetadataDatabaseCount(DataSourceMetadata dataSourceMetadata);

    /**
     * 获取分页的表详情
     * @param dataSourceMetadata 数据源基本信息
     * @return
     */
    public List<MetadataDatabase> getMetadataDatabaseListWithPage(DataSourceMetadata dataSourceMetadata);
    /**
     * 根据数据源id，数据库名称，表名，获取其下字段信息
     * @param data_source_id 数据源id
     * @param database 数据库名称
     * @param table 表名
     * @return
     */
    public List<MetadataField> getMetadataFieldList(@Param("data_source_id")String data_source_id,@Param("database")String database,@Param("table")String table);

    /**
     * 通过数据源id获取所有字段信息（字段信息中冗余了 库、 表 信息）
     * @param data_source_id
     * @return
     */
    public List<MetadataField> getMetadataFieldAllByDataSourceId(@Param("data_source_id")String data_source_id);
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

    /**
     * 插入字段与元数据标识中间表
     * @param metadata_field_id 字段id
     * @param metadata_identify_id 标识id
     * @param metadata_identify_name 标识名称，冗余
     * @return
     */
    public int insertMetadataFieldIdentifyRelation(@Param("metadata_field_id")String metadata_field_id,@Param("metadata_identify_id")String metadata_identify_id,@Param("metadata_identify_name")String metadata_identify_name);

    /**
     * 通过元数据字段id获取字段详情
     * @param metadata_field_id
     * @return
     */
    public List<List<Map<String,String>>> getMetadataFieldInfo(@Param("metadata_field_id")String metadata_field_id);
    /**
     * 通过元数据数据库id获取字段详情
     * @param metadata_database_id
     * @return
     */
    public MetadataDatabase getMetadataDatabaseInfo(@Param("metadata_database_id")String metadata_database_id);
    /**
     * 通过元数据 表id获取字段详情
     * @param metadata_table_id
     * @return
     */
    public MetadataTable getMetadataTableInfo(@Param("metadata_table_id")String metadata_table_id);
    /**
     * 根据字段id删除与其相关的标识
     * @param metadata_field_id
     * @return
     */
    public int deleteRelationByFieldId(@Param("metadata_field_id")String metadata_field_id);

    /**
     * 通过条件获取样本数据
     * @param condition 查询条件
     * @return 样本数据列表
     */
    public List<DataSourceSample> getSampleDataByCondition(DataSourceSample condition);

    /**
     * 通过数据源id获取其他所有的样本数据，数据结构为字段信息&样本数据
     * 字段信息与样本数据为一对多关系
     * @param data_source_id 数据源id
     * @return 字段列表，字段中包含样本数据list
     */
    /**
     * 通过数据源id获取其他所有的样本数据，数据结构为字段信息&样本数据
     *      * 字段信息与样本数据为一对多关系
     * @param data_source_id 数据源id
     * @param database_name 数据库名称
     * @param table_name 表名
     * @param is_auto_discovery 是否自动发现
     * @return
     */
    public List<MetadataField> getFieldSampleData(@Param("data_source_id")String data_source_id,@Param("database_name")String database_name,@Param("table_name")String table_name,@Param("is_auto_discovery")String is_auto_discovery);

}

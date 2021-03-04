package com.jz.bigdata.common.data_source_metadata.dao;

import com.jz.bigdata.common.data_source_metadata.entity.DataSourceMetadata;
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
     * 更新信息
     * @param data_source_id
     * @return
     */
    public int deleteByDataSourceId(@Param("data_source_id")String data_source_id);
    /**
     * 批量insert
     * @param list
     * @return
     */
    public int batchInsert(List<DataSourceMetadata> list);

    /**
     * 获取数据源下的数据库信息
     * @param data_source_id
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

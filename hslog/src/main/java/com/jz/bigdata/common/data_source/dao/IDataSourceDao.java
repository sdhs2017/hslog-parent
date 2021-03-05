package com.jz.bigdata.common.data_source.dao;


import com.jz.bigdata.common.data_source.entity.DataSource;
import com.jz.bigdata.common.event_alert.entity.EventAlert;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/2/3 11:11
 * @Description:
 */
public interface IDataSourceDao {
    /**
     * 新增
     * @param dataSource
     * @return
     */
    public int insert(DataSource dataSource);
    /**
     * 更新
     * @param dataSource
     * @return
     */
    public int update(DataSource dataSource);

    /**
     * 根据id获取详情
     * @param data_source_id
     * @return
     */
    public DataSource selectDataSourceInfoById(@Param("data_source_id") String data_source_id);

    /**
     * 查询数据源信息列表，带分页
     * @param dataSource
     * @return
     */
    public List<DataSource> getListByConditionWithPage(DataSource dataSource);
    /**
     * 查询数据源信息列表，带分页
     * @param dataSource
     * @return
     */
    public List<DataSource> getListByCondition(DataSource dataSource);

    /**
     * 获取列表count数
     * @param dataSource
     * @return
     */
    public List<List<Map<String,String>>> getCountByCondition(DataSource dataSource);

    /**
     * 批量删除
     * @param ids
     * @return
     */
    public int deletes(String[] ids);

}

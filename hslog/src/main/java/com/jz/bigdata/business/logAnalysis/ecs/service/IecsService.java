package com.jz.bigdata.business.logAnalysis.ecs.service;

import java.util.List;
import java.util.Map;

public interface IecsService {

    /**
     * 获取索引数据量通过条件
     * @param map 其他条件
     * @param starttime 时间范围-开始时间
     * @param endtime 时间范围-结束时间
     * @param indices 索引名称
     * @return
     */
    public long getCount(Map<String, String> map, String starttime,String endtime, String... indices) throws Exception;
    /**
     * 通过ID删除索引数据
     * @param index
     * @param id
     * @return
     */
    public String deleteById(String index, String id) throws Exception;
    /**
     * 全文检索
     * @param starttime 开始时间
     * @param endtime 截止时间
     * @param content 检索条件
     * @param multiQueryField 用于多字段匹配
     * @param map 其他查询条件
     * @param page 页码
     * @param size 当前页显示数据条数
     * @param indices 索引名称
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getListByContent(String starttime,String endtime,String content, String [] multiQueryField, Map<String, String> map, String page, String size, String... indices) throws Exception;

    /**
     * 日志管理-精确查询业务
     * @param map 其他条件
     * @param starttime 时间范围-开始时间
     * @param endtime 时间范围-结束时间
     * @param page 页码
     * @param size 每页展示数据条数
     * @param indices 索引名
     * @return 返回详细日志内容
     */
    public List<Map<String, Object>> getLogListByBlend(Map<String, String> map, String starttime, String endtime, String page, String size, String... indices) throws Exception;

    /**
     * 实现类sql的group by并进行sum求和计算的功能,包含时间范围、条件等
     * @param indices 索引名称
     * @param groupByField 需要进行聚合的字段
     * @param size 聚合结果最大返回数
     * @param starttime 时间范围-开始时间
     * @param endtime 时间范围-结束时间
     * @param map 其他限制条件
     * @return
     */
    public List<Map<String, Object>> groupByThenCount(String starttime, String endtime, String groupByField, int size, Map<String, String> map, String... indices) throws Exception;

    /**
     * 获取各个时段的日志数据
     * @param starttime 时间范围-开始时间
     * @param endtime 时间范围-结束时间
     * @param groupByDateField 需要进行聚合的时间字段
     * @param size 聚合结果最大返回数
     * @param map 其他限制条件
     * @param indices 索引
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getListGroupByTime(String starttime, String endtime, String groupByDateField,int size, Map<String, String> map, String... indices) throws Exception;

    /**
     * 获取各个时段的事件数据
     * @param starttime 时间范围-开始时间
     * @param endtime 时间范围-结束时间
     * @param groupByDateField 需要进行聚合的时间字段
     * @param subgroupbyField
     * @param subgroupbyType
     * @param size 聚合结果最大返回数
     * @param map 其他限制条件
     * @param indices 索引
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getListGroupByTimeAndEvent(String starttime, String endtime, String groupByDateField, String subgroupbyType, String subgroupbyField, int size, Map<String, String> map, String... indices) throws Exception;

    /**
     * 事件数据统计，分 全部/高危/中危/普通
     * @param starttime 时间范围-开始时间
     * @param endtime 时间范围-结束时间
     * @param map 其他限制条件
     * @param indices 索引
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getCountGroupByEventType(String starttime, String endtime,String groupByDateField,Map<String, String> map, String... indices) throws Exception;


}

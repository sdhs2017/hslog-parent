package com.hs.elsearch.dao.ecsDao;

import java.util.List;
import java.util.Map;

public interface IEcsSearchDao {

    /**
     * 通过id删除单一数据(doc)
     * @param index
     * @param id
     * @return
     */
    public String deleteById(String index, String id) throws Exception;

    /**
     * 通过查询条件获取日志数
     * @param map
     * @param indices
     * @return
     */
    public long getCount(Map<String, String> map, String starttime, String endtime, String... indices) throws Exception;

    /**
     * 全文检索
     * @param content 关键字
     * @param  multiQueryField 多字段查询
     * @param map 其他查询条件
     * @param page 分页
     * @param size 每页数据量
     * @param indices 索引名称
     * @return 返回符合关键字的日志内容
     */
    public List<Map<String, Object>> getListByContent(String content, String [] multiQueryField, Map<String, String> map, int page, int size, String... indices) throws Exception;

    /**
     *
     * @param map
     * @param starttime
     * @param endtime
     * @param from
     * @param size
     * @param indices
     * @return
     */
    public List<Map<String, Object>> getLogListByMap(Map<String, String> map, String starttime, String endtime, Integer from, Integer size, String... indices) throws Exception;

    /**
     * 带条件的聚合查询
     * @param starttime 时间范围-开始时间
     * @param endtime  时间范围-结束时间
     * @param groupByField 聚合字段
     * @param map 其他条件
     * @param indices 索引名称
     * @return 返回聚合结果
     */
    public List<Map<String, Object>> getListByAggregation(String starttime, String endtime, String groupByField, int size,
                                                          Map<String, String> map, String... indices) throws Exception;

    /**
     * 基于时间段的聚合查询
     * @param starttime
     * @param endtime
     * @param dateHistogramField
     * @param map
     * @param indices
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getListByDateHistogramAggregation(String starttime, String endtime, String dateHistogramField,
                                                                       Map<String, String> map, String... indices) throws Exception;

    /**
     * 基于时间段的嵌套复杂聚合查询
     * @param starttime
     * @param endtime
     * @param dateHistogramField
     * @param map
     * @param indices
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getListByDateHistogramAggregation(String starttime, String endtime, String dateHistogramField,
                                                                       String subField, String subAggsType, Map<String, String> map, String... indices) throws Exception;
}

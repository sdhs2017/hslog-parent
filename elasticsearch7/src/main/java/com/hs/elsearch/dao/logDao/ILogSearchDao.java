package com.hs.elsearch.dao.logDao;

import java.util.List;
import java.util.Map;

public interface ILogSearchDao {




    /**
     * 通过查询条件获取日志数
     * @param map
     * @param types
     * @param indices
     * @return
     */
    public long getCount(Map<String, String> map, String starttime, String endtime, String[] types, String... indices);


    /**
     * 带条件的聚合查询
     * @param types idnex types字段，7版本中移除
     * @param starttime 时间范围-开始时间
     * @param endtime  时间范围-结束时间
     * @param groupByField 聚合字段
     * @param map 其他条件
     * @param indices 索引名称
     * @return 返回聚合结果
     */
    public List<Map<String, Object>> getListByAggregation(String[] types, String starttime, String endtime, String groupByField, int size,
                                                          Map<String, String> map, String... indices) throws Exception;

    /**
     * 带条件的聚合查询
     * @param types idnex types字段，7版本中移除
     * @param starttime 时间范围-开始时间
     * @param endtime  时间范围-结束时间
     * @param groupByFields 多字段聚合字段
     * @param map 其他条件
     * @param indices 索引名称
     * @return 返回聚合结果
     */
    public List<Map<String, Object>> getListByAggregation(String[] types, String starttime, String endtime, String[] groupByFields, int size,
                                                          Map<String, String> map, String... indices) throws Exception;

    /**
     *
     * @param types idnex types字段，7版本中移除
     * @param starttime 时间范围-开始时间
     * @param endtime 时间范围-结束时间
     * @param dateHistogramField 时间字段
     * @param map 其他条件
     * @param indices 索引名称
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getListByDateHistogramAggregation(String[] types, String starttime, String endtime, String dateHistogramField,
                                                                       Map<String, String> map, String... indices) throws Exception;


    /**
     * 全文检索
     * @param content 关键字
     * @param userid 用户id
     * @param page 分页
     * @param size 每页数据量
     * @param types index types字段，7版本中移除
     * @param indices 索引名称
     * @return 返回符合关键字的日志内容
     */
    public List<Map<String, Object>> getListByContent(String content, String userid, int page, int size, String[] types, String... indices) throws Exception;

    /**
     *
     * @param map
     * @param starttime
     * @param endtime
     * @param types
     * @param indices
     * @return
     */
    public List<Map<String, Object>> getListByMap(Map<String, String> map, String starttime, String endtime, String[] types, String... indices) throws Exception;

    /**
     *
     * @param map
     * @param starttime
     * @param endtime
     * @param from
     * @param size
     * @param types
     * @param indices
     * @return
     */
    public List<Map<String, Object>> getListByMap(Map<String, String> map, String starttime, String endtime, Integer from, Integer size, String[] types, String... indices) throws Exception;

    /**
     *
     * @param map
     * @param starttime
     * @param endtime
     * @param from
     * @param size
     * @param types
     * @param indices
     * @return
     */
    public List<Map<String, Object>> getLogListByMap(Map<String, String> map, String starttime, String endtime, Integer from, Integer size, String[] types, String... indices) throws Exception;
}

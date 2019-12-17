package com.hs.elsearch.dao.flowDao;

import java.util.List;
import java.util.Map;

public interface IFlowSearchDao {

    /**
     * 通过查询条件获取日志数
     * @param map 条件
     * @param types index的type字段，在7版本中移除
     * @param indices 索引名称
     * @return
     */
    public long getFlowCount(Map<String,String> map, String starttime, String endtime, String [] types, String... indices);


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
                                                          Map<String, String> map,String... indices);

    /**
     * 带条件的聚合查询
     * @param types idnex types字段，7版本中移除
     * @param starttime 时间范围-开始时间
     * @param endtime  时间范围-结束时间
     * @param groupByField 聚合字段
     * @param sumField 求和字段
     * @param map 其他条件
     * @param indices 索引名称
     * @return 返回聚合结果
     */
    public List<Map<String, Object>> getListBySumOfAggregation(String[] types, String starttime, String endtime, String groupByField,
                                                               String  sumField, int size, Map<String, String> map,String... indices);

    /**
     * 带条件的metric聚合查询-sum
     * @param types idnex types字段，7版本中移除
     * @param starttime 时间范围-开始时间
     * @param endtime  时间范围-结束时间
     * @param sumField 求和字段
     * @param map 其他条件
     * @param indices 索引名称
     * @return 返回聚合结果
     */
    public List<Map<String, Object>> getListBySumOfMetrics(String[] types, String starttime, String endtime,
                                                               String  sumField, int size, Map<String, String> map,String... indices);
    /**
     * 条件查询
     * @param map 条件
     * @param starttime 时间范围-开始时间
     * @param endtime 时间范围-结束时间
     * @param from 起始值
     * @param size 返回数据
     * @param types index的type字段，在7版本中移除
     * @param indices 索引名称
     * @return 返回查询内容
     */
    public List<Map<String, Object>> getFlowListByMap(Map<String, String> map, String starttime, String endtime, Integer from, Integer size, String[] types, String... indices);
}

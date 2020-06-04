package com.jz.bigdata.business.logAnalysis.flow.service;

import com.hs.elsearch.entity.HttpRequestParams;
import com.hs.elsearch.entity.VisualParam;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface IflowService {

    /**
     * 获取索引数据量通过条件
     * @param index
     * @param types
     * @param map
     * @return
     */
    public long getCount(String index,String [] types,Map<String, String> map);

    /**
     * 新
     * 实现类sql的group by功能,包含时间范围、条件等
     * @param index 索引名称
     * @param types index 的 type字段，在7版本中移除
     * @param groupByField 需要进行聚合的字段
     * @param size 聚合结果最大返回数
     * @param starttime 时间范围-开始时间
     * @param endtime 时间范围-结束时间
     * @param map 其他限制条件
     * @return
     */
    public List<Map<String, Object>> groupBy(String index,String[] types,String groupByField, int size, String starttime, String endtime,Map<String, String> map) throws Exception;

    /**
     * 新
     * 实现类sql的group by功能,对多个字段进行聚合,包含时间范围、条件等
     * @param index 索引名称
     * @param types index 的 type字段，在7版本中移除
     * @param groupByField 需要进行聚合的字段,数组
     * @param size 聚合结果最大返回数
     * @param starttime 时间范围-开始时间
     * @param endtime 时间范围-结束时间
     * @param map 其他限制条件
     * @return
     */
    public List<Map<String, Object>> groupBys(String index,String[] types,String[] groupByField, int size, String starttime, String endtime,Map<String, String> map) throws Exception;


    /**
     * 新
     * 实现类sql的group by功能,包含时间范围、条件等
     * @param index 索引名称
     * @param types index 的 type字段，在7版本中移除
     * @param groupByFields 需要进行聚合的字段,数组
     * @param size 聚合结果最大返回数
     * @param starttime 时间范围-开始时间
     * @param endtime 时间范围-结束时间
     * @param map 其他限制条件
     * @return
     */
    public List<List<Map<String, Object>>> groupBy(String index,String[] types,String[] groupByFields, int size, String starttime, String endtime,Map<String, String> map) throws Exception;

    /**
     * 新
     * 实现类sql的group by并进行sum求和计算的功能,包含时间范围、条件等
     * @param index 索引名称
     * @param types index 的 type字段，在7版本中移除
     * @param groupByField 需要进行聚合的字段
     * @param sumField 聚合分组后进行sum计算的字段
     * @param size 聚合结果最大返回数
     * @param starttime 时间范围-开始时间
     * @param endtime 时间范围-结束时间
     * @param map 其他限制条件
     * @return
     */
    public List<Map<String, Object>> groupByThenSum(String index,String[] types,String groupByField,String sumField, int size, String starttime, String endtime,Map<String, String> map) throws Exception;

    /**
     * 新
     * 实现类sql的group by并进行avg平均值计算的功能,包含时间范围、条件等
     * @param indices 索引名称
     * @param types index 的 type字段，在7版本中移除
     * @param groupByField 需要进行聚合的字段
     * @param avgField 聚合分组后进行sum计算的字段
     * @param size 聚合结果最大返回数
     * @param starttime 时间范围-开始时间
     * @param endtime 时间范围-结束时间
     * @param map 其他限制条件
     * @return
     */
    public List<Map<String, Object>> groupByThenAvg(String[] types, String groupByField, String avgField, int size, String starttime, String endtime, Map<String, String> map, String... indices) throws Exception;

    /**
     * 新
     * 实现metrics sum,包含时间范围、条件等
     * @param types index 的 type字段，在7版本中移除
     * @param sumField 聚合进行sum计算的字段
     * @param starttime 时间范围-开始时间
     * @param endtime 时间范围-结束时间
     * @param map 其他限制条件
     * @param indices 索引名称
     * @return
     */
    public List<Map<String, Object>> getSumByMetrics(String[] types, String sumField, int size, String starttime, String endtime,Map<String, String> map, String... indices) throws Exception;

    /**
     * 新
     * 实现metrics sum,包含时间范围、条件等
     * @param types index 的 type字段，在7版本中移除
     * @param countField 聚合进行count计算的字段
     * @param starttime 时间范围-开始时间
     * @param endtime 时间范围-结束时间
     * @param map 其他限制条件
     * @param indices 索引名称
     * @return
     */
    public List<Map<String, Object>> getCountByMetrics(String[] types, String countField, int size, String starttime, String endtime,Map<String, String> map, String... indices) throws Exception;

    /**
     * 新
     * 流量管理-流量日志查询
     * @param map 其他条件
     * @param starttime 时间范围-开始时间
     * @param endtime 时间范围-结束时间
     * @param page 页码
     * @param size 每页展示数据条数
     * @param types index type字段，7版本移除
     * @param indices 索引名
     * @return 返回详细日志内容
     */
    public List<Map<String, Object>> getFlowListByBlend(Map<String, String> map, String starttime, String endtime, String page, String size, String[] types, String... indices) throws Exception;

    /**
     * @param params 相关参数
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getListByAggregation(VisualParam params) throws Exception;
    /**
     * 嵌套聚合
     * @param params 相关参数
     * @return
     * @throws Exception
     */
    public Map<String, LinkedList<Map<String,Object>>> getListByMultiAggregation(VisualParam params) throws Exception;
}

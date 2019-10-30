package com.hs.elsearch.dao;

import java.util.List;
import java.util.Map;

public interface IElasticsearchDao {


    /**
     * 用于初始化操作创建index的模板
     * @param tempalateName 模板名称
     * @param tempalatePattern index名称通配
     * @param settings 针对index模板的具体属性设置，包括分片数、副本数、最大查询值等，其中分片数设置之后是不能再修改的
     * @param type 在5.4版本中index的template需要指明mapping对应的type，在7版本后只需要指定mapping
     * @param mapping 字段属性
     * @return 创建成功返回true，失败false
     */
    public boolean createTemplateOfIndex(String tempalateName, String tempalatePattern, Map<String,Object> settings,String type, String mapping) throws Exception;

    /**
     * 通过查询条件获取日志数
     * @param map
     * @param types
     * @param indices
     * @return
     */
    public long getCount(Map<String,String> map, String starttime,String endtime, String [] types,String... indices);


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
    public List<Map<String, Object>> getListByAggregation(String[] types, String starttime, String endtime, String groupByField,
                                                           Map<String, String> map,String... indices);


    public List<Map<String, Object>> getListByDateHistogramAggregation(String[] types, String starttime, String endtime, String dateHistogramField,
                                                           Map<String, String> map,String... indices);

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
    public List<Map<String, Object>> getListByContent(String content,String userid,int page,int size,String[] types,String... indices);

    public List<Map<String, Object>> getListByMap(Map<String, String> map, String starttime, String endtime, String[] types,String... indices);
}

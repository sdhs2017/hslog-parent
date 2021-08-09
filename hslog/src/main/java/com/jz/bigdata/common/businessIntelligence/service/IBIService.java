package com.jz.bigdata.common.businessIntelligence.service;

import com.hs.elsearch.entity.SearchConditions;
import com.hs.elsearch.entity.VisualParam;
import com.hs.elsearch.util.MappingField;
import com.jz.bigdata.common.businessIntelligence.entity.Dashboard;
import com.jz.bigdata.common.businessIntelligence.entity.SqlSearchConditions;
import com.jz.bigdata.common.businessIntelligence.entity.Visualization;
import org.elasticsearch.action.DocWriteResponse;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface IBIService {

    /**
     * 通过X轴的聚合方式获取对应的field
     * @param templateName
     * @param indexName
     * @param agg 聚合方式
     * @return
     */
    public List<MappingField> getFieldByXAxisAggregation(String templateName, String indexName,String agg) throws Exception;
    /**
     * 通过Y轴的聚合方式获取对应的field
     * @param templateName
     * @param indexName
     * @param agg 聚合方式
     * @return
     */
    public List<MappingField> getFieldByYAxisAggregation(String templateName, String indexName,String agg) throws Exception;
    /**
     * 获取filter字段信息
     * @param templateName
     * @param source_type 数据源类型， template index
     * @param agg 聚合方式
     * @return
     */
    public List<MappingField> getFilterField(String templateName, String source_type, String agg) throws Exception;

    /**
     * 保存图表信息
     * @param visual 图表信息bean
     * @return
     */
    public DocWriteResponse.Result saveVisualization(Visualization visual , String indexName) throws Exception;
    /**
     * 根据聚合方式的不同，筛选出符合要求的字段信息
     * @param aggType 聚合类型
     * @return
     */
    public List<MappingField> getMappingFieldByAggType(String templateName,String source_type, String aggType) throws Exception;
    /**
     * 保存图表信息
     * @param visual 图表信息bean
     * @return
     */
    public DocWriteResponse.Result saveDashboard(Dashboard dashboard , String indexName) throws Exception;
    /**
     * 获取图表列表，不包含option和params信息（数据内容太多，影响响应速度）
     * @return
     */
    public String getVisualizations(String indexName, HttpSession session) throws Exception;
    /**
     * 获取仪表盘列表，不包含option和params信息（数据内容太多，影响响应速度）
     * @return
     */
    public String getDashboards(String indexName,HttpSession session) throws Exception;
    /**
     * 获取dashboard涉及到的template
     * @return
     */
    public String getDashboardTemplates(String ids,String indexName) throws Exception;
    /**
     * 根据id获取图表详情以及查询的数据结果
     * @param id
     * @return
     */
    public String getVisualizationById(String id,String indexName) throws Exception;
    /**
     * 根据id获取dashboard详情
     * @param id
     * @return
     */
    public String getDashboardById(String id,String indexName) throws Exception;
    /**
     * 根据id删除图表
     * @param id
     * @return
     */
    public String deleteVisualizationById(String id,String indexName) throws Exception;
    /**
     * 根据id删除仪表盘
     * @param id
     * @return
     */
    public String deleteDashboardById(String id,String indexName) throws Exception;
    /**
     * 查看该标题的图表是否存在
     * @param title 标题
     * @param indexName 索引
     * @return
     * @throws Exception
     */
    public boolean isVisualizationExists(String title,String indexName) throws Exception;
    /**
     * 查看该标题的仪表盘
     * @param title 标题
     * @param indexName 索引
     * @return
     * @throws Exception
     */
    public boolean isDashboardExists(String title,String indexName) throws Exception;

    /**
     * 获取cluster-setting信息中的persistent的search.max_buckets
     * @return
     * @throws Exception
     */
    public int getClusterSearchMaxBuckets() throws Exception;
    /**
     * 嵌套聚合
     * @param params 相关参数
     * @return
     * @throws Exception
     */
    public Map<String, Object> getMultiAggregationDataSet(VisualParam params) throws Exception;
    /**
     * 嵌套聚合，数据返回echart饼图格式
     * @param conditions 相关参数
     * @return
     * @throws Exception
     */
    public LinkedList<ArrayList<Map<String,Object>>> getMultiAggregationData_pie(SearchConditions conditions) throws Exception;
    /**
     * 嵌套聚合，数据返回指标数据格式，类似饼图
     * @param conditions 相关参数
     * @return
     * @throws Exception
     */
    public List<Map<String,Object>> getMultiAggregationData_metric(SearchConditions conditions) throws Exception;
    /**
     * 嵌套聚合，数据返回echart dataset格式并针对折线图做了处理
     * @param conditions 相关参数
     * @return
     * @throws Exception
     */
    public Map<String,Object> getMultiAggregationData_line(SearchConditions conditions) throws Exception;
    /**
     * 嵌套聚合，数据返回echart dataset格式并针对柱状图做了处理
     * @param conditions 相关参数
     * @return
     * @throws Exception
     */
    public Map<String,Object> getMultiAggregationData_bar(SearchConditions conditions) throws Exception;

    /**
     * 获取动态表格查询结果
     * @param conditions
     * @return
     * @throws Exception
     */
    public String getSearchData_dynamicTable(SearchConditions conditions) throws Exception;

    /**
     * 获取mysql数据库所有表
     * @return
     * @throws Exception
     */
    public List<Map<String,String>> showTables()throws Exception;

    /**
     * 获取表对应的所有字段
     * @param tableName
     * @return
     * @throws Exception
     */
    public List<Map<String,String>> showColumns(String tableName)throws Exception;

    /**
     * 根据条件组装sql语句并返回执行结果
     * @param sql
     * @return
     * @throws Exception
     */
    public Map<String, Object> getDataByConditions(SqlSearchConditions sqlSearchConditions)throws Exception;

    /**
     * 通过自定义索引名称获取该索引下的所有字段信息（基于mapping）
     * @param custom_index_name 索引名称
     * @param agg 聚合方式
     * @return 字段信息list
     * @throws Exception
     */
    public List<MappingField> getFieldsByCustomIndex(String custom_index_name,String agg) throws Exception;

    /**
     * 通过id获取ES该条记录的详情
     * @param index 索引名称
     * @param id 数据id
     * @return
     */
    public Map<String, String> getDetailsById(String index,String id) throws Exception;
}

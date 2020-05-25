package com.jz.bigdata.common.businessIntelligence.service;

import com.hs.elsearch.dao.biDao.entity.VisualParam;
import com.jz.bigdata.common.businessIntelligence.entity.Dashboard;
import com.jz.bigdata.common.businessIntelligence.entity.MappingField;
import com.jz.bigdata.common.businessIntelligence.entity.Visualization;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.cluster.settings.ClusterGetSettingsResponse;

import javax.servlet.http.HttpSession;
import java.util.List;

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
     * 实现类sql的group by并进行sum求和计算的功能,包含时间范围、条件等
     * @param params 统计相关的参数
     * @return
     */
    public String groupByThenSum(VisualParam params) throws Exception;
    /**
     * 实现类sql的group by并进行sum求和计算的功能,包含时间范围、条件等
     * @param params 统计相关的参数
     * @return
     */
    public String groupByThenCount(VisualParam params) throws Exception;
    /**
     * 实现类sql的group by并进行sum求和计算的功能,包含时间范围、条件等
     * @param params 统计相关的参数
     * @return
     */
    public String groupByThenAvg(VisualParam params) throws Exception;
    /**
     * 实现类sql的group by并进行sum求和计算的功能,包含时间范围、条件等
     * @param params 统计相关的参数
     * @return
     */
    public String groupByThenMax(VisualParam params) throws Exception;
    /**
     * 实现类sql的group by并进行sum求和计算的功能,包含时间范围、条件等
     * @param params 统计相关的参数
     * @return
     */
    public String groupByThenMin(VisualParam params) throws Exception;

    /**
     * 保存图表信息
     * @param visual 图表信息bean
     * @return
     */
    public DocWriteResponse.Result saveVisualization(Visualization visual , String indexName) throws Exception;
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
}

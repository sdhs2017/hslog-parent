package com.jz.bigdata.common.businessIntelligence.controller;

import com.hs.elsearch.entity.SearchConditions;
import com.hs.elsearch.service.ISearchService;
import com.hs.elsearch.util.ElasticConstant;
import com.hs.elsearch.util.ErrorInfoException;
import com.hs.elsearch.util.MappingField;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.businessIntelligence.entity.Dashboard;
import com.jz.bigdata.common.businessIntelligence.entity.HSData;
import com.jz.bigdata.common.businessIntelligence.entity.SqlSearchConditions;
import com.jz.bigdata.common.businessIntelligence.entity.Visualization;
import com.jz.bigdata.common.businessIntelligence.service.IBIService;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.DescribeLog;
import com.jz.bigdata.util.HttpRequestUtil;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.DocWriteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * BI报表
 */
@Slf4j
@Controller
@RequestMapping("/BI")
public class BIController {

    @Resource(name = "BIService")
    private IBIService iBIService;
    @Resource(name ="configProperty")
    private ConfigProperty configProperty;


    @Autowired
    protected ISearchService searchService;
    //filter通过聚合类型获取字段信息，默认获取所有字段信息(除了geo_point类型)
    private static final String FILTER_AGG_TYPE="AllExceptGeo";
    /**
     * Buckets
     * 通过X轴的聚合方式获取fields
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getFieldByXAxisAggregation", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "通过X轴的聚合方式获取对应的列")
    public String getFieldByXAxisAggregation(HttpServletRequest request){
        try{
            //聚合方式  支持count/max/min/sum/avg
            String agg = request.getParameter("agg");
            //获取数据源类型
            String source_type = request.getParameter("source_type")==null?"":request.getParameter("source_type");
            //数据返回结果
            List<MappingField> result;
            //数据源是index_name
            if(ElasticConstant.BI_SOURCE_TYPE_INDEX.equals(source_type)){
                String index_name = request.getParameter("index_name");
                result = iBIService.getFieldByXAxisAggregation(index_name,ElasticConstant.BI_SOURCE_TYPE_INDEX,agg);
            }else{
                //数据源是模板
                String templateName = request.getParameter("template_name");
                result = iBIService.getFieldByXAxisAggregation(templateName,ElasticConstant.BI_SOURCE_TYPE_TEMPLATE,agg);
            }
//            List<MappingField> result = iBIService.getFieldByXAxisAggregation(templateName,preIndexName+suffixIndexName,agg);
            return JSONArray.fromObject(result).toString();
        }catch(Exception e){
            log.error("通过X轴的聚合方式获取对应的列失败："+e.getStackTrace().toString());
            return Constant.failureMessage("数据查询失败");
        }
    }
    /**
     * Metrics
     * 通过Y轴的聚合方式获取fields
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getFieldByYAxisAggregation", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "通过Y轴的聚合方式获取对应的列")
    public String getFieldByYAxisAggregation(HttpServletRequest request){
        try{
            //聚合方式  支持count/max/min/sum/avg
            String agg = request.getParameter("agg");
            //获取数据源类型
            String source_type = request.getParameter("source_type")==null?"":request.getParameter("source_type");
            //数据返回结果
            List<MappingField> result;
            //数据源是index_name
            if(ElasticConstant.BI_SOURCE_TYPE_INDEX.equals(source_type)){
                String index_name = request.getParameter("index_name");
                result = iBIService.getFieldByYAxisAggregation(index_name,ElasticConstant.BI_SOURCE_TYPE_INDEX,agg);
            }else{
                //数据源是模板
                String templateName = request.getParameter("template_name");
                result = iBIService.getFieldByYAxisAggregation(templateName,ElasticConstant.BI_SOURCE_TYPE_TEMPLATE,agg);
            }
            return JSONArray.fromObject(result).toString();
        }catch(Exception e){
            log.error("通过Y轴的聚合方式获取对应的列失败："+e.getStackTrace().toString());
            return Constant.failureMessage("数据查询失败");
        }
    }
    /**
     * 通过filter方式获取fields
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getFieldByFilter", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "通过Filter方式获取对应的列")
    public String getFieldByFilter(HttpServletRequest request){
        try{
            //获取数据源类型
            String source_type = request.getParameter("source_type")==null?"":request.getParameter("source_type");
            //数据返回结果
            List<MappingField> result;
            //数据源是index_name
            if(ElasticConstant.BI_SOURCE_TYPE_INDEX.equals(source_type)){
                String index_name = request.getParameter("index_name");
                result = iBIService.getFilterField(index_name,ElasticConstant.BI_SOURCE_TYPE_INDEX,FILTER_AGG_TYPE);
            }else{
                //数据源是模板
                String templateName = request.getParameter("template_name");
                result = iBIService.getFilterField(templateName,ElasticConstant.BI_SOURCE_TYPE_TEMPLATE,FILTER_AGG_TYPE);
            }

            return JSONArray.fromObject(result).toString();
        }catch(Exception e){
            log.error("通过Filter方式获取对应的列失败："+e.getStackTrace().toString());
            return Constant.failureMessage("数据查询失败");
        }
    }
    /**
     * 创建动态表格时，获取所有的字段
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getFieldByDynamicTable", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "动态表格获取字段")
    public String getFieldByDynamicTable(HttpServletRequest request){
        try{
            //filter方式默认获取所有字段信息(除了geo_point类型)
            String agg = "All";
            //索引名称
            //String indexName = request.getParameter("indexName");
            //template名称
            String templateName = request.getParameter("template_name");
            //索引前缀
            String preIndexName = request.getParameter("pre_index_name");
            //索引后缀
            String suffixIndexName = request.getParameter("suffix_index_name");
            List<MappingField> result = iBIService.getFilterField(templateName,ElasticConstant.BI_SOURCE_TYPE_TEMPLATE,agg);
            return JSONArray.fromObject(result).toString();
        }catch(Exception e){
            log.error("动态表格获取字段失败："+e.getStackTrace().toString());
            return Constant.failureMessage("数据查询失败");
        }
    }
    /**
     * 通过字段类型获取对应的operator
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getOperatorByFiledType", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "通过字段类型获取对应的operation")
    public String getOperatorByFiledType(HttpServletRequest request){
        try{
            //字段类型
            String fieldType = request.getParameter("fieldType");
            return JSONArray.fromObject(ElasticConstant.fieldTypeOperator.get(fieldType)).toString();
        }catch(Exception e){
            log.error("通过字段类型获取对应的operation,失败："+e.getStackTrace().toString());
            return Constant.failureMessage("数据查询失败");
        }
    }
    /**
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDataByChartParams", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "通过图表参数获取查询结果，并返回")
    public String getDataByChartParams(HttpServletRequest request){
        try{
            //处理参数
            SearchConditions searchConditions = HttpRequestUtil.getSearchConditionsByRequest4Chart(request);
            //时间范围参数异常
            if(!Strings.isNullOrEmpty(searchConditions.getErrorInfo())){
                return Constant.failureMessage(searchConditions.getErrorInfo());
            }
            //如果x轴是时间聚合类型，进行计算
            if(searchConditions.getBucketList().size()>0&&"Date Histogram".equals(searchConditions.getBucketList().get(0).getAggType())){
                //计算聚合桶数，对超出设置search.max_buckets的，进行返回
                //计算方式，时间范围/时间间隔
                if(getSearchBuckets(searchConditions)){
                    //continue
                }else{
                    return Constant.failureMessage("数据查询失败!<br>请缩小时间范围或增大聚合间隔！");
                }
            }else{
                //continue
            }

            //判断index名称，如果是hslog*，则日期字段设置为logdate，如果是*beat*，则日期字段设置为@timestamp
            if(searchConditions.getIndex_name().indexOf("hslog")>=0){
                searchConditions.setDateField(Constant.PACKET_DATE_FIELD);
            }else if(searchConditions.getIndex_name().indexOf("beat")>=0){
                searchConditions.setDateField(Constant.BEAT_DATE_FIELD);
            }else{
                return Constant.failureMessage("数据源异常，请重新选择！");
            }

            Map<String,Object> result = searchService.getMultiAggDataSetWithZeroFill(searchConditions);
            return Constant.successData(JSONArray.fromObject(result).toString());
        }catch(ElasticsearchStatusException e){
            return handleElasticException(e);
        }catch(Exception e){
            log.error("数据可视化模块聚合查询失败："+e.getStackTrace().toString());
            return Constant.failureMessage("数据查询失败");
        }
    }
    /**
     *通过图表参数获取查询结果，并返回(饼图)
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDataByChartParams_pie", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "通过图表参数获取查询结果，并返回(饼图)")
    public String getDataByChartParams_pie(HttpServletRequest request){
        try{
            //处理参数
            SearchConditions searchConditions = HttpRequestUtil.getSearchConditionsByRequest4Chart(request);
            //时间范围参数异常
            if(!Strings.isNullOrEmpty(searchConditions.getErrorInfo())){
                return Constant.failureMessage(searchConditions.getErrorInfo());
            }
            //饼图目前仅支持一个metric指标，如果传的数据是多个，返回异常信息
            if(searchConditions.getMetricList().size()>1){
                return Constant.failureMessage("指标设置错误，请重新设置！");
            }
            LinkedList<ArrayList<Map<String,Object>>> result = iBIService.getMultiAggregationData_pie(searchConditions);
            return Constant.successData(JSONArray.fromObject(result).toString());
        }catch(ErrorInfoException e){
            return Constant.failureMessage(e.getMessage());
        }catch(ElasticsearchStatusException e){
            return handleElasticException(e);
        }catch(Exception e){
            log.error("数据可视化模块聚合查询失败："+e.getStackTrace().toString());
            return Constant.failureMessage("数据查询失败");
        }
    }
    /**
     *通过图表参数获取查询结果，并返回(指标/metric/文字块)
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDataByChartParams_metric", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "通过图表参数获取查询结果，并返回(指标)")
    public String getDataByChartParams_metric(HttpServletRequest request){
        try{
            //处理参数
            SearchConditions searchConditions = HttpRequestUtil.getSearchConditionsByRequest4Chart(request);
            //时间范围参数异常
            if(!Strings.isNullOrEmpty(searchConditions.getErrorInfo())){
                return Constant.failureMessage(searchConditions.getErrorInfo());
            }
            List<Map<String,Object>> result = iBIService.getMultiAggregationData_metric(searchConditions);
            return Constant.successData(JSONArray.fromObject(result).toString());
        }catch(ErrorInfoException e){
            return Constant.failureMessage(e.getMessage());
        }catch(ElasticsearchStatusException e){
            return handleElasticException(e);
        }catch(Exception e){
            log.error("数据可视化模块聚合查询失败："+e.getStackTrace().toString());
            return Constant.failureMessage("数据查询失败");
        }
    }
    /**
     *通过图表参数获取查询结果，并返回(折线图)
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDataByChartParams_line", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "通过图表参数获取查询结果，并返回(折线图)")
    public String getDataByChartParams_line(HttpServletRequest request){
        try{
            //处理参数
            SearchConditions searchConditions = HttpRequestUtil.getSearchConditionsByRequest4Chart(request);
            //时间范围参数异常
            if(!Strings.isNullOrEmpty(searchConditions.getErrorInfo())){
                return Constant.failureMessage(searchConditions.getErrorInfo());
            }
            Map<String,Object> result = iBIService.getMultiAggregationData_line(searchConditions);
            return Constant.successData(JSONArray.fromObject(result).toString());
        }catch(ErrorInfoException e){
            return Constant.failureMessage(e.getMessage());
        }catch(ElasticsearchStatusException e){
            return handleElasticException(e);
        }catch(Exception e){
            log.error("数据可视化模块聚合查询失败："+e.getStackTrace().toString());
            return Constant.failureMessage("数据查询失败");
        }
    }
    /**
     *通过图表参数获取查询结果，并返回(柱状图)
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDataByChartParams_bar", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "通过图表参数获取查询结果，并返回(柱状图)")
    public String getDataByChartParams_bar(HttpServletRequest request){
        try{
            //处理参数
            SearchConditions searchConditions = HttpRequestUtil.getSearchConditionsByRequest4Chart(request);
            //参数异常
            if(!Strings.isNullOrEmpty(searchConditions.getErrorInfo())){
                return Constant.failureMessage(searchConditions.getErrorInfo());
            }
            Map<String,Object> result = iBIService.getMultiAggregationData_bar(searchConditions);
            return Constant.successData(JSONArray.fromObject(result).toString());
        }catch(ErrorInfoException e){
            return Constant.failureMessage(e.getMessage());
        }catch(ElasticsearchStatusException e){
            return handleElasticException(e);
        }catch(Exception e){
            log.error("数据可视化模块聚合查询失败："+e.getStackTrace().toString());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     *通过图表参数获取查询结果，并返回(动态表格)
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDataByParams_dynamicTable", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "通过图表参数获取查询结果，并返回(动态表格)")
    public String getDataByParams_dynamicTable(HttpServletRequest request){
        try{
            //处理参数
            SearchConditions searchConditions = HttpRequestUtil.getSearchConditionsByRequest4DynamicTable(request);
            //时间范围参数异常
            if(!Strings.isNullOrEmpty(searchConditions.getErrorInfo())){
                return Constant.failureMessage(searchConditions.getErrorInfo());
            }
            String result = iBIService.getSearchData_dynamicTable(searchConditions);
            return Constant.successData(result);
        }catch(ErrorInfoException e){
            return Constant.failureMessage(e.getMessage());
        }catch(ElasticsearchStatusException e){
            return handleElasticException(e);
        }catch(Exception e){
            log.error("数据可视化模块查询失败："+e.getStackTrace().toString());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * 保存图表
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/saveVisualization", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "图表保存")
    public String saveVisualization(HttpServletRequest request, Visualization visual, HttpSession session){
        try{
            //另存为
            String isSaveAs = request.getParameter("isSaveAs");
            //用户新建保存或者编辑时另存，需要检测标题是否重复
            if(null!=isSaveAs&&"true".equals(isSaveAs)){
                if(iBIService.isVisualizationExists(visual.getTitle(),configProperty.getEs_hsdata_index())){
                    return Constant.failureMessage("标题名称重复，请修改！");
                }
            }
            //根据master用户确定表格是否可编辑，master不可编辑/删除，其他用的的可编辑/删除
            if("master".equals(session.getAttribute(Constant.SESSION_USERNAME).toString())){
                visual.setEditable(false);
                visual.setDeletable(false);
            }else{
                visual.setEditable(true);
                visual.setDeletable(true);
            }
            //visual.setId(UUID.randomUUID().toString());
            DocWriteResponse.Result result = iBIService.saveVisualization(visual,configProperty.getEs_hsdata_index());
            if (result == DocWriteResponse.Result.CREATED) {
                return Constant.successMessage("数据保存成功");
            } else if (result == DocWriteResponse.Result.UPDATED) {
                return Constant.successMessage("数据更新成功");
            }else{
                return Constant.failureMessage("数据操作成功");
            }
        }catch(Exception e){
            log.error("图表保存"+e.getMessage());
            return Constant.failureMessage("数据操作失败");
        }
    }
    /**
     * 保存仪表盘
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/saveDashboard", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "仪表盘保存")
    public String saveDashboard(HttpServletRequest request, Dashboard dashboard, HttpSession session){
        try{
            String isSaveAs = request.getParameter("isSaveAs");
            //用户新建保存或者编辑时另存，需要检测标题是否重复
            if(null!=isSaveAs&&"true".equals(isSaveAs)){
                if(iBIService.isDashboardExists(dashboard.getTitle(),configProperty.getEs_hsdata_index())){
                    return Constant.failureMessage("标题名称重复，请修改！");
                }
            }
            if("master".equals(session.getAttribute(Constant.SESSION_USERNAME).toString())){
                dashboard.setEditable(false);
                dashboard.setDeletable(false);
            }else{
                dashboard.setEditable(true);
                dashboard.setDeletable(true);
            }
            //dashboard.setId(UUID.randomUUID().toString());
            DocWriteResponse.Result result = iBIService.saveDashboard(dashboard,configProperty.getEs_hsdata_index());
            if (result == DocWriteResponse.Result.CREATED) {
                return Constant.successMessage("数据保存成功");
            } else if (result == DocWriteResponse.Result.UPDATED) {
                return Constant.successMessage("数据更新成功");
            }else{
                return Constant.failureMessage("数据操作成功");
            }
        }catch(Exception e){
            log.error("仪表盘保存"+e.getMessage());
            return Constant.failureMessage("数据操作失败");
        }
    }
    /**
     * 获取图表列表
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getVisualizations", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取图表列表")
    public String getVisualizations(HttpServletRequest request,HttpSession session){
        try{
            String result = iBIService.getVisualizations(configProperty.getEs_hsdata_index(),session);
            return Constant.successData(result);
        }catch(Exception e){
            log.error("获取图表列表"+e.getMessage());
            return Constant.successData(null);
        }
    }
    /**
     * 获取仪表盘
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDashboards", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取仪表盘列表")
    public String getDashboards(HttpServletRequest request,HttpSession session){
        try{
            String result = iBIService.getDashboards(configProperty.getEs_hsdata_index(),session);
            return Constant.successData(result);
        }catch(Exception e){
            log.error("获取仪表盘列表"+e.getMessage());
            return Constant.successData(null);
        }
    }
    /**
     * 获取图表的详情
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getVisualizationById", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取图表详情")
    public String getVisualizationById(HttpServletRequest request){
        try{
            String id = request.getParameter("id");
            String result = iBIService.getVisualizationById(id,configProperty.getEs_hsdata_index());
            return Constant.successData(result);
        }catch(Exception e){
            log.error("获取图表详情"+e.getMessage());
            return Constant.failureMessage("获取图表详情失败");
        }
    }
    /**
     * 获取图表的详情
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDashboardTemplates", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取dashboard的template列表")
    public String getDashboardTemplates(HttpServletRequest request,HttpSession session){
        try{
            String ids = request.getParameter("ids");
            String result = iBIService.getDashboardTemplates(ids,configProperty.getEs_hsdata_index());
            return Constant.successData(result);
        }catch(Exception e){
            log.error("获取dashboard的template列表失败"+e.getMessage());
            return Constant.failureMessage("获取Index Pattern失败!");//前端名称为index pattern，异常返回
        }
    }
    /**
     * 获取dashboard详情
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDashboardById", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取仪表盘详情")
    public String getDashboardById(HttpServletRequest request){
        try{
            String id = request.getParameter("id");
            String result = iBIService.getDashboardById(id,configProperty.getEs_hsdata_index());
            return Constant.successData(result);
        }catch(Exception e){
            log.error("获取仪表盘详情"+e.getMessage());
            return Constant.failureMessage("获取仪表盘详情失败");
        }
    }
    /**
     * 删除图表
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/deleteVisualizationById", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "删除图表")
    public String deleteVisualizationById(HttpServletRequest request){
        try{
            String id = request.getParameter("id");
            String result = iBIService.deleteVisualizationById(id,configProperty.getEs_hsdata_index());
            return Constant.successMessage(result);
        }catch(Exception e){
            log.error("删除图表"+e.getMessage());
            return Constant.failureMessage("删除图表失败");
        }
    }
    /**
     * 删除仪表盘
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/deleteDashboardById", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "删除仪表盘")
    public String deleteDashboardById(HttpServletRequest request){
        try{
            String id = request.getParameter("id");
            String result = iBIService.deleteDashboardById(id,configProperty.getEs_hsdata_index());
            return Constant.successMessage(result);
        }catch(Exception e){
            log.error("删除仪表盘"+e.getMessage());
            return Constant.failureMessage("删除仪表盘失败");
        }
    }
    /**
     * 计算聚合查询的  时间范围/时间间隔
     * 用于判定预估聚合桶数与配置项中的最大聚合桶数的大小
     * 小于：继续执行
     * 大于：返回提示信息
     * @param searchConditions
     * @return
     * @throws Exception 如果参数出现异常，统一在上游catch处理
     */
    private boolean getSearchBuckets(SearchConditions searchConditions) throws Exception {

        String startTime = searchConditions.getStartTime();
        String endTime = searchConditions.getEndTime();
        int intervalValue = searchConditions.getBucketList().get(0).getIntervalValue();
        String intervalType = searchConditions.getBucketList().get(0).getIntervalType();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateStart = formatter.parse(startTime);
        Date dateEnd = formatter.parse(endTime);
        //差值 秒
        long difValue = (dateEnd.getTime() - dateStart.getTime())/1000;
        //计算间隔 秒
        long intervalSeconds=1L;
        switch(intervalType.toUpperCase()){
            case "SECOND":
                intervalSeconds = intervalValue*1;
                break;
            case "MINUTE":
                intervalSeconds = intervalValue*60;
                break;
            case "HOURLY":
                intervalSeconds = intervalValue*60*60;
                break;
            case "DAILY":
                intervalSeconds = intervalValue*60*60*24;
                break;
            case "WEEKLY":
                intervalSeconds = intervalValue*60*60*24*7;
                break;
            case "MONTHLY":
                intervalSeconds = intervalValue*60*60*24*7*30;
                break;
            case "YEARLY":
                intervalSeconds = intervalValue*60*60*24*7*30*365;
                break;
            default:
                intervalSeconds = 1L;
                break;
        }
        //计算 时间范围/时间间隔
        long buckets = difValue/intervalSeconds;
        //计算出的桶数【小于】最大聚合桶数(查询es当前设置的值)，可执行下一步聚合查询
        if(buckets<iBIService.getClusterSearchMaxBuckets()){
            return true;
        }else{
            return false;
        }
    }

    /**
     * elastic异常处理机制
     * @param e
     * @return
     */
    private String handleElasticException(ElasticsearchStatusException e){
        /** 一级异常 ElasticsearchStatusException，通过catch获取*/

        /** 二级异常 search_phase_execution_exception*/
        //[Elasticsearch exception [type=search_phase_execution_exception, reason=]];
        String ES_ExceptionTypeSecondLevel = e.getDetailedMessage();
        /** 三级异常 too_many_buckets_exception */
        //nested: ElasticsearchException[Elasticsearch exception [type=too_many_buckets_exception, reason=Trying to create too many buckets.
        Throwable throwable = e.getCause();
        Throwable[] throwableList = e.getSuppressed();
        if(null!=throwable){
            String ES_ExceptionTypeThirdLevel = throwable.toString();
            if(ES_ExceptionTypeSecondLevel.indexOf("search_phase_execution_exception")>=0){
                if(ES_ExceptionTypeThirdLevel.indexOf("too_many_buckets_exception")>=0){
                    log.error("数据可视化模块，聚合异常，聚合桶数超出阈值（search.max_buckets）");
                    return Constant.failureMessage("数据查询失败!<br>请缩小时间范围或增大聚合间隔！");
                }else{
                    log.error("ElasticsearchStatusException->search_phase_execution_exception->"+e.getMessage());
                    return Constant.failureMessage("数据查询失败");
                }
            }else{
                log.error("ElasticsearchStatusException->非search_phase_execution_exception"+e.getMessage());
                return Constant.failureMessage("数据查询失败");
            }
        }else if(null!=throwableList){
            //返回的异常可能是一个数组
            String throwables = Arrays.toString(throwableList);
            if(throwables.indexOf("failed to parse date field")>=0){
                return Constant.failureMessage("数据查询失败,日期格式错误！");
            }else{
                return Constant.failureMessage("数据查询失败");
            }
        }else{
            return Constant.failureMessage("数据查询失败");
        }
    }
    /*******************sql部分*********************/
    @ResponseBody
    @RequestMapping(value="/getSqlOperators", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取sql运算符")
    public String getSqlOperators(){
        return Constant.successData(JSONArray.fromObject(Constant.SQL_OPERATOR).toString());
    }
    /**
     * 获取数据库所有表 ,jzLogAnalysis
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/showTables", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取数据库所有表")
    public String showTables(HttpServletRequest request){
        try{
            List<Map<String,String>> result = iBIService.showTables();
            return Constant.successData(JSONArray.fromObject(result).toString());
        }catch(Exception e){
            log.error("获取数据库所有表"+e.getMessage());
            return Constant.failureMessage("获取数据库表失败");
        }
    }
    /**
     * 获取数据库所有表
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/showColumns", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取数据库表的列信息")
    public String showColumns(HttpServletRequest request){
        try{
            String tableName = request.getParameter("tableName");
            List<Map<String,String>> result = iBIService.showColumns(tableName);
            return Constant.successData(JSONArray.fromObject(result).toString());
        }catch(Exception e){
            log.error("获取表的列失败"+e.getMessage());
            return Constant.failureMessage("获取数据库表的列信息失败");
        }
    }
    /**
     * 获取数据库所有表
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDataBySql", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "根据条件生成sql并查询结果")
    public String getDataBySql(HttpServletRequest request){
        try{
            SqlSearchConditions sqlSearchConditions = HttpRequestUtil.getSqlSearchConditionsByRequest(request);
            //参数异常
            if(!Strings.isNullOrEmpty(sqlSearchConditions.getErrorInfo())){
                return Constant.failureMessage(sqlSearchConditions.getErrorInfo());
            }
            Map<String, Object> result = iBIService.getDataByConditions(sqlSearchConditions);
            return Constant.successData(JSONArray.fromObject(result).toString());
        }catch(Exception e){
            log.error("数据查询失败"+e.getMessage());
            return Constant.failureMessage("数据查询失败");
        }
    }

    /**
     * 通过用户自定义索引名称查询其下的字段信息（mapping），并返回前端可用的数据格式
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getFieldsByCustomIndex", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取自定义index的字段信息")
    public String getFieldsByCustomIndex(HttpServletRequest request){
        //自定义索引名称
        String customIndex = request.getParameter("custom_index_name");
        try {
            List<MappingField> result = iBIService.getFieldsByCustomIndex(customIndex,ElasticConstant.ES_FIELDS_TYPE_SOURCE);
            return JSONArray.fromObject(result).toString();
        } catch (Exception e) {
            log.error("通过自定义index获取字段信息失败："+e.getMessage());
            return Constant.failureMessage("获取数据失败！");
        }
    }

    /**
     * 通过ES每条数据中的_id 查询该条数据的详情
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDetailsById", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "查询ES数据详情")
    public String getDetailsById(HttpServletRequest request){
        String _id = request.getParameter("_id");
        String _index = request.getParameter("_index");
        try {
            Map<String,String> result = iBIService.getDetailsById(_index,_id);
            return Constant.successData(JSONObject.fromObject(result).toString());
        } catch (Exception e) {
            log.error("获取详情失败！"+e.getMessage());
            return Constant.failureMessage("获取数据详情失败");
        }
    }
}

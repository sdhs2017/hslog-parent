package com.jz.bigdata.common.businessIntelligence.controller;

import com.hs.elsearch.entity.Bucket;
import com.hs.elsearch.entity.Metric;
import com.hs.elsearch.entity.VisualParam;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.businessIntelligence.entity.Dashboard;
import com.jz.bigdata.common.businessIntelligence.entity.MappingField;
import com.jz.bigdata.common.businessIntelligence.entity.Visualization;
import com.jz.bigdata.common.businessIntelligence.service.IBIService;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.DescribeLog;
import com.jz.bigdata.util.HttpRequestUtil;
import com.jz.bigdata.util.MapUtil;
import joptsimple.internal.Strings;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.DocWriteResponse;
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
@Controller
@RequestMapping("/BI")
public class BIController {
    private static Logger logger = Logger.getLogger(BIController.class);
    private final String biIndexName = "hsdata";
    @Resource(name = "BIService")
    private IBIService iBIService;
    @Resource(name ="configProperty")
    private ConfigProperty configProperty;
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
            //索引名称
            //String indexName = request.getParameter("indexName");
            //template名称
            String templateName = request.getParameter("template_name");
            //索引前缀
            String preIndexName = request.getParameter("pre_index_name");
            //索引后缀
            String suffixIndexName = request.getParameter("suffix_index_name");
            //
            List<MappingField> result = iBIService.getFieldByXAxisAggregation(templateName,preIndexName+suffixIndexName,agg);
            return JSONArray.fromObject(result).toString();
        }catch(Exception e){
            logger.error("通过X轴的聚合方式获取对应的列失败："+e.getStackTrace().toString());
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
            //索引名称
            //String indexName = request.getParameter("indexName");
            //template名称
            String templateName = request.getParameter("template_name");
            //索引前缀
            String preIndexName = request.getParameter("pre_index_name");
            //索引后缀
            String suffixIndexName = request.getParameter("suffix_index_name");
            List<MappingField> result = iBIService.getFieldByYAxisAggregation(templateName,preIndexName+suffixIndexName,agg);
            return JSONArray.fromObject(result).toString();
        }catch(Exception e){
            logger.error("通过Y轴的聚合方式获取对应的列失败："+e.getStackTrace().toString());
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
            VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
            //时间范围参数异常
            if(!Strings.isNullOrEmpty(params.getErrorInfo())){
                return Constant.failureMessage(params.getErrorInfo());
            }
            //组装要检索的index的名称： 前缀+后缀
            params.setIndex_name(params.getPre_index_name()+params.getSuffix_index_name());
            //查询条件处理,数据格式为json，{key:value,key:value}
            String queryParam = request.getParameter("queryParam");
            if(null!=queryParam){
                Map<String,String> paramMap = MapUtil.json2map(queryParam);
                params.setQueryParam(paramMap);
            }
            //如果x轴是时间聚合类型，进行计算
            if("Date Histogram".equals(params.getX_agg())){
                //计算聚合桶数，对超出设置search.max_buckets的，进行返回
                //计算方式，时间范围/时间间隔
                if(getSearchBuckets(params.getStartTime(),params.getEndTime(),params.getIntervalType(),params.getIntervalValue())){
                    //continue
                }else{
                    return Constant.failureMessage("数据查询失败!<br>请缩小时间范围或增大聚合间隔！");
                }
            }else{
                //continue
            }


            //判断index名称，如果是hslog*，则日期字段设置为logdate，如果是*beat*，则日期字段设置为@timestamp
            if(params.getIndex_name().indexOf("hslog")>=0){
                params.setDateField(Constant.PACKET_DATE_FIELD);
            }else if(params.getIndex_name().indexOf("beat")>=0){
                params.setDateField(Constant.BEAT_DATE_FIELD);
            }else{
                return Constant.failureMessage("数据源异常，请重新选择！");
            }
            /********临时处理******/
            //X轴
            //Bucket bucket = new Bucket(params.getX_agg(),params.getX_field(),params.getIntervalType(),params.getIntervalValue(),params.getSize(),params.getSort());
            //params.getBucketList().add(bucket);
            //Y轴
            //Metric metric = new Metric(params.getY_agg(),"COUNT".equals(params.getY_agg().toUpperCase())?params.getDateField():params.getY_field(),null);
            //params.getMetricList().add(metric);
            Map<String,Object> result = iBIService.getMultiAggregationDataSet(params);
            /*
            VisualParam vp = new VisualParam();
            Map<String, String[]> params = request.getParameterMap();
            vp.mapToBean(params);
            //组装要检索的index的名称： 前缀+后缀
            vp.setIndex_name(vp.getPre_index_name()+vp.getSuffix_index_name());
            //日期字段 //TODO 日期字段暂时写死
            vp.setDateField("@timestamp");
            //查询条件处理,数据格式为json，{key:value,key:value}
            String queryParam = request.getParameter("queryParam");
            if(null!=queryParam){
                Map<String,String> paramMap = MapUtil.json2map(queryParam);
                vp.setQueryParam(paramMap);
            }
            //如果x轴是时间聚合类型，进行计算
            if("Date".equals(vp.getX_agg())){
                //计算聚合桶数，对超出设置search.max_buckets的，进行返回
                //计算方式，时间范围/时间间隔
                if(getSearchBuckets(vp.getStartTime(),vp.getEndTime(),vp.getIntervalType(),vp.getIntervalValue())){
                    //continue
                }else{
                    return Constant.failureMessage("数据查询失败!<br>请缩小时间范围或增大聚合间隔！");
                }
            }else{
                //continue
            }

            //返回结果
            String result;
            //根据聚合方式调用不同的方法
            switch (vp.getY_agg()){
                case "Average":
                    result = iBIService.groupByThenAvg(vp);
                    break;
                case "Sum":
                    result = iBIService.groupByThenSum(vp);
                    break;
                case "Max":
                    result = iBIService.groupByThenMax(vp);
                    break;
                case "Min":
                    result = iBIService.groupByThenMin(vp);
                    break;
                case "Count":
                    result = iBIService.groupByThenCount(vp);
                    break;
                default:
                    result = Constant.failureMessage("聚合格式错误");
                    break;
            }
            */

            return Constant.successData(JSONArray.fromObject(result).toString());
        }catch(ElasticsearchStatusException e){
            /** 一级异常 ElasticsearchStatusException，通过catch获取*/

            /** 二级异常 search_phase_execution_exception*/
            //[Elasticsearch exception [type=search_phase_execution_exception, reason=]];
            String ES_ExceptionTypeSecondLevel = e.getDetailedMessage();
            /** 三级异常 too_many_buckets_exception */
            //nested: ElasticsearchException[Elasticsearch exception [type=too_many_buckets_exception, reason=Trying to create too many buckets.
            String ES_ExceptionTypeThirdLevel = e.getCause().toString();
            if(ES_ExceptionTypeSecondLevel.indexOf("search_phase_execution_exception")>=0){
                if(ES_ExceptionTypeThirdLevel.indexOf("too_many_buckets_exception")>=0){
                    logger.error("数据可视化模块，聚合异常，聚合桶数超出阈值（search.max_buckets）");
                    return Constant.failureMessage("数据查询失败!<br>请缩小时间范围或增大聚合间隔！");
                }else{
                    logger.error("ElasticsearchStatusException->search_phase_execution_exception->"+e.getMessage());
                    return Constant.failureMessage("数据查询失败");
                }
            }else{
                logger.error("ElasticsearchStatusException->非search_phase_execution_exception"+e.getMessage());
                return Constant.failureMessage("数据查询失败");
            }
        }catch(Exception e){
            logger.error("数据可视化模块聚合查询失败："+e.getStackTrace().toString());
            return Constant.failureMessage("数据查询失败");
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
                if(iBIService.isVisualizationExists(visual.getTitle(),biIndexName)){
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

            DocWriteResponse.Result result = iBIService.saveVisualization(visual,biIndexName);
            if (result == DocWriteResponse.Result.CREATED) {
                return Constant.successMessage("数据保存成功");
            } else if (result == DocWriteResponse.Result.UPDATED) {
                return Constant.successMessage("数据更新成功");
            }else{
                return Constant.failureMessage("数据操作成功");
            }
        }catch(Exception e){
            logger.error("图表保存"+e.getMessage());
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
                if(iBIService.isDashboardExists(dashboard.getTitle(),biIndexName)){
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
            DocWriteResponse.Result result = iBIService.saveDashboard(dashboard,biIndexName);
            if (result == DocWriteResponse.Result.CREATED) {
                return Constant.successMessage("数据保存成功");
            } else if (result == DocWriteResponse.Result.UPDATED) {
                return Constant.successMessage("数据更新成功");
            }else{
                return Constant.failureMessage("数据操作成功");
            }
        }catch(Exception e){
            logger.error("仪表盘保存"+e.getMessage());
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
            String result = iBIService.getVisualizations(biIndexName,session);
            return Constant.successData(result);
        }catch(Exception e){
            logger.error("获取图表列表"+e.getMessage());
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
            String result = iBIService.getDashboards(biIndexName,session);
            return Constant.successData(result);
        }catch(Exception e){
            logger.error("获取仪表盘列表"+e.getMessage());
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
            String result = iBIService.getVisualizationById(id,biIndexName);
            return Constant.successData(result);
        }catch(Exception e){
            logger.error("获取图表详情"+e.getMessage());
            return Constant.failureMessage("获取图表详情失败");
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
            String result = iBIService.getDashboardById(id,biIndexName);
            return Constant.successData(result);
        }catch(Exception e){
            logger.error("获取仪表盘详情"+e.getMessage());
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
            String result = iBIService.deleteVisualizationById(id,biIndexName);
            return Constant.successMessage(result);
        }catch(Exception e){
            logger.error("删除图表"+e.getMessage());
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
            String result = iBIService.deleteDashboardById(id,biIndexName);
            return Constant.successMessage(result);
        }catch(Exception e){
            logger.error("删除仪表盘"+e.getMessage());
            return Constant.failureMessage("删除仪表盘失败");
        }
    }

    /**
     * 计算聚合查询的  时间范围/时间间隔
     * 用于判定预估聚合桶数与配置项中的最大聚合桶数的大小
     * 小于：继续执行
     * 大于：返回提示信息
     * @param startTime 开始时间
     * @param endTime 截止时间
     * @param intervalType 间隔类型（分、小时、天）
     * @param intervalValue 间隔数值N（N分、N小时）
     * @return
     * @throws Exception 如果参数出现异常，统一在上游catch处理
     */
    private boolean getSearchBuckets(String startTime, String endTime, String intervalType, int intervalValue) throws Exception {
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

}

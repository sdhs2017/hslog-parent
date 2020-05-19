package com.jz.bigdata.common.businessIntelligence.controller;

import com.hs.elsearch.dao.biDao.entity.VisualParam;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.businessIntelligence.entity.Dashboard;
import com.jz.bigdata.common.businessIntelligence.entity.MappingField;
import com.jz.bigdata.common.businessIntelligence.entity.Visualization;
import com.jz.bigdata.common.businessIntelligence.service.IBIService;
import com.jz.bigdata.util.DescribeLog;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.elasticsearch.action.DocWriteResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
                JSONObject query = JSONObject.fromObject(queryParam);
                Iterator<String> iterator = query.keys();
                Map<String,String> paramMap = new HashMap<>();
                String key;
                while (iterator.hasNext()) {
                    key = iterator.next();
                    paramMap.put(key,query.getString(key));
                }
                vp.setQueryParam(paramMap);
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
            return Constant.successData(result);
        }catch(Exception e){
            logger.error("通过Y轴的聚合方式获取对应的列失败："+e.getStackTrace().toString());
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
    public String saveVisualization(HttpServletRequest request, Visualization visual){
        try{
            String isSaveAs = request.getParameter("isSaveAs");
            //用户新建保存或者编辑时另存，需要检测标题是否重复
            if(null!=isSaveAs&&"true".equals(isSaveAs)){
                if(iBIService.isVisualizationExists(visual.getTitle(),biIndexName)){
                    return Constant.failureMessage("标题名称重复，请修改！");
                }
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
    public String saveDashboard(HttpServletRequest request, Dashboard dashboard){
        try{
            String isSaveAs = request.getParameter("isSaveAs");
            //用户新建保存或者编辑时另存，需要检测标题是否重复
            if(null!=isSaveAs&&"true".equals(isSaveAs)){
                if(iBIService.isDashboardExists(dashboard.getTitle(),biIndexName)){
                    return Constant.failureMessage("标题名称重复，请修改！");
                }
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
    public String getVisualizations(HttpServletRequest request){
        try{
            String result = iBIService.getVisualizations(biIndexName);
            return Constant.successData(result);
        }catch(Exception e){
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
    public String getDashboards(HttpServletRequest request){
        try{
            String result = iBIService.getDashboards(biIndexName);
            return Constant.successData(result);
        }catch(Exception e){
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
            return Constant.failureMessage("删除仪表盘失败");
        }
    }

}

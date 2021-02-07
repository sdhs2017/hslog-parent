package com.jz.bigdata.common.alert.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hs.elsearch.entity.Bucket;
import com.hs.elsearch.entity.Metric;
import com.hs.elsearch.entity.QueryCondition;
import com.hs.elsearch.entity.SearchConditions;
import com.hs.elsearch.service.ISearchService;
import com.hs.elsearch.util.ElasticConstant;
import com.hs.elsearch.util.MappingField;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.alert.entity.Alert;
import com.jz.bigdata.common.alert.service.IAlertService;
import com.jz.bigdata.common.businessIntelligence.service.IBIService;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.DescribeLog;
import com.jz.bigdata.util.HttpRequestUtil;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.quartz.CronScheduleBuilder.cronSchedule;
@Slf4j
@Controller
@RequestMapping("/alert")
public class AlertController {
    @Resource(name = "AlertService")
    private IAlertService iAlertService;
    @Resource(name ="configProperty")
    private ConfigProperty configProperty;
    @Resource(name = "BIService")
    private IBIService iBIService;
    @Autowired
    protected ISearchService searchService;

    @ResponseBody
    @RequestMapping(value="/getAlertResult.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="获取告警查询结果")
    public String getAlertResult(HttpServletRequest request) {
        try{
            SearchConditions searchConditions = HttpRequestUtil.getSearchConditionsByRequest4Alert(request);
            //参数异常
            if(!Strings.isNullOrEmpty(searchConditions.getErrorInfo())){
                return Constant.failureMessage(searchConditions.getErrorInfo());
            }
            String result = this.iAlertService.getAlertResult(searchConditions);
            return Constant.successData(result);
        }catch(Exception e){
            log.error("数据查询失败："+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * 通过X轴的聚合方式获取fields,
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getFieldByAggResult", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "通过alert查询结果获取对应的列")
    public String getFieldByAggResult(HttpServletRequest request){
        try{
            //是查询原始数据（All）还是聚合结果（alertAgg）。
            String agg = request.getParameter("aggType");
            //template名称
            String templateName = request.getParameter("template_name");
            List<MappingField> result = iBIService.getMappingFieldByAggType(templateName,agg);
            return JSONArray.toJSONString(result);
        }catch(Exception e){
            log.error("通过alert查询结果获取对应的列失败："+e.getStackTrace().toString());
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
            return JSONArray.toJSONString(ElasticConstant.fieldTypeOperator_alert.get(fieldType));
        }catch(Exception e){
            log.error("通过字段类型获取对应的operation,失败："+e.getStackTrace().toString());
            return Constant.failureMessage("数据查询失败");
        }
    }

    /**
     * 创建告警任务
     */
    @ResponseBody
    @RequestMapping(value="/insert.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="创建告警任务")
    public String insert(HttpServletRequest request,Alert alert) {
        //cron表达式验证
        if(alert.getAlert_exec_type()!=null&&alert.getAlert_exec_type().equals("complex")){
            boolean cronExpressionFlag = CronExpression.isValidExpression(alert.getAlert_cron());
            if(!cronExpressionFlag){
                return Constant.failureMessage("cron表达式格式错误！");
            }
        }
        //TODO 时间戳问题提示
        try{
            boolean result = iAlertService.insert(alert);
            if(result){
                return Constant.successMessage("告警信息添加成功！");
            }else{
                return Constant.failureMessage("告警信息添加失败!");
            }
        }catch(Exception e){
            log.error("告警信息添加失败："+e.getMessage());
            return Constant.failureMessage("告警信息添加失败!");
        }
    }
    /**
     * 执行一次告警任务
     */
    @ResponseBody
    @RequestMapping(value="/startOnce.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="执行一次告警任务")
    public String startOnce(HttpServletRequest request) {
        String alert_id = request.getParameter("alert_id");
        try{
            boolean result = iAlertService.createQuartzAndStartOnce(alert_id);
            if(result){
                return Constant.successMessage("告警执行成功");
            }else{
                return Constant.failureMessage("告警执行失败!");
            }
        }catch (Exception e){
            log.error("告警执行失败"+e.getMessage());
            return Constant.failureMessage("告警执行失败!");
        }

    }

    /**
     * 验证cron表达式的正确性
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/cronValid.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="cron表达式验证")
    public String cronValid(HttpServletRequest request){
        String alert_cron = request.getParameter("alert_cron");
        boolean cronExpressionFlag = CronExpression.isValidExpression(alert_cron);
        if(!cronExpressionFlag){
            return Constant.failureMessage("执行周期：cron表达式格式错误！");
        }else{
            return Constant.successMessage();
        }
    }
    /**
     * 删除告警任务
     */
    @ResponseBody
    @RequestMapping(value="/delete.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="删除告警任务")
    public String delete(HttpServletRequest request){
        try{
            String alert_id = request.getParameter("alert_id");
            if(alert_id!=null){
                boolean result = iAlertService.delete(alert_id);
                if(result){
                    return Constant.successMessage("删除成功！");
                }else{
                    return Constant.failureMessage("删除失败！该告警信息不存在！");
                }
            }else{
                return Constant.failureMessage("参数异常！");
            }
        }catch(Exception e){
            log.error("删除告警信息异常"+e.getMessage());
            return Constant.failureMessage("删除告警信息失败！");
        }
    }
    /**
     * 删除告警任务，多选删除
     */
    @ResponseBody
    @RequestMapping(value="/deletes.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="删除告警任务")
    public String deletes(HttpServletRequest request){
        try{
            String alert_ids = request.getParameter("alert_ids");
            if(alert_ids!=null){
                boolean result = iAlertService.deletes(alert_ids);
                if(result){
                    return Constant.successMessage("删除成功！");
                }else{
                    return Constant.failureMessage("删除失败！该告警信息不存在！");
                }
            }else{
                return Constant.failureMessage("参数异常！");
            }
        }catch(Exception e){
            log.error("删除告警信息异常"+e.getMessage());
            return Constant.failureMessage("删除告警信息失败！");
        }
    }
    /**
     * 根据id获取告警详情
     */
    @ResponseBody
    @RequestMapping(value="/getAlertInfoById.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="获取告警信息详情")
    public String getAlertInfoById(HttpServletRequest request){

        try{
            String alert_id = request.getParameter("alert_id");
            Alert result = iAlertService.getAlertInfoById(alert_id);
            return Constant.successData(JSONObject.toJSON(result).toString());
        }catch (Exception e){
            return Constant.failureMessage("告警信息查询失败！");
        }
    }
    /**
     * 创建告警任务
     */
    @ResponseBody
    @RequestMapping(value="/update.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="更新告警任务")
    public String update(HttpServletRequest request,Alert alert,HttpSession session){
        try{
            //判定告警id是否存在
            if(com.mysql.jdbc.StringUtils.isNullOrEmpty(alert.getAlert_id())){
                return Constant.failureMessage("告警组信息修改失败！");
            }
            return this.iAlertService.updateById(alert,session);
        }catch(Exception e){
            return handleException(e);
        }
    }
    /**
     * 根据查询条件，查询告警基本信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getAlertInfoByCondition.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="查询告警信息")
    public String getAlertInfoByCondition(HttpServletRequest request,Alert alert, HttpSession session){
        try{
            //分页信息处理
            if(alert.getPageIndex()!=null&&alert.getPageSize()!=null){
                alert.setStartRecord((alert.getPageSize() * (alert.getPageIndex() - 1)));
            }
            Map<String, Object> result = iAlertService.getAlertInfoByCondition(alert);
            return Constant.successData(JSONArray.toJSON(result).toString());
        }catch (Exception e){
            return Constant.failureMessage("告警信息查询失败！");
        }
    }
    @ResponseBody
    @RequestMapping(value="/stopQuartz.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="暂停告警计划任务")
    public String stopQuartz(HttpServletRequest request){
        String alert_id = request.getParameter("alert_id");
        try{
            iAlertService.stopQuartz(alert_id);
            return Constant.successMessage("告警任务停止成功！");
        }catch (Exception e){
            log.error("计划任务停止异常："+e.getMessage());
            return Constant.failureMessage("告警任务停止失败！");
        }
    }
    @ResponseBody
    @RequestMapping(value="/startQuartz.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="启动告警计划任务")
    public String startQuartz(HttpServletRequest request){
        String alert_id = request.getParameter("alert_id");
        try{
            iAlertService.startQuartz(alert_id);
            return Constant.successMessage("告警任务启动成功！");
        }catch (Exception e){
            log.error("告警任务启动异常："+e.getMessage());
            return Constant.failureMessage("告警任务启动失败！");
        }
    }
    @ResponseBody
    @RequestMapping(value="/getAlertFireCount.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="某个时间段内告警事件count")
    public String getAlertFireCount(HttpServletRequest request){
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        //必须有起始和截至时间
        if(starttime!=null||endtime!=null){
            SearchConditions searchConditions = new SearchConditions();
            searchConditions.setIndex_name(Constant.ALERT_SEARCH_INDEX);
            searchConditions.setStartTime(starttime);
            searchConditions.setEndTime(endtime);
            searchConditions.setDateField("run_time");//运行事件字段作为日期字段，用于count
            //alert_fire 告警状态为true
            QueryCondition queryCondition = new QueryCondition("term","alert_fire",true,null);
            searchConditions.getQueryConditions().add(queryCondition);
            try {
                long result = searchService.getCountByConditionsQuery(searchConditions);
                return Constant.successMessage(result+"");
            } catch (Exception e) {
                log.error("执行ES查询失败："+e.getMessage());
                return Constant.failureMessage("数据查询失败");
            }
        }else{
            return Constant.failureMessage("时间参数错误");
        }
    }
    @ResponseBody
    @RequestMapping(value="/getAlertFireInfo.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="某个时间段内不同告警事件的count")
    public String getAlertFireInfo(HttpServletRequest request){
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        //必须有起始和截至时间
        if(starttime!=null||endtime!=null){
            SearchConditions searchConditions = new SearchConditions();
            searchConditions.setIndex_name(Constant.ALERT_SEARCH_INDEX);//index名称
            searchConditions.setStartTime(starttime);
            searchConditions.setEndTime(endtime);
            searchConditions.setDateField("run_time");//运行事件字段作为日期字段，用于count

            //alert_fire 告警状态为true
            QueryCondition queryCondition = new QueryCondition("term","alert_fire",true,null);
            searchConditions.getQueryConditions().add(queryCondition);
            //指标字段
            Metric metric = new Metric("count","run_time",null);
            searchConditions.getMetricList().add(metric);
            //聚合字段alert_id
            Bucket bucket = new Bucket();
            bucket.setAggType("terms");//term级聚合
            bucket.setField("alert_id.raw");//聚合字段
            bucket.setSize(999);//size，需要显示全部，默认为999
            bucket.setSort("desc");//倒叙
            searchConditions.getBucketList().add(bucket);
            try {
                List<Alert> result = iAlertService.getAlertFireInfo(searchConditions);
                return Constant.successData(JSONArray.toJSON(result).toString());
            } catch (Exception e) {
                log.error("执行ES查询失败："+e.getMessage());
                return Constant.failureMessage("数据查询失败");
            }
        }else{
            return Constant.failureMessage("时间参数错误");
        }
    }
    @ResponseBody
    @RequestMapping(value="/getAlertFireList.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="获取某个告警的告警状态为true的信息")
    public String getAlertFireList(HttpServletRequest request){
        String alert_id = request.getParameter("alert_id");
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        //分页
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if(alert_id!=null&&starttime!=null&&endtime!=null&&pageIndex!=null&&pageSize!=null){
            try{
                //分页参数处理
                Integer from = (Integer.parseInt(pageIndex)-1)*Integer.parseInt(pageSize);
                Integer size = Integer.parseInt(pageSize);
                Map<String,Object> result = iAlertService.getAlertList(alert_id,starttime,endtime,from,size,true);
                return Constant.successData(JSONArray.toJSON(result).toString());
            }catch (NumberFormatException e){
                log.error("分页参数异常"+e.getMessage());
                return Constant.failureMessage("分页参数错误，数据查询失败");
            }catch(Exception e){
                log.error("获取某个告警的告警状态为true的信息：失败："+e.getMessage());
                return Constant.failureMessage("数据查询失败！");
            }

        }else{
            return Constant.failureMessage("查询失败：参数异常！");
        }

    }
    @ResponseBody
    @RequestMapping(value="/getAlertList.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="获取某个告警的执行详情")
    public String getAlertList(HttpServletRequest request){
        String alert_id = request.getParameter("alert_id");
        //时间范围
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        //分页
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if(alert_id!=null&&starttime!=null&&endtime!=null&&pageIndex!=null&&pageSize!=null){

            try{
                //分页参数处理
                Integer from = (Integer.parseInt(pageIndex)-1)*Integer.parseInt(pageSize);
                Integer size = Integer.parseInt(pageSize);
                Map<String,Object> result = iAlertService.getAlertList(alert_id,starttime,endtime,from,size,null);
                return Constant.successData(JSONArray.toJSON(result).toString());
            }catch (NumberFormatException e){
                log.error("分页参数异常"+e.getMessage());
                return Constant.failureMessage("分页参数错误，数据查询失败");
            }catch(Exception e){
                log.error("获取某个告警的执行详情：失败："+e.getMessage());
                return Constant.failureMessage("数据查询失败！");
            }

        }else{
            return Constant.failureMessage("查询失败：参数异常！");
        }

    }

    /**
     * 告警信息增改异常处理，告警信息名称重复问题
     * @param e
     * @return
     */
    private String handleException(Exception e){
        Throwable throwable = e.getCause();
        Throwable[] throwableList = e.getSuppressed();
        if(null!=throwable){
            String throwableInfo = throwable.toString();
            if(throwableInfo.indexOf("UNIQUE_ALERT_NAME")>=0){
                log.error("告警信息名称重复："+e.getMessage());
                return Constant.failureMessage("告警信息名称重复！");
            }else{
                log.error("告警信息操作失败"+e.getMessage());
                return Constant.failureMessage("告警信息操作失败！");
            }
        }else if(null!=throwableList){
            //返回的异常可能是一个数组
            String throwables = Arrays.toString(throwableList);
            if(throwables.indexOf("UNIQUE_ALERT_NAME")>=0){
                log.error("告警信息名称重复："+e.getMessage());
                return Constant.failureMessage("告警信息名称重复！");
            }else{
                log.error("告警信息操作失败"+e.getMessage());
                return Constant.failureMessage("告警信息操作失败！");
            }
        }else{
            return Constant.failureMessage("告警信息操作失败！");
        }
    }
}

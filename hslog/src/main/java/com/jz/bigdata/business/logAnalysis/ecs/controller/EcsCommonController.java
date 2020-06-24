package com.jz.bigdata.business.logAnalysis.ecs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.hs.elsearch.entity.Bucket;
import com.hs.elsearch.entity.HttpRequestParams;
import com.hs.elsearch.entity.Metric;
import com.hs.elsearch.entity.VisualParam;
import com.jz.bigdata.business.logAnalysis.ecs.service.IecsService;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.alarm.service.IAlarmService;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.equipment.service.IEquipmentService;
import com.jz.bigdata.common.safeStrategy.service.ISafeStrategyService;
import com.jz.bigdata.roleauthority.user.service.IUserService;
import com.jz.bigdata.util.*;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 符合Elastic Common Schema (ECS)数据格式的查询
 * 公共部分
 */
@Controller
@RequestMapping("/ecsCommon")
public class EcsCommonController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name="ecsService")
    private IecsService ecsService;

    @Resource(name = "EquipmentService")
    private IEquipmentService equipmentService;

    @Resource(name ="configProperty")
    private ConfigProperty configProperty;

    @Resource(name ="SafeStrategyService")
    private ISafeStrategyService safeStrategyService;

    @Resource(name="AlarmService")
    private IAlarmService alarmService;

    @Resource(name ="UserService")
    private IUserService usersService;

    //@Autowired protected ClientTemplate clientTemplate;
    @Autowired
    protected ILogCrudDao logCrudDao;
    /**
     * 获取索引数据的数量
     * 用于首页日志总数展示、首页error日志数展示、单个资产报表中的日志总数、error数展示
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getIndicesCount",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="获取索引数据的数量")
    public String getIndicesCount(HttpServletRequest request) {
        //参数处理
        HttpRequestParams params = HttpRequestUtil.getParams(request);
        //返回结果
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        Map<String,Object> resultMap = new HashMap<>();
        /**
         *  error日志条数统计
         */
        try {
            long count = 0;
            Map<String, String> errorParam = new HashMap<>();
            errorParam.putAll(params.getQueryMap());
            errorParam.put("log.level", "error");
            count = ecsService.getCount(errorParam, null, null, configProperty.getEs_index());
            resultMap.put("indiceserror", count);
        } catch (Exception e) {
            logger.error("查询error日志数：失败！");
            logger.error(e.getMessage());
            resultMap.put("indiceserror", "获取异常");
        }

        /**
         * 所有日志总数据统计
         */
        try {
            long count = 0;
            Map<String, String> allParam = new HashMap<>();
            allParam.putAll(params.getQueryMap());
            count = ecsService.getCount(allParam, null, null, configProperty.getEs_index());
            resultMap.put("indices", count);
        } catch (Exception e) {
            logger.error("查询日志数：失败！");
            logger.error(e.getMessage());
            resultMap.put("indices", "获取异常");
        }
        resultList.add(resultMap);
        String result = JSONArray.fromObject(resultList).toString();
        logger.info("查询日志数：成功！");

        return result;
    }
    /**
     * @param request
     * 统计各时间段的日志数据量
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getLogCountGroupByTime_line", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="统计各时间段的日志数据量")
    public String getLogCountGroupByTime_line(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //index 和 日期字段初始化
        params.initDateFieldAndIndex(Constant.BEAT_DATE_FIELD,Constant.WINLOG_BEAT_INDEX);
        //X轴，日期，间隔1小时
        Bucket bucket = new Bucket("Date Histogram",Constant.BEAT_DATE_FIELD,"HOURLY",1,10,null);
        params.getBucketList().add(bucket);
        //Y轴，日志个数（count(@timestamp)）
        Metric metric = new Metric("count",Constant.BEAT_DATE_FIELD,"日志数");
        params.getMetricList().add(metric);
        try{
            Map<String, Object> result = ecsService.getMultiAggregationDataSet(params);
            return Constant.successData(JSONArray.fromObject(result).toString()) ;
        }catch(Exception e){
            logger.error("统计各时间段的日志数据量"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * old
     * @param request
     * 统计各时间段的日志数据量
     * @return
     */
    @ResponseBody
    @RequestMapping("/getLogCountGroupByTime")
    @DescribeLog(describe="统计各时间段的日志数据量")
    public List<Map<String, Object>> getCountGroupByTime(HttpServletRequest request) {
        String index = configProperty.getEs_index();
        //参数处理
        HttpRequestParams params = HttpRequestUtil.getParams(request);

        // 业务只查询范式化成功的日志
        //map.put("fields.failure","false");

        List<Map<String, Object>> list = new ArrayList<>();
        int size = 10;
        try {
            list = ecsService.getListGroupByTime(params.getStartTime(), params.getEndTime(), "@timestamp", size, params.getQueryMap(), index);
        } catch (Exception e) {
            logger.error("统计各时间段日志数据量：失败！");
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    /**
     * @param request
     * 统计各时间段的各事件数据量
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getCountGroupByTimeAndEvent_line", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="统计各时间段的各事件数据量")
    public String getCountGroupByTimeAndEvent_line(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //index 和 日期字段初始化
        params.initDateFieldAndIndex(Constant.BEAT_DATE_FIELD,Constant.WINLOG_BEAT_INDEX);
        //X轴，日期，间隔1小时
        Bucket bucket = new Bucket("Date Histogram",Constant.BEAT_DATE_FIELD,"HOURLY",1,10,null);
        params.getBucketList().add(bucket);
        //X轴  事件类别
        Bucket eventBucket = new Bucket("term","event.action",null,null,100,"desc");
        params.getBucketList().add(eventBucket);
        //Y轴，日志个数（count(@timestamp)）
        Metric metric = new Metric("count",Constant.BEAT_DATE_FIELD,"");
        params.getMetricList().add(metric);
        try{
            Map<String, Object> result = ecsService.getMultiAggregationDataSet(params);
            return Constant.successData(JSONArray.fromObject(result).toString()) ;
        }catch(Exception e){
            logger.error("统计各时间段的各事件数据量"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * old
     * @param request
     * 统计各时间段的各事件数据量
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getCountGroupByTimeAndEvent",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="统计各时间段的各事件数据量")
    public String getCountGroupByTimeAndEvent(HttpServletRequest request) {
        String index = configProperty.getEs_index();
        //参数处理
        HttpRequestParams params = HttpRequestUtil.getParams(request);
        // 业务只查询范式化成功的日志
        //map.put("fields.failure","false");
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, List<Map<String, Object>>>  result = new HashMap<>();
        int size = 10;
        Set<String> events = new HashSet<>();
        try {
            list = ecsService.getListGroupByTimeAndEvent(params.getStartTime(), params.getEndTime(), "@timestamp","terms", "event.action", size, params.getQueryMap(), index);
            /**
             * 遍历list获取所有的事件名称存入Set
             */
            for (Map tmpmap : list){
                List<Map<String, Object>> tmplist = (List<Map<String, Object>>) tmpmap.get("value");
                for (Map<String,Object> submap : tmplist){
                    events.addAll(submap.keySet());
                }
            }
            /**
             * 遍历Set事件名称
             */
            for (String event_name : events){
                List<Map<String, Object>> eventlist = new ArrayList<>();
                /**
                 * 遍历service层返回的list，构建返回前端的数据
                 * 一个event的前端结构应该是{"event_name":event_list(该事件在每个小时发生的次数)}
                 * event_list结构，list中是每个小时的map数据：[{hour:0,count:事件发生次数},{hour:1,count:事件发生次数},...,{hour:23,count:事件发生次数}]
                 */
                for (Map tmpmap : list){
                    // event_list结构中的map
                    Map<String, Object> sub = new HashMap<>();
                    // 时间
                    sub.put("hour",tmpmap.get("hour"));
                    // 获取service中封装的value，是一个List<Map<String, Object>>，map的key是event_name,value是event发生的次数
                    List<Map<String, Object>> valuelist = (ArrayList)tmpmap.get("value");
                    /**
                     * 判断valuelist不为null且不为空，原因是某个时间段会出现一个事件都不存在的情况
                     * true的情况进行遍历list
                     * false设置count为0
                     */
                    if (valuelist!=null&&!valuelist.isEmpty()){
                        /**
                         * 遍历valuelist，获取event_name和event发生次数
                         */
                        for (Map<String,Object> valuemap : valuelist){
                            // 如果没有匹配到设置为0，匹配到的情况下以实际数据为准，然后跳出当前循环
                            sub.put("count",valuemap.get(event_name)!=null?valuemap.get(event_name):0);
                            if (valuemap.get(event_name)!=null){
                                break;
                            }
                        }
                    }else {
                        sub.put("count",0);
                    }
                    eventlist.add(sub);
                }
                result.put(event_name,eventlist);
            }
        } catch (Exception e) {
            logger.error("统计各时间段的各事件数据量：失败！");
            logger.error(e.getMessage());
            e.printStackTrace();
        }


        return JSONArray.fromObject(result).toString();
    }
    /**
     * 精确查询日志事件
     * @param request
     * @author jiyourui
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getEventListByBlend",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="精确查询日志事件")
    public String getEventListByBlend(HttpServletRequest request, HttpSession session) {
        // receive parameter
        String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);
        Object userrole = session.getAttribute(Constant.SESSION_USERROLE);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new ConcurrentHashMap<>();

        try {
            map = MapUtil.removeMapEmptyValue(mapper.readValue(hsData, Map.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(map);
        Object pageo = map.get("page");
        Object sizeo = map.get("size");

        map.remove("page");
        map.remove("size");

        String page = pageo.toString();
        String size = sizeo.toString();

        // 提出参数的时间查询条件
        String starttime = "";
        String endtime = "";
        if (map.get("starttime")!=null) {
            Object start = map.get("starttime");
            starttime = start.toString();
            map.remove("starttime");
        }
        if (map.get("endtime")!=null) {
            Object end = map.get("endtime");
            endtime = end.toString();
            map.remove("endtime");
        }
        // 业务只查询范式化成功的日志
        //map.put("fields.failure","false");
        // 不为空字段设置，key固定，value是不为null的字段event.action，多个字段逗号相隔
        map.put("exists", "event.action");

        // 判断是否是非管理员角色，是传入参数用户id
        if (!userrole.equals(ContextRoles.MANAGEMENT)){
            map.put(ContextRoles.ECS_USERID,session.getAttribute(Constant.SESSION_USERID).toString());
        }

        List<Map<String, Object>> list = new ArrayList<>();

        try {
            list = ecsService.getLogListByBlend(map,starttime,endtime,page,size,configProperty.getEs_index());
        } catch (Exception e) {
            logger.error("精确查询日志事件：失败！");
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        Map<String, Object> allmap = new HashMap<>();
        allmap = list.get(0);
        list.remove(0);
        allmap.put("list", list);

        String result = JSONArray.fromObject(allmap).toString();
        String replace=result.replace("\\\\005", "<br/>");

        return replace;
    }

    /**
     * old
     * 通过设备id获取该设备日志列表
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getLogListByEquipment_old.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="条件获取设备日志数据")
    public String getLogListByEquipment_old(HttpServletRequest request, Equipment equipment) {

        String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new ConcurrentHashMap<>();

        try {
            map = MapUtil.removeMapEmptyValue(mapper.readValue(hsData, Map.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Object pageo = map.get("page");
        Object sizeo = map.get("size");

        map.remove("page");
        map.remove("size");

        String page = pageo.toString();
        String size = sizeo.toString();

        // 提出参数的时间查询条件
        String starttime = "";
        String endtime = "";
        if (map.get("starttime")!=null) {
            Object start = map.get("starttime");
            starttime = start.toString();
            map.remove("starttime");
        }
        if (map.get("endtime")!=null) {
            Object end = map.get("endtime");
            endtime = end.toString();
            map.remove("endtime");
        }
        // 业务只查询范式化成功的日志
        //map.put("fields.failure","false");

        List<Map<String, Object>> list = null;
        try {
            list = ecsService.getLogListByBlend(map,starttime,endtime,page,size,configProperty.getEs_index());
        } catch (Exception e) {
            logger.error("资产日志：查询失败");
            e.printStackTrace();
        }

        Map<String, Object> allmap = new HashMap<>();
        allmap = list.get(0);
        list.remove(0);
        allmap.put("list", list);

        String result = JSONArray.fromObject(allmap).toString();
        String replace=result.replace("\\\\005", "<br/>");
        logger.info("资产日志：查询成功");
        return replace;



    }
    /**
     * @param request
     * 统计各个日志级别的数据量
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getCountGroupByLogLevel_barAndPie", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="读取日志级别数据量")
    public String getCountGroupByLogLevel_barAndPie(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //index 和 日期字段初始化
        params.initDateFieldAndIndex(Constant.BEAT_DATE_FIELD,Constant.WINLOG_BEAT_INDEX);
        //X轴，日志级别（log.level）
        Bucket bucket = new Bucket("term","log.level",null,null,10,"desc");
        params.getBucketList().add(bucket);
        //Y轴，日志个数（count(@timestamp)）
        Metric metric = new Metric("count",Constant.BEAT_DATE_FIELD,"日志数");
        params.getMetricList().add(metric);
        try{
            Map<String, Object> result = ecsService.getMultiAggregationDataSet(params);
            return Constant.successData(JSONArray.fromObject(result).toString()) ;
        }catch(Exception e){
            logger.error("读取日志级别数据量"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * @param request
     * 统计各个日志级别的数据量
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getCountGroupByEventAction_barAndPie", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="读取日志级别数据量")
    public String getCountGroupByEventAction_barAndPie(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //index 和 日期字段初始化
        params.initDateFieldAndIndex(Constant.BEAT_DATE_FIELD,Constant.WINLOG_BEAT_INDEX);
        //X轴，日志级别（log.level）
        Bucket bucket = new Bucket("term","event.action",null,null,10,"desc");
        params.getBucketList().add(bucket);
        //Y轴，日志个数（count(@timestamp)）
        Metric metric = new Metric("count",Constant.BEAT_DATE_FIELD,"日志数");
        params.getMetricList().add(metric);
        try{
            Map<String, Object> result = ecsService.getMultiAggregationDataSet(params);
            return Constant.successData(JSONArray.fromObject(result).toString()) ;
        }catch(Exception e){
            logger.error("读取日志级别数据量"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * old
     * @param request
     * 统计各个日志级别的数据量
     * @return
     */
    @ResponseBody
    @RequestMapping("/getCountGroupByParam")
    @DescribeLog(describe="读取日志级别数据量")
    public List<Map<String, Object>> getCountGroupByParam(HttpServletRequest request) {

        String index = configProperty.getEs_index();
        //参数处理
        HttpRequestParams params = HttpRequestUtil.getParams(request);

        // 业务只查询范式化成功的日志
        //map.put("fields.failure","false");
        // 聚合返回的数据条数，在目前的产品中日志级别总共有8个，设置为10个保证8个正常显示
        int size = 10;
        List<Map<String, Object>> list = null;
        try {
            list = ecsService.groupByThenCount(params.getStartTime(),params.getEndTime(),params.getGroupField(),size,params.getQueryMap(),index);
        } catch (Exception e) {
            logger.error("统计日志级别数据量：失败！");
            e.printStackTrace();
        }

        return list;
    }
    /**
     * 通过设备id获取该资产日志列表（数据格式:ecs）
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getLogListByEquipment.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="条件获取资产日志数据")
    public String getLogListByEquipment(HttpServletRequest request, Equipment equipment) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //分页参数
        String page = request.getParameter("page");
        String size = request.getParameter("size");
        List<Map<String, Object>> list = null;
        try {
            list = ecsService.getLogListByBlend(params.getQueryParam(),params.getStartTime(),params.getEndTime(),page,size,Constant.WINLOG_BEAT_INDEX);
        } catch (Exception e) {
            logger.error("资产日志：查询失败");
        }

        Map<String, Object> allmap = new HashMap<>();
        allmap = list.get(0);
        list.remove(0);
        allmap.put("list", list);

        String result = JSONArray.fromObject(allmap).toString();
        String replace=result.replace("\\\\005", "<br/>");
        logger.info("资产日志：查询成功");
        return replace;
    }
    /**
     * 精确查询日志事件（数据格式:ecs）
     * @param request
     * @author jiyourui
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getListByBlend",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="精确查询日志事件")
    public String getListByBlend(HttpServletRequest request, HttpSession session) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //分页参数
        String page = request.getParameter("page");
        String size = request.getParameter("size");
        List<Map<String, Object>> list = new ArrayList<>();

        try {
            list = ecsService.getLogListByBlend(params.getQueryParam(),params.getStartTime(),params.getEndTime(),page,size,Constant.WINLOG_BEAT_INDEX);
        } catch (Exception e) {
            logger.error("精确查询日志事件：失败！");
            logger.error(e.getMessage());
        }

        Map<String, Object> allmap = new HashMap<>();
        allmap = list.get(0);
        list.remove(0);
        allmap.put("list", list);

        String result = JSONArray.fromObject(allmap).toString();
        String replace=result.replace("\\\\005", "<br/>");

        return replace;
    }
}

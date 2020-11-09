package com.jz.bigdata.util;

import com.google.gson.Gson;
import com.hs.elsearch.entity.*;
import com.hs.elsearch.util.HSDateUtil;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.businessIntelligence.entity.SqlSearchColumn;
import com.jz.bigdata.common.businessIntelligence.entity.SqlSearchConditions;
import com.jz.bigdata.common.businessIntelligence.entity.SqlSearchWhere;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.joda.time.DateTime;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理controller request的工具类
 */
@Slf4j
public class HttpRequestUtil {
    //数据转换时用到的gson对象
    private final static Gson gson = new Gson();

    /**
     * 处理参数 用于对报表的参数处理
     * @param request
     * @return
     */
    public static VisualParam getVisualParamByRequest(HttpServletRequest request){
        VisualParam visualParam = new VisualParam();
        Map<String, String[]> params = request.getParameterMap();
        //通过bean对应，直接处理部分参数，包括intervalValue、intervalType等等
        visualParam.mapToBean(params);
        //获取起始和截至时间
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        String last = request.getParameter("last");
        //last值不为空
        if(!Strings.isNullOrEmpty(last)){
            if(Strings.isNullOrEmpty(starttime)&&Strings.isNullOrEmpty(endtime)){
                //起始、截止时间为空，last不为空,计算起始和截止时间
                Map<String,String> map = getStartEndTimeByLast(last);
                //赋值，如果起始和截止时间能正常获取
                if(map.size()==2){
                    visualParam.setStartTime(map.get("starttime"));
                    visualParam.setEndTime(map.get("endtime"));
                }else{
                    //其他情况判定为时间范围数据异常，返回前端显示
                    visualParam.setErrorInfo("请重新设定时间范围!");
                }
            }else{
                //既有起始或截止时间，又有last，参数异常
                visualParam.setErrorInfo("请重新设定时间范围!");
            }
        }else{//last值为空
            //起始和截止时间都不为空,并且格式为yyyy-MM-dd HH:mm:ss
            if(!Strings.isNullOrEmpty(starttime)&&!Strings.isNullOrEmpty(endtime)&&starttime.matches("\\d{4}-\\d{2}-\\d{2}\\s{1}\\d{2}:\\d{2}:\\d{2}")&&endtime.matches("\\d{4}-\\d{2}-\\d{2}\\s{1}\\d{2}:\\d{2}:\\d{2}")){
                visualParam.setStartTime(starttime);
                visualParam.setEndTime(endtime);
            }else{
                //起始或截止时间存在为空的，也属于参数异常状态
                visualParam.setErrorInfo("请重新设定时间范围!");
            }
        }
        //查询条件处理,数据格式为json，{key:value,key:value}
        String queryParam = request.getParameter("queryParam");
        if(null!=queryParam){
            Map<String,String> paramMap = MapUtil.json2map(queryParam);
            //聚合后端用到的参数处理魔石
            for(Map.Entry<String,String> entity:paramMap.entrySet()){
                QueryCondition qc = new QueryCondition("term",entity.getKey(),entity.getValue(),"");
                visualParam.getQueryConditions().add(qc);
            }
            //原后端用到的参数处理
            visualParam.setQueryParam(paramMap);
        }
        //处理bucket聚合条件（X轴）
        String buckets = request.getParameter("buckets");
        if(null!=buckets){
            //buckets参数包含多个bucket对象
            JSONArray json = JSONArray.fromObject(buckets);
            //遍历
            for(Object beanObj:json.toArray()){
                //转bean
                Bucket bucket = JavaBeanUtil.mapToBean((Map)JSONObject.fromObject(beanObj), Bucket.class);
                //转换成功时，写入参数对象中
                if(null!=bucket){
                    //如果聚合类型是range。需要对参数进行再处理
                    if(bucket.getAggType().indexOf("Range")>=0){
                        JSONArray rangeArray = JSONObject.fromObject(beanObj).getJSONArray("ranges");
                        for(Object rangeObj : rangeArray){
                            Map<String,Object> rangeMap = (Map<String,Object>)rangeObj;
                            bucket.getRanges().add(rangeMap);
                        }
                    }
                    visualParam.getBucketList().add(bucket);
                }
            }
        }else{}
        //处理metric聚合条件（Y轴）
        String metrics = request.getParameter("metrics");
        if(null!=metrics){
            //buckets参数包含多个bucket对象
            JSONArray json = JSONArray.fromObject(metrics);
            //遍历
            for(Object beanObj:json.toArray()){
                //转bean
                Metric metric = JavaBeanUtil.mapToBean((Map)JSONObject.fromObject(beanObj), Metric.class);
                //转换成功时，写入参数对象中
                if(null!=metric){
                    visualParam.getMetricList().add(metric);
                }
            }
        }else{}
        return visualParam;
    }

    /**
     * 处理参数 用于对报表的参数处理,服务于动态表格
     * @param request
     * @return
     */
    public static SearchConditions getSearchConditionsByRequest4DynamicTable(HttpServletRequest request){
        SearchConditions searchConditions = getSearchConditionsByRequest_basic(request);
        try{
            //动态表格 ，数据为es类型时，对字段信息数据的处理
            String es_columns = request.getParameter("es_columns");
            if(null!=es_columns){
                //es_columns参数包含多个column对象
                JSONArray json = JSONArray.fromObject(es_columns);
                //遍历
                for(Object beanObj:json.toArray()){
                    //转bean
                    DataTableColumn column = JavaBeanUtil.mapToBean((Map)JSONObject.fromObject(beanObj), DataTableColumn.class);
                    //转换成功时，写入参数对象中
                    if(null!=column){
                        searchConditions.getDataTableColumns().add(column);
                    }
                }
            }
            //分页处理
            Integer page = searchConditions.getPage();
            Integer page_size = searchConditions.getPage_size();
            if (page!=null&&page_size!=null) {
                searchConditions.setFrom((page-1)*page_size);
            }
        }catch (Exception e){
            searchConditions.setErrorInfo("查询参数异常，请重新设置！");
        }
        return searchConditions;
    }

    /**
     * 告警模块的参数处理
     * @param request
     * @return
     */
    public static SearchConditions getSearchConditionsByRequest4Alert(HttpServletRequest request){
        SearchConditions searchConditions = new SearchConditions();
        try {
            Map<String, String[]> params = request.getParameterMap();
            //通过bean对应，直接处理部分参数
            searchConditions.mapToBean(params);
            //时间范围
            //Map<String,String> map = getStartEndTimeByLast(configProperty.getAlert_default_area());
            Map<String,String> map = getTimeAreaByAlertParam(request.getParameter("alert_exec_type"),request.getParameter("alert_time_cycle_num"),request.getParameter("alert_time_cycle_type"),request.getParameter("alert_time_type"),request.getParameter("alert_time"));
            //赋值，如果起始和截止时间能正常获取
            if(map.size()==2){
                searchConditions.setStartTime(map.get("starttime"));
                searchConditions.setEndTime(map.get("endtime"));
            }else{
                //其他情况判定为时间范围数据异常，返回前端显示
                searchConditions.setErrorInfo("时间查询参数异常！");
            }
            //组装要检索的index的名称： 前缀+后缀
            searchConditions.setIndex_name(searchConditions.getPre_index_name()+searchConditions.getSuffix_index_name());
            //判断index名称，如果是hslog*，则日期字段设置为logdate，如果是*beat*，则日期字段设置为@timestamp
            if(searchConditions.getIndex_name().indexOf("packet-")>=0){
                searchConditions.setDateField(Constant.PACKET_DATE_FIELD);
            }else if(searchConditions.getIndex_name().indexOf("beat")>=0){
                searchConditions.setDateField(Constant.BEAT_DATE_FIELD);
            }else{
                searchConditions.setErrorInfo("数据源异常，请重新选择数据源!");
            }
            //处理bucket聚合条件（X轴）
            String buckets = request.getParameter("alert_search_bucket");
            if(null!=buckets&&!"".equals(buckets)){
                //buckets参数包含多个bucket对象
                JSONArray json = JSONArray.fromObject(buckets);
                //遍历
                for(Object beanObj:json.toArray()){
                    //转bean
                    Bucket bucket = JavaBeanUtil.mapToBean((Map)JSONObject.fromObject(beanObj), Bucket.class);
                    //转换成功时，写入参数对象中
                    if(null!=bucket){
                        //如果聚合类型是range。需要对参数进行再处理
                        if(bucket.getAggType().indexOf("Range")>=0){
                            JSONArray rangeArray = JSONObject.fromObject(beanObj).getJSONArray("ranges");
                            for(Object rangeObj : rangeArray){
                                Map<String,Object> rangeMap = (Map<String,Object>)rangeObj;
                                bucket.getRanges().add(rangeMap);
                            }
                        }
                        searchConditions.getBucketList().add(bucket);
                    }
                }

            }
            //处理metric聚合条件（Y轴）
            String metrics = request.getParameter("alert_search_metric");
            if(null!=metrics&&!"".equals(metrics)){
                //buckets参数包含多个bucket对象
                JSONArray json = JSONArray.fromObject(metrics);
                //遍历
                for(Object beanObj:json.toArray()){
                    //转bean
                    Metric metric = JavaBeanUtil.mapToBean((Map)JSONObject.fromObject(beanObj), Metric.class);
                    //转换成功时，写入参数对象中
                    if(null!=metric){
                        searchConditions.getMetricList().add(metric);
                    }
                }

            }
            //---------处理filter--------
            String filters_visual = request.getParameter("alert_search_filters");
            if(null!=filters_visual&&!"".equals(filters_visual)){
                //filters中包含多个filter
                JSONArray json = JSONArray.fromObject(filters_visual);
                //遍历
                for(Object beanObj:json.toArray()){
                    //转bean
                    Filter filter = JavaBeanUtil.mapToBean((Map)JSONObject.fromObject(beanObj), Filter.class);
                    //转换成功时，写入参数对象中
                    if(null!=filter){
                        searchConditions.getFilters_alert().add(filter);
                    }
                }
            }
            //------------设置聚合结果size参数---------
            //如果metric和bucket都为空，则为查询原始数据，size设置为10，否则为聚合查询，size设置为0
            if(metrics!=null||buckets!=null){
                if(JSONArray.fromObject(metrics).size()>0||JSONArray.fromObject(buckets).size()>0){
                    searchConditions.setSize(0);
                }else{
                    searchConditions.setSize(10);
                }
            }else{
                searchConditions.setSize(10);
            }
        }catch (Exception e){
            searchConditions.setErrorInfo("查询参数异常，请重新设置！");
        }
        return searchConditions;
    }

    /**
     * 根据alert的时间参数获取起始和截止时间
     * @param alert_exec_type 时间类型 simple/complex
     * @param alert_time_cycle_num simple：执行周期数值
     * @param alert_time_cycle_type simple：执行周期类型（second minute hour）
     * @param alert_time_type complex：最近X分钟/小时    or  精确时间
     * @param alert_time complex ： 时间值  1-days  / 2020-01-01 00:00:00,2020-01-01 00:01:00
     * @return
     */
    public static Map<String,String> getTimeAreaByAlertParam(String alert_exec_type,String alert_time_cycle_num,String alert_time_cycle_type,String alert_time_type,String alert_time){
        Map<String,String> timeArea = new HashMap<>();
        if(alert_exec_type!=null){
            //如果选择的是普通类型时间
            if("simple".equals(alert_exec_type)){
                int alert_time_cycle_num_int = Integer.parseInt(alert_time_cycle_num);
                String endTime = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
                timeArea.put("endtime",endTime);
                switch(alert_time_cycle_type){
                    case "second":
                        //计算起始时间
                        timeArea.put("starttime",DateTime.now().minusSeconds(alert_time_cycle_num_int).toString("yyyy-MM-dd HH:mm:ss"));
                        break;
                    case "minute":
                        //计算起始时间
                        timeArea.put("starttime",DateTime.now().minusMinutes(alert_time_cycle_num_int).toString("yyyy-MM-dd HH:mm:ss"));
                        break;
                    case "hour":
                        //计算起始时间
                        timeArea.put("starttime",DateTime.now().minusHours(alert_time_cycle_num_int).toString("yyyy-MM-dd HH:mm:ss"));
                        break;
                }
            }else if("complex".equals(alert_exec_type)) {
                if(alert_time_type!=null){
                    if("last".equals(alert_time_type)){
                        //工具类获取时间范围
                        Map<String,String> timeMap = HttpRequestUtil.getStartEndTimeByLast(alert_time);
                        timeArea.putAll(timeMap);
                    }else if("range".equals(alert_time_type)){
                        //精确时间的起始和截止时间以逗号隔开
                        String[] timeArray = alert_time.split(",");
                        if(timeArray.length==2){
                            timeArea.put("starttime",timeArray[0]);
                            timeArea.put("endtime",timeArray[1]);
                        }
                    }else{
                        log.error("时间参数异常，alert_time_type："+alert_time_type);
                    }
                }
            }else{
                //参数异常，不做处理
                log.error("时间参数异常，alert_exec_type："+alert_exec_type);
            }
        }
        return timeArea;
    }
    /**
     * 处理参数 用于对报表的参数处理,服务于图表模块
     * @param request
     * @return
     */
    public static SearchConditions getSearchConditionsByRequest4Chart(HttpServletRequest request){
        SearchConditions searchConditions = getSearchConditionsByRequest_basic(request);
        try{
            //处理bucket聚合条件（X轴）
            String buckets = request.getParameter("buckets");
            if(null!=buckets){
                //buckets参数包含多个bucket对象
                JSONArray json = JSONArray.fromObject(buckets);
                //遍历
                for(Object beanObj:json.toArray()){
                    //转bean
                    Bucket bucket = JavaBeanUtil.mapToBean((Map)JSONObject.fromObject(beanObj), Bucket.class);
                    //转换成功时，写入参数对象中
                    if(null!=bucket){
                        //如果聚合类型是range。需要对参数进行再处理
                        if(bucket.getAggType().indexOf("Range")>=0){
                            JSONArray rangeArray = JSONObject.fromObject(beanObj).getJSONArray("ranges");
                            for(Object rangeObj : rangeArray){
                                Map<String,Object> rangeMap = (Map<String,Object>)rangeObj;
                                bucket.getRanges().add(rangeMap);
                            }
                        }
                        searchConditions.getBucketList().add(bucket);
                    }
                }
                //没有聚合字段相关参数时进行提示
                if(searchConditions.getBucketList().size()==0){
                    //searchConditions.setErrorInfo("聚合字段不能为空，请重新选择!");
                }
            }else{
                //没有聚合字段相关参数时进行提示
                searchConditions.setErrorInfo("聚合字段不能为空，请重新选择!");
            }
            //处理metric聚合条件（Y轴）
            String metrics = request.getParameter("metrics");
            if(null!=metrics){
                //buckets参数包含多个bucket对象
                JSONArray json = JSONArray.fromObject(metrics);
                //遍历
                for(Object beanObj:json.toArray()){
                    //转bean
                    Metric metric = JavaBeanUtil.mapToBean((Map)JSONObject.fromObject(beanObj), Metric.class);
                    //转换成功时，写入参数对象中
                    if(null!=metric){
                        searchConditions.getMetricList().add(metric);
                    }
                }
                //没有聚合字段相关参数时进行提示
                if(searchConditions.getMetricList().size()==0){
                    searchConditions.setErrorInfo("聚合字段不能为空，请重新选择!");
                }
            }else{
                //没有聚合字段相关参数时进行提示
                searchConditions.setErrorInfo("聚合字段不能为空，请重新选择!");
            }
        }catch(Exception e){
            searchConditions.setErrorInfo("查询参数异常，请重新设置！");
        }
        return searchConditions;
    }

    /**
     * sql查询条件处理
     * @param request
     * @return
     */
    public static SqlSearchConditions getSqlSearchConditionsByRequest(HttpServletRequest request){
        SqlSearchConditions sqlSearchConditions = new SqlSearchConditions();
        try{
            Map<String, String[]> params = request.getParameterMap();
            //通过bean对应，直接处理部分参数
            sqlSearchConditions.mapToBean(params);
            //列信息
            String columns = request.getParameter("columns");
            if(null!=columns){
                //columns参数包含多个column对象
                JSONArray json = JSONArray.fromObject(columns);
                //遍历
                for(Object beanObj:json.toArray()){
                    //转bean
                    SqlSearchColumn column = JavaBeanUtil.mapToBean((Map)JSONObject.fromObject(beanObj), SqlSearchColumn.class);
                    //转换成功时，写入参数对象中
                    if(null!=column){
                        sqlSearchConditions.getSqlSearchColumns().add(column);
                    }else{
                        sqlSearchConditions.setErrorInfo("列信息异常!");
                    }
                }
                //没有列
                if(sqlSearchConditions.getSqlSearchColumns().size()==0){
                    sqlSearchConditions.setErrorInfo("列信息异常!");
                }
            }else{
                //没有列时的异常显示
                sqlSearchConditions.setErrorInfo("至少选择一个列!");
            }
            //where条件
            String wheres = request.getParameter("wheres");
            if(null!=wheres){
                //wheres参数包含多个where对象
                JSONArray json = JSONArray.fromObject(wheres);
                //遍历
                for(Object beanObj:json.toArray()){
                    //转bean
                    SqlSearchWhere where = JavaBeanUtil.mapToBean((Map)JSONObject.fromObject(beanObj), SqlSearchWhere.class);
                    //转换成功时，写入参数对象中
                    if(null!=where){
                        sqlSearchConditions.getSqlSearchWheres().add(where);
                    }else{
                        sqlSearchConditions.setErrorInfo("条件参数异常！");
                    }
                }
            }else{
                //where条件可以为空
            }
            //分页信息
            if(sqlSearchConditions.getPage()==null||sqlSearchConditions.getPage_size()==null){
                sqlSearchConditions.setErrorInfo("查询参数异常！");
            }
        }catch(Exception e){
            sqlSearchConditions.setErrorInfo("查询参数异常，请重新设置！");
        }
        return sqlSearchConditions;
    }
    /**
     * 处理参数 用于对报表的参数处理
     * 基础参数处理，包括  index  query filter  时间范围
     * @param request
     * @return
     */
    private static SearchConditions getSearchConditionsByRequest_basic(HttpServletRequest request){
        SearchConditions searchConditions = new SearchConditions();
        try{
            Map<String, String[]> params = request.getParameterMap();
            //通过bean对应，直接处理部分参数，包括intervalValue、intervalType等等
            searchConditions.mapToBean(params);
            //获取起始和截至时间
            String starttime = request.getParameter("starttime");
            String endtime = request.getParameter("endtime");
            String last = request.getParameter("last");
            //last值不为空
            if(!Strings.isNullOrEmpty(last)){
                if(Strings.isNullOrEmpty(starttime)&&Strings.isNullOrEmpty(endtime)){
                    //起始、截止时间为空，last不为空,计算起始和截止时间
                    Map<String,String> map = getStartEndTimeByLast(last);
                    //赋值，如果起始和截止时间能正常获取
                    if(map.size()==2){
                        searchConditions.setStartTime(map.get("starttime"));
                        searchConditions.setEndTime(map.get("endtime"));
                    }else{
                        //其他情况判定为时间范围数据异常，返回前端显示
                        searchConditions.setErrorInfo("请重新设定时间范围!");
                    }
                }else{
                    //既有起始或截止时间，又有last，参数异常
                    searchConditions.setErrorInfo("请重新设定时间范围!");
                }
            }else{//last值为空
                //起始和截止时间都不为空,并且格式为yyyy-MM-dd HH:mm:ss
                if(!Strings.isNullOrEmpty(starttime)&&!Strings.isNullOrEmpty(endtime)&&starttime.matches("\\d{4}-\\d{2}-\\d{2}\\s{1}\\d{2}:\\d{2}:\\d{2}")&&endtime.matches("\\d{4}-\\d{2}-\\d{2}\\s{1}\\d{2}:\\d{2}:\\d{2}")){
                    searchConditions.setStartTime(starttime);
                    searchConditions.setEndTime(endtime);
                }else{
                    //起始或截止时间存在为空的，也属于参数异常状态
                    searchConditions.setErrorInfo("请重新设定时间范围!");
                }
            }
            //查询条件处理,数据格式为json，{key:value,key:value}
            String queryParam = request.getParameter("queryParam");
            if(null!=queryParam){
                Map<String,String> paramMap = MapUtil.json2map(queryParam);
                //聚合后端用到的参数处理魔石
                for(Map.Entry<String,String> entity:paramMap.entrySet()){
                    //目前通过参数传递过来的查询信息，默认采用term构建查询条件
                    //TODO 查询参数需要重新设计
                    QueryCondition qc = new QueryCondition("term",entity.getKey(),entity.getValue(),"");
                    searchConditions.getQueryConditions().add(qc);
                }
                //原后端用到的参数处理
                searchConditions.setQueryParam(paramMap);
            }
            //组装要检索的index的名称： 前缀+后缀
            searchConditions.setIndex_name(searchConditions.getPre_index_name()+searchConditions.getSuffix_index_name());
            //判断index名称，如果是hslog*，则日期字段设置为logdate，如果是*beat*，则日期字段设置为@timestamp
            if(searchConditions.getIndex_name().indexOf("packet-")>=0){
                searchConditions.setDateField(Constant.PACKET_DATE_FIELD);
            }else if(searchConditions.getIndex_name().indexOf("beat")>=0){
                searchConditions.setDateField(Constant.BEAT_DATE_FIELD);
            }else{
                searchConditions.setErrorInfo("数据源异常，请重新选择数据源!");
            }
            //---------处理filter--------图表filter---
            String filters_visual = request.getParameter("filters_visual");
            if(null!=filters_visual&&!"".equals(filters_visual)){
                //filters中包含多个filter
                JSONArray json = JSONArray.fromObject(filters_visual);
                //遍历
                for(Object beanObj:json.toArray()){
                    //转bean
                    Filter filter = JavaBeanUtil.mapToBean((Map)JSONObject.fromObject(beanObj), Filter.class);
                    //转换成功时，写入参数对象中
                    if(null!=filter){
                        searchConditions.getFilters_visual().add(filter);
                    }
                }
            }
            //---------处理filter--------dashboard filter---
            String filters_dashboard = request.getParameter("filters_dashboard");
            if(null!=filters_dashboard&&!"".equals(filters_dashboard)){
                //filters中包含多个filter
                JSONArray json = JSONArray.fromObject(filters_dashboard);
                //遍历
                for(Object beanObj:json.toArray()){
                    //转bean
                    Filter filter = JavaBeanUtil.mapToBean((Map)JSONObject.fromObject(beanObj), Filter.class);
                    //转换成功时，写入参数对象中
                    if(null!=filter){
                        searchConditions.getFilters_dashboard().add(filter);
                    }
                }
            }
            //---------处理filter--------table filter---
            String filters_table = request.getParameter("filters_table");
            if(null!=filters_table&&!"".equals(filters_table)){
                //filters中包含多个filter
                JSONArray json = JSONArray.fromObject(filters_table);
                //遍历
                for(Object beanObj:json.toArray()){
                    searchConditions.getFilters_table().add((Map)JSONObject.fromObject(beanObj));
                }
            }
            //queryBox 无法通过mapToBean的方式获取
            searchConditions.setQueryBox(request.getParameter("queryBox"));
        }catch(Exception e){
            searchConditions.setErrorInfo("查询参数异常，请重新设置！");
        }
        return searchConditions;
    }
    /**
     * 获取请求参数
     * @param request
     * @return
     */
    public static HttpRequestParams getParams(HttpServletRequest request){
        HttpRequestParams params = new HttpRequestParams();
        //获取起始和截至时间
        Map<String,String> timeMap = getStartEndTimeByRequest(request);
        //如果时间范围参数没有正常获取，设置状态位，方便其他逻辑的判定
        if(timeMap.size()!=2){
            params.setHasTimeArea(false);
        }else{
            params.setHasTimeArea(true);
            params.setStartTime(timeMap.get("starttime"));
            params.setEndTime(timeMap.get("endtime"));
        }
        params.setGroupField(request.getParameter("groupField"));//聚合字段
        //TODO 时间间隔参数处理，要转为int
        String intervalValue = request.getParameter("intervalValue");
        if(null!=intervalValue&&!"".equals(intervalValue)){
            //正则判定是不是数字
            params.setIntervalValue(intervalValue.matches("\\d+")?Integer.parseInt(intervalValue):null);//时间间隔
        }
        params.setIntervalType(request.getParameter("intervalType"));//间隔单位  秒、分钟、小时
        //hsdata参数，转为map
        String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);
        Map<String,String> map = new HashMap<>();

        if(null!=hsData){
            map = gson.fromJson(hsData,Map.class);
        }
        params.setQueryMap(map);
        return params;
    }
    /**
     * 根据前端传递的last参数计算起始和截止时间
     * @param last 最近的时间 例：15-min、1-daying
     * @return
     */
    public static Map<String,String> getStartEndTimeByLast(String last){
        //今天、这周、本月等时间类型对应的格式化定义
        String startPattern = "yyyy-MM-dd 00:00:00";
        String endPattern = "yyyy-MM-dd 23:59:59";
        SimpleDateFormat startSdf = new SimpleDateFormat(startPattern);
        SimpleDateFormat endSdf = new SimpleDateFormat(endPattern);
        Map<String,String> map = new HashMap<>();
        //判定参数是否为空
        if(null!=last&&""!=last){
            String[] lastArgs = last.split("-");
            //只有长度为2时才会处理
            if(lastArgs.length==2){
                //定义时间对象，并初始化年月日
                Calendar cal = Calendar.getInstance();
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                //当前时间，一般作为截至时间
                String endTime = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
                //第一位是数字
                int timeNumber = Integer.parseInt(lastArgs[0]);
                //第二位是时间的类型
                String timeType = lastArgs[1];
                //根据时间类型，使用不同的方式计算起始和截至时间
                switch(timeType){
                    case "daying"://今天
                        map.put("starttime", DateTime.now().toString(startPattern));
                        map.put("endtime", DateTime.now().toString(endPattern));
                        break;
                    case "weeking"://这周
                        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);//本周第一天
                        map.put("starttime", startSdf.format(cal.getTime()));
                        cal.add(Calendar.DAY_OF_WEEK, 6);//本周最后一天
                        map.put("endtime", endSdf.format(cal.getTime()));
                        break;
                    case "monthing"://本月
                        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));//本月第一天
                        map.put("starttime", startSdf.format(cal.getTime()));
                        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));//本月最后一天
                        map.put("endtime", endSdf.format(cal.getTime()));
                        break;
                    case "min"://最近N分钟
                        map.put("endtime",endTime);
                        map.put("starttime",DateTime.now().minusMinutes(timeNumber).toString("yyyy-MM-dd HH:mm:ss"));
                        break;
                    case "hour"://最近N小时
                        map.put("endtime",endTime);
                        map.put("starttime",DateTime.now().minusHours(timeNumber).toString("yyyy-MM-dd HH:mm:ss"));
                        break;
                    case "day"://最近N天
                        map.put("endtime",endTime);
                        map.put("starttime",DateTime.now().minusDays(timeNumber).toString("yyyy-MM-dd HH:mm:ss"));
                        break;
                    default:
                        //暂不处理
                        break;
                }
                return map;
            }else{
                //参数异常
                return map;
            }
        }else{
            //参数不存在，不做处理
            return map;
        }
    }

    /**
     * 根据请求参数获取起始和截至时间
     * @param request 请求对象
     * @return
     */
    public static Map<String,String> getStartEndTimeByRequest(HttpServletRequest request){
        Map<String,String> map = new HashMap<>();
        //时间参数last，只最近多少分、多少小时、今天、本周
        String last = request.getParameter("last");
        try{
            //如果last时间参数为空，判定传递的是否有起始和截至时间
            if(last==null||"".equals(last)){
                String starttime = request.getParameter("starttime");//起始时间
                String endtime = request.getParameter("endtime");//截止时间
                //时间格式判定，
                if(starttime!=null&&starttime.matches("\\d{4}-\\d{2}-\\d{2}\\s{1}\\d{2}:\\d{2}:\\d{2}")){
                    map.put("starttime",starttime);
                }else{
                    //不放入map中
                }
                if(endtime!=null&&endtime.matches("\\d{4}-\\d{2}-\\d{2}\\s{1}\\d{2}:\\d{2}:\\d{2}")){
                    map.put("endtime",endtime);
                }else{
                    //不放入map中
                }
                return map;
            }else{
                //根据last进行处理
                return getStartEndTimeByLast(last);
            }
        }catch(Exception e){
            //参数处理出现异常时，返回空map
            return new HashMap<>();
        }
    }

    /**
     * 处理动态折线图的参数
     * 在动态折线图中，如果用户停止轮询，选择一个时间范围进行查询时
     * 通过设置的默认点的个数计算间隔，以此进行聚合查询
     * @param params 请求参数
     * @param points 显示数据点的个数
     */
    public static void handleDynamicLineParams(VisualParam params,String points){
        try{
            //如果没有传时间间隔和间隔类型
            if(Strings.isNullOrEmpty(params.getIntervalType())&&params.getIntervalValue()==0){
                //计算起始和截止时间的秒数
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date starttime = sdf.parse(params.getStartTime());
                Date endtime = sdf.parse(params.getEndTime());
                long start = starttime.getTime();
                long end = endtime.getTime();
                int areaSeconds = (int)((end - start) / 1000);
                //通过默认的分隔点数，算出间隔，并取整
                int intervalValue = areaSeconds/Integer.parseInt(points);
                params.setIntervalType("second");
                params.setIntervalValue(intervalValue);
            }else{
                //不需要进行操作
            }
        }catch (Exception e){
            params.setErrorInfo("请重新设定时间范围！");
        }

    }
    public static void main(String[] args){
        //Map<String,String> map = HttpRequestUtil.getStartEndTimeByLast("1-day");
        //System.out.println(map.get("starttime"));
        //System.out.println(map.get("endtime"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("".matches("\\d{4}-\\d{2}-\\d{2}\\s{1}\\d{2}:\\d{2}:\\d{2}")?1:0);

    }
}

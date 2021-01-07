package com.jz.bigdata.common.alert.job;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.hs.elsearch.dao.searchDao.ISearchDao;
import com.hs.elsearch.entity.Bucket;
import com.hs.elsearch.entity.Filter;
import com.hs.elsearch.entity.Metric;
import com.hs.elsearch.entity.SearchConditions;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.alert.entity.AlertSnapshot;
import com.jz.bigdata.util.AggDataHandler;
import com.jz.bigdata.util.HttpRequestUtil;
import com.jz.bigdata.util.JavaBeanUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: yiyang
 * @Date: 2020/11/2 10:45
 * @Description: 告警模块，执行方法。
 * 1.获取参数
 * 2.构建searchCondition对象
 * 3.通过searchCondition构建es java 查询对象
 * 4.返回结果处理（与告警值对比）
 * 5.写入ES alert快照index（alert-default）
 */
@Slf4j
public class AlertJob implements Job {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//时间戳格式
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();//bean转json
    @Autowired
    protected ISearchDao searchDao;
    @Autowired
    protected ILogCrudDao logCurdDao;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        AlertSnapshot alertSnapshot = new AlertSnapshot();

        //传递参数，alert信息
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        //转成map
        Map<String,Object> map = jobDataMap.getWrappedMap();
        //写入部分快照参数
        alertSnapshot.setAlert_id(map.get("alert_id").toString());//告警id
        alertSnapshot.setAlert_search_filters(map.get("alert_search_filters").toString());//筛选条件
        alertSnapshot.setAlert_search_metric(map.get("alert_search_metric").toString());//统计条件
        alertSnapshot.setAlert_search_bucket(map.get("alert_search_bucket").toString());//分组条件
        alertSnapshot.setAlert_conditions(map.get("alert_conditions").toString());//告警条件
        alertSnapshot.setAlert_name(map.get("alert_name").toString());//告警名称
        alertSnapshot.setAlert_note(map.get("alert_note").toString());//告警说明
        alertSnapshot.setPre_index_name(map.get("pre_index_name").toString());//索引前缀
        alertSnapshot.setSuffix_index_name(map.get("suffix_index_name").toString());//索引后缀
        alertSnapshot.setTemplate_name(map.get("template_name").toString());//索引模板
        alertSnapshot.setAlert_cron(map.get("alert_cron").toString());//执行周期 cron表达式
        alertSnapshot.setAlert_time(map.get("alert_time").toString());//数据查询时间范围
        alertSnapshot.setAlert_time_type(map.get("alert_time_type").toString());//时间范围类型 last range
        alertSnapshot.setAlert_exec_type(map.get("alert_exec_type").toString());//执行周期类型 simple / complex(高级)
        alertSnapshot.setAlert_time_cycle_num(map.get("alert_time_cycle_num").toString());//执行周期数值
        alertSnapshot.setAlert_time_cycle_type(map.get("alert_time_cycle_type").toString());//执行周期时间类型： 秒 second   分钟  minute   小时  hour
        alertSnapshot.setRun_time(new Date());
        SearchConditions searchConditions = new SearchConditions();
        //组装要检索的index的名称： 前缀+后缀
        searchConditions.setIndex_name(map.get("pre_index_name").toString()+map.get("suffix_index_name").toString());
        //判断index名称，如果是hslog*，则日期字段设置为logdate，如果是*beat*，则日期字段设置为@timestamp
        if(searchConditions.getIndex_name().indexOf("packet-")>=0){
            searchConditions.setDateField(Constant.PACKET_DATE_FIELD);
        }else if(searchConditions.getIndex_name().indexOf("beat")>=0){
            searchConditions.setDateField(Constant.BEAT_DATE_FIELD);
        }else{
            log.error("定时任务index异常，index名称中不包含packet和beat");
        }
        //处理时间范围，所有参数都有值，至少是空字符串，因此toString不会报错
        Map<String,String> timeArea = HttpRequestUtil.getTimeAreaByAlertParam(map.get("alert_exec_type").toString(),map.get("alert_time_cycle_num").toString(),map.get("alert_time_cycle_type").toString(),map.get("alert_time_type").toString(),map.get("alert_time").toString());
        //赋值，如果起始和截止时间能正常获取
        if(timeArea.size()==2){
            searchConditions.setStartTime(timeArea.get("starttime"));
            searchConditions.setEndTime(timeArea.get("endtime"));
        }else{
            log.error("时间查询参数异常:"+JSONObject.fromObject(map));
            searchConditions.setErrorInfo("时间查询参数异常！");
        }
        //起始/截止时间
        try {
            alertSnapshot.setStart_time(simpleDateFormat.parse(searchConditions.getStartTime()));
            alertSnapshot.setEnd_time(simpleDateFormat.parse(searchConditions.getEndTime()));
        } catch (ParseException e) {
            log.error("时间格式转化失败！"+e.getMessage());
            log.error(searchConditions.getStartTime()+"--"+searchConditions.getEndTime());
        }
        //处理bucket聚合条件
        Object buckets = map.get("alert_search_bucket");
        if(null!=buckets&&!"".equals(buckets)){
            //buckets参数包含多个bucket对象
            JSONArray json = JSONArray.fromObject(buckets);
            //遍历
            for(Object beanObj:json.toArray()){
                //转bean
                Bucket bucket = JavaBeanUtil.mapToBean((Map) JSONObject.fromObject(beanObj), Bucket.class);
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
        Object metrics = map.get("alert_search_metric");
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
        Object filters_visual = map.get("alert_search_filters");
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
        //执行结果是否有满足条件的存在
        Boolean isFire = false;
        //执行ES查询的返回结果信息
        String resultInfo = "";
        //告警条件匹配次数
        int match_size = 0;
        //告警条件匹配结果内容
        List<String> match_result = new ArrayList<>();
        if(null==searchConditions.getErrorInfo()){
            //是聚合
            if(searchConditions.getMetricList().size()>0||searchConditions.getBucketList().size()>0){
                try{
                    //聚合查询的size为0
                    searchConditions.setSize(0);
                    SearchResponse searchResponse = searchDao.getResponse(searchConditions);
                    //数据进行一定程度范式化
                    List<Map<String,Object>> resultList = AggDataHandler.toAlertAgg(searchResponse.getAggregations(),searchConditions);
                    //resultInfo = JSONArray.fromObject(resultList).toString();
                    resultInfo = searchResponse.toString();
                    //结果判定，是否符合告警条件
                    for(Map<String,Object> result:resultList){
                        if(map.get("alert_conditions")!=null){
                            //告警条件
                            JSONArray json = JSONArray.fromObject(map.get("alert_conditions"));
                            //遍历条件，如果该结果符合所有条件，则视为触发了告警
                            //只要有一个条件不符合，就视为未触发，因此将结果集中的一个结果对象的判定 默认设置为触发，任意条件不满足都置为“未触发”
                            Boolean oneValueIsFire = true;
                            for(Object beanObj:json.toArray()){
                                //转bean
                                Filter filter = JavaBeanUtil.mapToBean((Map)JSONObject.fromObject(beanObj), Filter.class);
                                //转换成功时，写入参数对象中
                                if(null!=filter){
                                    //将条件与值进行匹配判定
                                    //如果是name值的判定
                                    if("name".equals(filter.getField())){
                                        switch(filter.getOperator()){
                                            case "is one of":
                                                String[] inValues = filter.getValues();
                                                if(!Arrays.asList(inValues).contains(result.get("name"))){
                                                    oneValueIsFire = false;
                                                }
                                                break;
                                            case "is not one of":
                                                String[] outValues = filter.getValues();
                                                if(Arrays.asList(outValues).contains(result.get("name"))){
                                                    oneValueIsFire = false;
                                                }
                                                break;
                                            case "is match":
                                                String matchValue = filter.getValue();
                                                if(result.get("name").toString().indexOf(matchValue)<0){
                                                    oneValueIsFire = false;
                                                }
                                                break;
                                            case "is not match":
                                                String notMatchValue = filter.getValue();
                                                if(result.get("name").toString().indexOf(notMatchValue)>=0){
                                                    oneValueIsFire = false;
                                                }
                                                break;
                                            case "=":
                                                if(!result.get("name").toString().equals(filter.getValue())){
                                                    oneValueIsFire = false;
                                                }
                                                break;
                                            case "!=":
                                                if(result.get("name").toString().equals(filter.getValue())){
                                                    oneValueIsFire = false;
                                                }
                                                break;
                                        }
                                    }else if("value".equals(filter.getField())){
                                        Double numberValue = Double.valueOf(result.get("value").toString());
                                        switch(filter.getOperator()){
                                            case ">":
                                                if(numberValue<=Double.valueOf(filter.getValue())){
                                                    oneValueIsFire = false;
                                                }
                                                break;
                                            case ">=":
                                                if(numberValue<Double.valueOf(filter.getValue())){
                                                    oneValueIsFire = false;
                                                }
                                                break;
                                            case "<":
                                                if(numberValue>=Double.valueOf(filter.getValue())){
                                                    oneValueIsFire = false;
                                                }
                                                break;
                                            case "<=":
                                                if(numberValue>Double.valueOf(filter.getValue())){
                                                    oneValueIsFire = false;
                                                }
                                                break;
                                            case "=":
                                                if(numberValue!=Double.valueOf(filter.getValue())){
                                                    oneValueIsFire = false;
                                                }
                                                break;
                                            case "!=":
                                                if(numberValue==Double.valueOf(filter.getValue())){
                                                    oneValueIsFire = false;
                                                }
                                                break;
                                            case "is one of":
                                                String[] inValues = filter.getValues();
                                                if(!Arrays.asList(inValues).contains(result.get("value"))){
                                                    oneValueIsFire = false;
                                                }
                                                break;
                                            case "is not one of":
                                                String[] outValues = filter.getValues();
                                                if(Arrays.asList(outValues).contains(result.get("value"))){
                                                    oneValueIsFire = false;
                                                }
                                                break;
                                        }

                                    }else{
                                        //聚合结果无第三个字段
                                        log.info("聚合结果字段有第三个字段：告警条件为信息为："+JSONObject.fromObject(filter).toString());
                                    }
                                }
                            }
                            //只有有一条记录满足条件即可产生告警
                            if(oneValueIsFire){
                                isFire = true;
                                match_size++;
                                match_result.add(JSONObject.fromObject(result).toString());
                            }
                        }
                    }
                    System.out.println(isFire);
                    System.out.println(resultInfo);
                }catch(Exception e){
                    log.error("Alert带聚合查询的结果处理异常："+e.getMessage());
                }
            }else{
                //告警条件
                Object alert_conditions = map.get("alert_conditions");
                if(null!=alert_conditions&&!"".equals(alert_conditions)){
                    JSONArray json = JSONArray.fromObject(alert_conditions);
                    //遍历
                    for(Object beanObj:json.toArray()){
                        //转bean
                        Filter filter = JavaBeanUtil.mapToBean((Map)JSONObject.fromObject(beanObj), Filter.class);
                        //转换成功时，写入参数对象中
                        if(null!=filter){
                            filter.setEnable(true);
                            searchConditions.getFilters_alert().add(filter);
                        }
                    }
                }
                try{
                    //
                    SearchResponse searchResponse = searchDao.getResponse(searchConditions);
                    ArrayList<Object> hitsResult = AggDataHandler.toHitsList(searchResponse);
                    //加上告警条件后的查询，返回的hits有数据，说明有达到告警条件的数据
                    if(hitsResult.size()>0){
                        isFire = true;
                        match_size = match_size+hitsResult.size();
                    }
                    //TODO 存储匹配的结果，原始数据会包含
                    //for(Object object:hitsResult){
                        //object
                    //}
                    //resultInfo = hitsResult.toString();
                    resultInfo = searchResponse.toString();
                    //返回结果数据处理
                    System.out.println(searchResponse.toString());
                }catch(Exception e){
                    log.error("ES执行异常："+e.getMessage());
                }
                System.out.println(isFire);
                System.out.println(resultInfo);
            }
            alertSnapshot.setResult(resultInfo);//执行结果
            alertSnapshot.setAlert_fire(isFire);//是否产生告警
            alertSnapshot.setMatch_size(match_size+"");

        }else{
            //参数处理异常，不执行查询
            alertSnapshot.setResult(searchConditions.getErrorInfo());//执行结果写入异常信息
            alertSnapshot.setAlert_fire(false);//异常情况
        }
        //System.out.println(gson.toJson(alertSnapshot));
        //index名称
        //TODO alert index规则调整
        IndexRequest request = new IndexRequest();
        request.index(Constant.ALERT_WRITE_INDEX);
        request.source(gson.toJson(alertSnapshot), XContentType.JSON);
        //写入ES
        logCurdDao.bulkProcessor_add(request);


        //TODO restful-api
        //System.out.println("................"+map.get("alert_id").toString());
    }
}
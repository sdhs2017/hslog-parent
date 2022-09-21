package com.jz.bigdata.common.alert.service.impl;

import com.google.gson.*;
import com.hs.elsearch.dao.searchDao.ISearchDao;
import com.hs.elsearch.entity.DataTableColumn;
import com.hs.elsearch.entity.QueryCondition;
import com.hs.elsearch.entity.SearchConditions;
import com.hs.elsearch.service.ISearchService;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.alert.dao.IAlertDao;
import com.jz.bigdata.common.alert.entity.Alert;
import com.jz.bigdata.common.alert.service.IAlertService;
import com.jz.bigdata.common.alert.job.AlertJob;
import com.jz.bigdata.util.AggDataHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.Aggregations;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.quartz.CronScheduleBuilder.cronSchedule;
@Slf4j
@Service(value="AlertService")
public class AlertServiceImpl implements IAlertService {
    private static final Gson gson = new Gson();

    private static final DateTimeFormatter dtf_time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Autowired
    protected ISearchDao searchDao;
    @Autowired
    protected ISearchService searchService;
    @Resource
    private IAlertDao alertDao;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Override
    public String getAlertResult(SearchConditions searchConditions) throws Exception {
        SearchResponse response = searchDao.getResponse(searchConditions);

        //size=0的情况为有聚合的结果
        if(searchConditions.getSize()==0){
            Aggregations agg = response.getAggregations();
            List<Map<String,Object>> result = AggDataHandler.toAlertAgg(agg,searchConditions);
            return JSONArray.fromObject(result).toString();
        }else{
            ArrayList<Object> result = AggDataHandler.toHitsList(response);
            return result.toString();
        }
    }

    @Override
    public boolean insert(Alert alert) {
        //告警信息的id
        String alert_id = UUID.randomUUID().toString();
        alert.setAlert_id(alert_id);
        //计划任务执行状态
        alert.setAlert_state(Constant.ALERT_STATE_RUNNING);
        int count = alertDao.insert(alert);//保存信息
        //保存成功
        if(count==1){
            try {
                createQuartz(alert);//创建计划任务
                return true;
            } catch (Exception e) {
                //删除插入数据库的告警信息
                alertDao.deleteAlert(alert_id);
                //创建任务失败
                log.error("创建任务失败："+e.getMessage());
                //检查任务是否存在
                Scheduler scheduler = schedulerFactoryBean.getScheduler();
                JobKey jobKey = JobKey.jobKey(alert_id, Constant.QUARTZ_JOB_GROUP);
                try {
                    if(scheduler.checkExists(jobKey)){
                        //存在则删除任务
                        deleteQuartz(alert_id);
                    }
                } catch (SchedulerException ex) {
                    log.error("新增告警失败，回滚时要删除计划任务，失败："+ex.getMessage());
                }
                return false;
            }
        }else{
            return false;
        }
    }

    @Override
    public Map<String, Object> getAlertInfoByCondition(Alert alert) {
        List<List<Map<String,String>>> count = alertDao.getAlertInfoCountByCondition(alert);
        List<Alert> result = alertDao.getAlertInfoByCondition(alert);
        try{
            for(Alert alertBean:result){
                //返回数据处理
                //执行周期
                if(alertBean.getAlert_exec_type()!=null&&alertBean.getAlert_exec_type().equals("simple")){
                    //执行周期
                    alertBean.setAlert_exec_cycle(alertBean.getAlert_time_cycle_num()+alertExecCycleType2EN(alertBean.getAlert_time_cycle_type()));
                    //时间范围
                    //alertBean.setAlert_time_range("最近"+alertBean.getAlert_time()+alertExecCycleType2EN(alertBean.getAlert_time()));
                }else{
                    alertBean.setAlert_exec_cycle(alertBean.getAlert_cron());
                }
                String alert_id = alertBean.getAlert_id();
                //Scheduler scheduler = schedulerFactoryBean.getScheduler();
                //TriggerKey triggerKey = new TriggerKey(alert_id,Constant.QUARTZ_TRIGGER_GROUP);
                //将running和stop转化成前端需要的true和false
                if(Constant.ALERT_STATE_RUNNING.equals(alertBean.getAlert_state())){
                    alertBean.setAlert_state_boolean(true);
                }else{
                    alertBean.setAlert_state_boolean(false);
                }
            }
        }catch(Exception e){
            log.error("获取任务状态失败！");
        }


        //组装前端需要的数据格式
        Map<String, Object> allMap = new HashMap<>();
        allMap.put("count",count.get(0).get(0).get("count"));
        allMap.put("list",result);
        return allMap;
    }

    @Override
    public boolean delete(String alert_id) throws Exception {
        //删除数据库数据
        alertDao.deleteAlert(alert_id);
        //删除计划任务
        deleteQuartz(alert_id);
        return true;
    }

    @Override
    public boolean deletes(String alert_ids) throws Exception {
        String[] ids = alert_ids.split(",");
        alertDao.deleteAlerts(ids);
        for(String id:ids){
            deleteQuartz(id);
        }
        return true;
    }

    @Override
    public String updateById(Alert alert, HttpSession session) {
        //当前用户信息
        alert.setAlert_update_user(session.getAttribute(Constant.SESSION_USERID).toString());
        //插入时间
        alert.setAlert_update_time(LocalDateTime.now().format(dtf_time));
        //更新告警信息
        try{
            alertDao.update(alert);
        }catch(Exception e){
            log.error("告警信息更新失败："+e.getMessage());
            //更新失败直接返回，不处理计划任务
            return Constant.failureMessage("告警信息更新失败！");
        }
        //更新计划任务信息
        //先删除，再新增
        if(deleteQuartz(alert.getAlert_id())){
            try {
                createQuartz(alert);
            } catch (Exception e) {
                log.error("创建计划任务失败："+e.getMessage());
                return Constant.failureMessage("告警信息更新成功，但计划任务创建失败，请重新启动！");
            }
        }
        return Constant.successMessage("告警信息更新成功！");
    }

    @Override
    public Alert getAlertInfoById(String alert_id) {
        return alertDao.getAlertInfoById(alert_id);
    }

    @Override
    public boolean stopQuartz(String alert_id) throws Exception {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        //获取计划任务
        JobKey jobKey = JobKey.jobKey(alert_id, Constant.QUARTZ_JOB_GROUP);
        //如果存在，停止
        if(scheduler.checkExists(jobKey)){
            scheduler.pauseJob(jobKey);
        }
        //更新数据库状态
        if(updateAlertState(alert_id)){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean startQuartz(String alert_id){
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        //获取该告警对应的计划任务
        JobKey jobKey = JobKey.jobKey(alert_id, Constant.QUARTZ_JOB_GROUP);
        try{
            //如果存在，重新启动
            if(scheduler.checkExists(jobKey)){
                scheduler.resumeJob(jobKey);
            }else{
                //获取alert基本信息
                Alert alert = alertDao.getAlertInfoById(alert_id);
                //创建
                createQuartz(alert);
            }
        }catch (Exception e){
            log.error("计划任务启动失败("+alert_id+")："+e.getMessage());
            return false;
        }
        //更新数据库状态
        if(updateAlertState(alert_id)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 仅执行一次的计划任务
     * @param alert_id
     * @return
     */
    public boolean createQuartzAndStartOnce(String alert_id){
        Alert alert = alertDao.getAlertInfoById(alert_id);
        if(alert!=null){
            //创建定时任务
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobDataMap map = null;
            try {
                //处理参数
                map = alert2JobDataMap(alert);
            } catch (Exception e) {
                log.error("计划任务参数处理异常："+e.getMessage());
                return false;
            }
            JobDetail jobDetail = JobBuilder.newJob(AlertJob.class)
                    .withDescription("告警任务-仅执行一次")
                    .withIdentity(alert.getAlert_id()+"-onlyOnce", Constant.QUARTZ_JOB_GROUP)
                    .usingJobData(map)
                    .build();
            SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger().withIdentity(alert.getAlert_id()+"-onlyOnce", Constant.QUARTZ_JOB_GROUP)
                    .startAt(new Date())
                    //重复执行的次数，因为加入任务的时候马上执行了，所以不需要重复，否则会多一次。
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(3).withRepeatCount(0))
                    .build();
            try {
                if(!scheduler.checkExists(JobKey.jobKey(alert.getAlert_id()+"-onlyOnce",Constant.QUARTZ_JOB_GROUP))){
                    scheduler.scheduleJob(jobDetail,simpleTrigger);
                }
                scheduler.start();
                return true;
            } catch (SchedulerException e) {
                log.error("任务启动失败："+e.getMessage());
                return false;
            }
        }else{
            log.error("未找到alert_id:"+alert_id+"  对应的告警信息！");
            return false;
        }

    }
    /**
     * 创建一个quartz任务
     * @param alert
     * @return
     * @throws Exception
     */
    private Boolean createQuartz(Alert alert){
        //创建定时任务
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobDataMap map = null;
        try {
            map = alert2JobDataMap(alert);
        } catch (Exception e) {
            log.error("计划任务参数处理异常："+e.getMessage());
            return false;
        }
        String cronExpress = "";
        //生成cron表达式
        if(alert.getAlert_exec_type()!=null&&alert.getAlert_exec_type().equals("complex")){
            cronExpress = alert.getAlert_cron();
        }else if(alert.getAlert_exec_type()!=null&&alert.getAlert_exec_type().equals("simple")){
            switch(alert.getAlert_time_cycle_type()){
                case "second":
                    cronExpress = "*/"+alert.getAlert_time_cycle_num()+" * * * * ?";
                    break;
                case "minute":
                    cronExpress = "0 */"+alert.getAlert_time_cycle_num()+" * * * ?";
                    break;
                case "hour":
                    cronExpress = "0 0 */"+alert.getAlert_time_cycle_num()+" * * ?";
                    break;
                default:
                    return false;
            }
        }else{
            return false;
        }
        //任务
        JobDetail jobDetail = JobBuilder.newJob(AlertJob.class)
                .withDescription("告警定时任务")
                .withIdentity(alert.getAlert_id(), Constant.QUARTZ_JOB_GROUP)
                .usingJobData(map)
                .build();

        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpress).withMisfireHandlingInstructionDoNothing();
        //触发器
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withSchedule(cronScheduleBuilder)
                .withIdentity(alert.getAlert_id(),Constant.QUARTZ_TRIGGER_GROUP)
                .build();
        try {
            if(!scheduler.checkExists(JobKey.jobKey(alert.getAlert_id(),Constant.QUARTZ_JOB_GROUP))){
                scheduler.scheduleJob(jobDetail,trigger);
            }
            //启动
            scheduler.start();
            return true;
        } catch (SchedulerException e) {
            log.error("任务启动失败："+e.getMessage());
            return false;
        }
    }

    /**
     * 删除计划任务
     * @param alert_id
     * @return
     */
    private Boolean deleteQuartz(String alert_id){
        Scheduler scheduler=schedulerFactoryBean.getScheduler();
        TriggerKey tiger= new TriggerKey(alert_id,Constant.QUARTZ_TRIGGER_GROUP);
        if(tiger==null){//未找到计划任务，判定为删除成功。
            return true;
        }else{
            try{
                scheduler.pauseTrigger(tiger);// 停止触发器
                scheduler.unscheduleJob(tiger);//移除触发器
                scheduler.deleteJob(new JobKey(alert_id,Constant.QUARTZ_JOB_GROUP));//删除job
                return true;
            }catch(SchedulerException e){
                log.error("删除计划任务失败！"+e.getMessage());
                //TODO 重启所有计划任务或者确认删除失败的原因进行相关处理
                return false;
            }
        }
    }

    /**
     * 将alert对象转化成jobDataMap
     * @param alert
     * @return
     * @throws Exception
     */
    public JobDataMap alert2JobDataMap(Alert alert) throws Exception {
        JobDataMap map = new JobDataMap();
        Class cls = alert.getClass();
        Field[] fields = cls.getDeclaredFields();
        //遍历Alert class的所有属性
        for(int i=0; i<fields.length; i++){
            Field f = fields[i];
            f.setAccessible(true);
            //保证所有属性都有值，在获取参数值时不会为空
            map.put(f.getName(), f.get(alert)==null?"":f.get(alert).toString());
        }
        return map;
    }

    /**
     * 通过告警id 检验对应计划任务执行状态，并更新到数据库
     * @param alert_id
     * @return
     */
    private boolean updateAlertState(String alert_id) {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        //查看trigger状态
        TriggerKey triggerKey = new TriggerKey(alert_id,Constant.QUARTZ_TRIGGER_GROUP);
        try{
            //none和pause（调度任务不存在或已暂停）
            if(Trigger.TriggerState.PAUSED.equals(scheduler.getTriggerState(triggerKey))||Trigger.TriggerState.NONE.equals(scheduler.getTriggerState(triggerKey))){
                alertDao.updateAlertState(alert_id,Constant.ALERT_STATE_STOP);
            }else{
                alertDao.updateAlertState(alert_id,Constant.ALERT_STATE_RUNNING);
            }
            return true;
        }catch(Exception e){
            return false;
        }
    }
    /**
     * 执行周期转换成文字 秒/second 分钟/minute 小时/hour
     * @param type
     * @return
     */
    private String alertExecCycleType2EN(String type){
        switch(type){
            case "second":
                return "秒";
            case "minute":
                return "分钟";
            case "hour":
                return "小时";
            default:
                return "";
        }
    }


    /**
     * 初始化告警的计划任务
     */
    public void initAlertQuartz(){
        //获取告警列表，无条件为查询所有
        List<Alert> alertList = alertDao.getAlertInfoByCondition(new Alert());
        for(Alert alert : alertList){
            //根据状态，创建任务并执行
            if(Constant.ALERT_STATE_RUNNING.equals(alert.getAlert_state())){
                createQuartz(alert);
            }
        }
    }

    @Override
    public List<Alert> getAlertFireInfo(SearchConditions searchConditions) throws Exception {
        List<Alert> result = new ArrayList<>();
        //获取聚合结果
        Aggregations aggregations = searchDao.getAggregation(searchConditions);
        //聚合结果处理
        List<Map<String,Object>> aggResultList = AggDataHandler.toAlertAgg(aggregations,searchConditions);

        //遍历
        for(Map<String,Object> map : aggResultList){
            String alert_id = map.get("name").toString();
            String fire_count = map.get("value").toString();
            //获取alert信息
            Alert alert = alertDao.getAlertInfoById(alert_id);
            //告警策略被删除，无法获取原始告警信息，不再显示
            if(alert!=null){
                alert.setFire_count(fire_count);
                //获取告警对应的执行列表（fire）
                //alert_fire 告警状态为true

                result.add(alert);
            }

        }
        return result;
    }

    @Override
    public Map<String, Object> getAlertList(String alert_id,String starttime,String endtime,int from,int size,Boolean isFire) throws Exception {
        //组装告警的查询条件
        SearchConditions searchConditions = new SearchConditions();
        searchConditions.setIndex_name(Constant.ALERT_SEARCH_INDEX);
        //时间范围
        searchConditions.setStartTime(starttime);
        searchConditions.setEndTime(endtime);
        searchConditions.setDateField("run_time");
        //仅查询产生了告警的信息
        if(isFire==null){
            //查询所有数据
        }else if(isFire){
            //告警状态为true的
            QueryCondition queryCondition = new QueryCondition("term","alert_fire",true,null);
            searchConditions.getQueryConditions().add(queryCondition);
        }else{
            //查询未产生告警的数据
            //告警状态为false的
            QueryCondition queryCondition = new QueryCondition("term","alert_fire",false,null);
            searchConditions.getQueryConditions().add(queryCondition);
        }
        //查询条件为告警id
        QueryCondition queryCondition_alert = new QueryCondition("term","alert_id.raw",alert_id,null);
        searchConditions.getQueryConditions().add(queryCondition_alert);
        //显示条数
        searchConditions.setPage_size(size);
        searchConditions.setFrom(from);
        //倒序排列
        DataTableColumn sortColumn = new DataTableColumn();
        sortColumn.setField("run_time");
        sortColumn.setSort("desc");
        searchConditions.getSortColumns().add(sortColumn);
        //查询单个告警对应的告警快照列表
        List<Map<String,Object>> searchHitsList = searchService.getSearchHitsList(searchConditions);
        long count = searchService.getCountByConditionsQuery(searchConditions);
        Map<String, Object> allMap = new HashMap<>();
        allMap.put("count",count);
        allMap.put("list",searchHitsList);
        return allMap;
    }

//    @Override
//    public Map<String, Object> getAlertList(String alert_id, String starttime, String endtime, int from , int size) throws Exception {
//        //组装告警的查询条件
//        SearchConditions searchConditions = new SearchConditions();
//        //时间范围
//        searchConditions.setIndex_name(Constant.ALERT_SEARCH_INDEX);
//        searchConditions.setStartTime(starttime);
//        searchConditions.setEndTime(endtime);
//        searchConditions.setDateField("run_time");
//        //查询条件为告警id
//        QueryCondition queryCondition_alert = new QueryCondition("term","alert_id.raw",alert_id,null);
//        searchConditions.getQueryConditions().add(queryCondition_alert);
//        //显示条数
//        searchConditions.setPage_size(size);
//        searchConditions.setFrom(from);
//        //倒序排列
//        DataTableColumn sortColumn = new DataTableColumn();
//        sortColumn.setField("run_time");
//        sortColumn.setSort("desc");
//        searchConditions.getSortColumns().add(sortColumn);
//        //查询单个告警对应的告警快照列表
//        List<Map<String,Object>> searchHitsList = searchService.getSearchHitsList(searchConditions);
//        long count = searchService.getCountByConditionsQuery(searchConditions);
//        Map<String, Object> allMap = new HashMap<>();
//        allMap.put("count",count);
//        allMap.put("list",searchHitsList);
//        return allMap;
//    }
}

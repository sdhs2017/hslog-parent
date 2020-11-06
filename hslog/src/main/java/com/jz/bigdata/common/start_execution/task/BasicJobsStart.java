package com.jz.bigdata.common.start_execution.task;

import com.jz.bigdata.common.Constant;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import static org.quartz.CronScheduleBuilder.cronSchedule;
@Slf4j
public class BasicJobsStart {
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;
    public void start(){
        try{
            //Scheduler scheduler = schedulerFactoryBean.getScheduler();
            //--------------cacheJob---------------
            //资产信息定时任务：定时查询资产表的相关信息，更新到AssetCache中 0 */2 * * * ? 2分钟执行一次
//            JobDetail jobDetail_cacheJob = createJobDetail("资产信息定时任务","cacheJob");
//            Trigger trigger_cacheJob = createTrigger(jobDetail_cacheJob,"0 */2 * * * ?");
//            scheduler.scheduleJob(jobDetail_cacheJob,trigger_cacheJob);
            Scheduler scheduler_cacheJob = schedulerFactoryBean.getScheduler();
            JobDetail jobDetail_cacheJob = JobBuilder.newJob(CacheJob.class)
                    .withDescription("资产信息定时任务")
                    .withIdentity("cacheJob", Constant.QUARTZ_JOB_GROUP)
                    .build();
            Trigger trigger_cacheJob = TriggerBuilder.newTrigger()
                    .forJob(jobDetail_cacheJob)
                    .withSchedule(cronSchedule("0 */2 * * * ?"))
                    .build();
            if(!scheduler_cacheJob.checkExists(JobKey.jobKey("cacheJob",Constant.QUARTZ_TRIGGER_GROUP))){
                scheduler_cacheJob.scheduleJob(jobDetail_cacheJob,trigger_cacheJob);
            }
            scheduler_cacheJob.start();
            //--------forcemergeIndexSegmentsJob-----------
            //自动合并index的segments任务  0 0 1 * * ? 每天1点钟执行一次
            Scheduler scheduler_forcemergeIndexSegmentsJob = schedulerFactoryBean.getScheduler();
            JobDetail jobDetail_forcemergeIndexSegmentsJob = JobBuilder.newJob(ForceMergeIndexSegmentsJob.class)
                    .withDescription("自动合并index的segments任务")
                    .withIdentity("forcemergeIndexSegmentsJob", Constant.QUARTZ_JOB_GROUP)
                    .build();
            Trigger trigger_forcemergeIndexSegmentsJob = TriggerBuilder.newTrigger()
                    .forJob(jobDetail_forcemergeIndexSegmentsJob)
                    .withSchedule(cronSchedule("0 0 1 * * ?"))
                    .build();
            if(!scheduler_forcemergeIndexSegmentsJob.checkExists(JobKey.jobKey("forcemergeIndexSegmentsJob",Constant.QUARTZ_TRIGGER_GROUP))){
                scheduler_forcemergeIndexSegmentsJob.scheduleJob(jobDetail_forcemergeIndexSegmentsJob,trigger_forcemergeIndexSegmentsJob);
            }
            scheduler_forcemergeIndexSegmentsJob.start();
            //-------domainJob-------------
            //domain定时入库任务：打开流量后，根据获取的流量包的domain url信息，与资产IP port对应，匹配成功更新资产表的domain字段  0 */2 * * * ?  2分钟执行一次
            Scheduler scheduler_domainJob = schedulerFactoryBean.getScheduler();
            JobDetail jobDetail_domainJob = JobBuilder.newJob(DomainJob.class)
                    .withDescription("domain定时入库任务")
                    .withIdentity("domainJob", Constant.QUARTZ_JOB_GROUP)
                    .build();
            Trigger trigger_domainJob = TriggerBuilder.newTrigger()
                    .forJob(jobDetail_domainJob)
                    .withSchedule(cronSchedule("0 */2 * * * ?"))
                    .build();
            if(!scheduler_domainJob.checkExists(JobKey.jobKey("domainJob",Constant.QUARTZ_TRIGGER_GROUP))){
                scheduler_domainJob.scheduleJob(jobDetail_domainJob,trigger_domainJob);
            }
            scheduler_domainJob.start();
            //-------updateRiskJob-------------
            //资产预警：根据当前资产配置的预警信息，计算预警阈值（高危/中危/低危）并更新到资产库  0 */5 * * * ?  5分钟执行一次
            Scheduler scheduler_updateRiskJob = schedulerFactoryBean.getScheduler();
            JobDetail jobDetail_updateRiskJob = JobBuilder.newJob(UpdateRiskJob.class)
                    .withDescription("domain定时入库任务")
                    .withIdentity("updateRiskJob", Constant.QUARTZ_JOB_GROUP)
                    .build();
            Trigger trigger_updateRiskJob = TriggerBuilder.newTrigger()
                    .forJob(jobDetail_updateRiskJob)
                    .withSchedule(cronSchedule("0 */5 * * * ?"))
                    .build();
            if(!scheduler_updateRiskJob.checkExists(JobKey.jobKey("updateRiskJob",Constant.QUARTZ_TRIGGER_GROUP))){
                scheduler_updateRiskJob.scheduleJob(jobDetail_updateRiskJob,trigger_updateRiskJob);
            }
            scheduler_updateRiskJob.start();
            //-------urlJob-------------
            //url定时入库任务：打开流量后，根据获取的流量包的url信息更新到数据库 服务表（serviceInfo）  0 */6 * * * ?  6分钟执行一次
            Scheduler scheduler_urlJob = schedulerFactoryBean.getScheduler();
            JobDetail jobDetail_urlJob = JobBuilder.newJob(UrlJob.class)
                    .withDescription("url定时入库任务")
                    .withIdentity("urlJob", Constant.QUARTZ_JOB_GROUP)
                    .build();
            Trigger trigger_urlJob = TriggerBuilder.newTrigger()
                    .forJob(jobDetail_urlJob)
                    .withSchedule(cronSchedule("0 */6 * * * ?"))
                    .build();
            if(!scheduler_urlJob.checkExists(JobKey.jobKey("urlJob",Constant.QUARTZ_TRIGGER_GROUP))){
                scheduler_urlJob.scheduleJob(jobDetail_urlJob,trigger_urlJob);
            }
            scheduler_urlJob.start();
        }catch (Exception e){
            log.error("---定时任务启动失败----"+e.getMessage());
        }

    }

    /**
     * 创建quartz 的 job（工作）对象
     * @param description
     * @param name 任务名称
     * @return
     */
    //TODO 参数泛型
//    public  JobDetail createJobDetail(String description, String name,T t){
//        JobDetail jobDetail = JobBuilder.newJob(t.class)
//                .withDescription(description)
//                .withIdentity(name, Constant.QUARTZ_JOB_GROUP)
//                .build();
//        return jobDetail;
//    }

    /**
     * 创建quartz 的 trigger（定时器）对象
     * @param jobDetail
     * @param cron
     * @return
     */
    public Trigger createTrigger(JobDetail jobDetail,String cron){
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withSchedule(cronSchedule(cron))
                .build();
        return trigger;
    }
}

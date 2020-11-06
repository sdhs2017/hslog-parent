package com.jz.bigdata.common.start_execution;

import com.jz.bigdata.business.logAnalysis.collector.service.ICollectorService;
import com.jz.bigdata.common.alert.service.IAlertService;
import com.jz.bigdata.common.asset.service.IAssetService;
import com.jz.bigdata.common.configuration.service.IConfigurationService;
import com.jz.bigdata.common.equipment.service.IEquipmentService;
import com.jz.bigdata.common.start_execution.cache.AssetCache;
import com.jz.bigdata.common.start_execution.cache.ConfigurationCache;
import com.jz.bigdata.common.start_execution.task.BasicJobsStart;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @Author: yiyang
 * @Date: 2020/11/2 15:36
 * @Description: 项目启动时需初始化的内容
 */
@Slf4j
public class start {
    @Resource(name="AssetService")
    private IAssetService assetService;
    @Resource(name = "EquipmentService")
    private IEquipmentService equipmentService;
    @Resource(name = "ConfigurationService")
    private IConfigurationService configurationService;
    @Resource(name = "CollectorService")
    private ICollectorService collectorService;
    @Resource(name = "AlertService")
    private IAlertService iAlertService;
    /**
     * 启动初始化方法
     */
    public void init()  {

        //2.各种cache
        AssetCache.INSTANCE.init(equipmentService,assetService);//资产初始化（虚拟资产and逻辑资产）
        ConfigurationCache.INSTANCE.init(configurationService);//mysql全局配置项初始化
        //TODO search cache初始化，目前其初始化逻辑与数据可视化模块绑定，
        //3.bulk_processor (ES 批量提交)
        collectorService.bulkProcessorInit();
        //4.tomcat重启时，需要重新启动告警模块的正在执行的计划任务
        iAlertService.initAlertQuartz();

        //1.系统自带定时任务,延迟执行60s
//        try{
//            Thread.sleep(60000);
//        }catch(InterruptedException e){
//            log.error("系统自带计划任务延迟失败！"+e.getMessage());
//        }
        //new BasicJobsStart().start();
    }
}

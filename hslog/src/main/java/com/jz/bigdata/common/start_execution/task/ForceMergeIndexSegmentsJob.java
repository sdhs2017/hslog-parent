package com.jz.bigdata.common.start_execution.task;

import com.jz.bigdata.business.logAnalysis.collector.service.ICollectorService;
import com.jz.bigdata.common.manage.service.IManageService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;

public class ForceMergeIndexSegmentsJob implements Job {
    @Resource(name = "manageService")
    private IManageService manageService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        manageService.indexForceMerge();
    }
}
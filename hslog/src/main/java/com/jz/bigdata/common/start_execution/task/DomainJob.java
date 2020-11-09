package com.jz.bigdata.common.start_execution.task;

import com.jz.bigdata.business.logAnalysis.collector.service.ICollectorService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;

public class DomainJob implements Job {
    @Resource(name = "CollectorService")
    private ICollectorService collectorService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        collectorService.insertDomain();
    }
}
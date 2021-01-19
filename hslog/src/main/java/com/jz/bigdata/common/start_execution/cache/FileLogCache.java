package com.jz.bigdata.common.start_execution.cache;

import com.jz.bigdata.common.fileLog.entity.FileLogField;
import com.jz.bigdata.common.fileLog.service.IFileLogService;

import java.util.List;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/1/14 15:47
 * @Description: 文件日志存储到数据库的cache信息
 * 包括模板key，字段信息
 */
public enum FileLogCache {
    INSTANCE;
    //key template_key value：字段信息
    private Map<String, List<FileLogField>> fileLogList;

    /**
     * 初始化方法
     */
    public void init(IFileLogService fileLogService){
        fileLogList = fileLogService.getFileLogInfo();
    }

    public Map<String, List<FileLogField>> getFileLogList() {
        return fileLogList;
    }

    public void setFileLogList(Map<String, List<FileLogField>> fileLogList) {
        this.fileLogList = fileLogList;
    }
}

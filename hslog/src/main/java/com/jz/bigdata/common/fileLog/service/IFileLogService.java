package com.jz.bigdata.common.fileLog.service;

import com.jz.bigdata.common.fileLog.entity.FileLogFields;

import java.io.IOException;
import java.util.List;

/**
 * @Author: yiyang
 * @Date: 2021/1/4 15:48
 * @Description: 文件日志上传模块service接口层
 */
public interface IFileLogService {
    /**
     *
     * @param list 修改后的字段信息
     * @throws Exception
     */
    public void reindex(List<FileLogFields> list) throws Exception;
}

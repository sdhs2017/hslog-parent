package com.jz.bigdata.common.fileLog.service;

import com.hs.elsearch.entity.SearchConditions;
import com.jz.bigdata.common.fileLog.entity.FileLogField;

import java.util.List;
import java.util.Map;

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
    public boolean reindex(List<FileLogField> list,String file_log_templateKey,String file_log_templateName) throws Exception;
    /**
     * 通过模板的key获取模板字段信息
     * @param file_log_templateKey
     * @return
     */
    List<FileLogField> getTemplateInfo(String file_log_templateKey);
    /**
     * 通过模板的key获取模板字段信息，不带系统内置日期字段
     * @param file_log_templateKey
     * @return
     */
    List<FileLogField> getTemplateInfo_without_timestamp(String file_log_templateKey);
    /**
     * 获取模板基础信息，模板名称及key
     * @return
     */
    List<FileLogField> getTemplateList();
    /**
     * 更新文件日志模板信息
     * @param fileLogField
     * @return
     */
    int update(FileLogField fileLogField);

    /**
     * 更新文件日志模板的名称
     * @param file_log_templateKey
     * @param file_log_templateName
     * @return
     */
    boolean updateTemplateName(String file_log_templateKey,String file_log_templateName);

    /**
     * 更新字段信息
     * @param list
     * @param file_log_templateKey
     * @return
     */
    boolean updateFieldsList(List<FileLogField> list,String file_log_templateKey) throws Exception;

    /**
     * 获取所有的文件日志模板数据
     * key为模板的key，value为字段信息list
     * @return
     */
    Map<String,List<FileLogField>> getFileLogInfo();

    /**
     * 插入文件日志模板字段信息
     * @param list
     * @return
     */
    boolean insert(List<FileLogField> list);

    /**
     * 根据key获取其字段信息，并组装成动态表头
     * @param file_log_templateKey
     * @return
     */
    List<Map<String,String>> getTemplateFields(String file_log_templateKey);

    /**
     * 查询模板数据
     * @param searchConditions
     * @return
     */
    public String getTemplateData(SearchConditions searchConditions) throws Exception;
}

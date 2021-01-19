package com.jz.bigdata.common.fileLog.dao;


import com.jz.bigdata.common.fileLog.entity.FileLogField;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description
 */
public interface IFileLogDao {
	/**
	 * 添加文件日志模板信息
	 * @param fileLogField
	 * @return
	 */
	int insert(FileLogField fileLogField);
	/**
	 * 添加文件日志模板信息,list
	 * @param list
	 * @return
	 */
	int insertList(List<FileLogField> list);
	/**
	 * 更新文件日志模板信息
	 * @param fileLogField
	 * @return
	 */
	int update(FileLogField fileLogField);
	/**
	 * 删除模板信息
	 * @param file_log_templateKey
	 * @return
	 */
	int delete(@Param("file_log_templateKey")String file_log_templateKey);

	/**
	 * 通过模板的key获取模板字段信息，前端字段编辑页面不显示系统自带的日期字段
	 * @param file_log_templateKey
	 * @return
	 */
	List<FileLogField> getTemplateInfo_without_timestamp(@Param("file_log_templateKey") String file_log_templateKey);

	/**
	 * 通过模板的key获取模板字段信息
	 * @param file_log_templateKey
	 * @return
	 */
	List<FileLogField> getTemplateInfo(@Param("file_log_templateKey") String file_log_templateKey);

	/**
	 * 获取模板基础信息，模板名称及key
	 * @return
	 */
	List<FileLogField> getTemplateList();

	/**
	 * 更新文件日志模板的名称
	 * @param file_log_templateKey
	 * @param file_log_templateName
	 * @return
	 */
	int updateTemplateName(@Param("file_log_templateKey")String file_log_templateKey,@Param("file_log_templateName")String file_log_templateName);


}

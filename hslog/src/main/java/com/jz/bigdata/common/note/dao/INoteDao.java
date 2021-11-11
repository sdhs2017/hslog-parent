package com.jz.bigdata.common.note.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jz.bigdata.common.note.entity.Note;

/**
 * @author shichengyu
 * @date 2017年11月30日 下午2:23:11
 * @description
 */
public interface INoteDao {
	int insert(Note note);
	
	List<String> count(@Param("account")String account,@Param("startTime")String startTime,@Param("endTime")String endTime);
	
	List<Note> selectLimitNote(@Param("account")String account,@Param("startTime")String startTime,@Param("endTime")String endTime,@Param("size")int size);
	
	List<Note> selectAll(Note note);
	
	int delete(String[] ids);
	
	int deleteAll();
	
	int backup();
	
	List<Note> tableName();
	
	int dropTable();
	
	int restore();
	//涉密，将note_back中的数据还原到note
	//将note_backup表中的数据重新插入note表中
	int restore_security();

	int dropTableNote();
	
	List<Note> selectByPage(@Param("startTime")String startTime,@Param("endTime")String endTime,@Param("account")String account,@Param("userName")String userName,@Param("departmentName")String departmentName,@Param("ip")String ip,@Param("startRecord")int startRecord,@Param("pageSize")int pageSize);

	/**
	 * 带角色的审计日志查询
	 * @param startTime 开始时间
	 * @param endTime 截止时间
	 * @param account
	 * @param userName
	 * @param departmentName
	 * @param ip
	 * @param startRecord
	 * @param pageSize
	 * @param roleids 角色id 数组
	 * @return
	 */
	List<Note> selectByPage_Security(@Param("startTime")String startTime,@Param("endTime")String endTime,@Param("account")String account,@Param("userName")String userName,@Param("departmentName")String departmentName,@Param("ip")String ip,@Param("startRecord")int startRecord,@Param("pageSize")int pageSize,@Param("roleids")String[] roleids);

	List<String> countByPage(@Param("startTime")String startTime,@Param("endTime")String endTime,@Param("account")String account,@Param("userName")String userName,@Param("departmentName")String departmentName,@Param("ip")String ip);

	/**
	 *  带角色的审计日志查询 count
	 * @param startTime
	 * @param endTime
	 * @param account
	 * @param userName
	 * @param departmentName
	 * @param ip
	 * @param roleids 角色id 数组
	 * @return
	 */
	List<String> countByPage_Security(@Param("startTime")String startTime,@Param("endTime")String endTime,@Param("account")String account,@Param("userName")String userName,@Param("departmentName")String departmentName,@Param("ip")String ip,@Param("roleids")String[] roleids);
}

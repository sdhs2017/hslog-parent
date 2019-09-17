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
	
	List<Note> selectLimitNote(@Param("account")String account,@Param("startTime")String startTime,@Param("endTime")String endTime);
	
	List<Note> selectAll(Note note);
	
	int delete(String[] ids);
	
	int deleteAll();
	
	int backup();
	
	List<Note> tableName();
	
	int dropTable();
	
	int restore();
	
	int dropTableNote();
	
	List<Note> selectByPage(@Param("startTime")String startTime,@Param("endTime")String endTime,@Param("account")String account,@Param("userName")String userName,@Param("departmentName")String departmentName,@Param("ip")String ip,@Param("startRecord")int startRecord,@Param("pageSize")int pageSize);
	
	List<String> countByPage(@Param("startTime")String startTime,@Param("endTime")String endTime,@Param("account")String account,@Param("userName")String userName,@Param("departmentName")String departmentName,@Param("ip")String ip);
	
}

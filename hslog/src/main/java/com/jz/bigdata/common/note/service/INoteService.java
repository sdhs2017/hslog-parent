package com.jz.bigdata.common.note.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.jz.bigdata.common.note.entity.Note;

/**
 * @author shichengyu
 * @date 2017年11月30日 下午2:39:34
 * @description
 */
public interface INoteService {
	int insert(Note note);
	
	List<String> count(String account,String startTime,String endTime);
	
	List<Note> selectAll(Note note);
	
	int delete(String[] ids);
	
	int deleteAll();
	
	int backup();
	
	int restore();

	
	String selectByPage(String startTime,String endTime,String account,String userName,String departmentName,String ip,int pageIndex,int pageSize);

	/**
	 * 日志可根据角色查询
	 * @param startTime
	 * @param endTime
	 * @param account
	 * @param userName
	 * @param departmentName
	 * @param ip
	 * @param pageIndex
	 * @param pageSize
	 * @param roleids
	 * @return
	 */
	String selectByPage_Security(String startTime,String endTime,String account,String userName,String departmentName,String ip,int pageIndex,int pageSize,String[] roleids);

}

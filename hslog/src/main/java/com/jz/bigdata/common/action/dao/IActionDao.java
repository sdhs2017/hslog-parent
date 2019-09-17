package com.jz.bigdata.common.action.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jz.bigdata.common.action.entity.Action;

public interface IActionDao {

	int insert(Action action);

	List<Action> selectAll(@Param("role")String role,@Param("userId")String userId);

	int updateById(Action action);

	int delete(String id);
	
	List<Action> selectActionByEventId(String eventId);
	
	List<Action> selectAllByType(String type);

}

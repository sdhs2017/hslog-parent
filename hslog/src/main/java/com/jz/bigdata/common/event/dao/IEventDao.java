package com.jz.bigdata.common.event.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jz.bigdata.common.event.entity.Event;

public interface IEventDao {
	int insert(Event event);

	List<Event> selectAll(@Param("role")String role,@Param("userId")String userId);

	int updataById(Event event);

	int delete(String id);
	
	List<Event> selectEventByActionId(String actionId);
	
	Event selectEvent(String id);
	
	

}

package com.jz.bigdata.common.action_event.dao;

import java.util.List;

import com.jz.bigdata.common.action_event.entity.Action_event;

public interface IAction_eventDao {
	
	int insert(List<Action_event> action_event);
	
	int deleteByEventId(String eventId);
	
	List<Action_event> selectAll();
	
	int deleteByActionId(String actionId);

}

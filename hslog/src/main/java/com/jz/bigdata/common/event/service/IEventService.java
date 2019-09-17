package com.jz.bigdata.common.event.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.jz.bigdata.common.event.entity.Event;

public interface IEventService {
	
	int insert(Event event);

	List<Event> selectAll(HttpSession session);

	int updataById(Event event);

	int delete(String id);
	
	List<Event> selectEventByActionId(String actionId);
	
	Map<String,Object> selectEventAction(String id);

}

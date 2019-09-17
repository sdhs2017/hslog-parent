package com.jz.bigdata.common.action.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.jz.bigdata.common.action.entity.Action;

public interface IActionService {
	
	int insert(Action action);

	List<Action> selectAll(HttpSession session);

	int updateById(Action action);

	int delete(String id);
	
	List<Action> selectActionByEventId(String eventId);
	
	List<Action> selectAllByType(String type);

}

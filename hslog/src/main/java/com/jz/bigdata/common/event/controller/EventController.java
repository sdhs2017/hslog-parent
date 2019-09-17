package com.jz.bigdata.common.event.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.event.entity.Event;
import com.jz.bigdata.common.event.service.IEventService;
import com.jz.bigdata.util.DescribeLog;

/**
 * @ClassName EventController
 * @Description
 * @Author shi cheng yu
 * @Date 2018年9月25日 上午10:57:51
 */
@Controller
@RequestMapping("/event")
public class EventController {

	@Resource(name = "EventService")
	private IEventService eventService;

	/**
	 * @param request
	 * @param event
	 * @return 添加事件
	 */
	@ResponseBody
//	@RequestMapping("/insert")
	@RequestMapping(value="/insert",produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "添加事件")
	public String insert(HttpServletRequest request, Event event) {

		// 结果一般命名为result
		int result = 0;
		result = eventService.insert(event);
		return result >= 1 ? Constant.successMessage() : Constant.failureMessage();
	}

	/**
	 * @param request
	 * @param department
	 * @return 查询信息
	 */
	@ResponseBody
//	@RequestMapping("/selectAll")
	@RequestMapping(value="/selectAll",produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "查询所有事件信息")
	public List<Event> selectAll(HttpServletRequest request,HttpSession session) {

		// 结果一般命名为result
		// int result = 0;
		// result=departmentService.insert(department);
		return eventService.selectAll(session);
	}

	/**
	 * @param request
	 * @param department
	 * @return 修改信息
	 */
	@ResponseBody
//	@RequestMapping("/updataById")
	@RequestMapping(value="/updataById",produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "修改事件信息")
	public String updataById(HttpServletRequest request, Event event) {

		// 结果一般命名为result
		int result = 0;
		result = eventService.updataById(event);
		return result >= 1 ? Constant.successMessage() : Constant.failureMessage();
	}

	/**
	 * @param request
	 * @return 根据id删除信息
	 */
	@ResponseBody
//	@RequestMapping("/delete")
	@RequestMapping(value="/delete",produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "删除事件")
	public String delete(HttpServletRequest request) {

		// 结果一般命名为result
		int result = 0;
		String id = request.getParameter("id");
		result = eventService.delete(id);
		return result >= 1 ? Constant.successMessage() : Constant.failureMessage();
	}

	/**
	 * @param request
	 * @param department
	 * @return 查询信息
	 */
	@ResponseBody
//	@RequestMapping("/selectEventByActionId")
	@RequestMapping(value="/selectEventByActionId",produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "根据动作id查询所有事件")
	public List<Event> selectEventByActionId(HttpServletRequest request) {
		String actionId = request.getParameter("actionId");
		return eventService.selectEventByActionId(actionId);
	}

	
	/**
	 * @param request
	 * @param department
	 * @return 修改时查询相关数据
	 */
	@ResponseBody
//	@RequestMapping("/selectEventAction")
	@RequestMapping(value="/selectEventAction",produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "修改时查询相关数据")
	public Map<String, Object> selectEventAction(HttpServletRequest request) {
		String id = request.getParameter("id");
		return eventService.selectEventAction(id);
	}
	
}

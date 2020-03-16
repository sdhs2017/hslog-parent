package com.jz.bigdata.common.action.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.action.entity.Action;
import com.jz.bigdata.common.action.service.IActionService;
import com.jz.bigdata.util.DescribeLog;

/**
 * @ClassName ActionController
 * @Description 
 * @Author shi cheng yu
 * @Date 2018年9月26日 下午2:51:12
 */
@Controller
@RequestMapping("/action")
public class ActionController {
	
	@Resource(name="ActionService")
	private IActionService actionService;
	
	@ResponseBody
//	@RequestMapping("/insert")
	@RequestMapping(value="/insert",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="添加动作")
	public String insert(HttpServletRequest request,Action action){

		//结果一般命名为result
		int result = 0;
		result=actionService.insert(action);
		return result>=1?Constant.successMessage():Constant.failureMessage();
	}
	
	
	/**
	 * @param request
	 * @param session
	 * @return 查询信息
	 */
	@ResponseBody
//	@RequestMapping("/selectAll")
	@RequestMapping(value="/selectAll",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="查询所有动作信息")
	public List<Action> selectAll(HttpServletRequest request,HttpSession session){

		//结果一般命名为result
//		int result = 0;
//		result=departmentService.insert(department);
		return actionService.selectAll(session);
	}
	
	/**
	 * @param request
	 * @param department
	 * @return 修改信息
	 */
	@ResponseBody
//	@RequestMapping("/updateById")
	@RequestMapping(value="/updateById",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="修改动作信息")
	public String updateById(HttpServletRequest request,Action action){

		//结果一般命名为result
		int result = 0;
		result=actionService.updateById(action);
		return result>=1?Constant.successMessage():Constant.failureMessage();
	}

	
	/**
	 * @param request
	 * @return
	 * 根据id删除信息
	 */
	@ResponseBody
//	@RequestMapping("/delete")
	@RequestMapping(value="/delete",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="删除动作")
	public String delete(HttpServletRequest request){

		//结果一般命名为result
		int result = 0;
		String id =request.getParameter("id");
		result=actionService.delete(id);
		return result >= 1 ? Constant.successMessage() : Constant.failureMessage();
	}
	
	
	/**
	 * @param request
	 * @return
	 * 根据事件id查询动作
	 */
	@ResponseBody
//	@RequestMapping("/selectActionByEventId")
	@RequestMapping(value="/selectActionByEventId",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="根据事件id查询动作")
	public List<Action> selectActionByEventId(HttpServletRequest request){

		//结果一般命名为result
		String eventId =request.getParameter("eventId");
		return actionService.selectActionByEventId(eventId);
	}
	
	@ResponseBody
//	@RequestMapping("/selectActionByEventId")
	@RequestMapping(value="/selectAllByType",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="根据动作日志类型查询动作")
	public List<Action> selectAllByType(HttpServletRequest request){

		//结果一般命名为result
		String type =request.getParameter("type");
		return actionService.selectAllByType(type);
	}

}

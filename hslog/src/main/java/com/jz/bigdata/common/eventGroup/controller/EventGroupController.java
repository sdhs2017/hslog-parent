package com.jz.bigdata.common.eventGroup.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jz.bigdata.common.eventGroup.entity.EventGroup;
import com.jz.bigdata.common.eventGroup.service.IEventGroupService;
import com.mysql.jdbc.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.util.DescribeLog;


/**
 * @description
 */
@Slf4j
@Controller
@RequestMapping("/eventGroup")
public class EventGroupController {

	@Resource(name = "EventGroupService")
	private IEventGroupService eventGroupService;

	@ResponseBody
	@RequestMapping(value="/insert.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="添加事件组")
	public String insert(HttpServletRequest request, EventGroup eventGroup, HttpSession session) {

		try{
			boolean	result = this.eventGroupService.insert(eventGroup,session);
			if(result){
				return Constant.successMessage();
			}else{
				return Constant.failureMessage("事件组添加失败！");
			}
		}catch(Exception e){
			return handleException(e);
		}
	}

	/**
	 * @param request
	 * @return 根据id删除数据
	 */
	@ResponseBody
	@RequestMapping(value="/delete.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="删除事件组")
	public String delete(HttpServletRequest request) {
		try{
			String id = request.getParameter("event_group_id");
			boolean	result = this.eventGroupService.delete(id);
			if(result){
				return Constant.successMessage();
			}else{
				return Constant.failureMessage("事件组删除失败！");
			}
		}catch(Exception e){
			return Constant.failureMessage("事件组删除失败！");
		}
	}


	/**
	 * 编辑事件组，查询单个事件组详情
	 * @param request
	 * @param eventGroup
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getEventGroupInfoById.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="查询单个事件组详情")
	public String getEventGroupInfoById(HttpServletRequest request,EventGroup eventGroup, HttpSession session){
		try{
			//事件组id
			String event_group_id = request.getParameter("event_group_id");
			EventGroup result = eventGroupService.getEventGroupInfoById(event_group_id);
			return Constant.successData(JSONObject.toJSON(result).toString());
		}catch (Exception e){
			return Constant.failureMessage("事件组信息查询失败！");
		}
	}
	/**
	 * 根据查询条件，查询事件组
	 * @param request
	 * @param eventGroup
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getEventGroupInfoByCondition.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="查询事件组")
	public String getEventGroupInfoByCondition(HttpServletRequest request,EventGroup eventGroup, HttpSession session){
		try{
			//分页信息处理
			if(eventGroup.getPageIndex()!=null&&eventGroup.getPageSize()!=null){
				eventGroup.setStartRecord((eventGroup.getPageSize() * (eventGroup.getPageIndex() - 1)));
			}
			Map<String, Object> result = eventGroupService.getEventGroupInfoByCondition(eventGroup);
			return Constant.successData(JSONArray.toJSON(result).toString());
		}catch (Exception e){
			return Constant.failureMessage("事件组信息查询失败！");
		}
	}



	/**
	 * @param request
	 * @param eventGroup
	 * @param session
	 * @return 修改事件组
	 */
	@ResponseBody
	@RequestMapping(value="/update.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="修改事件组")
	public String update(HttpServletRequest request, EventGroup eventGroup,HttpSession session) {
		try{
			//判定资产组id是否存在
			if(StringUtils.isNullOrEmpty(eventGroup.getEvent_group_id())){
				return Constant.failureMessage("事件组信息修改失败！");
			}
			boolean	result = this.eventGroupService.updateById(eventGroup,session);
			if(result){
				return Constant.successMessage();
			}else{
				return Constant.failureMessage("事件组信息修改失败！");
			}
		}catch(Exception e){
			return handleException(e);
		}
	}

	/**
	 * @param request
	 * @return 获取资产列表
	 */
	@ResponseBody
	@RequestMapping(value="/getEventGroupList.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取事件组列表")
	public String getEventGroupList(HttpServletRequest request) {
		try{
			List<Map<String,String>> result = this.eventGroupService.getEventGroupList();
			return Constant.successData(JSONArray.toJSONString(result));
		}catch(Exception e){
			return Constant.failureMessage("获取信息失败！");
		}
	}
	/**
	 * 通过日志类型获取事件列表，syslog/winlog，穿梭框使用
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getEventListByType.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取事件列表")
	public String getEventListByType(HttpServletRequest request) {
		try{
			String log_type = request.getParameter("log_type");
			List<Map<String,Object>> result = this.eventGroupService.getEventList(log_type);
			return Constant.successData(JSONArray.toJSONString(result));
		}catch(Exception e){
			return Constant.failureMessage("获取信息失败！");
		}
	}
	/**
	 * 通过日志类型获取事件列表，syslog/winlog,combobox使用，key为id  value为名称（英文）
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getEventListByType4Combobox.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取事件列表")
	public String getEventListByType4Combobox(HttpServletRequest request) {
		try{
			String log_type = request.getParameter("log_type");
			List<Map<String,Object>> result = this.eventGroupService.getEventList4Combobox(log_type);
			return Constant.successData(JSONArray.toJSONString(result));
		}catch(Exception e){
			return Constant.failureMessage("获取信息失败！");
		}
	}
    /**
     * 通过日志类型获取事件列表，syslog/winlog,combobox使用,key/value都是事件名称(英文)
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getEventListByType4Combobox_equal.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="获取事件列表")
    public String getEventListByType4Combobox_equal(HttpServletRequest request) {
        try{
            String log_type = request.getParameter("log_type");
            List<Map<String,Object>> result = this.eventGroupService.getEventList4Combobox_equal(log_type);
            return Constant.successData(JSONArray.toJSONString(result));
        }catch(Exception e){
            return Constant.failureMessage("获取信息失败！");
        }
    }
	/**
	 * 通过资产组的id获取事件列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getEventListByEventGroupId.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取事件列表")
	public String getEventListByEventGroupId(HttpServletRequest request) {
		try{
			String log_type = request.getParameter("event_group_id");
			List<Map<String,Object>> result = this.eventGroupService.getEventList(log_type);
			return Constant.successData(JSONArray.toJSONString(result));
		}catch(Exception e){
			return Constant.failureMessage("获取信息失败！");
		}
	}
	/**
	 * 事件组增改异常处理，事件组组名称重复问题
	 * @param e
	 * @return
	 */
	private String handleException(Exception e){
		Throwable throwable = e.getCause();
		Throwable[] throwableList = e.getSuppressed();
		if(null!=throwable){
			String throwableInfo = throwable.toString();
			if(throwableInfo.indexOf("UNIQUE_EVENT_GROUP_NAME")>=0){
				log.error("事件组名称重复："+e.getMessage());
				return Constant.failureMessage("事件组名称重复！");
			}else{
				log.error("事件组添加失败"+e.getMessage());
				return Constant.failureMessage("事件组信息添加失败！");
			}
		}else if(null!=throwableList){
			//返回的异常可能是一个数组
			String throwables = Arrays.toString(throwableList);
			if(throwables.indexOf("UNIQUE_EVENT_GROUP_NAME")>=0){
				log.error("事件组名称重复："+e.getMessage());
				return Constant.failureMessage("事件组名称重复！");
			}else{
				log.error("事件组添加失败"+e.getMessage());
				return Constant.failureMessage("事件组信息添加失败！");
			}
		}else{
			return Constant.failureMessage("事件组信息添加失败！");
		}
	}

}

package com.jz.bigdata.common.alarm.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.common.alarm.entity.Alarm;
import com.jz.bigdata.common.alarm.service.IAlarmService;
import com.jz.bigdata.util.DescribeLog;

/**
 * @author shichengyu
 * @date 2018年2月28日 下午1:58:56
 * @description
 */
@Controller
@RequestMapping("/alarm")
public class AlarmController {
	
	@Resource(name="AlarmService")
	private IAlarmService alarmService;
	
	/**
	 * @return 查询事件信息
	 */
	@ResponseBody
	@RequestMapping("/selectAll")
	@DescribeLog(describe="查询所有警告信息")
	public List<Alarm> selectAll(){
		return alarmService.selectAll();
	}
	

	
	/**
	 * @return 查询事件信息
	 */
	@ResponseBody
	@RequestMapping(value="/updateById",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="修改警告信息")
	public String  updateById(Alarm alarm){
		return alarmService.updateById(alarm);
	}
}

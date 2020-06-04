package com.jz.bigdata.common.safeStrategy.controller;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.business.logAnalysis.log.service.impl.LogServiceImpl;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.safeStrategy.entity.SafeStrategy;
import com.jz.bigdata.common.safeStrategy.service.ISafeStrategyService;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.DescribeLog;
import com.jz.bigdata.util.Uuid;

/**
 * @author shichengyu
 * @date 2018年3月31日 上午11:21:19
 * @description
 */
@Controller
@RequestMapping("/safeStrategy")
public class SafeStrategyController {
	
	@Resource(name = "SafeStrategyService")
	private ISafeStrategyService safeStrategyService;
	
	@Resource(name = "logService")
	private LogServiceImpl logService;
	
	@Resource(name ="configProperty")  
    private ConfigProperty configProperty;

	/**
	 * @param request
	 * @param safeStrategy
	 * @return 添加数据 修改数据
	 */
	@ResponseBody
//	@RequestMapping("/inserts")
	@RequestMapping(value="/insert", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="添加或修改安全策略")
	public String insert(HttpServletRequest request, SafeStrategy safeStrategy) {
		int result = 0;
		
		int time=0;
		String time_interval="";
		if(safeStrategy.getMonth()!=null&&!safeStrategy.getMonth().equals("")){
			time=Integer.valueOf(safeStrategy.getMonth())*30*24*60;
			time_interval=safeStrategy.getMonth()+"-";
		}else{
			time_interval="00-";
		}
		if(safeStrategy.getDay()!=null&&!safeStrategy.getDay().equals("")){
			time=Integer.valueOf(safeStrategy.getDay())*24*60+time;
			time_interval=time_interval+safeStrategy.getDay()+"-";
		}else{
			time_interval=time_interval+"00-";
		}
		if(safeStrategy.getHour()!=null&&!safeStrategy.getHour().equals("")){
			time=Integer.valueOf(safeStrategy.getHour())*60+time;
			time_interval=time_interval+safeStrategy.getHour()+"-";
		}else{
			time_interval=time_interval+"00-";
		}
		if(safeStrategy.getMinute()!=null&&!safeStrategy.getMinute().equals("")){
			time=Integer.valueOf(safeStrategy.getMinute())+time;
			time_interval=time_interval+safeStrategy.getMinute();
		}else{
			time_interval=time_interval+"00";
		}
		safeStrategy.setTime(time+"");
		safeStrategy.setTime_interval(time_interval);
		
//		int tt=1/0;
//		判断id是否为空
		if (safeStrategy.getId() == null || safeStrategy.getId().isEmpty()) {
			safeStrategy.setId(Uuid.getUUID());
			//TODO 通过设置唯一键 在insert时对触发异常的进行捕获判定，实现同样的去重效果。并且只访问数据库一次
			List<SafeStrategy> list=safeStrategyService.selectByequipmentIdEventType(safeStrategy);
			if(list.size()>0){
				return Constant.dataMessage();
			}
//			添加数据
			result = this.safeStrategyService.insert(safeStrategy);
		} else {
			// 更新操作
			result = this.safeStrategyService.updateById(safeStrategy);
		}
		
		String index = configProperty.getEs_index();
    	String types [] = null;
    	Date enddate = new Date();
		try {
			logService.getEventstypeCountByEquipmentid(index, types, safeStrategy.getEquipmentId(), enddate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result >= 1 ? Constant.successMessage() : Constant.failureMessage();
	}
	
	/**
	 * @param equipmentId
	 * @return
	 * 查询资产安全策略
	 */
	@ResponseBody
//	@RequestMapping("/inserts")
	@RequestMapping(value="/selectByEquipmentId", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="查询资产安全策略")
	public List<SafeStrategy> selectByEquipmentId(String equipmentId){
//		String equipmentId = request.getParameter("equipmentId");
		System.out.println(equipmentId);
		return safeStrategyService.selectByEquipmentId(equipmentId);		
	}

	
	/**
	 * @param request
	 * @return 根据id删除安全策略
	 */
	@ResponseBody
	@RequestMapping("/deletes")
	@DescribeLog(describe="删除用户")
	public String delete(HttpServletRequest request, Equipment equipment) {
		int result = 0;
//		获取id数组
		String[] ids = request.getParameter("ids").split(",");
		if (ids.length > 0) {
			result = this.safeStrategyService.delete(ids);
		}
		
		String index = configProperty.getEs_index();
    	String types [] = null;
    	Date enddate = new Date();
		try {
			logService.getEventstypeCountByEquipmentid(index, types, equipment.getId(), enddate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result >= 1 ? Constant.successMessage() : Constant.failureMessage();
	}
	
	
	/**
	 * @param request
	 * @return 根据id查询安全策略
	 */
	@ResponseBody
	@RequestMapping("/selectById")
	@DescribeLog(describe="根据id查询安全策略")
	public List<SafeStrategy> selectById(HttpServletRequest request) {
		
		String id = request.getParameter("id");
		System.out.println(id);
		
		return this.safeStrategyService.selectById(id);
	}
}

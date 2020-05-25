package com.jz.bigdata.common.equipment.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.hs.elsearch.dao.logDao.ILogCrudDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.equipment.service.IEquipmentService;
import com.jz.bigdata.components.kafka.logAnalysis.SysLogKafkaConsumer;
//import com.jz.bigdata.framework.spring.es.elasticsearch.ClientTemplate;
//import com.hs.elsearch.template.bak.ClientTemplate;
import com.jz.bigdata.util.DescribeLog;


/**
 * @author shichengyu
 * @date 2017年8月16日 下午2:40:21
 * @description
 */
@Controller
@RequestMapping("/equipment")
public class EquipmentController {
	
	@Resource(name = "EquipmentService")
	private IEquipmentService equipmentService;
	
	
	@Autowired protected ILogCrudDao logCrudDao;

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * @param request
	 * @param equipment
	 * @param session
	 * @return 添加/修改资产
	 */
	@ResponseBody
	@RequestMapping(value="/upsert.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="添加/修改资产")
	public String upsert(HttpServletRequest request, Equipment equipment,HttpSession session) {
		try{
			return this.equipmentService.upsert(equipment,session);
		}catch (Exception e){
			logger.error(e.getMessage());
			return Constant.failureMessage("操作失败！");
		}

	}

	/**
	 * @param request
	 * @return 根据id删除数据
	 */
	@ResponseBody
//	@RequestMapping("/delete")
	@RequestMapping(value="/delete.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="删除资产")
	public String delete(HttpServletRequest request) {
		try{
			int result = 0;
//		获取id数组
			String[] ids = request.getParameter("id").split(",");
//		数组长度大于0删除数据
			if (ids.length > 0) {
				result = this.equipmentService.delete(ids);
			}
			return result >= 1 ? Constant.successMessage() : Constant.failureMessage();
		}catch(Exception e){
			logger.error("删除资产"+e.getMessage());
			return Constant.failureMessage();
		}

	}

	
	/**
	 * @param request
	 * @param equipment
	 * @return 查询数据
	 */
	@ResponseBody
//	@RequestMapping("/selectAll")
	@RequestMapping(value="/selectAll.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="查询所有资产")
	public String selectAll(HttpServletRequest request, Equipment equipment,HttpSession session){
		try{
			return equipmentService.selectAll(equipment,session);
		}catch (Exception e){
			logger.error("查询所有资产"+e.getMessage());
			return Constant.failureMessage();
		}

	}
	
	/**
	 * @param request
	 * @param equipment
	 * @return
	 * 查询单个实体
	 */
	@ResponseBody
//	@RequestMapping("/selectEquipment")
	@RequestMapping(value="/selectEquipment.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="查询单个资产")
	public List<Equipment> selectEquipment(HttpServletRequest request, Equipment equipment) {
		List<Equipment> list= null;
		try {
			list = this.equipmentService.selectEquipment(equipment);
		} catch (Exception e) {
			logger.error("查询单个资产：失败"+e.getMessage());
		}
		return list;
	}

	
	/**
	 * @param request
	 * @param session
	 * @return 分页测试例子
	 */
	@ResponseBody
//	@RequestMapping("/selectPage")
	@RequestMapping(value="/selectPage.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="分页查询资产")
	public String selectPage(HttpServletRequest request,HttpSession session) {
		//页码数
		int pageIndex=Integer.parseInt(request.getParameter("pageIndex"));
		//每页显示的数量
		int pageSize=Integer.parseInt(request.getParameter("pageSize"));
		//主机名
		String hostName=request.getParameter("hostName");
		//名字
		String name=request.getParameter("name");
		//ip地址
		String ip=request.getParameter("ip");
		//日志类型
		String logType =request.getParameter("logType");
		// 资产类型
		String type = request.getParameter("type");

		String result = "";
		try {
			result = equipmentService.selectAllByPage(hostName,name,ip,logType,type, pageIndex, pageSize,session);
			logger.info("查询资产：成功");
		} catch (Exception e) {
			logger.error("查询资产：失败"+e.getMessage());
		}
		return result;
	}
	
	/**
	 * @param request
	 * @param equipment
	 * @return
	 * 查询单个实体
	 */
	@ResponseBody
//	@RequestMapping("/selectEquipment")
	@RequestMapping(value="/selectEquipmentByLog.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="日志跳转资产")
	public Map<String, Object> selectEquipmentByLog(HttpServletRequest request, Equipment equipment) {
		List<Equipment> list= null;
		try {
			list = this.equipmentService.selectEquipment(equipment);
		} catch (Exception e) {
			logger.error("查询资产：失败！");
			e.printStackTrace();
		}
		Map<String,Object> map =new HashMap<>();
		if(list.size()>0){
			map.put("success", "true");
			map.put("message", "查询成功");
			map.put("equipment", list);
			return map;
		}else{
			map.put("success", "false");
			map.put("message", "资产已删除");
			map.put("equipment", "");
			return map;
		}
	}
	@ResponseBody
	@RequestMapping(value="/checkNameUnique.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="验证资产名称是否重复")
	public String checkNameUnique(HttpServletRequest request, Equipment equipment) {

		try {
			if(this.equipmentService.checkNameUnique(equipment)){
				return Constant.failureMessage("资产名称重复，请重新输入！");
			}else{
				return Constant.successMessage();
			}
		} catch (Exception e) {
			logger.error("资产名称重复性查询异常！");
			e.printStackTrace();
			return Constant.failureMessage("资产重复，请重新输入！");
		}
	}
	@ResponseBody
	@RequestMapping(value="/checkIpAndLogTypeUnique.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="验证资产ip+日志类型是否重复")
	public String checkIpAndLogTypeUnique(HttpServletRequest request, Equipment equipment) {

		try {
			if(this.equipmentService.checkIpAndLogTypeUnique(equipment)){
				return Constant.failureMessage("资产“IP+日志类型”重复，请重新输入！");
			}else{
				return Constant.successMessage();
			}
		} catch (Exception e) {
			logger.error("资产名称重复性查询异常！");
			e.printStackTrace();
			return Constant.failureMessage("资产重复，请重新输入！");
		}
	}
	/**
	 * 获取id
	 * @param request
	 * @param equipment
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/insertBeathLogByEquipmentId")
//	@DescribeLog(describe="添加部门")
	public String getId(HttpServletRequest request,Equipment equipment){
//		equipment=equipmentService.selectEquipment(equipment);
////		System.out.println(equipment.getId());
////		System.out.println(equipment.getUserId());
////		System.out.println(equipment.getDepartmentId());
//		
//		KafkaConsumer m = new KafkaConsumer(equipment,clientTemplate);
//		Thread t = new Thread(m);
//		t.start();
//		equipment=this.equipmentService.selectOneEquipment(equipment);
//		System.out.println(equipment.getId());
//		System.out.println(equipment.getUserId());
//		System.out.println(equipment.getDepartmentId());
//		System.out.println(equipment.getId());
//		int num =0;
		/**
		 * TODO
		 * 返回值为空：未实现
		 * 返回值为SNMP：未实现
//		 */
//		if(EquipmentConstant.LOGTYPE_SYSLOG==equipment.getLogType()){
//			SysLogKafkaConsumer m = new SysLogKafkaConsumer(equipment,clientTemplate);
//			Thread t = new Thread(m);
//			t.start();
//			return Constant.successMessage();
//		}else if(EquipmentConstant.LOGTYPE_WMI==equipment.getLogType()){
//			WinLogKafkaConsumer m = new WinLogKafkaConsumer(equipment,clientTemplate);
//			Thread t = new Thread(m);
//			t.start();
//			return Constant.successMessage();
//		}else if(EquipmentConstant.LOGTYPE_SNMP==equipment.getLogType()){
//			//TODO
//			return Constant.successMessage();
//		}else if(EquipmentConstant.LOGTYPE_LOG4J==equipment.getLogType()){
//			Log4jKafkaConsumer m = new Log4jKafkaConsumer(equipment,clientTemplate);
//			Thread t = new Thread(m);
//			if(num == 0){
//				
//				t.start();
//				return Constant.successMessage();
//				
//			}else if(num == 1){
//				t.suspend();
//			}else if(num == 2){
//				t.resume();
//				
//				t.interrupt();
//			}else if(num ==3){
//				t.stop();
//			}
//			Log4jKafkaConsumer m = new Log4jKafkaConsumer(equipment,clientTemplate);
//			Thread t = new Thread(m);
//			t.start();
//			return Constant.successMessage();
//		}else{
//			SysLogKafkaConsumer m = new SysLogKafkaConsumer(equipment,clientTemplate);
//			Thread t = new Thread(m);
//			t.start();
//			return Constant.successMessage();
//			//TODO
////			return  Constant.failureMessage();
//		}
		
		SysLogKafkaConsumer m = new SysLogKafkaConsumer(equipment,logCrudDao);
		Thread t = new Thread(m);
		t.start();
		return Constant.successMessage();
		
		
		//TODO
		/**
		 * 处理成功、失败，返回条件
		 */
//		return null;
		
//		KafkaConsumer m = new KafkaConsumer(equipment,clientTemplate);
//		Thread t = new Thread(m);
//		t.start();
//		return null;
		
	}

}

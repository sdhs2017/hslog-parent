package com.jz.bigdata.common.equipment.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.common.base.Strings;
import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.jz.bigdata.util.BASE64Util;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.POI.ReadExcel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.equipment.service.IEquipmentService;
import com.jz.bigdata.components.kafka.logAnalysis.SysLogKafkaConsumer;
//import com.jz.bigdata.framework.spring.es.elasticsearch.ClientTemplate;
//import com.hs.elsearch.template.bak.ClientTemplate;
import com.jz.bigdata.util.DescribeLog;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


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

	@Resource(name = "configProperty")
	private ConfigProperty configProperty;

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public final static HashMap<String,String> equipment_type = new HashMap<>();
	static{
		equipment_type.put("交换机", "0101");
		equipment_type.put("路由器", "0102");
		equipment_type.put("IPS", "0201");
		equipment_type.put("IDS", "0202");
		equipment_type.put("抗DDOS", "0203");
		equipment_type.put("防火墙", "0204");
		equipment_type.put("Windows", "0301");
		equipment_type.put("Linux", "0302");
		equipment_type.put("虚拟机", "0303");
		equipment_type.put("Tomcat", "0401");
		equipment_type.put("Apache", "0402");
		equipment_type.put("IIS", "0403");
		equipment_type.put("Weblogic", "0404");
		equipment_type.put("Mysql", "0405");
		equipment_type.put("Oracle", "0406");
		equipment_type.put("Sqlserver", "0407");
		equipment_type.put("Db2", "0408");
		equipment_type.put("Nginx", "0409");
	}

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
	 * @return 查询数据
	 */
	@ResponseBody
//	@RequestMapping("/selectAll")
	@RequestMapping(value="/selectRisk.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="查询所有存在中、高危事件的资产")
	public String selectRisk(HttpServletRequest request){
		try{
			return equipmentService.selectRisk();
		}catch (Exception e){
			logger.error("查询所有存在中、高危事件的资产"+e.getMessage());
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

	@ResponseBody
	@RequestMapping(value="equipmentUpload")
	@DescribeLog(describe="资产文件上传")
	public String equipmentUpload(MultipartHttpServletRequest request,HttpSession session) {

		Iterator<String> fileNames = request.getFileNames();
		String filePath ="";
		while (fileNames.hasNext()) {

			//把fileNames集合中的值打出来
			String fileName=fileNames.next();

			/*
			 * request.getFiles(fileName)方法即通过fileName这个Key, 得到对应的文件
			 * 集合列表. 只是在这个Map中, 文件被包装成MultipartFile类型
			 */
			List<MultipartFile> fileList=request.getFiles(fileName);

			if (fileList.size()>0) {

				//遍历文件列表
				Iterator<MultipartFile> fileIte=fileList.iterator();

				while (fileIte.hasNext()) {

					//获得每一个文件
					MultipartFile multipartFile=fileIte.next();

					//获得原文件名
					String originalFilename = multipartFile.getOriginalFilename();
                    System.out.println("originalFilename: "+originalFilename);
					if(!originalFilename.equals("资产清单.xlsx")&&!originalFilename.equals("资产清单.xls")){
						return "文件名称或者文件类型有误，请确认！文件名称：资产清单，文件类型为Excel";
					}
					//设置保存路径.
					String path =getClass().getClassLoader().getResource("").getFile();
                    System.out.println(path);

					//检查该路径对应的目录是否存在. 如果不存在则创建目录
					File dir=new File(path);
					if (!dir.exists()) {
						dir.mkdirs();
					}

					filePath = path + originalFilename;
                    System.out.println("filePath: "+filePath);

					//保存文件
					File dest = new File(filePath);
					if (!(dest.exists())) {
						/*
						 * MultipartFile提供了void transferTo(File dest)方法,
						 * 将获取到的文件以File形式传输至指定路径.
						 */
						try {
							multipartFile.transferTo(dest);
						} catch (Exception e) {
							// TODO: handle exception
							return "资产清单上传失败！";
						}

					} else {
						if(dest.delete()) {
							try {
								multipartFile.transferTo(dest);
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								return "资产清单上传失败！";
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								return "资产清单上传失败！";
							}
						}
					}

					//MultipartFile也提供了其他一些方法, 用来获取文件的部分属性

					//获取文件contentType
					String contentType=multipartFile.getContentType();
//                    System.out.println("contentType: "+contentType);

					/*
					 * 获取name
					 * 其实这个name跟上面提到的getFileName值是一样的,
					 * 就是Map中Key的值. 即前台页面<input>中name=""
					 * 属性. 但是上面的getFileName只是得到这个Map的Key,
					 * 而Spring在处理上传文件的时候会把这个值以name属性
					 * 记录到对应的每一个文件. 如果需要从文件层面获取这个
					 * 值, 则可以使用该方法
					 */
					String name=multipartFile.getName();
//                    System.out.println("name: "+name);

					//获取文件大小, 单位为字节
					long size=multipartFile.getSize();
//                    System.out.println("size: "+size);

				}
			}
		}
		return importExcel(session,filePath);
	}

	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	@RequestMapping(value="importExcel")
	@DescribeLog(describe="导入excel")
	public String importExcel(HttpSession session,String filepath) {

		HashMap<String, String> result = new HashMap<>();
		if (Strings.isNullOrEmpty(filepath)){
			return Constant.failureMessage("资产清单文件为空，请确认！");
		}
		ReadExcel readExcel= new ReadExcel();

		//String filepath = "D:\\Computer_Science\\Java\\hslog-parent\\hslog\\target\\hslog\\WEB-INF\\classes\\资产清单.xlsx";
		List<List<String>> equipments = readExcel.getExcelInfo(filepath);

		boolean commit = true;
		BASE64Util base64Util = new BASE64Util();
		if (equipments.size()>Integer.valueOf(base64Util.decode(configProperty.getNumber()).trim())){
			return Constant.failureMessage("导入失败，资产清单中资产数超过了单节点支撑的300台资产，实际资产数为"+equipments.size());
		}
		for (List<String> list : equipments){
			Equipment equipment =new Equipment();
			equipment.setName(list.get(0));
			equipment.setIp(list.get(1));
			equipment.setHostName(list.get(2));
			equipment.setLogType(list.get(3));
			equipment.setLog_level(list.get(4));
			//主机类型
			if (!Strings.isNullOrEmpty(equipment_type.get(list.get(5)))){
				equipment.setType(equipment_type.get(list.get(5)));
			}else {
				// 直接退出，回滚操作
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return Constant.failureMessage("导入失败，资产类型错误："+list);
			}
			// 是否启用，默认启用1
			if (list.get(6).equals("否")){
				equipment.setStartUp(0);
			}else {
				equipment.setStartUp(1);
			}

			try {
				switch (equipmentService.batchInsert(equipment,session)){
					case 1:
						break;
					case 0:
						TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
						return Constant.failureMessage("资产导入失败，失败资产信息："+list);
					case 3:
						TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
						return Constant.failureMessage("资产达到上限300，无法添加！");
				}

			} catch (DataAccessException e) {
				e.printStackTrace();
				//异常类型
				if(e.getMessage().indexOf("MySQLIntegrityConstraintViolationException")>=0){
					//根据异常信息判定是否存在唯一索引重复
					if(e.getMessage().indexOf("nameUnique")>=0){
						return Constant.failureMessage("资产名称重复，请重新编辑该资产"+ list);
					}else if(e.getMessage().indexOf("ipLogTypeUnique")>=0){
						return Constant.failureMessage("资产“IP+日志类型”重复，请重新编辑该资产！"+ list);
					}else{
						logger.error(e.getMessage());
						return Constant.failureMessage("资产导入batchInsert MySQLIntegrityConstraintViolationException的其他情况！"+list);
					}
				}else{
					logger.error(e.getMessage());
					return Constant.failureMessage("资产导入batchInsert 其他异常情况！"+list);
				}
			}catch (Exception e) {
				e.printStackTrace();
				return Constant.failureMessage("其他异常情况！"+list);
			}
		}
		return result.toString();
	}

}

package com.jz.bigdata.common.equipment.controller;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Strings;
import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.jz.bigdata.util.BASE64Util;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.POI.ReadExcel;
import com.sun.tools.internal.jxc.ap.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
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
@Slf4j
@Controller
@RequestMapping("/equipment")
@Component
public class EquipmentController {
	
	@Resource(name = "EquipmentService")
	private IEquipmentService equipmentService;
	
	
	@Autowired protected ILogCrudDao logCrudDao;

	@Resource(name = "configProperty")
	private ConfigProperty configProperty;

	@Value(value="classpath:equipmentVisual.json")
	private org.springframework.core.io.Resource equipmentVisual;
	//日期格式
	private static final DateTimeFormatter dtf_time = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
			int result = this.equipmentService.upsert(equipment,session,false);
			if(result==1){
				return Constant.successMessage("操作成功！");
			}else if(result==2){
				return Constant.failureMessage("资产达到上限，无法添加！");
			}else{
				return Constant.successMessage("操作失败！");
			}
		}catch(DataAccessException e){//spring
			//异常类型
			if(e.getMessage().indexOf("MySQLIntegrityConstraintViolationException")>=0){
				//根据异常信息判定是否存在唯一索引重复
				if(e.getMessage().indexOf("nameUnique")>=0){
					return Constant.failureMessage("资产名称重复，请重新输入！");
				}else if(e.getMessage().indexOf("ipLogTypeUnique")>=0){
					return Constant.failureMessage("资产“IP+日志类型”重复，请重新输入！");
				}else{
					log.error("资产维护upsert MySQLIntegrityConstraintViolationException的其他情况！"+e.getMessage());
				}
			}else{
				log.error("资产维护upsert 其他异常情况！"+e.getMessage());
			}
			//其他异常情况
			return Constant.failureMessage("操作失败！");
		}catch (Exception e){
			log.error(e.getMessage());
			return Constant.failureMessage("操作失败！");
		}

	}
	/**
	 * @param request
	 * @param equipment
	 * @param session
	 * @return 添加/修改资产&资产组
	 */
	@ResponseBody
	@RequestMapping(value="/upsertWithAssetGroup.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="添加/修改资产&资产组")
	public String upsertWithAssetGroup(HttpServletRequest request, Equipment equipment,HttpSession session) {
		try{
			int result = this.equipmentService.upsert(equipment,session,true);
			if(result==1){
				return Constant.successMessage("操作成功！");
			}else if(result==2){
				return Constant.failureMessage("资产达到上限，无法添加！");
			}else{
				return Constant.successMessage("操作失败！");
			}
		}catch(DataAccessException e){//spring
			//异常类型
			if(e.getMessage().indexOf("MySQLIntegrityConstraintViolationException")>=0){
				//根据异常信息判定是否存在唯一索引重复
				if(e.getMessage().indexOf("nameUnique")>=0){
					return Constant.failureMessage("资产名称重复，请重新输入！");
				}else if(e.getMessage().indexOf("ipLogTypeUnique")>=0){
					return Constant.failureMessage("资产“IP+日志类型”重复，请重新输入！");
				}else{
					log.error("资产维护upsert MySQLIntegrityConstraintViolationException的其他情况！"+e.getMessage());
				}
			}else{
				log.error("资产维护upsert 其他异常情况！"+e.getMessage());
			}
			//其他异常情况
			return Constant.failureMessage("操作失败！");
		}catch (Exception e){
			log.error(e.getMessage());
			return Constant.failureMessage("操作失败！");
		}

	}

	/**
	 * @param request
	 * @return 根据id删除数据
	 */
	@ResponseBody
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
			log.error("删除资产"+e.getMessage());
			return Constant.failureMessage();
		}

	}
	/**
	 * @param request
	 * @return 根据id删除数据&资产组关系表
	 */
	@ResponseBody
	@RequestMapping(value="/deleteWithAssetGroup.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="删除资产&资产组关系表")
	public String deleteWithAssetGroup(HttpServletRequest request) {
		try{
			int result = 0;
//		获取id数组
			String[] ids = request.getParameter("id").split(",");
//		数组长度大于0删除数据
			if (ids.length > 0) {
				result = this.equipmentService.deleteWithAssetGroup(ids);
			}
			return result >= 1 ? Constant.successMessage() : Constant.failureMessage();
		}catch(Exception e){
			log.error("删除资产"+e.getMessage());
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
			log.error("查询所有资产"+e.getMessage());
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
			log.error("查询所有存在中、高危事件的资产"+e.getMessage());
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
			log.error("查询单个资产：失败"+e.getMessage());
		}
		return list;
	}
	/**
	 * @param request
	 * @return 获取资产列表,dashboard 设置资产/资产组 ，点击资产组后加载不包含在资产组中的资产
	 */
	@ResponseBody
	@RequestMapping(value="/getAssetList4Checkbox.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="dashboard设置资产/资产组时获取资产列表")
	public String getAssetList4Checkbox(HttpServletRequest request) {
		try{
			String asset_group_ids = request.getParameter("asset_group_ids");
			if(asset_group_ids==null){
				return Constant.failureMessage("参数异常！");
			}else{
				List<Map<String,String>> result = this.equipmentService.getAssetList4Checkbox(asset_group_ids);
				return Constant.successData(JSONArray.toJSONString(result));
			}

		}catch(Exception e){
			return Constant.failureMessage("获取资产列表失败！");
		}
	}
	/**
	 * @param request
	 * @return 获取资产列表,dashboard 设置资产/资产组后，悬浮窗要加载的资产信息
	 */
	@ResponseBody
	@RequestMapping(value="/getEquipmentListByDashboardSet.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="dashboard获取资产列表（或单个资产）")
	public String getEquipmentListByDashboardSet(HttpServletRequest request) {
		try{
			String asset_group_ids = request.getParameter("asset_group_ids");
			String asset_ids = request.getParameter("asset_ids");
			if(asset_group_ids==null||asset_ids==null){
				return Constant.failureMessage("参数异常！");
			}else{
				List<Equipment> result = this.equipmentService.getEquipmentListByDashboardSet(asset_group_ids.split(","),asset_ids.split(","));
				return Constant.successData(JSONArray.toJSONString(result));
			}

		}catch(Exception e){
			return Constant.failureMessage("获取资产/资产列表失败！");
		}
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
		//资产组id
		String asset_group_id = request.getParameter("asset_group_id");
		String result = "";
		try {
			result = equipmentService.selectAllByPage(hostName,name,ip,logType,type, pageIndex, pageSize,session,asset_group_id);
			log.info("查询资产：成功");
		} catch (Exception e) {
			log.error("查询资产：失败"+e.getMessage());
		}
		return result;
	}
	/**
	 * @param request
	 * @param session
	 * @return 分页测试例子
	 */
	@ResponseBody
	@RequestMapping(value="/selectPageWithRole.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="分页查询资产，带角色判定")
	public String selectPageWithRole(HttpServletRequest request,HttpSession session) {
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
		//资产组id
		String asset_group_id = request.getParameter("asset_group_id");
		String result = "";
		try {
			result = equipmentService.selectAllByPageWithRole(hostName,name,ip,logType,type, pageIndex, pageSize,session,asset_group_id);
			log.info("查询资产：成功");
		} catch (Exception e) {
			log.error("查询资产：失败"+e.getMessage());
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
			//list = this.equipmentService.selectEquipment(equipment);
			list = this.equipmentService.selectEquipmentByLog(equipment);
		} catch (Exception e) {
			log.error("查询资产：失败！");
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
			log.error("资产名称重复性查询异常！");
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
			log.error("资产名称重复性查询异常！");
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
	@RequestMapping(value="/equipmentExport")
	@DescribeLog(describe="资产导出")
	public String equipmentExport(HttpSession session, HttpServletRequest request,
									HttpServletResponse response){
		try{
			String path = getClass().getClassLoader().getResource("/download/").getFile();
			//生成文件
			String fileName = equipmentService.createEquipmentExportFile(path);

			File file = new File(path, fileName);

			if (file.exists()) {
				//设置响应类型
				response.setContentType("application/xlsm");
				//生成文件

				//设置Content-Disposition，设置attachment，浏览器会激活文件下载框；filename指定下载后默认保存的文件名
				//不设置Content-Disposition的话，文件会在浏览器内打卡，比如txt、img文件
				//为了解决中文名称乱码问题，浏览器下载的文件名 eg:资产列表2021-01-01
				String downloadFileName = new String(("资产导出列表"+LocalDateTime.now().format(dtf_time)).getBytes("utf-8"),"iso-8859-1");
				response.addHeader("Content-Disposition", "attachment; filename="+ downloadFileName+".xls");
				byte[] buffer = new byte[1024];
				FileInputStream fis = null;
				BufferedInputStream bis = null;
				// if using Java 7, use try-with-resources
				try {
					fis = new FileInputStream(file);
					bis = new BufferedInputStream(fis);
					OutputStream os = response.getOutputStream();
					int i = bis.read(buffer);
					while (i != -1) {
						os.write(buffer, 0, i);
						i = bis.read(buffer);
					}
				} catch (IOException ex) {
					// do something,
					// probably forward to an Error page
				} finally {
					if (bis != null) {
						try {
							bis.close();
						} catch (IOException e) {
						}
					}
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
						}
					}
				}
			}
		}catch (Exception e){
			log.error("资产导出失败！");
			return Constant.failureMessage("资产导出失败!");
		}
		return null;
	}
    @RequestMapping(value="/equipmentDownload")
    @DescribeLog(describe="资产清单模板下载")
    public String equipmentDownload(HttpSession session, HttpServletRequest request,
                                   HttpServletResponse response) throws UnsupportedEncodingException {
        //String dataDirectory = request.getServletContext().getRealPath("/WEB-INF/data");
        //System.out.println(dataDirectory);
        //设置模板下载路径.
        String path =getClass().getClassLoader().getResource("/download/").getFile();
        String fileName="资产清单.xlsm";

        File file = new File(path, fileName);

        if (file.exists()) {
            //设置响应类型，这里是下载pdf文件
            response.setContentType("application/xlsm");
            //设置Content-Disposition，设置attachment，浏览器会激活文件下载框；filename指定下载后默认保存的文件名
            //不设置Content-Disposition的话，文件会在浏览器内打卡，比如txt、img文件
            //为了解决中文名称乱码问题
            fileName = new String(fileName.getBytes("utf-8"),"iso-8859-1");
            response.addHeader("Content-Disposition", "attachment; filename="+fileName);
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            // if using Java 7, use try-with-resources
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (IOException ex) {
                // do something,
                // probably forward to an Error page
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return null;
    }


	@ResponseBody
	@RequestMapping(value="insertEquipmentUpload")
	@DescribeLog(describe="资产文件上传并导入到数据库")
	public String insertEquipmentUpload(MultipartHttpServletRequest request,HttpSession session) {

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
					if(!originalFilename.equals("资产清单.xlsm")&&!originalFilename.equals("资产清单.xlsx")){
						return Constant.failureMessage("文件名称或者文件类型有误，请确认！文件名称：资产清单，文件类型为Excel");
					}
					//设置保存路径.
					String path =getClass().getClassLoader().getResource("").getFile();
                    /*System.out.println(path);*/
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
						    e.printStackTrace();
							// TODO: handle exception
							return Constant.failureMessage("资产清单上传失败！");
						}
					} else {
						if(dest.delete()) {
							try {
								multipartFile.transferTo(dest);
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								return Constant.failureMessage("资产清单上传失败！");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								return Constant.failureMessage("资产清单上传失败！");
							}
						}
					}
				}
			}
		}
        ReadExcel readExcel= new ReadExcel();

		List<List<String>> equipments = null;
		try {
			equipments = readExcel.getExcelInfo(filePath);
		} catch (IOException e) {
			e.printStackTrace();
			return Constant.failureMessage("导入失败，资产清单有误，请确认");
		}

		BASE64Util base64Util = new BASE64Util();
        if (equipments.size()>Integer.valueOf(base64Util.decode(configProperty.getNumber()).trim())){
            return Constant.failureMessage("导入失败，资产清单中资产数超过了单节点支撑的300台资产，实际资产数为"+equipments.size());
        }
		List<Equipment> equipmentList = new ArrayList<>();
		for (List<String> list : equipments){
			Equipment equipment =new Equipment();
			// 资产名称
			equipment.setName(list.get(0));
			// 资产IP
			equipment.setIp(list.get(1));
			// 资产主机名
			equipment.setHostName(list.get(2));
			// 资产日志类型
			equipment.setLogType(list.get(3));
			// 判断需要收集的日志级别，不为空设置获取的数据
			if (!Strings.isNullOrEmpty(list.get(4))){
				if (list.get(4).equals("全选")){
					equipment.setLog_level("emergency,alert,critical,error,warning,notice,information,debug,");
				}else{
					equipment.setLog_level(list.get(4));
				}
			}else {
				// 为空设置全选
				equipment.setLog_level("emergency,alert,critical,error,warning,notice,information,debug,");
			}

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
			// 根域名
			if (!Strings.isNullOrEmpty(list.get(7))){
				equipment.setDomain(list.get(7));
			}
			// 端口
			if (!Strings.isNullOrEmpty(list.get(8))){
				equipment.setDomain(list.get(8));
			}
			// 系统
			if (!Strings.isNullOrEmpty(list.get(9))){
				equipment.setDomain(list.get(9));
			}
			// 系统版本号
			if (!Strings.isNullOrEmpty(list.get(10))){
				equipment.setDomain(list.get(10));
			}
			// 资产编号
			if (!Strings.isNullOrEmpty(list.get(11))){
				equipment.setDomain(list.get(11));
			}
			// 序列号
			if (!Strings.isNullOrEmpty(list.get(12))){
				equipment.setDomain(list.get(12));
			}
			// MAC地址
			if (!Strings.isNullOrEmpty(list.get(13))){
				equipment.setDomain(list.get(13));
			}
			// 描述
			if (!Strings.isNullOrEmpty(list.get(14))){
				equipment.setDomain(list.get(14));
			}
			// 资产价值
			if (!Strings.isNullOrEmpty(list.get(15))){
				equipment.setDomain(list.get(15));
			}
			//添加用户信息
			equipment.setUserId(session.getAttribute(Constant.SESSION_USERID).toString());
			equipmentList.add(equipment);
		}
		return equipmentService.insertBatch(equipmentList,session);
	}

	@ResponseBody
	@RequestMapping(value="insertByImportExcel")
	//@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	@DescribeLog(describe="导入excel")
	public String insertByImportExcel(HttpSession session,String filepath) {
        filepath = "D:\\Computer_Science\\Java\\hslog-parent\\hslog\\target\\hslog\\WEB-INF\\classes\\资产清单.xlsx";
		if (Strings.isNullOrEmpty(filepath)){
			return Constant.failureMessage("资产清单文件为空，请确认！");
		}
		ReadExcel readExcel= new ReadExcel();


		List<List<String>> equipments = null;
		try {
			equipments = readExcel.getExcelInfo(filepath);
		} catch (IOException e) {
			e.printStackTrace();
			return Constant.failureMessage("导入失败，资产清单文件有误，请确认");
		}

		BASE64Util base64Util = new BASE64Util();
		if (equipments.size()>Integer.valueOf(base64Util.decode(configProperty.getNumber()).trim())){
			return Constant.failureMessage("导入失败，资产清单中资产数超过了单节点支撑的300台资产，实际资产数为"+equipments.size());
		}

		List<Equipment> equipmentList = new ArrayList<>();
		for (List<String> list : equipments){
			Equipment equipment =new Equipment();
			equipment.setName(list.get(0));
			equipment.setIp(list.get(1));
			equipment.setHostName(list.get(2));
			equipment.setLogType(list.get(3));
			// 判断需要收集的日志级别，不为空设置获取的数据
			if (!Strings.isNullOrEmpty(list.get(4))){
                equipment.setLog_level(list.get(4));
            }else {
			    // 为空设置全选
                equipment.setLog_level("emergency,alert,critical,error,warning,notice,information,debug,");
            }

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
			equipmentList.add(equipment);
			/*try {
				switch (equipmentService.insertBatch(equipment,session)){
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
						log.error(e.getMessage());
						return Constant.failureMessage("资产导入batchInsert MySQLIntegrityConstraintViolationException的其他情况！"+list);
					}
				}else{
					log.error(e.getMessage());
					return Constant.failureMessage("资产导入batchInsert 其他异常情况！"+list);
				}
			}catch (Exception e) {
				e.printStackTrace();
				return Constant.failureMessage("其他异常情况！"+list);
			}*/
		}
		return equipmentService.insertBatch(equipmentList,session);

	}

	/**
	 * 资产概览点击右上角图表，根据资产的日志类型，获取可显示的dashboard的列表信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getDashboardsInfo", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取资产对应的dashboard信息")
	public String getDashboardsInfo(HttpServletRequest request){
		String logType = request.getParameter("logType");
		try{
			List<Map<String,String>> result = equipmentService.getDashboardsInfo(logType);
			return Constant.successData(JSONArray.toJSONString(result));
		}catch(Exception e){
			log.error("获取资产对应的dashboard信息失败："+e.getMessage());
			return Constant.failureMessage("获取dashboard信息失败！");
		}

	}

	/**
	 * 获取资产信息报表和详情相关的链接信息，用户点击时进行跳转
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getEquipmentVisual", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取资产可视化信息")
	public String getEquipmentVisual(){
		try{
			String content = FileUtils.readFileToString(equipmentVisual.getFile(), "UTF-8");
			return Constant.successData(content);
		}catch (Exception e){
			return Constant.failureMessage("资产可视化信息获取失败！");
		}

	}
}

package com.jz.bigdata.common.serviceInfo.controller;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.jz.bigdata.common.serviceInfo.dao.IServiceInfoDao;
import com.jz.bigdata.util.Uuid;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.serviceInfo.entity.ServiceInfo;
import com.jz.bigdata.common.serviceInfo.service.IServiceInfoService;
import com.jz.bigdata.util.DescribeLog;

/**
 * @ClassName ServiceController
 * @Description 
 * @Author shi cheng yu
 * @Date 2019年4月23日 上午10:13:35
 */
@Controller
@RequestMapping("/serviceInfo")
public class ServiceInfoController {
	
	@Resource(name = "ServiceInfoService")
	private IServiceInfoService serviceInfoService;

	@Resource
	private IServiceInfoDao serviceInfoDao;
	
	/**
	 * @param request
	 * @param event
	 * @return 添加数据
	 */
//	@ResponseBody
//	@RequestMapping(value="/insert",produces = "application/json; charset=utf-8")
//	@DescribeLog(describe = "添加数据")
//	public String insert(HttpServletRequest request, ServiceInfo serviceInfo) {
//
//		// 结果一般命名为result
//		int result = 0;
//		result = serviceInfoService.insert(serviceInfo);
//		return result >= 1 ? Constant.successMessage() : Constant.failureMessage();
//	}
	@ResponseBody
	@RequestMapping(value="/insertTest",produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "查询所有信息")
	public String insertTest(){
/*
		//模拟数据,url,something
		Map<String,String> map = new HashMap<>();
		map.put("http://192.168.2.182:8080/jzLog/hplus/js/content.min.js","1");
		map.put("http://192.168.2.182:8080/jzLog/equipment/selectPage.do","3");
		map.put("http://111.202.102.38:80/api/popup/lotus.php","6");
		map.put("http://192.168.2.182:8080/jzLog/view/device/deviceScan.html","9");
		map.put("http://192.168.0.1:80/a1","2");
		map.put("http://192.168.0.1:80/a2","4");
		map.put("http://192.168.0.1:80/a3","5");
		map.put("http://192.168.0.1:80/a4","7");
		map.put("http://192.168.0.1:80/a5","8");
		map.put("http://192.168.0.1:80/a6","10");


		Map<String,String> map1 = new HashMap<>();
		map1.put("http://27.221.30.46:80/1ddf8799fcacd39","1");
		map1.put("http://111.202.100.56:80/api/toolbox/geturl.php","3");
		map1.put("http://123.58.182.105:80/apptrack/pcconfinfo/flashMailMigrate.do","6");
		map1.put("http://36.250.65.105:80/pages/check/updateini.aspx","9");
		map1.put("http://192.168.0.1:80/a7","2");
		map1.put("http://192.168.0.1:80/a8","4");
		map1.put("http://192.168.0.1:80/a9","5");
		map1.put("http://192.168.0.1:80/a10","7");
		map1.put("http://192.168.0.1:80/a11","8");
		map1.put("http://192.168.0.1:80/a12","10");
		for(int i=0;i<10000;i++){
			map.put("http://192.168.0.1:80/a1"+i,"10");
			map1.put("http://192.168.0.1:80/a2"+i,"10");
		}

		for(int i=0;i<10000;i++){
			map.put("http://192.168.0.1:80/a6","10");
			map1.put("http://192.168.0.1:80/a6","10");
		}
		//两种测试方式
		boolean temp = false;
		//1.遍历两次

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<ServiceInfo> serviceslist = new ArrayList<ServiceInfo>();
		List<ServiceInfo> list = serviceInfoDao.selectAll(new ServiceInfo());
		String result = DateTime.now().toString("yyyy-MM-dd HH:mm:ss.SSS");
		//遍历所有传过来的url信息
		for(Map.Entry<String, String> key : map.entrySet()) {
			//遍历所有数据库中的信息，如果不存在，插入数据库中
			for(ServiceInfo s:list){
				if(s.getUrl().equals(key.getKey())){
					temp = true;
					break;
				}else{
					temp = false;
					continue;
				}
			}
			//如果没有重复，插入数据库中
			if(!temp){
				ServiceInfo funservice = new  ServiceInfo();
				funservice.setId(Uuid.getUUID());
				funservice.setName("1");
				funservice.setProtocol("1");
				funservice.setIp("1");
				funservice.setPort("1");
				funservice.setUrl(key.getKey());
				funservice.setRelativeUrl("1");
				funservice.setEquipmentId("1");
				funservice.setComplementState(1);
				funservice.setCreateTime(format.format(new Date()));
				funservice.setState(1);
				funservice.setDescribe("1");
				serviceslist.add(funservice);
			}
		}
		serviceInfoService.insert(serviceslist);
		result = result+"-----"+ DateTime.now().toString("yyyy-MM-dd HH:mm:ss.SSS");
		//2.直接插入
		for(Map.Entry<String, String> key : map1.entrySet()) {
			ServiceInfo funservice = new  ServiceInfo();
			funservice.setId(Uuid.getUUID());
			funservice.setName("1");
			funservice.setProtocol("1");
			funservice.setIp("1");
			funservice.setPort("1");
			funservice.setUrl(key.getKey());
			funservice.setRelativeUrl("1");
			funservice.setEquipmentId("1");
			funservice.setComplementState(1);
			funservice.setCreateTime(format.format(new Date()));
			funservice.setState(1);
			funservice.setDescribe("1");
			serviceInfoDao.insertExists(funservice);
		}
		result = result+"-----"+ DateTime.now().toString("yyyy-MM-dd HH:mm:ss.SSS");
		return result;
		*/
		return null;
	}
	/**
	 * @param request
	 * @param department
	 * @return 查询信息
	 */
	@ResponseBody
	@RequestMapping(value="/selectAll",produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "查询所有信息")
	public List<ServiceInfo> selectAll(HttpServletRequest request,ServiceInfo serviceInfo) {

		// 结果一般命名为result
		// int result = 0;
		// result=departmentService.insert(department);
		return serviceInfoService.selectAll(serviceInfo);
	}

	/**
	 * @param request
	 * @param department
	 * @return 修改信息
	 */
	@ResponseBody
	@RequestMapping(value="/updateById",produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "修改信息")
	public String updataById(HttpServletRequest request, ServiceInfo service) {

		// 结果一般命名为result
		int result = 0;
		result = serviceInfoService.updateById(service);
		return result >= 1 ? Constant.successMessage() : Constant.failureMessage();
	}

	/**
	 * @param request
	 * @return 根据id删除信息
	 */
	@ResponseBody
	@RequestMapping(value="/delete",produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "根据id删除数据")
	public String delete(HttpServletRequest request) {

		// 结果一般命名为result
		int result = 0;
		String[] ids = request.getParameter("id").split(",");
		//数组长度大于0删除数据
		if (ids.length > 0) {
			result = serviceInfoService.delete(ids);
		}
		return result >= 1 ? Constant.successMessage() : Constant.failureMessage();
	}

	

	/**
	 * @param request
	 * @param page
	 * @return 分页测试例子
	 */
	@ResponseBody
//	@RequestMapping("/selectPage")
	@RequestMapping(value="/selectPage.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="分页查询资产")
	public String selectPage(HttpServletRequest request,HttpSession session) {
		String name=request.getParameter("name");
		String ip=request.getParameter("ip");
		String port=request.getParameter("port");
		String protocol=request.getParameter("protocol");
		String url=request.getParameter("url");
		String relativeUrl=request.getParameter("relativeUrl");
		String complementStateString=request.getParameter("complementState");
		String stateString=request.getParameter("state");
		int complementState=0;
		int state=0;
		if(complementStateString.equals("")){
			complementState=2;
		}else{
			complementState=Integer.valueOf(complementStateString);
		}
		if(stateString.equals("")){
			state=2;
		}else{
			state=Integer.valueOf(stateString);
		}
		
		//页码数
		int pageIndex=Integer.parseInt(request.getParameter("pageIndex"));
		//每页显示的数量
		int pageSize=Integer.parseInt(request.getParameter("pageSize"));
		return serviceInfoService.selectAllByPage(name, ip, port, protocol, url, relativeUrl, complementState, state, pageIndex, pageSize);
	}
	
	
	/**
	 * @param request
	 * @return
	 * 根据id查询数据
	 */
	@ResponseBody
	@RequestMapping(value="/selectServiceById.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="根据id查询数据")
	public ServiceInfo selectServiceById(HttpServletRequest request){
		String id= request.getParameter("id");
		return serviceInfoService.selectServiceById(id);
		
	}

}

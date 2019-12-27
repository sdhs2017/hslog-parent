package com.jz.bigdata.common.serviceInfo.controller;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

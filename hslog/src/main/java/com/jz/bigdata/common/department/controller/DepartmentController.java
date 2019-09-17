package com.jz.bigdata.common.department.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.department.entity.Department;
import com.jz.bigdata.common.department.service.IDepartmentService;
import com.jz.bigdata.util.DescribeLog;

/**
 * @author shichengyu
 * @date 2017年8月4日 上午10:57:40
 * @description
 */
@Controller
@RequestMapping("/department")
public class DepartmentController {
	
	@Resource(name="DepartmentService")
	private IDepartmentService departmentService;
	
	/**
	 * @param request
	 * @param department
	 * @return 添加部门信息
	 */
	@ResponseBody
	@RequestMapping("/insert")
	@DescribeLog(describe="添加部门")
	public String insert(HttpServletRequest request,Department department){

		//结果一般命名为result
		int result = 0;
		result=departmentService.insert(department);
		return result>=1?Constant.successMessage():Constant.failureMessage();
	}
	
	
	/**
	 * @param request
	 * @param department
	 * @return 查询部门信息
	 */
	@ResponseBody
	@RequestMapping("/selectAll")
	@DescribeLog(describe="查询所有部门信息")
	public Map<String,Object> selectAll(HttpServletRequest request,Department department,HttpSession session){

		//结果一般命名为result
//		int result = 0;
//		result=departmentService.insert(department);
		return departmentService.selectAll(department,session);
	}
	
	/**
	 * @param request
	 * @param department
	 * @return 修改部门信息
	 */
	@ResponseBody
	@RequestMapping("/updateById")
	@DescribeLog(describe="修改部门信息")
	public String updateById(HttpServletRequest request,Department department){

		//结果一般命名为result
		int result = 0;
		result=departmentService.updateById(department);
		return result>=1?Constant.successMessage():Constant.failureMessage();
	}

	
	/**
	 * @param request
	 * @return
	 * 根据id删除部门
	 */
	@ResponseBody
	@RequestMapping("/delete")
	@DescribeLog(describe="删除部门")
	public boolean delete(HttpServletRequest request){

		//结果一般命名为result
//		int result = 0;
		String id =request.getParameter("id");
		return departmentService.delete(Integer.parseInt(id));
	}
	
	
	/**
	 * @return
	 * 查询所有数据树结构
	 */
	@ResponseBody
	@RequestMapping(value="/selectAllDepartment",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取部门树")
	public String selectAllDepartment(){
		return departmentService.selectAllDepartment();
		//李洪连 提交测试
	}
}

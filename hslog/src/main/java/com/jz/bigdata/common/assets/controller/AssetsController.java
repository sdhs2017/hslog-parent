package com.jz.bigdata.common.assets.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.assets.entity.Assets;
import com.jz.bigdata.common.assets.service.IAssetsService;
import com.jz.bigdata.util.DescribeLog;

@Controller
@RequestMapping("/assets")
public class AssetsController {
	
	@Resource(name="assetsService")
	private IAssetsService assetsService;
	
	@ResponseBody
//	@RequestMapping("/insert")
	@RequestMapping(value="/insert",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="添加数据")
	public String insert(HttpServletRequest request,Assets assets){
		//结果一般命名为result
		int result = 0;
		result=assetsService.insert(assets);
		return result>=1?Constant.successMessage():Constant.failureMessage();
	}
	
	
	/**
	 * @param request
	 * @param department
	 * @return 查询信息
	 */
	@ResponseBody
//	@RequestMapping("/selectAll")
	@RequestMapping(value="/selectAll",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="查询所有资产信息")
	public List<Assets> selectAll(HttpServletRequest request){

		//结果一般命名为result
//		int result = 0;
//		result=departmentService.insert(department);
		return assetsService.selectAll();
	}
	
	/**
	 * @param request
	 * @param assets
	 * @return 修改信息
	 */
	@ResponseBody
//	@RequestMapping("/updateById")
	@RequestMapping(value="/updateById",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="修改资产信息")
	public String updateById(HttpServletRequest request,Assets assets){

		//结果一般命名为result
		int result = 0;
		result=assetsService.updateById(assets);
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
	@DescribeLog(describe="删除资产")
	public String delete(HttpServletRequest request){

		int result = 0;
//		获取id数组
		String[] ids = request.getParameter("id").split(",");
//		数组长度大于0删除数据
		if (ids.length > 0) {
			result = this.assetsService.delete(ids);
		}
		return result >= 1 ? Constant.successMessage() : Constant.failureMessage();
	}
	
	/**
	 * @param request
	 * @param page
	 * @return 分页测试
	 */
	@ResponseBody
//	@RequestMapping("/selectPage")
	@RequestMapping(value="/selectAllByPage", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="分页查询")
	public String selectPage(HttpServletRequest request) {
		//页码数
		int pageIndex=Integer.parseInt(request.getParameter("pageIndex"));
		//每页显示的数量
		int pageSize=Integer.parseInt(request.getParameter("pageSize"));
		return assetsService.selectAllByPage( pageIndex, pageSize);
	}
	
	/**
	 * @param request
	 * @return 查询单个数据
	 */
	@ResponseBody
//	@RequestMapping("/selectPage")
	@RequestMapping(value="/selectOneAssets", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="查询单个数据")
	public Assets selectOneAssets(HttpServletRequest request,Assets assets) {
		return assetsService.selectOneAssets(assets);
	}
	
	
	/**
	 * @param request
	 * @param department
	 * @return 查询信息
	 */
	@ResponseBody
	@RequestMapping(value="/selectByIncrement",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="查询所有增量资产信息")
	public List<Assets> selectByIncrement(){
		return assetsService.selectByIncrement();
	}

}

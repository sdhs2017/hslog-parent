package com.jz.bigdata.common.asset.controller;

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
import com.jz.bigdata.common.asset.entity.Asset;
import com.jz.bigdata.common.asset.service.IAssetService;
import com.jz.bigdata.util.DescribeLog;


/**
 * @description
 */
@Controller
@RequestMapping("/asset")
public class AssetController {

	@Resource(name = "AssetService")
	private IAssetService assetService;


	@Autowired protected ILogCrudDao logCrudDao;

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@ResponseBody
//	@RequestMapping("/insert")
	@RequestMapping(value="/insert.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="添加或修改资产")
	public String insert(HttpServletRequest request, Asset asset,HttpSession session) {


		// 结果一般命名为result
		int	result = this.assetService.insert(asset,session);
		if(result==0){
			//return Constant.assetMaxInsertMessage();
			return Constant.failureMessage(2,"资产达到上限，无法添加！");
		}else if(result==1){
			//return Constant.assetNameIpInsertMessage();
			return Constant.failureMessage(2,"资产名,ip重复,无法添加！");
		}else{
			return Constant.successMessage();
		}
//		} else {// 更新操作
//			result = this.assetService.updateById(asset,session);
//		}
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
		int result = 0;
//		获取id数组
		String[] ids = request.getParameter("id").split(",");
//		数组长度大于0删除数据
		if (ids.length > 0) {
			result = this.assetService.delete(ids);
		}
		return result >= 1 ? Constant.successMessage() : Constant.failureMessage();
	}


	/**
	 * @param request
	 * @param asset
	 * @return 查询数据
	 */
	@ResponseBody
//	@RequestMapping("/selectAll")
	@RequestMapping(value="/selectAll.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="查询所有资产")
	public String selectAll(HttpServletRequest request, Asset asset,HttpSession session){
		return assetService.selectAll(asset,session);
	}

	/**
	 * @param request
	 * @param asset
	 * @return
	 * 查询单个实体
	 */
	@ResponseBody
//	@RequestMapping("/selectAsset")
	@RequestMapping(value="/selectAsset.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="查询单个资产")
	public List<Asset> selectAsset(HttpServletRequest request, Asset asset) {
		List<Asset> list= null;
		try {
			list = this.assetService.selectAsset(asset);
		} catch (Exception e) {
			logger.error("查询单个资产：失败");
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
			result = assetService.selectAllByPage(hostName,name,ip,logType,type, pageIndex, pageSize,session);
			logger.info("查询资产：成功");
		} catch (Exception e) {
			logger.error("查询资产：失败");
			e.printStackTrace();
		}
		return result;
	}


	/**
	 * @param request
	 * @param asset
	 * @param session
	 * @return 修改资产
	 */
	@ResponseBody
//	@RequestMapping("/insert")
	@RequestMapping(value="/update.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="修改资产")
	public String update(HttpServletRequest request, Asset asset,HttpSession session) {

		// 结果一般命名为result
		int	result = 0;
		result = this.assetService.updateById(asset,session);
		return result >= 1 ? Constant.successMessage() : Constant.failureMessage();
	}

	/**
	 * @param request
	 * @param asset
	 * @return
	 * 查询单个实体
	 */
	@ResponseBody
//	@RequestMapping("/selectAsset")
	@RequestMapping(value="/selectAssetByLog.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="日志跳转资产")
	public Map<String, Object> selectAssetByLog(HttpServletRequest request, Asset asset) {
		List<Asset> list= null;
		try {
			list = this.assetService.selectAsset(asset);
		} catch (Exception e) {
			logger.error("查询资产：失败！");
			e.printStackTrace();
		}
		Map<String,Object> map =new HashMap<>();
		if(list.size()>0){
			map.put("success", "true");
			map.put("message", "查询成功");
			map.put("asset", list);
			return map;
		}else{
			map.put("success", "false");
			map.put("message", "资产已删除");
			map.put("asset", "");
			return map;
		}
	}


}

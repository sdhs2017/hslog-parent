package com.jz.bigdata.common.assetGroup.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.jz.bigdata.common.assetGroup.entity.AssetGroup;
import com.jz.bigdata.common.assetGroup.service.IAssetGroupService;
import com.mysql.jdbc.StringUtils;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.util.DescribeLog;
import org.springframework.web.multipart.MultipartHttpServletRequest;


/**
 * @description
 */
@Slf4j
@Controller
@RequestMapping("/assetGroup")
public class AssetGroupController {

	@Resource(name = "AssetGroupService")
	private IAssetGroupService assetGroupService;

	@ResponseBody
	@RequestMapping(value="/insert.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="添加资产组")
	public String insert(HttpServletRequest request, AssetGroup assetGroup, HttpSession session) {

		try{
			boolean	result = this.assetGroupService.insert(assetGroup,session);
			if(result){
				return Constant.successMessage();
			}else{
				return Constant.failureMessage("资产组添加失败！");
			}
		}catch(Exception e){
			return handleException(e);
		}
	}

	/**
	 * @param request
	 * @return 根据id删除数据
	 */
	@ResponseBody
	@RequestMapping(value="/delete.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="删除资产组")
	public String delete(HttpServletRequest request) {
		try{
			String ids = request.getParameter("asset_group_ids");
			String[] idsArray = ids.split(",");
			boolean	result = this.assetGroupService.delete(idsArray);
			if(result){
				return Constant.successMessage();
			}else{
				return Constant.failureMessage("资产组删除失败！");
			}
		}catch(Exception e){
			return Constant.failureMessage("资产组删除失败！");
		}
	}


	/**
	 * 编辑资产，查询单个资产组详情
	 * @param request
	 * @param assetGroup
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getAssetGroupInfoById.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="查询单个资产组详情")
	public String getAssetGroupInfoById(HttpServletRequest request,AssetGroup assetGroup, HttpSession session){
		try{
			String asset_group_id = request.getParameter("asset_group_id");
			AssetGroup result = assetGroupService.getAssetGroupInfoById(asset_group_id);
			return Constant.successData(JSONObject.toJSON(result).toString());
		}catch (Exception e){
			return Constant.failureMessage("资产组信息查询失败！");
		}
	}
	/**
	 * 根据查询条件，查询资产组
	 * @param request
	 * @param assetGroup
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getAssetGroupInfoByCondition.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="查询资产组")
	public String getAssetGroupInfoByCondition(HttpServletRequest request,AssetGroup assetGroup, HttpSession session){
		try{
			//分页信息处理
			if(assetGroup.getPageIndex()!=null&&assetGroup.getPageSize()!=null){
				assetGroup.setStartRecord((assetGroup.getPageSize() * (assetGroup.getPageIndex() - 1)));
			}
			Map<String, Object> result = assetGroupService.getAssetGroupInfoByCondition(assetGroup);
			return Constant.successData(JSONArray.toJSON(result).toString());
		}catch (Exception e){
			return Constant.failureMessage("资产组信息查询失败！");
		}
	}



	/**
	 * @param request
	 * @param assetGroup
	 * @param session
	 * @return 修改资产组
	 */
	@ResponseBody
	@RequestMapping(value="/update.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="修改资产组")
	public String update(HttpServletRequest request, AssetGroup assetGroup,HttpSession session) {
		try{
			//判定资产组id是否存在
			if(StringUtils.isNullOrEmpty(assetGroup.getAsset_group_id())){
				return Constant.failureMessage("资产组信息修改失败！");
			}
			boolean	result = this.assetGroupService.updateById(assetGroup,session);
			if(result){
				return Constant.successMessage();
			}else{
				return Constant.failureMessage("资产组信息修改失败！");
			}
		}catch(Exception e){
			return handleException(e);
		}
	}
	/**
	 * @param request
	 * @return 获取资产列表,穿梭框
	 */
	@ResponseBody
	@RequestMapping(value="/getAssetList.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取资产列表")
	public String getAssetList(HttpServletRequest request) {
		try{
			List<Map<String,String>> result = this.assetGroupService.getAssetList();
			return Constant.successData(JSONArray.toJSONString(result));
		}catch(Exception e){
			return Constant.failureMessage("资产组添加失败！");
		}
	}
	/**
	 * @param request
	 * @return 获取资产列表,combobox
	 */
	@ResponseBody
	@RequestMapping(value="/getAssetList4Combobox.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取资产列表")
	public String getAssetList4Combobox(HttpServletRequest request) {
		try{
			String asset_group_id = request.getParameter("asset_group_id");
			List<Map<String,String>> result = this.assetGroupService.getAssetList4Combobox(asset_group_id);
			return Constant.successData(JSONArray.toJSONString(result));
		}catch(Exception e){
			return Constant.failureMessage("资产组添加失败！");
		}
	}
	/**
	 * @param request
	 * @return 获取资产列表，服务于alert的combobox，不添加空选项，用于dashboard设置资产/资产组
	 */
	@ResponseBody
	@RequestMapping(value="/getAssetGroupList4Checkbox.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="dashboard获取资产组列表")
	public String getAssetGroupList4Checkbox(HttpServletRequest request) {
		try{
			List<Map<String,String>> result = this.assetGroupService.getAssetGroupListCombobox();
			return Constant.successData(JSONArray.toJSONString(result));
		}catch(Exception e){
			return Constant.failureMessage("资产组添加失败！");
		}
	}
	/**
	 * @param request
	 * @return 获取资产列表，服务于alert的combobox，添加空选项
	 */
	@ResponseBody
	@RequestMapping(value="/getAssetGroupList4Combobox.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取资产组列表")
	public String getAssetGroupList4Combobox(HttpServletRequest request) {
		try{
			List<Map<String,String>> result = this.assetGroupService.getAssetGroupList4Combobox();
			return Constant.successData(JSONArray.toJSONString(result));
		}catch(Exception e){
			return Constant.failureMessage("资产组添加失败！");
		}
	}
	/**
	 * @param request
	 * @return 获取资产列表
	 */
	@ResponseBody
	@RequestMapping(value="/getAssetGroupList.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取资产组列表")
	public String getAssetGroupList(HttpServletRequest request) {
		try{
			List<Map<String,String>> result = this.assetGroupService.getAssetGroupList();
			return Constant.successData(JSONArray.toJSONString(result));
		}catch(Exception e){
			return Constant.failureMessage("资产组添加失败！");
		}
	}
	/**
	 * 获取资产组对应dashboard时的信息。包含该资产组下的资产列表id，以及资产组对应的dashboard的id
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getAssetGroupDashboardInfo.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取资产组列表")
	public String getAssetGroupDashboardInfo(HttpServletRequest request) {
		try{
			String asset_group_id = request.getParameter("asset_group_id");
			Map<String,Object> result = this.assetGroupService.getDashboardInfo(asset_group_id);
			return Constant.successData(JSONArray.toJSONString(result));
		}catch(Exception e){
			return Constant.failureMessage("获取资产组信息失败！");
		}
	}

	/**
	 * 通过资产组id，获取资产组中资产的日志类别有多少种，再加载可显示数据的dashboard的列表信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getDashboardsInfo", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取资产组对应的dashboard信息")
	public String getDashboardsInfo(HttpServletRequest request){
		String asset_group_id = request.getParameter("asset_group_id");
		try{
			List<Map<String,String>> result = this.assetGroupService.getDashboardsInfo(asset_group_id);
			return Constant.successData(JSONArray.toJSONString(result));
		}catch(Exception e){
			log.error("获取资产组对应的dashboard信息失败："+e.getMessage());
			return Constant.failureMessage("获取dashboard信息失败！");
		}

	}
	/**
	 * 资产组增改异常处理，资产组名称重复问题
	 * @param e
	 * @return
	 */
	private String handleException(Exception e){
		Throwable throwable = e.getCause();
		Throwable[] throwableList = e.getSuppressed();
		if(null!=throwable){
			String throwableInfo = throwable.toString();
			if(throwableInfo.indexOf("UNIQUE_ASSET_GROUP_NAME")>=0){
				log.error("资产组名称重复："+e.getMessage());
				return Constant.failureMessage("资产组名称重复！");
			}else{
				log.error("资产组添加失败"+e.getMessage());
				return Constant.failureMessage("资产组信息添加失败！");
			}
		}else if(null!=throwableList){
			//返回的异常可能是一个数组
			String throwables = Arrays.toString(throwableList);
			if(throwables.indexOf("UNIQUE_ASSET_GROUP_NAME")>=0){
				log.error("资产组名称重复："+e.getMessage());
				return Constant.failureMessage("资产组名称重复！");
			}else{
				log.error("资产组添加失败"+e.getMessage());
				return Constant.failureMessage("资产组信息添加失败！");
			}
		}else{
			return Constant.failureMessage("资产组信息添加失败！");
		}
	}

}

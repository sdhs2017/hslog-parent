package com.jz.bigdata.common.ReportModel.controller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hs.elsearch.entity.SearchConditions;
import com.jz.bigdata.common.ReportModel.entity.ReportModel;
import com.jz.bigdata.common.ReportModel.entity.ReportModelInfo;
import com.jz.bigdata.common.ReportModel.service.IReportModelService;
import com.jz.bigdata.util.*;
import com.jz.bigdata.util.HSTree.HSTree;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.joda.time.DateTime;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.common.Constant;


/**
 * @description 20220601-zyy
 */
@Slf4j
@Controller
@RequestMapping("/reportModel")
@Component
public class ReportModelController {

	@Resource(name = "ReportModelService")
	private IReportModelService reportModelService;

	//cache 存放生成的文件路径 . key ：2022-06-02 18(时)，value 文件全路径
	private Cache<String,String> reportFileCache = Caffeine.newBuilder()
			.maximumSize(100)//最大条数，
			//.expireAfterWrite(99999, TimeUnit.DAYS)//过期时间，不设置则不会过期
			.build();
	@ResponseBody
	@RequestMapping(value="/CreateReport.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="生成报告")
	public String CreateReport(HttpServletRequest request, HttpSession session) {
		//参数处理
		String base64_images = request.getParameter("base64_images");//图片
		String tables = request.getParameter("tables");//表格
		String metrics = request.getParameter("metrics");//指标
		String dashboardID = request.getParameter("dashboardID");//仪表板ID
		String reportModelType = request.getParameter("reportModelType");//报告模板类型
		String last = request.getParameter("last");//仪表板时间控件的值，用来计算持续时间
		try{
			String	result = this.reportModelService.createReport(base64_images,tables,metrics,dashboardID,reportModelType,last);
			//如果文件正常生成
			if(result.isEmpty()){
				return Constant.failureMessage("报告生成失败！");
			}else if(result.equals("ReportModelNotFound")){
				return Constant.failureMessage("未找到该类型的报告模板！");
			}else if(result.equals("ReportModelNotOnly")){
				return Constant.failureMessage("报告生成失败！存在多个模板！");
			}else{
				//写入缓存
				reportFileCache.put(DateTime.now().toString("yyyy-MM-dd HH"),result);
				return Constant.successMessage();
			}
		}catch(Exception e){
			log.error(e.getMessage());
			e.printStackTrace();
			return Constant.failureMessage("报告生成失败！");
		}
	}

	@RequestMapping(value="/reportDownload.do")
	@DescribeLog(describe="下载报告")
	public String reportDownload( HttpServletResponse response) {
		try{
			String file = reportFileCache.getIfPresent(DateTime.now().toString("yyyy-MM-dd HH"));
			//能获取到文件
			if(file!=null){
				File f = new File(file);
				if (f.exists()) {
					//设置响应类型 word
					response.setContentType("application/msword");

					//设置Content-Disposition，设置attachment，浏览器会激活文件下载框；filename指定下载后默认保存的文件名
					//不设置Content-Disposition的话，文件会在浏览器内打卡，比如txt、img文件
					//为了解决中文名称乱码问题，浏览器下载的文件名 eg:资产列表2021-01-01
					String downloadFileName = f.getName().substring(0,f.getName().length()-4)+ DateTime.now().toString(" HH-mm-ss")+".doc";
					downloadFileName = new String(downloadFileName.getBytes("UTF-8"), "iso-8859-1");
					response.addHeader("Content-Disposition", "attachment; filename="+downloadFileName);

					FileInputStream fis = null;
					BufferedInputStream bis = null;
					// if using Java 7, use try-with-resources
					try {
						fis = new FileInputStream(file);
						bis = new BufferedInputStream(fis);
						byte[] buffer = new byte[1024];
						OutputStream os = response.getOutputStream();

						int i = bis.read(buffer);
						while (i != -1) {
							os.write(buffer, 0, i);
							i = bis.read(buffer);
						}
					} catch (IOException ex) {
						log.error(ex.getMessage());
						return Constant.failureMessage("报告下载失败！");
						// do something,
						// probably forward to an Error page
					} finally {
						//关闭流
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
					//开始下载不需要提示
					return null;
				}else{
					//缓存路径存在，但文件未真实找到
					return Constant.failureMessage("未找到报告文件");
				}
			}else{
				//缓存找不到文件路径
				return Constant.failureMessage("未找到报告文件");
			}
		}catch (Exception e){
			log.error(e.getMessage());
			return Constant.failureMessage("报告下载失败！");
		}
	}
	@ResponseBody
	@RequestMapping(value="/getReportModelListByPage.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取带分页的报告模板信息列表")
	public String  getReportModelListByPage(HttpServletRequest request){
		try{
			//页码数
			int pageIndex=Integer.parseInt(request.getParameter("pageIndex"));
			//每页显示的数量
			int pageSize=Integer.parseInt(request.getParameter("pageSize"));
			// 获取起始数
			int startRecord = (pageSize * (pageIndex - 1));
			//模板名称
			String reportModelName = request.getParameter("reportModelName");
			//模板类型
			String reportModelType = request.getParameter("reportModelType");
			Map<String, Object> result = reportModelService.getReportModelListByPage(startRecord,pageSize,reportModelName,reportModelType);
			return Constant.successData(JSONObject.toJSON(result).toString());
		}catch (Exception e){
			log.error(e.getMessage());
			return Constant.failureMessage("数据查询失败！");
		}
	}
	@ResponseBody
	@RequestMapping(value="/getReportModelByReportModelID.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="通过reportModelID获取报告模板基本信息")
	public String getReportModelByReportModelID(HttpServletRequest request){
		try{
			//模板ID
			String reportModelID = request.getParameter("reportModelID");
			ReportModel result = reportModelService.getReportModelByReportModelID(reportModelID);
			return JSONObject.toJSON(result).toString();
		}catch (Exception e){
			log.error(e.getMessage());
			return Constant.failureMessage("数据查询失败！");
		}
	}
	@ResponseBody
	@RequestMapping(value="/updateReportModelByReportModelID.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="通过reportModelID更新报告模板基本信息")
	public String updateReportModelByReportModelID(HttpServletRequest request,ReportModel reportModel){
		try{
			//
			int result = reportModelService.updateReportModelByReportModelID(reportModel);
			//有影响的行数，则判断更新成功
			if(result>0){
				return Constant.successMessage();
			}else{
				return Constant.failureMessage("更新失败！");
			}

		}catch(DataAccessException e){
			//唯一索引 异常处理
			return Unique_Exception(e);
		}catch (Exception e){
			log.error(e.getMessage());
			return Constant.failureMessage("更新失败！");
		}
	}
	@ResponseBody
	@RequestMapping(value="/getReportModelInfoByReportModelInfoID.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="通过reportModelInfoID获取报告模板详情信息")
	public String getReportModelInfoByReportModelInfoID(HttpServletRequest request){
		try{
			//模板详情ID
			String reportModelInfoID = request.getParameter("reportModelInfoID");
			ReportModelInfo result = reportModelService.getReportModelInfoByReportModelInfoID(reportModelInfoID);
			return Constant.successData(JSONObject.toJSONString(result, SerializerFeature.WriteMapNullValue));
		}catch (Exception e){
			log.error(e.getMessage());
			return Constant.failureMessage("数据查询失败！");
		}
	}
    @ResponseBody
    @RequestMapping(value="/updateReportModelInfoByReportModelInfoID.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="通过reportModelInfoID更新报告模板详情信息")
	public String updateReportModelInfoByReportModelInfoID(ReportModelInfo reportModelInfo){
        try{
            int resultCount = reportModelService.updateReportModelInfoByReportModelInfoID(reportModelInfo);
            //更新了一条记录，正常
            if(resultCount==1){
                return Constant.successMessage("更新成功");
            }else if(resultCount>1){
                return Constant.failureMessage("更新异常，请检查数据是否正常");
            }else{
                return Constant.failureMessage("更新失败");
            }
        }catch (Exception e){
            log.error(e.getMessage());
            return Constant.failureMessage("更新失败");
        }
    }
	@ResponseBody
	@RequestMapping(value="/updateReportModelInfo.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="批量更新模板基本及详情信息")
	public String updateReportModelInfo(HttpServletRequest request,ReportModel reportModel){
		/**
		 * 参数说明：（已弃用）
		 * 1.treeOrderNodes 要重新排序的字段 数组string [{"id":"","order":"","parentId":""}]
		 * 2.updateReportModelInfo 要更新的节点信息 数组string [{"reportModelInfoID":"","fontsize":"","content":""}] 一条记录为一个ReportModelInfo的bean
		 * 3.addReportModelInfo 新增节点
		 * 4.reportModel 报告模板基本信息 ReportModel 的bean
		 */
		try{
			//要更新的模板详情信息列表
			String updateInfo = request.getParameter("updateInfo");
			//数据格式化处理
			List<ReportModelInfo> list = new ArrayList<>();
			JSONArray array = JSONArray.fromObject(updateInfo);
			for(Object obj:array){
				//json转bean
				ReportModelInfo info = JavaBeanUtil.mapToBean((Map) net.sf.json.JSONObject.fromObject(obj), ReportModelInfo.class);
				list.add(info);
			}
			boolean result = reportModelService.updateReportModelInfo(list,reportModel);
			if(result){
				return Constant.successMessage();
			}else{
				return Constant.failureMessage("更新失败");
			}
		}catch (Exception e){
			log.error(e.getMessage());
			return Constant.failureMessage("更新失败！");
		}
	}
	@ResponseBody
	@RequestMapping(value="/getReportModelInfoTree.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取报告模板信息Tree")
	public String getReportModelInfoTree(HttpServletRequest request){
		try{
			//报告模板ID
			String reportModelID = request.getParameter("reportModelID");
			List<HSTree> tree = reportModelService.getReportModelInfoTree(reportModelID);
			return JSONArray.fromObject(tree).toString();
		}catch(Exception e){
			log.error(e.getMessage());
			return Constant.failureMessage("获取数据失败！");
		}
	}
	@ResponseBody
	@RequestMapping(value="/getReportModelInfoAll.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取报告模板基本信息及详情")
	public String getReportModelInfoAll(HttpServletRequest request){
		try{
			//报告模板ID
			String reportModelID = request.getParameter("reportModelID");
			//通过报告模板ID获取报告模板的基本信息以及详情信息

			Map<String,Object> result = reportModelService.getReportModelInfoAll(reportModelID);
			if(result.size()>0){
				//已正常获取到数据，转成json，直接返回即可
				return Constant.successData(JSONObject.toJSON(result).toString());
			}else{
				//未找到数据，返回
				return Constant.failureMessage("未查询到数据");
			}
		}catch(Exception e){
			log.error(e.getMessage());
			return Constant.failureMessage("获取数据失败！");
		}
	}
	@ResponseBody
	@RequestMapping(value="/getReportModelsByDashboardID.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="通过dashboardID获取关联的模板列表")
	public String getReportModelsByDashboardID(HttpServletRequest request){
		try{
			String dashboardID = request.getParameter("dashboardID");
			List<ComboxEntity> result = reportModelService.getReportModelListByDashboardID(dashboardID);
			if(result.size()==0){
				return Constant.failureMessage("该仪表板未关联报告模板！");
			}else{
				return Constant.successData(JSONObject.toJSON(result).toString());
			}
		}catch(Exception e){
			log.error(e.getMessage());
			return Constant.failureMessage("获取数据失败！");
		}
	}
	@ResponseBody
	@RequestMapping(value="/getReportModelListByDashboardID_ReportModelAdd.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="通过dashboardID获取关联的模板列表，用于添加模板使用")
	public String getReportModelListByDashboardID(HttpServletRequest request){
		try{
			String dashboardID = request.getParameter("dashboardID");
			List<ComboxEntity> result = reportModelService.getReportModelListByDashboardID_ReportModelAdd(dashboardID);

			return Constant.successData(JSONObject.toJSON(result).toString());

		}catch(Exception e){
			log.error(e.getMessage());
			return Constant.failureMessage("获取数据失败！");
		}
	}
	@ResponseBody
	@RequestMapping(value="/getAllDashboardList.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取所有的dashboard列表")
	public String getAllDashboardList(HttpServletRequest request){
		try{
			List<ComboxEntity> result = reportModelService.getAllDashboardList();
			return Constant.successData(JSONObject.toJSON(result).toString());
		}catch(Exception e){
			log.error(e.getMessage());
			return Constant.failureMessage("仪表板信息获取失败！");
		}
	}
	@ResponseBody
	@RequestMapping(value="/getTablesByDashboardID.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取dashboard中的所有表格信息")
	public String getTablesByDashboardID(HttpServletRequest request){
		try{
			String dashboardID = request.getParameter("dashboardID");
			List<ComboxEntity> result = reportModelService.getTablesByDashboardID(dashboardID);
			return Constant.successData(JSONObject.toJSON(result).toString());
		}catch(Exception e){
			log.error(e.getMessage());
			return Constant.failureMessage("表格信息获取失败！");
		}
	}
	@ResponseBody
	@RequestMapping(value="/getImagesByDashboardID.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取dashboard中的可以转化成图片的图表信息")
	public String getImagesByDashboardID(HttpServletRequest request){
		try{
			String dashboardID = request.getParameter("dashboardID");
			List<ComboxEntity> result = reportModelService.getImagesByDashboardID(dashboardID);
			return Constant.successData(JSONObject.toJSON(result).toString());
		}catch(Exception e){
			log.error(e.getMessage());
			return Constant.failureMessage("图表信息获取失败！");
		}
	}
	@ResponseBody
	@RequestMapping(value="/getMetricsByDashboardID.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取dashboard中的指标报表")
	public String getMetricsByDashboardID(HttpServletRequest request){
		try{
			String dashboardID = request.getParameter("dashboardID");
			List<ComboxEntity> result = reportModelService.getMetricsByDashboardID(dashboardID);
			return Constant.successData(JSONObject.toJSON(result).toString());
		}catch(Exception e){
			log.error(e.getMessage());
			return Constant.failureMessage("指标信息获取失败！");
		}
	}
	@ResponseBody
	@RequestMapping(value="/UpdateTreeOrder.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="更新报告模板详情Tree的顺序")
	public String UpdateTreeOrder(HttpServletRequest request){
		//前端通过左侧tree节点的拖拽，实现调整顺序及子父关系。
		try{
			//要修改的节点信息参数
			String nodeList_Str = request.getParameter("nodeList");
			int result = reportModelService.UpdateTreeOrder(nodeList_Str);
			if(result>0){
				return Constant.successMessage();
			}else{
				return Constant.failureMessage("节点信息更新失败！请重新进入编辑页面！");
			}
		}catch(Exception e){
			log.error(e.getMessage());
			return Constant.failureMessage("节点信息更新失败！");
		}
	}
	@ResponseBody
	@RequestMapping(value="/insertReportModelInfo.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="添加报告模板详情记录")
	public String insertReportModelInfo(ReportModelInfo reportModelInfo){
		try{
			ReportModelInfo result = reportModelService.insertReportModelInfo(reportModelInfo);
			return Constant.successData(JSONObject.toJSON(result).toString());
		}catch(Exception e){
			log.error(e.getMessage());
			return Constant.failureMessage("添加失败！");
		}
	}
	@ResponseBody
	@RequestMapping(value="/deleteReportModelInfo.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="删除报告模板详情记录")
	public String deleteReportModelInfo(HttpServletRequest request){
		try{
			String reportModelInfoID = request.getParameter("reportModelInfoID");
			int result = reportModelService.deleteReportModelInfo(reportModelInfoID);
			if(result>0){
				return Constant.successMessage();
			}else{
				return Constant.failureMessage("删除失败！");
			}
		}catch(Exception e){
			log.error(e.getMessage());
			return Constant.failureMessage("删除失败！");
		}
	}
	@ResponseBody
	@RequestMapping(value="/insertReportModel.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="添加报告模板基本信息")
	public String insertReportModel(ReportModel reportModel,HttpServletRequest request){
		try{
			String extendReportModelID = request.getParameter("extendReportModelID");
			int result = reportModelService.insertReportModel(reportModel,extendReportModelID);
			if(result>0){
				return Constant.successMessage();
			}else{
				return Constant.failureMessage("添加失败！");
			}
		}catch(DataAccessException e){
			//唯一索引 异常处理
			return Unique_Exception(e);
		}catch(Exception e){
			log.error(e.getMessage());
			return Constant.failureMessage("添加失败！");
		}
	}
	@ResponseBody
	@RequestMapping(value="/deleteReportModel.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="删除报告模板")
	public String deleteReportModel(HttpServletRequest request){
		try{
			String reportModelIDs = request.getParameter("reportModelIDs");
			int result = reportModelService.deleteReportModel(reportModelIDs);
			if(result>0){
				return Constant.successMessage();
			}else{
				return Constant.failureMessage("删除失败！");
			}
		}catch(DataAccessException e){
			//唯一索引 异常处理
			return Unique_Exception(e);
		}catch(Exception e){
			log.error(e.getMessage());
			return Constant.failureMessage("删除失败！");
		}
	}
	/**
	 * 数据保存异常处理,spring mybatis 抛出的异常类型
	 * @param e
	 * @return
	 */
	private String Unique_Exception(DataAccessException e){
		//异常类型
		if(e.getMessage().indexOf("MySQLIntegrityConstraintViolationException")>=0){
			//根据异常信息判定是否存在唯一索引重复
			if(e.getMessage().indexOf("UNIQUE_REPORT_MODEL")>=0){
				log.info("报告模板的dashboardID和模板类型组成的唯一索引出现异常");
				return Constant.failureMessage("仪表板+模板类型 不能重复！");
			}else{
				log.error("报告模板基本信息数据更新异常！"+e.getMessage());
			}
		}else{
			log.error("报告模板基本信息数据更新异常！"+e.getMessage());
		}
		//其他异常情况
		return Constant.failureMessage("保存失败！");
	}
}

package com.jz.bigdata.common.ReportModel.service;

import java.util.List;
import java.util.Map;

import com.hs.elsearch.entity.SearchConditions;
import com.jz.bigdata.common.ReportModel.entity.ReportModel;
import com.jz.bigdata.common.ReportModel.entity.ReportModelInfo;
import com.jz.bigdata.util.ComboxEntity;
import com.jz.bigdata.util.HSTree.HSTree;

/**
 * @description
 */
public interface IReportModelService {
	/**
	 * 生成报告
	 * @param images 图片
	 * @param tables 表格
	 * @param metrics 指标
	 * @param dashboardID 仪表板ID
	 * @param reportModelType 报告类型（日报、周报、月报）
	 * @param last 事件范围
	 * @return 生成的报告文件的路径path
	 * @throws Exception
	 */
	String createReport(String images, String tables, String metrics, String dashboardID, String reportModelType,String last ) throws Exception;
	/**
	 * 查询报告模板列表
	 * @param startRecord 开始行
	 * @param PageSize 一页的条数
	 * @param reportModelName 模板名称
	 * @param reportModelType 模板类型
	 * @return
	 */
	Map<String, Object> getReportModelListByPage(int startRecord, int PageSize, String reportModelName, String reportModelType)throws Exception;

	/**
	 * 通过报告模板ID获取模板信息
	 * @param reportModelID 模板id
	 * @return
	 */
	ReportModel getReportModelByReportModelID(String reportModelID) ;
	/**
	 *  通过reportModelID更新模板信息
	 * @param reportModel 报告模板bean
	 * @return 语句影响的行数
	 */
	int updateReportModelByReportModelID(ReportModel reportModel) ;
	/**
	 * 通过ReportModelInfoID 获取ReportModelInfo  bean
	 * @param reportModelInfoID
	 * @return
	 */
	ReportModelInfo getReportModelInfoByReportModelInfoID(String reportModelInfoID) ;

	/**
	 * 批量更新模板详情信息
	 * @param list 模板详情列表
	 * @param reportModel 模板基本信息
	 * @return
	 */
	boolean updateReportModelInfo(List<ReportModelInfo> list,ReportModel reportModel) ;

	/**
	 *  通过报告模板ID 获取模板详情的tree结构数据
	 * @param reportModelID
	 * @return
	 */
	List<HSTree> getReportModelInfoTree(String reportModelID) ;

	/**
	 * 通过报告模板ID获取报告模板的基本信息以及详情信息
	 * @param reportModelID
	 * @return key:reportModel(基本信息) reportModelInfo（详情信息）
	 */
	Map<String,Object> getReportModelInfoAll(String reportModelID);

	/**
	 * 通过reportModelInfoID更新报告模板详情信息
	 * @param reportModelInfo 模板详情信息bean
	 * @return 更新的条数
	 */
	int updateReportModelInfoByReportModelInfoID(ReportModelInfo reportModelInfo);

	/**
	 * 通过dashboardID 获取报告模板信息列表
	 * @param dashboardID
	 * @return
	 */
	List<ComboxEntity> getReportModelListByDashboardID(String dashboardID);
	/**
	 * 通过dashboardID 获取报告模板信息列表
	 * @param dashboardID
	 * @return
	 */
	List<ComboxEntity> getReportModelListByDashboardID_ReportModelAdd(String dashboardID);

	/**
	 * 获取所有的dashboard列表（服务于下拉框）
	 * @return
	 */
	List<ComboxEntity> getAllDashboardList() throws Exception;

	/**
	 * 获取dashboard中的所有表格信息
	 * @param dashboardID
	 * @return
	 */
	List<ComboxEntity> getTablesByDashboardID(String dashboardID) throws Exception;
	/**
	 * 获取dashboard中的所有表格信息
	 * @param dashboardID
	 * @return
	 */
	List<ComboxEntity> getImagesByDashboardID(String dashboardID) throws Exception;
	/**
	 * 获取dashboard中的所有指标类型的报表
	 * @param dashboardID
	 * @return
	 */
	List<ComboxEntity> getMetricsByDashboardID(String dashboardID) throws Exception;
	/**
	 * 批量更新tree节点的子父关系和排序信息
	 * @param nodeList_Str
	 * @return 影响条数
	 */
	int UpdateTreeOrder(String nodeList_Str);

	/**
	 * 添加一条模板详情信息
	 * @param reportModelInfo
	 * @return 添加的信息（ID和文本）
	 */
	ReportModelInfo insertReportModelInfo(ReportModelInfo reportModelInfo);

	/**
	 * 根据模板信息详情ID 删除记录
	 * @param reportModelInfoID 要删除节点的ID
	 * @return
	 */
	int deleteReportModelInfo(String reportModelInfoID);
	/**
	 * 添加一条报告模板
	 * @param reportModel
	 * @param extendReportModelID 要继承的模板ID
	 * @return
	 */
	int insertReportModel(ReportModel reportModel,String extendReportModelID);

	/**
	 * 删除报告模板记录
	 * @param reportModelIDs 报告模板唯一ID，以逗号隔开
	 * @return
	 */
	int deleteReportModel(String reportModelIDs);
}

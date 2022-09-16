package com.jz.bigdata.common.ReportModel.dao;

import java.util.List;
import java.util.Map;

import com.jz.bigdata.common.ReportModel.entity.ReportModel;
import com.jz.bigdata.common.ReportModel.entity.ReportModelData;
import com.jz.bigdata.common.ReportModel.entity.ReportModelInfo;
import com.jz.bigdata.common.eventGroup.entity.Event;
import com.jz.bigdata.common.eventGroup.entity.EventGroup;
import com.jz.bigdata.common.eventGroup.entity.EventGroupRelations;
import org.apache.ibatis.annotations.Param;


/**
 * @description
 */
public interface IReportModelDao {
    /**
     * 通过父节点获取 模板设计详情信息
     * @param parentID 父节点ID
     * @return
     */
	List<ReportModelInfo> getReportModelInfoListByParentIDAndReportModelID(@Param("parentID")String parentID,@Param("reportModelID")String reportModelID);

    /**
     * 通过报告模板ID 和模板类型 获取模板信息
     * @param dashboardID 仪表板ID
     * @param reportModelType 报告模板类型
     * @return
     */
    List<ReportModel> getReportModelByDashboardIDAndType(@Param("dashboardID")String dashboardID,@Param("reportModelType")String reportModelType);


    /**
     * 通过报告模板ID获取模板信息
     * @param reportModelID 模板id
     * @return
     */
	ReportModel getReportModelByReportModelID(@Param("reportModelID")String reportModelID);

    /**
     * 通过ReportModelDataKey获取信息
     * @param ReportModelDataKey
     * @return
     */
    ReportModelData getReportModelDataByKey(@Param("ReportModelDataKey")String ReportModelDataKey);

    /**
     * 查询报告模板列表
     * @param startRecord 开始行
     * @param PageSize 一页的条数
     * @param reportModelName 模板名称
     * @param reportModelType 模板类型
     * @return 报告模板列表
     */
    List<ReportModel> getReportModelListByPage(@Param("startRecord")int startRecord,@Param("pageSize")int pageSize,@Param("reportModelName")String reportModelName,@Param("reportModelType")String reportModelType);

    /**
     * 获取报告模板基本信息列表的count（带查询条件）
     * @param reportModelName 模板名称
     * @param reportModelType 模板类型
     * @return
     */
    List<List<Map<String,String>>> getReportModelCount(@Param("reportModelName")String reportModelName,@Param("reportModelType")String reportModelType);
    /**
     *  通过reportModelID更新模板信息
     * @param reportModel 报告模板bean
     * @return 更新条数
     */
    int updateReportModelByReportModelID(ReportModel reportModel);
    /**
     *  通过reportModelID更新模板页眉页脚
     * @param reportModel 报告模板bean
     * @return 更新条数
     */
    int updateReportModelHeaderAndFooterByReportModelID(ReportModel reportModel);

    /**
     * 通过ReportModelInfoID 获取ReportModelInfo  bean
     * @param reportModelInfoID
     * @return
     */
    ReportModelInfo getReportModelInfoByReportModelInfoID(@Param("reportModelInfoID")String reportModelInfoID);

    /**
     * 根据模板详情ID 更新详情信息
     * @param reportModelInfo 模板详情bean
     * @return 影响行数
     */
    int updateReportModelInfoByReportModelInfoID(ReportModelInfo reportModelInfo);

    /**
     * 通过报告模板ID，获取所有的报告模板详情信息
     * @param reportModelID
     * @return
     */
    List<ReportModelInfo> getReportModelInfoByReportModelID(@Param("reportModelID")String reportModelID);

    /**
     * 通过dashboardID 获取报告模板信息列表
     * @param dashboardID
     * @return
     */
    List<ReportModel> getReportModelListByDashboardID(@Param("dashboardID")String dashboardID);

    /**
     * 暂不使用
     * 更新一条模板详情信息的tree关联字段 包括：reportModelInfoID，parentID，orderCode
     * @param reportModelInfoID 模板详情信息id
     * @param parentID 父节点id
     * @param orderCode 排序序号
     * @return
     */
    int UpdateTreeColumns(@Param("reportModelInfoID")String reportModelInfoID,@Param("parentID")String parentID,@Param("orderCode")int orderCode);

    /**
     * 批量更新模板详情信息的tree关联字段 包括：reportModelInfoID，parentID，orderCode
     * @param list
     * @return
     */
    int batchUpdateTreeInfo(List<ReportModelInfo> list);

    /**
     * 新增一条模板信息详情
     * @param reportModelInfo
     * @return
     */
    int insertReportModelInfo(ReportModelInfo reportModelInfo);

    /**
     * 批量添加模板详情信息
     * @param list
     * @return
     */
    int batchInsertReportModelInfo(List<ReportModelInfo> list);

    /**
     *
     * @param reportModelInfoID
     * @return
     */
    int deleteReportModelInfo(@Param("reportModelInfoID")String reportModelInfoID);

    /**
     * 添加一条报告模板
     * @param reportModel
     * @return
     */
    int saveReportModel(ReportModel reportModel);

    /**
     * 删除模板信息，包括该模板对应的详情信息
     * @param reportModelID 模板id
     * @return
     */
    int deleteReportModel(String reportModelID);
}

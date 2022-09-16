package com.jz.bigdata.common.ReportModel.entity;

/**
 * 报告模板
 */
public class ReportModel {
    private String reportModelID;//唯一UUID
    private String reportModelName;//报告名称
    private String reportModelType;//报告类型-英文（日报day、周报week、月报month）
    private String reportModelType_CN;//报告类型-中文
    private String reportModelExplain;//报告说明
    private String reportModelHeader;//页眉的内容
    private String reportModelFooter;//页脚的内容
    private String dashboardID;//仪表板ID
    private int state;//报告状态 （1：启用 0：停用）


    public String getReportModelType_CN() {
        return reportModelType_CN;
    }

    public void setReportModelType_CN(String reportModelType_CN) {
        this.reportModelType_CN = reportModelType_CN;
    }

    public String getDashboardID() {
        return dashboardID;
    }

    public void setDashboardID(String dashboardID) {
        this.dashboardID = dashboardID;
    }

    public String getReportModelID() {
        return reportModelID;
    }

    public void setReportModelID(String reportModelID) {
        this.reportModelID = reportModelID;
    }

    public String getReportModelName() {
        return reportModelName;
    }

    public void setReportModelName(String reportModelName) {
        this.reportModelName = reportModelName;
    }

    public String getReportModelType() {
        return reportModelType;
    }

    public void setReportModelType(String reportModelType) {
        this.reportModelType = reportModelType;
    }

    public String getReportModelExplain() {
        return reportModelExplain;
    }

    public void setReportModelExplain(String reportModelExplain) {
        this.reportModelExplain = reportModelExplain;
    }

    public String getReportModelHeader() {
        return reportModelHeader;
    }

    public void setReportModelHeader(String reportModelHeader) {
        this.reportModelHeader = reportModelHeader;
    }

    public String getReportModelFooter() {
        return reportModelFooter;
    }

    public void setReportModelFooter(String reportModelFooter) {
        this.reportModelFooter = reportModelFooter;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}

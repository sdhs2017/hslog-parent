package com.jz.bigdata.common.ReportModel.entity;

/**
 * 报告模板数据获取
 */
public class ReportModelData {
    private String reportModelDataKey;//文本内容中需要动态获取数据的标识
    /**
     * 获取数据的类型
     * image（图片）、date（日期）、sql（通过sql语句）、es（通过es），sql_table（数据库表格），es_table(es表格)
     * 获取数据的类型
     * image（图片）、date（日期）、sql（通过sql语句）、es（通过es），sql_table（数据库表格），es_table(es表格)
     */
    private String reportModelDataType;
    /**
     * 根据类型进行填写的获取数据的api：
     * date：yyyy-mm-dd
     * sql/sql_table:select count(1) as count from equipment
     * es:curl命令
     * es_table:可视化报表id
     */
    private String content;
    /**
     * 服务于image、sql、es
     * image为通过request获取的参数的名称。
     * sql、es为要获取查询出来的数据的列名
     * sql_table 需要获取多个列，并设置对应中文,eg：资产名称@equipmentname,IP地址@ip，日志类型@logType
     */
    private String dataGetFields;

    public String getReportModelDataKey() {
        return reportModelDataKey;
    }

    public void setReportModelDataKey(String reportModelDataKey) {
        this.reportModelDataKey = reportModelDataKey;
    }

    public String getReportModelDataType() {
        return reportModelDataType;
    }

    public void setReportModelDataType(String reportModelDataType) {
        this.reportModelDataType = reportModelDataType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDataGetFields() {
        return dataGetFields;
    }

    public void setDataGetFields(String dataGetFields) {
        this.dataGetFields = dataGetFields;
    }
}

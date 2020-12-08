package com.jz.bigdata.common.businessIntelligence.entity;

import com.jz.bigdata.business.logAnalysis.log.mappingbean.MappingOfSyslog;
import com.jz.bigdata.util.Bean2Mapping;

/**
 * 用来构建提交到ES的数据，
 * ES中的mapping信息存在一个目录关系：
 * 即visualization和dashboard是两个目录，各自有其对应的字段信息。
 * 在提交数据时，需要在数据外层加上一个visualization：{数据}
 * 考虑到后期还会增加其他的数据，因此使用一个统一的类进行管理
 * 并提供template初始化所需mapping信息
 */
public class HSData {
    private Visualization visualization;//图表
    private Dashboard dashboard;//仪表盘
    private boolean built_in;//是否为内置报表

    public boolean isBuilt_in() {
        return built_in;
    }

    public void setBuilt_in(boolean built_in) {
        this.built_in = built_in;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public Visualization getVisualization() {
        return visualization;
    }

    public void setVisualization(Visualization visualization) {
        this.visualization = visualization;
    }
    public String toMapping() {
        //图表mapping
        String visualFieldString =new Visualization().toMapping();
        //仪表盘mapping
        String dashboardFieldString =new Dashboard().toMapping();
        String hsdataTemplate = "{\n"+"\t\t\"properties\":{\n\"built_in\":{\"type\":\"boolean\"}," +visualFieldString+","+ dashboardFieldString+"\t\t\n" + "\t\t\t\t}"+"}";
        return hsdataTemplate;
    }
    public static void main(String[] args){
        System.out.println(new HSData().toMapping());
    }
}

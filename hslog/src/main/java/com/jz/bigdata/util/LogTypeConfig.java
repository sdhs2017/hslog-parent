package com.jz.bigdata.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2022/3/29 15:53
 * @Description: 与日志类型有关的配置文件。
 * 通过该配置文件实现前端日志类型下拉框、联动的日志级别下拉框、table、详情页的数据加载
 */
@Component("LogTypeConfig")
@Configuration
public class LogTypeConfig {
    @Value("#{logTypeConfig.logTypeToWeb}")
    private String logTypeToWeb;
    @Value("#{logTypeConfig.logTypeEquipment}")
    private String logTypeEquipment;
    @Value("#{logTypeConfig.logTypeSearch}")
    private String logTypeSearch;
    @Value("#{logTypeConfig.logTypeEvent}")
    private String logTypeEvent;
    @Value("#{logTypeConfig.logLevel_Syslog}")
    private String logLevel_Syslog;
    @Value("#{logTypeConfig.logLevel_winlog}")
    private String logLevel_winlog;
    @Value("#{logTypeConfig.logLevel_DNS}")
    private String logLevel_DNS;
    @Value("#{logTypeConfig.logLevel_DHCP}")
    private String logLevel_DHCP;
    @Value("#{logTypeConfig.tableHead_syslog}")
    private String tableHead_syslog;
    @Value("#{logTypeConfig.tableHead_winlog}")
    private String tableHead_winlog;
    @Value("#{logTypeConfig.tableHead_packet}")
    private String tableHead_packet;
    @Value("#{logTypeConfig.tableHead_metric}")
    private String tableHead_metric;
    @Value("#{logTypeConfig.tableHead_file}")
    private String tableHead_file;
    @Value("#{logTypeConfig.tableHead_mysql}")
    private String tableHead_mysql;
    @Value("#{logTypeConfig.tableHead_dns}")
    private String tableHead_dns;
    @Value("#{logTypeConfig.tableHead_dhcp}")
    private String tableHead_dhcp;
    @Value("#{logTypeConfig.formDetails_syslog}")
    private String formDetails_syslog;
    @Value("#{logTypeConfig.formDetails_winlog}")
    private String formDetails_winlog;
    @Value("#{logTypeConfig.formDetails_packet}")
    private String formDetails_packet;
    @Value("#{logTypeConfig.formDetails_metric}")
    private String formDetails_metric;
    @Value("#{logTypeConfig.formDetails_file}")
    private String formDetails_file;
    @Value("#{logTypeConfig.formDetails_mysql}")
    private String formDetails_mysql;
    @Value("#{logTypeConfig.formDetails_dns}")
    private String formDetails_dns;
    @Value("#{logTypeConfig.formDetails_dhcp}")
    private String formDetails_dhcp;
    @Value("#{logTypeConfig.logType_syslog}")
    private String logType_syslog;
    @Value("#{logTypeConfig.logType_winlog}")
    private String logType_winlog;
    @Value("#{logTypeConfig.logType_metric}")
    private String logType_metric;
    @Value("#{logTypeConfig.logType_packet}")
    private String logType_packet;
    @Value("#{logTypeConfig.logType_file}")
    private String logType_file;
    @Value("#{logTypeConfig.logType_mysql}")
    private String logType_mysql;
    @Value("#{logTypeConfig.logType_dns}")
    private String logType_dns;
    @Value("#{logTypeConfig.logType_dhcp}")
    private String logType_dhcp;
    @Value("#{logTypeConfig.formDetails_event}")
    private String formDetails_event;

    public String getFormDetails_event() {
        return formDetails_event;
    }

    public void setFormDetails_event(String formDetails_event) {
        this.formDetails_event = formDetails_event;
    }

    public String getTableHead_mysql() {
        return tableHead_mysql;
    }

    public void setTableHead_mysql(String tableHead_mysql) {
        this.tableHead_mysql = tableHead_mysql;
    }

    public String getLogTypeToWeb() {
        return logTypeToWeb;
    }

    public void setLogTypeToWeb(String logTypeToWeb) {
        this.logTypeToWeb = logTypeToWeb;
    }

    public String getLogTypeEquipment() {
        return logTypeEquipment;
    }

    public void setLogTypeEquipment(String logTypeEquipment) {
        this.logTypeEquipment = logTypeEquipment;
    }

    public String getLogTypeSearch() {
        return logTypeSearch;
    }

    public void setLogTypeSearch(String logTypeSearch) {
        this.logTypeSearch = logTypeSearch;
    }

    public String getLogTypeEvent() {
        return logTypeEvent;
    }

    public void setLogTypeEvent(String logTypeEvent) {
        this.logTypeEvent = logTypeEvent;
    }

    public String getLogLevel_Syslog() {
        return logLevel_Syslog;
    }

    public void setLogLevel_Syslog(String logLevel_Syslog) {
        this.logLevel_Syslog = logLevel_Syslog;
    }

    public String getLogLevel_winlog() {
        return logLevel_winlog;
    }

    public void setLogLevel_winlog(String logLevel_winlog) {
        this.logLevel_winlog = logLevel_winlog;
    }

    public String getLogLevel_DNS() {
        return logLevel_DNS;
    }

    public void setLogLevel_DNS(String logLevel_DNS) {
        this.logLevel_DNS = logLevel_DNS;
    }

    public String getLogLevel_DHCP() {
        return logLevel_DHCP;
    }

    public void setLogLevel_DHCP(String logLevel_DHCP) {
        this.logLevel_DHCP = logLevel_DHCP;
    }

    public String getTableHead_syslog() {
        return tableHead_syslog;
    }

    public void setTableHead_syslog(String tableHead_syslog) {
        this.tableHead_syslog = tableHead_syslog;
    }

    public String getTableHead_winlog() {
        return tableHead_winlog;
    }

    public void setTableHead_winlog(String tableHead_winlog) {
        this.tableHead_winlog = tableHead_winlog;
    }

    public String getTableHead_packet() {
        return tableHead_packet;
    }

    public void setTableHead_packet(String tableHead_packet) {
        this.tableHead_packet = tableHead_packet;
    }

    public String getTableHead_metric() {
        return tableHead_metric;
    }

    public void setTableHead_metric(String tableHead_metric) {
        this.tableHead_metric = tableHead_metric;
    }

    public String getTableHead_file() {
        return tableHead_file;
    }

    public void setTableHead_file(String tableHead_file) {
        this.tableHead_file = tableHead_file;
    }

    public String getTableHead_dns() {
        return tableHead_dns;
    }

    public void setTableHead_dns(String tableHead_dns) {
        this.tableHead_dns = tableHead_dns;
    }

    public String getTableHead_dhcp() {
        return tableHead_dhcp;
    }

    public void setTableHead_dhcp(String tableHead_dhcp) {
        this.tableHead_dhcp = tableHead_dhcp;
    }

    public String getFormDetails_syslog() {
        return formDetails_syslog;
    }

    public void setFormDetails_syslog(String formDetails_syslog) {
        this.formDetails_syslog = formDetails_syslog;
    }

    public String getFormDetails_winlog() {
        return formDetails_winlog;
    }

    public void setFormDetails_winlog(String formDetails_winlog) {
        this.formDetails_winlog = formDetails_winlog;
    }

    public String getFormDetails_packet() {
        return formDetails_packet;
    }

    public void setFormDetails_packet(String formDetails_packet) {
        this.formDetails_packet = formDetails_packet;
    }


    public String getFormDetails_file() {
        return formDetails_file;
    }

    public void setFormDetails_file(String formDetails_file) {
        this.formDetails_file = formDetails_file;
    }

    public String getFormDetails_mysql() {
        return formDetails_mysql;
    }

    public void setFormDetails_mysql(String formDetails_mysql) {
        this.formDetails_mysql = formDetails_mysql;
    }

    public String getFormDetails_dns() {
        return formDetails_dns;
    }

    public void setFormDetails_dns(String formDetails_dns) {
        this.formDetails_dns = formDetails_dns;
    }

    public String getFormDetails_dhcp() {
        return formDetails_dhcp;
    }

    public void setFormDetails_dhcp(String formDetails_dhcp) {
        this.formDetails_dhcp = formDetails_dhcp;
    }

    public String getLogType_syslog() {
        return logType_syslog;
    }

    public void setLogType_syslog(String logType_syslog) {
        this.logType_syslog = logType_syslog;
    }

    public String getLogType_winlog() {
        return logType_winlog;
    }

    public void setLogType_winlog(String logType_winlog) {
        this.logType_winlog = logType_winlog;
    }


    public String getLogType_packet() {
        return logType_packet;
    }

    public void setLogType_packet(String logType_packet) {
        this.logType_packet = logType_packet;
    }

    public String getLogType_file() {
        return logType_file;
    }

    public void setLogType_file(String logType_file) {
        this.logType_file = logType_file;
    }

    public String getLogType_mysql() {
        return logType_mysql;
    }

    public void setLogType_mysql(String logType_mysql) {
        this.logType_mysql = logType_mysql;
    }

    public String getLogType_dns() {
        return logType_dns;
    }

    public void setLogType_dns(String logType_dns) {
        this.logType_dns = logType_dns;
    }

    public String getLogType_dhcp() {
        return logType_dhcp;
    }

    public void setLogType_dhcp(String logType_dhcp) {
        this.logType_dhcp = logType_dhcp;
    }

    public String getFormDetails_metric() {
        return formDetails_metric;
    }

    public void setFormDetails_metric(String formDetails_metric) {
        this.formDetails_metric = formDetails_metric;
    }

    public String getLogType_metric() {
        return logType_metric;
    }

    public void setLogType_metric(String logType_metric) {
        this.logType_metric = logType_metric;
    }

    private Map<String,String> logTypeToWebMap;

    public void setLogTypeToWebMap(Map<String, String> logTypeToWebMap) {
        this.logTypeToWebMap = logTypeToWebMap;
    }

    /**
     * 获取日志类型后端与前端的对应关系，
     * eg：winlogbeat:winlog,packetbeat:packet,metricbeat:metric
     * @return map  key为后端值，value为前端要显示的值
     */
    public Map<String,String> getLogTypeToWebMap(){
        //判断是否已经进行了数据处理并赋值
        //为空则表示没有，进行数据的格式化处理
        if(logTypeToWebMap==null){
            Map<String,String> result = new HashMap<>();
            //根据数据格式进行格式化处理
            String[] logTypeToWebArray = this.getLogTypeToWeb().split(",");
            for(String logTypeToWebInfo:logTypeToWebArray){
                String[] logTypeToWebInfoArray = logTypeToWebInfo.split(":");
                //长度必须为2，保证与map的 k/V 匹配
                if(logTypeToWebInfoArray.length==2){
                    result.put(logTypeToWebInfoArray[0],logTypeToWebInfoArray[1]);
                }
            }
            return result;
        }else{
            //不为空，表示已处理，直接返回
            return logTypeToWebMap;
        }

    }

}

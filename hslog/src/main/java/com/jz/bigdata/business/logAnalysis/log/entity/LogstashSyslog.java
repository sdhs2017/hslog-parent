package com.jz.bigdata.business.logAnalysis.log.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import org.joda.time.DateTime;

public class LogstashSyslog {

    private Object message;//日志描述
    private Object pid;//进程id
    private Object host;//IP地址
    private Object type;//日志类型，所有数据都相同
    private Object timestamp;//时间
    private Object program;//产生日志的程序名称
    private Object facility;//数据生成源，ID
    private Object facility_label;//数据生成源，标签
    private Object logsource;//设备名称
    private Object severity;//日志等级，共8个等级
    private Object severity_label;//严重性标签
    private Object severity_name;//与severity_label相同，用来输出 写入ES的JSON
    private Object priority;//优先级：facility * 8 + severity 优先级越低情况越严重。
    private Object tags; //logstash格式匹配标签
    private String ip;//ip信息
    private String equipmentid;//资产id
    private String equipmentname;//资产名称
    private String userid;//用户id
    private String deptid;//部门id
    private Boolean failure;// logstash范式化是否失败，失败true，成功false
    private String assetname;//逻辑资产名称
    private String assetid;//逻辑资产id
    private Object module;//资产业务模块

    public String getAssetid() {
        return assetid;
    }

    public void setAssetid(String assetid) {
        this.assetid = assetid;
    }

    public void setSeverity_label(Object severity_label) {
        this.severity_label = severity_label;
    }

    public String getAssetname() {
        return assetname;
    }

    public void setAssetname(String assetname) {
        this.assetname = assetname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getEquipmentid() {
        return equipmentid;
    }

    public void setEquipmentid(String equipmentid) {
        this.equipmentid = equipmentid;
    }

    public String getEquipmentname() {
        return equipmentname;
    }

    public void setEquipmentname(String equipmentname) {
        this.equipmentname = equipmentname;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDeptid() {
        return deptid;
    }

    public void setDeptid(String deptid) {
        this.deptid = deptid;
    }

    @JSONField(name ="message")
    public Object getMessage() {
        return message;
    }
    @JSONField(name ="message")
    public void setMessage(Object message) {
        this.message = message;
    }
    @JSONField(name ="winlog.process.pid")
    public Object getPid() {
        return pid;
    }
    @JSONField(name ="pid")
    public void setPid(Object pid) {
        this.pid = pid;
    }
    @JSONField(name ="fields.ip")
    public Object getHost() {
        return host;
    }
    @JSONField(name ="host")
    public void setHost(Object host) {
        this.host = host;
    }
    @JSONField(name ="agent.type")
    public Object getType() {
        return type;
    }
    @JSONField(name ="type")
    public void setType(Object type) {
        this.type = type;
    }
    @JSONField(name ="@timestamp")
    public Object getTimestamp() {
        return timestamp;
    }
    @JSONField(name ="@timestamp")
    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
    @JSONField(name ="event.kind")
    public Object getProgram() {
        return program;
    }
    @JSONField(name ="program")
    public void setProgram(Object program) {
        this.program = program;
    }
    @JSONField(name ="syslog.facility.code")
    public Object getFacility() {
        return facility;
    }
    @JSONField(name ="facility")
    public void setFacility(Object facility) {
        this.facility = facility;
    }
    @JSONField(name ="syslog.facility.name")
    public Object getFacility_label() {
        return facility_label;
    }
    @JSONField(name ="facility_label")
    public void setFacility_label(Object facility_label) {
        this.facility_label = facility_label;
    }
    @JSONField(name ="host.hostname")
    public Object getLogsource() {
        return logsource;
    }
    @JSONField(name ="logsource")
    public void setLogsource(Object logsource) {
        this.logsource = logsource;
    }
    @JSONField(name ="syslog.severity.code")
    public Object getSeverity() {
        return severity;
    }
    @JSONField(name ="severity")
    public void setSeverity(Object severity) {
        this.severity = severity;
    }
    @JSONField(name ="log.level")
    public Object getSeverity_label() {
        return severity_label;
    }

    @JSONField(name ="syslog.severity.name")
    public Object getSeverity_name() {
        return severity_name;
    }
    @JSONField(name ="severity_label")
    public void setSeverity_name(Object severity_name) {
        //只有一个severity_label的注解，但是因为需要两个字段输出，因此进行两次赋值
        this.severity_label = severity_name;
        this.severity_name = severity_name;
    }
    @JSONField(name ="syslog.priority")
    public Object getPriority() {
        return priority;
    }
    @JSONField(name ="priority")
    public void setPriority(Object priority) {
        this.priority = priority;
    }

    @JSONField(name ="tags")
    public Object getTags() {
        return tags;
    }
    @JSONField(name ="tags")
    public void setTags(Object tags) {
        this.tags = tags;
    }

    public Boolean getFailure() {
        return failure;
    }

    public void setFailure(Boolean failure) {
        this.failure = failure;
    }

    @JSONField(name ="module")
    public Object getModule() {
        return module;
    }
    @JSONField(name ="module")
    public void setModule(Object module) {
        this.module = module;
    }

    public static void main(String [] args){

        //String log = "{\"priority\":30,\"facility\":3,\"severity_label\":\"Informational\",\"type\":\"system-syslog\",\"severity\":6,\"@timestamp\":\"2020-04-16T23:58:52.000Z\",\"host\":\"127.0.0.1\",\"facility_label\":\"system\",\"program\":\"logstash\",\"logsource\":\"jzlogserv181\",\"@version\":\"1\",\"message\":\"}\"}";
        String log = "{\"type\":\"system-syslog\",\"severity\":0,\"tags\":[\"_grokparsefailure_sysloginput\"],\"priority\":0,\"@version\":\"1\",\"facility\":0,\"facility_label\":\"kernel\",\"host\":\"172.21.0.9\",\"message\":\"<30>Apr 16 11:05:01 VM_0_9_centos logstash: [2020-04-16T11:05\n" +
                ":01,554][INFO ][logstash.inputs.syslog   ][main] new connection {:client=>\\\"172.21.0.9:60736\\\"}\\n\",\"@timestamp\":\"2020-04-16T11:05:01.650Z\",\"severity_label\":\"Emergency\"}";
        LogstashSyslog logstashSyslog = (LogstashSyslog) JSON.parseObject(log, LogstashSyslog.class);
        /**
         * 判断tags确认logstash对syslog的日志范式化是否成功
         */
        if (logstashSyslog.getTags()!=null&&logstashSyslog.getTags().toString().contains("failure")){
            logstashSyslog.setFailure(true);
        }else{
            logstashSyslog.setFailure(false);
        }

        String json = new Logstash2ECS().toJson(logstashSyslog);
        System.out.println(json);
    }
}

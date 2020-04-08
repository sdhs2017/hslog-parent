package com.jz.bigdata.business.logAnalysis.log.entity;

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
    private String ip;//ip信息
    private String equipmentid;//资产id
    private String equipmentname;//资产名称
    private String userid;//用户id
    private String deptid;//部门id

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

}

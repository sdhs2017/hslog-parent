package com.jz.bigdata.business.logAnalysis.log.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.jz.bigdata.util.Pattern_Matcher;
import com.mysql.jdbc.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import scala.Int;

import java.util.HashMap;
import java.util.Map;

public class Logstash2ECS {
    public final static Map<Integer, String> Severity = new HashMap<>();
    static{
        Severity.put(0, "emergency"); 		//system is unusable
        Severity.put(1, "alert");	 		//action must be taken immediately
        Severity.put(2, "critical");	 		//critical conditions
        Severity.put(3, "error");			//error conditions
        Severity.put(4, "warning");			//warning conditions
        Severity.put(5, "notice");			//normal but significant condition
        Severity.put(6, "information");	//informational messages
        Severity.put(7, "debug");			//debug-level messages
    }
    private Object priority;
    private Object message;
    private Agent agent;
    private Host host;
    private Event event;
    private Log log;
    @SerializedName("@timestamp")
    private Object timestamp;
    private Winlog winlog;
    private Fields fields;
    public class Syslog{
        private Severity severity;
        private Facility facility;
        private Object priority;

        public Severity getSeverity() {
            return severity;
        }

        public void setSeverity(Severity severity) {
            this.severity = severity;
        }

        public Facility getFacility() {
            return facility;
        }

        public void setFacility(Facility facility) {
            this.facility = facility;
        }

        public Object getPriority() {
            return priority;
        }

        public void setPriority(Object priority) {
            this.priority = priority;
        }
    }
    public class Severity{
        private Object code;
        private Object name;

        public Object getCode() {
            return code;
        }

        public void setCode(Object code) {
            this.code = code;
        }

        public Object getName() {
            return name;
        }

        public void setName(Object name) {
            this.name = name;
        }
    }
    public class Facility{
        private Object code;
        private Object name;

        public Object getCode() {
            return code;
        }

        public void setCode(Object code) {
            this.code = code;
        }

        public Object getName() {
            return name;
        }

        public void setName(Object name) {
            this.name = name;
        }
    }

    public class Agent{
        private Object type;

        public Object getType() {
            return type;
        }

        public void setType(Object type) {
            this.type = type;
        }
    }
    public class Host{
        private Object hostname;

        public Object getHostname() {
            return hostname;
        }

        public void setHostname(Object hostname) {
            this.hostname = hostname;
        }
    }
    public class Event{
        private Object kind;
        private String action;//事件类型
        private String action_cn;//事件中文说明

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getAction_cn() {
            return action_cn;
        }

        public void setAction_cn(String action_cn) {
            this.action_cn = action_cn;
        }

        public Object getKind() {
            return kind;
        }

        public void setKind(Object kind) {
            this.kind = kind;
        }
    }
    public class Log{
        private Object level;
        private Syslog syslog;

        public Syslog getSyslog() {
            return syslog;
        }

        public void setSyslog(Syslog syslog) {
            this.syslog = syslog;
        }

        public Object getLevel() {
            return level;
        }

        public void setLevel(Object level) {
            this.level = level;
        }
    }
    public class Winlog{
        private Process process;

        public Process getProcess() {
            return process;
        }

        public void setProcess(Process process) {
            this.process = process;
        }
    }
    public class Process{
        private Object pid;

        public Object getPid() {
            return pid;
        }

        public void setPid(Object pid) {
            this.pid = pid;
        }
    }
    public class Fields{
        private String ip;
        private String equipmentid;
        private String equipmentname;
        private String userid;
        private String deptid;
        private Boolean failure;
        private String assetname;
        private String assetid;

        public String getAssetid() {
            return assetid;
        }

        public void setAssetid(String assetid) {
            this.assetid = assetid;
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

        public Boolean getFailure() {
            return failure;
        }

        public void setFailure(Boolean failure) {
            this.failure = failure;
        }
    }

    public Fields getFields() {
        return fields;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }

    public Winlog getWinlog() {
        return winlog;
    }

    public void setWinlog(Winlog winlog) {
        this.winlog = winlog;
    }

    public Object getPriority() {
        return priority;
    }

    public void setPriority(Object priority) {
        this.priority = priority;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }
    @JSONField(name="@timestamp")
    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public Logstash2ECS build(LogstashSyslog logstashSyslog){
        //处理日志级别
        String severityName = Severity.get(logstashSyslog.getSeverity());
        //log.level
        Log log = new Log();
        log.setLevel(severityName);

        //log.syslog
        Logstash2ECS.Syslog syslog = new Logstash2ECS.Syslog();
        //syslog. severity
        Severity severity = new Severity();
        severity.setCode(logstashSyslog.getSeverity());//syslog. severity. code
        severity.setName(severityName); //syslog. severity. name
        syslog.setSeverity(severity);
        //syslog. facility
        Facility facility = new Facility();
        facility.setCode(logstashSyslog.getFacility());//syslog. facility.code
        facility.setName(logstashSyslog.getFacility_label());//syslog. facility.name
        syslog.setFacility(facility);
        //syslog. priority
        syslog.setPriority(logstashSyslog.getPriority());
        log.setSyslog(syslog);
        this.setLog(log);
        //message
        this.setMessage(logstashSyslog.getMessage());
        //agent.type
        Agent agent = new Agent();
        agent.setType("system-syslog".equals(logstashSyslog.getType())?"syslog":logstashSyslog.getType());
        this.setAgent(agent);
        //host.hostname
        Host host = new Host();
        host.setHostname(logstashSyslog.getLogsource());
        this.setHost(host);
        //event
        Event event = new Event();
        // event.kind
        event.setKind(logstashSyslog.getProgram());
        //event.action&action_cn 事件类别及描述
        if(Pattern_Matcher.isMatched(logstashSyslog.getMessage().toString(),"shutdown\\s+")){
            event.setAction("poweroff");
            event.setAction_cn("主机关机");
        }
        if(Pattern_Matcher.isMatched(logstashSyslog.getMessage().toString(),"Failed\\s+password\\s+for\\s+invalid\\s+user\\s+(.*?)\\s+from\\s+(\\d+\\.\\d+\\.\\d+\\.\\d+)\\s+port\\s+[0-9]{1,5}\\s+ssh2")){
            event.setAction("ssh failed");
            event.setAction_cn("ssh登录失败");
        }
        if(Pattern_Matcher.isMatched(logstashSyslog.getMessage().toString(),"NetworkManager")){
            event.setAction("NetworkManager");
            event.setAction_cn("网络服务");
        }
        if(Pattern_Matcher.isMatched(logstashSyslog.getMessage().toString(),"usb")){
            event.setAction("usb");
            event.setAction_cn("usb外接");
        }
        if(Pattern_Matcher.isMatched(logstashSyslog.getMessage().toString(),"Accepted password for (.*?) from (\\d+\\.\\d+\\.\\d+\\.\\d+) port [0-9]{1,5} ssh2")){
            event.setAction("sshd");
            event.setAction_cn("通过ssh方式进行操作");
        }
        if(Pattern_Matcher.isMatched(logstashSyslog.getMessage().toString(),"systemd-logind")){
            event.setAction("login");
            event.setAction_cn("用户登录");
        }
        if(Pattern_Matcher.isMatched(logstashSyslog.getMessage().toString(),"su:")){
            event.setAction("su");
            event.setAction_cn("通过su方式登录");
        }
        if(Pattern_Matcher.isMatched(logstashSyslog.getMessage().toString(),"Starting Session")||Pattern_Matcher.isMatched(logstashSyslog.getMessage().toString(),"session opened")){
            event.setAction("session");
            event.setAction_cn("开启新的会话窗口");
        }
        if(Pattern_Matcher.isMatched(logstashSyslog.getMessage().toString(),"rsyslogd")){
            event.setAction("rsyslogd");
            event.setAction_cn("rsyslog自身日志");
        }
        if(Pattern_Matcher.isMatched(logstashSyslog.getMessage().toString(),"kernel: pci")){
            event.setAction("pci");
            event.setAction_cn("pci日志");
        }
        if(Pattern_Matcher.isMatched(logstashSyslog.getMessage().toString(),"kernel: pci_bus")){
            event.setAction("pci_bus");
            event.setAction_cn("pci_bus日志");
        }
        if(Pattern_Matcher.isMatched(logstashSyslog.getMessage().toString(),"kernel: ACPI")){
            event.setAction("ACPI");
            event.setAction_cn("ACPI日志");
        }
        if(Pattern_Matcher.isMatched(logstashSyslog.getMessage().toString(),"kernel: PM")){
            event.setAction("PM");
            event.setAction_cn("PM日志");
        }
        if(Pattern_Matcher.isMatched(logstashSyslog.getMessage().toString(),"kernel: SRAT")){
            event.setAction("SRAT");
            event.setAction_cn("SRAT日志");
        }
        if(Pattern_Matcher.isMatched(logstashSyslog.getMessage().toString(),"CROND")){
            event.setAction("crond");
            event.setAction_cn("定时任务");
        }
        this.setEvent(event);
        //@timestamp
        this.setTimestamp(logstashSyslog.getTimestamp());
        //winlog. process.pid
        Winlog winlog = new Winlog();
        Process process = new Process();
        process.setPid(logstashSyslog.getPid());
        winlog.setProcess(process);
        this.setWinlog(winlog);
        //fields ip equipmentid equipmentname userid deptid failure
        Fields fields = new Fields();
        fields.setIp(logstashSyslog.getIp());
        fields.setDeptid(logstashSyslog.getDeptid());
        fields.setEquipmentid(logstashSyslog.getEquipmentid());
        fields.setEquipmentname(logstashSyslog.getEquipmentname());
        fields.setUserid(logstashSyslog.getUserid());
        fields.setFailure(logstashSyslog.getFailure());
        fields.setAssetid(logstashSyslog.getAssetid());
        fields.setAssetname(logstashSyslog.getAssetname());
        this.setFields(fields);
        return this;
        //return JSON.toJSON(this).toString();
    }
    public Logstash2ECS build(JsonObject jsonObject){

        //log.level
        Log log = new Log();
        //处理日志级别

        String severityCode = jsonObject.get("severity")==null?"":jsonObject.get("severity").getAsString();//编号
        String severityName = NumberUtils.isNumber(severityCode)?Severity.get(Integer.parseInt(severityCode)):"";//名称
        log.setLevel(severityName);

        //log.syslog
        Logstash2ECS.Syslog syslog = new Logstash2ECS.Syslog();
        //log.syslog.severity
        Severity severity = new Severity();
        severity.setCode(severityCode);//log.syslog.severity.code
        severity.setName(severityName); //log.syslog.severity.name
        syslog.setSeverity(severity);
        //log.syslog.facility
        Facility facility = new Facility();
        facility.setCode(jsonObject.get("facility")==null?"":jsonObject.get("facility").getAsString());//log.syslog.facility.code
        facility.setName(jsonObject.get("facility_label")==null?"":jsonObject.get("facility_label").getAsString());//log.syslog.facility.name
        syslog.setFacility(facility);
        //log.syslog.priority
        syslog.setPriority(jsonObject.get("priority")==null?"":jsonObject.get("priority").getAsString());
        log.setSyslog(syslog);
        this.setLog(log);
        //message
        String message =jsonObject.get("message").getAsString();
        this.setMessage(message);
        //agent.type
        Agent agent = new Agent();

        if(jsonObject.get("type")!=null){
            agent.setType("system-syslog".equals(jsonObject.get("type").getAsString())?"syslog":jsonObject.get("type").getAsString());
        }else{
            //不做处理
        }
        this.setAgent(agent);
        //host.hostname
        Host host = new Host();
        host.setHostname(jsonObject.get("logsource")==null?"":jsonObject.get("logsource").getAsString());
        this.setHost(host);
        //event
        Event event = new Event();
        // event.kind
        event.setKind(jsonObject.get("program")==null?"":jsonObject.get("program").getAsString());
        //event.action&action_cn 事件类别及描述
        if(Pattern_Matcher.isMatched(message,"shutdown\\s+")){
            event.setAction("poweroff");
            event.setAction_cn("主机关机");
        }
        if(Pattern_Matcher.isMatched(message,"Failed\\s+password\\s+for\\s+invalid\\s+user\\s+(.*?)\\s+from\\s+(\\d+\\.\\d+\\.\\d+\\.\\d+)\\s+port\\s+[0-9]{1,5}\\s+ssh2")){
            event.setAction("ssh failed");
            event.setAction_cn("ssh登录失败");
        }
        if(Pattern_Matcher.isMatched(message,"NetworkManager")){
            event.setAction("NetworkManager");
            event.setAction_cn("网络服务");
        }
        if(Pattern_Matcher.isMatched(message,"usb")){
            event.setAction("usb");
            event.setAction_cn("usb外接");
        }
        if(Pattern_Matcher.isMatched(message,"Accepted password for (.*?) from (\\d+\\.\\d+\\.\\d+\\.\\d+) port [0-9]{1,5} ssh2")){
            event.setAction("sshd");
            event.setAction_cn("通过ssh方式进行操作");
        }
        if(Pattern_Matcher.isMatched(message,"systemd-logind")){
            event.setAction("login");
            event.setAction_cn("用户登录");
        }
        if(Pattern_Matcher.isMatched(message,"su:")){
            event.setAction("su");
            event.setAction_cn("通过su方式登录");
        }
        if(Pattern_Matcher.isMatched(message,"Starting Session")||Pattern_Matcher.isMatched(message.toString(),"session opened")){
            event.setAction("session");
            event.setAction_cn("开启新的会话窗口");
        }
        if(Pattern_Matcher.isMatched(message,"rsyslogd")){
            event.setAction("rsyslogd");
            event.setAction_cn("rsyslog自身日志");
        }
        if(Pattern_Matcher.isMatched(message,"kernel: pci")){
            event.setAction("pci");
            event.setAction_cn("pci日志");
        }
        if(Pattern_Matcher.isMatched(message,"kernel: pci_bus")){
            event.setAction("pci_bus");
            event.setAction_cn("pci_bus日志");
        }
        if(Pattern_Matcher.isMatched(message,"kernel: ACPI")){
            event.setAction("ACPI");
            event.setAction_cn("ACPI日志");
        }
        if(Pattern_Matcher.isMatched(message,"kernel: PM")){
            event.setAction("PM");
            event.setAction_cn("PM日志");
        }
        if(Pattern_Matcher.isMatched(message,"kernel: SRAT")){
            event.setAction("SRAT");
            event.setAction_cn("SRAT日志");
        }
        if(Pattern_Matcher.isMatched(message,"CROND")){
            event.setAction("crond");
            event.setAction_cn("定时任务");
        }
        this.setEvent(event);
        //@timestamp
        this.setTimestamp(jsonObject.get("@timestamp")==null?"":jsonObject.get("@timestamp").getAsString());
        //winlog. process.pid
        Winlog winlog = new Winlog();
        Process process = new Process();
        process.setPid(jsonObject.get("pid")==null?"":jsonObject.get("pid").getAsString());
        winlog.setProcess(process);
        this.setWinlog(winlog);
        //fields 自定义字段，数据在外层进行标记
        Fields fields = new Fields();
        this.setFields(fields);
        return this;
        //return JSON.toJSON(this).toString();
    }
    public String toJson(){
        return JSON.toJSON(this).toString();
    }
}

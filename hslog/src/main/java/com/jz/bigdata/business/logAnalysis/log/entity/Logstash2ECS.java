package com.jz.bigdata.business.logAnalysis.log.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

public class Logstash2ECS {
    private Object priority;
    private Object message;
    private Agent agent;
    private Host host;
    private Event event;
    private Log log;
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

    public String toJson(LogstashSyslog logstashSyslog){
        //log.level
        Log log = new Log();
        log.setLevel(logstashSyslog.getSeverity_label());

        //log.syslog
        Logstash2ECS.Syslog syslog = new Logstash2ECS.Syslog();
        //syslog. severity
        Severity severity = new Severity();
        severity.setCode(logstashSyslog.getSeverity());//syslog. severity. code
        severity.setName(logstashSyslog.getSeverity_label()); //syslog. severity. name
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
        agent.setType(logstashSyslog.getType());
        this.setAgent(agent);
        //host.hostname
        Host host = new Host();
        host.setHostname(logstashSyslog.getLogsource());
        this.setHost(host);
        //event. kind
        Event event = new Event();
        event.setKind(logstashSyslog.getProgram());
        this.setEvent(event);

        //@timestamp
        this.setTimestamp(logstashSyslog.getTimestamp());
        //winlog. process.pid
        Winlog winlog = new Winlog();
        Process process = new Process();
        process.setPid(logstashSyslog.getPid());
        winlog.setProcess(process);
        this.setWinlog(winlog);
        //fields ip equipmentid equipmentname userid deptid
        Fields fields = new Fields();
        fields.setIp(logstashSyslog.getIp());
        fields.setDeptid(logstashSyslog.getDeptid());
        fields.setEquipmentid(logstashSyslog.getEquipmentid());
        fields.setEquipmentname(logstashSyslog.getEquipmentname());
        fields.setUserid(logstashSyslog.getUserid());
        this.setFields(fields);

        return JSON.toJSON(this).toString();
    }
}

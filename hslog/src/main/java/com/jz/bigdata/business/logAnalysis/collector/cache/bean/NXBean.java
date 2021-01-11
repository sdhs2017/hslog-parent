package com.jz.bigdata.business.logAnalysis.collector.cache.bean;

/**
 * @Author: yiyang
 * @Date: 2021/1/8 9:47
 * @Description: 农信记录数据写入bean
 */
public class NXBean {
    private String ip_type_level;//ip + 类型 + 日志级别
    private String ip;
    private String type;//类型
    private String level;//级别
    private int count;
    private String timestamp;//时间戳

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getIp_type_level() {
        return ip_type_level;
    }

    public void setIp_type_level(String ip_type_level) {
        this.ip_type_level = ip_type_level;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

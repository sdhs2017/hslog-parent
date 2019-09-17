package com.hs.elsearch.client.boot;


import org.springframework.stereotype.Component;

/**
 * @program: hslog
 * @description: elasticsearch client config read
 * @author: Savilio
 * @create: 2019-08-12 15:42
 **/

//@Component
//@ConfigurationProperties(prefix = "elasticsearch54")
public class ESConfig {

    private String cluster_name;
    private String ip;
    private int port;

    public String getCluster_name() {
        return cluster_name;
    }

    public void setCluster_name(String cluster_name) {
        this.cluster_name = cluster_name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

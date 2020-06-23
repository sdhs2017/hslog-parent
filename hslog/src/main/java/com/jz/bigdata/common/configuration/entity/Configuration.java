package com.jz.bigdata.common.configuration.entity;

/**
 * 配置项bean
 */
public class Configuration {
    private String configuration_key;//key
    private String configuration_value;//value

    public String getConfiguration_key() {
        return configuration_key;
    }

    public void setConfiguration_key(String configuration_key) {
        this.configuration_key = configuration_key;
    }

    public String getConfiguration_value() {
        return configuration_value;
    }

    public void setConfiguration_value(String configuration_value) {
        this.configuration_value = configuration_value;
    }
}

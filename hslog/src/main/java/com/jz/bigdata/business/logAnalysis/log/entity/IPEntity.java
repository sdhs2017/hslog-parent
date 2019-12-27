package com.jz.bigdata.business.logAnalysis.log.entity;

/**
 * @program: hsgit
 * @description: 通过geoip解析IP地址得到地理位置信息实体类
 * @author: jiyourui
 * @create: 2019-12-27 09:28
 **/
public class IPEntity {

    //国家
    String countryName;
    //国家代码
    String countryCode;

    //省份
    String provinceName;
    String provinceCode;

    //城市名称
    String cityName;

    //邮政编码
    String postalCode;

    //经度
    Double longitude;
    //纬度
    Double latitude;
    // 经纬度，纬度在前，经度在后
    String locations;

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }
}

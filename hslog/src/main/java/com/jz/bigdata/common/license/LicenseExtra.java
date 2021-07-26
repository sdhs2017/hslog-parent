package com.jz.bigdata.common.license;

/**
 * @Author: yiyang
 * @Date: 2021/7/7 10:34
 * @Description: license携带的自定义信息，存放在license的extra属性中，为客户信息，包含客户id、客户名称、产品版本信息
 */
public class LicenseExtra {
    private String custom_id;//客户id
    private String custom_name;//客户名称
    private String product_type;//产品类别

    public String getCustom_id() {
        return custom_id;
    }

    public void setCustom_id(String custom_id) {
        this.custom_id = custom_id;
    }

    public String getCustom_name() {
        return custom_name;
    }

    public void setCustom_name(String custom_name) {
        this.custom_name = custom_name;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }
}

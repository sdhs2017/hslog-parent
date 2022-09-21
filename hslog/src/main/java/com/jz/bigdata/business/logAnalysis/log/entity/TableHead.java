package com.jz.bigdata.business.logAnalysis.log.entity;


/**
 * @Author: yiyang
 * @Date: 2022/3/31 9:49
 * @Description:  前端tale的列的属性对应的bean
 */
public class TableHead{
    private String prop;//列的key
    private String label;//列的显示名称
    private String width;//宽度

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }
}
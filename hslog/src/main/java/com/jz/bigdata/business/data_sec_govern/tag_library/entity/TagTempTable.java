package com.jz.bigdata.business.data_sec_govern.tag_library.entity;

/**
 * @Author: yiyang
 * @Date: 2021/3/29 15:41
 * @Description: mybatis要求select必须要有result map，用于执行存储过程后的结果返回
 */
public class TagTempTable {
    private String ids;//元数据标识id

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}

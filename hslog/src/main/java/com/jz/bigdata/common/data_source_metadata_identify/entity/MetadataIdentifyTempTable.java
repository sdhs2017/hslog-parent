package com.jz.bigdata.common.data_source_metadata_identify.entity;

/**
 * @Author: yiyang
 * @Date: 2021/3/29 15:41
 * @Description: mybatis要求select必须要有result map
 */
public class MetadataIdentifyTempTable {
    private String ids;//元数据标识id

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}

package com.jz.bigdata.common.assetGroup.entity;

public class AssetGroupRelations {
    private String asset_id;//资产表id
    private String asset_type;//资产类型
    private String asset_name;//资产名称
    private String asset_ip;//资产IP
    private String asset_group_id;//资产组ID
    private String asset_group_name;//资产组名称
    private String asset_logType;//资产日志类型

    public String getAsset_logType() {
        return asset_logType;
    }

    public void setAsset_logType(String asset_logType) {
        this.asset_logType = asset_logType;
    }

    public String getAsset_id() {
        return asset_id;
    }

    public void setAsset_id(String asset_id) {
        this.asset_id = asset_id;
    }

    public String getAsset_type() {
        return asset_type;
    }

    public void setAsset_type(String asset_type) {
        this.asset_type = asset_type;
    }

    public String getAsset_name() {
        return asset_name;
    }

    public void setAsset_name(String asset_name) {
        this.asset_name = asset_name;
    }

    public String getAsset_ip() {
        return asset_ip;
    }

    public void setAsset_ip(String asset_ip) {
        this.asset_ip = asset_ip;
    }

    public String getAsset_group_id() {
        return asset_group_id;
    }

    public void setAsset_group_id(String asset_group_id) {
        this.asset_group_id = asset_group_id;
    }

    public String getAsset_group_name() {
        return asset_group_name;
    }

    public void setAsset_group_name(String asset_group_name) {
        this.asset_group_name = asset_group_name;
    }
}

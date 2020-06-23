package com.jz.bigdata.common.asset.cache;

import com.jz.bigdata.common.asset.entity.Asset;
import com.jz.bigdata.common.asset.service.IAssetService;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.equipment.service.IEquipmentService;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

/**
 * 资产相关数据缓存
 * 通过单例实现全局定义
 * 实现资产信息的初始化和定时更新
 *
 * 注：get方法使用synchronized修饰，解决 更新资产以及kafka获取资产列表时的并发问题
 */
public enum AssetCache {
    INSTANCE;
    /**
     * 虚拟资产
     */
    private Map<String, Equipment> equipmentMap;
    private Set<String> ipAddressSet;
    private Map<String, String> equipmentLogLevel;
    /**
     * 逻辑资产
     */
    private Map<String, Asset> assetMap;//key ip  value资产对象
    private Set<String> assetIpAddressSet;



    /**
     * 初始化资产信息
     */
    public void init(IEquipmentService equipmentService,IAssetService assetService){
        //初始化：获取设备列表、map
        this.equipmentMap = equipmentService.selectAllEquipment();
        this.ipAddressSet = equipmentService.selectAllIPAdress();
        this.equipmentLogLevel = equipmentService.selectLog_level();
        //初始化逻辑资产
        this.assetMap = assetService.selectAllAsset();
        this.assetIpAddressSet = assetService.selectAllIPAdress();
    }

    public synchronized Map<String, Equipment> getEquipmentMap() {
        return equipmentMap;
    }

    public synchronized Set<String> getIpAddressSet() {
        return ipAddressSet;
    }

    public synchronized Map<String, String> getEquipmentLogLevel() {
        return equipmentLogLevel;
    }

    public synchronized Map<String, Asset> getAssetMap() {
        return assetMap;
    }

    public synchronized Set<String> getAssetIpAddressSet() {
        return assetIpAddressSet;
    }

}

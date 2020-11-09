package com.jz.bigdata.common.start_execution.cache;

import com.jz.bigdata.common.asset.entity.Asset;
import com.jz.bigdata.common.asset.service.IAssetService;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.equipment.service.IEquipmentService;
import com.jz.bigdata.util.cache.AssetCacheUtil;

import javax.annotation.Resource;
import java.util.*;

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
    private Map<String, Equipment> equipmentMapKeyId;
    /**
     * 逻辑资产
     */
    private Map<String, Asset> assetMap;//key ip  value资产对象
    private Set<String> assetIpAddressSet;



    /**
     * 初始化资产信息
     */
    public void init(IEquipmentService equipmentService,IAssetService assetService){
        //对象初始化
        equipmentMap = new HashMap<>();
        ipAddressSet = new HashSet<>();
        equipmentLogLevel = new HashMap<>();
        equipmentMapKeyId = new HashMap<>();
        //查询所有虚拟资产
        List<Equipment> list = equipmentService.selectAllHostName();
        for (Equipment equipment:list) {
            equipmentMap.put(equipment.getIp() + equipment.getLogType(), equipment);//key ip+日志类型  value 虚拟资产对象
            ipAddressSet.add(equipment.getIp());//ip
            equipmentLogLevel.put(equipment.getId(), equipment.getLog_level());//key UUID value 日志级别
            equipmentMapKeyId.put(equipment.getId(), equipment);//key UUID value 虚拟资产对象
        }
        //初始化：获取设备列表、map
        //this.equipmentMap = equipmentService.selectAllEquipment();
        //this.equipmentMapKeyId = equipmentService.selectAllEquipment_key_id();
        //this.ipAddressSet = equipmentService.selectAllIPAdress();
        //this.equipmentLogLevel = equipmentService.selectLog_level();
        //初始化逻辑资产
        assetMap = new HashMap<>();
        assetIpAddressSet = new HashSet<>();
        List<Asset> assetList = assetService.selectAllHostName();
        for(Asset asset:assetList){
            assetMap.put(asset.getIp() , asset);//key ip地址  value 逻辑资产对象
            assetIpAddressSet.add(asset.getIp());//ip
        }
        //this.assetMap = assetService.selectAllAsset();
        //this.assetIpAddressSet = assetService.selectAllIPAdress();
    }

    public synchronized Map<String, Equipment> getEquipmentMapKeyId() {
        if(equipmentMapKeyId==null){
            new AssetCacheUtil().init();
        }
        return equipmentMapKeyId;
    }


    public synchronized Map<String, Equipment> getEquipmentMap() {
        if(equipmentMap==null){
            new AssetCacheUtil().init();
        }
        return equipmentMap;
    }

    public synchronized Set<String> getIpAddressSet() {
        if(ipAddressSet==null){
            new AssetCacheUtil().init();
        }
        return ipAddressSet;
    }

    public synchronized Map<String, String> getEquipmentLogLevel() {
        if(equipmentLogLevel==null){
            new AssetCacheUtil().init();
        }
        return equipmentLogLevel;
    }

    public synchronized Map<String, Asset> getAssetMap() {
        if(assetMap==null){
            new AssetCacheUtil().init();
        }
        return assetMap;
    }

    public synchronized Set<String> getAssetIpAddressSet() {
        if(assetIpAddressSet==null){
            new AssetCacheUtil().init();
        }
        return assetIpAddressSet;
    }

}

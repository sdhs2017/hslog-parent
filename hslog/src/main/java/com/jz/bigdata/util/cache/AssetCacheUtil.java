package com.jz.bigdata.util.cache;

import com.jz.bigdata.common.start_execution.cache.AssetCache;
import com.jz.bigdata.common.asset.service.IAssetService;
import com.jz.bigdata.common.equipment.service.IEquipmentService;

import javax.annotation.Resource;

/**
 * 资产缓存工具类，提供资产信息的初始化
 * author：yiyang
 * time:2020-10-30
 */
public class AssetCacheUtil {
    @Resource(name = "EquipmentService")
    private IEquipmentService equipmentService;

    @Resource(name = "AssetService")
    private IAssetService assetService;
    /**
     * 初始化方法
     */
    public void init(){
        //调用cache初始化方法
        AssetCache.INSTANCE.init(equipmentService,assetService);
    }
}

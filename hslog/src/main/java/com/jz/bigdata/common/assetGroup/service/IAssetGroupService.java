package com.jz.bigdata.common.assetGroup.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import com.jz.bigdata.common.asset.entity.Asset;
import com.jz.bigdata.common.assetGroup.entity.AssetGroup;

/**
 * @description
 */
public interface IAssetGroupService {
	/**
	 * 添加资产组
	 * @param assetGroup
	 * @param session
	 * @return
	 */
	boolean insert(AssetGroup assetGroup, HttpSession session);

	/**
	 * 删除资产组
	 * @param asset_group_id
	 * @return
	 */
	boolean delete(String asset_group_id);

	/**
	 * 通过id获取资产组信息
	 * @param asset_group_id
	 * @return
	 */
	AssetGroup getAssetGroupInfoById(String asset_group_id);

	/**
	 * 资产组查询
	 * @param assetGroup
	 * @return
	 */
	Map<String, Object> getAssetGroupInfoByCondition(AssetGroup assetGroup);

	/**
	 * 资产组信息更新
	 * @param assetGroup
	 * @param session
	 * @return
	 */
	boolean updateById(AssetGroup assetGroup, HttpSession session);

	/**
	 * 资产列表
	 * @return
	 */
	List<Map<String,String>> getAssetList();

	/**
	 * 资产组列表
	 * @return
	 */
	List<Map<String,String>> getAssetGroupList();

	/**
	 * 通过资产组id获取对应的dashboard信息
	 * @param asset_group_id
	 * @return
	 */
	Map<String,Object> getDashboardInfo (String asset_group_id);
}

package com.jz.bigdata.common.assetGroup.dao;

import java.util.List;
import java.util.Map;

import com.jz.bigdata.common.asset.entity.Asset;
import com.jz.bigdata.common.assetGroup.entity.AssetGroup;
import com.jz.bigdata.common.assetGroup.entity.AssetGroupRelations;
import com.jz.bigdata.common.equipment.entity.Equipment;
import org.apache.ibatis.annotations.Param;


/**
 * @description
 */
public interface IAssetGroupDao {
	/**
	 * 添加资产组
	 * @param assetGroup
	 * @return
	 */
	int insert(AssetGroup assetGroup);

	/**
	 * 添加资产/资产组关系表
	 * @param assetGroupRelations
	 * @return
	 */
	int insertAssetGroupRelations(AssetGroupRelations assetGroupRelations);

	/**
	 * 删除资产组
	 * @param asset_group_id
	 * @return
	 */
	int deleteAssetGroup(@Param("asset_group_id")String asset_group_id);

	/**
	 * 删除资产/资产组关系表
	 * @param asset_group_id
	 * @return
	 */
	int deleteAssetGroupRelations(@Param("asset_group_id")String asset_group_id);

	/**
	 * 通过资产组id获取资产组信息
	 * @param asset_group_id
	 * @return
	 */
	AssetGroup selectAssetGroupInfoById(@Param("asset_group_id")String asset_group_id);

	/**
	 * 通过查询条件获取资产组信息
	 * @param assetGroup
	 * @return
	 */
	List<AssetGroup> selectAssetGroupInfoByCondition(AssetGroup assetGroup);

	/**
	 * 更新资产组信息
	 * @param assetGroup
	 * @return
	 */
	int update(AssetGroup assetGroup);

	/**
	 *
	 * @param assetGroup
	 * @return
	 */
	List<List<Map<String,String>>> getAssetGroupCountByCondition(AssetGroup assetGroup);

	/**
	 * 获取资产组列表
	 * @return
	 */
	List<AssetGroup> getAssetGroupList();

}

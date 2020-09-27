package com.jz.bigdata.common.assetGroup.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.jz.bigdata.business.logAnalysis.ecs.service.IecsService;

import com.jz.bigdata.common.assetGroup.dao.IAssetGroupDao;
import com.jz.bigdata.common.assetGroup.entity.AssetGroup;
import com.jz.bigdata.common.assetGroup.entity.AssetGroupRelations;
import com.jz.bigdata.common.assetGroup.service.IAssetGroupService;
import com.jz.bigdata.common.equipment.dao.IEquipmentDao;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.roleauthority.user.dao.IUserDao;
import com.jz.bigdata.roleauthority.user.entity.User;
import org.springframework.stereotype.Service;

import com.jz.bigdata.business.logAnalysis.log.service.IlogService;
import com.jz.bigdata.common.Constant;

import com.jz.bigdata.util.BASE64Util;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.JavaBeanUtil;
import com.jz.bigdata.util.Uuid;

import net.sf.json.JSONArray;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**

 * @description
 */
@Service(value = "AssetGroupService")
public class AssetGroupServiceImpl implements IAssetGroupService {
	// 设置日期格式
	private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Resource
	private IAssetGroupDao assetGroupDao;

	@Resource
	private IUserDao userDao;

	@Resource
	private IEquipmentDao equipmentDao;

	@Resource(name = "configProperty")
	private ConfigProperty configProperty;


	/**
	 * @param assetGroup
	 * @return 添加数据
	 */
	@Override
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
	public boolean insert(AssetGroup assetGroup, HttpSession session) {
		/*------资产组信息-------*/
		//生成UUID
		assetGroup.setAsset_group_id(Uuid.getUUID());
		//当前用户信息
		assetGroup.setCreate_user_id(session.getAttribute(Constant.SESSION_USERID).toString());
		//插入时间
		assetGroup.setCreate_time(df.format(new Date()));
		//保存
		assetGroupDao.insert(assetGroup);
		/*---------处理资产组对应的资产信息------*/
		//拆分资产ids
		//String[] asset_ids = assetGroup.getAsset_ids().split(",");
		for(String asset_id:assetGroup.getAsset_ids()){
			//根据id获取资产信息，目前匹配的是虚拟资产
			Equipment equipment_param = new Equipment();
			equipment_param.setId(asset_id);
			Equipment equipment_info = equipmentDao.selectOneEquipment(equipment_param);
			if(null!=equipment_info){
				//插入资产与资产组关系表
				AssetGroupRelations assetGroupRelations = new AssetGroupRelations();
				assetGroupRelations.setAsset_id(equipment_info.getId());
				assetGroupRelations.setAsset_ip(equipment_info.getIp());
				assetGroupRelations.setAsset_name(equipment_info.getName());
				assetGroupRelations.setAsset_type(Constant.VIRTUAL_ASSET);
				assetGroupRelations.setAsset_group_id(assetGroup.getAsset_group_id());
				assetGroupRelations.setAsset_group_name(assetGroup.getAsset_group_name());
				assetGroupDao.insertAssetGroupRelations(assetGroupRelations);
			}
		}
		return true;
	}

	@Override
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
	public boolean delete(String asset_group_id) {
		assetGroupDao.deleteAssetGroup(asset_group_id);
		assetGroupDao.deleteAssetGroupRelations(asset_group_id);
		return true;
	}

	@Override
	public AssetGroup getAssetGroupInfoById(String asset_group_id) {
		AssetGroup assetGroup = assetGroupDao.selectAssetGroupInfoById(asset_group_id);
		if(null!=assetGroup){
			//组装资产信息id，前端加载使用
			String[] asset_ids = new String[assetGroup.getAsset_group_relations().size()];
			List<AssetGroupRelations> assetGroupRelations = assetGroup.getAsset_group_relations();
			for(int i=0;i<assetGroup.getAsset_group_relations().size();i++){
				asset_ids[i] = assetGroupRelations.get(i).getAsset_id();
			}
			assetGroup.setAsset_ids(asset_ids);
			return assetGroup;
		}else{
			return new AssetGroup();
		}
	}

	@Override
	public Map<String, Object> getAssetGroupInfoByCondition(AssetGroup assetGroup) {
		List<List<Map<String,String>>> count = assetGroupDao.getAssetGroupCountByCondition(assetGroup);
		List<AssetGroup> result = assetGroupDao.selectAssetGroupInfoByCondition(assetGroup);
		//组装前端需要的数据格式
		Map<String, Object> allMap = new HashMap<>();
		allMap.put("count",count.get(0).get(0).get("count"));
		allMap.put("list",result);
		return allMap;
	}

	@Override
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
	public boolean updateById(AssetGroup assetGroup, HttpSession session) {
		/*------资产组信息-------*/
		//当前用户信息
		assetGroup.setUpdate_user_id(session.getAttribute(Constant.SESSION_USERID).toString());
		//插入时间
		assetGroup.setUpdate_time(df.format(new Date()));
		//保存
		assetGroupDao.update(assetGroup);
		/*---------处理资产组对应的资产信息------*/
		//先删除原有关系表中的信息
		assetGroupDao.deleteAssetGroupRelations(assetGroup.getAsset_group_id());
		//拆分资产ids
		//String[] asset_ids = assetGroup.getAsset_ids().split(",");
		for(String asset_id:assetGroup.getAsset_ids()){
			//根据id获取资产信息，目前匹配的是虚拟资产
			Equipment equipment_param = new Equipment();
			equipment_param.setId(asset_id);
			Equipment equipment_info = equipmentDao.selectOneEquipment(equipment_param);
			if(null!=equipment_info){
				//插入资产与资产组关系表
				AssetGroupRelations assetGroupRelations = new AssetGroupRelations();
				assetGroupRelations.setAsset_id(equipment_info.getId());
				assetGroupRelations.setAsset_ip(equipment_info.getIp());
				assetGroupRelations.setAsset_name(equipment_info.getName());
				assetGroupRelations.setAsset_type(Constant.VIRTUAL_ASSET);
				assetGroupRelations.setAsset_group_id(assetGroup.getAsset_group_id());
				assetGroupRelations.setAsset_group_name(assetGroup.getAsset_group_name());
				assetGroupDao.insertAssetGroupRelations(assetGroupRelations);
			}
		}
		return true;
	}

	@Override
	public List<Map<String, String>> getAssetList() {
		List<Map<String, String>> result = new ArrayList<>();
		//获取所有资产信息
		List<Equipment> equipmentList = equipmentDao.selectAllHostName();
		for(Equipment equipment:equipmentList){
			Map<String,String> map = new HashMap<>();
			map.put(Constant.TRANSFER_KEY,equipment.getId());
			map.put(Constant.TRANSFER_LABEL,equipment.getName());
			result.add(map);
		}
		return result;
	}

	@Override
	public List<Map<String, String>> getAssetGroupList() {
		List<AssetGroup> assetGroups = assetGroupDao.getAssetGroupList();
		List<Map<String, String>> result = new ArrayList<>();
		//下拉框，全部
		Map<String,String> map_all = new HashMap<>();
		map_all.put(Constant.COMBOBOX_VALUE,"");
		map_all.put(Constant.COMBOBOX_LABEL,"全部");
		result.add(map_all);
		//遍历资产组  并重新组装数据
		for(AssetGroup assetGroup:assetGroups){
			Map<String,String> map = new HashMap<>();
			map.put(Constant.COMBOBOX_VALUE,assetGroup.getAsset_group_id());
			map.put(Constant.COMBOBOX_LABEL,assetGroup.getAsset_group_name());
			result.add(map);
		}
		return result;
	}
}

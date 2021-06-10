package com.jz.bigdata.common.equipment.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import com.jz.bigdata.common.equipment.entity.Equipment;

/**
 * @author shichengyu
 * @date 2017年8月16日 下午2:39:17
 * @description
 */
public interface IEquipmentService {

	int insert(Equipment equipment, HttpSession session);
	int upsert(Equipment equipment, HttpSession session,boolean withAssetGroup) throws Exception;

	/**
	 * 批量插入资产
	 * @param equipment
	 * @param session
	 * @return
	 * @throws Exception
	 */
	int insertBatch(Equipment equipment, HttpSession session);
	String insertBatch(List<Equipment> equipmentList, HttpSession session);

	String selectAll(Equipment equipment, HttpSession session);
	
	int updateById(Equipment equipment, HttpSession session);
	
	int delete(String[] ids);

	int deleteWithAssetGroup (String[] ids);
	
	List<Equipment> selectEquipment(Equipment equipment) throws Exception;

	List<Equipment> selectEquipmentByLog(Equipment equipment) throws Exception;
	
	String selectAllByPage(String hostName, String name, String ip, String logType,String type, int pageIndex, int pageSize, HttpSession session,String asset_group_id) throws Exception;
	
	Equipment selectOneEquipment(Equipment equipment);
	
	List<Equipment> selectAllHostName();

	Set<String> selectAllIPAdress();

	Map<String, Equipment> selectAllEquipment();
	Map<String, Equipment> selectAllEquipment_key_id();
	Map<String, String> selectLog_level();
	
	int upRiskById(String id, int high_risk, int moderate_risk, int low_risk);
	
	List<Equipment> selectAllEquipmentByRisk();
	
	int batchUpdate(List<Equipment> list);

	boolean checkNameUnique(Equipment equipment);
	boolean checkIpAndLogTypeUnique(Equipment equipment);
	String selectRisk();

	/**
	 * 根据资产组id获取不属于资产组的资产列表
	 * @param asset_group_ids
	 * @return
	 */
	public List<Map<String,String>> getAssetList4Checkbox(String asset_group_ids);
	/**
	 * dashboard设置资产/资产组后根据资产和资产组参数返回资产信息
	 * @param asset_group_ids
	 * @param asset_ids
	 * @return
	 */
	public List<Equipment> getEquipmentListByDashboardSet(String[] asset_group_ids,String[] asset_ids);

	/**
	 * 根据资产类型获取dashboard的信息
	 * @param logType
	 * @return
	 */
	public List<Map<String,String>> getDashboardsInfo(String logType);
}

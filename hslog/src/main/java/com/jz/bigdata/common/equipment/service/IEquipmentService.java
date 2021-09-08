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

	/**
	 * 获取资产信息，带角色判定（查询操作管理员的资产）
	 * @param hostName 主机名
	 * @param name 资产名称
	 * @param ip 资产ip
	 * @param logType 日志类型
	 * @param type
	 * @param pageIndex 第N页
	 * @param pageSize 每页条数
	 * @param session
	 * @param asset_group_id 资产组id
	 * @return
	 * @throws Exception
	 */
	String selectAllByPageWithRole(String hostName, String name, String ip, String logType,String type, int pageIndex, int pageSize, HttpSession session,String asset_group_id) throws Exception;

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


	/**
	 * 获取所有资产的数据
	 * @param path 文件路径
	 * @return 文件路径
	 */
	public String createEquipmentExportFile(String path);
}

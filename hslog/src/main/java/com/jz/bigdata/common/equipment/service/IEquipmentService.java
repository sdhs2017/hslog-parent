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
	String upsert(Equipment equipment, HttpSession session) throws Exception;
	String selectAll(Equipment equipment, HttpSession session);
	
	int updateById(Equipment equipment, HttpSession session);
	
	int delete(String[] ids);
	
	List<Equipment> selectEquipment(Equipment equipment) throws Exception;
	
	String selectAllByPage(String hostName, String name, String ip, String logType,String type, int pageIndex, int pageSize, HttpSession session) throws Exception;
	
	Equipment selectOneEquipment(Equipment equipment);
	
	List<Equipment> selectAllHostName();

	Set<String> selectAllIPAdress();

	Map<String, Equipment> selectAllEquipment();

	Map<String, String> selectLog_level();
	
	int upRiskById(String id, int high_risk, int moderate_risk, int low_risk);
	
	List<Equipment> selectAllEquipmentByRisk();
	
	int batchUpdate(List<Equipment> list);

	boolean checkNameUnique(Equipment equipment);
	boolean checkIpAndLogTypeUnique(Equipment equipment);
	String selectRisk();
}

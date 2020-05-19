package com.jz.bigdata.common.equipment.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.jz.bigdata.common.equipment.entity.Equipment;

/**
 * @author shichengyu
 * @date 2017年8月16日 下午2:37:33
 * @description
 */
public interface IEquipmentDao {
	
	int insert(Equipment equipment);
	
	List<Equipment> selectAll(Equipment equipment, @Param("role") String role, @Param("userId") String userId);
	
	int updateById(Equipment equipment);
	
	int delete(String[] ids);
	
	List<Equipment> selectEquipment(Equipment equipment);
	
	List<Equipment> selectAllByPage(@Param("hostName") String hostName, @Param("name") String name, @Param("ip") String ip, @Param("logType") String logType, @Param("type") String type, @Param("role") String role, @Param("userId") String userId, @Param("startRecord") int startRecord, @Param("pageSize") int pageSize);

	List<String> count(@Param("hostName") String hostName, @Param("name") String name, @Param("ip") String ip, @Param("logType") String logType, @Param("type") String type, @Param("role") String role, @Param("userId") String userId);
	
	Equipment selectOneEquipment(Equipment equipment);
	
	List<Equipment> selectAllHostName();
	
	int upRiskById(@Param("id") String id, @Param("high_risk") int high_risk, @Param("moderate_risk") int moderate_risk, @Param("low_risk") int low_risk);
	
	List<Equipment> selectAllEquipmentByRisk();
	
	List<Object> count_Number();
	
	List<Equipment> selectEquipmentByEventId(String eventId);
	
	Equipment selectByNameIp(Equipment equipment);
	
	int batchUpdate(List<Equipment> list);
	
	int deleteEvent(String[] ids);

	List<List<Map<String,String>>> checkNameUnique(Equipment equipment);

	List<List<Map<String,String>>> checkIpAndLogTypeUnique(Equipment equipment);

}

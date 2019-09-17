package com.jz.bigdata.common.safeStrategy.service;

import java.util.List;

import com.jz.bigdata.common.safeStrategy.entity.SafeStrategy;

/**
 * @author shichengyu
 * @date 2018年3月31日 上午11:22:14
 * @description
 */
public interface ISafeStrategyService {
	
	int insert(SafeStrategy safeStrategy);
	
	int updateById(SafeStrategy safeStrategy);
	
	List<SafeStrategy> selectByEquipmentId(String equipmentId);
	
	int delete(String[] ids);
	 
	List<SafeStrategy> selectById(String id);
	
	List<SafeStrategy> selectByequipmentIdEventType(SafeStrategy safeStrategy);
	 
	 

}

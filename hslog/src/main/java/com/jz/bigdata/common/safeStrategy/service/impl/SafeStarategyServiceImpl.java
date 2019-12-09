package com.jz.bigdata.common.safeStrategy.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.ConstraintViolationException;

import org.springframework.stereotype.Service;

import com.jz.bigdata.common.safeStrategy.dao.ISafeStrategyDao;
import com.jz.bigdata.common.safeStrategy.entity.SafeStrategy;
import com.jz.bigdata.common.safeStrategy.service.ISafeStrategyService;

/**
 * @author shichengyu
 * @date 2018年3月31日 上午11:22:55
 * @description
 */
@Service(value="SafeStrategyService")
public class SafeStarategyServiceImpl implements ISafeStrategyService {
	
	@Resource
	private ISafeStrategyDao safeStarategy;

	/**
	 * @param safeStrategy
	 * @return
	 * @description
	 * 添加数据
	 */
	@Override
	public int insert(SafeStrategy safeStrategy) {
		List<SafeStrategy> list= safeStarategy.selectByequipmentIdEventType(safeStrategy);
		if(list.size()>0){
			
		}
//		try{ 
//			safeStarategy.insert(safeStrategy);
//		}catch(Exception e) { 
////			e.getMessage();
//			System.err.println(e.getMessage());
//		}
		
		
		return safeStarategy.insert(safeStrategy);
	}

	/**
	 * @param safeStrategy
	 * @return
	 * @description
	 * 修改数据
	 */
	@Override
	public int updateById(SafeStrategy safeStrategy) {
		// TODO Auto-generated method stub
		return safeStarategy.updateById(safeStrategy);
	}

	/**
	 * @param equipmentId
	 * @return
	 * @description
	 * 根据资产id查询数据
	 */
	@Override
	public List<SafeStrategy> selectByEquipmentId(String equipmentId) {
		// TODO Auto-generated method stub
		return safeStarategy.selectByEquipmentId(equipmentId);
	}

	/**
	 * @param ids
	 * @return
	 * @description
	 * 删除数据
	 */
	@Override
	public int delete(String[] ids) {
		// TODO Auto-generated method stub
		return safeStarategy.delete(ids);
	}

	/**
	 * @param id
	 * @return
	 * @description
	 * 获取安却策略通过id
	 */
	@Override
	public List<SafeStrategy> selectById(String id) {
		List<SafeStrategy> list =safeStarategy.selectById(id);
		String[] time = list.get(0).getTime_interval().split("-");
		if(time.length>0){
			list.get(0).setMonth(time[0]);
			list.get(0).setDay(time[1]);
			list.get(0).setHour(time[2]);
			list.get(0).setMinute(time[3]);
		}
		return list;
	}

	/**
	 * @param safeStrategy
	 * @return
	 * @description
	 * 获取信息通过资产id类型
	 */
	@Override
	public List<SafeStrategy> selectByequipmentIdEventType(SafeStrategy safeStrategy) {
		return safeStarategy.selectByequipmentIdEventType(safeStrategy);
	}

}

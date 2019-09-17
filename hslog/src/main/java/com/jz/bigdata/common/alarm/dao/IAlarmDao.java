package com.jz.bigdata.common.alarm.dao;

import java.util.List;

import com.jz.bigdata.common.alarm.entity.Alarm;

/**
 * @author shichengyu
 * @date 2018年2月28日 下午1:09:59
 * @description
 */
public interface IAlarmDao {
	
	List<Alarm> selectAll();
	
	int updateById(Alarm alarm);
	
	List<Alarm> selectByEmailState();
	

}

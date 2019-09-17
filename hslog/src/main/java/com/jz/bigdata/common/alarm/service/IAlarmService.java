package com.jz.bigdata.common.alarm.service;

import java.util.List;
import java.util.Set;

import com.jz.bigdata.common.alarm.entity.Alarm;

/**
 * @author shichengyu
 * @date 2018年2月28日 下午1:53:22
 * @description
 */
public interface IAlarmService {
	
	List<Alarm> selectAll();
	
	String updateById(Alarm alarm);
	
	Set<String> selectByEmailState();

}

package com.jz.bigdata.common.alarm.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.alarm.dao.IAlarmDao;
import com.jz.bigdata.common.alarm.entity.Alarm;
import com.jz.bigdata.common.alarm.service.IAlarmService;

/**
 * @author shichengyu
 * @date 2018年2月28日 下午1:54:09
 * @description
 */
@Service(value = "AlarmService")
public class AlarmServiceImpl implements IAlarmService{
	
	@Resource
	private IAlarmDao alarmtDao;

	@Override
	public List<Alarm> selectAll() {
		
		return alarmtDao.selectAll();
	}

	@Override
	public String updateById(Alarm alarm) {
		int result=alarmtDao.updateById(alarm);
		return result>=1?Constant.successMessage():Constant.failureMessage();
	}

	@Override
	public Set<String> selectByEmailState() {
		Set<String> set=new HashSet<String>();
		List<Alarm> list =alarmtDao.selectByEmailState();
		for(int i=0;i<list.size();i++){
			set.add(list.get(i).getEvent_type());
		}
		return set;

//		return alarmtDao.selectByEmailState();
	}

}

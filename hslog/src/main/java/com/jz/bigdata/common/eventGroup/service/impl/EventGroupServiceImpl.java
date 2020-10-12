package com.jz.bigdata.common.eventGroup.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.jz.bigdata.common.eventGroup.dao.IEventGroupDao;
import com.jz.bigdata.common.eventGroup.entity.Event;
import com.jz.bigdata.common.eventGroup.entity.EventGroup;
import com.jz.bigdata.common.eventGroup.entity.EventGroupRelations;
import com.jz.bigdata.common.eventGroup.service.IEventGroupService;
import com.jz.bigdata.common.equipment.dao.IEquipmentDao;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.eventGroup.service.IEventGroupService;
import com.jz.bigdata.roleauthority.user.dao.IUserDao;
import org.springframework.stereotype.Service;

import com.jz.bigdata.common.Constant;

import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.Uuid;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**

 * @description
 */
@Service(value = "EventGroupService")
public class EventGroupServiceImpl implements IEventGroupService {
	// 设置日期格式
	private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Resource
	private IEventGroupDao eventGroupDao;

	@Resource
	private IUserDao userDao;

	@Resource
	private IEquipmentDao equipmentDao;

	@Resource(name = "configProperty")
	private ConfigProperty configProperty;


	/**
	 * @param eventGroup
	 * @return 添加数据
	 */
	@Override
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
	public boolean insert(EventGroup eventGroup, HttpSession session) {
		/*------事件组信息-------*/
		//生成UUID
		eventGroup.setEvent_group_id(Uuid.getUUID());
		//保存
		eventGroupDao.insert(eventGroup);
		/*---------处理事件组对应的事件信息------*/
		//拆分syslog事件ids
		for(Object event_id:eventGroup.getSyslog_event_ids()){
			EventGroupRelations eventGroupRelations = new EventGroupRelations();
			eventGroupRelations.setEvent_group_id(eventGroup.getEvent_group_id());
			eventGroupRelations.setEvent_id(event_id.toString());
			eventGroupDao.insertEventGroupRelations(eventGroupRelations);
		}
		//拆分winlog事件ids
		for(Object event_id:eventGroup.getWinlog_event_ids()){
			EventGroupRelations eventGroupRelations = new EventGroupRelations();
			eventGroupRelations.setEvent_group_id(eventGroup.getEvent_group_id());
			eventGroupRelations.setEvent_id(event_id.toString());
			eventGroupDao.insertEventGroupRelations(eventGroupRelations);
		}
		return true;
	}

	@Override
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
	public boolean delete(String event_group_id) {
		eventGroupDao.deleteEventGroup(event_group_id);
		eventGroupDao.deleteEventGroupRelations(event_group_id);
		return true;
	}

	@Override
	public EventGroup getEventGroupInfoById(String event_group_id) {
		EventGroup eventGroup = eventGroupDao.selectEventGroupInfoById(event_group_id);
		if(null!=eventGroup){
			//组装事件信息id，前端加载使用
			List<Integer> syslog_event_ids = new ArrayList<>();
			List<Integer> winlog_event_ids = new ArrayList<>();
			List<Event> eventList = eventGroup.getEvent_group_events();
			for(Event event:eventList){
				//事件组包含的事件类型，syslog和winlog分开处理
				if("syslog".equals(event.getEvent_type())){
					syslog_event_ids.add(event.getEvent_id());
				}else if("winlog".equals(event.getEvent_type())){
					winlog_event_ids.add(event.getEvent_id());
				}else{

				}
			}
			eventGroup.setSyslog_event_ids( syslog_event_ids.toArray());
			eventGroup.setWinlog_event_ids( winlog_event_ids.toArray());
			return eventGroup;
		}else{
			return new EventGroup();
		}
	}

	@Override
	public Map<String, Object> getEventGroupInfoByCondition(EventGroup eventGroup) {
		List<List<Map<String,String>>> count = eventGroupDao.getEventGroupCountByCondition(eventGroup);
		List<EventGroup> result = eventGroupDao.selectEventGroupInfoByCondition(eventGroup);
		//组装前端需要的数据格式
		Map<String, Object> allMap = new HashMap<>();
		allMap.put("count",count.get(0).get(0).get("count"));
		allMap.put("list",result);
		return allMap;
	}

	@Override
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
	public boolean updateById(EventGroup eventGroup, HttpSession session) {
		/*------事件组信息-------*/
		//保存
		eventGroupDao.update(eventGroup);
		/*---------处理资产组对应的资产信息------*/
		//先删除原有关系表中的信息
		eventGroupDao.deleteEventGroupRelations(eventGroup.getEvent_group_id());
		//拆分syslog事件ids
		for(Object event_id:eventGroup.getSyslog_event_ids()){
			EventGroupRelations eventGroupRelations = new EventGroupRelations();
			eventGroupRelations.setEvent_group_id(eventGroup.getEvent_group_id());
			eventGroupRelations.setEvent_id(event_id.toString());
			eventGroupDao.insertEventGroupRelations(eventGroupRelations);
		}
		//拆分winlog事件ids
		for(Object event_id:eventGroup.getWinlog_event_ids()){
			EventGroupRelations eventGroupRelations = new EventGroupRelations();
			eventGroupRelations.setEvent_group_id(eventGroup.getEvent_group_id());
			eventGroupRelations.setEvent_id(event_id.toString());
			eventGroupDao.insertEventGroupRelations(eventGroupRelations);
		}
		return true;
	}

	@Override
	public List<Map<String, String>> getEventGroupList() {
		List<EventGroup> eventGroups = eventGroupDao.getEventGroupList();
		List<Map<String, String>> result = new ArrayList<>();
		//下拉框，全部
		Map<String,String> map_all = new HashMap<>();
		map_all.put(Constant.COMBOBOX_VALUE,"");
		map_all.put(Constant.COMBOBOX_LABEL,"全部");
		result.add(map_all);
		//遍历资产组  并重新组装数据
		for(EventGroup eventGroup:eventGroups){
			Map<String,String> map = new HashMap<>();
			map.put(Constant.COMBOBOX_VALUE,eventGroup.getEvent_group_id());
			map.put(Constant.COMBOBOX_LABEL,eventGroup.getEvent_group_name());
			result.add(map);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getEventList(String event_type) {
		List<Map<String, Object>> result = new ArrayList<>();
		List<Event> list = eventGroupDao.getEventList(event_type);
		for(Event event:list){
			Map<String,Object> map = new HashMap<>();
			map.put(Constant.TRANSFER_KEY,event.getEvent_id());
			map.put(Constant.TRANSFER_LABEL,event.getEvent_name_en());
			result.add(map);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getEventList4Combobox(String event_type) {
		List<Map<String, Object>> result = new ArrayList<>();
		List<Event> list = eventGroupDao.getEventList(event_type);
		for(Event event:list){
			Map<String,Object> map = new HashMap<>();
			map.put(Constant.COMBOBOX_VALUE,event.getEvent_id());
			map.put(Constant.COMBOBOX_LABEL,event.getEvent_name_en());
			result.add(map);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getEventList4Combobox_equal(String event_type) {
		List<Map<String, Object>> result = new ArrayList<>();
		List<Event> list = eventGroupDao.getEventList(event_type);
		for(Event event:list){
			Map<String,Object> map = new HashMap<>();
			map.put(Constant.COMBOBOX_VALUE,event.getEvent_name_en());
			map.put(Constant.COMBOBOX_LABEL,event.getEvent_name_en());
			result.add(map);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getEventListByEventGroupId(String event_group_id) {
		return null;
	}
}

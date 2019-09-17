package com.jz.bigdata.common.event.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.action.dao.IActionDao;
import com.jz.bigdata.common.action.entity.Action;
import com.jz.bigdata.common.action_event.dao.IAction_eventDao;
import com.jz.bigdata.common.action_event.entity.Action_event;
import com.jz.bigdata.common.equipment.dao.IEquipmentDao;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.equipment_event.dao.IEquipment_eventDao;
import com.jz.bigdata.common.equipment_event.entity.Equipment_event;
import com.jz.bigdata.common.event.dao.IEventDao;
import com.jz.bigdata.common.event.entity.Event;
import com.jz.bigdata.common.event.service.IEventService;
import com.jz.bigdata.util.Uuid;

/**
 * @ClassName EventServiceImpl
 * @Description
 * @Author shi cheng yu
 * @Date 2018年9月25日 上午10:47:38
 */
@Service(value = "EventService")
public class EventServiceImpl implements IEventService {

	@Resource
	private IEventDao eventDao;
	@Resource
	private IAction_eventDao action_eventDao;
	@Resource
	private IActionDao actionDao;
	@Resource
	private IEquipment_eventDao equipment_eventDao;
	@Resource
	private IEquipmentDao equipmentDao;

	/**
	 * @param event
	 * @return
	 * @description 添加事件
	 */
	@Override
	public int insert(Event event) {
		// 分割actionid
		String[] actionId = event.getActionId().split(",");
		// 生成uuid
		String eventId = Uuid.getUUID();
		List<Action_event> list = new ArrayList<>();
		String[] parm = new String[2];
		// 循环遍历添加list
		for (int i = 0; i < actionId.length; i++) {
			parm = actionId[i].split("-");
			Action_event action_event = new Action_event();
			action_event.setActionId(parm[0]);
			action_event.setOrder(Integer.valueOf(parm[1]));
			action_event.setNumber(Integer.valueOf(parm[2]));
			action_event.setEventId(eventId);
			
			list.add(action_event);
		}
		int time = 0;
		String time_interval = "";
		// 计算时间换算成秒
		time = Integer.valueOf(event.getMonth()) * 30 * 24 * 60 + Integer.valueOf(event.getDay()) * 24 * 60
				+ Integer.valueOf(event.getHour()) * 60 + Integer.valueOf(event.getMinute());
		// 拼接时间
		time_interval = event.getMonth() + "-" + event.getDay() + "-" + event.getHour() + "-" + event.getMinute();
		event.setTime(time + "");
		event.setTime_interval(time_interval);
		event.setDeleteState(1);
		// 判断时间类型是否是资产类型
		if (event.getEvent_classify() == 1) {
			//分割资产id
			String[] equipmentId = event.getEquipmentIds().split(",");
			List<Equipment_event> equipmentList = new ArrayList<>();
			// 循环遍历添加list
			for (int i = 0; i < equipmentId.length; i++) {
				Equipment_event equipment_event = new Equipment_event();
				equipment_event.setEquipmentId(equipmentId[i]);
				equipment_event.setEventId(eventId);
				equipmentList.add(equipment_event);
			}
			equipment_eventDao.insert(equipmentList);
		}

		action_eventDao.insert(list);
		event.setId(eventId);
		return eventDao.insert(event);
	}

	/**
	 * @param event
	 * @return
	 * @description 查询事件
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Event> selectAll(HttpSession session) {
		// 查询数据Action_event
		List<Action_event> actionList = action_eventDao.selectAll();
		// 获取第一层
		List<Action_event> action = (List<Action_event>) actionList.get(0);
		// 查询数据Event
		String userId=session.getAttribute(Constant.SESSION_USERID).toString();
		String role=session.getAttribute(Constant.SESSION_USERROLE).toString();
		List<Event> eventList = eventDao.selectAll(role,userId);
		// 获取第一层
		List<Event> eve = (List<Event>) eventList.get(0);
		Map eventMap = new HashMap<>();
		Map<String, String> actionMap = new HashMap<>();
		StringBuffer sBuffer = new StringBuffer();
		List<Event> list = new ArrayList<>();
		// 循环eventlist
		for (int i = 0; i < eve.size(); i++) {
			// 循环actionlist
			for (int j = 0; j < action.size(); j++) {
				actionMap = (Map<String, String>) action.get(j);
				eventMap = (Map<String, String>) eve.get(i);
				// 判断eventId是否相同
				if (eventMap.get("id").equals(actionMap.get("eventId"))) {
					sBuffer.append(actionMap.get("name") + ",");
				}
			}
			eventMap.put("actionName", sBuffer.toString());
			// map转实体类
			Event eventClass = JSON.parseObject(JSON.toJSONString(eventMap), Event.class);
			list.add(eventClass);
			sBuffer.setLength(0);
		}

		return list;
	}

	/**
	 * @param event
	 * @return
	 * @description 修改事件
	 */
	@Override
	public int updataById(Event event) {
		// 删除关系表中的数据
		int result = action_eventDao.deleteByEventId(event.getId());
		//判断是否是资产事件
		if(event.getEvent_classify()==1) {
			result=equipment_eventDao.deleteByEventId(event.getId());
			//是否删除数据
			if(result>0) {
				//分割资产id
				String[] equipmentId = event.getEquipmentIds().split(",");
				List<Equipment_event> equipmentList = new ArrayList<>();
				// 循环遍历添加list
				for (int i = 0; i < equipmentId.length; i++) {
					Equipment_event equipment_event = new Equipment_event();
					equipment_event.setEquipmentId(equipmentId[i]);
					equipment_event.setEventId(event.getId());
					equipmentList.add(equipment_event);
				}
				equipment_eventDao.insert(equipmentList);
				
			}
		}
		
		// 是否删除成功
		if (result > 0) {
			List<Action_event> list = new ArrayList<>();
			// 分割actionid
			String[] actionId = event.getActionId().split(",");
			String[] parm = new String[2];
			// 循环遍历添加list
			for (int i = 0; i < actionId.length; i++) {
				parm = actionId[i].split("-");
				Action_event action_event = new Action_event();
				action_event.setActionId(parm[0]);
				action_event.setOrder(Integer.valueOf(parm[1]));
				action_event.setNumber(Integer.valueOf(parm[2]));
				action_event.setEventId(event.getId());
				list.add(action_event);
			}
			int time = 0;
			String time_interval = "";
			// 计算时间换算成秒
			time = Integer.valueOf(event.getMonth()) * 30 * 24 * 60 + Integer.valueOf(event.getDay()) * 24 * 60
					+ Integer.valueOf(event.getHour()) * 60 + Integer.valueOf(event.getMinute());
			// 拼接时间
			time_interval = event.getMonth() + "-" + event.getDay() + "-" + event.getHour() + "-" + event.getMinute();
			event.setTime(time + "");
			event.setTime_interval(time_interval);

			action_eventDao.insert(list);
			result = eventDao.updataById(event);
		}

		return result;
	}

	/**
	 * @param id
	 * @return
	 * @description 删除事件
	 */
	@Override
	public int delete(String id) {
		action_eventDao.deleteByEventId(id);
		return eventDao.delete(id);
	}

	/**
	 * @param actionId
	 * @return
	 * @description 根据动作id查询事件
	 */
	@Override
	public List<Event> selectEventByActionId(String actionId) {
		return eventDao.selectEventByActionId(actionId);
	}

	/**
	 * @param id
	 * @return 修改查询相关数据
	 */
	@Override
	public Map<String, Object> selectEventAction(String id) {
		Map<String, Object> map = new HashMap<>();
		// 查询event
		Event event = eventDao.selectEvent(id);
		// 查询action
		List<Action> list = actionDao.selectActionByEventId(id);
		String[] time = event.getTime_interval().split("-");
		if (time.length > 0) {
			event.setMonth(time[0]);
			event.setDay(time[1]);
			event.setHour(time[2]);
			event.setMinute(time[3]);
		}
		if(event.getEvent_classify()==1){
			List<Equipment> equipmentList=equipmentDao.selectEquipmentByEventId(id);
			map.put("equipment", equipmentList);
		}
		map.put("event", event);
		map.put("action", list);
		return map;
	}

}

package com.jz.bigdata.common.eventGroup.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.jz.bigdata.common.eventGroup.entity.EventGroup;

/**
 * @description
 */
public interface IEventGroupService {
	/**
	 * 添加资产组
	 * @param eventGroup
	 * @param session
	 * @return
	 */
	boolean insert(EventGroup eventGroup, HttpSession session);

	/**
	 * 删除资产组
	 * @param event_group_id
	 * @return
	 */
	boolean delete(String event_group_id);

	/**
	 * 通过id获取资产组信息
	 * @param event_group_id
	 * @return
	 */
	EventGroup getEventGroupInfoById(String event_group_id);

	/**
	 * 资产组查询
	 * @param eventGroup
	 * @return
	 */
	Map<String, Object> getEventGroupInfoByCondition(EventGroup eventGroup);

	/**
	 * 资产组信息更新
	 * @param eventGroup
	 * @param session
	 * @return
	 */
	boolean updateById(EventGroup eventGroup, HttpSession session);

	/**
	 * 事件组列表
	 * @return
	 */
	List<Map<String,String>> getEventGroupList();
	/**
	 * 事件列表,通过日志类型
	 * @return
	 */
	List<Map<String,Object>> getEventList(String event_type);
	/**
	 * 事件列表,通过日志类型
	 * @return
	 */
	List<Map<String,Object>> getEventList4Combobox(String event_type);

	/**
	 * 事件列表，通过事件组id
	 * @return
	 */
	List<Map<String,Object>> getEventListByEventGroupId(String event_group_id);
}

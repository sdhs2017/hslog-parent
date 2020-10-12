package com.jz.bigdata.common.eventGroup.dao;

import java.util.List;
import java.util.Map;

import com.jz.bigdata.common.eventGroup.entity.Event;
import com.jz.bigdata.common.eventGroup.entity.EventGroup;
import com.jz.bigdata.common.eventGroup.entity.EventGroupRelations;
import org.apache.ibatis.annotations.Param;


/**
 * @description
 */
public interface IEventGroupDao {
	/**
	 * 添加资产组
	 * @param eventGroup
	 * @return
	 */
	int insert(EventGroup eventGroup);

	/**
	 * 添加资产/资产组关系表
	 * @param eventGroupRelations
	 * @return
	 */
	int insertEventGroupRelations(EventGroupRelations eventGroupRelations);

	/**
	 * 删除资产组
	 * @param event_group_id
	 * @return
	 */
	int deleteEventGroup(@Param("event_group_id") String event_group_id);

	/**
	 * 删除资产/资产组关系表
	 * @param event_group_id
	 * @return
	 */
	int deleteEventGroupRelations(@Param("event_group_id") String event_group_id);

	/**
	 * 通过资产组id获取资产组信息
	 * @param event_group_id
	 * @return
	 */
	EventGroup selectEventGroupInfoById(@Param("event_group_id") String event_group_id);

	/**
	 * 通过查询条件获取资产组信息
	 * @param eventGroup
	 * @return
	 */
	List<EventGroup> selectEventGroupInfoByCondition(EventGroup eventGroup);

	/**
	 * 更新资产组信息
	 * @param eventGroup
	 * @return
	 */
	int update(EventGroup eventGroup);

	/**
	 *
	 * @param eventGroup
	 * @return
	 */
	List<List<Map<String,String>>> getEventGroupCountByCondition(EventGroup eventGroup);

	/**
	 * 获取资产组列表
	 * @return
	 */
	List<EventGroup> getEventGroupList();

	List<Event> getEventList(@Param("event_type")String event_type);
	List<Event> getEventListByEventGroupId(@Param("event_group_id")String event_group_id);
}

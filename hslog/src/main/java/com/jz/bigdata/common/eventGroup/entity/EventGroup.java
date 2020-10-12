package com.jz.bigdata.common.eventGroup.entity;


import java.util.ArrayList;
import java.util.List;

/**
 * @description
 */
public class EventGroup {
	private String event_group_id;//事件组id
	private String event_group_name;//事件组名称
	private String event_group_note;//事件组说明
	private Integer pageIndex;//第N页
	private Integer pageSize;//每页的条数
	private Integer event_id;//事件id ，查询参数
	private Integer startRecord;//根据分页信息计算出的起始条数
	private Object[] syslog_event_ids;//事件组对应的事件id，以逗号隔开
	private Object[] winlog_event_ids;//事件组对应的事件id，以逗号隔开
	private List<Event> event_group_events = new ArrayList<>();//事件组对应的多个事件

	public Integer getEvent_id() {
		return event_id;
	}

	public void setEvent_id(Integer event_id) {
		this.event_id = event_id;
	}

	public List<Event> getEvent_group_events() {
		return event_group_events;
	}

	public void setEvent_group_events(List<Event> event_group_events) {
		this.event_group_events = event_group_events;
	}

	public Object[] getSyslog_event_ids() {
		return syslog_event_ids;
	}

	public void setSyslog_event_ids(Object[] syslog_event_ids) {
		this.syslog_event_ids = syslog_event_ids;
	}

	public Object[] getWinlog_event_ids() {
		return winlog_event_ids;
	}

	public void setWinlog_event_ids(Object[] winlog_event_ids) {
		this.winlog_event_ids = winlog_event_ids;
	}

	public Integer getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getStartRecord() {
		return startRecord;
	}

	public void setStartRecord(Integer startRecord) {
		this.startRecord = startRecord;
	}

	public String getEvent_group_id() {
		return event_group_id;
	}

	public void setEvent_group_id(String event_group_id) {
		this.event_group_id = event_group_id;
	}

	public String getEvent_group_name() {
		return event_group_name;
	}

	public void setEvent_group_name(String event_group_name) {
		this.event_group_name = event_group_name;
	}

	public String getEvent_group_note() {
		return event_group_note;
	}

	public void setEvent_group_note(String event_group_note) {
		this.event_group_note = event_group_note;
	}
}

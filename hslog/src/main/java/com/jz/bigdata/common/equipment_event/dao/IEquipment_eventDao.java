package com.jz.bigdata.common.equipment_event.dao;

import java.util.List;

import com.jz.bigdata.common.equipment_event.entity.Equipment_event;

public interface IEquipment_eventDao {

	int insert(List<Equipment_event> equipment_event);

	int deleteByEventId(String eventId);

}

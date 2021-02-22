package com.jz.bigdata.common.event_alert.dao;


import com.jz.bigdata.common.event_alert.entity.EventAlert;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/2/3 11:11
 * @Description:
 */
public interface IEventAlertDao {
    /**
     * 新增事件告警设置
     * @param eventAlert
     * @return
     */
    public int insert(EventAlert eventAlert);
    /**
     * 更新事件告警设置
     * @param eventAlert
     * @return
     */
    public int update(EventAlert eventAlert);

    /**
     * 根据id获取事件告警信息
     * @param event_alert_id
     * @return
     */
    EventAlert selectEventAlertInfoById(@Param("event_alert_id") String event_alert_id);
    /**
     * 查询事件告警设置列表，不带分页
     * @param eventAlert
     * @return
     */
    public List<EventAlert> getListByCondition(EventAlert eventAlert);
    /**
     * 查询事件告警设置列表，不带分页
     * @param eventAlert
     * @return
     */
    public List<EventAlert> getListByEquipment(@Param("equipment_id")String equipment_id,@Param("equipment_type")String equipment_type);
    /**
     * 查询事件告警设置列表，带分页
     * @param eventAlert
     * @return
     */
    public List<EventAlert> getListByConditionWithPage(EventAlert eventAlert);

    /**
     * 获取列表count数
     * @param eventAlert
     * @return
     */
    public List<List<Map<String,String>>> getCountByCondition(EventAlert eventAlert);

    /**
     * 批量删除
     * @param ids
     * @return
     */
    public int deletes(String[] ids);

}

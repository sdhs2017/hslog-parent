package com.jz.bigdata.common.event_alert.service;


import com.jz.bigdata.common.event_alert.entity.EventAlert;

import java.util.List;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/1/27 10:29
 * @Description:
 */
public interface IEventAlertService {
    /**
     * 保存事件告警设置
     * @param eventAlert
     * @return
     */
    public boolean save(EventAlert eventAlert);

    public boolean delete(String[] ids);

    public boolean update(EventAlert eventAlert);

    public Map<String,Object> getListByCondition(EventAlert eventAlert);

    EventAlert selectEventAlertInfoById(String event_alert_id);

}

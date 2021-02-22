package com.jz.bigdata.common.event_alert.service.impl;

import com.jz.bigdata.common.event_alert.dao.IEventAlertDao;
import com.jz.bigdata.common.event_alert.entity.EventAlert;
import com.jz.bigdata.common.event_alert.service.IEventAlertService;
import com.jz.bigdata.common.siem.entity.Interval;
import com.jz.bigdata.common.siem.service.ISIEMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Author: yiyang
 * @Date: 2021/1/27 10:29
 * @Description:
 */
@Service(value = "EventAlertService")
public class EventAlertServiceImpl implements IEventAlertService {
    @Autowired
    protected IEventAlertDao eventAlertDao;


    @Override
    public boolean save(EventAlert eventAlert) {
        eventAlert.setEvent_alert_id(UUID.randomUUID().toString());
        int result = eventAlertDao.insert(eventAlert);
        if(result>0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean delete(String[] ids) {
        int result = eventAlertDao.deletes(ids);
        if(result>0){
            return true;
        }else{
            return false;
        }

    }

    @Override
    public boolean update(EventAlert eventAlert) {
        int result = eventAlertDao.update(eventAlert);
        if(result>0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Map<String,Object> getListByCondition(EventAlert eventAlert) {
        //获取count
        List<List<Map<String, String>>> count = eventAlertDao.getCountByCondition(eventAlert);
        //分页后的结果列表
        List<EventAlert> result = eventAlertDao.getListByConditionWithPage(eventAlert);

        //文字返回
//        List<EventAlert> newResult = new ArrayList<>();
//        for(EventAlert eventAlert1:result){
//            eventAlert1.setEvent_type(eventAlert1.getEvent_type().equals("0301")?"Winodws":(eventAlert1.getEvent_type().equals("0302")?"Linux":""));
//            newResult.add(eventAlert1);
//        }
        //组装前端需要的数据格式
        Map<String, Object> allMap = new HashMap<>();
        allMap.put("count",count.get(0).get(0).get("count"));
        allMap.put("list",result);
        return allMap;
    }

    @Override
    public EventAlert selectEventAlertInfoById(String event_alert_id) {
        return eventAlertDao.selectEventAlertInfoById(event_alert_id);
    }


}

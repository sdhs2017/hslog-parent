package com.jz.bigdata.business.logAnalysis.ecs.controller;

import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.jz.bigdata.business.logAnalysis.ecs.service.IecsService;
import com.jz.bigdata.business.logAnalysis.log.LogType;
import com.jz.bigdata.common.alarm.service.IAlarmService;
import com.jz.bigdata.common.equipment.service.IEquipmentService;
import com.jz.bigdata.common.safeStrategy.service.ISafeStrategyService;
import com.jz.bigdata.roleauthority.user.service.IUserService;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.DescribeLog;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 符合Elastic Common Schema (ECS)数据格式
 * 且数据的event.type为syslog的查询/统计（logstash输出到kafka的syslog）
 */
@Controller
@RequestMapping("/ecsSyslog")
public class EcsSyslogController {
    @Resource(name="ecsService")
    private IecsService ecsService;

    @Resource(name = "EquipmentService")
    private IEquipmentService equipmentService;

    @Resource(name ="configProperty")
    private ConfigProperty configProperty;

    @Resource(name ="SafeStrategyService")
    private ISafeStrategyService safeStrategyService;

    @Resource(name="AlarmService")
    private IAlarmService alarmService;

    @Resource(name ="UserService")
    private IUserService usersService;

    //@Autowired protected ClientTemplate clientTemplate;
    @Autowired
    protected ILogCrudDao logCrudDao;
    /**
     * 获取事件数据的数量
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getEventsCount",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="获取事件数据的数量")
    public String getEventsCount(HttpServletRequest request) {
        String equipmentid = request.getParameter("equipmentid");
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        try {
            Map<String, String> mappram = new HashMap<>();
            mappram.put("event", "event");
            mappram.put("log.level", "error");
            if (equipmentid!=null&&!equipmentid.equals("")) {
                mappram.put("fields.equipmentid", equipmentid);
            }
            long count = 0;
            count = ecsService.getCount(mappram, null, null, configProperty.getEs_index());
            map.put("eventserror", count);
        } catch (Exception e) {
            map.put("eventserror", "获取异常");
        }

        try {
            long count = 0;
            Map<String, String> mappram = new HashMap<>();
            mappram.put("event", "event");
            if (equipmentid!=null&&!equipmentid.equals("")) {
                mappram.put("fields.equipmentid", equipmentid);
            }
            count = ecsService.getCount(mappram, null, null, configProperty.getEs_index());
            map.put("events", count);
        } catch (Exception e) {
            map.put("events", "获取异常");
        }
        list.add(map);
        String result = JSONArray.fromObject(list).toString();

        return result;
    }
    /**
     * @param request
     * 统计各个事件的数据量
     * @return
     */
    @ResponseBody
    @RequestMapping("/getCountGroupByEventType")
    @DescribeLog(describe="统计各个事件的数据量")
    public List<List<Map<String, Object>>> getCountGroupByEventType(HttpServletRequest request) {
        List<List<Map<String, Object>>> list = new ArrayList<>();
        String equipmentid = request.getParameter("equipmentid");
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        // 0全部，1高危，2中等，3普通
        for(int i=0;i<4;i++) {
            List<Map<String, Object>> list1 = null;
            Map<String,String> map = new HashMap<>();
            map.put("fields.equipmentid",equipmentid);
            map.put("log.syslog.severity.code",i+"");
            try {
                list1 = ecsService.getCountGroupByEventType(starttime,endtime,"@timestamp",map,configProperty.getEs_index());

            } catch (Exception e) {
                e.printStackTrace();
            }
            list.add(list1);
        }

        return list;
    }
    /**
     * @param request
     * 统计各个日志级别的数据量
     * @return
     */
    @ResponseBody
    @RequestMapping("/getCountGroupByParam")
    @DescribeLog(describe="读取日志级别数据量")
    public List<Map<String, Object>> getCountGroupByParam(HttpServletRequest request) {

        String index = configProperty.getEs_index();
        String param = request.getParameter("param");
        String equipmentid = request.getParameter("equipmentid");
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");

        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        if (equipmentid!=null&&!equipmentid.equals("")) {
            map.put("fields.equipmentid", equipmentid);
        }

        // 聚合返回的数据条数，在目前的产品中日志级别总共有8个，设置为10个保证8个正常显示
        int size = 10;
        List<Map<String, Object>> list = null;
        try {
            list = ecsService.groupByThenCount(starttime,endtime,param,size,map,index);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}

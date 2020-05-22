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
import com.jz.bigdata.util.HttpRequestUtil;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name="ecsService")
    private IecsService ecsService;

    @Resource(name = "EquipmentService")
    private IEquipmentService equipmentService;

    @Resource(name ="configProperty")
    private ConfigProperty configProperty;

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
        //hsData参数处理
        Map<String, String> mapParam = HttpRequestUtil.getHsdata(request);
        //返回结果
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        Map<String,Object> resultMap = new HashMap<>();

        /**
         * 日志级别是error的事件数
         */
        try {
            Map<String, String> errorParam = new HashMap<>();
            errorParam.putAll(mapParam);
            errorParam.put("exists", "event.action");
            errorParam.put("log.level", "error");
            long count = 0;
            count = ecsService.getCount(errorParam, null, null, configProperty.getEs_index());
            resultMap.put("eventserror", count);
        } catch (Exception e) {
            logger.error("获取日志级别是error的事件数量：失败！");
            logger.error(e.getMessage());
            resultMap.put("eventserror", "获取异常");
        }

        /**
         * syslog 事件总数
         */
        try {
            long count = 0;
            Map<String, String> allParam = new HashMap<>();
            allParam.putAll(mapParam);
            // 不为空字段设置，key固定，value是不为null的字段event.action，多个字段逗号相隔
            allParam.put("exists", "event.action");
            count = ecsService.getCount(allParam, null, null, configProperty.getEs_index());
            resultMap.put("events", count);
        } catch (Exception e) {
            logger.error("获取事件数量：失败！");
            logger.error(e.getMessage());
            resultMap.put("events", "获取异常");
        }
        resultList.add(resultMap);
        String result = JSONArray.fromObject(resultList).toString();

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
            List<Map<String, Object>> event_list = null;
            Map<String,String> map = new HashMap<>();
            // 业务只查询范式化成功的日志
            //map.put("fields.failure","false");
            // 不为空字段设置，key固定，value是不为null的字段event.action，多个字段逗号相隔
            map.put("exists", "event.action");
            map.put("fields.equipmentid",equipmentid);
            map.put("log.syslog.severity.code",i+"");
            try {
                event_list = ecsService.getCountGroupByEventType(starttime,endtime,"@timestamp",map,configProperty.getEs_index());
            } catch (Exception e) {
                logger.error("统计各个事件的数据量:失败！");
                e.printStackTrace();
            }
            list.add(event_list);
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
        String groupByField = request.getParameter("param");
        String equipmentid = request.getParameter("equipmentid");
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");

        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        if (equipmentid!=null&&!equipmentid.equals("")) {
            map.put("fields.equipmentid", equipmentid);
        }
        // 业务只查询范式化成功的日志
        //map.put("fields.failure","false");
        // 聚合返回的数据条数，在目前的产品中日志级别总共有8个，设置为10个保证8个正常显示
        int size = 10;
        List<Map<String, Object>> list = null;
        try {
            list = ecsService.groupByThenCount(starttime,endtime,groupByField,size,map,index);
        } catch (Exception e) {
            logger.error("统计日志级别数据量：失败！");
            e.printStackTrace();
        }

        return list;
    }

}

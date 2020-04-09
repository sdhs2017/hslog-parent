package com.jz.bigdata.business.logAnalysis.ecs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.jz.bigdata.business.logAnalysis.ecs.service.IecsService;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.alarm.service.IAlarmService;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.equipment.service.IEquipmentService;
import com.jz.bigdata.common.safeStrategy.service.ISafeStrategyService;
import com.jz.bigdata.roleauthority.user.service.IUserService;
import com.jz.bigdata.util.*;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: hsgit
 * @description: 符合Elastic Common Schema (ECS)数据格式的查询
 * @author: jiyourui
 * @create: 2020-03-30 09:19
 **/

@Controller
@RequestMapping("/ecsWinlog")
public class EcsWinlogController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

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
     * 获取索引数据的数量
     * 用于首页日志总数展示、首页error日志数展示、单个资产报表中的日志总数、error数展示
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getIndicesCount",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="获取索引数据的数量")
    public String getIndicesCount(HttpServletRequest request) {


        String equipmentid = request.getParameter("equipmentid");
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        // error日志条数统计
        try {
            long count = 0;
            Map<String, String> mappram = new HashMap<>();
            mappram.put("log.level", "error");
            if (equipmentid!=null&&!equipmentid.equals("")) {
                mappram.put("fields.equipmentid", equipmentid);
            }

            count = ecsService.getCount(mappram, null, null, configProperty.getEs_index());
            map.put("indiceserror", count);
        } catch (Exception e) {
            map.put("indiceserror", "获取异常");
        }

        // 正常总数据统计
        try {
            long count = 0;
            Map<String, String> mappram = new HashMap<>();
            if (equipmentid!=null&&!equipmentid.equals("")) {
                mappram.put("fields.equipmentid", equipmentid);
            }
            count = ecsService.getCount(mappram, null, null, configProperty.getEs_index());
            map.put("indices", count);
        } catch (Exception e) {
            map.put("indices", "获取异常");
        }
        list.add(map);
        String result = JSONArray.fromObject(list).toString();

        return result;
    }

    /**
     * @param request
     * @return 删除结果String（DELETED、NOT_FOUND、NOOP）
     * 删除成功
     * 未找到
     * 删除失败
     */
    @ResponseBody
    @RequestMapping(value = "/deleteById",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="通过日志ID删除日志信息")
    public String deleteById(HttpServletRequest request) {
        String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);
        List<Map<String, Object>> list = MapUtil.json2ListMap(hsData);
        String result ="false";
        for (Map<String, Object> map : list) {
            try {
                result = ecsService.deleteById(map.get("index").toString(), map.get("id").toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 通过关键字获取日志信息
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getLogListByContent",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="全文检索")
    public String getLogListByContent(HttpServletRequest request, HttpSession session) {

        String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);
        Object userrole = session.getAttribute(Constant.SESSION_USERROLE);

        /**
         * 将String类型的json数据转为map
         */
        Map<String, String> mapper = MapUtil.json2map(hsData);
        Object wordso = mapper.get("words");
        Object pageo = mapper.get("page");
        Object sizeo = mapper.get("size");

        // 全文检索条件：关键字
        String keyWords = wordso!=null?wordso.toString():null;

        // 页码
        String page = pageo.toString();
        // 每页展示数据条数
        String size = sizeo.toString();

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, String> querymap = new HashMap<>();

        // 管理员角色为1，判断是否是管理员角色，如果是不需要补充条件，如果不是添加用户id条件，获取该用户权限下的数据
        if (!userrole.equals(ContextRoles.MANAGEMENT)) {
            querymap.put(ContextRoles.ECS_USERID,session.getAttribute(Constant.SESSION_USERID).toString());
        }
        // winlog可能涉及全文检索的字段
        String [] mutlifields = {"agent.type","winlog.keywords","log.level","message","fields.ip.raw","winlog.channel"};
        Map<String, Object> map = new HashMap<>();
        try {
            list = ecsService.getListByContent(keyWords,mutlifields,querymap,page,size,configProperty.getEs_index());
            logger.info("全文检索：查询成功");
            if (list.size()>0){
                map = list.get(0);
                list.remove(0);
                map.put("list", list);
            }
            map.put("state", true);
        } catch (Exception e) {
            logger.error("全文检索：查询失败"+e.getMessage());
            e.printStackTrace();
            map.put("state", false);
        }

        String result = JSONArray.fromObject(map).toString();
        String replace=result.replace("\\\\005", "<br/>");

        return replace;
    }

    /**
     * 精确查询
     * @param request
     * @author jiyourui
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getLogListByBlend",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="精确查询日志数据")
    public String getLogListByBlend(HttpServletRequest request,HttpSession session) {
        // receive parameter
        Object userrole = session.getAttribute(Constant.SESSION_USERROLE);
        String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new ConcurrentHashMap<String, String>();
        try {
            map = MapUtil.removeMapEmptyValue(mapper.readValue(hsData, Map.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Object pageo = map.get("page");
        Object sizeo = map.get("size");
        map.remove("page");
        map.remove("size");

        String page = pageo.toString();
        String size = sizeo.toString();

        // 管理员角色为1，判断是否是管理员角色，如果是不需要补充条件，如果不是添加用户id条件，获取该用户权限下的数据
        if (!userrole.equals(ContextRoles.MANAGEMENT)) {
            map.put(ContextRoles.ECS_USERID,session.getAttribute(Constant.SESSION_USERID).toString());
        }
        // 从参数中将时间条件提出
        String starttime = "";
        if (map.get("starttime")!=null&&!map.get("starttime").equals("")) {
            starttime = map.get("starttime");
            map.remove("starttime");

        }
        String endtime = "";
        if (map.get("endtime")!=null&&!map.get("endtime").equals("")){
            endtime = map.get("endtime");
            map.remove("endtime");
        }

        List<Map<String, Object>> list =null;

        try {
            list = ecsService.getLogListByBlend(map, starttime, endtime, page, size, configProperty.getEs_index());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> allmap = new HashMap<>();
        allmap = list.get(0);
        list.remove(0);
        allmap.put("list", list);
        String result = JSONArray.fromObject(allmap).toString();
        String replace=result.replace("\\\\005", "<br/>");

        return replace;
    }

    /**
     * 组合查询日志事件
     * @param request
     * @author jiyourui
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getEventListByBlend",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="组合查询日志事件")
    public String getEventListByBlend(HttpServletRequest request,HttpSession session) {
        // receive parameter
        String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);
        Object userrole = session.getAttribute(Constant.SESSION_USERROLE);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new ConcurrentHashMap<>();

        try {
            map = MapUtil.removeMapEmptyValue(mapper.readValue(hsData, Map.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(map);
        Object pageo = map.get("page");
        Object sizeo = map.get("size");

        map.remove("page");
        map.remove("size");

        String page = pageo.toString();
        String size = sizeo.toString();

        // 提出参数的时间查询条件
        String starttime = "";
        String endtime = "";
        if (map.get("starttime")!=null) {
            Object start = map.get("starttime");
            starttime = start.toString();
            map.remove("starttime");
        }
        if (map.get("endtime")!=null) {
            Object end = map.get("endtime");
            endtime = end.toString();
            map.remove("endtime");
        }


        // 判断是否是非管理员角色，是传入参数用户id
        if (!userrole.equals(ContextRoles.MANAGEMENT)){
            map.put(ContextRoles.ECS_USERID,session.getAttribute(Constant.SESSION_USERID).toString());
        }

        List<Map<String, Object>> list = new ArrayList<>();

        try {
            list = ecsService.getLogListByBlend(map,starttime,endtime,page,size,configProperty.getEs_index());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> allmap = new HashMap<>();
        allmap = list.get(0);
        list.remove(0);
        allmap.put("list", list);

        String result = JSONArray.fromObject(allmap).toString();
        String replace=result.replace("\\\\005", "<br/>");

        return replace;
    }

    /**
     * 通过设备id获取该设备日志列表
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getLogListByEquipment.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="条件获取设备日志数据")
    public String getLogListByEquipment(HttpServletRequest request, Equipment equipment) {

        String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new ConcurrentHashMap<>();

        try {
            map = MapUtil.removeMapEmptyValue(mapper.readValue(hsData, Map.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Object pageo = map.get("page");
        Object sizeo = map.get("size");

        map.remove("page");
        map.remove("size");

        String page = pageo.toString();
        String size = sizeo.toString();

        // 提出参数的时间查询条件
        String starttime = "";
        String endtime = "";
        if (map.get("starttime")!=null) {
            Object start = map.get("starttime");
            starttime = start.toString();
            map.remove("starttime");
        }
        if (map.get("endtime")!=null) {
            Object end = map.get("endtime");
            endtime = end.toString();
            map.remove("endtime");
        }

        List<Map<String, Object>> list = null;
        try {
            list = ecsService.getLogListByBlend(map,starttime,endtime,page,size,configProperty.getEs_index());
        } catch (Exception e) {
            logger.error("资产日志：查询失败");
            e.printStackTrace();
        }

        Map<String, Object> allmap = new HashMap<>();
        allmap = list.get(0);
        list.remove(0);
        allmap.put("list", list);

        String result = JSONArray.fromObject(allmap).toString();
        String replace=result.replace("\\\\005", "<br/>");
        logger.info("资产日志：查询成功");
        return replace;



    }

    /**
     * 通过beats发送的日志数据获取资产信息
     * @param request
     * @author jiyourui
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getEquipmentInfo",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="获取资产信息")
    public String getEquipmentInfo(HttpServletRequest request,HttpSession session) {
        // receive parameter
        String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);
        Object userrole = session.getAttribute(Constant.SESSION_USERROLE);

        Gson gson = new Gson();
        Map<String, String> map = gson.fromJson(hsData,Map.class);

        List<Map<String, Object>> list = new ArrayList<>();

        try {
            list = ecsService.getLogListByBlend(map,null,null,"0", "1",configProperty.getEs_index());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> allmap = new HashMap<>();
        /**
         * list的0是存储日志总数的，从1开始才是日志的内容
         */
        if (!list.isEmpty()&&((Long)list.get(0).get("count")!=0)){
            allmap = list.get(1);
        }else {
            // 空数据
            HashMap<String,Object> agent = new HashMap<>();
            agent.put("type","-");
            agent.put("version","-");
            allmap.put("agent",agent);
            HashMap<String,Object> host = new HashMap<>();
            host.put("name","-");
            host.put("hostname","-");
            HashMap<String,Object> os = new HashMap<>();
            os.put("build","-");
            os.put("kernel","-");
            os.put("name","-");
            os.put("version","-");
            os.put("platform","-");
            os.put("family","-");
            host.put("os",os);
            allmap.put("host",host);
        }

        return JSONArray.fromObject(allmap).toString();
    }

    /**
     * @param request
     * 统计各时间段的日志数据量
     * @return
     */
    @ResponseBody
    @RequestMapping("/getLogCountGroupByTime")
    @DescribeLog(describe="统计各时间段的日志数据量")
    public List<Map<String, Object>> getCountGroupByTime(HttpServletRequest request) {
        String index = configProperty.getEs_index();

        String param = request.getParameter("param");
        String equipmentid = request.getParameter("equipmentid");
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");

        String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);

        Gson gson = new Gson();
        Map map = gson.fromJson(hsData,Map.class);

        List<Map<String, Object>> list = new ArrayList<>();
        int size = 10;
        try {
            list = ecsService.getListGroupByTime(starttime, endtime, "@timestamp", size, map, index);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * @param request
     * 统计各个日志级别的数据量
     * @author jiyourui
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getCountGroupByLevel",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="统计各个日志级别的数据量")
    public List<Map<String, Object>> getCountGroupByLevel(HttpServletRequest request) {

        String index = configProperty.getEs_index();

        // TODO 前端字段改制：开始时间、结束时间作为单独字段传递;聚合单独字段；其他条件字段存入json中，方便后台处理

        /*String time = request.getParameter("@timestamp");
        String equipmentid = request.getParameter("equipmentid");*/
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);
        /**
         * 聚合字段--日志级别
         */
        String groupbyfield = "log.level";

        Gson gson = new Gson();
        Map map = Maps.newHashMap();
        map = gson.fromJson(hsData,Map.class);

        // 聚合返回的数据条数，在目前的产品中日志级别总共有8个，设置为10个保证8个正常显示
        int size = 10;
        List<Map<String, Object>> list = null;
        try {
            list = ecsService.groupByThenCount(starttime,endtime,groupbyfield,size,map,index);
            logger.error("统计各个日志级别的数据量 : 成功！");
        } catch (Exception e) {
            logger.error("统计各个日志级别的数据量 : 失败！");
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * @param request
     * 统计ECS事件的数据量
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getCountGroupByEvent",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="统计事件的数据量")
    public List<Map<String, Object>> getCountGroupByEvent(HttpServletRequest request) {
        String index = configProperty.getEs_index();


        String equipmentid = request.getParameter("equipmentid");
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");

        String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);
        /**
         * 聚合字段--事件
         */
        String groupbyfield = "event.action";
        Gson gson = new Gson();
        Map map = Maps.newHashMap();
        map = gson.fromJson(hsData,Map.class);
        int size = 10;

        List<Map<String, Object>> list = null;
        try {
            list = ecsService.groupByThenCount(starttime,endtime,groupbyfield,size,map,index);
            logger.info("统计事件的数据量 ：成功!");
        } catch (Exception e) {
            logger.error("统计事件的数据量 ：失败!");
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * @param request
     * 统计各时间段的各事件数据量
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getCountGroupByTimeAndEvent",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="统计各时间段的各事件数据量")
    public String getCountGroupByTimeAndEvent(HttpServletRequest request) {
        String index = configProperty.getEs_index();

        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");

        String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);

        Gson gson = new Gson();
        Map map = gson.fromJson(hsData,Map.class);

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, List<Map<String, Object>>>  result = new HashMap<>();
        int size = 10;
        Set<String> events = new HashSet<>();
        try {
            list = ecsService.getListGroupByTimeAndEvent(starttime, endtime, "@timestamp","terms", "event.action", size, map, index);
            /**
             * 遍历list获取所有的事件名称存入Set
             */
            for (Map tmpmap : list){
                List<Map<String, Object>> tmplist = (List<Map<String, Object>>) tmpmap.get("value");
                for (Map<String,Object> submap : tmplist){
                    events.addAll(submap.keySet());
                }
            }
            /**
             * 遍历Set事件名称
             */
            for (String event_name : events){
                List<Map<String, Object>> eventlist = new ArrayList<>();
                /**
                 * 遍历service层返回的list，构建返回前端的数据
                 * 一个event的前端结构应该是{"event_name":event_list(该事件在每个小时发生的次数)}
                 * event_list结构，list中是每个小时的map数据：[{hour:0,count:事件发生次数},{hour:1,count:事件发生次数},...,{hour:23,count:事件发生次数}]
                 */
                for (Map tmpmap : list){
                    // event_list结构中的map
                    Map<String, Object> sub = new HashMap<>();
                    // 时间
                    sub.put("hour",tmpmap.get("hour"));
                    // 获取service中封装的value，是一个List<Map<String, Object>>，map的key是event_name,value是event发生的次数
                    List<Map<String, Object>> valuelist = (ArrayList)tmpmap.get("value");
                    /**
                     * 判断valuelist不为null且不为空，原因是某个时间段会出现一个事件都不存在的情况
                     * true的情况进行遍历list
                     * false设置count为0
                     */
                    if (valuelist!=null&&!valuelist.isEmpty()){
                        /**
                         * 遍历valuelist，获取event_name和event发生次数
                         */
                        for (Map<String,Object> valuemap : valuelist){
                            // 如果没有匹配到设置为0，匹配到的情况下以实际数据为准，然后跳出当前循环
                            sub.put("count",valuemap.get(event_name)!=null?valuemap.get(event_name):0);
                            if (valuemap.get(event_name)!=null){
                                break;
                            }
                        }
                    }else {
                        sub.put("count",0);
                    }
                    eventlist.add(sub);
                }
                result.put(event_name,eventlist);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return JSONArray.fromObject(result).toString();
    }

}

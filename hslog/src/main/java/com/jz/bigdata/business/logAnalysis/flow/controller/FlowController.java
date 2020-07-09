package com.jz.bigdata.business.logAnalysis.flow.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hs.elsearch.entity.*;
import com.hs.elsearch.util.ElasticConstant;
import com.jz.bigdata.business.logAnalysis.flow.service.IflowService;
import com.jz.bigdata.business.logAnalysis.log.LogType;
import com.jz.bigdata.business.logAnalysis.log.service.IlogService;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.equipment.dao.IEquipmentDao;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.serviceInfo.dao.IServiceInfoDao;
import com.jz.bigdata.common.serviceInfo.entity.ServiceInfo;
import com.jz.bigdata.common.serviceInfo.service.IServiceInfoService;
import com.jz.bigdata.util.*;
import joptsimple.internal.Strings;
import net.sf.json.JSONArray;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.tools.ant.taskdefs.PathConvert;
import org.elasticsearch.ElasticsearchStatusException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import scala.collection.immutable.Stream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: hsgit
 * @description: 流量数据与前台交互
 * @author: jiyourui
 * @create: 2019-11-01 14:28
 **/
@Controller
@RequestMapping("/flow")
public class FlowController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    //默认查询或聚合结果的条数
    private final int size = 10;
    //数据统计默认时间间隔
    private final int basicTimeInterval=-5;

    @Resource(name="logService")
    private IlogService logService;

    @Resource(name="flowService")
    private IflowService flowService;

    @Resource(name ="configProperty")
    private ConfigProperty configProperty;
    @Resource(name="ServiceInfoService")
    private IServiceInfoService serviceInfoService;
    @Resource
    private IEquipmentDao equipmentDao;

    @Resource
    private IServiceInfoDao serviceInfoDao;

    /**
     *获取当前时间以及其减去间隔时间的时间数组
     * 数组第一个为开始，第二个为截止
     * @param timeInterval 时间间隔
     * @return
     */
    private Map<String,String> getStartEndTime(String timeInterval){
        Map<String,String> map = new HashMap<>();
        String endtime = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
        try {
            int t = Integer.parseInt(timeInterval);
            //数据时间间隔，以秒为单位
            String starttime = DateTime.now().plusSeconds(-t).toString("yyyy-MM-dd HH:mm:ss");
            map.put("starttime",starttime);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            //系统异常，设定一个默认时间间隔
            map.put("starttime",DateTime.now().plusSeconds(basicTimeInterval).toString("yyyy-MM-dd HH:mm:ss"));
        }
        map.put("endtime",endtime);
        return map;
    }

    /**
     * 根据前端传递的参数返回起始和截至时间
     * 数组第一个为开始，第二个为截止
     * @param request 请求对象
     * @return
     */
    private Map<String,String> getStartEndTime(HttpServletRequest request){

        String timeInterval = request.getParameter("timeInterval");
        //时间间隔为空，判定传递的是否是起始和截至时间
        if(timeInterval==null||"".equals(timeInterval)){
            Map<String,String> map = new HashMap<>();
            String starttime = request.getParameter("starttime");
            String endtime = request.getParameter("endtime");
            if(starttime!=null&&endtime!=null){
                map.put("starttime",starttime);
                map.put("endtime",endtime);
            }else{
                //参数异常，map直接返回
                //TODO 返回设置时间间隔
            }
            return map;
        }else{
            return getStartEndTime(timeInterval);
        }


    }
    /**
     * @param request
     * 统计netflow源IP、目的IP、源端口、目的端口的数量
     * @return
     */
    @ResponseBody
    @RequestMapping("/getTopGroupByIPOrPort")
    @DescribeLog(describe="统计netflow源IP、目的IP、源端口、目的端口的数量")
    public String getTopGroupByIPOrPort(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        // 默认需要统计的4个属性，目的ip、源ip、目的端口、源端口
        String [] groupbys = {"ipv4_dst_addr.raw","ipv4_src_addr.raw","l4_dst_port","l4_src_port"};
        String [] types = {"defaultpacket"};
        // 单个group条件
        String groupby = request.getParameter("groupfiled");
        // 应用协议
        String application_layer_protocol = request.getParameter("application_layer_protocol");
        // 时间段
        String starttime = request.getParameter("startTime");
        String endtime = request.getParameter("endTime");

        Map<String, String> searchmap = new HashMap<>();
        if (application_layer_protocol!=null&&!application_layer_protocol.equals("")) {
            searchmap.put("application_layer_protocol", "http");
            searchmap.put("requestorresponse", "request");
        }
        if (starttime!=null&&!starttime.equals("")) {
            starttime = starttime+" 00:00:00";
        }
        if (endtime!=null&&!endtime.equals("")) {
            endtime = endtime+" 23:59:59";
        }

        Map<String, List<Map<String, Object>>> map = new LinkedHashMap<String, List<Map<String, Object>>>();

        if (groupby!=null) {
            List<Map<String, Object>> list = null;
            try {
                list = flowService.groupBy(index, types, groupby+".raw",10, starttime, endtime, searchmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
            if (list.size()>0){
                for(Map.Entry<String, Object> key : list.get(0).entrySet()) {
                    Map<String,Object> tMap = new HashMap<>();
                    tMap.put("IpOrPort", key.getKey());
                    tMap.put("count", key.getValue());
                    tmplist.add(tMap);
                }
            }

            map.put(groupby, tmplist);
        }else {
            for(String param:groupbys) {
                List<Map<String, Object>> list = new ArrayList<>();
                try {
                    list = flowService.groupBy(index, types, param, 10, starttime, endtime, searchmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
                if (list.size()>0){
                    for(Map.Entry<String, Object> key : list.get(0).entrySet()) {
                        Map<String,Object> tMap = new HashMap<>();
                        tMap.put("IpOrPort", key.getKey());
                        tMap.put("count", key.getValue());
                        tmplist.add(tMap);
                    }
                }

                map.put(param.replace(".raw", ""), tmplist);
            }

        }

        return JSONArray.fromObject(map).toString();
    }

    /**
     * @param request
     * 统计应用资产的IP访问次数
     *Group目的地址IP统计数据包的个数，并将IP与资产表（日志系统-资产管理-资产列表equipment）中的IP进行对比，获取资产的名称，反馈到前端
     * @return
     */
    @ResponseBody
    @RequestMapping("/getDstIPCountGroupByHTTPSrcIP")
    @DescribeLog(describe="统计应用资产的IP访问次数")
    public String getDstIPCountGroupByHTTPSrcIP(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String  groupby = "ipv4_src_addr";
        String [] types = {"defaultpacket"};
        // 资产的ip
        String ipv4_dst_addr = request.getParameter("ipv4_dst_addr");

        // 构建参数map
        Map<String, String> map = new HashMap<String, String>();
        map.put("requestorresponse", "request");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if ((ipv4_dst_addr!=null&&!ipv4_dst_addr.equals(""))) {
            map.put("ipv4_dst_addr", ipv4_dst_addr);
        }

        int size = 10;
        //
        //list = logService.groupBy(index, types, groupby, map);
        try {
            list = flowService.groupBy(index, types, groupby, size,null,null,map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long ipv4_dst_addr_count = logService.getCount(index, types, map);

        // 中心圆数据统计
        Map<String,Object> ipv4_dst_addr_Map = new HashMap<>();
        ipv4_dst_addr_Map.put("ipv4_dst_addr", ipv4_dst_addr);
        ipv4_dst_addr_Map.put("count", ipv4_dst_addr_count);

        // IP访问次数统计
        List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
        if (list.size()>0){
            for(Map.Entry<String, Object> key : list.get(0).entrySet()) {
                Map<String,Object> tMap = new HashMap<>();
                tMap.put("source_ip", key.getKey());
                tMap.put("count", key.getValue());
                tmplist.add(tMap);
            }
        }


        Map<String,Object> result = new HashMap<>();
        result.put("ipv4_dst_addr", ipv4_dst_addr_Map);
        result.put("source", tmplist);

        return JSONArray.fromObject(result).toString();
    }

    /**
     * @param request
     * 通过netflow源IP、目的IP、源端口、目的端口的一项作为条件统计其他三项的数量
     * @return
     */
    @ResponseBody
    @RequestMapping("/getIPAndPortTop")
    @DescribeLog(describe="通过netflow源IP、目的IP、源端口、目的端口的一项作为条件统计其他三项的数量")
    public String getIPAndPortTop(HttpServletRequest request) {

        String index = configProperty.getEs_old_index();
        String groupby = request.getParameter("groupfiled");
        String iporport = request.getParameter("iporport");

        String [] groupbys = {"ipv4_dst_addr","ipv4_src_addr","l4_dst_port","l4_src_port"};
        String[] types = {"defaultpacket"};

        Map<String, String> searchmap = new HashMap<>();
        if (groupby!=null&&iporport!=null) {
            searchmap.put(groupby, iporport);
        }

        // top排行榜10
        int size = 10;
        Map<String, List<Map<String, Object>>> map = new LinkedHashMap<String, List<Map<String, Object>>>();
        for(String param:groupbys) {
            if (!param.equals(groupby)) {
                List<Map<String, Object>> list = null;
                try {
                    list = flowService.groupBy(index,types,param,size,null,null,searchmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
                if (list.size()>0){
                    for(Map.Entry<String, Object> key : list.get(0).entrySet()) {
                        Map<String,Object> tMap = new HashMap<>();
                        tMap.put("IpOrPort", key.getKey());
                        tMap.put("count", key.getValue());
                        tmplist.add(tMap);
                    }
                }

                map.put(param, tmplist);
            }
        }

        return JSONArray.fromObject(map).toString();
    }


    /**
     * @param request
     * 通过netflow数据获取网络拓扑数据
     * @return
     */
    @ResponseBody
    @RequestMapping("/getTopologicalData")
    @DescribeLog(describe="通过netflow数据获取网络拓扑数据")
    public String getTopologicalData(HttpServletRequest request) {

        String index = configProperty.getEs_old_index();
        String groupby = request.getParameter("groupfiled");
        String iporport = request.getParameter("iporport");
        String count = request.getParameter("count");

        // 双向划线
        String [] groupbys = {"ipv4_dst_addr","ipv4_src_addr"};
        String[] types = {"defaultpacket"};

        Map<String, String> searchmap = new HashMap<>();
        if (groupby!=null&&iporport!=null) {
            searchmap.put(groupby, iporport);
        }

        Map<String, List<Map<String, Object>>> map = new LinkedHashMap<String, List<Map<String, Object>>>();

        //System.out.println(new Date().getTime());
        long starttime = new Date().getTime();

        for(String param:groupbys) {
            if (!param.equals(groupby)) {
                // 第一层数据结果
                List<Map<String, Object>> list1 = null;
                try {
                    list1 = flowService.groupBy(index,types,param,5,null,null,searchmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                List<Map<String, Object>> datalist = new LinkedList<Map<String, Object>>();
                List<Map<String, Object>> linkslist = new LinkedList<Map<String, Object>>();


                // 组织data中的数据内容中心点
                Map<String,Object> dataMap = new HashMap<>();
                dataMap.put("node", 1);
                dataMap.put("name", iporport);
                dataMap.put("count", count);
                datalist.add(dataMap);
                if (list1.size()>0) {
                    // 遍历第一层数据结果
                    for (Map.Entry<String, Object> key1 : list1.get(0).entrySet()) {
                        // 组织data中的数据内容
                        Map<String, Object> dataMap1 = new HashMap<>();
                        dataMap1.put("node", 2);
                        dataMap1.put("name", "level2\n" + key1.getKey());
                        dataMap1.put("count", key1.getValue());
                        datalist.add(dataMap1);
                        // 组织links中的数据内容
                        Map<String, Object> linksMap1 = new HashMap<>();
                        linksMap1.put("node", 1);
                        if (groupby.equals("ipv4_src_addr")) {
                            linksMap1.put("source", iporport);
                            linksMap1.put("target", "level2\n" + key1.getKey());
                        } else {
                            linksMap1.put("source", "level2\n" + key1.getKey());
                            linksMap1.put("target", iporport);
                        }
                        linksMap1.put("count", key1.getValue());
                        linkslist.add(linksMap1);


                        // 第二层查询条件和数据结果
                        searchmap.put(groupby, key1.getKey());
                        List<Map<String, Object>> list2 = null;
                        try {
                            list2 = flowService.groupBy(index, types, param, 5, null, null, searchmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // 遍历第二层数据结果
                        for (Map.Entry<String, Object> key2 : list2.get(0).entrySet()) {
                            // 组织data中的数据内容
                            Map<String, Object> dataMap2 = new HashMap<>();
                            dataMap2.put("node", 3);
                            dataMap2.put("name", "level3\n" + key2.getKey());
                            dataMap2.put("count", key2.getValue());
                            datalist.add(dataMap2);
                            // 组织links中的数据内容
                            Map<String, Object> linksMap2 = new HashMap<>();
                            linksMap2.put("node", 2);
                            if (groupby.equals("ipv4_src_addr")) {
                                linksMap2.put("source", "level2\n" + key1.getKey());
                                linksMap2.put("target", "level3\n" + key2.getKey());
                            } else {
                                linksMap2.put("source", "level3\n" + key2.getKey());
                                linksMap2.put("target", "level2\n" + key1.getKey());
                            }

                            linksMap2.put("count", key2.getValue());
                            linkslist.add(linksMap2);

                            /*searchmap.put(groupby, key1.getKey());
                            List<Map<String, Object>> itelists = logService.groupBy(index, types, param, searchmap,5);
                            Map<String,Object> itemap = itelists.get(0);*/
                        }


                    }
                }
                map.put("data", datalist);
                map.put("links", linkslist);
            }
        }
        //System.out.println(new Date().getTime());
        long endtime = new Date().getTime();
        long ms = endtime-starttime;
        long time = (endtime-starttime)/1000;
        //System.out.println("----------------------聚合消耗时间："+time+"s ==========="+ms+"ms");

        return JSONArray.fromObject(map).toString();
    }



    /**
     * @param request
     * 通过netflow数据获取网络拓扑图
     * @return
     */
    @ResponseBody
    @RequestMapping("/getNetworkTopological")
    @DescribeLog(describe="通过netflow数据获取网络拓扑数据")
    public String getNetworkTopological(HttpServletRequest request) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        logger.info("进入业务流分析统计   "+format.format(new Date()));

        String index = configProperty.getEs_old_index();

        // 双向划线
        String [] groupbys = {"ipv4_src_addr.raw","ipv4_dst_addr.raw"};
        String[] types = {"defaultpacket"};

        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        Map<String, String> searchmap = new HashMap<>();
        // 设置时间段为一周
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (endtime!=null&&!endtime.equals("")) {
            endtime = endtime+" 23:59:59";
        }else {
            endtime = sdf.format(cal.getTime());
        }
        if (starttime!=null&&!starttime.equals("")) {
            starttime = starttime+" 00:00:00";
        }else {
            cal.add(Calendar.DATE, -7);
            starttime = sdf.format(cal.getTime());
        }

		/*searchmap.put("starttime", starttime);
		searchmap.put("endtime", endtime);*/

        Map<String, List<Map<String, Object>>> map = new LinkedHashMap<String, List<Map<String, Object>>>();

        List<Map<String, Object>> datalist = new LinkedList<Map<String, Object>>();
        List<Map<String, Object>> linkslist = new LinkedList<Map<String, Object>>();
        // 临时map
        Map<String,Object> tMap = new HashMap<>();

        // 聚合源IP和目的IP，处理他们的数据，得到一个以key（IP地址），value（相同IP：源IP访问量和目的IP访问量之和）的map
        for(String param:groupbys) {

            // 聚合源IP和目的IP
            List<Map<String, Object>> list = null;
            try {
                logger.warn("两次聚合查询（源IP，目的IP），聚合查询开始  "+format.format(new Date()));
                list = flowService.groupBy(index,types,param,100,starttime,endtime,searchmap);
                logger.warn("两次聚合查询（源IP，目的IP），聚合查询结束  "+format.format(new Date()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 数据处理，将源IP和目的IP两者之间相同的IP的值相加
            if (tMap.isEmpty()&&list.size()>0) {
                tMap = list.get(0);
            }else {
                if (list.size()>0){
                    for(Map.Entry<String, Object> entrymap : list.get(0).entrySet()) {
                        if (tMap.containsKey(entrymap.getKey())) {
                            int newvalue  = Integer.parseInt(tMap.get(entrymap.getKey()).toString())+Integer.parseInt(entrymap.getValue().toString());
                            tMap.put(entrymap.getKey(), newvalue);
                        }else{
                            tMap.put(entrymap.getKey(), entrymap.getValue());
                        }
                    }
                }

            }
        }

        // 遍历以上聚合数据结果
        for(Map.Entry<String, Object> key : tMap.entrySet()) {
            // 组织data中的数据内容
            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("name", key.getKey());
            dataMap.put("count", key.getValue());
            datalist.add(dataMap);
        }

        // 源IP、目的IP的连线，连线次数
        // linkslist = logService.groupBy(index, types, groupbys, searchmap,1000);
        try {
            logger.warn("源IP与目的IP之间的访问次数聚合，聚合查询开始  "+format.format(new Date()));
            linkslist = flowService.groupBys(index,types,groupbys,1000,starttime,endtime,searchmap);
            logger.warn("源IP与目的IP之间的访问次数聚合，聚合查询结束  "+format.format(new Date()));
        } catch (Exception e) {
            e.printStackTrace();
        }


        //遍历删除,通过遍历连线的list判断source和target两个值是否在tMap的key中，如果不在则删除该连线map
        Iterator<Map<String, Object>> iterator = linkslist.iterator();
        logger.warn("通过遍历聚合得到的访问次数，删除IP不存在连线，遍历开始  "+format.format(new Date()));
        while (iterator.hasNext()) {
            Map<String, Object> linkmap = iterator.next();
            if (!(tMap.containsKey(linkmap.get("source"))&&tMap.containsKey(linkmap.get("target")))) {
                iterator.remove();//使用迭代器的删除方法删除
            }/*else {
				System.out.println("包含："+linkmap);
			}*/
        }
        logger.warn("通过遍历聚合得到的访问次数，删除IP不存在连线，遍历结束  "+format.format(new Date()));
        map.put("data", datalist);
        map.put("links", linkslist);

        logger.info("结束业务流分析统计   "+format.format(new Date()));
        return JSONArray.fromObject(map).toString();
    }

    /**
     * 流量数据查询
     * @param request
     * @author jiyourui
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @ResponseBody
    @RequestMapping(value="/getFlowListByBlend",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="查询流量数据")
    public String getFlowListByBlend(HttpServletRequest request, HttpSession session) {
        // receive parameter
        Object userrole = session.getAttribute(Constant.SESSION_USERROLE);
        String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);
        List<Map<String, Object>> list =null;

        // 参数是否为空
        if (hsData!=null){
            ObjectMapper mapper = new ObjectMapper();
            // 处理map参数
            Map<String, String> map = null;
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

            if (!userrole.equals("1")) {
                map.put("userid",session.getAttribute(Constant.SESSION_USERID).toString());
            }

            ArrayList<String> arrayList = new ArrayList<>();

            // 判断type 是否存在，不存在使用默认，存在使用参数
            if (map.get("type")!=null&&!map.get("type").equals("")) {
                arrayList.add(map.get("type"));
                map.remove("type");
                String [] types = arrayList.toArray(new String[arrayList.size()]);
                try {
                    list = flowService.getFlowListByBlend(map, starttime, endtime, page, size, types, configProperty.getEs_old_index());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                String[] types = {LogType.LOGTYPE_DEFAULTPACKET};
                try {
                    list = flowService.getFlowListByBlend(map, starttime, endtime, page, size, types, configProperty.getEs_old_index());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Map<String, Object> allmap = new HashMap<>();
            if(list.size()>0){
                allmap = list.get(0);
                list.remove(0);
                allmap.put("list", list);
                String result = JSONArray.fromObject(allmap).toString();
                String replace=result.replace("\\\\005", "<br/>");
                return replace;
            }else{
                return Constant.failureMessage();
            }
        }else{
            try {
                list = flowService.getFlowListByBlend(null,  null, null, "1", "12", null, configProperty.getEs_old_index());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Map<String, Object> allmap = new HashMap<>();
            if(list.size()>0){
                allmap = list.get(0);
                list.remove(0);
                allmap.put("list", list);
                String result = JSONArray.fromObject(allmap).toString();
                String replace=result.replace("\\\\005", "<br/>");
                return replace;
            }else{
                return Constant.failureMessage();
            }
        }
    }

    /**
     * http组合查询
     * @param request
     * @author jiyourui
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @ResponseBody
    @RequestMapping(value="/getHttpLogListByBlend",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="HTTP组合查询日志数据")
    public String getHttpLogListByBlend(HttpServletRequest request,HttpSession session) throws JsonParseException, JsonMappingException, IOException {
        // receive parameter
        Object userrole = session.getAttribute(Constant.SESSION_USERROLE);
        String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);

        List<Map<String, Object>> list =null;

        // 判断封装参数的hsdata是否为null，不是解析里面的参数内容
        if (hsData!=null){
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = new HashMap<String, String>();
            // 处理返回的json参数，转为map
            map = MapUtil.removeMapEmptyValue(mapper.readValue(hsData, Map.class));

            Object pageo = map.get("page");
            Object sizeo = map.get("size");

            map.remove("page");
            map.remove("size");

            String page = pageo.toString();
            String size = sizeo.toString();

            String starttime = "";
            String endtime = "";
            // 判断时间范围查询条件是否存在，如果存在提取参数
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

            ArrayList<String> arrayList = new ArrayList<>();

            // 判断type是否存在，不存在使用默认type，存在使用参数
            if (map.get("type")!=null&&!map.get("type").equals("")) {
                arrayList.add(map.get("type"));
                map.remove("type");
                String [] types = arrayList.toArray(new String[arrayList.size()]);
                if (userrole.equals("1")) {
                    //list = logService.getListByMap(configProperty.getEs_old_index(), types, starttime, endtime, map,page,size);
                    try {
                        list = flowService.getFlowListByBlend(map,  starttime, endtime, page, size, types, configProperty.getEs_old_index());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    map.put("userid",session.getAttribute(Constant.SESSION_USERID).toString());
                    try {
                        list = flowService.getFlowListByBlend(map,  starttime, endtime, page, size, types, configProperty.getEs_old_index());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }else {
                String[] types = {LogType.LOGTYPE_DEFAULTPACKET};
                if (userrole.equals("1")) {
                    //list = logService.getListByMap(configProperty.getEs_old_index(), types, starttime, endtime, map,page,size);
                    try {
                        list = flowService.getFlowListByBlend(map,  starttime, endtime, page, size, types, configProperty.getEs_old_index());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    map.put("userid",session.getAttribute(Constant.SESSION_USERID).toString());
                    try {
                        list = flowService.getFlowListByBlend(map,  starttime, endtime, page, size, types, configProperty.getEs_old_index());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            Map<String, Object> allmap = new HashMap<>();
            if(list.size()>0){
                allmap = list.get(0);
                list.remove(0);
                allmap.put("list", list);
                String result = JSONArray.fromObject(allmap).toString();
                String replace=result.replace("\\\\005", "<br/>");
                return replace;
            }
        }else{
            try {
                list = flowService.getFlowListByBlend(null,  null, null, "1", "12", null, configProperty.getEs_old_index());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Map<String, Object> allmap = new HashMap<>();
            if(list.size()>0){
                allmap = list.get(0);
                list.remove(0);
                allmap.put("list", list);
                String result = JSONArray.fromObject(allmap).toString();
                String replace=result.replace("\\\\005", "<br/>");
                return replace;
            }
        }
        return Constant.failureMessage();//如果没有正常返回，则返回失败信息
    }

    /**
     * @param request
     * 统计domain被IP访问的次数
     * @return
     */
    @ResponseBody
    @RequestMapping("/getVisitCountGroupByHttpSourceIP")
    @DescribeLog(describe="统计IP-->domain的访问次数")
    public String getVisitCountGroupByHttpSourceIP(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String  groupby = "ipv4_src_addr";
        String [] types = {"defaultpacket"};
        // 资产的ip和端口
        String domain_url = request.getParameter("domain_url");

        // 构建参数map
        Map<String, String> map = new HashMap<String, String>();
        map.put("requestorresponse", "request");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if ((domain_url!=null&&!domain_url.equals(""))) {
            map.put("domain_url", domain_url);
        }

        int size = 10;
        //list = logService.groupBy(index, types, groupby, map);
        try {
            list = flowService.groupBy(index, types, groupby, size,null,null, map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long domain_url_count = logService.getCount(index, types, map);

        Map<String,Object> domainMap = new HashMap<>();
        domainMap.put("domain_url", domain_url);
        domainMap.put("count", domain_url_count);


        List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
        if (list.size()>0){
            for(Map.Entry<String, Object> key : list.get(0).entrySet()) {
                Map<String,Object> tMap = new HashMap<>();
                tMap.put("source_ip", key.getKey());
                tMap.put("count", key.getValue());
                tmplist.add(tMap);
            }
        }

        Map<String,Object> result = new HashMap<>();
        result.put("domain", domainMap);
        result.put("source", tmplist);

        return JSONArray.fromObject(result).toString();
    }

    /**
     * @param request
     * 统计url,应用画像
     * @return
     */
    @ResponseBody
    @RequestMapping("/getCountGroupByUrl")
    @DescribeLog(describe="统计url的数据量")
    public String getCountGroupByUrl(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String  groupby = "domain_url.raw";
        String [] types = {"defaultpacket"};
        // 资产的ip和端口即目的IP和目的端口
        String des_ip = request.getParameter("des_ip");
        String des_port = request.getParameter("des_port");
        // 源IP和源端口
        String source_ip = request.getParameter("source_ip");
        String source_port = request.getParameter("source_port");
        // 时间段
        Map<String,String> timeMap = getStartEndTime(request);
        //String starttime = request.getParameter("startTime");
        //String endtime = request.getParameter("endTime");

        String ipv4_dst_addr = request.getParameter("ipv4_dst_addr");
        String application_layer_protocol = request.getParameter("application_layer_protocol");

        Map<String, String> map = new HashMap<String, String>();
        map.put("requestorresponse", "request");

        if (des_ip!=null&&!des_ip.equals("")) {
            map.put("des_ip", des_ip);
        }
        if (des_port!=null&&!des_port.equals("")) {
            map.put("des_port", des_port);
        }
        if (source_ip!=null&&!source_ip.equals("")) {
            map.put("source_ip", source_ip);
        }
        if (source_port!=null&&!source_port.equals("")) {
            map.put("source_port", source_port);
        }
        if (ipv4_dst_addr!=null&&!ipv4_dst_addr.equals("")) {
            map.put("ipv4_dst_addr", ipv4_dst_addr);
        }
        if (application_layer_protocol!=null&&!application_layer_protocol.equals("")) {
            map.put("application_layer_protocol", application_layer_protocol);
        }
        /*
        if (starttime!=null&&!starttime.equals("")) {
            starttime = starttime+" 00:00:00";
        }
        if (endtime!=null&&!endtime.equals("")) {
            endtime = endtime+" 23:59:59";
        }*/

        // url 排行榜 TOP10
        int size = 10;
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        //list = logService.groupBy(index, types, groupby, map);
        try {
            list = flowService.groupBy(index,types,groupby,size,timeMap.get("starttime"),timeMap.get("endtime"),map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
        if (list.size()>0){
            for(Map.Entry<String, Object> key : list.get(0).entrySet()) {
                Map<String,Object> tMap = new HashMap<>();
                tMap.put("domain_url", key.getKey());
                tMap.put("count", key.getValue());
                tmplist.add(tMap);
            }
        }

        return JSONArray.fromObject(tmplist).toString();
    }

    /**
     * @param request
     * 统计domain下全url被访问的次数
     * @return
     */
    @ResponseBody
    @RequestMapping("/getCountGroupByHttpComUrl")
    @DescribeLog(describe="统计domain下全url被访问的次数")
    public String getCountGroupByHttpComUrl(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String  groupby = "complete_url.raw";
        String [] types = {"defaultpacket"};
        // 资产的ip和端口
        String domain_url = request.getParameter("domain_url");
        // 时间段
        String starttime = request.getParameter("startTime");
        String endtime = request.getParameter("endTime");
        // 构建参数map
        Map<String, String> map = new HashMap<String, String>();
        map.put("requestorresponse", "request");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if ((domain_url!=null&&!domain_url.equals(""))) {
            map.put("domain_url", domain_url);
        }
        if (starttime!=null&&!starttime.equals("")) {
            starttime = starttime+" 00:00:00";
        }
        if (endtime!=null&&!endtime.equals("")) {
            endtime = endtime+" 23:59:59";
        }

        int size = 10;

        //list = logService.groupBy(index, types, groupby, map);
        try {
            list = flowService.groupBy(index, types, groupby, size,starttime,endtime, map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
        if (list.size()>0){
            for(Map.Entry<String, Object> key : list.get(0).entrySet()) {
                Map<String,Object> tMap = new HashMap<>();
                tMap.put("complete_url", key.getKey());
                tMap.put("count", key.getValue());
                tmplist.add(tMap);
            }
        }

        return JSONArray.fromObject(tmplist).toString();
    }

    /**
     * @param request
     * 统计单个url被IP访问的次数
     * @return
     */
    @ResponseBody
    @RequestMapping("/getVisitCountOfComUrlGroupByHttpSourceIP")
    @DescribeLog(describe="统计单个url被IP访问的次数")
    public String getVisitCountOfComUrlGroupByHttpSourceIP(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String  groupby = "ipv4_src_addr";
        String [] types = {"defaultpacket"};
        // 资产的ip和端口
        String domain_url = request.getParameter("domain_url");
        String complete_url = request.getParameter("complete_url");

        // 构建参数map
        Map<String, String> map = new HashMap<String, String>();
        map.put("requestorresponse", "request");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if ((domain_url!=null&&!domain_url.equals(""))) {
            map.put("domain_url.raw", domain_url);
        }if (complete_url!=null&&!complete_url.equals("")) {
            map.put("complete_url.raw", complete_url);
        }

        int size =10;

        //list = logService.groupBy(index, types, groupby, map);
        try {
            list = flowService.groupBy(index, types, groupby, size,null,null, map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //map.put("domain_url", domain_url);

        long complete_url_count = logService.getCount(index, types, map);

        Map<String,Object> complete_urlMap = new HashMap<>();
        complete_urlMap.put("complete_url", complete_url);
        complete_urlMap.put("count", complete_url_count);


        List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
        if (list.size()>0){
            for(Map.Entry<String, Object> key : list.get(0).entrySet()) {
                Map<String,Object> tMap = new HashMap<>();
                tMap.put("source_ip", key.getKey());
                tMap.put("count", key.getValue());
                tmplist.add(tMap);
            }
        }

        Map<String,Object> result = new HashMap<>();
        result.put("complete_url", complete_urlMap);
        result.put("source", tmplist);

        return JSONArray.fromObject(result).toString();
    }
    /**
     * @param request
     * 流量统计/User-Agent信息/业务访问用户统计-操作系统
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getUserAgentOSGroupByTime_barAndPie", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="通过时间段统计操作系统的种类及数量")
    public String getUserAgentOSGroupByTime_barAndPie(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //查询条件
        Map<String,String> queryParam = new HashMap<>();
        queryParam.put("requestorresponse", "request");
        queryParam.put("application_layer_protocol", "http");
        params.setQueryParam(queryParam);
        //index 和 日期字段初始化
        params.initDateFieldAndIndex(Constant.PACKET_DATE_FIELD,Constant.PACKET_INDEX);
        //X轴，用户系统版本（user_agent_os）
        Bucket bucket = new Bucket("terms","user_agent_os.raw",null,null,10,"desc");
        params.getBucketList().add(bucket);
        //Y轴，数据包个数（count(logdate)）
        Metric metric = new Metric("count",Constant.PACKET_DATE_FIELD,"访问次数");
        params.getMetricList().add(metric);
        try{
            Map<String, Object> result = flowService.getMultiAggregationDataSet(params);
            return Constant.successData(JSONArray.fromObject(result).toString()) ;
        }catch(Exception e){
            logger.error("通过时间段统计操作系统的种类及数量"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * @param request
     * 流量统计/User-Agent信息/业务访问用户统计-操作系统
     * @return
     */
    @ResponseBody
    @RequestMapping("/getUserAgentOSGroupByTime")
    @DescribeLog(describe="通过时间段统计操作系统的种类及数量")
    public String getUserAgentOSGroupByTime(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String  groupby = "user_agent_os.raw";
        String [] types = {"defaultpacket"};

        // 构建参数map
        Map<String, String> map = new HashMap<String, String>();
        map.put("requestorresponse", "request");
        map.put("application_layer_protocol", "http");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String,String> tMap = getStartEndTime(request);
        int size =10;
        try {
            list = flowService.groupBy(index, types, groupby, size,tMap.get("starttime"),tMap.get("endtime"), map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONArray.fromObject(list).toString();
    }
    /**
     * @param request
     * 统计业务系统IP的数据访问包大小
     * @return
     */
    @ResponseBody
    @RequestMapping("/getRequestPacketOfDstIP")
    @DescribeLog(describe="统计业务系统IP的数据访问包大小")
    public String getRequestPacketOfDstIP(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String  groupby = "ipv4_dst_addr.raw";
        String sumfield = "packet_length";
        String [] types = {"defaultpacket"};

        // 构建参数map
        Map<String, String> map = new HashMap<String, String>();
        map.put("requestorresponse", "request");
        map.put("application_layer_protocol", "http");
        int size =10;
        Map<String,String> tMap = getStartEndTime(request);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list = flowService.groupByThenSum(index, types, groupby, sumfield, size,tMap.get("starttime"),tMap.get("endtime"), map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return JSONArray.fromObject(list).toString();
    }

    /**
     * @param request
     * 流量统计/User-Agent信息/业务访问用户统计-浏览器
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getUserAgentBrowserGroupByTime_barAndPie", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="通过时间段统计浏览器的种类及数量")
    public String getUserAgentBrowserGroupByTime_barAndPie(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //查询条件
        Map<String,String> queryParam = new HashMap<>();
        queryParam.put("requestorresponse", "request");
        queryParam.put("application_layer_protocol", "http");
        params.setQueryParam(queryParam);
        //index 和 日期字段初始化
        params.initDateFieldAndIndex(Constant.PACKET_DATE_FIELD,Constant.PACKET_INDEX);
        //X轴，浏览器（user_agent_browser）
        Bucket bucket = new Bucket("terms","user_agent_browser.raw",null,null,10,"desc");
        params.getBucketList().add(bucket);
        //Y轴，数据包个数（count(logdate)）
        Metric metric = new Metric("count",Constant.PACKET_DATE_FIELD,"访问次数");
        params.getMetricList().add(metric);
        try{
            Map<String, Object> result = flowService.getMultiAggregationDataSet(params);
            return Constant.successData(JSONArray.fromObject(result).toString()) ;
        }catch(Exception e){
            logger.error("通过时间段统计浏览器的种类及数量"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }

    /**
     * @param request
     * 流量统计/User-Agent信息/业务访问用户统计-浏览器
     * @return
     */
    @ResponseBody
    @RequestMapping("/getUserAgentBrowserGroupByTime")
    @DescribeLog(describe="通过时间段统计浏览器的种类及数量")
    public String getUserAgentBrowserGroupByTime(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String  groupby = "user_agent_browser.raw";
        String [] types = {"defaultpacket"};

        // 构建参数map
        Map<String, String> map = new HashMap<String, String>();
        map.put("requestorresponse", "request");
        map.put("application_layer_protocol", "http");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        //获取参数
        Map<String,String> tMap = getStartEndTime(request);
        int size =10;//默认top10
        try {
            list = flowService.groupBy(index, types, groupby, size,tMap.get("starttime"),tMap.get("endtime"), map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONArray.fromObject(list).toString();
    }
    /**
     * @param request
     * 嵌套聚合测试
     * @return
     */
    @ResponseBody
    @RequestMapping("/getCount_test")
    @DescribeLog(describe="嵌套聚合测试")
    public String getCount_test(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //index 和 日期字段初始化
        params.initDateFieldAndIndex(Constant.PACKET_DATE_FIELD,Constant.PACKET_INDEX);
        try{
            Map<String, LinkedList<Map<String, Object>>> list = flowService.getListByMultiAggregation(params);
            return JSONArray.fromObject(list).toString();
        }catch(Exception e){
            logger.error("嵌套聚合测试"+e.getMessage());
            e.printStackTrace();
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * @param request
     * 嵌套聚合测试，echart的dataset数据加载模式
     * @return
     */
    @ResponseBody
    @RequestMapping("/getCount_test4dataset")
    @DescribeLog(describe="嵌套聚合测试2")
    public String getCount_test4dataset(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //index 和 日期字段初始化
        params.initDateFieldAndIndex(Constant.PACKET_DATE_FIELD,Constant.PACKET_INDEX);
        try{
            Map<String, Object> list = flowService.getMultiAggregationDataSet(params);
            return JSONArray.fromObject(list).toString();
        }catch(Exception e){
            logger.error("嵌套聚合测试"+e.getMessage());
            e.printStackTrace();
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * @param request
     * 流量统计/全局实时流量/实时统计流量数据访问包大小
     * 计算某个时间段内的数据包大小
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getPacketLengthPerSecond_line",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="实时统计流量数据访问包长度")
    public String getPacketLengthPerSecond_line(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //该折线图默认为动态折线图，需要在用户选择固定时间时，对数据间隔进行计算
        HttpRequestUtil.handleDynamicLineParams(params,configProperty.getEchart_default_points());
        //时间范围参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //index 和 日期字段初始化
        params.initDateFieldAndIndex(Constant.PACKET_DATE_FIELD,Constant.PACKET_INDEX);

        try{
            //X轴，时间，logdate
            Bucket bucket = new Bucket("Date Histogram",Constant.PACKET_DATE_FIELD,params.getIntervalType(),params.getIntervalValue(),null,null);
            params.getBucketList().add(bucket);
            //Y轴，数据包长度（SUM(packet_length)）
            Metric metric = new Metric("sum","packet_length","流量");
            params.getMetricList().add(metric);
            Map<String, Object> result = flowService.getMultiAggregationDataSet(params);
            //轮询折线图
            //将数据进行处理，满足前端显示效果
            LinkedHashMap<String,ArrayList<Map<String,Object>>> newResult = ControllerDataTransUtil.convertToDynamicLineData(result);
            return Constant.successData(JSONArray.fromObject(newResult).toString());
        }catch(Exception e){
            logger.error("实时统计流量数据访问包长度"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * @param request
     * 流量统计/全局实时流量/实时统计流量数据访问包大小
     * 计算某个时间段内的数据包大小
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPacketLengthPerSecond")
    @DescribeLog(describe="实时统计流量数据访问包大小")
    public String getPacketLengthPerSecond(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String sumfield = "packet_length";
        String [] types = {"defaultpacket"};
        //获取参数
        Map<String,String> tMap = getStartEndTime(request);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        int size =10;

        try {
            list = flowService.getSumByMetrics(types,sumfield,size,tMap.get("starttime"),tMap.get("endtime"),null,index);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String,Object> result = new HashMap<>();
        if (list.size()>0){
            result.put("name",tMap.get("endtime"));
            Object [] value = {tMap.get("endtime"), list.get(0).get("agg")};
            result.put("value",value);
        }

        return JSONArray.fromObject(result).toString();
    }
    /**
     * @param request
     * 流量统计/IP主机流量/源ip地址流量
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getSrcIPFlow_barAndPie", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="源ip地址流量")
    public String getSrcIPFlow_barAndPie(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //index 和 日期字段初始化
        params.initDateFieldAndIndex(Constant.PACKET_DATE_FIELD,Constant.PACKET_INDEX);
        //X轴，源IP（ipv4_src_addr）
        Bucket bucket = new Bucket("terms","ipv4_src_addr",null,null,10,"desc");
        params.getBucketList().add(bucket);
        //Y轴，数据包大小（SUM(packet_length)）
        Metric metric = new Metric("sum","packet_length","数据包长度");
        params.getMetricList().add(metric);
        try{
            Map<String, Object> result = flowService.getMultiAggregationDataSet(params);
            //单位换算
            //该业务模块运算的数据为Byte，前端需要的是MB
            Map<String, Object> newResult = ControllerDataTransUtil.calculateMBytes(result);
            return Constant.successData(JSONArray.fromObject(newResult).toString()) ;
        }catch(Exception e){
            logger.error("源ip地址流量"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * @param request
     * 流量统计/IP主机流量/源ip地址流量
     * @return
     */
    @ResponseBody
    @RequestMapping("/getSrcIPFlow")
    @DescribeLog(describe="源ip地址流量")
    public String getSrcIPFlow(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String groupfield = "ipv4_src_addr";
        String sumfield = "packet_length";
        String [] types = {"defaultpacket"};
        //获取参数
        Map<String,String> tMap = getStartEndTime(request);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int size =10;
        try {
            list = flowService.groupByThenSum(index,types,groupfield,sumfield,size,tMap.get("starttime"),tMap.get("endtime"),null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return JSONArray.fromObject(list).toString();
    }
    /**
     * @param request
     * 流量统计/IP主机流量/源ip地址流量
     * @return
     */
    @ResponseBody
    @RequestMapping("/getSrcIPFlow_bar")
    @DescribeLog(describe="源ip地址流量")
    public String getSrcIPFlow_bar(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String groupfield = "ipv4_src_addr";
        String sumfield = "packet_length";
        String [] types = {"defaultpacket"};
        //获取参数
        Map<String,String> tMap = getStartEndTime(request);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int size =10;
        try {
            list = flowService.groupByThenSum(index,types,groupfield,sumfield,size,tMap.get("starttime"),tMap.get("endtime"),null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return JSONArray.fromObject(list).toString();
    }
    /**
     * @param request
     * 流量统计/IP主机流量/目的ip地址流量
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDstIPFlow_barAndPie", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="目的ip地址流量")
    public String getDstIPFlow_barAndPie(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //index 和 日期字段初始化
        params.initDateFieldAndIndex(Constant.PACKET_DATE_FIELD,Constant.PACKET_INDEX);
        //X轴，源IP（ipv4_dst_addr）
        Bucket bucket = new Bucket("terms","ipv4_dst_addr",null,null,10,"desc");
        params.getBucketList().add(bucket);
        //Y轴，数据包大小（SUM(packet_length)）
        Metric metric = new Metric("sum","packet_length","数据包大小");
        params.getMetricList().add(metric);
        try{
            Map<String, Object> result = flowService.getMultiAggregationDataSet(params);
            //单位换算
            //该业务模块运算的数据为Byte，前端需要的是MB
            Map<String, Object> newResult = ControllerDataTransUtil.calculateMBytes(result);
            return Constant.successData(JSONArray.fromObject(newResult).toString()) ;
        }catch(Exception e){
            logger.error("目的ip地址流量"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * @param request
     * 流量统计/IP主机流量/目的ip地址流量
     * @return
     */
    @ResponseBody
    @RequestMapping("/getDstIPFlow")
    @DescribeLog(describe="目的ip地址流量")
    public String getDstIPFlow(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String groupfield = "ipv4_dst_addr";
        String sumfield = "packet_length";
        String [] types = {"defaultpacket"};
        Map<String,String> tMap = getStartEndTime(request);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int size =10;
        try {
            list = flowService.groupByThenSum(index,types,groupfield,sumfield,size,tMap.get("starttime"),tMap.get("endtime"),null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return JSONArray.fromObject(list).toString();
    }
    /**
     * @param request
     * 流量统计/协议流量/传输层协议长度排行
     * Group传输层协议类型计算数据包大小（倒序 MB）
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getTransportLength_barAndPie", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="传输层协议长度排行")
    public String getTransportLength_barAndPie(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //index 和 日期字段初始化
        params.initDateFieldAndIndex(Constant.PACKET_DATE_FIELD,Constant.PACKET_INDEX);
        //X轴，源IP（protocol_name）
        Bucket bucket = new Bucket("terms","protocol_name.raw",null,null,10,"desc");
        params.getBucketList().add(bucket);
        //Y轴，数据包大小（SUM(packet_length)）
        Metric metric = new Metric("sum","packet_length","数据包长度");
        params.getMetricList().add(metric);
        try{
            Map<String, Object> result = flowService.getMultiAggregationDataSet(params);
            //单位换算
            //该业务模块运算的数据为Byte，前端需要的是MB
            Map<String, Object> newResult = ControllerDataTransUtil.calculateMBytes(result);
            return Constant.successData(JSONArray.fromObject(newResult).toString()) ;
        }catch(Exception e){
            logger.error("传输层协议长度排行"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * @param request
     * 流量统计/协议流量/传输层协议长度排行
     * Group传输层协议类型计算数据包大小（倒序）
     * @return
     */
    @ResponseBody
    @RequestMapping("/getTransportLength")
    @DescribeLog(describe="传输层协议长度排行")
    public String getTransportLength(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String groupfield = "protocol_name.raw";//.raw 不分词
        String sumfield = "packet_length";
        String [] types = {"defaultpacket"};
        Map<String,String> tMap = getStartEndTime(request);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int size =10;

        try {
            list = flowService.groupByThenSum(index,types,groupfield,sumfield,size,tMap.get("starttime"),tMap.get("endtime"),null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONArray.fromObject(list).toString();
    }
    /**
     * @param request
     * 流量统计/IP主机流量/源ip地址流量
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getApplicationLength_barAndPie", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="应用层协议长度排行")
    public String getApplicationLength_barAndPie(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //index 和 日期字段初始化
        params.initDateFieldAndIndex(Constant.PACKET_DATE_FIELD,Constant.PACKET_INDEX);
        //X轴，源IP（ipv4_src_addr）
        Bucket bucket = new Bucket("terms","application_layer_protocol.raw",null,null,10,"desc");
        params.getBucketList().add(bucket);
        //Y轴，数据包大小（SUM(packet_length)）
        Metric metric = new Metric("sum","packet_length","数据包长度");
        params.getMetricList().add(metric);
        try{
            Map<String, Object> result = flowService.getMultiAggregationDataSet(params);
            //单位换算
            //该业务模块运算的数据为Byte，前端需要的是MB
            Map<String, Object> newResult = ControllerDataTransUtil.calculateMBytes(result);
            return Constant.successData(JSONArray.fromObject(newResult).toString()) ;
        }catch(Exception e){
            logger.error("应用层协议长度排行"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * @param request
     * 流量统计/协议流量/应用层协议长度排行
     * Group应用层协议类型计算数据包大小（倒序）
     * @return
     */
    @ResponseBody
    @RequestMapping("/getApplicationLength")
    @DescribeLog(describe="应用层协议长度排行")
    public String getApplicationLength(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String groupfield = "application_layer_protocol.raw";
        String sumfield = "packet_length";
        String [] types = {"defaultpacket"};
        Map<String,String> tMap = getStartEndTime(request);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int size =10;

        try {
            list = flowService.groupByThenSum(index,types,groupfield,sumfield,size,tMap.get("starttime"),tMap.get("endtime"),null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONArray.fromObject(list).toString();
    }
    /**
     * @param request
     * 流量统计/协议流量/综合协议长度排行
     * 分别Group传输层和应用层协议类型计算数据包大小，并将两组数据合并排序（倒序）
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getMultipleLength_barAndPie", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="综合协议长度排行")
    public String getMultipleLength_barAndPie(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //index 和 日期字段初始化
        params.initDateFieldAndIndex(Constant.PACKET_DATE_FIELD,Constant.PACKET_INDEX);
        //X轴，应用层协议（application_layer_protocol）
        Bucket bucket = new Bucket("terms","application_layer_protocol.raw",null,null,10,"desc");
        params.getBucketList().add(bucket);
        //Y轴，数据包大小（SUM(packet_length)）
        Metric metric = new Metric("sum","packet_length","数据包长度");
        params.getMetricList().add(metric);
        try{
            Map<String, Object> resultApp = flowService.getMultiAggregationDataSet(params);
            //X轴，应用层协议（protocol_name）
            Bucket bucketProtocol = new Bucket("terms","protocol_name.raw",null,null,10,"desc");
            params.getBucketList().clear();
            params.getBucketList().add(bucketProtocol);
            Map<String, Object> resultProtocol = flowService.getMultiAggregationDataSet(params);
            //合并数据
            Map<String, Object> mergeResult = ControllerDataTransUtil.mergeResultBucket(resultApp,resultProtocol,"数据包长度");
            //换算单位
            Map<String, Object> newResult = ControllerDataTransUtil.calculateMBytes(mergeResult);
            return Constant.successData(JSONArray.fromObject(newResult).toString()) ;
        }catch(Exception e){
            logger.error("源ip地址流量"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * @param request
     * 流量统计/协议流量/综合协议长度排行
     * 分别Group传输层和应用层协议类型计算数据包大小，并将两组数据合并排序（倒序）
     * @return
     */
    @ResponseBody
    @RequestMapping("/getMultipleLength")
    @DescribeLog(describe="综合协议长度排行")
    public String getMultipleLength(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String groupfieldApplication = "application_layer_protocol.raw";
        String groupfieldTransport = "protocol_name.raw";
        String sumfield = "packet_length";
        String [] types = {"defaultpacket"};
        Map<String,String> tMap = getStartEndTime(request);
        List<Map<String, Object>> listApplication = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> listTransport = new ArrayList<Map<String, Object>>();
        int size =10;

        try {
            listApplication = flowService.groupByThenSum(index,types,groupfieldApplication,sumfield,size,tMap.get("starttime"),tMap.get("endtime"),null);
            listTransport = flowService.groupByThenSum(index,types,groupfieldTransport,sumfield,size,tMap.get("starttime"),tMap.get("endtime"),null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //将应用层和传输层协议合并
        Map<String, Object> maps = new HashMap<String, Object>();
        if(listTransport.size()>0){
            maps.putAll(listTransport.get(0));
        }
        if(listApplication.size()>0){
            maps.putAll(listApplication.get(0));
        }
        ArrayList<Map.Entry<String, Object>> arrayList = new ArrayList<Map.Entry<String, Object>>(maps.entrySet());
        //排序
        Collections.sort(arrayList,new Comparator<Map.Entry<String,Object>>() {
            @Override
            public int compare(Map.Entry<String,Object> o1,Map.Entry<String,Object> o2) {
                Double name1 = Double.valueOf(o1.getValue().toString());
                Double name2 = Double.valueOf(o2.getValue().toString());
                return  (int)(name2- name1);
            }
        });
        List<Map<String,Object>> listResult = new LinkedList<Map<String,Object>>();

        for(Map.Entry<String, Object> m:arrayList){
            Map<String,Object> cmap=new ConcurrentHashMap<>();
            cmap.put(m.getKey(),m.getValue());
            listResult.add(cmap);
        }

        return JSONArray.fromObject(listResult).toString();
    }
    /**
     * @param request
     * 流量统计/数据包类型/全局数据包类型及个数
     * 碎片包（[0,64)字节）、正常包（[64-1519]字节）、特大包（(1519, ~)字节）
     * 由于目前数据包最大仅取到1460，因此碎片包为[64,1460)，特大包为[1460,~)
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getPacketTypeCount_line",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="全局数据包类型及个数")
    public String getPacketTypeCount_line(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //该折线图默认为动态折线图，需要在用户选择固定时间时，对数据间隔进行计算
        HttpRequestUtil.handleDynamicLineParams(params,configProperty.getEchart_default_points());
        //时间范围参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        try{
            //index 和 日期字段初始化
            params.initDateFieldAndIndex(Constant.PACKET_DATE_FIELD,Constant.PACKET_INDEX);
            //X轴，时间，logdate
            Bucket bucket = new Bucket("Date Histogram",Constant.PACKET_DATE_FIELD,params.getIntervalType(),params.getIntervalValue(),null,null);
            params.getBucketList().add(bucket);
            ArrayList<Map<String,Object>> ranges = new ArrayList<>();
            //碎片包
            Map<String,Object> small = new HashMap<>();
            small.put("key","碎片包");
            small.put("from",0);
            small.put("to",64);
            //碎片包
            Map<String,Object> normal = new HashMap<>();
            normal.put("key","正常包");
            normal.put("from",64);
            normal.put("to",1460);
            //碎片包
            Map<String,Object> big = new HashMap<>();
            big.put("key","特大包");
            big.put("from",1460);
            big.put("to",null);
            ranges.add(small);
            ranges.add(normal);
            ranges.add(big);
            Bucket packetTypeBucket = new Bucket("Range","packet_length",params.getIntervalType(),params.getIntervalValue(),null,"desc",ranges);
            params.getBucketList().add(packetTypeBucket);
            //Y轴，数据包个数（count(packet_length)）
            Metric metric = new Metric("count",Constant.PACKET_DATE_FIELD,"");
            params.getMetricList().add(metric);
            Map<String, Object> result = flowService.getMultiAggregationDataSet(params);
            //轮询折线图
            //将数据进行处理，满足前端显示效果
            LinkedHashMap<String,ArrayList<Map<String,Object>>> newResult = ControllerDataTransUtil.convertToDynamicLineData(result);
            return Constant.successData(JSONArray.fromObject(newResult).toString());
        }catch(Exception e){
            logger.error("全局数据包类型及个数"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * @param request
     * 流量统计/数据包类型/全局数据包类型及个数
     * 碎片包（[0,64)字节）、正常包（[64-1519]字节）、特大包（(1519, ~)字节）
     * 由于目前数据包最大仅取到1460，因此碎片包为[64,1460)，特大包为[1460,~)
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPacketTypeCount")
    @DescribeLog(describe="全局数据包类型及个数")
    public String getPacketTypeCount(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String groupfield = "application_layer_protocol";
        //String sumfield = "packet_length";
        String [] types = {"defaultpacket"};
        Map<String,String> ttMap = getStartEndTime(request);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int size =10;

        try {
            //定义三种类型的数据包
            Map<String,String> tMap = new ConcurrentHashMap<>();
            tMap.put("small","0,64");
            tMap.put("normal","64,1460");
            tMap.put("big","1460");
            //遍历几种情况，分别统计出结果，并放到map中，
            Map<String, Object> map = new ConcurrentHashMap<>();
            for(Map.Entry<String,String> t:tMap.entrySet()){
                Map<String, String> m = new HashMap<String, String>();
                m.put("packet_length",t.getValue());
                List<Map<String, Object>> tempList = flowService.getCountByMetrics(types,groupfield,size,ttMap.get("starttime"),ttMap.get("endtime"),m);

                Map<String,Object> broadcast = new HashMap<>();
                broadcast.put("name",ttMap.get("endtime"));
                Object count = 0;
                if(tempList.size()>0){
                    count = tempList.get(0).get("agg");
                }
                Object [] value = {ttMap.get("endtime"),count};
                broadcast.put("value",value);
                map.put(t.getKey(),broadcast);
            }
            list.add(map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return JSONArray.fromObject(list).toString();
    }
    /**
     * @param request
     * 流量统计/资产流量/资产（ip） 数据包个数，取目的地址IP进行统计
     * Group目的地址IP统计数据包的个数，并将IP与资产表中的IP进行对比，获取资产的名称，反馈到前端。
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDstIPPacketCount_barAndPie", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="资产（ip） 数据包个数")
    public String getDstIPPacketCount_barAndPie(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //index 和 日期字段初始化
        params.initDateFieldAndIndex(Constant.PACKET_DATE_FIELD,Constant.PACKET_INDEX);
        //X轴，目的IP（ipv4_dst_addr）
        Bucket bucket = new Bucket("terms","ipv4_dst_addr",null,null,10,"desc");
        params.getBucketList().add(bucket);
        //Y轴，数据包大小（count(logdate)）
        Metric metric = new Metric("count",Constant.PACKET_DATE_FIELD,"数据包个数");
        params.getMetricList().add(metric);
        try{
            Map<String, Object> result = flowService.getMultiAggregationDataSet(params);
            //将 IP替换为逻辑资产名称
            Map<String, Object> newResult = ControllerDataTransUtil.transAssetName(result);
            return Constant.successData(JSONArray.fromObject(newResult).toString()) ;
        }catch(Exception e){
            logger.error("资产（ip） 数据包个数"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * @param request
     * 流量统计/资产流量/资产（ip） 数据包个数，取目的地址IP进行统计
     * Group目的地址IP统计数据包的个数，并将IP与资产表中的IP进行对比，获取资产的名称，反馈到前端。
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDstIPPacketCount", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="资产（ip） 数据包个数")
    public String getDstIPPacketCount(HttpServletRequest request,HttpSession session) {
        String index = configProperty.getEs_old_index();
        String groupfield = "ipv4_dst_addr";
        String [] types = {"defaultpacket"};
        Map<String,String> tMap = getStartEndTime(request);

        Map<String, String> map = new HashMap<String, String>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int size =10;

        try {
            list = flowService.groupBy(index,types,groupfield,size,tMap.get("starttime"),tMap.get("endtime"),map);
        } catch (Exception e) {
            e.printStackTrace();
        }


        List<Map<String,Object>> llist = new ArrayList<Map<String,Object>> ();
        if(list.size()>0){
            //遍历统计的数据
            for(Map.Entry<String,Object> tmap : list.get(0).entrySet()){
                Map<String,Object> lmap = new ConcurrentHashMap<>();
                //与获取的ip信息与资产ip进行对应
                List<Equipment> l = equipmentDao.selectAllByPage("", "", tmap.getKey(),"" ,"" ,"" ,"", 0,10);
                if(l.size()>=1){
                    lmap.put(l.get(0).getName(),tmap.getValue());
                }else{
                    lmap.put(tmap.getKey(),tmap.getValue());
                }
                llist.add(lmap);
            }
        }
        return JSONArray.fromObject(llist).toString();
    }
    /**
     * @param request
     * 流量统计/资产流量/资产（服务） 数据包个数
     * Group domain_url统计数据包的个数，并将url与服务表（流量管理-流量管理-服务列表serviceInfo）中的url进行对比，获取服务的名称，反馈到前端。
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDstUrlPacketCount_barAndPie", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="资产（服务）数据包个数")
    public String getDstUrlPacketCount_barAndPie(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //index 和 日期字段初始化
        params.initDateFieldAndIndex(Constant.PACKET_DATE_FIELD,Constant.PACKET_INDEX);
        //X轴，域名（domain_url.raw）
        Bucket bucket = new Bucket("terms","domain_url.raw",null,null,10,"desc");
        params.getBucketList().add(bucket);
        //Y轴，数据包个数（SUM(packet_length)）
        Metric metric = new Metric("count",Constant.PACKET_DATE_FIELD,"数据包个数");
        params.getMetricList().add(metric);
        try{
            Map<String, Object> result = flowService.getMultiAggregationDataSet(params);
            //将域名与服务列表中对应，替换为别名
            Map<String, Object> newResult = ControllerDataTransUtil.transServiceName(result,serviceInfoService.selectAll());
            return Constant.successData(JSONArray.fromObject(newResult).toString()) ;
        }catch(Exception e){
            logger.error("资产（服务） 数据包个数"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * @param request
     * 流量统计/资产流量/资产（服务） 数据包个数
     * Group domain_url统计数据包的个数，并将url与服务表（流量管理-流量管理-服务列表serviceInfo）中的url进行对比，获取服务的名称，反馈到前端。
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDstUrlPacketCount", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="资产（服务） 数据包个数")
    public String getDstUrlPacketCount(HttpServletRequest request,HttpSession session) {
        String index = configProperty.getEs_old_index();
        String groupfield = "domain_url.raw";
        String [] types = {"defaultpacket"};
        Map<String,String> tMap = getStartEndTime(request);

        Map<String, String> map = new HashMap<String, String>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int size =10;

        try {
            list = flowService.groupBy(index,types,groupfield,size,tMap.get("starttime"),tMap.get("endtime"),map);
        } catch (Exception e) {
            e.printStackTrace();
        }


        List<Map<String,Object>> llist = new ArrayList<Map<String,Object>> ();
        if(list.size()>0){
            //遍历统计的数据
            for(Map.Entry<String,Object> tmap : list.get(0).entrySet()){
                Map<String,Object> lmap = new ConcurrentHashMap<>();
                //与获取的ip信息与域名url进行对应
                ServiceInfo sInfo = serviceInfoDao.selectServiceByUrl(tmap.getKey());
                if(sInfo!=null){
                    lmap.put((sInfo.getName()==null||sInfo.getName()=="")?tmap.getKey():sInfo.getName(),tmap.getValue());
                }else{
                    lmap.put(tmap.getKey(),tmap.getValue());
                }
                llist.add(lmap);
            }
        }
        return JSONArray.fromObject(llist).toString();
    }
    /**
     * @param request
     * 流量统计/端口流量/目的端口总流量
     * Group 目的端口，计算数据包个数（倒序）
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDstPortCount_barAndPie", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="目的端口总流量")
    public String getDstPortCount_barAndPie(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //index 和 日期字段初始化
        params.initDateFieldAndIndex(Constant.PACKET_DATE_FIELD,Constant.PACKET_INDEX);
        //X轴，目的端口（l4_dst_port）
        Bucket bucket = new Bucket("terms","l4_dst_port",null,null,10,"desc");
        params.getBucketList().add(bucket);
        //Y轴，数据包个数（count(logdate)）
        Metric metric = new Metric("count",Constant.PACKET_DATE_FIELD,"数据包个数");
        params.getMetricList().add(metric);
        try{
            Map<String, Object> result = flowService.getMultiAggregationDataSet(params);
            return Constant.successData(JSONArray.fromObject(result).toString()) ;
        }catch(Exception e){
            logger.error("目的端口总流量"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * @param request
     * 流量统计/端口流量/目的端口总流量
     * Group 目的端口，计算数据包个数（倒序）
     * @return
     */
    @ResponseBody
    @RequestMapping("/getDstPortCount")
    @DescribeLog(describe="目的端口总流量")
    public String getDstPortCount(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String groupfield = "l4_dst_port";
        String [] types = {"defaultpacket"};
        Map<String,String> tMap = getStartEndTime(request);
        Map<String, String> map = new HashMap<String, String>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int size =10;

        try {
            list = flowService.groupBy(index,types,groupfield,size,tMap.get("starttime"),tMap.get("endtime"),map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        if(list.size()>0){
            for(Map.Entry<String,Object> m:list.get(0).entrySet()){
                Map<String,Object> temp = new HashMap<>();
                temp.put(m.getKey(),m.getValue());
                resultList.add(temp);
            }
        }
        return JSONArray.fromObject(resultList).toString();
    }
    /**
     * @param request
     * 流量统计/广播包/组播报/组播包数据+广播包数据个数
     * 组播包—根据目的IP模糊查询
     * 224.0.0.0-224.0.0.255
     * 224.0.1.0-224.0.1.255
     * 224.0.2.0-239.255.255.255
     * 广播包—根据源及目的IP模糊查询
     * *.255
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getMulticastAndBroadcastPacketTypeCount_line",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="组播+广播包数据个数")
    public String getMulticastAndBroadcastPacketTypeCount_line(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //该折线图默认为动态折线图，需要在用户选择固定时间时，对数据间隔进行计算
        HttpRequestUtil.handleDynamicLineParams(params,configProperty.getEchart_default_points());
        //时间范围参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }

        try{
            //index 和 日期字段初始化
            params.initDateFieldAndIndex(Constant.PACKET_DATE_FIELD,Constant.PACKET_INDEX);
            //X轴，时间，logdate
            Bucket bucket = new Bucket("Date Histogram",Constant.PACKET_DATE_FIELD,params.getIntervalType(),params.getIntervalValue(),null,null);
            params.getBucketList().add(bucket);

            /****广播包******/
            //Y轴，数据包个数（count(logdate)）
            Metric metric4Broadcast = new Metric("count",Constant.PACKET_DATE_FIELD,"广播包");
            params.getMetricList().add(metric4Broadcast);
            //查询条件
            ArrayList<QueryCondition> queryConditions4Broadcast = new ArrayList<>();
            //源IP 广播包
            QueryCondition queryConditionSrcIp4Broadcast = new QueryCondition("wildcard","ipv4_src_addr.raw","*.255","must");
            queryConditions4Broadcast.add(queryConditionSrcIp4Broadcast);
            //目的IP 广播包
            QueryCondition queryConditionDstIp4Broadcast = new QueryCondition("wildcard","ipv4_dst_addr.raw","*.255","must");
            queryConditions4Broadcast.add(queryConditionDstIp4Broadcast);
            params.setQueryConditions(queryConditions4Broadcast);
            //以上两个条件用should连接
            params.setQueryConnectionType("should");
            Map<String, Object> result4Broadcast = flowService.getMultiAggregationDataSet(params);
            /****组播包******/
            //Y轴，数据包个数（count(logdate)）
            Metric metric4Multicast = new Metric("count",Constant.PACKET_DATE_FIELD,"组播包");
            params.getMetricList().remove(metric4Broadcast);
            params.getMetricList().add(metric4Multicast);
            //查询条件
            ArrayList<QueryCondition> queryConditions4Multicast = new ArrayList<>();
            //范围条件
            ArrayList<QueryRange> rangesDstIp4Multicast = new ArrayList<>();
            rangesDstIp4Multicast.add(new QueryRange("224.0.0.0",false,"224.0.0.255",true));
            rangesDstIp4Multicast.add(new QueryRange("224.0.1.0",true,"224.0.1.255",true));
            rangesDstIp4Multicast.add(new QueryRange("224.0.2.0",true,"239.255.255.255",true));
            QueryCondition queryConditionDstIp4Multicast = new QueryCondition("range","ipv4_dst_addr",rangesDstIp4Multicast,"should");
            queryConditions4Multicast.add(queryConditionDstIp4Multicast);
            //参数赋值到params
            params.setQueryConditions(queryConditions4Multicast);
            params.setQueryConnectionType("must");
            Map<String, Object> result4Multicast = flowService.getMultiAggregationDataSet(params);

            //将数据进行处理，满足前端显示效果
            LinkedHashMap<String,ArrayList<Map<String,Object>>> newResult4Broadcast = ControllerDataTransUtil.convertToDynamicLineData(result4Broadcast);
            LinkedHashMap<String,ArrayList<Map<String,Object>>> newResult4Multicast = ControllerDataTransUtil.convertToDynamicLineData(result4Multicast);
            LinkedHashMap<String,ArrayList<Map<String,Object>>> newResult = new LinkedHashMap<>();
            newResult.putAll(newResult4Broadcast);
            newResult.putAll(newResult4Multicast);
            return Constant.successData(JSONArray.fromObject(newResult).toString());
        }catch(Exception e){
            logger.error("实时统计流量数据访问包个数"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }

    /**
     * @param request
     * 流量统计/广播包/组播报/组播包数据+广播包数据个数
     * 组播包—根据目的IP模糊查询
     * 224.0.0.0-224.0.0.255
     * 224.0.1.0-224.0.1.255
     * 224.0.2.0-239.255.255.255
     * 广播包—根据源及目的IP模糊查询
     * *.255
     * @return
     */
    @ResponseBody
    @RequestMapping("/getMulticastAndBroadcastPacketTypeCount")
    @DescribeLog(describe="组播+广播包数据个数")
    public String getMulticastAndBroadcastPacketTypeCount(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String countfield = "ipv4_dst_addr";
        String [] types = {"defaultpacket"};
        Map<String,String> tMap = getStartEndTime(request);

        // 组播条件
        Map<String, String> multicastmap = new HashMap<String, String>();
        multicastmap.put("multicast","ipv4_dst_addr");
        // 广播条件
        Map<String, String> broadcastmap = new HashMap<String, String>();
        broadcastmap.put("broadcast","ipv4_src_addr.raw,ipv4_dst_addr.raw");
        List<Map<String, Object>> multicastlist = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> broadcastlist = new ArrayList<Map<String, Object>>();
        int size =10;

        try {
            multicastlist = flowService.getCountByMetrics(types,countfield,size,tMap.get("starttime"),tMap.get("endtime"),multicastmap,index);
            broadcastlist = flowService.getCountByMetrics(types,countfield,size,tMap.get("starttime"),tMap.get("endtime"),broadcastmap,index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String,Object> mapList = new HashMap<>();
        //List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        // 组播包数据
        if (multicastlist.size()>0){
            Map<String,Object> multicast = new HashMap<>();
            multicast.put("name",tMap.get("endtime"));
            Object [] value = {tMap.get("endtime"),multicastlist.get(0).get("agg")};
            multicast.put("value",value);
            mapList.put("multicast",multicast);
            //list.add(multicast);
        }
        // 广播包数据
        if (broadcastlist.size()>0){
            Map<String,Object> broadcast = new HashMap<>();
            broadcast.put("name",tMap.get("endtime"));
            Object [] value = {tMap.get("endtime"),broadcastlist.get(0).get("agg")};
            broadcast.put("value",value);
            mapList.put("broadcast",broadcast);
            //list.add(broadcast);
        }

        return JSONArray.fromObject(mapList).toString();
    }
    /**
     * @param request
     * 流量统计/端口流量/TCP目的端口总流量
     * Group 目的端口Where协议类型为TCP，计算数据包个数（倒序）
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getTCPDstPortCount_barAndPie", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="TCP目的端口总流量")
    public String getTCPDstPortCount_barAndPie(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //查询条件，必须是TCP协议
        //Map<String,String> queryParam = new HashMap<>();
        //queryParam.put("protocol_name.raw","TCP");
        //params.setQueryParam(queryParam);
        ArrayList<QueryCondition> queryConditions = new ArrayList<>();
        QueryCondition qc = new QueryCondition("term","protocol_name.raw","TCP","must");
        queryConditions.add(qc);
        params.setQueryConditions(queryConditions);
        //index 和 日期字段初始化
        params.initDateFieldAndIndex(Constant.PACKET_DATE_FIELD,Constant.PACKET_INDEX);
        //X轴，目的端口（l4_dst_port）
        Bucket bucket = new Bucket("terms","l4_dst_port",null,null,10,"desc");
        params.getBucketList().add(bucket);
        //Y轴，数据包个数（count(logdate)）
        Metric metric = new Metric("count",Constant.PACKET_DATE_FIELD,"数据包个数");
        params.getMetricList().add(metric);
        try{
            Map<String, Object> result = flowService.getMultiAggregationDataSet(params);
            return Constant.successData(JSONArray.fromObject(result).toString()) ;
        }catch(Exception e){
            logger.error("TCP目的端口总流量"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * @param request
     * 流量统计/端口流量/TCP目的端口总流量
     * Group 目的端口Where协议类型为TCP，计算数据包大小（倒序），转换成KB
     * @return
     */
    @ResponseBody
    @RequestMapping("/getTCPDstPortCount")
    @DescribeLog(describe="TCP目的端口总流量")
    public String getTCPDstPortCount(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String groupfield = "l4_dst_port";
        String [] types = {"defaultpacket"};
        Map<String,String> tMap = getStartEndTime(request);
        Map<String, String> map = new HashMap<String, String>();
        map.put("protocol_name.raw","TCP");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int size =10;

        try {
            list = flowService.groupBy(index,types,groupfield,size,tMap.get("starttime"),tMap.get("endtime"),map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        if(list.size()>0){
            for(Map.Entry<String,Object> m:list.get(0).entrySet()){
                Map<String,Object> temp = new HashMap<>();
                temp.put(m.getKey(),m.getValue());
                resultList.add(temp);
            }
        }
        return JSONArray.fromObject(resultList).toString();
    }
    /**
     * @param request
     * 流量统计/端口流量/UDP目的端口总流量
     * Group 目的端口Where协议类型为UDP，计算数据包个数（倒序）
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getUDPDstPortCount_barAndPie", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="UDP目的端口总流量")
    public String getUDPDstPortCount_barAndPie(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //查询条件，必须是TCP协议
        //Map<String,String> queryParam = new HashMap<>();
        //queryParam.put("protocol_name.raw","UDP");
        //params.setQueryParam(queryParam);
        ArrayList<QueryCondition> queryConditions = new ArrayList<>();
        QueryCondition qc = new QueryCondition("term","protocol_name.raw","UDP","must");
        queryConditions.add(qc);
        params.setQueryConditions(queryConditions);
        //index 和 日期字段初始化
        params.initDateFieldAndIndex(Constant.PACKET_DATE_FIELD,Constant.PACKET_INDEX);
        //X轴，目的端口（l4_dst_port）
        Bucket bucket = new Bucket("terms","l4_dst_port",null,null,10,"desc");
        params.getBucketList().add(bucket);
        //Y轴，数据包个数（count(logdate)）
        Metric metric = new Metric("count",Constant.PACKET_DATE_FIELD,"数据包个数");
        params.getMetricList().add(metric);
        try{
            Map<String, Object> result = flowService.getMultiAggregationDataSet(params);
            return Constant.successData(JSONArray.fromObject(result).toString()) ;
        }catch(Exception e){
            logger.error("目的端口总流量"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * @param request
     * 流量统计/端口流量/UDP目的端口总流量
     * Group 目的端口Where协议类型为UDP，计算数据包大小（倒序），转换成KB
     * @return
     */
    @ResponseBody
    @RequestMapping("/getUDPDstPortCount")
    @DescribeLog(describe="UDP目的端口总流量")
    public String getUDPDstPortCount(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String groupfield = "l4_dst_port";
        String [] types = {"defaultpacket"};
        Map<String,String> tMap = getStartEndTime(request);
        Map<String, String> map = new HashMap<String, String>();
        map.put("protocol_name.raw","UDP");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int size =10;

        try {
            list = flowService.groupBy(index,types,groupfield,size,tMap.get("starttime"),tMap.get("endtime"),map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        if(list.size()>0){
            for(Map.Entry<String,Object> m:list.get(0).entrySet()){
                Map<String,Object> temp = new HashMap<>();
                temp.put(m.getKey(),m.getValue());
                resultList.add(temp);
            }
        }
        return JSONArray.fromObject(resultList).toString();
    }
    /**
     * @param request
     * 流量统计/全局实时流量/实时统计流量数据访问包大小
     * 计算某个时间段内的数据包个数
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getPacketCount_line",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="实时统计流量数据访问包个数")
    public String getPacketCount_line(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //该折线图默认为动态折线图，需要在用户选择固定时间时，对数据间隔进行计算
        HttpRequestUtil.handleDynamicLineParams(params,configProperty.getEchart_default_points());
        //时间范围参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }

        try{
            //index 和 日期字段初始化
            params.initDateFieldAndIndex(Constant.PACKET_DATE_FIELD,Constant.PACKET_INDEX);
            //X轴，时间，logdate
            Bucket bucket = new Bucket("Date Histogram",Constant.PACKET_DATE_FIELD,params.getIntervalType(),params.getIntervalValue(),null,null);
            params.getBucketList().add(bucket);
            //Y轴，数据包个数（count(packet_length)）
            Metric metric = new Metric("count",Constant.PACKET_DATE_FIELD,"数据包个数");
            params.getMetricList().add(metric);
            Map<String, Object> result = flowService.getMultiAggregationDataSet(params);
            //轮询折线图
            //将数据进行处理，满足前端显示效果
            LinkedHashMap<String,ArrayList<Map<String,Object>>> newResult = ControllerDataTransUtil.convertToDynamicLineData(result);
            return Constant.successData(JSONArray.fromObject(newResult).toString());
        }catch(Exception e){
            logger.error("实时统计流量数据访问包个数"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * @param request
     * 流量统计/全局实时流量/全局数据包个数
     * 计算某个时间段内的数据包个数
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPacketCount")
    @DescribeLog(describe="全局-数据包个数")
    public String getPacketCount(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String countfield = "packet_length";
        String [] types = {"defaultpacket"};
        Map<String,String> tMap = getStartEndTime(request);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        int size =10;

        try {
            list = flowService.getCountByMetrics(types,countfield,size,tMap.get("starttime"),tMap.get("endtime"),null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String,Object> result = new HashMap<>();
        if (list.size()>0){
            result.put("name",tMap.get("endtime"));
            Object [] value = {tMap.get("endtime"),list.get(0).get("agg")};
            result.put("value",value);
        }
        return JSONArray.fromObject(result).toString();
    }

    /**
     * @param request
     * 地图
     * @return
     */
    @ResponseBody
    @RequestMapping("/getMap")
    @DescribeLog(describe="首页地图地球-展示流量的流向")
    public String getMap(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String [] groupfields = {"src_addr_city.raw","dst_addr_city.raw"};
        String [] types = {"defaultpacket"};
        List<List<Map<String, Object>>> list = new ArrayList<>();
        //默认获取一周内的数据
        //String oneWeekSecond = "86400";
        //Map<String,String> map = getStartEndTime(oneWeekSecond);
        Map<String,String> map = getStartEndTime(request);
        int size =10;
        try {
            list = flowService.groupBy(index,types,groupfields,size,map.get("starttime"),map.get("endtime"),null);
            //list = flowService.groupBy(index,types,groupfields,size,"2020-05-20 00:00:00","2020-05-22 23:59:59",null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONArray.fromObject(list).toString();
    }
    /**
     * @param request
     * 流量统计/统计应用的平均响应时间
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDomaiUrlAvgResponsetime_barAndPie", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="统计应用的平均响应时间")
    public String getDomaiUrlAvgResponsetime_barAndPie(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //index 和 日期字段初始化
        params.initDateFieldAndIndex(Constant.PACKET_DATE_FIELD,Constant.PACKET_INDEX);
        //X轴，域名（domain_url）
        Bucket bucket = new Bucket("terms","domain_url.raw",null,null,10,"desc");
        params.getBucketList().add(bucket);
        //Y轴，平均响应时间（average(responsetime)）
        Metric metric = new Metric("average","responsetime","平均响应时间");
        params.getMetricList().add(metric);
        try{
            Map<String, Object> result = flowService.getMultiAggregationDataSet(params);
            return Constant.successData(JSONArray.fromObject(result).toString()) ;
        }catch(Exception e){
            logger.error("统计应用的平均响应时间"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * @param request
     * 统计应用的平均响应时间
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDomaiUrlAvgResponsetime", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="统计应用的平均响应时间")
    public String getDomaiUrlAvgResponsetime(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        String groupfields = "domain_url.raw";
        String avgfield = "responsetime";
        String [] types = {"defaultpacket"};
        String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);

        List<Map<String, Object>> list = new ArrayList<>();
        int size =10;
        // 判断封装参数的hsdata是否为null，不是解析里面的参数内容
        if (hsData!=null) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = new HashMap<String, String>();
            // 处理返回的json参数，转为map
            try {
                map = MapUtil.removeMapEmptyValue(mapper.readValue(hsData, Map.class));
            } catch (IOException e) {
                e.printStackTrace();
            }

            String starttime = "";
            String endtime = "";
            // 判断时间范围查询条件是否存在，如果存在提取参数
            if (map.get("starttime") != null) {
                Object start = map.get("starttime");
                starttime = start.toString();
                map.remove("starttime");
            }
            if (map.get("endtime") != null) {
                Object end = map.get("endtime");
                endtime = end.toString();
                map.remove("endtime");
            }

            try {
                list = flowService.groupByThenAvg(types,groupfields,avgfield,size,starttime,endtime,map,index);
            } catch (Exception e) {
                logger.error("统计应用的平均响应时间报错");
                e.printStackTrace();
            }
        }else {
            try {
                list = flowService.groupByThenAvg(types,groupfields,avgfield,size,null,null,null,index);
            } catch (Exception e) {
                logger.error("统计应用的平均响应时间报错");
                e.printStackTrace();
            }
        }
        //将获取的数据与服务列表中的信息进行匹配，服务列表中名字不为空的，进行替换
        List<Map<String,Object>> llist = new ArrayList<Map<String,Object>> ();
        if(list.size()>0){
            //遍历统计的数据
            for(Map<String,Object> tmap : list){
                Map<String,Object> lmap = new ConcurrentHashMap<>();
                //与获取的ip信息与域名url进行对应
                ServiceInfo sInfo = serviceInfoDao.selectServiceByUrl(tmap.get("key").toString());
                if(sInfo!=null&&sInfo.getName()!=null&&sInfo.getName()!=""){
                    lmap.put("key",sInfo.getName());
                    //lmap.put((sInfo.getName()==null||sInfo.getName()=="")?tmap.get("key").toString():sInfo.getName(),tmap.get("value").toString());
                }else{
                    lmap.put("key",tmap.get("key").toString());
                }
                lmap.put("value",tmap.get("value"));
                llist.add(lmap);
            }
        }
        return JSONArray.fromObject(llist).toString();
    }
    /**
     * @param request
     * 统计单个应用的功能url平均响应时间
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getRequestUrlAvgResponsetime_barAndPie", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="统计单个应用的功能url平均响应时间")
    public String getRequestUrlAvgResponsetime_barAndPie(HttpServletRequest request) {
        //处理参数
        VisualParam params = HttpRequestUtil.getVisualParamByRequest(request);
        //参数异常
        if(!Strings.isNullOrEmpty(params.getErrorInfo())){
            return Constant.failureMessage(params.getErrorInfo());
        }
        //index 和 日期字段初始化
        params.initDateFieldAndIndex(Constant.PACKET_DATE_FIELD,Constant.PACKET_INDEX);
        //X轴，全量url（complete_url）
        Bucket bucket = new Bucket("terms","complete_url.raw",null,null,10,"desc");
        params.getBucketList().add(bucket);
        //Y轴，平均响应时间（average(responsetime)）
        Metric metric = new Metric("average","responsetime","平均响应时间");
        params.getMetricList().add(metric);
        try{
            Map<String, Object> result = flowService.getMultiAggregationDataSet(params);
            return Constant.successData(JSONArray.fromObject(result).toString()) ;
        }catch(Exception e){
            logger.error("统计单个应用的功能url平均响应时间"+e.getMessage());
            return Constant.failureMessage("数据查询失败！");
        }
    }
    /**
     * @param request
     * 统计单个应用的功能url平均响应时间
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getRequestUrlAvgResponsetime", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="统计单个应用的功能url平均响应时间")
    public String getRequestUrlAvgResponsetime(HttpServletRequest request) {
        String index = configProperty.getEs_old_index();
        //获取全量的url，用以
        String groupfields = "complete_url.raw";
        String avgfield = "responsetime";
        String [] types = {"defaultpacket"};
        String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);

        List<Map<String, Object>> list = new ArrayList<>();
        int size =10;

        // 判断封装参数的hsdata是否为null，不是解析里面的参数内容
        if (hsData!=null) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = new HashMap<String, String>();
            // 处理返回的json参数，转为map
            try {
                map = MapUtil.removeMapEmptyValue(mapper.readValue(hsData, Map.class));
            } catch (IOException e) {
                e.printStackTrace();
            }

            String starttime = "";
            String endtime = "";
            // 判断时间范围查询条件是否存在，如果存在提取参数
            if (map.get("starttime") != null) {
                Object start = map.get("starttime");
                starttime = start.toString();
                map.remove("starttime");
            }
            if (map.get("endtime") != null) {
                Object end = map.get("endtime");
                endtime = end.toString();
                map.remove("endtime");
            }

            try {
                list = flowService.groupByThenAvg(types,groupfields,avgfield,size,starttime,endtime,map,index);
            } catch (Exception e) {
                logger.error("统计单个应用的功能url平均响应时间报错");
                e.printStackTrace();
            }
        }else {
            try {
                list = flowService.groupByThenAvg(types,groupfields,avgfield,size,null,null,null,index);
            } catch (Exception e) {
                logger.error("统计单个应用的功能url平均响应时间报错");
                e.printStackTrace();
            }
        }
        //将获取的数据与服务列表中的信息进行匹配，服务列表中名字不为空的，进行替换
        List<Map<String,Object>> llist = new ArrayList<Map<String,Object>> ();
        if(list.size()>0){
            //遍历统计的数据
            for(Map<String,Object> tmap : list){
                Map<String,Object> lmap = new ConcurrentHashMap<>();
                //与获取的ip信息与域名url进行对应
                ServiceInfo sInfo = serviceInfoDao.selectServiceByUrl(tmap.get("key").toString());
                if(sInfo!=null&&sInfo.getName()!=null&&sInfo.getName()!=""){
                    lmap.put("key",sInfo.getName());
                }else{
                    lmap.put("key",tmap.get("key").toString());
                }
                lmap.put("value",tmap.get("value"));
                llist.add(lmap);
            }
        }
        return JSONArray.fromObject(llist).toString();
    }


    /******************************性能测试***************************************************/

    /**
     * @param request
     * index segments 强制合并
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/indexForceMerge", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="index segments合并操作")
    public String indexForceMerge(HttpServletRequest request) {
        // 需要合并的索引
        String indices = request.getParameter("indices");
        // 合并后的segments数
        int segments = Integer.valueOf(request.getParameter("segments"));

        HashMap result = new HashMap();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            Date startdate = new Date();
            System.out.println("开始时间：" + format.format(startdate));
            result = logService.indexForceMerge(segments, true, indices);
            Date enddate = new Date();
            System.out.println("结束时间：" + format.format(enddate));
            // 响应时间计算
            long times = enddate.getTime() - startdate.getTime();
            System.out.println("时间差：" + times + " ms");
        } catch (ElasticsearchStatusException exception ) {
            // Elasticsearch exception [type=index_not_found_exception, reason=no such index [hslog_packet2020-04-27]]
            String DetailedMessage = exception.getDetailedMessage();
            if (DetailedMessage.indexOf("index_not_found_exception")!=-1){
                logger.error("未找到合并的索引："+indices);
                return Constant.failureMessage("未找到合并的索引："+indices);
            }
        } catch (Exception e) {
            logger.error("索引合并失败!");
            logger.error(e.getMessage());
            e.printStackTrace();
            return Constant.failureMessage("索引合并失败！");
        }
        return JSONArray.fromObject(result).toString();
    }


    /**
     * @param request
     * 测试单个idnex的数据查询速度
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getFlowListBeforeMerge", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="单个index的数据查询")
    public String getFlowListBeforeMerge(HttpServletRequest request) {

        String index = request.getParameter("indices");
        String [] indices = index.split(",");

        List<Map<String, Object>> list = new ArrayList<>();
        String size = "10";
        HashMap map = new HashMap();
        String [] type = {"defaultpacket"};
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            Date startdate = new Date();
            System.out.println("开始时间："+format.format(startdate));
            list = flowService.getFlowListByBlend(map, null, null, "1", size, type, indices);
            Date enddate = new Date();
            System.out.println("结束时间："+format.format(enddate));
            // 响应时间计算
            long times = enddate.getTime() - startdate.getTime();
            System.out.println("时间差："+times +" ms");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return JSONArray.fromObject(list).toString();
    }


    /**
     * @param request
     * 测试单个idnex的聚合查询，业务流分析是目前产品中最复杂的查询
     * @return
     */
    @ResponseBody
    @RequestMapping("/getNetworkTop")
    @DescribeLog(describe="通过netflow数据获取网络拓扑数据")
    public String getNetworkTop(HttpServletRequest request) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        logger.info("进入业务流分析统计   "+format.format(new Date()));

        String index = request.getParameter("indices");
        String [] indices = index.split(",");

        // 双向划线
        String [] groupbys = {"ipv4_src_addr.raw","ipv4_dst_addr.raw"};
        String[] types = {"defaultpacket"};

        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        Map<String, String> searchmap = new HashMap<>();

        Map<String, List<Map<String, Object>>> map = new LinkedHashMap<String, List<Map<String, Object>>>();

        List<Map<String, Object>> datalist = new LinkedList<Map<String, Object>>();
        List<Map<String, Object>> linkslist = new LinkedList<Map<String, Object>>();
        // 临时map
        Map<String,Object> tMap = new HashMap<>();


        Date startdate = new Date();
        System.out.println("聚合开始时间："+format.format(startdate));
        // 聚合源IP和目的IP，处理他们的数据，得到一个以key（IP地址），value（相同IP：源IP访问量和目的IP访问量之和）的map
        for(String param:groupbys) {

            // 聚合源IP和目的IP
            List<Map<String, Object>> list = null;
            try {
                logger.warn("两次聚合查询（源IP，目的IP），聚合查询开始  "+format.format(new Date()));
                list = flowService.groupBy(index,types,param,100,starttime,endtime,searchmap);
                logger.warn("两次聚合查询（源IP，目的IP），聚合查询结束  "+format.format(new Date()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 数据处理，将源IP和目的IP两者之间相同的IP的值相加
            if (tMap.isEmpty()&&list.size()>0) {
                tMap = list.get(0);
            }else {
                if (list.size()>0){
                    for(Map.Entry<String, Object> entrymap : list.get(0).entrySet()) {
                        if (tMap.containsKey(entrymap.getKey())) {
                            int newvalue  = Integer.parseInt(tMap.get(entrymap.getKey()).toString())+Integer.parseInt(entrymap.getValue().toString());
                            tMap.put(entrymap.getKey(), newvalue);
                        }else{
                            tMap.put(entrymap.getKey(), entrymap.getValue());
                        }
                    }
                }

            }
        }

        // 遍历以上聚合数据结果
        for(Map.Entry<String, Object> key : tMap.entrySet()) {
            // 组织data中的数据内容
            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("name", key.getKey());
            dataMap.put("count", key.getValue());
            datalist.add(dataMap);
        }

        // 源IP、目的IP的连线，连线次数
        // linkslist = logService.groupBy(index, types, groupbys, searchmap,1000);
        try {
            logger.warn("源IP与目的IP之间的访问次数聚合，聚合查询开始  "+format.format(new Date()));
            linkslist = flowService.groupBys(index,types,groupbys,1000,starttime,endtime,searchmap);
            logger.warn("源IP与目的IP之间的访问次数聚合，聚合查询结束  "+format.format(new Date()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Date enddate = new Date();
        System.out.println("聚合结束时间："+format.format(enddate));
        // 响应时间计算
        long times = enddate.getTime() - startdate.getTime();
        System.out.println("聚合耗时："+times +" ms");

        //遍历删除,通过遍历连线的list判断source和target两个值是否在tMap的key中，如果不在则删除该连线map
        Iterator<Map<String, Object>> iterator = linkslist.iterator();
        logger.warn("通过遍历聚合得到的访问次数，删除IP不存在连线，遍历开始  "+format.format(new Date()));
        while (iterator.hasNext()) {
            Map<String, Object> linkmap = iterator.next();
            if (!(tMap.containsKey(linkmap.get("source"))&&tMap.containsKey(linkmap.get("target")))) {
                iterator.remove();//使用迭代器的删除方法删除
            }/*else {
				System.out.println("包含："+linkmap);
			}*/
        }
        logger.warn("通过遍历聚合得到的访问次数，删除IP不存在连线，遍历结束  "+format.format(new Date()));
        map.put("data", datalist);
        map.put("links", linkslist);

        logger.info("结束业务流分析统计   "+format.format(new Date()));
        return JSONArray.fromObject(map).toString();
    }
}

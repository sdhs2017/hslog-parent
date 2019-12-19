package com.jz.bigdata.business.logAnalysis.flow.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jz.bigdata.business.logAnalysis.flow.service.IflowService;
import com.jz.bigdata.business.logAnalysis.log.LogType;
import com.jz.bigdata.business.logAnalysis.log.service.IlogService;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.equipment.dao.IEquipmentDao;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.serviceInfo.dao.IServiceInfoDao;
import com.jz.bigdata.common.serviceInfo.entity.ServiceInfo;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.ContextFront;
import com.jz.bigdata.util.DescribeLog;
import com.jz.bigdata.util.MapUtil;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParsePosition;
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

    @Resource(name="logService")
    private IlogService logService;

    @Resource(name="flowService")
    private IflowService flowService;

    @Resource(name ="configProperty")
    private ConfigProperty configProperty;

    @Resource
    private IEquipmentDao equipmentDao;

    @Resource
    private IServiceInfoDao serviceInfoDao;
    /**
     * @param request
     * 统计netflow源IP、目的IP、源端口、目的端口的数量
     * @return
     */
    @ResponseBody
    @RequestMapping("/getTopGroupByIPOrPort")
    @DescribeLog(describe="统计netflow源IP、目的IP、源端口、目的端口的数量")
    public String getTopGroupByIPOrPort(HttpServletRequest request) {
        String index = configProperty.getEs_index();
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
     * @return
     */
    @ResponseBody
    @RequestMapping("/getDstIPCountGroupByHTTPSrcIP")
    @DescribeLog(describe="统计应用资产的IP访问次数")
    public String getDstIPCountGroupByHTTPSrcIP(HttpServletRequest request) {
        String index = configProperty.getEs_index();
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

        String index = configProperty.getEs_index();
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

        String index = configProperty.getEs_index();
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
                // 遍历第一层数据结果
                for(Map.Entry<String, Object> key1 : list1.get(0).entrySet()) {
                    // 组织data中的数据内容
                    Map<String,Object> dataMap1 = new HashMap<>();
                    dataMap1.put("node", 2);
                    dataMap1.put("name", "level2\n"+key1.getKey());
                    dataMap1.put("count", key1.getValue());
                    datalist.add(dataMap1);
                    // 组织links中的数据内容
                    Map<String,Object> linksMap1 = new HashMap<>();
                    linksMap1.put("node", 1);
                    if (groupby.equals("ipv4_src_addr")) {
                        linksMap1.put("source", iporport);
                        linksMap1.put("target", "level2\n"+key1.getKey());
                    }else {
                        linksMap1.put("source", "level2\n"+key1.getKey());
                        linksMap1.put("target", iporport);
                    }
                    linksMap1.put("count", key1.getValue());
                    linkslist.add(linksMap1);


                    // 第二层查询条件和数据结果
                    searchmap.put(groupby, key1.getKey());
                    List<Map<String, Object>> list2 = null;
                    try {
                        list2 = flowService.groupBy(index,types,param,5,null,null,searchmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // 遍历第二层数据结果
                    for(Map.Entry<String, Object> key2: list2.get(0).entrySet()) {
                        // 组织data中的数据内容
                        Map<String,Object> dataMap2 = new HashMap<>();
                        dataMap2.put("node", 3);
                        dataMap2.put("name", "level3\n"+key2.getKey());
                        dataMap2.put("count", key2.getValue());
                        datalist.add(dataMap2);
                        // 组织links中的数据内容
                        Map<String,Object> linksMap2 = new HashMap<>();
                        linksMap2.put("node", 2);
                        if (groupby.equals("ipv4_src_addr")) {
                            linksMap2.put("source", "level2\n"+key1.getKey());
                            linksMap2.put("target", "level3\n"+key2.getKey());
                        }else {
                            linksMap2.put("source", "level3\n"+key2.getKey());
                            linksMap2.put("target", "level2\n"+key1.getKey());
                        }

                        linksMap2.put("count", key2.getValue());
                        linkslist.add(linksMap2);

						/*searchmap.put(groupby, key1.getKey());
						List<Map<String, Object>> itelists = logService.groupBy(index, types, param, searchmap,5);
						Map<String,Object> itemap = itelists.get(0);*/
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

        String index = configProperty.getEs_index();

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
                list = flowService.groupBy(index,types,param,100,starttime,endtime,searchmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 数据处理，将源IP和目的IP两者之间相同的IP的值相加
            if (tMap.isEmpty()) {
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
            linkslist = logService.groupBy(index,types,groupbys,1000,starttime,endtime,searchmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //遍历删除,通过遍历连线的list判断source和target两个值是否在tMap的key中，如果不在则删除该连线map
        Iterator<Map<String, Object>> iterator = linkslist.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> linkmap = iterator.next();
            if (!(tMap.containsKey(linkmap.get("source"))&&tMap.containsKey(linkmap.get("target")))) {
                iterator.remove();//使用迭代器的删除方法删除
            }/*else {
				System.out.println("包含："+linkmap);
			}*/
        }
        map.put("data", datalist);
        map.put("links", linkslist);


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
                    list = flowService.getFlowListByBlend(map, starttime, endtime, page, size, types, configProperty.getEs_index());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                String[] types = {LogType.LOGTYPE_DEFAULTPACKET};
                try {
                    list = flowService.getFlowListByBlend(map, starttime, endtime, page, size, types, configProperty.getEs_index());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Map<String, Object> allmap = new HashMap<>();
            allmap = list.get(0);
            list.remove(0);
            allmap.put("list", list);
            String result = JSONArray.fromObject(allmap).toString();
            String replace=result.replace("\\\\005", "<br/>");

            return replace;
        }else{
            try {
                list = flowService.getFlowListByBlend(null,  null, null, "1", "12", null, configProperty.getEs_index());
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
                    //list = logService.getListByMap(configProperty.getEs_index(), types, starttime, endtime, map,page,size);
                    try {
                        list = flowService.getFlowListByBlend(map,  starttime, endtime, page, size, types, configProperty.getEs_index());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    map.put("userid",session.getAttribute(Constant.SESSION_USERID).toString());
                    try {
                        list = flowService.getFlowListByBlend(map,  starttime, endtime, page, size, types, configProperty.getEs_index());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }else {
                String[] types = {LogType.LOGTYPE_DEFAULTPACKET};
                if (userrole.equals("1")) {
                    //list = logService.getListByMap(configProperty.getEs_index(), types, starttime, endtime, map,page,size);
                    try {
                        list = flowService.getFlowListByBlend(map,  starttime, endtime, page, size, types, configProperty.getEs_index());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    map.put("userid",session.getAttribute(Constant.SESSION_USERID).toString());
                    try {
                        list = flowService.getFlowListByBlend(map,  starttime, endtime, page, size, types, configProperty.getEs_index());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            Map<String, Object> allmap = new HashMap<>();
            allmap = list.get(0);
            list.remove(0);
            allmap.put("list", list);
            String result = JSONArray.fromObject(allmap).toString();
            String replace=result.replace("\\\\005", "<br/>");

            return replace;
        }else{
            try {
                list = flowService.getFlowListByBlend(null,  null, null, "1", "12", null, configProperty.getEs_index());
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
        String index = configProperty.getEs_index();
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
        String index = configProperty.getEs_index();
        String  groupby = "domain_url.raw";
        String [] types = {"defaultpacket"};
        // 资产的ip和端口即目的IP和目的端口
        String des_ip = request.getParameter("des_ip");
        String des_port = request.getParameter("des_port");
        // 源IP和源端口
        String source_ip = request.getParameter("source_ip");
        String source_port = request.getParameter("source_port");
        // 时间段
        String starttime = request.getParameter("startTime");
        String endtime = request.getParameter("endTime");

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

        if (starttime!=null&&!starttime.equals("")) {
            starttime = starttime+" 00:00:00";
        }
        if (endtime!=null&&!endtime.equals("")) {
            endtime = endtime+" 23:59:59";
        }

        // url 排行榜 TOP10
        int size = 10;
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        //list = logService.groupBy(index, types, groupby, map);
        try {
            list = flowService.groupBy(index,types,groupby,size,starttime,endtime,map);
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
        String index = configProperty.getEs_index();
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
        String index = configProperty.getEs_index();
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
     * 通过时间段统计操作系统的种类及数量
     * @return
     */
    @ResponseBody
    @RequestMapping("/getUserAgentOSGroupByTime")
    @DescribeLog(describe="通过时间段统计操作系统的种类及数量")
    public String getUserAgentOSGroupByTime(HttpServletRequest request) {
        String index = configProperty.getEs_index();
        String  groupby = "user_agent_os.raw";
        String [] types = {"defaultpacket"};

        // 构建参数map
        Map<String, String> map = new HashMap<String, String>();
        map.put("requestorresponse", "request");
        map.put("application_layer_protocol", "http");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        if (starttime!=null&&!starttime.equals("")) {
            if (!starttime.contains(" ")){
                starttime = starttime+" 00:00:00";
            }
        }
        if (endtime!=null&&!endtime.equals("")) {
            if (!endtime.contains(" ")){
                endtime = endtime+" 23:59:59";
            }
        }
        int size =10;
        //list = logService.groupBy(index, types, groupby, map);
        try {
            list = flowService.groupBy(index, types, groupby, size,starttime,endtime, map);
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
        String index = configProperty.getEs_index();
        String  groupby = "ipv4_dst_addr.raw";
        String sumfield = "packet_length";
        String [] types = {"defaultpacket"};

        // 构建参数map
        Map<String, String> map = new HashMap<String, String>();
        map.put("requestorresponse", "request");
        map.put("application_layer_protocol", "http");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        int size =10;

        try {
            list = flowService.groupByThenSum(index, types, groupby, sumfield, size,starttime,endtime, map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return JSONArray.fromObject(list).toString();
    }



    /**
     * @param request
     * 通过时间段统计浏览器的种类及数量
     * @return
     */
    @ResponseBody
    @RequestMapping("/getUserAgentBrowserGroupByTime")
    @DescribeLog(describe="通过时间段统计浏览器的种类及数量")
    public String getUserAgentBrowserGroupByTime(HttpServletRequest request) {
        String index = configProperty.getEs_index();
        String  groupby = "user_agent_browser.raw";
        String [] types = {"defaultpacket"};

        // 构建参数map
        Map<String, String> map = new HashMap<String, String>();
        map.put("requestorresponse", "request");
        map.put("application_layer_protocol", "http");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        //
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");

        int size =10;//默认top10

        //list = logService.groupBy(index, types, groupby, map);
        try {
            list = flowService.groupBy(index, types, groupby, size,starttime,endtime, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONArray.fromObject(list).toString();
    }

    /**
     * @param request
     * 实时统计流量数据访问包大小
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPacketLengthPerSecond")
    @DescribeLog(describe="实时统计流量数据访问包大小")
    public String getPacketLengthPerSecond(HttpServletRequest request) {
        String index = configProperty.getEs_index();
        String sumfield = "packet_length";
        String [] types = {"defaultpacket"};
        // 获取前端传入的时间参数
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        if (starttime!=null&&!starttime.equals("")) {
            if (!starttime.contains(" ")){
                starttime = starttime+" 00:00:00";
            }
        }
        if (endtime!=null&&!endtime.equals("")) {
            if (!endtime.contains(" ")){
                endtime = endtime+" 23:59:59";
            }
            // 如果开始时间为空，计算开始时间，默认结束时间减去2秒
            if (starttime==null||starttime.equals("")){
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ParsePosition pos = new ParsePosition(0);
                Date enddate = format.parse(endtime, pos);
                Calendar cal = Calendar.getInstance();
                cal.setTime(enddate);
                cal.add(Calendar.SECOND,-2);
                starttime = format.format(cal.getTime());
            }
        }
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        int size =10;

        try {
            list = flowService.getSumByMetrics(types,sumfield,size,starttime,endtime,null,index);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String,Object> result = new HashMap<>();
        if (list.size()>0){
            result.put("name",endtime);
            Object [] value = {endtime,list.get(0).get("agg")};
            result.put("value",value);
        }

        return JSONArray.fromObject(result).toString();
    }
    /**
     * @param request
     * 源ip地址流量
     * @return
     */
    @ResponseBody
    @RequestMapping("/getSrcIPFlow")
    @DescribeLog(describe="源ip地址流量")
    public String getSrcIPFlow(HttpServletRequest request) {
        String index = configProperty.getEs_index();
        String groupfield = "ipv4_src_addr";
        String sumfield = "packet_length";
        String [] types = {"defaultpacket"};
        // 获取前端传入的时间参数
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");

        // 构建参数map
        Map<String, String> map = new HashMap<String, String>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        int size =10;

        try {
            list = flowService.groupByThenSum(index,types,groupfield,sumfield,size,starttime,endtime,map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return JSONArray.fromObject(list).toString();
    }
    /**
     * @param request
     * 目的ip地址流量
     * @return
     */
    @ResponseBody
    @RequestMapping("/getDstIPFlow")
    @DescribeLog(describe="目的ip地址流量")
    public String getDstIPFlow(HttpServletRequest request) {
        String index = configProperty.getEs_index();
        String groupfield = "ipv4_dst_addr";
        String sumfield = "packet_length";
        String [] types = {"defaultpacket"};
        // 获取前端传入的时间参数
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        Map<String, String> map = new HashMap<String, String>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int size =10;

        try {
            list = flowService.groupByThenSum(index,types,groupfield,sumfield,size,starttime,endtime,map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return JSONArray.fromObject(list).toString();
    }
    /**
     * @param request
     * 传输层协议长度排行
     * @return
     */
    @ResponseBody
    @RequestMapping("/getTransportLength")
    @DescribeLog(describe="传输层协议长度排行")
    public String getTransportLength(HttpServletRequest request) {
        String index = configProperty.getEs_index();
        String groupfield = "protocol_name.raw";//.raw 不分词
        String sumfield = "packet_length";
        String [] types = {"defaultpacket"};
        // 获取前端传入的时间参数
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        Map<String, String> map = new HashMap<String, String>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int size =10;

        try {
            list = flowService.groupByThenSum(index,types,groupfield,sumfield,size,starttime,endtime,map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONArray.fromObject(list).toString();
    }
    /**
     * @param request
     * 应用层协议长度排行
     * @return
     */
    @ResponseBody
    @RequestMapping("/getApplicationLength")
    @DescribeLog(describe="应用层协议长度排行")
    public String getApplicationLength(HttpServletRequest request) {
        String index = configProperty.getEs_index();
        String groupfield = "application_layer_protocol.raw";
        String sumfield = "packet_length";
        String [] types = {"defaultpacket"};
        // 获取前端传入的时间参数
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        Map<String, String> map = new HashMap<String, String>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int size =10;

        try {
            list = flowService.groupByThenSum(index,types,groupfield,sumfield,size,starttime,endtime,map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONArray.fromObject(list).toString();
    }
    /**
     * @param request
     * 综合协议长度排行
     * @return
     */
    @ResponseBody
    @RequestMapping("/getMultipleLength")
    @DescribeLog(describe="综合协议长度排行")
    public String getMultipleLength(HttpServletRequest request) {
        String index = configProperty.getEs_index();
        String groupfieldApplication = "application_layer_protocol.raw";
        String groupfieldTransport = "protocol_name.raw";
        String sumfield = "packet_length";
        String [] types = {"defaultpacket"};
        // 获取前端传入的时间参数
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        Map<String, String> map = new HashMap<String, String>();
        List<Map<String, Object>> listApplication = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> listTransport = new ArrayList<Map<String, Object>>();
        int size =10;

        try {
            listApplication = flowService.groupByThenSum(index,types,groupfieldApplication,sumfield,size,starttime,endtime,map);
            listTransport = flowService.groupByThenSum(index,types,groupfieldTransport,sumfield,size,starttime,endtime,map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //将应用层和传输层协议合并
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.putAll(listTransport.get(0));
        maps.putAll(listApplication.get(0));
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
     * 全局数据包类型及个数（<64,64-1519,大于1519 ，由于数据包大小仅取到1460，因此设置为64-1460）
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPacketTypeCount")
    @DescribeLog(describe="全局数据包类型及个数")
    public String getPacketTypeCount(HttpServletRequest request) {
        String index = configProperty.getEs_index();
        String groupfield = "application_layer_protocol";
        //String sumfield = "packet_length";
        String [] types = {"defaultpacket"};
        // 获取前端传入的时间参数
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");

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
                List<Map<String, Object>> tempList = flowService.getCountByMetrics(types,groupfield,size,starttime,endtime,m);
                map.put(t.getKey(),tempList.get(0).get("agg"));
            }
            list.add(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONArray.fromObject(list).toString();
    }
    /**
     * @param request
     * 资产（ip） 数据包个数，取目的地址IP进行统计
     * @return
     */
    @ResponseBody
    @RequestMapping("/getDstIPPacketCount")
    @DescribeLog(describe="资产（ip） 数据包个数")
    public String getDstIPPacketCount(HttpServletRequest request,HttpSession session) {
        String index = configProperty.getEs_index();
        String groupfield = "ipv4_dst_addr";
        String [] types = {"defaultpacket"};
        // 获取前端传入的时间参数
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        if (starttime!=null&&!starttime.equals("")) {
            if (!starttime.contains(" ")){
                starttime = starttime+" 00:00:00";
            }
        }
        if (endtime!=null&&!endtime.equals("")) {
            if (!endtime.contains(" ")) {
                endtime = endtime + " 23:59:59";
            }
        }
        Map<String, String> map = new HashMap<String, String>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int size =10;

        try {
            list = flowService.groupBy(index,types,groupfield,size,starttime,endtime,map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //与获取的ip信息与资产ip进行对应

        List<Map<String,Object>> llist = new ArrayList<Map<String,Object>> ();
        //遍历统计的数据
        for(Map.Entry<String,Object> tmap : list.get(0).entrySet()){
            Map<String,Object> lmap = new ConcurrentHashMap<>();
            List<Equipment> l = equipmentDao.selectAllByPage("", "", tmap.getKey(),"" ,"" ,"" ,"", 0,10);
            if(l.size()>=1){
                lmap.put(l.get(0).getName(),tmap.getValue());
            }else{
                lmap.put(tmap.getKey(),tmap.getValue());
            }
            llist.add(lmap);
        }
        return JSONArray.fromObject(llist).toString();
    }
    /**
     * @param request
     * 资产（域名） 数据包个数
     * @return
     */
    @ResponseBody
    @RequestMapping("/getDstUrlPacketCount")
    @DescribeLog(describe="资产（域名） 数据包个数")
    public String getDstUrlPacketCount(HttpServletRequest request,HttpSession session) {
        String index = configProperty.getEs_index();
        String groupfield = "domain_url.raw";
        String [] types = {"defaultpacket"};
        // 获取前端传入的时间参数
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        if (starttime!=null&&!starttime.equals("")) {
            if (!starttime.contains(" ")){
                starttime = starttime+" 00:00:00";
            }
        }
        if (endtime!=null&&!endtime.equals("")) {
            if (!endtime.contains(" ")) {
                endtime = endtime + " 23:59:59";
            }
        }
        Map<String, String> map = new HashMap<String, String>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int size =10;

        try {
            list = flowService.groupBy(index,types,groupfield,size,starttime,endtime,map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //与获取的ip信息与资产ip进行对应

        List<Map<String,Object>> llist = new ArrayList<Map<String,Object>> ();
        //遍历统计的数据
        for(Map.Entry<String,Object> tmap : list.get(0).entrySet()){
            Map<String,Object> lmap = new ConcurrentHashMap<>();
            ServiceInfo sInfo = serviceInfoDao.selectServiceByUrl(tmap.getKey());
            if(sInfo!=null){
                lmap.put(sInfo.getName(),tmap.getValue());
            }else{
                lmap.put(tmap.getKey(),tmap.getValue());
            }
            llist.add(lmap);
        }
        return JSONArray.fromObject(llist).toString();
    }
    /**
     * @param request
     * 目的端口总流量
     * @return
     */
    @ResponseBody
    @RequestMapping("/getDstPortCount")
    @DescribeLog(describe="目的端口总流量")
    public String getDstPortCount(HttpServletRequest request) {
        String index = configProperty.getEs_index();
        String groupfield = "l4_dst_port";
        String [] types = {"defaultpacket"};
        // 获取前端传入的时间参数
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        Map<String, String> map = new HashMap<String, String>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int size =10;

        try {
            list = flowService.groupBy(index,types,groupfield,size,starttime,endtime,map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONArray.fromObject(list).toString();
    }
    /**
     * @param request
     * TCP目的端口总流量
     * @return
     */
    @ResponseBody
    @RequestMapping("/getTCPDstPortCount")
    @DescribeLog(describe="TCP目的端口总流量")
    public String getTCPDstPortCount(HttpServletRequest request) {
        String index = configProperty.getEs_index();
        String groupfield = "l4_dst_port";
        String [] types = {"defaultpacket"};
        // 获取前端传入的时间参数
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        Map<String, String> map = new HashMap<String, String>();
        map.put("protocol_name.raw","TCP");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int size =10;

        try {
            list = flowService.groupBy(index,types,groupfield,size,starttime,endtime,map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONArray.fromObject(list).toString();
    }
    /**
     * @param request
     * UDP目的端口总流量
     * @return
     */
    @ResponseBody
    @RequestMapping("/getUDPDstPortCount")
    @DescribeLog(describe="UDP目的端口总流量")
    public String getUDPDstPortCount(HttpServletRequest request) {
        String index = configProperty.getEs_index();
        String groupfield = "l4_dst_port";
        String [] types = {"defaultpacket"};
        // 获取前端传入的时间参数
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        Map<String, String> map = new HashMap<String, String>();
        map.put("protocol_name.raw","UDP");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int size =10;

        try {
            list = flowService.groupBy(index,types,groupfield,size,starttime,endtime,map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONArray.fromObject(list).toString();
    }
    /**
     * @param request
     * 全局-数据包个数
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPacketCount")
    @DescribeLog(describe="全局-数据包个数")
    public String getPacketCount(HttpServletRequest request) {
        String index = configProperty.getEs_index();
        String countfield = "packet_length";
        String [] types = {"defaultpacket"};
        // 获取前端传入的时间参数
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        if (starttime!=null&&!starttime.equals("")) {
            if (!starttime.contains(" ")){
                starttime = starttime+" 00:00:00";
            }
        }
        if (endtime!=null&&!endtime.equals("")) {
            if (!endtime.contains(" ")){
                endtime = endtime+" 23:59:59";
            }
            // 如果开始时间为空，计算开始时间，默认结束时间减去2秒
            if (starttime==null||starttime.equals("")){
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ParsePosition pos = new ParsePosition(0);
                Date enddate = format.parse(endtime, pos);
                Calendar cal = Calendar.getInstance();
                cal.setTime(enddate);
                cal.add(Calendar.SECOND,-2);
                starttime = format.format(cal.getTime());
            }
        }
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        int size =10;

        try {
            list = flowService.getCountByMetrics(types,countfield,size,starttime,endtime,null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String,Object> result = new HashMap<>();
        if (list.size()>0){
            result.put("name",endtime);
            Object [] value = {endtime,list.get(0).get("agg")};
            result.put("value",value);
        }
        return JSONArray.fromObject(result).toString();
    }
}

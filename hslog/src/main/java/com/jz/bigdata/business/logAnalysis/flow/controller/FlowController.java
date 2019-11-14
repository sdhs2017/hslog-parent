package com.jz.bigdata.business.logAnalysis.flow.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jz.bigdata.business.logAnalysis.flow.service.IflowService;
import com.jz.bigdata.business.logAnalysis.log.LogType;
import com.jz.bigdata.business.logAnalysis.log.service.IlogService;
import com.jz.bigdata.common.Constant;
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
import java.text.SimpleDateFormat;
import java.util.*;

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
            List<Map<String, Object>> list = flowService.groupBy(index, types, groupby+".raw",10, starttime, endtime, searchmap);

            List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
            for(Map.Entry<String, Object> key : list.get(0).entrySet()) {
                Map<String,Object> tMap = new HashMap<>();
                tMap.put("IpOrPort", key.getKey());
                tMap.put("count", key.getValue());
                tmplist.add(tMap);
            }
            map.put(groupby, tmplist);
        }else {
            for(String param:groupbys) {
                List<Map<String, Object>> list = flowService.groupBy(index, types, param, 10, starttime, endtime, searchmap);

                List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
                for(Map.Entry<String, Object> key : list.get(0).entrySet()) {
                    Map<String,Object> tMap = new HashMap<>();
                    tMap.put("IpOrPort", key.getKey());
                    tMap.put("count", key.getValue());
                    tmplist.add(tMap);
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
        list = flowService.groupBy(index, types, groupby, size,null,null,map);

        long ipv4_dst_addr_count = logService.getCount(index, types, map);

        // 中心圆数据统计
        Map<String,Object> ipv4_dst_addr_Map = new HashMap<>();
        ipv4_dst_addr_Map.put("ipv4_dst_addr", ipv4_dst_addr);
        ipv4_dst_addr_Map.put("count", ipv4_dst_addr_count);

        // IP访问次数统计
        List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
        for(Map.Entry<String, Object> key : list.get(0).entrySet()) {
            Map<String,Object> tMap = new HashMap<>();
            tMap.put("source_ip", key.getKey());
            tMap.put("count", key.getValue());
            tmplist.add(tMap);
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
                List<Map<String, Object>> list = flowService.groupBy(index,types,param,size,null,null,searchmap);

                List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
                for(Map.Entry<String, Object> key : list.get(0).entrySet()) {
                    Map<String,Object> tMap = new HashMap<>();
                    tMap.put("IpOrPort", key.getKey());
                    tMap.put("count", key.getValue());
                    tmplist.add(tMap);
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
                List<Map<String, Object>> list1 = flowService.groupBy(index,types,param,5,null,null,searchmap);

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
                    List<Map<String, Object>> list2 = flowService.groupBy(index,types,param,5,null,null,searchmap);
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
        String [] groupbys = {"ipv4_src_addr","ipv4_dst_addr"};
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
            List<Map<String, Object>> list = flowService.groupBy(index,types,param,100,starttime,endtime,searchmap);
            // 数据处理，将源IP和目的IP两者之间相同的IP的值相加
            if (tMap.isEmpty()) {
                tMap = list.get(0);
            }else {
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
        linkslist = logService.groupBy(index,types,groupbys,1000,starttime,endtime,searchmap);


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
    public String getFlowListByBlend(HttpServletRequest request, HttpSession session) throws JsonParseException, JsonMappingException, IOException {
        // receive parameter
        Object userrole = session.getAttribute(Constant.SESSION_USERROLE);
        String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);
        List<Map<String, Object>> list =null;

        // 参数是否为空
        if (hsData!=null){
            ObjectMapper mapper = new ObjectMapper();
            // 处理map参数
            Map<String, String> map = MapUtil.removeMapEmptyValue(mapper.readValue(hsData, Map.class));

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
                list = flowService.getFlowListByBlend(map, starttime, endtime, page, size, types, configProperty.getEs_index());
            }else {
                String[] types = {LogType.LOGTYPE_DEFAULTPACKET};
                list = flowService.getFlowListByBlend(map, starttime, endtime, page, size, types, configProperty.getEs_index());
            }
            Map<String, Object> allmap = new HashMap<>();
            allmap = list.get(0);
            list.remove(0);
            allmap.put("list", list);
            String result = JSONArray.fromObject(allmap).toString();
            String replace=result.replace("\\\\005", "<br/>");

            return replace;
        }else{
            list = flowService.getFlowListByBlend(null,  null, null, "1", "12", null, configProperty.getEs_index());
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
                    list = flowService.getFlowListByBlend(map,  starttime, endtime, page, size, types, configProperty.getEs_index());
                }else {
                    map.put("userid",session.getAttribute(Constant.SESSION_USERID).toString());
                    list = flowService.getFlowListByBlend(map,  starttime, endtime, page, size, types, configProperty.getEs_index());
                }
            }else {
                String[] types = {LogType.LOGTYPE_DEFAULTPACKET};
                if (userrole.equals("1")) {
                    //list = logService.getListByMap(configProperty.getEs_index(), types, starttime, endtime, map,page,size);
                    list = flowService.getFlowListByBlend(map,  starttime, endtime, page, size, types, configProperty.getEs_index());
                }else {
                    map.put("userid",session.getAttribute(Constant.SESSION_USERID).toString());
                    list = flowService.getFlowListByBlend(map,  starttime, endtime, page, size, types, configProperty.getEs_index());
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
            list = flowService.getFlowListByBlend(null,  null, null, "1", "12", null, configProperty.getEs_index());
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
        list = flowService.groupBy(index, types, groupby, size,null,null, map);

        long domain_url_count = logService.getCount(index, types, map);

        Map<String,Object> domainMap = new HashMap<>();
        domainMap.put("domain_url", domain_url);
        domainMap.put("count", domain_url_count);


        List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
        for(Map.Entry<String, Object> key : list.get(0).entrySet()) {
            Map<String,Object> tMap = new HashMap<>();
            tMap.put("source_ip", key.getKey());
            tMap.put("count", key.getValue());
            tmplist.add(tMap);
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
        list = flowService.groupBy(index,types,groupby,size,starttime,endtime,map);

        List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
        for(Map.Entry<String, Object> key : list.get(0).entrySet()) {
            Map<String,Object> tMap = new HashMap<>();
            tMap.put("domain_url", key.getKey());
            tMap.put("count", key.getValue());
            tmplist.add(tMap);
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
        list = flowService.groupBy(index, types, groupby, size,starttime,endtime, map);

        List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
        for(Map.Entry<String, Object> key : list.get(0).entrySet()) {
            Map<String,Object> tMap = new HashMap<>();
            tMap.put("complete_url", key.getKey());
            tMap.put("count", key.getValue());
            tmplist.add(tMap);
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
        list = flowService.groupBy(index, types, groupby, size,null,null, map);

        //map.put("domain_url", domain_url);

        long complete_url_count = logService.getCount(index, types, map);

        Map<String,Object> complete_urlMap = new HashMap<>();
        complete_urlMap.put("complete_url", complete_url);
        complete_urlMap.put("count", complete_url_count);


        List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
        for(Map.Entry<String, Object> key : list.get(0).entrySet()) {
            Map<String,Object> tMap = new HashMap<>();
            tMap.put("source_ip", key.getKey());
            tMap.put("count", key.getValue());
            tmplist.add(tMap);
        }

        Map<String,Object> result = new HashMap<>();
        result.put("complete_url", complete_urlMap);
        result.put("source", tmplist);

        return JSONArray.fromObject(result).toString();
    }


}

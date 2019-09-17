package com.jz.bigdata.business.logAnalysis.log.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.elasticsearch.action.index.IndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jz.bigdata.business.logAnalysis.log.LogType;
import com.jz.bigdata.business.logAnalysis.log.entity.App_file;
import com.jz.bigdata.business.logAnalysis.log.entity.DHCP;
import com.jz.bigdata.business.logAnalysis.log.entity.DNS;
import com.jz.bigdata.business.logAnalysis.log.entity.DefaultPacket;
import com.jz.bigdata.business.logAnalysis.log.entity.Log4j;
import com.jz.bigdata.business.logAnalysis.log.entity.Mysql;
import com.jz.bigdata.business.logAnalysis.log.entity.Netflow;
import com.jz.bigdata.business.logAnalysis.log.entity.PacketFilteringFirewal;
import com.jz.bigdata.business.logAnalysis.log.entity.Syslog;
import com.jz.bigdata.business.logAnalysis.log.entity.Unknown;
import com.jz.bigdata.business.logAnalysis.log.entity.Winlog;
import com.jz.bigdata.business.logAnalysis.log.service.IlogService;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.alarm.service.IAlarmService;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.equipment.service.IEquipmentService;
import com.jz.bigdata.common.safeStrategy.entity.SafeStrategy;
import com.jz.bigdata.common.safeStrategy.service.ISafeStrategyService;
import com.jz.bigdata.common.users.service.IUsersService;
//import com.jz.bigdata.framework.spring.es.elasticsearch.ClientTemplate;
import com.hs.elsearch.template.bak.ClientTemplate;
import com.jz.bigdata.util.BaseController;
import com.jz.bigdata.util.CSVUtil;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.DescribeLog;
import com.jz.bigdata.util.Sendmail;

import net.sf.json.JSONArray;

@Controller
@RequestMapping("/log")
public class LogController extends BaseController{

	@Resource(name="logService")
	private IlogService logService;
	
	@Resource(name = "EquipmentService")
	private IEquipmentService equipmentService;
	
	@Resource(name ="configProperty")  
    private ConfigProperty configProperty;
	
	@Resource(name ="SafeStrategyService")  
    private ISafeStrategyService safeStrategyService;
	
	@Resource(name="AlarmService")
	private IAlarmService alarmService;
	
	@Resource(name ="UsersService")
	private IUsersService usersService;
	
	@Autowired protected ClientTemplate clientTemplate;
	
	private String exportProcess = "[{\"state\":\"finished\",\"value\":\"1-1\"}]";
	
	
	/**
	 * 轮巡导出状态
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getExportProcess")
	@DescribeLog(describe="轮巡导出状态")
	public String getExportProcess() {
		return exportProcess;
	}

	public void setExportProcess(String exportProcess) {
		this.exportProcess = exportProcess;
	}
	
	
	/**
	 * @param request
	 * @return 获取索引数据ById
	 */
	@ResponseBody
	@RequestMapping("/getListById")
	@DescribeLog(describe="通过日志ID获取日志信息")
	public String getListById(HttpServletRequest request) {
		//index=estest&type=Log4j&id=AV6FBY7KXlvRoY9aku1N
		String index = request.getParameter("index");
		String type = request.getParameter("type");
		String id = request.getParameter("id");
//		List<Map<String, Object>> list = logService.searchById("estest", "Log4j", "AV6FBY7KXlvRoY9aku1N");
//		String result = JSONArray.fromObject(list).toString();
//		String result = logService.searchById("estest", "Log4j", "AV6FBY7KXlvRoY9aku1N");
		String result = logService.searchById(index, type, id);
		System.out.println("-----------------result:----------------------");
		System.out.println(result);
		return result;
	}
	
	/**
	 * @param request
	 * @return 删除结果String（DELETED、NOT_FOUND、NOOP）
	 */
	@ResponseBody
	@RequestMapping("/deleteById")
	@DescribeLog(describe="通过日志ID删除日志信息")
	public String deleteById(HttpServletRequest request) {
		String type = request.getParameter("type");
		String id = request.getParameter("id");
		String [] types = type.split(",");
		String [] ids = id.split(",");
		String result ="false";
		if (types.length<2) {
			for(int i=0;i<ids.length;i++) {
				result = logService.deleteById(configProperty.getEs_index(), types[0], ids[i]);
			}
		}else {
			for(int i=0;i<ids.length;i++) {
				result = logService.deleteById(configProperty.getEs_index(), types[i], ids[i]);
			}
			
		}
		
		
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value="/createIndexAndMapping",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="初始化数据结构")
	public String createIndexAndMapping(HttpServletRequest request) {
		Map<String, Object> map= new HashMap<>();
		try {
			logService.createIndexAndmapping(configProperty.getEs_index(),LogType.LOGTYPE_SYSLOG, new Syslog().toMapping());
			logService.createIndexAndmapping(configProperty.getEs_index(),LogType.LOGTYPE_WINLOG, new Winlog().toMapping());
			logService.createIndexAndmapping(configProperty.getEs_index(),LogType.LOGTYPE_LOG4J, new Log4j().toMapping());
			logService.createIndexAndmapping(configProperty.getEs_index(),LogType.LOGTYPE_MYSQLLOG, new Mysql().toMapping());
			logService.createIndexAndmapping(configProperty.getEs_index(),LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG, new PacketFilteringFirewal().toMapping());
			logService.createIndexAndmapping(configProperty.getEs_index(),LogType.LOGTYPE_NETFLOW, new Netflow().toMapping());
			logService.createIndexAndmapping(configProperty.getEs_index(),LogType.LOGTYPE_DNS, new DNS().toMapping());
			logService.createIndexAndmapping(configProperty.getEs_index(),LogType.LOGTYPE_DHCP, new DHCP().toMapping());
			logService.createIndexAndmapping(configProperty.getEs_index(),LogType.LOGTYPE_APP_FILE, new App_file().toMapping());
			logService.createIndexAndmapping(configProperty.getEs_index(),LogType.LOGTYPE_APP_APACHE, new App_file().toMapping());
			logService.createIndexAndmapping(configProperty.getEs_index(),LogType.LOGTYPE_UNKNOWN, new Unknown().toMapping());
			
			// 网络数据包
			/*logService.createIndexAndmapping(configProperty.getEs_index(),LogType.LOGTYPE_HTTP, new Http().toMapping());
			logService.createIndexAndmapping(configProperty.getEs_index(),LogType.LOGTYPE_HTTPS, new Https().toMapping());
			logService.createIndexAndmapping(configProperty.getEs_index(),LogType.LOGTYPE_TCP, new Tcp().toMapping());*/
			logService.createIndexAndmapping(configProperty.getEs_index(),LogType.LOGTYPE_DEFAULTPACKET, new DefaultPacket().toMapping());
			
			map.put("state", true);
			map.put("msg", "数据结构初始化成功！");
			return JSONArray.fromObject(map).toString();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			map.put("state", false);
			map.put("msg", "数据结构初始化失败！");
			return JSONArray.fromObject(map).toString();
		}
		
	}
	
	
	/**
	 * @param request
	 * @return 
	 */
	@ResponseBody
	@RequestMapping("/getListByType")
	@DescribeLog(describe="通过日志类型获取数据")
	public List<Map<String, Object>> getListByType(HttpServletRequest request) {
		String index = request.getParameter("index");
		String type = request.getParameter("type");
		
		List<Map<String, Object>> list = logService.index(index, type);
		
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
		String type = request.getParameter("type");
		String [] types = null;
		if (!type.equals("")) {
			types = type.split(",");
		}
		String param = request.getParameter("param");
		String time = request.getParameter("time");
		String equipmentid = request.getParameter("equipmentid");
		
		Map<String, String> map = new HashMap<>();
		if (equipmentid!=null&&!equipmentid.equals("")) {
			map.put("equipmentid", equipmentid);
		}
		if (time!=null&&!time.equals("")) {
			map.put("logdate", time);
		}
		
		List<Map<String, Object>> list = logService.groupBy(index, types, param, map);
		
		return list;
	}
	
	/**
	 * @param request
	 * 统计事件的数据量
	 * @return 
	 */
	@ResponseBody
	@RequestMapping("/getCountGroupByEvent")
	@DescribeLog(describe="统计事件的数据量")
	public List<Map<String, Object>> getCountGroupByEvent(HttpServletRequest request) {
		String index = configProperty.getEs_index();
		String type = request.getParameter("type");
		String [] types = null;
		if (!type.equals("")) {
			types = type.split(",");
		}
		String dates = request.getParameter("param");
		String equipmentid = request.getParameter("equipmentid");
		String groupby = "event_type";
		
		List<Map<String, Object>> list = logService.getEventListGroupByEventType(index, types, dates, equipmentid, groupby);
		
		return list;
	}
	
	/**
	 * @param request
	 * 统计各个事件的数据量
	 * @return 
	 */
	@ResponseBody
	@RequestMapping("/getCountGroupByEventType")
	@DescribeLog(describe="统计各个事件的数据量")
	public List<Map<String, Object>> getCountGroupByEventType(HttpServletRequest request) {
		String index = configProperty.getEs_index();
		String type = request.getParameter("type");
		String [] types = null;
		if (!type.equals("")) {
			types = type.split(",");
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String param = request.getParameter("param");
		String equipmentid = request.getParameter("equipmentid");
		String [] hours = {"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
		// 0全部，1高危，2中等，3普通
		for(int i=0;i<4;i++) {
			List<Map<String, Object>> list1 = logService.getEventListGroupByTime(index, types, param,equipmentid,"event_type",i);
			Map<String, Object> map = new HashMap<>();
			for(String hour : hours) {
				map.put(hour, list1.get(0).get(hour)!=null?list1.get(0).get(hour):0);
			}
			list.add(map);
		}
		
		return list;
	}
	
	
	/**
	 * @param request
	 * 统计各时间段的数据量
	 * @return 
	 */
	@ResponseBody
	@RequestMapping("/getCountGroupByTime")
	@DescribeLog(describe="读取各时段日志数据量")
	public List<Map<String, Object>> getCountGroupByTime(HttpServletRequest request) {
		String index = configProperty.getEs_index();
		String type = request.getParameter("type");
		String[] types = null;
		if (type!=null&&!type.equals("")) {
			types = type.split(",");
		}
		
		String param = request.getParameter("param");
		String equipmentid = request.getParameter("equipmentid");
		String [] hours = {"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
		List<Map<String, Object>> list = logService.getListGroupByTime(index, types, param,equipmentid);
		
		Map<String, Object> map = new HashMap<>();
		for(String hour : hours) {
			map.put(hour, list.get(0).get(hour)!=null?list.get(0).get(hour):0);
		}
		list.clear();
		list.add(map);
		return list;
	}
	
	/**
	 * @param request
	 * 统计某时间段内的事件数量
	 * @return 
	 */
	@ResponseBody
	@RequestMapping("/getCountGroupByEventstype")
	@DescribeLog(describe="统计某时间段内的事件数量")
	public List<Map<String, Object>> getCountGroupByEventstype(HttpServletRequest request) {
		String index = configProperty.getEs_index();
		String type = request.getParameter("type");
		String [] types = null;
		if (!type.equals("")) {
			types = type.split(",");
		}
		String equipmentid = request.getParameter("equipmentid");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date enddate = new Date();
		String endtime = format.format(enddate);
		DecimalFormat df = new DecimalFormat("#.00");
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		List<SafeStrategy> safelist = safeStrategyService.selectByEquipmentId(equipmentid);
		for (SafeStrategy safeStrategy : safelist) {
			String dates = safeStrategy.getTime();
			String event_type = safeStrategy.getEvent_type();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(enddate);
			calendar.add(Calendar.MINUTE, -Integer.valueOf(dates));
			Date startdate = calendar.getTime();
			String starttime = format.format(startdate);
			List<Map<String, Object>> loglist = logService.getListGroupByEvent(index, types, equipmentid,event_type,starttime,endtime);
			
			if (!loglist.get(0).isEmpty()) {
				float per = Float.valueOf(loglist.get(0).get(safeStrategy.getEvent_type()).toString())/safeStrategy.getNumber();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("event_type", safeStrategy.getEvent_type());
				map.put("per",df.format(per*100));
				map.put("starttime",starttime);
				map.put("endtime", endtime);
				list.add(map);
			}else {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("event_type", safeStrategy.getEvent_type());
				map.put("per",df.format(0));
				map.put("starttime",starttime);
				map.put("endtime", endtime);
				list.add(map);
			}
		}
		
		return list;
	}
	
	/**
	 * @param request
	 * 统计url
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
		if (starttime!=null&&!starttime.equals("")) {
			map.put("starttime", starttime+" 00:00:00");
		}
		if (endtime!=null&&!endtime.equals("")) {
			map.put("endtime", endtime+" 23:59:59");
		}
		if (ipv4_dst_addr!=null&&!ipv4_dst_addr.equals("")) {
			map.put("ipv4_dst_addr", ipv4_dst_addr);
		}
		if (application_layer_protocol!=null&&!application_layer_protocol.equals("")) {
			map.put("application_layer_protocol", application_layer_protocol);
		}
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = logService.groupBy(index, types, groupby, map);
		
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		for(Entry<String, Object> key : list.get(0).entrySet()) {
			Map<String,Object> tMap = new HashMap<>();
			tMap.put("domain_url", key.getKey());
			tMap.put("count", key.getValue());
			tmplist.add(tMap);
		}
		
		
		return JSONArray.fromObject(tmplist).toString();
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
		
		list = logService.groupBy(index, types, groupby, map);
		
		long ipv4_dst_addr_count = logService.getCount(index, types, map);
		
		// 中心圆数据统计
		Map<String,Object> ipv4_dst_addr_Map = new HashMap<>();
		ipv4_dst_addr_Map.put("ipv4_dst_addr", ipv4_dst_addr);
		ipv4_dst_addr_Map.put("count", ipv4_dst_addr_count);
		
		// IP访问次数统计
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		for(Entry<String, Object> key : list.get(0).entrySet()) {
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
		
		list = logService.groupBy(index, types, groupby, map);
		
		long domain_url_count = logService.getCount(index, types, map);
		
		Map<String,Object> domainMap = new HashMap<>();
		domainMap.put("domain_url", domain_url);
		domainMap.put("count", domain_url_count);
		
		
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		for(Entry<String, Object> key : list.get(0).entrySet()) {
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
			map.put("starttime", starttime+" 00:00:00");
		}
		if (endtime!=null&&!endtime.equals("")) {
			map.put("endtime", endtime+" 23:59:59");
		}
		list = logService.groupBy(index, types, groupby, map);
		
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		for(Entry<String, Object> key : list.get(0).entrySet()) {
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
			map.put("domain_url", domain_url);
		}if (complete_url!=null&&!complete_url.equals("")) {
			map.put("complete_url", complete_url);
		}
		list = logService.groupBy(index, types, groupby, map);
		
		//map.put("domain_url", domain_url);
		
		long complete_url_count = logService.getCount(index, types, map);
		
		Map<String,Object> complete_urlMap = new HashMap<>();
		complete_urlMap.put("complete_url", complete_url);
		complete_urlMap.put("count", complete_url_count);
		
		
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		for(Entry<String, Object> key : list.get(0).entrySet()) {
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
	
	/**
	 * 
	 * @param request
	 * @return 
	 */
	@ResponseBody
	@RequestMapping("/getListOrderByParam")
	@DescribeLog(describe="获取排序后的日志数据")
	public String getListOrderByParam(HttpServletRequest request) {
//		String index = request.getParameter("index");
		String type = request.getParameter("type");
		String param = request.getParameter("param");
		String  order = request.getParameter("order");
		String page = request.getParameter("page");
		String size = request.getParameter("size");
		
		List<Map<String, Object>> list = logService.orderBy(configProperty.getEs_index(), type, param, order,page,size);
		Map<String, Object> map = new HashMap<>();
		map = list.get(0);
		list.remove(0);
		map.put("list", list);
		
		String result = JSONArray.fromObject(map).toString();
		
		System.out.println("---------------result----------------");
		System.err.println(result);
		return result;
	}
	
	
	
	/**
	 * 通过设备id获取该设备日志列表
	 * @param requestt
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value="/getLogListByEquipment.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="条件获取设备日志数据")
	public String getLogListByEquipment(HttpServletRequest request,Equipment equipment) {
		
		String ztData = request.getParameter("hsData");
		if(ztData!=null) {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> map = new HashMap<String, String>();
			
			try {
				map = removeMapEmptyValue(mapper.readValue(ztData, Map.class));
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			equipment.setId(map.get("id"));
			equipment=equipmentService.selectOneEquipment(equipment);
			
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
			}else {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				endtime = format.format(new Date());
			}
			
			Object pageObject = map.get("page");
			String page = pageObject.toString();
			map.remove("page");
			Object sizeObject = map.get("size");
			String size = sizeObject.toString();
			map.remove("size");
			
			String type = equipment.getLogType();
			
			/*int logType = equipment.getLogType();
			if(EquipmentConstant.LOGTYPE_SYSLOG==logType){
				type = EquipmentConstant.LOGTYPE_SYSLOG_EN;
			}else if(EquipmentConstant.LOGTYPE_WMI==logType){
				type = EquipmentConstant.LOGTYPE_WMI_EN;
			}else if(EquipmentConstant.LOGTYPE_SNMP==logType){
				type = EquipmentConstant.LOGTYPE_SNMP_EN;
			}else if(EquipmentConstant.LOGTYPE_LOG4J==logType){
				type = EquipmentConstant.LOGTYPE_LOG4J_EN;
			}else if(EquipmentConstant.LOGTYPE_MYSQL==logType){
				type = EquipmentConstant.LOGTYPE_MYSQL_EN;
			}else if(EquipmentConstant.LOGTYPE_FIREWALL==logType){
				type = EquipmentConstant.LOGTYPE_FIREWALL_EN;
			}else{
				type = null;
			}*/
			
			String equipmentId = equipment.getId();
			
			map.put("equipmentid", equipmentId);
			map.remove("id");
			ArrayList<String> arrayList = new ArrayList<>();
			
			if (type.equals("netflow")) {
				arrayList.add("defaultpacket");
			}
			String [] types = null;
			if (type!=null) {
				types = arrayList.toArray(new String[arrayList.size()]);
			}
			
			List<Map<String, Object>> list = logService.getListByMap(configProperty.getEs_index(), types, starttime, endtime, map,page,size);
			
			Map<String, Object> allmap = new HashMap<>();
			allmap = list.get(0);
			list.remove(0);
			allmap.put("list", list);
			String result = JSONArray.fromObject(allmap).toString();
			
			String replace=result.replace("\\\\005", "<br/>");
			System.out.println("---------------result----------------");
			System.err.println(result);
			return replace;
		}else {
			Map<String, Object> allmap = new HashMap<>();
			allmap.put("state", false);
			allmap.put("msg", "错误，参数为空");
			String result = JSONArray.fromObject(allmap).toString();
			return result;
		}
		
		
	}
	
	/**
	 * 通过关键字获取日志信息
	 * @param requestt
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getLogListByContent",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="查询日志数据")
	public String getLogListByContent(HttpServletRequest request,HttpSession session) {
		
		String ztData = request.getParameter("hsData");
		Object userrole = session.getAttribute(Constant.SESSION_USERROLE);
		
		Map<String, String> mapper = toMap(ztData);
		Object wordso = mapper.get("words");
		Object pageo = mapper.get("page");
		Object sizeo = mapper.get("size");
		String keyWords = null;
		if (wordso!=null) {
			keyWords = wordso.toString();
		}
		
		String page = pageo.toString();
		String size = sizeo.toString();
		
		String[] types = {LogType.LOGTYPE_LOG4J,LogType.LOGTYPE_WINLOG,LogType.LOGTYPE_SYSLOG,LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG,LogType.LOGTYPE_UNKNOWN,LogType.LOGTYPE_MYSQLLOG,LogType.LOGTYPE_NETFLOW,LogType.LOGTYPE_DNS,LogType
				.LOGTYPE_DHCP};

		List<Map<String, Object>> list =null;
		
		try {
			if (userrole.equals("1")) {
				list = logService.getListByContent(configProperty.getEs_index(), types, keyWords,page,size);
			}else {
				list = logService.getListByContent(configProperty.getEs_index(), types, keyWords,session.getAttribute(Constant.SESSION_USERID).toString(),page,size);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, Object> map = new HashMap<>();
		map = list.get(0);
		list.remove(0);
		map.put("list", list);
		
		String result = JSONArray.fromObject(map).toString();
//		System.out.println("result ="+result);
		String replace=result.replace("\\\\005", "<br/>");
//		System.out.println("---------------result----------------");
		
		return replace;
	}
	
	/**
	 * 组合查询
	 * @param requestt
	 * @author jiyourui
	 * @return 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value="/getLogListByBlend",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="组合查询日志数据")
	public String getLogListByBlend(HttpServletRequest request,HttpSession session) throws JsonParseException, JsonMappingException, IOException {
		// receive parameter
		Object userrole = session.getAttribute(Constant.SESSION_USERROLE);
		String ztData = request.getParameter("hsData");
		System.out.println(ztData);
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		
		map = removeMapEmptyValue(mapper.readValue(ztData, Map.class));
		System.out.println(map);
		Object pageo = map.get("page");
		Object sizeo = map.get("size");
		map.remove("page");
		map.remove("size");
		
		String page = pageo.toString();
		String size = sizeo.toString();
		
		
		ArrayList<String> arrayList = new ArrayList<>();
		List<Map<String, Object>> list =null;
		
		if (map.get("type")!=null&&!map.get("type").equals("")) {
			arrayList.add(map.get("type"));
			map.remove("type");
			String [] types = arrayList.toArray(new String[arrayList.size()]);
			if (userrole.equals("1")) {
				list = logService.getListByBlend(configProperty.getEs_index(), types, map,page,size);
			}else {
				list = logService.getListByBlend(configProperty.getEs_index(), types, map,session.getAttribute(Constant.SESSION_USERID).toString(),page,size);
			}
		}else {
			String[] types = {LogType.LOGTYPE_LOG4J,LogType.LOGTYPE_WINLOG,LogType.LOGTYPE_SYSLOG,LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG,LogType.LOGTYPE_UNKNOWN,LogType.LOGTYPE_MYSQLLOG,LogType.LOGTYPE_NETFLOW};
			if (userrole.equals("1")) {
				list = logService.getListByBlend(configProperty.getEs_index(), types, map,page,size);
			}else {
				list = logService.getListByBlend(configProperty.getEs_index(), types, map,session.getAttribute(Constant.SESSION_USERID).toString(),page,size);
			}
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
	 * 组合查询
	 * @param requestt
	 * @author jiyourui
	 * @return 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value="/getLogListByFlow",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="业务流分析-深钻-日志内容")
	public String getLogListByFlow(HttpServletRequest request,HttpSession session) throws JsonParseException, JsonMappingException, IOException {
		// receive parameter
		Object userrole = session.getAttribute(Constant.SESSION_USERROLE);
		String ztData = request.getParameter("hsData");
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		
		map = removeMapEmptyValue(mapper.readValue(ztData, Map.class));
		System.out.println(map);
		Object pageo = map.get("page");
		Object sizeo = map.get("size");
		
		String page = pageo.toString();
		String size = sizeo.toString();
		map.remove("page");
		map.remove("size");
		
		ArrayList<String> arrayList = new ArrayList<>();
		List<Map<String, Object>> list =null;
		Map<String, String[]> param = new HashMap<>();
		String[] multified = {"ipv4_dst_addr","ipv4_src_addr"};
		param.put(map.get("ip"), multified);
		
		if (map.get("type")!=null&&!map.get("type").equals("")) {
			arrayList.add(map.get("type"));
			map.remove("type");
			String [] types = arrayList.toArray(new String[arrayList.size()]);
			if (userrole.equals("1")) {
				list = logService.getListByMultiField(configProperty.getEs_index(), types, param,map,page,size);
			}else {
				list = logService.getListByBlend(configProperty.getEs_index(), types, map,session.getAttribute(Constant.SESSION_USERID).toString(),page,size);
			}
		}else {
			String[] types = {LogType.LOGTYPE_LOG4J,LogType.LOGTYPE_WINLOG,LogType.LOGTYPE_SYSLOG,LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG,LogType.LOGTYPE_UNKNOWN,LogType.LOGTYPE_MYSQLLOG,LogType.LOGTYPE_NETFLOW};
			if (userrole.equals("1")) {
				list = logService.getListByBlend(configProperty.getEs_index(), types, map,page,size);
			}else {
				list = logService.getListByBlend(configProperty.getEs_index(), types, map,session.getAttribute(Constant.SESSION_USERID).toString(),page,size);
			}
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
	 * 查询+导出
	 * @param requestt
	 * @author jiyourui
	 * @return 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value="/exportLogList",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="导出查询的日志数据")
	public String exportLogList(HttpServletRequest request,HttpSession session) throws JsonParseException, JsonMappingException, IOException {
		
		Object userrole = session.getAttribute(Constant.SESSION_USERROLE);
		Object username = session.getAttribute(Constant.SESSION_USERNAME);
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd'T'HHmmss");
		
		String hsData = request.getParameter("hsData");
		Map<String, Object> allmap= new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		
		map = removeMapEmptyValue(mapper.readValue(hsData, Map.class));
		
		Object pageo = map.get("page");
		Object sizeo = map.get("size");
		Object exportSizeo = map.get("exportSize");
		
		map.remove("page");
		map.remove("size");
		map.remove("exportSize");
		
		String page = pageo.toString();
		String size = sizeo.toString();
		String exportSize = exportSizeo.toString();
		
		
		Integer sizeInt = Integer.valueOf(exportSize);
		int filesizes = 10000;
		
		int forsize = sizeInt/filesizes;
		int modsize = sizeInt%filesizes;
		
		int fileSize = 0;
		
		Map<String, Object> resultmap = new HashMap<>();
		if (forsize>0&&modsize>0) {
			fileSize = forsize+1;
			resultmap.put("state", "doing");
			resultmap.put("value", "0-"+fileSize);
			setExportProcess(JSONArray.fromObject(resultmap).toString());
		}else if (forsize>0&&modsize==0) {
			fileSize = forsize;
			resultmap.put("state", "doing");
			resultmap.put("value", "0-"+fileSize);
			setExportProcess(JSONArray.fromObject(resultmap).toString());
		}else {
			resultmap.put("state", "doing");
			resultmap.put("value", "0-1");
			setExportProcess(JSONArray.fromObject(resultmap).toString());
		}
		
		try {
			for(int i=1;i<=forsize;i++) {
				
				page = String.valueOf(i);
				String filesize = String.valueOf(filesizes);
				
				ArrayList<String> arrayList = new ArrayList<>();
				List<Map<String, Object>> list = null;
				if (map.get("type")!=null&&!map.get("type").equals("")) {
					arrayList.add(map.get("type"));
					String [] types = arrayList.toArray(new String[arrayList.size()]);
					if (userrole.equals("1")) {
						System.out.println("10000条数据测试开始时间：     ---------"+timeformat.format(new Date()));
						list = logService.getListByBlend(configProperty.getEs_index(), types, map,page,filesize);
						System.out.println("10000条数据测试结束时间：     ---------"+timeformat.format(new Date()));
					}else {
						list = logService.getListByBlend(configProperty.getEs_index(), types, map,session.getAttribute(Constant.SESSION_USERID).toString(),page,filesize);
					}
				}else {
					String[] types = {LogType.LOGTYPE_LOG4J,LogType.LOGTYPE_WINLOG,LogType.LOGTYPE_SYSLOG,LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG,LogType.LOGTYPE_UNKNOWN,LogType.LOGTYPE_MYSQLLOG,LogType.LOGTYPE_NETFLOW};
					if (userrole.equals("1")) {
						System.out.println("10000条数据测试开始时间：     ---------"+timeformat.format(new Date()));
						list = logService.getListByBlend(configProperty.getEs_index(), types, map,page,filesize);
						System.out.println("10000条数据测试结束时间：     ---------"+timeformat.format(new Date()));
					}else {
						list = logService.getListByBlend(configProperty.getEs_index(), types, map,session.getAttribute(Constant.SESSION_USERID).toString(),page,filesize);
					}
				}
				
				// 设置表格头
		        Object[] head = {"时间", "日志类型", "日志级别", "资产名称", "资产IP", "日志内容"};
		        List<Object> headList = Arrays.asList(head);
				Date date = new Date();
				// 过滤第一条，第一条数据为总数统计
		        list.remove(0);
		        CSVUtil.createCSVFile(headList, list, "/home"+File.separator+"exportfile"+File.separator+username+File.separator+dateformat.format(date), "exportlog"+timeformat.format(date),null);
		        
		        if (i==forsize&&modsize==0) {
		        	resultmap.put("state", "finished");
					resultmap.put("value", i+"-"+fileSize);
					setExportProcess(JSONArray.fromObject(resultmap).toString());
				}else {
					resultmap.put("state", "doing");
					resultmap.put("value", i+"-"+fileSize);
					setExportProcess(JSONArray.fromObject(resultmap).toString());
				}
		       
			}
			if (modsize>0) {
				page = String.valueOf(forsize+1);
				String filesize = String.valueOf(modsize);
				
				ArrayList<String> arrayList = new ArrayList<>();
				List<Map<String, Object>> list = null;
				
				if (map.get("type")!=null&&!map.get("type").equals("")) {
					arrayList.add(map.get("type"));
					String [] types = arrayList.toArray(new String[arrayList.size()]);
					if (userrole.equals("1")) {
						System.out.println("10000条数据测试开始时间：     ---------"+timeformat.format(new Date()));
						list = logService.getListByBlend(configProperty.getEs_index(), types, map,page,filesize);
						System.out.println("10000条数据测试结束时间：     ---------"+timeformat.format(new Date()));
					}else {
						list = logService.getListByBlend(configProperty.getEs_index(), types, map,session.getAttribute(Constant.SESSION_USERID).toString(),page,filesize);
					}
				}else {
					String[] types = {LogType.LOGTYPE_LOG4J,LogType.LOGTYPE_WINLOG,LogType.LOGTYPE_SYSLOG,LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG,LogType.LOGTYPE_UNKNOWN,LogType.LOGTYPE_MYSQLLOG,LogType.LOGTYPE_NETFLOW};
					if (userrole.equals("1")) {
						System.out.println("10000条数据测试开始时间：     ---------"+timeformat.format(new Date()));
						list = logService.getListByBlend(configProperty.getEs_index(), types, map,page,filesize);
						System.out.println("10000条数据测试结束时间：     ---------"+timeformat.format(new Date()));
					}else {
						list = logService.getListByBlend(configProperty.getEs_index(), types, map,session.getAttribute(Constant.SESSION_USERID).toString(),page,filesize);
					}
				}
				
				// 设置表格头
		        Object[] head = {"时间", "日志类型", "日志级别", "资产名称", "资产IP", "日志内容" };
		        List<Object> headList = Arrays.asList(head);
		        
		        Date date = new Date();
		        // 过滤第一条，第一条数据为总数统计
		        list.remove(0);
		        // 开始写入csv文件
		        CSVUtil.createCSVFile(headList, list, "/home"+File.separator+"exportfile"+File.separator+username+File.separator+dateformat.format(date), "exportlog"+timeformat.format(date),null);
		        if (forsize>0) {
		        	resultmap.put("state", "finished");
					resultmap.put("value", fileSize+"-"+fileSize);
					setExportProcess(JSONArray.fromObject(resultmap).toString());
				}else {
					resultmap.put("state", "finished");
					resultmap.put("value", "1-1");
					setExportProcess(JSONArray.fromObject(resultmap).toString());
				}
			}
			allmap.put("state", true);
			allmap.put("msg", "日志导出成功");
		} catch (Exception e) {
			allmap.put("state", false);
			allmap.put("msg", "日志导出失败");
			e.printStackTrace();
		}
		
		String result = JSONArray.fromObject(allmap).toString();
		
		return result;
	}
	

	public String Callback() {

		return null;
    }
	
	/*public static Map<String, String> mapRemoveWithNullByRecursion(Map<String, String> map){
		Set<Entry<String, String>> set = map.entrySet();
		Iterator<Entry<String, String>> it = set.iterator();
		Map<String, Object> map2 = new HashMap<String, Object>();
		while(it.hasNext()){
			Entry<String, String> en = it.next();
			if(!(en.getValue() instanceof String){
				if(null == en.getValue() || "".equals(en.getValue())){
					it.remove();
				}
			}else{
				map2 = (Map) en.getValue();
				mapRemoveWithNullByRecursion(map2);
			}
		}
		return map;
	}*/
	
	/**
	 * DNS组合查询
	 * @param requestt
	 * @author jiyourui
	 * @return 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value="/getDNSLogListByBlend",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="DNS组合查询日志数据")
	public String getDNSLogListByBlend(HttpServletRequest request,HttpSession session) throws JsonParseException, JsonMappingException, IOException {
		// receive parameter
		Object userrole = session.getAttribute(Constant.SESSION_USERROLE);
		String hsData = request.getParameter("hsData");
		System.out.println(hsData);
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		
		map = removeMapEmptyValue(mapper.readValue(hsData, Map.class));
		System.out.println(map);
		Object pageo = map.get("page");
		Object sizeo = map.get("size");
		
		map.remove("page");
		map.remove("size");
		
		String page = pageo.toString();
		String size = sizeo.toString();
		
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
		
		ArrayList<String> arrayList = new ArrayList<>();
		List<Map<String, Object>> list =null;
		
		if (map.get("type")!=null&&!map.get("type").equals("")) {
			arrayList.add(map.get("type"));
			map.remove("type");
			String [] types = arrayList.toArray(new String[arrayList.size()]);
			if (userrole.equals("1")) {
				list = logService.getListByMap(configProperty.getEs_index(), types, starttime, endtime, map,page,size);
			}else {
				list = logService.getListByMap(configProperty.getEs_index(), types, starttime, endtime, map,session.getAttribute(Constant.SESSION_USERID).toString(),page,size);
			}
		}else {
			String[] types = {LogType.LOGTYPE_LOG4J,LogType.LOGTYPE_WINLOG,LogType.LOGTYPE_SYSLOG,LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG,LogType.LOGTYPE_UNKNOWN,LogType.LOGTYPE_MYSQLLOG,LogType.LOGTYPE_NETFLOW,LogType.LOGTYPE_DNS,LogType.LOGTYPE_DHCP};
			if (userrole.equals("1")) {
				list = logService.getListByMap(configProperty.getEs_index(), types, starttime, endtime, map,page,size);
			}else {
				list = logService.getListByMap(configProperty.getEs_index(), types, starttime, endtime, map,session.getAttribute(Constant.SESSION_USERID).toString(),page,size);
			}
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
	 * http组合查询
	 * @param requestt
	 * @author jiyourui
	 * @return 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value="/getHttpLogListByBlend",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="HTTP组合查询日志数据")
	public String getHttpLogListByBlend(HttpServletRequest request,HttpSession session) throws JsonParseException, JsonMappingException, IOException {
		// receive parameter
		Object userrole = session.getAttribute(Constant.SESSION_USERROLE);
		String hsData = request.getParameter("hsData");
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		
		map = removeMapEmptyValue(mapper.readValue(hsData, Map.class));
		System.out.println(map);
		Object pageo = map.get("page");
		Object sizeo = map.get("size");
		
		map.remove("page");
		map.remove("size");
		
		String page = pageo.toString();
		String size = sizeo.toString();
		
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
		
		ArrayList<String> arrayList = new ArrayList<>();
		List<Map<String, Object>> list =null;
		
		if (map.get("type")!=null&&!map.get("type").equals("")) {
			arrayList.add(map.get("type"));
			map.remove("type");
			String [] types = arrayList.toArray(new String[arrayList.size()]);
			if (userrole.equals("1")) {
				list = logService.getListByMap(configProperty.getEs_index(), types, starttime, endtime, map,page,size);
			}else {
				list = logService.getListByMap(configProperty.getEs_index(), types, starttime, endtime, map,session.getAttribute(Constant.SESSION_USERID).toString(),page,size);
			}
		}else {
			String[] types = {LogType.LOGTYPE_LOG4J,LogType.LOGTYPE_WINLOG,LogType.LOGTYPE_SYSLOG,LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG,LogType.LOGTYPE_UNKNOWN,LogType.LOGTYPE_MYSQLLOG,LogType.LOGTYPE_NETFLOW,LogType.LOGTYPE_DNS,LogType.LOGTYPE_DHCP};
			if (userrole.equals("1")) {
				list = logService.getListByMap(configProperty.getEs_index(), types, starttime, endtime, map,page,size);
			}else {
				list = logService.getListByMap(configProperty.getEs_index(), types, starttime, endtime, map,session.getAttribute(Constant.SESSION_USERID).toString(),page,size);
			}
		}
		Map<String, Object> allmap = new HashMap<>();
		allmap = list.get(0);
		list.remove(0);
		allmap.put("list", list);
		String result = JSONArray.fromObject(allmap).toString();
		String replace=result.replace("\\\\005", "<br/>");
		
		return replace;
	
	}
	
	public static Map<String,String> removeMapEmptyValue(Map<String,String> paramMap){
		Set<String> set = paramMap.keySet();
		Iterator<String> it = set.iterator();
		List<String> listKey = new ArrayList<String>();
		while (it.hasNext()) {
		  String str = it.next();
		  if(paramMap.get(str)==null || "".equals(paramMap.get(str))){
			  listKey.add(str) ;
		  }
		}
		for (String key : listKey) {
		    paramMap.remove(key);
		}
		return paramMap;
	}
	
	/**
	 * 组合查询日志事件
	 * @param requestt
	 * @author jiyourui
	 * @return 
	 */
	@ResponseBody
	@RequestMapping(value="/getEventListByBlend",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="组合查询日志事件")
	public String getEventListByBlend(HttpServletRequest request,HttpSession session) {
		// receive parameter
		
		String type = request.getParameter("type");
		String starttime = request.getParameter("startTime");
		String endtime = request.getParameter("endTime");
		String ip = request.getParameter("ip");
		String equipmentid = request.getParameter("equipmentid");
		String hostname = request.getParameter("hostname");
		String event_level = request.getParameter("event_level");
		String event_levels = request.getParameter("event_levels");
		String event_type = request.getParameter("event_type");
		String page = request.getParameter("page");
		String size = request.getParameter("size");
		Object userrole = session.getAttribute(Constant.SESSION_USERROLE);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("event", "event");
		if (starttime!=null&&!starttime.equals("")) {
			map.put("starttime", starttime);
		}
		if (endtime!=null&&!endtime.equals("")) {
			map.put("endtime", endtime);
		}
		if (ip!=null&&!ip.equals("")) {
			map.put("ip", ip);
		}
		if (hostname!=null&&!hostname.equals("")) {
			map.put("hostname", hostname);
		}
		if (event_level!=null&&!event_level.equals("")) {
			map.put("event_level", event_level);
		}
		if (event_levels!=null&&!event_levels.equals("")) {
			map.put("event_levels", event_levels);
		}
		if (event_type!=null&&!event_type.equals("")) {
			map.put("event_type", event_type);
		}
		if (equipmentid!=null&&!equipmentid.equals("")) {
			map.put("equipmentid", equipmentid);
		}
		
		List<Map<String, Object>> list =null;
		
		String[] types = {LogType.LOGTYPE_LOG4J,LogType.LOGTYPE_WINLOG,LogType.LOGTYPE_SYSLOG,LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG,LogType.LOGTYPE_UNKNOWN,LogType.LOGTYPE_MYSQLLOG,LogType.LOGTYPE_NETFLOW};
		if (userrole.equals("1")) {
			list = logService.getListByBlend(configProperty.getEs_index(), types, map,page,size);
		}else {
			list = logService.getListByBlend(configProperty.getEs_index(), types, map,session.getAttribute(Constant.SESSION_USERID).toString(),page,size);
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
	 * 通过事件获取日志信息(未完成)
	 * @param requestt
	 * @return
	 */
	/*@ResponseBody
	@RequestMapping(value="/getLogListByLevel",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="通过事件查询日志信息")*/
	public String getLogListByEvent(HttpServletRequest request) {
		
		// 获取动作列表(事件)
		String actions = request.getParameter("actions");
		// 时间
		
		// 资产id
		
		
		String[] types = {LogType.LOGTYPE_LOG4J,LogType.LOGTYPE_WINLOG,LogType.LOGTYPE_SYSLOG,LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG,LogType.LOGTYPE_UNKNOWN,LogType.LOGTYPE_MYSQLLOG,LogType.LOGTYPE_NETFLOW};
		
		Map<String, String> map = new HashMap<>();
		//map.put("operation_level", keyWords);
		List<Map<String, Object>> list =null;
		try {
			list = logService.getListByMap(configProperty.getEs_index(), types, map);
		} catch (Exception e) {
		}
		String result = JSONArray.fromObject(list).toString();
		String replace=result.replace("\\\\005", "<br/>");
		
		return replace;
	}
	
	/**
	 * 通过日志级别获取日志信息
	 * @param requestt
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getLogListByLevel",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="通过日志级别查询数据")
	public String getLogListByLevel(HttpServletRequest request) {
		
		String keyWords = request.getParameter("words");
		
		String[] types = {LogType.LOGTYPE_LOG4J,LogType.LOGTYPE_WINLOG,LogType.LOGTYPE_SYSLOG,LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG,LogType.LOGTYPE_UNKNOWN,LogType.LOGTYPE_MYSQLLOG,LogType.LOGTYPE_NETFLOW};
		
		Map<String, String> map = new HashMap<>();
		map.put("operation_level", keyWords);
		List<Map<String, Object>> list =null;
		try {
			list = logService.getListByMap(configProperty.getEs_index(), types, map);
		} catch (Exception e) {
		}
		String result = JSONArray.fromObject(list).toString();
		String replace=result.replace("\\\\005", "<br/>");
		
		return replace;
	}
	
	/**
	 * 获取索引数据的数量
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getIndicesCount",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取索引数据的数量")
	public String getIndicesCount(HttpServletRequest request) {
		
		
		String[] types = {LogType.LOGTYPE_LOG4J,LogType.LOGTYPE_WINLOG,LogType.LOGTYPE_SYSLOG,LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG,LogType.LOGTYPE_UNKNOWN,LogType.LOGTYPE_MYSQLLOG,LogType.LOGTYPE_NETFLOW};
		//String[] types = null;
		String equipmentid = request.getParameter("equipmentid");
		Map<String, Object> map = new HashMap<>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		// error日志条数统计
		try {
			long count = 0;
			Map<String, String> mappram = new HashMap<>();
			mappram.put("operation_level", "error");
			if (equipmentid!=null&&!equipmentid.equals("")) {
				mappram.put("equipmentid", equipmentid);
			}
			
			count = logService.getCount(configProperty.getEs_index(), types, mappram);
			map.put("indiceserror", count);
		} catch (Exception e) {
			map.put("indiceserror", "获取异常");
		}
		
		// 正常数据统计
		try {
			long count = 0;
			Map<String, String> mappram = new HashMap<>();
			if (equipmentid!=null&&!equipmentid.equals("")) {
				mappram.put("equipmentid", equipmentid);
			}/*else {
				count = logService.getCount(configProperty.getEs_index(), types,null);
			}*/
			count = logService.getCount(configProperty.getEs_index(), types,mappram);
			map.put("indices", count);
		} catch (Exception e) {
			map.put("indices", "获取异常");
		}
		list.add(map);
		String result = JSONArray.fromObject(list).toString();
		
		return result;
	}
	
	
	/**
	 * 首页获取索引中流量数据的数量
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getIndicesCountByType",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="首页获取索引中流量数据的数量")
	public String getIndicesCountByType(HttpServletRequest request) {
		
		// index中type为defaultpacket的数据量统计
		String[] types = {LogType.LOGTYPE_DEFAULTPACKET};
		//String[] types = null;
		Map<String, Object> map = new HashMap<>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		try {
			long count = 0;
			count = logService.getCount(configProperty.getEs_index(), types,null);
			map.put("indices_defaultpacket", count);
		} catch (Exception e) {
			map.put("indices_defaultpacket", "获取异常");
		}
		list.add(map);
		String result = JSONArray.fromObject(list).toString();
		
		return result;
	}
	
	/**
	 * 获取事件数据的数量
	 * @param requestt
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getEventsCount",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取事件数据的数量")
	public String getEventsCount(HttpServletRequest request) {
		
		
		String[] types = {LogType.LOGTYPE_LOG4J,LogType.LOGTYPE_WINLOG,LogType.LOGTYPE_SYSLOG,LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG,LogType.LOGTYPE_UNKNOWN,LogType.LOGTYPE_MYSQLLOG,LogType.LOGTYPE_NETFLOW};
		String equipmentid = request.getParameter("equipmentid");
		Map<String, Object> map = new HashMap<>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		try {
			Map<String, String> mappram = new HashMap<>();
			mappram.put("event", "event");
			mappram.put("event_level", "error");
			if (equipmentid!=null&&!equipmentid.equals("")) {
				mappram.put("equipmentid", equipmentid);
			}
			long count = 0;
			count = logService.getCount(configProperty.getEs_index(), types, mappram);
			map.put("eventserror", count);
		} catch (Exception e) {
			map.put("eventserror", "获取异常");
		}
		
		try {
			long count = 0;
			Map<String, String> mappram = new HashMap<>();
			mappram.put("event", "event");
			if (equipmentid!=null&&!equipmentid.equals("")) {
				mappram.put("equipmentid", equipmentid);
				count = logService.getCount(configProperty.getEs_index(), types,mappram);
			}else {
				count = logService.getCount(configProperty.getEs_index(), types,mappram);
			}
			
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
			searchmap.put("starttime", starttime+" 00:00:00");
		}
		if (endtime!=null&&!endtime.equals("")) {
			searchmap.put("endtime", endtime+" 23:59:59");
		}
		
		Map<String, List<Map<String, Object>>> map = new LinkedHashMap<String, List<Map<String, Object>>>();
		
		if (groupby!=null) {
			List<Map<String, Object>> list = logService.groupBy(index, types, groupby+".raw", searchmap);
			
			List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
			for(Entry<String, Object> key : list.get(0).entrySet()) {
				Map<String,Object> tMap = new HashMap<>();
				tMap.put("IpOrPort", key.getKey());
				tMap.put("count", key.getValue());
				tmplist.add(tMap);
			}
			map.put(groupby, tmplist);
		}else {
			for(String param:groupbys) {
				List<Map<String, Object>> list = logService.groupBy(index, types, param, searchmap);
				
				List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
				for(Entry<String, Object> key : list.get(0).entrySet()) {
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
		
		Map<String, List<Map<String, Object>>> map = new LinkedHashMap<String, List<Map<String, Object>>>();
		for(String param:groupbys) {
			if (!param.equals(groupby)) {
				List<Map<String, Object>> list = logService.groupBy(index, types, param, searchmap,10);
				
				List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
				for(Entry<String, Object> key : list.get(0).entrySet()) {
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
				List<Map<String, Object>> list1 = logService.groupBy(index, types, param, searchmap,5);
				
				List<Map<String, Object>> datalist = new LinkedList<Map<String, Object>>();
				List<Map<String, Object>> linkslist = new LinkedList<Map<String, Object>>();
				
				
				// 组织data中的数据内容中心点
				Map<String,Object> dataMap = new HashMap<>();
				dataMap.put("node", 1);
				dataMap.put("name", iporport);
				dataMap.put("count", count);
				datalist.add(dataMap);
				// 遍历第一层数据结果
				for(Entry<String, Object> key1 : list1.get(0).entrySet()) {
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
					List<Map<String, Object>> list2 = logService.groupBy(index, types, param, searchmap,5);
					// 遍历第二层数据结果
					for(Entry<String, Object> key2: list2.get(0).entrySet()) {
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
		
		Map<String, String> searchmap = new HashMap<>();
		// 设置时间段为一周
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String endtime = sdf.format(cal.getTime());
		cal.add(Calendar.DATE, -7);
		String starttime = sdf.format(cal.getTime());
		searchmap.put("starttime", starttime);
		searchmap.put("endtime", endtime);
		
		Map<String, List<Map<String, Object>>> map = new LinkedHashMap<String, List<Map<String, Object>>>();
		
		List<Map<String, Object>> datalist = new LinkedList<Map<String, Object>>();
		List<Map<String, Object>> linkslist = new LinkedList<Map<String, Object>>();
		// 临时map
		Map<String,Object> tMap = new HashMap<>();
		
		for(String param:groupbys) {

			// 第一层数据结果
			List<Map<String, Object>> list = logService.groupBy(index, types, param, searchmap,100);
			if (tMap.isEmpty()) {
				tMap = list.get(0);
			}else {
				for(Entry<String, Object> entrymap : list.get(0).entrySet()) {
					if (tMap.containsKey(entrymap.getKey())) {
						int newvalue  = Integer.parseInt(tMap.get(entrymap.getKey()).toString())+Integer.parseInt(entrymap.getValue().toString());
						tMap.put(entrymap.getKey(), newvalue);
					}else{
						tMap.put(entrymap.getKey(), entrymap.getValue());
					}
				}
			}
			
		}
		
		linkslist = logService.groupBy(index, types, groupbys, searchmap,1000);
		System.out.println("---------------------------"+linkslist.size()+"-------------------------------");
		
		// 遍历第一层数据结果
		for(Entry<String, Object> key : tMap.entrySet()) {
			// 组织data中的数据内容
			Map<String,Object> dataMap = new HashMap<>();
			dataMap.put("name", key.getKey());
			dataMap.put("count", key.getValue());
			datalist.add(dataMap);
		}
		map.put("data", datalist);
		map.put("links", linkslist);
		
		
		return JSONArray.fromObject(map).toString();
	}
	
	/**
	 * 导入历史数据
	 * @param requestt
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/importHistoricalData",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="导入历史数据")
	public String importHistoricalData(HttpServletRequest request) {
		
		String filepath = request.getParameter("filepath");
		String DEFAULT_REGEX = "^ java.|^   at";
		//日志类型
		String logType="unknown";
		Log4j log4j = null;
		Winlog winlog;
		PacketFilteringFirewal packetFilteringFirewal;
		Mysql mysql;
		Syslog syslog;
		
		//初始化：获取设备列表、map
		Map<String, Equipment> equipmentMap = equipmentService.selectAllEquipment();
				
		Set<String> ipadressSet = equipmentService.selectAllIPAdress();
				
		Map<String, String> equipmentLogType = equipmentService.selectLog_level();
				
		Set<String> eventType = alarmService.selectByEmailState();
		
		Map<String, Object> map = new HashMap<>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<IndexRequest> requests = new ArrayList<IndexRequest>();
		
		File file = new File(filepath);
		
		try{
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create(); 
			String json;
			
			//	资产、ip地址
			Equipment equipment;
			String ipadress;
			
			StringBuilder builder = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String log = null;
            
            //使用readLine方法，一次读一行
            while((log = br.readLine())!=null){
				
				// 日志过滤正则
				// log4j日志信息过滤条件
				Pattern facility_pattern = Pattern.compile("local3:");
				Matcher facility_matcher = facility_pattern.matcher(log);
				Pattern pattern = Pattern.compile(DEFAULT_REGEX);
				// 防火墙-包过滤日志信息过滤条件
				Pattern logtype_pattern = Pattern.compile("logtype=1");
				Matcher logtype_matcher = logtype_pattern.matcher(log);
				Pattern dmg_pattern = Pattern.compile("包过滤日志");
				Matcher dmg_matcher = dmg_pattern.matcher(log);
				// 防火墙-其他日志信息过滤条件
				Pattern logothertype_pattern = Pattern.compile("logtype=");
				Matcher logtotherype_matcher = logothertype_pattern.matcher(log);
				Pattern dmgother_pattern = Pattern.compile("dsp_msg=");
				Matcher dmgother_matcher = dmgother_pattern.matcher(log);
				// windows安全审计
				Pattern win2008pattern = Pattern.compile("Security-Auditing:");
				Matcher win2008matcher = win2008pattern.matcher(log);
				Pattern win2003pattern = Pattern.compile("Security:");
				Matcher win2003matcher = win2003pattern.matcher(log);
				// mysql日志
				Pattern mysqlpattern = Pattern.compile("timestamp");
				Matcher mysqlmatcher = mysqlpattern.matcher(log);
				
				if (facility_matcher.find()) {
					logType = LogType.LOGTYPE_LOG4J;
					synchronized (log) {
						String logleft = log.substring(0, log.indexOf(facility_matcher.group(0))+facility_matcher.group(0).length());
						
						Matcher m = pattern.matcher(log.replace(logleft, ""));
						//判断是否符合正则表达式 如果符合，表明这是一条开始数据
						if(m.find()) {
							log = log.replace(logleft, "");
							//添加数据
							builder.append(" \\005 "+log);
						}else {
							//添加builder
							if (builder.length()!=0) {
								//进入范式化
								try {
									log4j = new Log4j(builder.toString());
								} catch (Exception e) {
									continue;
								}
								ipadress = log4j.getIp();
								//判断是否在资产ip地址池里
								if(ipadressSet.contains(ipadress)){
									//判断是否在已识别资产里————日志类型可识别
									equipment=equipmentMap.get(log4j.getIp() +logType);
									if(equipment!=null){
										log4j.setUserid(equipment.getUserId());
										log4j.setDeptid(String.valueOf(equipment.getDepartmentId()));
										log4j.setEquipmentname(equipment.getName());
										log4j.setEquipmentid(equipment.getId());
										json = gson.toJson(log4j);
										requests.add(clientTemplate.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_LOG4J, json));
									}else{
										log4j.setUserid(LogType.LOGTYPE_UNKNOWN);
										log4j.setDeptid(LogType.LOGTYPE_UNKNOWN);
										log4j.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
										log4j.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
										json = gson.toJson(log4j);
										requests.add(clientTemplate.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_LOG4J, json));
									}
								}else{
									//不在资产ip池里，暂不处理
									//TODO
								}

								//清空数据
								builder.delete(0, builder.length());
							}
							builder.append(log);
							
						}
					}
				}else if (logtype_matcher.find()&&dmg_matcher.find()) {
					logType = LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG;
					try {
						packetFilteringFirewal = new PacketFilteringFirewal(log);
					} catch (Exception e) {
						continue;
					}
					ipadress = packetFilteringFirewal.getIp();
					if (ipadressSet.contains(ipadress)) {
						equipment=equipmentMap.get(packetFilteringFirewal.getIp() +logType);
						if (equipment!=null) {
							packetFilteringFirewal.setUserid(equipment.getUserId());
							packetFilteringFirewal.setDeptid(String.valueOf(equipment.getDepartmentId()));
							packetFilteringFirewal.setEquipmentid(equipment.getId());
							packetFilteringFirewal.setEquipmentname(equipment.getName());
							json = gson.toJson(packetFilteringFirewal);
							requests.add(clientTemplate.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG, json));
						}else {
							packetFilteringFirewal.setUserid(LogType.LOGTYPE_UNKNOWN);
							packetFilteringFirewal.setDeptid(LogType.LOGTYPE_UNKNOWN);
							packetFilteringFirewal.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
							packetFilteringFirewal.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
							json = gson.toJson(packetFilteringFirewal);
							requests.add(clientTemplate.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG, json));
						}
					}else {
						//不在资产ip池里，暂不处理
					}
					//es暂无防火墙包过滤日志对应的mapping，暂未入库es
				}else if(logtotherype_matcher.find()&&dmgother_matcher.find()){
					//防火墙、不包括包过滤日志，暂不处理
					System.out.println("-------不做处理-------------");
				}/*else if (mysqlmatcher.find()) {
					logType = LogType.LOGTYPE_MYSQLLOG;
					mysql = new Mysql(log);
					ipadress = mysql.getIp();
					if (ipadressSet.contains(ipadress)) {
						equipment=equipmentMap.get(mysql.getIp() +logType);
						if (equipment!=null) {
							mysql.setUserid(equipment.getUserId());
							mysql.setDeptid(String.valueOf(equipment.getDepartmentId()));
							mysql.setEquipmentname(equipment.getName());
							mysql.setEquipmentid(equipment.getId());
							json = gson.toJson(mysql);
							requests.add(clientTemplate.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_MYSQLLOG, json));
						}else {
							mysql.setUserid(LogType.LOGTYPE_UNKNOWN);
							mysql.setDeptid(String.valueOf(LogType.LOGTYPE_UNKNOWN));
							mysql.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
							mysql.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
							json = gson.toJson(mysql);
							requests.add(clientTemplate.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_MYSQLLOG, json));
						}
					}else {
						//不在资产ip池里，暂不处理
					}
				}*/
				else if(win2003matcher.find()||win2008matcher.find()){
					//windows、evtsys组件收集日志
					logType = LogType.LOGTYPE_WINLOG;
					try {
						winlog = new Winlog(log);
					} catch (Exception e) {
						continue;
					}
					ipadress = winlog.getIp();
					//判断是否在资产ip地址池里
					if(ipadressSet.contains(ipadress)){
						//判断是否在已识别资产里————日志类型可识别
						equipment=equipmentMap.get(winlog.getIp() +logType);
						if(equipment != null){
							if (equipmentLogType.get(equipment.getId()).indexOf(winlog.getOperation_level().toLowerCase())!=-1) {
								winlog.setUserid(equipment.getUserId());
								winlog.setDeptid(String.valueOf(equipment.getDepartmentId()));
								winlog.setEquipmentname(equipment.getName());
								winlog.setEquipmentid(equipment.getId());
								json = gson.toJson(winlog);
								requests.add(clientTemplate.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_WINLOG, json));
							}
						}else{
							winlog.setUserid(LogType.LOGTYPE_UNKNOWN);
							winlog.setDeptid(LogType.LOGTYPE_UNKNOWN);
							winlog.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
							winlog.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
							json = gson.toJson(winlog);
							requests.add(clientTemplate.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_WINLOG, json));
						}
					}else{
						//不在资产ip池里，暂不处理
					}
				}else {
					logType = LogType.LOGTYPE_SYSLOG;
					try {
						syslog = new Syslog(log);
					} catch (Exception e) {
						continue;
					}
					ipadress = syslog.getIp();
					//判断是否在资产ip地址池里
					if(ipadressSet.contains(ipadress)){
						//判断是否在已识别资产里————日志类型可识别
						equipment = equipmentMap.get(syslog.getIp() +logType);
						if(null != equipment){
							if (equipmentLogType.get(equipment.getId()).indexOf(syslog.getOperation_level().toLowerCase())!=-1) {
								syslog.setUserid(equipment.getUserId());
								syslog.setDeptid(String.valueOf(equipment.getDepartmentId()));
								syslog.setEquipmentid(equipment.getId());
								syslog.setEquipmentname(equipment.getName());
								if (eventType.contains(syslog.getEvent_type())) {
									Sendmail sendmail = new Sendmail(syslog.getIp(), syslog.getEquipmentname(), syslog.getEvent_des(), usersService.selectById(syslog.getUserid()).getEmail());
								}
								json = gson.toJson(syslog);
								requests.add(clientTemplate.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_SYSLOG, json));
							}
						}else{
							//在资产ip地址池里，但是无法识别日志类型
							syslog.setUserid(LogType.LOGTYPE_UNKNOWN);
							syslog.setDeptid(LogType.LOGTYPE_UNKNOWN);
							syslog.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
							syslog.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
							json = gson.toJson(syslog);
							requests.add(clientTemplate.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_UNKNOWN, json));
						}
					}else{
						//不在资产ip池里，暂不处理
					}
				}
				// 根据配置参数进行批量提交
				if (requests.size()==configProperty.getEs_bulk()) {
					clientTemplate.bulk(requests);
					requests.clear();
				}
            }
            br.close();
            map.put("result", "导入历史数据成功！");
        }catch(Exception e){
        	requests.clear();
            map.put("result", "导入历史数据失败！");
            e.printStackTrace();
        }finally {
			if (requests.size()>0) {
				clientTemplate.bulk(requests);
			}
		}
		
		list.add(map);
		String result = JSONArray.fromObject(list).toString();
		
		return result;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

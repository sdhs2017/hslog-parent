package com.jz.bigdata.business.logAnalysis.log.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gson.*;
import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.jz.bigdata.business.logAnalysis.log.entity.*;
import com.jz.bigdata.roleauthority.user.service.IUserService;
import com.jz.bigdata.business.logAnalysis.log.mappingbean.MappingOfFilebeat;
import com.jz.bigdata.business.logAnalysis.log.mappingbean.MappingOfFirewalls;
import com.jz.bigdata.business.logAnalysis.log.mappingbean.MappingOfNet;
import com.jz.bigdata.business.logAnalysis.log.mappingbean.MappingOfSyslog;
import com.jz.bigdata.util.*;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.indices.IndexTemplateMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jz.bigdata.business.logAnalysis.log.LogType;
import com.jz.bigdata.business.logAnalysis.log.service.IlogService;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.alarm.service.IAlarmService;
import com.jz.bigdata.common.equipment.EquipmentConstant;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.equipment.service.IEquipmentService;
import com.jz.bigdata.common.safeStrategy.entity.SafeStrategy;
import com.jz.bigdata.common.safeStrategy.service.ISafeStrategyService;


import net.sf.json.JSONArray;

@Controller
@RequestMapping("/log")
public class LogController extends BaseController{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

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

	@Resource(name ="UserService")
	private IUserService usersService;

	//@Autowired protected ClientTemplate clientTemplate;
	@Autowired protected ILogCrudDao logCrudDao;

	//FileUtils.readFileToString(auditTemplate.getFile(),"UTF-8")获取内容
	@Value(value="classpath:template/auditBeatTemplate.json")
	private org.springframework.core.io.Resource auditTemplate;
	@Resource(name ="BeatTemplate")
	private BeatTemplate beatTemplate;


	private String exportProcess = "[{\"state\":\"finished\",\"value\":\"1-1\"}]";

	//默认查询types
	String[] default_types = {LogType.LOGTYPE_LOG4J,LogType.LOGTYPE_WINLOG,LogType.LOGTYPE_SYSLOG,LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG,LogType.LOGTYPE_UNKNOWN,LogType.LOGTYPE_MYSQLLOG,LogType.LOGTYPE_NETFLOW};


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
		String index = request.getParameter("index");
		String type = request.getParameter("type");
		String id = request.getParameter("id");
		String result = null;
		try {
			result = logService.searchById(index, type, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);
		List<Map<String, Object>> list = MapUtil.json2ListMap(hsData);
		String result ="false";
		for (Map<String, Object> map : list) {
			try {
				result = logService.deleteById(map.get("index").toString(), map.get("type").toString(), map.get("id").toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/*String type = request.getParameter("type");
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

		}*/


		return result;
	}

	@ResponseBody
	@RequestMapping(value="/createIndexAndMapping",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="初始化数据结构")
	public String createIndexAndMapping(HttpServletRequest request) {
		Map<String, Object> map= new HashMap<>();

		try {

			/**
			 * 初始化工作一：elasticsearch7版本创建template，elasticsearch5版本创建当天index
			 */
			Map<String, Object> settingmap = new HashMap<>();
			settingmap.put("index.max_result_window", configProperty.getEs_max_result_window());
			settingmap.put("index.number_of_shards", configProperty.getEs_number_of_shards());
			settingmap.put("index.number_of_replicas", configProperty.getEs_number_of_replicas());
			settingmap.put("index.lifecycle.name", "hs_policy");
			// elasticsearch7 版本初始化template
			logService.initOfElasticsearch(configProperty.getEs_templatename(),"hslog_syslog*",null,settingmap,new MappingOfSyslog().toMapping());
			logService.initOfElasticsearch(configProperty.getEs_templatename(),"hslog_packet*",null,settingmap,new MappingOfNet().toMapping());

			// 初始化当天的index
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String index = configProperty.getEs_index().replace("*",format.format(new Date()));

			logService.initOfElasticsearch(index,null,LogType.LOGTYPE_SYSLOG,settingmap,new Syslog().toMapping());
			logService.initOfElasticsearch(index,null,LogType.LOGTYPE_WINLOG,settingmap,new Winlog().toMapping());
			logService.initOfElasticsearch(index,null,LogType.LOGTYPE_LOG4J,settingmap,new Log4j().toMapping());
			logService.initOfElasticsearch(index,null,LogType.LOGTYPE_MYSQLLOG,settingmap,new Mysql().toMapping());
			logService.initOfElasticsearch(index,null,LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG,settingmap,new PacketFilteringFirewal().toMapping());
			logService.initOfElasticsearch(index,null,LogType.LOGTYPE_NETFLOW,settingmap,new Netflow().toMapping());
			logService.initOfElasticsearch(index,null,LogType.LOGTYPE_DNS,settingmap,new DNS().toMapping());
			logService.initOfElasticsearch(index,null,LogType.LOGTYPE_DHCP,settingmap,new DHCP().toMapping());
			logService.initOfElasticsearch(index,null,LogType.LOGTYPE_APP_FILE,settingmap,new App_file().toMapping());
			logService.initOfElasticsearch(index,null,LogType.LOGTYPE_APP_APACHE,settingmap,new App_file().toMapping());
			logService.initOfElasticsearch(index,null,LogType.LOGTYPE_UNKNOWN,settingmap,new Unknown().toMapping());
			logService.initOfElasticsearch(index,null,LogType.LOGTYPE_DEFAULTPACKET,settingmap,new DefaultPacket().toMapping());


			// 更新index的settings属性
			Map<String, Object> setting = new HashMap<>();
			setting.put("index.max_result_window", configProperty.getEs_max_result_window());
			setting.put("index.number_of_replicas", configProperty.getEs_number_of_replicas());
			logService.updateSettings(index, setting);
			/**
			 * 初始化工作二：在初始化过程中增加备份仓库的建立，节省在安装过程中实施人员的curl命令操作
			 */
			try {
				// 当备份仓库没有建立的情况下，通过名称查询会报missing错误
				List<Map<String, Object>> repositories = logService.getRepositoriesInfo(configProperty.getEs_repository_name());

				if(repositories.isEmpty()) {
					Boolean result = logService.createRepositories(configProperty.getEs_repository_name(), configProperty.getEs_repository_path());
					if (!result) {
						map.put("state", true);
						map.put("msg", "备份仓库初始化失败！");
						return JSONArray.fromObject(map).toString();
					}
				}
			} catch (Exception e) {
				Boolean result = logService.createRepositories(configProperty.getEs_repository_name(), configProperty.getEs_repository_path());
				if (!result) {
					map.put("state", false);
					map.put("msg", "备份仓库初始化失败！");
					return JSONArray.fromObject(map).toString();
				}
			}

			/**
			 * 初始化工作三：在初始化过程中创建index的生命周期
			 */
			try {
				Boolean LifeCycleResult = logService.createLifeCycle("hs_policy",Long.parseLong(configProperty.getEs_days_of_log_storage()));
				if (!LifeCycleResult){
					map.put("state", false);
					map.put("msg", "创建index生命周期失败！");
					return JSONArray.fromObject(map).toString();
				}
			} catch (Exception e){
				e.printStackTrace();
				logger.error("创建index生命周期报错："+e.getMessage());

			}

			/**
			 * 初始化工作四：在初始化过程中创建完index的生命周期后开启生命周期管理
			 */
			try {
				String status = logService.getLifecycleManagementStatus();
				if (status.equals("STOPPED")||status.equals("STOPPING")){
					Boolean startIndexLifeCyclestatus = logService.startIndexLifeCycle();
					if (!startIndexLifeCyclestatus){
						map.put("state", false);
						map.put("msg", "开启index生命周期管理失败！");
						return JSONArray.fromObject(map).toString();
					}
				}
			} catch (Exception e){
				e.printStackTrace();
				map.put("state", false);
				map.put("msg", "开启index生命周期管理失败！");
				return JSONArray.fromObject(map).toString();
			}

			map.put("state", true);
			map.put("msg", "初始化成功！");
			return JSONArray.fromObject(map).toString();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			map.put("state", false);
			map.put("msg", "数据结构初始化失败！");
			return JSONArray.fromObject(map).toString();
		}

	}
	@ResponseBody
	@RequestMapping(value="/createIndexAndMapping4Beats",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="初始化数据结构")
	public String createIndexAndMapping4Beats(HttpServletRequest request){
		Map<String, Object> map= new HashMap<>();
		try {

			/**
			 * 初始化工作一：elasticsearch7版本创建template，elasticsearch5版本创建当天index
			 */
			Map<String, Object> settingmap = new HashMap<>();
			settingmap.put("index.max_result_window", configProperty.getEs_max_result_window());
			settingmap.put("index.number_of_shards", configProperty.getEs_number_of_shards());
			settingmap.put("index.number_of_replicas", configProperty.getEs_number_of_replicas());
			settingmap.put("index.lifecycle.name", "hs_policy");
			settingmap.put("mapping.total_fields.limit", "5000");//默认1000，数据字段过多，需要调整配置
			// elasticsearch7 版本初始化template
			logService.initOfElasticsearch(configProperty.getEs_templatename(),"hslog_syslog*",null,settingmap,new MappingOfSyslog().toMapping());
			logService.initOfElasticsearch(configProperty.getEs_templatename(),"hslog_packet*",null,settingmap,new MappingOfNet().toMapping());

			//初始化beat template
			logService.initOfElasticsearch("auditbeat-7.6.1","auditbeat-7.6.1*",null,settingmap,beatTemplate.getAuditBeatTemplate());
			logService.initOfElasticsearch("winlogbeat-7.6.0","winlogbeat-7.6.0*",null,settingmap,beatTemplate.getWinlogBeatTemplate());
			logService.initOfElasticsearch("packetbeat-7.6.1","packetbeat-7.6.1*",null,settingmap,beatTemplate.getPacketBeatTemplate());
			logService.initOfElasticsearch("filebeat-7.6.1","filebeat-7.6.1*",null,settingmap,beatTemplate.getFileBeatTemplate());
			logService.initOfElasticsearch("metricbeat-7.6.0","metricbeat-7.6.0*",null,settingmap,beatTemplate.getMetricBeatTemplate());

			//初始化cache4BI

			/**
			 * 初始化工作二：在初始化过程中增加备份仓库的建立，节省在安装过程中实施人员的curl命令操作
			 */
			try {
				// 当备份仓库没有建立的情况下，通过名称查询会报missing错误
				List<Map<String, Object>> repositories = logService.getRepositoriesInfo(configProperty.getEs_repository_name());

				if(repositories.isEmpty()) {
					Boolean result = logService.createRepositories(configProperty.getEs_repository_name(), configProperty.getEs_repository_path());
					if (!result) {
						map.put("state", true);
						map.put("msg", "备份仓库初始化失败！");
						return JSONArray.fromObject(map).toString();
					}
				}
			} catch (Exception e) {
				Boolean result = logService.createRepositories(configProperty.getEs_repository_name(), configProperty.getEs_repository_path());
				if (!result) {
					map.put("state", false);
					map.put("msg", "备份仓库初始化失败！");
					return JSONArray.fromObject(map).toString();
				}
			}

			/**
			 * 初始化工作三：在初始化过程中创建index的生命周期
			 */
			try {
				Boolean LifeCycleResult = logService.createLifeCycle("hs_policy",Long.parseLong(configProperty.getEs_days_of_log_storage()));
				if (!LifeCycleResult){
					map.put("state", false);
					map.put("msg", "创建index生命周期失败！");
					return JSONArray.fromObject(map).toString();
				}
			} catch (Exception e){
				e.printStackTrace();
				logger.error("创建index生命周期报错："+e.getMessage());

			}

			/**
			 * 初始化工作四：在初始化过程中创建完index的生命周期后开启生命周期管理
			 */
			try {
				String status = logService.getLifecycleManagementStatus();
				if (status.equals("STOPPED")||status.equals("STOPPING")){
					Boolean startIndexLifeCyclestatus = logService.startIndexLifeCycle();
					if (!startIndexLifeCyclestatus){
						map.put("state", false);
						map.put("msg", "开启index生命周期管理失败！");
						return JSONArray.fromObject(map).toString();
					}
				}
			} catch (Exception e){
				e.printStackTrace();
				map.put("state", false);
				map.put("msg", "开启index生命周期管理失败！");
				return JSONArray.fromObject(map).toString();
			}

			map.put("state", true);
			map.put("msg", "初始化成功！");
			return JSONArray.fromObject(map).toString();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			map.put("state", false);
			map.put("msg", "数据结构初始化失败！");
			return JSONArray.fromObject(map).toString();
		}
	}
	/**
	 * 获取template信息，暂时只支持一个template的查询
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getTemplateInfo")
	@DescribeLog(describe="获取template信息")
	public String getTemplateInfo(HttpServletRequest request){
		String templateName = request.getParameter("templatename");

		try{
			List<IndexTemplateMetaData> data = logService.getTemplate(templateName);
			IndexTemplateMetaData tmpData = data.get(0);
			Map<String,Object> resultMap = new HashMap<>();
			resultMap.put("name",tmpData.name());
			resultMap.put("mappings",tmpData.mappings().getSourceAsMap());
			return JSONObject.fromObject(resultMap).toString();
		}catch(Exception e){
			e.printStackTrace();
			return Constant.failureMessage("查询失败");
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

		List<Map<String, Object>> list = null;
		try {
			list = logService.index(index, type);
		} catch (Exception e) {
			e.printStackTrace();
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
		String type = request.getParameter("type");
		String [] types = null;
		if (!type.equals("")) {
			types = type.split(",");
		}
		String param = request.getParameter("param");
		String time = request.getParameter("time");
		String equipmentid = request.getParameter("equipmentid");
		String starttime = request.getParameter("starttime");
		String endtime = request.getParameter("endtime");

		ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
		if (equipmentid!=null&&!equipmentid.equals("")) {
			map.put("equipmentid", equipmentid);
		}
		if (time!=null&&!time.equals("")) {
			map.put("logdate", time);
		}
		if (starttime!=null&&!starttime.equals("")) {
			starttime = starttime+" 00:00:00";
		}
		if (endtime!=null&&!endtime.equals("")) {
			endtime = endtime+" 23:59:59";
		}

		// 聚合返回的数据条数，在目前的产品中日志级别总共有8个，设置为10个保证8个正常显示
		int size = 10;
		List<Map<String, Object>> list = null;
		try {
			list = logService.groupBy(index, types, param,size, starttime, endtime, map);
		} catch (Exception e) {
			e.printStackTrace();
		}

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

		List<Map<String, Object>> list = null;
		try {
			list = logService.getEventListGroupByEventType(index, types, dates, equipmentid, groupby);
		} catch (Exception e) {
			e.printStackTrace();
		}

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
	public List<List<Map<String, Object>>> getCountGroupByEventType(HttpServletRequest request) {
		String index = configProperty.getEs_index();
		String type = request.getParameter("type");
		String [] types = null;
		if (!type.equals("")) {
			types = type.split(",");
		}
		List<List<Map<String, Object>>> list = new ArrayList<>();
		String date = request.getParameter("param");
		String equipmentid = request.getParameter("equipmentid");
		String [] hours = {"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
		// 0全部，1高危，2中等，3普通
		for(int i=0;i<4;i++) {
			List<Map<String, Object>> list1 = null;
			try {
				list1 = logService.getEventListGroupByTime(index, types, date,equipmentid,"event_type",i);
			} catch (Exception e) {
				e.printStackTrace();
			}
			/*Map<String, Object> map = new HashMap<>();
			for(String hour : hours) {
				map.put(hour, list1.get(0).get(hour)!=null?list1.get(0).get(hour):0);
			}*/
			list.add(list1);
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
		//String [] hours = {"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
		List<Map<String, Object>> list = null;
		try {
			list = logService.getListGroupByTime(index, types, param,equipmentid);
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*for (Map<String, Object> map : list){

		}*/
		/*Map<String, Object> map = new HashMap<>();
		for(String hour : hours) {
			map.put(hour, list.get(0).get(hour)!=null?list.get(0).get(hour):0);
		}
		list.clear();
		list.add(map);*/
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
			ConcurrentHashMap<String,String> safemap = new ConcurrentHashMap<>();
			safemap.put("equipmentid",equipmentid);
			safemap.put("event_type",event_type);
			//List<Map<String, Object>> loglist = logService.getListGroupByEvent(index, types, equipmentid,event_type,starttime,endtime);
			List<Map<String, Object>> loglist = null;
			try {
				loglist = logService.groupBy(index, types,event_type, 10,starttime, endtime,safemap);
			} catch (Exception e) {
				e.printStackTrace();
			}

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
	 *
	 * @param request
	 * @return
	 *//*
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
	}*/



	/**
	 * 通过设备id获取该设备日志列表
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value="/getLogListByEquipment.do", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="条件获取设备日志数据")
	public String getLogListByEquipment(HttpServletRequest request,Equipment equipment) {

		String ztData = request.getParameter(ContextFront.DATA_CONDITIONS);
		if(ztData!=null) {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> map = new ConcurrentHashMap<>();

			try {
				map = MapUtil.removeMapEmptyValue(mapper.readValue(ztData, Map.class));
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 查询设备信息
			equipment.setId(map.get("id"));
			equipment=equipmentService.selectOneEquipment(equipment);

			String starttime = "";
			String endtime = "";
			// 判断查询条件，开始时间和结束时间不为null，添加到查询条件中，默认结束时间为当前时间
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

			// 获取资产的日志类型
			String logtype = equipment.getLogType();
			// 获取资产id
			String equipmentId = equipment.getId();

			map.put("equipmentid", equipmentId);
			map.remove("id");
			ArrayList<String> arrayList = new ArrayList<>();

			// 历史遗留，新的流量日志不会和资产绑定，该判断无效
			if (logtype.equals("netflow")) {
				arrayList.add("defaultpacket");
			}
			String [] types = null;
			// 判断资产日志类型
			if (logtype!=null) {
				types = arrayList.toArray(new String[arrayList.size()]);
			}

			List<Map<String, Object>> list = null;
			try {
				list = logService.getListByMap(map,  starttime, endtime, page, size, types, configProperty.getEs_index());
			} catch (Exception e) {
				e.printStackTrace();
			}

			Map<String, Object> allmap = new HashMap<>();
			allmap = list.get(0);
			list.remove(0);
			allmap.put("list", list);
			String result = JSONArray.fromObject(allmap).toString();

			String replace=result.replace("\\\\005", "<br/>");
			/*System.out.println("---------------result----------------");
			System.err.println(result);*/
			logger.info("查询资产的日志内容");
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
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getLogListByContent",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="查询日志数据")
	public String getLogListByContent(HttpServletRequest request,HttpSession session) {

		String ztData = request.getParameter(ContextFront.DATA_CONDITIONS);
		Object userrole = session.getAttribute(Constant.SESSION_USERROLE);

		Map<String, String> mapper = MapUtil.json2map(ztData);
		Object wordso = mapper.get("words");
		Object pageo = mapper.get("page");
		Object sizeo = mapper.get("size");
		String keyWords = null;
		// 全文检索条件：关键字
		if (wordso!=null) {
			keyWords = wordso.toString();
		}

		String page = pageo.toString();
		String size = sizeo.toString();

		//String[] types = {LogType.LOGTYPE_LOG4J,LogType.LOGTYPE_WINLOG,LogType.LOGTYPE_SYSLOG,LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG,LogType.LOGTYPE_UNKNOWN,LogType.LOGTYPE_MYSQLLOG,LogType.LOGTYPE_NETFLOW};
		String[] types = {LogType.LOGTYPE_LOG4J,LogType.LOGTYPE_WINLOG,LogType.LOGTYPE_SYSLOG,LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG,LogType.LOGTYPE_UNKNOWN,LogType.LOGTYPE_MYSQLLOG,LogType.LOGTYPE_NETFLOW};

		Map<String, Object> map = new HashMap<>();
		List<Map<String, Object>> list = new ArrayList<>();

		try {
			// 判断是否是管理员角色，是传入null，否传入用户id
			if (userrole.equals(ContextRoles.MANAGEMENT)) {
				list = logService.getListByContent(keyWords,null,page,size,types,configProperty.getEs_index());
			}else {
				list = logService.getListByContent(keyWords,session.getAttribute(Constant.SESSION_USERID).toString(),page,size,types,configProperty.getEs_index());
			}
			if (list.size()>0){
				map = list.get(0);
				list.remove(0);
				map.put("list", list);
			}
			map.put("state", true);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			map.put("state", false);
		}

		String result = JSONArray.fromObject(map).toString();
//		System.out.println("result ="+result);
		String replace=result.replace("\\\\005", "<br/>");
//		System.out.println("---------------result----------------");

		return replace;
	}

	/**
	 * 组合查询
	 * @param request
	 * @author jiyourui
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getLogListByBlend",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="组合查询日志数据")
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
			map.put("userid",session.getAttribute(Constant.SESSION_USERID).toString());
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



		ArrayList<String> arrayList = new ArrayList<>();
		List<Map<String, Object>> list =null;

		// 判断type是否存在，如果存在使用type值，否则使用默认
		if (map.get("type")!=null&&!map.get("type").equals("")) {
			arrayList.add(map.get("type"));
			map.remove("type");
			String [] types = arrayList.toArray(new String[arrayList.size()]);
			try {
				list = logService.getLogListByBlend(map, starttime, endtime, page, size, types, configProperty.getEs_index());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			try {
				list = logService.getLogListByBlend(map, starttime, endtime, page, size, default_types, configProperty.getEs_index());
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
	}

	/**
	 * 组合查询
	 * @param request
	 * @author jiyourui
	 * @return
	 */
	/*@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value="/getLogListByFlow",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="业务流分析-深钻-日志内容")
	public String getLogListByFlow(HttpServletRequest request,HttpSession session) {
		// receive parameter
		Object userrole = session.getAttribute(Constant.SESSION_USERROLE);
		String ztData = request.getParameter(ContextFront.DATA_CONDITIONS);

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
			if (userrole.equals(ContextRoles.MANAGEMENT)) {
				list = logService.getListByMultiField(configProperty.getEs_index(), types, param,map,page,size);
			}else {
				//list = logService.getListByBlend(configProperty.getEs_index(), types, map,session.getAttribute(Constant.SESSION_USERID).toString(),page,size);
                list = logService.getLogListByBlend(map, null, null, page, size, types, configProperty.getEs_index());
			}
		}else {
			String[] types = {LogType.LOGTYPE_LOG4J,LogType.LOGTYPE_WINLOG,LogType.LOGTYPE_SYSLOG,LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG,LogType.LOGTYPE_UNKNOWN,LogType.LOGTYPE_MYSQLLOG,LogType.LOGTYPE_NETFLOW};
			if (userrole.equals(ContextRoles.MANAGEMENT)) {
				list = logService.getListByBlend(configProperty.getEs_index(), types, map,page,size);
			}else {
				//list = logService.getListByBlend(configProperty.getEs_index(), types, map,session.getAttribute(Constant.SESSION_USERID).toString(),page,size);
                list = logService.getLogListByBlend(map, null, null, page, size, types, configProperty.getEs_index());
			}
		}
		Map<String, Object> allmap = new HashMap<>();
		allmap = list.get(0);
		list.remove(0);
		allmap.put("list", list);
		String result = JSONArray.fromObject(allmap).toString();
		String replace=result.replace("\\\\005", "<br/>");

		return replace;
	}*/


	/**
	 * 查询+导出至文件，放在服务器指定目录
	 * @param request
	 * @author jiyourui
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value="/exportLogList",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="导出查询的日志数据")
	public String exportLogList(HttpServletRequest request,HttpSession session) {

		Object userrole = session.getAttribute(Constant.SESSION_USERROLE);
		// 使用手机号作为导出的路径
		Object userphone = session.getAttribute(Constant.SESSION_USERACCOUNT);
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd'_'HH:mm:ss");

		String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);
		// 返回結果
		Map<String, Object> allmap= new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		// 使用线程安全的map
		Map<String, String> map = new ConcurrentHashMap<>();
		try {
			map = MapUtil.removeMapEmptyValue(mapper.readValue(hsData, Map.class));
		} catch (IOException e) {
			e.printStackTrace();
		}


		Object pageo = map.get("page");
		Object sizeo = map.get("size");
		Object exportSizeo = map.get("exportSize");

		// 管理员角色为1，判断是否是管理员角色，如果是不需要补充条件，如果不是添加用户id条件，获取该用户权限下的数据
		if (!userrole.equals(ContextRoles.MANAGEMENT)) {
			map.put("userid",session.getAttribute(Constant.SESSION_USERID).toString());
		}

		String starttime = null;
		// 判断时间条件是否存在，存在将时间提出
		if (map.get("starttime")!=null&&!map.get("starttime").equals("")) {
			starttime = map.get("starttime");
			map.remove("starttime");

		}
		String endtime = null;
		if (map.get("endtime")!=null&&!map.get("endtime").equals("")){
			endtime = map.get("endtime");
			map.remove("endtime");
		}


		map.remove("page");
		map.remove("size");
		map.remove("exportSize");

		String exportSize = exportSizeo.toString();


		Integer sizeInt = Integer.valueOf(exportSize);

		//csv文件单个标签页存放数据条数
		int sheetsizes = 10000;

		// 获得需要创建的csv文件格式
		int forsize = sizeInt/sheetsizes;
		// 取余，获得需要导出的不满10000条数据
		int modsize = sizeInt%sheetsizes;

		// 导出状态默认值
		int fileSize = 0;

		Map<String, Object> resultmap = new HashMap<>();
		// 根据文件数设置导出状态
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
			// 先到处每个sheet页10000条的数据
			for(int i=1;i<=forsize;i++) {
				// 构建新的page size ，size为10000条
				String page = String.valueOf(i);
				String size = String.valueOf(sheetsizes);

				ArrayList<String> arrayList = new ArrayList<>();
				List<Map<String, Object>> list = null;
				// 判断type是否存在，如果存在使用type值，否则使用默认
				if (map.get("type")!=null&&!map.get("type").equals("")) {
					arrayList.add(map.get("type"));
					String [] types = arrayList.toArray(new String[arrayList.size()]);
					list = logService.getLogListByBlend(map, starttime, endtime, page, size, types, configProperty.getEs_index());
				}else {
					list = logService.getLogListByBlend(map, starttime, endtime, page, size, default_types, configProperty.getEs_index());

				}

				// 设置表格头
				Object[] head = {"时间", "日志类型", "日志级别", "资产名称", "资产IP", "日志内容"};
				List<Object> headList = Arrays.asList(head);
				Date date = new Date();
				// 过滤第一条，第一条数据为总数统计
				list.remove(0);
				CSVUtil.createCSVFile(headList, list, "/home"+File.separator+"exportfile"+File.separator+userphone+File.separator+dateformat.format(date), "exportlog"+timeformat.format(date),null);
				//CSVUtil.createCSVFile(headList, list, "D:\\"+File.separator+"exportfile"+File.separator+username+File.separator+dateformat.format(date), "exportlog"+timeformat.format(date),null);

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
			// 导出不足10000条的剩余数据
			if (modsize>0) {
				// 构建新的page size ，size为modsize
				String page = String.valueOf(forsize+1);
				String size = String.valueOf(modsize);

				ArrayList<String> arrayList = new ArrayList<>();
				List<Map<String, Object>> list = null;

				// 判断type是否存在，如果存在使用type值，否则使用默认
				if (map.get("type")!=null&&!map.get("type").equals("")) {
					arrayList.add(map.get("type"));
					String [] types = arrayList.toArray(new String[arrayList.size()]);
					list = logService.getLogListByBlend(map, starttime, endtime, page, size, types, configProperty.getEs_index());
				}else {
					list = logService.getLogListByBlend(map, starttime, endtime, page, size, default_types, configProperty.getEs_index());
				}

				// 设置表格头
				Object[] head = {"时间", "日志类型", "日志级别", "资产名称", "资产IP", "日志内容" };
				List<Object> headList = Arrays.asList(head);

				Date date = new Date();
				// 过滤第一条，第一条数据为总数统计
				list.remove(0);
				// 开始写入csv文件
				CSVUtil.createCSVFile(headList, list, "/home"+File.separator+"exportfile"+File.separator+userphone+File.separator+dateformat.format(date), "exportlog"+timeformat.format(date),null);
				//CSVUtil.createCSVFile(headList, list, "D:\\"+File.separator+"exportfile"+File.separator+username+File.separator+dateformat.format(date), "exportlog"+timeformat.format(date),null);
				//  根据导出文件个数返回导出状态
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
	 * @param request
	 * @author jiyourui
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value="/getDNSLogListByBlend",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="DNS组合查询日志数据")
	public String getDNSLogListByBlend(HttpServletRequest request,HttpSession session) {
		// receive parameter
		Object userrole = session.getAttribute(Constant.SESSION_USERROLE);
		String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);
		//System.out.println(hsData);
		//hsData的参数说明{日志类型：type=dns, starttime=, endtime=, dns客户端ip：dns_clientip=, dns_view=, dns域名：dns_domain_name=, dns解析数据类型：dns_ana_type=, dns服务器：dns_server=, page=1, size=12}

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
		if (!userrole.equals(ContextRoles.MANAGEMENT)) {
			map.put("userid",session.getAttribute(Constant.SESSION_USERID).toString());
		}

		ArrayList<String> arrayList = new ArrayList<>();
		List<Map<String, Object>> list =null;

		// 判断type是否存在，如果存在使用type值，否则使用默认
		if (map.get("type")!=null&&!map.get("type").equals("")) {
			arrayList.add(map.get("type"));
			map.remove("type");
			String [] types = arrayList.toArray(new String[arrayList.size()]);
			try {
				list = logService.getListByMap(map,  starttime, endtime, page, size, types, configProperty.getEs_index());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			String[] types = {LogType.LOGTYPE_DNS};
			try {
				list = logService.getListByMap(map,  starttime, endtime, page, size, types, configProperty.getEs_index());
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
        //System.out.println(hsData);
        //hsData的参数说明{日志类型：type=dns, starttime=, endtime=, dns客户端ip：dns_clientip=, dns_view=, dns域名：dns_domain_name=, dns解析数据类型：dns_ana_type=, dns服务器：dns_server=, page=1, size=12}

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
		//
		/*String type = request.getParameter("type");
		// 开始时间
		String starttime = request.getParameter("starttime");
		// 结束时间
		String endtime = request.getParameter("endtime");
		// 资产ip
		String ip = request.getParameter("ip");
		// 资产id
		String equipmentid = request.getParameter("equipmentid");
		// 资产名
		String hostname = request.getParameter("hostname");
		// 事件级别，对应日志级别（0-8）
		String event_level = request.getParameter("event_level");
		// 事件级别，高中低危，0-3高危
		String event_levels = request.getParameter("event_levels");
		// 事件类型
		String event_type = request.getParameter("event_type");
		String page = request.getParameter("page");
		String size = request.getParameter("size");*/
		Object userrole = session.getAttribute(Constant.SESSION_USERROLE);

		//Map<String, String> map = new HashMap<String, String>();
		// 事件查询自定义值，该字段确认日志中是否定性事件
		map.put("event", "event");

		/*if (ip!=null&&!ip.equals("")) {
			map.put("ip", ip);
		}
		if (hostname!=null&&!hostname.equals("")) {
			map.put("equipmentname", hostname);
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
		}*/
		// 判断是否是非管理员角色，是传入参数用户id
		if (!userrole.equals(ContextRoles.MANAGEMENT)){
			map.put("userid",session.getAttribute(Constant.SESSION_USERID).toString());
		}

		List<Map<String, Object>> list =null;

		String[] types = {LogType.LOGTYPE_LOG4J,LogType.LOGTYPE_WINLOG,LogType.LOGTYPE_SYSLOG,LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG,LogType.LOGTYPE_UNKNOWN,LogType.LOGTYPE_MYSQLLOG,LogType.LOGTYPE_NETFLOW};
		try {
			list = logService.getLogListByBlend(map,starttime,endtime,page,size,types,configProperty.getEs_index());
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
	 * 通过事件获取日志信息(未完成)
	 * @param request
	 * @return
	 */
	/*@ResponseBody
	@RequestMapping(value="/getLogListByLevel",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="通过事件查询日志信息")*/
	/*public String getLogListByEvent(HttpServletRequest request) {

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
	}*/

	/**
	 * 通过日志级别获取日志信息
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getLogListByLevel",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="通过日志级别查询数据")
	public String getLogListByLevel(HttpServletRequest request) {

		String operation_level = request.getParameter("words");

		String[] types = default_types;

		Map<String, String> map = new HashMap<>();
		map.put("operation_level", operation_level);
		List<Map<String, Object>> list =null;
		try {
			list = logService.getListByMap(map, null, null, types, configProperty.getEs_index());
		} catch (Exception e) {
			e.printStackTrace();
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
	 * @param request
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
					list = logService.groupBy(index,types,param,size,null,null,searchmap);
				} catch (Exception e) {
					e.printStackTrace();
				}

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
				//List<Map<String, Object>> list1 = logService.groupBy(index, types, param, searchmap,5);
				List<Map<String, Object>> list1 = null;
				try {
					list1 = logService.groupBy(index,types,param,5,null,null,searchmap);
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
					//List<Map<String, Object>> list2 = logService.groupBy(index, types, param, searchmap,5);
					List<Map<String, Object>> list2 = null;
					try {
						list2 = logService.groupBy(index,types,param,5,null,null,searchmap);
					} catch (Exception e) {
						e.printStackTrace();
					}
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
	 * 导入历史数据
	 * @param request
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
		//BulkRequest requests = new BulkRequest();
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
										requests.add(logCrudDao.insertNotCommit(configProperty.getEs_index(), LogType.LOGTYPE_LOG4J, json));
									}else{
										log4j.setUserid(LogType.LOGTYPE_UNKNOWN);
										log4j.setDeptid(LogType.LOGTYPE_UNKNOWN);
										log4j.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
										log4j.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
										json = gson.toJson(log4j);
										requests.add(logCrudDao.insertNotCommit(configProperty.getEs_index(), LogType.LOGTYPE_LOG4J, json));
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
							//requests.add(clientTemplate.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG, json));
							requests.add(logCrudDao.insertNotCommit(configProperty.getEs_index(), LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG, json));
						}else {
							packetFilteringFirewal.setUserid(LogType.LOGTYPE_UNKNOWN);
							packetFilteringFirewal.setDeptid(LogType.LOGTYPE_UNKNOWN);
							packetFilteringFirewal.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
							packetFilteringFirewal.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
							json = gson.toJson(packetFilteringFirewal);
							requests.add(logCrudDao.insertNotCommit(configProperty.getEs_index(), LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG, json));
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
								requests.add(logCrudDao.insertNotCommit(configProperty.getEs_index(), LogType.LOGTYPE_WINLOG, json));
							}
						}else{
							winlog.setUserid(LogType.LOGTYPE_UNKNOWN);
							winlog.setDeptid(LogType.LOGTYPE_UNKNOWN);
							winlog.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
							winlog.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
							json = gson.toJson(winlog);
							requests.add(logCrudDao.insertNotCommit(configProperty.getEs_index(), LogType.LOGTYPE_WINLOG, json));
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
								requests.add(logCrudDao.insertNotCommit(configProperty.getEs_index(), LogType.LOGTYPE_SYSLOG, json));
							}
						}else{
							//在资产ip地址池里，但是无法识别日志类型
							syslog.setUserid(LogType.LOGTYPE_UNKNOWN);
							syslog.setDeptid(LogType.LOGTYPE_UNKNOWN);
							syslog.setEquipmentid(LogType.LOGTYPE_UNKNOWN);
							syslog.setEquipmentname(LogType.LOGTYPE_UNKNOWN);
							json = gson.toJson(syslog);
							requests.add(logCrudDao.insertNotCommit(configProperty.getEs_index(), LogType.LOGTYPE_UNKNOWN, json));
						}
					}else{
						//不在资产ip池里，暂不处理
					}
				}
				// 根据配置参数进行批量提交
				if (requests.size()==configProperty.getEs_bulk()) {
					//clientTemplate.bulk(requests);
					logCrudDao.bulkInsert(requests);
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
				try {
					logCrudDao.bulkInsert(requests);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		list.add(map);
		String result = JSONArray.fromObject(list).toString();

		return result;
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(new Syslog().toMapping());

	}

}

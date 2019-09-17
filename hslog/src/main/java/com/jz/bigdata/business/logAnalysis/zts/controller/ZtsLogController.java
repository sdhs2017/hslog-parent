package com.jz.bigdata.business.logAnalysis.zts.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jz.bigdata.business.logAnalysis.log.LogType;
import com.jz.bigdata.business.logAnalysis.log.controller.LogController;
import com.jz.bigdata.business.logAnalysis.log.entity.Log4j;
import com.jz.bigdata.business.logAnalysis.log.entity.Syslog;
import com.jz.bigdata.business.logAnalysis.log.entity.Unknown;
import com.jz.bigdata.business.logAnalysis.log.entity.Winlog;
import com.jz.bigdata.business.logAnalysis.log.entity.ZtsApp;
import com.jz.bigdata.business.logAnalysis.log.entity.ZtsLog4j;
import com.jz.bigdata.business.logAnalysis.log.entity.ZtsSyslog;
import com.jz.bigdata.business.logAnalysis.zts.service.impl.ZtsLogServiceImpl;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.DescribeLog;

import net.sf.json.JSONArray;

//@Controller
//@RequestMapping("/log")
public class ZtsLogController extends LogController{
	
	private static Logger logger = Logger.getLogger(ZtsLogController.class);

	@Resource(name ="configProperty")  
    ConfigProperty configProperty;
	
	@Resource(name="ZtsLogServiceImpl")
	private ZtsLogServiceImpl ztsLogService;
	
	@ResponseBody
	@RequestMapping(value="/createIndexAndMapping",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="初始化数据结构")
	public String createIndexAndMapping(HttpServletRequest request) {
		Map<String, Object> map= new HashMap<>();
		try {
			//ztsLogService.createIndexAndmapping(configProperty.getEs_index(),LogType.LOGTYPE_WINLOG, new Winlog().toMapping());
			//ztsLogService.createIndexAndmapping(configProperty.getEs_index(),LogType.LOGTYPE_LOG4J, new Log4j().toMapping());
			//ztsLogService.createIndexAndmapping(configProperty.getEs_index(),LogType.LOGTYPE_UNKNOWN, new Unknown().toMapping());
			// 定制业务
//			ztsLogService.createIndexAndmapping(configProperty.getEs_index(),LogType.LOGTYPE_SYSLOG, new ZtsSyslog().toMapping());
//			ztsLogService.createIndexAndmapping(configProperty.getEs_index(),LogType.LOGTYPE_LOG4J, new ZtsLog4j().toMapping());
			
			ztsLogService.createIndexAndmapping(configProperty.getEs_index(),LogType.LOGTYPE_APPLOG, new ZtsApp().toMapping());
			map.put("state", true);
			map.put("msg", "数据结构初始化成功！");
			logger.info(map);
			return JSONArray.fromObject(map).toString();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			map.put("state", false);
			map.put("msg", "数据结构初始化失败！");
			map.put("message", e.getMessage());
			logger.info(map);
			return JSONArray.fromObject(map).toString();
		}
		
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
		String ztData = request.getParameter("ztData");
		System.out.println(ztData);
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		
		map = removeMapEmptyValue(mapper.readValue(ztData, Map.class));
		System.out.println(map);
		Object pageo = map.get("page");
		Object sizeo = map.get("size");
		
		String page = pageo.toString();
		String size = sizeo.toString();
		
		
		ArrayList<String> arrayList = new ArrayList<>();
		List<Map<String, Object>> list =null;
		
		if (map.get("type")!=null&&!map.get("type").equals("")) {
			arrayList.add(map.get("type"));
			String [] types = arrayList.toArray(new String[arrayList.size()]);
			if (userrole.equals("1")) {
				list = ztsLogService.getListByBlend(configProperty.getEs_index(), types, map,page,size);
			}else {
				list = ztsLogService.getListByBlend(configProperty.getEs_index(), types, map,session.getAttribute(Constant.SESSION_USERID).toString(),page,size);
			}
		}else {
			String[] types = {LogType.LOGTYPE_LOG4J,LogType.LOGTYPE_WINLOG,LogType.LOGTYPE_SYSLOG,LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG,LogType.LOGTYPE_UNKNOWN,LogType.LOGTYPE_MYSQLLOG};
			if (userrole.equals("1")) {
				list = ztsLogService.getListByBlend(configProperty.getEs_index(), types, map,page,size);
			}else {
				list = ztsLogService.getListByBlend(configProperty.getEs_index(), types, map,session.getAttribute(Constant.SESSION_USERID).toString(),page,size);
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
	
}

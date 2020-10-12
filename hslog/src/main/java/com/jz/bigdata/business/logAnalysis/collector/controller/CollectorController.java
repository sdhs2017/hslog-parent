package com.jz.bigdata.business.logAnalysis.collector.controller;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.asset.cache.AssetCache;
import com.jz.bigdata.common.asset.service.IAssetService;
import com.jz.bigdata.common.configuration.cache.ConfigurationCache;
import com.jz.bigdata.common.configuration.service.IConfigurationService;
import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.business.logAnalysis.collector.service.ICollectorService;
import com.jz.bigdata.business.logAnalysis.log.service.IlogService;
import com.jz.bigdata.common.assets_old.service.IAssetsService;
import com.jz.bigdata.common.equipment.service.IEquipmentService;


import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.DescribeLog;

import net.sf.json.JSONArray;

@Controller
@RequestMapping("/collector")
public class CollectorController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource(name = "CollectorService")
	private ICollectorService collectorService;

	@Resource(name = "EquipmentService")
	private IEquipmentService equipmentService;

	@Resource(name = "AssetService")
	private IAssetService assetService;

	@Resource(name = "configProperty")
	private ConfigProperty configProperty;

	@Resource(name = "ConfigurationService")
	private IConfigurationService configurationService;
	
	@Resource(name="logService")
	private IlogService logService;


//	@Resource
//	private MascanCollector mascanCollector;
	@Resource(name="assetsService")
	private IAssetsService masscanipService;

	@ResponseBody
	@RequestMapping("/kafkaTest")
	public boolean kafkaTest() {
		return collectorService.getKafkaCollectorState();
	}
	// 获取采集器开启或关闭状态，true为开启，false为关闭
	@ResponseBody
	@RequestMapping("/getCollectorState")
	public boolean getCollectorState() {
		return collectorService.getKafkaCollectorState();
	}

	// 开启采集器
	@ResponseBody
	@RequestMapping(value = "/startCollectorState", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "开启数据采集器")
	public String startKafkaCollector() {
		String resultInfo;
		Map<String, Object> map = new HashMap<>();
		/**
		 * 判断index或者index的template是否存在，如果不存在提示执行初始化操作
		 */
		if (!logService.checkOfIndexOrTemplate(configProperty.getEs_index())){
			resultInfo = "数据采集器开启失败，请先执行初始化操作";
			map.put("state", false);
			map.put("msg", resultInfo);
			logger.info(resultInfo);
			return JSONArray.fromObject(map).toString();
		}
		/**
		 *更新资产缓存信息，全局配置项信息
		 */
		try{
			AssetCache.INSTANCE.init(equipmentService,assetService);
			ConfigurationCache.INSTANCE.init(configurationService);
		}catch (Exception e){
			e.printStackTrace();
			map.put("state", false);
			map.put("msg", "资产信息获取失败！");
			return JSONArray.fromObject(map).toString();
		}
		/**
		 * 符合上述条件后执行开启kafka采集器
		 */
		boolean result = collectorService.startKafkaCollector();
		/**
		 * 判断kafka开启是否正常，给前端页面返回对应的状态信息和描述
		 */
		if (result) {
			resultInfo = "数据采集器开启成功";
			map.put("state", result);
			map.put("msg", resultInfo);
			logger.info(resultInfo);
			return JSONArray.fromObject(map).toString();
		} else {
			resultInfo = "数据采集器开启失败，请勿重复开启";
			map.put("state", result);
			map.put("msg", resultInfo);
			logger.info(resultInfo);
			return JSONArray.fromObject(map).toString();
		}
	}

	// 关闭采集器
	@ResponseBody
	@RequestMapping(value = "/stopKafkaCollector", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "关闭数据采集器")
	public String stopKafkaCollector() {
		String resultInfo;
		Map<String, Object> map = new HashMap<>();
		try {
			boolean result = collectorService.stopKafkaCollector();
			if (result) {
				resultInfo = "数据采集器关闭成功";
				map.put("state", result);
				map.put("msg", resultInfo);
				logger.info(resultInfo);
				return JSONArray.fromObject(map).toString();
			} else {
				resultInfo = "数据采集器关闭失败，已关闭";
				map.put("state", result);
				map.put("msg", resultInfo);
				logger.info(resultInfo);
				return JSONArray.fromObject(map).toString();
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
			map.put("state", false);
			map.put("msg", "数据采集器关闭失败");
			return JSONArray.fromObject(map).toString();
		}
	}

	// 监听采集器状态
	@ResponseBody
	@RequestMapping(value = "/stateKafkaCollector", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "监控数据采集器状态")
	public String stateKafkaCollector() {
		Map<String, Object> map = new HashMap<>();
		boolean result = collectorService.stateKafkaCollector();
		map.put("state", result);
		return JSONArray.fromObject(map).toString();
	}

	// 开启masscan扫描
	@ResponseBody
	@RequestMapping(value = "/startMasscanCollector", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "开启masscan扫描")
	public String startMasscanCollector(HttpServletRequest request) {
		
		String  ports = configProperty.getMasscan_ports();
		String startip=request.getParameter("startip");
		String endip=request.getParameter("endip");
		boolean resultstate = collectorService.stateMasscanCollector();
		Map<String, Object> map = new HashMap<>();
		if(resultstate==false){
			map.put("state", resultstate);
			map.put("msg", "资产扫描器开启失败，请勿重复开启");
			return JSONArray.fromObject(map).toString();
		}else{
			boolean result = collectorService.startMasscanCollector(startip,endip, ports,masscanipService,configProperty);
			if(result==true){
				map.put("state", result);
				map.put("msg", "资产扫描器开启成功");
				return JSONArray.fromObject(map).toString();
			}else{
				map.put("state", result);
				map.put("msg", "资产扫描器开启失败");
				return JSONArray.fromObject(map).toString();
			}
		}
		
	}
	
	// 监控Masscan状态
	@ResponseBody
	@RequestMapping(value = "/stateMasscanCollector", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "监控Masscan状态")
	public String stateMasscanCollector() {
		Map<String, Object> map = new HashMap<>();
		boolean result = collectorService.stateMasscanCollector();
		map.put("state", result);
		return JSONArray.fromObject(map).toString();
	}
	
	// pcap4j抓取数据包
	@ResponseBody
	@RequestMapping(value = "/startPcap4jCollector", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "开启pcap4j抓取数据包")
	public String startPcap4jCollector(HttpServletRequest request) {
		
		Map<String, Object> map = new HashMap<>();
		// 判断index是否存在，如果不存在提示执行初始化操作
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		// 判断index是否存在，如果不存在提示执行初始化操作
		if (!logService.checkOfIndexOrTemplate(configProperty.getEs_index())){
			map.put("state", false);
			map.put("msg", "流量采集器开启失败，请先执行初始化操作");
			return JSONArray.fromObject(map).toString();
		}
		
		String result = collectorService.startPcap4jCollector();

		return result;
	}
    // 获取caffeine占用内存大小
    @ResponseBody
    @RequestMapping(value = "/getCaffeineSize", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取caffeine占用内存大小")
    public String getCaffeineSize(HttpServletRequest request) {
        return collectorService.getCaffeineSize();
    }
	// 监控pcap4j抓取数据包运行状态
	@ResponseBody
	@RequestMapping(value = "/statePcap4jCollector", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "监控pcap4j抓取数据包运行状态")
	public String statePcap4jCollector(HttpServletRequest request) {
		return collectorService.statePcap4jCollector();
	}
	
	// 停止pcap4j抓取数据包
	@ResponseBody
	@RequestMapping(value = "/stopPcap4jCollector", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "停止pcap4j抓取数据包")
	public String stopPcap4jCollector(HttpServletRequest request) {
		return collectorService.stopPcap4jCollector();
	}
	
	// 获取http中request的url并入库
	/*@ResponseBody
	@RequestMapping(value = "/insertUrl", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "获取http中request的url并入库")*/
	/*public String insertUrl(HttpServletRequest request) {
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Url> list = urldao.selectAll();
		if (list.isEmpty()) {
			for(String url : urlSet) {
				Url u = new Url();
				u.setDate(format.format(new Date()));
				u.setUrl(url);
			}
		}else {
			Map<String, Integer> map = new HashMap<>();
			for (Url url : list) {
				map.put(url.getUrl(), 1);
			}
			for(String url : urlSet) {
				if (map.get(url)!=1) {
					Url u = new Url();
					u.setDate(format.format(new Date()));
					u.setUrl(url);
				}
			}
		}
		
		return "";
		//return JSONArray.fromObject(map).toString();
	}*/
	
		
	/**
     * 根据IP获取指定网卡设备
	* @param NameOrIP 网卡IP或者网卡名
	* 
	* @return 指定的设备对象
	*/
	public static PcapNetworkInterface getCaptureNetworkInterface(String NameOrIP) {
		List<PcapNetworkInterface> allDevs;
		try {
			// 获取全部的网卡设备列表，Windows如果获取不到网卡信息，输入：net start npf  启动网卡服务
			allDevs = Pcaps.findAllDevs();
		
			 for (PcapNetworkInterface networkInterface : allDevs) {
			     List<PcapAddress> addresses = networkInterface.getAddresses();
			     for (PcapAddress pcapAddress : addresses) {
			    	 // 通过判断传入的参数是IP还是网卡名来获取正式的网卡信息
			    	 if(getSubUtil(NameOrIP,"\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")!=""){
			    		 // 获取网卡IP地址
				         String ip = pcapAddress.getAddress().getHostAddress();
				         if (ip != null && ip.contains(NameOrIP)) {
				             // 返回指定的设备对象
				        	 // System.out.println("filter:"+ip);
				             return networkInterface;
				         }
			    	 }else {
			    		 String name = networkInterface.getName();
						 if(NameOrIP.equals(name)) {
							 return networkInterface;
						 }
					}
			         
			     }
			 }
		} catch (PcapNativeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}



	// ****************************************beats数据发送到kafka，kafka采集器管理*****************************************************

	// 开启kakfaOfBeats采集器
	@ResponseBody
	@RequestMapping(value = "/startKafkaOfBeatsCollector", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "开启数据采集器")
	public String startKafkaOfBeatsCollector() {

		Map<String, Object> map = new HashMap<>();


		try{
			/**
			 *更新资产缓存信息
			 */
			AssetCache.INSTANCE.init(equipmentService,assetService);
			ConfigurationCache.INSTANCE.init(configurationService);
		}catch (Exception e){
			e.printStackTrace();
			map.put("state", false);
			map.put("msg", "资产信息获取失败！");
			return JSONArray.fromObject(map).toString();
		}
		/**
		 * 符合上述条件后执行开启kafka采集器
		 */
		boolean result = collectorService.startKafkaOfBeatsCollector();
		/**
		 * 判断kafka开启是否正常，给前端页面返回对应的状态信息和描述
		 */
		if (result) {
			map.put("state", result);
			map.put("msg", "数据采集器开启成功");
			return JSONArray.fromObject(map).toString();
		} else {
			map.put("state", result);
			map.put("msg", "数据采集器开启失败，请勿重复开启");
			return JSONArray.fromObject(map).toString();
		}
	}

	// 关闭kakfaOfBeats采集器
	@ResponseBody
	@RequestMapping(value = "/stopKafkaOfBeatsCollector", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "关闭数据采集器")
	public String stopKafkaOfBeatsCollector() {
		Map<String, Object> map = new HashMap<>();
		try {
			boolean result = collectorService.stopKafkaOfBeatsCollector();
			if (result) {
				map.put("state", result);
				map.put("msg", "数据采集器关闭成功");
				return JSONArray.fromObject(map).toString();
			} else {
				map.put("state", result);
				map.put("msg", "数据采集器已关闭");
				return JSONArray.fromObject(map).toString();
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
			map.put("state", false);
			map.put("msg", "数据采集器关闭失败");
			return JSONArray.fromObject(map).toString();
		}
	}

	// 监听KafkaOfBeats采集器状态
	@ResponseBody
	@RequestMapping(value = "/stateKafkaOfBeatsCollector", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "监控数据采集器状态")
	public String stateKafkaOfBeatsCollector() {
		Map<String, Object> map = new HashMap<>();
		boolean result = collectorService.stateKafkaOfBeatsCollector();
		map.put("state", result);
		return JSONArray.fromObject(map).toString();
	}


	/**
	 * 正则匹配
	 * @param soap
	 * @param rgex
	 * @return 返回匹配的内容
	 */
 	public static String getSubUtil(String soap,String rgex){  
         Pattern pattern = Pattern.compile(rgex);// 匹配的模式  
         Matcher m = pattern.matcher(soap);  
         while(m.find()){
             return m.group(0);
         }  
         return "";  
    }


	/**
	 * 开启kafka of agent
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/startAgentKafkaListener", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "开启AgentKafkaListener")
	public String startKafkaBeatsListener() {
		try{
			/**
			 *更新资产、全局配置缓存信息
			 */
			AssetCache.INSTANCE.init(equipmentService,assetService);
			ConfigurationCache.INSTANCE.init(configurationService);
			return collectorService.startAgentKafkaListener();
		}catch (Exception e){
			System.out.println("Agent采集器开启失败"+e.getMessage());
			logger.error("Agent采集器开启失败"+e.getMessage());
			return Constant.failureMessage("Agent采集器开启失败 ！");
		}
	}

	/**
	 * 关闭kafka of agent
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/stopAgentKafkaListener", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "关闭AgentKafkaListener")
	public String stopAgentKafkaListener() {
		return collectorService.stopAgentKafkaListener();
	}
	// 监听agent采集器状态
	@ResponseBody
	@RequestMapping(value = "/getAgentKafkaListenerState", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "监控agent采集器状态")
	public String getAgentKafkaListenerState() {
		Map<String, Object> map = new HashMap<>();
		boolean result = collectorService.getAgentKafkaListenerState();
		map.put("state", result);
		return JSONArray.fromObject(map).toString();
	}
	/**
	 * 开启kafka of syslog
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/startSyslogKafkaListener", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "开启SyslogKafkaListener")
	public String startSyslogKafkaListener() {
		try{
			/**
			 *更新资产、全局配置缓存信息
			 */
			AssetCache.INSTANCE.init(equipmentService,assetService);
			ConfigurationCache.INSTANCE.init(configurationService);
			return collectorService.startSyslogKafkaListener();
		}catch (Exception e){
			logger.error("Syslog采集器开启失败"+e.getMessage());
			return Constant.failureMessage("Syslog采集器开启失败！");
		}
	}

	/**
	 * 关闭kafka of syslog
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/stopSyslogKafkaListener", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "关闭SyslogKafkaListener")
	public String stopSyslogKafkaListener() {
		return collectorService.stopSyslogKafkaListener();
	}
	// 监听agent采集器状态
	@ResponseBody
	@RequestMapping(value = "/getSyslogKafkaListenerState", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "监控agent采集器状态")
	public String getSyslogKafkaListenerState() {
		Map<String, Object> map = new HashMap<>();
		boolean result = collectorService.getSyslogKafkaListenerState();
		map.put("state", result);
		return JSONArray.fromObject(map).toString();
	}
}

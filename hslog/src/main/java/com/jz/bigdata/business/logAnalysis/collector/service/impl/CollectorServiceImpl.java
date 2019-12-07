package com.jz.bigdata.business.logAnalysis.collector.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.FutureTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import com.hs.elsearch.dao.logDao.ILogCrudDao;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jz.bigdata.business.logAnalysis.collector.kafka.KafkaCollector;
import com.jz.bigdata.business.logAnalysis.collector.masscan.HeartbeatCollector;
import com.jz.bigdata.business.logAnalysis.collector.masscan.MascanCollector;
import com.jz.bigdata.business.logAnalysis.collector.pcap4j.PacketStream;
import com.jz.bigdata.business.logAnalysis.collector.pcap4j.Pcap4jCollector;
import com.jz.bigdata.business.logAnalysis.collector.pcap4j.TcpStream;
import com.jz.bigdata.business.logAnalysis.collector.service.ICollectorService;
import com.jz.bigdata.common.alarm.service.IAlarmService;
import com.jz.bigdata.common.assets.entity.Assets;
import com.jz.bigdata.common.assets.service.IAssetsService;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.equipment.service.IEquipmentService;
import com.jz.bigdata.common.serviceInfo.entity.ServiceInfo;
import com.jz.bigdata.common.serviceInfo.service.IServiceInfoService;
import com.jz.bigdata.common.url.dao.IUrlDao;
import com.jz.bigdata.common.users.service.IUsersService;
//import com.jz.bigdata.framework.spring.es.elasticsearch.ClientTemplate;
//import com.hs.elsearch.template.bak.ClientTemplate;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.Uuid;

import net.sf.json.JSONArray;

@Service(value="CollectorService")
public class CollectorServiceImpl implements ICollectorService{
	
	//开关
	boolean flag = false;
	
	//kafka采集器
	KafkaCollector kc = null;
	
	MascanCollector Masscan = null;
	
	boolean flagMasscan = false;
	
	Thread t;
	Thread masscanThread;
	
	// pcap4j 开关设置
	Thread pcap4jthread = null;
	FutureTask<String> futureTask = null;
	Pcap4jCollector pcap4jCollector  = null;
	private Set<String> domainSet = new HashSet<>();
	private Map<String, String> urlmap = new HashMap<String, String>();
	//private Set<String> urlSet = new HashSet<>();
	PacketStream packetStream ;
	
	@Resource(name="assetsService")
	private IAssetsService assetsService;
	
	@Resource(name ="configProperty")  
    private ConfigProperty configProperty;
	
	@Resource(name = "EquipmentService")
	private IEquipmentService equipmentService;
	
	@Resource(name = "ServiceInfoService")
	private IServiceInfoService serviceInfoService;
	
	@Resource
	private IUrlDao urldao;
	
	
//	public Thread getT() {
//		return t;
//	}
//	public void setT(Thread t) {
//		this.t = t;
//	}

	/**
	 * 获取采集器开启或关闭状态，true为开启，false为关闭
	 * @return
	 */
	public boolean getKafkaCollectorState(){
		boolean result = false;
		if(null!=kc){
			result = kc.isStarted();
		}
		return result;
	}
	
	@SuppressWarnings("finally")
	public boolean initKafkaCollector(IEquipmentService equipmentService, ILogCrudDao logCurdDao, ConfigProperty configProperty, IAlarmService alarmService, IUsersService usersService){

		boolean result = false;
		try{
//			if(!flag){
				kc = new KafkaCollector(equipmentService,logCurdDao,configProperty,alarmService,usersService);
				flag = true;
//			}
			result = true;
		}finally{
			return result;
		}

		
	}

	/**
	 * 启动kafka采集器
	 * @param equipmentService
	 * @param logCurdDao
	 * @param configProperty
	 * @param alarmService
	 * @param usersService
	 * @return
	 */
	@Override
	public boolean startKafkaCollector(IEquipmentService equipmentService,ILogCrudDao logCurdDao,ConfigProperty configProperty,IAlarmService alarmService,IUsersService usersService){
		boolean result = false;
		//如果为true，则表示已经开启，反之，则为未开启，需要初始化
		
		initKafkaCollector(equipmentService,logCurdDao,configProperty,alarmService,usersService);
		if(!kc.isStarted()){

			t = new Thread(kc);
			kc.setStarted(true);
			t.start();
			
			result = true;
		}else{
//				kc.setStarted(true);
//				result = true;
		}
		
		
		return result;
	}
	
	/**
	 * 关闭kafka采集器
	 * @return
	 * @throws InterruptedException
	 */
	public boolean stopKafkaCollector() throws InterruptedException{
		boolean result = false;
		//如果为true，则表示已经开启，需要关闭，反之，则为未开启
		
		if(flag){
			kc.setStarted(false);
			kc.closeKafkaStream();
//			flag = false;
			result = true;
		}else{
			
		}
		
//		if(null!=kc && kc.isStarted()){
//			kc.setStarted(false);
//			
//			
////			t.join();
//			
////			t.sleep(10000);
//			//阻塞
////			t.interrupt();
//			
////			t.stop();
////			t=null;
////			kc = null;
//			
//			result = true;
//		}
		return result;
	}
	

	public boolean stateKafkaCollector() {
		if (null==kc) {
			return false;
		}
		return kc.isStarted();
	}

	
	/**
	 * @return
	 * @description
	 * 启动Masscan
	 */
	@Override
	public boolean startMasscanCollector(String startip,String endip,String ports,IAssetsService masscanipService,ConfigProperty configProperty) {
		
		boolean result = false;
		//如果为true，则表示已经开启
		List<String> list =new ArrayList<>();
		
		String[] startips= startip.split("\\.");
		String[] endips= endip.split("\\.");
		
		int start=Integer.valueOf(startips[3]);
		int end=Integer.valueOf(endips[3]);
		List<Assets> assetsList=masscanipService.selectAll();
		Boolean isIn=false;
		for(int i=start;i<=end;i++){
			for(Assets assets:assetsList){
				if(assets.getIp().equals((startips[0]+"."+startips[1]+"."+startips[2]+"."+i))==true){
					isIn=true;
					break;
				}
			}
			if(isIn==false){
				list.add((startips[0]+"."+startips[1]+"."+startips[2]+"."+i));
			}
			isIn=false;
		}
		Masscan = new MascanCollector(list,ports,masscanipService,configProperty);
		if(!Masscan.getStarted()){
			result=true;
		}
		
		return result;
	}

	@Override
	public boolean stateMasscanCollector() {
		if(null==Masscan){
			return true;
		}else{
			return Masscan.getStarted();
		}
		
	}
	
	/**
	 * 开启pcap4j
	 */
	public String startPcap4jCollector(ILogCrudDao logCurdDao,ConfigProperty configProperty) {
		
		Map<String, Object> map = new HashMap<>();
		
		HashMap<String, TcpStream> tcpStreamList=new HashMap<String, TcpStream>();
		PcapNetworkInterface nif = getCaptureNetworkInterface(configProperty.getPcap4j_network());
		
		if(nif==null)
        {
        	map.put("state", false);
			map.put("msg", "网卡获取失败！数据包采集器开启失败！");
			return JSONArray.fromObject(map).toString();
        }
        // 抓取包长度
        int snaplen = 64 * 1024;
        // 超时50ms
        int timeout = 50;
        // 初始化抓包器
        PcapHandle.Builder phb = new PcapHandle.Builder(nif.getName()).snaplen(snaplen)
            .promiscuousMode(PromiscuousMode.PROMISCUOUS).timeoutMillis(timeout)
            .bufferSize(1 * 1024 * 1024);
        PcapHandle handle = null;
		try {
			handle = phb.build();
		} catch (PcapNativeException e) {
			//e.printStackTrace();
			map.put("state", false);
			map.put("msg", "网卡build失败！数据包采集器开启失败！"+e.getMessage());
			return JSONArray.fromObject(map).toString();
		}
        // handle = nif.openLive(snaplen, PromiscuousMode.NONPROMISCUOUS, timeout);

        /** 设置TCP过滤规则 */
        //String filter = "ip and tcp and (port 443)";
        /** 设置TCP过滤规则 */
        String filter = "ip and (tcp or udp or icmp)";
        
            
        // 设置过滤器
        try {
			handle.setFilter(filter, BpfCompileMode.OPTIMIZE);
		} catch (PcapNativeException e) {
			//e.printStackTrace();
			map.put("state", false);
			map.put("msg", "网卡设置过滤器失败！数据包采集器开启失败！"+e.getMessage());
			return JSONArray.fromObject(map).toString();
		} catch (NotOpenException e) {
			//e.printStackTrace();
			map.put("state", false);
			map.put("msg", "网卡设置过滤器失败！数据包采集器开启失败！"+e.getMessage());
			return JSONArray.fromObject(map).toString();
		}
        Gson gson = new GsonBuilder()
				 .setDateFormat("yyyy-MM-dd HH:mm:ss")  
				 .create(); 
        
        List<IndexRequest> requests = new ArrayList<IndexRequest>();
		//BulkRequest requests = new BulkRequest();
        
        //初始化listener
        PacketListener listener = new PacketListener() {
        	public void gotPacket(Packet packet) {
        		try {
        			//packetStream = new PacketStream(configProperty,clientTemplate,gson,requests,domainSet,urlmap);
        			packetStream = new PacketStream(configProperty,logCurdDao,gson,requests,domainSet,urlmap);
            		packetStream.gotPacket(packet);
       			} catch (Exception e) {
       				System.out.println("---------------jiyourui-----new PacketStream-------报错信息:------------"+e.getLocalizedMessage());
       				System.out.println("---------------jiyourui-----new PacketStream-------报错信息:------------"+e.getMessage());
       				e.printStackTrace();
       			}
           }
        };
		
		try {
			pcap4jCollector = new Pcap4jCollector(configProperty.getPcap4j_network(),handle,listener);
			futureTask = new FutureTask<>(pcap4jCollector);
			
			pcap4jthread = new Thread(futureTask);
			pcap4jthread.start();
			
			if(pcap4jthread.isAlive()==true){
				map.put("state", pcap4jthread.isAlive());
				map.put("msg", "数据包采集器开启成功");
				return JSONArray.fromObject(map).toString();
			}else{
				map.put("state", pcap4jthread.isAlive());
				map.put("msg", "数据包采集器开启失败");
				return JSONArray.fromObject(map).toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("state", false);
			map.put("msg", "数据包采集器开启失败");
			return JSONArray.fromObject(map).toString();
		}
		
	}
	
	/**
	 * 监控pcap4j
	 */
	public String statePcap4jCollector() {
		Map<String, Object> map = new HashMap<>();
		if (pcap4jCollector!=null) {
			map.put("state", pcap4jCollector.getPcap4jStatus());
		}else {
			map.put("state", false);
		}
		
		return JSONArray.fromObject(map).toString();
	}
	
	/**
	 * 
	 * 关闭pcap4j
	 * @return
	 */
	public String stopPcap4jCollector() {
		
		Map<String, Object> map = new HashMap<>();
		if (pcap4jCollector!=null) {
			if (pcap4jCollector.getPcap4jStatus()) {
				pcap4jCollector.closePcap4j();
				map.put("state", true);
				map.put("msg", "数据包采集器关闭成功");
			}else {
				map.put("state", true);
				map.put("msg", "数据包采集器已经关闭");
			}
		}else {
			map.put("state", false);
			map.put("msg", "数据包采集器未启动");
		}
		
		return JSONArray.fromObject(map).toString();
	}
	
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
				 System.out.println(networkInterface.getName());
				 // 通过判断传入的参数是IP还是网卡名来获取正式的网卡信息
		    	 if(getSubUtil(NameOrIP,"\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")!=""){
		    		 List<PcapAddress> addresses = networkInterface.getAddresses();
				     for (PcapAddress pcapAddress : addresses) {
				    	// 获取网卡IP地址
				         String ip = pcapAddress.getAddress().getHostAddress();
				         if (ip != null && ip.contains(NameOrIP)) {
				             // 返回指定的设备对象
				        	 // System.out.println("filter:"+ip);
				             return networkInterface;
				         }
				     }
		    	 }else {
		    		 String name = networkInterface.getName();
		    		 System.out.println("实际name："+name+"----------传入name："+NameOrIP);
					 if(NameOrIP.equals(name)) {
						 return networkInterface;
					 }
				}
			     
			 }
		} catch (PcapNativeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
 	
 	// 正则匹配
  	public static String getSubUtilSimple(String soap, String rgex) {
  		Pattern pattern = Pattern.compile(rgex);// 匹配的模式
  		Matcher m = pattern.matcher(soap);
  		while (m.find()) {
  			return m.group(1);
  		}
  		return null;
  	}
	
	/**
	 * 资产心跳机制
	 */
	public void assetsHeartBeat() {
		List<Assets> list = assetsService.selectAll();
		if (!list.isEmpty()) {
			new HeartbeatCollector(list,assetsService,configProperty);
		}else {
			System.out.println("资产表中暂无数据.....");
		}
		
	}
	
	/**
	 * 定时任务将获取的http url 插入到servicefunction表中
	 * @return
	 */
	public void insertUrl() {
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 资产表获取domain
		List<Equipment> list = equipmentService.selectAllHostName();
		
		// 临时serviceinfo list
		List<ServiceInfo> serviceslist = new ArrayList<ServiceInfo>();
		Map<String, String> tmpurlmap = new HashMap<String, String>();
		tmpurlmap.putAll(urlmap);
		urlmap.clear();
		
		if (!list.isEmpty()) {
			for(Entry<String, String> key : tmpurlmap.entrySet()) {
				for(Equipment equipment : list) {
					if (equipment.getDomain()!=null&&key.getValue().equals(equipment.getDomain())) {
						ServiceInfo funservice = new  ServiceInfo();
						String protocol = getSubUtilSimple(key.getKey(), "^(.*?)[://]");
						String relativeUrl = getSubUtilSimple(key.getKey(), "[:][0-9]{1,5}(.*?)$");
						funservice.setId(Uuid.getUUID());
						funservice.setCreateTime(format.format(new Date()));
						funservice.setEquipmentId(equipment.getId());
						funservice.setIp(equipment.getIp());
						funservice.setPort(equipment.getPort());
						funservice.setProtocol(protocol);
						funservice.setUrl(key.getKey());
						funservice.setRelativeUrl(relativeUrl);
						funservice.setState(1);
						serviceslist.add(funservice);
						break;
					}
				}
			}
		}
		if (!serviceslist.isEmpty()) {
			serviceInfoService.insert(serviceslist);
		}
		
	}
	/**
	 * 定时任务将获取的http 根域名  插入到资产表中
	 * @return
	 */
	public void insertDomain() {
		
		List<Equipment> list = equipmentService.selectAllHostName();
		Set<String> tmpdomain = new HashSet<>();
		tmpdomain.addAll(domainSet);
		domainSet.clear();
		
		List<Equipment> Equipmentlist = new ArrayList<>();
		
		if (!list.isEmpty()) {
			for(String domain : tmpdomain) {
				String ip = getSubUtil(domain, "\\d+\\.\\d+\\.\\d+\\.\\d+");
				String port = getSubUtilSimple(domain, "[:]([0-9]{1,5})[/]");
				for(Equipment equipment : list) {
					if (equipment.getIp().equals(ip)&&equipment.getPort()!=null&&equipment.getPort().equals(port)) {
						if (equipment.getDomain()==null||equipment.getDomain().equals("")) {
							equipment.setDomain(domain);
							Equipmentlist.add(equipment);
							break;
						}
					}
				}
			}
		}
		if (!Equipmentlist.isEmpty()) {
			equipmentService.batchUpdate(Equipmentlist);
		}
		
	}
}

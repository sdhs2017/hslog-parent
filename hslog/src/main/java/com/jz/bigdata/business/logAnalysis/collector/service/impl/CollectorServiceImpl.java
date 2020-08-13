package com.jz.bigdata.business.logAnalysis.collector.service.impl;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import com.carrotsearch.sizeof.RamUsageEstimator;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.jz.bigdata.common.asset.cache.AssetCache;
import com.jz.bigdata.business.logAnalysis.collector.kafka.KafakaOfBeatsCollector;
import com.jz.bigdata.business.logAnalysis.log.LogType;
import com.jz.bigdata.business.logAnalysis.log.entity.Http;
import com.jz.bigdata.common.asset.service.IAssetService;
import com.jz.bigdata.common.configuration.cache.ConfigurationCache;
import com.jz.bigdata.common.configuration.service.IConfigurationService;
import com.jz.bigdata.common.serviceInfo.dao.IServiceInfoDao;
import com.jz.bigdata.roleauthority.user.service.IUserService;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.pcap4j.core.*;
import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.jz.bigdata.common.assets_old.entity.Assets;
import com.jz.bigdata.common.assets_old.service.IAssetsService;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.equipment.service.IEquipmentService;
import com.jz.bigdata.common.serviceInfo.entity.ServiceInfo;
import com.jz.bigdata.common.serviceInfo.service.IServiceInfoService;
import com.jz.bigdata.common.url.dao.IUrlDao;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.Uuid;

import net.sf.json.JSONArray;

@Service(value="CollectorService")
public class CollectorServiceImpl implements ICollectorService{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * *****************************************默认kafka管理器属性****************************8
	 */

	//开关
	private Boolean flag = false;

	//kafka采集器
	KafkaCollector kc = null;

	//kafka线程
	Thread t;

	/**
	 * *****************************************masscan管理器属性****************************8
	 */
	// masscan开关
	boolean flagMasscan = false;
	// masscan
	MascanCollector Masscan = null;
	// masscan线程
	Thread masscanThread;

	/**
	 * *****************************************pcap4j流量采集管理器属性****************************8
	 */
	// pcap4j 线程
	Thread pcap4jThread = null;
	FutureTask<String> futureTask = null;
	Pcap4jCollector pcap4jCollector  = null;
	private Set<String> domainSet = new HashSet<>();
	private Map<String, String> urlmap = new HashMap<String, String>();
	//private Set<String> urlSet = new HashSet<>();
	PacketStream packetStream ;
	//咖啡因缓存对象定义
	Cache<Long, Http> httpCache = null;

	/**
	 * *****************************************kafka of beats数据采集管理器属性****************************8
	 */
	//kafka of beats 开关
	boolean kafkaOfBeatsFlag = false;
	// kafka of beats 采集器
	KafakaOfBeatsCollector kafakaOfBeatsCollector = null;
	//kafka of beats 线程
	Thread kafkaOfBeatsThread = null;
	
	@Resource(name="assetsService")
	private IAssetsService assetsService;
	@Resource(name="AssetService")
	private IAssetService assetService;
	@Resource(name ="configProperty")  
    private ConfigProperty configProperty;
	
	@Resource(name = "EquipmentService")
	private IEquipmentService equipmentService;
	
	@Resource(name = "ServiceInfoService")
	private IServiceInfoService serviceInfoService;
	@Resource(name = "ConfigurationService")
	private IConfigurationService configurationService;

	@Autowired
	protected ILogCrudDao logCurdDao;
	
	@Resource
	private IUrlDao urldao;

	@Resource
	private IServiceInfoDao serviceInfoDao;

	@Resource(name = "AlarmService")
	private IAlarmService alarmService;

	@Resource(name = "UserService")
	private IUserService usersService;
	/*@Autowired
	private ehcache cacheManager;*/
	
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
	public boolean initKafkaCollector(IEquipmentService equipmentService, IAssetService assetService, ILogCrudDao logCurdDao, ConfigProperty configProperty, IAlarmService alarmService, IUserService usersService){

		boolean result = false;
		try{
			if(!flag){
				kc = new KafkaCollector(equipmentService,assetService,logCurdDao,configProperty,alarmService,usersService);
				flag = true;
			}
			result = true;
		}finally{
			return result;
		}
	}

	/**
	 * 启动kafka采集器
	 * @return
	 */
	@Override
	public synchronized boolean startKafkaCollector(){
		//TODO 后续考虑使用ReentrantLock实现
		boolean result = false;
		//如果为true，则表示已经开启，反之，则为未开启，需要进行kafka的初始化
		initKafkaCollector(equipmentService,assetService,logCurdDao,configProperty,alarmService,usersService);
		/**
		 * 如果为非开启状态，则新建kafka线程
		 */
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
			kc.closeKafkaStream();//同时会将线程关闭
			flag = false;
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
	public static String getRandomString(int length) {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(str.length());
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}
	/**
	 * 开启pcap4j
	 */
	public String startPcap4jCollector() {
		
		Map<String, Object> map = new HashMap<>();
		// elasticsearch批量提交缓存区
		List<IndexRequest> requests = Collections.synchronizedList(new ArrayList<IndexRequest>());
		IndexRequest request =new IndexRequest();
		// 针对javabean中date类型的格式转化
		Gson gson = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd HH:mm:ss")
				.create();

		HashMap<String, TcpStream> tcpStreamList=new HashMap<String, TcpStream>();
		PcapNetworkInterface nif = getCaptureNetworkInterface(configProperty.getPcap4j_network());
		
		if(nif==null)
        {
        	map.put("state", false);
			map.put("msg", "网卡获取失败！数据包采集器开启失败！");
			return JSONArray.fromObject(map).toString();
        }

		// 手动加载-初始化缓存
		httpCache = Caffeine.newBuilder()
				.maximumSize(3000000)//300万流量，占用大概3G内存
				.expireAfterWrite(1, TimeUnit.HOURS)//过期时间1小时
				.recordStats()
				//.expireAfterAccess(1,TimeUnit.SECONDS)
				/*
               所有存储到缓存容器中的数据，在被移除时都会触发listener事件，该listener事件有要移除的对象信息以及移除的状态：
				EXPLICIT: 当request找到配对的response时，调用remove方法后，触发listener的状态。（这个状态不需要进行处理）
				REPLACED: 更新的时候，其实相当于把老的value给删了。（未写处理方式）
				COLLECTED: 用于我们的垃圾收集器，java的软引用，弱引用机制。（未写处理方式）
				EXPIRED： 过期淘汰，即超过设置的1小时。（打上unmatched标记，入ES）
				SIZE: 大小淘汰，当超过最大的时候就会进行淘汰，设置的300W。（未写处理方式）
                */
				.removalListener((Long Long, Http http, RemovalCause cause) ->
				{

					//System.out.println("驱逐原因：" + cause);
					if("EXPLICIT".equals(cause)){
						//不需要进行处理
					}else if ("EXPIRED".equals(cause)||"SIZE".equals(cause)){
					    http.setFlag("unmatched");
					    // 过期的request数据入库
						/*String json = gson.toJson(http);
						try {
							//requests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_index(), http.getIndex_suffix(), http.getLogdate()), LogType.LOGTYPE_DEFAULTPACKET, json));
							*//*request.index(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),http.getIndex_suffix(),http.getLogdate()));
							request.source(json, XContentType.JSON);
							logCurdDao.bulkProcessor_add(request);*//*
						} catch (Exception e) {
							e.printStackTrace();
						}*/
					}

				})
				.build();
        // 抓取包长度
        int snaplen = 64 * 1024 * 20;
        // 超时50ms
        int timeout = 50;
        // 初始化抓包器
        PcapHandle.Builder phb = new PcapHandle.Builder(nif.getName()).snaplen(snaplen)
            .promiscuousMode(PromiscuousMode.PROMISCUOUS).timeoutMillis(timeout)
            .bufferSize(10 * 1024 * 1024);
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


		final int[] j = {0};
		//BulkRequest requests = new BulkRequest();

        //初始化listener
        PacketListener listener = new PacketListener() {
        	public void gotPacket(PcapPacket packet) {
        		try {
        			//packetStream = new PacketStream(configProperty,clientTemplate,gson,requests,domainSet,urlmap);
        			//packetStream = new PacketStream(configProperty,logCurdDao,gson,requests,domainSet,urlmap,httpCache);
        			packetStream = new PacketStream(configProperty,logCurdDao,gson,request,domainSet,urlmap,httpCache);
            		packetStream.gotPacket(packet);
       			} catch (Exception e) {
					logger.error("new PacketStream-------报错信息:"+e.getMessage());
        			//System.out.println("---------------jiyourui-----new PacketStream-------报错信息:------------"+e.getLocalizedMessage());
       				//System.out.println("---------------jiyourui-----new PacketStream-------报错信息:------------"+e.getMessage());
       				e.printStackTrace();
       			}
           }
        };
		
		try {
			pcap4jCollector = new Pcap4jCollector(configProperty.getPcap4j_network(),handle,listener);
			futureTask = new FutureTask<>(pcap4jCollector);

			pcap4jThread = new Thread(futureTask);
			pcap4jThread.start();
			
			if(pcap4jThread.isAlive()==true){
				map.put("state", pcap4jThread.isAlive());
				map.put("msg", "数据包采集器开启成功");
				return JSONArray.fromObject(map).toString();
			}else{
				map.put("state", pcap4jThread.isAlive());
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

		// TODO  关闭流量采集的时候httpcache需要处理

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


	//获取caffeine数据占用内存大小
	public String getCaffeineSize(){

		return "length:"+httpCache.asMap().size()+"----size(byte):"+RamUsageEstimator.sizeOf(httpCache)+"";
	}


	public boolean initKafkaOfBeatsCollector(){

		boolean result = false;
		try{
			if(!kafkaOfBeatsFlag){
				kafakaOfBeatsCollector = new KafakaOfBeatsCollector(logCurdDao, configProperty);
				kafkaOfBeatsFlag = true;
			}
			result = true;
		}finally{
			return result;
		}
	}

	@Override
	public boolean startKafkaOfBeatsCollector() {
		boolean result = false;

		initKafkaOfBeatsCollector();

		/**
		 * 如果为非开启状态，则新建kafka线程
		 */
		if(!kafakaOfBeatsCollector.isStarted()){
			kafkaOfBeatsThread = new Thread(kafakaOfBeatsCollector);
			// 设置为开启状态
			kafakaOfBeatsCollector.setStarted(true);
			// 线程开启操作
			kafkaOfBeatsThread.start();
			result = true;
		}else{
			/**
			 * 开启状态，无需再开启，返回false
			 */
		}

		return result;
	}

	@Override
	public boolean stopKafkaOfBeatsCollector() {
		boolean result = false;
		/**
		 * 如果kafkaOfBeatsFlag为true，则采集器的线程是开启状态，进入关闭线程的操作；
		 */
		if(kafkaOfBeatsFlag){
			kafakaOfBeatsCollector.setStarted(false);
			kafakaOfBeatsCollector.closeKafkaStream();
			kafkaOfBeatsFlag = false;
			result = true;
		}else{
			/**
			 * 如果为false则线程未开启，不需要执行关闭操作
			 */
		}
		return result;
	}

	@Override
	public boolean stateKafkaOfBeatsCollector() {
		/**
		 * 如果kafakaOfBeatsCollector未被初始化，那么采集器的状态必然为关闭状态，返回false
		 */
		if (kafakaOfBeatsCollector==null) {
			return false;
		}
		/**
		 * kafakaOfBeatsCollector初始化后，返回其运行状态
		 */
		return kafakaOfBeatsCollector.isStarted();
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
	 * cache初始化
	 * 资产/全局配置项
	 * //TODO BICache
	 */
	public void cacheInit(){
		ConfigurationCache.INSTANCE.init(configurationService);
		AssetCache.INSTANCE.init(equipmentService,assetService);
	}
	/**
	 * 定时任务将获取的http url 插入到servicefunction表中
	 * @return
	 */
	/*
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
	*/
	/**
	 * 定时任务将获取的http url 插入到serviceInfo表中
	 * @return
	 */
	public void insertUrl()  {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 资产表获取domain
		//List<Equipment> list = equipmentService.selectAllHostName();

		// 临时serviceinfo list
		List<ServiceInfo> serviceslist = new ArrayList<ServiceInfo>();
		Map<String, String> tmpurlmap = new HashMap<String, String>();
		tmpurlmap.putAll(urlmap);
		urlmap.clear();

		//遍历所有传过来的url信息
		for(Entry<String, String> key : tmpurlmap.entrySet()) {
			ServiceInfo funservice = new  ServiceInfo();
			String protocol = getSubUtilSimple(key.getKey(), "^(.*?)[://]");
			String relativeUrl = getSubUtilSimple(key.getKey(), "[:][0-9]{1,5}(.*?)$");
			funservice.setId(Uuid.getUUID());
			funservice.setCreateTime(format.format(new Date()));
			try{
				URI url = new URI(key.getKey());
				//funservice.setEquipmentId(equipment.getId());
				funservice.setIp(url.getHost());
				funservice.setPort(url.getPort()+"");
			}catch(Exception e){
				e.printStackTrace();
				//出现异常信息，进行标记
				funservice.setDescribe("AbnormalUrl");
			}
			funservice.setProtocol(protocol);
			funservice.setUrl(key.getKey());
			funservice.setRelativeUrl(relativeUrl);
			funservice.setState(1);
			serviceslist.add(funservice);
		}
		//数据为空时，插入数据会报错
		if(serviceslist.size()>0){
			//返回插入的条数
			int count = serviceInfoService.insertIgnore(serviceslist);
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

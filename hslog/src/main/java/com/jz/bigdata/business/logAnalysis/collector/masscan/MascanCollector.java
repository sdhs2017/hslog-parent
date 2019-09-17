package com.jz.bigdata.business.logAnalysis.collector.masscan;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.jz.bigdata.common.assets.entity.Assets;
import com.jz.bigdata.common.assets.service.IAssetsService;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.ExecuteCmd;
import com.jz.bigdata.util.Uuid;

public class MascanCollector implements Runnable {

	private Semaphore semaphore;

	private String IPS;

	private String ports;

	
	public Semaphore getSemaphore() {
		return semaphore;
	}

	public void setSemaphore(Semaphore semaphore) {
		this.semaphore = semaphore;
	}

	public String getIPS() {
		return IPS;
	}

	public void setIPS(String iPS) {
		IPS = iPS;
	}

	public String getPorts() {
		return ports;
	}

	public void setPorts(String ports) {
		this.ports = ports;
	}

	final long awaitTime = 100 * 1000;
	private ExecutorService threadPool=Executors.newCachedThreadPool();
	private IAssetsService assetsService;
	private ConfigProperty configProperty;
	
	public MascanCollector(Semaphore semaphore, String IPS, String ports,IAssetsService assetsService,ConfigProperty configProperty) {
		this.semaphore = semaphore;
		this.IPS = IPS;
		this.ports = ports;
		this.assetsService=assetsService;
		this.configProperty = configProperty;
	}
	public MascanCollector(Semaphore semaphore, String IPS, String ports) {
		this.semaphore = semaphore;
		this.IPS = IPS;
		this.ports = ports;
	}
	

	/**
	 * 重写线程执行内容
	 */
	@Override
	public void run() {

		try {

			// 获取 信号量 执行许可
			semaphore.acquire();
			String filepath = configProperty.getMasscan_path();
			File file = new File(filepath);
			String[] rgexs = { "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}", "Discovered" };
			Map<String, String> result = ExecuteCmd.execCmd("./masscan "+IPS+" -p"+ports, file, rgexs, true);
//			System.out.println(result);
			// 释放 信号量 许可
			semaphore.release();
			Date endtime = new Date();
			String resultIp=result.get("./masscan "+IPS+" -p"+ports);
			String ports = getSubUtilSimple(resultIp, "port\\s+(.*?)[/]");
			resultIp=getSubUtil(resultIp,"\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        String time = format.format(endtime.getTime());//这个就是把时间戳经过处理得到期望格式的时间
	        if(null!=resultIp&&!resultIp.equals("")){
	        	Assets assets =new Assets();
	        	assets.setId(Uuid.getUUID());
	        	assets.setIp(resultIp);
	        	assets.setPorts(ports);
	        	assets.setState("正常");
	        	assets.setResponseDate(format.format(new Date()));;
	        	assets.setCreateTime(time);
	 	        assetsService.insert(assets);
	        }
	       


		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 重写线程执行内容
	 */
	/*@Override
	public void run() {
		
		try {
			
			// 获取 信号量 执行许可
			semaphore.acquire();
			String[] rgexs = { "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}", "Discovered" };
			Map<String, ArrayList<String>> result = ExecuteCmd.execCmd("D:/Computer_Science/IP探测/masscan/masscan "+IPS+" -p"+ports, null, rgexs);
			System.out.println(result);
			// 释放 信号量 许可
			semaphore.release();
			
			StringBuilder stringports = new StringBuilder();
			ArrayList<String> list = result.get("masscan"+IPS);
			if (!list.isEmpty()) {
				for(int i=0; i<list.size();i++) {
					String ports = getSubUtilSimple(list.get(i), "port\\s+(.*?)[/]");
					if ((i+1)<list.size()) {
						stringports.append(ports+",");
					}else{
						stringports.append(ports);
					}
					
					String protocol = getSubUtilSimple(list.get(i), "[/](.*?)\\s+on");
					System.out.println(ports+"   "+protocol);
				};
				System.out.println(stringports);
				
				Date endtime = new Date();
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String time = format.format(endtime.getTime());//这个就是把时间戳经过处理得到期望格式的时间
				Assets assets =new Assets();
				assets.setId(Uuid.getUUID());
				assets.setIp(IPS);
				assets.setPorts(stringports.toString());
				assets.setCreateTime(time);
				assetsService.insert(assets);
			}
			
			
			
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}*/

	public MascanCollector(List<String> list, String ports,IAssetsService assetsService,ConfigProperty configProperty) {

		final Semaphore semaphore = new Semaphore(5);

		for (String ip : list) {
			threadPool.execute(new MascanCollector(semaphore, ip, ports,assetsService,configProperty));
		}
		 threadPool.shutdown();

		/*
		 * try {
		 * 
		 * // (所有的任务都结束的时候，返回TRUE) if(!threadPool.awaitTermination(awaitTime,
		 * TimeUnit.MILLISECONDS)){ // 超时的时候向线程池中所有的线程发出中断(interrupted)。
		 * threadPool.shutdownNow(); } } catch (InterruptedException e) { //
		 * awaitTermination方法被中断的时候也中止线程池中全部的线程的执行。 System.out.println(
		 * "awaitTermination interrupted: " + e); threadPool.shutdownNow(); }
		 */

		if (threadPool.isTerminated()) {
			threadPool.shutdownNow();
		}

	}
	
	public MascanCollector(List<String> list, String ports) {

		final Semaphore semaphore = new Semaphore(5);

		for (String ip : list) {
			threadPool.execute(new MascanCollector(semaphore, ip, ports));
		}
		 threadPool.shutdown();

		if (threadPool.isTerminated()) {
			threadPool.shutdownNow();
		}

	}

	/**
	 * @return
	 * 查看线程运行状态(ture：运行完成  false：线程未运行结束)
	 */
	public Boolean getStarted() {
		return threadPool.isTerminated();
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
         return null;  
    }
 	
 	/**
	 * 正则匹配
	 * @param soap
	 * @param rgex
	 * @return 返回中括号的内容
	 */
 	public static String getSubUtilSimple(String soap, String rgex) {
 		Pattern pattern = Pattern.compile(rgex);// 匹配的模式
 		Matcher m = pattern.matcher(soap);
 		while (m.find()) {
 			return m.group(1);
 		}
 		return null;
 	}
 	
public static void main(String[] args) {
		
		String ports = "1,11,13,15,17,19,21,22,23,25,26,30,31,32,33,34,35,36,37,38,39,43,53,69,70,79,80,81,82,83,84,85,88,98,100,102,110,111,113,119,123,135,137,139,143,161,179,199,214,264,280,322,389,407,443,444,445,449,465,497,500,502,505,510,514,515,517,518,523,540,548,587,591,616,620,623,626,628,631,636,666,731,771,782,783,789,873,888,898,900,901,902,989,990,992,993,994,995,1000,1001,1010,1022,1023,1026,1040,1041,1042,1043,1091,1098,1099,1200,1212,1214,1220,1234,1241,1248,1302,1311,1314,1344,1400,1419,1432,1434,1443,1467,1471,1501,1503,1505,1521,1604,1610,1611,1666,1687,1688,1720,1723,1830,1901,1911,1947,1962,1967,2000,2001,2002,2010,2024,2030,2048,2051,2052,2055,2064,2080,2082,2083,2086,2087,2160,2181,2222,2252,2306,2323,2332,2375,2376,2396,2404,2406,2427,2443,2455,2480,2525,2600,2628,2715,2869,2967,3000,3002,3005,3052,3075,3128,3280,3306,3310,3333,3372,3388,3389,3443,3478,3531,3689,3774,3790,3872,3940,4000,4022,4040,4045,4155,4300,4369,4433,4443,4444,4567,4660,4711,4848,4911,5000,5001,5007,5009,5038,5050,5051,5060,5061,5222,5269,5280,5357,5400,5427,5432,5443,5550,5555,5560,5570,5598,5601,5632,5800,5801,5802,5803,5820,5900,5901,5902,5984,5985,5986,6000,6060,6061,6080,6103,6112,6346,6379,6432,6443,6544,6600,6666,6667,6668,6669,6670,6679,6697,6699,6779,6780,6782,6969,7000,7001,7002,7007,7070,7077,7100,7144,7145,7180,7187,7199,7200,7210,7272,7402,7443,7479,7776,7777,7780,8000,8001,8002,8003,8004,8005,8006,8007,8008,8009,8010,8025,8030,8042,8060,8069,8080,8083,8084,8085,8086,8087,8088,8089,8090,8098,8112,8118,8129,8138,8181,8182,8194,8333,8351,8443,8480,8500,8529,8554,8649,8765,8834,8880,8881,8882,8883,8884,8885,8886,8887,8888,8890,8899,8983,9000,9001,9002,9003,9030,9050,9051,9080,9083,9090,9091,9100,9151,9191,9200,9292,9300,9333,9334,9443,9527,9595,9600,9801,9864,9870,9876,9943,9944,9981,9997,9999,10000,10001,10005,10030,10035,10080,10243,10443,11000,11211,11371,11965,12000,12203,12345,12999,13013,13666,13720,13722,14000,14443,14534,15000,15001,15002,16000,16010,16922,16923,16992,16993,17988,18080,18086,18264,19150,19888,19999,20000,20547,23023,25000,25010,25020,25565,26214,26470,27015,27017,27960,28006,28017,29999,30444,31337,31416,32400,32750,32751,32752,32753,32754,32755,32756,32757,32758,32759,32760,32761,32762,32763,32764,32765,32766,32767,32768,32769,32770,32771,32772,32773,32774,32775,32776,32777,32778,32779,32780,32781,32782,32783,32784,32785,32786,32787,32788,32789,32790,32791,32792,32793,32794,32795,32796,32797,32798,32799,32800,32801,32802,32803,32804,32805,32806,32807,32808,32809,32810,34012,34567,34599,37215,37777,38978,40000,40001,40193,44443,44818,47808,49152,49153,50000,50030,50060,50070,50075,50090,50095,50100,50111,50200,52869,53413,55555,56667,60010,60030,60443,61616,64210,64738";
		String startIP = "2.16.8.0";
 		String [] ips = startIP.split("\\.");
 		ArrayList<String> list = new ArrayList<String>();
 		/*for(int i = 0;i<=255;i++ ) {
 			//Integer.valueOf(ips[3]);
 			list.add(ips[0]+"."+ips[1]+"."+ips[2]+"."+i);
 		}*/
 		list.add("192.168.0.12");
 		/*list.add("192.168.0.11");
 		list.add("192.168.0.10");
 		list.add("192.168.0.9");
 		list.add("192.168.0.8");
 		list.add("192.168.0.7");
 		list.add("192.168.0.6");
 		list.add("192.168.0.5");
 		list.add("192.168.0.4");
 		list.add("192.168.0.3");*/
		String[] rgexs = { "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}", "Discovered" };
		
		
		MascanCollector mascanCollector = new MascanCollector(list, ports);
		
		
		/*Map<String, String> result = ExecuteCmd.execCmd("D:/Computer_Science/IP探测/masscan/masscan 192.168.0.12 -p10-1000", null, rgexs, true);
		System.out.println(result);*/
	}
}

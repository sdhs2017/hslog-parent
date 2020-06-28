package com.jz.bigdata.business.logAnalysis.collector.pcap4j;


import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.jz.bigdata.common.configuration.cache.ConfigurationCache;
import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.pcap4j.core.PcapPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;

import com.google.gson.Gson;
import com.jz.bigdata.business.logAnalysis.log.LogType;
import com.jz.bigdata.business.logAnalysis.log.entity.DefaultPacket;
import com.jz.bigdata.business.logAnalysis.log.entity.Http;
//import com.jz.bigdata.framework.spring.es.elasticsearch.ClientTemplate;
//import com.hs.elsearch.template.bak.ClientTemplate;
import com.jz.bigdata.util.ConfigProperty;

public class PacketStream {

	
	final Logger logger = Logger.getLogger(PacketStream.class);
	
	private Http http;
	private DefaultPacket defaultpacket;
	private Set<String> domainSet ;
	private Map<String, String> urlmap;
	
	boolean iDestroy = false;
	
	ConfigProperty configProperty;
	//ClientTemplate clientTemplate;
	ILogCrudDao logCurdDao;
	Gson gson;
	String json;
	
	private ProtocolListener listener = new TLSProtocolListener();
	private HashMap<String,LinkedList<TcpPacket>> ackSendBuffer=new HashMap<String,LinkedList<TcpPacket>>();
	private HashMap<String,LinkedList<TcpPacket>> ackRecvBuffer=new HashMap<String,LinkedList<TcpPacket>>();

	//BulkRequest requests ;
	List<IndexRequest> requests;
	Cache<Long, Http> httpCache;
	
	public PacketStream(ConfigProperty configProperty,ILogCrudDao logCurdDao,Gson gson,List<IndexRequest> requests,Set<String> domainSet,Map<String, String> urlmap, Cache<Long, Http> httpCache)
	{
		this.configProperty = configProperty;
		this.logCurdDao = logCurdDao;
		this.gson = gson;
		this.requests = requests;
		this.domainSet = domainSet;
		this.urlmap = urlmap;
		this.httpCache = httpCache;
	}
	
	

	public void gotPacket(PcapPacket packet){
		
		try {
			//TcpPacket tcppacket =packet.getBuilder().getPayloadBuilder().build().get(TcpPacket.class);
			IpV4Packet ip4packet =packet.get(IpV4Packet.class);
			packet.getTimestamp();
			// 识别http数据包的正则表达式
			String httpRequest = "^(POST|GET) /[^\\s]* HTTP/1.[0,1]";
			String httpResponse = "^HTTP/1.[0,1] [0-9]{0,3} *";

			// index名称定义
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			//String index = configProperty.getEs_old_index().replace("*",format.format(new Date()));

			// 通过ip4packet包的header信息确认流量包是什么协议，该判断条件为是TCP协议
			if (ip4packet.getHeader().getProtocol().toString().contains("TCP")) {
				TcpPacket tcpPacket = packet.get(TcpPacket.class);
				String dst_port = tcpPacket.getHeader().getDstPort().valueAsString();
				// 判断TCP流量包的payload是否为null，如果不为空则继续解析payload中的内容
				if (tcpPacket.getPayload()!=null) {
					String payloadString = tcpPacket.getPayload().toString().substring(tcpPacket.getPayload().toString().indexOf(":")+1).trim();
					// 通过正则判断应用层协议是否为http
					if ((getSubUtil(hexStringToString(payloadString), httpRequest)!=""||getSubUtil(hexStringToString(payloadString), httpResponse)!="")&&!dst_port.equals("9300")) {
						try {
							http =new Http(packet);
							http.setHslog_type(LogType.LOGTYPE_DEFAULTPACKET);
							if (http.getDomain_url()!=null&&!http.getDomain_url().equals("")) {
								domainSet.add(http.getDomain_url());
							}
							if (http.getRequest_url()!=null&&!http.getRequest_url().equals("")) {
								urlmap.put("http://"+http.getIpv4_dst_addr()+":"+http.getL4_dst_port()+""+http.getRequest_url(), http.getDomain_url());
							}
							// 如果http协议是request的请求，则将数据存入到caffeine的cache中
							if (http.getRequestorresponse()!=null&&http.getRequestorresponse().equals("request")){
								//TODO 对于不符合要求的request进行处理
								if(http.getPacket_length()>=1460||(http.getDomain_url().indexOf("_bulk")>=0)){
									int a=1;
									//System.out.println(http.getAcknum()+"----"+http.getSession_status());
									//httpCache.put(http.getNextacknum(),http);
								}else{
									httpCache.put(http.getNextacknum(),http);
								}

							}else if (http.getRequestorresponse()!=null&&http.getRequestorresponse().equals("response")){
								// 通过response的acknum查找缓存区对应request数据，且触发caffeine的过期机制（将时间过期数据从缓存中移除）
								Http httprequest = httpCache.getIfPresent(http.getAcknum());
								// 当request的数据存在时进行：1、request/response配对；2、计算响应时间
								// 当request不存在时，直接将response数据入库
								if (httprequest!=null){
								    // 生成uuid进行request/response打标签
								    httprequest.setFlag(UUID.randomUUID().toString());
									http.setFlag(httprequest.getFlag());
									// 将计算的响应时间存入request和response
									httprequest.setResponsetime(http.getLogdate().getTime() - httprequest.getLogdate().getTime());
									http.setResponsetime(http.getLogdate().getTime() - httprequest.getLogdate().getTime());
									//httpCache.put(httprequest.getNextacknum(),httprequest);
									httpCache.invalidate(httprequest.getNextacknum());

									// request数据入库
									json = gson.toJson(httprequest);
									requests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),http.getIndex_suffix(),http.getLogdate()), LogType.LOGTYPE_DEFAULTPACKET, json));
								}else{
								    http.setFlag("unmatched");
                                }
                                // response数据入库
								json = gson.toJson(http);
								requests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),http.getIndex_suffix(),http.getLogdate()), LogType.LOGTYPE_DEFAULTPACKET, json));
							}


						} catch (Exception e) {
							System.out.println("---------------范式化失败·······---------"+e.getMessage());
							e.printStackTrace();
						}
						
					}else {
						defaultpacket = new DefaultPacket(packet);
						if("ACK".equals(defaultpacket.getSession_status())&&defaultpacket.getPacket_length()!=1404&&defaultpacket.getPacket_length()!=1424){
							int a=0;
						}
						//System.out.println(defaultpacket.getSession_status()+"---"+defaultpacket.getPacket_length());
						defaultpacket.setHslog_type(LogType.LOGTYPE_DEFAULTPACKET);
						if (defaultpacket.getApplication_layer_protocol()!=null&&defaultpacket.getApplication_layer_protocol().equals("HTTPS")) {
							defaultpacket.setEncryption_based_protection_protocol(GetEncryptionProtocol(packet));
						}
						json = gson.toJson(defaultpacket);
						//requests.add(clientTemplate.insertNo(index, LogType.LOGTYPE_DEFAULTPACKET, json));
						//requests.add(logCurdDao.insertNotCommit(index, LogType.LOGTYPE_DEFAULTPACKET, json));
						requests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),defaultpacket.getIndex_suffix(),defaultpacket.getLogdate()), LogType.LOGTYPE_DEFAULTPACKET, json));
					}
				}
				
			}else {
				defaultpacket = new DefaultPacket(packet);
				defaultpacket.setHslog_type(LogType.LOGTYPE_DEFAULTPACKET);
				if (defaultpacket.getApplication_layer_protocol()!=null&&defaultpacket.getApplication_layer_protocol().equals("HTTPS")) {
					defaultpacket.setEncryption_based_protection_protocol(GetEncryptionProtocol(packet));
				}
				json = gson.toJson(defaultpacket);
				//requests.add(clientTemplate.insertNo(index, LogType.LOGTYPE_DEFAULTPACKET, json));
				requests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),defaultpacket.getIndex_suffix(),defaultpacket.getLogdate()), LogType.LOGTYPE_DEFAULTPACKET, json));
			}

			Object es_bulk = ConfigurationCache.INSTANCE.getConfigurationCache().getIfPresent("es_bulk");
			if (requests.size()>= (es_bulk!=null?Integer.parseInt(es_bulk.toString()):0)) {
				try {
					logCurdDao.bulkInsert(requests);
					//System.out.println("caffine length-----------"+httpCache.asMap().size());
					requests.clear();
				} catch (Exception e) {
					//logger.error("----------------jiyourui-----clientTemplate.bulk------报错信息：-----"+e.getMessage());
					e.printStackTrace();
				}
				
			}
		} catch (NumberFormatException ee){
            defaultpacket = new DefaultPacket(packet);
            defaultpacket.setHslog_type(LogType.LOGTYPE_DEFAULTPACKET);
            defaultpacket.setOperation_des("jiyourui");
            if (defaultpacket.getApplication_layer_protocol()!=null&&defaultpacket.getApplication_layer_protocol().equals("HTTPS")) {
                defaultpacket.setEncryption_based_protection_protocol(GetEncryptionProtocol(packet));
            }
            json = gson.toJson(defaultpacket);
            logger.error(json);
            try {
            	System.out.println("异常数据，提示requests size:" + requests.size());
                requests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_old_index(),defaultpacket.getIndex_suffix(),defaultpacket.getLogdate()), LogType.LOGTYPE_DEFAULTPACKET, json));
                logCurdDao.bulkInsert(requests);
                requests.clear();
                System.out.println("异常数据采集后提交，提示requests size:" + requests.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
            ee.printStackTrace();
        } catch (Exception e) {
			//logger.error("----------------jiyourui-----gotPacket------报错信息：-----"+e.getMessage());
			e.printStackTrace();
		}
		



	
	
		
	}
	
	public boolean enDestroy()
	{
		return iDestroy;
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
 	 * 16进制转换成为string类型字符串
 	 * @param s
 	 * @return
 	 */
 	public static String hexStringToString(String s) throws Exception {

 	    if (s == null || s.equals("")) {
 	        return null;
 	    }
 	    s = s.replace(" ", "");
 	    byte[] baKeyword = new byte[s.length() / 2];
 	    for (int i = 0; i < baKeyword.length; i++) {
 	    	baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
 	    }
 	    s = new String(baKeyword, "UTF-8");
 	    return s;
 	}
 	
 	/**
 	 * @param packet
 	 * @return
 	 * 获取加密协议
 	 */
 	public  String GetEncryptionProtocol(Packet packet){
		TcpPacket tcppacket = packet.getBuilder().getPayloadBuilder().build().get(TcpPacket.class);
		IpV4Packet ip4packet = packet.get(IpV4Packet.class);
		//定义seq
		String ackSeq = "";
		String server = "";
		//获取ackseq
		ackSeq = Long.toString(tcppacket.getHeader().getAcknowledgmentNumberAsLong());

		boolean isSend = false;
		//是否存在serverIP
		if (ip4packet.getHeader().getDstAddr().toString().contains(server)) {
			isSend = true;
		} else {
			isSend = false;
		}
		//这个包是带数据的，发送端告诉接收端，
		//这个数据包以及以前接收到的数据包需要交给应用层立即进行处理
		String version="";
		if (tcppacket.getHeader().getPsh()) {
			//是否包含serviceIp
			if (!isSend) {
				LinkedList<TcpPacket> ps = ackSendBuffer.get(ackSeq);
				if (ps == null) {
					ps = new LinkedList<TcpPacket>();
					ackSendBuffer.put(ackSeq, ps);
				}
				ps.add(tcppacket);
				//获取count
				int sendCount = tcppacket.getHeader().getSequenceNumber()
						- tcppacket.getHeader().getSequenceNumber() + tcppacket.getRawData().length
						- tcppacket.getHeader().getRawData().length;
				byte[] buffer = new byte[sendCount];
				int pos = 0;
				//遍历packet
				for (TcpPacket p : ackSendBuffer.get(ackSeq)) {
					//获取长度
					int packdatalength = p.getRawData().length - p.getHeader().getRawData().length;
					//长度为0跳出循环
					if (packdatalength == 0)
						continue;
					//复制数组
					System.arraycopy(p.getRawData(), p.getHeader().getRawData().length, buffer, pos,
							packdatalength);
					pos += packdatalength;
				}
				//获取秘钥信息
				version =this.listener.onSend(ackSeq, buffer);
				buffer = null;
				//删除map
				ackSendBuffer.remove(ackSeq);
//				return version;
			} else {
				//舒适化赋值
				LinkedList<TcpPacket> ps = ackRecvBuffer.get(ackSeq);
				if (ps == null) {
					ps = new LinkedList<TcpPacket>();
					ackRecvBuffer.put(ackSeq, ps);
				}
				ps.add(tcppacket);
				//获取count
				int recvCount = tcppacket.getHeader().getSequenceNumber()
						- tcppacket.getHeader().getSequenceNumber() + tcppacket.getRawData().length
						- tcppacket.getHeader().getRawData().length;
				byte[] buffer = new byte[recvCount];
				int pos = 0;
				//遍历packet
				for (TcpPacket p : ackRecvBuffer.get(ackSeq)) {
					//获取数据长度
					int packdatalength = p.getRawData().length - p.getHeader().getRawData().length;
					if (packdatalength == 0)
						continue;
					//复制数组
					System.arraycopy(p.getRawData(), p.getHeader().getRawData().length, buffer, pos,
							packdatalength);
					pos += packdatalength;
				}
				//获取秘钥信息
				version=this.listener.onRecv(ackSeq, buffer);
				buffer = null;
				//删除map
				ackRecvBuffer.remove(ackSeq);
//				return version;
			}

		}
		if (tcppacket.getHeader().getFin()) {
			iDestroy = true;
		}
		return version;
	}
 	
 	public static void main(String [] args) {
 		Gson gson = new Gson();
 		//模拟数据，beat穿过来的一条数据
 		String data = "{\"@timestamp\":\"2020-03-16T09:05:13.735Z\",\"@metadata\":{\"beat\":\"winlogbeat\",\"type\":\"_doc\",\"version\":\"7.6.0\"},\"host\":{\"name\":\"ZHANGYIYANG\",\"id\":\"d2c6f258-099c-466c-b748-c0e71faae209\",\"hostname\":\"ZHANGYIYANG\",\"architecture\":\"x86_64\",\"os\":{\"platform\":\"windows\",\"version\":\"10.0\",\"family\":\"windows\",\"name\":\"Windows 10 Enterprise 2016 LTSB\",\"kernel\":\"10.0.14393.2395 (rs1_release_inmarket.180714-1932)\",\"build\":\"14393.2395\"}},\"log\":{\"level\":\"信息\"},\"message\":\"帐户登录失败。\\n\\n使用者:\\n\\t安全 ID:\\t\\tS-1-0-0\\n\\t帐户名:\\t\\t-\\n\\t帐户域:\\t\\t-\\n\\t登录 ID:\\t\\t0x0\\n\\n登录类型:\\t\\t\\t3\\n\\n登录失败的帐户:\\n\\t安全 ID:\\t\\tS-1-0-0\\n\\t帐户名:\\t\\tliangsht\\n\\t帐户域:\\t\\t.\\n\\n失败信息:\\n\\t失败原因:\\t\\t未知用户名或密码错误。\\n\\t状态:\\t\\t\\t0xC000006D\\n\\t子状态:\\t\\t0xC0000064\\n\\n进程信息:\\n\\t调用方进程 ID:\\t0x0\\n\\t调用方进程名:\\t-\\n\\n网络信息:\\n\\t工作站名:\\tLIANGSHANTING\\n\\t源网络地址:\\t192.168.200.49\\n\\t源端口:\\t\\t32773\\n\\n详细身份验证信息:\\n\\t登录进程:\\t\\tNtLmSsp \\n\\t身份验证数据包:\\tNTLM\\n\\t传递服务:\\t-\\n\\t数据包名(仅限 NTLM):\\t-\\n\\t密钥长度:\\t\\t0\\n\\n登录请求失败时在尝试访问的计算机上生成此事件。\\n\\n“使用者”字段指明本地系统上请求登录的帐户。这通常是一个服务(例如 Server 服务)或本地进程(例如 Winlogon.exe 或 Services.exe)。\\n\\n“登录类型”字段指明发生的登录的种类。最常见的类型是 2 (交互式)和 3 (网络)。\\n\\n“进程信息”字段表明系统上的哪个帐户和进程请求了登录。\\n\\n“网络信息”字段指明远程登录请求来自哪里。“工作站名”并非总是可用，而且在某些情况下可能会留为空白。\\n\\n“身份验证信息”字段提供关于此特定登录请求的详细信息。\\n\\t-“传递服务”指明哪些直接服务参与了此登录请求。\\n\\t-“数据包名”指明在 NTLM 协议之间使用了哪些子协议。\\n\\t-“密钥长度”指明生成的会话密钥的长度。如果没有请求会话密钥，则此字段为 0。\",\"winlog\":{\"task\":\"登录\",\"event_data\":{\"TargetDomainName\":\".\",\"SubjectDomainName\":\"-\",\"SubjectUserName\":\"-\",\"TargetUserSid\":\"S-1-0-0\",\"SubStatus\":\"0xc0000064\",\"TargetUserName\":\"liangsht\",\"SubjectLogonId\":\"0x0\",\"Status\":\"0xc000006d\",\"KeyLength\":\"0\",\"LogonProcessName\":\"NtLmSsp \",\"TransmittedServices\":\"-\",\"SubjectUserSid\":\"S-1-0-0\",\"LmPackageName\":\"-\",\"LogonType\":\"3\",\"AuthenticationPackageName\":\"NTLM\",\"FailureReason\":\"%%2313\"},\"computer_name\":\"ZHANGYIYANG\",\"keywords\":[\"审核失败\"],\"record_id\":175748,\"provider_guid\":\"{54849625-5478-4994-A5BA-3E3B0328C30D}\",\"activity_id\":\"{F02C442B-FB3F-0003-3A44-2CF03FFBD501}\",\"process\":{\"pid\":900,\"thread\":{\"id\":10804}},\"provider_name\":\"Microsoft-Windows-Security-Auditing\",\"channel\":\"Security\",\"api\":\"wineventlog\",\"logon\":{\"type\":\"Network\",\"failure\":{\"sub_status\":\"User logon with misspelled or bad user account\",\"reason\":\"Unknown user name or bad password.\",\"status\":\"This is either due to a bad username or authentication information\"}},\"event_id\":4625,\"opcode\":\"信息\"},\"event\":{\"kind\":\"event\",\"provider\":\"Microsoft-Windows-Security-Auditing\",\"module\":\"security\",\"outcome\":\"failure\",\"code\":4625,\"action\":\"logon-failed\",\"created\":\"2020-03-16T09:17:55.378Z\",\"category\":\"authentication\",\"type\":\"authentication_failure\"},\"source\":{\"ip\":\"192.168.200.49\",\"port\":32773,\"domain\":\"LIANGSHANTING\"},\"ecs\":{\"version\":\"1.4.0\"},\"agent\":{\"type\":\"winlogbeat\",\"ephemeral_id\":\"61d9a3c0-0e20-4959-b0de-906087d0f68e\",\"hostname\":\"ZHANGYIYANG\",\"id\":\"30aceb8a-496f-429e-bfc0-f383734c0eac\",\"version\":\"7.6.0\"},\"user\":{\"id\":\"S-1-0-0\",\"name\":\"liangsht\",\"domain\":\".\"},\"process\":{\"executable\":\"-\",\"name\":\"-\",\"pid\":0}}";
 		JsonElement je = new JsonParser().parse(data);
		//转成JsonObject
		JsonObject jobj = je.getAsJsonObject();
		//通过key查value
		System.out.println(jobj.get("message"));
		//多层的key查value
		System.out.println(jobj.getAsJsonObject("winlog").get("task"));
		//插入一个K/V
		jobj.addProperty("test","123");
		System.out.println(jobj.toString());
		/*String payload = "HTTP/1.1 200 Content-Disposition: inline;filename=f.txt Content-Type: application/json;charset=utf-8 Content-Length";
 		String pString = "POST /jzLog/collector/statePcap4jCollector.do HTTP/1.1 Host: 192.168.2.182:8080 User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0 Accept";
		// 识别http数据包的正则表达式
		String httpRequest = "^(POST|GET) /[^\\s]* HTTP/1.[0,1]";
		String httpResponse = "^HTTP/1.[0,1] [0-9]{0,3} *";
		if ((getSubUtil(payload, httpRequest)!=""||getSubUtil(payload, httpResponse)!="")) {
			System.out.println(payload);
		}
		if ((getSubUtil(pString, httpRequest)!=""||getSubUtil(pString, httpResponse)!="")) {
			System.out.println(pString);
		}*/
		/*
		String payload = "[Illegal Packet (45 bytes)]\n" +
				"  Hex stream: 00 2b fa 73 01 00 00 01 00 00 00 00 00 00 02 32 39 02 36 38 03 31 38 30 02 38 31 07 69 6e 2d 61 64 64 72 04 61 72 70 61 00 00 0c 00 01\n" +
				"  Cause: org.pcap4j.packet.IllegalRawDataException: The data is too short to build a question in DnsHeader. data: fe ee 0b ca e5 69 52 54 00 b4 e4 09 08 00 45 00 00 55 49 80 40 00 40 06 3a b5 ac 15 00 09 b7 3c 53 13 d4 c6 00 35 27 17 3a de 2a bc 19 72 50 18 00 e5 55 74 00 00 00 2b fa 73 01 00 00 01 00 00 00 00 00 00 02 32 39 02 36 38 03 31 38 30 02 38 31 07 69 6e 2d 61 64 64 72 04 61 72 70 61 00 00 0c 00 01, offset: 54, length: 45, cursor: 45";
		String sss = payload.substring(payload.indexOf(":")+1).trim();
		System.out.println(sss);

		String hexstrem = "525400b4e409feee0bcae5690800456801a4915a4000fb0644aa3ae3c0e5ac1500090035dc0cf2c337ea6c80dc2c801800f3435400000101080a7017ad8b2daefdc8";

		try {
			String s = hexStringToString(hexstrem);
			System.out.println(s);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
}

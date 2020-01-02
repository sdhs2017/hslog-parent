package com.jz.bigdata.business.logAnalysis.collector.pcap4j;


import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hs.elsearch.dao.logDao.ILogCrudDao;
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
	
	public PacketStream(ConfigProperty configProperty,ILogCrudDao logCurdDao,Gson gson,List<IndexRequest> requests,Set<String> domainSet,Map<String, String> urlmap)
	{
		this.configProperty = configProperty;
		this.logCurdDao = logCurdDao;
		this.gson = gson;
		this.requests = requests;
		this.domainSet = domainSet;
		this.urlmap = urlmap;
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
			//String index = configProperty.getEs_index().replace("*",format.format(new Date()));

			if (ip4packet.getHeader().getProtocol().toString().contains("TCP")) {
				TcpPacket tcpPacket = packet.get(TcpPacket.class);
				String dst_port = tcpPacket.getHeader().getDstPort().valueAsString();
				if (tcpPacket.getPayload()!=null) {
					String payloadString = tcpPacket.getPayload().toString().substring(tcpPacket.getPayload().toString().indexOf(":")+1).trim();
					if ((getSubUtil(hexStringToString(payloadString), httpRequest)!=""||getSubUtil(hexStringToString(payloadString), httpResponse)!="")&&!dst_port.equals("9300")) {
						try {
							http =new Http(packet);
							http.setHslog_type(LogType.LOGTYPE_DEFAULTPACKET);
							if (http.getDomain_url()!=null&&!http.getDomain_url().equals("")) {
								domainSet.add(http.getDomain_url());
							}
							if (http.getRequest_url()!=null&&!http.getRequest_url().equals("")) {
								urlmap.put("http://"+http.getIpv4_dst_addr()+":"+http.getL4_dst_port()+""+http.getRequest_url(), http.getDomain_url());
								//System.out.println("--urlmap---"+urlmap);
							}
							json = gson.toJson(http);
							//requests.add(clientTemplate.insertNo(configProperty.getEs_index(), LogType.LOGTYPE_DEFAULTPACKET, json));
							//requests.add(logCurdDao.insertNotCommit(index, LogType.LOGTYPE_DEFAULTPACKET, json));
							requests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_index(),http.getIndex_suffix(),http.getLogdate()), LogType.LOGTYPE_DEFAULTPACKET, json));
						} catch (Exception e) {
							System.out.println("---------------范式化失败·······---------"+e.getMessage());
							e.printStackTrace();
						}
						
					}else {
						defaultpacket = new DefaultPacket(packet);
						defaultpacket.setHslog_type(LogType.LOGTYPE_DEFAULTPACKET);
						if (defaultpacket.getApplication_layer_protocol()!=null&&defaultpacket.getApplication_layer_protocol().equals("HTTPS")) {
							defaultpacket.setEncryption_based_protection_protocol(GetEncryptionProtocol(packet));
						}
						json = gson.toJson(defaultpacket);
						//requests.add(clientTemplate.insertNo(index, LogType.LOGTYPE_DEFAULTPACKET, json));
						//requests.add(logCurdDao.insertNotCommit(index, LogType.LOGTYPE_DEFAULTPACKET, json));
						requests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_index(),defaultpacket.getIndex_suffix(),defaultpacket.getLogdate()), LogType.LOGTYPE_DEFAULTPACKET, json));
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
				requests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_index(),defaultpacket.getIndex_suffix(),defaultpacket.getLogdate()), LogType.LOGTYPE_DEFAULTPACKET, json));
			}
			
		
			if (requests.size()==configProperty.getEs_bulk()) {
				try {
					logCurdDao.bulkInsert(requests);
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
                requests.add(logCurdDao.insertNotCommit(logCurdDao.checkOfIndex(configProperty.getEs_index(),defaultpacket.getIndex_suffix(),defaultpacket.getLogdate()), LogType.LOGTYPE_DEFAULTPACKET, json));
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
		}
	}
}

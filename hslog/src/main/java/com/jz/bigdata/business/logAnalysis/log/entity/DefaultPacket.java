package com.jz.bigdata.business.logAnalysis.log.entity;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

import com.google.gson.Gson;
import com.jz.bigdata.business.logAnalysis.log.LogType;


/**
 * @ClassName DefaultPacket
 * @Description 网络流量数据包 范式化
 * @Author 
 */
/**
 * @author jiyourui
 *
 */
public class DefaultPacket {


	/**
	 * id
	 */
	private String id;
	/**
	 * userid
	 */
	private String userid;
	/**
	 * deptid
	 */
	private String deptid;
	/**
	 * equipmentid
	 */
	private String equipmentid;
	/**
	 * equipmentname
	 */
	private String equipmentname;
	/**
	 * 日志时间
	 */
	private Date logdate;
	private String logtime;
	/*private String logtime_minute;
	private String logtime_hour;
	private String logtime_day;
	private String logtime_month;
	private String logtime_year;*/
	/**
	 * 操作类型
	 */
	private String operation_level;
	/**
	 * ip地址
	 */
	private String ip;
	/**
	 * 操作描述
	 */
	private String operation_des;
	/**
	 * 日志类型
	 */
	private String hslog_type;

	/**
	 * 设置index名称的后缀
	 */
	private String index_suffix;
	
	// -------------数据包关键属性---------------
	/**
	 * 源端口
	 */
	private String l4_src_port;
	/**
	 * 目的端口
	 */
	private String l4_dst_port;
	/**
	 * 目的地址
	 */
	private String ipv4_dst_addr;
	/**
	 * 原地址
	 */
	private String ipv4_src_addr;
	/**
	 * 协议值
	 */
	private String protocol;
	/**
	 * 协议名称
	 */
	private String protocol_name;
	/**
	 * 应用层协议
	 */
	private String application_layer_protocol;
	/**
	 * 加密保护协议
	 */
	private String encryption_based_protection_protocol;
	/**
	 * 有效负载数据包
	 */
	private String payload;
	/**
	 * 数据包来源
	 */
	private String packet_source;
	
	private String acknum; // tcp 确认号
	private String seqnum; // tcp 顺序号

	// -------------http数据包属性---------------
	private String requestorresponse;//请求或返回
	private String request_url;//请求地址
	private String response_state;//返回状态
	private String request_type;//请求类型
	private String complete_url; // 完整的url http://192.168.2.182:8080/jzLog/getIndicesCount.do?_=1555924017369
	private String url_param; // url参数
	private String domain_url; // 域名

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getDeptid() {
		return deptid;
	}

	public void setDeptid(String deptid) {
		this.deptid = deptid;
	}

	public String getEquipmentid() {
		return equipmentid;
	}

	public void setEquipmentid(String equipmentid) {
		this.equipmentid = equipmentid;
	}

	public String getEquipmentname() {
		return equipmentname;
	}

	public void setEquipmentname(String equipmentname) {
		this.equipmentname = equipmentname;
	}

	public Date getLogdate() {
		return logdate;
	}

	public void setLogdate(Date logdate) {
		this.logdate = logdate;
	}

	public String getLogtime() {
		return logtime;
	}

	public void setLogtime(String logtime) {
		this.logtime = logtime;
	}

	/*public String getLogtime_minute() {
		return logtime_minute;
	}

	public void setLogtime_minute(String logtime_minute) {
		this.logtime_minute = logtime_minute;
	}

	public String getLogtime_hour() {
		return logtime_hour;
	}

	public void setLogtime_hour(String logtime_hour) {
		this.logtime_hour = logtime_hour;
	}

	public String getLogtime_day() {
		return logtime_day;
	}

	public void setLogtime_day(String logtime_day) {
		this.logtime_day = logtime_day;
	}

	public String getLogtime_month() {
		return logtime_month;
	}

	public void setLogtime_month(String logtime_month) {
		this.logtime_month = logtime_month;
	}

	public String getLogtime_year() {
		return logtime_year;
	}

	public void setLogtime_year(String logtime_year) {
		this.logtime_year = logtime_year;
	}*/

	public String getOperation_level() {
		return operation_level;
	}

	public void setOperation_level(String operation_level) {
		this.operation_level = operation_level;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getOperation_des() {
		return operation_des;
	}

	public void setOperation_des(String operation_des) {
		this.operation_des = operation_des;
	}

	public String getHslog_type() {
		return hslog_type;
	}

	public void setHslog_type(String hslog_type) {
		this.hslog_type = hslog_type;
	}

	public String getIndex_suffix() {
		return index_suffix;
	}

	public void setIndex_suffix(String index_suffix) {
		this.index_suffix = index_suffix;
	}

	public String getL4_src_port() {
		return l4_src_port;
	}

	public void setL4_src_port(String l4_src_port) {
		this.l4_src_port = l4_src_port;
	}

	public String getL4_dst_port() {
		return l4_dst_port;
	}

	public void setL4_dst_port(String l4_dst_port) {
		this.l4_dst_port = l4_dst_port;
	}

	public String getIpv4_dst_addr() {
		return ipv4_dst_addr;
	}

	public void setIpv4_dst_addr(String ipv4_dst_addr) {
		this.ipv4_dst_addr = ipv4_dst_addr;
	}

	public String getIpv4_src_addr() {
		return ipv4_src_addr;
	}

	public void setIpv4_src_addr(String ipv4_src_addr) {
		this.ipv4_src_addr = ipv4_src_addr;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getProtocol_name() {
		return protocol_name;
	}

	public void setProtocol_name(String protocol_name) {
		this.protocol_name = protocol_name;
	}

	public String getApplication_layer_protocol() {
		return application_layer_protocol;
	}

	public void setApplication_layer_protocol(String application_layer_protocol) {
		this.application_layer_protocol = application_layer_protocol;
	}

	public String getEncryption_based_protection_protocol() {
		return encryption_based_protection_protocol;
	}

	public void setEncryption_based_protection_protocol(String encryption_based_protection_protocol) {
		this.encryption_based_protection_protocol = encryption_based_protection_protocol;
	}
	
	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getPacket_source() {
		return packet_source;
	}

	public void setPacket_source(String packet_source) {
		this.packet_source = packet_source;
	}

	public String getAcknum() {
		return acknum;
	}

	public void setAcknum(String acknum) {
		this.acknum = acknum;
	}

	public String getSeqnum() {
		return seqnum;
	}

	public void setSeqnum(String seqnum) {
		this.seqnum = seqnum;
	}

	public String getRequestorresponse() {
		return requestorresponse;
	}

	public void setRequestorresponse(String requestorresponse) {
		this.requestorresponse = requestorresponse;
	}

	public String getRequest_url() {
		return request_url;
	}

	public void setRequest_url(String request_url) {
		this.request_url = request_url;
	}

	public String getResponse_state() {
		return response_state;
	}

	public void setResponse_state(String response_state) {
		this.response_state = response_state;
	}

	public String getRequest_type() {
		return request_type;
	}

	public void setRequest_type(String request_type) {
		this.request_type = request_type;
	}

	public String getComplete_url() {
		return complete_url;
	}

	public void setComplete_url(String complete_url) {
		this.complete_url = complete_url;
	}

	public String getUrl_param() {
		return url_param;
	}

	public void setUrl_param(String url_param) {
		this.url_param = url_param;
	}

	public String getDomain_url() {
		return domain_url;
	}

	public void setDomain_url(String domain_url) {
		this.domain_url = domain_url;
	}

	public DefaultPacket(){
		
	}
	
	public DefaultPacket(Packet packet) {
		
		IpV4Packet ip4packet =packet.get(IpV4Packet.class);
		//System.out.println("协议值："+ip4packet.getHeader().getProtocol());
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date nowtime = new Date();
		this.logdate = nowtime;
		this.logtime = format.format(nowtime);
		/*String [] tmp = this.logtime.split(" ");
    	String [] date = tmp[0].split("-");
    	String [] time = tmp[1].split(":");
    	this.logtime_year = date[0];
    	this.logtime_month = date[1];
    	this.logtime_day = date[2];
    	this.logtime_hour = time[0];
    	this.logtime_minute = time[1];*/
		
		
		// 通过ipv4数据包中的协议值来选择解析数据包的协议方法
		if (ip4packet.getHeader().getProtocol().toString().contains("TCP")) {
			
			TcpPacket tcppacket = packet.getBuilder().getPayloadBuilder().build().get(TcpPacket.class);
			
			this.ipv4_dst_addr = ip4packet.getHeader().getDstAddr().toString().replaceAll("/", "");
			this.ipv4_src_addr = ip4packet.getHeader().getSrcAddr().toString().replaceAll("/", "");
			this.l4_dst_port = tcppacket.getHeader().getDstPort().valueAsInt()+"";
			this.l4_src_port = tcppacket.getHeader().getSrcPort().valueAsInt()+"";
			
			this.acknum = tcppacket.getHeader().getAcknowledgmentNumber()+"";
			this.seqnum = tcppacket.getHeader().getSequenceNumberAsLong()+"";
			
			this.protocol="6";
			this.protocol_name="TCP";
			if (tcppacket.getPayload()!=null) {
				this.payload = tcppacket.getPayload().toString();
			}
			
			String hexstring = tcppacket.toHexString().replaceAll(" ", "");
			if (hexstring.contains("170303")||hexstring.contains("160301")||hexstring.contains("150303")||hexstring.contains("160303")||hexstring.contains("140303")) {
				if (tcppacket.getHeader().getAck()&&tcppacket.getHeader().getPsh()) {
					this.application_layer_protocol = "HTTPS";
				}else if (tcppacket.getHeader().getAck()&&hexstring.indexOf("170303")>40) {
					this.application_layer_protocol = "HTTPS";
				}else{
					this.application_layer_protocol = "";
				}
			}
			
			
		}else if (ip4packet.getHeader().getProtocol().toString().contains("UDP")) {
			UdpPacket udpPacket = packet.getBuilder().getPayloadBuilder().build().get(UdpPacket.class);
			this.ipv4_dst_addr = ip4packet.getHeader().getDstAddr().toString().replaceAll("/", "");
			this.ipv4_src_addr = ip4packet.getHeader().getSrcAddr().toString().replaceAll("/", "");
			this.l4_dst_port = udpPacket.getHeader().getDstPort().valueAsInt()+"";
			this.l4_src_port = udpPacket.getHeader().getSrcPort().valueAsInt()+"";
			
			this.protocol="17";
			this.protocol_name="UDP";
			this.payload = udpPacket.getPayload().toString();
			
		}else if (ip4packet.getHeader().getProtocol().toString().contains("ICMPv4")) {
			this.ipv4_dst_addr = ip4packet.getHeader().getDstAddr().toString().replaceAll("/", "");
			this.ipv4_src_addr = ip4packet.getHeader().getSrcAddr().toString().replaceAll("/", "");
			
			this.protocol="1";
			this.protocol_name="ICMPv4";
			//this.payload = udpPacket.getPayload().toString();
		}else {
			System.out.println("协议值："+ip4packet.getHeader().getProtocol());
		}
		this.packet_source = "libpcap";

		this.index_suffix = "packet";
	}
	
	public String toMapping() {
		String template = "{\n" + "\t\t\"properties\":{\n" + "\t\t{#}\n" + "\t\t\t\t}" + "}";
		String fieldString = getClassMapping(new DefaultPacket());
		template = template.replace("{#}", fieldString);
		return template;
	}

	public <T> String getClassMapping(T classes) {

		StringBuilder fieldstring = new StringBuilder();

		Field[] fields = classes.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			fieldstring.append("\t\t\t\t\"" + fields[i].getName().toLowerCase() + "\": {\n");
			fieldstring.append("\t\t\t\t\t\t\"type\": \""
					+ GetElasticSearchMappingType(fields[i].getType().getSimpleName(), fields[i].getName()) + "\n");
			if (fields[i].getName().equals("id")) {
				fieldstring.append("\t\t\t\t\t\t,\"index\": \"" + "false\"" + "\n");
			}
			if (fields[i].getName().equals("userid") || fields[i].getName().equals("deptid")
					|| fields[i].getName().equals("equipmentid") || fields[i].getName().equals("equipmentname")
					|| fields[i].getName().equals("logtime") || fields[i].getName().equals("logtime_minute")
					|| fields[i].getName().equals("logtime_hour") || fields[i].getName().equals("logtime_day")
					|| fields[i].getName().equals("logtime_month") || fields[i].getName().equals("logtime_year")
					|| fields[i].getName().equals("operation_level") || fields[i].getName().equals("ip")
					|| fields[i].getName().equals("ipv4_dst_addr") || fields[i].getName().equals("ipv4_src_addr")
					|| fields[i].getName().equals("l4_src_port")|| fields[i].getName().equals("l4_dst_port")
					|| fields[i].getName().equals("protocol")|| fields[i].getName().equals("protocol_name")
					|| fields[i].getName().equals("application_layer_protocol")|| fields[i].getName().equals("encryption_based_protection_protocol")
					|| fields[i].getName().equals("packet_source")
					// http 需要聚合的字段值
					|| fields[i].getName().equals("request_type")|| fields[i].getName().equals("domain_url")
					|| fields[i].getName().equals("complete_url")|| fields[i].getName().equals("url_param")
					|| fields[i].getName().equals("request_url")|| fields[i].getName().equals("response_state")) {
				fieldstring.append("\t\t\t\t\t\t,\"fielddata\": " + "true" + "\n");
			}
			if (fields[i].getName().equals("operation_des") || fields[i].getName().equals("ip")
					|| fields[i].getName().equals("equipmentname") || fields[i].getName().equals("ipv4_dst_addr")
					|| fields[i].getName().equals("ipv4_src_addr") || fields[i].getName().equals("l4_src_port")
					|| fields[i].getName().equals("l4_dst_port") || fields[i].getName().equals("protocol_name")
					|| fields[i].getName().equals("application_layer_protocol")|| fields[i].getName().equals("encryption_based_protection_protocol")
					|| fields[i].getName().equals("packet_source")|| fields[i].getName().equals("request_url")
					|| fields[i].getName().equals("complete_url")|| fields[i].getName().equals("url_param")
					|| fields[i].getName().equals("domain_url") ) {
				fieldstring.append("\t\t\t\t\t\t,\"analyzer\": \"" + "index_ansj\"" + "\n");
				fieldstring.append("\t\t\t\t\t\t,\"search_analyzer\": \"" + "query_ansj\"" + "\n");
			}
			if (fields[i].getName().equals("equipmentname") || fields[i].getName().equals("ipv4_src_addr")
					|| fields[i].getName().equals("ipv4_dst_addr") || fields[i].getName().equals("request_url")
					|| fields[i].getName().equals("domain_url") || fields[i].getName().equals("url_param")
					|| fields[i].getName().equals("complete_url")) {
				fieldstring.append("\t\t\t\t\t\t,\"fields\": " + "{\"raw\": {\"type\": \"keyword\"}}" + "\n");
			}
			if (i == fields.length - 1) {
				fieldstring.append("\t\t\t\t\t}\n");
			} else {
				fieldstring.append("\t\t\t\t\t},\n");
			}
		}
		return fieldstring.toString();
	}

	private static String GetElasticSearchMappingType(String varType, String name) {
		String es = "text";
		switch (varType) {
		case "Date":
			es = "date\"\n" + "\t\t\t\t\t\t,\"format\":\"yyyy-MM-dd HH:mm:ss\"\n" + "\t\t\t\t\t\t";
			break;
		case "Double":
			es = "double\"\n" + "\t\t\t\t\t\t,\"null_value\":\"NaN\"";
			break;
		case "Long":
			es = "long\"";
			break;
		default:
			if (name.equals("id")) {
				es = "keyword\"";
			} else {
				es = "text\"";
			}

			break;
		}
		return es;
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

	// 正则匹配
	public static String getSubUtil(String soap, String rgex) {
		Pattern pattern = Pattern.compile(rgex);// 匹配的模式
		Matcher m = pattern.matcher(soap);
		while (m.find()) {
			return m.group(0);
		}
		return null;
	}
	public static void main(String[] args) {
		/*String tcp ="6 (TCP)" ;
		String udp ="17 (UDP)";
		
		String tcp_protocol_value =getSubUtilSimple(tcp,"(\\d+)");
		String tcp_protocol_name = getSubUtilSimple(tcp,"[(](.*?)[)]");
		System.out.println(tcp_protocol_value+":"+tcp_protocol_name);*/
		System.out.println(new DefaultPacket().toMapping());
	}
}

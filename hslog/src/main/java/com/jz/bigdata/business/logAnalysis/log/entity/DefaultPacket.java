package com.jz.bigdata.business.logAnalysis.log.entity;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jz.bigdata.util.GeoIPUtil;
import org.pcap4j.core.PcapPacket;
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
	
	private Long acknum; // tcp 确认号
	private Long seqnum; // tcp 顺序号
	private Long nextacknum; // 下一个流量数据包的acknum

	// -------------http数据包属性---------------
	private String requestorresponse;//请求或返回
	private String request_url;//请求地址
	private String response_state;//返回状态
	private String request_type;//请求类型
	private String complete_url; // 完整的url http://192.168.2.182:8080/jzLog/getIndicesCount.do?_=1555924017369
	private String url_param; // url参数
	private String domain_url; // 域名
	private Integer packet_length; // 数据包大小
	private String user_agent; // 客户端信息
	private String user_agent_os;  // 客户端操作系统
	private String user_agent_browser;  // 客户端浏览器
	private String user_agent_browser_version; // 客户端浏览器版本
	private String session_status; // 会话状态

	/**
	 * 目的地址详细信息
	 */
	private String dst_addr_country; // 国家
	private String dst_addr_province; // 省份
	private String dst_addr_city; // 城市
	private String dst_addr_locations; // 经纬度
	/**
	 * 源地址
	 */
	private String src_addr_country;// 国家
	private String src_addr_province;// 省份
	private String src_addr_city;// 城市
	private String src_addr_locations;// 经纬度

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

	public Long getAcknum() {
		return acknum;
	}

	public void setAcknum(Long acknum) {
		this.acknum = acknum;
	}

	public Long getSeqnum() {
		return seqnum;
	}

	public void setSeqnum(Long seqnum) {
		this.seqnum = seqnum;
	}

	public Long getNextacknum() {
		return nextacknum;
	}

	public void setNextacknum(Long nextacknum) {
		this.nextacknum = nextacknum;
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

	public Integer getPacket_length() {
		return packet_length;
	}

	public void setPacket_lenght(Integer packet_length) {
		this.packet_length = packet_length;
	}

	public String getUser_agent() {
		return user_agent;
	}

	public void setUser_agent(String user_agent) {
		this.user_agent = user_agent;
	}

	public String getUser_agent_os() {
		return user_agent_os;
	}

	public void setUser_agent_os(String user_agent_os) {
		this.user_agent_os = user_agent_os;
	}

	public String getUser_agent_browser() {
		return user_agent_browser;
	}

	public void setUser_agent_browser(String user_agent_browser) {
		this.user_agent_browser = user_agent_browser;
	}

	public String getUser_agent_browser_version() {
		return user_agent_browser_version;
	}

	public void setUser_agent_browser_version(String user_agent_browser_version) {
		this.user_agent_browser_version = user_agent_browser_version;
	}

	public String getSession_status() {
		return session_status;
	}

	public void setSession_status(String session_status) {
		this.session_status = session_status;
	}

	public void setPacket_length(Integer packet_length) {
		this.packet_length = packet_length;
	}

	public String getDst_addr_country() {
		return dst_addr_country;
	}

	public void setDst_addr_country(String dst_addr_country) {
		this.dst_addr_country = dst_addr_country;
	}

	public String getDst_addr_province() {
		return dst_addr_province;
	}

	public void setDst_addr_province(String dst_addr_province) {
		this.dst_addr_province = dst_addr_province;
	}

	public String getDst_addr_city() {
		return dst_addr_city;
	}

	public void setDst_addr_city(String dst_addr_city) {
		this.dst_addr_city = dst_addr_city;
	}

	public String getDst_addr_locations() {
		return dst_addr_locations;
	}

	public void setDst_addr_locations(String dst_addr_locations) {
		this.dst_addr_locations = dst_addr_locations;
	}

	public String getSrc_addr_country() {
		return src_addr_country;
	}

	public void setSrc_addr_country(String src_addr_country) {
		this.src_addr_country = src_addr_country;
	}

	public String getSrc_addr_province() {
		return src_addr_province;
	}

	public void setSrc_addr_province(String src_addr_province) {
		this.src_addr_province = src_addr_province;
	}

	public String getSrc_addr_city() {
		return src_addr_city;
	}

	public void setSrc_addr_city(String src_addr_city) {
		this.src_addr_city = src_addr_city;
	}

	public String getSrc_addr_locations() {
		return src_addr_locations;
	}

	public void setSrc_addr_locations(String src_addr_locations) {
		this.src_addr_locations = src_addr_locations;
	}

	public DefaultPacket(){
		
	}
	
	public DefaultPacket(PcapPacket packet) {
		
		IpV4Packet ip4packet =packet.get(IpV4Packet.class);
		//System.out.println("协议值："+ip4packet.getHeader().getProtocol());
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.logdate = Date.from(packet.getTimestamp());
		this.logtime = format.format(this.logdate);
		this.ipv4_dst_addr = ip4packet.getHeader().getDstAddr().toString().replaceAll("/", "");
		this.ipv4_src_addr = ip4packet.getHeader().getSrcAddr().toString().replaceAll("/", "");

		if (this.ipv4_dst_addr!=null&&!this.ipv4_dst_addr.equals("")){
			try {
				this.dst_addr_country = GeoIPUtil.getIPMsg(this.ipv4_dst_addr).getCountryName();
				this.dst_addr_province = GeoIPUtil.getIPMsg(this.ipv4_dst_addr).getProvinceName();
				this.dst_addr_city = GeoIPUtil.getIPMsg(this.ipv4_dst_addr).getCityName();
				if (GeoIPUtil.getIPMsg(this.ipv4_dst_addr).getLocations()!=null){
					this.dst_addr_locations = GeoIPUtil.getIPMsg(this.ipv4_dst_addr).getLocations();
				}
			}catch (Exception e){
				this.dst_addr_country = "中国";
				this.dst_addr_province = "山东";
				this.dst_addr_city = "jinan";
				this.dst_addr_locations = "36.4006,117.0113";
			}

		}

		if (this.ipv4_src_addr!=null&&!this.ipv4_src_addr.equals("")){
			try {
				this.src_addr_country = GeoIPUtil.getIPMsg(this.ipv4_src_addr).getCountryName();
				this.src_addr_province = GeoIPUtil.getIPMsg(this.ipv4_src_addr).getProvinceName();
				this.src_addr_city = GeoIPUtil.getIPMsg(this.ipv4_src_addr).getCityName();
				if (GeoIPUtil.getIPMsg(this.ipv4_src_addr).getLocations()!=null){
					this.src_addr_locations = GeoIPUtil.getIPMsg(this.ipv4_src_addr).getLocations();
				}

			}catch (Exception e){
				//e.printStackTrace();
				this.src_addr_country = "中国";
				this.src_addr_province = "山东";
				this.src_addr_city = "jinan";
				this.src_addr_locations = "36.4006,117.0113";
			}

		}

		// 通过ipv4数据包中的协议值来选择解析数据包的协议方法
		if (ip4packet.getHeader().getProtocol().toString().contains("TCP")) {
			
			TcpPacket tcppacket = packet.getBuilder().getPayloadBuilder().build().get(TcpPacket.class);
			// 根据head中状态的boolean值补全会话状态，多个状态以&分割
			StringBuilder builder = new StringBuilder();
			if(tcppacket.getHeader().getUrg()){
				builder.append("URG");
			}
			if (tcppacket.getHeader().getSyn()){
				if (builder.length()>1){
					builder.append("&SYN");
				}else{
					builder.append("SYN");
				}
			}
			if (tcppacket.getHeader().getRst()){
				if (builder.length()>1){
					builder.append("&RST");
				}else{
					builder.append("RST");
				}
			}
			if (tcppacket.getHeader().getFin()){
				if (builder.length()>1){
					builder.append("&FIN");
				}else{
					builder.append("FIN");
				}
			}
			if (tcppacket.getHeader().getPsh()){
				if (builder.length()>1){
					builder.append("&PSH");
				}else{
					builder.append("PSH");
				}
			}
			if (tcppacket.getHeader().getAck()){
				if (builder.length()>1){
					builder.append("&ACK");
				}else{
					builder.append("ACK");
				}
			}
			this.session_status = builder.toString();

			this.packet_length = tcppacket.getPayload().getRawData().length;
			this.l4_dst_port = tcppacket.getHeader().getDstPort().valueAsInt()+"";
			this.l4_src_port = tcppacket.getHeader().getSrcPort().valueAsInt()+"";
			
			this.acknum = tcppacket.getHeader().getAcknowledgmentNumberAsLong();
			this.seqnum = tcppacket.getHeader().getSequenceNumberAsLong();
			if (this.seqnum!=null&&this.packet_length!=null){
				this.nextacknum = this.seqnum + this.packet_length;
			}
			
			this.protocol="6";
			this.protocol_name="TCP";
			if (tcppacket.getPayload()!=null) {
				//TODO 非http暂时取消存储payload数据
				//this.payload = packet.toString();
			}
			
			String hexstring = tcppacket.toHexString().replaceAll(" ", "");
			if (hexstring.contains("170303")||hexstring.contains("160301")||hexstring.contains("150303")||hexstring.contains("160303")||hexstring.contains("140303")) {
				if (tcppacket.getHeader().getAck()&&tcppacket.getHeader().getPsh()) {
					this.application_layer_protocol = "https";
				}else if (tcppacket.getHeader().getAck()&&hexstring.indexOf("170303")>40) {
					this.application_layer_protocol = "https";
				}else{
					//this.application_layer_protocol = "";
				}
			}
			
			
		}else if (ip4packet.getHeader().getProtocol().toString().contains("UDP")) {
			try {
				UdpPacket udpPacket = packet.getBuilder().getPayloadBuilder().build().get(UdpPacket.class);
				this.l4_dst_port = udpPacket.getHeader().getDstPort().valueAsInt()+"";
				this.l4_src_port = udpPacket.getHeader().getSrcPort().valueAsInt()+"";

				if (udpPacket.getPayload()!=null){
					//数据包长度
					this.packet_length = udpPacket.getPayload().length();
					//this.payload = udpPacket.getPayload().toString();
				}
			}catch (ArrayIndexOutOfBoundsException arraye){
                //this.payload = packet.toString();
                this.operation_des = "ArrayIndexOutOfBoundsException";
                arraye.printStackTrace();
            }catch (Exception e){
				//this.payload = packet.toString();
				this.operation_des = "异常udp";
				e.printStackTrace();
			}

			this.protocol="17";
			this.protocol_name="UDP";

			
		}else if (ip4packet.getHeader().getProtocol().toString().contains("ICMPv4")) {

			this.protocol="1";
			this.protocol_name="ICMPv4";
			if (ip4packet.getPayload()!=null){
				//数据包长度
				this.packet_length = ip4packet.getPayload().length();
				//this.payload = udpPacket.getPayload().toString();
			}
			//this.payload = udpPacket.getPayload().toString();
		}else {
			System.out.println("协议值："+ip4packet.getHeader().getProtocol());
		}
		this.packet_source = "libpcap";

		this.index_suffix = "default";
	}
	
	public String toMapping() {
		String template = "{\n" + "\t\t\"properties\":{\n" + "\t\t{#}\n" + "\t\t\t\t}" + "}";
		String fieldString = getClassMapping(new DefaultPacket());
		template = template.replace("{#}", fieldString);
		return template;
	}

	public <T> String getClassMapping(T classes) {

		StringBuilder fieldstring = new StringBuilder();

		// 设置需要聚合的字段
		String [] fielddata = {
				// 基本字段
				"equipmentid", "equipmentname","logtime", "hostname","userid","deptid","ip", "operation_facility","operation_level", "process",  "eventid", "event_type",
				// dhcp字段
				"dhcp_type","client_mac" ,"client_hostname", "error_log","network_error",
				// dns字段
				"dns_view","dns_domain_name","dns_ana_type","dns_server",
				// 流量字段
				"protocol","protocol_name","application_layer_protocol","encryption_based_protection_protocol","packet_source",
				"l4_src_port","l4_dst_port","ipv4_src_addr","ipv4_dst_addr","request_type","domain_url","complete_url","url_param",
				"request_url","response_state","user_agent_os","user_agent_browser","session_status",
				// 防火墙字段
				"from","devid","dname","logtype","mod","act","sa","da","pa",
				// filebeat
				"host"};

		// 设置需要分词的字段
		String [] analyzer = {
				// 基本字段
				"operation_des","equipmentname","hostname","process","event_des","ip",
				// dhcp分词字段
				"dhcp_type","client_mac", "client_hostname", "error_log","network_error",
				// dns分词字段
				"dns_view","dns_domain_name","dns_ana_type","dns_server",
				// 流量包字段，包含http
				"l4_src_port","l4_dst_port","protocol_name","application_layer_protocol","encryption_based_protection_protocol",
				"packet_source","request_url","complete_url","url_param","domain_url","ipv4_dst_addr","ipv4_src_addr",
				"user_agent_os","user_agent_browser","session_status",
				// 防火墙
				"act",
				// filebeat
				"host","name"};

		// 设置raw，保证聚合时不会聚合分词内容，主要针对ip地址和url
		String [] rawkeywords = {
				// ip
				"client_ip","dns_clientip","ip","relay_ip","ipv4_src_addr","ipv4_dst_addr","from","sa","da","pa",
				// url
				"equipmentname","request_url","domain_url","url_param","complete_url","protocol_name","application_layer_protocol","encryption_based_protection_protocol",
				// user-agent
				"user_agent_os","user_agent_browser","session_status"};

		Field[] fields = classes.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			fieldstring.append("\t\t\t\t\"" + fields[i].getName().toLowerCase() + "\": {\n");
			fieldstring.append("\t\t\t\t\t\t\"type\": \""
					+ GetElasticSearchMappingType(fields[i].getType().getSimpleName(), fields[i].getName()) + "\n");
			if (fields[i].getName().equals("id")) {
				fieldstring.append("\t\t\t\t\t\t,\"index\": \"" + "false\"" + "\n");
			}
			if (Arrays.asList(fielddata).contains(fields[i].getName())) {
				fieldstring.append("\t\t\t\t\t\t,\"fielddata\": " + "true" + "\n");
			}
			if (Arrays.asList(analyzer).contains(fields[i].getName())) {
				fieldstring.append("\t\t\t\t\t\t,\"analyzer\": \"" + "index_ansj\"" + "\n");
				fieldstring.append("\t\t\t\t\t\t,\"search_analyzer\": \"" + "query_ansj\"" + "\n");
			}
			if (Arrays.asList(rawkeywords).contains(fields[i].getName())) {
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
		case "Integer":
			es = "integer\"";
			break;
		case "Boolean":
			es = "boolean\"";
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

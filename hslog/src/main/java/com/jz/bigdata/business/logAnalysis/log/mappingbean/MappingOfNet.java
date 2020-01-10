package com.jz.bigdata.business.logAnalysis.log.mappingbean;

import java.util.Date;

import com.jz.bigdata.util.Bean2Mapping;

/**
 * 
 * @author jiyourui
 * 服务于elasticsearch创建关于packet类型index的template
 */
public class MappingOfNet {
	
	/**
	 * id
	 */
	private String id;
	/**
	 * 日志类型
	 */
	private String hslog_type;
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
	 * 源地址
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


	/* -------------- 针对http数据包属性 ------------  */
	
	/**
	 * 请求或返回
	 */
	private String requestorresponse;
	/**
	 * 请求地址
	 */
	private String request_url;
	/**
	 * 返回状态
	 */
	private String response_state;
	/**
	 * 请求类型
	 */
	private String request_type;
	/**
	 * 完整的url,例如： http://192.168.2.182:8080/jzLog/getIndicesCount.do?_=1555924017369
	 */
	private String complete_url;
	/**
	 * url参数
	 */
	private String url_param; 
	/**
	 * 域名
	 */
	private String domain_url;
	/**
	 * 数据包大小
	 */
	private Integer packet_length;
	/**
	 * 客户端信息
	 */
	private String user_agent;
	/**
	 * 客户端操作系统
	 */
	private String user_agent_os;
	/**
	 *  客户端浏览器
	 */
	private String user_agent_browser;
	/**
	 * 客户端浏览器版本
	 */
	private String user_agent_browser_version;
	/**
	 * 会话状态
	 */
	private String session_status;

	/**
	 * 目的地址详细信息
	 */
	private String dst_addr_country;
	private String dst_addr_province;
	private String dst_addr_city;
	private String dst_addr_locations;
	/**
	 * 源地址
	 */
	private String src_addr_country;
	private String src_addr_province;
	private String src_addr_city;
	private String src_addr_locations;

	/**
	 * 关联字段
	 */
	/*private String joinfield;*/
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getHslog_type() {
		return hslog_type;
	}


	public void setHslog_type(String hslog_type) {
		this.hslog_type = hslog_type;
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

	public void setPacket_length(Integer packet_length) {
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

	public String toMapping() {
		String template = "{\n" + "\t\t\"properties\":{\n" + "\t\t{#}\n" + "\t\t\t\t}" + "}";
		String fieldString = Bean2Mapping.getClassMapping(this);
		template = template.replace("{#}", fieldString);
		return template;
	}

	public static void main(String [] args) {
		System.out.println(new MappingOfNet().toMapping());
	}
}

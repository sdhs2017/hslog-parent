package com.jz.bigdata.business.logAnalysis.log.entity;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.google.gson.Gson;

/**
 * @ClassName Netflow
 * @Description 
 * @Author shi cheng yu
 * @Date 2018年12月10日 下午2:41:55
 */
public class Netflow {
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
	private String logtime_minute;
	private String logtime_hour;
	private String logtime_day;
	private String logtime_month;
	private String logtime_year;
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
	
	private String host;
	
	
	private net_flow netflow;
	
	//业务类
	private int l4_src_port;//源端口
	private int l4_dst_port;//目的端口
	private String ipv4_dst_addr;//目的地址
	private String ipv4_src_addr;//原地址
	
	private int dst_as;
	private int in_pkts;
	private String first_switched;//首次转换
	private String ipv4_next_hop;//下一个心跳
	private int sampling_algorithm;//抽样算法
	private int in_bytes;//容量
	private String protocol;//协议值
	private String protocol_name;//协议名称
	private int tcp_flags;//tcp标志
	private int src_as;//
	private int output_snmp;//输出snmp
	private int dst_mask;//
	private int src_tos;//
	private int src_mask;//
	private int version;//版本
	private int flow_seq_num;//
	private int flow_records;//流量记录
	private int engine_type;//引擎类型
	private int engine_id;//引擎id
	private int input_snmp;//输入snmp
	private String last_switched;//最后转换
	private int sampling_interval;//采样间隔
	private String packet_source;//数据包来源
	
	
	public int getDst_as() {
		return dst_as;
	}

	public void setDst_as(int dst_as) {
		this.dst_as = dst_as;
	}

	public int getIn_pkts() {
		return in_pkts;
	}

	public void setIn_pkts(int in_pkts) {
		this.in_pkts = in_pkts;
	}

	public String getFirst_switched() {
		return first_switched;
	}

	public void setFirst_switched(String first_switched) {
		this.first_switched = first_switched;
	}

	public String getIpv4_next_hop() {
		return ipv4_next_hop;
	}

	public void setIpv4_next_hop(String ipv4_next_hop) {
		this.ipv4_next_hop = ipv4_next_hop;
	}

	public int getSampling_algorithm() {
		return sampling_algorithm;
	}

	public void setSampling_algorithm(int sampling_algorithm) {
		this.sampling_algorithm = sampling_algorithm;
	}

	public int getIn_bytes() {
		return in_bytes;
	}

	public void setIn_bytes(int in_bytes) {
		this.in_bytes = in_bytes;
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

	public int getTcp_flags() {
		return tcp_flags;
	}

	public void setTcp_flags(int tcp_flags) {
		this.tcp_flags = tcp_flags;
	}

	public int getSrc_as() {
		return src_as;
	}

	public void setSrc_as(int src_as) {
		this.src_as = src_as;
	}

	public int getOutput_snmp() {
		return output_snmp;
	}

	public void setOutput_snmp(int output_snmp) {
		this.output_snmp = output_snmp;
	}

	public int getDst_mask() {
		return dst_mask;
	}

	public void setDst_mask(int dst_mask) {
		this.dst_mask = dst_mask;
	}

	public int getSrc_tos() {
		return src_tos;
	}

	public void setSrc_tos(int src_tos) {
		this.src_tos = src_tos;
	}

	public int getSrc_mask() {
		return src_mask;
	}

	public void setSrc_mask(int src_mask) {
		this.src_mask = src_mask;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getFlow_seq_num() {
		return flow_seq_num;
	}

	public void setFlow_seq_num(int flow_seq_num) {
		this.flow_seq_num = flow_seq_num;
	}

	public int getFlow_records() {
		return flow_records;
	}

	public void setFlow_records(int flow_records) {
		this.flow_records = flow_records;
	}

	public int getEngine_type() {
		return engine_type;
	}

	public void setEngine_type(int engine_type) {
		this.engine_type = engine_type;
	}

	public int getEngine_id() {
		return engine_id;
	}

	public void setEngine_id(int engine_id) {
		this.engine_id = engine_id;
	}

	public int getInput_snmp() {
		return input_snmp;
	}

	public void setInput_snmp(int input_snmp) {
		this.input_snmp = input_snmp;
	}

	public String getLast_switched() {
		return last_switched;
	}

	public void setLast_switched(String last_switched) {
		this.last_switched = last_switched;
	}

	public int getSampling_interval() {
		return sampling_interval;
	}

	public void setSampling_interval(int sampling_interval) {
		this.sampling_interval = sampling_interval;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

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

	public String getLogtime_minute() {
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

	public net_flow getNetflow() {
		return netflow;
	}

	public void setNetflow(net_flow netflow) {
		this.netflow = netflow;
	}

	
	public int getL4_src_port() {
		return l4_src_port;
	}

	public void setL4_src_port(int l4_src_port) {
		this.l4_src_port = l4_src_port;
	}

	public int getL4_dst_port() {
		return l4_dst_port;
	}

	public void setL4_dst_port(int l4_dst_port) {
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


	class net_flow{
		private int dst_as;
		private int in_pkts;
		private String first_switched;//首次转换
		private String ipv4_next_hop;//下一个心跳
		private int l4_src_port;//源端口
		private int sampling_algorithm;//抽样算法
		private int in_bytes;//容量
		private String protocol;//协议
		private int tcp_flags;//tcp标志
		private int l4_dst_port;//目的端口
		private int src_as;//
		private int output_snmp;//输出snmp
		private int dst_mask;//
		private String ipv4_dst_addr;//目的地址
		private int src_tos;//
		private int src_mask;//
		private int version;//版本
		private int flow_seq_num;//
		private int flow_records;//流量记录
		private String ipv4_src_addr;//原地址
		private int engine_type;//引擎类型
		private int engine_id;//引擎id
		private int input_snmp;//输入snmp
		private String last_switched;//最后转换
		private int sampling_interval;//采样间隔
		public int getDst_as() {
			return dst_as;
		}
		public void setDst_as(int dst_as) {
			this.dst_as = dst_as;
		}
		public int getIn_pkts() {
			return in_pkts;
		}
		public void setIn_pkts(int in_pkts) {
			this.in_pkts = in_pkts;
		}
		public String getFirst_switched() {
			return first_switched;
		}
		public void setFirst_switched(String first_switched) {
			this.first_switched = first_switched;
		}
		public String getIpv4_next_hop() {
			return ipv4_next_hop;
		}
		public void setIpv4_next_hop(String ipv4_next_hop) {
			this.ipv4_next_hop = ipv4_next_hop;
		}
		public int getL4_src_port() {
			return l4_src_port;
		}
		public void setL4_src_port(int l4_src_port) {
			this.l4_src_port = l4_src_port;
		}
		public int getSampling_algorithm() {
			return sampling_algorithm;
		}
		public void setSampling_algorithm(int sampling_algorithm) {
			this.sampling_algorithm = sampling_algorithm;
		}
		public int getIn_bytes() {
			return in_bytes;
		}
		public void setIn_bytes(int in_bytes) {
			this.in_bytes = in_bytes;
		}
		public String getProtocol() {
			return protocol;
		}
		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}
		public int getTcp_flags() {
			return tcp_flags;
		}
		public void setTcp_flags(int tcp_flags) {
			this.tcp_flags = tcp_flags;
		}
		public int getL4_dst_port() {
			return l4_dst_port;
		}
		public void setL4_dst_port(int l4_dst_port) {
			this.l4_dst_port = l4_dst_port;
		}
		public int getSrc_as() {
			return src_as;
		}
		public void setSrc_as(int src_as) {
			this.src_as = src_as;
		}
		public int getOutput_snmp() {
			return output_snmp;
		}
		public void setOutput_snmp(int output_snmp) {
			this.output_snmp = output_snmp;
		}
		public int getDst_mask() {
			return dst_mask;
		}
		public void setDst_mask(int dst_mask) {
			this.dst_mask = dst_mask;
		}
		public String getIpv4_dst_addr() {
			return ipv4_dst_addr;
		}
		public void setIpv4_dst_addr(String ipv4_dst_addr) {
			this.ipv4_dst_addr = ipv4_dst_addr;
		}
		public int getSrc_tos() {
			return src_tos;
		}
		public void setSrc_tos(int src_tos) {
			this.src_tos = src_tos;
		}
		public int getSrc_mask() {
			return src_mask;
		}
		public void setSrc_mask(int src_mask) {
			this.src_mask = src_mask;
		}
		public int getVersion() {
			return version;
		}
		public void setVersion(int version) {
			this.version = version;
		}
		public int getFlow_seq_num() {
			return flow_seq_num;
		}
		public void setFlow_seq_num(int flow_seq_num) {
			this.flow_seq_num = flow_seq_num;
		}
		public int getFlow_records() {
			return flow_records;
		}
		public void setFlow_records(int flow_records) {
			this.flow_records = flow_records;
		}
		public String getIpv4_src_addr() {
			return ipv4_src_addr;
		}
		public void setIpv4_src_addr(String ipv4_src_addr) {
			this.ipv4_src_addr = ipv4_src_addr;
		}
		public int getEngine_type() {
			return engine_type;
		}
		public void setEngine_type(int engine_type) {
			this.engine_type = engine_type;
		}
		public int getEngine_id() {
			return engine_id;
		}
		public void setEngine_id(int engine_id) {
			this.engine_id = engine_id;
		}
		public int getInput_snmp() {
			return input_snmp;
		}
		public void setInput_snmp(int input_snmp) {
			this.input_snmp = input_snmp;
		}
		public String getLast_switched() {
			return last_switched;
		}
		public void setLast_switched(String last_switched) {
			this.last_switched = last_switched;
		}
		public int getSampling_interval() {
			return sampling_interval;
		}
		public void setSampling_interval(int sampling_interval) {
			this.sampling_interval = sampling_interval;
		}
	}
	
	public Netflow(){
		
	}
	
	public Netflow(String log, Calendar cal, Map<String, String> protocolmap) {
		
		StringBuilder sb = new StringBuilder(log);
		sb =sb.replace(sb.indexOf("@timestamp"),sb.indexOf("@version") ,"");
		log =sb.toString();
		log=log.replace("=>", ":"); 
		Gson gson = new Gson();
		Netflow netflow = gson.fromJson(log,Netflow.class);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC+8"));
		
		this.ip = netflow.getHost();
//		// 时间处理
		try {
			this.logdate = sdf.parse(netflow.getNetflow().getFirst_switched());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		cal.setTime(this.logdate);
		this.logtime = format.format(this.logdate.getTime());
		this.logtime_year = String.valueOf(cal.get(Calendar.YEAR));
		this.logtime_month = String.format("%02d", cal.get(Calendar.MONTH) + 1);
		this.logtime_day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
		this.logtime_hour = String.format("%02d", cal.get(Calendar.HOUR_OF_DAY));
		this.logtime_minute = String.format("%02d", cal.get(Calendar.MINUTE));
		
		this.l4_src_port=netflow.getNetflow().l4_src_port;
		this.l4_dst_port=netflow.getNetflow().l4_dst_port;
		this.ipv4_dst_addr=netflow.getNetflow().ipv4_dst_addr;
		this.ipv4_src_addr=netflow.getNetflow().ipv4_src_addr;
		/*this.dst_as=netflow.getNetflow().getDst_as();
		this.in_pkts=netflow.getNetflow().getIn_pkts();
		this.first_switched=netflow.getNetflow().getFirst_switched();
		this.ipv4_next_hop=netflow.getNetflow().getIpv4_next_hop();
		this.sampling_algorithm=netflow.getNetflow().getSampling_algorithm();
		this.in_bytes=netflow.getNetflow().getIn_bytes();*/
		this.protocol=netflow.getNetflow().getProtocol();
		this.packet_source = "netflow";
		if (this.protocol!=null) {
			if (protocolmap.get(this.protocol)!=null) {
				this.protocol_name = protocolmap.get(this.protocol);
			}else {
				this.protocol_name = "";
			}
			
		}else {
			this.protocol_name = "";
		}
		/*this.tcp_flags=netflow.getNetflow().getTcp_flags();
		this.src_as=netflow.getNetflow().getSrc_as();
		this.output_snmp=netflow.getNetflow().getOutput_snmp();
		this.dst_mask=netflow.getNetflow().getDst_mask();
		this.src_tos=netflow.getNetflow().getSrc_tos();
		this.src_mask=netflow.getNetflow().getSrc_mask();
		this.version=netflow.getNetflow().getVersion();
		this.flow_seq_num=netflow.getNetflow().getFlow_seq_num();
		this.flow_records=netflow.getNetflow().getFlow_records();
		this.engine_type=netflow.getNetflow().getEngine_type();
		this.engine_id=netflow.getNetflow().getEngine_id();
		this.input_snmp=netflow.getNetflow().getInput_snmp();
		this.last_switched=netflow.getNetflow().getLast_switched();
		this.sampling_interval=netflow.getNetflow().getSampling_interval();*/
		this.operation_level="";
//		netflow.setOperation_des(log);
		this.operation_des=log;
	}
	
	
	public String toMapping() {
		String template = "{\n" + "\t\t\"properties\":{\n" + "\t\t{#}\n" + "\t\t\t\t}" + "}";
		String fieldString = getClassMapping(new Netflow());
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
					|| fields[i].getName().equals("protocol") || fields[i].getName().equals("host")
					|| fields[i].getName().equals("protocol_name")|| fields[i].getName().equals("packet_source")) {
				fieldstring.append("\t\t\t\t\t\t,\"fielddata\": " + "true" + "\n");
			}
			if (fields[i].getName().equals("operation_des") || fields[i].getName().equals("ip")
					|| fields[i].getName().equals("equipmentname") || fields[i].getName().equals("ipv4_dst_addr")
					|| fields[i].getName().equals("ipv4_src_addr") || fields[i].getName().equals("l4_src_port")
					|| fields[i].getName().equals("l4_dst_port") || fields[i].getName().equals("protocol_name")
					|| fields[i].getName().equals("host") || fields[i].getName().equals("packet_source")) {
				fieldstring.append("\t\t\t\t\t\t,\"analyzer\": \"" + "index_ansj\"" + "\n");
				fieldstring.append("\t\t\t\t\t\t,\"search_analyzer\": \"" + "query_ansj\"" + "\n");
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
	
	
	
	public static void main(String[] args) {
		String log ="{\r\n" + 
				"       \"netflow\" : {\r\n" + 
				"                    \"dst_as\" : 0,\r\n" + 
				"                   \"in_pkts\" : 6,\r\n" + 
				"            \"first_switched\" : \"2018-12-10T01:48:26.999Z\",\r\n" + 
				"             \"ipv4_next_hop\" : \"0.0.0.0\",\r\n" + 
				"               \"l4_src_port\" : 52978,\r\n" + 
				"        \"sampling_algorithm\" : 0,\r\n" + 
				"                  \"in_bytes\" : 452,\r\n" + 
				"                  \"protocol\" : 6,\r\n" + 
				"                 \"tcp_flags\" : 0,\r\n" + 
				"               \"l4_dst_port\" : 9300,\r\n" + 
				"                    \"src_as\" : 0,\r\n" + 
				"               \"output_snmp\" : 0,\r\n" + 
				"                  \"dst_mask\" : 16,\r\n" + 
				"             \"ipv4_dst_addr\" : \"192.168.2.182\",\r\n" + 
				"                   \"src_tos\" : 0,\r\n" + 
				"                  \"src_mask\" : 16,\r\n" + 
				"                   \"version\" : 5,\r\n" + 
				"              \"flow_seq_num\" : 0,\r\n" + 
				"              \"flow_records\" : 1,\r\n" + 
				"             \"ipv4_src_addr\" : \"123.232.103.226\",\r\n" + 
				"               \"engine_type\" : 99,\r\n" + 
				"                 \"engine_id\" : 99,\r\n" + 
				"                \"input_snmp\" : 0,\r\n" + 
				"             \"last_switched\" : \"2018-12-10T01:48:26.999Z\",\r\n" + 
				"         \"sampling_interval\" : 0\r\n" + 
				"    },\r\n" + 
				"    \"@timestamp\" : 2018-12-10T01:48:56.000Z,\r\n" + 
				"      \"@version\" : \"1\",\r\n" + 
				"          \"host\" : \"192.168.2.182\",\r\n" + 
				"          \"tags\" : []\r\n" + 
				"}";
		Calendar cal = Calendar.getInstance();
		
		System.out.println(new Netflow().toMapping());
		
		
		String mapstring = "{1:ICMP,3:IGMP,6:TCP,8:EGP,9:IGP,17:UDP,41:IPv6,89:OSPF}";
    	
    	Gson gson = new Gson();
    	Map<String, String> map = new HashMap<String, String>();
    	map = gson.fromJson(mapstring, map.getClass());
		new Netflow(log, cal, map);
		
	}
	
}

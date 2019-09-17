package com.jz.bigdata.business.logAnalysis.log.entity;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;

/**
 * @ClassName Https
 * @Description 
 * @Author shi cheng yu
 * @Date 2019年4月12日 下午1:32:57
 */
public class Tcp {
	
	private String source_port;//源端口
	private String des_port;//目标端口
	private String source_ip;//源ip
	private String des_ip;//目标ip
	private String protocol;//协议
//	private String requestorresponse;
//	private String request_url;
//	private String response_state;
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
	 * 操作描述
	 */
	private String operation_des;

	public String getSource_port() {
		return source_port;
	}

	public void setSource_port(String source_port) {
		this.source_port = source_port;
	}

	public String getDes_port() {
		return des_port;
	}

	public void setDes_port(String des_port) {
		this.des_port = des_port;
	}

	public String getSource_ip() {
		return source_ip;
	}

	public void setSource_ip(String source_ip) {
		this.source_ip = source_ip;
	}

	public String getDes_ip() {
		return des_ip;
	}

	public void setDes_ip(String des_ip) {
		this.des_ip = des_ip;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
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

	public String getOperation_des() {
		return operation_des;
	}

	public void setOperation_des(String operation_des) {
		this.operation_des = operation_des;
	}
	
	

	public Tcp(Packet packet){
		IpV4Packet ip4packet =packet.get(IpV4Packet.class);
		TcpPacket tcpPacket = packet.get(TcpPacket.class);
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.logdate=currentTime;
		this.logtime =formatter.format(currentTime);
    	String [] tmp = this.logtime.split(" ");
    	String [] date = tmp[0].split("-");
    	String [] time = tmp[1].split(":");
    	this.logtime_year = date[0];
    	this.logtime_month = date[1];
    	this.logtime_day = date[2];
    	this.logtime_hour = time[0];
    	this.logtime_minute = time[1];
		this.source_ip=ip4packet.getHeader().getSrcAddr().toString();
		this.source_port=tcpPacket.getHeader().getSrcPort().valueAsInt()+"";
		this.des_ip=ip4packet.getHeader().getDstAddr().toString();
		this.des_port=tcpPacket.getHeader().getDstPort().valueAsInt()+"";
		this.protocol="tcp";
	}
	
	
	
	
	public Tcp() {
	}

	public String toMapping() {
		String template = "{\n" + "\t\t\"properties\":{\n" + "\t\t{#}\n" + "\t\t\t\t}" + "}";
		String fieldString = getClassMapping(new Tcp());
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
					|| fields[i].getName().equals("source_port")
					|| fields[i].getName().equals("des_port")|| fields[i].getName().equals("source_ip")
					|| fields[i].getName().equals("des_ip")|| fields[i].getName().equals("protocol")) {
				fieldstring.append("\t\t\t\t\t\t,\"fielddata\": " + "true" + "\n");
			}
			if (fields[i].getName().equals("operation_des") || fields[i].getName().equals("equipmentname")
					|| fields[i].getName().equals("des_port") || fields[i].getName().equals("source_port")
					|| fields[i].getName().equals("source_ip")
					|| fields[i].getName().equals("des_ip")) {
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
		Tcp http =new Tcp();
		
		System.out.println(http.toMapping());
		
	}
	
	

}

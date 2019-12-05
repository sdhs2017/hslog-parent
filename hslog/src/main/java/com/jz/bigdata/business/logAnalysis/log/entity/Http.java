package com.jz.bigdata.business.logAnalysis.log.entity;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;


/**
 * @ClassName Http
 * @Description 
 * @Author shi cheng yu
 * @Date 2019年4月12日 下午1:33:07
 */
public class Http {
	
	private String l4_src_port;//源端口
	private String l4_dst_port;//目标端口
	private String ipv4_src_addr;//源ip
	private String ipv4_dst_addr;//目标ip
	private String protocol;//协议值
	/**
	 * 协议名称
	 */
	private String protocol_name;// 协议名称
	/**
	 * 应用层协议
	 */
	private String application_layer_protocol;
	/**
	 * 数据包来源
	 */
	private String packet_source;
	private String requestorresponse;//请求或返回
	private String request_url;//请求地址
	private String response_state;//返回状态
	private String request_type;//请求类型
	private String acknum; // tcp 确认号
	private String seqnum; // tcp 顺序号
	private String complete_url; // 完整的url http://192.168.2.182:8080/jzLog/getIndicesCount.do?_=1555924017369
	private String url_param; // url参数
	private String domain_url; // 域名
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

	public String getIpv4_src_addr() {
		return ipv4_src_addr;
	}

	public void setIpv4_src_addr(String ipv4_src_addr) {
		this.ipv4_src_addr = ipv4_src_addr;
	}

	public String getIpv4_dst_addr() {
		return ipv4_dst_addr;
	}

	public void setIpv4_dst_addr(String ipv4_dst_addr) {
		this.ipv4_dst_addr = ipv4_dst_addr;
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

	public String getPacket_source() {
		return packet_source;
	}

	public void setPacket_source(String packet_source) {
		this.packet_source = packet_source;
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

	public String getOperation_des() {
		return operation_des;
	}

	public void setOperation_des(String operation_des) {
		this.operation_des = operation_des;
	}

	

	public String getRequest_type() {
		return request_type;
	}

	public void setRequest_type(String request_type) {
		this.request_type = request_type;
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

	/**
	 * @param packet
	 * 构造方法，填充数据
	 */
	public Http(Packet packet){
		IpV4Packet ip4packet =packet.get(IpV4Packet.class);
		TcpPacket tcpPacket = packet.get(TcpPacket.class);
//		System.out.println(log);
		
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.logdate=currentTime;
		this.logtime =formatter.format(currentTime);
    	/*String [] tmp = this.logtime.split(" ");
    	String [] date = tmp[0].split("-");
    	String [] time = tmp[1].split(":");
    	this.logtime_year = date[0];
    	this.logtime_month = date[1];
    	this.logtime_day = date[2];
    	this.logtime_hour = time[0];
    	this.logtime_minute = time[1];*/
		
		this.ipv4_src_addr=ip4packet.getHeader().getSrcAddr().toString().replaceAll("/", "");
		this.l4_src_port=tcpPacket.getHeader().getSrcPort().valueAsInt()+"";
		this.ipv4_dst_addr=ip4packet.getHeader().getDstAddr().toString().replaceAll("/", "");
		this.l4_dst_port=tcpPacket.getHeader().getDstPort().valueAsInt()+"";
		this.protocol="6";
		this.protocol_name="TCP";
		this.application_layer_protocol = "http";
		this.packet_source = "libpcap";
		String httphex = tcpPacket.getPayload().toString().substring(tcpPacket.getPayload().toString().indexOf(":")+1).trim();
		this.operation_des=hexStringToString(httphex);
		//httprequest
		String httpRequest = "^(POST|GET) /[^\\s]* HTTP/1.[0,1]";
		//httpResponse
		String httpResponse = "^HTTP/1.[0,1] [0-9]{0,3} *";
		
		this.acknum = tcpPacket.getHeader().getAcknowledgmentNumber()+"";
		this.seqnum = tcpPacket.getHeader().getSequenceNumberAsLong()+"";
		
		//获取数据是否为空
		if (httphex!=null&&!httphex.equals("")) {
			
			// http请求报文解析
			if (getSubUtil(hexStringToString(httphex), httpRequest)!="") {
				
				this.requestorresponse="request";
				//根据空格分割数据
				String httpcontent=getSubUtil(hexStringToString(httphex), httpRequest);
				String[] message=httpcontent.split("\\s+");
				//循环添加数据
				for(int i=0;i<message.length;i++){
					if(i==0){
						this.request_type=message[0];
					}else if(i==1){
						String request_url_tmp = null;
						try {
							request_url_tmp=message[1];
						} catch (Exception e) {
							System.err.println(e.getMessage());
						}
						if (request_url_tmp!=null) {
							if (request_url_tmp.contains("?")) {
								this.request_url = getSubUtilSimple(request_url_tmp,"^(.*?)[?]");
								this.url_param = getSubUtilSimple(request_url_tmp,"[?](.*?)$");
							}else {
								this.request_url = request_url_tmp;
							}
						}
					}
				}
				
				if (this.request_url!=null) {
					this.complete_url = this.application_layer_protocol+"://"+this.ipv4_dst_addr+":"+this.l4_dst_port+this.request_url;
					if (!getSubUtil(this.request_url,"^[/].*?[/]").equals("")) {
						this.domain_url = "http://"+this.ipv4_dst_addr+":"+this.l4_dst_port+""+getSubUtil(this.request_url,"^[/].*?[/]");
					}else if (!getSubUtil(this.request_url,"^[/].*?$").equals("")) {
						this.domain_url = "http://"+this.ipv4_dst_addr+":"+this.l4_dst_port+""+getSubUtil(this.request_url,"^[/].*?$");
					}else if (getSubUtil(this.request_url,"^[/].*?$").equals("")) {
						this.domain_url = "http://"+this.ipv4_dst_addr+":"+this.l4_dst_port+""+"";
					}
				}
			
			// http返回报文解析
			}else if (getSubUtil(hexStringToString(httphex), httpResponse)!="") {
				this.requestorresponse="response";
				String httpRespons=getSubUtil(hexStringToString(httphex), httpResponse);
				//根据空格分割数据
				String[] message=httpRespons.split("\\s+");
				this.response_state=message[1];
				
				
			}
			
		}

		this.index_suffix = "packet";
        
	}
		
	public Http() {
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
	
 	/**
	 * 正则匹配
	 * @param soap
	 * @param rgex
	 * @return 返回括号中匹配的内容
	 */
 	public static String getSubUtilSimple(String soap, String rgex) {
 		Pattern pattern = Pattern.compile(rgex);// 匹配的模式
 		Matcher m = pattern.matcher(soap);
 		while (m.find()) {
 			return m.group(1);
 		}
 		return "";
 	}
		
	/**
	 * 16进制转换成为string类型字符串
	 * @param s
	 * @return
	 */
	public static String hexStringToString(String s) {
	    if (s == null || s.equals("")) {
	        return null;
	    }
	    s = s.replace(" ", "");
	    byte[] baKeyword = new byte[s.length() / 2];
	    for (int i = 0; i < baKeyword.length; i++) {
	        try {
	            baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    try {
	        s = new String(baKeyword, "UTF-8");
	        new String();
	    } catch (Exception e1) {
	        e1.printStackTrace();
	    }
	    return s;
	}
		
	
	
	public String toMapping() {
		String template = "{\n" + "\t\t\"properties\":{\n" + "\t\t{#}\n" + "\t\t\t\t}" + "}";
		String fieldString = getClassMapping(new Http());
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
					|| fields[i].getName().equals("l4_src_port")|| fields[i].getName().equals("protocol")
					|| fields[i].getName().equals("l4_dst_port")|| fields[i].getName().equals("ipv4_src_addr")
					|| fields[i].getName().equals("ipv4_dst_addr")|| fields[i].getName().equals("request_type")
					|| fields[i].getName().equals("complete_url")|| fields[i].getName().equals("url_param")
					|| fields[i].getName().equals("domain_url")
					|| fields[i].getName().equals("request_url")|| fields[i].getName().equals("response_state")) {
				fieldstring.append("\t\t\t\t\t\t,\"fielddata\": " + "true" + "\n");
			}
			if (fields[i].getName().equals("operation_des") || fields[i].getName().equals("equipmentname")
					|| fields[i].getName().equals("ipv4_src_addr")
					|| fields[i].getName().equals("ipv4_dst_addr")|| fields[i].getName().equals("request_url")
					|| fields[i].getName().equals("complete_url")|| fields[i].getName().equals("url_param")
					|| fields[i].getName().equals("domain_url") 
					) {
				fieldstring.append("\t\t\t\t\t\t,\"analyzer\": \"" + "index_ansj\"" + "\n");
				fieldstring.append("\t\t\t\t\t\t,\"search_analyzer\": \"" + "query_ansj\"" + "\n");
			}
			if (fields[i].getName().equals("equipmentname") || fields[i].getName().equals("ipv4_src_addr")
					|| fields[i].getName().equals("ipv4_dst_addr") || fields[i].getName().equals("request_url")
					|| fields[i].getName().equals("domain_url") 
					|| fields[i].getName().equals("complete_url")|| fields[i].getName().equals("url_param")) {
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
	
	public static void main(String[] args) {
		Http http =new Http();
		
		System.out.println(http.toMapping());
		
	}
	
	
}

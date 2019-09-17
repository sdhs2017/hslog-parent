/*package com.jz.bigdata.business.logAnalysis.collector.pcap4j;

import java.util.HashMap;
import java.util.List;

import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapIpV4Address;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;


public class Pcap4jCollectorTmp implements Runnable{
	
	*//**
	 * 需要获取网卡信息的IP地址
	 *//*
	private String ip;
	
	static String client ="";
	
	static HashMap<String, TcpStream> tcpStreamList=new HashMap<String, TcpStream>();
	
	public Pcap4jCollectorTmp(String ip) {
		this.ip = ip;
	}

	@Override
	public void run() {

    	PcapNetworkInterface nif = getCaptureNetworkInterface(ip);
    	System.out.println("--------------------------");
    	System.out.println(nif.getAddresses());
    	for(PcapAddress a:nif.getAddresses())
        {
        	if(a instanceof PcapIpV4Address)
        	{
        		client = a.getAddress().toString();
        		break;
        	}
        }
        if(client.equals(""))
        {
        	return;
        }
        // 抓取包长度
        int snaplen = 64 * 1024;
        // 超时50ms
        int timeout = 50;
        // 初始化抓包器
        PcapHandle.Builder phb = new PcapHandle.Builder(nif.getName()).snaplen(snaplen)
            .promiscuousMode(PromiscuousMode.PROMISCUOUS).timeoutMillis(timeout)
            .bufferSize(1 * 1024 * 1024);
        PcapHandle handle = null;
		try {
			handle = phb.build();
		} catch (PcapNativeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        // handle = nif.openLive(snaplen, PromiscuousMode.NONPROMISCUOUS, timeout);

        *//** 设置TCP过滤规则 *//*
        //String filter = "ip and tcp and (port 443)";
        *//** 设置TCP过滤规则 *//*
        String filter = "ip and tcp and (port 8080)";
        
            
        // 设置过滤器
        try {
			handle.setFilter(filter, BpfCompileMode.OPTIMIZE);
		} catch (PcapNativeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
      //初始化listener
        PacketListener listener = new PacketListener() {
        	
        	 public void gotPacket(Packet packet) {
        		 // public void gotPacket(PcapPacket packet) {
            	
            	
            	TcpPacket tcppacket =packet.getBuilder().getPayloadBuilder().build().get(TcpPacket.class);
    			IpV4Packet ip4packet =packet.get(IpV4Packet.class);
    			
    			String server = "";
    			String serverPort ="";
    			if(ip4packet.getHeader().getDstAddr().toString().contains(client))
    			{
    				server=ip4packet.getHeader().getSrcAddr().toString();
    				serverPort = tcppacket.getHeader().getSrcPort().valueAsString();
    				
    			}else if(ip4packet.getHeader().getSrcAddr().toString().contains(client))
    			{
    				server=ip4packet.getHeader().getDstAddr().toString();
    				serverPort = tcppacket.getHeader().getDstPort().valueAsString();
    			}else
    			{
    				return;
    			}
    			
    			TcpStream tps =null;
            	if(tcpStreamList.get(server+serverPort) == null)
            	{
            		tps= new TcpStream(server,client);
            		tcpStreamList.put(server+serverPort, tps);
            		tps.gotPacket(packet);

            	}else
            	{
            		tps = tcpStreamList.get(server+serverPort);
            		tps.gotPacket(packet);
            	}
            	
            	if(tps.enDestroy())
            	{
            		tps = null;
            		tcpStreamList.put(server+serverPort, tps);
            		tcpStreamList.remove(server+serverPort);
            		
            	}
                
            }

		
        };
        //直接使用loop
        try {
			handle.loop(1000000, listener);
		} catch (PcapNativeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
		
	}
	
	*//**
	     * 根据IP获取指定网卡设备
	* @param localHost 网卡IP
	* 
	* @return 指定的设备对象
	*//*
	public static PcapNetworkInterface getCaptureNetworkInterface(String localHost) {
	List<PcapNetworkInterface> allDevs;
	try {
	 // 获取全部的网卡设备列表，Windows如果获取不到网卡信息，输入：net start npf  启动网卡服务
	 allDevs = Pcaps.findAllDevs();
	
	 for (PcapNetworkInterface networkInterface : allDevs) {
	     List<PcapAddress> addresses = networkInterface.getAddresses();
	     for (PcapAddress pcapAddress : addresses) {
	         // 获取网卡IP地址
	         String ip = pcapAddress.getAddress().getHostAddress();
	//         System.out.println(ip);
	         if (ip != null && ip.contains(localHost)) {
	             // 返回指定的设备对象
	//         	System.out.println("filter:"+ip);
	         	
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

}
*/
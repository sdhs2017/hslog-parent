package com.jz.bigdata.business.logAnalysis.collector.pcap4j;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

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


public class Pcap4jCollector implements Callable<String>{
	
	/**
	 * 需要获取网卡信息的IP地址
	 */
	private String ip;
	
	static String client ="";
	
	private PcapHandle handle = null;
	
	private PacketListener listener =null;
	
	static HashMap<String, TcpStream> tcpStreamList=new HashMap<String, TcpStream>();
	
	public Pcap4jCollector(String ip,PcapHandle handle,PacketListener listener) {
		this.ip = ip;
		this.handle = handle;
		this.listener = listener;
	}

	@Override
	public String call() {
    	
        //直接使用loop
		// loop 参数说明-1代表无穷包接收
        try {
			handle.loop(-1, listener);
		} catch (PcapNativeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.out.println("---------------jiyourui----------handle.loop--报错信息:------------"+e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//System.out.println("---------------jiyourui----------handle.loop--报错信息:------------"+e.getMessage());
			e.printStackTrace();
		} catch (NotOpenException e) {
			// TODO Auto-generated catch block
			//System.out.println("---------------jiyourui----------handle.loop--报错信息:------------"+e.getMessage());
			e.printStackTrace();
		}
	
		return null;
	}
	
	public boolean getPcap4jStatus() {
		return handle.isOpen();
	}

	public void closePcap4j() {
		try {
			handle.breakLoop();
		} catch (NotOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		handle.close();
	}

}

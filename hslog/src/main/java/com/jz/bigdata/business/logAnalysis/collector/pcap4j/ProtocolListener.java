package com.jz.bigdata.business.logAnalysis.collector.pcap4j;

public class ProtocolListener {

	public String onSend(String seq,byte[] data)
	{
//		String s = new String(data);
		//System.out.println("onSend:"+seq+":"+s);
		return "";
	}
	public String onRecv(String seq,byte[] data)
	{
//		String s = new String(data);
		//System.out.println("onRecv:"+seq+":"+s);
		return "";
	}
}

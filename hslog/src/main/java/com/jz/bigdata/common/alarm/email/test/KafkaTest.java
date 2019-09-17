package com.jz.bigdata.common.alarm.email.test;

import java.util.concurrent.BlockingQueue;

import com.jz.bigdata.common.alarm.email.MessageInfo;

public class KafkaTest implements Runnable{
	
	private BlockingQueue<MessageInfo> mi;
	public KafkaTest(BlockingQueue<MessageInfo> mi){
		this.mi = mi;
	}

	int i=0;
	
	@Override
	public void run() {
		while(true){
			System.out.println("数据处理中....k"+i);
			MessageInfo m = new MessageInfo();
			m.setTitle("title"+i);
			try {
				mi.put(m);
				Thread.sleep(1000);
				i++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	

}

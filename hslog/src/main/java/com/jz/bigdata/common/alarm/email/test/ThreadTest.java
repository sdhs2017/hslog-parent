package com.jz.bigdata.common.alarm.email.test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.jz.bigdata.common.alarm.email.MessageInfo;

public class ThreadTest {
	public static void main(String[] args){
		
//		MessageInfo mi = new MessageInfo();
		
		
		//缓冲池
		BlockingQueue<MessageInfo> mi = new LinkedBlockingQueue<MessageInfo>(10);
//		KafkaTest k = new KafkaTest(mi);
//		SendEmailTest s = new SendEmailTest(mi);
//		k.run();
//		s.run();
		
		Thread k = new Thread(new KafkaTest(mi));
		Thread s = new Thread(new SendEmailTest(mi));
		
		k.start();
		s.start();
		
	}

}

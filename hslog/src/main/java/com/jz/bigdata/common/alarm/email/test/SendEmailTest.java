package com.jz.bigdata.common.alarm.email.test;

import java.util.concurrent.BlockingQueue;

import com.jz.bigdata.common.alarm.email.MessageInfo;

public class SendEmailTest implements Runnable{
	
	private BlockingQueue<MessageInfo> mi;
	public SendEmailTest(BlockingQueue<MessageInfo> mi){
		this.mi = mi;
	}

	int i=0;
	
	@Override
	public void run() {
		
		
		while(true){
			System.out.println("发送邮件中：....s"+i);
			try {
				MessageInfo m = mi.take();
				System.out.println(m.getTitle());
//				wait();
				Thread.sleep(2000);
				i++;
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}

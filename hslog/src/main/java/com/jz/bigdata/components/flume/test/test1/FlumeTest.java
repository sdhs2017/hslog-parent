package com.jz.bigdata.components.flume.test.test1;

/**
 * 
 * http://blog.csdn.net/high2011/article/details/51596919
 * http://blog.csdn.net/u010022051/article/details/42776695
 * @author Dy
 *
 */
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;

public class FlumeTest {
	
	public static void main(String[] args) {
//		MyRpcClientFacade client = new MyRpcClientFacade();
//		client.init("192.168.142.115", 41414); // flume所在的机器ip，testflume5配置的java端口号
//		for (int i = 0; i < 13; i++) {
//			String sampleData = "Hello Flume!" + new Random().nextInt(220);
//			client.sendDataToFlume(sampleData);
//		}
//		client.cleanUp();
		
	   Date currentTime = new Date();
//	   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	   String dateString = formatter.format(currentTime);
//	   System.out.println(dateString);
	   
	}
}

class MyRpcClientFacade {
	private RpcClient client;
	private String hostname;
	private int port;

	public void init(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		this.client = RpcClientFactory.getDefaultInstance(hostname, port);
	}

	public void sendDataToFlume(String data) {
		Event event = EventBuilder.withBody(data, Charset.forName("UTF-8"));
		try {
			client.append(event);
		} catch (EventDeliveryException e) {
			client.close();
			client = null;
			client = RpcClientFactory.getDefaultInstance(hostname, port);
		}
	}

	public void cleanUp() {
		client.close();
	}
}
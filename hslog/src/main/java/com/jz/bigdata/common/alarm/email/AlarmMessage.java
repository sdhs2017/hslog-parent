package com.jz.bigdata.common.alarm.email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class AlarmMessage {
	
	
	
	public MimeMessage getMessage(Session session,MessageInfo messageInfo,String sendFrom,String... sendToArray) throws AddressException, MessagingException{

		// 创建邮件对象
		MimeMessage message = new MimeMessage(session);
		// 指明邮件的发件人
		message.setFrom(new InternetAddress(sendFrom));
		
//		@SuppressWarnings("static-access")
//		InternetAddress[] internetAddressTo = new InternetAddress().parse("15806410519@163.com,15806410519@sohu.com,410183089@qq.com");  
		
		//遍历数组
		String sendTo="";
		for(String s:sendToArray){
			sendTo=sendTo+s+",";
		}
		//去掉最后一个逗号
		@SuppressWarnings("static-access")
		InternetAddress[] internetAddressTo = new InternetAddress().parse(sendTo.substring(0,sendTo.length()-1));
		
		// 指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
//				message.setRecipient(Message.RecipientType.TO, new InternetAddress("15806410519@163.com"));
//				message.setRecipient(Message.RecipientType.CC, new InternetAddress("15806410519@sohu.com"));
		message.setRecipients(Message.RecipientType.TO,internetAddressTo); 
//				message.setRecipient(Message.RecipientType.TO, new InternetAddress("1012156899@qq.com"));
//				message.setRecipient(Message.RecipientType.TO, new InternetAddress("15806410519@sina.cn")); 
		// 邮件的标题
//		message.setSubject("测试：只包含文本的简单邮件");
		message.setSubject(messageInfo.getTitle());
		// 邮件的文本内容
//		message.setContent("你好啊！"+ip+":"+name+":"+logMessage, "text/html;charset=UTF-8");
		message.setContent("你好啊！"+messageInfo.getIp()+":"+messageInfo.getName()+":"+messageInfo.getLogMessage(), "text/html;charset=UTF-8");
		// 返回创建好的邮件对象
		return message;
		
	}
	
	public Session getSession(){
		Properties properties = new Properties();
		//新浪
//		properties.setProperty("mail.host", "smtp.sina.cn");
		//qq
		properties.setProperty("mail.host", "smtp.qq.com");
		properties.setProperty("mail.transport.protocol", "smtp");
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.port", "465");
		properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		
		// 1、创建session
		Session session = Session.getInstance(properties);
		// 开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
		session.setDebug(true);
		
		return session;
	}

}

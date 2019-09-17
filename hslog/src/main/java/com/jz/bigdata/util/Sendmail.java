package com.jz.bigdata.util;
import java.util.Properties;  
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;  
import javax.mail.Transport;  
import javax.mail.internet.InternetAddress;  
import javax.mail.internet.MimeMessage;  
  
/** 
* @ClassName: Sendmail 
* @Description: Sendmail类继承Thread，因此Sendmail就是一个线程类，这个线程类用于给指定的用户发送Email 
* @author: 石成宇
* @date: 2015-1-12 下午10:43:48 
* 
*/   
public class Sendmail extends Thread {  
	
	
	
	
	public  Sendmail(String ip,String name,String logMessage ,String... email) throws MessagingException{
		Properties properties = new Properties();
		//QQ服务器
//		properties.setProperty("mail.host", "smtp.sina.cn");
		properties.setProperty("mail.host", "smtp.qq.com");
		//协议名称。smtp表示简单邮件传输协议
		properties.setProperty("mail.transport.protocol", "smtp");
		// 将这个参数设为true，让服务器进行认证,认证用户名和密码是否正确  
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.port", "465"); 
		//使用ssl协议来保证连接安全
		properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		// // 使用JavaMail发送邮件的5个步骤
		// 1、创建session
		Session session = Session.getInstance(properties);
		// 开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
		session.setDebug(true);
		// 2、通过session得到transport对象
		Transport ts = session.getTransport();
		// 3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给smtp服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
		// qq账号需要授权填写的密码为授权码，不是账号登录密码，其他如sina,163,sohu等邮箱为检测视情况而定
		// QQ邮箱需要使用SSL，端口号465或587 ts.connect("smtp.qq.com",587,"QQ号","授权码");
		// 4、创建邮件
		ts.connect( "410183089@qq.com", "zcpticzijsqnbhjf");
//		ts.connect("15806410519", "as123456"); 
		Message message;
		try {
			message = createSimpleMail(session,ip,name,logMessage,email);
			// 5、发送邮件
			
			
			ts.sendMessage(message, message.getAllRecipients());
			// zcpticzijsqnbhjf
			// pgzprusjwqzqbjcj
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ts.close();
	}
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Sendmail sendmail=new Sendmail("192.168.0.129","linux","危险","410183089@qq.com,15806410519@sohu.com");
		
	}

	public static MimeMessage createSimpleMail(Session session,String ip,String name,String logMessage,String... sendToArray) throws Exception {
		// 创建邮件对象
		MimeMessage message = new MimeMessage(session);
		// 指明邮件的发件人
		message.setFrom(new InternetAddress("410183089@qq.com"));
		//遍历数组
		String sendTo="";
		for(String s:sendToArray){
			sendTo=sendTo+s+",";
		}
		
		@SuppressWarnings("static-access")
		InternetAddress[] internetAddressTo = new InternetAddress().parse(sendTo.substring(0,sendTo.length()-1));
		
//		@SuppressWarnings("static-access")
//		InternetAddress[] internetAddressTo = new InternetAddress().parse("15806410519@163.com,15806410519@sohu.com,410183089@qq.com");  
		
		// 指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
//		message.setRecipient(Message.RecipientType.TO, new InternetAddress("15806410519@163.com"));
//		message.setRecipient(Message.RecipientType.CC, new InternetAddress("15806410519@sohu.com"));
		message.setRecipients(Message.RecipientType.TO,internetAddressTo); 
//		message.setRecipient(Message.RecipientType.TO, new InternetAddress("1012156899@qq.com"));
//		message.setRecipient(Message.RecipientType.TO, new InternetAddress("15806410519@sina.cn")); 
		// 邮件的标题
		message.setSubject("事件告警");
		// 邮件的文本内容
		message.setContent("你好！<br/> 主机名 :    "+name+"<br/>     ip:   "+ip+"<br/>    报警内容:     "+logMessage, "text/html;charset=UTF-8");
		// 返回创建好的邮件对象
		return message;

	}
}  
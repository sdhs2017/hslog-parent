package com.jz.bigdata.util;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @author shichengyu
 * @description controller请求拦截器，请求到达controller前
 */
public class SendEmail extends Thread {
	
	private String ip="";
	private String name="";
	private String logMessage="";
	private String email="";
	

	public SendEmail(String ip,String name,String logMessage,String email) {
		 this.ip=ip;
		 this.name=name;
		 this.logMessage=logMessage;
		 this.email = email;
		 
	}

	public SendEmail() {
		// this.email = email;
	}

	public void run() {
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
		// 2、通过session得到transport对象
		Transport ts = null;
		// 3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给smtp服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
		// qq账号需要授权填写的密码为授权码，不是账号登录密码，其他如sina,163,sohu等邮箱为检测视情况而定
		// QQ邮箱需要使用SSL，端口号465或587 ts.connect("smtp.qq.com",587,"QQ号","授权码");
		// 4、创建邮件
//		ts.connect( "410183089@qq.com", "zcpticzijsqnbhjf");
//		ts.connect("15806410519", "as123456"); 
		Message message;
		try {
			ts = session.getTransport();
			//qq
			ts.connect( "410183089@qq.com", "zcpticzijsqnbhjf");
			//新浪邮箱
//			ts.connect("15806410519", "as123456"); 
			message = createSimpleMail(session,ip,name,logMessage);
			// 5、发送邮件
			ts.sendMessage(message, message.getAllRecipients());
			// zcpticzijsqnbhjf
			// pgzprusjwqzqbjcj
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ts.close();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		SendEmail ww=new SendEmail("192.168.0.129","linux","危险","15806410519@sohu.com");
		ww.start();

//		Properties properties = new Properties();
//		//QQ服务器
////		properties.setProperty("mail.host", "smtp.sina.cn");
//		properties.setProperty("mail.host", "smtp.qq.com");
//		//协议名称。smtp表示简单邮件传输协议
//		properties.setProperty("mail.transport.protocol", "smtp");
//		// 将这个参数设为true，让服务器进行认证,认证用户名和密码是否正确  
//		properties.setProperty("mail.smtp.auth", "true");
//		properties.setProperty("mail.smtp.port", "465"); 
//		//使用ssl协议来保证连接安全
//		properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//		// properties.setProperty("mail.smtp.port", 465);
//		// properties.setProperty("mail.smtp.socketFactory.port", 465);
//		// prop.setProperty("mail.host", "smtp.qq.com");
//		// prop.setProperty("mail.transport.protocol", "smtp");
//		// prop.setProperty("mail.smtp.auth", "true");
//		// // 使用JavaMail发送邮件的5个步骤
//		// 1、创建session
//		Session session = Session.getInstance(properties);
//		// 开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
//		session.setDebug(true);
//		// 2、通过session得到transport对象
//		Transport ts = session.getTransport();
//		// 3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给smtp服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
//		// qq账号需要授权填写的密码为授权码，不是账号登录密码，其他如sina,163,sohu等邮箱为检测视情况而定
//		// QQ邮箱需要使用SSL，端口号465或587 ts.connect("smtp.qq.com",587,"QQ号","授权码");
//		// 4、创建邮件
//		ts.connect( "410183089@qq.com", "zcpticzijsqnbhjf");
////		ts.connect("15806410519", "as123456"); 
//		Message message;
//		try {
//			message = createSimpleMail(session,ip,name,logMessage);
//			// 5、发送邮件
//			ts.sendMessage(message, message.getAllRecipients());
//			// zcpticzijsqnbhjf
//			// pgzprusjwqzqbjcj
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		ts.close();
	}

	public static MimeMessage createSimpleMail(Session session,String ip,String name,String logMessage) throws Exception {
		// 创建邮件对象
		MimeMessage message = new MimeMessage(session);
		// 指明邮件的发件人
		message.setFrom(new InternetAddress("410183089@qq.com"));
		@SuppressWarnings("static-access")
		InternetAddress[] internetAddressTo = new InternetAddress().parse("15806410519@163.com,15806410519@sohu.com,410183089@qq.com");  
		
		// 指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
//		message.setRecipient(Message.RecipientType.TO, new InternetAddress("15806410519@163.com"));
//		message.setRecipient(Message.RecipientType.CC, new InternetAddress("15806410519@sohu.com"));
		message.setRecipients(Message.RecipientType.TO,internetAddressTo); 
//		message.setRecipient(Message.RecipientType.TO, new InternetAddress("1012156899@qq.com"));
//		message.setRecipient(Message.RecipientType.TO, new InternetAddress("15806410519@sina.cn")); 
		// 邮件的标题
		message.setSubject("只包含文本的简单邮件");
		// 邮件的文本内容
		message.setContent("你好啊！"+ip+":"+name+":"+logMessage, "text/html;charset=UTF-8");
		// 返回创建好的邮件对象
		return message;

	}

}
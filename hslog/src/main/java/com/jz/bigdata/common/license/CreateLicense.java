package com.jz.bigdata.common.license;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.prefs.Preferences;

import javax.security.auth.x500.X500Principal;

import de.schlichtherle.license.CipherParam;
import de.schlichtherle.license.DefaultCipherParam;
import de.schlichtherle.license.DefaultKeyStoreParam;
import de.schlichtherle.license.DefaultLicenseParam;
import de.schlichtherle.license.KeyStoreParam;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseParam;

public class CreateLicense {

	//common param
	private static String PRIVATEALIAS = "";
	private static String KEYPWD = "";
	private static String STOREPWD = "";
	private static String SUBJECT = "";
	private static String licPath = "";
	private static String priPath = "";
	//license content
	private static String issuedTime = "";
	private static String notBefore = "";
	private static String notAfter = "";
	private static String consumerType = "";
	private static int consumerAmount = 0;
	private static String info = "";
	// 为了方便直接用的API里的例子
	// X500Princal是一个证书文件的固有格式，详见API
	//private final static X500Principal DEFAULTHOLDERANDISSUER = new X500Principal("CN=Duke、OU=JavaSoft、O=Sun Microsystems、C=US");
	private final static X500Principal DEFAULTHOLDERANDISSUER = new X500Principal("CN=hs,OU=hs,O=hs,L=jinan, ST=sd, C=ZH");
		
	public void setParam(String propertiesPath) {
		// 获取参数
		Properties prop = new Properties();
		InputStream in = CreateLicense.class.getClassLoader().getResourceAsStream(propertiesPath);
		InputStream keystores = getClass().getResourceAsStream("/privateKeys.keystore");
		final InputStream keystore = CreateLicense.class.getClassLoader().getResourceAsStream("privateKeys.keystore");
		try {
			prop.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PRIVATEALIAS = prop.getProperty("PRIVATEALIAS");
		KEYPWD = prop.getProperty("KEYPWD");
		STOREPWD = prop.getProperty("STOREPWD");
		SUBJECT = prop.getProperty("SUBJECT");
		KEYPWD = prop.getProperty("KEYPWD");
		licPath = prop.getProperty("licPath");
		priPath = prop.getProperty("priPath");
		//license content
		issuedTime = prop.getProperty("issuedTime");
		notBefore = prop.getProperty("notBefore");
		notAfter = prop.getProperty("notAfter");
		consumerType = prop.getProperty("consumerType");
		consumerAmount = Integer.valueOf(prop.getProperty("consumerAmount"));
		info = prop.getProperty("info");
		
	}

	public boolean create() {		
		try {
			/************** 证书发布者端执行 ******************/
			LicenseManager licenseManager = LicenseManagerHolder
					.getLicenseManager(initLicenseParams0());
			licenseManager.store((createLicenseContent()), new File(licPath));	
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("客户端证书生成失败!");
			return false;
		}
		System.out.println("服务器端生成证书成功!");
		return true;
	}

	// 返回生成证书时需要的参数
	private static LicenseParam initLicenseParams0() {
		Preferences preference = Preferences
				.userNodeForPackage(CreateLicense.class);
		// 设置对证书内容加密的对称密码
		CipherParam cipherParam = new DefaultCipherParam(STOREPWD);
		// 参数1,2从哪个Class.getResource()获得密钥库;参数3密钥库的别名;参数4密钥库存储密码;参数5密钥库密码
		KeyStoreParam privateStoreParam = new DefaultKeyStoreParam(
				CreateLicense.class, priPath, PRIVATEALIAS, STOREPWD, KEYPWD);
		LicenseParam licenseParams = new DefaultLicenseParam(SUBJECT,
				preference, privateStoreParam, cipherParam);
		return licenseParams;
	}

	// 从外部表单拿到证书的内容
	public final static LicenseContent createLicenseContent() {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		LicenseContent content = null;
		content = new LicenseContent();
		content.setSubject(SUBJECT);
		content.setHolder(DEFAULTHOLDERANDISSUER);
		content.setIssuer(DEFAULTHOLDERANDISSUER);
		try {
			content.setIssued(format.parse(issuedTime));
			content.setNotBefore(format.parse(notBefore));
			content.setNotAfter(format.parse(notAfter));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		content.setConsumerType(consumerType);
		content.setConsumerAmount(consumerAmount);
		content.setInfo(info);
		// 扩展
		content.setExtra(new Object());
		return content;
	}
	
	public InputStream getResourceAsStream(String path) {
		return CreateLicense.class.getClassLoader().getResourceAsStream(path);
		
	}
}

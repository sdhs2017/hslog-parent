package com.jz.bigdata.common.license;

public class licenseVerifyTest {

	public static void main(String[] args){
		VerifyLicense vLicense = new VerifyLicense();
		//获取参数
		vLicense.setParam("/verifyparam.properties");
		//验证证书
		vLicense.verify();
	}
}

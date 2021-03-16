package com.jz.bigdata.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @author shichengyu
 * @date 2017年12月8日 上午10:56:31
 * @description
 * MD5加密
 */
public class MD5 {
	public static String EncoderByMd5(String str)  {
		// 确定计算方法
		MessageDigest md5;
		String newstr="";
		try {
			md5 = MessageDigest.getInstance("MD5");
			//base64解密
			BASE64Decoder base64Decoder = new BASE64Decoder();
			//base64 加密
			BASE64Encoder base64en = new BASE64Encoder();
			// 加密后的字符串
			newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));


//			EncoderByMd5
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return newstr;
	}

}

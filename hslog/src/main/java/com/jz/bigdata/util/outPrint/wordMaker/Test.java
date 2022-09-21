package com.jz.bigdata.util.outPrint.wordMaker;

import java.io.UnsupportedEncodingException;

public class Test {
	
	public static void main(String[] args){
		String b = "";
		try {
			b = new String(b.getBytes("GBK"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

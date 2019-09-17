package com.jz.bigdata.common.alarm.email;

import java.util.Arrays;

public class Test {
	
	public static void main(String[] args){
		test2("15806410519@163.com","15806410519@sohu.com","410183089@qq.com");
		
		
	}
	
	public static void test(String... t){
		System.out.println(Arrays.toString(t));
	}
	
	public static void test2(String... t){
		String m = "";
		for(String s : t){
			m=m+s+",";
		}
		System.out.println(m.substring(0, m.length()-1));
	}

}

package com.jz.bigdata.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pattern_Matcher {

	/**
	 * 正则匹配
	 * @param soap 内容
	 * @param rgex 正则
	 * @return 返回匹配的内容
	 */
 	public static String getMatchedContent(String soap,String rgex){  
         Pattern pattern = Pattern.compile(rgex);// 匹配的模式  
         Matcher m = pattern.matcher(soap);  
         while(m.find()){
             return m.group(0);
         }  
         return "";  
    }
	/**
	 * 正则匹配
	 * @param soap 内容
	 * @param regex 正则
	 * @return 返回是否匹配成功
	 */
	public static Boolean isMatched(String soap,String regex){
		Pattern pattern = Pattern.compile(regex);// 匹配的模式
		Matcher m = pattern.matcher(soap);
		return m.find();
	}
 	/**
	 * 正则匹配
	 * @param soap 内容
	 * @param rgex 正则
	 * @return 返回正则（.*?）中匹配的内容
	 */
   	public static String getMatchedContentByParentheses(String soap, String rgex) {
   		Pattern pattern = Pattern.compile(rgex);// 匹配的模式
   		Matcher m = pattern.matcher(soap);
   		while (m.find()) {
   			return m.group(1);
   		}
   		return "";
   	}

	/**
	 * 正则匹配
	 * @param soap 内容
	 * @param rgex 正则
	 * @return 返回正则（.*?）中匹配的内容
	 */
	public static Matcher getMatchedByParentheses(String soap, String rgex) {
		Pattern pattern = Pattern.compile(rgex);// 匹配的模式
		Matcher m = pattern.matcher(soap);
		return m;
	}
   	
   	public static void main(String [] args) {

	}
}

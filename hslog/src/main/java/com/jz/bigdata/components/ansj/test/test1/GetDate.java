package com.jz.bigdata.components.ansj.test.test1;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shichengyu
 * @date 2017年8月11日 下午1:17:00
 * @description
 * 传字符串截取时间
 */
public class GetDate {
	
	public static void main(String[] args){
		String data = "2017-08-10 11:54:34,352 [DEBUG]  [==>  Preparing: select id, name, level, superiorId, subordinate,orderId,comment from department where 1=1 and level = ? ]  [com.jz.bigdata.common.department.dao.IDepartmentDao.selectAll] debug()[139] [210348] ";
		System.out.println(getDate(data));
	}
	
	public static String getDate(String data){
		
		Pattern pattern = Pattern.compile("[0-9]{4}[-][0-9]{1,2}[-][0-9]{1,2}[ ][0-9]{1,2}[:][0-9]{1,2}[:][0-9]{1,2}");
		 
		Matcher matcher = pattern.matcher(data);  
		String dateStr = null;  
		if(matcher.find()){  
			dateStr = matcher.group(0);  
		}  
		  
		String str =dateStr.toString();  
//		System.out.println(str);  
		return str;
		
		
	}

}

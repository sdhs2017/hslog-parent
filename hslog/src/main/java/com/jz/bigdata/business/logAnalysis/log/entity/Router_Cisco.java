package com.jz.bigdata.business.logAnalysis.log.entity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Router_Cisco {

public final static Map<Integer, String> facility = new HashMap<>();
	
	public final static Map<Integer, String> Severity = new HashMap<>();
	static{
		facility.put(0, "kern");	//kernel messages
		facility.put(1, "user");	//user-level messages
		facility.put(2, "mail");	//mail system
		facility.put(3, "daemon");	//system daemons
		facility.put(4, "auth");	//security/authorization messages
		facility.put(5, "syslog");	//messages generated internally by syslogd
		facility.put(6, "lpr");		//line printer subsystem
		facility.put(7, "news");	//network news subsystem
		facility.put(8, "uucp");	//UUCP subsystem
		facility.put(9, "kern");	//clock daemon
		facility.put(10, "authpriv");	//security/authorization messages
		facility.put(11, "ftp");	//FTP daemon
		facility.put(15, "cron");	//scheduling daemon
		 	 	
		 	 	
		Severity.put(0, "Emergency"); 		//system is unusable
		Severity.put(1, "Alert");	 		//action must be taken immediately
		Severity.put(2, "Critical");	 		//critical conditions
		Severity.put(3, "Error");			//error conditions
		Severity.put(4, "Warn");			//warning conditions
		Severity.put(5, "Notice");			//normal but significant condition
		Severity.put(6, "Info");	//informational messages
		Severity.put(7, "Debug");			//debug-level messages
	}
	
	String operation_facility;
	
	String severity;
	
	String logtime;
	
	String hostname;
	
	String ip;
	
	String msg;
	/**
	 * 日志级别
	 */
	String operation_level;
	/**
	 * 操作描述
	 */
	String operation_des;
	
	public Router_Cisco() {
		
	}
	
	public Router_Cisco(String log) {
		// PRI
		Pattern PRIpattern = Pattern.compile("<[0-9]{1,5}>");
		Matcher PRImatcher = PRIpattern.matcher(log);
		// 时间1
		Pattern datePattern1 = Pattern.compile("[0-9]{4}[-][0-9]{1,2}[-][0-9]{1,2}[ ][0-9]{1,2}[:][0-9]{1,2}[:][0-9]{1,2}");
		Matcher datematcher1 = datePattern1.matcher(log);
		// 时间2
		Pattern datePattern2 = Pattern.compile("[/*A-Za-z]{3}\\s+[0-9]{1,2}\\s+[0-9]{1,2}[:][0-9]{1,2}[:][0-9]{1,2}[.][0-9]{1,3}");
		Matcher datematcher2 = datePattern2.matcher(log);
		// IP
		Pattern ipPattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)");  
        Matcher ipmatcher = ipPattern.matcher(log);
        //
       /* Pattern Pattern = Pattern.compile("[%(.*?):]");  
        Matcher imatcher = ipPattern.matcher(log);*/
        
        
        if (PRImatcher.find()) {
        	String PRI = PRImatcher.group(0);
			PRI = PRI.substring(1, PRI.length()-1);
			Integer PRIint = Integer.valueOf(PRI);
			String facility = getFacility(PRIint/8);
			this.operation_facility = facility;
			System.out.print("日志类型："+facility+"\t");
			String level = getLevel(PRIint%8);
			this.operation_level = level;
			System.out.print("日志级别:"+level+"\t");
		}
        
        if (datematcher1.find()&&datematcher2.find()) {
			System.out.println(datematcher1.group(0));
			System.out.println(datematcher2.group(0));
			//this.logtime = 
		}
        
        if (ipmatcher.find()) {
			this.ip = ipmatcher.group(0);
		}
	}
	
	public static String getFacility(Integer value) {
		return facility.get(value);
	}
	public static String getLevel(Integer value) {
		return Severity.get(value);
	}
	
	public static void main(String [] args) throws IOException {
		FileInputStream fis=new FileInputStream("C:\\Users\\jiyourui\\Desktop\\all.log");
        InputStreamReader isr=new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line = "";
        while ((line=br.readLine())!=null) {
        	System.out.println(line);
        	Router_Cisco router_Cisco = new Router_Cisco(line);
        }
	}
}

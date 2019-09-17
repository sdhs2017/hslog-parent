package com.jz.bigdata.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jz.bigdata.business.logAnalysis.log.LogType;
import com.jz.bigdata.business.logAnalysis.log.entity.Log4j;
import com.jz.bigdata.business.logAnalysis.log.entity.Mysql;
import com.jz.bigdata.business.logAnalysis.log.entity.PacketFilteringFirewal;
import com.jz.bigdata.business.logAnalysis.log.entity.Syslog;
import com.jz.bigdata.business.logAnalysis.log.entity.Winlog;

public class Testentity {

	public static final String DEFAULT_REGEX = "^ java.|^   at";
	
	public String format(String log) {
		
		if (log==null||log.equals("")) {
			System.out.println("数据为空！");
			return "";
		}
		
		StringBuilder builder = new StringBuilder();
		
		Gson gson = new GsonBuilder()
				 .setDateFormat("yyyy-MM-dd HH:mm:ss")  
				 .create(); 
		String json;
		
		//日志类型
		String logType="unknown";
		Log4j log4j = null;
		Winlog winlog = null;
		PacketFilteringFirewal packetFilteringFirewal = null;
		Mysql mysql = null;
		Syslog syslog = null;
		
		// 日志过滤正则
		// log4j日志信息过滤条件
		Pattern facility_pattern = Pattern.compile("local3:");
		Matcher facility_matcher = facility_pattern.matcher(log);
		Pattern pattern = Pattern.compile(DEFAULT_REGEX);
		// 防火墙-包过滤日志信息过滤条件
		Pattern logtype_pattern = Pattern.compile("logtype=1");
		Matcher logtype_matcher = logtype_pattern.matcher(log);
		Pattern dmg_pattern = Pattern.compile("包过滤日志");
		Matcher dmg_matcher = dmg_pattern.matcher(log);
		// 防火墙-其他日志信息过滤条件
		Pattern logothertype_pattern = Pattern.compile("logtype=");
		Matcher logtotherype_matcher = logothertype_pattern.matcher(log);
		Pattern dmgother_pattern = Pattern.compile("dsp_msg=");
		Matcher dmgother_matcher = dmgother_pattern.matcher(log);
		// windows安全审计
		Pattern win2008pattern = Pattern.compile("Security-Auditing:");
		Matcher win2008matcher = win2008pattern.matcher(log);
		Pattern win2003pattern = Pattern.compile("Security:");
		Matcher win2003matcher = win2003pattern.matcher(log);
		// mysql日志
		Pattern mysqlpattern = Pattern.compile("timestamp");
		Matcher mysqlmatcher = mysqlpattern.matcher(log);
		
		if (facility_matcher.find()) {
			logType = LogType.LOGTYPE_LOG4J;
			synchronized (log) {
				String logleft = log.substring(0, log.indexOf(facility_matcher.group(0))+facility_matcher.group(0).length());
				
				Matcher m = pattern.matcher(log.replace(logleft, ""));
				//判断是否符合正则表达式 如果符合，表明这是一条开始数据
				if(m.find()) {
					log = log.replace(logleft, "");
					//添加数据
					builder.append(" \\005 "+log);
				}else {
					//添加builder
					if (builder.length()!=0) {
						//进入范式化
						try {
							log4j = new Log4j(builder.toString());
						}catch (Exception e) {
							System.out.println(logType+"范式化报错！");
						}
						
						json = gson.toJson(log4j);
						System.out.println(logType+"范式化内容："+json);

						//清空数据
						builder.delete(0, builder.length());
					}
					builder.append(log);
					
				}
			}
		}else if (logtype_matcher.find()&&dmg_matcher.find()) {
			logType = LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG;
			try {
				packetFilteringFirewal = new PacketFilteringFirewal(log);
			} catch (Exception e) {
				System.out.println(logType+"范式化报错！");
			}
			
			json = gson.toJson(packetFilteringFirewal);
			System.out.println(logType+"范式化内容："+json);
			
		//es暂无防火墙包过滤日志对应的mapping，暂未入库es
		}else if(logtotherype_matcher.find()&&dmgother_matcher.find()){
			//防火墙、不包括包过滤日志，暂不处理
			System.out.println("-------不做处理-------------");
		}else if (mysqlmatcher.find()) {
			logType = LogType.LOGTYPE_MYSQLLOG;
			try {
				mysql = new Mysql(log);
			} catch (Exception e) {
				System.out.println(logType+"范式化报错！");
			}
			
			json = gson.toJson(mysql);
			System.out.println(logType+"范式化内容："+json);
		}
		else if(win2003matcher.find()||win2008matcher.find()){
			//windows、evtsys组件收集日志
			logType = LogType.LOGTYPE_WINLOG;
			try {
				winlog = new Winlog(log);
			} catch (Exception e) {
				System.out.println(logType+"范式化报错！");
			}
			
			json = gson.toJson(winlog);
			System.out.println(logType+"范式化内容："+json);
		}else {
			logType = LogType.LOGTYPE_SYSLOG;
			try {
				syslog = new Syslog(log);
			} catch (Exception e) {
				System.out.println(logType+"范式化报错！");
			}
			
			json = gson.toJson(syslog);
			System.out.println(logType+"范式化内容："+json);
		}
		
		return "";
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		if (args.length<2) {
			System.out.println("参数设置有误！参考：Testentity.jar 1 ''");
		}else {
			Testentity testentity = new Testentity();
			if (args[0].equals("1")||args.equals("文件")) {
				File file = new File(args[1]);
				try{
		            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
		            String s = null;
		            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
		                //result.append(System.lineSeparator()+s);
		            	testentity.format(s);
		            }
		            br.close();    
		        }catch(Exception e){
		            e.printStackTrace();
		        }
			}else if (args[0].equals("0")||args.equals("日志")) {
				testentity.format(args[1]);
			}
		}
		
	}

}

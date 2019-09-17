package com.jz.bigdata.components.zabbix.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtilLocal {
	
	public static void main(String[] args) {
		
		DateUtilLocal t = new DateUtilLocal();
		
//		Date d = new Date();
//		String beginDate = "1435845268096";
//
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//		String sd = sdf.format(new Date(Long.parseLong(beginDate))); // 时间戳转换日期
//		System.out.println(d.getTime());
//		System.out.println(sd);
//		String sdt = sdf.format(d);
//		System.out.println("当前系统时间:" + sdt);
//		System.out.println("系统时间戳:" + d.getTime());
//		System.out.println("时间戳转换日期:" + sd);
//		try{
//			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
//			d = sf.parse("2015-07-03 22:20:20");// 日期转换为时间戳
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		long timeStemp = d.getTime();
//		System.out.println("日期转换时间戳:" + timeStemp);
//

//		System.out.println(">>>>>>>>>>>>>//时间戳转换日期>"+ t.stampToTime("1435845268096"));
//		System.out.println("<<<<<<<<<<<<<<<<//日期转换为时间戳<"+ t.timeToStamp("2015-07-03"));
		
		
		System.out.println(t.timeToStamp("2019-06-10"));
		
	}

	/* //时间戳转换日期 */
	public String stampToTime(String stamp) {
		String sd = "";
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sd = sdf.format(new Date(Long.parseLong(stamp))); // 时间戳转换日期
		return sd;
	}

	/* //日期转换为时间戳 */
	public long timeToStamp(String timers) {
		Date d = new Date();
		long timeStemp = 0;
		try {
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			d = sf.parse(timers);// 日期转换为时间戳
		} catch (Exception e) {
			e.printStackTrace();
		}
		timeStemp = d.getTime();
		return timeStemp;
	}

}

package com.jz.bigdata.components.zabbix;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jz.bigdata.components.zabbix.util.DateUtilLocal;
import com.jz.bigdata.components.zabbix.util.ZabbixUtil2;

import io.github.hengyunabc.zabbix.api.DefaultZabbixApi;
import io.github.hengyunabc.zabbix.api.Request;
import io.github.hengyunabc.zabbix.api.RequestBuilder;
import io.github.hengyunabc.zabbix.api.ZabbixApi;

public class ZabbixApiTest {
	
	static String url = "http://192.168.129.129/zabbix/api_jsonrpc.php";
	static String user = "Admin";
	static String pass = "zabbix";
	
	public static void main(String[] args) {
		
		try {
			ZabbixUtil2 zu = new ZabbixUtil2(user,pass,url);
			//获取zabbix中所以的主机群组列表
			String result = zu.getHostGroupList();
			//获取主机列表
			String result2 = zu.getHostList();
			//通过群组id获取主机ID
			String result3 = zu.getHostByGroupid(4);
			//获取zabbix报警列表
			//@param timeFrom 仅返回在给定时间之后生成的警报。
			String result4 = zu.getAlertList_timefrom((new DateUtilLocal().timeToStamp("2019-06-11"))/1000);
			//获取zabbix报警列表
			//@param timeFrom 仅返回在给定时间之后生成的警报。
			String result42 = zu.getAlertList_timetill((new DateUtilLocal().timeToStamp("2019-06-11"))/1000);
			//获取指定组的获取zabbix报警列表
			String result5 = zu.getAlertListByGroupids(4,(new DateUtilLocal().timeToStamp("2019-06-11"))/1000);
			//获取指定组的获取zabbix报警列表
			String result52 = zu.getAlertListByGroupidsWithoutTime(4);
			//获取zabbix最近问题列表
			String result6 = zu.getTriggerInfoList();
			//根据群组id和机器host获取触发器信息列表
			String result7 = zu.getTrigger(4,"Zabbix server");
			//根据触发器id获取触发器信息
			String result8 = zu.getTriggerByTriggerId(13509);
			
			//获取详细信息
			String result9 = zu.getItemList();
			//
			String result10 = zu.getTriggerPrototypeByGroupid(4);
			//
			String result11 = zu.getTriggerPrototypeByTriggerids(13509);
			
			//
//			String result12 = zu.getTriggerInfo(groupid, lastChangeSince);

			
			System.out.println(result9);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

}

package com.jz.bigdata.components.zabbix.util;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.github.hengyunabc.zabbix.api.DefaultZabbixApi;
import io.github.hengyunabc.zabbix.api.Request;
import io.github.hengyunabc.zabbix.api.RequestBuilder;
import io.github.hengyunabc.zabbix.api.ZabbixApi;

public class ZabbixUtil {
	
	static String url = "http://192.168.129.129/zabbix/api_jsonrpc.php";
	static String user = "Admin";
	static String pass = "zabbix";

	
	public static void main(String[] args) {
//		java.util.Date dt = new Date();
//		System.out.println(dt.toString());   //java.util.Date的含义
//		long lSysTime1 = dt.getTime() / 1000;   //得到秒数，Date类型的getTime()返回毫秒数
//		System.out.println(lSysTime1);
		//1560096000000
		
		new ZabbixUtil().test();
		
		
	}
	
	public void test() {
		DefaultZabbixApi zabbixApi = new DefaultZabbixApi(url);
		zabbixApi.init();
        try{
		    boolean loginResult = zabbixApi.login(user, pass);
		    if (!loginResult) {
			    System.err.println("login fail");
		    }
		    //查询所有主机
		    Request request = RequestBuilder.newBuilder().method("hostgroup.get").paramEntry("output", "extend").build();

		    Request request2 = RequestBuilder.newBuilder().method("host.get").
				   paramEntry("output", "extend").build();
		    
		    Request request3 = RequestBuilder.newBuilder().method("alert.get").
                    paramEntry("output","extend").
                    paramEntry("time_from",1560096000).//从某个时间点开始
                    paramEntry("time_till",1560268800).//截止某个时间点
//                    paramEntry("time_from",DateUtil.getTimeMs(6)/1000).//从某个时间点开始
//                    paramEntry("time_till",DateUtil.getTimeMs(5)/1000).//截止某个时间点
                    paramEntry("hostids", new String[]{"10084","10109"}). build();

		    //获取所有触发器信息
		    Request request123 = RequestBuilder.newBuilder().method("trigger.get").
		    		paramEntry("output","extend").build();
		    //获取 监控项id为 24028、24015 的触发器信息
		    Request request456 = RequestBuilder.newBuilder().method("trigger.get").
		    		paramEntry("output","extend").
		    		paramEntry("itemids", new String[]{"10010","10011","10012","10016"}). build();
		    
		    
		    JSONObject json = new JSONObject();
		    json.put("key_", new String[]{"system"});
		    Request request55 = RequestBuilder.newBuilder().method("item.get").
		        paramEntry("output", new String[]{"hostid","key_","itemid","lastvalue","prevvalue"})
		        .paramEntry("hostids", new String[]{"10010"})
//		        .paramEntry("hostids", new String[]{"10112"})
		    	.paramEntry("filter",json).build();

		    
		    
		    //执行请求
		    JSONObject resJson = zabbixApi.call(request55);
		    //处理结果
		    String error = String.valueOf(resJson.get("error"));
		    if (!StringUtils.isEmpty(error) && error != "null") {
		    	System.out.println("调用zabbix接口出错");
		    }else{
				JSONArray jsonArray = resJson.getJSONArray("result");
				String resultStr = jsonArray.toJSONString();
				System.out.println("结果：：：：："+resultStr);
		    }
         }catch(Exception e){
            e.printStackTrace();
         }finally {
            if (zabbixApi!=null) {
            	zabbixApi.destroy();
            }
         }    
	}
	
	
	public void loginTest() {
		String url = "http://192.168.129.129/zabbix/api_jsonrpc.php";
		ZabbixApi zabbixApi = new DefaultZabbixApi(url);
		zabbixApi.init();

//		boolean login = zabbixApi.login("zabbix.dev", "goK0Loqua4Eipoe");
		boolean login = zabbixApi.login("Admin", "zabbix");
		System.out.println("login:" + login);

		String host = "192.168.129.129";
		JSONObject filter = new JSONObject();

		filter.put("host", new String[] { host });
		Request getRequest = RequestBuilder.newBuilder()
				.method("host.get").paramEntry("filter", filter)
				.build();
		JSONObject getResponse = zabbixApi.call(getRequest);
		System.out.println(getResponse);
//		String hostid = getResponse.getJSONArray("result")
//				.getJSONObject(0).getString("hostid");
//		System.err.println(hostid);
	}

}

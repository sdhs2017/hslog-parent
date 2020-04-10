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
	 * @param rgex 正则
	 * @return 返回是否匹配成功
	 */
	public static Boolean isMatched(String soap,String rgex){
		Pattern pattern = Pattern.compile(rgex);// 匹配的模式
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
   	
   	public static void main(String [] args) {
   		
   		String log ="{\"snapshots\":[{\"snapshot\":\"snapshot\",\"uuid\":\"6gOQWizJQWWrl-KuCwAysQ\",\"version_id\":5040099,\"version\":\"5.4.0\",\"indices\":[\"packet-analysis3\"],\"state\":\"SUCCESS\",\"start_time\":\"2019-05-21T03:05:52.123Z\",\"start_time_in_millis\":1558407952123,\"end_time\":\"2019-05-21T03:07:30.410Z\",\"end_time_in_millis\":1558408050410,\"duration_in_millis\":98287,\"failures\":[],\"shards\":{\"total\":5,\"failed\":0,\"successful\":5}}]}\r\n" + 
   				"";
   		String success = "{\"snapshots\":[{\"snapshot\":\"snapshot\",\"uuid\":\"lUXYw4lxToeTxYC3VX9skg\",\"version_id\":5040099,\"version\":\"5.4.0\",\"indices\":[\"eslog-analysis\"],\"state\":\"SUCCESS\",\"start_time\":\"2019-07-25T06:33:43.993Z\",\"start_time_in_millis\":1564036423993,\"end_time\":\"2019-07-25T07:03:30.988Z\",\"end_time_in_millis\":1564038210988,\"duration_in_millis\":1786995,\"failures\":[],\"shards\":{\"total\":5,\"failed\":0,\"successful\":5}}]}";
   		String failed = "{\"snapshots\":[{\"snapshot\":\"snapshot\",\"uuid\":\"lUXYw4lxToeTxYC3VX9skg\",\"version_id\":5040099,\"version\":\"5.4.0\",\"indices\":[\"eslog-analysis\"],\"state\":\"FAILED\",\"start_time\":\"2019-07-25T06:33:43.993Z\",\"start_time_in_millis\":1564036423993,\"end_time\":\"2019-07-25T07:03:30.988Z\",\"end_time_in_millis\":1564038210988,\"duration_in_millis\":1786995,\"failures\":[],\"shards\":{\"total\":5,\"failed\":0,\"successful\":5}}]}";
   		String in_prcess = "{\"snapshots\":[{\"snapshot\":\"snapshot\",\"uuid\":\"lUXYw4lxToeTxYC3VX9skg\",\"version_id\":5040099,\"version\":\"5.4.0\",\"indices\":[\"eslog-analysis\"],\"state\":\"IN_PROGRESS\",\"start_time\":\"2019-07-25T06:33:43.993Z\",\"start_time_in_millis\":1564036423993,\"failures\":[],\"shards\":{\"total\":0,\"failed\":0,\"successful\":0}}]}";
   		String missLog = "{\"error\":{\"root_cause\":[{\"type\":\"snapshot_missing_exception\",\"reason\":\"[EsBackup:snapshot] is missing\"}],\"type\":\"snapshot_missing_exception\",\"reason\":\"[EsBackup:snapshot] is missing\"},\"status\":404}";
	   	// 日志过滤
		if (Pattern_Matcher.getMatchedContentByParentheses(log, "\"state\":\"(.*?)\"")!="") {
			System.out.println(Pattern_Matcher.getMatchedContentByParentheses(log, "\"state\":\"(.*?)\""));
		}
		// 日志过滤
		if (Pattern_Matcher.getMatchedContent(missLog, "\"type\":\"snapshot_missing_exception\"")!="") {
			String miss = Pattern_Matcher.getMatchedContent(missLog, "\"type\":\"snapshot_missing_exception\"");
			System.out.println(miss);
			if (miss.contains("missing")) {
				System.out.println(miss);
			}
		}
		
		String resultSuccess = Pattern_Matcher.getMatchedContentByParentheses(failed,
				"\"state\":\"(.*?)\"");
		String resultMissing = Pattern_Matcher.getMatchedContent(failed,
				"\"type\":\"snapshot_missing_exception\"");

		// createRepertory();
		// 判断备份快照状态，成功
		if (resultSuccess.equals("SUCCESS")) {
			System.out.println("上次创建快照成功");
			System.out.println("删除成功快照");
			System.out.println("创建快照");
		// 判断快照状态提示快照missing，没有创建过快照
		} else if(resultMissing.contains("missing")){
			// 初次创建快照
			System.out.println("第一次创建");
			System.out.println("删除快照");
			System.out.println("创建快照");
		} else if (resultSuccess.equals("IN_PROGRESS")) {
			System.out.println("创建快照中，不需要任何操作");
		} else if (resultSuccess.equals("FAILED")) {
			System.out.println("上次创建快照失败");
			System.out.println("删除失败快照");
			System.out.println("创建快照");
		}else {
			// 其他任何异常情况
			System.out.println("其他异常导致快照状态不正常");
			System.out.println("删除快照");
			System.out.println("创建快照");
		}

	}
}

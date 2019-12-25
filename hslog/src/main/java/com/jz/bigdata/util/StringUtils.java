package com.jz.bigdata.util;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Default;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.abel533.echarts.Option;

/**
 * @author yiyang 2016-07-23
 * 处理String字符串的类
 */
public class StringUtils {
	
	/**
	 * @param ids 以逗号分隔的id,id,id,
	 * @return 带上单引号'id','id','id',''
	 */
	public static String ConvertIds(String ids){
		//如果字符串最后一位是逗号，去掉
		if(ids.lastIndexOf(",")==ids.length()-1){
			ids = ids.substring(0,ids.length()-1);
		}
		String[] tid = ids.split(",");
		for(int i=0;i<tid.length;i++){
			ids = ids.replaceAll(tid[i], "'"+tid[i]+"'");
		}
		return ids;
	}
	
	/**
	 * @param option Echart option对象
	 * @return 转换成的JSON
	 * @throws JsonProcessingException
	 */
	public static String optionToJson(Option option) throws JsonProcessingException{
		ObjectMapper objectMapper = new ObjectMapper();  
	    objectMapper.setSerializationInclusion(Include.NON_NULL); 
	    String userMapJson = objectMapper.writeValueAsString(option);
	    return userMapJson;
	}
	
	/**
	 * @param mapList
	 * @return JSON
	 * @throws JSONException 
	 * @description 将dao返回的maplist转化成json格式
	 */
	public static String mapListToJson(List<Map<String,Object>> mapList) throws JSONException{
		JSONArray array = new JSONArray(); 
		for(Map<String,Object> map:mapList){
			JSONObject jsonObj = new JSONObject(); 
			for (Map.Entry<String, Object> entry : map.entrySet()) { 
				jsonObj.put(entry.getKey(),entry.getValue());
			}
			array.put(jsonObj);
		}
		return array.toString();
	}
	
	/**
	 * @param request 请求
	 * @param paramName 参数名称
	 * @return 返回参数值
	 * @throws UnsupportedEncodingException
	 * @description 处理汉字乱码问题     未使用
	 */
	public static String getParamByRequest(HttpServletRequest request,String paramName) throws UnsupportedEncodingException{
		String result = new String(request.getParameter(paramName).getBytes("iso-8859-1"),"utf-8");
		return result;
	}

	/**
	 * 获取Byte换算后的数据，保留小数位根据decimal处理
	 * @param decimal  "0.00"保留2位小数
	 * @param unit  要转换成的单位  KB MB GB
	 * @param value 要转换的数据
	 * @return
	 */
	public static String getByteUnitNum(String decimal,String unit,Object value){
		String agg = "0.00";
		try{
			double byteSum = Double.parseDouble(value.toString());
			DecimalFormat a = new DecimalFormat(decimal);
			switch(unit){
				case "KB":
					agg = a.format(byteSum/1024);
					break;
				case "MB":
					agg = a.format(byteSum/1024/1024);
					break;
				case "GB":
					agg = a.format(byteSum/1024/1024/1024);
					break;
				default:break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return agg;
	}
}

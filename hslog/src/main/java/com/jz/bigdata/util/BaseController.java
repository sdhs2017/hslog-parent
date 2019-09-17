package com.jz.bigdata.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BaseController {
	
	@SuppressWarnings("unchecked")
	public Map<String, String> toMap(String json) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		
		try {
			map = removeMapEmptyValue(mapper.readValue(json, Map.class));
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return map;
	}
	
	public static Map<String,String> removeMapEmptyValue(Map<String,String> paramMap){
		Set<String> set = paramMap.keySet();
		Iterator<String> it = set.iterator();
		List<String> listKey = new ArrayList<String>();
		while (it.hasNext()) {
		  String str = it.next();
		  if(paramMap.get(str)==null || "".equals(paramMap.get(str))){
			  listKey.add(str) ;
		  }
		}
		for (String key : listKey) {
		    paramMap.remove(key);
		}
		return paramMap;
	}

}

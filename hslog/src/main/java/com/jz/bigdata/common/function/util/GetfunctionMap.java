package com.jz.bigdata.common.function.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.jz.bigdata.common.function.entity.Function;
import com.jz.bigdata.common.function.service.IFunctionService;
import com.jz.bigdata.common.function.service.impl.FunctionServiceImpl;

/**
 * @author shichengyu
 * @date 2018年2月8日 下午6:00:10
 * @description
 */
public class GetfunctionMap {
//	@Resource(name = "FunctionService")
//	private IFunctionService functionService;
	
	public  static Map<String,Map<String,String>> map =new HashMap<String,Map<String,String>>();

	public  void getfunctionMap(int role,IFunctionService functionService){
//		System.out.println(role);
		List<Function> list=functionService.selectAllByRole(role);
		 Map<String,String> mapFunction= new HashMap<String,String>();
//		 System.out.println(list.size());
		 for(Function function : list) {
//			  System.out.println(function.get);
			 mapFunction.put(function.getResource(), function.getDescribes());
			}
		 map.put(String.valueOf(role), mapFunction);
//		 System.err.println(map.get("1"));
		
	}

}

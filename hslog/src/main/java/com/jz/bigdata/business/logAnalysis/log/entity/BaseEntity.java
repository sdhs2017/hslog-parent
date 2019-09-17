package com.jz.bigdata.business.logAnalysis.log.entity;

import java.lang.reflect.Field;
import java.util.Arrays;

public class BaseEntity {

	
	/**
	 * 通过对class属性的操作，实现es的mapping映射
	 * @param classes
	 * @return
	 */
	public <T> String getClassMapping(T classes) {
		
	   StringBuilder fieldstring = new StringBuilder();
		
	   String [] fielddata = {"userid","deptid","equipmentid","logtime","ip","hostname","operation_facility","operation_level","process","logtime_year","logtime_month","logtime_day","logtime_hour","logtime_minute","equipmentname","devid","dname","logtype","pri","mod","act","user","from","result"};
       Field[] fields = classes.getClass().getDeclaredFields();
       for (int i = 0; i < fields.length; i++) {
            fieldstring.append("\t\t\t\t\"" + fields[i].getName().toLowerCase() + "\": {\n");
            fieldstring.append("\t\t\t\t\t\t\"type\": \""
                       + getElasticSearchMappingType(fields[i].getType().getSimpleName(),fields[i].getName()) + "\n");
            if (fields[i].getName().equals("id")) {
            	fieldstring.append("\t\t\t\t\t\t,\"index\": \""
                        + "false\"" + "\n");
			}
            if (Arrays.asList(fielddata).contains(fields[i].getName())) {
				fieldstring.append("\t\t\t\t\t\t,\"fielddata\": "
                       + "true" + "\n");
			}
            if (fields[i].getName().equals("operation_des")||fields[i].getName().equals("ip")||fields[i].getName().equals("process")||fields[i].getName().equals("hostname")||fields[i].getName().equals("equipmentname")||fields[i].getName().equals("dsp_msg")||fields[i].getName().equals("cmd")||fields[i].getName().equals("from")||fields[i].getName().equals("event")) {
	           	fieldstring.append("\t\t\t\t\t\t,\"analyzer\": \""
	           	+ "index_ansj\"" + "\n");
	           	fieldstring.append("\t\t\t\t\t\t,\"search_analyzer\": \""
	           	+ "query_ansj\"" + "\n");
			}
            if (i == fields.length-1) {
                   fieldstring.append("\t\t\t\t\t}\n");
               } else {
                   fieldstring.append("\t\t\t\t\t},\n");
               }
       }
       return fieldstring.toString();
	}

	/**
	 * 根据字段的类型和名称确定该字段的在ES中的数据结构
	 * @param varType
	 * @param name
	 * @return
	 */
	private static String getElasticSearchMappingType(String varType,String name) {
	    String es = "text";
	    switch (varType) {
	    case "Date":
	        es = "date\"\n"+"\t\t\t\t\t\t,\"format\":\"yyyy-MM-dd HH:mm:ss\"\n"+"\t\t\t\t\t\t";
	        break;
	    case "Double":
	        es = "double\"\n"+"\t\t\t\t\t\t,\"null_value\":\"NaN\"";
	        break;
	    case "Long":
	        es = "long\"";
	        break;
	    case "Integer":
	        es = "integer\"";
	        break;
	    case "Boolean":
	    	es ="boolean\"";
	    	break;
	    default:
	    	if (name.equals("id")) {
	    		es = "keyword\"";
			}else {
				es = "text\"";
			}
	        
	        break;
	    }
	    return es;
	}
}

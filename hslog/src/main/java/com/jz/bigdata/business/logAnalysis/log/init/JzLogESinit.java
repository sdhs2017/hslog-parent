package com.jz.bigdata.business.logAnalysis.log.init;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

//import com.jz.bigdata.framework.spring.es.elasticsearch.ClientTemplate;
import com.hs.elsearch.template.bak.ClientTemplate;

public class JzLogESinit {
	
//	@Autowired protected ClientTemplate clientTemplate;
	
	public static void main(String[] args){
		
	}
	
	public void deleteIndex(String index,ClientTemplate clientTemplate){
		clientTemplate.deleteByIndex(index);
	}
	
	public List<Map<String, Object>> countGroupBy(String index, String[] type,String param,ClientTemplate clientTemplate){
		
		return clientTemplate.getListGroupByQueryBuilder(index,type,param,null);
	}
	
	public <T> void init(String index, String type,T classes,ClientTemplate clientTemplate){
		boolean result = false;
		 String template = "{\n" 
                   +"\t\t\"properties\":{\n"
                           + "\t\t{#}\n" 
                   + "\t\t\t\t}"
               +"}";
		String fieldString =  getClassMapping(classes);
		template = template.replace("{#}",fieldString);
		clientTemplate.addMapping(index, type, template);
	}
	
	public <T> String getClassMapping(T classes) {
		
		StringBuilder fieldstring = new StringBuilder();
		
        Field[] fields = classes.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
             fieldstring.append("\t\t\t\t\"" + fields[i].getName().toLowerCase() + "\": {\n");
             fieldstring.append("\t\t\t\t\t\t\"type\": \""
                        + GetElasticSearchMappingType(fields[i].getType().getSimpleName(),fields[i].getName()) + "\n");
             if (fields[i].getName().equals("id")) {
            	 fieldstring.append("\t\t\t\t\t\t,\"index\": \""
                         + "false\"" + "\n");
			}
             if (!fields[i].getName().equals("operation_des")&&!fields[i].getType().getSimpleName().equals("Date")&&!fields[i].getName().equals("id")) {
				fieldstring.append("\t\t\t\t\t\t,\"fielddata\": "
                        + "true" + "\n");
			}
             if (i == fields.length-1) {
                    fieldstring.append("\t\t\t\t\t}\n");
                } else {
                    fieldstring.append("\t\t\t\t\t},\n");
                }
        }
        return fieldstring.toString();
	}

	private static String GetElasticSearchMappingType(String varType,String name) {
        String es = "text";
        switch (varType) {
        case "Date":
            es = "date\"\n"+"\t\t\t\t\t\t,\"format\":\"yyyy-MM-dd HH:mm:ss,S\"\n"+"\t\t\t\t\t\t";
            break;
        case "Double":
            es = "double\"\n"+"\t\t\t\t\t\t,\"null_value\":\"NaN\"";
            break;
        case "Long":
            es = "long\"";
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

/*package com.jz.bigdata.business.logAnalysis.log.dao.impl;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;

import com.jz.bigdata.business.logAnalysis.log.dao.IlogDao;
import com.jz.bigdata.framework.spring.es.elasticsearch.ClientTemplate;


public class LogDaoImpl implements IlogDao {

	
	
	@Autowired protected ClientTemplate clientTemplate;
	
	
	// es 排序方式
	private SortOrder sortOrder;
	
	private TransportClient client;
	*//**
	 * 连接默认集群
	 * @return 
	 *//*
	@SuppressWarnings("resource")
	public LogDaoImpl() {
		Settings settings = Settings.builder()
				// 设置es集群名称
		        .put("cluster.name", "myElasticsearchCluster").build();
		        // 设置监听
				//.put("client.transport.sniff", true)
		try {
			client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.4.2.5"), 9300));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	*//**
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 * 实现id查询
	 *//*
	public List<Map<String, Object>> selectById(String index,String type,String id) {
		// TODO Auto-generated method stub
    	
    	List<Map<String, Object>> list = clientTemplate.prepareSearch(index, type, id);
    	
		return list;
	}

	public List<Map<String, Object>> selectAll(String index,String type) {
		// TODO Auto-generated method stub
    
    	List<Map<String, Object>> list = clientTemplate.selectAll(index, type);
    	
		return list;
	}

	@Override
	public int SelectCountByDate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int SelectCountByDate(String index, String type) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	*//**
	 * @param index
	 * @return
	 *//*
	public boolean createIndex(String index) {
		// TODO Auto-generated method stub
		if(clientTemplate.indexExists(index))
			return false;
		boolean result = clientTemplate.createIndex(index);
		
		return result;
	}
	
	*//**
	 * @param index
	 * @param type
	 * @param classes
	 * 配置mapping
	 *//*
	public <T> Boolean addMapping(String index, String type,T classes) {
		// TODO Auto-generated method stub
		boolean result = false;
		 String template = "{\n" 
                     +"\t\t\"properties\":{\n"
                             + "\t\t{#}\n" 
                     + "\t\t\t\t}"
                 +"}";
		String fieldString =  getClassMapping(classes);
		template = template.replace("{#}",fieldString);
		
		result = clientTemplate.addMapping(index, type, template);
		
		return result;
		
	}

	*//**
	 * @param index
	 * @param type
	 * @return
	 * 获取index+type下的索引数据条数
	 *//*
	public long count(String index, String type) {
		// TODO Auto-generated method stub
    	long length = clientTemplate.count(index, type);
		
		return length;
	}
	
	
	*//**
	 * @param index
	 * @param type
	 * @param json
	 * 实现索引新增
	 *//*
	public void insert(String index, String type, String json) {
		// TODO Auto-generated method stub
		clientTemplate.insert(index, type, json);
	}
	
	public void delete(String index, String type, String id) {
		// TODO Auto-generated method stub
		clientTemplate.delete(index, type, id);
	}
	
	*//**
	 * @param index
	 * @param type
	 * @param param
	 * @param order
	 * @return
	 * 实现sql order by
	 *//*
	public List<Map<String, Object>> getListOrderByParam(String index, String type,String param,String order) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		
		if (param.isEmpty()) {
			return list;
		}
		if (order == null) {
			sortOrder = SortOrder.ASC;
		}else if (order.isEmpty()||order.equals("asc")) {
			sortOrder = SortOrder.ASC;
		}else if (order.equals("desc")) {
			sortOrder = SortOrder.DESC;
		}
		
    	list = clientTemplate.getListOrderByParam(index, type, param, sortOrder);
		
		return list;
	}
	
	*//**
	 * @param index
	 * @param type
	 * @param param
	 * @return
	 * 实现sql group by
	 *//*
	public List<Map<String, Object>> countGroupBy(String index, String type,String param) {
		// TODO Auto-generated method stub
    	
    	List<Map<String, Object>> list = clientTemplate.countGroupBy(index, type, param);
    	

		return list;
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
*/
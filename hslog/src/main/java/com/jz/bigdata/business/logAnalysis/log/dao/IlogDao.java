/*package com.jz.bigdata.business.logAnalysis.log.dao;

import java.util.List;
import java.util.Map;



public interface IlogDao {

	List<Map<String, Object>> selectById(String index,String type,String id);
	
	int SelectCountByDate();

	int SelectCountByDate(String index, String type);

	List<Map<String, Object>> selectAll(String index, String type);
	
	long count(String index,String type);
	
	boolean createIndex(String index);
	
	public <T> Boolean addMapping(String index, String type,T classes);
	
	void insert(String index,String type,String json);
	
	void delete(String index,String type,String id);
	
	List<Map<String, Object>> getListOrderByParam(String index, String type,String param,String order);
	
	List<Map<String, Object>> countGroupBy(String index, String type,String param);
}
*/
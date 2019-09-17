package com.jz.bigdata.business.logAnalysis.log.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeResponse;

public interface IlogService {

	public List<Map<String, Object>> index(String index,String type) ;
	
	/**
	 * 创建elasticsearch的index
	 * @param index
	 */
	public void createIndex(String index);	
	
	/**
	 * 判断index是否存在
	 * @param index
	 * @return
	 */
	public boolean indexExists(String index);
	
	/**
	 * 创建elasticsearch的index和type，并配置mapping
	 * @param index
	 * @param type
	 * @param mappingproperties
	 */
	public void createIndexAndmapping(String index, String type, String mappingproperties);
	
	/**
	 * 向elasticsearch新建索引数据
	 * @param index
	 * @param type
	 * @param json
	 */
	public void insert(String index,String type,String json);
	
	/**
	 * 获取索引数据量通过条件
	 * @param index
	 * @param types
	 * @param map
	 * @return
	 */
	public long getCount(String index,String [] types,Map<String, String> map);
	
	/**
	 * 实现类sql的group by功能
	 * @param index
	 * @param type
	 * @param param groupby的key值
	 * @param map 条件参数
	 * @return
	 */
	public List<Map<String, Object>> groupBy(String index,String[] type,String param,Map<String, String> map);
	
	/**
	 * 实现类sql的group by功能
	 * @param index
	 * @param type
	 * @param param groupby的key值
	 * @param size 设置es group by返回的数据条数，es默认是10条
	 * @param map 条件参数
	 * @return
	 */
	public List<Map<String, Object>> groupBy(String index, String[] types, String param, Map<String, String> map, int size);
	
	/**
	 * 实现类sql的group by功能
	 * @param index
	 * @param type
	 * @param []param groupby的key值
	 * @param size 设置es group by返回的数据条数，es默认是10条
	 * @param map 条件参数
	 * @return
	 */
	public List<Map<String, Object>> groupBy(String index, String[] types, String[] param, Map<String, String> map, int size);
	
	/**
	 * 分页排序
	 * @param index
	 * @param type
	 * @param param
	 * @param order
	 * @param page
	 * @param size
	 * @return
	 */
	public List<Map<String, Object>> orderBy(String index,String type,String param,String order,String page,String size) ;
	
	/**
	 * 根据设备id查询索引数据
	 * @param index
	 * @param type
	 * @param param
	 * @param order
	 * @param equipmentId
	 * @param page
	 * @param size
	 * @return
	 *//*
	public List<Map<String, Object>> getLogListByEquipmentId(String index,String type,String param,String order,String equipmentId,String page,String size) ;*/
	
	public void update();

	/**
	 * 通过ID查询索引数据
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 */
	public String searchById(String index,String type,String id);
	
	/**
	 * 
	 * @param index
	 * @param types
	 * @param content
	 * @param from
	 * @param size
	 * @return
	 */
	public List<Map<String, Object>> getListByContent(String index,String[] types,String content,String page,String size);
	
	/**
	 * 
	 * @param index
	 * @param types
	 * @param content
	 * @param userid
	 * @param from
	 * @param size
	 * @return
	 */
	public List<Map<String, Object>> getListByContent(String index,String[] types,String content,String userid,String page,String size);
	
	/**
	 * @param index
	 * @param types
	 * @param param
	 * @param equipmentid
	 * TO DO 获取资产各个时段的日志数据
	 * @return
	 */
	public List<Map<String, Object>> getListGroupByTime(String index,String[] types,String param,String equipmentid);
	
	public List<Map<String, Object>> getEventListGroupByTime(String index,String[] types,String dates,String equipmentid,String eventtype,int i);
	
	public List<Map<String, Object>> getListByMap(String index,String[] types,Map<String, String> map);

	/**
	 * getListByBlend重载
	 * 通过遍历map中的查询条件进行查询
	 * @param logindex
	 * @param array
	 * @param map
	 * @param from
	 * @param size
	 * @return
	 */
	public List<Map<String, Object>> getListByBlend(String logindex, String[] types, Map<String, String> map,String page,String size);
	
	/**
	 * getListByBlend重载userid
	 * 通过遍历map中的查询条件进行查询
	 * @param index
	 * @param types
	 * @param pamap
	 * @param userid
	 * @param from
	 * @param size
	 * @return
	 */
	public List<Map<String, Object>> getListByBlend(String index, String[] types, Map<String, String> pamap, String userid,String page,String size);

	/**
	 * 通过map查询数据
	 * @param index
	 * @param types
	 * @param starttime
	 * @param endtime
	 * @param pamap
	 * @return
	 */
	public List<Map<String, Object>> getListByMap(String index, String[] types, String starttime, String endtime,Map<String, String> map);
	
	/**
	 * 通过时间段+map
	 * @param index
	 * @param types
	 * @param starttime
	 * @param endtime
	 * @param map
	 * @param page
	 * @param size
	 * @return
	 */
	public List<Map<String, Object>> getListByMap(String index, String[] types, String starttime, String endtime,Map<String, String> map,String page,String size);
	
	/**
	 * 通过时间段+map+userid
	 * @param index
	 * @param types
	 * @param starttime
	 * @param endtime
	 * @param map
	 * @param userid
	 * @param page
	 * @param size
	 * @return
	 */
	public List<Map<String, Object>> getListByMap(String index, String[] types, String starttime, String endtime,Map<String, String> map, String userid, String page, String size);

	/**
	 * 通过ID删除索引数据
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 */
	public String deleteById(String index, String type, String id);

	public List<Map<String, Object>> getEventListGroupByEventType(String index, String[] types, String dates, String equipmentid,
			String groupby);

	List<Map<String, Object>> getListGroupByEvent(String index, String[] types, String equipmentid, String event_type,
			String starttime, String endtime);

	List<Map<String, Object>> getEventstypeCountByEquipmentid(String index, String[] types, String equipmentid, Date enddate);

	/**
	 * 构建multiField查询
	 * @param index
	 * @param types
	 * @param multifield map中的 key是查询内容，value是需要查询的字段，value为String数组
	 * @param param 其他条件参数
	 * @param page
	 * @param size
	 * @return
	 */
	List<Map<String, Object>> getListByMultiField(String index, String[] types, Map<String, String[]> multifield, Map<String, String> param, String page, String size);
	
	/**
	 * 
	 * @param indices
	 * @param type
	 * @param map 查询条件
	 * @return 删除数
	 * 
	 * 通过查询数据实现批量删除
	 */
	public long deleteByQuery(String[] indices,String type,Map<String, String> map) ;
	
	/**
	 * 
	 * @param indices
	 * @return
	 * 
	 * 实现indices合并
	 */
	public ForceMergeResponse indexForceMerge(String[] indices);
	
	/**
	 * 
	 * @param indices
	 * @return
	 * 
	 * 实现indices中对已删除segments的合并达到释放存储空间的作用
	 */
	public ForceMergeResponse indexForceMergeForDelete(String[] indices);
	
	/**
	 * 实现删除数据后强制合并 删除段释放存储空间
	 */
	public void deleteAndForcemerge(String[] indices, String type, Map<String, String> map) ;

	
}

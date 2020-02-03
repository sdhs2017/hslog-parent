package com.jz.bigdata.business.logAnalysis.log.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeResponse;

public interface IlogService {

	/**
	 * 查看index中某个type下的数据
	 * @param index 索引名
	 * @param type
	 * @return
	 */
	public List<Map<String, Object>> index(String index,String type) throws Exception;

	/**
	 * 创建elasticsearch的index
	 * @param index 索引名称
	 */
	public void createIndex(String index) throws Exception;

	/**
	 * 判断index是否存在
	 * @param index 索引名称
	 * @return 存在返回true，不存在false
	 */
	public boolean indexExists(String index) throws Exception;

	/**
	 * 创建elasticsearch的index和type，并配置mapping
	 * @param index 索引名称
	 * @param type 索引类型（7版本删除）
	 * @param mappingproperties 字段属性
	 */
	@Deprecated
	public void createIndexAndmapping(String index, String type, String mappingproperties) throws Exception;

	/**
	 * 用于elasticsearch的初始化操作，引用不同elasticsearch包在dao层区分操作
	 * 使用5的包在dao层初始化index，引用7的包在dao层初始化template
	 * @param indexOrTemplate
	 * @param type 索引类型 通过该字段的判断确认操作
	 * @param settings
	 * @param mapping
	 */
	public void initOfElasticsearch(String indexOrTemplate,String templatePattern,String type,Map<String, Object> settings,String mapping) throws Exception;

	/**
	 *
	 * 用于初始化操作创建index的模板
	 * @param tempalateName 模板名称
	 * @param tempalatePattern index名称通配
	 * @param settings 针对index模板的具体属性设置，包括分片数、副本数、最大查询值等，其中分片数设置之后是不能再修改的
	 * @param type 在5.4版本中index的template需要指明mapping对应的type，在7版本后只需要指定mapping
	 * @param mapping 字段属性
	 * @return 创建成功返回true，失败false
	 */
	public boolean createTemplateOfIndex(String tempalateName, String tempalatePattern, Map<String,Object> settings,String type, String mapping) throws Exception;

	/**
	 * 向elasticsearch新建索引数据
	 * @param index
	 * @param type
	 * @param json
	 */
	public void insert(String index,String type,String json) throws Exception;

	/**
	 * 获取索引数据量通过条件
	 * @param index
	 * @param types
	 * @param map
	 * @return
	 */
	public long getCount(String index,String [] types,Map<String, String> map);
	public long getCount(Map<String, String> map, String starttime,String endtime, String[] types, String... indices);

	/**
	 * 新
	 * 实现类sql的group by功能,包含时间范围、条件等
	 * @param index 索引名称
	 * @param types index 的 type字段，在7版本中移除
	 * @param groupByField 需要进行聚合的字段
	 * @param size 聚合结果最大返回数
	 * @param starttime 时间范围-开始时间
	 * @param endtime 时间范围-结束时间
	 * @param map 其他限制条件
	 * @return
	 */
	public List<Map<String, Object>> groupBy(String index,String[] types,String groupByField, int size, String starttime, String endtime,Map<String, String> map) throws Exception;

	/**
	 * 新
	 * 实现类sql的group by功能,对多个字段进行聚合,包含时间范围、条件等
	 * @param index 索引名称
	 * @param types index 的 type字段，在7版本中移除
	 * @param groupByField 需要进行聚合的字段,数组
	 * @param size 聚合结果最大返回数
	 * @param starttime 时间范围-开始时间
	 * @param endtime 时间范围-结束时间
	 * @param map 其他限制条件
	 * @return
	 */
	public List<Map<String, Object>> groupBy(String index,String[] types,String[] groupByField, int size, String starttime, String endtime,Map<String, String> map) throws Exception;
	/**
	 * 实现类sql的group by功能
	 * @param index
	 * @param types
	 * @param groupByField groupby的key值
	 * @param map 条件参数
	 * @return
	 */
	/*public List<Map<String, Object>> groupBy(String index,String[] types,String groupByField,Map<String, String> map);*/



	/**
	 * 分页排序
	 *//*
	public List<Map<String, Object>> orderBy(String index,String type,String param,String order,String page,String size) ;*/

	/*/**
	 * 根据设备id查询索引数据
	 * @param index
	 * @param type
	 * @param param
	 * @param order
	 * @param equipmentId
	 * @param page
	 * @param size
	 * @return
	 */
	/*public List<Map<String, Object>> getLogListByEquipmentId(String index,String type,String param,String order,String equipmentId,String page,String size) ;*/

	public void update();

	/**
	 * 通过ID查询索引数据
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 */
	public String searchById(String index,String type,String id) throws Exception;

	/**
	 * 全文检索
	 * @param indices
	 * @param types
	 * @param content
	 * @param page
	 * @param size
	 * @return
	 */
	//public List<Map<String, Object>> getListByContent(String index,String[] types,String content,String page,String size);
	public List<Map<String, Object>> getListByContent(String content,String userid,String page,String size,String[] types,String... indices) throws Exception;
	/**
	 * 全文检索
	 * @param index
	 * @param types
	 * @param content
	 * @param userid
	 * @param page
	 * @param size
	 * @return
	 */
	//public List<Map<String, Object>> getListByContent(String index,String[] types,String content,String userid,String page,String size);

	/**
	 * 新
	 * @param index 索引名
	 * @param types index 的 type字段，在7版本中移除
	 * @param param
	 * @param equipmentid 资产id
	 * TO DO 获取资产各个时段的日志数据
	 * @return
	 */
	public List<Map<String, Object>> getListGroupByTime(String index,String[] types,String param,String equipmentid) throws Exception;

	/**
	 * 新
	 * 根据时间统计单个资产的各个事件级别数量
	 * @param index 索引名
	 * @param types index 的 type字段，在7版本中移除
	 * @param dates 聚合时间
	 * @param equipmentid 资产id
	 * @param eventtype 聚合字段
	 * @param size 聚合结果数
	 * @return
	 */
	public List<Map<String, Object>> getEventListGroupByTime(String index,String[] types,String dates,String equipmentid,String eventtype,int size) throws Exception;

	/**
	 * 新
	 * 通过map构建的查询条件进行数据查询
	 * @param map 查询条件
	 * @param starttime 时间范围-开始时间
	 * @param endtime  时间范围-结束时间
	 * @param types index type字段，在7版本中移除
	 * @param indices 索引名称
	 * @return 返回符合查询条件的日志内容
	 */
	public List<Map<String, Object>> getListByMap(Map<String, String> map, String starttime, String endtime, String[] types,String... indices) throws Exception;

	/**
	 * 新
	 * 通过map构建的查询条件进行数据查询
	 * @param map 查询条件
	 * @param starttime 时间范围-开始时间
	 * @param endtime  时间范围-结束时间
	 * @param page 页码
	 * @param size 单页显示条数
	 * @param types index type字段，在7版本中移除
	 * @param indices 索引名称
	 * @return 返回符合查询条件的日志内容
	 */
	public List<Map<String, Object>> getListByMap(Map<String, String> map, String starttime, String endtime, String page, String size, String[] types,String... indices) throws Exception;


	/**
	 * 新
	 * 日志管理-精确查询业务
	 * @param map 其他条件
	 * @param starttime 时间范围-开始时间
	 * @param endtime 时间范围-结束时间
	 * @param page 页码
	 * @param size 每页展示数据条数
	 * @param types index type字段，7版本移除
	 * @param indices 索引名
	 * @return 返回详细日志内容
	 */
	public List<Map<String, Object>> getLogListByBlend(Map<String, String> map, String starttime, String endtime, String page, String size, String[] types, String... indices) throws Exception;
	/**
	 * getListByBlend重载
	 * 通过遍历map中的查询条件进行查询
	 * @param logindex
	 * @param types
	 * @param map
	 * @param page
	 * @param size
	 * @return
	 */
	//public List<Map<String, Object>> getListByBlend(String logindex, String[] types, Map<String, String> map,String page,String size);

	/**
	 * getListByBlend重载userid
	 * 通过遍历map中的查询条件进行查询
	 * @param index
	 * @param types
	 * @param pamap
	 * @param userid
	 * @param page
	 * @param size
	 * @return
	 */
	/*public List<Map<String, Object>> getListByBlend(String index, String[] types, Map<String, String> pamap, String userid,String page,String size);*/

	/**
	 * 通过map查询数据
	 * @param index
	 * @param types
	 * @param starttime
	 * @param endtime
	 * @param map
	 * @return
	 */
	/*public List<Map<String, Object>> getListByMap(String index, String[] types, String starttime, String endtime,Map<String, String> map);*/

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
	/*public List<Map<String, Object>> getListByMap(String index, String[] types, String starttime, String endtime,Map<String, String> map,String page,String size);*/

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
	/*public List<Map<String, Object>> getListByMap(String index, String[] types, String starttime, String endtime,Map<String, String> map, String userid, String page, String size);*/

	/**
	 * 通过ID删除索引数据
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 */
	public String deleteById(String index, String type, String id) throws Exception;

	/**
	 * 新
	 * 根据时间统计单个资产的事件数量
	 * @param index 索引名称
	 * @param types 索引的type字段，7版本移除
	 * @param today 时间
	 * @param equipmentid 设备id
	 * @param groupby 聚合字段
	 * @return
	 */
	public List<Map<String, Object>> getEventListGroupByEventType(String index, String[] types, String today, String equipmentid,
																  String groupby) throws Exception;

	/**
	 * 统计某个时间段内单个资产的事件类型
	 * @param index
	 * @param types
	 * @param equipmentid
	 * @param event_type
	 * @param starttime
	 * @param endtime
	 * @return
	 *//*
	List<Map<String, Object>> getListGroupByEvent(String index, String[] types, String equipmentid, String event_type,
												  String starttime, String endtime);*/

	/**
	 * 统计客户自定义的安全策略数据，实现安全告警
	 * @param index
	 * @param types
	 * @param equipmentid
	 * @param enddate
	 * @return
	 */
	List<Map<String, Object>> getEventstypeCountByEquipmentid(String index, String[] types, String equipmentid, Date enddate) throws Exception;

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
	/*List<Map<String, Object>> getListByMultiField(String index, String[] types, Map<String, String[]> multifield, Map<String, String> param, String page, String size);*/

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
	public ForceMergeResponse indexForceMergeForDelete(String[] indices) throws Exception;

	/**
	 * 实现删除数据后强制合并 删除段释放存储空间
	 */
	public void deleteAndForcemerge(String[] indices, String type, Map<String, String> map) throws Exception;

	/**
	 * 创建备份仓库
	 * @param repositoryName 备份仓库名称
	 * @param repoPath 备份仓库路径
	 * @return
	 */
	public boolean createRepositories(String repositoryName,String repoPath) throws Exception;

	/**
	 * 获取备份仓库信息
	 * @param repositoryName
	 * @return
	 */
	public List<Map<String, Object>> getRepositoriesInfo(String ... repositoryName) throws Exception;

	/**
	 * 删除备份仓库
	 * @param repositoryName
	 * @return
	 */
	public boolean deleteRepositories(String repositoryName) throws Exception;

	/**
	 * 更新index的setting属性
	 * @param index
	 * @param map
	 * @return
	 */
	public boolean updateSettings(String index,Map<String, Object> map) throws Exception;

	/**
	 * 定时任务：创建
	 * @return
	 */
	public boolean createIndexRegularly() throws Exception;

	/**
	 * 用于开启服务前的验证，保证elasticsearch5版本的index必须存在或者elasticsearch7版本的template必须存在
	 * @param indexOrTemplate 索引名称或者模板命名称(elasticsearch5版本传入index，elasticsearch7版本传入template)
	 * @return
	 */
	public boolean checkOfIndexOrTemplate(String... indexOrTemplate);

	/**
	 * 创建 index 的生命周期设置
	 * @param policy_name 生命周期名称
	 * @param delete_duration index删除周期
	 * @return
	 * @throws Exception
	 */
	public boolean createLifeCycle(String policy_name,long delete_duration) throws Exception;

	/**
	 * 开启生命周期管理
	 * @return
	 * @throws Exception
	 */
	public boolean startIndexLifeCycle() throws Exception;

	/**
	 * 查看生命周期管理状态
	 * @return
	 * @throws Exception
	 */
	public String getLifecycleManagementStatus() throws Exception;

}

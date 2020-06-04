package com.jz.bigdata.business.logAnalysis.log.service.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.hs.elsearch.dao.logDao.ILogIndexDao;
import com.hs.elsearch.dao.logDao.ILogSearchDao;
import com.jz.bigdata.business.logAnalysis.log.LogType;
import com.jz.bigdata.business.logAnalysis.log.entity.*;
import com.jz.bigdata.common.asset.service.IAssetService;
import com.jz.bigdata.roleauthority.user.service.IUserService;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeResponse;
import org.elasticsearch.client.indices.IndexTemplateMetaData;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jz.bigdata.business.logAnalysis.collector.service.ICollectorService;
import com.jz.bigdata.business.logAnalysis.log.service.IlogService;
import com.jz.bigdata.common.alarm.service.IAlarmService;
import com.jz.bigdata.common.equipment.service.IEquipmentService;
import com.jz.bigdata.common.safeStrategy.entity.SafeStrategy;
import com.jz.bigdata.common.safeStrategy.service.ISafeStrategyService;


import com.jz.bigdata.util.ConfigProperty;


@Service(value="logService")
public class LogServiceImpl implements IlogService {

	//@Autowired protected CrudTemplate clientTemplate;
    @Autowired protected ILogCrudDao logCrudDao;

    @Autowired protected ILogIndexDao logIndexDao;

	@Autowired protected ILogSearchDao logSearchDao;

	@Resource(name ="SafeStrategyService")
	private ISafeStrategyService safeStrategyService;

	@Resource(name = "EquipmentService")
	private IEquipmentService equipmentService;

	@Resource(name = "CollectorService")
	private ICollectorService collectorService;

	@Resource(name = "configProperty")
	private ConfigProperty configProperty;

	@Resource(name = "AlarmService")
	private IAlarmService alarmService;

	@Resource(name = "UserService")
	private IUserService usersService;

	@Resource(name = "AssetService")
	private IAssetService assetService;
	// es 排序方式
	private SortOrder sortOrder;

	@Override
	public List<Map<String, Object>> index(String index,String type) throws Exception {
        String [] types = {type};
		return logSearchDao.getListByMap(null,null,null, types,index);
	}

	@Override
	public void insert(String index,String type,String json) throws Exception {
		logCrudDao.insert(index, type, json);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public String deleteById(String index,String type,String id) throws Exception {
		// TODO Auto-generated method stub
		return logCrudDao.deleteById(index, type, id);
	}

	@Override
	public void createIndex(String index) throws Exception {
		// TODO Auto-generated method stub
		logIndexDao.createIndex(index);
	}

	@Override
	public List<Map<String, Object>> groupBy(String index, String[] types, String groupByField, int size, String starttime,
											 String endtime, Map<String, String> termsmap) throws Exception {

		return logSearchDao.getListByAggregation(types,starttime,endtime,groupByField,size,termsmap,index);
	}

	@Override
	public List<Map<String, Object>> groupBy(String index, String[] types, String[] groupByField, int size, String starttime,
											 String endtime, Map<String, String> map) throws Exception {

		return logSearchDao.getListByAggregation(types,starttime,endtime,groupByField,size,map,index);
	}

	/*@Override
	public List<Map<String, Object>> groupBy(String index, String[] types, String param, Map<String, String> termsmap) {

		List<Map<String, Object>> list = null;
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		Map<String, String> map = new HashMap<>();
		map.putAll(termsmap);
		if (map!=null&&!map.isEmpty()) {
			if (map.get("starttime")!=null&&map.get("endtime")!=null) {
				queryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(map.get("starttime")).lte(map.get("endtime")));
				map.remove("starttime");
				map.remove("endtime");
			}else if (map.get("starttime")!=null) {
				queryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(map.get("starttime")));
				map.remove("starttime");
			}else if (map.get("endtime")!=null) {
				queryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(map.get("endtime")));
				map.remove("endtime");
			}
			for(Map.Entry<String, String> entry : map.entrySet()){
				if (entry.getKey().equals("logdate")) {
					queryBuilder.must(QueryBuilders.rangeQuery(entry.getKey()).format("yyyy-MM-dd").gte(entry.getValue()));
				}else if (entry.getKey().equals("domain_url")||entry.getKey().equals("complete_url")) {
					// 短语匹配
					queryBuilder.must(QueryBuilders.matchPhraseQuery(entry.getKey(), entry.getValue()));
				}*//*else if (entry.getKey().equals("application_layer_protocol")) {
					queryBuilder.must(QueryBuilders.multiMatchQuery(entry.getKey(), "http"));
				}*//*else {
					queryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
				}
			}
			list = clientTemplate.getListGroupByQueryBuilder(index, types, param,queryBuilder);
		}else {
			list = clientTemplate.getListGroupByQueryBuilder(index, types, param,null);
		}

		return list;
	}*/


	/**
	 *
	 * @param index
	 * @param types
	 * @param today
	 * @param equipmentid
	 * TO DO 获取资产各个时段的日志数据
	 * @return
	 */
	public List<Map<String, Object>> getListGroupByTime(String index,String[] types,String today,String equipmentid) throws Exception {

		/*String groupby = "logtime_hour";
		List<Map<String, Object>> list = clientTemplate.getListGroupByQueryBuilder(index, types, today,groupby,equipmentid);*/

		List<Map<String, Object>> list = null;

		Map<String, String> map = new HashMap<>();
		if (equipmentid!=null&&!equipmentid.equals("")){
			map.put("equipmentid",equipmentid);
		}
		Date nowTime = new Date();
		SimpleDateFormat yyyyMMdd_format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 判断传入的时间参数是否是今天，是则进入，否则else
		if (today.equals(yyyyMMdd_format.format(nowTime))) {
			String starttime = today+" 00:00:00";
			String endtime = format.format(nowTime);
			list = logSearchDao.getListByDateHistogramAggregation(types,starttime,endtime,"logdate",map,index);
		}else {
			String starttime = today+" 00:00:00";
			String endtime = today+" 23:59:59";
			list = logSearchDao.getListByDateHistogramAggregation(types,starttime,endtime,"logdate",map,index);
		}


		return list;
	}

	/**
	 *
	 * @param index
	 * @param types
	 * @param equipmentid
	 * @param event_type
	 * @param starttime
	 * @param endtime
	 * @return
	 * service层
	 */
	/*@Override
	public List<Map<String, Object>> getListGroupByEvent(String index,String[] types,String equipmentid,String event_type,String starttime,String endtime) {

		QueryBuilder queryBuilder = null;
		BoolQueryBuilder Queryeid = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("equipmentid", equipmentid));
		BoolQueryBuilder Queryevent = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("event_type", event_type));
		BoolQueryBuilder Querydate = QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime).lte(endtime));
		queryBuilder =  QueryBuilders.boolQuery()
				.must(Queryeid)
				.must(Queryevent)
				.must(Querydate);
		List<Map<String, Object>> list = clientTemplate.getListGroupByQueryBuilder(index, types, "event_type", queryBuilder);

		return list;
	}*/
	/**
	 * @param index
	 * @param types
	 * @param equipmentid
	 * @param enddate
	 * @return List<Map<String, Object>>
	 * service层
	 */
	@Override
	public List<Map<String, Object>> getEventstypeCountByEquipmentid(String index,String[] types,String equipmentid,Date enddate) throws Exception {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String endtime = format.format(enddate);
		DecimalFormat df = new DecimalFormat("#.00");

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		List<SafeStrategy> safelist = safeStrategyService.selectByEquipmentId(equipmentid);

		Integer high_risk = 0;
		Integer moderate_risk = 0;
		Integer low_risk = 0;

		for (SafeStrategy safeStrategy : safelist) {
			String dates = safeStrategy.getTime();
			String event_type = safeStrategy.getEvent_type();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(enddate);
			calendar.add(Calendar.MINUTE, -Integer.valueOf(dates));
			Date startdate = calendar.getTime();
			String starttime = format.format(startdate);
			ConcurrentHashMap<String,String> safemap = new ConcurrentHashMap<>();
            safemap.put("fields.equipmentid",equipmentid);
            safemap.put("event.action",event_type);
            //List<Map<String, Object>> loglist = getListGroupByEvent(index, types, equipmentid,event_type,starttime,endtime);
            List<Map<String, Object>> loglist = this.groupBy(index, types,"event.action", 100,starttime, endtime,safemap);


			if (!loglist.get(0).isEmpty()) {
				float per = Float.valueOf(loglist.get(0).get(safeStrategy.getEvent_type()).toString())/safeStrategy.getNumber();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("event_type", safeStrategy.getEvent_type());
				map.put("per",df.format(per));
				map.put("starttime",starttime);
				map.put("endtime", endtime);

				if (per>0.8) {
					// 高危数据
					high_risk++;
				}else if (per>0.5&&per<=0.8) {
					// 中危数据
					moderate_risk++;
				}else if (per<=0.5) {
					// 低危数据
					low_risk++;
				}
				list.add(map);
			}else {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("event_type", safeStrategy.getEvent_type());
				map.put("per",df.format(0));
				map.put("starttime",starttime);
				map.put("endtime", endtime);
				low_risk++;
				list.add(map);
			}
		}

		equipmentService.upRiskById(equipmentid, high_risk, moderate_risk, low_risk);

		return list;
	}


	@Override
	public List<Map<String, Object>> getEventListGroupByTime(String index,String[] types,String today,String equipmentid,String eventtype,int i) throws Exception {


		Date nowTime = new Date();
		SimpleDateFormat yyyyMMdd_format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
		// 针对事件查询的必加字段
		map.put("event_type","");
		if (i>=0&&i<=3){
			map.put("event_level",i+"");
		}
		if (equipmentid!=null&&!equipmentid.equals("")){
			map.put("equipmentid",equipmentid);
		}

		//List<Map<String, Object>> list = clientTemplate.getListGroupByQueryBuilder(index, types, groupby, queryBuilder,24);

		List<Map<String, Object>> list = null;
		// 判断传入的时间参数是否是今天，是则进入，否则else
		if (today.equals(yyyyMMdd_format.format(nowTime))) {
			String starttime = today+" 00:00:00";
			String endtime = format.format(nowTime);
			list = logSearchDao.getListByDateHistogramAggregation(types,starttime,endtime,"logdate",map,index);
		}else {
			String starttime = today+" 00:00:00";
			String endtime = today+" 23:59:59";
			list = logSearchDao.getListByDateHistogramAggregation(types,starttime,endtime,"logdate",map,index);
		}

		return list;
	}

	/**
	 * 新
	 * @param index 索引名称
	 * @param types 索引的type字段，7版本移除
	 * @param today 时间
	 * @param equipmentid 设备id
	 * @param groupby 聚合字段
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getEventListGroupByEventType(String index,String[] types,String today,String equipmentid,String groupby) throws Exception {


		Date nowTime = new Date();
		SimpleDateFormat yyyyMMdd_format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
		// 针对事件查询的必加字段
		map.put("event_type","");
		if (equipmentid!=null&&!equipmentid.equals("")){
			map.put("equipmentid",equipmentid);
		}

		List<Map<String, Object>> list = null;
		// 判断传入的时间参数是否是今天，是则进入，否则else
		if (today.equals(yyyyMMdd_format.format(nowTime))) {
			String starttime = today+" 00:00:00";
			String endtime = format.format(nowTime);
			list = logSearchDao.getListByAggregation(types,starttime,endtime,groupby,10,map,index);
		}else {
			String starttime = today+" 00:00:00";
			String endtime = today+" 23:59:59";
			list = logSearchDao.getListByAggregation(types,starttime,endtime,groupby,10,map,index);
		}
		return list;
	}

	/*@Override
	public List<Map<String, Object>> orderBy(String index, String type, String param,String order,String page,String size) {
		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();

		Integer fromInt = 0;
		Integer sizeInt = 10;
		//long count = 0;

		if (page!=null&&size!=null) {
			fromInt = (Integer.parseInt(page)-1)*Integer.parseInt(size);
			sizeInt = Integer.parseInt(size);
		}

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
		Map<String,String> map = new HashMap<String,String>();
		list = clientTemplate.getListOrderByParam(index, type, param, sortOrder,map,fromInt,sizeInt);
		return list;
	}*/

	/**
	 * 通过资产获取日志
	 *//*
	@Override
	public List<Map<String, Object>> getLogListByEquipmentId(String index, String type, String param,String order,String equipmentId,String page,String size) {
		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();

		Integer fromInt = 0;
		Integer sizeInt = 10;
		long count = 0;

		if (page!=null&&size!=null) {
			fromInt = (Integer.parseInt(page)-1)*Integer.parseInt(size);
			sizeInt = Integer.parseInt(size);
		}

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
		Map<String,String> map = new HashMap<String,String>();
		map.put("equipmentid", equipmentId);

		String [] types = {type};
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		queryBuilder.must(QueryBuilders.matchQuery("equipmentid", equipmentId));
		count = clientTemplate.count(index, types, queryBuilder);

		Map<String, Object> mapcount = new HashMap<String,Object>();
		//日志总量
		mapcount.put("count", count);
		list.add(mapcount);
		list.addAll(clientTemplate.getListOrderByParam(index, type, param, sortOrder,map,fromInt,sizeInt));
		//list = clientTemplate.getListOrderByParam(index, type, param, sortOrder,map,fromInt,sizeInt);
		return list;
	}*/

	@Override
	public void createIndexAndmapping(String index, String type,String mappingproperties) throws Exception {

		// 判断mapping属性不为null时创建index
		if (mappingproperties!=null&&!mappingproperties.equals("")) {
			// settings 暂时为null
			/**
			 * TODO
			 */
			// 更新index的settings
			Map<String,Object> settings = new HashMap<>();
			settings.put("index.max_result_window", configProperty.getEs_max_result_window());
			settings.put("index.number_of_shards", configProperty.getEs_number_of_shards());
			settings.put("index.number_of_replicas",configProperty.getEs_number_of_replicas());
			logIndexDao.addMapping(index, type,settings, mappingproperties);
		}


	}

	@Override
	public void initOfElasticsearch(String indexOrTemplate, String templatePattern, String type, Map<String, Object> settings, String mapping) throws Exception {
		logIndexDao.initOfElasticsearch(indexOrTemplate,templatePattern,type,settings,mapping);
	}

	@Override
	public boolean createTemplateOfIndex(String tempalateName, String tempalatePattern, Map<String, Object> settings, String type, String mapping) throws Exception {
		return logIndexDao.createTemplateOfIndex(tempalateName,tempalatePattern,settings,type,mapping);
	}

	/**
	 * @param index
	 * @param types
	 * @param content
	 * @return
	 * service层 混合查询 列+正文内容
	 */
	/*@Override
	public List<Map<String, Object>> getListByContent(String index,String[] types,String content,String page,String size) {

		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		SearchHit[] hits = null;
		Integer fromInt = 0;
		Integer sizeInt = 10;
		long count = 0;

		if (page!=null&&size!=null) {
			fromInt = (Integer.parseInt(page)-1)*Integer.parseInt(size);
			sizeInt = Integer.parseInt(size);
		}

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date())));

		// "多个匹配"  匹配的列进行归纳,包括设备id，设备ip，日志类型，日志内容
		if(content!=null&&!content.equals("")) {
			content = content.trim();
			QueryBuilder multiMatchQueryBuilder;
			if (content.contains(" ")&&content.length()>10) {
				//String length  = (content.split(" ").length)+"";

				multiMatchQueryBuilder = QueryBuilders.matchQuery("operation_des", content).operator(Operator.AND);
				count = clientTemplate.count(index, types, multiMatchQueryBuilder);
				if (count<1) {
					multiMatchQueryBuilder  = QueryBuilders.multiMatchQuery(content, "operation_level","operation_des","ip","hostname","process","operation_facility","userid").fuzziness("AUTO");
				}
			}else {
				multiMatchQueryBuilder  = QueryBuilders.multiMatchQuery(content, "operation_level","operation_des","ip","hostname","process","operation_facility","userid");
			}
			//MultiMatchQueryBuilder multiMatchQueryBuilder  = QueryBuilders.multiMatchQuery(content, "operation_level","operation_des","ip","hostname","process","operation_facility","userid").fuzziness("AUTO");
			boolQueryBuilder.must(multiMatchQueryBuilder);

			count = clientTemplate.count(index, types, boolQueryBuilder);
			hits = clientTemplate.getHitsByQueryBuilder(index, types, boolQueryBuilder,"logdate",SortOrder.DESC,fromInt,sizeInt);
			// 在通过分词查询会后没有结果的情况下，在通过多字段模糊匹配查询，以达到查询效果的目的（该查询效率比较低）
			if (hits.length<1) {
				content = "*"+content.toLowerCase()+"*";

				QueryBuilder wildcardqueryBuilder1 = QueryBuilders.wildcardQuery("operation_des", content);
				QueryBuilder wildcardqueryBuilder2 = QueryBuilders.wildcardQuery("operation_level", content);
				QueryBuilder wildcardqueryBuilder3 = QueryBuilders.wildcardQuery("ip", content);
				QueryBuilder wildcardqueryBuilder4 = QueryBuilders.wildcardQuery("hostname", content);
				QueryBuilder wildcardqueryBuilder5 = QueryBuilders.wildcardQuery("process", content);
				QueryBuilder wildcardqueryBuilder6 = QueryBuilders.wildcardQuery("operation_facility", content);
				QueryBuilder wildcardqueryBuilder7 = QueryBuilders.wildcardQuery("userid", content);
				boolQueryBuilder.should(wildcardqueryBuilder1);
				boolQueryBuilder.should(wildcardqueryBuilder2);
				boolQueryBuilder.should(wildcardqueryBuilder3);
				boolQueryBuilder.should(wildcardqueryBuilder4);
				boolQueryBuilder.should(wildcardqueryBuilder5);
				boolQueryBuilder.should(wildcardqueryBuilder6);
				boolQueryBuilder.should(wildcardqueryBuilder7);
				count = clientTemplate.count(index, types, boolQueryBuilder);
				hits = clientTemplate.getHitsByQueryBuilder(index, types, boolQueryBuilder,"logdate",SortOrder.DESC,fromInt,sizeInt);
			}
		}else {
			count = clientTemplate.count(index, types, boolQueryBuilder);
			hits = clientTemplate.getHitsByQueryBuilder(index, types, boolQueryBuilder,"logdate",SortOrder.DESC,fromInt,sizeInt);
		}

		Map<String, Object> mapcount = new HashMap<String,Object>();
		//日志总量
		mapcount.put("count", count);
		list.add(mapcount);
		for(SearchHit hit : hits) {
			Map<String, HighlightField> highlightFields = hit.getHighlightFields();
			HighlightField operation_desField = highlightFields.get("operation_des");
			HighlightField operation_levelField = highlightFields.get("operation_level");
			Map<String, Object> map = hit.getSourceAsMap();
			map.put("index", hit.getIndex());
			map.put("type", hit.getType());
			map.put("id", hit.getId());

			if (operation_desField!=null) {
				Text[] texts = operation_desField.fragments();
				String name = "";
				for(Text text :texts){
					name += text;
				}
				map.put("operation_des",name);
			}
			if (operation_levelField!=null) {
				Text[] texts = operation_levelField.fragments();
				String name = "";
				for(Text text :texts){
					name += text;
				}
				map.put("operation_level",name);
			}

			list.add(map);
		}

		return list;
	}*/

	/**
	 * @param index
	 * @param types
	 * @param content
	 * @param userid
	 * @return
	 * service层 混合查询 列+正文内容
	 */
	/*@Override
	public List<Map<String, Object>> getListByContent(String index,String[] types,String content,String userid,String page,String size) {
		*//**
		 * 1.功能菜单点击日志检索，弹出页面
		 * 2.日志检索页面点击检索按钮
		 * 2.1有条件
		 * 2.2无条件
		 *//*


		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		SearchHit[] hits = null;
		Integer fromInt = 0;
		Integer sizeInt = 10;
		long count = 0;

		if (page!=null&&size!=null) {
			fromInt = (Integer.parseInt(page)-1)*Integer.parseInt(size);
			sizeInt = Integer.parseInt(size);
		}

		Map<String,String> map = new HashMap<>();
		if (userid!=null&&!userid.equals("")){
		    map.put("userid",userid);
        }

		QueryBuilder user = QueryBuilders.termQuery("userid", userid);
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date())));
		// "多个匹配"  匹配的列进行归纳,包括设备id，设备ip，日志类型，日志内容
		if(content!=null&&!content.equals("")) {
			MultiMatchQueryBuilder multiMatchQueryBuilder  = QueryBuilders.multiMatchQuery(content, "operation_level","operation_des","ip","hostname","process","operation_facility","userid");
			BoolQueryBuilder QueryBuilder = QueryBuilders.boolQuery();
			QueryBuilder.should(multiMatchQueryBuilder);
			QueryBuilder.must(user);
			count = clientTemplate.count(index, types, QueryBuilder);
			hits = clientTemplate.getHitsByQueryBuilder(index, types, QueryBuilder,"logdate",SortOrder.DESC,fromInt,sizeInt);
			//2.1无查询结果，在分词查询之后无结果的情况下，需要通过多字段模糊查询达到查询效果（查询效率较低）
			if (hits.length<1) {
				content = "*"+content.toLowerCase()+"*";

				QueryBuilder wildcardqueryBuilder1 = QueryBuilders.wildcardQuery("operation_des", content);
				QueryBuilder wildcardqueryBuilder2 = QueryBuilders.wildcardQuery("operation_level", content);
				QueryBuilder wildcardqueryBuilder3 = QueryBuilders.wildcardQuery("ip", content);
				QueryBuilder wildcardqueryBuilder4 = QueryBuilders.wildcardQuery("hostname", content);
				QueryBuilder wildcardqueryBuilder5 = QueryBuilders.wildcardQuery("process", content);
				QueryBuilder wildcardqueryBuilder6 = QueryBuilders.wildcardQuery("operation_facility", content);
				QueryBuilder wildcardqueryBuilder7 = QueryBuilders.wildcardQuery("userid", content);
				boolQueryBuilder.should(wildcardqueryBuilder1);
				boolQueryBuilder.should(wildcardqueryBuilder2);
				boolQueryBuilder.should(wildcardqueryBuilder3);
				boolQueryBuilder.should(wildcardqueryBuilder4);
				boolQueryBuilder.should(wildcardqueryBuilder5);
				boolQueryBuilder.should(wildcardqueryBuilder6);
				boolQueryBuilder.should(wildcardqueryBuilder7);
				boolQueryBuilder.must(user);
				count = clientTemplate.count(index, types, boolQueryBuilder);
				hits = clientTemplate.getHitsByQueryBuilder(index, types, boolQueryBuilder,"logdate",SortOrder.DESC,fromInt,sizeInt);
			}
		}else {
			//条件为空，全量提取，时间倒序排，只校验用户
			boolQueryBuilder.must(user);
			count = clientTemplate.count(index, types, boolQueryBuilder);
			hits = clientTemplate.getHitsByQueryBuilder(index, types, boolQueryBuilder,"logdate",SortOrder.DESC,fromInt,sizeInt);
		}



		Map<String, Object> mapcount = new HashMap<String,Object>();
		//日志总量
		mapcount.put("count", count);
		list.add(mapcount);

		for(SearchHit hit : hits) {
			Map<String, HighlightField> highlightFields = hit.getHighlightFields();
			HighlightField operation_desField = highlightFields.get("operation_des");
			HighlightField operation_levelField = highlightFields.get("operation_level");
			Map<String, Object> map = hit.getSourceAsMap();
			map.put("index", hit.getIndex());
			map.put("type", hit.getType());
			map.put("id", hit.getId());
			if (operation_desField!=null) {
				Text[] texts = operation_desField.fragments();
				String name = "";
				for(Text text :texts){
					name += text;
				}
				map.put("operation_des",name);
			}
			if (operation_levelField!=null) {
				Text[] texts = operation_levelField.fragments();
				String name = "";
				for(Text text :texts){
					name += text;
				}
				map.put("operation_level",name);
			}

			list.add(map);
		}

		return list;
	}*/

	@Override
	public List<Map<String, Object>> getListByContent(String content,String userid,String page,String size,String[] types,String... indices) throws Exception {

		Integer fromInt = 0;
		Integer sizeInt = 10;

		if (page!=null&&size!=null) {
			fromInt = (Integer.parseInt(page)-1)*Integer.parseInt(size);
			sizeInt = Integer.parseInt(size);
		}
		return logSearchDao.getListByContent(content,userid,fromInt,sizeInt,types,indices);
	}

	/**
	 * @param index
	 * @param types
	 * @param pamap
	 * @return
	 * service层  组合查询+关键字
	 */
	/*@Override
	public List<Map<String, Object>> getListByBlend(String index,String[] types,Map<String, String> pamap,String page,String size) {

		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		SearchHit[] hits = null;
		Integer fromInt = 0;
		Integer sizeInt = 10;
		long count = 0;

		if (page!=null&&size!=null) {
			fromInt = (Integer.parseInt(page)-1)*Integer.parseInt(size);
			sizeInt = Integer.parseInt(size);
		}


		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 针对特殊字段进行查询处理，如时间、多字段、分词字段等
		// 时间段
		if (pamap.get("starttime")!=null&&pamap.get("endtime")!=null) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(pamap.get("starttime")).lte(pamap.get("endtime")));
			pamap.remove("starttime");
			pamap.remove("endtime");
		}else if (pamap.get("starttime")!=null) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(pamap.get("starttime")).lte(format.format(new Date())));
			pamap.remove("starttime");
		}else if (pamap.get("endtime")!=null) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(pamap.get("endtime")));
			pamap.remove("endtime");
		}else {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date())));
		}
		// 判断是否是事件查询，如果是事件查询，es会判断event_type不为null
		if (pamap.get("event")!=null) {
			boolQueryBuilder.must(QueryBuilders.constantScoreQuery(QueryBuilders.existsQuery("event_type")));
			pamap.remove("event");
		}
		if (pamap.get("event_levels")!=null) {
			if (pamap.get("event_levels").equals("高危")) {
				boolQueryBuilder.must(QueryBuilders.rangeQuery("event_level").gte(0).lte(3));
			}else if (pamap.get("event_levels").equals("中危")) {
				boolQueryBuilder.must(QueryBuilders.rangeQuery("event_level").gte(4).lte(5));
			}else if (pamap.get("event_levels").equals("普通")) {
				boolQueryBuilder.must(QueryBuilders.rangeQuery("event_level").gte(6).lte(7));
			}
			pamap.remove("event_levels");
		}
		// 多字段查询，这里是为了查询该IP在源地址或目的地址中的数据
		if (pamap.get("multifield_ip")!=null) {
			String[] multified = {"ipv4_dst_addr","ipv4_src_addr"};
			boolQueryBuilder.must(QueryBuilders.multiMatchQuery(pamap.get("multifield_ip"), multified));
			pamap.remove("multifield_ip");
		}
		// operation_level 类似于in查询
		if (pamap.get("operation_level")!=null) {
			String [] operation_level = pamap.get("operation_level").split(",");
			boolQueryBuilder.must(QueryBuilders.termsQuery("operation_level", operation_level));
			pamap.remove("operation_level");
		}
		// 日志来源需要使用match
		if (pamap.get("packet_source")!=null) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("packet_source", pamap.get("packet_source")));
			pamap.remove("packet_source");
		}
		// equipmentname查询资产名称，局限性第一个字
		if (pamap.get("hostname")!=null) {
			//boolQueryBuilder.must();
			count = clientTemplate.count(index, types, QueryBuilders.queryStringQuery(pamap.get("hostname")).field("equipmentname").defaultOperator(Operator.AND));
			if (count<1) {
				count = clientTemplate.count(index, types, QueryBuilders.queryStringQuery(pamap.get("hostname")).field("equipmentname").defaultOperator(Operator.OR));
				if (count<1) {
					boolQueryBuilder.must(QueryBuilders.wildcardQuery("equipmentname", "*"+pamap.get("hostname")+"*"));
				}else {
					boolQueryBuilder.must(QueryBuilders.queryStringQuery(pamap.get("hostname")).field("equipmentname").defaultOperator(Operator.OR));
				}
			}else {
				boolQueryBuilder.must(QueryBuilders.queryStringQuery(pamap.get("hostname")).field("equipmentname").defaultOperator(Operator.AND));
			}
			pamap.remove("hostname");
		}

		if (pamap.get("domain_url")!=null) {
			boolQueryBuilder.must(QueryBuilders.termQuery("domain_url.raw", pamap.get("domain_url")));
			pamap.remove("domain_url");
		}
		if (pamap.get("complete_url")!=null) {
			boolQueryBuilder.must(QueryBuilders.termQuery("complete_url.raw", pamap.get("complete_url")));
			pamap.remove("complete_url");
		}

		for(Map.Entry<String, String> entry : pamap.entrySet()) {
			boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue().toLowerCase()));
		}

		count = clientTemplate.count(index, types, boolQueryBuilder);
		hits = clientTemplate.getHitsByQueryBuilder(index, types, boolQueryBuilder,"logdate",SortOrder.DESC,fromInt,sizeInt);

		Map<String, Object> mapcount = new HashMap<String,Object>();
		//日志总量
		mapcount.put("count", count);
		list.add(mapcount);

		for(SearchHit hit : hits) {
			Map<String, HighlightField> highlightFields = hit.getHighlightFields();
			HighlightField operation_desField = highlightFields.get("operation_des");
			HighlightField operation_levelField = highlightFields.get("operation_level");
			Map<String, Object> map = hit.getSourceAsMap();
			map.put("index", hit.getIndex());
			map.put("type", hit.getType());
			map.put("id", hit.getId());
			if (operation_desField!=null) {
				Text[] texts = operation_desField.fragments();
				String name = "";
				for(Text text :texts){
					name += text;
				}
				map.put("operation_des",name);
			}
			if (operation_levelField!=null) {
				Text[] texts = operation_levelField.fragments();
				String name = "";
				for(Text text :texts){
					name += text;
				}
				map.put("operation_level",name);
			}

			list.add(map);
		}

		return list;
	}*/

	/**
	 * @param index
	 * @param types
	 * @param pamap
	 * @param userid
	 * @return
	 * service层  组合查询+关键字
	 */
	/*@Override
	public List<Map<String, Object>> getListByBlend(String index,String[] types,Map<String, String> pamap,String userid,String page,String size) {

		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		SearchHit[] hits = null;
		Integer fromInt = 0;
		Integer sizeInt = 10;
		long count = 0;
		if (page!=null&&size!=null) {
			fromInt = (Integer.parseInt(page)-1)*Integer.parseInt(size);
			sizeInt = Integer.parseInt(size);
		}
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 时间段
		if (pamap.get("starttime")!=null&&pamap.get("endtime")!=null) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(pamap.get("starttime")).lte(pamap.get("endtime")));
			pamap.remove("starttime");
			pamap.remove("endtime");
		}else if (pamap.get("starttime")!=null) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(pamap.get("starttime")).lte(format.format(new Date())));
			pamap.remove("starttime");
		}else if (pamap.get("endtime")!=null) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(pamap.get("endtime")));
			pamap.remove("endtime");
		}else {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date())));
		}
		// 判断是否是事件查询，如果是事件查询，es会判断event_type不为null
		if (pamap.get("event")!=null) {
			boolQueryBuilder.must(QueryBuilders.constantScoreQuery(QueryBuilders.existsQuery("event_type")));
			pamap.remove("event");
		}
		if (pamap.get("event_levels")!=null) {
			if (pamap.get("event_levels").equals("高危")) {
				boolQueryBuilder.must(QueryBuilders.rangeQuery("event_level").gte(0).lte(3));
			}else if (pamap.get("event_levels").equals("中危")) {
				boolQueryBuilder.must(QueryBuilders.rangeQuery("event_level").gte(4).lte(5));
			}else if (pamap.get("event_levels").equals("普通")) {
				boolQueryBuilder.must(QueryBuilders.rangeQuery("event_level").gte(6).lte(7));
			}
			pamap.remove("event_levels");
		}
		if (pamap.get("multifield_ip")!=null) {
			String[] multified = {"ipv4_dst_addr","ipv4_src_addr"};
			boolQueryBuilder.must(QueryBuilders.multiMatchQuery(pamap.get("multifield_ip"), multified));
			pamap.remove("multifield_ip");
		}
		// operation_level 类似于in查询
		if (pamap.get("operation_level")!=null) {
			String [] operation_level = pamap.get("operation_level").split(",");
			boolQueryBuilder.must(QueryBuilders.termsQuery("operation_level", operation_level));
			pamap.remove("operation_level");
		}
		// 日志来源需要使用match
		if (pamap.get("packet_source")!=null) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("packet_source", pamap.get("packet_source")));
			pamap.remove("packet_source");
		}
		// equipmentname查询资产名称，局限性第一个字
		if (pamap.get("hostname")!=null) {
			//boolQueryBuilder.must();
			count = clientTemplate.count(index, types, QueryBuilders.queryStringQuery(pamap.get("hostname")).field("equipmentname").defaultOperator(Operator.AND));
			if (count<1) {
				count = clientTemplate.count(index, types, QueryBuilders.queryStringQuery(pamap.get("hostname")).field("equipmentname").defaultOperator(Operator.OR));
				if (count<1) {
					boolQueryBuilder.must(QueryBuilders.wildcardQuery("equipmentname", "*"+pamap.get("hostname")+"*"));
				}else {
					boolQueryBuilder.must(QueryBuilders.queryStringQuery(pamap.get("hostname")).field("equipmentname").defaultOperator(Operator.OR));
				}
			}else {
				boolQueryBuilder.must(QueryBuilders.queryStringQuery(pamap.get("hostname")).field("equipmentname").defaultOperator(Operator.AND));
			}
			pamap.remove("hostname");
		}

		if (pamap.get("domain_url")!=null) {
			boolQueryBuilder.must(QueryBuilders.termQuery("domain_url.raw", pamap.get("domain_url")));
			pamap.remove("domain_url");
		}
		if (pamap.get("complete_url")!=null) {
			boolQueryBuilder.must(QueryBuilders.termQuery("complete_url.raw", pamap.get("complete_url")));
			pamap.remove("complete_url");
		}

		for(Map.Entry<String, String> entry : pamap.entrySet()) {
			boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue().toLowerCase()));
		}

		boolQueryBuilder.must(QueryBuilders.termQuery("userid", userid));

		count = clientTemplate.count(index, types, boolQueryBuilder);
		hits = clientTemplate.getHitsByQueryBuilder(index, types, boolQueryBuilder,"logdate",SortOrder.DESC,fromInt,sizeInt);

		Map<String, Object> mapcount = new HashMap<String,Object>();
		//日志总量
		mapcount.put("count", count);
		list.add(mapcount);

		for(SearchHit hit : hits) {
			Map<String, HighlightField> highlightFields = hit.getHighlightFields();
			HighlightField operation_desField = highlightFields.get("operation_des");
			HighlightField operation_levelField = highlightFields.get("operation_level");
			Map<String, Object> map = hit.getSourceAsMap();
			map.put("index", hit.getIndex());
			map.put("type", hit.getType());
			map.put("id", hit.getId());
			if (operation_desField!=null) {
				Text[] texts = operation_desField.fragments();
				String name = "";
				for(Text text :texts){
					name += text;
				}
				map.put("operation_des",name);
			}
			if (operation_levelField!=null) {
				Text[] texts = operation_levelField.fragments();
				String name = "";
				for(Text text :texts){
					name += text;
				}
				map.put("operation_level",name);
			}

			list.add(map);
		}

		return list;
	}*/


	/**
	 * @param index
	 * @param types
	 * @param starttime
	 * @param endtime
	 * @param map
	 * @return
	 * service层  时间段+map
	 */
	/*@Override
	public List<Map<String, Object>> getListByMap(String index,String[] types,String starttime,String endtime,Map<String, String> map) {

		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		// 时间段
		if (!starttime.equals("")&&!endtime.equals("")) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime).lte(endtime));
		}else if (!starttime.equals("")) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime));
		}else if (!endtime.equals("")) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(endtime));
		}
		// 遍历map
		for(Map.Entry<String, String> entry : map.entrySet()){
			QueryBuilder queryBuilder = QueryBuilders.termQuery(entry.getKey(), entry.getValue());
			boolQueryBuilder.must(queryBuilder);
		}

		list = clientTemplate.getListByQueryBuilder(index, types, boolQueryBuilder,"logdate",SortOrder.DESC);


		return list;
	}*/

	/**
	 * @param index
	 * @param types
	 * @param content
	 * @return
	 * service层  时间段+map+分页
	 */
	/*@Override
	public List<Map<String, Object>> getListByMap(String index,String[] types,String starttime,String endtime,Map<String, String> map,String page,String size) {

		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();

		Integer fromInt = 0;
		Integer sizeInt = 10;
		long count = 0;
		if (page!=null&&size!=null) {
			fromInt = (Integer.parseInt(page)-1)*Integer.parseInt(size);
			sizeInt = Integer.parseInt(size);
		}

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		// 时间段
		if (!starttime.equals("")&&!endtime.equals("")) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime).lte(endtime));
		}else if (!starttime.equals("")) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime));
		}else if (!endtime.equals("")) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(endtime));
		}
		// 遍历map
		for(Map.Entry<String, String> entry : map.entrySet()){
			// 日志级别字段属于复选查询，需要处理为terms
			if (entry.getKey().equals("operation_level")) {
				String [] operation_level = entry.getValue().split(",");
				boolQueryBuilder.must(QueryBuilders.termsQuery("operation_level", operation_level));
			}else if(entry.getKey().equals("dns_domain_name")){
				QueryBuilder queryBuilder = QueryBuilders.matchQuery(entry.getKey(), entry.getValue());
				boolQueryBuilder.must(queryBuilder);
			}else {
				QueryBuilder queryBuilder = QueryBuilders.termQuery(entry.getKey(), entry.getValue());
				boolQueryBuilder.must(queryBuilder);
			}

		}
		//日志总量
		count = clientTemplate.count(index, types, boolQueryBuilder);
		Map<String, Object> mapcount = new HashMap<String,Object>();
		mapcount.put("count", count);

		list.add(mapcount);
		//logSearchDao.getListByMap(map,null,null,types,indices);

		list.addAll(clientTemplate.getListByQueryBuilder(index, types, boolQueryBuilder,"logdate",SortOrder.DESC,fromInt,sizeInt));

		return list;
	}*/

	/**
	 * @param index
	 * @param types
	 * @param starttime
	 * @param endtime
	 * @param map
	 * @param userid
	 * @return
	 * service层  时间段+map+分页
	 */
	/*@Override
	public List<Map<String, Object>> getListByMap(String index,String[] types,String starttime,String endtime,Map<String, String> map,String userid, String page,String size) {

		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();

		Integer fromInt = 0;
		Integer sizeInt = 10;
		long count = 0;
		if (page!=null&&size!=null) {
			fromInt = (Integer.parseInt(page)-1)*Integer.parseInt(size);
			sizeInt = Integer.parseInt(size);
		}

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		// 时间段
		if (!starttime.equals("")&&!endtime.equals("")) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime).lte(endtime));
		}else if (!starttime.equals("")) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime));
		}else if (!endtime.equals("")) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(endtime));
		}
		// 遍历map
		for(Map.Entry<String, String> entry : map.entrySet()){
			if (entry.getKey().equals("operation_level")) {
				String [] operation_level = entry.getValue().split(",");
				boolQueryBuilder.must(QueryBuilders.termsQuery("operation_level", operation_level));
			}else {
				QueryBuilder queryBuilder = QueryBuilders.termQuery(entry.getKey(), entry.getValue());
				boolQueryBuilder.must(queryBuilder);
			}

		}
		//日志总量
		count = clientTemplate.count(index, types, boolQueryBuilder);
		Map<String, Object> mapcount = new HashMap<String,Object>();
		mapcount.put("count", count);

		list.add(mapcount);
		list.addAll(clientTemplate.getListByQueryBuilder(index, types, boolQueryBuilder,"logdate",SortOrder.DESC,fromInt,sizeInt));

		return list;
	}*/

	/**
	 * service层
	 * @param map 查询条件
	 * @param starttime 时间范围-开始时间
	 * @param endtime  时间范围-结束时间
	 * @param types index type字段，在7版本中移除
	 * @param indices 索引名称
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getListByMap(Map<String, String> map, String starttime, String endtime, String[] types,String... indices) throws Exception {

		/*BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 时间段
		if (map.get("starttime")!=null&&map.get("endtime")!=null) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(map.get("starttime")).lte(map.get("endtime")));
			map.remove("starttime");
			map.remove("endtime");
		}else if (map.get("starttime")!=null) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(map.get("starttime")).lte(format.format(new Date())));
			map.remove("starttime");
		}else if (map.get("endtime")!=null) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(map.get("endtime")));
			map.remove("endtime");
		}else {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date())));
		}

		for(Map.Entry<String, String> entry : map.entrySet()){
			*//*QueryBuilder matchqueryBuilder = QueryBuilders.matchQuery(entry.getKey(), entry.getValue());
			boolQueryBuilder.must(matchqueryBuilder);*//*
			QueryBuilder wildcardqueryBuilder = QueryBuilders.wildcardQuery(entry.getKey(), "*"+entry.getValue()+"*");
			boolQueryBuilder.must(wildcardqueryBuilder);
		}

		List<Map<String, Object>> list = clientTemplate.getListByQueryBuilder(index, types, boolQueryBuilder,"logdate",SortOrder.DESC);*/

		List<Map<String, Object>> list = logSearchDao.getListByMap(map,null,null,types,indices);

		return list;
	}

	@Override
	public List<Map<String, Object>> getListByMap(Map<String, String> map, String starttime, String endtime, String page, String size, String[] types, String... indices) throws Exception {
		Integer fromInt = 0;
		Integer sizeInt = 10;
		long count = 0;
		if (page!=null&&size!=null) {
			fromInt = (Integer.parseInt(page)-1)*Integer.parseInt(size);
			sizeInt = Integer.parseInt(size);
		}

		List<Map<String, Object>> list = new ArrayList<>();
		//日志总量
		count = logSearchDao.getCount(map,starttime,endtime,types,indices);
		Map<String, Object> mapcount = new HashMap<String,Object>();
		mapcount.put("count", count);

		list.add(mapcount);

		list.addAll(logSearchDao.getListByMap(map,starttime,endtime,fromInt,sizeInt,types,indices));
		return list;
	}

	@Override
	public List<Map<String, Object>> getLogListByBlend(Map<String, String> map, String starttime, String endtime, String page, String size, String[] types, String... indices) throws Exception {

		Integer fromInt = 0;
		Integer sizeInt = 10;
		long count = 0;
		if (page!=null&&size!=null) {
			fromInt = (Integer.parseInt(page)-1)*Integer.parseInt(size);
			sizeInt = Integer.parseInt(size);
		}

		List<Map<String, Object>> list = new ArrayList<>();
		//日志总量
		count = logSearchDao.getCount(map,starttime,endtime,types,indices);
		Map<String, Object> mapcount = new HashMap<String,Object>();
		mapcount.put("count", count);

		list.add(mapcount);

		list.addAll(logSearchDao.getLogListByMap(map,starttime,endtime,fromInt,sizeInt,types,indices));
		return list;
	}

	/**
	 * @param index
	 * @param types
	 * @param multifieldparam
	 * @return
	 * service层  单个查询条件多匹配查询
	 */
	/*@Override
	public List<Map<String, Object>> getListByMultiField(String index,String[] types,Map<String, String[]> multifieldparam,Map<String, String> param, String page,String size) {

		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		SearchHit[] hits = null;
		Integer fromInt = 0;
		Integer sizeInt = 10;
		long count = 0;

		if (page!=null&&size!=null) {
			fromInt = (Integer.parseInt(page)-1)*Integer.parseInt(size);
			sizeInt = Integer.parseInt(size);
		}

		// "多个匹配"  匹配的列进行归纳,包括设备id，设备ip，日志类型，日志内容
		if(!param.isEmpty()) {
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			for (Entry<String, String[]> tmp : multifieldparam.entrySet()) {
				boolQueryBuilder.must(QueryBuilders.multiMatchQuery(tmp.getKey(), tmp.getValue()));
			}
			count = clientTemplate.count(index, types, boolQueryBuilder);
			hits = clientTemplate.getHitsByQueryBuilder(index, types, boolQueryBuilder,"logdate",SortOrder.DESC,fromInt,sizeInt);
		}

		Map<String, Object> mapcount = new HashMap<String,Object>();
		//日志总量
		mapcount.put("count", count);
		list.add(mapcount);
		for(SearchHit hit : hits) {
			Map<String, HighlightField> highlightFields = hit.getHighlightFields();
			HighlightField operation_desField = highlightFields.get("operation_des");
			HighlightField operation_levelField = highlightFields.get("operation_level");
			Map<String, Object> map = hit.getSourceAsMap();
			map.put("index", hit.getIndex());
			map.put("type", hit.getType());
			map.put("id", hit.getId());

			if (operation_desField!=null) {
				Text[] texts = operation_desField.fragments();
				String name = "";
				for(Text text :texts){
					name += text;
				}
				map.put("operation_des",name);
			}
			if (operation_levelField!=null) {
				Text[] texts = operation_levelField.fragments();
				String name = "";
				for(Text text :texts){
					name += text;
				}
				map.put("operation_level",name);
			}

			list.add(map);
		}

		return list;
	}*/

	/**
	 * service层
	 */
	/*public List<Map<String, Object>> getListByMapAndContent(String index,String[] types,Map<String, String> map,String content) {

		String [] keys = {"equipmentid","operation_level","operation_des"};
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		if (!Strings.isNullOrEmpty(content)) {
			for(String key : keys){
				if (!map.containsKey(key)) {
					map.put(key, content);
				}
			}
		}
		for(Map.Entry<String, String> entry : map.entrySet()){
			*//*QueryBuilder matchqueryBuilder = QueryBuilders.matchQuery(entry.getKey(), entry.getValue());
			boolQueryBuilder.must(matchqueryBuilder);*//*
			QueryBuilder wildcardqueryBuilder = QueryBuilders.wildcardQuery(entry.getKey(), "*"+entry.getValue()+"*");
			boolQueryBuilder.should(wildcardqueryBuilder);
		}

		List<Map<String, Object>> list = clientTemplate.getListByQueryBuilder(index, types, boolQueryBuilder,"logdate",SortOrder.DESC);

		return list;
	}
*/
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

	@Override
	public String searchById(String index, String type, String id) throws Exception {

		return logCrudDao.searchById(index,type,id).toString();
	}

	@Override
	public long getCount(String index,String [] types,Map<String, String> map) {

		/*long result = 0;
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		if (map!=null&&!map.isEmpty()) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// 时间段
			if (map.get("starttime")!=null&&map.get("endtime")!=null) {
				queryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(map.get("starttime")).lte(map.get("endtime")));
				map.remove("starttime");
				map.remove("endtime");
			}else if (map.get("starttime")!=null) {
				queryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(map.get("starttime")).lte(format.format(new Date())));
				map.remove("starttime");
			}else if (map.get("endtime")!=null) {
				queryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(map.get("endtime")));
				map.remove("endtime");
			}else {
				queryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date())));
			}
			for(Map.Entry<String, String> entry : map.entrySet()){
				if (entry.getKey().equals("event")) {
					// 字段不为null查询
					queryBuilder.must(QueryBuilders.constantScoreQuery(QueryBuilders.existsQuery("event_type")));
				}else if(entry.getKey().equals("event_level")){
					// 范围查询
					queryBuilder.must(QueryBuilders.rangeQuery("event_level").gte(0).lte(3));
				}else if (entry.getKey().equals("domain_url")||entry.getKey().equals("complete_url")) {
					// 短语匹配
					queryBuilder.must(QueryBuilders.matchPhraseQuery(entry.getKey(), entry.getValue()));
				}else {
					// 不分词精确查询
					queryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
				}
			}
			try {
				result = clientTemplate.count(index, types,queryBuilder);
			} catch (Exception e) {
				result = 0;
			}
		}else {
			result = clientTemplate.count(index, types,null);
		}*/

		return logSearchDao.getCount(map,null,null, types,index);
	}

	@Override
	public long getCount(Map<String, String> map, String starttime, String endtime, String[] types, String... indices) {


		return logSearchDao.getCount(map,starttime,endtime, types,indices);
	}

	@Override
	public long deleteByQuery(String[] indices, String type, Map<String, String> map) {

		long result = 0;

		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

		if (map!=null) {
			if (map.get("starttime")!=null&&map.get("endtime")!=null) {
				queryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(map.get("starttime")).lte(map.get("endtime")));
				map.remove("starttime");
				map.remove("endtime");
			}else if (map.get("starttime")!=null) {
				queryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(map.get("starttime")));
				map.remove("starttime");
			}else if (map.get("endtime")!=null) {
				queryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(map.get("endtime")));
				map.remove("endtime");
			}
			for(Map.Entry<String, String> entry : map.entrySet()){
				if (entry.getKey().equals("event")) {
					// 字段不为null查询
					queryBuilder.must(QueryBuilders.constantScoreQuery(QueryBuilders.existsQuery("event_type")));
				}else if(entry.getKey().equals("event_level")){
					// 范围查询
					queryBuilder.must(QueryBuilders.rangeQuery("event_level").gte(0).lte(3));
				}else if (entry.getKey().equals("domain_url")||entry.getKey().equals("complete_url")) {
					// 短语匹配
					queryBuilder.must(QueryBuilders.matchPhraseQuery(entry.getKey(), entry.getValue()));
				}else {
					// 不分词精确查询
					queryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
				}
			}
			try {
				result = logCrudDao.countDeleteByQuery(queryBuilder,indices);
			} catch (Exception e) {
				result = 0;
			}
		}

		return result;
	}

	@Override
	public ForceMergeResponse indexForceMerge(String[] indices) {
		// TODO Auto-generated method stub

		// 针对以后架构实现的多indices可能会对indices进行合并操作
		// 暂时不实现这个方法，该合并需要考虑单个索引的大小，默认的合并段设置是1

		return null;
	}

	@Override
	public ForceMergeResponse indexForceMergeForDelete(String[] indices) throws Exception {

		// 该强制合并操作不对index的segments做操作
		int maxNumSegments = -1;
		// 该合并操作针对删除数据后希望释放存储空间
		boolean onlyExpungeDeletes = true;

		return logIndexDao.indexForceMerge(indices, maxNumSegments, onlyExpungeDeletes);

	}

	@Override
	public void deleteAndForcemerge(String[] indices, String type, Map<String, String> map) throws Exception {

		if (type!=null&&!type.equals("")) {
			map.put("_type", type);
		}

		map.put("starttime", "");
		map.put("endtime", "");

		try {
			// 无论采集器是否开启都执行一次关闭操作
			boolean stopresult = collectorService.stopKafkaCollector();
			if (stopresult) {
				System.out.println("数据采集器关闭成功");
			} else {
				System.out.println("数据采集器关闭失败，已关闭");
			}

			long delete_count = deleteByQuery(indices,type,map);

			System.out.println("删除数据条数："+delete_count);
			indexForceMergeForDelete(indices);

			boolean startresult = collectorService.startKafkaCollector(equipmentService, assetService, logCrudDao, configProperty,
					alarmService, usersService);

			if (startresult) {
				System.out.println("数据采集器开启成功");
			} else {
				System.out.println("数据采集器开启失败，请勿重复开启");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean indexExists(String index) throws Exception {

		return logIndexDao.indexExists(index);
	}

	@Override
	public boolean createRepositories(String repositoryName, String repoPath) throws Exception {

		return logIndexDao.createRepositories(repositoryName, repoPath);
	}

	@Override
	public List<Map<String, Object>> getRepositoriesInfo(String... repositoryName) throws Exception {

		return logIndexDao.getRepositoriesInfo(repositoryName);
	}

	@Override
	public boolean deleteRepositories(String repositoryName) throws Exception {

		return logIndexDao.deleteRepositories(repositoryName);
	}

	@Override
	public boolean updateSettings(String index, Map<String, Object> map) throws Exception {

		// 判断index是否存在，不存在则不需要更新
		if (indexExists(index)){
			return logIndexDao.updateSettings(index, map);
		}else {
			return true;
		}

	}

	@Override
	public boolean createIndexRegularly() throws Exception {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DAY_OF_MONTH, 1);
		String index = configProperty.getEs_index().replace("*",format.format(c.getTime()));
		//System.out.println(index);

		// 更新index的settings
		Map<String,Object> settings = new HashMap<>();
		settings.put("index.max_result_window", configProperty.getEs_max_result_window());
		settings.put("index.number_of_shards", configProperty.getEs_number_of_shards());
		settings.put("index.number_of_replicas",configProperty.getEs_number_of_replicas());

		if(logIndexDao.indexExists(index)){
			System.out.println(index+"  已存在！");
			return true;
		}else {

			try {
				logIndexDao.addMapping(index, LogType.LOGTYPE_SYSLOG, settings, new Syslog().toMapping());
                logIndexDao.addMapping(index,LogType.LOGTYPE_WINLOG, settings, new Winlog().toMapping());
                logIndexDao.addMapping(index,LogType.LOGTYPE_LOG4J, settings, new Log4j().toMapping());
                logIndexDao.addMapping(index,LogType.LOGTYPE_MYSQLLOG, settings, new Mysql().toMapping());
                logIndexDao.addMapping(index,LogType.LOGTYPE_PACKETFILTERINGFIREWALL_LOG, settings, new PacketFilteringFirewal().toMapping());
                logIndexDao.addMapping(index,LogType.LOGTYPE_NETFLOW, settings, new Netflow().toMapping());
                logIndexDao.addMapping(index,LogType.LOGTYPE_DNS, settings, new DNS().toMapping());
                logIndexDao.addMapping(index,LogType.LOGTYPE_DHCP, settings, new DHCP().toMapping());
                logIndexDao.addMapping(index,LogType.LOGTYPE_APP_FILE, settings, new App_file().toMapping());
                logIndexDao.addMapping(index,LogType.LOGTYPE_APP_APACHE, settings, new App_file().toMapping());
                logIndexDao.addMapping(index,LogType.LOGTYPE_UNKNOWN, settings, new Unknown().toMapping());

                logIndexDao.addMapping(index,LogType.LOGTYPE_DEFAULTPACKET, settings, new DefaultPacket().toMapping());
			}catch (Exception e){
				e.printStackTrace();
				System.out.println("创建index失败！！！");
				return false;
			}

		}

		return true;
	}

	@Override
	public boolean checkOfIndexOrTemplate(String... indexOrTemplate){
		boolean result = false;

		//indexOrTemplate = indexOrTemplate.replace("*","");
		try {
			result = logIndexDao.checkOfIndexOrTemplate(indexOrTemplate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public boolean createLifeCycle(String policy_name, long delete_duration) throws Exception {
		return logIndexDao.createLifeCycle(policy_name, delete_duration);
	}

	@Override
	public boolean startIndexLifeCycle() throws Exception {
		return logIndexDao.startIndexLifeCycle();
	}

	@Override
	public String getLifecycleManagementStatus() throws Exception {
		return logIndexDao.getLifecycleManagementStatus();
	}

	@Override
	public List<IndexTemplateMetaData> getTemplate(String... templatename) throws Exception {
		return logIndexDao.getTemplateData(templatename);
	}


}

/*
package com.jz.bigdata.business.logAnalysis.zts.service.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import com.jz.bigdata.business.logAnalysis.log.service.impl.LogServiceImpl;
import com.jz.bigdata.business.logAnalysis.zts.service.IztslogService;

@Service("ZtsLogServiceImpl")
public class ZtsLogServiceImpl extends LogServiceImpl implements IztslogService{

	*/
/**
	 * @param index
	 * @param types
	 * @param map
	 * @return
	 * service层  精确查询  管理员
	 *//*

	@Override
	public List<Map<String, Object>> getListByBlend(String index,String[] types,Map<String, String> pamap,String page,String size) {
		
		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		SearchHit[] hits = null;
		Integer fromInt = 0;
		Integer sizeInt = 12;
		long count = 0;

		if (page!=null&&size!=null) {
			fromInt = (Integer.parseInt(page)-1)*Integer.parseInt(size);
			sizeInt = Integer.parseInt(size);
		}
		
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		// 时间段
		if (pamap.get("starttime")!=null&&pamap.get("endtime")!=null) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(pamap.get("starttime")).lte(pamap.get("endtime")));
		}else if (pamap.get("starttime")!=null) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(pamap.get("starttime")));
		}else if (pamap.get("endtime")!=null) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(pamap.get("endtime")));
		}
		if (pamap.get("event")!=null) {
			boolQueryBuilder.must(QueryBuilders.constantScoreQuery(QueryBuilders.existsQuery("event_type")));
		}
		if (pamap.get("event_levels")!=null) {
			if (pamap.get("event_levels").equals("高危")) {
				boolQueryBuilder.must(QueryBuilders.rangeQuery("event_level").gte(0).lte(3));
			}else if (pamap.get("event_levels").equals("中危")) {
				boolQueryBuilder.must(QueryBuilders.rangeQuery("event_level").gte(4).lte(5));
			}else if (pamap.get("event_levels").equals("普通")) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("event_level").gte(6).lte(7));
			}
			
		}
				
		// IP
		if (pamap.get("ip")!=null) {
			boolQueryBuilder.must(QueryBuilders.termQuery("ip", pamap.get("ip")));
		}
		// equipmentid
		if (pamap.get("equipmentid")!=null) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("equipmentid", pamap.get("equipmentid")));
		}
		// event_level
		if (pamap.get("event_level")!=null) {
			boolQueryBuilder.must(QueryBuilders.termQuery("event_level", pamap.get("event_level")));
		}
		// operation_level
		if (pamap.get("operation_level")!=null) {
			boolQueryBuilder.must(QueryBuilders.termQuery("operation_level", pamap.get("operation_level")));
		}
		// event_type
		if (pamap.get("event_type")!=null) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("event_type", pamap.get("event_type")));
		}
		if (pamap.get("user")!=null) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("user", pamap.get("user")));
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
	}
	
	*/
/**
	 * @param index
	 * @param types
	 * @param map
	 * @param userid
	 * @return
	 * service层  组合查询 非管理员
	 *//*

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
		// 时间段
		if (pamap.get("starttime")!=null&&pamap.get("endtime")!=null) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(pamap.get("starttime")).lte(pamap.get("endtime")));
		}else if (pamap.get("starttime")!=null) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(pamap.get("starttime")));
		}else if (pamap.get("endtime")!=null) {
			boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(pamap.get("endtime")));
		}
		if (pamap.get("event")!=null) {
			boolQueryBuilder.must(QueryBuilders.constantScoreQuery(QueryBuilders.existsQuery("event_type")));
		}
		// IP
		if (pamap.get("ip")!=null) {
			boolQueryBuilder.must(QueryBuilders.termQuery("ip", pamap.get("ip")));
		}

		// event_level
		if (pamap.get("event_level")!=null) {
			boolQueryBuilder.must(QueryBuilders.termQuery("event_level", pamap.get("event_level")));
		}
		// operation_level
		if (pamap.get("operation_level")!=null) {
			boolQueryBuilder.must(QueryBuilders.termQuery("operation_level", pamap.get("operation_level")));
		}
		// equipmentname
		if (pamap.get("hostname")!=null) {
			boolQueryBuilder.should(QueryBuilders.matchQuery("equipmentname", pamap.get("hostname")));
		}
		// event_type
		if (pamap.get("event_type")!=null) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("event_type", pamap.get("event_type")));
		}
		// user
		if (pamap.get("user")!=null) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("user", pamap.get("user")));
		}
		boolQueryBuilder.must(QueryBuilders.matchQuery("userid", userid));
		
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
	}*//*

}
*/

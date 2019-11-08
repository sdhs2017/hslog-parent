/*
package com.hs.elsearch.template.bak;

import org.apache.log4j.Logger;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.repositories.delete.DeleteRepositoryRequest;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesRequest;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesResponse;
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequest;
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryResponse;
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.flush.FlushResponse;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeRequest;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.bulk.byscroll.BulkByScrollResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.cluster.metadata.RepositoryMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.repositories.fs.FsRepository;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import net.sf.json.util.JSONBuilder;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class ClientTemplate implements IndexSearchEngine<SearchHit>, NodeOperations {

	private static Logger logger = Logger.getLogger(ClientTemplate.class);

	private Client client;

	private final String indexName;

	public ClientTemplate(final Client client) {
		//Assert.notNull(client, "客户端不允许为空");
		logger.info("module elasticsearch5 template初始化··· ···");
		this.client = client;
		this.indexName = "";
	}

	public ClientTemplate(final Client client, final String indexName) {
		Assert.notNull(client, "客户端不允许为空");
		Assert.notNull(indexName, "节点名不允许为空");
		logger.info("module elasticsearch5 template初始化··· ···"+indexName);
		this.client = client;
		this.indexName = indexName;
	}

	protected void createIndex(final String indexType, final Settings settings, final XContentBuilder mapping){
		createIndex(indexType, indexType, settings, mapping);
	}

	protected void createIndex(final String indexName,final String indexType, final Settings settings, final XContentBuilder mapping){
		executeGet(new NodeCallback<CreateIndexResponse>() {
			@Override
			public ActionFuture<CreateIndexResponse> execute(
					IndicesAdminClient client) {
				CreateIndexRequest request = Requests.createIndexRequest(indexName);

				if(settings != null )
					request.settings(settings);

				if(indexType != null && mapping != null)
					request.mapping(indexType, mapping);

				return client.create(request);
			}
		});
	}

	@Override
	public <Q> List<SearchHit> search(String field, Q queryString,
									  int maxResults) {
		final QueryBuilder qb =
				QueryBuilders.queryStringQuery(String.valueOf(queryString)).field(field);

		return searchInternal(qb, maxResults);
	}

	@Override
	public List<SearchHit> search(String queryString, int maxResults) {
		final QueryStringQueryBuilder query =
				QueryBuilders.queryStringQuery(queryString);
		return searchInternal(query, maxResults);
	}

	@Override
	public boolean indexExists() {
		return indexExists(indexName);
	}

	@Override
	public boolean indexExists(String indexName) {
		executeGet(new ClusterCallback<ClusterHealthResponse>() {
			@Override
			public ActionFuture<ClusterHealthResponse> execute(
					final ClusterAdminClient admin) {
				return admin.health(Requests.clusterHealthRequest().waitForStatus(ClusterHealthStatus.YELLOW));
			}
		});

		final IndicesStatsResponse response = executeGet(new NodeCallback<IndicesStatsResponse>(){
			@Override
			public ActionFuture<IndicesStatsResponse> execute(
					final IndicesAdminClient admin) {
				return admin.stats(new IndicesStatsRequest());
			}
		});

		return response.getIndices().get(indexName) != null;
	}

	@Override
	public void deleteIndex() {
		deleteIndex(indexName);
	}

	@Override
	public void deleteIndex(final String indexName) {
		executeGet(new NodeCallback<DeleteIndexResponse>() {

			@Override
			public ActionFuture<DeleteIndexResponse> execute(
					final IndicesAdminClient admin) {
				return admin.delete(Requests.deleteIndexRequest(indexName));
			}
		});
	}

	@Override
	public void refreshIndex() {
		refreshIndex(indexName);
	}

	@Override
	public void refreshIndex(final String indexName) {
		executeGet(new NodeCallback<RefreshResponse>() {

			@Override
			public ActionFuture<RefreshResponse> execute(
					IndicesAdminClient admin) {
				return admin.refresh(Requests.refreshRequest(new String[]{ indexName }));
			}
		});
	}

	@Override
	public void closeIndex() {
		closeIndex(indexName);
	}

	@Override
	public void closeIndex(final String indexName) {
		executeGet(new NodeCallback<CloseIndexResponse>() {
			@Override
			public ActionFuture<CloseIndexResponse> execute(
					IndicesAdminClient admin) {
				return admin.close(Requests.closeIndexRequest(indexName));
			}
		});
	}

	@Override
	public void flushIndex() {
		flushIndex(indexName);
	}

	@Override
	public void flushIndex(final String indexName) {
		executeGet(new NodeCallback<FlushResponse>() {
			@Override
			public ActionFuture<FlushResponse> execute(IndicesAdminClient admin) {
				return admin.flush(Requests.flushRequest(indexName));
			}
		});
	}

	@Override
	@Deprecated
	public void snapshotIndex() {
		snapshotIndex(indexName);
	}

	@Override
	@Deprecated
	public void snapshotIndex(String indexName) {

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ActionResponse> T executeGet(NodeCallback<T> callback) {
		IndicesAdminClient indicesAdmin = this.client.admin().indices();
		ActionFuture<T> action = callback.execute(indicesAdmin);
		ActionResponse response = action.actionGet();
		return (T) response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ActionResponse> T executeGet(ClusterCallback<T> callback) {
		ClusterAdminClient clusterAdmin = this.client.admin().cluster();
		ActionFuture<T> action = callback.execute(clusterAdmin);
		ActionResponse response = action.actionGet();

		return (T) response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ActionResponse> T executeGet(ClientCallback<T> callback) {
		ActionFuture<T> action = callback.execute(this.client);
		ActionResponse response = action.actionGet();

		return (T) response;
	}

	private List<SearchHit> searchInternal(final QueryBuilder query, final int maxResults){
		final SearchResponse response = executeGet(new ClientCallback<SearchResponse>() {
			@Override
			public ActionFuture<SearchResponse> execute(final Client client) {
				final SearchRequest request = Requests.searchRequest().searchType(SearchType.DFS_QUERY_THEN_FETCH);
				final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
				sourceBuilder.query(query).size(maxResults);
				request.source(sourceBuilder);
				return client.search(request);
			}
		});

		return Arrays.asList(response.getHits().getHits());
	}

	@Override
	public String getIndexName() {
		return indexName;
	}













	*/
/**
	 * @param index
	 * @return
	 *//*

	public boolean createIndex(String index) {
		// TODO Auto-generated method stub
		boolean result = false;

		CreateIndexResponse indexResponse = this.client.admin()
				.indices()
				.prepareCreate(index)
				.get();

		result = indexResponse.isAcknowledged();
		return result;
	}

	*/
/**
	 * @param index
	 * @param type
	 * @param json
	 * 实现索引新增通过json
	 *//*

	public void insert(String index, String type, String json) {
		// TODO Auto-generated method stub
		IndexRequestBuilder response = this.client.prepareIndex(index, type);
		response.setSource(json);
		response.execute().actionGet();
	}

	*/
/**
	 * @param index
	 * @param type
	 * @param json
	 * 实现索引新增通过json
	 *//*

	public IndexRequest insertNo(String index, String type, String json) {
		// TODO Auto-generated method stub
		IndexRequestBuilder response = this.client.prepareIndex(index, type).setSource(json);
		IndexRequest request = response.request();
		return request;
	}

	*/
/**
	 *
	 * @param index
	 * @param type
	 * @param id
	 * @param map
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws IOException
	 * 更新索引
	 *//*

	public void update(String index,String type,String id,Map<String, String> map) throws InterruptedException, ExecutionException, IOException {

		XContentBuilder builder = XContentFactory.jsonBuilder();
		builder.startObject();
		for (String key : map.keySet()) {
			builder.field(key, map.get(key));
		}
		builder.endObject();


		UpdateRequest updateRequest = new UpdateRequest(index, type, id)
				.doc(builder);
		UpdateResponse updateResponse = client.update(updateRequest).get();
		GetResult result = updateResponse.getGetResult();
	}
	*/
/**
	 *
	 * @param index
	 * @param type
	 * @param id
	 * @param map
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws IOException
	 * 更新索引
	 *//*

	public void merge(String index,String type,String id,Map<String, String> map) throws InterruptedException, ExecutionException, IOException {

		XContentBuilder builder = XContentFactory.jsonBuilder();
		builder.startObject();
		for (String key : map.keySet()) {
			//QueryBuilders.constantScoreQuery(QueryBuilders.existsQuery(key));
			//queryBuilder.mustNot(QueryBuilders.regexpQuery("pcSkuId", "[0-9]+"));
			builder.field(key, map.get(key));
		}
		builder.endObject();


		UpdateRequest updateRequest = new UpdateRequest(index, type, id)
				.doc(builder);
		UpdateResponse updateResponse = client.update(updateRequest).get();
		GetResult result = updateResponse.getGetResult();
	}

	*/
/**
	 * @param index
	 * @param type
	 * @param id
	 * 删除索引通过id
	 *//*

	public String delete(String index, String type, String id) {
		// TODO Auto-generated method stub
		DeleteResponse response = this.client.prepareDelete(index, type, id)
				.get();
		Result result=response.getResult();
		return result.toString();
	}

	*/
/**
	 *
	 * @param index
	 * @return
	 * 删除索引
	 *//*

	public boolean deleteByIndex(String index) {
		boolean result = false;
		DeleteIndexResponse dResponse = client.admin().indices().prepareDelete(index)
				.execute().actionGet();
		result = dResponse.isAcknowledged();

		return result;
	}

	*/
/**
	 * @param index
	 * @param types
	 * @return
	 * 获取index+type下的索引数据条数
	 *//*

	public long count(String index, String [] types,QueryBuilder queryBuilder) {
		SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
		if (types!=null&&types.length>0) {
			searchRequestBuilder.setTypes(types);
		}
		if (queryBuilder!=null) {
			searchRequestBuilder.setQuery(queryBuilder);
		}
		SearchResponse response = searchRequestBuilder.get();
		long length = response.getHits().getTotalHits();
		return length;
	}

	*/
/**
	 *
	 * @param queryBuilder
	 * @param types
	 * @param indices
	 * @return
	 * 获取indices+types+query条件的日志数
	 *//*

	public long getCountByQuery(QueryBuilder queryBuilder, String [] types,String... indices) {

		SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indices);

		if (types!=null&&types.length>0) {
			searchRequestBuilder.setTypes(types);
		}
		if (queryBuilder!=null) {
			searchRequestBuilder.setQuery(queryBuilder);
		}
		SearchResponse response = searchRequestBuilder.get();
		long length = response.getHits().getTotalHits();
		return length;
	}

	*/
/**
	 * @param indices
	 * @param type
	 * @param id
	 * @return
	 *//*

	public List<Map<String, Object>>  prepareSearch(final String indices, final String type, final String id) {
		SearchResponse searchResponse = null;
		searchResponse = this.client.prepareSearch(indices).setTypes(type).get();
		SearchHits hits = searchResponse.getHits();

		SearchHit [] searchHits = hits.getHits();

		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		if (searchHits.length>0) {
			for(SearchHit hit : searchHits) {
				Map<String, Object> map = hit.getSource();
				map.put("index", hit.getIndex());
				map.put("type", hit.getType());
				map.put("id", hit.getId());

				list.add(map);
			}
		}
		return list;
	}

	public String searchById(final String indices, final String type, final String id){
//		IdsQueryBuilder iqb = new IdsQueryBuilder();
//		SearchResponse searchResponse = null;
//    	searchResponse = this.client.

		GetResponse getResponse = this.client
				.prepareGet()   // 准备进行get操作，此时还有真正地执行get操作。（与直接get的区别）
				.setIndex(indices)  // 要查询的
				.setType(type)
				.setId(id)
				.get();
//		Map<String,Object> map = getResponse.getSource();
//		map.put("index", getResponse.getIndex());
//		map.put("type", getResponse.getType());
//		map.put("id", getResponse.getId());
		return getResponse.getSourceAsString();
	}

	*/
/**
	 * @param index
	 * @param type
	 * @param param
	 * @param order
	 * @return
	 * 实现sql order by
	 *//*

	public List<Map<String, Object>> getListOrderByParam(String index, String type,String param,SortOrder order,Map<String,String> conditions,Integer from,Integer size) {
		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();

		SearchRequestBuilder sb = this.client.prepareSearch(index);
		if (type!=null&&!type.equals("")) {
			sb.setTypes(type);
		}

		Set<String>   keys = conditions.keySet();
		for(String key :keys){
			System.out.println(key+" "+conditions.get(key));
			sb.setQuery(QueryBuilders.matchQuery(key, conditions.get(key)));
		}
		SearchResponse searchResponse = sb.addSort(param, order).setFrom(from).setSize(size).get();

		SearchHits hits = searchResponse.getHits();
		SearchHit [] searchHits = hits.getHits();

		if (searchHits.length>0) {
			for(SearchHit hit : searchHits) {
				Map<String, Object> map = hit.getSource();
				map.put("index", hit.getIndex());
				map.put("type", hit.getType());
				map.put("id", hit.getId());
				list.add(map);
			}
		}
		return list;
	}
//	public List<Map<String, Object>> getListOrderByParam(String index, String type,String param,SortOrder order) {
//		// TODO Auto-generated method stub
//		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
//
//		SearchResponse searchResponse = this.client.prepareSearch(index)
//				.setTypes(type)
//				.addSort(param, order)
//				.get();
//
//		SearchHits hits = searchResponse.getHits();
//		SearchHit [] searchHits = hits.getHits();
//
//		if (searchHits.length>0) {
//			for(SearchHit hit : searchHits) {
//				Map<String, Object> map = hit.getSource();
//				map.put("index", hit.getIndex());
//				map.put("type", hit.getType());
//				map.put("id", hit.getId());
//
//				list.add(map);
//			}
//		}
//
//		return list;
//	}

	*/
/**
	 * @param indices
	 * @param queryBuilder
	 * @return 删除文档量
	 * 实现通过查询批量删除
	 *//*

	public long countDeleteByQuery(String [] indices,QueryBuilder queryBuilder) {

		BulkByScrollResponse response = DeleteByQueryAction.INSTANCE.newRequestBuilder(client).filter(queryBuilder).source(indices).get();

		long deletecount = response.getDeleted();



		return deletecount;
	}


	*/
/**
	 *
	 * @param indices 需要合并的索引
	 * @param maxNumSegments 合并段数
	 * @param onlyExpungeDeletes 是否仅合并删除段
	 * @return
	 *//*

	public ForceMergeResponse indexForceMerge(String [] indices,int maxNumSegments,boolean onlyExpungeDeletes) {

		ForceMergeRequest request = new ForceMergeRequest(indices);
		// 要合并的段数。 要完全合并索引，请将其设置为1.默认为只检查是否需要执行合并，如果需要，则执行它。
		if (maxNumSegments!=-1) {
			request.maxNumSegments(maxNumSegments);
		}
		// 合并进程是否只删除其中包含删除内容的段,此标志只允许合并已删除的段。默认为false。
		if (onlyExpungeDeletes) {
			request.onlyExpungeDeletes(true);
		}
		// 默认强制合并后执行刷新
		request.flush(true);

		ForceMergeResponse response = client.admin().indices().forceMerge(request).actionGet();

		return response;
	}


	*/
/**
	 * @param index
	 * @param type
	 * @return
	 * 查询type下的所有数据
	 *//*

	public List<Map<String, Object>> selectAll(String index,String type,String orderfield,SortOrder order) {
		// TODO Auto-generated method stub

		SearchResponse searchResponse = this.client.prepareSearch(index)
				.setTypes(type)
				.addSort(orderfield, order)
				.get();

		SearchHits hits = searchResponse.getHits();
		SearchHit [] searchHits = hits.getHits();

		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		if (searchHits.length>0) {
			for(SearchHit hit : searchHits) {
				Map<String, Object> map = hit.getSource();
				map.put("index", hit.getIndex());
				map.put("type", hit.getType());
				map.put("id", hit.getId());

				list.add(map);
			}
		}
		return list;
	}

	*/
/**
	 * @param requests
	 * 批量提交
	 *//*

	public void bulk(List<IndexRequest> requests) {
		BulkRequestBuilder bulkRequest = client.prepareBulk();

		for (IndexRequest request : requests) {
			bulkRequest.add(request);
		}

		BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		if (bulkResponse.hasFailures()) {
			System.out.println("批量创建索引错误！");
		}
	}

	*/
/**
	 * 初始化mapping
	 * @param index
	 * @param type
	 * @param template mapping配置
	 *//*

	@SuppressWarnings("deprecation")
	public Boolean addMapping(String index, String type,String template) {
		boolean result = false;
		Builder settings = Settings.builder()
				.put("index.max_result_window", 100000000)
				.put("index.number_of_shards", 5)
				.put("index.number_of_replicas",2);

		if (this.indexExists(index)) {
			// 更新index的setting属性
			*/
/*UpdateSettingsRequest request = new UpdateSettingsRequest(index);
			request.settings(settings);
			UpdateSettingsResponse response = this.client.admin().indices().updateSettings(request).actionGet();*//*


			// 添加mapping信息
			PutMappingRequest mapping = Requests.putMappingRequest(index).type(type).source(template);
			PutMappingResponse mappingResponse =this.client.admin().indices().putMapping(mapping).actionGet();
			result = mappingResponse.isAcknowledged();
		}else {
			CreateIndexResponse indexResponse = this.client.admin().indices().prepareCreate(index).setSettings(settings).addMapping(type, template).get();
			result = indexResponse.isAcknowledged();
		}
		return result;

	}

	*/
/**
	 * 更新index的setting属性
	 * @param index
	 * @param map
	 * @return
	 *//*

	public boolean updateSettings(String index,Map<String, Object> map) {
		boolean result = false;
		*/
/*Builder settings = Settings.builder()
				.put("index.max_result_window", 100000000)
				//.put("index.number_of_shards", 5)
				.put("index.number_of_replicas",2);*//*

		// 更新index的setting属性
		UpdateSettingsRequest request = new UpdateSettingsRequest(index);
		request.settings(map);
		UpdateSettingsResponse response = this.client.admin().indices().updateSettings(request).actionGet();
		result = response.isAcknowledged();

		return result;
	}

	*/
/**
	 * @param index
	 * @param types
	 * @param queryBuilder
	 * @return List<Map<String, Object>>
	 * template层
	 *//*

	public List<Map<String, Object>> getListByQueryBuilder(String index,String[] types,QueryBuilder queryBuilder,String orderfield,SortOrder order) {

		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();

		SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
		if (types!=null&&types.length>0) {
			searchRequestBuilder.setTypes(types);
		}
		searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		searchRequestBuilder.setQuery(queryBuilder);
		searchRequestBuilder.addSort(orderfield, order);
		searchRequestBuilder.setSize(1000);
		searchRequestBuilder.setExplain(true);

		SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

		SearchHit [] searchHits = searchResponse.getHits().getHits();
		for(SearchHit hit : searchHits) {
			Map<String, Object> map = hit.getSource();
			map.put("index", hit.getIndex());
			map.put("type", hit.getType());
			map.put("id", hit.getId());

			list.add(map);
		}

		return list;
	}

	*/
/**
	 * 分页
	 * @param index
	 * @param types
	 * @param queryBuilder
	 * @return List<Map<String, Object>>
	 * template层
	 *//*

	public List<Map<String, Object>> getListByQueryBuilder(String index,String[] types,QueryBuilder queryBuilder,String orderfield,SortOrder order,Integer from,Integer size) {

		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();

		SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
		if (types!=null&&types.length>0) {
			searchRequestBuilder.setTypes(types);
		}
		searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		searchRequestBuilder.setQuery(queryBuilder);
		searchRequestBuilder.addSort(orderfield, order);
		searchRequestBuilder.setFrom(from).setSize(size);
		searchRequestBuilder.setExplain(true);

		SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

		SearchHit [] searchHits = searchResponse.getHits().getHits();
		for(SearchHit hit : searchHits) {
			Map<String, Object> map = hit.getSource();
			map.put("index", hit.getIndex());
			map.put("type", hit.getType());
			map.put("id", hit.getId());

			list.add(map);
		}

		return list;
	}

	*/
/**
	 * @param index
	 * @param types
	 * @param queryBuilder
	 * @return searchHit[]
	 * template层
	 *//*

	public SearchHit [] getHitsByQueryBuilder(String index,String[] types,QueryBuilder queryBuilder,String orderfield,SortOrder order,Integer from,Integer size) {

		SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
		if (types!=null&&types.length>0) {
			searchRequestBuilder.setTypes(types);
		}
		searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		searchRequestBuilder.setQuery(queryBuilder);
		searchRequestBuilder.addSort(orderfield, order);
		searchRequestBuilder.setFrom(from).setSize(size);
		searchRequestBuilder.setExplain(true);

		HighlightBuilder highlightBuilder = new HighlightBuilder().field("*").requireFieldMatch(false);
		highlightBuilder.preTags("<span style=\"color:red\">");
		highlightBuilder.postTags("</span>");
		highlightBuilder.fragmentSize(500);
		searchRequestBuilder.highlighter(highlightBuilder);
		SearchHit [] searchHits = {};
		try {
			SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
			searchHits = searchResponse.getHits().getHits();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();

		}



		return searchHits;
	}

	*/
/**
	 * @param index
	 * @param types
	 * @param dates
	 * @param param
	 * @param eid
	 * @return
	 * template层
	 *//*

	public List<Map<String, Object>> getListGroupByQueryBuilder(String index,String[] types,String dates,String param,String eid) {

		SearchRequestBuilder sBuilder = client.prepareSearch(index);
		if (types!=null&&types.length>0) {
			sBuilder.setTypes(types);
		}
		QueryBuilder queryBuilder = null;
		Date nowtime = new Date();
		SimpleDateFormat yyyyMMdd_format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		BoolQueryBuilder Querydate = null;
		if (dates.equals(yyyyMMdd_format.format(nowtime))) {
			String starttime = dates+" 00:00:00";
			String endtime = format.format(nowtime);
			Querydate = QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime).lte(endtime));
		}else {
			String starttime = dates+" 00:00:00";
			String endtime = dates+" 23:59:59";
			Querydate = QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime).lte(endtime));
		}

		*/
/*String [] date = dates.split("-");
		QueryBuilder querytime = QueryBuilders.boolQuery()
				.must(QueryBuilders.matchPhraseQuery("logtime_year", date[0]))
				.must(QueryBuilders.matchPhraseQuery("logtime_month", date[1]))
				.must(QueryBuilders.matchPhraseQuery("logtime_day", date[2]));*//*


		if(eid!=null&&!eid.equals("")) {
			QueryBuilder queryequipmentid = QueryBuilders.boolQuery()
					.must(QueryBuilders.matchPhraseQuery("equipmentid", eid));
			queryBuilder = QueryBuilders.boolQuery()
					.must(Querydate)
					.must(queryequipmentid);
		}else {
			queryBuilder = QueryBuilders.boolQuery()
					.must(Querydate);
		}

		sBuilder.setQuery(queryBuilder);

		String count = param+"_count";
		// 聚合查询group by
		AggregationBuilder  aggregationBuilder = AggregationBuilders.terms(count).field(param).size(24);

		sBuilder.addAggregation(aggregationBuilder);

		SearchResponse response = sBuilder.execute().actionGet();

		Aggregations aggregations = response.getAggregations();

		Terms terms  = aggregations.get(count);

		List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

		Map<String, Object> map = new HashMap<String, Object>();

		for(Bucket bucket:terms.getBuckets()) {
			map.put(bucket.getKeyAsString(), bucket.getDocCount());
		}

		list.add(map);

		return list;
	}

	*/
/**
	 *
	 * @param index
	 * @param types
	 * @param groupby
	 * @param queryBuilder
	 * @return
	 * template层  实现sql group by
	 *//*

	public List<Map<String, Object>> getListGroupByQueryBuilder(String index,String[] types,String groupby,QueryBuilder queryBuilder) {

		SearchRequestBuilder sBuilder = client.prepareSearch(index);
		if (types!=null&&types.length>0) {
			sBuilder.setTypes(types);
		}

		if (queryBuilder!=null) {
			sBuilder.setQuery(queryBuilder);
		}


		String count = groupby+"_count";
		// 聚合查询group by
		AggregationBuilder  termsQueryBuilder = AggregationBuilders.terms(count).field(groupby).order(Terms.Order.count(false));

		sBuilder.addAggregation(termsQueryBuilder);

		SearchResponse response = sBuilder.execute().actionGet();

		Aggregations aggregations = response.getAggregations();

		Terms terms  = aggregations.get(count);

		List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

		Map<String, Object> map = new LinkedHashMap<String, Object>();

		for(Bucket bucket:terms.getBuckets()) {
			map.put(bucket.getKeyAsString(), bucket.getDocCount());
		}

		list.add(map);

		return list;
	}

	*/
/**
	 *
	 * @param index
	 * @param types
	 * @param groupby
	 * @param queryBuilder
	 * @param size
	 * @return
	 * template层  实现sql group by
	 *//*

	public List<Map<String, Object>> getListGroupByQueryBuilder(String index,String[] types,String groupby,QueryBuilder queryBuilder,int size) {

		SearchRequestBuilder sBuilder = client.prepareSearch(index);
		if (types!=null&&types.length>0) {
			sBuilder.setTypes(types);
		}

		if (queryBuilder!=null) {
			sBuilder.setQuery(queryBuilder);
		}

		String count = groupby+"_count";
		// 聚合查询group by
		AggregationBuilder  termsQueryBuilder = AggregationBuilders.terms(count).field(groupby).order(Terms.Order.count(false)).size(size);

		sBuilder.addAggregation(termsQueryBuilder);

		SearchResponse response = sBuilder.execute().actionGet();

		Aggregations aggregations = response.getAggregations();

		Terms terms  = aggregations.get(count);

		List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

		Map<String, Object> map = new LinkedHashMap<String, Object>();

		for(Bucket bucket:terms.getBuckets()) {
			map.put(bucket.getKeyAsString(), bucket.getDocCount());
		}

		list.add(map);

		return list;
	}

	*/
/**
	 *
	 * @param index
	 * @param types
	 * @param groupbys
	 * @param queryBuilder
	 * @param size
	 * @return
	 * template层  实现sql group by
	 *//*

	public List<Map<String, Object>> getListGroupByQueryBuilder(String index,String[] types,String[] groupbys,QueryBuilder queryBuilder,int size) {

		List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

		SearchRequestBuilder sBuilder = client.prepareSearch(index);
		if (types!=null&&types.length>0) {
			sBuilder.setTypes(types);
		}

		if (queryBuilder!=null) {
			sBuilder.setQuery(queryBuilder);
		}
		AggregationBuilder  termsQueryBuilder = null;
		for(String groupby : groupbys) {
			String count = groupby+"_count";
			// 聚合查询group by
			if (termsQueryBuilder==null) {
				termsQueryBuilder = AggregationBuilders.terms(count).field(groupby).order(Terms.Order.count(false)).size(size);
			}else {
				termsQueryBuilder.subAggregation(AggregationBuilders.terms(count).field(groupby).order(Terms.Order.count(false)).size(size));
			}

		}

		sBuilder.addAggregation(termsQueryBuilder);

		SearchResponse response = sBuilder.execute().actionGet();

		Aggregations aggregations = response.getAggregations();

		Terms terms  = aggregations.get(groupbys[0]+"_count");

		for(Bucket bucket:terms.getBuckets()) {

			Terms terms1 = (Terms) bucket.getAggregations().asMap().get(groupbys[1]+"_count");
			for(Bucket bucket2 : terms1.getBuckets()) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				map.put("source", bucket.getKeyAsString());
				map.put("target",bucket2.getKeyAsString());
				map.put("count", bucket2.getDocCount());
				list.add(map);
			}

		}



		return list;
	}

	*/
/**
	 * 通过名称查询备份仓库
	 * @param repositories 备份仓库名称
	 * @return
	 *//*

	public List<Map<String, Object>> getRepositoriesInfo(String... repositories) {
		GetRepositoriesRequest getRepositoriesRequest = new  GetRepositoriesRequest();
		getRepositoriesRequest.repositories(repositories);
		GetRepositoriesResponse respose = client.admin().cluster().getRepositories(getRepositoriesRequest).actionGet();
		List<RepositoryMetaData> list = respose.repositories();
		List<Map<String, Object>> repositorieslist = new ArrayList<Map<String,Object>>();
		for(RepositoryMetaData metadata:list) {
			Map<String, Object> map = new HashMap<>();
			map.put("name", metadata.name());
			map.putAll(metadata.settings().getAsMap());
			repositorieslist.add(map);
		}
		return repositorieslist;
	}

	*/
/**
	 * 创建备份仓库
	 * @param repositoryName 备份仓库名称
	 * @param repoPath 备份仓库路径
	 * @return
	 *//*

	public Boolean createRepositories(String repositoryName,String repoPath) {

		PutRepositoryRequest request = new PutRepositoryRequest();
		request.name(repositoryName);
		request.type(FsRepository.TYPE);

		Map<String, Object> map = new HashMap<>();
		//map.put("location", "/home/elsearch/es_backups/my_backup/");
		map.put("location", repoPath);
		map.put("compress", true);
		request.settings(map);

		PutRepositoryResponse response = client.admin().cluster().putRepository(request).actionGet();

		return response.isAcknowledged();

	}

	*/
/**
	 * 删除备份仓库
	 * @param repositoryName
	 * @return
	 *//*

	public Boolean deleteRepositories(String repositoryName) {
		DeleteRepositoryRequest request = new DeleteRepositoryRequest(repositoryName);
		request.timeout(TimeValue.timeValueMinutes(1));
		request.timeout("1m");

		AcknowledgedResponse response = client.admin().cluster().deleteRepository(request).actionGet();
		return response.isAcknowledged();
	}
}
*/

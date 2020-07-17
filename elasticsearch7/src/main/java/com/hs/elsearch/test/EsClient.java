package com.hs.elsearch.test;


import com.alibaba.fastjson.JSON;
import com.hs.elsearch.dao.biDao.IBIDao;
import com.hs.elsearch.dao.biDao.impl.BIDaoImpl;
import com.hs.elsearch.util.HSDateUtil;
import com.sun.tools.javac.util.Convert;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ValueCount;
import org.elasticsearch.search.aggregations.metrics.ValueCountAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

public class EsClient {
    @Autowired
    protected IBIDao biDao;

    private static RestHighLevelClient client = null;
    private final static String index = "hslog_packet*";

    /**
     * 初始化es client
     */
    static {
        try {
            //HttpHost httpHost = new HttpHost("www.sdhsdata.com",9200,"http");
            //HttpHost httpHost = new HttpHost("192.168.2.181",9201,"http");
            //RestClientBuilder builder = RestClient.builder(httpHost);
            //client = new RestHighLevelClient(builder);

            HttpHost httpHost = new HttpHost("192.168.2.181",9200,"http");
            // 用户名、密码
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic","hsdata.123"));

            //restClient = RestClient.builder(httpHost);
            RestClientBuilder builder = RestClient.builder(httpHost);
            //设置安全
            builder.setHttpClientConfigCallback(httpClientBuilder ->
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
            );

            client = new RestHighLevelClient(builder);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    //wildcard  模糊查询
    //match_phrase
    //match_phrase_prefix
    //prefix
    //https://elasticsearch.cn/question/6327
    //比对mapping入之前的数据和get之后的数据
    //https://www.jianshu.com/p/fb2761cd569b
    //multi_match 已实现
    //通配符 ？ *
    //match 查询条件分词
    //分词器调研，性能方面等
    public static void main(String args[]) throws Exception {
        /*
        //获取索引
        GetIndexRequest indexRequest = new GetIndexRequest(index);

        getIndexMapping(index);//获取mapping
        getCountByRangeQuery(index);//范围查询，获取count
        getCountByTermsQuery(index);//一个字段，多个value的匹配查询，获取count
        getCountByMultiMatchQuery(index);//多个字段，一个value的匹配查询，获取count
        getCountByMatchQuery(index);//对value分词后的查询，获取count
        getListByCountOfMetrics(index);//基本的聚合查询，group+count，获取count值
        getListBySumOfMetrics(index);//基本的聚合查询，group+sum，获取sum值
        getListByQuery(index);//获取查询后的数据list，获取list<bean>
        getListByAggregation(index);//获取聚合查询后的list （group后count）,获取list<map>


        getListBySumAggregation(index);//获取聚合查询后的list （group后sum）,获取list<map>

        List<String> templatePattern = new ArrayList<>();
        templatePattern.add("hsdata*");
        Map<String, Object> settingmap = new HashMap<>();
        settingmap.put("index.max_result_window", 100000000);
        settingmap.put("index.number_of_shards", 2);
        settingmap.put("index.number_of_replicas", 1);
        settingmap.put("index.lifecycle.name", "hs_policy");
        String mapping = "{\"properties\":{\"visualization\":{\"properties\":{\"title\":{\"type\":\"text\"},\"description\":{\"type\":\"text\"},\"type\":{\"type\":\"keyword\"},\"indexName\":{\"type\":\"keyword\"},\"option\":{\"type\":\"text\"},\"params\":{\"type\":\"text\"}}}}}";
        createOrupdateTemplate("hsdata",templatePattern,settingmap,mapping);
        */
        //bool查询
        /*
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //起始截止时间
        String starttime = "2020-01-01 12:12:12";
        String endtime = "2020-01-01 19:12:12";
        //
        //定义查询内容，gte是包含左侧，lte是包含右侧，gt lt 表示不包含
        boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime).lt(endtime));
        //向es提交查询
        long length = getCountByQuery(boolQueryBuilder,"winlogbeat-7.6.0-2020-04-08","winlogbeat-7.6.0-2020-04-31*");
        System.out.println("rangeQuery查询count---"+length);


         */


        //es.biDao.getMultiAggregation(param);
        //biDao.getMultiAggregation(param);

    }
    public static void getCountAgg(String... indices) throws Exception{
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("ip", "192.168.2.182"));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte("2019-08-01 03:15:11").lte("2019-08-31 22:15:29"));
        // 聚合查询group by
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms("count").field("event_type").order(BucketOrder.count(false)).size(10);
        System.out.println("开始:"+DateTime.now().toString());
        // 返回聚合的内容
        Aggregations aggregations = getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, indices);
        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();
        if (aggregations!=null){
            Terms terms  = aggregations.get("count");
            Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();
            for(Terms.Bucket bucket:terms.getBuckets()) {
                bucketmap.put(bucket.getKeyAsString(), bucket.getDocCount());
            }
            list.add(bucketmap);
        }
        System.out.println("结束:"+DateTime.now().toString()+"-----------group+where(query)查询---"+JSON.toJSONString(list));
    }
    public static void getCount(String... index) throws Exception {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("event_type", "crond"));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte("2019-08-01 00:01:11").lte("2019-08-31 23:59:44"));
        System.out.println("开始:"+DateTime.now().toString());
        //向es提交查询
        long length = getCountByQuery(boolQueryBuilder,index);
        System.out.println("结束:"+DateTime.now().toString()+"---------termQuery查询count---"+length);
    }
    public static boolean createOrupdateTemplate(String tempalateName, List<String> templatePattern, Map<String,Object> settings, String mapping) throws Exception {
        // elasticsearch 索引模板名称
        PutIndexTemplateRequest request = new PutIndexTemplateRequest(tempalateName);
        // 设置模板的匹配值，动态创建索引时索引名与模板进行匹配，例如：eslog-*可以匹配到eslog-analysis
        request.patterns(templatePattern);
        // 设置创建索引时的分片数、副本数等
        request.settings(settings);
        // mapping的具体设置
        request.mapping(mapping, XContentType.JSON);
        // 设置模板的优先级，参数int类型，不设置默认是0，我们默认1，值越大优先级越高
        request.order(1);
        // 设置版本，int类型
        request.version();
        // 是否覆盖原有模板，false：覆盖，true：不覆盖，会报模板已存在的错
        request.create(false);

        AcknowledgedResponse putTemplateResponse = client.indices().putTemplate(request, RequestOptions.DEFAULT);

        boolean acknowledged = putTemplateResponse.isAcknowledged();
        System.out.println(acknowledged);
        return acknowledged;
    }
    /**
    获取当前index的mapping
    REST API：
    GET hslog_packet2020-01-01/_mapping
     */
    public static void getIndexMapping(String index) throws Exception {

        GetMappingsRequest getRequest = new GetMappingsRequest();
        getRequest.indices(index);
        GetMappingsResponse getMappingResponse = client.indices().getMapping(getRequest, RequestOptions.DEFAULT);
        System.out.println("getMapping-----"+ JSON.toJSONString(getMappingResponse.mappings()));
    }
    /**
    获取时间范围内的count 使用rangequery
    REST API:
    GET hslog_packet2020-01-01/_count
    {
      "query": {
        "bool": {
          "must": [
            {
              "range": {
                "logdate": {
                  "gte": "2020-01-01 12:12:12",
                  "lte": "2020-01-01 19:12:12"
                }
              }
            }
          ]
        }
      }
    }
    */
    public static void getCountByRangeQuery(String index) throws Exception {
        //bool查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //起始截止时间
        String starttime = "2020-01-01 12:12:12";
        String endtime = "2020-01-01 19:12:12";
        //
        //定义查询内容，gte是包含左侧，lte是包含右侧，gt lt 表示不包含
        boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime).lt(endtime));
        //向es提交查询
        long length = getCountByQuery(boolQueryBuilder,index);
        System.out.println("rangeQuery查询count---"+length);
    }
    /**
    获取指定K/Values的count 使用termsquery
    REST API:
    GET hslog_packet2020-01-01/_count
    {
      "query": {
        "bool": {
          "must": [
            {
              "terms": {
                "hslog_type": [
                  "defaultpacket"
                ]
              }
            }
          ]
        }
      }
    }
    */
    public static void getCountByTermsQuery(String index) throws Exception {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //定义value值，数组，可设置多个值
        String[] values = {"defaultpacket"};
        //定义field
        String field = "hslog_type";

        //定义查询内容及方式，要注意kv的顺序
        boolQueryBuilder.must(QueryBuilders.termsQuery(field,values));
        //向es提交查询
        long length = getCountByQuery(boolQueryBuilder,index);
        System.out.println("termQuery查询count---"+length);
    }
    /**
    获取指定Keys/Value的count 使用multiMatchQuery   多字段查询
    REST API:
    GET hslog_packet2020-01-01/_count
    {
      "query": {
        "bool": {
          "must": [
            {
              "multi_match": {
                "query": "51.81.29.61",
                "fields": ["ipv4_dst_addr","ipv4_src_addr"]
              }
            }
          ]
        }
      }
    }
    */
    public static void getCountByMultiMatchQuery(String index) throws Exception {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //定义value值，IP
        String value = "51.81.29.61";
        //查询的字段
        String[] multified = {"ipv4_dst_addr","ipv4_src_addr"};
        //
        //定义查询内容及方式（与上面的方法的kv是相反，可根据rest api）
        boolQueryBuilder.must(QueryBuilders.multiMatchQuery(value,multified));
        //向es提交查询
        long length = getCountByQuery(boolQueryBuilder,index);
        System.out.println("MultiMatchQuery查询count---"+length);
    }
    /**
    获取指定Key/Value的count 使用MatchQuery 单字段查询
    REST API:
    GET hslog_packet2020-01-01/_count
    {
      "query": {
        "bool": {
          "must": [
            {
              "match": {
                "packet_source": "libpcap"
              }
            }
          ]
        }
      }
    }
    */
    public static void getCountByMatchQuery(String index) throws Exception {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //定义value值
        String value = "libpcap";
        //要查询的字段
        String field = "packet_source";
        //
        //定义查询内容及方式
        boolQueryBuilder.must(QueryBuilders.matchQuery(field,value));
        //向es提交查询
        long length = getCountByQuery(boolQueryBuilder,index);
        System.out.println("MatchQuery查询count---"+length);
    }
    /**
     获取指定Key/Value的list 取0-10条，以logdate倒序 使用MatchQuery
     REST API:
     GET hslog_packet2020-01-01/_search
    {
      "query": {
        "bool": {
          "must": [
            {
              "match": {
                "packet_source": "libpcap"
              }
            }
          ]
        }
      },
      "sort": [
        {
          "logdate": {
            "order": "desc"
          }
        }
      ],
      "from": 0,
      "size": 10
    }
     */
    public static void getListByQuery(String index) throws Exception {
        //查询条件构造器
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //定义value值
        String value = "libpcap";
        //要查询的字段
        String field = "packet_source";
        //排序字段
        String orderField = "logdate";
        //分页
        Integer fromInt = 0;
        Integer sizeInt = 10;
        //
        //定义查询内容及方式
        boolQueryBuilder.must(QueryBuilders.matchQuery(field,value));
        // 构建排序体,指定排序字段 及排序方式（正序/倒叙）
        SortBuilder sortBuilder = SortBuilders.fieldSort(orderField).order(SortOrder.DESC);
        //向es提交查询
        List<Map<String, Object>> list = new ArrayList<>();
        list.addAll(getListByBuilder(boolQueryBuilder,sortBuilder,fromInt,sizeInt,index));
        System.out.println("queryList查询---"+JSON.toJSONString(list));
    }
    /**
     获取指定Key/Value的list 取0-10条，以logdate倒序 使用MatchQuery
     REST API:
     GET hslog_packet2020-01-01/_search?size=0
    {
      "query": {
        "bool": {
          "must": [
            {
              "range": {
                "logdate": {
                  "gte": "2020-01-01 12:12:12",
                  "lte": "2020-01-01 19:12:12"
                }
              }
            }
          ]
        }
      },
      "aggs": {
        "agg": {
          "terms": {
            "field": "protocol_name",
            "size": 10
          }
        }
      }
    }
     */
    public static void getListByAggregation(String index) throws Exception {
        //查询条件构造器
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //起始截止时间
        String starttime = "2020-01-01 12:12:12";
        String endtime = "2020-01-01 19:12:12";
        //聚合字段
        String groupField = "protocol_name";
        //
        Integer size = 10;
        //
        //定义查询内容，gte是包含左侧，lte是包含右侧，gt lt 表示不包含
        boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime).lt(endtime));
        // 聚合条件处理
        String count = "aggs";
        // 聚合查询group by
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms(count).field(groupField).order(BucketOrder.count(false)).size(size);

        // 返回聚合的内容
        Aggregations aggregations = getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, index);

        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();
        if (aggregations!=null){
            Terms terms  = aggregations.get(count);
            Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();
            for(Terms.Bucket bucket:terms.getBuckets()) {
                bucketmap.put(bucket.getKeyAsString(), bucket.getDocCount());
            }
            list.add(bucketmap);
        }
        System.out.println("group+where(query)查询---"+JSON.toJSONString(list));
    }
    /**
     获取指定对某字段进行group 然后求另一列的sum值，并且根据sum值倒叙排列，限定时间范围
     REST API:
     GET hslog_packet2020-01-01/_search?size=0
    {
      "query": {
        "bool": {
          "must": [
            {
              "range": {
                "logdate": {
                  "gte": "2020-01-01 12:12:12",
                  "lte": "2020-01-01 19:12:12"
                }
              }
            }
          ]
        }
      },
      "aggs": {
        "aggs":{
          "terms": {
            "field": "ipv4_dst_addr.raw",
            "size": 10,
            "order": {
              "sumValue": "desc"
            }
          },
          "sum": {
            "sumValue": {
              "sum": {
                "field": "packet_length"
              }
            }
          }
        }
      }
    }
     */
    public static void getListBySumAggregation(String index) throws Exception {
        //查询条件构造器
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //起始截止时间
        String starttime = "2020-01-01 12:12:12";
        String endtime = "2020-01-01 19:12:12";
        //聚合字段
        String groupField = "ipv4_dst_addr.raw";
        //求和字段
        String sumField = "packet_length";
        //
        Integer size = 10;
        //
        //定义查询内容，gte是包含左侧，lte是包含右侧，gt lt 表示不包含
        boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime).lt(endtime));
        //aggs 和sum均为别名
        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms("aggs").field(groupField).order(BucketOrder.compound(BucketOrder.aggregation("sum",false))).size(size);
        // 在bucket上聚合metric查询sum
        SumAggregationBuilder sumBuilder = AggregationBuilders.sum("sum").field(sumField);

        aggregationBuilder.subAggregation(sumBuilder);

        // 返回聚合的内容
        Aggregations aggregations = getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, index);

        Terms terms  = aggregations.get("aggs");

        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();

        Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();
        //遍历数据获取数值，k/v类型
        for(Terms.Bucket bucket:terms.getBuckets()) {
            Sum sum = bucket.getAggregations().get("sum");
            //长度byte，除以1024转为kb
            bucketmap.put(bucket.getKeyAsString(), Math.round(sum.getValue()/1024));
        }

        list.add(bucketmap);
        System.out.println("sum Group OrderSumDesc查询---"+JSON.toJSONString(list));
    }
    /**
     基本的聚合模式，query+对某字段的count（字段存在+1）
     REST API:
     GET hslog_packet2020-01-01/_search?size=0
    {
      "query": {
        "bool": {
          "must": [
            {
              "range": {
                "logdate": {
                  "gte": "2020-01-01 12:12:12",
                  "lte": "2020-01-01 19:12:12"
                }
              }
            }
          ]
        }
      },
      "aggs": {
        "agg": {
          "value_count": {
            "field": "application_layer_protocol"
          }
        }
      }
    }
     */
    public static void getListByCountOfMetrics(String index) throws Exception {
        //查询条件构造器
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //起始截止时间
        String starttime = "2020-01-01 12:12:12";
        String endtime = "2020-01-01 19:12:12";
        //聚合字段
        String countField = "application_layer_protocol";

        //定义查询内容，gte是包含左侧，lte是包含右侧，gt lt 表示不包含
        boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime).lt(endtime));

        // 聚合metric查询 获取count
        ValueCountAggregationBuilder countAggregationBuilder = AggregationBuilders.count("agg").field(countField);

        // 返回聚合的内容
        Aggregations aggregations = getAggregationsByBuilder(boolQueryBuilder, countAggregationBuilder, index);
        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();
        if (aggregations!=null){
            ValueCount count  = aggregations.get("agg");
            Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();
            bucketmap.put(count.getName(), count.getValue());
            list.add(bucketmap);
        }
        System.out.println("countMetrics查询---"+JSON.toJSONString(list));
    }
    /**
     基本的聚合模式，query+对某字段的sum
     REST API:
     GET hslog_packet2020-01-01/_search?size=0
    {
      "query": {
        "bool": {
          "must": [
            {
              "range": {
                "logdate": {
                  "gte": "2020-01-01 12:12:12",
                  "lte": "2020-01-01 19:12:12"
                }
              }
            }
          ]
        }
      },
      "aggs": {
        "agg": {
          "sum": {
            "field": "packet_length"
          }
        }
      }
    }
     */
    public static void getListBySumOfMetrics(String index) throws Exception {
        //查询条件构造器
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //起始截止时间
        String starttime = "2020-01-01 12:12:12";
        String endtime = "2020-01-01 19:12:12";
        //聚合字段
        String sumField = "packet_length";

        //定义查询内容，gte是包含左侧，lte是包含右侧，gt lt 表示不包含
        boolQueryBuilder.must(QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").gte(starttime).lt(endtime));

        // 聚合metric查询sum
        SumAggregationBuilder sumBuilder = AggregationBuilders.sum("agg").field(sumField);

        // 返回聚合的内容
        Aggregations aggregations = getAggregationsByBuilder(boolQueryBuilder, sumBuilder, index);
        List<Map<String, Object>> list = new LinkedList<Map<String,Object>>();
        if (aggregations!=null){
            Sum sum  = aggregations.get("agg");
            Map<String, Object> bucketmap = new LinkedHashMap<String, Object>();
            //长度byte，除以1024转为kb
            bucketmap.put(sum.getName(), Math.round(sum.getValue()/1024));
            list.add(bucketmap);
        }
        System.out.println("sumMetrics查询---"+JSON.toJSONString(list));
    }

    /**
     * 通过查询条件获取count
     * @param queryBuilder
     * @param indices
     * @return
     * 获取index下的索引数据条数
     */
    public static long getCountByQuery(QueryBuilder queryBuilder, String... indices) throws Exception {

        //SearchRequest searchRequest = new SearchRequest(indices);
        CountRequest countRequest = new CountRequest(indices);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        if (queryBuilder!=null) {
            searchSourceBuilder.query(queryBuilder);
        }
        countRequest.source(searchSourceBuilder);

        CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
        long length = countResponse.getCount();
        return length;
    }
    /**
     * 通过查询体（QueryBuilder）获取数据（带排序功能）
     * 带有排序的分页查询
     * @param queryBuilder 查询体
     * @param sortBuilder 排序体
     * @param from 翻页
     * @param size 每页数据条数
     * @param indices
     * @return
     */
    public static List<Map<String, Object>> getListByBuilder(QueryBuilder queryBuilder, SortBuilder sortBuilder, Integer from, Integer size, String... indices) throws Exception {

        SearchRequest searchRequest = new SearchRequest(indices);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        if (queryBuilder!=null){
            sourceBuilder.query(queryBuilder);
        }
        if (sortBuilder!=null){
            sourceBuilder.sort(sortBuilder);
        }
        if (from>=0&&size>=0){
            sourceBuilder.from(from).size(size);
        }
        searchRequest.source(sourceBuilder);

        SearchResponse response = client.search(searchRequest,RequestOptions.DEFAULT);

        SearchHits searchHits = response.getHits();

        return getListBySearchHit(searchHits);
    }
    /**
     * 通过SearchHit数组获取数据，补全index、type、id，组成list，返回
     * TODO 确认api是否有直接返回集合的方法
     * @param searchHits
     * @return
     */
    public static List<Map<String, Object>> getListBySearchHit(SearchHits searchHits){
        SearchHit[] searchHit = searchHits.getHits();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(SearchHit hit : searchHit) {
            Map<String, Object> map = hit.getSourceAsMap();
            map.put("index", hit.getIndex());
            map.put("id", hit.getId());
            map.put("type",map.get("hslog_type"));
            map.remove("payload");//payload数据量太大，影响请求响应速度。
            // 处理高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            for(Map.Entry<String, HighlightField> entry : highlightFields.entrySet()){
                if (entry.getValue()!=null){
                    Text[] texts = entry.getValue().fragments();
                    String name = "";
                    for(Text text :texts){
                        name += text;
                    }
                    map.put(entry.getKey(),name);
                }
            }
            list.add(map);
        }
        return list;
    }
    /**
     * 带有条件的聚合查询
     * @param queryBuilder
     * @param aggregationBuilder
     * @param indices
     * @return
     */
    public static Aggregations getAggregationsByBuilder(QueryBuilder queryBuilder, AggregationBuilder aggregationBuilder, String... indices) throws Exception {

        SearchRequest searchRequest = new SearchRequest(indices);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        if (queryBuilder!=null){
            searchSourceBuilder.query(queryBuilder);
        }
        if (aggregationBuilder!=null){
            searchSourceBuilder.aggregation(aggregationBuilder);
        }
        searchRequest.source(searchSourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        Aggregations aggregations= response.getAggregations();

        return aggregations;
    }
}
package com.hs.elsearch.dao.impl;

import com.hs.elsearch.dao.IElasticsearchDao;
import com.hs.elsearch.template.ESTransportSearchTemplate;
import com.hs.elsearch.template.bak.ClientTemplate;
import org.apache.directory.api.util.Strings;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: hslog-parent
 * @description: elasticsearch持久化层
 * @author: jiyourui
 * @create: 2019-09-12 14:17
 **/
public class ElasticsearchDao implements IElasticsearchDao {

    private static Logger logger = Logger.getLogger(ElasticsearchDao.class);

    /*private ClientTemplate clientTemplate;

    public ElasticsearchDao(final ClientTemplate clientTemplate){
        logger.info("module elasticsearch5 Dao层 初始化··· ···");
        this.clientTemplate = clientTemplate;
    }*/

    @Autowired
    ESTransportSearchTemplate searchTemplate;

    @Override
    public long getCount(Map<String, String> map, String[] types, String... indices) {

        long result = 0;

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (map!=null&&!map.isEmpty()) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 时间段处理
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
                result = searchTemplate.getCountByQuery(queryBuilder, types,indices);
            } catch (Exception e) {
                result = 0;
            }
        }else {

            try {
                result = searchTemplate.getCountByQuery(null, types,indices);
            } catch (Exception e){
                result = 0;
            }

        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getListByContent(String content, String userid, int page, int size, String[] types, String... indices) {

        // 构建查询体
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 查询条件-用户id
        if (userid!=null&&!userid.equals("")){
            QueryBuilder userQueryBuilder = QueryBuilders.termQuery("userid", userid);
            boolQueryBuilder.must(userQueryBuilder);
        }
        // 查询条件-时间范围
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryBuilder dateQueryBuilder = QueryBuilders.rangeQuery("logdate").format("yyyy-MM-dd HH:mm:ss").lte(format.format(new Date()));
        boolQueryBuilder.must(dateQueryBuilder);
        // 查询条件-查询关键字
        if (content!=null&&!content.equals("")){
            // 去除查询条件前后的无用空格
            content = content.trim();
            MultiMatchQueryBuilder contentQueryBuilder = QueryBuilders.multiMatchQuery(Strings.toLowerCase(content),"operation_level","operation_des","ip","hostname","process","operation_facility"," equipmentname");
            boolQueryBuilder.must(contentQueryBuilder);
        }

        // 构建排序体,指定排序字段
        SortBuilder sortBuilder = SortBuilders.fieldSort("logdate").order(SortOrder.DESC);
        // 构建高亮体，指定包含查询条件的所有列都高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("*").requireFieldMatch(false);
        highlightBuilder.preTags("<span style=\"color:red\">");
        highlightBuilder.postTags("</span>");
        highlightBuilder.fragmentSize(500);

        return searchTemplate.getListByBuilder(boolQueryBuilder,sortBuilder,highlightBuilder,page,size,types,indices);
    }
}

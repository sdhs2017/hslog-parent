package com.hs.elsearch.dao.impl;

import com.hs.elsearch.dao.IElasticsearchDao;
import com.hs.elsearch.template.bak.ClientTemplate;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @program: hslog-parent
 * @description: elasticsearch持久化层
 * @author: jiyourui
 * @create: 2019-09-12 14:17
 **/
public class ElasticsearchDao implements IElasticsearchDao {

    private static Logger logger = Logger.getLogger(ElasticsearchDao.class);

    private ClientTemplate clientTemplate;

    public ElasticsearchDao(final ClientTemplate clientTemplate){
        logger.info("module elasticsearch5 Dao层 初始化··· ···");
        this.clientTemplate = clientTemplate;
    }

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
                result = clientTemplate.getCountByQuery(queryBuilder, types,indices);
            } catch (Exception e) {
                result = 0;
            }
        }else {

            try {
                result = clientTemplate.getCountByQuery(null, types,indices);
            } catch (Exception e){
                result = 0;
            }

        }

        return result;
    }
}

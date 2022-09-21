package com.hs.elsearch.dao.common.impl;

import com.hs.elsearch.dao.common.ICommonDao;
import com.hs.elsearch.entity.HttpRequestParams;
import com.hs.elsearch.template.IndexTemplate;
import com.hs.elsearch.template.SearchTemplate;
import com.hs.elsearch.util.HSDateUtil;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.snapshots.SnapshotInfo;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonDaoImpl implements ICommonDao {
    @Autowired
    SearchTemplate searchTemplate;
    @Autowired
    IndexTemplate indexTemplate;
    @Override
    public List<Map<String, Object>> getListByDateHistogramAggregation(HttpRequestParams params) throws Exception {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 时间段查询条件处理
        if (params.getHasTimeArea()) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery(params.getTimeField()).format("yyyy-MM-dd HH:mm:ss").gte(params.getStartTime()).lte(params.getEndTime()));
        }else {
            //如果真的不存在时间参数，获取当前时间所谓截止时间进行处理
            boolQueryBuilder.must(QueryBuilders.rangeQuery(params.getTimeField()).format("yyyy-MM-dd HH:mm:ss").lte(DateTime.now().toString("yyyy-MM-dd HH:mm:ss")));
        }
        // 聚合bucket查询group by
        AggregationBuilder aggregationBuilder = null;
        //默认秒钟间隔
        DateHistogramInterval dateHis = DateHistogramInterval.seconds(10);
        switch(params.getIntervalType()){
            case "seconds":
                dateHis = DateHistogramInterval.seconds(params.getIntervalValue());
                aggregationBuilder = AggregationBuilders.dateHistogram("aggs").field(params.getTimeField()).fixedInterval(dateHis);
                break;
            case "minutes":
                dateHis = DateHistogramInterval.minutes(params.getIntervalValue());
                aggregationBuilder = AggregationBuilders.dateHistogram("aggs").field(params.getTimeField()).fixedInterval(dateHis);
                break;
            case "hours":
                dateHis = DateHistogramInterval.hours(params.getIntervalValue());
                aggregationBuilder = AggregationBuilders.dateHistogram("aggs").field(params.getTimeField()).fixedInterval(dateHis);
                break;
        }


        // 返回聚合的内容
        Aggregations aggregations = searchTemplate.getAggregationsByBuilder(boolQueryBuilder, aggregationBuilder, params.getIndices());
        Histogram agg = aggregations.get("agg");

        List<Map<String, Object>> list = new ArrayList<>();
        // For each entry
        for (Histogram.Bucket entry : agg.getBuckets()) {
            Map<String, Object> aggmap = new HashMap<>();
            //new DateTime(entry.getKey());
            ZonedDateTime key = (ZonedDateTime)entry.getKey();
            //DateTime key = (DateTime) entry.getKey();    // Key
            String keyAsString = entry.getKeyAsString(); // Key as String
            long docCount = entry.getDocCount();         // Doc count
            //aggmap.put("hour",key.hourOfDay().getAsString());
            aggmap.put("hour",key.getHour());
            aggmap.put("count",docCount);
            list.add(aggmap);

        }
        return list;
    }

    @Override
    public boolean reindex(Script script, String target_index, String... source_indices) throws IOException {
        return indexTemplate.reindex(script,target_index,source_indices);
    }

    @Override
    public boolean updateIndexSettings(Settings settings, String... indices) throws IOException {
        return indexTemplate.updateIndexSettings(settings,indices);
    }

    @Override
    public boolean deleteIndices(String... indices) throws Exception {
        return indexTemplate.deleteIndex(indices);
    }

    @Override
    public boolean deleteTemplate(String templateName) throws Exception {
        return indexTemplate.deleteTemplate(templateName);
    }

    @Override
    public boolean createIndex(String indexName, Settings.Builder setting, String mappingProperties) throws Exception {
        return indexTemplate.createIndex(indexName,setting,mappingProperties);
    }

    @Override
    public boolean indexExists(String... indices) throws Exception {
        return indexTemplate.indexExists(indices);
    }

    @Override
    public boolean closeIndex(String... indices) throws Exception {
        return indexTemplate.closeIndex(indices);
    }

    @Override
    public boolean openIndex(String... indices) throws Exception {
        return indexTemplate.openIndex(indices);
    }

    @Override
    public boolean createSLMPolicy(String indices, String policy_id, String name, String schedule, String repository) throws IOException {
        return indexTemplate.createSLMPolicy(indices,policy_id,name,schedule,repository);
    }

    @Override
    public String executeSLMPolicy(String policy_id) throws IOException {
        return indexTemplate.executeSLMPolicy(policy_id);
    }

    @Override
    public boolean deleteSLMPolicy(String policy_id) throws IOException {
        return indexTemplate.deleteSLMPolicy(policy_id);
    }

    @Override
    public List<SnapshotInfo> getSnapListByPolicyId(String repositoryName) throws IOException {
        return indexTemplate.getSnapListByPolicyId(repositoryName);
    }

    @Override
    public boolean restoreSnapshot(String repositoryName, String snapshotName) throws IOException {
        return indexTemplate.restoreSnapshot(repositoryName,snapshotName);
    }
}

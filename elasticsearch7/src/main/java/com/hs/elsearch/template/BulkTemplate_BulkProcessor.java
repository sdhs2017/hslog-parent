package com.hs.elsearch.template;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @program: hslog-parent
 * @description:
 * @author: jiyourui
 * @create: 2020-08-12 11:05
 **/
@Slf4j
public class BulkTemplate_BulkProcessor {


    RestHighLevelClient restHighLevelClient;

    public BulkTemplate_BulkProcessor(final RestHighLevelClient restHighLevelClient){
        this.restHighLevelClient = restHighLevelClient;
    }

    private BulkProcessor bulkProcessor;


    //@PostConstruct
    public void init(int bulkActions,int concurrentRequests) {

        // 创建bulkprocessor的监听
        BulkProcessor.Listener listener = new BulkProcessor.Listener() {

            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                //重写beforeBulk,在每次bulk request发出前执行,在这个方法里面可以知道在本次批量操作中有多少操作数
                //int numberOfActions = request.numberOfActions();
                //double MB = request.estimatedSizeInBytes()/1024/1024;
                //double KB = request.estimatedSizeInBytes()/1024;
                //System.out.println(format.format(new Date())+"  Executing bulk [{"+ executionId +"}] with {"+ numberOfActions +"} requests, with {"+MB+"} MB, {"+KB+"} KB");
                //LOGGER.info("Executing bulk [{}] with {} requests, with {} MB, {} KB", executionId, numberOfActions,MB,KB);
            }


            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                //重写afterBulk方法，每次批量请求结束后执行，可以在这里知道是否有错误发生。
                if (response.hasFailures()) {
                    log.error("Bulk [{}] executed with failures,response = {}", executionId, response.buildFailureMessage());
                } else {
                    //System.out.println(format.format(new Date())+" Bulk [{"+executionId+"}] completed in {"+response.getTook().getMillis()+"} milliseconds");
                    //LOGGER.info("Bulk [{}] completed in {} milliseconds", executionId, response.getTook().getMillis());
                }
                //BulkItemResponse[] responses = response.getItems();
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                //重写方法，如果发生错误就会调用。
                log.error("Failed to execute bulk", failure);
            }
        };

        BulkProcessor bulkProcessor = BulkProcessor.builder(
                (request, bulkListener) -> restHighLevelClient.bulkAsync(request, RequestOptions.DEFAULT, bulkListener),
                listener)
                // 批量提交阈值2000
                .setBulkActions(bulkActions)
                // 达到刷新的大小10MB
                .setBulkSize(new ByteSizeValue(10, ByteSizeUnit.MB))
                // 固定刷新的时间频率5s
                .setFlushInterval(TimeValue.timeValueSeconds(5L))
                // 并发线程数
                .setConcurrentRequests(concurrentRequests)
                // 重试补偿策略，失败后100ms重新执行请求，最大请求3次
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3)).build();

        this.bulkProcessor = bulkProcessor;
    }



    @PreDestroy
    public void destroy() {
        try {
            bulkProcessor.awaitClose(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Failed to close bulkProcessor", e);
        }
        log.info("bulkProcessor closed!");
    }


    public void update(UpdateRequest request) {
        this.bulkProcessor.add(request);
    }


    public void add(IndexRequest request) {
        this.bulkProcessor.add(request);
    }
}

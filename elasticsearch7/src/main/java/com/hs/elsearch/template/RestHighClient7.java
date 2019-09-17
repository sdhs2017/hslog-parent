package com.hs.elsearch.template;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RestHighClient7 implements FactoryBean<RestHighLevelClient>, InitializingBean, DisposableBean {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    //Java High Level REST Client （高版本client）
    private RestHighLevelClient restHighLevelClient;

    private Map<String, String> elasticsearchbean;

    public void setElasticsearchbean(Map<String, String> elasticsearchbean) {
        this.elasticsearchbean = elasticsearchbean;
    }

    @Override
    public void destroy() throws Exception {
        if (restHighLevelClient != null) {
            restHighLevelClient.close();
        }
    }

    @Override
    public RestHighLevelClient getObject() throws Exception {
        logger.info("------------elasticsearch连接初始化成功------------");
        //System.out.println("------------拿到elasticsearch连接------------");
        return restHighLevelClient;
    }

    @Override
    public Class<RestHighLevelClient> getObjectType() {
        return RestHighLevelClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        buildClient();
    }


    //初始化client
    private void buildClient() {

        logger.info("------------启动连接elasticsearch-----IP:"+elasticsearchbean.get("es_ip")+"   PORT:"+elasticsearchbean.get("es_port")+"-------");
        //System.out.println("------------启动连接elasticsearch------------");
        HttpHost httpHost = new HttpHost(elasticsearchbean.get("es_ip"),Integer.valueOf(elasticsearchbean.get("es_port")),"http");
        //restClient = RestClient.builder(httpHost);
        RestClientBuilder builder = RestClient.builder(httpHost);

        restHighLevelClient = new RestHighLevelClient(builder);
    }
}

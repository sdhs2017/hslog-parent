package com.hs.elsearch.client;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
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
        // 用户名、密码
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(elasticsearchbean.get("es_user"),elasticsearchbean.get("es_password")));

        //restClient = RestClient.builder(httpHost);
        RestClientBuilder builders = RestClient.builder(httpHost);
        //设置安全
        builders.setHttpClientConfigCallback(httpClientBuilder ->
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
        );
        //请求配置
        builders.setRequestConfigCallback(builder -> builder
                .setConnectTimeout(10000)//连接超时
                .setSocketTimeout(300000)//请求超时300秒
                );

        restHighLevelClient = new RestHighLevelClient(builders);
    }
}

package com.hs.elsearch.template.bak;

import org.apache.log4j.Logger;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;


public class EStransportclient implements FactoryBean<TransportClient>, InitializingBean, DisposableBean {

    private static Logger logger = Logger.getLogger(EStransportclient.class);

    private TransportClient client;

    private Map<String, String> elasticsearchbean;

    public void setElasticsearchbean(Map<String, String> elasticsearchbean) {
        this.elasticsearchbean = elasticsearchbean;
    }

    public void buildingClient() throws UnknownHostException {

        Settings settings = Settings.builder().put("cluster.name", elasticsearchbean.get("es_name")).build();
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(elasticsearchbean.get("es_ip")), Integer.valueOf(elasticsearchbean.get("es_port")) ));

    }

    @Override
    public void destroy() throws Exception {
        if (client!=null){
            client.close();
        }

    }

    @Override
    public TransportClient getObject() throws Exception {
        System.out.println("elasticsearch transport-client builded successful!");
        return client;
    }

    @Override
    public Class<TransportClient> getObjectType() {
        return TransportClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("开始初始化Transport-client，elasticsearch-ip：" +elasticsearchbean.get("es_ip") + "  elasticsearch-port: " + elasticsearchbean.get("es_port"));
        buildingClient();
    }
}

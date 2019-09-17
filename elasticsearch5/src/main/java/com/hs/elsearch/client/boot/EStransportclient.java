package com.hs.elsearch.client.boot;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;


//@Component
public class EStransportclient implements FactoryBean<TransportClient>, InitializingBean, DisposableBean {

    private TransportClient client;

//    @Autowired
    ESConfig esConfig;

    public void buildingClient() throws UnknownHostException {

        Settings settings = Settings.builder().put("cluster.name", esConfig.getCluster_name()).build();
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esConfig.getIp()), esConfig.getPort() ));
        System.out.println("es初始化连接："+esConfig.getIp()+":"+esConfig.getPort());
    }

    @Override
    public void destroy() throws Exception {
        if (client!=null){
            client.close();
        }

    }

    @Override
    public TransportClient getObject() throws Exception {
        System.out.println("es connection success");
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
        buildingClient();
    }
}

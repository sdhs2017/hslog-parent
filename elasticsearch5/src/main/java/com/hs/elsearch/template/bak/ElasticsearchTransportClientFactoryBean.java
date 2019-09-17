/*
package com.hs.elasearch.template;

//import com.jz.bigdata.framework.spring.es.setting.PropertiesSettingsLoader;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.io.Streams;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.loader.SettingsLoader;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

//import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;

*/
/**
* @ClassName: ElasticsearchTransportClientFactoryBean 
*
* @Description: Elasticsearch TransportClient 客户端工厂类
*
* @author Huatao Jin
* 
* @date 2016年3月18日 下午3:03:06 
*  
*//*

public class ElasticsearchTransportClientFactoryBean implements FactoryBean<Client>, InitializingBean, DisposableBean {

	private static Logger logger = LoggerFactory.getLogger(ElasticsearchTransportClientFactoryBean.class);
	
	private Client client;
	
	private List<Resource> configLocations;
	
	private Resource configLocation;
	
	private Map<String, String> settings;
	
	private Map<String, String> transportAddresses;
	
	public void setConfigLocations(List<Resource> configLocations) {
		this.configLocations = configLocations;
	}

	public void setConfigLocation(Resource configLocation) {
		this.configLocation = configLocation;
	}

	public void setSettings(Map<String, String> settings) {
		this.settings = settings;
	}

	public void setTransportAddresses(Map<String, String> transportAddresses) {
		this.transportAddresses = transportAddresses;
	}

	@Override
	public void destroy() throws Exception {
		client.close();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		internalCreateTransportClient();
	}
	
	@SuppressWarnings("resource")
	private void internalCreateTransportClient(){
		Settings.Builder builder = Settings.builder();
		
		if(null != configLocation)
			internalLoadSettings(builder, configLocation);
		
		if(null != configLocations){
			for(final Resource location:configLocations){
				internalLoadSettings(builder, location);
			}
		}
		
		if(null != settings)
			builder.put(settings);
		
		*/
/**
		 * dengyu
		 * set cluster.name
		 *//*

		Settings settings = Settings.builder().put("cluster.name", transportAddresses.get("es_name")).build();
		// jiyourui  安装x-pack之后settings的配置修改
		*/
/*Settings settings = Settings.builder()
				.put("cluster.name", transportAddresses.get("es_name"))
				.put("xpack.security.transport.ssl.enabled", false)
				// 在安装x-pack之后需要增加用户和密码验证
				.put("xpack.security.user","elastic:hsdata.321")
				.build();*//*

		
		// jiyourui  注释掉
		client = new PreBuiltTransportClient(settings);
		
		*/
/*if(transportAddresses != null){
			for(final Entry<String, Integer> address: transportAddresses.entrySet()){
				if(address.getKey() != null && !address.getValue().equals("")){
					logger.info("正在添加 transport IP地址:" + address.getKey() + " 端口:" + address.getValue());
					try {
						InetAddress e1 = InetAddress.getByName(address.getKey());
//						((PreBuiltTransportClient)client).addTransportAddress(new InetSocketTransportAddress(e1, address.getValue()));
						((PreBuiltTransportClient)client).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ConnectionInfo.ES_IP), Integer.valueOf(ConnectionInfo.ES_PORT)));
//						((PreBuiltTransportClient)client).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.100.101"), 9300));
					} catch (UnknownHostException e) {
						logger.info("解析主机地址异常", e);
					}
				}
			}
		}*//*

		
		// jiyourui 注释掉
		if(transportAddresses != null){
			logger.info("正在添加 transport IP地址:" + transportAddresses.get("es_ip") + " 端口:" + transportAddresses.get("es_port"));
			try {
				//InetAddress e1 = InetAddress.getByName(transportAddresses.get("es_ip"));
//				((PreBuiltTransportClient)client).addTransportAddress(new InetSocketTransportAddress(e1, address.getValue()));
				((PreBuiltTransportClient)client).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(transportAddresses.get("es_ip")), Integer.valueOf(transportAddresses.get("es_port")) ));
//				((PreBuiltTransportClient)client).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.100.101"), 9300));
			} catch (UnknownHostException e) {
				logger.info("解析主机地址异常", e);
			}
		}
	}
	
	*/
/*private void internalLoadSettings(Settings.Builder builder, Resource configLocation){
		try{
			final String fileName = configLocation.getFilename();
			if(logger.isInfoEnabled()){
				logger.info("正在从" + fileName +"加载配置文件...");
			}
			//builder.loadFromStream(fileName, configLocation.getInputStream());
			if(fileName.endsWith(".properties")){
				SettingsLoader settingsLoader = new PropertiesSettingsLoader();
				Map<String, String> loadedSettings =
						settingsLoader.load(Streams.copyToString(new InputStreamReader(configLocation.getInputStream(), StandardCharsets.UTF_8)));
				if(!loadedSettings.isEmpty()){
					builder.put(loadedSettings);
				}
			}
		}catch(final Exception e){
			throw new IllegalArgumentException("无法加载配置文件:" + configLocation.getDescription() , e);
		}
	}*//*


	@Override
	public Client getObject() throws Exception {
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

	
}
*/

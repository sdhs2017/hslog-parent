package com.jz.bigdata.components.elasticsearch.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.transport.client.PreBuiltTransportClient;


public class EsUtil {  
    private static TransportClient client = null;  
    private static Settings settings =null;
//    private static String ip = "192.168.2.143";
    private static String ip = "124.133.246.61";
    private static int port = 9300;
    
    private static BulkProcessor bulkProcessor;
  
    public static TransportClient getTransportClient() throws UnknownHostException {  
        if (client == null  
                || (client).connectedNodes().isEmpty()) {  
            synchronized (EsUtil.class) {  
                if (client == null  
                        || ( client).connectedNodes()  
                                .isEmpty()) {  
//                    settings = Settings.builder().put("cluster.name", "myElasticsearchCluster").put("client.transport.sniff", true).build();
                    settings = Settings.builder().put("cluster.name", "myElasticsearchCluster").build();
//                    settings = Settings.builder().put("cluster.name", "myElsearchCluster").put("client.transport.sniff", true).build();
//        			client = new PreBuiltTransportClient(settings)
//        					.addTransportAddress(
//        							new InetSocketTransportAddress(InetAddress.getByName(ip), port));
        			client = new PreBuiltTransportClient(settings)
                            .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip), port));
                }  
            }  
        }  
        return client;  
    }
    
    public static BulkProcessor getBulkProcessor(TransportClient client){
    	/**
    	 * BulkProcessor，功能、参数较多
    	 * 
    	 */
    	bulkProcessor = BulkProcessor.builder(
	        client,  
	        new BulkProcessor.Listener() {
	        	
	            /* (non-Javadoc)
	             * This method is called just before bulk is executed. You can for example see the numberOfActions with request.numberOfActions()
	             * @see org.elasticsearch.action.bulk.BulkProcessor.Listener#beforeBulk(long, org.elasticsearch.action.bulk.BulkRequest)
	             */
	            public void beforeBulk(long executionId,BulkRequest request) {
	            	System.out.println("before");
	            	System.out.println(request.numberOfActions());
	            } 

	            /*  (non-Javadoc)
	             *  This method is called after bulk execution. You can for example check if there was some failing requests with response.hasFailures()
	             * @see org.elasticsearch.action.bulk.BulkProcessor.Listener#afterBulk(long, org.elasticsearch.action.bulk.BulkRequest, org.elasticsearch.action.bulk.BulkResponse)
	             */
	            public void afterBulk(long executionId,
	                                  BulkRequest request,
	                                  BulkResponse response) { 
	            	System.out.println("after1");
	            	System.out.println(response.hasFailures());
	            } 
	            
	            /* 	(non-Javadoc)
				 *	This method is called when the bulk failed and raised a Throwable
	             * @see org.elasticsearch.action.bulk.BulkProcessor.Listener#afterBulk(long, org.elasticsearch.action.bulk.BulkRequest, java.lang.Throwable)
	             */
	            public void afterBulk(long executionId,
	                                  BulkRequest request,
	                                  Throwable failure) { 
	            	System.out.println("after2");
	            	System.out.println(failure.getMessage());
	            }
	        })
    		//We want to execute the bulk every 10 000 requests
//	        .setBulkActions(10000) 
	        //We want to flush the bulk every 5mb
//	        .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB)) 
	        //We want to flush the bulk every 10 seconds whatever the number of requests
	        .setFlushInterval(TimeValue.timeValueSeconds(10)) 
	        /*
	         * Set the number of concurrent requests. A value of 0 means that only a single request 
	         * will be allowed to be executed. A value of 1 means 1 concurrent request is allowed to be executed 
	         * while accumulating new bulk requests.
	         * 
	         * 允许同时执行数
	         */
	        .setConcurrentRequests(1) 
	        /*
	         * Set a custom backoff policy which will initially wait for 100ms, increase exponentially 
	         * and retries up to three times. A retry is attempted whenever one or more bulk item requests 
	         * have failed with an EsRejectedExecutionException which indicates that there were too little 
	         * compute resources available for processing the request. To disable backoff, pass BackoffPolicy.noBackoff().
	         */
	        .setBackoffPolicy(
	            BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3)) 
//	        .setBackoffPolicy(
//	        		BackoffPolicy.noBackoff()) 
	        .build();
    	return bulkProcessor;
    }
  
    public static void close(TransportClient client) {  
        if (client != null) {  
            client.close();  
        }  
    }  
    
    
}  
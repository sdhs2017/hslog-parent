package com.hs.elsearch.template.bak;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.client.ClusterAdminClient;

/** 
* @ClassName: ClusterCallback 
*
* @Description: 一次 Elasticsearch 集群基本操作的回调接口
*
* @author Huatao Jin
* 
* @date 2016年3月18日 上午11:02:15 
* 
* @param <T> 客户端操作返回的子类
*/
public interface ClusterCallback<T extends ActionResponse> {
	
	/** 
	* @Title: execute 
	* @Description: 使用配置集群客户端对象执行一次操作
	* @param admin
	* 			一个集群客户端对象
	* @return ActionFuture<T>    
	* 		    一个ActionFuture子类   
	* @author Huatao Jin
	*/
	ActionFuture<T> execute(final ClusterAdminClient admin);
}

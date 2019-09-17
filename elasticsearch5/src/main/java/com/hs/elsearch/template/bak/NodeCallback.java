package com.hs.elsearch.template.bak;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.client.IndicesAdminClient;

/** 
* @ClassName: NodeCallback 
*
* @Description: 一次 Elasticsearch 节点基本操作的回调接口
*
* @author Huatao Jin
* 
* @date 2016年3月18日 上午11:09:31 
* 
* @param <T> 客户端操作返回的子类
*/
public interface NodeCallback<T extends ActionResponse> {
	
	/** 
	* @Title: execute 
	* @Description: 使用配置节点客户端对象执行一次操作
	* @param client
	* 			一个节点客户端对象
	* @return ActionFuture<T>    
	* 			一个ActionFuture子类   
	* @author Huatao Jin
	*/
	ActionFuture<T> execute(final IndicesAdminClient client);
}

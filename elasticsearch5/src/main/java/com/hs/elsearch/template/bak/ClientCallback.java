package com.hs.elsearch.template.bak;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.client.Client;

/** 
* @ClassName: ClientCallback 
*
* @Description:一次Elasticsearch基本操作的回调接口
*
* @author Huatao Jin
* 
* @date 2016年3月18日 上午10:55:48 
* 
* @param <T> 客户端操作返回的子类
*/
public interface ClientCallback<T extends ActionResponse> {

	/** 
	* @Title: execute 
	* @Description: 使用配置客户端对象执行一次操作 
	* @param client
	* 			一个ES客户端实例对象
	* @return ActionFuture<T>
	* 			一个ActionFuture子类   	
	* @author Huatao Jin
	*/
	ActionFuture<T> execute(final Client client);
}

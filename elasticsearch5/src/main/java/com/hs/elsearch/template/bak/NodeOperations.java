package com.hs.elsearch.template.bak;

import com.hs.elsearch.template.bak.ClientCallback;
import com.hs.elsearch.template.bak.ClusterCallback;
import com.hs.elsearch.template.bak.NodeCallback;
import org.elasticsearch.action.ActionResponse;

/** 
* @ClassName: NodeOperations 
*
* @Description: 指定一个索引名称，该接口描述了Elasticsearch节点常用操作
*
* @author Huatao Jin
* 
* @date 2016年3月18日 上午11:12:05 
*  
*/
public interface NodeOperations {

	/** 
	* @Title: indexExists 
	* @Description:  索引是否存在
	* @return boolean  索引存在返回true 
	* @author Huatao Jin
	*/
	boolean indexExists();
	
	boolean indexExists(final String indexName);
	
	/** 
	* @Title: deleteIndex 
	* @Description:  删除索引  
	* @author Huatao Jin
	*/
	void deleteIndex();
	
	void deleteIndex(final String indexName);
	
	/** 
	* @Title: refreshIndex 
	* @Description: 简单刷新索引   
	* @author Huatao Jin
	*/
	void refreshIndex();
	
	void refreshIndex(final String indexName);
	
	/** 
	* @Title: closeIndex 
	* @Description: 关闭索引   
	* @author Huatao Jin
	*/
	void closeIndex();
	
	void closeIndex(final String indexName);
	
	/** 
	* @Title: flushIndex 
	* @Description: 持久性刷新索引
	* @author Huatao Jin
	*/
	void flushIndex();
	
	void flushIndex(final String indexName);
	
	/** 
	* @Title: snapshotIndex 
	* @Description: 创建一个索引持久化快照    
	* @author Huatao Jin
	*/
	@Deprecated
	void snapshotIndex();
	
	@Deprecated
	void snapshotIndex(final String indexName);
	
	/** 
	* @Title: executeGet 
	* @Description: 执行一次获取节点操作
	* @param callback
	* @return T    
	* @throws 
	* @author Huatao Jin
	*/
	<T extends ActionResponse> T executeGet(final NodeCallback<T> callback);
	
	/** 
	* @Title: executeGet 
	* @Description: 执行一次获取集群操作
	* @param callback
	* @return T    
	* @throws 
	* @author Huatao Jin
	*/
	<T extends ActionResponse> T executeGet(final ClusterCallback<T> callback);
	
	/** 
	* @Title: executeGet 
	* @Description: 执行一次获取客户端操作 
	* @param callback
	* @return T    
	* @throws 
	* @author Huatao Jin
	*/
	<T extends ActionResponse> T executeGet(final ClientCallback<T> callback);
	
	/** 
	* @Title: getIndexName 
	* @Description: 获取索引名称
	* @return String    
	* @throws 
	* @author Huatao Jin
	*/
	String getIndexName();
}

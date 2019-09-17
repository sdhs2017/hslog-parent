package com.hs.elsearch.template.bak;

import java.util.List;

/** 
* @ClassName: IndexSearchEngine 
*
* @Description: 一个通用索引搜索引擎接口
*
* @author Huatao Jin
* 
* @date 2016年3月18日 上午10:56:36 
* 
* @param <T> 搜索结果类型参数
*/
public interface IndexSearchEngine<T> {

	/** 
	* @Title: search 
	* @Description: 对于给定的一个搜索字符，返回搜索命中的结果
	* @param field	
	* 			搜索的字段
	* @param queryString
	* 			待搜索的字符
	* @param maxResults
	* 			搜索结果的最大条数
	* @return List<T>    
	* 			返回的结果集
	* @author Huatao Jin
	*/
	<Q> List<T> search(final String field, final Q queryString, int maxResults);
	
	
	
	/** 
	* @Title: search 
	* @Description:  对于给定的一个查询字符，返回搜索命中的结果
	* @param queryString
	* 			待搜索的字符
	* @param maxResults
	* 			搜索结果的最大条数
	* @return List<T>    
	* 			返回的结果集
	* @author Huatao Jin
	*/
	List<T> search(final String queryString, int maxResults);
}

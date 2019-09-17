package com.jz.bigdata.common.serviceInfo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jz.bigdata.common.serviceInfo.entity.ServiceInfo;

/**
 * @ClassName IServiceDao
 * @Description
 * @Author shi cheng yu
 * @Date 2019年4月23日 上午11:12:29
 */
public interface IServiceInfoDao {
	int insert(List<ServiceInfo> list);

	List<ServiceInfo> selectAll(ServiceInfo serviceInfo);

	int delete(String[] ids);

	int updateById(ServiceInfo serviceInfo);

	List<ServiceInfo> selectAllByPage(@Param("name")String name,@Param("ip")String ip,@Param("port")String port,@Param("protocol")String protocol,
			@Param("url")String url,@Param("relativeUrl")String relativeUrl,@Param("complementState")int complementState,@Param("state")int state,
			@Param("startRecord")int startRecord,@Param("pageSize")int pageSize);

	List<String> count(@Param("name")String name,@Param("ip")String ip,@Param("port")String port,@Param("protocol")String protocol,
			@Param("url")String url,@Param("relativeUrl")String relativeUrl,@Param("complementState")int complementState,@Param("state")int state);
	
	ServiceInfo selectServiceByUrl(@Param("url")String url);
	
	ServiceInfo selectServiceById(@Param("id")String id);

}

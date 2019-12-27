package com.jz.bigdata.common.serviceInfo.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jz.bigdata.common.serviceInfo.entity.ServiceInfo;



/**
 * @ClassName IServiceService
 * @Description
 * @Author shi cheng yu
 * @Date 2019年4月23日 上午10:13:11
 */
public interface IServiceInfoService {
	int insert(List<ServiceInfo> list);
	int insertIgnore(List<ServiceInfo> list);

	List<ServiceInfo> selectAll(ServiceInfo serviceInfo);

	int delete(String[] ids);

	int updateById(ServiceInfo serviceInfo);
	
	String selectAllByPage(String name,String ip,String port,String protocol,
			String url,String relativeUrl,int complementState,int state, int pageIndex, int pageSize);
	
	ServiceInfo selectServiceByUrl(String url);
	
	ServiceInfo selectServiceById(String id);

}

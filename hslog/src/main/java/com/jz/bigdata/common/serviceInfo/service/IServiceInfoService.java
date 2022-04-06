package com.jz.bigdata.common.serviceInfo.service;

import java.util.List;
import java.util.Map;

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
	Map<String,ServiceInfo> selectAll();

	int delete(String[] ids);

	int updateById(ServiceInfo serviceInfo);

	/**
	 * 查询服务列表数据
	 * @param name 服务名称
	 * @param ip ip地址
	 * @param port 端口
	 * @param protocol 协议
	 * @param url 地址
	 * @param relativeUrl 相对路径
	 * @param complementState 补全状态
	 * @param state 是否启用
	 * @param pageIndex 分页下标
	 * @param pageSize 每页条数
	 * @return
	 */
	String selectAllByPage(String name,String ip,String port,String protocol,
			String url,String relativeUrl,int complementState,int state, int pageIndex, int pageSize);
	
	ServiceInfo selectServiceByUrl(String url);
	
	ServiceInfo selectServiceById(String id);

}

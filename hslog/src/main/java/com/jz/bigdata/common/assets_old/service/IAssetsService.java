package com.jz.bigdata.common.assets_old.service;

import java.util.List;

import com.jz.bigdata.common.assets_old.entity.Assets;


public interface IAssetsService {
	int insert(Assets assets);

	List<Assets> selectAll();

	int updateById(Assets assets);

	int delete(String[] ids);
	
	String selectAllByPage(int pageIndex,int pageSize,Assets assets);
	
	//查询单个数据
	Assets selectOneAssets(Assets assets);
	
	//扫描增量数据 
	List<Assets> selectByIncrement();
	
	//修改状态
	int updateState(String id,String state,String upDateTime);


}

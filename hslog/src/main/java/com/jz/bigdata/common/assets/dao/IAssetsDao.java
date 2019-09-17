package com.jz.bigdata.common.assets.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jz.bigdata.common.assets.entity.Assets;


public interface IAssetsDao {
	int insert(Assets assets);

	List<Assets> selectAll();

	int updateById(Assets assets);

	int delete(String[] ids);
	
	List<Assets> selectAllByPage(@Param("startRecord")int startRecord,@Param("pageSize")int pageSize);

	List<String> count();
	
	Assets selectOneAssets(Assets assets);

	List<Assets> selectByIncrement(@Param("oldDate")String oldDate,@Param("date")String date);
	
	int updateState(@Param("id")String id,@Param("state")String state,@Param("responseDate")String responseDate);

}

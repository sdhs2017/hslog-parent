package com.jz.bigdata.common.asset.dao;

import java.util.List;

import com.jz.bigdata.common.asset.entity.Asset;
import org.apache.ibatis.annotations.Param;


/**
 * @description
 */
public interface IAssetDao {
	
	int insert(Asset asset);
	
	List<Asset> selectAll(Asset asset, @Param("role") String role, @Param("userId") String userId);
	
	int updateById(Asset asset);
	
	int delete(String[] ids);
	
	List<Asset> selectAsset(Asset asset);
	
	List<Asset> selectAllByPage(@Param("hostName") String hostName, @Param("name") String name, @Param("ip") String ip, @Param("logType") String logType, @Param("type") String type, @Param("role") String role, @Param("userId") String userId, @Param("startRecord") int startRecord, @Param("pageSize") int pageSize);

	List<String> count(@Param("hostName") String hostName, @Param("name") String name, @Param("ip") String ip, @Param("logType") String logType, @Param("type") String type, @Param("role") String role, @Param("userId") String userId);

	Asset selectOneAsset(Asset asset);
	
	List<Asset> selectAllHostName();
	
	int upRiskById(@Param("id") String id, @Param("high_risk") int high_risk, @Param("moderate_risk") int moderate_risk, @Param("low_risk") int low_risk);
	
	List<Asset> selectAllAssetByRisk();
	
	List<Object> count_Number();

	Asset selectByNameIp(Asset asset);
	
	int batchUpdate(List<Asset> list);

}

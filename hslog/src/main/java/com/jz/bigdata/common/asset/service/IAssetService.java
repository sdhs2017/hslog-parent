package com.jz.bigdata.common.asset.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import com.jz.bigdata.common.asset.entity.Asset;

/**
 * @description
 */
public interface IAssetService {

	int insert(Asset asset, HttpSession session);
	
	String selectAll(Asset asset, HttpSession session);
	
	int updateById(Asset asset, HttpSession session);
	
	int delete(String[] ids);
	
	List<Asset> selectAsset(Asset asset) throws Exception;
	
	String selectAllByPage(String hostName, String name, String ip, String logType, String type, int pageIndex, int pageSize, HttpSession session) throws Exception;

	Asset selectOneAsset(Asset asset);
	
	List<Asset> selectAllHostName();

	Set<String> selectAllIPAdress();

	Map<String, Asset> selectAllAsset();

	Map<String, String> selectLog_level();
	
	int upRiskById(String id, int high_risk, int moderate_risk, int low_risk);
	
	List<Asset> selectAllAssetByRisk();
	
	int batchUpdate(List<Asset> list);
	
}

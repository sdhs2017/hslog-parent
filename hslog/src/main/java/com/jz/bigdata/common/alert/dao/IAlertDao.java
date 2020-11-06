package com.jz.bigdata.common.alert.dao;

import com.jz.bigdata.common.alert.entity.Alert;
import com.jz.bigdata.common.assetGroup.entity.AssetGroup;
import com.jz.bigdata.common.assetGroup.entity.AssetGroupRelations;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * @description
 */
public interface IAlertDao {
	/**
	 * 添加资产组
	 * @param assetGroup
	 * @return
	 */
	int insert(Alert alert);

	/**
	 * 获取告警信息count
	 * @param alert
	 * @return
	 */
	List<List<Map<String,String>>> getAlertInfoCountByCondition(Alert alert);

	/**
	 * 根据分页获取告警信息
	 * @param alert
	 * @return
	 */
	List<Alert> getAlertInfoByCondition(Alert alert);
	/**
	 * 删除告警信息
	 * @param alert_id
	 * @return
	 */
	int deleteAlert(@Param("alert_id")String alert_id);
	/**
	 * 更新资产组信息
	 * @param alert
	 * @return
	 */
	int update(Alert alert);
	/**
	 * 通过告警id获取告警组信息
	 * @param alert_id
	 * @return
	 */
	Alert getAlertInfoById(@Param("alert_id")String alert_id);

	/**
	 * 更新计划任务执行状态
	 * @param alert_id
	 * @param alert_state
	 * @return
	 */
	int updateAlertState(@Param("alert_id")String alert_id,@Param("alert_state")String alert_state);
}

package com.jz.bigdata.common.manage.service;

import org.elasticsearch.snapshots.SnapshotInfo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IManageService {

	/**
	 * 获取服务器的磁盘使用情况
	 * @param user
	 * @param passwd
	 * @param host
	 * @return
	 */
	public Map<String, String> getDiskUsage(String user,String passwd,String host) ;

	/**
	 * 执行curl命令
	 * @param type
	 * @param url
	 * @return
	 */
	public String doCurl(String type,String url) ;

	/**
	 * 通过认证用户名和密码执行curl命令
	 * @param url
	 * @param host_user
	 * @param host_passwd
	 * @param host_ip
	 * @return
	 */
	public Map<String, String> doshell(String url, String host_user, String host_passwd, String host_ip);

	/**
	 * 直接在本地执行脚本
	 * @param shell
	 * @param filepath
	 * @return
	 */
	public Map<String, String> doshell(String shell,String filepath);
	/**
	 * 定时任务--对昨天的index segments进行合并操作
	 * @return
	 */
	public String indexForceMerge();
	/**
	 * Event
	 * @return
	 */
	public String updateRisk();
	/**
	 * 关闭索引
	 * @param indices 索引名称
	 * @return
	 *
	 */
	public boolean closeIndex(String...indices)  ;

	/**
	 * 开启索引
	 * @param indices 索引名称
	 * @return
	 *
	 */
	public boolean openIndex(String... indices) ;
	/*
	 * 创建快照备份策略
	 * @param indices 要进行快照备份的索引名称
	 * @param policy_id 策略id
	 * @param name 快照名称
	 * @param schedule 定时器，cron表达式
	 * @param repository 备份仓库名称
	 * @return
	 */
	public boolean createSLMPolicy(String indices,String policy_id,String name,String schedule,String repository);

	/**
	 * 执行快照策略，
	 * 返回执行后的第一个快照名称
	 * @param policy_id 快照策略ID
	 */
	public boolean executeSLMPolicy(String policy_id) ;

	/**
	 * 根据策略id删除快照策略
	 * @param policy_id 快照策略ID
	 * @return
	 */
	public boolean deleteSLMPolicy(String policy_id) ;
	/**
	 * 根据存储仓库获取所有快照的列表
	 * @param repositoryName 存储仓库名称
	 * @return 快照列表
	 */
	public List<Map<String,String>> getSnapListByPolicyId(String repositoryName);

	/**
	 * 还原快照
	 * @param repositoryName 存储仓库名称
	 * @param snapshotName 快照名称
	 * @return
	 */
	public boolean restoreSnapshot(String repositoryName,String snapshotName);
}

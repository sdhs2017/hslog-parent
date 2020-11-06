package com.jz.bigdata.common.manage.service;

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
}

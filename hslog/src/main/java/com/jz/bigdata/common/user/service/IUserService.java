package com.jz.bigdata.common.user.service;

import javax.servlet.http.HttpSession;

import com.jz.bigdata.common.user.entity.User;

/**
 * @author yiyang
 * @date 2016年7月28日 上午11:47:47
 * @description 
 */
public interface IUserService {

	public Boolean login(User user,HttpSession session);
	
	public Boolean checkLogin(HttpSession session);
	
	public Boolean loginOut(HttpSession session);
}

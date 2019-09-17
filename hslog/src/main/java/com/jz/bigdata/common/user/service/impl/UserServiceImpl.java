package com.jz.bigdata.common.user.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.user.dao.IUserDao;
import com.jz.bigdata.common.user.entity.User;
import com.jz.bigdata.common.user.service.IUserService;

/**
 * @author yiyang
 * @date 2016年7月28日 上午11:49:33
 * @description 
 */
@Service(value="UserService")
public class UserServiceImpl implements IUserService{
	
	@Resource
	private IUserDao userDao;
	
	
	/**
	 * @param user
	 * @param session
	 * @return 是否登陆成功 true/false
	 * @description 登陆操作
	 */
	public Boolean login(User user,HttpSession session){
		//查询账号密码对应的用户信息
		List<User> _userList = this.userDao.selectByNamePwd(user);
		if(_userList.size()==1){
			User _user = _userList.get(0);
			if(_user.getUserid()!=null){
				session.setAttribute(Constant.SESSION_USERID, _user.getUserid());
				session.setAttribute(Constant.SESSION_ID, session.getId());
				return true;
			}
		}
		return false;
	}

	/**
	 * @param session
	 * @return
	 * @description 验证session 信息
	 */
	public Boolean checkLogin(HttpSession session) {
		if(session.getAttribute(Constant.SESSION_USERID)!=null&&session.getId().equals(session.getAttribute(Constant.SESSION_ID))){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * @param session
	 * @return
	 * @description 登陆，清空session
	 */
	public Boolean loginOut(HttpSession session) {
		session.removeAttribute(Constant.SESSION_ID);
		session.removeAttribute(Constant.SESSION_USERID);
		return true;
	}
	
}

package com.jz.bigdata.roleauthority.user.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.annotations.Param;

import com.jz.bigdata.roleauthority.user.entity.User;
import com.jz.bigdata.roleauthority.user.util.Page;

import net.sf.json.JSONObject;

/**
 * @author shichengyu
 * @date 2017年8月1日 上午10:06:12
 * @description
 */
public interface IUserService {
	 int insert(User user);
	 
	 List<User> selectAll(User user);
	 
	 int delete(String[] ids);
	 
	 int updateById(User user);
	 
	 Map<String,Object> selectPage(Page page, HttpSession session);
	 
	 List<User> selectUser(String id);
	 
	 User selectById(String id);
	 
	 String login(User user, HttpSession session) throws Exception;
	 
	 public Boolean checkLogin(HttpSession session);
	 
	 public Boolean loginOut(HttpSession session);
	 
	 String selectUserRole(HttpSession session);
	 
	 int registerUser(User user);
	 
	 List<User> selectByName(User user);
	 
	 String updatePasswordById(String id, String password, String oldPassword);

	/**
	 * 通过用户id重置密码
	 * @param id
	 * @return 影响条数
	 */
	 public String resetPasswordById(String id);
}

package com.jz.bigdata.common.users.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jz.bigdata.common.users.entity.User;
import com.jz.bigdata.common.users.util.Page;

/**
 * @author shichengyu
 * @date 2017年8月1日 上午10:08:39
 * @description
 */
public interface IUsersDao {
	
	 int insert(User user);
	 
	 List<User> selectAll(User user);
	 
	 int delete(String[] ids);
	 
	 int updateById(User user);
	 
	 List<User> selectPage(Page page);
	 
	 List<String> count(Page page);
	 
	 List<User> selectUser(String id);
	 
	 User selectById(String id);
	 
	 List<User> selectByPhonePwd(User user);
	 
	 int updateByPhone(String phone);
	 
	 List<User> selectUserRole(String id);
	 
	 int registerUser(User user);
	 
	 List<User> selectByName(User user);
	 
	 int updatePasswordById(@Param("id")String id,@Param("password")String password);
	 
	 List<User> selectByPasswordId(@Param("id")String id,@Param("password")String password);
	 
	 

}

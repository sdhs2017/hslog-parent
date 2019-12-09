package com.jz.bigdata.roleauthority.user.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.jz.bigdata.roleauthority.user.entity.User;
import com.jz.bigdata.roleauthority.user.util.Page;

/**
 * @author shichengyu
 * @date 2017年8月1日 上午10:08:39
 * @description
 */
public interface IUserDao {
	
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

	int insertVisitor(@Param("userID")String userID,@Param("roleID")String roleID );

	 List<User> selectByName(User user);
	 
	 int updatePasswordById(@Param("id") String id, @Param("password") String password);
	 
	 List<User> selectByPasswordId(@Param("id") String id, @Param("password") String password);
	 
	 int insertUserRole(@Param("userID")String userID,@Param("roleID")String roleID);

	 int deleteUserRole(@Param("userID")String userID);

	List<List<Map<String,Object>>> selectRoleByUserID(@Param("userID")String userID);
}

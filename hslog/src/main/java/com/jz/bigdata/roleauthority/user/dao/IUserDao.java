package com.jz.bigdata.roleauthority.user.dao;

import java.util.List;
import java.util.Map;

import com.jz.bigdata.roleauthority.menu.entity.Button;
import com.jz.bigdata.roleauthority.menu.entity.Menu;
import com.jz.bigdata.roleauthority.role.entity.Role;
import com.jz.bigdata.roleauthority.system.entity.System;
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

	User selectByPhonePwd(User user);

	User selectByPhone(User user);

	 int updateByPhone(String phone);

	List<List<Map<String,Object>>> selectUserRole(String id);

	int registerUser(User user);

	int insertVisitor(@Param("userID")String userID,@Param("roleID")String roleID );

	 List<User> selectByName(User user);
	 
	 int updatePasswordById(@Param("id") String id, @Param("password") String password);
	 
	 List<User> selectByPasswordId(@Param("id") String id, @Param("password") String password);
	 
	 int insertUserRole(@Param("userID")String userID,@Param("roleID")String roleID);

	 int deleteUserRole(@Param("userID")String userID);

	List<List<Map<String,Object>>> selectRoleByUserID(@Param("userID")String userID);

	/**
	 * 批量提交菜单信息
	 * @return
	 */
	int insertMenuBatch(List<Menu> list);
	/**
	 * 批量提交菜单信息
	 * @return
	 */
	int insertSystemBatch(List<System> list);
	/**
	 * 批量提交菜单信息
	 * @return
	 */
	int insertButtonBatch(List<Button> list);
	/**
	 * 批量提交角色信息
	 * @return
	 */
	int insertIgnoreRoleBatch(List<Role> list);

	/**
	 * 删除菜单相关的表，系统、菜单、按钮
	 * @return
	 */
	int deleteButtonMenuSystem();

	/**
	 * 删除不存在的系统、菜单、按钮所在的 角色菜单关系表
	 * @return
	 */
	int deleteNotExistRoleMenus();

	/**
	 * 插入角色 菜单中间表，带ignore，插入失败忽略
	 * @param menuAndButtonID
	 * @param fk_roleid
	 * @return
	 */
	int insertIgnoreMenuButton(@Param("menuAndButtonID")String menuAndButtonID,@Param("fk_roleid")String fk_roleid);

}

package com.jz.bigdata.roleauthority.role.dao;

import java.util.List;

import com.jz.bigdata.roleauthority.role.entity.Role;
import com.jz.bigdata.roleauthority.system.entity.System;


/**
 * @author shichengyu
 * @date 2018年5月10日 下午5:55:35
 * @description
 */
public interface IRoleDao {

	List<Role> selectAllRole();
	int insert(Role role);
	int update(Role role);
	int delete(String id);
	Role getEntity(String id);
}

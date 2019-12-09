package com.jz.bigdata.roleauthority.role.service;

import com.jz.bigdata.roleauthority.role.entity.Role;

import java.util.List;

/**
 * @author shichengyu
 * @date 2018年5月10日 下午5:54:43
 * @description
 */
public interface IRoleService {
	List<Role> selectAllRole();
	Boolean insert(Role role);
	Boolean update(Role role);
	Boolean delete(String id);
	Role getEntity(String id);
}

package com.jz.bigdata.roleauthority.role.service.impl;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import com.jz.bigdata.roleauthority.role.dao.IRoleDao;
import com.jz.bigdata.roleauthority.role.entity.Role;
import com.jz.bigdata.roleauthority.role.service.IRoleService;
import com.jz.bigdata.roleauthority.system.dao.ISystemDao;
import com.jz.bigdata.roleauthority.system.entity.System;
import com.jz.bigdata.roleauthority.system.service.ISystemService;
import org.springframework.stereotype.Service;



/**
 * @author shichengyu
 * @date 2018年5月10日 下午5:55:19
 * @description
 */
@Service(value = "RoleService")
public class RoleServiceImpl implements IRoleService {
	

	
	@Resource
	private IRoleDao roleDao;


	@Override
	public List<Role> selectAllRole() {
		return roleDao.selectAllRole();
	}

	@Override
	public Boolean insert(Role role) {
		role.setId(UUID.randomUUID().toString());
		return roleDao.insert(role)==1?true:false;
	}

	@Override
	public Boolean update(Role role) {
		return roleDao.update(role)==1?true:false;
	}

	@Override
	public Boolean delete(String id) {
		return roleDao.delete(id)==1?true:false;
	}

	@Override
	public Role getEntity(String id) {
		return roleDao.getEntity(id);
	}
}

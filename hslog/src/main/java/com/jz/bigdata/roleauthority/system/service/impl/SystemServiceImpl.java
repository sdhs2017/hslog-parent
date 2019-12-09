package com.jz.bigdata.roleauthority.system.service.impl;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import com.jz.bigdata.roleauthority.system.dao.ISystemDao;
import com.jz.bigdata.roleauthority.system.entity.System;
import com.jz.bigdata.roleauthority.system.service.ISystemService;
import org.springframework.stereotype.Service;



/**
 * @author shichengyu
 * @date 2018年5月10日 下午5:55:19
 * @description
 */
@Service(value = "SystemService")
public class SystemServiceImpl implements ISystemService {
	

	
	@Resource
	private ISystemDao systemDao;


	@Override
	public List<System> selectAllSystem() {
		return systemDao.selectAllSystem();
	}

	@Override
	public Boolean insert(System sys) {
		sys.setId(UUID.randomUUID().toString());
		return systemDao.insert(sys)==1?true:false;
	}

	@Override
	public Boolean update(System sys) {
		return systemDao.update(sys)==1?true:false;
	}

	@Override
	public Boolean delete(String id) {
		return systemDao.delete(id)==1?true:false;
	}

	@Override
	public System getEntity(String id) {
		return systemDao.getEntity(id);
	}
}

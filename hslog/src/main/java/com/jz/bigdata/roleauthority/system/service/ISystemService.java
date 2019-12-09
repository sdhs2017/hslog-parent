package com.jz.bigdata.roleauthority.system.service;

import com.jz.bigdata.roleauthority.system.entity.System;

import java.util.List;

/**
 * @author shichengyu
 * @date 2018年5月10日 下午5:54:43
 * @description
 */
public interface ISystemService {
	List<System> selectAllSystem();
	Boolean insert(System sys);
	Boolean update(System sys);
	Boolean delete(String id);
	System getEntity(String id);
}

package com.jz.bigdata.roleauthority.system.dao;

import java.util.List;
import com.jz.bigdata.roleauthority.system.entity.System;


/**
 * @author shichengyu
 * @date 2018年5月10日 下午5:55:35
 * @description
 */
public interface ISystemDao {

	List<System> selectAllSystem();
	int insert(System sys);
	int update(System sys);
	int delete(String id);
	System getEntity(String id);
}

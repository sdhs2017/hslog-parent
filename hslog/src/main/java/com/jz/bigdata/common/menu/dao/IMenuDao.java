package com.jz.bigdata.common.menu.dao;

import java.util.List;

import com.jz.bigdata.common.menu.entity.Menu;

/**
 * @author shichengyu
 * @date 2018年5月10日 下午5:55:35
 * @description
 */
public interface IMenuDao {
	
	List<Menu> selectAll(int role);

}

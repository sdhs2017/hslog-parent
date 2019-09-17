package com.jz.bigdata.common.menu.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.jz.bigdata.common.menu.dao.IMenuDao;
import com.jz.bigdata.common.menu.entity.Menu;
import com.jz.bigdata.common.menu.service.IMenuService;
import com.jz.bigdata.common.menu.util.TreeBuilder;

/**
 * @author shichengyu
 * @date 2018年5月10日 下午5:55:19
 * @description
 */
@Service(value = "MenuService")
public class MenuServiceImpl implements IMenuService {
	
	static Logger logger = Logger.getLogger(MenuServiceImpl.class); 
	
	@Resource
	private IMenuDao menuDao;

	/**
	 * @return
	 * @description
	 * 查询菜单栏
	 */
	@Override
	public String selectAll(int role) {
		List<Menu> list =menuDao.selectAll(role);
//		String treeList=TreeBuilder.
		TreeBuilder treeBuilder = new TreeBuilder(list);
//		list转json输
		String menuTree=treeBuilder.buildJSONTree();
//		System.err.println(menuTree);
		
		return menuTree;
	}

}

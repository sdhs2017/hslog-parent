package com.jz.bigdata.roleauthority.menu.util;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.alibaba.fastjson.JSONArray;
import com.jz.bigdata.roleauthority.menu.entity.Menu;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;

/**
 * @author shichengyu
 * @date 2017年8月9日 上午9:39:45
 * @description 拼接json樹
 */

/**
 * TODO
 * JDK8新特性：构建tree
 */
public class TreeBuilder {
	// 获取list集合信息
	List<Menu> menuList = new ArrayList<Menu>();

	// 构造函数
	public TreeBuilder(List<Menu> list)  {
		super();
		try{
			for(int i=0;i<list.size();i++){
				Menu menu = new Menu();
				BeanUtils.copyProperties(menu,list.get(i));
				this.menuList.add(menu);
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * @return
	 *
	 * 		list转json 构建树形结构
	 */
	public String buildJSONTree() {
		// 获取list集合
		List<Menu> menuTree = buildTree();
		// String jsonArray = JSONArray.toJSONString(nodeTree);
		return JSONArray.toJSONString(menuTree);
	}

	/**
	 * @return 构建树形结构方法
	 */
	public List<Menu> buildTree() {
		List<Menu> treeNodes = new ArrayList<Menu>();
		// 获取所有的根节点
		List<Menu> rootNodes = getRootNodes();
		// 遍历根节点
		for (Menu rootNode : rootNodes) {
			// 获取根节点下所有节点信息
			buildChildNodes(rootNode);
			// 添加到list集合中
			treeNodes.add(rootNode);
		}
		return treeNodes;
	}

	/**
	 * @return 获取所有的根节点信息
	 */
	public List<Menu> getRootNodes() {
		List<Menu> rootNodes = new ArrayList<Menu>();
		for (Menu menu : menuList) {

			// 判断是否是根节点
			if (rootNode(menu)) {
				// 根节点添加到list中
				rootNodes.add(menu);
			}
		}
		List<Menu> rootNo = new ArrayList<Menu>();
		for (Menu menu : rootNodes) {
			// 判断是否是根节点是否删除了根节点
			if (menu.getSuperiorId()==null||menu.getSuperiorId().isEmpty()) {
				// 根节点添加到list中
				rootNo.add(menu);
			}
		}
		//对list进行排序
		Collections.sort(rootNo, new Comparator<Menu>() {
			@Override
			public int compare(Menu arg0, Menu arg1) {
				int diff = arg0.getChildId() - arg1.getChildId();
				if (diff > 0) {
					return 1;
				} else if (diff < 0) {
					return -1;
				}
				return 0; // 相等为0
			}
		});
		System.out.println(rootNo.toString());
		return rootNo;
	}

	/**
	 * @param
	 * @return 循环遍历是否是根节点
	 */
	public boolean rootNode(Menu menu) {
		boolean isRootNode = true;
		// 循环判断是否是根节点
		if (menu.getSuperiorId() != null) {
			for (Menu depart : menuList) {
				if (menu.getSuperiorId().equals(depart.getId()) ) {
					isRootNode = false;
					break;
				}
			}

		}

		return isRootNode;
	}

	/**
	 *
	 * 获取子菜单添加到集合中
	 */
	public void buildChildNodes(Menu menu) {
		List<Menu> children = getChildNodes(menu);
		// 所有的数据添加到list
		if (!children.isEmpty()) {
			for (Menu child : children) {
				buildChildNodes(child);
			}

			menu.setMenus(children);
		}
	}

	/**
	 * @param
	 * @return 获取父节点下的子节点
	 */
	public List<Menu> getChildNodes(Menu menu) {
		List<Menu> childNodes = new ArrayList<Menu>();
		// 循环遍历获取父节点下的子节点
		for (Menu depart : menuList) {
			if (depart.getSuperiorId() != null) {
				if (menu.getId().equals(depart.getSuperiorId()) ) {
					childNodes.add(depart);
				}
			}
		}
		//对list进行排序
//		Collections.sort(childNodes, new Comparator<Menu>() {
//			public int compare(Menu arg0, Menu arg1) {
//				return arg0.getChildId() + "".compareTo(arg1.getChildId() + "");
//			}
//		});
		Collections.sort(childNodes, new Comparator<Menu>() {
			@Override
			public int compare(Menu arg0, Menu arg1) {
				int diff = arg0.getChildId() - arg1.getChildId();
				if (diff > 0) {
					return 1;
				} else if (diff < 0) {
					return -1;
				}
				return 0; // 相等为0
			}
		});

		return childNodes;
	}

}

package com.jz.bigdata.roleauthority.menu.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.jz.bigdata.roleauthority.menu.entity.Button;
import com.jz.bigdata.util.MapUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.jz.bigdata.roleauthority.menu.dao.IMenuDao;
import com.jz.bigdata.roleauthority.menu.entity.Menu;
import com.jz.bigdata.roleauthority.menu.service.IMenuService;
import com.jz.bigdata.roleauthority.menu.util.TreeBuilder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
	 * 获取该用户下的某特定系统的菜单
	 * @param userID
	 * @param systemID
	 * @return
	 */
	@Override
	public String selectMenu(String userID, String systemID) {
		List<List<Menu>> list =menuDao.selectMenu(userID,systemID);
		TreeBuilder treeBuilder = new TreeBuilder(list.get(0));
		String menuTree=treeBuilder.buildJSONTree();
		return menuTree;
	}

	/**
	 * 获取用户可访问的系统列表
	 * @param userID
	 * @return
	 */
	@Override
	public String selectSystem(String userID) {
		List<Menu> list =menuDao.selectSystem(userID);
		TreeBuilder treeBuilder = new TreeBuilder(list);
		String menuTree=treeBuilder.buildJSONTree();
		return menuTree;
	}

	/**
	 * 添加菜单
	 * @param menu
	 * @return
	 */
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
	public Boolean insert(Menu menu,String btn){
		//如果该节点信息中有父节点信息，检验该父节点信息是否存在
		if(menu.getSuperiorId()!=null&&!"".equals(menu.getSuperiorId())){
			List<List<Map<String,Object>>> i = menuDao.selectExistParentMenuById(menu.getSuperiorId());
			if("0".equals(i.get(0).get(0).get("num"))){
				return false;
			}
		}
		menu.setId(UUID.randomUUID().toString());
		//添加按钮
		if(btn!=null&&!btn.isEmpty()){//按钮参数为空，不进行处理
			List<Map<String, Object>> list = MapUtil.json2ListMap(btn);
			for(Map<String, Object> m :list){
				String name = m.get("name").toString();
				String id = m.get("id").toString();
				menuDao.insertButton(name,id,menu.getId());
			}
		}
		//添加菜单
		menuDao.insert(menu);
		return true;
	}
	/**
	 * 更新菜单
	 * @param menu
	 * @return
	 */
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
	public Boolean update(Menu menu,String btn){
		//如果该节点信息中有父节点信息，检验该父节点信息是否存在
		if(menu.getSuperiorId()!=null&&!"".equals(menu.getSuperiorId())){
			List<List<Map<String,Object>>> i = menuDao.selectExistParentMenuById(menu.getSuperiorId());
			if("0".equals(i.get(0).get(0).get("num"))){
				return false;
			}
		}
		//变更按钮
		if(btn!=null&&!btn.isEmpty()){//按钮参数为空，不进行处理
			//删除已有的按钮
			menuDao.deleteButtonByMenuID(menu.getId());
			List<Map<String, Object>> list = MapUtil.json2ListMap(btn);
			for(Map<String, Object> m :list){
				String name = m.get("name").toString();
				String id = m.get("id").toString();
				menuDao.insertButton(name,id,menu.getId());
			}
		}
		menuDao.update(menu);
		return true;
	}
	/**
	 * 配置菜单及按钮信息
	 * @param
	 * @return
	 */
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
	public Boolean upsertMenuButton(String roleID, String ids){

		//删除已有的配置信息
		menuDao.deleteMenuButtonByRoleID(roleID);
		for(String id:ids.split(",")){
			if(!"".equals(id)){
				menuDao.insertMenuButton(id,roleID);
			}
		}
		return true;
	}

	@Override
	public List<Button> selectButtonListByUser(String userID) {
		return menuDao.selectButtonListByUser(userID);
	}

	/**
	 * 删除菜单
	 * @param id
	 * @return
	 */
	public Boolean delete(String id){
		return menuDao.delete(id)==1?true:false;
	}

	@Override
	public List<Menu> selectMenuBySysID(String systemID) {
		return menuDao.selectMenuBySysID(systemID);
	}

	@Override
	public Menu getEntity(String id) {
		Menu m = menuDao.getEntity(id);
		List<Map<String,Object>> list = menuDao.selectButtonByMenuID(m.getId());
		m.setBtn(JSON.toJSONString(list));
		return m;
	}

	@Override
	public String selectSystemMenu() {
		List<List<Menu>> list =menuDao.selectSystemMenu();
		TreeBuilder treeBuilder = new TreeBuilder(list.get(0));
		String menuTree=treeBuilder.buildJSONTree();
		return menuTree;
	}

	@Override
	public String selectSystemMenuByIDs(List<String> ids) {
		List<List<Menu>> list =menuDao.selectSystemMenuByIDs(ids);
		TreeBuilder treeBuilder = new TreeBuilder(list.get(0));
		String menuTree=treeBuilder.buildJSONTree();
		return menuTree;
	}


}

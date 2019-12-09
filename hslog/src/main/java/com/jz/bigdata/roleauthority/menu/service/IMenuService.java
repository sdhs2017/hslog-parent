package com.jz.bigdata.roleauthority.menu.service;

import com.jz.bigdata.roleauthority.menu.entity.Button;
import com.jz.bigdata.roleauthority.menu.entity.Menu;

import java.util.List;

/**
 * @author shichengyu
 * @date 2018年5月10日 下午5:54:43
 * @description
 */
public interface IMenuService {
	String selectMenu(String userID,String systemID);
	String selectSystem(String userID);
	Boolean insert(Menu menu,String btn);
	Boolean update(Menu menu,String btn);
	Boolean delete(String id);
	List<Menu> selectMenuBySysID(String systemID);
	Menu getEntity(String id);
	String selectSystemMenu();
	String selectSystemMenuByIDs(List<String> ids);
	Boolean upsertMenuButton(String roleID,String ids);
	List<Button> selectButtonListByUser(String userID);
}

package com.jz.bigdata.roleauthority.menu.dao;

import java.util.List;
import java.util.Map;

import com.jz.bigdata.roleauthority.menu.entity.Button;
import com.jz.bigdata.roleauthority.menu.entity.Menu;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.ibatis.annotations.Param;

/**
 * @author shichengyu
 * @date 2018年5月10日 下午5:55:35
 * @description
 */
public interface IMenuDao {

	List<List<Menu>> selectMenu(@Param("userID")String userID, @Param("systemID")String systemID);

	List<Menu> selectSystem(@Param("userID")String userID);

	List<Menu> selectAllSystem();

	int insert(Menu menu);

	int update(Menu menu);

	int delete(String id);

	Menu getEntity(String id);

	List<Menu> selectMenuBySysID(String systemID);

	int insertButton(@Param("name")String name,@Param("id")String id,@Param("menuid")String menuid);

	int deleteButtonByMenuID(@Param("menuid")String menuid);

	List<Map<String,Object>> selectButtonByMenuID(@Param("pk_menu_id")String pk_menu_id);

	List<List<Menu>> selectSystemMenu();

	List<List<Menu>> selectSystemMenuByIDs(@Param("ids")List<String> ids);

	int insertMenuButton(@Param("menuAndButtonID")String menuAndButtonID,@Param("fk_roleid")String fk_roleid);
	int deleteMenuButtonByRoleID(@Param("fk_roleid")String fk_roleid);
	List<Button> selectButtonListByUser(@Param("userID")String userID);
}

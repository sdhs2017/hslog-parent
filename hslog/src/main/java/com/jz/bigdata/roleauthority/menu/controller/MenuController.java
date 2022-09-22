package com.jz.bigdata.roleauthority.menu.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jz.bigdata.roleauthority.menu.entity.Button;
import com.jz.bigdata.roleauthority.menu.entity.Menu;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.roleauthority.menu.service.IMenuService;
import com.jz.bigdata.util.DescribeLog;

import java.util.*;

/**
 * @author shichengyu
 * @date 2018年5月10日 下午5:54:09
 * @description
 */
@Slf4j
@Controller
@RequestMapping("/menu")
public class MenuController {
	@Resource(name="MenuService")
	private IMenuService menuService;
	
	/**
	 * @return
	 * 查询所有数据树结构
	 */
	@ResponseBody
	@RequestMapping(value="/selectMenu",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取菜单栏-用户页面菜单加载")
	public String selectMenu(HttpServletRequest request){
		try{
			HttpSession session = request.getSession();
			String userID=(String) session.getAttribute(Constant.SESSION_USERID);

			String systemID = request.getParameter("systemID");
			return menuService.selectMenu(userID,systemID);
		}catch(Exception e){
			log.error(""+e.getMessage());
			return Constant.failureMessage("用户页面菜单加载失败！");
		}

	}
	@ResponseBody
	@RequestMapping(value="/selectSystemMenu",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取系统及菜单tree")
	public String selectSystemMenu(HttpServletRequest request){
		try{
			//获取角色id
			String roleid = request.getParameter("roleid");
			List<String> menuIds = menuService.selectMenuIdsByRoleId(roleid);

			List<Menu> systemMenu = menuService.selectSystemMenu();
			Map<String,Object> result = new HashMap<>();
			result.put("systemMenu",systemMenu);
			result.put("menuButtonIds",menuIds);
			return JSONObject.toJSON(result).toString();
		}catch(Exception e){
			log.error(""+e.getMessage());
			return Constant.failureMessage("获取系统及菜单tree失败！");
		}

	}
	@ResponseBody
	@RequestMapping(value="/selectMenuBySysID",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="根据系统ID获取菜单列表")
	public List<Menu> selectMenuBySysID(HttpServletRequest request){
		try{
			String systemID = request.getParameter("systemID");
			return menuService.selectMenuBySysID(systemID);
		}catch(Exception e){
			log.error(""+e.getMessage());
			return new ArrayList<>();
		}

	}
	@ResponseBody
	@RequestMapping(value="/selectSystem",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取用户所属系统")
	public String selectSystem(HttpServletRequest request){
		try{
			HttpSession session = request.getSession();
			String userID=(String) session.getAttribute(Constant.SESSION_USERID);
			return menuService.selectSystem(userID);
		}catch(Exception e){
			log.error(""+e.getMessage());
			return Constant.failureMessage("获取用户所属系统失败！");
		}

	}
	@ResponseBody
	@RequestMapping(value="/upsert",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="增改菜单")
	public String upsert(Menu menu,HttpServletRequest request){
		try{
			String btn = request.getParameter("btn");
			if(menu.getId()==null||menu.getId()==""){
				return menuService.insert(menu,btn)?Constant.successMessage():Constant.failureMessage("父级菜单Id不正确");
			}else{
				return menuService.update(menu,btn)?Constant.successMessage():Constant.failureMessage("父级菜单Id不正确");
			}
		}catch(Exception e){
			log.error(""+e.getMessage());
			return Constant.failureMessage("增改菜单失败！");
		}

	}
	@ResponseBody
	@RequestMapping(value="/delete",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="删除菜单")
	public String delete(HttpServletRequest request){
		try{
			String id = request.getParameter("id");
			if(id==null||id==""){
				return Constant.failureMessage();
			}else{
				return menuService.delete(id)?Constant.successMessage():Constant.failureMessage();
			}
		}catch(Exception e){
			log.error(""+e.getMessage());
			return Constant.failureMessage("删除菜单失败！");
		}

	}
	@ResponseBody
	@RequestMapping(value="/getEntity",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取菜单信息")
	public Menu getEntity(HttpServletRequest request){
		try{
			String id = request.getParameter("id");
			if(id==null||id==""){
				return new Menu();
			}else{
				return menuService.getEntity(id);
			}
		}catch(Exception e){
			log.error(""+e.getMessage());
			return new Menu();
		}

	}
	@ResponseBody
	@RequestMapping(value="/selectSystemMenuByIDs",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取菜单按钮信息")
	public String selectSystemMenuByIDs(HttpServletRequest request){
		try{
			String ids = request.getParameter("ids");
			String roleid = request.getParameter("roleid");
			List<String> idList = Arrays.asList(ids.split(","));
			List<String> buttonIds = menuService.selectButtonIdsByRoleId(roleid);
			List<Menu> menuList = menuService.selectSystemMenuByIDs(idList);
			Map<String,Object> result = new HashMap<>();
			result.put("systemMenu",menuList);
			result.put("menuButtonIds",buttonIds);
			return JSONObject.toJSON(result).toString();
		}catch(Exception e){
			log.error(""+e.getMessage());
			return Constant.failureMessage("获取菜单按钮信息失败！");
		}

	}
	@ResponseBody
	@RequestMapping(value="/upsertMenuButton",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="配置角色的菜单及按钮信息")
	public String upsertMenuButton(HttpServletRequest request){
		try{
			String roleID =  request.getParameter("roleID");
			String buttonIds =  request.getParameter("buttonIds");//按钮
			String menuIds = request.getParameter("menuIds");//菜单
			//将两个id合并，取并集
			for(String bid:buttonIds.split(",")){
				//如果buttonid与菜单id中没有重复
				if(!(menuIds.indexOf(bid)>=0)){
					menuIds +=","+bid;
				}
			}
			return menuService.upsertMenuButton(roleID,menuIds)?Constant.successMessage():Constant.failureMessage();
		}catch(Exception e){
			log.error(""+e.getMessage());
			return Constant.failureMessage("配置角色的菜单及按钮信息失败！");
		}

	}
	@ResponseBody
	@RequestMapping(value="/selectButtonListByUser",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取用户的所有按钮配置信息")
	public List<Button> selectButtonListByUser(HttpServletRequest request){
		try{
			HttpSession session = request.getSession();
			String userID=(String) session.getAttribute(Constant.SESSION_USERID);
			return menuService.selectButtonListByUser(userID);
		}catch(Exception e){
			log.error(""+e.getMessage());
			return new ArrayList<>();
		}

	}

}

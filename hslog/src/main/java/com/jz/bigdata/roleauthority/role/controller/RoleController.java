package com.jz.bigdata.roleauthority.role.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.roleauthority.role.entity.Role;
import com.jz.bigdata.roleauthority.role.service.IRoleService;
import com.jz.bigdata.roleauthority.system.entity.System;
import com.jz.bigdata.roleauthority.system.service.ISystemService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.util.DescribeLog;

import java.util.List;

/**
 * @author shichengyu
 * @date 2018年5月10日 下午5:54:09
 * @description
 */
@Controller
@RequestMapping("/role")
public class RoleController {
	
	@Resource(name="RoleService")
	private IRoleService roleService;
	

	@ResponseBody
	@RequestMapping(value="/selectAllRole",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取所有角色")
	public List<Role> selectAllRole(HttpServletRequest request){
		return roleService.selectAllRole();
	}

	@ResponseBody
	@RequestMapping(value="/upsert",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="增改角色信息")
	public String upsert(Role role){
		if( role.getId()==null||role.getId().isEmpty()){//新增
			return roleService.insert(role)? Constant.successMessage():Constant.failureMessage();
		}else{//修改
			return roleService.update(role)? Constant.successMessage():Constant.failureMessage();
		}
	}

	@ResponseBody
	@RequestMapping(value="/delete",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="删除角色信息")
	public String delete(HttpServletRequest request){
		String id = request.getParameter("id");
		if(id==null||id.isEmpty()){
			return Constant.failureMessage("删除失败！");
		}else{
			return  roleService.delete(id)?Constant.successMessage():Constant.failureMessage();
		}
	}
	@ResponseBody
	@RequestMapping(value="/getEntity",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取系统信息")
	public Role getEntity(HttpServletRequest request){
		String id = request.getParameter("id");
		if(id==null||id.isEmpty()){
			return new Role();
		}else{
			return  roleService.getEntity(id);
		}
	}
	//TODO 给角色配置菜单&按钮

	//TODO 给角色添加账号

}

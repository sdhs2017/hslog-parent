package com.jz.bigdata.common.menu.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.menu.service.IMenuService;
import com.jz.bigdata.util.DescribeLog;

/**
 * @author shichengyu
 * @date 2018年5月10日 下午5:54:09
 * @description
 */
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
	@RequestMapping(value="/selectAll",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取菜单栏")
	public String selectAll(HttpServletRequest request){
		HttpSession session = request.getSession();
		String role=(String) session.getAttribute(Constant.SESSION_USERROLE);
		int ro=Integer.valueOf(role);
		return menuService.selectAll(ro);
	}
	

}

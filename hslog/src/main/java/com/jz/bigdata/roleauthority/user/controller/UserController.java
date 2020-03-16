package com.jz.bigdata.roleauthority.user.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.license.VerifyLicense;
import com.jz.bigdata.roleauthority.user.entity.User;
import com.jz.bigdata.roleauthority.user.service.IUserService;
import com.jz.bigdata.roleauthority.user.util.Page;
import com.jz.bigdata.util.DescribeLog;
import com.jz.bigdata.util.Uuid;

/**
 * @author shichengyu
 * @date 2017年8月1日 上午10:09:08
 * @description 用户管理相关模块
 */
@Controller
@RequestMapping("/user")
public class UserController {

	@Resource(name = "UserService")
	private IUserService userService;
	
	@Resource(name = "licenseService")
	private VerifyLicense verifyLicense;

	public static  String address;
	
	public static String map;
	/**
	 * @return 查询所有数据
	 */
	@ResponseBody
	@RequestMapping("/selectAlls")
	@DescribeLog(describe="查询所有用户")
	public  Map<String,Object> selectAll(Page page,HttpSession session) {
		return userService.selectPage(page,session);
	}

	/**
	 * @param request
	 * @param user
	 * @return 添加数据 修改数据
	 */
	@ResponseBody
//	@RequestMapping("/inserts")
	@RequestMapping(value="/inserts", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="添加或修改用户")
	public String insert(HttpServletRequest request, User user) {
		int result = 0;
//		判断id是否为空
		if (user.getId() == null || user.getId().isEmpty()) {
			List<User> userList =userService.selectByName(user);
			if(userList.size()<1 ){
				user.setId(Uuid.getUUID());
//				添加数据
				result = this.userService.insert(user);
			}else{
				return Constant.repetitionMessage();
			}

		} else {
			result = this.userService.updateById(user);
		}
		return result >= 1 ? Constant.successMessage() : Constant.failureMessage();
	}

	/**
	 * @param request
	 * @return 根据id删除数据
	 */
	@ResponseBody
	@RequestMapping("/deletes")
	@DescribeLog(describe="删除用户")
	public String delete(HttpServletRequest request) {
		int result = 0;
//		获取id数组
		String[] ids = request.getParameter("ids").split(",");
		if (ids.length > 0) {
			result = this.userService.delete(ids);
		}
		return result >= 1 ? Constant.successMessage() : Constant.failureMessage();
	}

	/**
	 * @param request
	 * @param page
	 * @return 分页测试例子
	 */
	@ResponseBody
	@RequestMapping("/selectPage")
	@DescribeLog(describe="分页查询用户")
	public  Map<String,Object> selectPage(HttpServletRequest request, Page page,HttpSession session) {
		return userService.selectPage(page,session);
	}

	/**
	 * @param request
	 * @return
	 * 通过id查询用户信息
	 */
	@ResponseBody
	@RequestMapping("/selectUser")
	@DescribeLog(describe="查询单个用户信息")
	public List<User> selectUser(HttpServletRequest request) {
//		获取id
		String id = request.getParameter("id");
		return userService.selectUser(id);
	}
	
	
	/**
	 * @param
	 * @return
	 * 通过id查询用户信息
	 */
	@ResponseBody
	@RequestMapping(value="/selectUserRole", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="查询用户角色信息")
	public String selectUserRole(HttpSession session) {
//		获取id
//		String id = request.getParameter("id");
		String role = userService.selectUserRole(session);
		return Constant.successMessage(role);
		//return role;
	}
	/**
	 * @param request
	 * @return
	 * 根据id查询信息
	 */
	@ResponseBody
	@RequestMapping("/selectById")
	@DescribeLog(describe="根据id查询用户信息")
	public User selectById(HttpServletRequest request){
//		获取id
		String id = request.getParameter("id");
//		查询数据
		User user= userService.selectById(id);

		return user;
	}
	
	
	/**
	 * @param request
	 * @param user
	 * @param session
	 * @return 登陆是否成功信息
	 * @description  登陆验证模块，并将session信息保存
	 */
	@ResponseBody
//	@RequestMapping("/login")
	@RequestMapping(value="/login", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="用户登录")
	public String login(HttpServletRequest request,User user,HttpSession session){

		return this.userService.login(user,session);
	}
	
	
	/**
	 * @param session
	 * @return 
	 * @description 验证session信息，确认是否登陆成功 
	 */
	@ResponseBody
	@RequestMapping("/checkLogin")
	@DescribeLog(describe="登录确认")
	public String checkLogin(HttpSession session){
		Boolean result = this.userService.checkLogin(session);
		return result?"{\"success\":\"true\"}":"{\"success\":\"false\"}";
	}
	
	/**
	 * @param session
	 * @return 
	 * @throws UnsupportedEncodingException 
	 * @description 系统登陆，清空session
	 */
	@ResponseBody
	@RequestMapping("/logout")
	@DescribeLog(describe="退出登录")
	public String loginOut(HttpSession session) throws UnsupportedEncodingException{
		Boolean result = this.userService.loginOut(session);
		return result?"{\"success\":\"true\"}":"{\"success\":\"false\"}";
	}
	
	@ResponseBody
//	@RequestMapping("/registerUser")
	@RequestMapping(value="/registerUser", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="注册用户")
	public String registerUser(User user) {
		int result =0;
		try {
			List<User> userList =userService.selectByName(user);
			if(userList.size()<1 ){
				user.setId(Uuid.getUUID());
//			添加数据
				result=userService.registerUser(user);
			}else{
				return Constant.repetitionMessage();
			}
		}catch (Exception e){
			e.getStackTrace();
		}
		return result == 2 ? Constant.successMessage() : Constant.failureMessage();
	}
	@ResponseBody
//	@RequestMapping("/registerUser")
	@RequestMapping(value="/updatePasswordById", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="修改密码")
	public String updatePasswordById(HttpServletRequest request) {
		String id= request.getParameter("id");
		String password= request.getParameter("password");
		String oldPassword=request.getParameter("oldPassword");
//		System.out.println("id"+id+"password"+password+"oldPassword"+password);
		return userService.updatePasswordById(id, password,oldPassword);
	}

}

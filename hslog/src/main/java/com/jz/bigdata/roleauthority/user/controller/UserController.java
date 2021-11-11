package com.jz.bigdata.roleauthority.user.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.jz.bigdata.common.start_execution.cache.ConfigurationCache;
import com.jz.bigdata.util.MD5;
import com.jz.bigdata.util.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {
	@Resource(name = "UserService")
	private IUserService userService;
	
	@Resource(name = "licenseService")
	private VerifyLicense verifyLicense;

	public static  String address;
	
	public static String map;

	private boolean user_lock = false;
	/**
	 * @return 用户锁定页面
	 */
	@ResponseBody
	@RequestMapping(value="/userLocked", produces="application/json; charset=utf-8")
	@DescribeLog(describe="用户锁定页面")
	public String userLock(Page page,HttpSession session) {
		//锁定，即将用户session延长，72小时
		session.setMaxInactiveInterval(60*60*72);
		user_lock = true;
		return Constant.successMessage();
	}
	/**
	 * @return 用户解锁页面
	 */
	@ResponseBody
	@RequestMapping(value="/userUnlock" ,produces = "application/json; charset=utf-8")
	@DescribeLog(describe="用户解锁页面")
	public String userUnlock(HttpServletRequest request,HttpSession session) {
		//验证密码
		String password =request.getParameter("password");
		String phone = session.getAttribute(Constant.SESSION_USERACCOUNT).toString();
		try{
			User user = new User();
			user.setPhone(phone);
			user.setPassword(MD5.EncoderByMd5(RSAUtil.decrypt(password,RSAUtil.getPrivateKey())));
			//通过账号和密码查询用户信息
			User _user = userService.selectByPhonePwd(user);
			if(_user!=null){
				//可以正常查询出用户
				String timeout = ConfigurationCache.INSTANCE.getConfigurationCache().getIfPresent(Constant.SESSION_TIMEOUT).toString();
				//锁定，即将用户session延长
				session.setMaxInactiveInterval(Integer.parseInt(timeout)*60);
				user_lock = false;
				return Constant.successMessage();
			}else{
				return Constant.failureMessage("密码错误！");
			}
		}catch (Exception e){
			return Constant.failureMessage("解锁失败！");
		}


	}
	/**
	 * @return 获取用户页面锁定状态
	 */
	@ResponseBody
	@RequestMapping(value="/getUserLockState" ,produces = "application/json; charset=utf-8")
	@DescribeLog(describe="获取用户页面锁定状态")
	public String getUserLockState(Page page,HttpSession session) {
		return user_lock?Constant.successMessage():Constant.failureMessage();
	}

	/**
	 * @return 查询所有数据
	 */
	@ResponseBody
	@RequestMapping("/selectAlls")
	@DescribeLog(describe="查询所有用户")
	public  Map<String,Object> selectAll(Page page,HttpSession session) {
		try{
			return userService.selectPage(page,session);
		}catch(Exception e){
			log.error("查询所有用户"+e.getMessage());
			return null;
		}

	}
	/**
	 * @return 涉密，查询当前角色下的所有用户
	 */
	@ResponseBody
	@RequestMapping("/selectAll_Security")
	@DescribeLog(describe="查询所有用户")
	public  Map<String,Object> selectAll_Security(Page page,HttpSession session) {
		try{
			String roleid = session.getAttribute(Constant.SESSION_USERROLE).toString();
			page.setRoleid(roleid);
			return userService.selectPage(page,session);
		}catch(Exception e){
			log.error("查询所有用户"+e.getMessage());
			return null;
		}

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
		try{
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
			return result >= 1 ? Constant.successMessageWithTarget("操作成功！","用户名:"+user.getPhone()) : Constant.failureMessageWithTarget("操作失败！","用户名:"+user.getPhone());
		}catch(Exception e){
			log.error("添加或修改用户"+e.getMessage());
			return Constant.failureMessageWithTarget("操作失败！","用户名:"+user.getPhone());
		}

	}
	/**
	 * @param request
	 * @param user
	 * @return 涉密，添加数据 修改数据，添加用户时会根据当前用户角色创建相同角色用户
	 */
	@ResponseBody
//	@RequestMapping("/inserts")
	@RequestMapping(value="/insert_Security", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="添加或修改用户")
	public String insert_Secutiry(HttpServletRequest request, User user,HttpSession session) {
		try{
			int result = 0;
			//判断id是否为空
			if (user.getId() == null || user.getId().isEmpty()) {
				List<User> userList =userService.selectByName(user);
				if(userList.size()<1 ){
					//验证密码复杂度
					if(!userService.checkPwd_Boolean(user.getPassword())){
						return Constant.failureMessage("密码复杂度未满足要求！");
					}
					user.setId(Uuid.getUUID());
					//获取当前用户角色
					String roleid = session.getAttribute(Constant.SESSION_USERROLE).toString();
					user.setRole(roleid);//要添加的用户角色与当前用户角色相同
					//添加数据
					result = this.userService.insert(user);
				}else{
					return Constant.repetitionMessage();
				}

			} else {
				result = this.userService.updateById(user);
			}
			return result >= 1 ? Constant.successMessageWithTarget("操作成功！","用户名:"+user.getPhone()) : Constant.failureMessageWithTarget("操作失败！","用户名:"+user.getPhone());
		}catch(Exception e){
			log.error("添加或修改用户"+e.getMessage());
			return Constant.failureMessageWithTarget("操作失败！","用户名:"+user.getPhone());
		}

	}
	/**
	 * @param request
	 * @return 设置用户角色
	 */
	@ResponseBody
	@DescribeLog(describe="设置用户角色")
	@RequestMapping(value="/setUserRole", produces = "application/json; charset=utf-8")
	public String setUserRole(HttpServletRequest request) {
		try{
			String user_ids = request.getParameter("user_ids");
			String role_ids = request.getParameter("role_ids");
			int result = userService.setUserRole(user_ids,role_ids);
			return result >= 1 ? Constant.successMessage() : Constant.failureMessage();
		}catch(Exception e){
			log.error("删除用户"+e.getMessage());
			return Constant.failureMessage("删除用户失败");
		}

	}
	/**
	 * @param request
	 * @return 根据id删除数据
	 */
	@ResponseBody
	@RequestMapping("/deletes")
	@DescribeLog(describe="删除用户")
	public String delete(HttpServletRequest request) {
		try{
			int result = 0;
//		获取id数组
			String[] ids = request.getParameter("ids").split(",");
			if (ids.length > 0) {
				result = this.userService.delete(ids);
			}
			return result >= 1 ? Constant.successMessage() : Constant.failureMessage();
		}catch(Exception e){
			log.error("删除用户"+e.getMessage());
			return Constant.failureMessage("删除用户失败");
		}

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
		try{
			return userService.selectPage(page,session);
		}catch(Exception e){
			log.error("分页查询用户"+e.getMessage());
			return null;
		}

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
		try{
//		获取id
			String id = request.getParameter("id");
			return userService.selectUser(id);
		}catch(Exception e){
			log.error("查询单个用户信息"+e.getMessage());
			return null;
		}

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
		try{
			String role = userService.selectUserRole(session);
			return Constant.successMessage(role);
		}catch(Exception e){
			log.error("查询用户角色信息"+e.getMessage());
			return Constant.failureMessage("查询用户角色信息失败");
		}


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
		try{
//		获取id
			String id = request.getParameter("id");
//		查询数据
			User user= userService.selectById(id);

			return user;
		}catch(Exception e){
			log.error("根据id查询用户信息"+e.getMessage());
			return null;
		}

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
		try{

			return this.userService.login(user,session,request);
		}catch(Exception e){
			log.error("用户登录"+e.getMessage());
			return Constant.failureMessage("用户登录失败！");
		}

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

		try{
			Boolean result = this.userService.checkLogin(session);
			return result?"{\"success\":\"true\"}":"{\"success\":\"false\"}";
		}catch(Exception e){
			log.error("登录确认失败"+e.getMessage());
			return Constant.failureMessage("登录确认失败！");
		}

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
		try{
			Boolean result = this.userService.loginOut(session);
			return result?"{\"success\":\"true\"}":"{\"success\":\"false\"}";
		}catch(Exception e){
			log.error("退出登录失败"+e.getMessage());
			return Constant.failureMessage("退出登录失败");
		}

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
			log.error("注册用户"+e.getMessage());
			e.getStackTrace();
		}
		return result == 2 ? Constant.successMessage() : Constant.failureMessage();
	}
	@ResponseBody
//	@RequestMapping("/registerUser")
	@RequestMapping(value="/updatePasswordById", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="修改密码")
	public String updatePasswordById(HttpServletRequest request) {
		try{
			String id= request.getParameter("id");
			String password= request.getParameter("password");
			String oldPassword=request.getParameter("oldPassword");
			//密码解密
			password = RSAUtil.decrypt(password,RSAUtil.getPrivateKey());
			oldPassword = RSAUtil.decrypt(oldPassword,RSAUtil.getPrivateKey());
			//验证密码复杂度
			if(!userService.checkPwd_Boolean(password)){
				return Constant.failureMessage("新密码复杂度未满足要求！");
			}
			return userService.updatePasswordById(id, password,oldPassword);
		}catch(Exception e){
			log.error("修改密码失败"+e.getMessage());
			return Constant.failureMessage("修改密码失败！");
		}

	}
	@ResponseBody
	@RequestMapping(value="/resetPasswordById", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="重置密码")
	public String resetPasswordById(HttpServletRequest request) {
		try{
			String id= request.getParameter("id");
			return userService.resetPasswordById(id);
		}catch(Exception e){
			log.error("密码重置失败："+e.getMessage());
			return Constant.failureMessage("密码重置失败！");
		}
	}

	@ResponseBody
	@RequestMapping(value="/checkPwd", produces = "application/json; charset=utf-8")
	@DescribeLog(describe="校验密码复杂度")
	public String checkPwd(HttpServletRequest request) {
		try{
			String password= request.getParameter("password");
			return userService.checkPwd_info(password);
		}catch(Exception e){
			log.error("校验密码复杂度失败："+e.getMessage());
			return Constant.failureMessage("校验密码复杂度失败！");
		}
	}
}

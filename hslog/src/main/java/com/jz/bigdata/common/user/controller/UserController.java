package com.jz.bigdata.common.user.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.license.VerifyLicense;
import com.jz.bigdata.common.user.entity.User;
import com.jz.bigdata.common.user.service.IUserService;

/**
 * @author yiyang
 * @date 2016年7月28日 上午11:47:09
 * @description 登陆及相关验证模块
 */
@Controller
@RequestMapping("/user")
public class UserController {
	
	@Resource(name="UserService")
	private IUserService userService;
	
	/*@Resource(name="licenseService")
	private VerifyLicense verifyLicense;*/
	
	/**
	 * @param request
	 * @param user
	 * @param session
	 * @return 登陆是否成功信息
	 * @description  登陆验证模块，并将session信息保存
	 */
	@ResponseBody
	@RequestMapping("/login")
	public String login(HttpServletRequest request,User user,HttpSession session){
		Boolean result = this.userService.login(user,session);
		VerifyLicense vLicense = new VerifyLicense();
		//获取参数
		vLicense.setParam("/verifyparam.properties");
		//验证证书
		Boolean vresult = vLicense.verify();
		
		if (result&&vresult) {
			return Constant.loginMessage(result, 1, "登录成功！");
		}else if (!result) {
			return Constant.loginMessage(result, 2, "账户或密码不正确！");
		}else {
			return Constant.loginMessage(vresult, 2, "产品已过期！");
		}
		
		//return result?"{\"success\":\"true\"}":"{\"success\":\"false\"}";
	}
	
	/**
	 * @param session
	 * @return 
	 * @description 系统登陆，清空session
	 */
	@ResponseBody
	@RequestMapping("/loginOut")
	public String loginOut(HttpSession session){
		Boolean result = this.userService.loginOut(session);
		return result?"{\"success\":\"true\"}":"{\"success\":\"false\"}";
	}
	
	/**
	 * @param session
	 * @return 
	 * @description 验证session信息，确认是否登陆成功 
	 */
	@ResponseBody
	@RequestMapping("/checkLogin")
	public String checkLogin(HttpSession session){
		Boolean result = this.userService.checkLogin(session);
		return result?"{\"success\":\"true\"}":"{\"success\":\"false\"}";
	}
}

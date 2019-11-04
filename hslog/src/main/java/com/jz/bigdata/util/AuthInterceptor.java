package com.jz.bigdata.util;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.function.dao.IFunctionDao;
import com.jz.bigdata.common.function.service.IFunctionService;
import com.jz.bigdata.common.function.util.GetfunctionMap;
import com.jz.bigdata.common.users.dao.IUsersDao;
import com.jz.bigdata.common.users.entity.User;
import com.jz.bigdata.common.users.service.IUsersService;


/**
 * @author yiyang
 * @date 2016年7月29日 上午10:48:15
 * @description controller请求拦截器，请求到达controller前
 */
public class AuthInterceptor extends HandlerInterceptorAdapter {

	@Resource(name = "UsersService")
	private IUsersService userService;

	@Resource(name = "FunctionService")
	private IFunctionService functionService;

	@Resource
	private IUsersDao userDao;

	@Resource
	private IFunctionDao functionDao;

	/**
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 * @throws Exception
	 * @description
	 * 拦截器
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		//如果是登陆请求，放行
		if(handler.toString().indexOf(Constant.LOGINPATH)>=0||handler.toString().indexOf(Constant.REGISTERPATH)>=0||handler.toString().indexOf(Constant.uploadPATH)>=0){
			//退出登录，注册，上传不拦截
			/**
			 * TODO
			 * equals改为indexof(*)>0
			 * 或者 只判断项目路径后面的相对路径
			 */
			if(request.getRequestURI().contains("/users/logout.do")||request.getRequestURI().contains("/users/registerUser.do")){
				return true;
			}else if(request.getRequestURI().contains("/upload/licenseUpload.do")){
				//TODO
				/**
				 * 校验上传文件是否合法
				 */
				return true;
			}else if(request.getRequestURI().contains("apiLog")){
				//TODO
				/**
				 * 通过api token访问，需要处理安全问题
				 * 权限校验逻辑与登录校验逻辑相同
				 */
				return true;
			}else{
				//登陆，校验
				String phone=request.getParameter("phone");
				String password=request.getParameter("password");
				User user =new User();
				user.setPhone(phone);
				user.setPassword(MD5.EncoderByMd5(password));
				//查询用户
				List<User> _userList = userDao.selectByPhonePwd(user);

//    		System.out.println(_userList.get(0).getName());
				GetfunctionMap getfunctionMap= new GetfunctionMap();
//    		getfunctionMap.map;
				//获取权限列表
				/**
				 * TODO
				 * 后期修改
				 * 1.多线程隐患：GetfunctionMap.map应声明为线程安全的Map
				 * 2.不使用静态方式声明，不使用对象.成员变量形式引用
				 */
				Map<String,Map<String,String>> map=GetfunctionMap.map;
//    		System.out.println(map.get(_userList.get(0).getRole()));

				if(_userList.size()<1){
					/**
					 * TODO
					 * true为放行
					 * 接口为boolean，无法设置返回内容，所以放行，在UserServiceImpl的login里处理返回
					 * 也可以用以下方式返回
					 * out.print("{\"success\":\"false\",\"message\":\"登录超时，请刷新页面重新登录\"}");
					 */
					return true;
				}else{
					//判断缓存中是否有数据
					/**
					 * TODO
					 * 后期修改：
					 * function的map加载改为初始全部加载
					 */
					if(map.get(String.valueOf(_userList.get(0).getRole()) )==null||map.get(String.valueOf(_userList.get(0).getRole())).equals("")){
						getfunctionMap.getfunctionMap(_userList.get(0).getRole(),functionService);
					}
				}
//    		System.out.println(GetfunctionMap.map.size());
//    		funService.map;
//    		functionService.getClass()
//    		functionService
//    	    functionService.selectAll(_userList.get(0).getRole());

//    		System.err.println(request.getRequestURI());
//    		PrintWriter out = response.getWriter();
//    		response.setCharacterEncoding("utf-8");
//            out.print("您没有权限");

				return true;
			}
		}else{
			//session超时检测
			HttpSession session = request.getSession();
			//session userid不为空且sessionid一致
//        	System.err.println(session.getAttribute(Constant.SESSION_USERID)!=null);
//        	System.err.println(session.getId().equals(session.getAttribute(Constant.SESSION_ID)));
//        	System.err.println(session.getAttribute(Constant.SESSION_USERID));
//        	System.err.println(session.getAttribute(Constant.SESSION_ID));
			//判断session是否超时
			if(session.getAttribute(Constant.SESSION_USERID)!=null&&session.getId().equals(session.getAttribute(Constant.SESSION_ID))){
//        		System.err.println(session.getAttribute(Constant.SESSION_USERID));
				Map<String,Map<String,String>> map=GetfunctionMap.map;
//        		System.out.println(Constant.SESSION_USERROLE);
//        		System.out.println(session.getAttribute(Constant.SESSION_USERROLE));
//        		System.out.println(map.get(session.getAttribute(Constant.SESSION_USERROLE).toString()));
//        		System.out.println(request.getRequestURI().toString());
				//是否有权限
				/**
				 * TODO
				 * request.getRequestURI().toString()为带项目名的路径，
				 * 如果数据库function表中的resource字段后期删除项目名，则此处要截取掉项目名
				 */
				if(map!=null&&map.get(session.getAttribute(Constant.SESSION_USERROLE).toString()).get(request.getRequestURI().toString())!=null){
					return true;
					//无权限
				}else{
					response.setContentType("text/html;charset=utf-8");
					PrintWriter out = response.getWriter();
//        			JSONObject jsStr = JSONObject.parseObject("{msg:您没有权限}");
//        			JSONArray array=JSONArray.fromObject("{msg:您没有权限}");
//            		response.setCharacterEncoding("utf-8");

					/**
					 * TODO
					 * 规范返回信息，与前端回调匹配
					 */
					out.print("您没有权限");

					return false;
				}
//        		System.out.println(map.size());
//        		System.out.println(map.get(session.getAttribute(Constant.SESSION_USERROLE)).get("/jzLog/users/selectUser.do"));
//        		return true;
				//无权限
			}else{
				response.setContentType("text/html;charset=utf-8");
				PrintWriter out = response.getWriter();
//    			JSONObject jsStr = JSONObject.parseObject("{msg:您没有权限}");
//    			JSONArray array=JSONArray.fromObject("{msg:您没有权限}");
//        		response.setCharacterEncoding("utf-8");

				/**
				 * TODO
				 * 验证返回信息是否规范
				 */
				out.print("{\"success\":\"false\",\"message\":\"登录超时，请刷新页面重新登录\"}");
				return false;
			}
		}

	}


}
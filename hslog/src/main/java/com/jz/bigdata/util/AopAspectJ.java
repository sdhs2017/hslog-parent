package com.jz.bigdata.util;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.jz.bigdata.roleauthority.user.dao.IUserDao;
import com.jz.bigdata.roleauthority.user.entity.User;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.note.entity.Note;
import com.jz.bigdata.common.note.service.INoteService;


/**
 * @author shichengyu 审计日志
 */
@Aspect
@Component
public class AopAspectJ {

	/**
	 * 必须为final String类型的,注解里要使用的变量只能是静态常量类型的
	 */
	// public static final String EDP = "execution(*
	// com.jz.bigdata..*.controller.*.*(..))";
	// 扫描包路径
	@Pointcut("execution(*  com.jz.bigdata..*.controller.*.*(..))")
	public void myMethod() {
	};

	// @Pointcut("execution(*
	// com.jz.bigdata.common.*.controller.*.*(..))||execution(*
	// com.jz.bigdata.business.logAnalysis.log.controller.*.*(..))")
	// public void myMethod(){};
	//

	@Resource(name = "NoteService") // 这里我用resource注解，一般用的是@Autowired，他们的区别如有时间我会在后面的博客中来写
	private INoteService noteService;

	@Resource
	private IUserDao userDao;

	/**
	 * 切面的前置方法 即方法执行前拦截到的方法 在目标方法执行之前的通知
	 * 
	 * @param jp
	 * @throws ClassNotFoundException
	 */
	@Before("myMethod()")
	public void doBefore(JoinPoint jp) throws ClassNotFoundException {
		// 获取request
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String ip = AopAspectJ.getIpAddr(request);
		// 获取session
		HttpSession session = request.getSession();
		// 退出登录判断
		if (jp.getSignature().getName().equals("loginOut") && session.getAttribute(Constant.SESSION_USERID) != null) {

			// 调用的方法名
			String methodName = jp.getSignature().getName();
			// new note 赋值
			Note note = new Note();
			note.setId(Uuid.getUUID());
			Date date = new Date();
			note.setTime(new Timestamp(date.getTime()));
			note.setResult(1);
			note.setIp(ip);

			// 拦截的实体类，就是当前正在执行的controller
			String targetName = jp.getTarget().getClass().getName();
			Class targetClass = Class.forName(targetName);
			Method[] methods = targetClass.getMethods();
			// 拦截的方法参数
			Object[] arguments = jp.getArgs();
			// 获取controller描述
			String describe = "";
			// 获取controller描述
			for (Method method : methods) {
				// 判断方法名
				if (method.getName().equals(methodName)) {
					Class[] clazzs = method.getParameterTypes();
					if (clazzs.length == arguments.length) {
						describe = method.getAnnotation(DescribeLog.class).describe();
						// operationName =
						// method.getAnnotation(Log.class).operationName();
						break;
					}
				}
			}
			// 赋值
			note.setUserId(session.getAttribute(Constant.SESSION_USERID).toString());
			String describeSrt = "";
			//判断部门是否存在
			if (session.getAttribute(Constant.SESSION_DEPARTMENTID) == null) {
				describeSrt = "用户账号:" + session.getAttribute(Constant.SESSION_USERACCOUNT).toString() + "    " + "操作:"
						+ describe + "    " + "操作状态:成功";
			} else {
				note.setDepartmentId(Integer.valueOf(session.getAttribute(Constant.SESSION_DEPARTMENTID).toString()));
				describeSrt = "用户账号:" + session.getAttribute(Constant.SESSION_USERACCOUNT).toString() + "    " + "部门:"
						+ session.getAttribute(Constant.SESSION_DEPARTMENTNAME).toString() + "    " + "操作:" + describe
						+ "    " + "操作状态:成功";
			}

			note.setDescribe(describeSrt);
			note.setAccount(session.getAttribute(Constant.SESSION_USERACCOUNT).toString());
			// 添加日志
			noteService.insert(note);

		}

	}

	/**
	 * 在方法正常执行通过之后执行的通知叫做返回通知 可以返回到方法的返回值 在注解后加入returning
	 * 
	 * @param jp
	 * @param result
	 */
	// @AfterReturning(value=EDP,returning="result")
	public void doAfterReturning(JoinPoint jp, String result) {
//		System.out.println("===========执行后置通知============");
	}

	/**
	 * 最终通知：目标方法调用之后执行的通知（无论目标方法是否出现异常均执行）
	 * 
	 * @param jp
	 */
	// @After(value=EDP)
	public void doAfter(JoinPoint jp) {
//		System.out.println("===========执行最终通知============");
	}

	/**
	 * 环绕通知：目标方法调用前后执行的通知，可以在方法调用前后完成自定义的行为。
	 * 
	 * @param pjp
	 * @return
	 * @throws Throwable
	 *             正常操作日志记录
	 * 
	 */
	@Around("myMethod()")
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {

		// System.out.println("======执行环绕通知开始=========");
		// 调用方法的参数
		// Object[] args = pjp.getArgs();

		// 获取目标对象
		// Object target = pjp.getTarget();
		// 执行完方法的返回值
		// 调用proceed()方法，就会触发切入点方法执行
		Object result = pjp.proceed();

		// System.out.println(result);
		// 获取返回类型
		// Signature signature = pjp.getSignature();
		// 获取返回类型
		// Class returnType = ((MethodSignature) signature).getReturnType();

		// 获取request
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		//获取ip地址
		String ip = AopAspectJ.getIpAddr(request);
		// String ip2=AopAspectJ.getIpAddr(request);
		// String ip3=AopAspectJ.getRemortIP(request);
		// System.err.println("ip:"+ip);
		// System.out.println("ip2:"+ip2);
		// System.err.println("ip3:"+ip3);
		// 获取session
		HttpSession session = request.getSession();
		// System.out.println(session.getAttribute(Constant.SESSION_USERID));

		// 调用的方法名
		String methodName = pjp.getSignature().getName();

		// 拦截的实体类，就是当前正在执行的controller
		String targetName = pjp.getTarget().getClass().getName();
		Class targetClass = Class.forName(targetName);
		Method[] methods = targetClass.getMethods();
		// 拦截的方法参数
		Object[] arguments = pjp.getArgs();
		// 获取controller描述
		String describe = "";
		for (Method method : methods) {
			// 判断方法名
			if (method.getName().equals(methodName)) {
				Class[] clazzs = method.getParameterTypes();
				if (clazzs.length == arguments.length) {
					describe = method.getAnnotation(DescribeLog.class).describe();
					// operationName =
					// method.getAnnotation(Log.class).operationName();
					break;
				}
			}
		}
		// new note 赋值
		Note note = new Note();
		note.setId(Uuid.getUUID());
		Date date = new Date();
		note.setTime(new Timestamp(date.getTime()));
		note.setIp(ip);
		// 密码错误登录不成功
		if (describe.equals("用户登录")) {

			// note.setResult(0);
			User user = (User) pjp.getArgs()[1];
			note.setAccount(user.getPhone());

			String describeSrt = "";
			// 登录失败
			if (result.toString().equals("{\"success\":\"false\",\"message\":\"登录失败，账号或密码错误\"}")) {
				note.setResult(0);
				note.setError("登录失败");
				describeSrt = "用户账号:" + user.getPhone() + "    " + "操作:" + describe + "    " + "操作状态:失败";
				note.setDescribe(describeSrt);
				noteService.insert(note);
				// 登陆成功
			} else if (result.toString().equals("{\"success\":\"true\",\"message\":\"登录成功\"}")) {
				note.setResult(1);
				// note.setError("登录成功");
				describeSrt = "用户账号:" + user.getPhone() + "    " + "操作:" + describe + "    " + "操作状态:成功";
				note.setDescribe(describeSrt);
				noteService.insert(note);
				// 账号暂停
			}else if (result.toString().equals("{\"success\":\"false\",\"message\":\"账号暂停服务\"}")){
				note.setResult(0);
				note.setError("账号暂停服务");
				describeSrt = "用户账号:" + user.getPhone() + "    " + "操作:" + describe + "    " + "操作状态:失败";
				note.setDescribe(describeSrt);
				noteService.insert(note);

			}else if (result.toString().equals("{\"success\":\"false\",\"message\":\"您已连续5次输入密码错误，账号已被锁定\"}")){
				//账号锁定{"success":"false","message":"您已连续5次输入密码错误，账号已被锁定"}
				note.setResult(0);
				note.setError("账号已锁定");
				describeSrt = "用户账号:" + user.getPhone() + "    " + "操作:" + describe + "    " + "操作状态:失败";
				note.setDescribe(describeSrt);
				noteService.insert(note);
			}else {
				describeSrt = "用户账号:" + user.getPhone() + "    " + "操作:" + describe + "    " + "操作状态:失败";
				note.setResult(0);
				note.setError("登录失败");
				note.setDescribe(describeSrt);
				noteService.insert(note);
			}
			// 注册账号
		} else if (describe.equals("注册用户")) {
			User user = (User) pjp.getArgs()[0];
			note.setAccount(user.getPhone());
			String describeSrt = "";
			note.setResult(1);
			// note.setError("登录成功");
			describeSrt = "用户账号:" + user.getPhone() + "    " + "操作:" + describe + "    " + "操作状态:成功";
			note.setDescribe(describeSrt);
			noteService.insert(note);
			// 定时任务
		} else if (describe.equals("监控数据采集器状态") || describe.equals("获取服务器磁盘使用情况")) {
			// 退出登录
		} else if (session.getAttribute(Constant.SESSION_USERID) != null
				&& !pjp.getSignature().getName().equals("loginOut")) {// 所有的不报异常方法
			note.setResult(1);
			note.setUserId(session.getAttribute(Constant.SESSION_USERID).toString());
			String describeSrt = "";
			if (session.getAttribute(Constant.SESSION_DEPARTMENTID) == null) {
				describeSrt = "用户账号:" + session.getAttribute(Constant.SESSION_USERACCOUNT).toString() + "    " + "操作:"
						+ describe + "    " + "操作状态:成功";
			} else {
				note.setDepartmentId(Integer.valueOf(session.getAttribute(Constant.SESSION_DEPARTMENTID).toString()));
				// 拼接描述
				describeSrt = "用户账号:" + session.getAttribute(Constant.SESSION_USERACCOUNT).toString() + "    " + "部门:"
						+ session.getAttribute(Constant.SESSION_DEPARTMENTNAME).toString() + "    " + "操作:" + describe
						+ "    " + "操作状态:成功";

			}
			note.setDescribe(describeSrt);
			note.setAccount(session.getAttribute(Constant.SESSION_USERACCOUNT).toString());
			noteService.insert(note);
		}

		// note
		// System.out.println("输出,方法名：" + methodName + ";目标对象：" + target +
		// ";返回值：" + result);
		// System.out.println("======执行环绕通知结束=========");
		return result;
	}

	/**
	 * 在目标方法非正常执行完成, 抛出异常的时候会走此方法
	 * 
	 * @param jp
	 * @param ex
	 * @throws ClassNotFoundException
	 *             异常操作日志记录
	 */
	@AfterThrowing(value = "myMethod()", throwing = "ex")
	public void doAfterThrowing(JoinPoint jp, Exception ex) throws ClassNotFoundException {
		// 获取request
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		//获取ip
		String ip = AopAspectJ.getIpAddr(request);
		// 获取session
		HttpSession session = request.getSession();
		// 调用的方法名
		String methodName = jp.getSignature().getName();
		// new note 赋值
		Note note = new Note();
		note.setId(Uuid.getUUID());
		Date date = new Date();
		note.setTime(new Timestamp(date.getTime()));
		note.setResult(0);
		note.setIp(ip);

		// 拦截的实体类，就是当前正在执行的controller
		String targetName = jp.getTarget().getClass().getName();
		Class targetClass = Class.forName(targetName);
		Method[] methods = targetClass.getMethods();
		// 拦截的方法参数
		Object[] arguments = jp.getArgs();
		String describe = "";
		// 获取controller描述
		for (Method method : methods) {
			// 陪判断方法名
			if (method.getName().equals(methodName)) {
				Class[] clazzs = method.getParameterTypes();
				if (clazzs.length == arguments.length) {
					describe = method.getAnnotation(DescribeLog.class).describe();
					// operationName =
					// method.getAnnotation(Log.class).operationName();
					break;
				}
			}
		}

		if (describe.equals("用户登录")) {// 用户登录报异常
			User user = (User) jp.getArgs()[1];
			note.setAccount(user.getPhone());
			note.setError(ex.getClass().getName());
			String describeSrt = "用户账号:" + user.getPhone() + "    " + "操作:" + describe + "    " + "操作状态:失败";
			note.setDescribe(describeSrt);
			noteService.insert(note);
		} else {// 正常情况异常处理
			note.setUserId(session.getAttribute(Constant.SESSION_USERID).toString());
			note.setAccount(session.getAttribute(Constant.SESSION_USERACCOUNT).toString());
			note.setError(ex.getClass().getName());

			String describeSrt = "";

			if (session.getAttribute(Constant.SESSION_DEPARTMENTID) == null) {
				describeSrt = "用户账号:" + session.getAttribute(Constant.SESSION_USERACCOUNT).toString() + "    " + "操作:"
						+ describe + "    " + "操作状态:失败";

			} else {// 带部门
				note.setDepartmentId(Integer.valueOf(session.getAttribute(Constant.SESSION_DEPARTMENTID).toString()));
				describeSrt = "用户账号:" + session.getAttribute(Constant.SESSION_USERACCOUNT).toString() + "    " + "部门:"
						+ session.getAttribute(Constant.SESSION_DEPARTMENTNAME).toString() + "    " + "操作:" + describe
						+ "    " + "操作状态:失败";
			}
			note.setDescribe(describeSrt);

			noteService.insert(note);
		}

	}


	/**
	 * @param request
	 * @return
	 * 判断浏览器
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 获取当前网络ip
	 * 
	 * @param request
	 * @return
	 */
	public String getIpAddrs(HttpServletRequest request) {
		String ipAddress = request.getHeader("x-forwarded-for");
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ipAddress = inet.getHostAddress();
			}
		}
		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
															// = 15
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}
		return ipAddress;
	}

	/**
	 * @param request
	 * @return
	 * 获取ip
	 */
	public static String getRemortIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr();
		}
		return request.getHeader("x-forwarded-for");
	}

}
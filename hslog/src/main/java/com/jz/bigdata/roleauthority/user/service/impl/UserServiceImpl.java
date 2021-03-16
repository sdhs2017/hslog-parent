package com.jz.bigdata.roleauthority.user.service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.department.dao.IDepartmentDao;
import com.jz.bigdata.common.department.entity.Department;
import com.jz.bigdata.common.license.VerifyLicense;
import com.jz.bigdata.common.note.dao.INoteDao;
import com.jz.bigdata.common.note.entity.Note;
import com.jz.bigdata.roleauthority.user.dao.IUserDao;
import com.jz.bigdata.roleauthority.user.entity.User;
import com.jz.bigdata.roleauthority.user.service.IUserService;
import com.jz.bigdata.roleauthority.user.util.Page;
import com.jz.bigdata.util.MD5;
import com.jz.bigdata.util.Uuid;

import net.sf.json.JSONObject;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @author shichengyu
 * @date 2017年8月1日 上午10:06:51
 * @description 用户管理相关模块
 */
@Service(value="UserService")
public class UserServiceImpl implements IUserService {

	private static final DateTimeFormatter dtf_time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	@Resource
	private IUserDao userDao;
	
	@Resource
	private IDepartmentDao departmentDao;
	
	@Resource
	private INoteDao noteDao;
	
	@Resource(name = "licenseService")
	private VerifyLicense verifyLicense;

	/**
	 * @param user
	 * @return
	 * @description
	 * 添加数据
	 */
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
	public int insert(User user) {
		//默认添加时用户状态正常
		user.setState(1);
		user.setPassword(MD5.EncoderByMd5(user.getPassword()));
		String[] roleIDs = user.getRole().split(",");
		for(String roleID : roleIDs){//遍历角色id   添加到用户角色中间表中
			userDao.insertUserRole(user.getId(),roleID);
		}
		return userDao.insert(user);
	}


	/**
	 * @return
	 * @description
	 * 查询所有数据
	 */
	public List<User> selectAll(User user) {
		return userDao.selectAll(user);
	}

	/**
	 * @param ids
	 * @return
	 * @description
	 * 根据id删除数据
	 */
	@Override
	public int delete(String[] ids) {
		return userDao.delete(ids);
	}

	/**
	 * @param user
	 * @return
	 * @description
	 * 修改数据
	 * 
	 */
	@Override
	public int updateById(User user) {
		userDao.deleteUserRole(user.getId());
		String[] roleIDs = user.getRole().split(",");
		for(String roleID : roleIDs){//遍历角色id   添加到用户角色中间表中
			userDao.insertUserRole(user.getId(),roleID);
		}
		int tt= userDao.updateById(user);
//		int t=1/0;
		return tt;
	}


	/**
	 * @param page
	 * @return
	 * @description
	 * 分页查询
	 */
	@Override
	public Map<String,Object> selectPage(Page page,HttpSession session) {
//		开始数
		page.setStartRecord(page.getPageSize()*(page.getPageIndex()-1));
		//page.setRole(Integer.valueOf((String) session.getAttribute(Constant.SESSION_USERROLE)));
		page.setId((String) session.getAttribute(Constant.SESSION_USERID));
//		总数
		List<String> count=userDao.count(page);
		Map<String,Object> map =new HashMap<String,Object>();
//		List li=new ArrayList<>();
//		添加到map，拦截器多结果返回原因，取第一个结果
		map.put("count", (count.get(0)));
//		List<Map<String,Object>> list=new ArrayList<>();
//		用户信息添加到map
		List<User> listUser= userDao.selectPage(page);
		
		map.put("user", listUser);
//		list.add(map);
		return map;  
	}


	/**
	 * @param id
	 * @return
	 * @description
	 * 通过id查询单个用户
	 */
	@Override
	public List<User> selectUser(String id) {
		return userDao.selectUser(id);
	}


	/**
	 * @param id
	 * @return
	 * @description
	 * 根据id返回单个数据信息
	 */
	@Override
	public User selectById(String id) {
		List<List<Map<String,Object>>> role = userDao.selectRoleByUserID(id);
		User u = userDao.selectById(id);
		u.setRole(role.get(0).get(0).get("role")==null?"":role.get(0).get(0).get("role").toString());
		return u;
	}

	/**
	 * @param user
	 * @param session
	 * @return 是否登陆成功 true/false
	 * @description 登陆操作
	 */
	public String login(User user,HttpSession session){
		//查询账号密码对应的用户信息
		// 2021-3-16 需求：前端传的账号密码要加密,密码信息前端已加密，只需要查询即可
		//user.setPassword(MD5.EncoderByMd5(user.getPassword()));
		//通过账号和密码查询用户信息
		User _user = userDao.selectByPhonePwd(user);
		Map<String,Object> map =new HashMap<String,Object>();
		//获取参数
		verifyLicense.setParam("/verifyparam.properties");
		//验证证书
		Boolean vresult = verifyLicense.verify();
		//证书是否存在
		if (vresult) {
			//是否有账号密码都匹配的账号信息
			if(_user!=null){
				//状态值为2 账号停用
				if(_user.getState()==2){
					map.put("success", "false");
					map.put("message", "账号暂停服务");
					return JSONObject.fromObject(map).toString();
				}else if(_user.getState()==0){//状态值为 0 账号被锁定
					//TODO 判定账号能否解锁
					map.put("success", "false");
					map.put("message", "您已连续5次输入密码错误，账号已被锁定");
					return JSONObject.fromObject(map).toString();
				}else{//账号正常
					Department department= departmentDao.selectDepartment((_user.getDepartmentId())+"");
					session.setAttribute(Constant.SESSION_USERID, _user.getId());
					session.setAttribute(Constant.SESSION_USERNAME, _user.getName());
					session.setAttribute(Constant.SESSION_USERACCOUNT, _user.getPhone());

					//是否有所属部门
					if(_user.getDepartmentId()!=0){
						session.setAttribute(Constant.SESSION_DEPARTMENTNAME, department.getName());
						session.setAttribute(Constant.SESSION_DEPARTMENTID, _user.getDepartmentId());
						session.setAttribute(Constant.SESSION_DEPARTMENTNODEID, department.getDepartmentNodeId());
					}

					session.setAttribute(Constant.SESSION_ID, session.getId());
					//TODO 角色相关模块的处理
					//User userInfo = this.selectById(_user.getId());
					//session.setAttribute(Constant.SESSION_USERROLE, userInfo.getRole());
					session.setAttribute(Constant.SESSION_USERROLE, "1");
					map.put("success", "true");
					map.put("message", "登录成功");
					map.put("user", _user);
					return JSONObject.fromObject(map).toString();
				}
			}else{
				map.put("success", "false");
				map.put("message", "登录失败，账号或密码错误");
			}
			//计算当前时间之前的半小时的起始和截至时间
			LocalDateTime end = LocalDateTime.now();
			//当前时间-截止时间
			String endTime = end.format(dtf_time);
			//起始时间
			String startTime = end.minusMinutes(30).format(dtf_time);

			// 查询半小时以内近5次的账号登录状态
			List<Note> list=noteDao.selectLimitNote(user.getPhone(), startTime, endTime);
			Boolean res = false;
			//
			//判断登录密码次数过多，5次锁定锁定账号
			if(list.size()>=5){
				for(int i=0;i<list.size();i++){
					if(list.get(i).getResult()==1){
						res=true;
					}
				}
				//修改状态，为了保证不覆盖停用状态以及锁定的重复更新，更新时增加条件 状态位不为2和0
				if(res==false){
					userDao.updateByPhone(user.getPhone());
				}
			}
			map.put("success", "false");
			map.put("message", "登录失败，账号或密码错误");
			return JSONObject.fromObject(map).toString();
		//无授权
		}else {
			map.put("success", "false");
			map.put("message", "产品已过期");
			return JSONObject.fromObject(map).toString();
		}

	}
	
	public static void main(String[] agrs){
		
	   Date d=new Date();   
	   SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
	   System.out.println("今天的日期："+df.format(d));   
	   System.out.println("两天前的日期：" + df.format(new Date(d.getTime() -  30 * 60 * 1000)));  
	   System.out.println("三天后的日期：" + df.format(new Date(d.getTime() + 3 * 24 * 60 * 60 * 1000)));
	}
	

	/**
	 * @param session
	 * @return
	 * @description 验证session 信息
	 */
	public Boolean checkLogin(HttpSession session) {
		if(session.getAttribute(Constant.SESSION_USERID)!=null&&session.getId().equals(session.getAttribute(Constant.SESSION_ID))){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * @param session
	 * @return
	 * @description 登陆，清空session
	 */
	public Boolean loginOut(HttpSession session) {
		session.removeAttribute(Constant.SESSION_ID);
		session.removeAttribute(Constant.SESSION_USERID);
		session.removeAttribute(Constant.SESSION_DEPARTMENTID);
		return true;
	}


	/**
	 * @param session
	 * @return
	 * @description
	 * 获取角色用户信息
	 */
	@Override
	public String selectUserRole(HttpSession session) {

		List<List<Map<String,Object>>> uList = userDao.selectUserRole(session.getAttribute(Constant.SESSION_USERID).toString());
		if(uList.size()==1){
			return uList.get(0).get(0).get("role").toString();
		}else{
			return "";
		}

	}


	/**
	 * @param user
	 * @return
	 * @description
	 * 用户注册
	 */
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
	@Override
	public int registerUser(User user) {
		user.setPassword(MD5.EncoderByMd5(user.getPassword()));
		String userID = Uuid.getUUID().toString();
		user.setId(userID);
		//注册用户默认用户状态正常
		user.setState(1);
		//用户基本信息
		userDao.registerUser(user);
		//角色权限表，新用户默认为游客。
		String roleID = "visitor";
		userDao.insertVisitor(userID,roleID);
		return 2;
	}

	/**
	 * @param user
	 * @return
	 * @description
	 * 查询用户是否已经存在
	 */
	@Override
	public List<User> selectByName(User user) {
		return userDao.selectByName(user);
	}


	/**
	 * @param id
	 * @return
	 * @description
	 * 修改密码
	 */
	@Override
	public String updatePasswordById(String id,String password,String oldPassword) {
		int result=0;
		String pwd=MD5.EncoderByMd5(password);
		String oldPwd=MD5.EncoderByMd5(oldPassword);
		List<User> user=userDao.selectByPasswordId(id, oldPwd);
		if(user.size()>0){
			result=userDao.updatePasswordById(id,pwd);
		}
		
		return result >= 1 ? Constant.successMessage() : Constant.updateUserPasswordMessage();
	}
}

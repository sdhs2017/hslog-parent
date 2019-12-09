package com.jz.bigdata.common.department.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.jz.bigdata.roleauthority.user.dao.IUserDao;
import com.jz.bigdata.roleauthority.user.entity.User;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jz.bigdata.common.Constant;
//import org.springframework.transaction.annotation;
//import org.springframework.transaction.annotation;
import com.jz.bigdata.common.department.dao.IDepartmentDao;
import com.jz.bigdata.common.department.entity.Department;
import com.jz.bigdata.common.department.service.IDepartmentService;
import com.jz.bigdata.common.department.util.TreeBuilder;


/**
 * @author shichengyu
 * @date 2017年8月4日 上午10:57:00
 * @description
 */
@Service(value = "DepartmentService")
public class DepartmentServiceImpl implements IDepartmentService {
	
	static Logger logger = Logger.getLogger(DepartmentServiceImpl.class); 
	
	
	@Resource
	private IDepartmentDao departmentDao;

	@Resource
	private IUserDao userDao;

	/**
	 * @param department
	 * @return
	 * @description 添加节点
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional 
	public int insert(Department department) {
		int result =0;
//		id不为空级别加一
 		if(department.getId()!=0){
			department.setLevel(department.getLevel()+1);
		} 
//		int t=1/0;
//		添加数据
		result= departmentDao.insert(department);
		//判断最上级id是否存在
		if(department.getDepartmentNodeId()==0){
			//获取刚添加的公司id
			List<Department> de =	departmentDao.selectMaxId();
			Department depa=new Department();
			List<Map<String,Object>> lists=(List<Map<String, Object>>) de.get(0);
			//赋值
			depa.setId(Integer.valueOf((String) lists.get(0).get("maxId")));
			depa.setDepartmentNodeId(Integer.valueOf((String) lists.get(0).get("maxId")));
			//修改最上级id
			departmentDao.updateDepartmentNodeId(depa);
		}
//		修改上级是否有下级的属性
		departmentDao.updateSuperiorId(department);
		return result;
	}

	/**
	 * @param department
	 * @return
	 * @description 查询部门列表
	 */
	@Override
	public Map<String,Object> selectAll(Department department,HttpSession session) {
		
		Map<String,Object> map=new HashMap<String,Object>();
//		根据部门id查询用户
		if(department.getId()!=0){
			User user=new User();
			user.setDepartmentId(department.getId());
			//TODO
			//user.setRole(Integer.valueOf((String) session.getAttribute(Constant.SESSION_USERROLE)));
			user.setId((String) session.getAttribute(Constant.SESSION_USERID));
			List<User> listUser= userDao.selectAll(user);
			map.put("user", listUser);
			
		}
//		查询部门
		List<Department> listDepartment=departmentDao.selectAll(department);
		map.put("department", listDepartment);
		
		
//		return departmentDao.selectAll(department);
		return map;
	}

	/**
	 * @param department
	 * @return
	 * @description 修改部门信息
	 */
	@Override
	public int updateById(Department department) {
		return departmentDao.updateById(department);
	}

	/**
	 * @param id
	 * @return
	 * @description
	 * 根据id删除数据
	 */
//	@Transactional 
	@Override
	public Boolean delete(int id) {
		User user = new User();
		user.setDepartmentId(id);
//		查询用户信息
		List<User> list = userDao.selectAll(user);
		Department department=new Department();
		department.setId(id);
//		获取用户数据
		List<User> listUser=(List) list.get(0);
//		获取部门数据
		List<Department> listDepartment =departmentDao.selectAll(department);
//		判断是否有数据存在
		if (listUser.size()>0 ||listDepartment.size()>0) {
			return false;
		} else {
//			没有下一级数据删除部门
			int result= departmentDao.delete(id);
			if(result>0){
				return true;
			}
			return false;
		}

//		return result;
	}


	
	
	/**
	 * @return
	 * @description
	 * 获取数据拼接字符串树
	 */
	@Override
	public String selectAllDepartment() {
//		查询所有数据
		List<Department> list=departmentDao.selectAllDepartment();
		
		TreeBuilder treeBuilder = new TreeBuilder(list);
//		list转json输
		String departmentTree=treeBuilder.buildJSONTree();
		/*替换字符串*/
		String replaceName = departmentTree.replaceAll("name", "text");
		String replaceChildren=replaceName.replaceAll("menus", "children");
		
		return replaceChildren;
	}
}

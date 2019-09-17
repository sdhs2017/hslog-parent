package com.jz.bigdata.common.department.dao;

import java.util.List;

import com.jz.bigdata.common.department.entity.Department;

/**
 * @author shichengyu
 * @date 2017年8月4日 上午10:54:08
 * @description
 */
public interface IDepartmentDao {
	
	int insert(Department department);
	
	List<Department> selectAll(Department department);
	
	int updateById(Department department);
	
	int delete(int id);
	
	List<Department> selectSubordinate(int  id);
	
	int updateSuperiorId(Department department);
	
	List<Department> selectAllDepartment();
	
	Department selectDepartment(String id);
	
	List<Department> selectMaxId();
	
	int updateDepartmentNodeId(Department department);
	
//	List<Map<String,Object>>

}

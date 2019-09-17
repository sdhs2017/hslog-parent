package com.jz.bigdata.common.department.util;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.jz.bigdata.common.department.entity.Department;

/**
 * @author shichengyu
 * @date 2017年8月9日 上午9:39:45
 * @description
 * 拼接json樹
 */
public class TreeBuilder {
	//获取list集合信息
	List<Department> departments = new ArrayList<Department>();
	//构造函数
	public TreeBuilder(List<Department> departments) {
		super();
        this.departments= departments;
	}
	
	 
    /**
     * @return
     * 
     * list转json 构建树形结构
     */
    public String buildJSONTree() {
    	//获取list集合
		List<Department>departmentTree = buildTree();
//		String jsonArray = JSONArray.toJSONString(nodeTree);
		return JSONArray.toJSONString(departmentTree);
    }
	
    /**
     * @return
     * 构建树形结构方法
     */
    public List<Department> buildTree() {
        List<Department> treeNodes = new ArrayList<Department>();
        //获取所有的根节点
        List<Department>rootNodes = getRootNodes();
        //遍历根节点
        for (Department rootNode : rootNodes) {
        	//获取根节点下所有节点信息
	        buildChildNodes(rootNode);
	        //添加到list集合中
	        treeNodes.add(rootNode);
        }
        return treeNodes;
    }
    
    /**
     * @return
     * 获取所有的根节点信息
     */
    public List<Department> getRootNodes() {
	    List<Department> rootNodes = new ArrayList<Department>();
	    for (Department department : departments){
	    	//判断是否是根节点
	        if (rootNode(department)) {
	        	//根节点添加到list中
	        	rootNodes.add(department);
	        }
	    }
	    return rootNodes;
    }
    
    /**
     * @param department
     * @return
     * 循环遍历是否是根节点
     */
    public boolean rootNode(Department department) {
    	boolean isRootNode = true;
//    	循环判断是否是根节点
    	if(department.getSuperiorId()!=null){
    		for (Department depart: departments){
    			if (department.getSuperiorId()==depart.getId()) {
    				isRootNode= false;
    			    break;
    		    }
    		}
       
        }
        return isRootNode;
    }
    
    
    /**
     * @param department
     * 获取子菜单添加到集合中
     */
    public void buildChildNodes(Department department) {
		List<Department> children = getChildNodes(department); 
//		所有的数据添加到list
		if (!children.isEmpty()) {
		     for(Department child : children) {
		              buildChildNodes(child);
		     } 
		             
		     department.setMenus(children);
		}
    }
    
    /**
     * @param department
     * @return
     * 获取父节点下的子节点
     */
    public List<Department> getChildNodes(Department department) {
    	List<Department>childNodes = new ArrayList<Department>();
//    	魂环遍历获取父节点下的子节点
        for (Department depart : departments){
        	if(depart.getSuperiorId()!=null){
	        	if (department.getId()==depart.getSuperiorId()) {
	        		childNodes.add(depart);
	        	}
        	}
        }
        
        return childNodes;
    }
    

}

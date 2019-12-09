package com.jz.bigdata.common.function.dao;

import java.util.List;

import com.jz.bigdata.common.function.entity.Function;

/**
 * @author shichengyu
 * @date 2018年2月8日 上午11:27:23
 * @description
 */
public interface IFunctionDao {
	
	 List<Function> selectAllByRole(int role);
	
	 List<Function> selectAll();
	 
	 int insert(Function function);
}

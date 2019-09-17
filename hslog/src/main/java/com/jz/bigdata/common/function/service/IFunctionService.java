package com.jz.bigdata.common.function.service;

import java.util.List;
import java.util.Map;

import com.jz.bigdata.common.function.entity.Function;

/**
 * @author shichengyu
 * @date 2018年2月8日 上午11:39:12
 * @description
 */
public interface IFunctionService {
	
	List<Function> selectAllByRole(int role);
	
	Map<String, List<Function>> selectAll();
	
	int insert(Function function);

}

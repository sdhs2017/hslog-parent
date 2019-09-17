package com.jz.bigdata.common.ansj_dic.dao;

import java.util.List;

import com.jz.bigdata.common.ansj_dic.entity.Dic;

public interface IAnsj_dicDao {
	
	Dic selectByName(String name);
	
	int insert(Dic dic);
	
	int insertList(List<Dic> dic);
	
	
}

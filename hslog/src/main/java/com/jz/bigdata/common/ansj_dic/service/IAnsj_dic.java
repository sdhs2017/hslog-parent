package com.jz.bigdata.common.ansj_dic.service;

import java.util.List;

import com.jz.bigdata.common.ansj_dic.entity.Dic;

public interface IAnsj_dic {
	
	int insert(Dic dic);
	
	int insertList(List<Dic> diclist);
	
}

package com.jz.bigdata.common.ansj_dic.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.jz.bigdata.common.ansj_dic.dao.IAnsj_dicDao;
import com.jz.bigdata.common.ansj_dic.entity.Dic;
import com.jz.bigdata.common.ansj_dic.service.IAnsj_dic;

@Service(value = "Ansj_dicService")
public class Ansj_dicServiceImpl implements IAnsj_dic {

	@Resource
	private IAnsj_dicDao ansj_dicDao;
	@Override
	public int insert(Dic dic) {
		// TODO Auto-generated method stub
		return ansj_dicDao.insert(dic);
	}
	@Override
	public int insertList(List<Dic> diclist) {
		// TODO Auto-generated method stub
		return 0;
	}

}

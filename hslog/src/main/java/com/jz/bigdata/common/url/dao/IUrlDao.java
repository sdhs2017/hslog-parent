package com.jz.bigdata.common.url.dao;

import java.util.List;

import com.jz.bigdata.common.url.entity.Url;

/**
 * @ClassName IUrlDao
 * @Description 
 * @Author shi cheng yu
 * @Date 2019年4月18日 下午5:07:03
 */
public interface IUrlDao {
	/**
	 * @param safeStrategy
	 * @return
	 * 添加
	 */
	int insert(Url url);
	
	//查询url
	List<Url> selectAll();

}

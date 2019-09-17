package com.jz.bigdata.common.user.dao;

import java.util.List;

import com.jz.bigdata.common.user.entity.User;

/**
 * @author yiying
 * @date 2016年7月28日 上午11:47:23
 * @description 
 */
public interface IUserDao {
	
	int deleteByPrimaryKey(String userid);

    int insert(User user);

    int insertSelective(User user);

    User selectByPrimaryKey(String userid);
    
    List<User> selectByNamePwd(User user);

    int updateByPrimaryKeySelective(User user);

    int updateByPrimaryKey(User user);
}

package com.jz.bigdata.business.health.test.book.dao;

import java.util.List;
import java.util.Map;

import com.jz.bigdata.business.health.test.book.entity.Book;

public interface IBookDao {
	
	Book selectById(String id);
	
	int insert(Book book);
	
	List<Book> selectList(Book book);
	
	int updateById(Book book);
	
	int delete(String[] ids);
	
	List<List<Map<String,Object>>> getMapListResult();
	
	int number();
	
	List<Book> selectByquery(String query);
}

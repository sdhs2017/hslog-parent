package com.jz.bigdata.business.health.test.book.service;

import java.util.List;

import com.jz.bigdata.business.health.test.book.entity.Book;

public interface IBookService {
	
	public Book selectById(String id) throws Exception;
	
	public int insert(Book book) throws Exception;
	
	public List<Book> selectList(Book book) throws Exception;
	
	public int updateById(Book book) throws Exception;
	
	public int delete(String[] ids) throws Exception;
	
	public String getMapListResult() throws Exception;
	
	public String multiListResult() throws Exception;
	
	public String ajaxException() throws Exception; 
	
	public List<Book> selectByquery(String query) throws Exception;
	
}

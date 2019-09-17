package com.jz.bigdata.business.health.test.book.entity;

import java.io.Serializable;

import org.springframework.stereotype.Component;

/**
 * @author yiyang 2016-07-25
 * 书籍实体类
 */
//@Component("Book")
public class Book implements Serializable {
	private String id;
	private String bookName;
	private String writer;
	private String date;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public String getWriter() {
		return writer;
	}
	public void setWriter(String writer) {
		this.writer = writer;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
}

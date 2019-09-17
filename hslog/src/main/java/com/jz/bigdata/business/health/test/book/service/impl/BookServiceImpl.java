package com.jz.bigdata.business.health.test.book.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.abel533.echarts.Label;
import com.github.abel533.echarts.Option;
import com.github.abel533.echarts.code.Orient;
import com.github.abel533.echarts.code.Tool;
import com.github.abel533.echarts.code.X;
import com.github.abel533.echarts.code.Y;
import com.github.abel533.echarts.data.Data;
import com.github.abel533.echarts.series.Pie;
import com.github.abel533.echarts.style.ItemStyle;
import com.jz.bigdata.util.StringUtils;
import com.jz.bigdata.business.health.test.book.dao.IBookDao;
import com.jz.bigdata.business.health.test.book.entity.Book;
import com.jz.bigdata.business.health.test.book.service.IBookService;
import com.jz.bigdata.components.redis.test.test1.RedisService;
import com.jz.bigdata.components.redis.test.test1.TestRedis;

@Service(value="BookService")
public class BookServiceImpl implements IBookService {
	@Resource
	private IBookDao bookDao;
	public Book selectById(String id) {
		return this.bookDao.selectById(id);
	}

	@Override
	public int insert(Book book) throws Exception{
		return this.bookDao.insert(book);
	}

	@Override
	public int updateById(Book book) throws Exception{
		return this.bookDao.updateById(book);
	}

	@Override
	public int delete(String[] ids) throws Exception{
		return this.bookDao.delete(ids);
	}

	@Override
	public List<Book> selectList(Book book) throws Exception{
//		List<Book> list =new ArrayList<Book>();
		
//		System.out.println(list.get(1));
		return this.bookDao.selectList(book);
	}

	@Override
	public String getMapListResult() throws Exception {
		//获取结果集数据对象
		List<List<Map<String,Object>>> resultList = this.bookDao.getMapListResult();
		//默认获取第一个结果集
		List<Map<String,Object>> result = resultList.get(0);
		//遍历
		for(Map<String,Object> map:result)  
        {  
            String value = (String) map.get("id");
            System.out.println(value);
        } 
		String jsonResult = StringUtils.mapListToJson(result);
		return jsonResult;
	}
	private List<Map<String,Object>> getListMap(){
		//获取结果集数据对象
		List<List<Map<String,Object>>> resultList = this.bookDao.getMapListResult();
		//默认获取第一个结果集
		List<Map<String,Object>> result1 = resultList.get(0);
		return result1;
	}
	private Map<String,Object> mapList(){
		//第二个结果
		Map<String,Object> map = new HashMap<String,Object>();
		return map;
	}
	private String getEchart() throws Exception{
		//echart对象
		Option option = new Option();
		ItemStyle dataStyle = new ItemStyle();
	    dataStyle.normal().label(new Label().show(false)).labelLine().show(false);
	
	    ItemStyle placeHolderStyle = new ItemStyle();
	    placeHolderStyle.normal().color("rgba(0,0,0,0)").label(new Label().show(false)).labelLine().show(false);
	    placeHolderStyle.emphasis().color("rgba(0,0,0,0)");
	
	    option.title().text("你幸福吗？")
	            .subtext("From ExcelHome")
	            .sublink("http://e.weibo.com/1341556070/AhQXtjbqh")
	            .x(X.center)
	            .y(Y.center)
	            .itemGap(20)
	            .textStyle().color("rgba(30,144,255,0.8)")
	            .fontFamily("微软雅黑")
	            .fontSize(35)
	            .fontWeight("bolder");
	    option.tooltip().show(true).formatter("{a} <br/>{b} : {c} ({d}%)");
	    option.legend().orient(Orient.vertical)
	            .x("(function(){return document.getElementById('main').offsetWidth / 2;})()")
	            .y(56)
	            .itemGap(12)
	            .data("68%的人表示过的不错", "29%的人表示生活压力很大", "3%的人表示“我姓曾”");
	    option.toolbox().show(true).feature(Tool.mark, Tool.dataView, Tool.restore, Tool.saveAsImage);
	
	    Pie p1 = new Pie("1");
	    p1.clockWise(false).radius(125, 150).itemStyle(dataStyle)
	            .data(new Data("68%的人表示过的不错", 68), new Data("invisible", 32).itemStyle(placeHolderStyle));
	
	    Pie p2 = new Pie("2");
	    p2.clockWise(false).radius(100, 125).itemStyle(dataStyle)
	            .data(new Data("29%的人表示生活压力很大", 29), new Data("invisible", 71).itemStyle(placeHolderStyle));
	
	    Pie p3 = new Pie("3");
	    p3.clockWise(false).radius(75, 100).itemStyle(dataStyle)
	            .data(new Data("3%的人表示“我姓曾”", 3), new Data("invisible", 97).itemStyle(placeHolderStyle));
	
	    option.series(p1, p2, p3);
	    String op = "";
		op = StringUtils.optionToJson(option);
		return op;
	}
	/**
	 * @return json
	 * @description 多结果数据返回
	 */
	public String multiListResult() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		List<Map<String,Object>> result1 = getListMap();
		Map<String,Object> booklist = mapList();
		String op =  getEchart();
	    //将结果放入map
	    map.put("r1", result1);
		map.put("r2", booklist);
	    map.put("r3", op);
		JSONObject result = new JSONObject(map);
		return result.toString();
	}
	

	@Override
	public String ajaxException() throws Exception {
		throw new Exception();
	}

	/**
	 * @param query
	 * @return
	 * @throws Exception
	 * @description 查询redis信息
	 */
	@Override
	public List<Book> selectByquery(String query) throws Exception {
//		获取redis配置
		RedisService redisService =(RedisService) TestRedis.app.getBean("redisService");
		List<Book> list=new ArrayList<Book>();
//		定义rediskey
		String queryredis=null;
//		String list= (String) redisService.get(query);
//		判断参数是否为null
		if(query.equals("")||query==null){
			queryredis="all";
		}else{
			queryredis=query;
		}
//		通过key获取redis数据判断是否有值
		byte[] byt=redisService.get(queryredis.getBytes());
//		 beginTime = System.currentTimeMillis();  
//		判断redis是否有值
		if(byt==null||byt.equals("")||byt.length<=0){
//			从数据库取值
		    list=bookDao.selectByquery(query);
//		    值放到redis
			redisService.set(queryredis.getBytes(),TestRedis.serialize(list));
			return list;
		}else{
//			redis取值反序列化成list
			list=(List<Book>) TestRedis.unserizlize(byt);
			return list;
		}
		
	}

}

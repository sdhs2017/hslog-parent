package com.jz.bigdata.business.health.test.book.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.abel533.echarts.Option;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.Magic;
import com.github.abel533.echarts.code.Tool;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.feature.MagicType;
import com.github.abel533.echarts.series.Line;
import com.jz.bigdata.business.health.test.book.entity.Book;
import com.jz.bigdata.business.health.test.book.service.IBookService;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.util.StringUtils;

/**
 * @author 张艺阳
 * @date 2016年7月25日 下午4:41:06
 * @description 书籍相关功能模块
 */
@Controller
@RequestMapping("/book")
public class BookController {
	
	@Resource(name="BookService")
	private IBookService bookService;
	
	/**
	 * @param request
	 * @param book
	 * @return 是否成功
	 * @description 添加操作
	 */
	@ResponseBody
	@RequestMapping("/insert")
	public String insert(HttpServletRequest request,Book book) throws Exception{
		int result = this.bookService.insert(book);
		return result>=1?Constant.successMessage():Constant.failureMessage();
	}
	
	/**
	 * @param request
	 * @param book
	 * @return 返回一个List，一般用于Grid加载
	 * @description 查询操作
	 */
	@ResponseBody
	@RequestMapping("/selectList")
	public List<Book> selectList(HttpServletRequest request,Book book) throws Exception{
		System.out.println(book.getWriter());
		System.out.println(book);
		return this.bookService.selectList(book);
	}
	
	/**
	 * @param request
	 * @return 单个Book对象查询返回，一般用于Form加载
	 */
	@ResponseBody
	@RequestMapping("/selectById")
	public Book selectById(HttpServletRequest request) throws Exception{
		String id = request.getParameter("id");
		return this.bookService.selectById(id);
	}
	
	/**
	 * @param request
	 * @param book 需要传递entity对象
	 * @return 是否成功
	 * 实现增、改功能，根据传递实体的主键（id）是否为空判定是insert还是update。
	 */
	@ResponseBody
	@RequestMapping("/upsert")
	public String upsert(HttpServletRequest request,Book book) throws Exception{
		//结果一般命名为result
		int result = 0;
		//如果id为空，进行添加操作
		if(book.getId()==null||book.getId().isEmpty()){
			result = this.bookService.insert(book);
		}else{//更新操作
			result = this.bookService.updateById(book);
		}
		return result>=1?Constant.successMessage():Constant.failureMessage();
	}
	
	/**
	 * @param request
	 * @return 根据删除成功条数 确定是否删除成功
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/delete")
	public String delete(HttpServletRequest request)throws Exception {
		int result = 0;
		String[] ids = request.getParameter("ids").split(",");
		if(ids.length>0){
			result = this.bookService.delete(ids);
		}
		return result>=1?Constant.successMessage():Constant.failureMessage();
		
	}
	
	/**
	 * @param request
	 * @return Echart-java转化举例
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping("/getEchart")
	public String getEchart(HttpServletRequest request) throws Exception{
		Option option = new Option();
		option.legend("高度(km)与气温(°C)变化关系");
	 
	    option.toolbox().show(true).feature(Tool.mark, Tool.dataView, new MagicType(Magic.line, Magic.bar), Tool.restore, Tool.saveAsImage);
	 
	    option.calculable(true);
	    option.tooltip().trigger(Trigger.axis).formatter("{value}");
	 
	    ValueAxis valueAxis = new ValueAxis();
	    valueAxis.axisLabel().formatter("{value} °C");
	    option.xAxis(valueAxis);
	 
	    CategoryAxis categoryAxis = new CategoryAxis();
	    categoryAxis.axisLine().onZero(false);
	    categoryAxis.axisLabel().formatter("{value}");
	    categoryAxis.boundaryGap(false);
	    categoryAxis.data(0, 10, 20, 30, 40, 50, 60, 70, 80);
	    option.yAxis(categoryAxis);
	 
	    Line line = new Line();
	    line.smooth(true).name("高度(km)与气温(°C)变化关系").data(15, -50, -56.5, -46.5, -22.1, -2.5, -27.7, -55.7, -76.5).itemStyle().normal().lineStyle().shadowColor("rgba(0,0,0,0.4)");
	    option.series(line);
		return StringUtils.optionToJson(option);
	}
	@ResponseBody
	@RequestMapping("/getMapListResult")
	public String multiListResult() throws Exception{
		return this.bookService.multiListResult();
	}
	
	@ResponseBody
	@RequestMapping("/getException")
	public String testException(HttpServletRequest request) throws Exception{
		String state = request.getParameter("state");
		switch (state){
			case "ajax":
				return this.bookService.ajaxException();//非正常的异常抛出
			case "successstate0":
				return Constant.successMessage();
			case "falsestate0":
				return Constant.failureMessage(2, "操作出错，有问题");
			default :
				return Constant.failureMessage(3, "为啥呢？");
		}
	}
	
	/**
	 * @param request
	 * @param book
	 * @return 返回一个List，一般用于Grid加载
	 * @description 查询操作
	 */
	@ResponseBody
	@RequestMapping("/selectListRedis")
	public List<Book> selectListRedis(HttpServletRequest request,Book book) throws Exception{
		return this.bookService.selectList(book);
	}
	
	/**
	 * @param request
	 * @param book
	 * @return 返回一个List，一般用于Grid加载
	 * @description 查询操作(带参数查询)
	 */
	@ResponseBody
	@RequestMapping("/selectByquery")
	public List<Book> selectByquery(HttpServletRequest request) throws Exception{
		
		String query =request.getParameter("query");
		return this.bookService.selectByquery(query);
		
//		return this.bookService.selectList(book);
	}
	
	
}

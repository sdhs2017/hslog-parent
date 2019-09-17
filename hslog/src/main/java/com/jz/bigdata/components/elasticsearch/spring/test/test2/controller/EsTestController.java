/*
package com.jz.bigdata.components.elasticsearch.spring.test.test2.controller;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.business.health.test.book.entity.Book;
import com.jz.bigdata.framework.spring.es.crud.entity.ArticleInf;
//import com.jz.bigdata.framework.spring.es.elasticsearch.ClientTemplate;
import com.hs.elasearch.template.ClientTemplate;
import com.jz.bigdata.framework.spring.es.example.EsTemplateTest;
import com.jz.bigdata.framework.spring.es.example.entity.Article;

*/
/**
 * @author Dy
 *
 *//*

@Controller
@RequestMapping("/estest")
public class EsTestController {
	
	@Autowired protected ClientTemplate clientTemplate;
	
//	@Autowired
//	private ArticleInfSearchRepository articleInfSearchRepository;
	
//	@Autowired
//    ElasticsearchTemplate elasticsearchTemplate;

	*/
/**
	 * @param request
	 * @param book
	 * @return 是否成功
	 * @description 添加操作
	 *//*

	@ResponseBody
	@RequestMapping("/test")
	public String test2(HttpServletRequest request,Book book) throws Exception{
		
		System.out.println(123);
		EsTemplateTest et = new EsTemplateTest();
		
		String indices = "article";
		String type = "doc";
		String id = "2001";
		
		Article a = new Article();
		a.setId(indices+"_"+type+"_"+id+"_"+"20170906"+"_"+id);
		a.setAuthor("author"+id);
		a.setTitle("title"+id);
		a.setContent("content"+id);
		a.setDescribe("describe"+id);
		
//		boolean result = elasticsearchTemplate.indexExists("article");
//		System.out.println(result);
		
//		et.insertOrUpdate(indices, type, id, generateJson(a),clientTemplate);
		
		
		return "success";
	}
	
//	@ResponseBody
//	@RequestMapping("/test")
//	public String test(HttpServletRequest request,Book book) throws Exception{
//		ArticleInf a = new ArticleInf();
//		a.setArticleTitle("article1");
//		a.setReleaseTime(new Date());
////		articleInfSearchRepository.save(a);
//		
//		System.out.println("--------------success---------------");
//		return "success";
//	}
	
	public static XContentBuilder generateJson(Article article){
		XContentBuilder builder = null;
		String resultJson;
		try{
			builder = jsonBuilder()
				    .startObject()
				        .field("id", article.getId())
				        .field("title", article.getTitle())
				        .field("author", article.getAuthor())
				        .field("content", article.getContent())
				        .field("describe", article.getDescribe())
				    .endObject();
			resultJson = builder.string();
//			System.out.println(resultJson);
		}catch(Exception e){
			e.printStackTrace();
		}
		return builder;
	}
	
}
*/

package com.jz.bigdata.ai.baidu.nlp.conn.impl;

import java.util.HashMap;

import org.json.JSONObject;

import com.baidu.aip.nlp.AipNlp;

public class NplDemo {
	
	public void sample(AipNlp client) {
//	    String text = "百度是一家高科技公司";
//	    String text = "词法分析接口向用户提供分词、词性标注、专名识别三大功能；能够识别出文本串中的基本词汇（分词），对这些词汇进行重组、标注组合后词汇的词性，并进一步识别出命名实体";
		String text = "今天天气怎么样";
		
	    // 传入可选参数调用接口
	    HashMap<String, Object> options = new HashMap<String, Object>();
	    
	    options.put("mode", 1);
	    
	    // 词法分析
	    JSONObject res = client.lexer(text, options);
	    System.out.println(res.toString(2));

	}

}

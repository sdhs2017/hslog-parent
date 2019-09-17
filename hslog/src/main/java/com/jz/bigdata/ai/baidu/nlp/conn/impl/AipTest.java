package com.jz.bigdata.ai.baidu.nlp.conn.impl;

import org.json.JSONObject;

import com.baidu.aip.nlp.AipNlp;

public class AipTest {
	
	public static void main(String[] args) {
		
		AipNlp client = AipConnImpl.getInstance().getAipConn();
		
        // 调用接口
//        String text = "百度是一家高科技公司";
//		String text = "词法分析接口向用户提供分词、词性标注、专名识别三大功能；能够识别出文本串中的基本词汇（分词），对这些词汇进行重组、标注组合后词汇的词性，并进一步识别出命名实体";
//        JSONObject res = client.lexer(text, null);
//        System.out.println(res.toString(2));
		
		NplDemo nplDemo = new NplDemo();
		nplDemo.sample(client);
		
	}

}

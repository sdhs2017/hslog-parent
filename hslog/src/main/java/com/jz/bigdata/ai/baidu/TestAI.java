package com.jz.bigdata.ai.baidu;

import org.json.JSONObject;

import com.baidu.aip.nlp.AipNlp;
import com.jz.bigdata.ai.baidu.nlp.AipConnInfo;
import com.jz.bigdata.ai.baidu.nlp.AipConnection;
import com.jz.bigdata.ai.baidu.nlp.AipConnection2;

public class TestAI {
	
	public static void main(String[] args) {
		
		//单例
//		AipNlp client = AipConnection.INSTANCE.getAipNlp();
		
		//私有内部构造器
//		AipNlp client = AipConnInfo.getInstance().getAipNlp();
		
		//内部静态类
		AipNlp client = AipConnection2.getInstance().getAipNlp();
		
		
        // 调用接口
        String text = "百度是一家高科技公司";
        JSONObject res = client.lexer(text, null);
        System.out.println(res.toString(2));
	}

}

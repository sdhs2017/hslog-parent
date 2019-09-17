package com.jz.bigdata.ai.baidu.nlp.conn.impl;

import com.baidu.aip.nlp.AipNlp;
import com.jz.bigdata.ai.baidu.nlp.conn.AipConn;

/**
 * 单例模式：枚举，工厂
 * @author Savilio
 *
 */
public enum AipConnImpl implements AipConn{
	
	INSTANCE{
		@Override
		public AipNlp getAipConn() {
			return client;
		}
	};
	
	public static final String APP_ID = "16716325";
    public static final String API_KEY = "a6goHeDwLycs5rs7FY6tqRSZ";
    public static final String SECRET_KEY = "6VWf4ZftSQO0kLWaFGEHvjBsGEZaZfWw";
    
    private static AipNlp client;
    
    
    public static AipConn getInstance() {
    	return AipConnImpl.INSTANCE;
    }
    
    private AipConnImpl() {
    	init();
    }
    
	/**
	 * 	初始化链接
	 */
	private void init() {
		client = new AipNlp(APP_ID, API_KEY, SECRET_KEY);
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
//        client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
//        client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
        System.setProperty("aip.log4j.conf", "log4j_aip.properties");
	}

}

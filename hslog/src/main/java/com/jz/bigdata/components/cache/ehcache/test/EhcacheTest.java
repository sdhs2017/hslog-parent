package com.jz.bigdata.components.cache.ehcache.test;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import com.jz.bigdata.components.cache.ehcache.service.EhCacheTestService;


@Configuration
@EnableCaching
public class EhcacheTest implements EhCacheTestService{

	@Cacheable(value="cacheTest",key="#param")
	public String getTestInfo(String param) {
		Long timestamp = System.currentTimeMillis();
        return timestamp.toString();
	}
	
	public static void main(String[] args){
		String info = new EhcacheTest().getTestInfo("test");
		System.out.println(info);
	}
	
	

}

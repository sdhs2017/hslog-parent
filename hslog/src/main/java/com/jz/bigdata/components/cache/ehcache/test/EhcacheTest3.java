package com.jz.bigdata.components.cache.ehcache.test;

import java.util.Date;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import com.googlecode.ehcache.annotations.TriggersRemove;
import com.googlecode.ehcache.annotations.When;


@Configuration
@EnableCaching
public class EhcacheTest3 {
	
	
	
	
//	@Cacheable(cacheName="cacheTest")
//	@Cacheable(value="cacheTest",key="#param")
	@TriggersRemove(cacheName="cacheTest", when=When.AFTER_METHOD_INVOCATION, removeAll=true) 
	public int test(){
//		int sum=0;
//		for(int i=0;i<1000000000;i++){
//			sum+=i;
//		}
		return sum();
	}
	
	public static int sum(){
		int sum=0;
		for(int i=0;i<1000000000;i++){
			sum+=i;
		}
		return sum;
	}
	
	public static void main(String[] args){
//		Date start=new Date();   
//		int y = new EhcacheTest3().test();
//		System.out.println(y);
//		Date end=new Date(); 
//		
//		System.out.println((end.getTime()-start.getTime()));
//		
//		Date start2=new Date();   
//		int y2 = new EhcacheTest3().test();
//		System.out.println(y2);
//		Date end2=new Date(); 
//		
//		System.out.println((end2.getTime()-start2.getTime()));
	}

}

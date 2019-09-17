package com.jz.bigdata.components.cache.ehcache.test;

import java.util.Date;

public class EhcacheMain {
	
	public static void main(String[] args){
		EhcacheTest3 ec = new EhcacheTest3();
		Date start=new Date();   
		int y = ec.test();
		System.out.println(y);
		Date end=new Date(); 
		
		System.out.println((end.getTime()-start.getTime()));
		
		
		Date start2=new Date();   
		int y2 = ec.test();
		System.out.println(y2);
		Date end2=new Date(); 
		
		System.out.println((end2.getTime()-start2.getTime()));
			
	}

}

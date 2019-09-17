package com.jz.bigdata.components.cache.ehcache.test;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class EhcacheTest2 {
	
	public static void main(String[] args){
		
//		ClassLoader.getResourceAsStream(“com/explorers/abc.jpg”)
		
		// Create a cache manager
//	    final CacheManager cacheManager = new CacheManager();
		CacheManager cacheManager = CacheManager.create("./src/main/resources/ehcache-setting.xml");
		

	    // create the cache called "helloworld"
	    Cache cache = cacheManager.getCache("helloworld");

	    // create a key to map the data to
	    String key = "greeting";

	    // Create a data element
	    Element putGreeting = new Element(key, "Hello, World!");

	    // Put the element into the data store
	    cache.put(putGreeting);

	    // Retrieve the data element
	    Element getGreeting = cache.get(key);

	    // Print the value
	    System.out.println(getGreeting.getObjectValue());
	}
	
}

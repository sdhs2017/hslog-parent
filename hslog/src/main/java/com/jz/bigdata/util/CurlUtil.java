package com.jz.bigdata.util;

import static org.toilelibre.libe.curl.Curl.curl;

import org.apache.http.HttpResponse;

import static org.toilelibre.libe.curl.Curl.$;

public class CurlUtil {
	
	
	
	public static void main(String[] args) {
		saltstack_test2();
//		es_curl_test2();
	}
	
	
	public static void saltstack_test() {
		HttpResponse response = curl("-k https://192.168.1.109:8000/login -H 'Accept: application/x-yaml'  -d username='saltapi' -d password='123456'  -d eauth='pam'");
		
		System.out.println(response.toString());
	}
	
	public static void saltstack_test2() {
		String result = $("curl -k https://192.168.1.109:8000/login -H \"Accept: application/x-yaml\"  -d username='saltapi' -d password='123456'  -d eauth='pam'");
		System.out.println(result.toString());
	}
	
	public static void es_curl_test() {
		HttpResponse res = curl("-XGET \"http://192.168.2.182:9200/packet-analysis3\"");
		System.out.println(res.toString());
//		System.out.println(res.get);
	}
	
	public static void es_curl_test2() {
		String result = $("curl -XGET \"http://192.168.2.182:9200/packet-analysis3\"");
		System.out.println(result);
	}

}

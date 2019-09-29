package com.jz.bigdata.common.updateIp;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.common.manage.service.IManageService;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.DescribeLog;

@Controller  
@RequestMapping("/ip") 
public class UpdateIpController {
	
	@Resource(name = "configProperty")
	private ConfigProperty configProperty;

	@Resource(name="manageService")
	private IManageService manageService;
	
	@ResponseBody
	@RequestMapping("/getIp")
	
	@DescribeLog(describe="获取本地ip地址")
	public String updataIp(){
		Map<String,Object> map = new HashMap<>();
		try {
			map.put("success",true);
			map.put("message",configProperty.getHost_ip());
			return JSONArray.fromObject(map).toString();
		}catch (Exception e){
			e.printStackTrace();
			map.put("success",false);
			map.put("message","获取IP失败！");
			return JSONArray.fromObject(map).toString();
		}
	}
	
	@ResponseBody
	@RequestMapping("/updateIp")
	@DescribeLog(describe="修改ip地址")  
	public Map<String, String> updateIp(String user,String passwd,String host,String ip){
		String url="sh /opt/jzlog/ip_modify "+ip;
		return manageService.doshell(url, user, passwd, host);
		
	}
	

}

package com.jz.bigdata.common.product.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonParser;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.start_execution.cache.ConfigurationCache;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.DescribeLog;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.util.hash.Hash;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/6/2 15:30
 * @Description: 用于切换不同系统（包括登录页，首页，logo，版本号等信息）
 */
@Slf4j
@Controller
@RequestMapping("/product")
public class ProductController {
    @Resource(name ="configProperty")
    private ConfigProperty configProperty;

    @ResponseBody
    @RequestMapping(value="/getProductInfo",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="获取系统信息")
    public String getProductInfo() {
        try{
            Map<String,Object> result = new HashMap<>();
            result.put("success","true");
            Map<String,Object> message = new HashMap<>();
            message.put(Constant.LOGO_PATH,ConfigurationCache.INSTANCE.getConfigurationCache().getIfPresent(Constant.LOGO_PATH).toString());
            message.put(Constant.VERSION,ConfigurationCache.INSTANCE.getConfigurationCache().getIfPresent(Constant.VERSION).toString());
            message.put(Constant.INDEX_PAGE,ConfigurationCache.INSTANCE.getConfigurationCache().getIfPresent(Constant.INDEX_PAGE).toString());
            result.put("message", message);
            return JSONObject.toJSONString(result);
        }catch (Exception e){
            log.error("获取系统信息失败",e.getMessage());
            return Constant.failureMessage("获取系统信息失败!");
        }

    }
}

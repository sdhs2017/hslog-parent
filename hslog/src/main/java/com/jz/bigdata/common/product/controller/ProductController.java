package com.jz.bigdata.common.product.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonParser;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.DescribeLog;

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
            result.put("message", JSONObject.parse(configProperty.getProduct_info()));
            return JSONObject.toJSONString(result);
        }catch (Exception e){
            return Constant.failureMessage("获取系统信息失败!");
        }

    }
}

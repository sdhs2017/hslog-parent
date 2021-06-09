package com.jz.bigdata.common.homepage.controller;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.DescribeLog;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @Author: yiyang
 * @Date: 2021/5/28 15:09
 * @Description: 获取配置文件中的首页路径，目前有日志首页和数据安全治理首页（数据源管理）
 */
@Controller
@RequestMapping("/homepage")
public class HomePageController {
    @Resource(name ="configProperty")
    private ConfigProperty configProperty;

    @ResponseBody
    @RequestMapping(value="/getHomepageUrl",produces = "application/json; charset=utf-8")
    @DescribeLog(describe="获取首页路径")
    public  String getHomepageUrl() {
        try{
            return Constant.successMessage(configProperty.getHomepage_url());
        }catch (Exception e){
            return Constant.failureMessage("首页获取失败");
        }

    }

}

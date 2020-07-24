package com.jz.bigdata.common.configuration.controller;

import com.jz.bigdata.common.configuration.cache.ConfigurationCache;
import net.sf.json.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.*;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.asset.entity.Asset;
import com.jz.bigdata.common.configuration.entity.Configuration;
import com.jz.bigdata.common.configuration.service.IConfigurationService;
import com.jz.bigdata.util.DescribeLog;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import scala.collection.immutable.Stream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @description
 * 全局配置项操作
 */
@Controller
@RequestMapping("/configuration")
public class ConfigurationController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "ConfigurationService")
    private IConfigurationService configurationService;

    @ResponseBody
    @RequestMapping(value="/update.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="修改配置项")
    public String update(HttpServletRequest request) {
        //参数处理
        Map<String, String[]> params = request.getParameterMap();
        List<Configuration> list = new ArrayList<>();
        for(Map.Entry<String,String[]> entity:params.entrySet()){
            Configuration configuration = new Configuration();
            configuration.setConfiguration_key(entity.getKey());
            configuration.setConfiguration_value(entity.getValue()[0]);
            list.add(configuration);
        }
        try{
            int result = configurationService.update(list);
            ConfigurationCache.INSTANCE.init(configurationService);//更新缓存
            if(result>0){
                return Constant.successMessage();
            }else{
                return Constant.failureMessage("修改配置信息失败");
            }
        }catch (Exception e){
            logger.error("修改配置信息失败"+e.getMessage());
            return Constant.failureMessage("修改配置信息失败");
        }
    }
    @ResponseBody
    @RequestMapping(value="/selectByKey.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="通过key获取配置信息")
    public String selectByKey(HttpServletRequest request, Configuration configuration, HttpSession session) {
        try{
            List<Configuration> result = configurationService.selectByKey(configuration.getConfiguration_key());
            //查询回的数据不为空且长度为1，证明查询成功，其他情况都返回空
            if(null!=result&&result.size()==1){
                return Constant.successData(result.get(0).getConfiguration_value());
            }else{
                return Constant.failureMessage("未找到该配置项");
            }
        }catch (Exception e){
            logger.error("查询配置信息失败"+e.getMessage());
            return Constant.failureMessage("查询配置信息失败");
        }
    }
    @ResponseBody
    @RequestMapping(value="/selectAll.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="获取所有配置信息")
    public String selectAll(HttpServletRequest request, Configuration configuration, HttpSession session) {
        try{
            List<Configuration> result = configurationService.selectAll();
            //数据库中无数据
            if(result==null||result.size()==0){
                return Constant.successData("");
            }else{
                return Constant.successData(JSONArray.fromObject(result).toString());
            }

        }catch (Exception e){
            logger.error("查询配置信息失败"+e.getMessage());
            return Constant.failureMessage("查询配置信息失败");
        }
    }
}

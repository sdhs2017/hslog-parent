package com.jz.bigdata.common.configuration.controller;

import com.jz.bigdata.business.logAnalysis.log.service.IlogService;
import com.jz.bigdata.common.start_execution.cache.ConfigurationCache;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.configuration.entity.Configuration;
import com.jz.bigdata.common.configuration.service.IConfigurationService;
import com.jz.bigdata.util.DescribeLog;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description
 * 全局配置项操作
 */
@Slf4j
@Controller
@RequestMapping("/configuration")
public class ConfigurationController {

    @Resource(name = "ConfigurationService")
    private IConfigurationService configurationService;
    @Resource(name="logService")
    private IlogService logService;

    @ResponseBody
    @RequestMapping(value="/update.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="修改配置项")
    public String update(HttpServletRequest request) {
        //参数处理
        Map<String, String[]> params = request.getParameterMap();
        List<Configuration> list = new ArrayList<>();
        //批量参数的处理
        //涉密
        // 可能进行参数有效性的后端验证
        for(Map.Entry<String,String[]> entity:params.entrySet()){
            Configuration configuration = new Configuration();
            configuration.setConfiguration_key(entity.getKey());
            configuration.setConfiguration_value(entity.getValue()[0]);//默认只取第一个的值
            list.add(configuration);
//            //涉密，增加对参数值有效性核对
//            switch(entity.getKey()){
//                case "disk_data_watermark":case "disk_data_watermark_high":case "disk_system_watermark":
//                    try{
//                        int value = Integer.parseInt(entity.getValue()[0]);
//                        if(!(value>0&&value<=100)){
//                            return Constant.failureMessage();
//                        }
//                    }catch(Exception e){
//                        log.error("参数错误");
//                        return Constant.failureMessage();
//                    }
//                break;
//                case "es_bulk" :case "es_storage_day" :case "pwd_expire_day" :case "pwd_length" :case "pwd_try" :case "session_timeout" :
//                    try{
//                        int value = Integer.parseInt(entity.getValue()[0]);
//                        if("es_bulk".equals(entity.getKey())&&!(value>0)){
//                            return Constant.failureMessage();
//                        }
//                        if("es_storage_day".equals(entity.getKey())&&!(value>90)){
//                            return Constant.failureMessage();
//                        }
//                        if("pwd_expire_day".equals(entity.getKey())&&!(value>0&&value<=7)){
//                            return Constant.failureMessage();
//                        }
//                        if("pwd_length".equals(entity.getKey())&&!(value>=8)){
//                            return Constant.failureMessage();
//                        }
//                        if("pwd_try".equals(entity.getKey())&&!(value>0&&value<=5)){
//                            return Constant.failureMessage();
//                        }
//                        if("session_timeout".equals(entity.getKey())&&!(value>0&&value<=10)){
//                            return Constant.failureMessage();
//                        }
//                    }catch(Exception e){
//                        log.error("参数错误");
//                    }
//                break;
//
//
//            }
        }
        try{
            int result = configurationService.update(list);
            ConfigurationCache.INSTANCE.init(configurationService);//更新缓存
            if(request.getParameter(Constant.ES_STORAGE_DAY)!=null){
                //更新index生命周期
                Boolean LifeCycleResult = logService.createLifeCycle("hs_policy",Long.parseLong(request.getParameter(Constant.ES_STORAGE_DAY)));
                if (!LifeCycleResult){
                    log.error("更新索引生命周期管理失败！！！");
                }else{
                    log.info("更新索引生命周期管理成功！！！");
                }
            }

            //重新初始化es的bulk_processer
            if(request.getParameter("es_bulk")!=null&&request.getParameter("concurrent_requests")!=null){
                logService.bulkProcessor_init(Integer.valueOf(request.getParameter("es_bulk")),Integer.valueOf(request.getParameter("concurrent_requests")));
            }
            if(result>0){
                return Constant.successMessage();
            }else{
                return Constant.failureMessage("修改配置信息失败");
            }
        }catch (Exception e){
            log.error("修改配置信息失败"+e.getMessage());
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
            log.error("查询配置信息失败"+e.getMessage());
            return Constant.failureMessage("查询配置信息失败");
        }
    }
    @ResponseBody
    @RequestMapping(value="/selectPwdExpireDay.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="获取密码超时时间")
    public String selectPwdExpireDay(HttpServletRequest request, Configuration configuration, HttpSession session) {
        try{

            List<Configuration> result = configurationService.selectByKey(Constant.PWD_EXPIRE_DAY_NAME);
            //查询回的数据不为空且长度为1，证明查询成功，其他情况都返回空
            if(null!=result&&result.size()==1){
                return Constant.successData(result.get(0).getConfiguration_value());
            }else{
                return Constant.failureMessage("未找到该配置项");
            }
        }catch (Exception e){
            log.error("查询配置信息失败"+e.getMessage());
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
            log.error("查询配置信息失败"+e.getMessage());
            return Constant.failureMessage("查询配置信息失败");
        }
    }
}

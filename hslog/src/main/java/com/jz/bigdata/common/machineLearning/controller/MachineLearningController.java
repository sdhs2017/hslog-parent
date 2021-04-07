package com.jz.bigdata.common.machineLearning.controller;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.machineLearning.service.IMachineLearningService;
import com.jz.bigdata.util.DescribeLog;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/3/17 16:53
 * @Description: AI controller 用于具体业务
 */
@Slf4j
@Controller
@RequestMapping("/ml")
public class MachineLearningController {
    @Resource(name = "machineLearningService")
    private IMachineLearningService iMachineLearningService;

    @ResponseBody
    @RequestMapping("/getDetectResult")
    @DescribeLog(describe = "获取检测结果")
    public String getDetectResult(HttpServletRequest request) {
        try{
            Map<String, Object> result = iMachineLearningService.getDetectResult();
            return Constant.successData(JSONArray.fromObject(result).toString());
        }catch (Exception e){
            log.error("获取检测结果失败："+e.getMessage());
            return Constant.failureMessage("获取检测结果失败！");
        }
    }
    @ResponseBody
    @RequestMapping("/getTrainData")
    @DescribeLog(describe = "获取训练数据")
    public String getTrainData(HttpServletRequest request) {
        try{
            Map<String, Object> result = iMachineLearningService.getTrainData();
            return Constant.successData(JSONArray.fromObject(result).toString());
        }catch (Exception e){
            log.error("获取训练数据："+e.getMessage());
            return Constant.failureMessage("获取训练数据！");
        }
    }
    @ResponseBody
    @RequestMapping("/getTestData")
    @DescribeLog(describe = "获取检测数据")
    public String getTestData(HttpServletRequest request) {
        try{
            Map<String, Object> result = iMachineLearningService.getTestData();
            return Constant.successData(JSONArray.fromObject(result).toString());
        }catch (Exception e){
            log.error("获取训练数据："+e.getMessage());
            return Constant.failureMessage("获取训练数据！");
        }
    }
}

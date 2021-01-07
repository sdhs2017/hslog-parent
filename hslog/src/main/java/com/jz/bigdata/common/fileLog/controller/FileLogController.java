package com.jz.bigdata.common.fileLog.controller;

import com.jz.bigdata.common.fileLog.entity.FileLogFields;
import com.jz.bigdata.common.fileLog.service.IFileLogService;
import com.jz.bigdata.util.DescribeLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: yiyang
 * @Date: 2021/1/4 15:46
 * @Description: 文件日志上传处理模块
 */
@Slf4j
@Controller
@RequestMapping("/fileLog")
public class FileLogController {
    @Resource(name = "FileLogService")
    private IFileLogService fileLogService;
    @ResponseBody
    @RequestMapping(value="/test.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="测试")
    public String test(HttpServletRequest request) {
        try{
            String source_index = request.getParameter("source_index");
            String target_index = request.getParameter("target_index");
            //TODO fields转化成对象
            String fields = request.getParameter("target_index");

            List<FileLogFields> list = new ArrayList<>();

            fileLogService.reindex(list);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

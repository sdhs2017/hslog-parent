package com.jz.bigdata.common.fileLog.controller;

import com.hs.elsearch.entity.DataTableColumn;
import com.hs.elsearch.entity.SearchConditions;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.configuration.entity.Configuration;
import com.jz.bigdata.common.fileLog.entity.FileLogField;
import com.jz.bigdata.common.fileLog.service.IFileLogService;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.DescribeLog;
import com.jz.bigdata.util.JavaBeanUtil;
import com.mysql.jdbc.StringUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    @Resource(name ="configProperty")
    private ConfigProperty configProperty;
    @ResponseBody
    @RequestMapping(value="/updateTemplateInfo.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="更新文件日志模板信息")
    public String updateTemplateInfo(HttpServletRequest request) {
        try{
            List<FileLogField> list = new ArrayList<>();
            String templateInfo = request.getParameter("file_log_fields");
            String file_log_templateName = request.getParameter("file_log_templateName");
            String file_log_templateKey = request.getParameter("file_log_templateKey");
            //更新状态，1为只改file_log_templateName（模板名称），2为改了字段信息（不管模板名称改不改）
            String updateState = request.getParameter("updateState");
            boolean result = false;
            if("1".equals(updateState)){
                result = fileLogService.updateTemplateName(file_log_templateKey,file_log_templateName);
                if(result){
                    return Constant.successMessage("更新成功！");
                }else{
                    return Constant.failureMessage("更新失败！");
                }
            }else{
                //将参数转化为对象
                if(null!=templateInfo){
                    //参数包含多个FileLogFields对象
                    JSONArray json = JSONArray.fromObject(templateInfo);
                    //TODO 字段重复判定
                    //遍历
                    for(Object beanObj:json.toArray()){
                        //转bean
                        FileLogField fileLogField = JavaBeanUtil.mapToBean((Map) JSONObject.fromObject(beanObj), FileLogField.class);
                        //模板名称更新
                        fileLogField.setFile_log_templateName(file_log_templateName);
                        //转换成功时，写入参数对象中
                        if(null!= fileLogField){
                            list.add(fileLogField);
                        }
                    }
                }else{
                    return Constant.failureMessage("参数异常！");
                }
                return fileLogService.reindex(list,file_log_templateKey,file_log_templateName);


            }

        }catch(Exception e){
            log.error("更新文件日志模板信息异常："+e.getMessage());
            return Constant.failureMessage("更新失败！");
        }
    }
    @ResponseBody
    @RequestMapping(value="/getFileLogTemplateList.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="获取文件日志模板列表")
    public String getFileLogTemplateList(HttpServletRequest request) {
        List<FileLogField> list = fileLogService.getTemplateList();
        return Constant.successData(JSONArray.fromObject(list).toString());
    }
    @ResponseBody
    @RequestMapping(value="/getFileLogTemplateFields.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="获取文件日志模板下的字段信息")
    public String getFileLogTemplateFields(HttpServletRequest request) {
        String templateKey = request.getParameter("file_log_templateKey");
        if(StringUtils.isNullOrEmpty(templateKey)){
            return Constant.failureMessage("参数异常！");
        }else{
            List<FileLogField> list = fileLogService.getTemplateInfo(templateKey);
            return Constant.successData(JSONArray.fromObject(list).toString());
        }


    }
    @ResponseBody
    @RequestMapping(value="/getTemplateFields.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="获取动态表头")
    public String getTemplateFields(HttpServletRequest request) {
        String templateKey = request.getParameter("file_log_templateKey");
        if(StringUtils.isNullOrEmpty(templateKey)){
            return Constant.failureMessage("暂无数据！");
        }else{
            List<Map<String,String>> list = fileLogService.getTemplateFields(templateKey);
            return Constant.successData(JSONArray.fromObject(list).toString());
        }
    }
    @ResponseBody
    @RequestMapping(value="/getTemplateData.do", produces = "application/json; charset=utf-8")
    @DescribeLog(describe="获取数据")
    public String getTemplateData(HttpServletRequest request) {
        try{
            String templateKey = request.getParameter("file_log_templateKey");
            String starttime = request.getParameter("starttime");
            String endtime = request.getParameter("endtime");
            String page = request.getParameter("page");
            String size = request.getParameter("size");
            SearchConditions searchConditions = new SearchConditions();
            //index名称
            searchConditions.setIndex_name(configProperty.getEs_filelog_pre()+templateKey+"*");
            //起始和截止时间
            searchConditions.setStartTime(starttime);
            searchConditions.setEndTime(endtime);
            //分页
            Integer fromInt = 0;//默认值
            Integer sizeInt = 10;//默认值
            if (page!=null&&size!=null) {
                fromInt = (Integer.parseInt(page)-1)*Integer.parseInt(size);
                sizeInt = Integer.parseInt(size);
            }
            searchConditions.setFrom(fromInt);
            searchConditions.setPage_size(sizeInt);
            //时间字段
            searchConditions.setDateField(Constant.BEAT_DATE_FIELD);
            //排序

            DataTableColumn sort = new DataTableColumn();
            sort.setField("@timestamp");
            sort.setSort("asc");
            List<DataTableColumn> sortList = new ArrayList<>();
            sortList.add(sort);
            searchConditions.setSortColumns(sortList);
            String result = fileLogService.getTemplateData(searchConditions);
            return Constant.successData(result);
        }catch (Exception e){
            log.error("获取数据失败："+e.getMessage());
            return Constant.failureMessage("获取数据失败");
        }
    }
}

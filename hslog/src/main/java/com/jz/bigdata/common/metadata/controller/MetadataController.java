package com.jz.bigdata.common.metadata.controller;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.metadata.custom.TemplateDateField;
import com.jz.bigdata.common.metadata.entity.Metadata;
import com.jz.bigdata.common.metadata.service.IMetadataService;
import com.jz.bigdata.util.ComboxEntity;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.DescribeLog;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import scala.collection.immutable.Stream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取ES的metadata
 */
@Slf4j
@Controller
@RequestMapping("/metadata")
public class MetadataController {
    @Resource(name = "metadataService")
    private IMetadataService iMetadataService;
    @Resource(name ="configProperty")
    private ConfigProperty configProperty;
    @ResponseBody
    @RequestMapping(value="/getMedataByTemplate", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取template，结构化返回")
    public String getMedataByTemplate(HttpServletRequest request) {
        try{
            String template = request.getParameter("template");
            List<Metadata> list = iMetadataService.getTemplateMapping(template);
            if(list!=null){
                return JSONArray.fromObject(list).toString();
            }else{
                return Constant.failureMessage("Template数据查询出错");
            }
        }catch(Exception e){
            log.error("template数据获取失败");
            return Constant.failureMessage("数据获取失败");
        }

    }
    @ResponseBody
    @RequestMapping(value="/getMedataByIndexDynamically",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取index的mapping，结构化返回,动态加载")
    public String getMedataByIndexDynamically(HttpServletRequest request) {
        try{
            String index = request.getParameter("index");
            String id = request.getParameter("id");
            List<Metadata> list =iMetadataService.getIndexMappingDynamically(index,id);
            if(list!=null){
                return JSONArray.fromObject(list).toString();
            }else{
                return Constant.failureMessage("IndexMapping数据查询出错");
            }
        }catch(Exception e){
            log.error("index mapping 获取失败");
            return Constant.failureMessage("数据获取失败");
        }
    }
    @ResponseBody
    @RequestMapping(value="/getMedataByTemplateDynamically",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取template，结构化返回，动态加载")
    public String getMedataByTemplateDynamically(HttpServletRequest request) {
        try{
            String template = request.getParameter("template");
            String id = request.getParameter("id");
            List<Metadata> list = iMetadataService.getTemplateMappingDynamically(template,id);
            if(list!=null){
                return JSONArray.fromObject(list).toString();
            }else{
                return Constant.failureMessage("Template数据查询出错");
            }
        }catch(Exception e){
            log.error("template数据获取失败");
            return Constant.failureMessage("数据获取失败");
        }

    }
    @ResponseBody
    @RequestMapping(value="/getMedataByIndex", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取index的mapping，结构化返回")
    public String getMedataByIndex(HttpServletRequest request) {
        try{
            String index = request.getParameter("index");
            List<Metadata> list =iMetadataService.getIndexMapping(index);
            if(list!=null){
                return JSONArray.fromObject(list).toString();
            }else{
                return Constant.failureMessage("IndexMapping数据查询出错");
            }
        }catch(Exception e){
            log.error("index mapping 获取失败");
            return Constant.failureMessage("数据获取失败");
        }
    }
    @ResponseBody
    @RequestMapping(value="/getIndexTree", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取template/index的tree结构数据")
    public String getIndexTree(HttpServletRequest request){
        try{
            //根据查询条件对template进行筛选
            //String templates = request.getParameter("templates");
            //读取配置文件.获取Es_tempalatePattern属性值
            String es_tempalatePattern = configProperty.getEs_tempalatePattern();
            return iMetadataService.getIndexTree(es_tempalatePattern);
        }catch(Exception e){
            log.error("template/index Tree数据获取失败");
            return Constant.failureMessage("数据获取失败");
        }
    }
    @ResponseBody
    @RequestMapping(value="/getTemplates", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取template信息")
    public String getTemplates(HttpServletRequest request){
        try{
            //根据查询条件对template进行筛选
            //String templates = request.getParameter("templates");
            //读取配置文件.获取Es_tempalatePattern属性值
            String es_tempalatePattern = configProperty.getEs_tempalatePattern();
            List<ComboxEntity> result = iMetadataService.getTemplates(es_tempalatePattern);
            return Constant.successData(JSONArray.fromObject(result).toString());
        }catch(Exception e){
            log.error("template数据获取失败");
            return Constant.failureMessage("数据获取失败");
        }
    }
    @ResponseBody
    @RequestMapping(value="/getPreIndexByTemplate", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取index前缀列表信息")
    public String getPreIndexByTemplate(HttpServletRequest request){
        try{
            //根据查询条件对template进行筛选
            String templateName = request.getParameter("templateName");
            String preIndexName = iMetadataService.getPreIndexByTemplate(templateName);
            Map<String,Object> result = new HashMap<>();
            result.put("preIndexName",preIndexName);//根据template获取的index前缀
            result.put("dateField", TemplateDateField.getDateField(templateName));//根据template获取其对应的日期字段
            return Constant.successData(JSONObject.fromObject(result).toString());
        }catch(Exception e){
            log.error("template数据获取失败");
            return Constant.failureMessage("数据获取失败");
        }
    }
    @ResponseBody
    @RequestMapping(value="/getSuffixIndexByPre", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取index后缀列表信息")
    public String getSuffixIndexByPre(HttpServletRequest request){
        try{
            //根据查询条件对template进行筛选
            String preIndexName = request.getParameter("preIndexName");
            String result = iMetadataService.getSuffixIndexByPre(preIndexName);
            return Constant.successData(result);
        }catch(Exception e){
            log.error("template数据获取失败");
            return Constant.failureMessage("数据获取失败");
        }
    }
}

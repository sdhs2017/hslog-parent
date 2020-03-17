package com.jz.bigdata.common.metadata.controller;

import com.hs.elsearch.template.IndexTemplate;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.metadata.entity.Metadata;
import com.jz.bigdata.common.metadata.service.IMetadataService;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.DescribeLog;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * 获取ES的metadata
 */
@Controller
@RequestMapping("/metadata")
public class MetadataController {
    private static Logger logger = Logger.getLogger(MetadataController.class);
    @Resource(name = "metadataService")
    private IMetadataService iMetadataService;
    @Resource(name ="configProperty")
    private ConfigProperty configProperty;
    @ResponseBody
    @RequestMapping("/getMedataByTemplate")
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
            logger.error("template数据获取失败");
            return Constant.failureMessage("数据获取失败");
        }

    }
    @ResponseBody
    @RequestMapping("/getMedataByIndex")
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
            logger.error("index mapping 获取失败");
            return Constant.failureMessage("数据获取失败");
        }
    }
    @ResponseBody
    @RequestMapping("/getIndexTree")
    @DescribeLog(describe = "获取template/index的tree结构数据")
    public String getIndexTree(HttpServletRequest request){
        try{
            //根据查询条件对template进行筛选
            //String templates = request.getParameter("templates");
            //读取配置文件.获取Es_tempalatePattern属性值
            String es_tempalatePattern = configProperty.getEs_tempalatePattern();
            return iMetadataService.getIndexTree(es_tempalatePattern);
        }catch(Exception e){
            logger.error("template/index Tree数据获取失败");
            return Constant.failureMessage("数据获取失败");
        }
    }
}

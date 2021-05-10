package com.jz.bigdata.business.data_sec_govern.label.controller;

import com.jz.bigdata.business.data_sec_govern.label.entity.Label;
import com.jz.bigdata.business.data_sec_govern.label.service.IDSGLabelService;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.util.DescribeLog;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/4/30 14:48
 * @Description: 数据源管理-标签管理，实现增删改查功能
 */
@Slf4j
@Controller
@RequestMapping("/DSGLabel")
public class DSGLabelController {
    @Resource(name = "DSGLabelService")
    private IDSGLabelService labelService;
    /**
     * 发现方式 0 无规则 1按正则发现
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getLabelDiscoverWay",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取发现方式")
    public String getLabelDiscoverWay(HttpServletRequest request){
        try{

            return Constant.successData(JSONArray.fromObject(Constant.DSG_TAG_LIBRARY_DISCOVER_WAY).toString());
        }catch (Exception e){
            log.error("获取发现方式失败："+e.getMessage());
            return Constant.failureMessage("获取发现方式失败！");
        }
    }
    /**
     * 添加标签
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/insertLabel",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "添加标签")
    public String insertLabel(HttpServletRequest request, Label label){
        try{
            int result = labelService.insertLabel(label);
            if(result>0){
                return Constant.successMessage("添加标签成功");
            }else{
                return Constant.failureMessage("添加标签失败");
            }
        }catch(DataAccessException e){
            return Unique_Exception(e);
        }
        catch(Exception e){
            log.error("添加标签失败："+e.getMessage());
            return Constant.failureMessage("添加标签失败！");
        }
    }
    /**
     * 删除标签
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/deleteLabel",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "删除标签")
    public String deleteLabel(HttpServletRequest request){
        try{
            String ids = request.getParameter("ids");
            int result = labelService.deleteLabels(ids);
            if(result>0){
                return Constant.successMessage("删除标签成功");
            }else{
                return Constant.failureMessage("删除标签失败");
            }
        }catch(Exception e){
            log.error("删除标签失败："+e.getMessage());
            return Constant.failureMessage("删除标签失败！");
        }
    }
    /**
     * 修改标签
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/updateLabel",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "修改标签")
    public String updateLabel(HttpServletRequest request, Label label){
        try{
            int result = labelService.updateLabel(label);
            if(result>0){
                return Constant.successMessage("修改标签成功");
            }else{
                return Constant.failureMessage("修改标签失败");
            }
        }catch(DataAccessException e){
            return Unique_Exception(e);
        }
        catch(Exception e){
            log.error("修改标签失败："+e.getMessage());
            return Constant.failureMessage("修改标签失败！");
        }
    }
    /**
     * 通过标签id获取标签详情
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getLabelInfoById",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "通过标签id获取标签详情")
    public String getLabelInfoById(HttpServletRequest request){
        try{
            String label_id = request.getParameter("label_id");
            Label result = labelService.getLabelInfoById(label_id);
            return Constant.successData(JSONObject.fromObject(result).toString());
        }
        catch(Exception e){
            log.error("通过标签id获取标签详情失败："+e.getMessage());
            return Constant.failureMessage("通过标签id获取标签详情失败！");
        }
    }
    /**
     * 标签查询
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/searchLabel",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "标签查询")
    public String searchLabel(HttpServletRequest request,Label label){
        try{
            //分页信息处理
            if(label.getPageIndex()!=null&&label.getPageSize()!=null){
                label.setStartRecord((label.getPageSize() * (label.getPageIndex() - 1)));
            }
            Map<String, Object> result = labelService.searchLabel(label);
            return Constant.successData(JSONArray.fromObject(result).toString());
        }
        catch(Exception e){
            log.error("标签查询失败："+e.getMessage());
            return Constant.failureMessage("标签查询失败！");
        }
    }
    /**
     * 获取标签列表，仅有名称和id，服务于前端combobox
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getLabelAll4Combobox",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取标签列表 服务于前端combobox")
    public String getLabelAll4Combobox(HttpServletRequest request){
        try{
            List<Map<String, Object>> result = labelService.getLabelAll4Combobox();
            return Constant.successData(JSONArray.fromObject(result).toString());
        }
        catch(Exception e){
            log.error("获取标签列表 服务于前端combobox失败："+e.getMessage());
            return Constant.failureMessage("获取标签列表失败！");
        }
    }

    /**
     * 数据保存异常处理,spring mybatis 抛出的异常类型
     * @param e
     * @return
     */
    private String Unique_Exception(DataAccessException e){
        //异常类型
        if(e.getMessage().indexOf("MySQLIntegrityConstraintViolationException")>=0){
            //根据异常信息判定是否存在唯一索引重复
            if(e.getMessage().indexOf("UNIQUE_KEY")>=0){
                log.info("名称重复");
                return Constant.failureMessage("名称重复，请重新输入！");
            }else{
                log.error("信息保存异常！"+e.getMessage());
            }
        }else{
            log.error("信息保存其他异常情况！"+e.getMessage());
        }
        //其他异常情况
        return Constant.failureMessage("保存失败！");
    }
}

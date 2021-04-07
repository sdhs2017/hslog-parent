package com.jz.bigdata.common.data_source_metadata_identify.controller;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.data_source_metadata_identify.entity.MetadataIdentifyBasic;
import com.jz.bigdata.common.data_source_metadata_identify.entity.MetadataIdentifyDetails;
import com.jz.bigdata.common.data_source_metadata_identify.service.IMetadataIdentifyService;
import com.jz.bigdata.util.DescribeLog;
import com.jz.bigdata.util.TreeCombobox;
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

/**
 * @Author: yiyang
 * @Date: 2021/3/23 14:48
 * @Description: metadata信息标识
 */
@Slf4j
@Controller
@RequestMapping("/metadataIdentify")
public class MetadataIdentifyController {
    @Resource(name = "MetadataIdentifyService")
    private IMetadataIdentifyService metadataIdentifyService;

    /**
     * 添加元数据标识基础分类
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/insertMetadataIdentify_Basic",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "添加元数据标识基础分类")
    public String insertMetadataIdentify_Basic(HttpServletRequest request, MetadataIdentifyBasic metadataIdentifyBasic){
        try{
            int result = metadataIdentifyService.insertMetadataIdentify_Basic(metadataIdentifyBasic);
            if(result>0){
                return Constant.successMessage("添加成功！");
            }else{
                return Constant.failureMessage("添加失败！");
            }
        }catch(DataAccessException e){
            return Unique_Exception(e);
        }catch (Exception e){
            log.error("添加元数据标识基础分类失败："+e.getMessage());
            return Constant.failureMessage("保存失败！");
        }
    }
    /**
     * 修改元数据标识基础分类
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/updateMetadataIdentify_Basic",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "修改元数据标识基础分类")
    public String updateMetadataIdentify_Basic(HttpServletRequest request, MetadataIdentifyBasic metadataIdentifyBasic){
        try{
            int result = metadataIdentifyService.updateMetadataIdentify_Basic(metadataIdentifyBasic);
            if(result==1){
                return Constant.successMessage("更新成功！");
            }else{
                return Constant.successMessage("更新失败！");
            }
        }catch (Exception e){
            log.error("更新元数据标识基础分类失败："+e.getMessage());
            return Constant.failureMessage("更新失败！");
        }
    }
    /**
     * 删除元数据标识基础分类
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/deleteMetadataIdentify_Basic",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "删除元数据标识基础分类")
    public String deleteMetadataIdentify_Basic(HttpServletRequest request){
        try{
            String basic_ids = request.getParameter("ids");
            if(basic_ids!=null){
                String[] ids = basic_ids.split(",");
                int result = metadataIdentifyService.deleteMetadataIdentify_Basic(ids);
                if(result>0){
                    return Constant.successMessage("删除成功！");
                }else{
                    return Constant.successMessage("删除失败！");
                }
            }else{
                return Constant.failureMessage("参数异常！");
            }

        }catch (Exception e){
            log.error("删除元数据标识基础分类失败："+e.getMessage());
            return Constant.failureMessage("删除失败！");
        }
    }
    /**
     * 获取元数据标识基础分类列表
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getBasicList",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取元数据标识基础分类列表")
    public String getBasicList(HttpServletRequest request){
        try{
            return Constant.successData(JSONArray.fromObject(metadataIdentifyService.getBasicList()).toString());
        }catch (Exception e){
            log.error("获取元数据标识基础分类列表："+e.getMessage());
            return Constant.failureMessage("查询失败！");
        }
    }
    /**
     * 通过父节点id获取其下一级分类列表
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/selectBasicInfoById",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取其下一级分类列表")
    public String selectBasicInfoById(HttpServletRequest request){
        try{
            String fid = request.getParameter("fid");
            return Constant.successData(JSONArray.fromObject(metadataIdentifyService.getDetailsListBySuperiorId(fid)).toString());
        }catch (Exception e){
            log.error("获取其下一级分类列表："+e.getMessage());
            return Constant.failureMessage("查询失败！");
        }
    }
    /**
     * 添加元数据标识详细分类
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/insertMetadataIdentify_Details",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "添加元数据标识基础分类")
    public String insertMetadataIdentify_Details(HttpServletRequest request, MetadataIdentifyDetails metadataIdentifyDetails){
        try{
            //本模块功能实现在添加时，如果成功，返回添加节点的信息
            //前端效果无法达到预期，但接口功能保留
            MetadataIdentifyDetails result = metadataIdentifyService.insertMetadataIdentify_Details(metadataIdentifyDetails);
            if(result!=null){
                return Constant.successMessage("添加成功！");
            }else{
                return Constant.failureMessage("添加失败！");
            }
        }catch(DataAccessException e){
            return Unique_Exception(e);
        }catch (Exception e){
            log.error("添加元数据标识详细分类失败："+e.getMessage());
            return Constant.failureMessage("保存失败！");
        }
    }
    /**
     * 修改元数据标识详细分类
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/updateMetadataIdentify_Details",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "修改元数据标识基础分类")
    public String updateMetadataIdentify_Details(HttpServletRequest request, MetadataIdentifyDetails metadataIdentifyDetails){
        try{
            int result = metadataIdentifyService.updateMetadataIdentify_Details(metadataIdentifyDetails);
            if(result==1){
                return Constant.successMessage("更新成功！");
            }else{
                return Constant.successMessage("更新失败！");
            }
        }catch (Exception e){
            log.error("更新元数据标识详细分类失败："+e.getMessage());
            return Constant.failureMessage("更新失败！");
        }
    }
    /**
     * 删除元数据标识详细分类
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/deleteMetadataIdentify_Details",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "删除元数据标识基础分类")
    public String deleteMetadataIdentify_Details(HttpServletRequest request){
        try{
            String details_id = request.getParameter("id");
            int result = metadataIdentifyService.deleteMetadataIdentify_Details(details_id);
            if(result>0){
                return Constant.successMessage("删除成功！");
            }else{
                return Constant.successMessage("删除失败！");
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("删除元数据标识基础分类失败："+e.getMessage());
            return Constant.failureMessage("删除失败！");
        }
    }
    /**
     * 获取数据源标识tree
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getMetadataIdentifyTree",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取数据源标识tree")
    public String getMetadataIdentifyTree(HttpServletRequest request){
        try{
            List<TreeCombobox> result = metadataIdentifyService.getMetadataIdentifyTree();
            return Constant.successData(JSONArray.fromObject(result).toString());
        }catch (Exception e){
            log.error("获取数据源标识tree失败："+e.getMessage());
            return Constant.failureMessage("获取数据失败！");
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

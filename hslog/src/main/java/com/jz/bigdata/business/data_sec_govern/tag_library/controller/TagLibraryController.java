package com.jz.bigdata.business.data_sec_govern.tag_library.controller;

import com.jz.bigdata.business.data_sec_govern.tag_library.entity.TagBasic;
import com.jz.bigdata.business.data_sec_govern.tag_library.entity.TagDetail;
import com.jz.bigdata.business.data_sec_govern.tag_library.service.ITagLibraryService;
import com.jz.bigdata.common.Constant;
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
 * @Description: 标签库
 */
@Slf4j
@Controller
@RequestMapping("/tagLibrary")
public class TagLibraryController {
    @Resource(name = "TagLibraryService")
    private ITagLibraryService TagLibraryService;

    /**
     * 添加元数据标识基础分类
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/insertTagLibrary_Basic",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "添加元数据标识基础分类")
    public String insertTagLibrary_Basic(HttpServletRequest request, TagBasic tagBasic){
        try{
            int result = TagLibraryService.insertTagLibrary_Basic(tagBasic);
            if(result>0){
                return Constant.successMessage("添加成功！");
            }else{
                return Constant.failureMessage("添加失败！");
            }
        }catch(DataAccessException e){
            return Unique_Exception(e);
        }catch (Exception e){
            log.error("添加标签库基础分类失败："+e.getMessage());
            return Constant.failureMessage("保存失败！");
        }
    }
    /**
     * 修改标签库基础分类
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/updateTagLibrary_Basic",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "修改标签库基础分类")
    public String updateTagLibrary_Basic(HttpServletRequest request, TagBasic tagBasic){
        try{
            int result = TagLibraryService.updateTagLibrary_Basic(tagBasic);
            if(result==1){
                return Constant.successMessage("更新成功！");
            }else{
                return Constant.successMessage("更新失败！");
            }
        }catch (Exception e){
            log.error("更新标签库基础分类失败："+e.getMessage());
            return Constant.failureMessage("更新失败！");
        }
    }
    /**
     * 删除标签库基础分类
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/deleteTagLibrary_Basic",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "删除标签库基础分类")
    public String deleteTagLibrary_Basic(HttpServletRequest request){
        try{
            String basic_ids = request.getParameter("ids");
            if(basic_ids!=null){
                String[] ids = basic_ids.split(",");
                int result = TagLibraryService.deleteTagLibrary_Basic(ids);
                if(result>0){
                    return Constant.successMessage("删除成功！");
                }else{
                    return Constant.successMessage("删除失败！");
                }
            }else{
                return Constant.failureMessage("参数异常！");
            }

        }catch (Exception e){
            log.error("删除标签库基础分类失败："+e.getMessage());
            return Constant.failureMessage("删除失败！");
        }
    }
    /**
     * 通过id获取标签库基础分类详情
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getTagLibraryBasicInfoById",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "通过id获取标签库基础分类详情")
    public String getTagLibraryBasicInfoById(HttpServletRequest request){
        try{
            String basic_id = request.getParameter("id");
            TagBasic result = TagLibraryService.getTagLibraryBasicInfoById(basic_id);
            return Constant.successMessage(JSONObject.fromObject(result).toString());
        }catch (Exception e){
            e.printStackTrace();
            log.error("通过id获取标签库详细分类详情失败："+e.getMessage());
            return Constant.failureMessage("获取信息失败！");
        }
    }
    /**
     * 获取标签库基础分类列表
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getTagBasicList",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取标签库基础分类列表")
    public String getTagBasicList(HttpServletRequest request){
        try{
            return Constant.successData(JSONArray.fromObject(TagLibraryService.getTagBasicList()).toString());
        }catch (Exception e){
            log.error("获取标签库基础分类列表："+e.getMessage());
            return Constant.failureMessage("查询失败！");
        }
    }
    /**
     * 通过父节点id获取其下一级分类列表
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getTagDetailListBySuperiorId",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取其下一级分类列表")
    public String getTagDetailListBySuperiorId(HttpServletRequest request){
        try{
            String fid = request.getParameter("fid");
            return Constant.successData(JSONArray.fromObject(TagLibraryService.getTagDetailListBySuperiorId(fid)).toString());
        }catch (Exception e){
            log.error("获取其下一级分类列表："+e.getMessage());
            return Constant.failureMessage("查询失败！");
        }
    }
    /**
     * 发现方式 0 无规则 1按正则发现
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getTagDiscoverWay",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取发现方式")
    public String getTagDiscoverWay(HttpServletRequest request){
        try{

            return Constant.successData(JSONArray.fromObject(Constant.DSG_TAG_LIBRARY_DISCOVER_WAY).toString());
        }catch (Exception e){
            log.error("获取发现方式失败："+e.getMessage());
            return Constant.failureMessage("获取发现方式失败！");
        }
    }
    /**
     * 添加标签库详细分类
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/insertTagLibrary_Detail",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "添加标签库基础分类")
    public String insertTagLibrary_Detail(HttpServletRequest request, TagDetail tagDetail){
        try{
            //本模块功能实现在添加时，如果成功，返回添加节点的信息
            //前端效果无法达到预期，但接口功能保留
            TagDetail result = TagLibraryService.insertTagLibrary_Detail(tagDetail);
            if(result!=null){
                return Constant.successMessage("添加成功！");
            }else{
                return Constant.failureMessage("添加失败！");
            }
        }catch(DataAccessException e){
            return Unique_Exception(e);
        }catch (Exception e){
            log.error("添加标签库详细分类失败："+e.getMessage());
            return Constant.failureMessage("保存失败！");
        }
    }
    /**
     * 修改标签库详细分类
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/updateTagLibrary_Detail",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "修改标签库基础分类")
    public String updateTagLibrary_Detail(HttpServletRequest request, TagDetail tagDetail){
        try{
            int result = TagLibraryService.updateTagLibrary_Detail(tagDetail);
            if(result==1){
                return Constant.successMessage("更新成功！");
            }else{
                return Constant.successMessage("更新失败！");
            }
        }catch (Exception e){
            log.error("更新标签库详细分类失败："+e.getMessage());
            return Constant.failureMessage("更新失败！");
        }
    }
    /**
     * 删除标签库详细分类
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/deleteTagLibrary_Detail",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "删除标签库详细分类")
    public String deleteTagLibrary_Detail(HttpServletRequest request){
        try{
            String detail_id = request.getParameter("id");
            int result = TagLibraryService.deleteTagLibrary_Detail(detail_id);
            if(result>0){
                return Constant.successMessage("删除成功！");
            }else{
                return Constant.successMessage("删除失败！");
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("删除标签库基础分类失败："+e.getMessage());
            return Constant.failureMessage("删除失败！");
        }
    }
    /**
     * 通过id获取标签库详细分类详情
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getTagLibraryDetailInfoById",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "通过id获取标签库详细分类详情")
    public String getTagLibraryDetailInfoById(HttpServletRequest request){
        try{
            String detail_id = request.getParameter("id");
            TagDetail result = TagLibraryService.getTagLibraryDetailInfoById(detail_id);
            return Constant.successMessage(JSONObject.fromObject(result).toString());
        }catch (Exception e){
            e.printStackTrace();
            log.error("通过id获取标签库详细分类详情失败："+e.getMessage());
            return Constant.failureMessage("获取信息失败！");
        }
    }
    /**
     * 获取数据源标识tree
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getTagLibraryTree",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取数据源标识tree")
    public String getTagLibraryTree(HttpServletRequest request){
        try{
            List<TreeCombobox> result = TagLibraryService.getTagLibraryTree();
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

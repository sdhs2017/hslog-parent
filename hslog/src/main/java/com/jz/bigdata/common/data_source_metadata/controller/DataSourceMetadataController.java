package com.jz.bigdata.common.data_source_metadata.controller;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.data_source_metadata.entity.DataSourceMetadata;
import com.jz.bigdata.common.data_source_metadata.entity.MetadataField;
import com.jz.bigdata.common.data_source_metadata.entity.MetadataTable;
import com.jz.bigdata.common.data_source_metadata.service.IDataSourceMetadataService;
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
 * @Date: 2021/3/1 13:54
 * @Description: 数据源的元数据
 */
@Slf4j
@Controller
@RequestMapping("/dataSourceMetadata")
public class DataSourceMetadataController {
    @Resource(name = "DataSourceMetadataService")
    private IDataSourceMetadataService dataSourceMetadataService;
    /**
     * 获取元数据tree
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getMetadataTree",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取元数据tree")
    public String getMetadataTree(HttpServletRequest request){
        try{
            return Constant.successData(JSONArray.fromObject(dataSourceMetadataService.getMetadataTree()).toString());
        }catch (Exception e){
            log.error("获取元数据tree异常："+e.getMessage());
            return Constant.failureMessage("数据获取失败");
        }
    }
    /**
     * 获取库下的表信息
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getTableInfo",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取库下的表信息")
    public String getTableInfo(HttpServletRequest request, DataSourceMetadata dataSourceMetadata){
        try{
            //分页信息处理
            if(dataSourceMetadata.getPageIndex()!=null&&dataSourceMetadata.getPageSize()!=null){
                dataSourceMetadata.setStartRecord((dataSourceMetadata.getPageSize() * (dataSourceMetadata.getPageIndex() - 1)));
            }
            return Constant.successData(JSONArray.fromObject(dataSourceMetadataService.getTableInfo(dataSourceMetadata)).toString());
        }catch (Exception e){
            log.error("获取元数据tree异常："+e.getMessage());
            return Constant.failureMessage("数据获取失败");
        }
    }
    /**
     * 获取表的字段信息
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getFieldInfo",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取表的字段信息")
    public String getFieldInfo(HttpServletRequest request, DataSourceMetadata dataSourceMetadata){
        try{
            //分页信息处理
            if(dataSourceMetadata.getPageIndex()!=null&&dataSourceMetadata.getPageSize()!=null){
                dataSourceMetadata.setStartRecord((dataSourceMetadata.getPageSize() * (dataSourceMetadata.getPageIndex() - 1)));
            }
            return Constant.successData(JSONArray.fromObject(dataSourceMetadataService.getFieldInfo(dataSourceMetadata)).toString());
        }catch (Exception e){
            log.error("获取表的字段信息异常："+e.getMessage());
            return Constant.failureMessage("数据获取失败");
        }
    }
    /**
     * 获取表格分类
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getMetadataTableType",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取表格分类")
    public String getMetadataTableType(HttpServletRequest request){
        try{
            return Constant.successData(JSONArray.fromObject(Constant.DATA_SOURCE_METADATA_TABLE_TYPE).toString());
        }catch (Exception e){
            log.error("获取数据异常："+e.getMessage());
            return Constant.failureMessage("数据获取失败！");
        }
    }
    /**
     * 获取字段敏感等级
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getMetadataFieldSensitiveLevel",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取字段敏感等级")
    public String getMetadataFieldSensitiveLevel(HttpServletRequest request){
        try{
            return Constant.successData(JSONArray.fromObject(Constant.DATA_SOURCE_METADATA_FIELD_SensitiveLevel).toString());
        }catch (Exception e){
            log.error("获取数据异常："+e.getMessage());
            return Constant.failureMessage("数据获取失败！");
        }
    }

    /**
     * 更新元数据表信息
     * @param request
     * @param metadataTable
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/updateMetadataTableInfo",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "更新元数据表信息")
    public String updateMetadataTableInfo(HttpServletRequest request, MetadataTable metadataTable){
        try{
            int result = dataSourceMetadataService.updateMetadataTableInfo(metadataTable);
            if(result>0){
                return Constant.successMessage("更新成功");
            }else{
                return Constant.failureMessage("更新失败");
            }
        }catch (Exception e){
            log.error("更新失败："+e.getMessage());
            return Constant.failureMessage("更新失败");
        }
    }
    /**
     * 更新元数据字段信息
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/updateMetadataFieldInfo",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "更新元数据字段信息")
    public String updateMetadataFieldInfo(HttpServletRequest request, MetadataField metadataField){
        try{
            int result = dataSourceMetadataService.updateMetadataFieldInfo(metadataField);
            if(result>0){
                return Constant.successMessage("更新成功");
            }else{
                return Constant.failureMessage("更新失败");
            }
        }catch (Exception e){
            log.error("更新失败："+e.getMessage());
            return Constant.failureMessage("更新失败");
        }
    }
    /**
     * 更新信息 停用
     * @param request
     * @return
     */
//    @ResponseBody
//    @RequestMapping(value="/update",produces = "application/json; charset=utf-8")
//    @DescribeLog(describe = "更新信息")
//    public String update(HttpServletRequest request,DataSourceMetadata dataSourceMetadata){
//        try{
//            int result = dataSourceMetadataService.update(dataSourceMetadata);
//            if(result>0){
//                return Constant.successMessage("更新成功");
//            }else{
//                return Constant.failureMessage("更新失败");
//            }
//        }catch (Exception e){
//            log.error("更新失败："+e.getMessage());
//            return Constant.failureMessage("更新失败");
//        }
//    }
    /**
     * 数据预览，元数据管理，数据源已初始化
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDataPreview",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "数据预览")
    public String getDataPreview(HttpServletRequest request){
        String databaseName = request.getParameter("database");
        String tableName = request.getParameter("table");
        String data_source_id = request.getParameter("data_source_id");
        try{
            Map<String,Object> result = dataSourceMetadataService.getDataPreview(databaseName,tableName,data_source_id);
            return Constant.successData(JSONArray.fromObject(result).toString());
        }catch (Exception e){
            //e.printStackTrace();
            log.error("数据预览失败："+e.getMessage());
            return Constant.failureMessage("数据获取失败！");
        }
    }

}

package com.jz.bigdata.common.data_source.controller;

import com.hs.elsearch.util.MappingField;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.data_source.entity.DataSource;
import com.jz.bigdata.common.data_source.service.IDataSourceService;
import com.jz.bigdata.common.eventGroup.service.IEventGroupService;
import com.jz.bigdata.common.event_alert.entity.EventAlert;
import com.jz.bigdata.common.event_alert.service.IEventAlertService;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.DescribeLog;
import com.sun.tools.internal.jxc.ap.Const;
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
 * @Date: 2021/2/23 10:28
 * @Description: 数据源
 */
@Slf4j
@Controller
@RequestMapping("/dataSource")
public class DataSourceController {
    @Resource(name = "DataSourceService")
    private IDataSourceService dataSourceService;
    @Resource(name ="configProperty")
    private ConfigProperty configProperty;
    /**
     * 获取事件类型
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDataSourceItemType",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取数据源类型")
    public String getDataSourceItemType(HttpServletRequest request){
        return Constant.successData(JSONArray.fromObject(Constant.DATA_SOURCE_ITEM_TYPE).toString());
    }
    /**
     * 测试链接
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/testConnection",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "数据源连接测试")
    public String testConnection(HttpServletRequest request,DataSource dataSource){
        try{
            return dataSourceService.checkConnection(dataSource);
        }catch (Exception e){
            log.error("数据源连接测试失败："+e.getMessage());
            return Constant.failureMessage("连接失败！");
        }
    }
    /**
     * 数据源初始化
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/dataSourceInit",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "数据源初始化")
    public String dataSourceInit(HttpServletRequest request){
        String ids = request.getParameter("data_source_ids");//数据源id
        try{
            boolean result = dataSourceService.initByDataSourceIds(ids);
            if(result){
                return Constant.successMessage("初始化成功！");
            }else{
                return Constant.failureMessage("初始化失败！");
            }
        }catch (Exception e){
            //e.printStackTrace();
            log.error("数据源初始化失败："+e.getMessage());
            return Constant.failureMessage("数据源初始化失败！");
        }
    }
    /**
     * 测试链接
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDataBase",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取数据源下的库")
    public String getDataBase(HttpServletRequest request){
        String data_source_id = request.getParameter("data_source_id");
        try{
            List<Map<String,String>> list = dataSourceService.getDataBase(data_source_id);
            return Constant.successData(JSONArray.fromObject(list).toString());
        }catch (Exception e) {
            log.error("数据源库或表信息获取失败："+e.getMessage());
            return Constant.failureMessage("数据获取失败！");
        }

    }
    /**
     * 获取数据库下的表
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getTablesByDatabase",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取库下的表")
    public String getTablesByDatabase(HttpServletRequest request){
        String data_source_id = request.getParameter("data_source_id");
        String database = request.getParameter("database");
        try{
            List<Map<String,String>> list = dataSourceService.getTablesByDatabase_tree(database,data_source_id);
            return Constant.successData(JSONArray.fromObject(list).toString());
        }catch (Exception e) {
            log.error("数据源库或表信息获取失败："+e.getMessage());
            return Constant.failureMessage("数据获取失败！");
        }

    }
    /**
     * 获取表字段信息
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getTableFields",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取表字段信息")
    public String getTableFields(HttpServletRequest request){
        String data_source_id = request.getParameter("data_source_id");
        String database = request.getParameter("database");
        String table = request.getParameter("table");
        try{
            List<Map<String,Object>> list = dataSourceService.getTableFieldsInfo(database,table,data_source_id);
            return Constant.successData(JSONArray.fromObject(list).toString());
        }catch (Exception e) {
            log.error("表信息获取失败："+e.getMessage());
            return Constant.failureMessage("数据获取失败！");
        }

    }
    /**
     * 保存数据源
     * @param request
     * @param dataSource
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/save",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "保存数据源")
    public String save (HttpServletRequest request, DataSource dataSource){
        try{
            if(dataSourceService.save(dataSource)){
                return Constant.successMessage("保存成功！");
            }else{
                return Constant.failureMessage("保存失败！");
            }
        }catch(DataAccessException e){
            return Unique_Exception(e);
        }catch (Exception e){
            log.error("数据源保存失败："+e.getMessage());
            return Constant.failureMessage("保存失败！");
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
            if(e.getMessage().indexOf("UNIQUE_NAME")>=0){
                log.info("数据源名称重复");
                return Constant.failureMessage("数据源名称重复，请重新输入！");
            }else{
                log.error("数据源信息保存异常！"+e.getMessage());
            }
        }else{
            log.error("数据源信息保存其他异常情况！"+e.getMessage());
        }
        //其他异常情况
        return Constant.failureMessage("保存失败！");
    }
    /**
     * 获取数据源列表，带分页
     * @param request
     * @param dataSource
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDataSourceList",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取数据源列表")
    public String getDataSourceList(HttpServletRequest request,DataSource dataSource){
        try{
            //分页信息处理
            if(dataSource.getPageIndex()!=null&&dataSource.getPageSize()!=null){
                dataSource.setStartRecord((dataSource.getPageSize() * (dataSource.getPageIndex() - 1)));
            }
            Map<String, Object> result = dataSourceService.getListByCondition(dataSource);
            return Constant.successData(JSONArray.fromObject(result).toString());
        }catch (Exception e){
            log.error("数据源列表查询失败："+e.getMessage());
            return Constant.failureMessage("查询失败！");
        }

    }
    /**
     * 删除，批量
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/delete",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "删除数据源")
    public String delete(HttpServletRequest request){
        try{
            String data_source_ids = request.getParameter("data_source_ids");
            String[] idList = data_source_ids.split(",");
            if(dataSourceService.delete(idList)){
                return Constant.successMessage("删除成功！");
            }else{
                return Constant.failureMessage("删除失败！");
            }
        }catch (Exception e){
            log.error("删除数据源失败："+e.getMessage());
            return Constant.failureMessage("删除失败！");
        }
    }

    /**
     * 更新
     * @param request
     * @param dataSource
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/update",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "更新数据源")
    public String update(HttpServletRequest request,DataSource dataSource){
        try{

            if(dataSourceService.update(dataSource)){
                return Constant.successMessage("更新成功！");
            }else{
                return Constant.failureMessage("更新失败！");
            }
        }catch(DataAccessException e){
            return Unique_Exception(e);
        }catch (Exception e){
            log.error("更新数据源失败："+e.getMessage());
            return Constant.failureMessage("更新失败！");
        }
    }

    /**
     * 获取数据源信息
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDataSourceInfo",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取数据源信息")
    public String getDataSourceInfo(HttpServletRequest request){
        try{
            String data_source_id = request.getParameter("data_source_id");
            DataSource dataSource = dataSourceService.selectDataSourceInfoById(data_source_id);
            return Constant.successData(JSONObject.fromObject(dataSource).toString());
        }catch (Exception e){
            log.error("数据获取失败："+e.getMessage());
            return Constant.failureMessage("数据获取失败！");
        }
    }
}

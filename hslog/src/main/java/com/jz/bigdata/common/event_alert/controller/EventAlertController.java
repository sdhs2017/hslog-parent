package com.jz.bigdata.common.event_alert.controller;

import com.hs.elsearch.util.MappingField;
import com.jz.bigdata.common.Constant;
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
 * @Date: 2021/1/27 10:28
 * @Description: siem模块
 */
@Slf4j
@Controller
@RequestMapping("/eventAlert")
public class EventAlertController {
    @Resource(name = "EventAlertService")
    private IEventAlertService eventAlertService;
    @Resource(name = "EventGroupService")
    private IEventGroupService eventGroupService;
    @Resource(name ="configProperty")
    private ConfigProperty configProperty;
    /**
     * 获取事件类型
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getEventType",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取事件类型")
    public String getEventType(HttpServletRequest request){
        return Constant.successData(JSONArray.fromObject(Constant.EVENT_TYPE).toString());
    }

    /**
     * 通过事件类型获取对应的事件列表
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getEventByType",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取事件列表")
    public String getEventByType(HttpServletRequest request){
        String event_type = request.getParameter("event_type");
        List<Map<String,Object>> result = eventGroupService.getEventList4Combobox_eventAlert(event_type);
        return Constant.successData(JSONArray.fromObject(result).toString());
    }

    /**
     * 事件告警保存
     * @param request
     * @param eventAlert
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/save",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "保存事件告警设置")
    public String save (HttpServletRequest request, EventAlert eventAlert){
        try{
            if(eventAlertService.save(eventAlert)){
                return Constant.successMessage("保存成功！");
            }else{
                return Constant.failureMessage("保存失败！");
            }
        }catch(DataAccessException e){//spring
            return Unique_Exception(e);
        }catch (Exception e){
            log.error("事件告警设置保存失败："+e.getMessage());
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
                log.info("事件告警名称重复");
                return Constant.failureMessage("事件告警名称重复，请重新输入！");
            }else{
                log.error("事件告警设置异常！"+e.getMessage());
            }
        }else{
            log.error("事件告警设置其他异常情况！"+e.getMessage());
        }
        //其他异常情况
        return Constant.failureMessage("保存失败！");
    }
    /**
     * 获取事件告警列表，带分页
     * @param request
     * @param eventAlert
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getEventAlertList",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取事件告警列表")
    public String getEventAlertList(HttpServletRequest request,EventAlert eventAlert){
        try{
            //分页信息处理
            if(eventAlert.getPageIndex()!=null&&eventAlert.getPageSize()!=null){
                eventAlert.setStartRecord((eventAlert.getPageSize() * (eventAlert.getPageIndex() - 1)));
            }
            Map<String, Object> result = eventAlertService.getListByCondition(eventAlert);
            return Constant.successData(JSONArray.fromObject(result).toString());
        }catch (Exception e){
            log.error("事件告警设置查询失败："+e.getMessage());
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
    @DescribeLog(describe = "删除事件告警设置")
    public String delete(HttpServletRequest request){
        try{
            String event_alert_ids = request.getParameter("event_alert_ids");
            String[] idList = event_alert_ids.split(",");
            if(eventAlertService.delete(idList)){
                return Constant.successMessage("删除成功！");
            }else{
                return Constant.failureMessage("删除失败！");
            }
        }catch (Exception e){
            log.error("删除事件告警设置失败："+e.getMessage());
            return Constant.failureMessage("删除失败！");
        }
    }

    /**
     * 更新
     * @param request
     * @param eventAlert
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/update",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "更新事件告警设置")
    public String update(HttpServletRequest request,EventAlert eventAlert){
        try{

            if(eventAlertService.update(eventAlert)){
                return Constant.successMessage("更新成功！");
            }else{
                return Constant.failureMessage("更新失败！");
            }
        }catch(DataAccessException e){
            return Unique_Exception(e);
        }catch (Exception e){
            log.error("更新事件告警设置失败："+e.getMessage());
            return Constant.failureMessage("更新失败！");
        }
    }

    /**
     * 获取事件告警设置
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getEventAlertInfo",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取事件告警设置")
    public String getEventAlertInfo(HttpServletRequest request){
        try{
            String event_alert_id = request.getParameter("event_alert_id");
            EventAlert eventAlert = eventAlertService.selectEventAlertInfoById(event_alert_id);
            return Constant.successData(JSONObject.fromObject(eventAlert).toString());
        }catch (Exception e){
            log.error("数据获取失败："+e.getMessage());
            return Constant.failureMessage("数据获取失败！");
        }
    }
}

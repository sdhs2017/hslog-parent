package com.jz.bigdata.common.siem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hs.elsearch.entity.SearchConditions;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.eventGroup.service.IEventGroupService;
import com.jz.bigdata.common.siem.entity.Interval;
import com.jz.bigdata.common.siem.service.ISIEMService;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.DescribeLog;
import com.jz.bigdata.util.HttpRequestUtil;
import com.sun.tools.internal.jxc.ap.Const;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @Author: yiyang
 * @Date: 2021/1/27 10:28
 * @Description: siem模块
 */
@Slf4j
@Controller
@RequestMapping("/siem")
public class SIEMController {
    @Resource(name = "SIEMService")
    private ISIEMService siemService;
    @Resource(name ="configProperty")
    private ConfigProperty configProperty;
    @ResponseBody
    @RequestMapping(value="/getSIEMInfo",produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "获取SIEM结果信息")
    public String getSIEMInfo(HttpServletRequest request){
        try{
            String equipment_id = request.getParameter("equipment_id");
            String equipment_type = request.getParameter("equipment_type");
            SearchConditions searchConditions = HttpRequestUtil.getSearchConditionsByRequest_time(request);
            LinkedHashMap<String, Stack<Interval>> result =siemService.getSIEMResult_equipment(equipment_id,equipment_type,searchConditions);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            Map<String,Object> last_result = new HashMap<>();
            last_result.put("data",result);
            last_result.put("start_time",searchConditions.getStartTime());
            last_result.put("end_time",searchConditions.getEndTime());
            String json = mapper.writeValueAsString(last_result);
            return Constant.successData(json);
        }catch (Exception e){
            log.error("获取SIEM结果失败："+e.getMessage());
            return Constant.failureMessage("获取SIEM结果失败!");
        }
    }
}

package com.jz.bigdata.util;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理controller request的工具类
 */
public class HttpRequestUtil {
    //数据转换时用到的gson对象
    private final static Gson gson = new Gson();

    /**
     * 获取请求参数
     * @param request
     * @return
     */
    public static HttpRequestParams getParams(HttpServletRequest request){
        HttpRequestParams params = new HttpRequestParams();
        params.setStartTime(request.getParameter("starttime"));//起始时间
        params.setEndTime(request.getParameter("endtime"));//截止时间
        params.setGroupField(request.getParameter("groupField"));//聚合字段
        //hsdata参数，转为map
        String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);
        Map<String,String> map = new HashMap<>();

        if(null!=hsData){
            map = gson.fromJson(hsData,Map.class);
        }
        params.setQueryMap(map);
        return params;
    }
}

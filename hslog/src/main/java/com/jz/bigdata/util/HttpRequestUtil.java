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
     * 将请求参数中的hsData转换成Map<String,Object>
     * @param request
     * @return
     */
    public static Map<String,String> getHsdata(HttpServletRequest request){
        String hsData = request.getParameter(ContextFront.DATA_CONDITIONS);
        Map<String,String> map = new HashMap<>();

        if(null!=hsData){
            map = gson.fromJson(hsData,Map.class);
        }
        return map;
    }
}

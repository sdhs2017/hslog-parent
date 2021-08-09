package com.jz.bigdata.util;

import com.jz.bigdata.roleauthority.menu.entity.Menu;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: yiyang
 * @Date: 2021/7/7 23:48
 * @Description: JSON转换工具类
 */
public class JSONUtil {
    /**
     * 通过泛型实现对JSONArray 转化成 List<T>的方法
     * @param json
     * @param transClass
     * @param <T>
     * @return
     */
    public static <T> List<T> getBeanListByJSONArrayString(String json,Class<?> transClass){
        List<T> result = new ArrayList<>();
        JSONArray array = JSONArray.fromObject(json);
        for(Object obj:array.toArray()){
            JSONObject jsonObject = JSONObject.fromObject(obj);
            T t = (T) JSONObject.toBean(jsonObject,transClass);
            result.add(t);
        }
        return result;
    }
}

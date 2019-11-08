package com.jz.bigdata.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

/**
 * @program: hslog-parent
 * @description: MapUtil
 * @author: Savilio
 * @create: 2019-11-08 10:50
 **/

public class MapUtil {

    /**
     *
     * @param json
     * @return
     */
    public static Map<String, String> json2map(String json) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new HashMap<String, String>();

        try {
            map = removeMapEmptyValue(mapper.readValue(json, Map.class));
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return map;
    }

    /**
     * map去除value为空的项
     * @param map
     * @return
     */
    public static Map<String,String> removeMapEmptyValue(Map<String,String> map){
        Set<String> set = map.keySet();
        Iterator<String> it = set.iterator();
        List<String> listKey = new ArrayList<String>();
        while (it.hasNext()) {
            String str = it.next();
            if(map.get(str)==null || "".equals(map.get(str))){
                listKey.add(str) ;
            }
        }
        for (String key : listKey) {
            map.remove(key);
        }
        return map;
    }

}

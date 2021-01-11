package com.jz.bigdata.business.logAnalysis.collector.cache;

import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.jz.bigdata.business.logAnalysis.collector.cache.bean.NXBean;
import com.jz.bigdata.business.logAnalysis.log.entity.Http;
import com.jz.bigdata.util.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: yiyang
 * @Date: 2021/1/8 9:42
 * @Description:
 */
public enum NXCache {
    INSTANCE;
    public Map<String, Integer> countMap = new HashMap<>();
    public Map<String,NXBean> beanMap = new HashMap<>();
    //最大时间
    public Map<String,Date> timeMap = new HashMap<>();


    /**
     * 数据未入库的处理方法，记录，统计！
     * @param ip
     * @param type
     * @param level
     */
    public void insert(String ip,String type,String level){
        //日志级别
        if(NXCache.INSTANCE.beanMap.containsKey(ip+type+level)){
            //count++
            Integer count = NXCache.INSTANCE.countMap.get(ip+type+level);
            NXCache.INSTANCE.countMap.put(ip+type+level,count+1);
        }else{
            //初始化信息
            NXBean bean = new NXBean();
            bean.setCount(0);
            bean.setIp(ip);
            bean.setType(type);
            if(null!=level){
                bean.setLevel(level);
            }
            bean.setIp_type_level(ip+type+level);
            NXCache.INSTANCE.beanMap.put(ip+type+level,bean);
            //第一条初始化
            NXCache.INSTANCE.countMap.put(ip+type+level,1);

        }
    }


}

package com.hs.elsearch.util;

import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Slf4j
public class HSDateUtil {

    /********DateTimeFormatter线程安全********/
    // 时间戳，用来匹配传入的时间参数，进行转换
    private static final DateTimeFormatter dtf_time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    //日期格式，用来进行输出,2020.03.01
    private static final DateTimeFormatter dtf_date = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    /**
     * 将时间范围内的日期进行返回，返回的日期格式为  前缀+2020.01.01*
     * 用来进行es的数据查询
     * @param startTime 起始时间
     * @param endTime 截止时间
     * @param prefixes 前缀 带*
     * @return
     */
    public static String[] dateArea2Indices(String startTime,String endTime,String... prefixes){

        LocalDateTime ldt_start = LocalDateTime.parse(startTime,dtf_time);
        LocalDateTime ldt_end = LocalDateTime.parse(endTime,dtf_time);
        //时分秒清零
        ldt_start = ldt_start.withHour(0).withMinute(0).withSecond(0);
        ldt_end = ldt_end.withHour(0).withMinute(0).withSecond(0);
        try{
            String[] result = findIndices(ldt_start,ldt_end,prefixes);
            return result;
        }catch(Exception e){
            log.error("index根据日期范围拆分异常：startTime："+startTime+"-----endTime:"+endTime);
            //出现异常，使用*进行查询，防止系统抛出异常
            return new String[]{"*"};
        }
    }


    /**
     * 根据时间范围和前缀，返回  前缀+yyyy.MM.dd*的索引名称
     * @param dBegin
     * @param dEnd
     * @param prefixes
     * @return
     */
    private static String[] findIndices(LocalDateTime dBegin, LocalDateTime dEnd,String... prefixes) {
        List<String> lDate = new ArrayList<String>();
        for(String prefix:prefixes){
            //第一天,将index中的*替换为日期+*
            lDate.add(prefix.replace("*",dtf_date.format(dBegin)+"*"));
            // 此日期是否在指定日期之后
            //对应将时间的时分秒清零，防止出现2020-01-01 12:00:00和2020-01-01 13:00:00 时，计算出现问题
            while (dBegin.isBefore(dEnd))
            {
                // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
                dBegin = dBegin.plusDays(1);
                lDate.add(prefix.replace("*",dtf_date.format(dBegin)+"*"));

            }
        }
        return lDate.toArray(new String[lDate.size()]);
    }

    public static void main(String[] args) {
        //DateTimeFormatter dtf_time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 获取日期
        //System.out.println(LocalDateTime.now().format(dtf_time));
    }
}

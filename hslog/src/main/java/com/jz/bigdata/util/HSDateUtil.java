package com.jz.bigdata.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HSDateUtil{
    //时间戳，用来匹配传入的时间参数，进行转换
    private static final SimpleDateFormat sdf_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //日期格式，用来进行输出,2020.03.01
    private static final SimpleDateFormat sd_date = new SimpleDateFormat("yyyy.MM.dd");

    /**
     * 将时间范围内的日期进行返回，返回的日期格式为  前缀+2020.01.01*
     * 用来进行es的数据查询
     * @param startTime 起始时间
     * @param endTime 截止时间
     * @param prefix 前缀 带*
     * @return
     */
    public static String[] dateArea2Indices(String startTime,String endTime,String prefix){
        Date begin;//开始时间
        Date end;//截止时间
        try{
            begin = sdf_time.parse(startTime);
            end = sdf_time.parse(endTime);
            String[] result = findIndices(begin,end,prefix);
            return result;
        }catch(Exception e){
            return null;
        }
    }

    /**
     * 根据时间范围和前缀，返回  前缀+yyyy.MM.dd*的索引名称
     * @param dBegin
     * @param dEnd
     * @param prefix
     * @return
     */
    private static String[] findIndices(Date dBegin, Date dEnd,String prefix) {
        List<String> lDate = new ArrayList<String>();
        //第一天,将index中的*替换为日期+*
        lDate.add(prefix.replace("*",sd_date.format(dBegin)+"*"));
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(dBegin);

        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(dEnd);

        // 此日期是否在指定日期之后
        while (dEnd.after(calBegin.getTime()))
        {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            lDate.add(prefix.replace("*",sd_date.format(calBegin.getTime())+"*"));
        }
        return lDate.toArray(new String[lDate.size()]);
    }
    public static void main(String[] args){
        dateArea2Indices("2019-08-01 12:00:00","2019-09-01 12:00:00","winlogbeat-*");
    }
}


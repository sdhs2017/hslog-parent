package com.hs.elsearch.util;

import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregation;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2020/11/25 15:57
 * @Description:
 * 定义其字段的数据类型或精度。
 * 例：event.duration:ns (纳秒)
 */
public class FieldsValueFormatter {
    /**
     * ESC类型字段，ecs字段基本一致，单独定义
     */
    public static final Map<String,String> ECS_FIELDS_NUMBER = new HashMap<>();
    static{
        //持续时间，纳秒，需转换成毫秒
        ECS_FIELDS_NUMBER.put("event.duration","ns");
    }

    /**
     * 数据转化，判定数据是否在定义的需转化的数组中，并进行特定的转化
     * @param field 字段
     * @param value 转化成double类型的结果数值
     * @return
     */
    public static Object formatter(String field, Double value){

        Object result = null;
        String ecs_fields_number_value = ECS_FIELDS_NUMBER.get(field);
        //是否在已定义的字段map中
        if(null!=ecs_fields_number_value){
            switch(ecs_fields_number_value){
                case "ns":
                    //保留小数点2位
                    DecimalFormat decimalFormat=new DecimalFormat("0.00");
                    //纳秒转化成毫秒
                    result = decimalFormat.format(value/1000/1000);
                    break;
                default:
                    result = value;
                    break;
            }
        }else{
            result = value;
        }
        return result;
    }
}

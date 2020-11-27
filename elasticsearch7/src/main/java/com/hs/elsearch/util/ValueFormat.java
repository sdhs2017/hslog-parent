package com.hs.elsearch.util;

import com.hs.elsearch.entity.Metric;
import joptsimple.internal.Strings;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregation;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * 处理es返回的value值
 * 根据前端设置单位进行处理
 * 支持byte类型和百分比的转换
 */
public class ValueFormat {
    /**
     * 只将数值进行转换，不带单位
     * @param value
     * @param unit
     * @return
     */
    public static Object formatter(NumericMetricsAggregation.SingleValue value,String unit,Metric metric){
        //先将数值转成double
        Double double_value = (Double.isInfinite(value.value())||Double.isNaN(value.value()))?0:value.value();
        Object result;
        //保留小数点2位
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
        if(!Strings.isNullOrEmpty(unit)){
            try{
                switch (unit.toUpperCase()){
                    case "%":
                        Float floatValue = Float.parseFloat(double_value.toString());
                        floatValue = floatValue*100;
                        result = decimalFormat.format(floatValue);
                        break;
                    case "MB":
                        double_value = double_value/1024/1024;//byte -> MB
                        result = decimalFormat.format(double_value);
                        break;
                    case "GB":
                        double_value = double_value/1024/1024/1024;//byte -> GB
                        result = decimalFormat.format(double_value);
                        break;
                    case "TB":
                        double_value = double_value/1024/1024/1024/1024;//byte -> TB
                        result = decimalFormat.format(double_value);
                        break;
                    default:
                        result = double_value;
                        break;
                }
            }catch(Exception e){
                result = double_value.toString();
            }
        }else{
            result = FieldsValueFormatter.formatter(metric.getField(),double_value);
        }
        return result;
    }

    /**
     * 将数值转换并带上单位，需要给数字添加千分位
     * @param value
     * @param unit
     * @return
     */
    public static Object formatterAppendUnit(NumericMetricsAggregation.SingleValue value, String unit,Metric metric){
        Double double_value = (Double.isInfinite(value.value())||Double.isNaN(value.value()))?0:value.value();
        Object result;
        //保留小数点2位
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
        if(!Strings.isNullOrEmpty(unit)){
            try{
                switch (unit.toUpperCase()){
                    case "%":
                        Float floatValue = Float.parseFloat(double_value.toString());
                        floatValue = floatValue*100;
                        result = decimalFormat.format(floatValue)+unit;
                        break;
                    case "MB":
                        double_value = double_value/1024/1024;//byte -> MB
                        result =  addThousandSeparator(decimalFormat.format(double_value))+unit;
                        break;
                    case "GB":
                        double_value = double_value/1024/1024/1024;//byte -> GB
                        result = addThousandSeparator(decimalFormat.format(double_value))+unit;
                        break;
                    case "TB":
                        double_value = double_value/1024/1024/1024/1024;//byte -> TB
                        result = addThousandSeparator(decimalFormat.format(double_value))+unit;
                        break;
                    default:
                        //不进行任何单位转换时，需要对count类型的结果取整
                        if("Count".equals(metric.getAggType())){
                            result = addThousandSeparator(double_value.intValue()+"");
                        }else{
                            result = addThousandSeparator(double_value+"");
                        }

                        break;
                }
            }catch(Exception e){
                result = double_value.toString();
            }
        }else{
            //不进行任何单位转换时，需要对count类型的结果取整
            if("Count".equals(metric.getAggType())){
                Object value_obj = FieldsValueFormatter.formatter(metric.getField(),double_value);
                //count取整，主要是出去默认保留的以为小数（例：100.0）
                result = addThousandSeparator(Double.valueOf(value_obj.toString()).intValue()+"");
            }else{
                result = addThousandSeparator(FieldsValueFormatter.formatter(metric.getField(),double_value)+"");
            }
        }
        return result;
    }
    /**
     * @Title: addThousandSeparator
     * @Description: 格式化数字为千分位
     * @param text
     * @return 设定文件,由于double的位数有限制，所以这里自己拆分的方式来做。
     * @return String 返回类型
     */
    public static String addThousandSeparator(String text) {
        if (text == null) {
            return null;
        }
        int index = text.indexOf(".");
        if (index > 0) {
            String integerPartial = text.substring(0, index);
            String decimalPartial = text.substring(index);
            return addThousandSeparatorForInteger(integerPartial) + decimalPartial;
        } else {
            return addThousandSeparatorForInteger(text);
        }
    }

    // 只给整数加千分位分隔符
    public static String addThousandSeparatorForInteger(String text) {
        int index = text.indexOf(".");
        if (index != -1) {
            return text;
        } else {
            int length = text.length();
            ArrayList<String> stringContainer = new ArrayList<String>();
            while (length > 3) {
                stringContainer.add(text.substring(length - 3, length));
                length = length - 3;
            }
            stringContainer.add(text.substring(0, length)); // 将最前面的小于三个数字的也加入到数组去
            StringBuffer buffer = new StringBuffer();
            for (int i = stringContainer.size() - 1; i >= 0; i--) {
                buffer.append(stringContainer.get(i) + ",");
            }
            buffer.deleteCharAt(buffer.length() - 1);
            return buffer.toString();
        }
    }
}

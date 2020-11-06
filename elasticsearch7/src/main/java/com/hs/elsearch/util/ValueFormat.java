package com.hs.elsearch.util;

import joptsimple.internal.Strings;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ValueFormat {
    public static Object formatter(Double value,String unit){
        Object result;
        //保留小数点2位
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
        if(!Strings.isNullOrEmpty(unit)){
            try{
                switch (unit.toUpperCase()){
                    case "%":
                        Float folatValue = Float.parseFloat(value.toString());
                        folatValue = folatValue*100;
                        result = decimalFormat.format(folatValue);
                        break;
                    case "MB":
                        value = value/1024/1024;//byte -> MB
                        result = decimalFormat.format(value);
                        break;
                    case "GB":
                        value = value/1024/1024/1024;//byte -> GB
                        result = decimalFormat.format(value);
                        break;
                    case "TB":
                        value = value/1024/1024/1024/1024;//byte -> TB
                        result = decimalFormat.format(value);
                        break;
                    default:
                        result = value;
                        break;
                }
            }catch(Exception e){
                result = value.toString();
            }
        }else{
            result = value;
        }
        return result;
    }
    public static Object formatterAppendUnit(Double value,String unit){
        Object result;
        //保留小数点2位
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
        if(!Strings.isNullOrEmpty(unit)){
            try{
                switch (unit.toUpperCase()){
                    case "%":
                        Float folatValue = Float.parseFloat(value.toString());
                        folatValue = folatValue*100;
                        result = decimalFormat.format(folatValue)+unit;
                        break;
                    case "MB":
                        value = value/1024/1024;//byte -> MB
                        result =  addThousandSeparator(decimalFormat.format(value))+unit;
                        break;
                    case "GB":
                        value = value/1024/1024/1024;//byte -> GB
                        result = addThousandSeparator(decimalFormat.format(value))+unit;
                        break;
                    case "TB":
                        value = value/1024/1024/1024/1024;//byte -> TB
                        result = addThousandSeparator(decimalFormat.format(value))+unit;
                        break;
                    default:
                        result = addThousandSeparator(value+"");
                        break;
                }
            }catch(Exception e){
                result = value.toString();
            }
        }else{
            result = addThousandSeparator(value+"");
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

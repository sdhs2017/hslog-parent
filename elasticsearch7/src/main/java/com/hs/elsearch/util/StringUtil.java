package com.hs.elsearch.util;

public class StringUtil {
    /**
     * 处理字符串两端的引号
     * @param text
     * @return
     */
    public static String removeQuotation(String text){
        //去掉两端空格
        text = text.trim();
        //如果字段内容符合 \"abc123\" 则去掉两端的引号
        if(text.matches("\".*?\"")){
            text = text.substring(1,text.length()-1);
        }
        return text;
    }

    public static void main(String[] args) {
        System.out.println(removeQuotation("\"123abc\""));
    }
}

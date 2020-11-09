package com.jz.bigdata.util;

import org.apache.directory.api.util.Strings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @program: hsgit
 * @description: 人员角色
 * @author: jiyourui
 * @create: 2019-11-11 00:43
 **/
public class ContextRoles {

    // 管理员角色
    public static final String MANAGEMENT = "1";

    /**
     * beats types
     */
    public static final String WINLOGBEAT = "winlogbeat";
    public static final String PACKETBEAT = "packetbeat";
    public static final String METRICBEAT = "metricbeat";
    public static final String FILEBEAT = "filebeat";


    /**
     * beats log level 中文情况说明
     */
    public static final String LOG_LEVEL_INFO = "信息";

    public static final String LOG_LEVEL_WARN = "警告";

    public static final String LOG_LEVEL_ERROR = "错误";

    /**
     * ecs中userid的字段
     */
    public static final String ECS_USERID = "fields.userid";

    public static void main(String [] args){
        /*String date = "2020-03-25T01:23:41.386Z";
        *//*System.out.println(date.substring(0,date.indexOf("T")));*//*
        *//*Date now = new Date();*//*
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        *//*System.out.println(format.format(now));*//*

        try {
            format.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            Date ss = format.parse(date);
            System.out.println(format.format(ss));
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        String content = "等";
        String ss = Strings.toLowerCase(content);
        System.out.println(ss);
    }
}

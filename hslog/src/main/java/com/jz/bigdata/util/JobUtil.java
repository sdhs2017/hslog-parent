package com.jz.bigdata.util;

import org.quartz.JobDataMap;

import java.lang.reflect.Field;

/**
 * @Author: yiyang
 * @Date: 2021/4/28 15:17
 * @Description:
 */
public class JobUtil {
    /**
     * 将bean对象转化成jobDataMap
     * @param bean 对象
     * @return JobDataMap
     * @throws Exception
     */
    public static <T> JobDataMap alert2JobDataMap(T bean) throws Exception {
        JobDataMap map = new JobDataMap();
        Class cls = bean.getClass();
        Field[] fields = cls.getDeclaredFields();
        //遍历Alert class的所有属性
        for(int i=0; i<fields.length; i++){
            Field f = fields[i];
            f.setAccessible(true);
            //保证所有属性都有值，在获取参数值时不会为空
            map.put(f.getName(), f.get(bean)==null?"":f.get(bean).toString());
        }
        return map;
    }
}

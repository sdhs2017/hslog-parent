package com.jz.bigdata.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanMap;

public class JavaBeanUtil {

	private static Logger logger = LoggerFactory.getLogger(JavaBeanUtil.class);

	public static <T> T convertMapToBean(T bean,Map<String,Object> map){
		try{
			BeanMap beanMap = BeanMap.create(bean);
			beanMap.putAll(map);
		}catch (Exception e){
			e.printStackTrace();
		}
		return bean;
	}
	/**
	 * map 转实体类
	 * 
	 * @param clazz
	 * @param map
	 * @param <T>
	 * @return
	 */
	public static <T> T convertMapToBean(Class<T> clazz, Map<String, Object> map) {
		T obj = null;
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
			obj = clazz.newInstance(); // 创建 JavaBean 对象

			// 给 JavaBean 对象的属性赋值
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; i++) {
				PropertyDescriptor descriptor = propertyDescriptors[i];
				String propertyName = descriptor.getName();

				if (map.containsKey(propertyName)) {
					// 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
					try {
						Object value = map.get(propertyName);
						if ("".equals(value)) {
							value = null;
						}
						Object[] args = new Object[1];
						args[0] = value;
						// String args = value.toString();
						// System.out.println("字段："+propertyName+" 类型
						// ："+descriptor.getPropertyType().toString());
						if (descriptor.getPropertyType().toString().equals("int")) {
							descriptor.getWriteMethod().invoke(obj,
									(value != null ? Integer.parseInt(value.toString()) : 0));
							/*
							 * if(value!=null) { descriptor.getWriteMethod().invoke(obj,
							 * Integer.parseInt(value.toString())); }else {
							 * descriptor.getWriteMethod().invoke(obj, args); }
							 */

						} else {
							descriptor.getWriteMethod().invoke(obj, args);
						}

					} catch (InvocationTargetException e) {
						// TODO: handle exception
						logger.error("convertMapToBean字段映射失败 Error{}", e);
						continue;
					}

				}
			}
		} catch (IllegalAccessException e) {
			logger.error("convertMapToBean 实例化JavaBean失败 Error{}", e);
		} catch (IntrospectionException e) {
			logger.error("convertMapToBean 分析类属性失败 Error{}", e);
		} catch (IllegalArgumentException e) {
			logger.error("convertMapToBean 映射错误 Error{}", e);
		} catch (InstantiationException e) {
			logger.error("convertMapToBean 实例化 JavaBean 失败 Error{}", e);
		} /*
			 * catch (InvocationTargetException e){
			 * logger.error("convertMapToBean字段映射失败 Error{}" ,e); }
			 */catch (Exception e) {
			logger.error("convertMapToBean Error{}", e);
		}
		return (T) obj;
	}

}

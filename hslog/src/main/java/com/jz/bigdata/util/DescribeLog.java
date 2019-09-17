package com.jz.bigdata.util;
import java.lang.annotation.*;

/**
 * 
 * 自定义controller描述注解
 */

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DescribeLog {
    String describe()  default "";
}
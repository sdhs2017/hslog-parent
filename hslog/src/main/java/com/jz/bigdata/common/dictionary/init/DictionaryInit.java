package com.jz.bigdata.common.dictionary.init;

import com.jz.bigdata.common.dictionary.struct.Dictionary;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/5/7 23:24
 * @Description: 字典类初始化方法，研发人员在声明新的字典对象时，在该类进行声明
 */
@Slf4j
public class DictionaryInit {
    //----------------------dictionary_keys----------------------------
    /**
     * 该部分用于声明字典的父级key，命名必须规范，值不能有重复
     */

    //数据源管理-标签管理-发现规则
    public static final String DSG_TAG_LIBRARY_DISCOVER_WAY = "DSG_TAG_LIBRARY_DISCOVER_WAY";
    //数据源管理-数据源基本信息-数据发现状态
    public static final String DATA_SOURCE_DISCOVER_STATE = "DATA_SOURCE_DISCOVER_STATE";
    //数据源管理-数据源基本信息-是否初始化
    public static final String DATA_SOURCE_INIT_STATE = "DATA_SOURCE_INIT_STATE";
    //元数据管理-是否自动发现
    public static final String DATA_SOURCE_IS_AUTO_DISCOVERY = "DATA_SOURCE_IS_AUTO_DISCOVERY";

    //others

    //------------------------------------------

    public static Map<Object, Map<Object,Object>> init() {
        Dictionary dictionary = new Dictionary();
        try{
            //------------------------------------------------
            /**
             *  初始化字典数据
             */
            //数据源管理-标签管理-发现规则
            dictionary.setKey(DSG_TAG_LIBRARY_DISCOVER_WAY).setList(1,"按正则发现");
            //数据源管理-数据源基本信息-数据发现状态 0；未开始   1：敏感数据发现中...   2：敏感数据发现完成
            dictionary.setKey(DATA_SOURCE_DISCOVER_STATE).setList(0,"未开始").setList(1,"数据发现进行中...").setList(2,"数据发现完成").setList(3,"数据发现失败");
            //数据源管理-数据源基本信息-是否初始化 0:否  1：是
            dictionary.setKey(DATA_SOURCE_INIT_STATE).setList(0,"否").setList(1,"是");
            //元数据管理-是否自动发现 0:否  1：是
            dictionary.setKey(DATA_SOURCE_IS_AUTO_DISCOVERY).setList("0","否").setList("1","是");
            //others

            //-----------------------------------------------
        }catch (Exception e){
            log.error("字典初始化失败："+e.getMessage());
        }
        return dictionary.getDictionaryList();
    }
}

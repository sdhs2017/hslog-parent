package com.jz.bigdata.common.dictionary.struct;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/5/5 16:23
 * @Description: 字典抽象类 参考StringBuffer类及其抽象类
 * 字典基本模型如下：
 * 父级key： eg:DISCOVER_WAY （数据源敏感字段发现方式）
 * value： 一组K/V
 *      eg: 0/无规则    1/按正则发现
 */
public class Dictionary {
    /**
     * 构造方法
     */
    public Dictionary() {
    }

    /**
     * 存储数据的对象，格式参考类注释
     */
    private Map<Object, Map<Object,Object>> dictionaryList = new HashMap<>();
    /**
     * 一组K/V对应的父级的key
     */
    Object key;

    /**
     * 获取当前key的值
     * @return key（obj）
     */
    public Object getKey() {
        return key;
    }

    /**
     * 字典key赋值
     * @param key
     * @return 对象引用
     */
    public Dictionary setKey(Object key) {
        this.key = key;
        return this;
    }

    //获取字典存储对象
    public Map<Object, Map<Object,Object>> getDictionaryList(){
        return this.dictionaryList;
    }



    /**
     * 写入一组K/V eg：0/无规则 1/按正则发现
     * @param listKey
     * @param listValue
     * @return 返回当前对象的引用，可以一直调用
     */
    public Dictionary setList(Object listKey, Object listValue) throws Exception {
        //如果存储对象为空，进行初始化
        if(this.dictionaryList==null){
            this.dictionaryList = new HashMap<>();
        }
        //TODO setkey之后才能setList， 还不清楚如何实现
        //如果key存在
        if(getKey()!=null){
            //如果setlist时，父级key对应的一组字典K/V对象不为空
            if(this.dictionaryList.get(getKey())!=null){
                //直接写入对象
                this.dictionaryList.get(getKey()).put(listKey,listValue);
            }else{
                //创建用户存储一组字典K/V的对象，使用map存储，因为key不能重复
                Map<Object,Object> map = new HashMap<>();
                map.put(listKey,listValue);
                this.dictionaryList.put(getKey(),map);
            }
        }else{
            throw new Exception("缺少key，使用setKey赋值！");
        }
        return this;
    }
}

package com.jz.bigdata.common.dictionary.cache;

import com.jz.bigdata.common.dictionary.init.DictionaryInit;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/5/5 19:11
 * @Description: 字典类缓存，进行字典的初始化操作 并提供获取字典数据的方法，使用单例实现，并保证线程安全。
 * main方法提供使用示例。
 *
 * kv字典的id ，eg：combox id
 *
 * 1.getComboboxList(String key) 根据key获取相应的combobox列表
 * 2.getDictionaryCache()获取所有字典数据  key：Object 一组字典列表的标识   value：Map<Object,Object> 字典列表的K/V
 * 字典基本模型如下：
 *  * key： DISCOVER_WAY （数据源敏感字段发现方式）
 *  * value： 一组K/V
 *  *       0/无规则
 *  *       1/按正则发现
 */
@Slf4j
public enum DictionaryCache {
    INSTANCE;
    //生成前端所需combobox所需的标签
    //eg:[{"label":"无规则","value":0},{"label":"按正则发现","value":1}]
    private final String COMBOBOX_LABEL = "label";
    private final String COMBOBOX_VALUE = "value";
    //字典数据存放对象
    private Map<Object, Map<Object,Object>> dictionaryCache;

    /**
     * 获取字典对象，原始数据
     * @return 返回数据格式参考类注释
     */
    public Map<Object, Map<Object,Object>> getDictionaryCache() {
        // 无内容，进行初始化
        if(this.dictionaryCache==null||this.dictionaryCache.size()==0){
            dictionaryCache = DictionaryInit.init();
        }
        return dictionaryCache;
    }

    /**
     * 获取combobox类型的返回数据
     * @param key 字典的key，用于查找一组字典列表
     * @return combobox类型的返回数据 eg:[{"label":"无规则","value":0},{"label":"按正则发现","value":1}]
     */
    public List<Map<String,Object>> getComboboxList(String key){
        List<Map<String,Object>> result = new ArrayList<>();
        //如果没有数据，进行一次初始化
        if(this.dictionaryCache==null||this.dictionaryCache.size()==0){
            dictionaryCache = DictionaryInit.init();
        }
        //遍历查找到的数据，转化成前端所需格式
        Map<Object,Object> list = this.dictionaryCache.get(key);
        //要查找的key存在数据
        if(list!=null){
            //遍历这组字典数据，组装combobox结构数据
            for(Map.Entry<Object, Object> entry : list.entrySet()){
                Map<String,Object> map = new HashMap<>();
                //value对应combobox的label
                map.put(COMBOBOX_LABEL,entry.getValue());
                //key对应combobox的value
                map.put(COMBOBOX_VALUE,entry.getKey());
                result.add(map);
            }
        }else{
            //key不存在数据，直接返回即可
        }
        return result;
    }
    //--------test-------------
    public static void main(String[] args) {
        //使用方法 ：DictionaryCache.INSTANCE.getComboboxList(DictionaryInit.DSG_TAG_LIBRARY_DISCOVER_WAY)
        //结果 eg:[{"label":"无规则","value":0},{"label":"按正则发现","value":1}]
        System.out.println(JSONArray.fromObject(DictionaryCache.INSTANCE.getComboboxList(DictionaryInit.DSG_TAG_LIBRARY_DISCOVER_WAY)));
    }
}

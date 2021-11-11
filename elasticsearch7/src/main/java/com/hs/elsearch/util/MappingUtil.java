package com.hs.elsearch.util;

import java.util.*;

public class MappingUtil {
    //ES获取mapping后的结构中，多级之间通过properties进行嵌套
    private static final String ES_SOURCEMAP_PROPERTIES_FIELD_NAME = "properties";
    //ES 字段类型的key
    private static final String ES_SOURCEMAP_FIELD_TYPE = "type";
    //ES 字段是否可聚合对应的key
    private static final String ES_SOURCEMAP_CAN_AGG = "fielddata";
    //有些字段会通过fields属性拓展2级类型
    //示例：
    // "title" : {
    //     "type" : "text",
    //     "fields" : {
    //       "raw" : {
    //         "type" : "keyword"
    //       }
    //    }
    //  }
    //title为text title.raw为keyword
    private static final String ES_SOURCEMAP_FIELDS = "fields";
    /**
     * mapping对象格式化
     * @param sourceMap mapping对象列表
     * @return List<Metadata>
     */
    public static List<MappingField> getAllListMetadataBySourceMap(Map<String,Object> sourceMap){
        List<MappingField> mappingFieldList = new ArrayList<MappingField>();
        //获取mapping下的properties
        Map<String,Object> propertiesMap = (Map<String, Object>) sourceMap.get(ES_SOURCEMAP_PROPERTIES_FIELD_NAME);
        //遍历properties下的数据 每个元素的key为字段名，value为相关的属性和值
        for(Map.Entry<String, Object> map:propertiesMap.entrySet()){
            //递归
            mappingFieldList.addAll(getChildren(map,""));
        }
        //排序
        Collections.sort(mappingFieldList,new Comparator<MappingField>() {
            //升序排序
            public int compare(MappingField o1,
                               MappingField o2) {
                return o1.getFieldName().toLowerCase().compareTo(o2.getFieldName().toLowerCase());
            }
        });
        return mappingFieldList;
    }

    /**
     * 递归模块，循环遍历节点下的信息
     * @param propertiesMap
     * @param path
     * @return
     */
    private static List<MappingField> getChildren(Map.Entry<String, Object> propertiesMap, String path){
        List<MappingField> metadataList = new ArrayList<>();

        //获取value
        Map<String,Object> fieldValue = (Map<String, Object>)propertiesMap.getValue();
        //获取key
        String fieldName = propertiesMap.getKey();
        //组装新的路径，上下级之间用.隔开
        String fieldPathName = ("".equals(path)?fieldName:path+"."+fieldName);
        //尝试获取其properties属性
        Object properties = fieldValue.get(ES_SOURCEMAP_PROPERTIES_FIELD_NAME);
        if(properties!=null){
            /**
             * 如果存在properties，认为这个节点是下一级目录，并不是数据存储字段
             * 因此只作为一个父级节点来处理，继续找子节点
             */
            //遍历properties下的数据 每个元素的key为字段名，value为相关的属性和值
            for(Map.Entry<String, Object> map:((Map<String,Object>)properties).entrySet()) {
                metadataList.addAll(getChildren(map,fieldPathName));
            }
        }else{
            /**
             * 如果不存在，则说明该字段是最终节点，解析信息
             */

            MappingField mappingField = new MappingField();
            //字段名
            mappingField.setFieldName(fieldPathName);
            //字段类型
            if(fieldValue.get(ES_SOURCEMAP_FIELD_TYPE)!=null){
                //字段是否可排序
                if(ElasticConstant.SORT_TYPES.contains(fieldValue.get(ES_SOURCEMAP_FIELD_TYPE))){
                    mappingField.setSortable(true);
                }else{
                    mappingField.setSortable(false);
                }
                mappingField.setFieldType(fieldValue.get(ES_SOURCEMAP_FIELD_TYPE).toString());
            }
            //字段可聚合
            if(fieldValue.get(ES_SOURCEMAP_CAN_AGG)!=null){
                mappingField.setFieldData((boolean)fieldValue.get(ES_SOURCEMAP_CAN_AGG));
            }
            metadataList.add(mappingField);
            //raw信息，也作为一个单独的字段处理
            if(fieldValue.get(ES_SOURCEMAP_FIELDS)!=null){
                MappingField fields = new MappingField();
                //raw存在于fields的值中
                Map<String, Object> rawField = (Map<String, Object>)fieldValue.get(ES_SOURCEMAP_FIELDS);
                for(Map.Entry<String, Object> rawEntry:rawField.entrySet()){
                    Map<String, Object> raw = (Map<String, Object>)rawEntry.getValue();
                    fields.setFieldName(fieldPathName+"."+rawEntry.getKey());
                    //数据类型
                    if(raw.get(ES_SOURCEMAP_FIELD_TYPE)!=null){
                        fields.setFieldType(raw.get(ES_SOURCEMAP_FIELD_TYPE).toString());
                    }
                    metadataList.add(fields);
                }
            }

        }
        return metadataList;
    }
    /**
     * mapping对象格式化，将其多级结构打平
     * @param sourceMap mapping对象列表
     * @return Map<String,MappingField>
     */
    public static Map<String,MappingField> getAllMapMetadataBySourceMap(Map<String,Object> sourceMap){
        Map<String,MappingField> mappingFieldMap = new HashMap<>();
        //获取mapping下的properties
        Map<String,Object> propertiesMap = (Map<String, Object>) sourceMap.get(ES_SOURCEMAP_PROPERTIES_FIELD_NAME);
        //遍历properties下的数据 每个元素的key为字段名，value为相关的属性和值
        for(Map.Entry<String, Object> map:propertiesMap.entrySet()){
            //递归
            mappingFieldMap.putAll(getChildren2Map(map,""));
        }
        return mappingFieldMap;
    }

    /**
     * 递归模块，循环遍历节点下的信息
     * @param propertiesMap
     * @param path
     * @return
     */
    private static Map<String,MappingField> getChildren2Map(Map.Entry<String, Object> propertiesMap, String path){
        Map<String,MappingField> metadataList = new HashMap<>();

        //获取value
        Map<String,Object> fieldValue = (Map<String, Object>)propertiesMap.getValue();
        //获取key
        String fieldName = propertiesMap.getKey();
        //组装新的路径，上下级之间用.隔开
        String fieldPathName = ("".equals(path)?fieldName:path+"."+fieldName);
        //尝试获取其properties属性
        Object properties = fieldValue.get(ES_SOURCEMAP_PROPERTIES_FIELD_NAME);
        if(properties!=null){
            /**
             * 如果存在properties，认为这个节点是下一级目录，并不是数据存储字段
             * 因此只作为一个父级节点来处理，继续找子节点
             */
            //遍历properties下的数据 每个元素的key为字段名，value为相关的属性和值
            for(Map.Entry<String, Object> map:((Map<String,Object>)properties).entrySet()) {
                metadataList.putAll(getChildren2Map(map,fieldPathName));
            }
        }else{
            /**
             * 如果不存在，则说明该字段是最终节点，解析信息
             */
            MappingField mappingField = new MappingField();
            //字段名
            mappingField.setFieldName(fieldPathName);
            //字段类型
            if(fieldValue.get(ES_SOURCEMAP_FIELD_TYPE)!=null){
                //字段是否可排序
                if(ElasticConstant.SORT_TYPES.contains(fieldValue.get(ES_SOURCEMAP_FIELD_TYPE))){
                    mappingField.setSortable(true);
                }else{
                    mappingField.setSortable(false);
                }
                mappingField.setFieldType(fieldValue.get(ES_SOURCEMAP_FIELD_TYPE).toString());
            }
            //字段可聚合
            if(fieldValue.get(ES_SOURCEMAP_CAN_AGG)!=null){
                mappingField.setFieldData((boolean)fieldValue.get(ES_SOURCEMAP_CAN_AGG));
            }
            metadataList.put(fieldPathName,mappingField);
            //字段下的fields属性信息，也作为一个单独的字段处理
            if(fieldValue.get(ES_SOURCEMAP_FIELDS)!=null){
                MappingField fields = new MappingField();
                //raw存在于fields的值中
                Map<String, Object> rawField = (Map<String, Object>)fieldValue.get(ES_SOURCEMAP_FIELDS);
                for(Map.Entry<String, Object> rawEntry:rawField.entrySet()){
                    Map<String, Object> raw = (Map<String, Object>)rawEntry.getValue();
                    fields.setFieldName(fieldPathName+"."+rawEntry.getKey());
                    //数据类型
                    if(raw.get(ES_SOURCEMAP_FIELD_TYPE)!=null){
                        fields.setFieldType(raw.get(ES_SOURCEMAP_FIELD_TYPE).toString());
                    }
                    fields.setIsFields(true);
                    metadataList.put(fieldPathName+"."+rawEntry.getKey(),fields);
                }
            }

        }
        return metadataList;
    }
    /**
     * map 按 key 升序排序， key为string，value为任意对象，通过泛型实现
     * @param map key为string，value为任意对象的map
     * @return
     */
    public static <T> Map<String, T> sortByKey(Map<String, T> map) {
        Map<String, T> result = new LinkedHashMap<>(map.size());
        map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }
}

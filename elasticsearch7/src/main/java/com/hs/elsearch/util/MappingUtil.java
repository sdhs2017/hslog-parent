package com.hs.elsearch.util;

import java.util.*;

public class MappingUtil {
    /**
     * mapping对象格式化
     * @param sourceMap mapping对象列表
     * @return List<Metadata>
     */
    public static List<MappingField> getAllListMetadataBySourceMap(Map<String,Object> sourceMap){
        List<MappingField> mappingFieldList = new ArrayList<MappingField>();
        //获取mapping下的properties
        Map<String,Object> propertiesMap = (Map<String, Object>) sourceMap.get("properties");
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
        Object properties = fieldValue.get("properties");
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
            if(fieldValue.get("type")!=null){
                mappingField.setFieldType(fieldValue.get("type").toString());
            }
            //字段可聚合
            if(fieldValue.get("fielddata")!=null){
                mappingField.setFieldData((boolean)fieldValue.get("fielddata"));
            }
            metadataList.add(mappingField);
            //raw信息，也作为一个单独的字段处理
            if(fieldValue.get("fields")!=null){
                MappingField fields = new MappingField();
                //raw存在于fields的值中
                Map<String, Object> rawField = (Map<String, Object>)fieldValue.get("fields");
                for(Map.Entry<String, Object> rawEntry:rawField.entrySet()){
                    Map<String, Object> raw = (Map<String, Object>)rawEntry.getValue();
                    fields.setFieldName(fieldPathName+"."+rawEntry.getKey());
                    //数据类型
                    if(raw.get("type")!=null){
                        fields.setFieldType(raw.get("type").toString());
                    }
                    metadataList.add(fields);
                }
            }

        }
        return metadataList;
    }


}

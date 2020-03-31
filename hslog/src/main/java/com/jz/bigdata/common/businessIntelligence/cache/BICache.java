package com.jz.bigdata.common.businessIntelligence.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hs.elsearch.dao.logDao.ILogIndexDao;
import com.jz.bigdata.common.businessIntelligence.entity.MappingField;
import com.jz.bigdata.common.metadata.service.impl.MetadataServiceImpl;
import org.apache.log4j.Logger;
import org.elasticsearch.client.indices.IndexTemplateMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 根据业务需求，在点击聚合类型时需要加载不同的字段信息。
 * 将基础类型进行分类：
 * date：date
 * number：long,integer,short,byte,double,float,half_float,scaled_float
 * keyword：keyword以及fielddata=true
 *
 * 将聚合类型进行分类
 * Sum/Avg: number
 * Max/Min:date&number
 * Date:date
 * Terms:keyword
 *
 * cache的key为templateName
 * Map的key为templateName+类型
 * value为一个List<MappingField>用来存储该类型对应的所有字段信息
 * 单例实现
 */
public enum BICache {
    INSTANCE;
    private static Logger logger = Logger.getLogger(BICache.class);
    //
    private Cache<String, List<MappingField>> biCache = Caffeine.newBuilder()
            .maximumSize(100)//最大条数，
            //.expireAfterWrite(99999, TimeUnit.DAYS)//过期时间，
            .recordStats()
            .build();

    public Cache<String, List<MappingField>> getBiCache() {
        return biCache;
    }

    public void setBiCache(Cache<String, List<MappingField>> biCache) {
        this.biCache = biCache;
    }

    /**
     * 将template的结构信息初始化到cache中
     * @param templateName
     */
    public boolean putBiCache(String templateName,ILogIndexDao logIndexDao){
        //写入cache是否成功，用来判断是否有数据写入
        boolean result = false;
        try{
            //获取template的数据
            List<IndexTemplateMetaData> list = logIndexDao.getTemplateData(templateName);
            //由于API仅提供类似正则匹配的模糊查询，返回的都是list，但是对于本功能而言，需要返回唯一一条记录
            for(IndexTemplateMetaData itmd:list){
                Map<String,List<MappingField>> cacheValue = new HashMap<>();
                //获取mapping信息
                MappingMetaData mmd = itmd.mappings();
                String name = itmd.name();
                //获取mapping信息
                Map<String, Object> sourceMap = mmd.getSourceAsMap();
                //获取所有字段信息
                List<MappingField> fieldList = getAllListMetadataBySourceMap(sourceMap);
                //定义三个类型的list
                List<MappingField> forSumAvgList = new ArrayList<>();
                List<MappingField> forMaxMinList = new ArrayList<>();
                List<MappingField> forTermsList = new ArrayList<>();
                List<MappingField> forDateList = new ArrayList<>();
                //遍历list，放到cache中
                for(MappingField mf :fieldList){
                    switch(mf.getFieldType()){
                        //number类型
                        case "long":case "integer":case "short":case "byte":case "double":case "float":case "half_float":case "scaled_float":
                            forSumAvgList.add(mf);
                            forMaxMinList.add(mf);
                            break;
                        case "date":
                            forDateList.add(mf);
                            forMaxMinList.add(mf);
                            break;
                        case "keyword":
                            forTermsList.add(mf);
                            break;
                        default:
                            //其他类型需要对fielddata进行判定，为true时,作为keyword存储
                            if(mf.getFieldData()){
                                forTermsList.add(mf);
                            }
                            break;
                    }
                }
                //放入cache中
                this.biCache.put(name+"SumAvg",forSumAvgList);
                this.biCache.put(name+"MaxMin",forMaxMinList);
                this.biCache.put(name+"Terms",forTermsList);
                this.biCache.put(name+"Date",forDateList);
                result = true;
            }
        }catch(Exception e){
            logger.error("template信息初始化到cache失败！"+e.getMessage());
        }
        return result;
    }
    /**
     * mapping对象格式化
     * @param sourceMap mapping对象列表
     * @return List<Metadata>
     */
    private List<MappingField> getAllListMetadataBySourceMap(Map<String,Object> sourceMap){
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
    private List<MappingField> getChildren(Map.Entry<String, Object> propertiesMap, String path){
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

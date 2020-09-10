package com.hs.elsearch.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hs.elsearch.dao.logDao.ILogIndexDao;
import com.hs.elsearch.util.MappingField;
import com.hs.elsearch.util.MappingUtil;
import org.apache.log4j.Logger;
import org.elasticsearch.client.indices.IndexTemplateMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public enum SearchCache {
    INSTANCE;
    private static Logger logger = Logger.getLogger(SearchCache.class);
    //服务于可视化模块
    private Cache<String, List<MappingField>> biCache = Caffeine.newBuilder()
            .maximumSize(100)//最大条数，
            //.expireAfterWrite(99999, TimeUnit.DAYS)//过期时间，
            .recordStats()
            .build();
    //mapping字段信息，服务于对字段类型的查询需求
    private Cache<String, Map<String,String>> mappingCache = Caffeine.newBuilder()
            .maximumSize(100)//最大条数，
            //.expireAfterWrite(99999, TimeUnit.DAYS)//过期时间，
            .recordStats()
            .build();

    public Cache<String, List<MappingField>> getBiCache() {
        return biCache;
    }


    public Cache<String, Map<String, String>> getMappingCache() {
        return mappingCache;
    }


    /**
     * 将template的结构信息初始化到cache中
     * @param templateName
     */
    public boolean init(String templateName,ILogIndexDao logIndexDao){
        //写入cache是否成功，用来判断是否有数据写入
        boolean result = false;
        try{
            //获取template的数据
            List<IndexTemplateMetaData> list = logIndexDao.getTemplateData(templateName);
            //由于API仅提供类似正则匹配的模糊查询，返回的都是list，但是对于本功能而言，需要返回唯一一条记录
            for(IndexTemplateMetaData itmd:list){
                //获取mapping信息
                MappingMetaData mmd = itmd.mappings();
                String name = itmd.name();
                //获取mapping信息
                Map<String, Object> sourceMap = mmd.getSourceAsMap();
                //获取所有字段信息
                List<MappingField> fieldList = MappingUtil.getAllListMetadataBySourceMap(sourceMap);
                //定义不同类型的list
                List<MappingField> forNumber = new ArrayList<>();
                List<MappingField> forNumberOrDate = new ArrayList<>();
                List<MappingField> forTermsList = new ArrayList<>();
                List<MappingField> forDateList = new ArrayList<>();
                List<MappingField> forIpList = new ArrayList<>();
                //用来进行filter字段的显示，除geo_point类型外的其他所有类型都包括
                List<MappingField> forAllExceptGeoPointList = new ArrayList<>();
                //
                Map<String,String> mappingMap = new HashMap<>();
                //遍历list，放到cache中
                for(MappingField mf :fieldList){
                    mappingMap.put(mf.getFieldName(),mf.getFieldType());
                    switch(mf.getFieldType()){
                        //number类型
                        case "long":case "integer":case "short":case "byte":case "double":case "float":case "half_float":case "scaled_float":
                            forNumber.add(mf);
                            forNumberOrDate.add(mf);
                            forAllExceptGeoPointList.add(mf);
                            break;
                        case "date":
                            forDateList.add(mf);
                            forNumberOrDate.add(mf);
                            forAllExceptGeoPointList.add(mf);
                            break;
                        case "keyword":
                            forTermsList.add(mf);
                            forAllExceptGeoPointList.add(mf);
                            break;
                        case "ip":
                            forIpList.add(mf);
                            forTermsList.add(mf);
                            forAllExceptGeoPointList.add(mf);
                            break;
                        case "geo_point":
                            //forIpList.add(mf);
                            break;
                        case "text":
                            forAllExceptGeoPointList.add(mf);
                            break;
                        case "boolean":
                            forAllExceptGeoPointList.add(mf);
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
                this.biCache.put(name+"Number",forNumber);
                this.biCache.put(name+"NumberOrDate",forNumberOrDate);
                this.biCache.put(name+"Terms",forTermsList);
                this.biCache.put(name+"Date",forDateList);
                this.biCache.put(name+"Ip",forIpList);
                this.biCache.put(name+"AllExceptGeo",forAllExceptGeoPointList);
                this.mappingCache.put(name,mappingMap);
                result = true;
            }
        }catch(Exception e){
            logger.error("template信息初始化到cache失败！"+e.getMessage());
        }
        return result;
    }

}

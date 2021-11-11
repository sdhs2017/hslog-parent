package com.hs.elsearch.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hs.elsearch.dao.logDao.ILogIndexDao;
import com.hs.elsearch.util.ElasticConstant;
import com.hs.elsearch.util.MappingField;
import com.hs.elsearch.util.MappingUtil;
import com.mysql.jdbc.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.elasticsearch.client.indices.IndexTemplateMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@Slf4j
public enum SearchCache {
    INSTANCE;
    //服务于可视化模块
    private Cache<String, List<MappingField>> biCache = Caffeine.newBuilder()
            .maximumSize(100)//最大条数，
            .expireAfterWrite(6, TimeUnit.HOURS)//过期时间，6小时
            .recordStats()
            .build();
    //mapping字段信息，服务于对字段类型的查询需求
    private Cache<String, Map<String,String>> mappingCache = Caffeine.newBuilder()
            .maximumSize(100)//最大条数，
            .expireAfterWrite(6, TimeUnit.HOURS)//过期时间，6小时
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
    /**
     *
     * @param templateName
     * @param source_type
     * @param logIndexDao
     * @return
     */
    public boolean init(String templateName,String source_type,ILogIndexDao logIndexDao){
        //写入cache是否成功，用来判断是否有数据写入
        boolean result = false;
        try{
            if(StringUtils.isNullOrEmpty(templateName)){
                //alert返回字段
                List<MappingField> alertField = new ArrayList<>();
                MappingField name = new MappingField();
                name.setFieldName("name");
                name.setFieldType("keyword");
                MappingField value = new MappingField();
                value.setFieldName("value");
                value.setFieldType("double");
                alertField.add(name);
                alertField.add(value);
                this.biCache.put("alertAgg",alertField);
            }else{
                //最终形成的字段信息,
                List<MappingField> fieldList ;
                //判断数据源类型
                if(ElasticConstant.BI_SOURCE_TYPE_TEMPLATE.equals(source_type)){
                    //通过template获取
                    List<IndexTemplateMetaData> list = logIndexDao.getTemplateData(templateName);
                    IndexTemplateMetaData templateMetaData;
                    if(list.size()==1){
                        //由于API仅提供类似正则匹配的模糊查询，返回的都是list，但是对于本功能而言，需要返回唯一一条记录
                        // template 默认只取第一个，如果查出多个则为异常情况
                        templateMetaData = list.get(0);
                        //获取mapping信息
                        Map<String, Object> sourceMap = templateMetaData.mappings().getSourceAsMap();
                        //获取所有字段信息
                        fieldList = MappingUtil.getAllListMetadataBySourceMap(sourceMap);
                    }else{
                        //不写入数据，仅进行初始化
                        fieldList = new ArrayList<>();
                    }
                }else{
                    //定义Map<String,MappingField> key为字段名，value为字段详情
                    Map<String,MappingField> result_tmp = new HashMap<>();
                    //通过ES API获取 自定义index的mapping信息 key为真实index名称，value为mapping对象
                    Map<String, MappingMetaData> mappingList = logIndexDao.getIndexMappingData(templateName);
                    //遍历mappingList
                    for (Map.Entry<String, MappingMetaData> mappingListEntry : mappingList.entrySet()) {
                        //获取mapping信息并转为map
                        Map<String,Object> mappingMetaData = mappingListEntry.getValue().getSourceAsMap();
                        result_tmp.putAll(MappingUtil.getAllMapMetadataBySourceMap(mappingMetaData));
                    }
                    //排序 并获取所有value 行程前端所需的list
                    fieldList = new ArrayList(MappingUtil.sortByKey(result_tmp).values());
                }
                //定义不同类型的list
                List<MappingField> forNumber = new ArrayList<>();
                List<MappingField> forNumberOrDate = new ArrayList<>();
                List<MappingField> forTermsList = new ArrayList<>();
                List<MappingField> forDateList = new ArrayList<>();
                List<MappingField> forIpList = new ArrayList<>();
                List<MappingField> forSourceList = new ArrayList<>();
                //用来进行filter字段的显示，除geo_point类型外的其他所有类型都包括
                List<MappingField> forAllExceptGeoPointList = new ArrayList<>();
                //所有字段，用来进行动态表格的字段显示
                List<MappingField> forAllList = new ArrayList<>();
                Map<String,String> mappingMap = new HashMap<>();
                //遍历list，放到cache中
                for(MappingField mf :fieldList){
                    mappingMap.put(mf.getFieldName(),mf.getFieldType());
                    forAllList.add(mf);
                    //非fields字段，都可以作为表格中的列进行显示
                    if(!mf.getIsFields()){
                        forSourceList.add(mf);
                    }
                    switch(mf.getFieldType()){
                        //number类型
                        case "long":case "integer":case "short":case "byte":case "double":case "float":case "half_float":case "scaled_float":
                            forTermsList.add(mf);
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
                            forTermsList.add(mf);
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
                this.biCache.put(templateName+ElasticConstant.ES_FIELDS_TYPE_NUMBER,forNumber);
                this.biCache.put(templateName+ElasticConstant.ES_FIELDS_TYPE_NUMBER_OR_DATE,forNumberOrDate);
                this.biCache.put(templateName+ElasticConstant.ES_FIELDS_TYPE_TERMS,forTermsList);
                this.biCache.put(templateName+ElasticConstant.ES_FIELDS_TYPE_DATE,forDateList);
                this.biCache.put(templateName+ElasticConstant.ES_FIELDS_TYPE_IP,forIpList);
                this.biCache.put(templateName+ElasticConstant.ES_FIELDS_TYPE_ALL_EXCEPT_GEO,forAllExceptGeoPointList);
                this.biCache.put(templateName+ElasticConstant.ES_FIELDS_TYPE_ALL,forAllList);
                this.biCache.put(templateName+ElasticConstant.ES_FIELDS_TYPE_SOURCE,forSourceList);
                this.mappingCache.put(templateName,mappingMap);
                result = true;


//                //获取template的数据
//                List<IndexTemplateMetaData> list = logIndexDao.getTemplateData(templateName);
//                //由于API仅提供类似正则匹配的模糊查询，返回的都是list，但是对于本功能而言，需要返回唯一一条记录
//                for(IndexTemplateMetaData itmd:list){
//                    //获取mapping信息
//                    MappingMetaData mmd = itmd.mappings();
//                    String name = itmd.name();
//                    //获取mapping信息
//                    Map<String, Object> sourceMap = mmd.getSourceAsMap();
//                    //获取所有字段信息
//                    List<MappingField> fieldList = MappingUtil.getAllListMetadataBySourceMap(sourceMap);
//                    //定义不同类型的list
//                    List<MappingField> forNumber = new ArrayList<>();
//                    List<MappingField> forNumberOrDate = new ArrayList<>();
//                    List<MappingField> forTermsList = new ArrayList<>();
//                    List<MappingField> forDateList = new ArrayList<>();
//                    List<MappingField> forIpList = new ArrayList<>();
//                    //用来进行filter字段的显示，除geo_point类型外的其他所有类型都包括
//                    List<MappingField> forAllExceptGeoPointList = new ArrayList<>();
//                    //所有字段，用来进行动态表格的字段显示
//                    List<MappingField> forAllList = new ArrayList<>();
//                    Map<String,String> mappingMap = new HashMap<>();
//                    //遍历list，放到cache中
//                    for(MappingField mf :fieldList){
//                        mappingMap.put(mf.getFieldName(),mf.getFieldType());
//                        forAllList.add(mf);
//                        switch(mf.getFieldType()){
//                            //number类型
//                            case "long":case "integer":case "short":case "byte":case "double":case "float":case "half_float":case "scaled_float":
//                                forTermsList.add(mf);
//                                forNumber.add(mf);
//                                forNumberOrDate.add(mf);
//                                forAllExceptGeoPointList.add(mf);
//                                break;
//                            case "date":
//                                forDateList.add(mf);
//                                forNumberOrDate.add(mf);
//                                forAllExceptGeoPointList.add(mf);
//                                break;
//                            case "keyword":
//                                forTermsList.add(mf);
//                                forAllExceptGeoPointList.add(mf);
//                                break;
//                            case "ip":
//                                forIpList.add(mf);
//                                forTermsList.add(mf);
//                                forAllExceptGeoPointList.add(mf);
//                                break;
//                            case "geo_point":
//                                //forIpList.add(mf);
//                                break;
//                            case "text":
//                                forAllExceptGeoPointList.add(mf);
//                                break;
//                            case "boolean":
//                                forTermsList.add(mf);
//                                forAllExceptGeoPointList.add(mf);
//                                break;
//                            default:
//                                //其他类型需要对fielddata进行判定，为true时,作为keyword存储
//                                if(mf.getFieldData()){
//                                    forTermsList.add(mf);
//                                }
//                                break;
//                        }
//                    }
//                    //放入cache中
//                    this.biCache.put(name+"Number",forNumber);
//                    this.biCache.put(name+"NumberOrDate",forNumberOrDate);
//                    this.biCache.put(name+"Terms",forTermsList);
//                    this.biCache.put(name+"Date",forDateList);
//                    this.biCache.put(name+"Ip",forIpList);
//                    this.biCache.put(name+"AllExceptGeo",forAllExceptGeoPointList);
//                    this.biCache.put(name+"All",forAllList);
//                    this.mappingCache.put(name,mappingMap);
//                    result = true;
                }



        }catch(Exception e){
            log.error("template信息初始化到cache失败！"+e.getMessage());
        }
        return result;
    }

}

package com.jz.bigdata.common.businessIntelligence.service.impl;

import com.hs.elsearch.dao.biDao.IBIDao;
import com.hs.elsearch.dao.flowDao.IFlowSearchDao;
import com.hs.elsearch.dao.logDao.ILogIndexDao;
import com.jz.bigdata.common.businessIntelligence.cache.BICache;
import com.jz.bigdata.common.businessIntelligence.controller.BIController;
import com.jz.bigdata.common.businessIntelligence.entity.MappingField;
import com.jz.bigdata.common.businessIntelligence.service.IBIService;
import com.jz.bigdata.common.metadata.entity.Metadata;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.elasticsearch.client.indices.IndexTemplateMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
@Service(value="BIService")
public class BIServiceImpl implements IBIService {
    /**
     * number类型包含long、integer、short、byte、double、float、half_float、scaled_float
     */
    //用,将具体类型隔开
    private final String numberTypes = ",long,integer,short,byte,double,float,half_float,scaled_float,";//定义number类型
    private final String dateTypes = ",date,";
    private final String keywordTypes = ",keyword,";

    @Autowired
    protected ILogIndexDao logIndexDao;
    @Autowired
    protected IBIDao ibiDao;
    @Override
    public List<MappingField> getFieldByXAxisAggregation(String templateName, String indexName, String agg) throws Exception {
        return getMappingFieldByAggType(templateName,indexName,agg);
    }

    @Override
    public List<MappingField> getFieldByYAxisAggregation(String templateName, String indexName, String agg) throws Exception {
        return getMappingFieldByAggType(templateName,indexName,agg);
    }

    @Override
    public String groupByThenSum(String index, String groupByField, String groupByFieldType, String sumField, int size, String sort, String starttime, String endtime, Map<String, String> map) throws Exception {
        List<Map<String, Object>> list = ibiDao.getListBySumOfAggregation(starttime,endtime,groupByField,groupByFieldType,sumField,size,sort,map,index);
        //处理数据
        return this.dataFormat(list);
    }

    @Override
    public String groupByThenCount(String index, String groupByField, String groupByFieldType, String sumField, int size, String sort, String starttime, String endtime, Map<String, String> map) throws Exception {
        List<Map<String, Object>> list = ibiDao.getListByCountOfAggregation(starttime,endtime,groupByField,groupByFieldType,size,sort,map,index);
        //处理数据
        return this.dataFormat(list);
    }

    @Override
    public String groupByThenAvg(String index, String groupByField, String groupByFieldType, String sumField, int size, String sort, String starttime, String endtime, Map<String, String> map) throws Exception {
        List<Map<String, Object>> list = ibiDao.getListByAvgOfAggregation(starttime,endtime,groupByField,groupByFieldType,sumField,size,sort,map,index);
        //处理数据
        return this.dataFormat(list);
    }

    @Override
    public String groupByThenMax(String index, String groupByField, String groupByFieldType, String sumField, int size, String sort, String starttime, String endtime, Map<String, String> map) throws Exception {
        List<Map<String, Object>> list = ibiDao.getListByMaxOfAggregation(starttime,endtime,groupByField,groupByFieldType,sumField,size,sort,map,index);
        //处理数据
        return this.dataFormat(list);
    }

    @Override
    public String groupByThenMin(String index, String groupByField, String groupByFieldType, String sumField, int size, String sort, String starttime, String endtime, Map<String, String> map) throws Exception {
        List<Map<String, Object>> list = ibiDao.getListByMinOfAggregation(starttime,endtime,groupByField,groupByFieldType,sumField,size,sort,map,index);
        //处理数据
        return this.dataFormat(list);
    }

    /**
     * 将ES的聚合结果进行处理返回成前端需要的格式。
     * [{"data":[7.51308144E8,2.49168344E8],"name":["ZHANGYIYANG","jyr-PC"]}]
     * 由于返回接口都是统一的List<Map<String, Object>>,但是正常数据中list一般就只有一条记录
     * 因此在组装数据时，对list和map都遍历，但是逻辑上会将多个list记录视作一类
     * 保证返回结果在前端是可识别的。
     * @param list
     * @return
     */
    private String dataFormat(List<Map<String, Object>> list){
        Map<String, Object> tmplist = new HashMap<String, Object>();
        List<String> name = new ArrayList<>();
        List<Object> data = new ArrayList<>();
        for(Map<String, Object> tMap:list){
            for(Map.Entry<String, Object> key : tMap.entrySet()) {
                name.add(key.getKey());
                data.add(key.getValue());
            }
        }
        tmplist.put("name",name);
        tmplist.put("data",data);
        return JSONArray.fromObject(tmplist).toString();
    }

    /**
     * 根据聚合方式的不同，筛选出符合要求的字段信息
     * @param indexName 索引名称
     * @param aggType 聚合类型
     * @return
     */
    private List<MappingField> getMappingFieldByAggType(String templateName, String indexName,String aggType) throws Exception {
        //通过index获取template信息

        //获取所有字段信息
        //List<MappingField> list = getMappingFieldByIndex(indexName);
        List<MappingField> result = new ArrayList<>();
        switch(aggType){

            case "Average":case "Sum"://获取所有number类型字段信息
                //result = formatFields(list,numberTypes,false);
                result = getFieldByIndexAndAgg(indexName,templateName,"SumAvg");
                break;
            case "Max":case "Min"://获取所有number和date类型字段信息
                //result = formatFields(list,numberTypes+dateTypes,false);
                result = getFieldByIndexAndAgg(indexName,templateName,"MaxMin");
                break;
            case "Terms"://type为keyword，或者fielddata=true
                //result = formatFields(list,keywordTypes,true);
                result = getFieldByIndexAndAgg(indexName,templateName,"Terms");
                break;
            case "Date"://获取所有date类型字段信息
                //result = formatFields(list,dateTypes,false);
                result = getFieldByIndexAndAgg(indexName,templateName,"Date");
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 通过选择的索引或模板 以及聚合方式，返回对应的字段信息
     * @param indexName 索引名称
     * @param templateName 模板名称
     * @param agg 聚合参数
     * @return
     */
    private List<MappingField> getFieldByIndexAndAgg(String indexName,String templateName,String agg){
        List<MappingField> list = BICache.INSTANCE.getBiCache().getIfPresent(templateName+agg);

        //如果没有获取到mapping信息，尝试更新cache
        if(null==list||0==list.size()){
            BICache.INSTANCE.putBiCache(templateName,logIndexDao);
            //更新完毕后根据template+agg获取fields
            list = BICache.INSTANCE.getBiCache().getIfPresent(templateName+agg);
        }
        return list;
    }
    /**
     * 筛选字段
     * @param list 源字段（总的）
     * @param fieldTypes 筛选的字段类型
     * @param fieldData fielddata=true的情况
     * @return
     */
    private List<MappingField> formatFields(List<MappingField> list,String fieldTypes,boolean fieldData){
        //筛选后的字段
        List<MappingField> newList = new ArrayList<>();
        for(MappingField mappingField:list){
            //字段类型加上,, 保证准确匹配
            //先根据类型筛选，再根据fielddata
            if(fieldTypes.indexOf(","+mappingField.getFieldType()+",")>=0){
                newList.add(mappingField);
            }else{
                //fieldData=true的情况
                if(fieldData&&mappingField.getFieldData()){
                    newList.add(mappingField);
                }
            }
        }
        return newList;
    }
    /**
     * 通过index名称获取所有field
     * @param indexName
     * @return
     */
    private List<MappingField> getMappingFieldByIndex(String indexName) throws Exception {
        List<MappingField> metadataList = new ArrayList<MappingField>();
        //获取template的数据
        Map<String, MappingMetaData> mapMapping = logIndexDao.getIndexMappingData(indexName);
        //获取MappingMetaData
        MappingMetaData mapping = mapMapping.get(indexName);
        if(mapping!=null){
            //获取mapping信息
            Map<String,Object> sourceMap = mapping.getSourceAsMap();
            return getAllListMetadataBySourceMap(sourceMap);
        }else{
            return null;
        }
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

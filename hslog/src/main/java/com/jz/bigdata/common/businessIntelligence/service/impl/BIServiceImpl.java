package com.jz.bigdata.common.businessIntelligence.service.impl;

import com.google.gson.*;
import com.hs.elsearch.dao.biDao.IBIDao;
import com.hs.elsearch.entity.VisualParam;
import com.hs.elsearch.dao.globalDao.IGlobalDao;
import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.hs.elsearch.dao.logDao.ILogIndexDao;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.businessIntelligence.cache.BICache;
import com.jz.bigdata.common.businessIntelligence.entity.Dashboard;
import com.jz.bigdata.common.businessIntelligence.entity.HSData;
import com.jz.bigdata.common.businessIntelligence.entity.MappingField;
import com.jz.bigdata.common.businessIntelligence.entity.Visualization;
import com.jz.bigdata.common.businessIntelligence.service.IBIService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.cluster.settings.ClusterGetSettingsResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.util.*;

@Service(value="BIService")
public class BIServiceImpl implements IBIService {
    //json处理全局对象
    private final Gson gson = new GsonBuilder().create();

    @Autowired
    protected ILogIndexDao logIndexDao;
    @Autowired
    protected ILogCrudDao logCurdDao;
    @Autowired
    protected IBIDao ibiDao;
    @Autowired
    protected IGlobalDao globalDao;
    @Override
    public List<MappingField> getFieldByXAxisAggregation(String templateName, String indexName, String agg) throws Exception {
        return getMappingFieldByAggType(templateName,indexName,agg);
    }

    @Override
    public List<MappingField> getFieldByYAxisAggregation(String templateName, String indexName, String agg) throws Exception {
        return getMappingFieldByAggType(templateName,indexName,agg);
    }

    @Override
    public String groupByThenSum(VisualParam params) throws Exception {
        List<Map<String, Object>> list = ibiDao.getListBySumOfAggregation(params);
        //处理数据
        return this.dataFormat(list,params);
    }

    @Override
    public String groupByThenCount(VisualParam params) throws Exception {
        List<Map<String, Object>> list = ibiDao.getListByCountOfAggregation(params);
        //处理数据
        return this.dataFormat(list,params);
    }

    @Override
    public String groupByThenAvg(VisualParam params) throws Exception {
        List<Map<String, Object>> list = ibiDao.getListByAvgOfAggregation(params);
        //处理数据
        return this.dataFormat(list,params);
    }

    @Override
    public String groupByThenMax(VisualParam params) throws Exception {
        List<Map<String, Object>> list = ibiDao.getListByMaxOfAggregation(params);
        //处理数据
        return this.dataFormat(list,params);
    }

    @Override
    public String groupByThenMin(VisualParam params) throws Exception {
        List<Map<String, Object>> list = ibiDao.getListByMinOfAggregation(params);
        //处理数据
        return this.dataFormat(list,params);
    }

    @Override
    public DocWriteResponse.Result saveVisualization(Visualization visual, String indexName) throws Exception {
        //构建数据格式
        HSData hsdata = new HSData();
        hsdata.setVisualization(visual);
        //新增or更新
        DocWriteResponse.Result result = logCurdDao.upsert(indexName,visual.getId(),gson.toJson(hsdata));
        return result;

    }

    @Override
    public DocWriteResponse.Result saveDashboard(Dashboard dashboard, String indexName) throws Exception {
        //构建数据格式
        HSData hsdata = new HSData();
        hsdata.setDashboard(dashboard);
        //新增or更新
        DocWriteResponse.Result result = logCurdDao.upsert(indexName,dashboard.getId(),gson.toJson(hsdata));
        return result;
    }

    @Override
    public String getVisualizations(String indexName, HttpSession session) throws Exception {
        List<Visualization> result = new ArrayList<>();
        //获取index的visualization列存在数据的信息
        List<Map<String,Object>> list = ibiDao.getListExistsField(indexName,"visualization");
        //遍历结果集
        for(Map<String,Object> map:list){
            Visualization visual = new Visualization();
            visual.setId(map.get("id").toString());
            //visualization存储图表的数据
            Map<String,Object> visualizationInfo = (HashMap<String,Object>)map.get("visualization");
            visual.setTitle(visualizationInfo.get("title").toString());//标题
            visual.setDescription(visualizationInfo.get("description").toString());//描述
            visual.setType(visualizationInfo.get("type").toString());//图表类型
            //master用户在BI模块可以进行编辑保存
            if("master".equals(session.getAttribute(Constant.SESSION_USERNAME).toString())){
                visual.setEditable(true);//是否可编辑
                visual.setDeletable(true);//是否可删除
            }else{
                visual.setEditable((Boolean)visualizationInfo.get("editable"));//是否可编辑
                visual.setDeletable((Boolean)visualizationInfo.get("deletable"));//是否可删除
            }

            //visual.setIndexName(visualizationInfo.get("indexName").toString());//数据查询的数据源（index名称）
            result.add(visual);
        }
        return JSONArray.fromObject(result).toString();
    }

    @Override
    public String getDashboards(String indexName,HttpSession session) throws Exception {
        List<Dashboard> result = new ArrayList<>();
        //获取index的visualization列存在数据的信息
        List<Map<String,Object>> list = ibiDao.getListExistsField(indexName,"dashboard");
        //遍历结果集
        for(Map<String,Object> map:list){
            Dashboard dashboard = new Dashboard();
            dashboard.setId(map.get("id").toString());
            //visualization存储图表的数据
            Map<String,Object> dashboardInfo = (HashMap<String,Object>)map.get("dashboard");
            dashboard.setTitle(dashboardInfo.get("title").toString());//标题
            dashboard.setDescription(dashboardInfo.get("description").toString());//描述
            //master用户在BI模块可以进行编辑保存
            if("master".equals(session.getAttribute(Constant.SESSION_USERNAME).toString())){
                dashboard.setEditable(true);//是否可编辑
                dashboard.setDeletable(true);//是否可删除
            }else{
                dashboard.setEditable((Boolean)dashboardInfo.get("editable"));//是否可编辑
                dashboard.setDeletable((Boolean)dashboardInfo.get("deletable"));//是否可删除
            }

            result.add(dashboard);
        }
        return JSONArray.fromObject(result).toString();
    }

    @Override
    public String getVisualizationById(String id,String indexName) throws Exception {
        Visualization visual =new Visualization();
        //根据id获取图表详情
        Map<String, Object> map = logCurdDao.searchById(indexName,"",id);
        if(null!=map){
            //将数据赋值给bean，方便回显
            visual.setId(map.get("id").toString());
            Map<String,Object> visualization = (HashMap<String,Object>)map.get("visualization");
            visual.setType(visualization.get("type").toString());
            visual.setDescription(visualization.get("description").toString());
            visual.setTitle(visualization.get("title").toString());
            visual.setIndex_name(visualization.get("pre_index_name").toString()+visualization.get("suffix_index_name").toString());
            visual.setTemplate_name(visualization.get("template_name").toString());
            visual.setPre_index_name(visualization.get("pre_index_name").toString());
            visual.setSuffix_index_name(visualization.get("suffix_index_name").toString());
            visual.setOption(visualization.get("option").toString());
            //处理查询参数
            String params = visualization.get("params").toString();
            visual.setParams(params);
            //查询数据
            //1.解析params变成map  params中的数据格式为{a:1,b:2,c:3} json格式
            //JsonElement jsonElement = new JsonParser().parse(params);

            //JsonObject jsonObject= jsonElement.getAsJsonObject();
            /*
            HashMap<String,String[]> result = new ObjectMapper().readValue(params, HashMap.class);
            VisualParam vp = new VisualParam();
            vp = vp.mapToBean(result);
            //返回结果
            String searchResult;
            //根据聚合方式调用不同的方法
            switch (vp.getY_agg()){
                case "Average":
                    searchResult = groupByThenAvg(vp);
                    break;
                case "Sum":
                    searchResult = groupByThenSum(vp);
                    break;
                case "Max":
                    searchResult = groupByThenMax(vp);
                    break;
                case "Min":
                    searchResult = groupByThenMin(vp);
                    break;
                case "Count":
                    searchResult = groupByThenCount(vp);
                    break;
                default:
                    searchResult = "";
                    break;
            }
            visual.setData(searchResult);
            */

        }
        return JSONObject.fromObject(visual).toString();
    }

    @Override
    public String getDashboardById(String id, String indexName) throws Exception {
        Dashboard dashboard =new Dashboard();
        //根据id获取图表详情
        Map<String, Object> map = logCurdDao.searchById(indexName,"",id);
        if(null!=map){
            //将数据赋值给bean，方便回显
            dashboard.setId(map.get("id").toString());
            Map<String,Object> visualization = (HashMap<String,Object>)map.get("dashboard");
            dashboard.setDescription(visualization.get("description").toString());
            dashboard.setTitle(visualization.get("title").toString());
            dashboard.setOption(visualization.get("option").toString());

        }
        return JSONObject.fromObject(dashboard).toString();
    }

    @Override
    public String deleteVisualizationById(String id, String indexName) throws Exception {
        return logCurdDao.deleteById(indexName,"",id);
    }

    @Override
    public String deleteDashboardById(String id, String indexName) throws Exception {
        return logCurdDao.deleteById(indexName,"",id);
    }

    @Override
    public boolean isVisualizationExists(String title, String indexName) throws Exception {
        Map<String,String> map = new HashMap<>();
        //按照标题名称查询
        map.put("visualization.title.raw",title);
        long count = ibiDao.getCount(map,indexName);
        if(count>0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean isDashboardExists(String title, String indexName) throws Exception {
        Map<String,String> map = new HashMap<>();
        //按照标题名称查询
        map.put("dashboard.title.raw",title);
        long count = ibiDao.getCount(map,indexName);
        if(count>0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public int getClusterSearchMaxBuckets() throws Exception {
        ClusterGetSettingsResponse setting = globalDao.getClusterSetting();
        String result = setting.getPersistentSettings().get("search.max_buckets");
        return Integer.parseInt(result);
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
    private String dataFormat(List<Map<String, Object>> list,VisualParam param){
        String x_type = "";
        String y_type = "";
        //X轴为时间，处理时间
        if("Date".equals(param.getX_agg())){
            x_type="date";
        }
        //Y轴为bytes
        if(param.getY_field().indexOf("bytes")>=0||param.getY_field().indexOf("system.memory.free")>=0){
            y_type="bytes";
        }
        //Y轴为百分比
        if(param.getY_field().indexOf("pct")>=0){
            y_type="pct";
        }
        Map<String, Object> tmplist = new HashMap<String, Object>();
        List<String> name = new ArrayList<>();
        List<Object> data = new ArrayList<>();
        for(Map<String, Object> tMap:list){
            for(Map.Entry<String, Object> key : tMap.entrySet()) {
                if("date".equals(x_type)){
                    name.add(this.valueFormatter(key.getKey(),x_type));
                }else{
                    name.add(key.getKey());
                }

                //y轴为pct，*100
                if("pct".equals(y_type)){
                    data.add(this.valueFormatter(key.getValue(),y_type));
                }else if ("bytes".equals(y_type)){
                    data.add(this.valueFormatter(key.getValue(),y_type));
                }else{
                    data.add(key.getValue());
                }

            }
        }
        tmplist.put("name",name);
        tmplist.put("data",data);
        return JSONArray.fromObject(tmplist).toString();
    }
    private String valueFormatter(Object value,String type){
        String result = null;
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
        try{
            switch (type){
                case "pct":
                    Float folatValue = Float.parseFloat(value.toString());
                    folatValue = folatValue*100;
                    result = decimalFormat.format(folatValue);
                    break;
                case "date":
                    DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    DateTime dateTime = DateTime.parse(value.toString(), dtf);
                    result = dateTime.toString("yyyy-MM-dd HH:mm:ss");
                    break;
                case "bytes":
                    Double byteValue = (Double)value;
                    byteValue = byteValue/1024/1024/1024;//byte -> GB
                    result = decimalFormat.format(byteValue);
                    break;
                default:
                    result = value.toString();
                    break;
            }
        }catch(Exception e){
            result = value.toString();
        }

        return result;
    }
    /**
     * 根据聚合方式的不同，筛选出符合要求的字段信息
     * @param indexName 索引名称
     * @param aggType 聚合类型
     * @return
     */
    private List<MappingField> getMappingFieldByAggType(String templateName, String indexName,String aggType) throws Exception {
        //通过模糊的indexName 获取index列表

        //获取indices
        String[] indices = logIndexDao.getIndices(indexName+"*");
        //排序
        Arrays.sort(indices);
        //获取最新的index信息
        indexName = indices[indices.length-1];
        //获取所有字段信息
        //List<MappingField> list = getMappingFieldByIndex(indexName);
        List<MappingField> result = new ArrayList<>();
        switch(aggType){

            case "Average":case "Sum"://获取所有number类型字段信息
                //result = formatFields(list,numberTypes,false);
                result = getFieldByIndexAndAgg(indexName,templateName,"Number");
                break;
            case "Max":case "Min"://获取所有number和date类型字段信息
                //result = formatFields(list,numberTypes+dateTypes,false);
                result = getFieldByIndexAndAgg(indexName,templateName,"NumberOrDate");
                break;
            case "Terms"://type为keyword，或者fielddata=true
                //result = formatFields(list,keywordTypes,true);
                result = getFieldByIndexAndAgg(indexName,templateName,"Terms");
                break;
            case "Date Histogram"://获取所有date类型字段信息
                //result = formatFields(list,dateTypes,false);
                result = getFieldByIndexAndAgg(indexName,templateName,"Date");
                break;
            case "Range"://获取所有number类型字段信息
                //result = formatFields(list,dateTypes,false);
                result = getFieldByIndexAndAgg(indexName,templateName,"Number");
                break;
            case "Date Range"://获取所有date类型字段信息
                //result = formatFields(list,dateTypes,false);
                result = getFieldByIndexAndAgg(indexName,templateName,"Date");
                break;
            case "IPv4 Range"://获取所有ip类型字段信息
                //result = formatFields(list,dateTypes,false);
                result = getFieldByIndexAndAgg(indexName,templateName,"Ip");
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
    @Override
    public Map<String, Object> getMultiAggregationDataSet(VisualParam params) throws Exception {
        return ibiDao.getMultiAggregation4dateset(params);
    }
}

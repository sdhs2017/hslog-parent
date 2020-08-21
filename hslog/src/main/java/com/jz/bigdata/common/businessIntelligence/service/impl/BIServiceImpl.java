package com.jz.bigdata.common.businessIntelligence.service.impl;

import com.google.gson.*;
import com.hs.elsearch.dao.biDao.IBIDao;
import com.hs.elsearch.dao.searchDao.ISearchDao;
import com.hs.elsearch.entity.Bucket;
import com.hs.elsearch.entity.Metric;
import com.hs.elsearch.entity.SearchConditions;
import com.hs.elsearch.entity.VisualParam;
import com.hs.elsearch.dao.globalDao.IGlobalDao;
import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.hs.elsearch.dao.logDao.ILogIndexDao;
import com.hs.elsearch.service.ISearchService;
import com.hs.elsearch.util.ElasticConstant;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.businessIntelligence.cache.BICache;
import com.jz.bigdata.common.businessIntelligence.entity.Dashboard;
import com.jz.bigdata.common.businessIntelligence.entity.HSData;
import com.jz.bigdata.common.businessIntelligence.entity.MappingField;
import com.jz.bigdata.common.businessIntelligence.entity.Visualization;
import com.jz.bigdata.common.businessIntelligence.service.IBIService;
import com.jz.bigdata.util.CSVUtil;
import joptsimple.internal.Strings;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.cluster.settings.ClusterGetSettingsResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregation;
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
    @Autowired
    protected ISearchDao searchDao;
    @Autowired
    protected ISearchService searchService;
    @Override
    public List<MappingField> getFieldByXAxisAggregation(String templateName, String indexName, String agg) throws Exception {
        return getMappingFieldByAggType(templateName,indexName,agg);
    }

    @Override
    public List<MappingField> getFieldByYAxisAggregation(String templateName, String indexName, String agg) throws Exception {
        return getMappingFieldByAggType(templateName,indexName,agg);
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

    @Override
    public Map<String, Object> getMultiAggregationDataSet(VisualParam params) throws Exception {
        return ibiDao.getMultiAggregation4dateset(params);
    }

    @Override
    public LinkedList<ArrayList<Map<String,Object>>> getMultiAggregationData_pie(SearchConditions conditions) throws Exception {
        /**
         * 数据返回示例：
         * [
         *   [{value: 335, name: '192.168.2.1'},{value: 125, name: '192.168.2.2'}...],---内圈
         *   [{value: 111, name: '8080'},{value: 55, name: '8081'}....],--中圈
         *   [{value: 50, name: 'TCP'}......]---外圈
         * ]
         */
        LinkedList<ArrayList<Map<String,Object>>> result = new LinkedList<>();
        //获取聚合结果
        Aggregations aggregations = searchDao.getAggregation(conditions);

        //根据饼图要求处理数据
        if(conditions.getBucketList().size()>0){
            //获取第一个聚合字段信息，用以组装别名，获取对应数据
            Bucket bucket = conditions.getBucketList().get(0);

            //第一层无法使用递归，第一层和2-n层需要：声明一些对象以及相关处理
            MultiBucketsAggregation terms  = aggregations.get(bucket.getAggType()+"-"+bucket.getField());
            //如果无法获取结果集，直接返回空数据
            if(null==terms||(null!=terms&&null==terms.getBuckets())){
                //不需要进行其他操作
            }else{
                int i=0; //定义遍历到第几层，用于中断递归
                //第一层也会作为X轴的图标
                for(MultiBucketsAggregation.Bucket bucketItem:terms.getBuckets()) {
                    handleAggData4Pie(conditions,i,bucketItem,result,"");
                }
            }

        }
        return result;
    }

    @Override
    public Map<String, Object> getMultiAggregationData_line(SearchConditions conditions) throws Exception {
        //折线图需要补零操作
        Map<String, Object> dataSet = searchService.getMultiAggDataSetWithZeroFill(conditions);
        //数据处理
        handleLastPoints4DateHistogram(conditions,dataSet);
        //TransforDataToFile(dataSet);
        //TransforDataToFile_Basic(dataSet);
        return dataSet;
    }

    @Override
    public Map<String, Object> getMultiAggregationData_bar(SearchConditions conditions) throws Exception {
        //柱状图不需要补零
        Map<String, Object> dataSet = searchService.getMultiAggDataSet(conditions);
        //数据处理
        handleLastPoints4DateHistogram(conditions,dataSet);
        return dataSet;
    }

    /**
     * 针对折线图和柱状图的数据处理，当聚合是以时间序列处理，对最后一个时间点的数据需要进行处理，保证图表显示的友好度
     * @param conditions
     * @param dataSet
     */
    private void handleLastPoints4DateHistogram(SearchConditions conditions,Map<String, Object> dataSet){
        List<LinkedHashMap<String,Object>> source = (List<LinkedHashMap<String, Object>>) dataSet.get(ElasticConstant.SOURCE);
        //折线图需要处理最后一个时间点的数据
        //如果X轴为时间(bucket第一个为X轴),且最后一个点的时间与截止时间相同（并且最后一个点的聚合值为0），去掉最后一个点
        if(conditions.getBucketList().size()>0&&"Date Histogram".equals(conditions.getBucketList().get(0).getAggType())&&source.get(source.size()-1).get(ElasticConstant.XAXIS).equals(conditions.getEndTime())){
            //获取最后一个时间点的数据集
            LinkedHashMap<String,Object> points = source.get(source.size()-1);
            //对于折线图，如果最后一个时间点的所有数据都为0，吧最后一个时间点的数据去掉
            boolean isAllZero = true;
            for(Map.Entry<String,Object> point:points.entrySet()){
                if(!ElasticConstant.XAXIS.equals(point.getKey())&&Double.parseDouble(point.getValue().toString())!=0.00){
                    isAllZero=false;
                    break;//存在不等于0的，中断执行
                }
            }
            //去掉最后一个点
            if(isAllZero){
                source.remove(source.size()-1);
            }
        }
    }
    /**
     * 递归方法，循环遍历聚合结果
     * @param conditions 请求参数
     * @param i 指处理第i+1次聚合数据
     * @param bucket 第i次聚合结果的bucket对相同
     * @return Double metric的值，饼图都是数值类型
     */
    private Double handleAggData4Pie(SearchConditions conditions, int i, MultiBucketsAggregation.Bucket bucket,LinkedList<ArrayList<Map<String,Object>>> result,String nextkey){
        //如果第i+1层聚合的结果没有放到result中，则先创建list。
        if(i>=result.size()){
            ArrayList<Map<String,Object>> aggResult = new ArrayList<>();
            result.add(aggResult);
        }else{
            //第i+1层的数据对象已经存在，在后续执行代码中进行数据填充
        }
        //每一层都需要进行数据的填充
        //获取要写入结果的对象
        ArrayList<Map<String,Object>> tempResult = result.get(i);
        //定义数据点，包含name和value属性，eg:{value: 335, name: '192.168.2.1'}
        Map<String,Object> dataPoint = new HashMap<>();
        //图例名称，eg:第一层IP，第二层port，第一层的图例为：-192.168.1.1，第二层的图例为：-192.168.1.1-8080
        nextkey = nextkey+"-"+bucket.getKeyAsString();
        //写入图例名称
        dataPoint.put("name",nextkey.indexOf("-")==0?nextkey.substring(1):nextkey);
        //饼图只有一个metric，metric的长度为0的情况已经在生成SearchConditions对象时进行了判定（必须>0）
        //大于1的情况已经在controller层进行了处理。
        Metric metric = conditions.getMetricList().get(0);
        //获取metric对应的值
        NumericMetricsAggregation.SingleValue value = bucket.getAggregations().get(!Strings.isNullOrEmpty(metric.getAliasName())?metric.getAliasName():(metric.getAggType()+"-"+metric.getField()));
        //Double pointValue = (Double.isInfinite(value.value())||Double.isNaN(value.value()))?0:value.value();

        Double pointValue = 0.00;

        //通过判断终止递归
        if(i<conditions.getBucketList().size()-1){
            //获取这次聚合结果对应的
            Bucket bucketEntity = conditions.getBucketList().get(i+1);
            //获取聚合结果
            MultiBucketsAggregation terms = bucket.getAggregations().get(bucketEntity.getAggType()+"-"+bucketEntity.getField());
            //下一次递归时  i需要+1，进入第i+1层聚合结果的处理中
            i++;
            //遍历第i层的结果集
            for(MultiBucketsAggregation.Bucket aggBucket:terms.getBuckets()){
                //将其下的每个聚合结果的值返回，由于是数值类型，通过累加得到汇总的值
                pointValue=pointValue+handleAggData4Pie(conditions,i,aggBucket,result,nextkey);
            }
        }else{
            //递归结束，根据当前点的value值进行赋值
            pointValue = (Double.isInfinite(value.value())||Double.isNaN(value.value()))?0:value.value();

        }
        dataPoint.put("value",pointValue);
        tempResult.add(dataPoint);
        return pointValue;
    }

    /**
     * 将查询出的数据导出为可供机器学习使用的csv文件
     * 源数据要求：指标类数据统计为累加的过程，需要通过相减获取其增加值
     * 生成的csv文件格式：第一列为时间，第2-n列为数据
     * @return
     */
    private String TransforDataToFile(Map<String, Object> dataSet){
        //定义表头
        List<String> head = new ArrayList<>();
        //数据
        List<Map<String, Object>> dataList = new ArrayList<>();
        //源数据中的数据字段
        List<LinkedHashMap<String,Object>> source = (List<LinkedHashMap<String, Object>>) dataSet.get(ElasticConstant.SOURCE);
        //源数据中的图例字段（即要转成的表头字段）
        LinkedHashSet<String> dimensions = (LinkedHashSet<String>) dataSet.get(ElasticConstant.DIMENSIONS);
        //dimensions为表头,遍历
        for(String column:dimensions){
            head.add(column);
        }
        //处理数据需要计算两个点之间的差值
        //定义两个点的数据对象
        LinkedHashMap<String, Object> before;
        LinkedHashMap<String, Object> after = new LinkedHashMap<>();
        //source为具体数据，每条数据为一组K/V
        for(LinkedHashMap<String, Object> row:source){
            //数据点的轮转
            before = after;
            after = row;
            if(before.size()>0&&after.size()>0){
                //计算两个点的差值,形成新的数据点
                dataList.add(calDifBetweenTwoPoints(before,after,dimensions));
            }

        }
        CSVUtil.createCSVFile(head,head,dataList,"e:/","train");
        return null;
    }
    /**
     * 将查询出的数据导出为可供机器学习使用的csv文件
     * 源数据要求：无，不对数据做任何处理
     * 生成的csv文件格式：第一列为时间，第2-n列为数据
     * @return
     */
    private String TransforDataToFile_Basic(Map<String, Object> dataSet){
        //定义表头
        List<String> head = new ArrayList<>();
        //数据
        List<Map<String, Object>> dataList = new ArrayList<>();
        //源数据中的数据字段
        List<LinkedHashMap<String,Object>> source = (List<LinkedHashMap<String, Object>>) dataSet.get(ElasticConstant.SOURCE);
        //源数据中的图例字段（即要转成的表头字段）
        LinkedHashSet<String> dimensions = (LinkedHashSet<String>) dataSet.get(ElasticConstant.DIMENSIONS);
        //dimensions为表头,遍历
        for(String column:dimensions){
            head.add(column);
        }
        dataList.addAll(source);
        //dataList.add(row);
        CSVUtil.createCSVFile(head,head,dataList,"e:/","train");
        return null;
    }
    /**
     * 计算两组数据的值的差
     * @param before
     * @param after
     * @param dimensions
     * @return
     */
    private Map<String, Object> calDifBetweenTwoPoints(LinkedHashMap<String, Object> before,LinkedHashMap<String, Object> after,LinkedHashSet<String> dimensions){
        Map<String, Object> newPoint = new HashMap<>();
        //保留小数点2位
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
        //放入时间戳
        newPoint.put(ElasticConstant.XAXIS,after.get(ElasticConstant.XAXIS));
        //其他点进行计算
        for(String column:dimensions){
            //计算除了时间轴外的其他点
            if(!column.equals(ElasticConstant.XAXIS)){
                Double beforeValue = Double.parseDouble(before.get(column).toString());
                Double afterValue = Double.parseDouble(after.get(column).toString());
                newPoint.put(column,decimalFormat.format(afterValue-beforeValue));
            }

        }
        return newPoint;
    }

}

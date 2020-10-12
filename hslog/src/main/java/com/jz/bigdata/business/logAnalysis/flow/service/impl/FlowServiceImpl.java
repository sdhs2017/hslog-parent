package com.jz.bigdata.business.logAnalysis.flow.service.impl;

import com.hs.elsearch.dao.biDao.IBIDao;
import com.hs.elsearch.dao.common.ICommonDao;
import com.hs.elsearch.dao.logDao.ILogSearchDao;
import com.hs.elsearch.dao.flowDao.IFlowSearchDao;
import com.hs.elsearch.entity.HttpRequestParams;
import com.hs.elsearch.entity.VisualParam;
import com.jz.bigdata.business.logAnalysis.flow.service.IflowService;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @program: hsgit
 * @description: 实现IflowService接口
 * @author: jiyourui
 * @create: 2019-11-05 15:00
 **/
@Service(value="flowService")
public class FlowServiceImpl implements IflowService {

    @Autowired
    protected ILogSearchDao elasticsearchDao;

    @Autowired
    protected IFlowSearchDao flowSearchDao;

    @Autowired
    protected ICommonDao commonDao;

    @Autowired
    protected IBIDao biDao;
    @Override
    public long getCount(String index, String[] types, Map<String, String> map) {
        return flowSearchDao.getFlowCount(map,null,null, types,index);
    }

    @Override
    public List<Map<String, Object>> groupBy(String index, String[] types, String groupByField, int size, String starttime, String endtime, Map<String, String> map) throws Exception {

        return flowSearchDao.getListByAggregation(types,starttime,endtime,groupByField,size,map,index);
    }

    @Override
    public List<Map<String, Object>> groupBys(String index, String[] types, String[] groupByField, int size, String starttime, String endtime, Map<String, String> map) throws Exception {
        return flowSearchDao.getListByAggregations(types,starttime,endtime,groupByField,size,map,index);
    }

    @Override
    public List<List<Map<String, Object>>> groupBy(String index, String[] types, String[] groupByFields, int size, String starttime, String endtime, Map<String, String> map) throws Exception {
        return flowSearchDao.getListByAggregation(types,starttime,endtime,groupByFields,size,map,index);
    }

    @Override
    public List<Map<String, Object>> groupByThenSum(String index, String[] types, String groupByField, String sumField, int size, String starttime, String endtime, Map<String, String> map) throws Exception {
        return flowSearchDao.getListBySumOfAggregation(types,starttime,endtime,groupByField,sumField,size,map,index);
    }

    @Override
    public List<Map<String, Object>> groupByThenAvg(String[] types, String groupByField, String avgField, int size, String starttime, String endtime, Map<String, String> map, String... indices) throws Exception {
        return flowSearchDao.getListByAvgOfAggregation(types,starttime,endtime,groupByField,avgField,size,map,indices);
    }

    @Override
    public List<Map<String, Object>> getSumByMetrics(String[] types, String sumField, int size, String starttime, String endtime, Map<String, String> map,String... indices) throws Exception {
        return flowSearchDao.getListBySumOfMetrics(types,starttime,endtime,sumField,size,map,indices);
    }

    @Override
    public List<Map<String, Object>> getCountByMetrics(String[] types, String countField, int size, String starttime, String endtime, Map<String, String> map, String... indices) throws Exception {
        return flowSearchDao.getListByCountOfMetrics(types,starttime,endtime,countField,size,map,indices);
    }

    @Override
    public List<Map<String, Object>> getFlowListByBlend(Map<String, String> map, String starttime, String endtime, String page, String size, String[] types, String... indices) throws Exception {

        SearchHit[] hits = null;
        Integer fromInt = 0;
        Integer sizeInt = 10;
        long count = 0;

        if (page!=null&&size!=null) {
            fromInt = (Integer.parseInt(page)-1)*Integer.parseInt(size);
            sizeInt = Integer.parseInt(size);
        }
        System.out.println("------1.1--------");
        List<Map<String, Object>> list = new ArrayList<>();
        //日志总量
        count = flowSearchDao.getFlowCount(map,starttime,endtime,types,indices);

        Map<String, Object> mapcount = new HashMap<String,Object>();
        mapcount.put("count", count);

        list.add(mapcount);
        list.addAll(flowSearchDao.getFlowListByMap(map,starttime,endtime,fromInt,sizeInt,types,indices));
        return list;
    }

    @Override
    public List<Map<String, Object>> getListByAggregation(VisualParam params) throws Exception {
        return biDao.getListByAggregation(params);
    }

    @Override
    public Map<String,LinkedList<Map<String,Object>>> getListByMultiAggregation(VisualParam params) throws Exception {
        return biDao.getMultiDateHistogramAggregation(params);
    }

    @Override
    public Map<String, Object> getMultiAggregationDataSet(VisualParam params) throws Exception {
        return biDao.getMultiAggregation4dateset(params);
    }


}

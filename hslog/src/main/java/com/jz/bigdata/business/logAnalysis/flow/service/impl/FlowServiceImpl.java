package com.jz.bigdata.business.logAnalysis.flow.service.impl;

import com.hs.elsearch.dao.logDao.ILogSearchDao;
import com.hs.elsearch.dao.flowDao.IFlowSearchDao;
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


    @Override
    public long getCount(String index, String[] types, Map<String, String> map) {
        return flowSearchDao.getFlowCount(map,null,null, types,index);
    }

    @Override
    public List<Map<String, Object>> groupBy(String index, String[] types, String groupByField, int size, String starttime, String endtime, Map<String, String> map) throws Exception {

        return flowSearchDao.getListByAggregation(types,starttime,endtime,groupByField,size,map,index);
    }

    @Override
    public List<Map<String, Object>> groupByThenSum(String index, String[] types, String groupByField, String sumField, int size, String starttime, String endtime, Map<String, String> map) throws Exception {
        return flowSearchDao.getListBySumOfAggregation(types,starttime,endtime,groupByField,sumField,size,map,index);
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

        List<Map<String, Object>> list = new ArrayList<>();
        //日志总量
        count = flowSearchDao.getFlowCount(map,starttime,endtime,types,indices);
        Map<String, Object> mapcount = new HashMap<String,Object>();
        mapcount.put("count", count);

        list.add(mapcount);
        list.addAll(flowSearchDao.getFlowListByMap(map,starttime,endtime,fromInt,sizeInt,types,indices));
        return list;
    }
}

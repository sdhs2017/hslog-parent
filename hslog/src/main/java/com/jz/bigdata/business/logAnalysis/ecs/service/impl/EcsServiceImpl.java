package com.jz.bigdata.business.logAnalysis.ecs.service.impl;

import com.hs.elsearch.dao.ecsDao.IEcsSearchDao;
import com.jz.bigdata.business.logAnalysis.ecs.service.IecsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: hsgit
 * @description: 实现 Elastic Common Schema (ECS)的业务逻辑处理
 * @author: jiyourui
 * @create: 2020-03-30 14:47
 **/
@Service(value="ecsService")
public class EcsServiceImpl implements IecsService {

    @Autowired
    protected IEcsSearchDao ecsSearchDao;

    @Override
    public long getCount(Map<String, String> map, String starttime, String endtime, String... indices) throws Exception {
        return ecsSearchDao.getCount(map, starttime, endtime, indices);
    }

    @Override
    public String deleteById(String index, String id) throws Exception {
        return ecsSearchDao.deleteById(index, id);
    }

    @Override
    public List<Map<String, Object>> getListByContent(String content, String[] multiQueryField, Map<String, String> map, String page, String size, String... indices) throws Exception {
        Integer fromInt = 0;
        Integer sizeInt = 10;

        if (page!=null&&size!=null) {
            fromInt = (Integer.parseInt(page)-1)*Integer.parseInt(size);
            sizeInt = Integer.parseInt(size);
        }
        return ecsSearchDao.getListByContent(content,multiQueryField,map,fromInt,sizeInt,indices);
    }

    @Override
    public List<Map<String, Object>> getLogListByBlend(Map<String, String> map, String starttime, String endtime, String page, String size, String... indices) throws Exception {
        Integer fromInt = 0;
        Integer sizeInt = 10;
        long count = 0;
        if (page!=null&&size!=null) {
            fromInt = (Integer.parseInt(page)-1)*Integer.parseInt(size);
            sizeInt = Integer.parseInt(size);
        }

        List<Map<String, Object>> list = new ArrayList<>();
        //日志总量
        count = ecsSearchDao.getCount(map,starttime,endtime,indices);
        Map<String, Object> mapcount = new HashMap<String,Object>();
        mapcount.put("count", count);

        list.add(mapcount);

        list.addAll(ecsSearchDao.getLogListByMap(map,starttime,endtime,fromInt,sizeInt,indices));

        return list;
    }

    @Override
    public List<Map<String, Object>> groupByThenCount(String starttime, String endtime, String groupByField, int size, Map<String, String> map, String... indices) throws Exception {
        return ecsSearchDao.getListByAggregation(starttime,endtime,groupByField,size,map,indices);
    }

    @Override
    public List<Map<String, Object>> getListGroupByTime(String starttime, String endtime, String groupByDateField, int size, Map<String, String> map, String... indices) throws Exception {
        return ecsSearchDao.getListByDateHistogramAggregation(starttime, endtime, groupByDateField, map, indices);
    }

    @Override
    public List<Map<String, Object>> getListGroupByTimeAndEvent(String starttime, String endtime, String groupByDateField, String subgroupbyType, String subgroupbyField, int size, Map<String, String> map, String... indices) throws Exception {
        return ecsSearchDao.getListByDateHistogramAggregation(starttime,endtime,groupByDateField,subgroupbyField,subgroupbyType,map,indices);
    }
}

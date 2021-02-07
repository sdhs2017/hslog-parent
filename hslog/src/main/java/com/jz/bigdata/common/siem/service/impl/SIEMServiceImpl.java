package com.jz.bigdata.common.siem.service.impl;

import com.hs.elsearch.entity.DataTableColumn;
import com.hs.elsearch.entity.Filter;
import com.hs.elsearch.entity.SearchConditions;
import com.hs.elsearch.service.ISearchService;
import com.jz.bigdata.common.event_alert.dao.IEventAlertDao;
import com.jz.bigdata.common.event_alert.entity.EventAlert;
import com.jz.bigdata.common.event_alert.service.IEventAlertService;
import com.jz.bigdata.common.siem.entity.Interval;
import com.jz.bigdata.common.siem.service.ISIEMService;
import com.jz.bigdata.util.HttpRequestUtil;
import com.jz.bigdata.util.JavaBeanUtil;
import com.jz.bigdata.util.StringUtils;
import joptsimple.internal.Strings;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Author: yiyang
 * @Date: 2021/1/27 10:29
 * @Description:
 */
@Service(value = "SIEMService")
public class SIEMServiceImpl implements ISIEMService {

    @Autowired
    protected IEventAlertDao eventAlertDao;
    private final String event_field = "event.action";
    private final String equipment_field = "fields.equipmentid";
    private final String equipment_index = "winlogbeat-*";//syslog/winlog 对应index
    private final String timestamp_field = "@timestamp";//syslog/winlog 对应日期字段
    @Autowired
    protected ISearchService searchService;
    //日期格式
    private final DateTimeFormatter dtf_time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private final DateTimeFormatter dtf_timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    /**
     * 区间合并算法
     * @param searchHitsList ES查询出的数据
     * @return
     */
    private Map<String, Stack<Interval>> intervalMerge(List<Map<String,Object>> searchHitsList){
//        System.out.println(LocalDateTime.now().format(dtf_time));
//        //最终结果数据
//        // key = 告警title  value = 合并后，一个告警会存在多个时间段
//        //Intervals 记录起始和截止时间。
//        //Stack为JAVA的堆栈类。标准的后进先出类。
//        // 区间合并算法只对最后进入的数据进行对比，因为使用堆栈。实现数据的快速读写。
//        Map<String, Stack<Interval>> resultList = new HashMap<>();
//        /*区间合并算法
//         * 通过将起始时间进行正向排序，对比当前数据的起始时间与上一条数据的截止时间。
//         * 如果起始时间大于截止时间，证明两个时间段不连续，否则就是连续或有交集，需要进行合并（即将当前数据的截止时间覆盖上条数据的截止时间）
//         */
//        for(Map<String,Object> map:searchHitsList){
//            //告警已存在
//            if(resultList.containsKey(map.get(interval_key))){
//                //获取stack堆栈的最后一个存储的对象
//                Interval interval_stack = resultList.get(map.get(interval_key)).peek();
//                //start1>end0 表示当前数据与最后一个存储的时间范围无交集
//                if(LocalDateTime.parse(map.get(start).toString(),dtf_time).isAfter(LocalDateTime.parse(interval_stack.getEnd(),dtf_time))){
//                    //将当前的时间范围放入堆栈
//                    Interval interval = new Interval();
//                    interval.setStart(map.get(start).toString());
//                    interval.setEnd(map.get(end).toString());
//                    resultList.get(map.get(interval_key)).push(interval);
//                }else{
//                    resultList.get(map.get(interval_key)).peek().setEnd(map.get(end).toString());
//
//                }
//            }else{
//                //结果序列中第一次出现该告警
//                //将起始和截止时间组成的对象放到stack堆栈中
//                Interval interval = new Interval();
//                interval.setStart(map.get(start).toString());
//                interval.setEnd(map.get(end).toString());
//                Stack<Interval> stack = new Stack<>();
//                stack.push(interval);
//                resultList.put(map.get(interval_key).toString(),stack);
//            }
//        }
//        System.out.println(LocalDateTime.now().format(dtf_time));
//        System.out.println(com.alibaba.fastjson.JSONArray.toJSON(resultList));
//        return resultList;
        return null;
    }

    /**
     * 日志事件合并成时间区间
     * @param searchHitsList
     * @return
     */
    private Stack<Interval> logMerge(List<Map<String,Object>> searchHitsList,long time_area){
        Stack<Interval> stack = new Stack<>();

        for(Map<String,Object> log:searchHitsList) {
            LocalDateTime this_time = LocalDateTime.parse(log.get("@timestamp").toString(), dtf_timestamp);
            //先把第一条的时间作为起始
            if(stack.empty()){
                Interval interval = new Interval();
                interval.setStart(this_time);
                interval.setCount(1);
                stack.push(interval);
            }else{
                if (stack.peek().getEnd() == null) {
                    //当前时间与起始时间的差值 大于 time_area
                    if (this_time.minusSeconds(time_area).isAfter(stack.peek().getStart())) {
                        //将当前时间覆盖起始时间
                        stack.peek().setStart(this_time);
                    } else {
                        stack.peek().setEnd(this_time);
                        stack.peek().setCount(2);
                    }
                } else {
                    //有截止时间，就需要与截止时间比较   大于time_area
                    if (this_time.minusSeconds(time_area).isAfter(stack.peek().getEnd())) {
                        //最新的时间段已经结束。创建新的时间段对象，写入堆栈
                        Interval interval = new Interval();
                        interval.setStart(LocalDateTime.parse(log.get("@timestamp").toString(), dtf_timestamp));
                        interval.setCount(1);
                        stack.push(interval);
                    } else {
                        //覆盖截止时间，count++
                        stack.peek().setEnd(this_time);
                        stack.peek().setCount(stack.peek().getCount() + 1);
                    }
                }
            }

        }
        return stack;
    }
    @Override
    public LinkedHashMap<String, Stack<Interval>> getSIEMResult_equipment(String equipment_id, String equipment_type,SearchConditions searchConditions) throws Exception {
        LinkedHashMap<String, Stack<Interval>> result = new LinkedHashMap<>();

        //获取该资产对应的事件告警列表数据
        List<EventAlert> list = eventAlertDao.getListByEquipment(equipment_id,equipment_type);
        //遍历
        for(EventAlert eventAlert:list){
            SearchConditions newSearchCondition = new SearchConditions();
            newSearchCondition.setStartTime(searchConditions.getStartTime());
            newSearchCondition.setEndTime(searchConditions.getEndTime());
            newSearchCondition.setDateField(timestamp_field);//日期字段
            newSearchCondition.setIndex_name(equipment_index);//index
            //---------处理filter--------
            String event_filters = eventAlert.getEvent_filters();
            if(null!=event_filters&&!"".equals(event_filters)){
                //filters中包含多个filter
                JSONArray json = JSONArray.fromObject(event_filters);
                //遍历
                for(Object beanObj:json.toArray()){
                    //转bean
                    Filter filter = JavaBeanUtil.mapToBean((Map) JSONObject.fromObject(beanObj), Filter.class);
                    //转换成功时，写入参数对象中
                    if(null!=filter){
                        newSearchCondition.getFilters_alert().add(filter);
                    }
                }
            }
            //根据事件告警设置生成filter条件
            Filter filter = new Filter();
            filter.setField(event_field);
            filter.setOperator("is term");
            filter.setValue(eventAlert.getEvent_name());
            filter.setEnable(true);
            newSearchCondition.getFilters_alert().add(filter);
            //资产id
            Filter filter_equipment = new Filter();
            filter_equipment.setField(equipment_field);
            filter_equipment.setOperator("is term");
            filter_equipment.setValue(equipment_id);
            filter_equipment.setEnable(true);
            newSearchCondition.getFilters_alert().add(filter_equipment);
            //设置排序字段
            DataTableColumn dataTableColumn = new DataTableColumn();
            dataTableColumn.setField("@timestamp");
            dataTableColumn.setSort("asc");
            //searchConditions.getSortColumns().add(dataTableColumn);
            newSearchCondition.getDataTableColumns().add(dataTableColumn);
            //要获取的数据条数，设置足够大的size
            //TODO 是否有更好的处理方式
            newSearchCondition.setFrom(0);
            newSearchCondition.setPage_size(9999999);
            //
            //获取数据
            List<Map<String,Object>> searchHitsList = searchService.getSearchHitsList(newSearchCondition);
            //数据合并

            Stack<Interval> tmpMerge = this.logMerge(searchHitsList,timeConvertToSecond(eventAlert.getEvent_area_num(),eventAlert.getEvent_area_unit()));
            //设置告警限制，超过限制值才允许显示
            Stack<Interval> newStack = new Stack<>();
            for(Interval interval:tmpMerge){
                if(interval.getCount()>=eventAlert.getAlert_count()){
                    interval.setTitle("事件告警名称："+eventAlert.getEvent_alert_name()+"<br>发生次数："+interval.getCount()+"<br>告警条件：事件数>="+eventAlert.getAlert_count());
                    newStack.push(interval);
                }
            }
            if(newStack.size()>0){
                result.put(eventAlert.getEvent_alert_name(),newStack);
            }

        }

        return result;
    }

    /**
     * 将时间参数转化成秒
     * @param time_num 时间数值
     * @param time_unit 时间单位 秒/分钟/小时
     * @return
     */
    private long timeConvertToSecond(int time_num,String time_unit){
        switch (time_unit)
        {
            case "second":
                return time_num;
            case "minute":
                return time_num*60;
            case "hour":
                return time_num*60*60;
            default:
                return time_num;
        }
    }
}

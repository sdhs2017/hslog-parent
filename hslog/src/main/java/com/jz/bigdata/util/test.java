package com.jz.bigdata.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: yiyang
 * @Date: 2021/1/20 17:03
 * @Description:
 */
public class test {
        public static void main(String[] args) {
            String data = "devid=3 dname=\"SecGateNSG\" date=\"2017-08-15 17:59:19\" mod=inf pri=alert msg=The interface status changes, ifName:[ge5], admin:[Enable], state:[UP], link:[UP]";
            Pattern pattern = Pattern.compile("devid=(\\d+)\\sdname=\\\"([^\\\"]*)\\\"\\sdate=\\\"([^\\\"]*)\\\"\\smod=([^\\s]*)\\spri=([^\\s]*)\\smsg=([^\\,]*)\\,\\s*(ifName:\\[\\w+\\d+]\\,\\s*admin:\\[\\w+\\]\\,\\s*state:\\[\\w*\\]\\,\\s*link:\\[\\w*\\])");   //正则匹配
            Matcher matcher = pattern.matcher(data);
            while (matcher.find()) {
                System.out.println("All is:" + matcher.group(0) );
                System.out.println(matcher.group(1));
                System.out.println(matcher.group(2));
                System.out.println(matcher.group(3));
            }
            ;

        }
}

class timeMerge{

    public static RestHighLevelClient restHighLevelClient;
    public static void main(String[] args) {

//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
//        String basicTime = "2021-01-01 12:00:00";
//        //示例源数据
//        List<Map<String,Object>> result = new ArrayList<>();
//        for(int i=1;i<100;i++){
//            for(int j=1;j<10000;j++){
//                Random random = new Random();
//                Map<String,Object> map = new HashMap<>();
//                basicTime = formatter.format(LocalDateTime.parse(basicTime,formatter).plusSeconds(random.nextInt(100)));
//                String end = formatter.format(LocalDateTime.parse(basicTime,formatter).plusSeconds(random.nextInt(100)));
//                map.put("key","事件"+i);
//                map.put("start",basicTime);
//                map.put("end",end);
//                result.add(map);
//            }
//        }
//
//        System.out.println(LocalDateTime.now().format(formatter2));
//        //最终结果数据
//        // key = title
//        // value = starttime:stack endtime:stack
//        Map<String,Stack<Interval>> list = new HashMap<>();
//        for(Map<String,Object> map:result){
//            if(list.containsKey(map.get("key"))){
//
//                Interval intervals = list.get(map.get("key")).peek();
//                //start1>end0 无交集
//                if(LocalDateTime.parse(map.get("start").toString(),formatter).isAfter(LocalDateTime.parse(intervals.getEnd(),formatter))){
//                    Interval interval = new Interval();
//                    interval.setStart(map.get("start").toString());
//                    interval.setEnd(map.get("end").toString());
//                    list.get(map.get("key")).push(interval);
//                }else{
//                    list.get(map.get("key")).peek().setEnd(map.get("end").toString());
//
//                }
//            }else{
//                Interval interval = new Interval();
//                interval.setStart(map.get("start").toString());
//                interval.setEnd(map.get("end").toString());
//                Stack<Interval> stack = new Stack<>();
//                stack.push(interval);
//                list.put(map.get("key").toString(),stack);
//            }
//        }
//        System.out.println(LocalDateTime.now().format(formatter2));
//        System.out.println(JSONArray.toJSON(list));

    }
}
class timeMerge2{

    //无分类测试
    public static void main(String[] args) {
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String basicTime = "2021-01-01T12:00:00.000Z";
        List<Map<String,Object>> result = new ArrayList<>();
        for(int i=0;i<1000000;i++){
            Random random = new Random();
            Map<String,Object> map = new HashMap<>();
            basicTime = formatter2.format(LocalDateTime.parse(basicTime,formatter2).plusSeconds(random.nextInt(100)));
            map.put("@timestamp",basicTime);
            result.add(map);
        }
        System.out.println(LocalDateTime.now().format(formatter2));
        LocalDateTime start_time = null;
        LocalDateTime end_time = null;
        LocalDateTime next_time = null;
        int count = 0;
        long time_area = 90;//单位  毫秒

        List<Map<String,String>> list = new ArrayList<>();
        //先把第一条的时间作为起始+截止
        for(Map<String,Object> log:result){
            if(start_time!=null&&end_time!=null){
                //都不为空，看当前时间是否可以与该区间合并
                next_time = LocalDateTime.parse(log.get("@timestamp").toString(),formatter2);
                //差值大于time_area
                if(next_time.minusSeconds(time_area).isAfter(end_time)){
                    //if(Duration.between(end_time,next_time).toMillis()>time_area){
                    //区间已经结束，放入结果集种
                    Map<String,String> area = new HashMap<>();
                    area.put("start_time",formatter1.format(start_time));
                    area.put("end_time",formatter1.format(end_time));
                    area.put("count",count+"");
                    list.add(area);
                    start_time=null;
                    end_time=null;
                    count=0;
                }else{
                    end_time = next_time;
                    count++;
                }
            }else if(start_time==null){
                //新区间的第一个数值过来
                start_time = LocalDateTime.parse(log.get("@timestamp").toString(),formatter2);
                count++;
            }else{
                end_time = LocalDateTime.parse(log.get("@timestamp").toString(),formatter2);
                //第二个数值过来，计算差值，大于time_ares
                if(end_time.minusSeconds(time_area).isAfter(start_time)){
                    //if( Duration.between(start_time,end_time).toMillis()>time_area){
                    start_time = end_time;
                    end_time = null;
                }else{
                    //计数
                    count++;
                }
            }



//            if(start_time==null){
//                //新区间的第一个数值过来
//                start_time = LocalDateTime.parse(log.get("@timestamp").toString(),formatter2);
//                count++;
//            }else if(end_time==null){
//                end_time = LocalDateTime.parse(log.get("@timestamp").toString(),formatter2);
//                //第二个数值过来，计算差值，大于time_ares
//                if(end_time.minusSeconds(time_area).isAfter(start_time)){
//                //if( Duration.between(start_time,end_time).toMillis()>time_area){
//                    start_time = end_time;
//                    end_time = null;
//                }else{
//                    //无操作
//                    count++;
//                }
//            }else{
//                  //都不为空，看当前时间是否可以与该区间合并
//                next_time = LocalDateTime.parse(log.get("@timestamp").toString(),formatter2);
//                //差值大于time_area
//                if(next_time.minusSeconds(time_area).isAfter(end_time)){
//                    //if(Duration.between(end_time,next_time).toMillis()>time_area){
//                    //区间已经结束，放入结果集种
//                    Map<String,String> area = new HashMap<>();
//                    area.put("start_time",formatter1.format(start_time));
//                    area.put("end_time",formatter1.format(end_time));
//                    area.put("count",count+"");
//                    list.add(area);
//                    start_time=null;
//                    end_time=null;
//                    count=0;
//                }else{
//                    end_time = next_time;
//                    count++;
//                }
//            }
        }
        System.out.println(LocalDateTime.now().format(formatter2));
        System.out.println(JSONArray.toJSON(list));

    }
}


//多线程

class timeMerge3{

    //带事件分类测试
    public static void main(String[] args) throws JsonProcessingException {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String basicTime = "2021-01-01T12:00:00.000Z";
        List<Map<String,Object>> result = new ArrayList<>();
        for(int i=0;i<10000;i++){
            Random random = new Random();
            Map<String,Object> map = new HashMap<>();
            basicTime = formatter2.format(LocalDateTime.parse(basicTime,formatter2).plusSeconds(random.nextInt(100)));
            map.put("@timestamp",basicTime);
            map.put("event.action","事件"+random.nextInt(50));
            result.add(map);
        }
        System.out.println(LocalDateTime.now().format(formatter2));
        long time_area = 9000;//10s

        Map<String,Stack<Interval>> resultMap = new HashMap<>();
        //先把第一条的时间作为起始+截止
        for(Map<String,Object> log:result) {
            //结果列表种已存在该事件
            if (resultMap.containsKey(log.get("event.action").toString())) {
                Stack<Interval> stack = resultMap.get(log.get("event.action").toString());
                LocalDateTime this_time = LocalDateTime.parse(log.get("@timestamp").toString(), formatter2);
                //如果没有截止时间
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
                        interval.setStart(LocalDateTime.parse(log.get("@timestamp").toString(), formatter2));
                        interval.setCount(1);
                        stack.push(interval);
                    } else {
                        //覆盖截止时间，count++
                        stack.peek().setEnd(this_time);
                        stack.peek().setCount(stack.peek().getCount() + 1);
                    }
                }
            } else {
                //不存在事件
                Interval interval = new Interval();
                interval.setStart(LocalDateTime.parse(log.get("@timestamp").toString(), formatter2));
                interval.setCount(1);
                Stack<Interval> stack = new Stack<>();
                stack.push(interval);
                resultMap.put(log.get("event.action").toString(), stack);
            }

        }


        System.out.println(LocalDateTime.now().format(formatter2));
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String json = mapper.writeValueAsString(resultMap);
        System.out.println(json);

    }
}
//时间类
class Interval{
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime start;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end;
    private int count;

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

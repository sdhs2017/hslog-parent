package com.jz.bigdata.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2020/12/2 14:00
 * @Description: dashboard的配置信息，主要描述不同日志类型对应的dashboard——id
 */
public class DashboardConfig {
    //-----------日志类型 syslog

    //------------日志类型 metric
    //单个资产
    public static final List<Map<String,String>> METRIC_DASHBOARDS_ASSET = new ArrayList<>();
    static{
        Map<String,String> map = new HashMap<>();
        map.put("name","log");
        map.put("id","CMCPunUBEtm5D8ifHXoY");
        map.put("type","dashboard");
        METRIC_DASHBOARDS_ASSET.add(map);
    }
    //资产组
    public static final List<Map<String,String>> METRIC_DASHBOARDS_ASSET_GROUP = new ArrayList<>();
    static{
        Map<String,String> map = new HashMap<>();
        map.put("name","资产指标");
        map.put("id","_qxeunUBEtm5D8if4qKB");
        map.put("type","dashboard");
        METRIC_DASHBOARDS_ASSET_GROUP.add(map);
    }
    //-------------日志类型 packet
    //单个资产
    public static final List<Map<String,String>> PACKET_DASHBOARDS_ASSET = new ArrayList<>();
    static{
        String[] names = {"网络流量","HTTP","HTTPS","DNS","DHCPv4","MYSQL"};
        String[] ids = {"w71o9HUBnnDgfQAGJfOD","yAd0-XUBnnDgfQAG4-5m","Q_c7-XUBnnDgfQAGHbNJ","UqxG-HUBnnDgfQAGG4dJ","KNXu-HUBnnDgfQAGVQ9I","o6cf_XUBnnDgfQAG7M1C"};
        for(int i=0;i<names.length;i++){
            Map<String,String> map = new HashMap<>();
            map.put("name",names[i]);
            map.put("id",ids[i]);
            map.put("type","dashboard");
            PACKET_DASHBOARDS_ASSET.add(map);
        }
    }
    //资产组
    public static final List<Map<String,String>> PACKET_DASHBOARDS_ASSET_GROUP = new ArrayList<>();
    static{
        Map<String,String> map = new HashMap<>();
        map.put("name","流量数据概览");
        map.put("id","deQT83UBnnDgfQAG5zvt");
        map.put("type","dashboard");
        PACKET_DASHBOARDS_ASSET_GROUP.add(map);
        //PACKET_DASHBOARDS_ASSET_GROUP.put("流量数据概览","deQT83UBnnDgfQAG5zvt");
    }
    //-----------日志类型syslog
    public static final List<Map<String,String>> SYSLOG_DASHBOARDS_ASSET_GROUP = new ArrayList<>();
    static{
        Map<String,String> map = new HashMap<>();
        map.put("name","linux");
        map.put("id","QvCh2HQBnnP6Ef6k2Tz8");
        map.put("type","dashboard");
        SYSLOG_DASHBOARDS_ASSET_GROUP.add(map);
//        SYSLOG_DASHBOARDS_ASSET_GROUP.put("SYSLOG","QvCh2HQBnnP6Ef6k2Tz8");
    }
    //-----------日志类型winlog
    public static final List<Map<String,String>> WINLOG_DASHBOARDS_ASSET_GROUP = new ArrayList<>();
    static{
        Map<String,String> map = new HashMap<>();
        map.put("name","windows");
        map.put("id","uvWs2HQBnnP6Ef6klX9M");
        map.put("type","dashboard");
        WINLOG_DASHBOARDS_ASSET_GROUP.add(map);
//        WINLOG_DASHBOARDS_ASSET_GROUP.put("WINLOG","uvWs2HQBnnP6Ef6klX9M");
    }
    //-----------日志类型winlog+syslog
    public static final List<Map<String,String>> SYSLOG_WINLOG_DASHBOARDS_ASSET_GROUP = new ArrayList<>();
    static{
        Map<String,String> map = new HashMap<>();
        map.put("name","linux&windows");
        map.put("id","4EUtznQBqKrf67HaI3pC");
        map.put("type","dashboard");
        SYSLOG_WINLOG_DASHBOARDS_ASSET_GROUP.add(map);
//        SYSLOG_WINLOG_DASHBOARDS_ASSET_GROUP.put("SYSLOG&WINLOG","4EUtznQBqKrf67HaI3pC");
    }
    //都要有的SIEM
    public static final List<Map<String,String>> SIEM = new ArrayList<>();
    static{
        Map<String,String> map = new HashMap<>();
        map.put("name","SIEM");
        map.put("id","");
        map.put("type","siem");
        SIEM.add(map);
//        PACKET_DASHBOARDS_ASSET.put("SIEM","");
    }
    //日志类型为syslog的资产，点击报表时的syslog项为写死页面
    public static final List<Map<String,String>> SYSLOG = new ArrayList<>();
    static{
        Map<String,String> map = new HashMap<>();
        map.put("name","SYSLOG");
        map.put("id","syslogEquipmentEcharts");
        map.put("type","syslog");
        SYSLOG.add(map);
//        PACKET_DASHBOARDS_ASSET.put("SIEM","");
    }
    //日志类型为winlog的资产，点击报表时的winlog项为写死页面
    public static final List<Map<String,String>> WINLOG = new ArrayList<>();
    static{
        Map<String,String> map = new HashMap<>();
        map.put("name","WINLOG");
        map.put("id","winEquipmentEcharts");
        map.put("type","winlog");
        WINLOG.add(map);
//        PACKET_DASHBOARDS_ASSET.put("SIEM","");
    }

}

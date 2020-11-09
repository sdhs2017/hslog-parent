package com.jz.bigdata.util;

import com.hs.elsearch.util.ElasticConstant;
import com.jz.bigdata.common.start_execution.cache.AssetCache;
import com.jz.bigdata.common.asset.entity.Asset;
import com.jz.bigdata.common.serviceInfo.entity.ServiceInfo;
import joptsimple.internal.Strings;
import net.sf.json.JSONArray;

import java.util.*;

/**
 * controller层对数据需要进行再处理的工具类
 */
public class ControllerDataTransUtil {

    /**
     * 用于计算将byte类型的数据转为MB
     * @param oldResult 原数据
     * @return
     */
    public static Map<String, Object> calculateMBytes(Map<String, Object> oldResult){
        Map<String, Object> newResult = new HashMap<>();
        if(oldResult.size()>0){
            //图例不变
            newResult.put(ElasticConstant.DIMENSIONS,oldResult.get(ElasticConstant.DIMENSIONS));
            List<LinkedHashMap<String,Object>> newSource = new ArrayList<>();
            //获取数据源
            Object source = oldResult.get(ElasticConstant.SOURCE);
            //进行遍历 每一个对象为一个X轴坐标对应的N个图例的点的集合
            JSONArray array = JSONArray.fromObject(source);
            for(Object xPoints:array){
                LinkedHashMap<String,Object> newPoint = new LinkedHashMap<>();
                Map<String,Object> points =  MapUtil.json2mapObject(xPoints.toString());
                //对每一个点重新进行计算并放入新的数据对象中
                for(Map.Entry<String,Object> entry:points.entrySet()){
                    if(entry.getKey()==ElasticConstant.XAXIS){
                        newPoint.put(ElasticConstant.XAXIS,entry.getValue());//X轴信息不进行处理
                    }else{
                        //Byte转MB
                        newPoint.put(entry.getKey(),(int)(Double.parseDouble(entry.getValue().toString())/1024/1024));
                    }
                }
                newSource.add(newPoint);
            }
            newResult.put(ElasticConstant.SOURCE,newSource);
            return newResult;
        }else{
            return newResult;
        }

    }

    /**
     * 将数据转化为轮询动态折线图所需要的数据,去掉第一个和最后一个点
     * @param oldResult
     * @return
     */
    public static LinkedHashMap<String,ArrayList<Map<String,Object>>> convertToDynamicLineData(Map<String, Object> oldResult){
        /**
         * 返回结果类型 eg:
         * {
         *   lineA:[{"name":"2020-06-02 09:35:17","value":["2020-06-02 09:35:17",8]},{"name":"2020-06-02 09:35:17","value":["2020-06-02 09:35:17",8]}],
         *   lineB:[{"name":"2020-06-02 09:35:17","value":["2020-06-02 09:35:17",8]},{"name":"2020-06-02 09:35:17","value":["2020-06-02 09:35:17",8]}]
         * }
         */
        LinkedHashMap<String,ArrayList<Map<String,Object>>> newResult = new LinkedHashMap<>();
        if(oldResult.size()>0){
            Object source = oldResult.get(ElasticConstant.SOURCE);//数据
            Object dimensions = oldResult.get(ElasticConstant.DIMENSIONS);//图例
            JSONArray sourceArray = JSONArray.fromObject(source);
            //遍历图例
            for(Object line:JSONArray.fromObject(dimensions)){
                //获取到一个图例名称，并且不是“xAxis”（数据中对X轴的别名，与图例放在一个数组中，需要摘除）
                if(!line.toString().equals(ElasticConstant.XAXIS)){
                    //一条线上的N个点集合
                    ArrayList<Map<String,Object>> newLinePoints = new ArrayList<>();
                    //遍历源数据，每一个对象为X轴某个时间点对应的N个Y轴的值
                    for(Object xPoints:sourceArray){
                        //新的数据对象：一条线上的某个点
                        //eg：{"name":"2020-06-02 09:35:17","value":["2020-06-02 09:35:17",8]}
                        Map<String,Object> newPoint = new HashMap<>();
                        //转成Map，方便取值
                        Map<String,Object> points =  MapUtil.json2mapObject(xPoints.toString());
                        //X轴坐标
                        newPoint.put("name",points.get(ElasticConstant.XAXIS));
                        //X轴坐标&对应的值
                        Object [] value = {points.get(ElasticConstant.XAXIS), points.get(line.toString())};
                        newPoint.put("value",value);
                        newLinePoints.add(newPoint);
                    }
                    //移除最后一个点
                    newLinePoints.remove(newLinePoints.size()-1);
                    //移除第一个点
                    newLinePoints.remove(0);
                    newResult.put(line.toString(),newLinePoints);
                }
            }
            return newResult;
        }else{
            return newResult;
        }

    }

    /**
     * 合并两个结果集的数据，source数据的合并及排序(图例只有一个的情况)
     * 该方法可能会因为数据格式的转换出现异常，异常统一throw到controller进行处理
     * @param resultA
     * @param resultB
     * @param dimensionName 图例名称
     * @return
     */
    public static Map<String,Object> mergeResultBucket(Map<String, Object> resultA,Map<String, Object> resultB,String dimensionName) throws Exception{
        Map<String,Object> newResult = new HashMap<>();
        //图例不变
        newResult.put(ElasticConstant.DIMENSIONS,resultA.get(ElasticConstant.DIMENSIONS));
        //获取source
        List<LinkedHashMap<String,Object>> sourceA = (List<LinkedHashMap<String,Object>>)resultA.get(ElasticConstant.SOURCE);
        List<LinkedHashMap<String,Object>> sourceB = (List<LinkedHashMap<String,Object>>)resultB.get(ElasticConstant.SOURCE);
        //两个source合并
        List<LinkedHashMap<String,Object>> newSource = new ArrayList<>();
        newSource.addAll(sourceA);
        newSource.addAll(sourceB);
        //排序
        Collections.sort(newSource,new Comparator<LinkedHashMap<String,Object>>() {
            @Override
            public int compare(LinkedHashMap<String,Object> o1,LinkedHashMap<String,Object> o2) {
                Double name1 = Double.valueOf(o1.get(dimensionName).toString());
                Double name2 = Double.valueOf(o2.get(dimensionName).toString());
                return  (int)(name2- name1);
            }
        });
        newResult.put(ElasticConstant.SOURCE,newSource);
        return newResult;
    }

    /**
     * 将数据与资产名称（逻辑资产）进行对应
     * @param oldResult 原数据
     * @return
     */
    public static Map<String,Object> transAssetName(Map<String, Object> oldResult)throws Exception{
        Map<String,Object> newResult = new HashMap<>();
        if(oldResult.size()>0){
            List<LinkedHashMap<String,Object>> newSource = new ArrayList<>();
            //图例不变
            newResult.put(ElasticConstant.DIMENSIONS,oldResult.get(ElasticConstant.DIMENSIONS));
            //获取缓存中的资产列表
            Map<String, Asset> assetMap = AssetCache.INSTANCE.getAssetMap();
            //获取数据，并进行遍历
            List<LinkedHashMap<String,Object>> source = (List<LinkedHashMap<String,Object>>)oldResult.get(ElasticConstant.SOURCE);//数据
            for(LinkedHashMap<String,Object> xPoints:source){
                //如果在资产列表中可以找到该IP，将IP替换为资产名称
                if(assetMap.get(xPoints.get(ElasticConstant.XAXIS).toString())!=null){
                    xPoints.replace(ElasticConstant.XAXIS,assetMap.get(xPoints.get(ElasticConstant.XAXIS).toString()).getName());
                }else{
                    //数据不需要进行处理
                }
                //将数据放到新的结果集中
                newSource.add(xPoints);
            }
            newResult.put(ElasticConstant.SOURCE,newSource);
            return newResult;
        }else{
            return newResult;
        }

    }

    /**
     * 将数据中的url替换为服务的别名
     * @param oldResult 原数据
     * @param serviceInfoMap 服务列表
     * @return
     */
    public static Map<String,Object> transServiceName(Map<String, Object> oldResult,Map<String,ServiceInfo> serviceInfoMap)throws Exception{
        Map<String,Object> newResult = new HashMap<>();
        List<LinkedHashMap<String,Object>> newSource = new ArrayList<>();
        //图例不变
        newResult.put(ElasticConstant.DIMENSIONS,oldResult.get(ElasticConstant.DIMENSIONS));

        //获取数据，并进行遍历
        List<LinkedHashMap<String,Object>> source = (List<LinkedHashMap<String,Object>>)oldResult.get(ElasticConstant.SOURCE);//数据
        for(LinkedHashMap<String,Object> xPoints:source){
            //如果在服务列表中可以找到该url，并且对应的服务别名不为空，将url替换为别名
            if(serviceInfoMap.get(xPoints.get(ElasticConstant.XAXIS).toString())!=null&&!Strings.isNullOrEmpty(serviceInfoMap.get(xPoints.get(ElasticConstant.XAXIS).toString()).getName())){
                xPoints.replace(ElasticConstant.XAXIS,serviceInfoMap.get(xPoints.get(ElasticConstant.XAXIS).toString()).getName());
            }else{
                //数据不需要进行处理
            }
            //将数据放到新的结果集中
            newSource.add(xPoints);
        }
        newResult.put(ElasticConstant.SOURCE,newSource);
        return newResult;
    }
}

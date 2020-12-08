package com.jz.bigdata.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hs.elsearch.dao.logDao.ILogCrudDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author: yiyang
 * @Date: 2020/12/3 13:45
 * @Description: dashboard初始化报表
 */
@Slf4j
@Component("HsData")
public class HsData {
    @Autowired
    protected ILogCrudDao logCurdDao;
    @Value(value="classpath:database/es/hsdata.json")
    private Resource hsdata_json;

    /**
     * 初始化可视化报表中的数据
     * @return
     */
    public boolean initHsData(){
        try{
//            File file = hsdata_json.getFile();
//            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
//            BufferedReader bf = new BufferedReader(inputReader);
//            // 按行读取字符串
//            String str;
//            while ((str = bf.readLine()) != null) {
//                IndexRequest request = new IndexRequest();
//                request.index("hsdata_test");
//                request.source(str, XContentType.JSON);
//                logCurdDao.bulkProcessor_add(request);
//            }
            String content = FileUtils.readFileToString(hsdata_json.getFile(), "UTF-8");
            JsonElement jsonElementAudit = new JsonParser().parse(content);
            JsonArray array= jsonElementAudit.getAsJsonArray();
            for(JsonElement obj:array){
                //System.out.println(obj.getAsJsonObject().get("_id").toString());
                //System.out.println(obj.getAsJsonObject().get("_source").toString());
                IndexRequest request = new IndexRequest();
                request.index("hsdata_test1");
                request.id(obj.getAsJsonObject().get("_id").getAsString());
                request.source(obj.getAsJsonObject().get("_source").toString(), XContentType.JSON);
                logCurdDao.bulkProcessor_add(request);
            }
            return true;
        }catch(Exception e){
            log.error("hsdata数据初始化失败！"+e.getMessage());
            return false;
        }

    }

    /**
     * 获取新的可视化模块报表的所有id，出现异常时抛出，在外层判定以保证逻辑正常
     * @return
     * @throws Exception
     */
    public Set<String> getHsDataIds() throws Exception{
        Set<String> result = new HashSet<>();
        String content = FileUtils.readFileToString(hsdata_json.getFile(), "UTF-8");
        JsonElement jsonElementAudit = new JsonParser().parse(content);
        JsonArray array= jsonElementAudit.getAsJsonArray();
        for(JsonElement obj:array){
            result.add(obj.getAsJsonObject().get("_id").getAsString());
        }
        return result;
    }
}

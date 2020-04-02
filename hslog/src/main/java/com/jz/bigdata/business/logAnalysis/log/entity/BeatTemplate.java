package com.jz.bigdata.business.logAnalysis.log.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component("BeatTemplate")
public class BeatTemplate {
    @Value(value="classpath:template/auditBeatTemplate.json")
    private Resource auditBeatTemplate;
    @Value(value="classpath:template/winlogBeatTemplate.json")
    private Resource winlogBeatTemplate;
    @Value(value="classpath:template/fileBeatTemplate.json")
    private Resource fileBeatTemplate;
    @Value(value="classpath:template/metricBeatTemplate.json")
    private Resource metricBeatTemplate;
    @Value(value="classpath:template/packetBeatTemplate.json")
    private Resource packetBeatTemplate;
    @Value(value="classpath:template/addFields.json")
    private Resource addFields;

    public String getAuditBeatTemplate(){
        return addFields(auditBeatTemplate);
    }
    public String getWinlogBeatTemplate(){
        return addFields(winlogBeatTemplate);
    }
    public String getFileBeatTemplate(){
        return addFields(fileBeatTemplate);
    }
    public String getMetricBeatTemplate(){
        return addFields(metricBeatTemplate);
    }
    public String getPacketBeatTemplate(){
        return addFields(packetBeatTemplate);
    }

    /**
     * 通过注解获取的文件内容，添加ip和equipmentid
     * @param beatTemplate 读取的文件信息
     * @return 返回新的mapping信息
     */
    private String addFields(Resource beatTemplate){
        try{
            String content = FileUtils.readFileToString(beatTemplate.getFile(), "UTF-8");
            JsonElement jsonElementAudit = new JsonParser().parse(content);
            JsonObject jsonObjectAudit= jsonElementAudit.getAsJsonObject();
            jsonObjectAudit.getAsJsonObject("properties").add("fields",new JsonParser().parse(FileUtils.readFileToString(addFields.getFile(), "UTF-8")));
            return jsonObjectAudit.toString();
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }
}

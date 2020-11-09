package com.jz.bigdata.common.alert.service;

import com.hs.elsearch.entity.SearchConditions;
import com.jz.bigdata.common.alert.entity.Alert;
import com.jz.bigdata.common.assetGroup.entity.AssetGroup;
import org.quartz.SchedulerException;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

public interface IAlertService {
    /**
     * alert第一步设置后，获取返回结果，数据格式为JSON
     * @param searchConditions
     * @return
     */
    public String getAlertResult(SearchConditions searchConditions) throws Exception;

    /**
     * 新增alert
     * @param alert
     * @return
     * @throws Exception
     */
    public boolean insert(Alert alert) throws Exception;
    /**
     * 告警信息查询
     * @param alert
     * @return
     */
    Map<String, Object> getAlertInfoByCondition(Alert alert);
    /**
     * 删除资产组
     * @param alert_id
     * @return
     */
    boolean delete(String alert_id) throws Exception;
    /**
     * 告警信息更新
     * @param assetGroup
     * @param session
     * @return
     */
    String updateById(Alert alert, HttpSession session) throws Exception;
    /**
     * 通过id获取告警信息
     * @param alert_id
     * @return
     */
    Alert getAlertInfoById(String alert_id);

    /**
     * 停止告警计划任务
     * @param alert_id
     * @return
     * @throws Exception
     */
    public boolean stopQuartz(String alert_id) throws Exception;
    /**
     * 启动告警计划任务
     * @param alert_id
     * @return
     * @throws Exception
     */
    public boolean startQuartz(String alert_id) throws Exception;
    /**
     * 初始化告警的计划任务
     */
    public void initAlertQuartz();

    /**
     * 获取告警详情，告警状态为true
     * @param searchConditions
     * @return
     */
    public List<Alert> getAlertFireInfo(SearchConditions searchConditions) throws Exception;

    /**
     * 获取某个告警的执行结果（fire状态）
     * @param alert_id 告警id
     * @param starttime  起始时间
     * @param endtime 截至时间
     * @param from 开始行
     * @param size 每页多少条
     * @return
     */
    //public List<Map<String,Object>> getAlertFireList(String alert_id,String starttime,String endtime,int from,int size) throws Exception;

    /**
     *
     * @param alert_id 告警id
     * @param starttime 起始时间
     * @param endtime 截止时间
     * @param from 开始行
     * @param size 每页多少条
     * @param isFire 是否查询已经产生告警的数据 true:已告警  false：未告警  null：全部
     * @return
     * @throws Exception
     */
    public Map<String,Object> getAlertList(String alert_id,String starttime,String endtime,int from,int size,Boolean isFire) throws Exception;
}

package com.jz.bigdata.common.machineLearning.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/3/17 16:56
 * @Description: AI service接口层
 */
public interface IMachineLearningService {
    /**
     * 获取检测结果
     * @return key：timestamp 时间轴 real_value 日志数 color 颜色 size 点的大小
     * @throws Exception
     */
    public Map<String, Object> getDetectResult() throws  Exception;

    /**
     * 获取训练数据
     * @return key timestamp: x轴  时间    value ：y轴 日志数
     * @throws Exception
     */
    public Map<String,Object> getTrainData() throws Exception;
    /**
     * 获取待检测数据
     * @return key timestamp: x轴  时间    value ：y轴 日志数
     * @throws Exception
     */
    public Map<String,Object> getTestData() throws Exception;
}

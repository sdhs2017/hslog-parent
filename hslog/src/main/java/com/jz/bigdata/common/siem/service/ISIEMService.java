package com.jz.bigdata.common.siem.service;


import com.hs.elsearch.entity.SearchConditions;
import com.jz.bigdata.common.siem.entity.Interval;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @Author: yiyang
 * @Date: 2021/1/27 10:29
 * @Description:
 */
public interface ISIEMService {
    /**
     * 查询资产siem数据
     * @param equipment_id 资产id
     * @param equipment_type 资产类型
     * @param searchConditions 其他查询条件
     * @return
     * @throws Exception
     */
    public LinkedHashMap<String, Stack<Interval>> getSIEMResult_equipment(String equipment_id, String equipment_type, SearchConditions searchConditions) throws  Exception;
}

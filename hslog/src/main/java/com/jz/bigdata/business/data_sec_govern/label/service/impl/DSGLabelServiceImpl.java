package com.jz.bigdata.business.data_sec_govern.label.service.impl;

import com.jz.bigdata.business.data_sec_govern.label.dao.IDSGLabelDao;
import com.jz.bigdata.business.data_sec_govern.label.entity.Label;
import com.jz.bigdata.business.data_sec_govern.label.service.IDSGLabelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author: yiyang
 * @Date: 2021/4/30 14:14
 * @Description: 标签管理 service层实现
 */
@Slf4j
@Service(value = "DSGLabelService")
public class DSGLabelServiceImpl implements IDSGLabelService {
    @Autowired
    protected IDSGLabelDao labelDao;
    @Override
    public int insertLabel(Label label) {
        label.setLabel_id(UUID.randomUUID().toString());

        return labelDao.insertLabel(label);
    }

    @Override
    public int updateLabel(Label label) {
        return labelDao.updateLabel(label);
    }

    @Override
    public Label getLabelInfoById(String label_id) {
        return labelDao.getLabelInfoById(label_id);
    }

    @Override
    public int deleteLabels(String ids) {
        return labelDao.deleteLabels(ids.split(","));
    }

    @Override
    public Map<String, Object> searchLabel(Label label) {
        //获取count
        List<List<Map<String, String>>> count = labelDao.searchCount(label);
        //分页后的结果列表
        List<Label> result = labelDao.searchLabel(label);
        //组装前端需要的数据格式
        Map<String, Object> allMap = new HashMap<>();
        allMap.put("count",count.get(0).get(0).get("count"));
        allMap.put("list",result);
        return allMap;
    }

    @Override
    public List<Map<String, Object>> getLabelAll4Combobox() {
        List<Map<String, Object>> comboboxLabel = new ArrayList<>();
        //查询所有标签列表
        List<Label> labelList = labelDao.searchLabel(new Label());
        for(Label label : labelList){
            Map<String, Object> row = new HashMap<>();
            row.put("label",label.getLabel_name());
            row.put("value",label.getLabel_id());
            comboboxLabel.add(row);
        }
        return comboboxLabel;
    }
}

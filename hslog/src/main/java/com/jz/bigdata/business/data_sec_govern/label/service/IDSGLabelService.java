package com.jz.bigdata.business.data_sec_govern.label.service;

import com.jz.bigdata.business.data_sec_govern.label.entity.Label;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/4/30 14:14
 * @Description: service 借口
 */
public interface IDSGLabelService {
    /**
     * 添加标签
     * @param label 标签基本信息
     * @return 影响行数
     */
    public int insertLabel(Label label);

    /**
     * 更新标签
     * @param label
     * @return 影响行数
     */
    public int updateLabel(Label label);

    /**
     * 通过标签id 获取标签详情
     * @param label_id 标签id
     * @return 标签详情bean
     */
    public Label getLabelInfoById(String label_id);

    /**
     * 删除标签，支持多选删除
     * @param ids 标签id  以逗号隔开
     * @return 影响行数
     */
    public int deleteLabels(String ids);

    /**
     * 通过查询条件获取标签列表
     * @param label 查询条件bean
     * @return 标签列表
     */
    public Map<String, Object> searchLabel(Label label);

    /**
     * 获取所有标签列表，服务于前端combobox
     * @return combobox结构的list map中key：label combobox显示内容/ value combobox用于传到后端的id（标签id）
     */
    public List<Map<String, Object>> getLabelAll4Combobox();
}

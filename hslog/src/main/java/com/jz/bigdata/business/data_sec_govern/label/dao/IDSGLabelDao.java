package com.jz.bigdata.business.data_sec_govern.label.dao;

import com.jz.bigdata.business.data_sec_govern.label.entity.FieldLabelRelation;
import com.jz.bigdata.business.data_sec_govern.label.entity.Label;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/4/30 14:15
 * @Description:
 */
public interface IDSGLabelDao {
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
    public Label getLabelInfoById(@Param("label_id") String label_id);

    /**
     * 删除标签，支持多选删除
     * @param ids 标签id  以逗号隔开
     * @return 影响行数
     */
    public int deleteLabels(String[] ids);

    /**
     * 通过查询条件获取标签列表
     * @param label 查询条件bean
     * @return 标签列表
     */
    public List<Label> searchLabel(Label label);

    /**
     * 获取count 用于页面显示总条数
     * @param label 查询条件
     * @return count
     */
    public List<List<Map<String,String>>> searchCount(Label label);

    /**
     * 元数据字段与标签的多对多关系表 批量入库
     * @param list 入库数据  list
     * @return 入库行数
     */
    public int batchInsertRelation(List<FieldLabelRelation> list);

    /**
     * 根据数据源id删除字段与标签的关系表信息
     * @param data_source_id 数据源id
     * @return 删除条数
     */
    public int deleteRelationsByDataSourceId(@Param("data_source_id") String data_source_id);

    /**
     * 根据字段id删除其标签信息
     * @param metadata_field_id 字段id
     * @return
     */
    public int deleteRelationsByFieldId(@Param("metadata_field_id") String metadata_field_id);

    /**
     * 插入一条字段与标签的关系数据
     * @param data_source_id 数据源id
     * @param metadata_field_id 字段id
     * @param dsg_label_id 标签id
     * @param dsg_label_name 标签名称
     * @return
     */
    public int insertFieldLabelRelation(@Param("data_source_id") String data_source_id,@Param("metadata_field_id") String metadata_field_id,@Param("dsg_label_id") String dsg_label_id,@Param("dsg_label_name") String dsg_label_name);
}

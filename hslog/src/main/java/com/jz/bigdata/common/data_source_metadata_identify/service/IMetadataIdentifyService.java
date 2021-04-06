package com.jz.bigdata.common.data_source_metadata_identify.service;

import com.jz.bigdata.common.data_source_metadata_identify.entity.MetadataIdentifyBasic;
import com.jz.bigdata.common.data_source_metadata_identify.entity.MetadataIdentifyDetails;
import com.jz.bigdata.util.TreeCombobox;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * @Author: yiyang
 * @Date: 2021/3/23 14:55
 * @Description:
 */
public interface IMetadataIdentifyService {
    /**
     * 添加元数据标识基础分类
     * @param metadataIdentifyBasic 基础信息bean
     * @return 成功后返回节点信息
     */
    public int insertMetadataIdentify_Basic(MetadataIdentifyBasic metadataIdentifyBasic);

    /**
     * 更新元数据标识基本分类
     * @param metadataIdentifyBasic
     * @return 影响条数
     */
    public int updateMetadataIdentify_Basic(MetadataIdentifyBasic metadataIdentifyBasic);

    /**
     * 批量删除元数据标识基本分类
     * @param ids
     * @return 影响条数
     */
    public int deleteMetadataIdentify_Basic(String[] ids);

    /**
     * 获取元数据标识基本分类 列表
     * @return
     */
    public List<MetadataIdentifyBasic> getBasicList();

    /**
     * 根据元数据标识基本分类ID获取详情
     * @param identify_basic_id
     * @return
     */
    public MetadataIdentifyBasic selectBasicInfoById( String identify_basic_id);

    /**
     * 添加元数据标识详细分类
     * @param metadataIdentifyDetails
     * @return 插入成功，返回节点信息
     */
    public MetadataIdentifyDetails insertMetadataIdentify_Details(MetadataIdentifyDetails metadataIdentifyDetails);
    /**
     * 更新元数据标识详细分类
     * @param metadataIdentifyDetails
     * @return 影响条数
     */
    public int updateMetadataIdentify_Details(MetadataIdentifyDetails metadataIdentifyDetails);

    /**
     * 批量删除元数据标识详细分类
     * @param ids
     * @return 影响条数
     */
    public int deleteMetadataIdentify_Details(String ids);

    /**
     * 根据元数据标识详细分类ID获取详情
     * @param identify_details_id
     * @return
     */
    public MetadataIdentifyDetails selectDetailsInfoById(String identify_details_id);

    /**
     * 通过父节点获取子节点的信息列表
     * @param identify_details_superior
     * @return
     */
    public List<MetadataIdentifyDetails> getDetailsListBySuperiorId(String identify_details_superior);

    /**
     * 获取元数据标识的整体tree结构
     * @return tree list
     */
    public List<TreeCombobox> getMetadataIdentifyTree();
}

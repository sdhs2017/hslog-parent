package com.jz.bigdata.common.data_source_metadata_identify.dao;

import com.jz.bigdata.common.data_source_metadata_identify.entity.MetadataIdentifyBasic;
import com.jz.bigdata.common.data_source_metadata_identify.entity.MetadataIdentifyDetails;
import com.jz.bigdata.common.data_source_metadata_identify.entity.MetadataIdentifyTempTable;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/3/24 10:09
 * @Description: 数据层
 */
public interface IMetadataIdentifyDao {
    /**
     * 更新元数据标识后，需要将关系表中的数据也进行更新
     * @param metadata_identify_name
     * @param metadata_identify_id
     * @return 影响条数
     */
    public int updateRelations(@Param("metadata_identify_name")String metadata_identify_name,@Param("metadata_identify_id")String metadata_identify_id);

    /**
     * 删除元数据标识后，关系表中的有关该标识的信息也要删除
     * @param metadata_identify_name
     * @return 影响条数
     */
    public int deleteRelations(@Param("metadata_identify_id")String metadata_identify_name);
    /**
     * 添加元数据标识基本分类
     * @param metadataIdentifyBasic
     * @return 影响条数
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
    public MetadataIdentifyBasic selectBasicInfoById(@Param("identify_basic_id") String identify_basic_id);

    /**
     * 添加元数据标识详细分类
     * @param metadataIdentifyDetails
     * @return 影响条数
     */
    public int insertMetadataIdentify_Details(MetadataIdentifyDetails metadataIdentifyDetails);
    /**
     * 更新元数据标识详细分类
     * @param metadataIdentifyDetails
     * @return 影响条数
     */
    public int updateMetadataIdentify_Details(MetadataIdentifyDetails metadataIdentifyDetails);

    /**
     * 删除元数据标识详细分类,使用存储过程查询子节点
     * @param identify_details_id
     * @return 所有子节点
     */
    public List<MetadataIdentifyTempTable> selectMetadataIdentify_Details_ids(@Param("identify_details_id")String identify_details_id);

    /**
     * 通过基础分类的id  删除下属所有子类的信息
     * @param ids 多个基础类别id  批量删除
     * @return
     */
    public int deleteDetailsByBasicId(String[] ids);

    /**
     * 根据元数据标识详细分类ID获取详情
     * @param identify_details_id
     * @return
     */
    public MetadataIdentifyDetails selectDetailsInfoById(@Param("identify_details_id") String identify_details_id);

    /**
     * 通过父节点获取子节点的信息列表
     * @param identify_details_superior
     * @return
     */
    public List<MetadataIdentifyDetails> getDetailsListBySuperiorId(@Param("identify_details_superior") String identify_details_superior);

    /**
     * 批量删除元数据标识信息
     * @param ids
     * @return
     */
    public int deleteMetadataIdentify_Details_batch(String[] ids);

    /**
     * 批量删除元数据字段与标识对应关系信息
     * @param ids
     * @return
     */
    public int deleteMetadataIdentify_relation_batch(String[] ids);

    /**
     * 删除基础标识时，涉及到基础标识及其所有子类的字段标识信息都要删除
     * @param ids 基础标识id
     * @return
     */
    public int deleteRelationByBasicIdentify(String[] ids);
}

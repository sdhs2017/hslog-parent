package com.jz.bigdata.business.data_sec_govern.tag_library.service;

import com.jz.bigdata.business.data_sec_govern.tag_library.entity.TagBasic;
import com.jz.bigdata.business.data_sec_govern.tag_library.entity.TagDetail;
import com.jz.bigdata.util.TreeCombobox;

import java.util.List;

/**
 * @Author: yiyang
 * @Date: 2021/3/23 14:55
 * @Description:
 */
public interface ITagLibraryService {
    /**
     * 添加元数据标识基础分类
     * @param tagBasic 基础信息bean
     * @return 成功后返回节点信息
     */
    public int insertTagLibrary_Basic(TagBasic tagBasic);

    /**
     * 更新元数据标识基本分类
     * @param tagBasic
     * @return 影响条数
     */
    public int updateTagLibrary_Basic(TagBasic tagBasic);

    /**
     * 批量删除元数据标识基本分类
     * @param ids
     * @return 影响条数
     */
    public int deleteTagLibrary_Basic(String[] ids);

    /**
     * 获取元数据标识基本分类 列表
     * @return
     */
    public List<TagBasic> getTagBasicList();

    /**
     * 根据元数据标识基本分类ID获取详情
     * @param identify_basic_id
     * @return
     */
    public TagBasic selectBasicInfoById(String identify_basic_id);

    /**
     * 添加元数据标识详细分类
     * @param tagDetail
     * @return 插入成功，返回节点信息
     */
    public TagDetail insertTagLibrary_Detail(TagDetail tagDetail);
    /**
     * 更新元数据标识详细分类
     * @param tagDetail
     * @return 影响条数
     */
    public int updateTagLibrary_Detail(TagDetail tagDetail);

    /**
     * 批量删除元数据标识详细分类
     * @param ids
     * @return 影响条数
     */
    public int deleteTagLibrary_Detail(String ids);

    /**
     * 根据元数据标识详细分类ID获取详情
     * @param tag_Detail_id
     * @return
     */
    public TagDetail selectTagDetailInfoById(String tag_Detail_id);

    /**
     * 通过父节点获取子节点的信息列表
     * @param tag_Detail_superior
     * @return
     */
    public List<TagDetail> getTagDetailListBySuperiorId(String tag_Detail_superior);

    /**
     * 获取标签库整体tree结构
     * @return tree list
     */
    public List<TreeCombobox> getTagLibraryTree();

    /**
     * 通过id获取标签详细分类详情
     * @param tag_detail_id
     * @return
     */
    public TagDetail getTagLibraryDetailInfoById(String tag_detail_id);
    /**
     * 通过id获取标签基础分类详情
     * @param tag_detail_id
     * @return
     */
    public TagBasic getTagLibraryBasicInfoById(String tag_basic_id);
}

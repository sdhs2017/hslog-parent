package com.jz.bigdata.business.data_sec_govern.tag_library.dao;

import com.jz.bigdata.business.data_sec_govern.tag_library.entity.TagBasic;
import com.jz.bigdata.business.data_sec_govern.tag_library.entity.TagDetail;
import com.jz.bigdata.business.data_sec_govern.tag_library.entity.TagTempTable;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: yiyang
 * @Date: 2021/3/24 10:09
 * @Description: 数据层
 */
public interface ITagLibraryDao {
    /**
     * 更新元数据标识后，需要将关系表中的数据也进行更新
     * @param tag_name
     * @param tag_id
     * @return 影响条数
     */
    public int updateRelations(@Param("tag_name") String tag_name, @Param("tag_id") String tag_id);

    /**
     * 删除元数据标识后，关系表中的有关该标识的信息也要删除
     * @param tag_id
     * @return 影响条数
     */
    public int deleteRelations(@Param("tag_id") String tag_id);
    /**
     * 添加标签库基本分类
     * @param tagBasic
     * @return 影响条数
     */
    public int insertTagLibrary_Basic(TagBasic tagBasic);
    /**
     * 更新标签库基本分类
     * @param tagBasic
     * @return 影响条数
     */
    public int updateTagLibrary_Basic(TagBasic tagBasic);

    /**
     * 批量删除标签库基本分类
     * @param ids
     * @return 影响条数
     */
    public int deleteTagLibrary_Basic(String[] ids);

    /**
     * 获取标签库基本分类 列表
     * @return
     */
    public List<TagBasic> getTagBasicList();

    /**
     * 根据标签库基本分类ID获取详情
     * @param tag_basic_id
     * @return
     */
    public TagBasic selectTagBasicInfoById(@Param("tag_basic_id") String tag_basic_id);

    /**
     * 添加标签库详细分类
     * @param tagDetail
     * @return 影响条数
     */
    public int insertTagLibrary_Detail(TagDetail tagDetail);
    /**
     * 更新元数据标识详细分类
     * @param tagDetail
     * @return 影响条数
     */
    public int updateTagLibrary_Details(TagDetail tagDetail);

    /**
     * 通过基础分类的id  删除下属所有子类的信息
     * @param ids 多个基础类别id  批量删除
     * @return
     */
    public int deleteTagDetailByBasicId(String[] ids);

    /**
     * 根据元数据标识详细分类ID获取详情
     * @param tag_detail_id
     * @return
     */
    public TagDetail selectTagDetailInfoById(@Param("tag_detail_id") String tag_detail_id);

    /**
     * 通过父节点获取子节点的信息列表
     * @param tag_detail_superior
     * @return
     */
    public List<TagDetail> getTagDetailListBySuperiorId(@Param("tag_detail_superior") String tag_detail_superior);

    /**
     * 批量删除元数据标识信息
     * @param ids
     * @return
     */
    public int deleteTagDetail_batch(String[] ids);

    /**
     * 批量删除元数据字段与标识对应关系信息
     * @param ids
     * @return
     */
    public int deleteTagRelation_batch(String[] ids);

    /**
     * 删除基础标识时，涉及到基础标识及其所有子类的字段标识信息都要删除
     * @param ids 基础标识id
     * @return
     */
    public int deleteRelationByTagBasicId(String[] ids);

    /**
     * 删除标签库详细分类,使用存储过程查询子节点
     * @param tag_detail_id
     * @return 所有子节点
     */
    public List<TagTempTable> selectTagDetail_ids(@Param("tag_detail_id") String tag_detail_id);

    /**
     *  通过标签库详细分类 id 获取详情
     * @param tag_detail_id 标签库详细分类 id
     * @return
     */
    public TagDetail getTagLibraryDetailInfoById(@Param("tag_detail_id") String tag_detail_id);
    /**
     *  通过标签库详基础类 id 获取详情
     * @param tag_basic_id 标签库基础分类 id
     * @return
     */
    public TagBasic getTagLibraryBasicInfoById(@Param("tag_detail_id") String tag_basic_id);
}

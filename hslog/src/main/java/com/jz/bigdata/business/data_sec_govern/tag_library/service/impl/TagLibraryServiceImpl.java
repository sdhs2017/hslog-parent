package com.jz.bigdata.business.data_sec_govern.tag_library.service.impl;


import com.jz.bigdata.business.data_sec_govern.tag_library.dao.ITagLibraryDao;
import com.jz.bigdata.business.data_sec_govern.tag_library.entity.TagBasic;
import com.jz.bigdata.business.data_sec_govern.tag_library.entity.TagDetail;
import com.jz.bigdata.business.data_sec_govern.tag_library.entity.TagTempTable;
import com.jz.bigdata.business.data_sec_govern.tag_library.service.ITagLibraryService;
import com.jz.bigdata.util.TreeCombobox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Author: yiyang
 * @Date: 2021/3/23 14:55
 * @Description: 数据源
 */
@Slf4j
@Service(value = "TagLibraryService")
public class TagLibraryServiceImpl implements ITagLibraryService {

    @Autowired
    protected ITagLibraryDao TagLibraryDao;
    @Override
    public int insertTagLibrary_Basic(TagBasic tagBasic) {
        //生成id
        tagBasic.setTag_basic_id(UUID.randomUUID().toString());
        return TagLibraryDao.insertTagLibrary_Basic(tagBasic);
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
    public int updateTagLibrary_Basic(TagBasic tagBasic) {
        //信息更新后，需要将关系表中对应的名称也进行更新
        //TODO
        //TagLibraryDao.updateRelations(tagBasic.getTag_basic_name(),tagBasic.getTag_basic_id());
        return TagLibraryDao.updateTagLibrary_Basic(tagBasic);
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
    public int deleteTagLibrary_Basic(String[] ids) {
        //删除基础分类及字段涉及到的 字段与标识关系表中的数据
        //TODO
        //TagLibraryDao.deleteRelationByTagBasicId(ids);
        //1.删除基础分类
        int basicResult = TagLibraryDao.deleteTagLibrary_Basic(ids);
        //2.删除子类
        TagLibraryDao.deleteTagDetailByBasicId(ids);
        //返回删除基础分类的影响条数，以此来判断是否正常删除(子类可能没有，删除条数为零。)
        return basicResult;
    }

    @Override
    public List<TagBasic> getTagBasicList() {
        return TagLibraryDao.getTagBasicList();
    }

    @Override
    public TagBasic selectBasicInfoById(String tag_basic_id) {
        return TagLibraryDao.selectTagBasicInfoById(tag_basic_id);
    }

    @Override
    public TagDetail insertTagLibrary_Detail(TagDetail tagDetail) {
        //生成id
        tagDetail.setTag_detail_id(UUID.randomUUID().toString());

        int result = TagLibraryDao.insertTagLibrary_Detail(tagDetail);
        if(result==1){
            //添加成功
            return tagDetail;
        }else{
            return null;
        }
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
    public int updateTagLibrary_Detail(TagDetail tagDetail) {
        //信息更新后，需要将关系表中对应的名称也进行更新
        //TODO
        //TagLibraryDao.updateRelations(tagDetail.getTag_detail_name(), tagDetail.getTag_detail_id());
        return TagLibraryDao.updateTagLibrary_Details(tagDetail);
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
    public int deleteTagLibrary_Detail(String id) {
        //获取要删除节点及其所有子节点的id
        List<TagTempTable> result = TagLibraryDao.selectTagDetail_ids(id);
        String[] ids = new String[result.size()];
        //遍历
        for(int i=0;i<result.size();i++){

            ids[i] = result.get(i).getIds();

        }
        //删除 元数据标识表中的信息
        int count_detail = TagLibraryDao.deleteTagDetail_batch(ids);
        //删除字段与标识的对应关系表中的信息
        //TODO
        //int count_relation = TagLibraryDao.deleteTagRelation_batch(ids);
        //return count_detail+count_relation;
        return count_detail;
    }

    @Override
    public TagDetail selectTagDetailInfoById(String tag_detail_id) {
        return TagLibraryDao.selectTagDetailInfoById(tag_detail_id);
    }

    @Override
    public List<TagDetail> getTagDetailListBySuperiorId(String tag_detail_superior) {
        return TagLibraryDao.getTagDetailListBySuperiorId(tag_detail_superior);
    }

    @Override
    public List<TreeCombobox> getTagLibraryTree() {
        List<TreeCombobox> result = new ArrayList<>();
        //获取元数据标识基本分类的列表
        List<TagBasic> basicList = TagLibraryDao.getTagBasicList();
        //遍历
        for(TagBasic basic:basicList){
            TreeCombobox treeCombobox = new TreeCombobox();
            treeCombobox.setId(basic.getTag_basic_id());
            treeCombobox.setText(basic.getTag_basic_name());
            treeCombobox.setChildren(getTagLibraryRec(basic.getTag_basic_id()));
            result.add(treeCombobox);
        }
        return result;
    }

    @Override
    public TagDetail getTagLibraryDetailInfoById(String tag_detail_id) {

        return TagLibraryDao.getTagLibraryDetailInfoById(tag_detail_id);
    }

    @Override
    public TagBasic getTagLibraryBasicInfoById(String tag_basic_id) {
        return TagLibraryDao.getTagLibraryBasicInfoById(tag_basic_id);
    }

    /**
     * 递归方法，通过父节点id获取其下子节点信息，子节点继续作为父节点获取下级节点
     * @param fid 父节点id
     * @return tree list
     */
    private List<TreeCombobox> getTagLibraryRec(String fid){
        List<TreeCombobox> result = new ArrayList<>();
        //获取下级节点
        List<TagDetail> list = TagLibraryDao.getTagDetailListBySuperiorId(fid);
        for(TagDetail details:list){
            //创建tree节点信息
            TreeCombobox treeCombobox = new TreeCombobox();
            treeCombobox.setId(details.getTag_detail_id());
            treeCombobox.setText(details.getTag_detail_name());
            treeCombobox.setChildren(getTagLibraryRec(details.getTag_detail_id()));
            result.add(treeCombobox);
        }
        return result;
    }
}

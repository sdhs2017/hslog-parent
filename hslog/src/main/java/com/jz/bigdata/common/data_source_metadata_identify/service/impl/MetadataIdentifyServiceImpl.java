package com.jz.bigdata.common.data_source_metadata_identify.service.impl;


import com.jz.bigdata.common.data_source_metadata_identify.dao.IMetadataIdentifyDao;
import com.jz.bigdata.common.data_source_metadata_identify.entity.MetadataIdentifyBasic;
import com.jz.bigdata.common.data_source_metadata_identify.entity.MetadataIdentifyDetails;
import com.jz.bigdata.common.data_source_metadata_identify.entity.MetadataIdentifyTempTable;
import com.jz.bigdata.common.data_source_metadata_identify.service.IMetadataIdentifyService;
import com.jz.bigdata.util.TreeCombobox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: yiyang
 * @Date: 2021/3/23 14:55
 * @Description: 数据源
 */
@Slf4j
@Service(value = "MetadataIdentifyService")
public class MetadataIdentifyServiceImpl implements IMetadataIdentifyService {

    @Autowired
    protected IMetadataIdentifyDao metadataIdentifyDao;
    @Override
    public int insertMetadataIdentify_Basic(MetadataIdentifyBasic metadataIdentifyBasic) {
        //生成id
        metadataIdentifyBasic.setIdentify_basic_id(UUID.randomUUID().toString());
        return metadataIdentifyDao.insertMetadataIdentify_Basic(metadataIdentifyBasic);
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
    public int updateMetadataIdentify_Basic(MetadataIdentifyBasic metadataIdentifyBasic) {
        //信息更新后，需要将关系表中对应的名称也进行更新
        metadataIdentifyDao.updateRelations(metadataIdentifyBasic.getIdentify_basic_name(),metadataIdentifyBasic.getIdentify_basic_id());
        return metadataIdentifyDao.updateMetadataIdentify_Basic(metadataIdentifyBasic);
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
    public int deleteMetadataIdentify_Basic(String[] ids) {
        //删除基础分类及字段涉及到的 字段与标识关系表中的数据
        metadataIdentifyDao.deleteRelationByBasicIdentify(ids);
        //1.删除基础分类
        int basicResult = metadataIdentifyDao.deleteMetadataIdentify_Basic(ids);
        //2.删除子类
        metadataIdentifyDao.deleteDetailsByBasicId(ids);
        //返回删除基础分类的影响条数，以此来判断是否正常删除(子类可能没有，删除条数为零。)
        return basicResult;
    }

    @Override
    public List<MetadataIdentifyBasic> getBasicList() {
        return metadataIdentifyDao.getBasicList();
    }

    @Override
    public MetadataIdentifyBasic selectBasicInfoById(String identify_basic_id) {
        return metadataIdentifyDao.selectBasicInfoById(identify_basic_id);
    }

    @Override
    public MetadataIdentifyDetails insertMetadataIdentify_Details(MetadataIdentifyDetails metadataIdentifyDetails) {
        //生成id
        metadataIdentifyDetails.setIdentify_details_id(UUID.randomUUID().toString());

        int result = metadataIdentifyDao.insertMetadataIdentify_Details(metadataIdentifyDetails);
        if(result==1){
            //添加成功
            return metadataIdentifyDetails;
        }else{
            return null;
        }
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
    public int updateMetadataIdentify_Details(MetadataIdentifyDetails metadataIdentifyDetails) {
        //信息更新后，需要将关系表中对应的名称也进行更新
        metadataIdentifyDao.updateRelations(metadataIdentifyDetails.getIdentify_details_name(),metadataIdentifyDetails.getIdentify_details_id());
        return metadataIdentifyDao.updateMetadataIdentify_Details(metadataIdentifyDetails);
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
    public int deleteMetadataIdentify_Details(String id) {
        //获取要删除节点及其所有子节点的id
        List<MetadataIdentifyTempTable> result = metadataIdentifyDao.selectMetadataIdentify_Details_ids(id);
        String[] ids = new String[result.size()];
        //遍历
        for(int i=0;i<result.size();i++){

            ids[i] = result.get(i).getIds();

        }
        //删除 元数据标识表中的信息
        int count_detail = metadataIdentifyDao.deleteMetadataIdentify_Details_batch(ids);
        //删除字段与标识的对应关系表中的信息
        int count_relation = metadataIdentifyDao.deleteMetadataIdentify_relation_batch(ids);
        return count_detail+count_relation;
    }

    @Override
    public MetadataIdentifyDetails selectDetailsInfoById(String identify_details_id) {
        return metadataIdentifyDao.selectDetailsInfoById(identify_details_id);
    }

    @Override
    public List<MetadataIdentifyDetails> getDetailsListBySuperiorId(String identify_details_superior) {
        return metadataIdentifyDao.getDetailsListBySuperiorId(identify_details_superior);
    }

    @Override
    public List<TreeCombobox> getMetadataIdentifyTree() {
        List<TreeCombobox> result = new ArrayList<>();
        //获取元数据标识基本分类的列表
        List<MetadataIdentifyBasic> basicList = metadataIdentifyDao.getBasicList();
        //遍历
        for(MetadataIdentifyBasic basic:basicList){
            TreeCombobox treeCombobox = new TreeCombobox();
            treeCombobox.setId(basic.getIdentify_basic_id());
            treeCombobox.setText(basic.getIdentify_basic_name());
            treeCombobox.setChildren(getMetadataIdentifyRec(basic.getIdentify_basic_id()));
            result.add(treeCombobox);
        }
        return result;
    }

    /**
     * 递归方法，通过父节点id获取其下子节点信息，子节点继续作为父节点获取下级节点
     * @param fid 父节点id
     * @return tree list
     */
    private List<TreeCombobox> getMetadataIdentifyRec(String fid){
        List<TreeCombobox> result = new ArrayList<>();
        //获取下级节点
        List<MetadataIdentifyDetails> list = metadataIdentifyDao.getDetailsListBySuperiorId(fid);
        for(MetadataIdentifyDetails details:list){
            //创建tree节点信息
            TreeCombobox treeCombobox = new TreeCombobox();
            treeCombobox.setId(details.getIdentify_details_id());
            treeCombobox.setText(details.getIdentify_details_name());
            treeCombobox.setChildren(getMetadataIdentifyRec(details.getIdentify_details_id()));
            result.add(treeCombobox);
        }
        return result;
    }
}

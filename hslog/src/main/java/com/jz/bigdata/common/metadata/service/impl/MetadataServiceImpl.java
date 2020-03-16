package com.jz.bigdata.common.metadata.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hs.elsearch.dao.logDao.ILogIndexDao;
import com.jz.bigdata.common.metadata.controller.MetadataController;
import com.jz.bigdata.common.metadata.entity.Metadata;
import com.jz.bigdata.common.metadata.service.IMetadataService;
import com.jz.bigdata.roleauthority.menu.entity.Menu;
import com.jz.bigdata.roleauthority.menu.util.TreeBuilder;
import org.apache.log4j.Logger;
import org.elasticsearch.client.indices.IndexTemplateMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Int;

import javax.xml.bind.annotation.XmlType;
import java.util.*;

@Service(value="metadataService")
public class MetadataServiceImpl implements IMetadataService {
    private static Logger logger = Logger.getLogger(MetadataServiceImpl.class);
    @Autowired
    protected ILogIndexDao logIndexDao;
    @Override
    public List<Metadata> getTemplateMapping(String templateName) throws Exception {
        //获取template的数据
        List<IndexTemplateMetaData> list = logIndexDao.getTemplateData(templateName);
        //由于API仅提供类似正则匹配的模糊查询，返回的都是list，但是对于本功能而言，需要返回唯一一条记录
        if(list.size()==1){
            IndexTemplateMetaData temp = list.get(0);
            //获取mapping信息
            MappingMetaData mmd = temp.mappings();
            //获取mapping信息
            Map<String, Object> sourceMap = mmd.getSourceAsMap();
            return getListMetadataBySourceMap(sourceMap);
        }else{
            return null;
        }
    }

    @Override
    public List<Metadata> getIndexMapping(String index) throws Exception {
        List<Metadata> metadataList = new ArrayList<Metadata>();
        //获取template的数据
        Map<String,MappingMetaData> mapMapping = logIndexDao.getIndexMappingData(index);
        //获取MappingMetaData
        MappingMetaData mapping = mapMapping.get(index);
        if(mapping!=null){
            //获取mapping信息
            Map<String,Object> sourceMap = mapping.getSourceAsMap();
            return getListMetadataBySourceMap(sourceMap);
        }else{
            return null;
        }
    }

    @Override
    public String getIndexTree(String es_tempalatePattern) throws Exception {
       List<Menu> listMenu = new ArrayList<>();
        //获取所有template的数据
        List<IndexTemplateMetaData> list = logIndexDao.getTemplateData("*");
        //遍历template
        for(IndexTemplateMetaData template:list){
            //将所有的template进行筛选，只选择
            if(template.name().indexOf(es_tempalatePattern.replace("*",""))>=0){
                //一级菜单 template
                Menu templateMenu = new Menu();
                templateMenu.setId(template.name());
                templateMenu.setMenuName(template.name());
                templateMenu.setState("1");//第一级菜单标识
                //二级菜单
                List<Menu> indexMenus = new ArrayList<>();
                //获取template的pattern，例如template名为hslog_packet的pattern为hslog_packet*
                List<String> patterns = template.patterns();
                for(String pattern:patterns){
                    //获取该pattern下的所有index
                    try{
                        //在pattern无法匹配到index时，会抛出异常，需要在当前进行处理
                        String[] indices = logIndexDao.getIndices(pattern);
                        //遍历index
                        for(String index:indices){
                            //二级菜单 template
                            Menu indexMenu = new Menu();
                            indexMenu.setId(index);
                            indexMenu.setMenuName(index);
                            indexMenu.setState("2");//第二级菜单标识
                            indexMenus.add(indexMenu);
                        }
                    }catch(Exception e){
                        logger.error(pattern+"未找到index");
                    }

                }
                //父子关系
                templateMenu.setMenus(indexMenus);
                listMenu.add(templateMenu);
            }
        }
        TreeBuilder treeBuilder = new TreeBuilder(listMenu);
        String menuTree=treeBuilder.buildJSONTree();
        return menuTree;
    }

    /**
     * mapping对象格式化
     * @param sourceMap mapping对象列表
     * @return List<Metadata>
     */
    private List<Metadata> getListMetadataBySourceMap(Map<String,Object> sourceMap){
        List<Metadata> metadataList = new ArrayList<Metadata>();
        //获取mapping下的properties
        Map<String,Object> propertiesMap = (Map<String, Object>) sourceMap.get("properties");
        //这里将map转换成list
        List<Map.Entry<String,Object>> list = new ArrayList<Map.Entry<String,Object>>(propertiesMap.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String,Object>>() {
            //升序排序
            public int compare(Map.Entry<String, Object> o1,
                               Map.Entry<String, Object> o2) {
                return o1.getKey().toLowerCase().compareTo(o2.getKey().toLowerCase());
            }
        });
        Metadata meta;
        //遍历properties下的数据 每个元素的key为字段名，value为相关的属性和值
        for(Map.Entry<String, Object> smap:list){
            //获取value
            Map<String,Object> fieldValue = (Map<String, Object>)smap.getValue();
            meta = new Metadata();
            //字段名
            meta.setFieldName(smap.getKey());
            //数据类型
            if(fieldValue.get("type")!=null){
                meta.setFieldType(Metadata.FieldType.fromStr(fieldValue.get("type").toString()));
            }else{
                //不需要进行赋值
            }
            //存储分词器
            if(fieldValue.get("analyzer")!=null){
                meta.setAnalyzer(Metadata.Analyzer.fromStr(fieldValue.get("analyzer").toString()));
            }else{
                //不需要进行赋值
            }
            //查询分词器
            if(fieldValue.get("search_analyzer")!=null){
                meta.setSearchAnalyzer(Metadata.SearchAnalyzer.fromStr(fieldValue.get("search_analyzer").toString()));
            }else{
                //不需要进行赋值
            }
            //是否可聚合
            if(fieldValue.get("fielddata")!=null){
                meta.setFieldData((Boolean)fieldValue.get("fielddata"));
            }
            //格式化
            meta.setFormat(fieldValue.get("format")!=null?fieldValue.get("format").toString():"");
            //raw
            if(fieldValue.get("fields")!=null){
                //raw存在于fields的值中
                Map<String, Object> rawField = (Map<String, Object>)fieldValue.get("fields");
                for(Map.Entry<String, Object> rawEntry:rawField.entrySet()){
                    Map<String, Object> raw = (Map<String, Object>)rawEntry.getValue();
                    //数据类型
                    if(raw.get("type")!=null){
                        meta.setRawType(Metadata.RawType.fromStr(raw.get("type").toString()));
                    }else{
                        //不需要进行赋值
                    }
                    //限制长度
                    if(raw.get("ignore_above")!=null){
                        meta.setRawIgnoreAbove((Integer)raw.get("ignore_above"));
                    }
                }
            }
            metadataList.add(meta);
        }
        return metadataList;
    }
}

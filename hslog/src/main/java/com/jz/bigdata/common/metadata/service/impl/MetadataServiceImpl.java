package com.jz.bigdata.common.metadata.service.impl;

import com.hs.elsearch.dao.logDao.ILogIndexDao;
import com.jz.bigdata.common.metadata.entity.Metadata;
import com.jz.bigdata.common.metadata.entity.TableHeader;
import com.jz.bigdata.common.metadata.service.IMetadataService;
import com.jz.bigdata.roleauthority.menu.entity.Menu;
import com.jz.bigdata.roleauthority.menu.util.TreeBuilder;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.elasticsearch.client.indices.IndexTemplateMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            return getAllListMetadataBySourceMap(sourceMap);
        }else{
            return null;
        }
    }



    @Override
    public List<Metadata> getIndexMapping(String indexName) throws Exception {
        List<Metadata> metadataList = new ArrayList<Metadata>();
        //获取template的数据
        Map<String,MappingMetaData> mapMapping = logIndexDao.getIndexMappingData(indexName);
        //获取MappingMetaData
        MappingMetaData mapping = mapMapping.get(indexName);
        if(mapping!=null){
            //获取mapping信息
            Map<String,Object> sourceMap = mapping.getSourceAsMap();
            return getAllListMetadataBySourceMap(sourceMap);
        }else{
            return null;
        }
    }



    @Override
    public List<Metadata> getTemplateMappingDynamically(String templateName, String id) throws Exception {
        //获取template的数据
        List<IndexTemplateMetaData> list = logIndexDao.getTemplateData(templateName);
        //由于API仅提供类似正则匹配的模糊查询，返回的都是list，但是对于本功能而言，需要返回唯一一条记录
        if(list.size()==1){
            IndexTemplateMetaData temp = list.get(0);
            //获取mapping信息
            MappingMetaData mmd = temp.mappings();
            //获取mapping信息
            Map<String, Object> sourceMap = mmd.getSourceAsMap();
            return getListMetadataBySourceMapDynamically(sourceMap,id);
        }else{
            return null;
        }
    }
    @Override
    public List<Metadata> getIndexMappingDynamically(String indexName, String id) throws Exception {
        List<Metadata> metadataList = new ArrayList<Metadata>();
        //获取template的数据
        Map<String,MappingMetaData> mapMapping = logIndexDao.getIndexMappingData(indexName);
        //获取MappingMetaData
        MappingMetaData mapping = mapMapping.get(indexName);
        if(mapping!=null){
            //获取mapping信息
            Map<String,Object> sourceMap = mapping.getSourceAsMap();
            return getListMetadataBySourceMapDynamically(sourceMap,id);
        }else{
            return null;
        }
    }
    @Override
    public String getIndexTree(String es_tempalatePattern) throws Exception {
       List<Menu> listMenu = new ArrayList<>();
        //获取所有template的数据
        List<IndexTemplateMetaData> list = logIndexDao.getTemplateData("*");
        Collections.sort(list,new Comparator<IndexTemplateMetaData>() {
            //升序排序
            public int compare(IndexTemplateMetaData o1,
                               IndexTemplateMetaData o2) {
                return o1.name().toLowerCase().compareTo(o2.name().toLowerCase());
            }
        });
        //遍历template
        for(IndexTemplateMetaData template:list){
            //将所有的template进行筛选，只选择
            //TODO 加上beat的筛选，后期需要进行
            if(template.name().indexOf(es_tempalatePattern.replace("*",""))>=0||template.name().indexOf("beat-")>=0){
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
                        //倒序排列
                        Collections.reverse(Arrays.asList(indices));
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
     * 根据mapping的properties信息，以及路径信息，获取该节点下子节点的信息
     * @param sourceMap mapping的properties信息
     * @param id 为属性所在的路径.如fields.ip  fields.equipmentid
     * @return
     */
    private List<Metadata> getListMetadataBySourceMapDynamically(Map<String,Object> sourceMap,String id){
        List<Metadata> metadataList = new ArrayList<Metadata>();
        Map<String,Object> map = sourceMap;
        //根据id获取字段路径，从原始mapping中获取前端点击的节点的下一级信息
        if(null!=id&&""!=id){
            //将路径进行拆分
            String[] path = id.split("\\.");
            //遍历path中的各个节点，分级深入，获取最终一个节点的信息
            for(int i=0;i<path.length;i++){
                map = (Map<String,Object>)((Map<String, Object>) map.get("properties")).get(path[i]);
            }
        }

        //这里将map的properties信息转换成list
        List<Map.Entry<String,Object>> list = new ArrayList<Map.Entry<String,Object>>(((Map<String, Object>)map.get("properties")).entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String,Object>>() {
            //升序排序
            public int compare(Map.Entry<String, Object> o1,
                               Map.Entry<String, Object> o2) {
                return o1.getKey().toLowerCase().compareTo(o2.getKey().toLowerCase());
            }
        });
        Object fieldType;//字段类型
        //遍历properties下的数据 每个元素的key为字段名，value为相关的属性和值
        for(Map.Entry<String, Object> fieldMap:list){
            Metadata meta = new Metadata();
            //组装id，根据路径以.隔开 如field.id field.equipment
            meta.setId(((null!=id&&""!=id)?(id+"."):"")+fieldMap.getKey());
            meta.setFieldName(fieldMap.getKey());
            //获取value
            Map<String,Object> fieldValue = (Map<String, Object>)fieldMap.getValue();
            //数据类型
            fieldType = fieldValue.get("type");
            if(fieldType!=null){
                //meta.setFieldType(Metadata.FieldType.fromStr(fieldValue.get("type").toString()));
                meta.setFieldType(fieldType.toString());
                //是否可聚合,fielddata=true/keyword/ip/date
                if((fieldValue.get("fielddata")!=null&&"true".equals(fieldValue.get("fielddata"))||(fieldType!=null&&("keyword".equals(fieldType.toString())||"ip".equals(fieldType.toString())||"date".equals(fieldType.toString()))))){
                    meta.setFieldData("是");
                }else{
                    meta.setFieldData("否");
                }
            }else{
                //该字段没有类型，不存储数据，不具备是否可聚合的属性
                meta.setFieldData("");
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
                        //meta.setRawType(Metadata.RawType.fromStr(raw.get("type").toString()));
                        meta.setRawType(raw.get("type").toString());
                    }else{
                        //不需要进行赋值
                    }
                    //限制长度
                    if(raw.get("ignore_above")!=null){
                        meta.setRawIgnoreAbove((Integer)raw.get("ignore_above"));
                    }
                }
            }
            //是否还有子节点
            if(fieldValue.get("properties")!=null){
                meta.setHasChildren(true);
            }else{
                meta.setHasChildren(false);
            }
            metadataList.add(meta);
        }
        return metadataList;
    }




    /**
     * mapping对象格式化
     * @param sourceMap mapping对象列表
     * @return List<Metadata>
     */
    private List<Metadata> getAllListMetadataBySourceMap(Map<String,Object> sourceMap){
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
        //遍历properties下的数据 每个元素的key为字段名，value为相关的属性和值
        for(Map.Entry<String, Object> smap:list){
            //递归  每一个元素都是一个metadata
            metadataList.add(getChildren(smap));
        }

        return metadataList;
    }

    /**
     * 暂不使用
     * 递归模块，循环遍历节点下的信息
     * @param map
     * @return
     */
    private Metadata getChildren(Map.Entry<String, Object> map){
        //每一个节点都是一个metadata
        Metadata meta = new Metadata();
        meta.setId(UUID.randomUUID().toString());
        //metadata的children
        List<Metadata> metaChildren = new ArrayList<>();
        //获取value
        Map<String,Object> fieldValue = (Map<String, Object>)map.getValue();
        //获取key
        String fieldName = map.getKey();
        meta.setFieldName(fieldName);

        //尝试获取其properties属性
        Object properties = fieldValue.get("properties");
        if(properties!=null){
            /**
             * 如果存在properties，认为这个节点是下一级目录，并不是数据存储字段
             * 因此只作为一个父级节点来处理，继续找子节点
             */
            List<Map.Entry<String,Object>> list = new ArrayList<Map.Entry<String,Object>>(((Map<String, Object>)properties).entrySet());
            Collections.sort(list,new Comparator<Map.Entry<String,Object>>() {
                //升序排序
                public int compare(Map.Entry<String, Object> o1,
                                   Map.Entry<String, Object> o2) {
                    return o1.getKey().toLowerCase().compareTo(o2.getKey().toLowerCase());
                }
            });

            //遍历properties下的数据 每个元素的key为字段名，value为相关的属性和值
            for(Map.Entry<String, Object> smap:list) {
                metaChildren.add(getChildren(smap));
            }
            meta.setChildren(metaChildren);
        }else{
            /**
             * 如果不存在，则说明该字段是最终节点，解析信息，并进行赋值
             */

            meta.setFieldName(fieldName);
            //数据类型
            if(fieldValue.get("type")!=null){
                //meta.setFieldType(Metadata.FieldType.fromStr(fieldValue.get("type").toString()));
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
                //meta.setFieldData((Boolean)fieldValue.get("fielddata"));
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
                        //meta.setRawType(Metadata.RawType.fromStr(raw.get("type").toString()));
                    }else{
                        //不需要进行赋值
                    }
                    //限制长度
                    if(raw.get("ignore_above")!=null){
                        meta.setRawIgnoreAbove((Integer)raw.get("ignore_above"));
                    }
                }
            }
        }
        return meta;
    }
    /**
     * 暂时不再使用
     * mapping对象格式化
     * @param sourceMap mapping对象列表
     * @return List<Metadata>
     */
    private List<Metadata> _getListMetadataBySourceMap(Map<String,Object> sourceMap){
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
                //meta.setFieldType(Metadata.FieldType.fromStr(fieldValue.get("type").toString()));
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
                //meta.setFieldData((Boolean)fieldValue.get("fielddata"));
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
                        //meta.setRawType(Metadata.RawType.fromStr(raw.get("type").toString()));
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

    /**
     * 生成自定义表头信息
     * @return
     */
    private static List<TableHeader> getTableHeader(){
        List<TableHeader> tableHeaders = new ArrayList<>();
        tableHeaders.add(new TableHeader("fieldName","字段名","",200,null));
        tableHeaders.add(new TableHeader("fieldType","数据类型","toLowcase",null,null));
        tableHeaders.add(new TableHeader("fieldData","是否可聚合","changeVal",null,null));
        tableHeaders.add(new TableHeader("format","格式","",null,null));
        tableHeaders.add(new TableHeader("analyzer","存储分词器","toLowcase",null,null));
        tableHeaders.add(new TableHeader("searchAnalyzer","查询分词器","toLowcase",null,null));
        List<TableHeader> tableHeaderRaw = new ArrayList<>();
        tableHeaderRaw.add(new TableHeader("rawType","数据类型","toLowcase",null,null));
        tableHeaderRaw.add(new TableHeader("rawIgnoreAbove","限制长度","toEmpty",null,null));

        tableHeaders.add(new TableHeader("Raw","Raw","toLowcase1",null,tableHeaderRaw));
        return tableHeaders;
    }
    public static void main(String[] args){
        System.out.println(JSONArray.fromObject(getTableHeader()).toString());

    }
}

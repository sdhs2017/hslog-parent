package com.jz.bigdata.common.fileLog.service.impl;

import com.hs.elsearch.dao.common.ICommonDao;
import com.hs.elsearch.dao.logDao.ILogIndexDao;
import com.jz.bigdata.business.logAnalysis.collector.service.ICollectorService;
import com.jz.bigdata.business.logAnalysis.log.service.IlogService;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.fileLog.dao.IFileLogDao;
import com.jz.bigdata.common.fileLog.entity.FileLogField;
import com.jz.bigdata.common.fileLog.service.IFileLogService;
import com.jz.bigdata.common.start_execution.cache.FileLogCache;
import com.jz.bigdata.util.ConfigProperty;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/1/4 15:50
 * @Description:
 */
@Slf4j
@Service(value = "FileLogService")
public class FileLogServiceImpl implements IFileLogService {
    @Autowired
    protected ICommonDao commonDao;
    @Resource(name ="configProperty")
    private ConfigProperty configProperty;
    @Autowired
    protected ILogIndexDao logIndexDao;
    @Resource(name="logService")
    private IlogService logService;
    @Resource
    private IFileLogDao fileLogDao;
    @Resource(name = "CollectorService")
    private ICollectorService collectorService;
    @Override
    public boolean reindex(List<FileLogField> newFieldList,String file_log_templateKey,String file_log_templateName) throws Exception {
        //1.停止filebeat
        collectorService.stopFileLogKafkaListener();

        //2.组装mapping，以及reindex所需的script
        //script，设置reindex逻辑，将source index字段与target index字段进行对应
        StringBuffer scriptString = new StringBuffer();
        String template_name = "";
        StringBuffer templateMapping = new StringBuffer();
        templateMapping.append("{\"properties\":{\"@timestamp\":{\"type\":\"date\"},");
        if(newFieldList!=null&&newFieldList.size()>0){
            //组装template名称,必须全小写
            template_name = (configProperty.getEs_filelog_pre()+newFieldList.get(0).getFile_log_templateKey()+"_").toLowerCase();

            //组装tempalte mapping 以及reindex需要的script
            for(FileLogField fileLogField :newFieldList){
                //组装templateMapping
                //date类型需要加format
                if("date".equals(fileLogField.getFile_log_type())){
                    templateMapping.append("\""+ fileLogField.getFile_log_field()+"\":{\"type\":\""+ fileLogField.getFile_log_type()+"\",\"format\":\""+ fileLogField.getFile_log_format()+"\"},");
                }else{
                    templateMapping.append("\""+ fileLogField.getFile_log_field()+"\":{\"type\":\""+ fileLogField.getFile_log_type()+"\"},");
                }

                //组装script,根据原fields信息与新fields信息，通过order 一一对应
                //如果某字段被标记为日期字段，需要scripts特殊处理
                if("true".equals(fileLogField.getFile_log_is_timestamp())){
                    //定义原数据的时间格式
                    scriptString.append("def sf = new SimpleDateFormat(\""+ fileLogField.getFile_log_format()+"\");");
                    scriptString.append("def dt = sf.parse(ctx._source.field"+ fileLogField.getFile_log_order()+");");
                    //新日期的时间格式
                    scriptString.append("def sf_new = new SimpleDateFormat(\"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'\");");
                    //写入，该字段为系统默认时间戳，字段名称为@timestamp，默认为系统当前时间，如果用户设置了数据的其他date类型字段为时间戳，
                    //则需要将设置的时间戳字段的数据写入到@timestamp中，并且进行格式转换
                    scriptString.append("ctx._source['@timestamp'] = sf_new.format(dt);");
                }
                //同时，原字段仍要保留
                scriptString.append("ctx._source."+ fileLogField.getFile_log_field()+" = ctx._source.remove(\"field"+ fileLogField.getFile_log_order()+"\");");
            }
        }
        //template收尾处理
        templateMapping.delete(templateMapping.length()-1,templateMapping.length());
        templateMapping.append("}}");
        //3.创建template
        //template setting信息
        Map<String, Object> settingmap = new HashMap<>();
        // 索引最大查询条数
        settingmap.put("index.max_result_window", configProperty.getEs_max_result_window());
        // 索引分片数，已经创建的索引不能更改，适用于新的索引
        settingmap.put("index.number_of_shards", configProperty.getEs_number_of_shards());
        // 索引副本数，副本数有助于提高查询效率，前提是有相对应的elasticsearch节点
        settingmap.put("index.number_of_replicas", configProperty.getEs_number_of_replicas());
        // 索引的生命周期管理
        settingmap.put("index.lifecycle.name", "hs_policy");
        // 默认1000，数据字段过多，需要调整配置
        settingmap.put("mapping.total_fields.limit", configProperty.getEs_mapping_total_fields_limit());
        //创建template
        logService.initOfElasticsearch(template_name,template_name+"*",null,settingmap,templateMapping.toString());
        //4.reindex
        //生成script对象
        Script reindexScript = new Script(ScriptType.INLINE, "painless", scriptString.toString(), new HashMap<String, Object>());
        //将index设置为不允许写
        //PUT test_source03/_settings
        //{
        //  "index": {
        //    "blocks": {
        //      "write":true
        //    }
        //  }
        //}
        //index.block.read_only 及index.blocks.read_only_allow_delete 为ES为硬盘使用超过阈值（默认95%）时设置，
        //特别时index.blocks.read_only_allow_delete属性，当手动设置为true时，ES硬盘使用正常，会自动删除该属性（相当于false）。
        Settings onlyReadSetting =
                Settings.builder()
                        .put("blocks.write", true)//index.blocks.write 设置为true，不允许写数据
                        .build();
        //取消index的只读
        Settings cancelOnlyReadSetting =
                Settings.builder()
                        .put("blocks.write", false)
                        .build();
        //根据template名称获取已收取数据的N个index
        String[] indices = logIndexDao.getIndices(template_name+"*");
        //遍历N个index
        for(int i=0;i<indices.length;i++){
            String source_index = indices[i];
            //1.设置为只读
            boolean writeResult = commonDao.updateIndexSettings(onlyReadSetting,source_index);
            if(writeResult){
                //2.reindex到临时index
                boolean result_tmp = commonDao.reindex(null,source_index+"_tmp",source_index);

                if(result_tmp){//执行成功
                    //3.删除原index
                    boolean deleteSourceIndexResult = commonDao.deleteIndices(source_index);
                    //如果删除失败，直接返回
                    if(!deleteSourceIndexResult){
                        log.error("删除index:"+source_index+"失败！");
                        //撤销index只读
                        commonDao.updateIndexSettings(cancelOnlyReadSetting,source_index);
                        return false;
                    }else{
                        //4.字段信息变化带来的reindex
                        boolean result = commonDao.reindex(reindexScript,source_index,source_index+"_tmp");
                        //5.reindex是否成功
                        if(result){
                            //删除tmp index
                            boolean deleteTmpIndexResult = commonDao.deleteIndices(source_index+"_tmp");
                            //如果删除失败，直接返回
                            if(!deleteTmpIndexResult){
                                //将前面的reindex及删除功能还原
                                commonDao.deleteIndices(source_index);//删除target index
                                commonDao.deleteTemplate(template_name);//删除模板
                                commonDao.reindex(null,source_index,source_index+"_tmp");//
                                log.error("删除index:"+source_index+"_tmp失败！");
                                //基本不会出现失败
                                return false;
                            }else{
                                //删除成功，不做任何操作
                            }
                        }else{
                            //reindex失败，将tmpindex重新还原到原index
                            commonDao.reindex(null,source_index,source_index+"_tmp");
                            commonDao.deleteIndices(source_index+"_tmp");
                            log.error("将index："+source_index+"_tmp   reindex到最终index时失败！");
                            //基本不会出现失败
                            return false;
                        }
                    }
                }else{
                    log.error("将index："+source_index+"reindex到临时index时失败！");
                    //撤销index只读
                    commonDao.updateIndexSettings(cancelOnlyReadSetting,source_index);
                    return false;
                }

            }else{
                log.error("index:"+source_index+"设置为只读失败！");
                return false;
            }

        }

        //5.判断更新是否成功，更新数据库,上面代码未return，证明执行成功
        //实现事务，先删除再insert
        updateFieldsList(newFieldList,file_log_templateKey);
        //启动file-log beat
        collectorService.startFileLogKafkaListener();
        //无异常返回
        return true;
    }

    @Override
    public List<FileLogField> getTemplateInfo(String file_log_templateKey) {
        return fileLogDao.getTemplateInfo(file_log_templateKey);
    }

    @Override
    public List<FileLogField> getTemplateList() {
        return fileLogDao.getTemplateList();
    }

    @Override
    public int update(FileLogField fileLogField) {
        return fileLogDao.update(fileLogField);
    }

    @Override
    public boolean updateTemplateName(String file_log_templateKey, String file_log_templateName) {
        int result = fileLogDao.updateTemplateName(file_log_templateKey,file_log_templateName);
        if(result>0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
    public boolean updateFieldsList(List<FileLogField> list,String file_log_templateKey) {
        fileLogDao.delete(file_log_templateKey);
        //新增时 需要补上日期字段
        FileLogField fileLogField_timestamp = new FileLogField();
        fileLogField_timestamp.setFile_log_templateName(list.size()>0?list.get(0).getFile_log_templateName():file_log_templateKey);
        fileLogField_timestamp.setFile_log_templateKey(file_log_templateKey);
        fileLogField_timestamp.setFile_log_field("@timestamp");
        fileLogField_timestamp.setFile_log_text("日期");
        fileLogField_timestamp.setFile_log_type("date");
        fileLogField_timestamp.setFile_log_order(0);
        list.add(fileLogField_timestamp);
        fileLogDao.insertList(list);
        //更新cache
        FileLogCache.INSTANCE.getFileLogList().put(file_log_templateKey,list);
        return true;
    }

    @Override
    public Map<String, List<FileLogField>> getFileLogInfo() {
        Map<String, List<FileLogField>> result = new HashMap<>();
        //获取文件日志模板列表
        List<FileLogField> templateList = fileLogDao.getTemplateList();
        for(FileLogField fileLogField:templateList){
            //根据模板key获取字段信息
            List<FileLogField> fieldList = fileLogDao.getTemplateInfo(fileLogField.getFile_log_templateKey());
            result.put(fileLogField.getFile_log_templateKey(),fieldList);
        }
        return result;
    }

    @Override
    public boolean insert(List<FileLogField> list) {
        int result = fileLogDao.insertList(list);
        if(result>0){
            //插入成功，插入cache
            FileLogCache.INSTANCE.getFileLogList().put(list.get(0).getFile_log_templateKey(),list);
            return true;
        }else{
            return false;
        }
    }

    /**
     * 在遍历index进行reindex过程中，如果第2-n个index出现reindex失败的情况，则需要将已经reindex的index进行还原
     * @param i
     * @param indices
     * @return
     */
    private boolean restoreIndices(int i,String[] indices,String file_log_templateKey) throws Exception {

        //i为出错时的下标。大于0则进行还原操作
        if(i>0){
            //获取原字段信息
            List<FileLogField> list = fileLogDao.getTemplateInfo(file_log_templateKey);

            //创建mapping信息
            StringBuffer indexMapping = new StringBuffer();
            indexMapping.append("{\"properties\":{\"@timestamp\":{\"type\":\"date\"},");
            //script，设置reindex逻辑，将source index字段与target index字段进行对应
            StringBuffer scriptString = new StringBuffer();
            //遍历，组装 mapping信息 及所需script
            for(FileLogField fileLogField:list){
                //组装templateMapping
                //date类型需要加format
                if("date".equals(fileLogField.getFile_log_type())){
                    indexMapping.append("\""+ fileLogField.getFile_log_field()+"\":{\"type\":\""+ fileLogField.getFile_log_type()+"\",\"format\":\""+ fileLogField.getFile_log_format()+"\"},");
                }else{
                    indexMapping.append("\""+ fileLogField.getFile_log_field()+"\":{\"type\":\""+ fileLogField.getFile_log_type()+"\"},");
                }

                //组装script,根据原fields信息与新fields信息，通过order 一一对应
                //如果某字段被标记为日期字段，需要scripts特殊处理
                if("true".equals(fileLogField.getFile_log_is_timestamp())){
                    //定义原数据的时间格式
                    scriptString.append("def sf = new SimpleDateFormat(\""+ fileLogField.getFile_log_format()+"\");");
                    scriptString.append("def dt = sf.parse(ctx._source.field"+ fileLogField.getFile_log_order()+");");
                    //新日期的时间格式
                    scriptString.append("def sf_new = new SimpleDateFormat(\"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'\");");
                    //写入，该字段为系统默认时间戳，字段名称为@timestamp，默认为系统当前时间，如果用户设置了数据的其他date类型字段为时间戳，
                    //则需要将设置的时间戳字段的数据写入到@timestamp中，并且进行格式转换
                    scriptString.append("ctx._source['@timestamp'] = sf_new.format(dt);");
                }
                //同时，原字段仍要保留
                scriptString.append("ctx._source."+ fileLogField.getFile_log_field()+" = ctx._source.remove(\"field"+ fileLogField.getFile_log_order()+"\");");
            }
            //生成script对象
            Script reindexScript = new Script(ScriptType.INLINE, "painless", scriptString.toString(), new HashMap<String, Object>());
            //template收尾处理
            indexMapping.delete(indexMapping.length()-1,indexMapping.length());
            indexMapping.append("}}");
            for(int j=0;j<i;j++){
                String indexName = indices[j];
                //reindex到临时index
                commonDao.reindex(null,indexName+"_tmp",indexName);
                //删除原index
                commonDao.deleteIndices(indexName);
                //创新targetindex
                commonDao.createIndex(indexName,null,indexMapping.toString());
                //reindex 到新创建的index
                commonDao.reindex(reindexScript,indexName+"_tmp",indexName);
            }
        }
        return true;
    }

}

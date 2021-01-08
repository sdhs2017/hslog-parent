package com.jz.bigdata.common.fileLog.service.impl;

import com.hs.elsearch.dao.common.ICommonDao;
import com.hs.elsearch.dao.flowDao.IFlowSearchDao;
import com.hs.elsearch.dao.logDao.ILogIndexDao;
import com.jz.bigdata.common.fileLog.entity.FileLogFields;
import com.jz.bigdata.common.fileLog.service.IFileLogService;
import com.jz.bigdata.util.ConfigProperty;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service(value = "FileLogService")
public class FileLogServiceImpl implements IFileLogService {
    @Autowired
    protected ICommonDao commonDao;
    @Resource(name ="configProperty")
    private ConfigProperty configProperty;
    @Autowired
    protected ILogIndexDao logIndexDao;
    @Override
    public void reindex(List<FileLogFields> newFieldList) throws Exception {
        //TODO  1.停止filebeat，将当前index设置为不允许写

        //2.查询出原fields信息
        List<FileLogFields> oldFieldsList = new ArrayList<>();
        //模拟数据
        FileLogFields f1 = new FileLogFields();
        f1.setFileLog_key("WCM_LOG");
        f1.setFileLog_Name("WCM系统日志");
        f1.setFileLog_field("field1");
        f1.setFileLog_text("字段1");
        f1.setFileLog_type("text");
        f1.setFileLog_order(1);
        oldFieldsList.add(f1);
        FileLogFields f2 = new FileLogFields();
        f2.setFileLog_key("WCM_LOG");
        f2.setFileLog_Name("WCM系统日志");
        f2.setFileLog_field("field2");
        f2.setFileLog_text("字段2");
        f2.setFileLog_type("text");
        f2.setFileLog_order(2);
        oldFieldsList.add(f2);
        //3.组装mapping，以及reindex所需的script
        //script，设置reindex逻辑，将source index字段与target index字段进行对应
        StringBuffer scriptString = new StringBuffer();
        String template_name = "";
        String index_name = "";
        StringBuffer templateMapping = new StringBuffer();
        if(newFieldList!=null&&newFieldList.size()>0){
            //组装template名称
            template_name = configProperty.getEs_filelog_pre()+newFieldList.get(0).getFileLog_key()+"_";
            //组装tempalte mapping 以及reindex需要的script
            for(FileLogFields fileLogFields:newFieldList){
                //TODO 组装templateMapping
                //TODO 组装script,根据原fields信息与新fields信息，通过order 一一对应
                //TODO 判断是否为日期字段，专门处理
            }
        }
        Script reindexScript = new Script(ScriptType.INLINE, "painless", scriptString.toString(), new HashMap<String, Object>());
        //4.创建template

        //5.reindex

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
        //根据template名称获取已收取数据的N个index
        String[] indices = logIndexDao.getIndices(template_name+"*");
        //遍历N个index
        for(String source_index:indices){
            //1.设置为只读
            commonDao.updateIndexSettings(onlyReadSetting,source_index);
            //2.reindex到临时index
            commonDao.reindex(null,source_index+"_tmp",source_index);
            //3.删除原index
            commonDao.deleteIndices(source_index);
            //4.type变化带来的reindex
            commonDao.reindex(reindexScript,source_index,source_index+"_tmp");
            //5.删除tmp inex
            commonDao.deleteIndices(source_index+"_tmp");
        }

        //6.判断更新是否成功，更新数据库




        //script，设置reindex逻辑，将source index字段与target index字段进行对应
//        StringBuffer scriptString = new StringBuffer();
//        String[] targetFields = {"LOGID","LOGTYPE","LOGDESC","LOGOBJNAME","LOGOPTYPE","LOGRESULT","LOGOPTIME","LOGUSER","LOGUSERIP","EXECTIME","LOGOBJID","STIMEMILLIS","ETIMEMILLIS"};
//        for(int i=1;i<=targetFields.length;i++){
//            scriptString.append("ctx._source."+targetFields[i-1]+" = ctx._source.remove(\"field"+i+"\");");
//        }

//        Script script = new Script(ScriptType.INLINE, "painless", scriptString.toString(), new HashMap<String, Object>());
//        //进行reindex
//        commonDao.reindex(script,target_index,source_index);
//        //删除原index
//        commonDao.deleteIndices(source_index);
    }
}

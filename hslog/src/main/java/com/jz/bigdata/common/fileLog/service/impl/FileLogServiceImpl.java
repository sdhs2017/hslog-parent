package com.jz.bigdata.common.fileLog.service.impl;

import com.hs.elsearch.dao.common.ICommonDao;
import com.hs.elsearch.dao.flowDao.IFlowSearchDao;
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
    @Override
    public void reindex(List<FileLogFields> list) throws Exception {
        //1.停止filebeat，将当前index设置为不允许写
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
        Settings settings =
                Settings.builder()
                        .put("blocks.write", true)//index.blocks.write 设置为true，不允许写数据
                        .build();
        //commonDao.updateIndexSettings(settings,source_index);
        //TODO 停止filebeat

        //2.查询出原fields信息
        List<FileLogFields> oldFieldsList = new ArrayList<>();

        //3.组装mapping，以及reindex所需的script
        //script，设置reindex逻辑，将source index字段与target index字段进行对应
        StringBuffer scriptString = new StringBuffer();
        String template_name = "";
        String index_name = "";
        StringBuffer templateMapping = new StringBuffer();
        if(list!=null&&list.size()>0){
            //组装template名称
            template_name = configProperty.getEs_filelog_pre()+list.get(0).getFileLog_key()+"_";
            //组装tempalte mapping 以及reindex需要的script
            for(FileLogFields fileLogFields:list){
                //TODO 组装templateMapping
                //TODO 组装script,根据原fields信息与新fields信息，通过order 一一对应
                //TODO 判断是否为日期字段，专门处理
            }

            //TODO 根据template名称获取已收取数据的N个index
            //TODO 再根据index分别执行reindex

        }
        //4.创建template

        //5.reindex

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

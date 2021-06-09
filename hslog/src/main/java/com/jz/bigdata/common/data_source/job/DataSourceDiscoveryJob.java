package com.jz.bigdata.common.data_source.job;

import com.google.common.base.Strings;
import com.jz.bigdata.business.data_sec_govern.label.dao.IDSGLabelDao;
import com.jz.bigdata.business.data_sec_govern.label.entity.FieldLabelRelation;
import com.jz.bigdata.business.data_sec_govern.label.entity.Label;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.data_source.dao.IDataSourceDao;
import com.jz.bigdata.common.data_source.entity.DataSource;
import com.jz.bigdata.common.data_source.service.IDataSourceService;
import com.jz.bigdata.common.data_source_metadata.dao.IDataSourceMetadataDao;
import com.jz.bigdata.common.data_source_metadata.entity.DataSourceSample;
import com.jz.bigdata.common.data_source_metadata.entity.MetadataDatabase;
import com.jz.bigdata.common.data_source_metadata.entity.MetadataField;
import com.jz.bigdata.common.data_source_metadata.entity.MetadataTable;
import com.jz.bigdata.util.DruidUtil;
import com.jz.bigdata.util.Pattern_Matcher;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Author: yiyang
 * @Date: 2021/4/28 14:17
 * @Description:敏感数据发现计划任务。
 * 数据发现过程一般会比较长，因此通过quartz的计划任务实现。设置为仅执行一次即可。
 */
@Slf4j
public class DataSourceDiscoveryJob implements Job {
    //批量insert数据条数大小
    private final int batch_size=1000;
    @Autowired
    protected IDataSourceDao dataSourceDao;
    @Autowired
    protected IDataSourceMetadataDao dataSourceMetadataDao;
    @Resource(name = "DataSourceService")
    private IDataSourceService dataSourceService;
    @Autowired
    protected IDSGLabelDao labelDao;
    //时间戳格式
    private static final DateTimeFormatter dtf_time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try{
            //获取参数
            JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
            //将参数转成map
            Map<String,Object> param_map = jobDataMap.getWrappedMap();
            String data_source_ids = param_map.get("data_source_ids").toString();//数据源id
            String[] ids = data_source_ids.split(",");
            //数据源下的库
            List<MetadataDatabase> databaseList;
            //取库下的表
            List<MetadataTable> tableList;
            //表下的字段+样本数据
            List<MetadataField> fieldListWithSample;
            //样本数据列表
            List<DataSourceSample> sampleList;
            //正则匹配计数器
            Map<String,Integer> matchResult;
            //字段标签关系表，入库list
            List<FieldLabelRelation> relationList;
            //查询标签数据（需进行正则匹配标签）,默认查询所有标签
            List<Label> labelList = labelDao.searchLabel(new Label());

            //遍历数据源
            for(String data_source_id:ids){
                try{
                    //删除原关系数据
                    labelDao.deleteRelationsByDataSourceId(data_source_id);
                    //初始化入库列表
                    relationList= new ArrayList<>();
                    //获取数据源下的库，并进行遍历
                    databaseList = dataSourceMetadataDao.getMetadataDatabaseList_discovery(data_source_id);
                    for(MetadataDatabase database : databaseList){
                        //获取库下的表，并进行遍历
                        tableList = dataSourceMetadataDao.getMetadataTableList_discovery(data_source_id,database.getMetadata_database_name());
                        for(MetadataTable table:tableList){
                            try{
                                System.out.println(data_source_id+"-"+database.getMetadata_database_name()+"-"+table.getMetadata_table_name());
                                //1.通过数据源id获取所有字段信息，字段信息中冗余了库、表信息，以及字段信息下对应的样本数据
                                fieldListWithSample = dataSourceMetadataDao.getFieldSampleData(data_source_id,database.getMetadata_database_name(),table.getMetadata_table_name(),"1");
//                                String data_source_id_temp = "4550aa7f-447d-4803-a740-b90103c66905";
//                                String dbname = "SYS";
//                                String tablename = "OPATCH_XINV_TAB";
//                                fieldListWithSample = dataSourceMetadataDao.getFieldSampleData(data_source_id_temp,dbname,tablename);
                                //2.遍历至字段，根据数据源id、库、表、字段 向已经持久化的样本数据中查询数据
                                for(MetadataField metadataField:fieldListWithSample){
                                    //获取字段下的样本数据
                                    sampleList = metadataField.getSampleList();
                                    //3.正则匹配（目前仅有正则方式匹配，其他方式待开发）
                                    //定义匹配度的存储对象，key 正则标签id，value 匹配成功次数
                                    matchResult = new HashMap<>();
                                    //遍历样本数据
                                    for(DataSourceSample dataSourceSample:sampleList){
                                        //遍历正则标签
                                        for(Label label:labelList){
                                            //匹配成功
                                            if(Pattern_Matcher.isMatched(dataSourceSample.getField_content(),label.getLabel_discover_regex())){
                                                //JAVA8方法 //如果key存在，就调用sum 1 到map的value，如果key不存在，put（key，1）
                                                matchResult.merge(label.getLabel_id(),1,Integer::sum);
                                            }
                                        }
                                    }
                                    //4.计算匹配度，并生成匹配成功列表
                                    //继续遍历标签，分别计算当前字段下，每个标签的匹配度
                                    for (Label label:labelList){
                                        //该标签下有匹配信息
                                        if(matchResult.get(label.getLabel_id())!=null){
                                            //计算匹配度（%）
                                            float percent = matchResult.get(label.getLabel_id())*100/sampleList.size();
                                            //如果匹配度大于等于确认比例
                                            if(percent>=label.getLabel_discover_percent()){
                                                //当前字段需要打上该标签，生成入库对象
                                                FieldLabelRelation relation = new FieldLabelRelation();
                                                relation.setData_source_id(data_source_id);
                                                relation.setMetadata_field_id(metadataField.getMetadata_field_id());
                                                relation.setDsg_label_id(label.getLabel_id());
                                                relation.setDsg_label_name(label.getLabel_name());
                                                relationList.add(relation);
                                                System.out.println(metadataField.getMetadata_database_name()+"--"+metadataField.getMetadata_table_name()+"--"+metadataField.getMetadata_field_name());
                                                System.out.println(label.getLabel_name()+"---"+percent);
                                            }
                                        }

                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    }

                    //数据持久化
                    if(relationList.size()>batch_size){
                        int part = relationList.size()/batch_size;
                        List<FieldLabelRelation> listPage;//分页list
                        for (int i = 0; i < part; i++) {
                            //取前1000条
                            listPage = relationList.subList(0, batch_size);
                            //批量插入
                            labelDao.batchInsertRelation(listPage);
                            //剔除
                            relationList.subList(0, batch_size).clear();
                        }
                    }
                    //如果等于0，sql拼装会出错，导致执行异常
                    if(relationList.size()>0){
                        //插入余数
                        labelDao.batchInsertRelation(relationList);
                    }


                    //更新数据源状态信息,已完成
                    dataSourceDao.updateDiscoveryState(data_source_id,2);
                }catch (Exception e){
                    log.error("敏感数据发现任务执行失败(数据源id"+data_source_id+"):"+e.getMessage());
                    //更新数据源状态信息,已完成
                    dataSourceDao.updateDiscoveryState(data_source_id,3);

                }

            }
        }catch (Exception e){
            log.error("敏感数据发现任务执行失败："+e.getMessage());
        }


    }
}

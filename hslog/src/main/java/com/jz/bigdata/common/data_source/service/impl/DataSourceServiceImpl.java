package com.jz.bigdata.common.data_source.service.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.jz.bigdata.business.data_sec_govern.label.dao.IDSGLabelDao;
import com.jz.bigdata.business.data_sec_govern.label.entity.FieldLabelRelation;
import com.jz.bigdata.business.data_sec_govern.label.entity.Label;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.data_source.dao.IDataSourceDao;
import com.jz.bigdata.common.data_source.entity.DataSource;
import com.jz.bigdata.common.data_source.job.DataSourceDiscoveryJob;
import com.jz.bigdata.common.data_source.service.IDataSourceService;
import com.jz.bigdata.common.data_source_metadata.dao.IDataSourceMetadataDao;
import com.jz.bigdata.common.data_source_metadata.entity.*;
import com.jz.bigdata.common.data_source_metadata.service.IDataSourceMetadataService;
import com.jz.bigdata.common.dictionary.cache.DictionaryCache;
import com.jz.bigdata.common.dictionary.init.DictionaryInit;
import com.jz.bigdata.util.DruidUtil;
import com.jz.bigdata.util.Pattern_Matcher;
import com.jz.bigdata.util.RSAUtil;
import com.mysql.jdbc.StringUtils;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.SQLSyntaxErrorException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Author: yiyang
 * @Date: 2021/2/23 10:29
 * @Description:
 */
@Slf4j
@Service(value = "DataSourceService")
public class DataSourceServiceImpl implements IDataSourceService {
    //
    //多种类型数据库查询出的数据库列名不一致，通过别名统一
    //由于mysql 查询数据库的语句无法设置别名，因此向mysql的名称靠齐
    //private final String DATABASE_ALIAS = "Database";
    //统一表的别名
    //private final String TABLE_ALIAS = "TABLE_NAME";
    //批量insert数据条数大小
    private final int batch_size=1000;
    //样本数据字段最大长度，超过该长度的字段不入库
    private final int MAX_SAMPLE_LENGTH = 100000;
    //时间戳格式
    private static final DateTimeFormatter dtf_time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Autowired
    protected IDataSourceDao dataSourceDao;
    @Autowired
    protected IDataSourceMetadataDao dataSourceMetadataDao;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;
    @Autowired
    protected IDSGLabelDao labelDao;
    @Resource(name = "DataSourceMetadataService")
    private IDataSourceMetadataService dataSourceMetadataService;
    /**
     * 数据源信息保存
     * @param dataSource 基本信息bean
     * @return 保存成功/失败
     */
    @Override
    public boolean save(DataSource dataSource) {
        //唯一ID
        dataSource.setData_source_id(UUID.randomUUID().toString());
        //创建时间
        dataSource.setData_source_create_time(LocalDateTime.now().format(dtf_time));
        //未初始化
        dataSource.setData_source_is_initialized(0);
        //未进行数据发现
        dataSource.setData_source_discovery_state(0);
        int result = dataSourceDao.insert(dataSource);
        if(result>0){
            return true;
        }else{
            return false;
        }
    }
    /**
     * 数据源删除，支持批量删除
     * @param ids 数据源id，以逗号隔开
     * @return 删除成功/失败
     */
    @Override
    public boolean delete(String[] ids) {
        int result = dataSourceDao.deletes(ids);
        //删除成功，关闭druid DataSource
        if(result>0){
            String druid_data_source_name;
            DruidDataSource druidDataSource;
            for(String id:ids){
                druid_data_source_name = DruidUtil.DRUID_DATA_SOURCE_ID+id;
                //查看该datasource是否已存在
                druidDataSource = (DruidDataSource) DruidStatManagerFacade.getInstance().getDruidDataSourceByName(druid_data_source_name);
                //如果存在，则关闭链接
                if(druidDataSource!=null){
                    druidDataSource.close();
                }
            }
            return true;
        }else{
            return false;
        }

    }
    /**
     * 数据源更新，更新时需要将已存在的druid datasource对象关闭
     * @param dataSource 数据源信息
     * @return 更新成功/失败
     */
    @Override
    public boolean update(DataSource dataSource) {
        dataSource.setData_source_update_time(LocalDateTime.now().format(dtf_time));
        int result = dataSourceDao.update(dataSource);
        //更新后需要关闭连接源
        //生成druid datasource名称
        String druid_data_source_name = DruidUtil.DRUID_DATA_SOURCE_ID+dataSource.getData_source_id();
        //查看该datasource是否已存在
        DruidDataSource druidDataSource = (DruidDataSource) DruidStatManagerFacade.getInstance().getDruidDataSourceByName(druid_data_source_name);
        //如果存在，则关闭链接
        if(druidDataSource!=null){
            druidDataSource.close();
        }
        if(result>0){
            return true;
        }else{
            return false;
        }
    }
    /**
     *  获取数据源列表详情
     * @param dataSource 查询条件
     * @return count 总条数/list 分页后的详情数据
     */
    @Override
    public Map<String,Object> getListByCondition(DataSource dataSource) {
        //获取count
        List<List<Map<String, String>>> count = dataSourceDao.getCountByCondition(dataSource);
        //分页后的结果列表
        List<List<Map<String,Object>>> result = dataSourceDao.getListByConditionWithPage(dataSource);
        //存在字典列，即数据库和前端显示不同，进行处理
        for(Map<String,Object> tmp:result.get(0)){
            tmp.put("data_source_is_initialized_label",DictionaryCache.INSTANCE.getDictionaryCache().get(DictionaryInit.DATA_SOURCE_INIT_STATE).get(Integer.parseInt(tmp.get("data_source_is_initialized").toString())).toString());
            tmp.put("data_source_discovery_state_label",DictionaryCache.INSTANCE.getDictionaryCache().get(DictionaryInit.DATA_SOURCE_DISCOVER_STATE).get(Integer.parseInt(tmp.get("data_source_discovery_state").toString())).toString());
        }
        //组装前端需要的数据格式
        Map<String, Object> allMap = new HashMap<>();
        allMap.put("count",count.get(0).get(0).get("count"));
        allMap.put("list",result);
        return allMap;
    }

    /**
     * 通过数据源唯一ID获取数据源详情
     * @param data_source_id
     * @return 数据源详情对象
     */
    @Override
    public DataSource selectDataSourceInfoById(String data_source_id) throws Exception {
        //账号密码加密(RSA)
        DataSource dataSource = dataSourceDao.selectDataSourceInfoById(data_source_id);
        dataSource.setData_source_password(RSAUtil.encrypt(dataSource.getData_source_password(),RSAUtil.getPublicKey()));
        dataSource.setData_source_username(RSAUtil.encrypt(dataSource.getData_source_username(),RSAUtil.getPublicKey()));
        return dataSource;
    }
    /**
     * 测试连接池连接
     * 在此处
     * @param dataSource 数据源基本信息
     * @return 连接成功或失败的json信息
     */
    @Override
    public String checkConnection(DataSource dataSource) {
        try{
            boolean bool = DruidUtil.checkConnection(dataSource);
            if(bool){
                return Constant.successMessage("连接成功！");
            }else{
                return Constant.successMessage("连接失败！");
            }
        } catch (Exception e) {
            log.error("测试连接池连接失败"+e.getMessage());
            return Constant.failureMessage("连接失败！",e.getCause().getMessage());
        }
    }
    /**
     * 获取数据源下的数据库信息,库的信息存放到一个tree节点中。节点信息为前端定义，具体可查看data2treeNode方法
     * @param data_source_id 数据源id
     * @return  tree节点的list，一个tree节点为一个map。key为tree节点所需的属性名，value为值
     * @throws Exception
     */
    @Override
    public List<Map<String, String>> getDataBase(String data_source_id) throws Exception {
        List<Map<String, String>> result = new ArrayList<>();//执行结果再处理后的结果，用户返回
        String sql;//sql语句
        List<Map<String, Object>> sql_result;//sql初始执行结果
        //获取链接信息
        DataSource dataSource = dataSourceDao.selectDataSourceInfoById(data_source_id);
        if(dataSource!=null){
            //根据不同类型获取数据库/实例
            switch(dataSource.getData_source_item_type()){
                case Constant.DATA_SOURCE_ITEM_TYPE_MYSQL:
                    if(Strings.isNullOrEmpty(dataSource.getData_source_dbname())){
                        //数据库为空，查询所有数据库
                        sql = "show databases";
                        sql_result = DruidUtil.executeQuery(dataSource,sql,null);
                        result = data2treeNode(sql_result,Constant.DATA_SOURCE_DATABASE_ALIAS,null,"true");
                    }else{
                        //通过填写的数据库的信息生成节点
                        result = createDatabaseNode(dataSource.getData_source_dbname());
                    }
                    break;
                case Constant.DATA_SOURCE_ITEM_TYPE_SQLSERVER:
                    //sqlserver链接要求必须输入数据库，因此直接返回一级菜单
                    result = createDatabaseNode(dataSource.getData_source_dbname());
                    break;
                case Constant.DATA_SOURCE_ITEM_TYPE_ORACLE:
                    //显示所有用户，oracle 一个用户相当于一个数据库
                    sql = "select USERNAME as \""+Constant.DATA_SOURCE_DATABASE_ALIAS+"\" from dba_users order by USERNAME";
                    sql_result = DruidUtil.executeQuery(dataSource,sql,null);
                    result = data2treeNode(sql_result,Constant.DATA_SOURCE_DATABASE_ALIAS,null,"true");
                    break;
                default:
                    //nothing
                    break;
            }
        }
        return result;
    }
    /**
     * 通过数据库获取表信息,用于动态加载tree
     * @param database 数据库
     * @return map为一个表的tree节点信息，key为tree节点所需属性，value为对应的值
     * @throws Exception
     */
    @Override
    public List<Map<String, String>> getTablesByDatabase_tree(String database,String data_source_id) throws Exception {
        List<Map<String, String>> result = null;
        //获取链接信息
        DataSource dataSource = dataSourceDao.selectDataSourceInfoById(data_source_id);
        Connection connection = null;
        if(dataSource!=null){
            try{
                connection = DruidUtil.getConnection(dataSource);
                if(connection!=null){
                    List<Map<String, Object>> tableList = getTablesByDatabase(database,connection,dataSource.getData_source_item_type());
                    result = data2treeNode(tableList,Constant.DATA_SOURCE_TABLE_ALIAS,database,"false");
                }else{
                    result = new ArrayList<>();
                }
            }catch (Exception e){
                log.error("根据database："+database+"，获取表失败："+e.getMessage());
                result = new ArrayList<>();
            }finally {
                if(connection!=null){
                    connection.close();
                }
            }
        }
        return result;
    }
    /**
     * 获取表的字段信息
     * @param database 数据库
     * @param table 表
     * @param data_source_id 数据源id
     * @return 表的字段详情，一个map代表一个字段的基本信息，key为字段属性（列名，类型，长度，是否为空等），value为值（以OBJ存储）
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> getTableFieldsInfo(String database, String table, String data_source_id) throws Exception {
        List<Map<String, Object>> result = null;
        //获取链接信息
        DataSource dataSource = dataSourceDao.selectDataSourceInfoById(data_source_id);
        if(dataSource!=null){
            //获取连接
            Connection connection = DruidUtil.getConnection(dataSource);
            try{
                if(connection!=null){
                    //获取字段信息列表
                    result = getTableFields(database,table,dataSource.getData_source_item_type(),connection);
                }else{
                    result = new ArrayList<>();
                }
            } catch (Exception e) {
                log.error("获取字段信息失败："+e.getMessage());
                result = new ArrayList<>();
            }finally {
                if(connection!=null){
                    connection.close();
                }
            }
        }
        return result;
    }

    /**
     * 根据data source id 进行数据初始化，将数据库、表、字段信息保存,并根据条件进行自动标识
     * @param data_source_ids 数据源id
     * @param is_auto_identify 是否自动标识
     * @param sample_num 抽样行数
     * @return 返回到前端的json信息
     * @throws Exception
     */
    @Override
    public String initByDataSourceIds(String data_source_ids, String is_auto_identify, String sample_num) throws Exception {
        //前端显示信息
        StringBuffer message = new StringBuffer();
        //拆分多个数据源id
        String[] data_source_id_list =  data_source_ids.split(",");
        //每个数据源分别创建定时任务，分开进行初始化
        for(String data_source_id:data_source_id_list){
            //根据数据源id获取数据源基本信息
            DataSource dataSource = dataSourceDao.selectDataSourceInfoById(data_source_id);
            //状态2为正在初始化，该状态不允许重新初始化，需等待完成
            if(dataSource.getData_source_is_initialized()==2){
                System.out.println("数据源："+dataSource.getData_source_name()+"正在初始化中，无需重复执行！");
                message.append("数据源："+dataSource.getData_source_name()+"正在初始化中，无需重复执行！");
                message.append("<br >");
            }else{
                //创建任务并启动
                Map<String,String> job_result = createJobAndStart(data_source_id,is_auto_identify,sample_num);
                //创建任务并启动成功
                if("true".equals(job_result.get("status"))){
                    //2.更新数据源状态信息
                    //TODO 将数据库状态更新  正在初始化/初始化失败
                    System.out.println("数据源："+dataSource.getData_source_name()+"正在初始化中......");
                    //3.生成初始化任务信息
                    //TODO 添加一条日志，数据库初始化已开始执行。写入开始时间。
                    System.out.println("初始化log：开始时间"+LocalDateTime.now().format(dtf_time));
                    System.out.println("初始化任务创建成功...");
                    System.out.println("初始化任务启动成功...");
                }else{
                    //2.更新数据源状态信息
                    //TODO 将数据库状态更新  正在初始化/初始化失败
                    System.out.println("数据源："+dataSource.getData_source_name()+"初始化失败：");

                    //3.生成初始化任务信息
                    //TODO 添加一条日志，数据库初始化已开始执行。写入开始时间。
                    System.out.println("初始化log：开始时间"+LocalDateTime.now().format(dtf_time));
                    System.out.println(job_result.get("info"));
                }
            }

        }

        return Constant.successMessage(message.toString());
    }

    /**
     * 创建数据源初始化任务并启动
     * @param data_source_id 数据源id
     * @param is_auto_identify 是否自动标识
     * @param sample_num 抽样数据条数
     * @return  返回结果  key1:status:任务是否创建并开始执行 true/false 字符串
     *                   key2:info:如果status为false，需要将错误信息写入到执行日志表中
     */
    private Map<String,String> createJobAndStart(String data_source_id, String is_auto_identify, String sample_num){

        Map<String,String> result = new HashMap<>();
        //1.创建定时任务，只执行一次
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        //参数传递到job线程内
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("data_source_id",data_source_id);
        jobDataMap.put("is_auto_identify",is_auto_identify);
        jobDataMap.put("sample_num",sample_num);
        JobDetail jobDetail = JobBuilder.newJob(DataSourceDiscoveryJob.class)
                .withDescription("数据源初始化任务-仅执行一次")
                .withIdentity(data_source_id+"-onlyOnce", Constant.QUARTZ_JOB_GROUP)
                .usingJobData(jobDataMap)
                .build();
        SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger().withIdentity(data_source_id+"-onlyOnce", Constant.QUARTZ_JOB_GROUP)
                .startAt(new Date())
                //重复执行的次数，因为加入任务的时候马上执行了，所以不需要重复，否则会多一次。
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(3).withRepeatCount(0))
                .build();
        try {
            if(!scheduler.checkExists(JobKey.jobKey(data_source_id+"-onlyOnce",Constant.QUARTZ_JOB_GROUP))){
                scheduler.scheduleJob(jobDetail,simpleTrigger);
            }
            //启动
            scheduler.start();
        } catch (SchedulerException e) {
            log.error("任务启动失败："+e.getMessage());
            result.put("status","false");
            result.put("info","任务启动失败！");
            return result;
        }
        result.put("status","true");
        result.put("info","初始化任务创建并成功启动！");
        return result;
    }
    /**
     * 获取表的字段信息
     * @param database 数据库名
     * @param table 表名
     * @param data_source_item_type 数据库类型
     * @param connection 连接池,需要在外面对connection关闭
     * @return 表的字段详情，一个map代表一个字段的基本信息，key为字段属性（列名，类型，长度，是否为空等），value为值（以OBJ存储）
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> getTableFields(String database, String table,String data_source_item_type, Connection connection) throws Exception {
        List<Map<String, Object>> result;
        String sql_fields;
        Object[] param;
        try{
            //判断数据库类型
            switch(data_source_item_type){
                case Constant.DATA_SOURCE_ITEM_TYPE_MYSQL:
                    sql_fields = "select COLUMN_NAME,DATA_TYPE,CHARACTER_MAXIMUM_LENGTH AS LENGTH,IS_NULLABLE,COLUMN_COMMENT " +
                            "from INFORMATION_SCHEMA.COLUMNS where table_schema = ? and table_name = ? ORDER BY COLUMN_NAME";
                    param = new Object[]{database, table};
                    result = DruidUtil.executeQuery(connection,sql_fields,param);
                    break;
                case Constant.DATA_SOURCE_ITEM_TYPE_SQLSERVER:
                    sql_fields = "SELECT A.name as COLUMN_NAME,B.name as DATA_TYPE,A.length as LENGTH," +
                            "CASE WHEN A.isnullable='0' THEN 'NO' ELSE 'YES' END as IS_NULLABLE," +
                            "CAST(D.[value] AS VARCHAR) as COLUMN_COMMENT " +
                            "FROM SYSCOLUMNS A " +
                            "LEFT JOIN SYSTYPES B ON A.xtype = B.xusertype " +
                            "LEFT JOIN sys.extended_properties D ON A.id = D.major_id AND A.colid = D.minor_id AND D.name = 'MS_DESCRIPTION' " +
                            "WHERE A.id = OBJECT_ID(?) " +
                            "ORDER BY A.name";
                    param = new Object[]{table};
                    result = DruidUtil.executeQuery(connection,sql_fields,param);
                    break;
                case Constant.DATA_SOURCE_ITEM_TYPE_ORACLE:
                    sql_fields = "select t.COLUMN_NAME,c.COMMENTS AS COLUMN_COMMENT,t.NULLABLE AS IS_NULLABLE,t.DATA_TYPE AS DATA_TYPE,t.CHAR_LENGTH AS LENGTH" +
                            " from all_tab_columns t, all_col_comments c " +
                            " where t.TABLE_NAME = c.TABLE_NAME " +
                            " and t.COLUMN_NAME = c.COLUMN_NAME " +
                            " and t.owner = ? and t.TABLE_NAME = ? "  +
                            " order by t.COLUMN_NAME";
                    param = new Object[]{database,table};
                    result = DruidUtil.executeQuery(connection,sql_fields,param);
                    break;
                default:
                    result = new ArrayList<>();
                    break;
            }
        }catch (Exception e){
            log.error("获取字段信息失败："+database+"-"+table+e.getMessage());
            throw e;//抛出异常，保证回滚
        }
        return result;
    }


    /**
     * 数据源metadata初始化（新），将库，表，字段拆分为3个表存储
     * @param data_source_ids 数据源IDs，以逗号隔开，用来获取数据源基本信息
     * @return  布尔，成功/失败
     * @throws Exception
     */
    @Override
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
    public boolean initByDataSourceIds(String data_source_ids) throws Exception {
        //默认抽样行数
        int sample_num = 100;
        //批量处理，参数以逗号隔开
        String[] ids = data_source_ids.split(",");
        String sql;//sql语句
        List<Map<String, Object>> databaseList;//数据库列表
        //遍历id
        for(String data_source_id : ids){
            //库、表、字段分别存储到3个表中
            List<MetadataDatabase> batchList_database = new ArrayList<>();
            List<MetadataTable> batchList_table = new ArrayList<>();
            List<MetadataField> batchList_field = new ArrayList<>();
            //抽样数据持久化
            List<DataSourceSample> batchList_sample = new ArrayList<>();

            //获取数据源信息
            DataSource dataSource = dataSourceDao.selectDataSourceInfoById(data_source_id);
            //赋值实际的抽样行数
            sample_num = dataSource.getData_source_sample_num();
            //定义数据库链接
            Connection connection = null;
            try{
                //获取数据库连接
                connection = DruidUtil.getConnection(dataSource);
                if(connection!=null){
                    //开始初始化
                    //1.删除原初始化数据
                    //库、表、字段、样本数据
                    dataSourceMetadataDao.deleteMetadataByDataSourceId(data_source_id);
                    //2.获取数据库列表
                    // 有些数据源需要先查出有哪些数据库，有些直接在数据源定义好了
                    //mysql数据库且“数据库/实例”为空  或者 数据库类型为oracle，需要查询出有哪些数据库
                    if((Strings.isNullOrEmpty(dataSource.getData_source_dbname())&&Constant.DATA_SOURCE_ITEM_TYPE_MYSQL.equals(dataSource.getData_source_item_type()))
                            ||Constant.DATA_SOURCE_ITEM_TYPE_ORACLE.equals(dataSource.getData_source_item_type())){
                        //获取数据库列表
                        switch (dataSource.getData_source_item_type()){
                            case Constant.DATA_SOURCE_ITEM_TYPE_MYSQL:
                                sql = "show databases";
                                databaseList = DruidUtil.executeQuery(connection,sql,null);
                                break;
                            case Constant.DATA_SOURCE_ITEM_TYPE_ORACLE:
                                //数据库别名与mysql对齐 oracle 别名会默认改为大写，需要通过双引号包括
                                sql = "select USERNAME as \""+Constant.DATA_SOURCE_DATABASE_ALIAS+"\" from dba_users order by USERNAME";
                                databaseList = DruidUtil.executeQuery(connection,sql,null);
                                break;
                            default:
                                //保证list初始化，否则for循环对象为null时报错
                                databaseList = new ArrayList<>();
                                break;
                        }

                    }else{
                        //数据库已经在数据源中填写
                        databaseList = new ArrayList<>();
                        Map<String,Object> dbMap = new HashMap<>();
                        dbMap.put(Constant.DATA_SOURCE_DATABASE_ALIAS,dataSource.getData_source_dbname());
                        databaseList.add(dbMap);
                    }
                    //3.遍历数据库库列表，取表和字段信息
                    for(Map<String,Object> databaseInfo:databaseList){
                        //初始化的数据库基本信息
                        MetadataDatabase metadataDatabase = new MetadataDatabase();
                        metadataDatabase.setMetadata_database_id(UUID.randomUUID().toString());
                        metadataDatabase.setData_source_id(dataSource.getData_source_id());
                        metadataDatabase.setMetadata_database_name(databaseInfo.get(Constant.DATA_SOURCE_DATABASE_ALIAS).toString());
                        metadataDatabase.setMetadata_is_auto_discovery("1");//默认数据自动发现
                        batchList_database.add(metadataDatabase);
                        //获取所有表
                        List<Map<String, Object>> tableList = getTablesByDatabase(databaseInfo.get(Constant.DATA_SOURCE_DATABASE_ALIAS).toString(),connection,dataSource.getData_source_item_type());
                        for(Map<String, Object> tableInfo:tableList){
                            //初始化的表的基本信息
                            MetadataTable metadataTable = new MetadataTable();
                            metadataTable.setMetadata_table_id(UUID.randomUUID().toString());
                            metadataTable.setData_source_id(dataSource.getData_source_id());
                            metadataTable.setMetadata_database_name(databaseInfo.get(Constant.DATA_SOURCE_DATABASE_ALIAS).toString());
                            metadataTable.setMetadata_table_name(tableInfo.get(Constant.DATA_SOURCE_TABLE_ALIAS).toString());
                            metadataTable.setMetadata_is_auto_discovery("1");//默认数据自动发现
                            batchList_table.add(metadataTable);

                            //查询出的表 样本数据对象
                            List<Map<String, String>> table_data = null;
                            try{

                                //查询表样本数据
                                table_data = dataSourceMetadataService.getTableSampleData(dataSource,databaseInfo.get(Constant.DATA_SOURCE_DATABASE_ALIAS).toString(),tableInfo.get(Constant.DATA_SOURCE_TABLE_ALIAS).toString(),sample_num);
                                //遍历，转化成纵表
                                for(Map<String,String> row:table_data){
                                    //遍历map
                                    for (Map.Entry entry:row.entrySet()){
                                        if(entry.getValue()!=null){
                                            //字符串长度小于设定的阈值（100000）
                                            if( entry.getValue().toString().length()<MAX_SAMPLE_LENGTH){
                                                //数据库（本地持久化）存储的一行数据
                                                //打标签（库，表，字段）
                                                DataSourceSample dataSourceSample = new DataSourceSample();
                                                dataSourceSample.setData_source_id(dataSource.getData_source_id());
                                                dataSourceSample.setDatabase_name(databaseInfo.get(Constant.DATA_SOURCE_DATABASE_ALIAS).toString());
                                                dataSourceSample.setTable_name(tableInfo.get(Constant.DATA_SOURCE_TABLE_ALIAS).toString());
                                                dataSourceSample.setField_name(entry.getKey().toString());
                                                //数据不为NULL时，且进行赋值
                                                if(entry.getValue()!=null){
                                                    dataSourceSample.setField_content(entry.getValue().toString());
                                                }else{
                                                    //不赋值，在数据库会默认为NULL
                                                }
                                                batchList_sample.add(dataSourceSample);
                                            }else{
                                                //字符串长度过大
                                                log.info(dataSource.getData_source_id()+"----"+databaseInfo.get(Constant.DATA_SOURCE_DATABASE_ALIAS).toString()+"----"+tableInfo.get(Constant.DATA_SOURCE_TABLE_ALIAS).toString()+"---"+entry.getKey().toString());
                                                System.out.println("样本数据字段长度过大");
                                                System.out.println(dataSource.getData_source_id()+"----"+databaseInfo.get(Constant.DATA_SOURCE_DATABASE_ALIAS).toString()+"----"+tableInfo.get(Constant.DATA_SOURCE_TABLE_ALIAS).toString()+"---"+entry.getKey().toString());
                                            }
                                        }else{

                                        }
                                    }
                                }
                                //获取表下的字段信息
                                List<Map<String, Object>> fieldList = getTableFields(databaseInfo.get(Constant.DATA_SOURCE_DATABASE_ALIAS).toString(),tableInfo.get(Constant.DATA_SOURCE_TABLE_ALIAS).toString(),dataSource.getData_source_item_type(),connection);
                                for(Map<String,Object> fieldInfo:fieldList){
                                    //字段的基本信息
                                    MetadataField metadataField = new MetadataField();
                                    metadataField.setMetadata_field_id(UUID.randomUUID().toString());
                                    metadataField.setMetadata_field_name(fieldInfo.get("COLUMN_NAME").toString());
                                    metadataField.setData_source_id(dataSource.getData_source_id());
                                    metadataField.setMetadata_database_name(databaseInfo.get(Constant.DATA_SOURCE_DATABASE_ALIAS).toString());
                                    metadataField.setMetadata_table_name(tableInfo.get(Constant.DATA_SOURCE_TABLE_ALIAS).toString());
                                    //类型，长度，注释，是否为空等字段都可能为空
                                    metadataField.setMetadata_field_type(fieldInfo.get("DATA_TYPE")!=null?fieldInfo.get("DATA_TYPE").toString():"");
                                    metadataField.setMetadata_field_length(fieldInfo.get("LENGTH")!=null?fieldInfo.get("LENGTH").toString():"");
                                    metadataField.setMetadata_field_isnull(fieldInfo.get("IS_NULLABLE")!=null?fieldInfo.get("IS_NULLABLE").toString():"");
                                    metadataField.setMetadata_field_comment(fieldInfo.get("COLUMN_COMMENT")!=null?fieldInfo.get("COLUMN_COMMENT").toString():"");
                                    metadataField.setMetadata_is_auto_discovery("1");//默认数据自动发现
                                    batchList_field.add(metadataField);
                                }
                            }catch (SQLSyntaxErrorException e){
                                //捕获已知的查询权限异常

                                //SQL语句执行异常，并且报oracle的权限不足问题，eg:ORA-01031: 权限不足
                                if(e.getMessage().indexOf("ORA-01031")>=0){
                                    log.error("捕获 oracle 查询样本数据SQLSyntaxErrorException，异常代码：ORA-01031");
                                }
                                //数据源名称-数据库名称-表名
                                log.error(dataSource.getData_source_name()+"-"+databaseInfo.get(Constant.DATA_SOURCE_DATABASE_ALIAS).toString()+"-"+tableInfo.get(Constant.DATA_SOURCE_TABLE_ALIAS).toString());
                                //具体异常信息 eg:ORA-01031: 权限不足
                                log.error(e.getMessage());
                                continue;
                            }catch (Exception e){
                                log.error("捕获 oracle 查询样本数据 其他类型异常：");
                                //数据源名称-数据库名称-表名
                                log.error(dataSource.getData_source_name()+"-"+databaseInfo.get(Constant.DATA_SOURCE_DATABASE_ALIAS).toString()+"-"+tableInfo.get(Constant.DATA_SOURCE_TABLE_ALIAS).toString());
                                //具体异常信息 eg:ORA-01031: 权限不足
                                log.error(e.getMessage());
                                //其他异常信息也进行忽略
                                continue;
                            }


                        }
                    }

                    //4.数据过大会抛出异常，提示请求大小超过设置的阈值，改为每1000条insert一次
                    //TODO 具体插入多少条效率最高还需进行调研
                    //TODO 泛型方法提出来？
                    //数据库一般不会超过阈值
                    dataSourceMetadataDao.batchInsertDatabase(batchList_database);
                    //表和字段需要进行拆分提交
                    if(batchList_table.size()>batch_size){
                        int part = batchList_table.size()/batch_size;
                        List<MetadataTable> listPage;//分页list
                        for (int i = 0; i < part; i++) {
                            //取前1000条
                            listPage = batchList_table.subList(0, batch_size);
                            //批量插入
                            dataSourceMetadataDao.batchInsertTable(listPage);
                            //剔除
                            batchList_table.subList(0, batch_size).clear();
                        }
                    }
                    //插入余数
                    dataSourceMetadataDao.batchInsertTable(batchList_table);

                    //字段拆分 批量提交
                    if(batchList_field.size()>batch_size){
                        int part = batchList_field.size()/batch_size;
                        List<MetadataField> listPage;//分页list
                        for (int i = 0; i < part; i++) {
                            //取前1000条
                            listPage = batchList_field.subList(0, batch_size);
                            //批量插入
                            dataSourceMetadataDao.batchInsertField(listPage);
                            //剔除
                            batchList_field.subList(0, batch_size).clear();
                        }
                    }
                    //插入余数
                    dataSourceMetadataDao.batchInsertField(batchList_field);

                    //样本数据持久化
                    if(batchList_sample.size()>batch_size){
                        int part = batchList_sample.size()/batch_size;
                        List<DataSourceSample> listPage;//分页list
                        for (int i = 0; i < part; i++) {
                            //取前1000条
                            listPage = batchList_sample.subList(0, batch_size);
                            //批量插入
                            dataSourceMetadataDao.batchInsertSample(listPage);
                            //剔除
                            batchList_sample.subList(0, batch_size).clear();
                        }
                    }
                    //插入余数
                    dataSourceMetadataDao.batchInsertSample(batchList_sample);

                    //5.更新初始化信息到数据源中
                    dataSource.setData_source_is_initialized(1);//已经初始化
                    dataSource.setData_source_init_time(LocalDateTime.now().format(dtf_time));//初始化完成时间
                    dataSourceDao.update(dataSource);
                }else{
                    //获取不到连接池信息
                    log.info("无法获取connection信息 ，data_source_id="+data_source_id);
                    return false;
                }
            }catch (Exception e){
                log.error("数据源初始化异常："+e.getMessage());
                //抛出异常，实现回滚
                throw e;
            }finally {
                //一个数据源的初始化完毕后，关闭链接
                if(connection!=null){
                    connection.close();
                }
            }
        }
        return true;
    }

    @Override
    public boolean dataDiscovery(String data_source_ids) throws Exception {

        //拆分数据源id，支持多个数据源同时进行标签匹配
        String[] ids = data_source_ids.split(",");
        //先更新多个数据源的状态，保证不会重复执行
        for(String data_source_id:ids){
            //更新数据源 数据发现状态信息,进行中
            dataSourceDao.updateDiscoveryState(data_source_id,1);
        }
        //1.创建定时任务，只执行一次
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        //参数传递到job线程内
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("data_source_ids",data_source_ids);
        JobDetail jobDetail = JobBuilder.newJob(DataSourceDiscoveryJob.class)
                .withDescription("敏感数据发现任务-仅执行一次")
                .withIdentity(data_source_ids+"-onlyOnce", Constant.QUARTZ_JOB_GROUP)
                .usingJobData(jobDataMap)
                .build();
        SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger().withIdentity(data_source_ids+"-onlyOnce", Constant.QUARTZ_JOB_GROUP)
                .startAt(new Date())
                //重复执行的次数，因为加入任务的时候马上执行了，所以不需要重复，否则会多一次。
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(3).withRepeatCount(0))
                .build();
        try {
            if(!scheduler.checkExists(JobKey.jobKey(data_source_ids+"-onlyOnce",Constant.QUARTZ_JOB_GROUP))){
                scheduler.scheduleJob(jobDetail,simpleTrigger);
            }
            //启动
            scheduler.start();
        } catch (SchedulerException e) {
            log.error("任务启动失败："+e.getMessage());
            return false;
        }
        return true;
    }


    /**已停用
     * 根据数据库信息获取下属表和字段的信息,
     * @param connection
     * @param database 数据库
     * @param dataSource 数据源信息
     * @return
     * @throws Exception
     */
//    private List<DataSourceMetadata> getTableAndFieldsMetadata(Connection connection,String database,DataSource dataSource) throws Exception {
//        List<DataSourceMetadata> result = new ArrayList<>();
//        String tableName ;
//        //获取所有表
//        List<Map<String, Object>> table_list = getTablesByDatabase(database,connection,dataSource.getData_source_item_type());
//        for(Map<String,Object> table_map:table_list){
//            tableName = table_map.get("TABLE_NAME")!=null?table_map.get("TABLE_NAME").toString():"";
//            //表信息
//            DataSourceMetadata metadata_table = new DataSourceMetadata();
//            metadata_table.setMetadata_id(UUID.randomUUID().toString());
//            metadata_table.setData_source_id(dataSource.getData_source_id());
//            metadata_table.setMetadata_type("table");//元数据类型-表
//            metadata_table.setMetadata_database(database);
//            metadata_table.setMetadata_table(tableName);//表名
//            result.add(metadata_table);
//            //字段信息
//            List<Map<String, Object>> fields_list = getTableFields(database,tableName,dataSource.getData_source_item_type(),connection);
//            for(Map<String,Object> field_map:fields_list){
//                DataSourceMetadata metadata_field = new DataSourceMetadata();
//                metadata_field.setMetadata_id(UUID.randomUUID().toString());
//                metadata_field.setData_source_id(dataSource.getData_source_id());
//                metadata_field.setMetadata_type("field");//元数据类型-字段
//                metadata_field.setMetadata_database(database);//数据库
//                metadata_field.setMetadata_table(tableName);//表名
//                metadata_field.setMetadata_field(field_map.get("COLUMN_NAME")!=null?field_map.get("COLUMN_NAME").toString():"");//字段名
//                metadata_field.setMetadata_field_type(field_map.get("DATA_TYPE")!=null?field_map.get("DATA_TYPE").toString():"");//类型
//                metadata_field.setMetadata_remark(field_map.get("COLUMN_COMMENT")!=null?field_map.get("COLUMN_COMMENT").toString():"");//注释
//                result.add(metadata_field);
//            }
//
//        }
//        return result;
//    }

    /**
     * 将查询出的数据转化成tree结构的节点list
     * @param result_table 表信息list
     * @param labelName tree名称，一般为表名
     * @param database 数据库名
     * @return tree节点的list，一个tree节点为一个map。key为tree节点所需的属性名，value为值
     */
    private List<Map<String, String>> data2treeNode(List<Map<String, Object>> result_table,String labelName,String database,String isExpand){
        List<Map<String, String>> result = new ArrayList<>();
        //遍历，组装数据
        for(Map<String, Object> map:result_table){
            Map<String,String> node = new HashMap<>();
            node.put("label",map.get(labelName).toString());//节点显示文本
            node.put("id",map.get(labelName).toString());//节点id
            node.put("child","[]");//子节点
            node.put("isExpand",isExpand);//是否可展开
            node.put("database",database);//当节点为表时，节点上需要带有上级节点的信息，即database
            result.add(node);
        }
        return result;
    }

    /**
     * 创建数据库tree节点
     * @param database 数据库名称
     * @return tree节点的list，一个tree节点为一个map。key为tree节点所需的属性名，value为值
     */
    private List<Map<String, String>> createDatabaseNode(String database){
        List<Map<String, String>> result = new ArrayList<>();
        Map<String,String> node = new HashMap<>();
        node.put("label",database);//节点显示文本
        node.put("id",database);//节点id
        node.put("child","[]");//子节点
        node.put("isExpand","true");//是否可展开
        node.put("database",database);//当节点为表时，节点上需要带有上级节点的信息，即database
        result.add(node);
        return result;
    }


    /**
     * 根据数据库获取下面的表信息
     * TABLE_NAME 表名
     * @param database 数据库名称
     * @param connection 连接
     * @param data_source_item_type 数据库类型
     * @return 表的基本信息，list为条数，map的key为TABLE_NAME ，value为表名
     * @throws Exception
     */
    public List<Map<String, Object>> getTablesByDatabase(String database,Connection connection,String data_source_item_type) throws Exception {
        String sql; //要执行的sql语句
        List<Map<String, Object>> result_table;//返回的结果集
        Object[] param; //参数
        try{
            //判断数据库类型
            switch(data_source_item_type){
                case Constant.DATA_SOURCE_ITEM_TYPE_MYSQL:
                    sql = "select TABLE_NAME from information_schema.`TABLES` where TABLE_SCHEMA = ? order by TABLE_NAME";
                    param = new Object[]{database};
                    result_table = DruidUtil.executeQuery(connection,sql,param);
                    break;
                case Constant.DATA_SOURCE_ITEM_TYPE_SQLSERVER:
                    //数据库或实例不为空,直接获取表信息
                    sql = "SELECT NAME as TABLE_NAME FROM SYSOBJECTS WHERE XTYPE='U' ORDER BY NAME";
                    result_table = DruidUtil.executeQuery(connection,sql,null);
                    break;
                case Constant.DATA_SOURCE_ITEM_TYPE_ORACLE:
                    //IOT表的溢出表不进行显示
                    String sql_table = "select TABLE_NAME from ALL_TABLES where owner=? and (IOT_type != 'IOT_OVERFLOW' OR IOT_type IS NULL) ORDER BY TABLE_NAME";
                    param = new Object[]{database};
                    result_table = DruidUtil.executeQuery(connection,sql_table,param);
                    break;
                default:
                    result_table = new ArrayList<>();
                    break;
            }
        }catch (Exception e){
            log.error("获取表信息失败："+e.getMessage());
            throw e;//异常抛出  保证回滚
        }
        return result_table;
    }
}

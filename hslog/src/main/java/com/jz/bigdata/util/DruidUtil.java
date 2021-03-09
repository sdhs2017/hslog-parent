package com.jz.bigdata.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.data_source.entity.DataSource;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/2/8 10:30
 * @Description:
 * mysql    支持，大规模使用
 * oracle	支持，大规模使用
 * sqlserver	支持
 * postgres	支持
 * db2	支持
 * h2	支持
 * derby	支持
 * sqlite	支持
 * sybase	支持
 */
@Slf4j
public class DruidUtil {

    /**
     * 数据库连接池（druid连接池）对象引用
     */
    private static DruidDataSource druidPool;

    public static void main(String[] args) {
        String driver_mysql = "com.mysql.jdbc.Driver";
        String driver_sqlserver = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
        String driver_oracle = "oracle.jdbc.driver.OracleDriver";
        String driver_db2 = "com.ibm.db2.jdbc.app.DB2Driver";
        String url_mysql = "jdbc:mysql://192.168.2.181:3306/jzLogAnalysis?useUnicode=yes&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true";
        String url_sqlserver = "jdbc:sqlserver://localhost:1433;DatabaseName=user";
        String url_oracle = "jdbc:oracle:thin:@//localhost:1521:DataBaseName";
        String url_db2 = "jdbc:db2://<:port>/dbname";
        String userName = "root";
        String password = "JZdata.123";
        try {
            // 通过直接创建连接池对象的方式创建连接池对象
             DruidDataSource druidDataSource = new DruidDataSource();
             druidDataSource.setUsername(userName);
             druidDataSource.setPassword(password);
             druidDataSource.setUrl(url_mysql);
             druidDataSource.setDriverClassName(driver_mysql);
            druidPool = druidDataSource;

            Connection connection = DruidUtil.getConnection();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    /**
     * 获取链接
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        Connection connection = druidPool.getConnection();
        return connection;
    }

    public static DruidDataSource getDruidPool() {
        return druidPool;
    }

    /*********************************************以上为测试代码**************************************************************************/
    //数据源链接对象id前缀
    public final static String DRUID_DATA_SOURCE_ID = "hs-datasource-";
    /**
     * 测试连接功能，测试连接完成后需将datasource及connection进行关闭
     * @param dataSource
     * @return
     * @throws Exception
     */

    public static boolean checkConnection(DataSource dataSource) throws SQLException {
        boolean result = false;
        //连接信息
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername(dataSource.getData_source_username());
        druidDataSource.setPassword(dataSource.getData_source_password());
        druidDataSource.setUrl(getUrl(dataSource));
        druidDataSource.setDriverClassName(getDriver(dataSource.getData_source_item_type()));
        druidDataSource.setBreakAfterAcquireFailure(true);//失败后不继续重连
        druidDataSource.setFailFast(true);//异常可捕获
        Connection connection = null;
        try {
            connection = druidDataSource.getConnection();
            if(connection!=null){
                result = true;
            }
        } catch (Exception e) {
            log.error("数据源链接失败："+e.getMessage());
        }finally {
            if(connection!=null){
                connection.close();
            }
            //关闭datasource
            druidDataSource.close();
        }
        return result;
    }
    /**
     * 正常查询时需要获取connection，
     * @param dataSource
     * @return
     * @throws Exception
     */

    public static Connection getConnection(DataSource dataSource) {
        Connection connection = null;
        try{
            //生成druid datasource名称
            String druid_data_source_name = DRUID_DATA_SOURCE_ID+dataSource.getData_source_id();
            //查看该datasource是否已存在
            DruidDataSource druidDataSource = (DruidDataSource) DruidStatManagerFacade.getInstance().getDruidDataSourceByName(druid_data_source_name);
            if(druidDataSource!=null){
                //已存在，直接获取connection
                connection = druidDataSource.getConnection();
            }else{
                //连接信息
                DruidDataSource newDruidDataSource = new DruidDataSource();
                newDruidDataSource.setName(druid_data_source_name);
                //TODO 提出来
                newDruidDataSource.setUsername(dataSource.getData_source_username());//账号
                newDruidDataSource.setPassword(dataSource.getData_source_password());//密码
                newDruidDataSource.setUrl(getUrl(dataSource));//url
                newDruidDataSource.setDriverClassName(getDriver(dataSource.getData_source_item_type()));//driver
                newDruidDataSource.setBreakAfterAcquireFailure(true);//失败后不继续重连
                newDruidDataSource.setFailFast(true);//异常可捕获
                connection = newDruidDataSource.getConnection();
            }
        }catch (Exception e){
            log.error("数据源获取链接失败："+e.getMessage());
        }

        return connection;
    }
    /**
     * 根据填写的链接信息生成url
     * @param dataSource
     * @return
     */
    public static String getUrl(DataSource dataSource){
        switch(dataSource.getData_source_item_type()){
            case Constant.DATA_SOURCE_ITEM_TYPE_MYSQL:
                return "jdbc:mysql://"+dataSource.getData_source_ip()+":"+dataSource.getData_source_port()+"/"+(Strings.isNullOrEmpty(dataSource.getData_source_dbname())?"":dataSource.getData_source_dbname());
            case Constant.DATA_SOURCE_ITEM_TYPE_SQLSERVER:
                return "jdbc:sqlserver://"+dataSource.getData_source_ip()+":"+dataSource.getData_source_port()+";"+(Strings.isNullOrEmpty(dataSource.getData_source_dbname())?"":"DatabaseName="+dataSource.getData_source_dbname());
            case Constant.DATA_SOURCE_ITEM_TYPE_ORACLE:
                return "jdbc:oracle:thin:@//"+dataSource.getData_source_ip()+":"+dataSource.getData_source_port()+(Strings.isNullOrEmpty(dataSource.getData_source_dbname())?"":"/"+dataSource.getData_source_dbname());
            default:
                return null;
        }
    }
    /**
     * 根据数据源类别返回对应的Driver
     * @param data_source_item_type
     * @return
     */
    public static String getDriver(String data_source_item_type){
        switch (data_source_item_type){
            case Constant.DATA_SOURCE_ITEM_TYPE_MYSQL:
                return "com.mysql.jdbc.Driver";
            case Constant.DATA_SOURCE_ITEM_TYPE_SQLSERVER:
                return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case Constant.DATA_SOURCE_ITEM_TYPE_ORACLE:
                return "oracle.jdbc.driver.OracleDriver";
            default:
                return null;//不存在返回空
        }
    }

    /**用于一个connection执行一次sql后关闭所有连接
     * 当一个方法传递的参数为null时，是值传递，其他都是引用传递，因此resultSet不需要作为参数传递到执行的方法中
     * 执行sql语句并将结果进行处理，返回list
     * @param dataSource
     * @param sql
     * @param params
     * @return
     * @throws SQLException
     */
    public static List<Map<String, Object>> executeQuery(DataSource dataSource,String sql, Object[] params)  {
        List<Map<String, Object>> result = new ArrayList<>();
        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement statement= null;
        try{
            connection = getConnection(dataSource);
            //TODO 如何把statement 提出来
            //sql+参数
            statement = connection.prepareStatement(sql);
            // 参数赋值
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }
            }
            // 执行
            resultSet =  statement.executeQuery();
            //处理resultset数据，处理后才能关闭statment和resultset
            result = resultSetToList(resultSet,result);
        }catch (Exception e){
            log.error("查询数据异常："+e.getMessage());
        }finally {
            close(connection,resultSet,statement);
        }
        return result;
    }


    /**用于一个connection需要执行多个sql后,connection需要在执行完多个sql后再关闭
     * 执行sql语句并将结果进行处理，返回list
     * @param connection
     * @param sql
     * @param params
     * @return
     * @throws SQLException
     */
    public static List<Map<String, Object>> executeQuery(Connection connection,String sql, Object[] params) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        ResultSet resultSet = null;
        PreparedStatement statement= null;
        try{
            //sql+参数
            statement = connection.prepareStatement(sql);
            // 参数赋值
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }
            }
            // 执行
            resultSet =  statement.executeQuery();
            //处理resultset数据，处理后才能关闭statment和resultset
            result = resultSetToList(resultSet,result);
        }catch (Exception e){
            log.error("查询数据异常："+e.getMessage());
        }finally {
            close(null,resultSet,statement);
        }
        return result;
    }
    /**用于一个connection需要执行多个sql后,connection需要在执行完多个sql后再关闭
     * SQL 查询将查询结果直接放入ResultSet中
     * @param connection
     * @param sql    SQL语句
     * @param params 参数数组，若没有参数则为null
     * @return 结果集
     */
//    public static ResultSet executeQueryRS(Connection connection,String sql, Object[] params) throws SQLException {
//        PreparedStatement statement= null;
//        ResultSet resultSet = null;
//        try {
//            statement = connection.prepareStatement(sql);
//            // 参数赋值
//            if (params != null) {
//                for (int i = 0; i < params.length; i++) {
//                    statement.setObject(i + 1, params[i]);
//                }
//            }
//            // 执行
//            resultSet =  statement.executeQuery();
//        } catch (SQLException e) {
//            log.error("sql语句执行错误："+e.getMessage());
//        }finally {
//            if(statement!=null){
//                statement.close();
//            }
//        }
//        return resultSet;
//    }
    /**
     * 执行sql后关闭相关连接
     * @param connection
     * @param resultSet
     * @param statement
     */
    private static void close(Connection connection,ResultSet resultSet,PreparedStatement statement){
        try{
            if(statement!=null){
                statement.close();
            }
        } catch (SQLException e) {
            log.error("statement关闭失败"+e.getMessage());
        }
        try{
            if (resultSet!=null){
                resultSet.close();
            }
        } catch (SQLException e) {
            log.error("resultSet关闭失败"+e.getMessage());
        }
        try{
            if(connection!=null){
                connection.close();
            }
        } catch (SQLException e) {
            log.error("connection关闭失败"+e.getMessage());
        }

    }
    /**
     * ResultSet转化成list
     * @param rst
     * @return
     * @throws SQLException
     */
    private static List<Map<String, Object>> resultSetToList(ResultSet rst,List<Map<String, Object>> list) throws SQLException {
        ResultSetMetaData md = rst.getMetaData(); //获得结果集结构信息,元数据
        int columnCount = md.getColumnCount();   //获得列数
        while (rst.next()) {
            Map<String,Object> rowData = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(md.getColumnLabel(i), rst.getObject(i));
            }
            list.add(rowData);
        }
        return list;
    }

}

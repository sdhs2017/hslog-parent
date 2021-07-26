package com.jz.bigdata.util;

import com.alibaba.druid.DbType;
import com.alibaba.druid.pool.DruidAbstractDataSource;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.alibaba.druid.util.HexBin;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.data_source.entity.DataSource;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.apache.ibatis.mapping.ResultSetType;

import java.io.Closeable;
import java.sql.*;
import java.sql.Date;
import java.util.*;

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

//    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").create();
    public static void main(String[] args) {
        //sql语句获取 库 表  字段信息，
        String sql = "select t1.c1,t2.c2 from table1 as t1 left join table2 as t2 on t1.id = t2.id where t1.name=1";
        sql = "select jzLogAnalysis.equipment.t1,jzLogAnalysis.equipment.t2,jzLogAnalysis.equipment.t1 from jzLogAnalysis.equipment,jzLogAnalysis0517.test";
        sql = "select 1";
        sql = "insert into note\\\\n\\\\t\\\\t\\\\n\\\\t\\\\t ( id,\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\tresult,\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\tnote.`describe`,\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\ttime,\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\tuserId,\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\taccount,\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\tdepartmentId,\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\tip,\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\tstate ) \\\\n\\\\t\\\\t values ( '21648dbabe2e4f1fbf7d6622b80ed60f',\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\t1,\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\t'用户账号:15069109870    部门:公司    操作:监控agent采集器状态    操作状态:成功',\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\t'2021-05-26 12:20:10.45',\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\t'81ce6983fc1542c69706c70bb7b2098e',\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\t'15069109870',\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\t1,\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\t'0:0:0:0:0:0:0:1',\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\n\\\\t\\\\t\\\\t\\\\t1 )\\\"}";
//        sql = "update t1 set c1='' and c2=''";
//        sql = "delete from t1 where c1=''";
        //String sql = "select name, age from t_user where id = 1";

        DbType dbType = JdbcConstants.MYSQL;
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        SQLStatement stmt = stmtList.get(0);

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(statVisitor);
        //涉及到的字段，真实表名.字段名：
        System.out.println(""+statVisitor.getColumns()); // [t_user.name, t_user.age, t_user.id]
        //表的操作类型SELECT/UPDATE/DELETE等：
        System.out.println(""+statVisitor.getTables()); // {t_user=Select}
        //条件：
        System.out.println(""+statVisitor.getConditions()); // [t_user.id = 1]


//        String driver_mysql = "com.mysql.jdbc.Driver";
//        String driver_sqlserver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//        String driver_oracle = "oracle.jdbc.driver.OracleDriver";
//        String driver_db2 = "com.ibm.db2.jdbc.app.DB2Driver";
//        String url_mysql = "jdbc:mysql://192.168.2.181:3306/jzLogAnalysis";
//        String url_sqlserver = "jdbc:sqlserver://192.168.2.211:1433;DatabaseName=jzmssql";
//        String url_oracle = "jdbc:oracle:thin:@//192.168.2.133:1521/ORCL";
//        String url_db2 = "jdbc:db2://<:port>/dbname";
//        String userName = "system";
//        String password = "jzxt@2021";
//
//
//        DruidDataSource druidDataSource = new DruidDataSource();
//        druidDataSource.setUsername(userName);
//        druidDataSource.setPassword(password);
//        druidDataSource.setUrl(url_sqlserver);
//        druidDataSource.setDriverClassName(driver_sqlserver);
//        druidDataSource.setBreakAfterAcquireFailure(true);//失败后不继续重连
//        druidDataSource.setFailFast(true);//异常可捕获
//        druidDataSource.setConnectionErrorRetryAttempts(0);
//        //druidDataSource.setMaxWait(2000);
//        Connection connection = null;
//        try {
//            // 通过直接创建连接池对象的方式创建连接池对象
//            connection =  druidDataSource.getConnection();
//
//            //DruidAbstractDataSource.PhysicalConnectionInfo physicalConnectionInfo = druidDataSource.createPhysicalConnection();
//
////            String sql = "select * from \"AUDSYS\".\"AUD$UNIFIED\" where rownum<=100";
////            PreparedStatement statement= null;
////            ResultSet resultSet = null;
////            try{
////                //sql+参数
////                statement = connection.prepareStatement(sql);
////                // 执行
////                resultSet =  statement.executeQuery();
////                //处理resultset数据，处理后才能关闭statment和resultset
////                List<Map<String, String>> list = resultSetToListString(resultSet);
////                System.out.println(JSONArray.fromObject(list).toString());
//////                System.out.println(gson.toJson(list));
////            }catch (Exception e){
////                e.printStackTrace();
////            }finally {
////                close(null,resultSet,statement);
////            }
//
//        } catch (SQLException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            //log.error(e.getMessage());
//            System.out.println("----------------"+e.getCause().getMessage());
//        }finally {
////            close
//            JdbcUtils.close( connection);
//            //druidDataSource.close();
//        }

    }
    private static String execMySql(String sql) {
        StringBuilder out = new StringBuilder();
        MySqlOutputVisitor visitor = new MySqlOutputVisitor(out);
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
         for (SQLStatement statement : statementList) {
         statement.accept(visitor);
         visitor.println();
         }
        return out.toString();
    }
    private static  List<Map<String, Object>> resultSetToList2Test(ResultSet rst) throws SQLException {
        ResultSetMetaData md = rst.getMetaData(); //获得结果集结构信息,元数据
        int columnCount = md.getColumnCount();   //获得列数
        List<Map<String, Object>> list = new ArrayList<>();
        while (rst.next()) {
            Map<String, Object> rowData = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                if(md.getColumnLabel(i).equals("SQL_TEXT")){

                }else{
                    //rowData.put(md.getColumnLabel(i), rst.getObject(i));
                    //System.out.println(md.getColumnLabel(i)+"--------"+rst.getObject(i));
                }

                //rowData.put(md.getColumnLabel(i), rst.getObject(i)!=null?rst.getObject(i).toString():"");
                System.out.println(md.getColumnLabel(i)+"--------"+String.valueOf(rst.getString(i)));

            }
            list.add(rowData);
        }
        return list;
    }



    /*********************************************以上为测试代码**************************************************************************/



    //数据源链接对象id前缀
    public final static String DRUID_DATA_SOURCE_ID = "hs-datasource-";
    /**
     * 测试连接功能，测试连接完成后需将datasource及connection进行关闭
     * @param dataSource
     * @return 布尔，true/false  成功/失败
     * @throws SQLException service层捕获，进行更详细的处理
     */
    public static boolean checkConnection(DataSource dataSource) throws SQLException {
        boolean result = false;
        //连接信息
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername(dataSource.getData_source_username());//用户名
        druidDataSource.setPassword(dataSource.getData_source_password());//密码
        druidDataSource.setUrl(getUrl(dataSource));//生成连接url
        druidDataSource.setDriverClassName(getDriver(dataSource.getData_source_item_type()));//设置driver
        druidDataSource.setBreakAfterAcquireFailure(true);//失败后不继续重连
        druidDataSource.setFailFast(true);//异常可捕获
        druidDataSource.setConnectionErrorRetryAttempts(0);//系统默认为1，即进行一次尝试
        Connection connection = null;
        try {
            connection = druidDataSource.getConnection();
            //获取的connection不为空判定为连接成功
            if(connection!=null){
                result = true;
            }
        }finally {
            //连接成功后，关闭connection
            try{
                if(connection!=null){
                    connection.close();
                }
            } catch (SQLException e) {
                log.error("connection关闭失败"+e.getMessage());
            }
            //关闭datasource
            druidDataSource.close();
        }
        return result;
    }
    /**
     * 正常查询时需要获取connection，
     * 由于对于一个数据源，查询会比较频繁，因此不再重复生成druid data source 再关闭
     * 通过唯一的名称标记，使用时直接获取
     * @param dataSource 数据源信息
     * @return
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
     * @param dataSource 数据源基本信息
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
     * @param data_source_item_type 数据源类型
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

    /**
     * 执行数据查询，返回数据将value转化为string，方便前端显示
     * @param dataSource 数据源信息
     * @param sql 要执行的sql
     * @param params 参数
     * @return
     * @throws SQLException 将异常抛出，保证执行异常时能数据回滚
     */
    public static List<Map<String,String>> executeQuery_stringValue(DataSource dataSource,String sql, Object[] params) throws SQLException {
        List<Map<String, String>> rows ;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            //获取连接
            conn = getConnection(dataSource);
            //预创建sql
            stmt = conn.prepareStatement(sql);
            //组装参数
            setParameters(stmt, params);
            //获取结果集
            rs = stmt.executeQuery();
            //将结果集转化成list，值为string类型
            rows = resultSetToListString(rs);

        }finally {
            close(conn,rs,stmt);
//            JdbcUtils.close(rs);
//            JdbcUtils.close(stmt);
//            JdbcUtils.close(conn);
        }

        return rows;
    }

    /**
     * 组装sql中的参数
     * @param stmt
     * @param params
     * @throws SQLException
     */
    private static void setParameters(PreparedStatement stmt, Object[] params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
        }
    }
    /**用于一个connection执行一次sql后关闭所有连接
     * 执行sql语句并将结果进行处理，返回list
     * @param dataSource 数据源信息
     * @param sql 要执行的sql
     * @param params 参数
     * @return
     * @throws SQLException 将异常抛出，保证执行异常时能数据回滚
     */
    public static List<Map<String, Object>> executeQuery(DataSource dataSource,String sql, Object[] params) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement= null;
        ResultSet resultSet = null;
        try{
            connection = getConnection(dataSource);
            //sql+参数
            statement = connection.prepareStatement(sql);
            // 参数赋值
            setParameters(statement, params);
            // 执行
            resultSet =  statement.executeQuery();
            //处理resultset数据，处理后才能关闭statment和resultset
            result = resultSetToList(resultSet);
        } finally {
            close(connection,resultSet,statement);
        }
        return result;
    }


    /**用于一个connection需要执行多个sql后,connection需要在执行完多个sql后再关闭
     * 执行sql语句并将结果进行处理，返回list
     * @param connection 数据库连接
     * @param sql 待执行sql语句
     * @param params 参数，数组类型，用来与sql组装statement
     * @return
     * @throws SQLException 将异常抛出，保证执行异常时能数据回滚
     */
    public static List<Map<String, Object>> executeQuery(Connection connection,String sql, Object[] params) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        PreparedStatement statement= null;
        ResultSet resultSet = null;
        try{
            //sql+参数
            statement = connection.prepareStatement(sql);
            // 参数赋值
            setParameters(statement, params);
            // 执行
            resultSet =  statement.executeQuery();
            //处理resultset数据，处理后才能关闭statment和resultset
            result = resultSetToList(resultSet);
        }finally {
            close(null,resultSet,statement);
        }
        return result;
    }

    /**
     * 执行sql语句，返回执行成功或者失败
     * @param connection 链接
     * @param sql 执行语句
     * @param params 参数
     * @return
     * @throws SQLException
     */
    public static boolean execute(Connection connection,String sql, Object[] params) throws SQLException {

        PreparedStatement statement= null;
        ResultSet resultSet = null;
        try{
            //sql+参数
            statement = connection.prepareStatement(sql);
            // 参数赋值
            setParameters(statement, params);
            // 执行
            return statement.execute();
        }finally {
            close(null,resultSet,statement);
        }
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
            if (resultSet!=null){
                resultSet.close();
            }
        } catch (SQLException e) {
            log.error("resultSet关闭失败"+e.getMessage());
        }
        try{
            if(statement!=null){
                statement.close();
            }
        } catch (SQLException e) {
            log.error("statement关闭失败"+e.getMessage());
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
     * ResultSet转化成list，保留value的类型 ，使用Object
     * @param rst
     * @return
     * @throws SQLException
     */
    private static List<Map<String, Object>> resultSetToList(ResultSet rst) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
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
    /**
     * ResultSet转化成list，将value值转化成string，方便前端显示
     * @param rs
     * @return
     * @throws SQLException
     */
    private static List<Map<String, String>> resultSetToListString(ResultSet rs) throws SQLException {

        List<Map<String, String>> list = new ArrayList<>();
        ResultSetMetaData metadata = rs.getMetaData(); //获得结果集结构信息,元数据
        int columnCount = metadata.getColumnCount();   //获得列数
        while (rs.next()) {
            Map<String,String> rowData = new HashMap<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                int type = metadata.getColumnType(columnIndex);

                if (type == Types.VARCHAR || type == Types.CHAR || type == Types.NVARCHAR || type == Types.NCHAR) {
                    rowData.put(metadata.getColumnLabel(columnIndex), rs.getString(columnIndex));
                    //out.print(rs.getString(columnIndex));
                } else if (type == Types.DATE) {
                    Date date = rs.getDate(columnIndex);
                    if (rs.wasNull()) {
                        //out.print("null");
                        rowData.put(metadata.getColumnLabel(columnIndex), "null");
                    } else {
                        //out.print(date.toString());
                        rowData.put(metadata.getColumnLabel(columnIndex), date.toString());
                    }
                } else if (type == Types.BIT) {
                    boolean value = rs.getBoolean(columnIndex);
                    if (rs.wasNull()) {
                        //out.print("null");
                        rowData.put(metadata.getColumnLabel(columnIndex), "null");
                    } else {
                        //out.print(Boolean.toString(value));
                        rowData.put(metadata.getColumnLabel(columnIndex), Boolean.toString(value));
                    }
                } else if (type == Types.BOOLEAN) {
                    boolean value = rs.getBoolean(columnIndex);
                    if (rs.wasNull()) {
//                        out.print("null");
                        rowData.put(metadata.getColumnLabel(columnIndex), "null");
                    } else {
//                        out.print(Boolean.toString(value));
                        rowData.put(metadata.getColumnLabel(columnIndex), Boolean.toString(value));
                    }
                } else if (type == Types.TINYINT) {
                    byte value = rs.getByte(columnIndex);
                    if (rs.wasNull()) {
//                        out.print("null");
                        rowData.put(metadata.getColumnLabel(columnIndex), "null");
                    } else {
//                        out.print(Byte.toString(value));
                        rowData.put(metadata.getColumnLabel(columnIndex), Byte.toString(value));
                    }
                } else if (type == Types.SMALLINT) {
                    short value = rs.getShort(columnIndex);
                    if (rs.wasNull()) {
//                        out.print("null");
                        rowData.put(metadata.getColumnLabel(columnIndex), "null");
                    } else {
//                        out.print(Short.toString(value));
                        rowData.put(metadata.getColumnLabel(columnIndex), Short.toString(value));
                    }
                } else if (type == Types.INTEGER) {
                    int value = rs.getInt(columnIndex);
                    if (rs.wasNull()) {
//                        out.print("null");
                        rowData.put(metadata.getColumnLabel(columnIndex), "null");
                    } else {
//                        out.print(Integer.toString(value));
                        rowData.put(metadata.getColumnLabel(columnIndex), Integer.toString(value));
                    }
                } else if (type == Types.BIGINT) {
                    long value = rs.getLong(columnIndex);
                    if (rs.wasNull()) {
//                        out.print("null");
                        rowData.put(metadata.getColumnLabel(columnIndex), "null");
                    } else {
//                        out.print(Long.toString(value));
                        rowData.put(metadata.getColumnLabel(columnIndex), Long.toString(value));
                    }
                } else if (type == Types.TIMESTAMP || type == Types.TIMESTAMP_WITH_TIMEZONE) {
//                    out.print(String.valueOf(rs.getTimestamp(columnIndex)));
                    rowData.put(metadata.getColumnLabel(columnIndex), String.valueOf(rs.getTimestamp(columnIndex)));
                } else if (type == Types.DECIMAL) {
//                    out.print(String.valueOf(rs.getBigDecimal(columnIndex)));
                    rowData.put(metadata.getColumnLabel(columnIndex), String.valueOf(rs.getBigDecimal(columnIndex)));
                } else if (type == Types.CLOB) {
                    //oracle字段类型，用来存储大数据，最大可存储4GB
//                    out.print(String.valueOf(rs.getString(columnIndex)));
                    rowData.put(metadata.getColumnLabel(columnIndex), String.valueOf(rs.getString(columnIndex)));
                } else if (type == Types.JAVA_OBJECT) {
                    Object object = rs.getObject(columnIndex);
                    if (rs.wasNull()) {
//                        out.print("null");
                        rowData.put(metadata.getColumnLabel(columnIndex), "null");
                    } else {
//                        out.print(String.valueOf(object));
                        rowData.put(metadata.getColumnLabel(columnIndex), String.valueOf(object));
                    }
                } else if (type == Types.LONGVARCHAR) {
                    Object object = rs.getString(columnIndex);

                    if (rs.wasNull()) {
//                        out.print("null");
                        rowData.put(metadata.getColumnLabel(columnIndex), "null");
                    } else {
//                        out.print(String.valueOf(object));
                        rowData.put(metadata.getColumnLabel(columnIndex), String.valueOf(object));
                    }
                } else if (type == Types.NULL) {
//                    out.print("null");
                    rowData.put(metadata.getColumnLabel(columnIndex), "null");
                } else {
                    Object object = rs.getObject(columnIndex);

                    if (rs.wasNull()) {
//                        out.print("null");
                        rowData.put(metadata.getColumnLabel(columnIndex), "null");
                    } else {
                        if (object instanceof byte[]) {
                            byte[] bytes = (byte[]) object;
                            String text = HexBin.encode(bytes);
//                            out.print(text);
                            rowData.put(metadata.getColumnLabel(columnIndex), text);
                        } else {
//                            out.print(String.valueOf(object));
                            rowData.put(metadata.getColumnLabel(columnIndex), String.valueOf(object));
                        }
                    }
                }
            }
            list.add(rowData);
        }
        return list;
    }


}

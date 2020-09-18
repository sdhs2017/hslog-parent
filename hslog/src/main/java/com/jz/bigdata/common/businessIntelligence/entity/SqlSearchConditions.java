package com.jz.bigdata.common.businessIntelligence.entity;

import com.hs.elsearch.entity.SearchConditions;
import org.apache.commons.beanutils.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * sql查询参数
 */
public class SqlSearchConditions {
    private String tableName; //表名
    private List<SqlSearchColumn> sqlSearchColumns = new ArrayList<>(); //查询的列
    private List<SqlSearchWhere> sqlSearchWheres = new ArrayList<>();//where条件
    private String errorInfo;//异常信息，用来进行回显
    private Integer page;//分页，第几页
    private Integer page_size;//分页，每页多少条

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPage_size() {
        return page_size;
    }

    public void setPage_size(Integer page_size) {
        this.page_size = page_size;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<SqlSearchColumn> getSqlSearchColumns() {
        return sqlSearchColumns;
    }

    public void setSqlSearchColumns(List<SqlSearchColumn> sqlSearchColumns) {
        this.sqlSearchColumns = sqlSearchColumns;
    }

    public List<SqlSearchWhere> getSqlSearchWheres() {
        return sqlSearchWheres;
    }

    public void setSqlSearchWheres(List<SqlSearchWhere> sqlSearchWheres) {
        this.sqlSearchWheres = sqlSearchWheres;
    }
    /**
     *  map转bean
     * @param map 参数map
     * @return
     */
    public SqlSearchConditions mapToBean(Map<String,String[]> map){
        try{
            //其他参数处理
            BeanUtils.populate(this,map);
        }catch(Exception e){
            e.printStackTrace();
        }
        return this;
    }
}

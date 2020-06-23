package com.hs.elsearch.entity;

/**
 * 构建查询条件对象
 * 一个实体类代表一个查询
 */
public class QueryCondition {
    private String searchType;//查询条件的类型，term range
    private String searchField;//查询的字段
    private Object searchValue;//查询字段对应的结果,不同的查询类型所需数据格式也不同，因此使用obj
    private String conntectionType;//连接类型，该查询内的条件的连接方式，and/or

    public QueryCondition(String searchType,String searchField,Object searchValue,String conntectionType ){
        this.searchField = searchField;
        this.searchType = searchType;
        this.searchValue = searchValue;
        this.conntectionType = conntectionType;
    }
    public String getConntectionType() {
        return conntectionType;
    }

    public void setConntectionType(String conntectionType) {
        this.conntectionType = conntectionType;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getSearchField() {
        return searchField;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    public Object getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(Object searchValue) {
        this.searchValue = searchValue;
    }
}

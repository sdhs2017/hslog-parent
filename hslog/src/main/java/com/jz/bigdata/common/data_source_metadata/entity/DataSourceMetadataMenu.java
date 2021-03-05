package com.jz.bigdata.common.data_source_metadata.entity;

import java.util.List;

/**
 * @Author: yiyang
 * @Date: 2021/3/2 9:37
 * @Description: 用于构建前端元数据显示tree的bean
 */
public class DataSourceMetadataMenu {
    private String id;
    private String menuName;//菜单名称
    private String state;//菜单第几级
    private List<DataSourceMetadataMenu> menus;//子节点
    private String data_source_id;//数据源id
    private String database;//数据库
    private String table;//表

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<DataSourceMetadataMenu> getMenus() {
        return menus;
    }

    public void setMenus(List<DataSourceMetadataMenu> menus) {
        this.menus = menus;
    }

    public String getData_source_id() {
        return data_source_id;
    }

    public void setData_source_id(String data_source_id) {
        this.data_source_id = data_source_id;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}

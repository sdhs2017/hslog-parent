package com.jz.bigdata.util;

import java.util.List;

/**
 * @Author: yiyang
 * @Date: 2021/3/26 13:58
 * @Description: 下拉tree bean
 */
public class TreeCombobox {
    private String id;//菜单id
    private String text;//菜单名称
    private List<TreeCombobox> children;//子节点

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<TreeCombobox> getChildren() {
        return children;
    }

    public void setChildren(List<TreeCombobox> children) {
        this.children = children;
    }
}

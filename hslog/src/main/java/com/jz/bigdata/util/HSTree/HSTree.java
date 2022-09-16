package com.jz.bigdata.util.HSTree;

import java.util.List;

/**
 * @Author: yiyang
 * @Date: 2022/6/2 21:36
 * @Description: 用于构建多级tree的数据结构
 */
public class HSTree {
    private String id;//tree节点 id
    private String label;//tree 节点名称
    private int OrderCode;//排序字段
    private List<HSTree> children;//子节点

    public int getOrderCode() {
        return OrderCode;
    }

    public void setOrderCode(int orderCode) {
        OrderCode = orderCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<HSTree> getChildren() {
        return children;
    }

    public void setChildren(List<HSTree> children) {
        this.children = children;
    }
}

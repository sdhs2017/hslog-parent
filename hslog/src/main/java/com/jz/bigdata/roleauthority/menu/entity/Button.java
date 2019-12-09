package com.jz.bigdata.roleauthority.menu.entity;

public class Button {
    private  String id;
    private String buttonName;//按钮名称
    private String buttonID;//按钮id
    private String pk_menu_id;//所属菜单id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getButtonID() {
        return buttonID;
    }

    public void setButtonID(String buttonID) {
        this.buttonID = buttonID;
    }

    public String getPk_menu_id() {
        return pk_menu_id;
    }

    public void setPk_menu_id(String pk_menu_id) {
        this.pk_menu_id = pk_menu_id;
    }
}

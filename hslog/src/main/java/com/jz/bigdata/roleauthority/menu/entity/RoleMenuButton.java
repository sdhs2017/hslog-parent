package com.jz.bigdata.roleauthority.menu.entity;

/**
 * @Author: yiyang
 * @Date: 2022/9/22 10:40
 * @Description:
 */
public class RoleMenuButton {
    private String fk_menuAndButon_id;//菜单或按钮的id
    private String fk_roleid;//角色id

    public String getFk_menuAndButon_id() {
        return fk_menuAndButon_id;
    }

    public void setFk_menuAndButon_id(String fk_menuAndButon_id) {
        this.fk_menuAndButon_id = fk_menuAndButon_id;
    }

    public String getFk_roleid() {
        return fk_roleid;
    }

    public void setFk_roleid(String fk_roleid) {
        this.fk_roleid = fk_roleid;
    }
}

package com.jz.bigdata.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Author: yiyang
 * @Date: 2021/7/23 9:40
 * @Description: 用于读取用户角色权限 菜单配置信息的bean
 */
@Component("RoleAuthorityProperty")
@Configuration
public class RoleAuthorityProperty {

    @Value("#{roleauthority.logMenus}")
    private String logMenus;
    @Value("#{roleauthority.packetMenus}")
    private String packetMenus;
    @Value("#{roleauthority.assetMenus}")
    private String assetMenus;
    @Value("#{roleauthority.visualMenus}")
    private String visualMenus;
    @Value("#{roleauthority.alertMenus}")
    private String alertMenus;
    @Value("#{roleauthority.dsgMenus}")
    private String dsgMenus;
    @Value("#{roleauthority.systemMenus}")
    private String systemMenus;

    @Value("#{roleauthority.buttons}")
    private String buttons;
    @Value("#{roleauthority.systems}")
    private String systems;
    @Value("#{roleauthority.roleMenus}")
    private String roleMenus;
    @Value("#{roleauthority.defaultUsers}")
    private String defaultUsers;
    @Value("#{roleauthority.roles}")
    private String roles;
    @Value("#{roleauthority.equipment_chart_default}")
    private String equipment_chart_default;
    @Value("#{roleauthority.equipment_chart_alert}")
    private String equipment_chart_alert;

    public String getEquipment_chart_default() {
        return equipment_chart_default;
    }

    public void setEquipment_chart_default(String equipment_chart_default) {
        this.equipment_chart_default = equipment_chart_default;
    }

    public String getEquipment_chart_alert() {
        return equipment_chart_alert;
    }

    public void setEquipment_chart_alert(String equipment_chart_alert) {
        this.equipment_chart_alert = equipment_chart_alert;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getButtons() {
        return buttons;
    }

    public void setButtons(String buttons) {
        this.buttons = buttons;
    }

    public String getSystems() {
        return systems;
    }

    public void setSystems(String systems) {
        this.systems = systems;
    }

    public String getRoleMenus() {
        return roleMenus;
    }

    public void setRoleMenus(String roleMenus) {
        this.roleMenus = roleMenus;
    }

    public String getDefaultUsers() {
        return defaultUsers;
    }

    public void setDefaultUsers(String defaultUsers) {
        this.defaultUsers = defaultUsers;
    }

    public String getLogMenus() {
        return logMenus;
    }

    public void setLogMenus(String logMenus) {
        this.logMenus = logMenus;
    }

    public String getPacketMenus() {
        return packetMenus;
    }

    public void setPacketMenus(String packetMenus) {
        this.packetMenus = packetMenus;
    }

    public String getAssetMenus() {
        return assetMenus;
    }

    public void setAssetMenus(String assetMenus) {
        this.assetMenus = assetMenus;
    }

    public String getVisualMenus() {
        return visualMenus;
    }

    public void setVisualMenus(String visualMenus) {
        this.visualMenus = visualMenus;
    }

    public String getAlertMenus() {
        return alertMenus;
    }

    public void setAlertMenus(String alertMenus) {
        this.alertMenus = alertMenus;
    }

    public String getDsgMenus() {
        return dsgMenus;
    }

    public void setDsgMenus(String dsgMenus) {
        this.dsgMenus = dsgMenus;
    }

    public String getSystemMenus() {
        return systemMenus;
    }

    public void setSystemMenus(String systemMenus) {
        this.systemMenus = systemMenus;
    }
}

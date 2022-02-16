package com.jz.bigdata.roleauthority.user.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.jz.bigdata.common.configuration.dao.IConfigurationDao;
import com.jz.bigdata.common.configuration.entity.Configuration;
import com.jz.bigdata.common.configuration.service.IConfigurationService;
import com.jz.bigdata.common.license.LicenseExtra;
import com.jz.bigdata.common.start_execution.cache.ConfigurationCache;
import com.jz.bigdata.roleauthority.menu.entity.Button;
import com.jz.bigdata.roleauthority.menu.entity.Menu;
import com.jz.bigdata.roleauthority.role.entity.Role;
import com.jz.bigdata.roleauthority.system.entity.System;
import com.jz.bigdata.util.*;
import de.schlichtherle.license.LicenseContent;
import joptsimple.internal.Strings;
import net.sf.json.JSONArray;
import org.joda.time.DateTime;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.department.dao.IDepartmentDao;
import com.jz.bigdata.common.department.entity.Department;
import com.jz.bigdata.common.license.VerifyLicense;
import com.jz.bigdata.common.note.dao.INoteDao;
import com.jz.bigdata.common.note.entity.Note;
import com.jz.bigdata.roleauthority.user.dao.IUserDao;
import com.jz.bigdata.roleauthority.user.entity.User;
import com.jz.bigdata.roleauthority.user.service.IUserService;
import com.jz.bigdata.roleauthority.user.util.Page;

import net.sf.json.JSONObject;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @author shichengyu
 * @date 2017年8月1日 上午10:06:51
 * @description 用户管理相关模块
 */
@Service(value="UserService")
public class UserServiceImpl implements IUserService {

	private final DateTimeFormatter dtf_time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	//不同模块对应的菜单
	//日志模块
//	private final String logMenus = "[{\"id\":\"10000\",\"menuName\":\"首页\",\"superiorId\":\"\",\"childId\":\"0\",\"url\":\"\",\"icon\":\"fa fa-desktop\",\"state\":\"1\",\"fk_system_id\":\"log\"},{\"id\":\"10500\",\"menuName\":\"日志管理\",\"superiorId\":\"\",\"childId\":\"5\",\"url\":\"\",\"icon\":\"fa fa-list-alt\",\"state\":\"1\",\"fk_system_id\":\"log\"},{\"id\":\"10700\",\"menuName\":\"事件管理\",\"superiorId\":\"\",\"childId\":\"8\",\"url\":\"\",\"icon\":\"fa fa-file-text-o\",\"state\":\"1\",\"fk_system_id\":\"log\"},{\"id\":\"10701\",\"menuName\":\"事件组\",\"superiorId\":\"10700\",\"childId\":\"0\",\"url\":\"eventGroup/eventGroup.vue\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"log\"},{\"id\":\"10800\",\"menuName\":\"平台管理\",\"superiorId\":\"\",\"childId\":\"9\",\"url\":\"\",\"icon\":\"fa fa-th\",\"state\":\"1\",\"fk_system_id\":\"log\"},{\"id\":\"10801\",\"menuName\":\"审计中心\",\"superiorId\":\"10800\",\"childId\":\"0\",\"url\":\"auditLog/auditLog.html\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"log\"},{\"id\":\"1198792f-ee87-4965-9366-a7cdc9e601cc\",\"menuName\":\"复合查询\",\"superiorId\":\"10500\",\"childId\":\"2\",\"url\":\"logsManage/complexSearch.vue\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"log\"},{\"id\":\"20d6b4ef-6079-4d4a-83af-a57baf4c488e\",\"menuName\":\"文件类日志\",\"superiorId\":\"10500\",\"childId\":\"5\",\"url\":\"logsManage/fileLogManage.vue\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"log\"},{\"id\":\"4f80bfab-ce60-4ab3-8b8a-6a3734af37c9\",\"menuName\":\"精确查询\",\"superiorId\":\"10500\",\"childId\":\"1\",\"url\":\"logsManage/accurateSearch2.vue\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"log\"},{\"id\":\"5af2d7e5-5ce4-40d7-8a65-1196b1128873\",\"menuName\":\"首页\",\"superiorId\":\"10000\",\"childId\":\"1\",\"url\":\"index/index_n.vue\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"log\"},{\"id\":\"63d60e7e-9c4c-4abf-a5c8-eef73844eabb\",\"menuName\":\"控制中心\",\"superiorId\":\"10800\",\"childId\":\"1\",\"url\":\"platformManage/controlCenter_n.vue\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"log\"},{\"id\":\"6d5eef70-c72f-4a40-b154-fd88db5c0b39\",\"menuName\":\"事件搜索\",\"superiorId\":\"10700\",\"childId\":\"2\",\"url\":\"eventManage/eventSearch2.vue\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"log\"},{\"id\":\"a5b2a483-bfc4-4b99-a347-966f5f68920f\",\"menuName\":\"全文检索\",\"superiorId\":\"10500\",\"childId\":\"0\",\"url\":\"logsManage/fulltextRetrieval.vue\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"log\"},{\"id\":\"d47006fa-8d2a-4980-81c5-f2aa593253f2\",\"menuName\":\"事件关联查询\",\"superiorId\":\"10700\",\"childId\":\"1\",\"url\":\"eventManage/eventSearch_group.vue\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"log\"}]";
//	//流量模块
//	private final String packetMenus = "[{\"id\":\"10803\",\"menuName\":\"流量控制中心\",\"superiorId\":\"11200\",\"childId\":\"2\",\"url\":\"auditLog/flowServiceManage.html\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"packet\"},{\"id\":\"10900\",\"menuName\":\"流量管理\",\"superiorId\":\"\",\"childId\":\"6\",\"url\":\"\",\"icon\":\"fa fa-code-fork\",\"state\":\"1\",\"fk_system_id\":\"packet\"},{\"id\":\"10901\",\"menuName\":\"业务流分析\",\"superiorId\":\"11500\",\"childId\":\"10\",\"url\":\"logPro/networkTopology.html\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"packet\"},{\"id\":\"10902\",\"menuName\":\"流量日志\\r\\n\",\"superiorId\":\"10900\",\"childId\":\"0\",\"url\":\"logPro/flowLogs.html\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"packet\"},{\"id\":\"10903\",\"menuName\":\"HTTP日志\",\"superiorId\":\"10900\",\"childId\":\"2\",\"url\":\"logPro/httpLogs.html\\r\\n\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"packet\"},{\"id\":\"10904\",\"menuName\":\"服务列表 \",\"superiorId\":\"10900\",\"childId\":\"3\",\"url\":\"device/serviceList.html\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"packet\"},{\"id\":\"10905\",\"menuName\":\"应用画像\",\"superiorId\":\"10900\",\"childId\":\"4\",\"url\":\"logPro/urlRanking.html\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"packet\"},{\"id\":\"11000\",\"menuName\":\"流量监控\",\"superiorId\":\"\",\"childId\":\"7\",\"url\":\"\",\"icon\":\"fa fa-clock-o\",\"state\":\"1\",\"fk_system_id\":\"packet\"},{\"id\":\"11100\",\"menuName\":\"首页\",\"superiorId\":\"\",\"childId\":\"0\",\"url\":\"\",\"icon\":\"fa fa-desktop\",\"state\":\"1\",\"fk_system_id\":\"packet\"},{\"id\":\"11101\",\"menuName\":\"首页 \",\"superiorId\":\"11100\",\"childId\":\"0\",\"url\":\"flowManage/flowIndex\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"packet\"},{\"id\":\"11200\",\"menuName\":\"平台管理\",\"superiorId\":\"\",\"childId\":\"10\",\"url\":\"\",\"icon\":\"fa fa-th\",\"state\":\"1\",\"fk_system_id\":\"packet\"},{\"id\":\"11500\",\"menuName\":\"业务流分析\",\"superiorId\":\"\",\"childId\":\"9\",\"url\":\"\",\"icon\":\"fa fa-object-ungroup\",\"state\":\"1\",\"fk_system_id\":\"packet\"},{\"id\":\"40b6133c-f455-4fc0-9e80-e8cc9ea9fdee\",\"menuName\":\"广播包/组播包\",\"superiorId\":\"11000\",\"childId\":\"5\",\"url\":\"flowManage/mulAndBro\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"packet\"},{\"id\":\"525a59b7-e794-4d25-9b5a-ac3926498fcc\",\"menuName\":\"IP主机流量\",\"superiorId\":\"11000\",\"childId\":\"3\",\"url\":\"flowManage/IPHostFlow\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"packet\"},{\"id\":\"56d24f8e-b552-4f23-905f-13a267de2e8c\",\"menuName\":\"资产流量\",\"superiorId\":\"11000\",\"childId\":\"7\",\"url\":\"flowManage/equipmentFlow\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"packet\"},{\"id\":\"636c9ae1-5848-4799-bd89-64a64988b678\",\"menuName\":\"数据包类型\",\"superiorId\":\"11000\",\"childId\":\"6\",\"url\":\"flowManage/packetType\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"packet\"},{\"id\":\"7bb1e4c7-bf1d-4920-b885-a76b6e6b4835\",\"menuName\":\"应用性能分析\",\"superiorId\":\"11000\",\"childId\":\"10\",\"url\":\"flow/performanceAnalysis.vue\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"packet\"},{\"id\":\"8f34b263-6606-489d-a880-10493e3d2f79\",\"menuName\":\"全局端口流量\",\"superiorId\":\"11000\",\"childId\":\"8\",\"url\":\"flowManage/portFlow\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"packet\"},{\"id\":\"a4b36b08-37c4-4a83-b69b-b4631967280c\",\"menuName\":\"User-Agent信息\",\"superiorId\":\"11000\",\"childId\":\"9\",\"url\":\"flowManage/userAgentInfo\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"packet\"},{\"id\":\"c323f476-d17a-4fc3-9fdf-bdc49f879f97\",\"menuName\":\"协议流量\",\"superiorId\":\"11000\",\"childId\":\"4\",\"url\":\"flowManage/protocolFlow\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"packet\"},{\"id\":\"cbffef63-8f45-4817-9946-1e230f1d16c4\",\"menuName\":\"全局实时流量\",\"superiorId\":\"11000\",\"childId\":\"2\",\"url\":\"flowManage/realTimeFlow\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"packet\"}]";
//	//资产管理模块
//	private final String assetMenus = "[{\"id\":\"10300\",\"menuName\":\"资产管理\",\"superiorId\":\"\",\"childId\":\"3\",\"url\":\"\",\"icon\":\"fa fa-laptop\",\"state\":\"1\",\"fk_system_id\":\"asset\"},{\"id\":\"10306\",\"menuName\":\"资产概览\",\"superiorId\":\"10300\",\"childId\":\"0\",\"url\":\"device/device2.html\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"asset\"}]";
//	//可视化模块
//	private final String visualMenus = "[{\"id\":\"11400\",\"menuName\":\"数据可视化\",\"superiorId\":\"\",\"childId\":\"9\",\"url\":\"\",\"icon\":\"fa fa-lightbulb-o\",\"state\":\"1\",\"fk_system_id\":\"visual\"},{\"id\":\"93a2eaa7-ec05-4bc3-bcdf-6fd39c97e6d5\",\"menuName\":\"元数据\",\"superiorId\":\"11400\",\"childId\":\"3\",\"url\":\"dashboard/property.vue\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"visual\"},{\"id\":\"c465f4c6-192d-4c46-b7ad-e700fae0b542\",\"menuName\":\"仪表板\",\"superiorId\":\"11400\",\"childId\":\"1\",\"url\":\"dashboard/dashboardList.vue\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"visual\"},{\"id\":\"fded6532-6724-4862-b155-7d9dd37a8502\",\"menuName\":\"图表列表\",\"superiorId\":\"11400\",\"childId\":\"2\",\"url\":\"dashboard/chartsList.vue\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"visual\"}]";
//	//告警模块
//	private final String alertMenus = "[{\"id\":\"075619da-5819-423e-b5cf-8a323c458163\",\"menuName\":\"事件告警\",\"superiorId\":\"11600\",\"childId\":\"2\",\"url\":\"alarmManage/eventAlarmList.vue\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"alert\"},{\"id\":\"11600\",\"menuName\":\"告警管理\",\"superiorId\":\"\",\"childId\":\"5\",\"url\":\"\",\"icon\":\"fa fa-bell-o\",\"state\":\"1\",\"fk_system_id\":\"alert\"},{\"id\":\"11700\",\"menuName\":\"首页\",\"superiorId\":\"\",\"childId\":\"0\",\"url\":\"\",\"icon\":\"fa fa-desktop\",\"state\":\"1\",\"fk_system_id\":\"alert\"},{\"id\":\"3858056b-9baf-44fc-8116-5fbf2a7cb5cf\",\"menuName\":\"自定义告警\",\"superiorId\":\"11600\",\"childId\":\"1\",\"url\":\"alarmManage/alarmList.vue\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"alert\"},{\"id\":\"dcc19de1-492e-42e7-b5f9-fae245879ea5\",\"menuName\":\"告警\",\"superiorId\":\"11700\",\"childId\":\"0\",\"url\":\"alarmManage/alert.vue\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"alert\"},{\"id\":\"f0a9c2fe-7581-42e1-a3bd-735fd4ed0018\",\"menuName\":\"异常检测\",\"superiorId\":\"11800\",\"childId\":\"1\",\"url\":\"test/detectionAnomalies.vue\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"alert\"}]";
//	//数据资产安全治理模块
//	private final String dsgMenus = "[{\"id\":\"11900\",\"menuName\":\"数据安全治理\",\"superiorId\":\"\",\"childId\":\"0\",\"url\":\"\",\"icon\":\"fa fa-database\",\"state\":\"1\",\"fk_system_id\":\"dsg\"},{\"id\":\"6aa9f5b3-c208-4ee2-bb5b-073a04190e17\",\"menuName\":\"元数据管理\",\"superiorId\":\"11900\",\"childId\":\"1\",\"url\":\"dataSource/metadata.vue\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"dsg\"},{\"id\":\"b7ef8a1f-80e2-4405-ad32-bfd1a0cfed25\",\"menuName\":\"分类分级配置\",\"superiorId\":\"11900\",\"childId\":\"2\",\"url\":\"dataSource/classification.vue\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"dsg\"},{\"id\":\"fc7ae83d-a0b9-447c-8567-bd621d2c5185\",\"menuName\":\"发现规则配置\",\"superiorId\":\"11900\",\"childId\":\"3\",\"url\":\"dataSource/tabManage.vue\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"dsg\"},{\"id\":\"fdbd7be3-eb5d-4372-a322-d016b8b5f21d\",\"menuName\":\"数据源管理\",\"superiorId\":\"11900\",\"childId\":\"0\",\"url\":\"dataSource/dataSourceIndex.vue\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"dsg\"}]";
//	//系统管理模块
//	private final String systemMenus = "[{\"id\":\"10100\",\"menuName\":\"系统管理\",\"superiorId\":\"\",\"childId\":\"1\",\"url\":\"\",\"icon\":\"fa fa-users\",\"state\":\"1\",\"fk_system_id\":\"system\"},{\"id\":\"10101\",\"menuName\":\"用户管理 \",\"superiorId\":\"10100\",\"childId\":\"3\",\"url\":\"userManage/userManage.html\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"system\"},{\"id\":\"10103\",\"menuName\":\"角色管理 \",\"superiorId\":\"10100\",\"childId\":\"2\",\"url\":\"roleManage/roleManage.html\",\"icon\":\"\",\"state\":\"1\",\"fk_system_id\":\"system\"}]";
//	//按钮，目前按钮都集中在角色管理（master）用户中，不会因为不同产品发生变化，因此相对固定。
//	private final String buttons = "[{\"id\":\"4936cd84-bf86-11eb-a219-3085a9a7d2b3\",\"buttonName\":\"添加机构\",\"buttonID\":\"userManage_addDepartment\",\"pk_menu_id\":\"10101\"},{\"id\":\"4936df5a-bf86-11eb-a219-3085a9a7d2b3\",\"buttonName\":\"修改机构\",\"buttonID\":\"userManage_reviseDepartment\",\"pk_menu_id\":\"10101\"},{\"id\":\"4936f055-bf86-11eb-a219-3085a9a7d2b3\",\"buttonName\":\"删除机构\",\"buttonID\":\"userManage_deleteDepartment\",\"pk_menu_id\":\"10101\"},{\"id\":\"49370094-bf86-11eb-a219-3085a9a7d2b3\",\"buttonName\":\"添加用户\",\"buttonID\":\"userManage_addUser\",\"pk_menu_id\":\"10101\"},{\"id\":\"493710b1-bf86-11eb-a219-3085a9a7d2b3\",\"buttonName\":\"删除用户\",\"buttonID\":\"userManage_deleteUser\",\"pk_menu_id\":\"10101\"},{\"id\":\"49372019-bf86-11eb-a219-3085a9a7d2b3\",\"buttonName\":\"修改用户\",\"buttonID\":\"userManage_reviseUser\",\"pk_menu_id\":\"10101\"},{\"id\":\"49372f4d-bf86-11eb-a219-3085a9a7d2b3\",\"buttonName\":\"批量删除用户\",\"buttonID\":\"userManage_moredeleteUser\",\"pk_menu_id\":\"10101\"},{\"id\":\"49373f2a-bf86-11eb-a219-3085a9a7d2b3\",\"buttonName\":\"添加用户_无角色\",\"buttonID\":\"userManage_addUser2\",\"pk_menu_id\":\"10101\"},{\"id\":\"49374ea3-bf86-11eb-a219-3085a9a7d2b3\",\"buttonName\":\"设置权限\",\"buttonID\":\"userManage_setRole\",\"pk_menu_id\":\"10101\"},{\"id\":\"49375e7f-bf86-11eb-a219-3085a9a7d2b3\",\"buttonName\":\"密码过期设置1-7d\",\"buttonID\":\"userManage_expireDate\",\"pk_menu_id\":\"10101\"},{\"id\":\"49376ddd-bf86-11eb-a219-3085a9a7d2b3\",\"buttonName\":\"密码过期无限制\",\"buttonID\":\"userManage_expireDate_1_7\",\"pk_menu_id\":\"10101\"},{\"id\":\"a09d243a-e73c-11ea-a0a5-000c2900a492\",\"buttonName\":\"资产可视化按钮\",\"buttonID\":\"equipment2_dashboard\",\"pk_menu_id\":\"10306\"},{\"id\":\"f05549c4-cdd9-11eb-a219-3085a9a7d2b3\",\"buttonName\":\"添加资产\",\"buttonID\":\"equipment_addEquipment\",\"pk_menu_id\":\"10301\"},{\"id\":\"f06011ba-cdd9-11eb-a219-3085a9a7d2b3\",\"buttonName\":\"删除资产\",\"buttonID\":\"equipment_moredeleteEquipment\",\"pk_menu_id\":\"10301\"},{\"id\":\"f06af21e-cdd9-11eb-a219-3085a9a7d2b3\",\"buttonName\":\"所有资产报表\",\"buttonID\":\"equipment_allCharts\",\"pk_menu_id\":\"10301\"},{\"id\":\"f082050e-cdd9-11eb-a219-3085a9a7d2b3\",\"buttonName\":\"修改资产\",\"buttonID\":\"equipment_reviseEquipment\",\"pk_menu_id\":\"10301\"},{\"id\":\"f08d357e-cdd9-11eb-a219-3085a9a7d2b3\",\"buttonName\":\"查询资产日志\",\"buttonID\":\"equipment_equipmentLogs\",\"pk_menu_id\":\"10301\"},{\"id\":\"f097def1-cdd9-11eb-a219-3085a9a7d2b3\",\"buttonName\":\"查询资产报表\",\"buttonID\":\"equipment_equipmentCharts\",\"pk_menu_id\":\"10301\"},{\"id\":\"f0a27b6c-cdd9-11eb-a219-3085a9a7d2b3\",\"buttonName\":\"查询资产事件\",\"buttonID\":\"equipment_equipmentEvents\",\"pk_menu_id\":\"10301\"},{\"id\":\"f0ad32c9-cdd9-11eb-a219-3085a9a7d2b3\",\"buttonName\":\"设置安全策略\",\"buttonID\":\"equipment_equipmentSafe\",\"pk_menu_id\":\"10301\"},{\"id\":\"f0b8245f-cdd9-11eb-a219-3085a9a7d2b3\",\"buttonName\":\"潜在威胁分析\",\"buttonID\":\"equipment_equipmentThreat\",\"pk_menu_id\":\"10301\"}]";
//	//系统，声明所有系统，根据license中要求的不同系统进行筛选。
//	private final String systems = "[{\"id\":\"alert\",\"sys_name\":\"告警监测\",\"note\":\"\",\"icon\":\"fa fa-bell-o\",\"order\":\"5\"},{\"id\":\"asset\",\"sys_name\":\"资产管理\",\"note\":\"\",\"icon\":\"fa fa-laptop\",\"order\":\"3\"},{\"id\":\"dsg\",\"sys_name\":\"数据安全治理\",\"note\":\"\",\"icon\":\"fa fa-database\",\"order\":\"6\"},{\"id\":\"log\",\"sys_name\":\"日志系统\",\"note\":\"\",\"icon\":\"fa fa-list-alt\",\"order\":\"1\"},{\"id\":\"packet\",\"sys_name\":\"流量管理\",\"note\":\"\",\"icon\":\"fa fa-code-fork\",\"order\":\"2\"},{\"id\":\"system\",\"sys_name\":\"系统管理\",\"note\":\"\",\"icon\":\"\",\"order\":\"1\"},{\"id\":\"visual\",\"sys_name\":\"数据可视化\",\"note\":\"\",\"icon\":\"fa fa-lightbulb-o\",\"order\":\"4\"}]";
//	//角色，目前所有系列产品提供两个基础角色，超级管理员(master)和操作管理员(admin)
//	private final String roles = "[{\"id\":\"admin\",\"role_name\":\"系统管理员\",\"note\":\"涵盖大部分功能模块的增删改查功能\"},{\"id\":\"master\",\"role_name\":\"超级管理员\",\"note\":\"\"}]";
//	//角色权限，在角色权限关系表中，设置该角色对全模块的菜单，后台可根据是否有该菜单控制显示，不会出现异常
//	private final String roleMenus = "[{\"fk_menuAndButon_id\":\"10103\",\"fk_roleid\":\"master\"},{\"fk_menuAndButon_id\":\"10101\",\"fk_roleid\":\"master\"},{\"fk_menuAndButon_id\":\"c465f4c6-192d-4c46-b7ad-e700fae0b542\",\"fk_roleid\":\"master\"},{\"fk_menuAndButon_id\":\"fded6532-6724-4862-b155-7d9dd37a8502\",\"fk_roleid\":\"master\"},{\"fk_menuAndButon_id\":\"93a2eaa7-ec05-4bc3-bcdf-6fd39c97e6d5\",\"fk_roleid\":\"master\"},{\"fk_menuAndButon_id\":\"system\",\"fk_roleid\":\"master\"},{\"fk_menuAndButon_id\":\"10100\",\"fk_roleid\":\"master\"},{\"fk_menuAndButon_id\":\"visual\",\"fk_roleid\":\"master\"},{\"fk_menuAndButon_id\":\"11400\",\"fk_roleid\":\"master\"},{\"fk_menuAndButon_id\":\"4936cd84-bf86-11eb-a219-3085a9a7d2b3\",\"fk_roleid\":\"master\"},{\"fk_menuAndButon_id\":\"4936df5a-bf86-11eb-a219-3085a9a7d2b3\",\"fk_roleid\":\"master\"},{\"fk_menuAndButon_id\":\"4936f055-bf86-11eb-a219-3085a9a7d2b3\",\"fk_roleid\":\"master\"},{\"fk_menuAndButon_id\":\"493710b1-bf86-11eb-a219-3085a9a7d2b3\",\"fk_roleid\":\"master\"},{\"fk_menuAndButon_id\":\"49372019-bf86-11eb-a219-3085a9a7d2b3\",\"fk_roleid\":\"master\"},{\"fk_menuAndButon_id\":\"49372f4d-bf86-11eb-a219-3085a9a7d2b3\",\"fk_roleid\":\"master\"},{\"fk_menuAndButon_id\":\"49373f2a-bf86-11eb-a219-3085a9a7d2b3\",\"fk_roleid\":\"master\"},{\"fk_menuAndButon_id\":\"49374ea3-bf86-11eb-a219-3085a9a7d2b3\",\"fk_roleid\":\"master\"},{\"fk_menuAndButon_id\":\"49375e7f-bf86-11eb-a219-3085a9a7d2b3\",\"fk_roleid\":\"master\"},{\"fk_menuAndButon_id\":\"11700\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"dcc19de1-492e-42e7-b5f9-fae245879ea5\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"11600\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"3858056b-9baf-44fc-8116-5fbf2a7cb5cf\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"075619da-5819-423e-b5cf-8a323c458163\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"10306\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"dsg\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"11900\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"fdbd7be3-eb5d-4372-a322-d016b8b5f21d\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"6aa9f5b3-c208-4ee2-bb5b-073a04190e17\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"b7ef8a1f-80e2-4405-ad32-bfd1a0cfed25\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"fc7ae83d-a0b9-447c-8567-bd621d2c5185\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"10000\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"5af2d7e5-5ce4-40d7-8a65-1196b1128873\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"a5b2a483-bfc4-4b99-a347-966f5f68920f\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"4f80bfab-ce60-4ab3-8b8a-6a3734af37c9\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"1198792f-ee87-4965-9366-a7cdc9e601cc\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"10701\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"d47006fa-8d2a-4980-81c5-f2aa593253f2\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"6d5eef70-c72f-4a40-b154-fd88db5c0b39\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"10801\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"63d60e7e-9c4c-4abf-a5c8-eef73844eabb\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"11100\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"11101\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"10900\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"10902\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"10903\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"10904\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"10905\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"11000\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"cbffef63-8f45-4817-9946-1e230f1d16c4\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"525a59b7-e794-4d25-9b5a-ac3926498fcc\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"c323f476-d17a-4fc3-9fdf-bdc49f879f97\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"40b6133c-f455-4fc0-9e80-e8cc9ea9fdee\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"636c9ae1-5848-4799-bd89-64a64988b678\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"56d24f8e-b552-4f23-905f-13a267de2e8c\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"8f34b263-6606-489d-a880-10493e3d2f79\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"a4b36b08-37c4-4a83-b69b-b4631967280c\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"7bb1e4c7-bf1d-4920-b885-a76b6e6b4835\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"11500\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"10901\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"11200\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"10803\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"c465f4c6-192d-4c46-b7ad-e700fae0b542\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"fded6532-6724-4862-b155-7d9dd37a8502\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"93a2eaa7-ec05-4bc3-bcdf-6fd39c97e6d5\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"alert\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"asset\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"10300\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"log\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"10500\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"10700\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"10800\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"packet\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"visual\",\"fk_roleid\":\"admin\"},{\"fk_menuAndButon_id\":\"11400\",\"fk_roleid\":\"admin\"}]";
//
//	//内置账号信息 一般为master账号
//	private final String defaultUsers = "[{\"id\":\"c758041e-1f65-4e3a-91d0-1714fe08f1c0\",\"phone\":\"master\",\"password\":\"4QrcOUm6Wau+VuBX8g+IPg==\",\"name\":\"master\",\"sex\":\"1\",\"age\":\"\",\"email\":\"\",\"departmentId\":\"0\",\"state\":\"1\"}]";

	//角色 菜单中间表  列名
	private final String FK_MENU_KEY = "fk_menuAndButon_id";
	private final String FK_ROLE_KEY = "fk_roleid";
	@Resource
	private IUserDao userDao;
	
	@Resource
	private IDepartmentDao departmentDao;
	
	@Resource
	private INoteDao noteDao;
	
	@Resource(name = "licenseService")
	private VerifyLicense verifyLicense;
	@Resource(name ="configProperty")
	private ConfigProperty configProperty;
	@Resource
	private IConfigurationDao configurationDao;
	@Resource(name = "ConfigurationService")
	private IConfigurationService configurationService;
	@Resource(name ="RoleAuthorityProperty")
	private RoleAuthorityProperty roleAuthorityProperty;
	/**
	 * @param user
	 * @return
	 * @description
	 * 添加数据
	 */
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
	public int insert(User user) {
		//默认添加时用户状态正常
		user.setState(1);
		user.setPassword(MD5.EncoderByMd5(user.getPassword()));
		//新建用户时，添加密码最新的更新时间
		user.setPwd_update_time(LocalDateTime.now().format(dtf_time));
		String[] roleIDs = user.getRole().split(",");
		for(String roleID : roleIDs){//遍历角色id   添加到用户角色中间表中
			userDao.insertUserRole(user.getId(),roleID);
		}
		return userDao.insert(user);
	}


	/**
	 * @return
	 * @description
	 * 查询所有数据
	 */
	public List<User> selectAll(User user) {
		return userDao.selectAll(user);
	}

	/**
	 * @param ids
	 * @return
	 * @description
	 * 根据id删除数据
	 */
	@Override
	public int delete(String[] ids) {
		return userDao.delete(ids);
	}

	/**
	 * @param user
	 * @return
	 * @description
	 * 修改数据
	 * 
	 */
	@Override
	public int updateById(User user) {
		userDao.deleteUserRole(user.getId());
		String[] roleIDs = user.getRole().split(",");
		for(String roleID : roleIDs){//遍历角色id   添加到用户角色中间表中
			userDao.insertUserRole(user.getId(),roleID);
		}
		int tt= userDao.updateById(user);
//		int t=1/0;
		return tt;
	}


	/**
	 * @param page
	 * @return
	 * @description
	 * 分页查询
	 */
	@Override
	public Map<String,Object> selectPage(Page page,HttpSession session) {
//		开始数
		page.setStartRecord(page.getPageSize()*(page.getPageIndex()-1));
		//page.setRole(Integer.valueOf((String) session.getAttribute(Constant.SESSION_USERROLE)));
		page.setId((String) session.getAttribute(Constant.SESSION_USERID));
//		总数
		List<String> count=userDao.count(page);
		Map<String,Object> map =new HashMap<String,Object>();
//		List li=new ArrayList<>();
//		添加到map，拦截器多结果返回原因，取第一个结果
		map.put("count", (count.get(0)));
//		List<Map<String,Object>> list=new ArrayList<>();
//		用户信息添加到map
		List<User> listUser= userDao.selectPage(page);
		
		map.put("user", listUser);
//		list.add(map);
		return map;  
	}



	/**
	 * @param id
	 * @return
	 * @description
	 * 通过id查询单个用户
	 */
	@Override
	public List<User> selectUser(String id) {
		return userDao.selectUser(id);
	}


	/**
	 * @param id
	 * @return
	 * @description
	 * 根据id返回单个数据信息
	 */
	@Override
	public User selectById(String id) {
		List<List<Map<String,Object>>> role = userDao.selectRoleByUserID(id);
		User u = userDao.selectById(id);
		u.setRole(role.get(0).get(0).get("role")==null?"":role.get(0).get(0).get("role").toString());
		return u;
	}

	/**
	 * @param user
	 * @param session
	 * @return 是否登陆成功 true/false
	 * @description 登陆操作
	 */
	public String login(User user, HttpSession session, HttpServletRequest request) throws Exception {
		//验证登录信息是否在登录黑名单

		//查询账号密码对应的用户信息
		// 2021-3-16 需求：前端传的账号密码要加密，目前通过RSA加密解密，因此需要先解密
		user.setPhone(RSAUtil.decrypt(user.getPhone(),RSAUtil.getPrivateKey()));
		user.setPassword(MD5.EncoderByMd5(RSAUtil.decrypt(user.getPassword(),RSAUtil.getPrivateKey())));
		String ip = getIpAddr(request);
		Object ip_black = ConfigurationCache.INSTANCE.getConfigurationCache().getIfPresent(Constant.IP_BLACK);
		if(ip_black!=null&&ip_black.toString().indexOf(ip)>=0){
			return Constant.failureMessage("IP被限制登录！");
		}
		//user.setPassword(MD5.EncoderByMd5(user.getPassword()));
		//通过账号和密码查询用户信息
		User _user = userDao.selectByPhonePwd(user);
		Map<String,Object> map =new HashMap<String,Object>();
		//首页，用于登录成功后的跳转
		map.put("homepage",configProperty.getHomepage_url());
		//获取参数
		verifyLicense.setParam("/verifyparam.properties");
		//验证证书
		Boolean vresult = verifyLicense.verify();
		//证书是否存在
		if (vresult) {
			//获取证书中的信息（客户名称以及到期时间）
			LicenseContent licenseContent = verifyLicense.getLicenseContent();
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = licenseContent.getNotAfter();
			map.put("license_due_time",format.format(date));
			LicenseExtra extra = (LicenseExtra)licenseContent.getExtra();
			map.put("custom_name",extra.getCustom_name());
			//是否有账号密码都匹配的账号信息
			if(_user!=null){
				//状态值为2 账号停用
				if(_user.getState()==2){
					map.put("success", "false");
					map.put("message", "账号暂停服务");
					return JSONObject.fromObject(map).toString();
				}else if(_user.getState()==0){//状态值为 0 账号被锁定
					//TODO 判定账号能否解锁
					map.put("success", "false");
					map.put("message", "您已连续多次输入密码错误，账号已被锁定");
					return JSONObject.fromObject(map).toString();
				}else{//账号正常
					Department department= departmentDao.selectDepartment((_user.getDepartmentId())+"");
					session.setAttribute(Constant.SESSION_USERID, _user.getId());
					session.setAttribute(Constant.SESSION_USERNAME, _user.getName());
					session.setAttribute(Constant.SESSION_USERACCOUNT, _user.getPhone());

					//是否有所属部门
					if(_user.getDepartmentId()!=0){
						session.setAttribute(Constant.SESSION_DEPARTMENTNAME, department.getName());
						session.setAttribute(Constant.SESSION_DEPARTMENTID, _user.getDepartmentId());
						session.setAttribute(Constant.SESSION_DEPARTMENTNODEID, department.getDepartmentNodeId());
					}

					session.setAttribute(Constant.SESSION_ID, session.getId());
					//设置超时时间
					String timeout = ConfigurationCache.INSTANCE.getConfigurationCache().getIfPresent(Constant.SESSION_TIMEOUT).toString();
					//设置超时时间
					session.setMaxInactiveInterval(Integer.parseInt(timeout)*60);
					//账号密码超时判定
					//获取超时时间，单位：天
					List<Configuration> result = configurationDao.selectByKey(Constant.PWD_EXPIRE_DAY_NAME);
					int pwd_expire_day = 0;
					if(result.size()==1&&result.get(0).getConfiguration_key().equals(Constant.PWD_EXPIRE_DAY_NAME)){
						pwd_expire_day = Integer.parseInt(result.get(0).getConfiguration_value());
					}
					//TODO 角色相关模块的处理
					User userInfo = this.selectById(_user.getId());
					session.setAttribute(Constant.SESSION_USERROLE, userInfo.getRole());
					//session.setAttribute(Constant.SESSION_USERROLE, "1");
					//获取密码的更新时间
					String update_time = _user.getPwd_update_time();
					//该字段无信息，自动判断为超时，进行密码修改
					//返回的时间格式不准确，秒数字后带  .0 需要进行截取
					if(!Strings.isNullOrEmpty(update_time)&&update_time.length()>=19){
						LocalDateTime update_date_time = LocalDateTime.parse(update_time.substring(0,19), dtf_time);
						//如果已经超时，提示
						if(update_date_time.isBefore(LocalDateTime.now().minusDays(pwd_expire_day))){
							map.put("success", "true");
							map.put("message", "密码已过期，请修改密码！");
							map.put("state","0");//需重置密码
							map.put("user", _user);
							return JSONObject.fromObject(map).toString();
						}
					}
					//其他情况为正常登陆
					map.put("success", "true");
					map.put("state","1");
					map.put("message", "登录成功");
					map.put("user", _user);
					return JSONObject.fromObject(map).toString();
				}
			}else{
				map.put("success", "false");
				map.put("message", "登录失败，账号或密码错误");
			}
			//计算当前时间之前的半小时的起始和截至时间
			LocalDateTime end = LocalDateTime.now();
			//当前时间-截止时间
			String endTime = end.format(dtf_time);
			//起始时间
			String startTime = end.minusMinutes(30).format(dtf_time);


			//获取密码尝试次数
			String tryCount = ConfigurationCache.INSTANCE.getConfigurationCache().getIfPresent(Constant.PWD_TRY).toString();
			// 查询半小时以内近X次的账号登录状态
			List<Note> list=noteDao.selectLimitNote(user.getPhone(), startTime, endTime,tryCount!=null?Integer.parseInt(tryCount):5);
			Boolean res = false;
			//判断登录密码次数过多，默认5次
			if(list.size()>=(tryCount!=null?Integer.parseInt(tryCount):5)){
				for(int i=0;i<list.size();i++){
					if(list.get(i).getResult()==1){
						res=true;
					}
				}
				//修改状态，为了保证不覆盖停用状态以及锁定的重复更新，更新时增加条件 状态位不为2和0
				if(res==false){
					userDao.updateByPhone(user.getPhone());
				}
			}
			map.put("success", "false");
			map.put("message", "登录失败，账号或密码错误");
			return JSONObject.fromObject(map).toString();
		//无授权
		}else {
			map.put("success", "false");
			map.put("message", "产品已过期");
			return JSONObject.fromObject(map).toString();
		}

	}
	
	public static void main(String[] agrs){
//		List<Menu> menu = JSONUtil.getBeanListByJSONArrayString(logMenu,Menu.class);
//		int a=1;
//		JSONArray array = JSONArray.fromObject(logRoleMenu);
//		for(Object obj:array.toArray()){
//			JSONObject jsonObject = JSONObject.fromObject(obj);
//			JSONObject.fromObject(obj).get("fk_menuAndButon_id");
//			JSONObject.fromObject(obj).get("fk_roleid");
//
//		}
//	   Date d=new Date();
//	   SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	   System.out.println("今天的日期："+df.format(d));
//	   System.out.println("两天前的日期：" + df.format(new Date(d.getTime() -  30 * 60 * 1000)));
//	   System.out.println("三天后的日期：" + df.format(new Date(d.getTime() + 3 * 24 * 60 * 60 * 1000)));
	}
	

	/**
	 * @param session
	 * @return
	 * @description 验证session 信息
	 */
	public Boolean checkLogin(HttpSession session) {
		if(session.getAttribute(Constant.SESSION_USERID)!=null&&session.getId().equals(session.getAttribute(Constant.SESSION_ID))){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * @param session
	 * @return
	 * @description 登陆，清空session
	 */
	public Boolean loginOut(HttpSession session) {
		session.removeAttribute(Constant.SESSION_ID);
		session.removeAttribute(Constant.SESSION_USERID);
		session.removeAttribute(Constant.SESSION_DEPARTMENTID);
		return true;
	}


	/**
	 * @param session
	 * @return
	 * @description
	 * 获取角色用户信息
	 */
	@Override
	public String selectUserRole(HttpSession session) {

		List<List<Map<String,Object>>> uList = userDao.selectUserRole(session.getAttribute(Constant.SESSION_USERID).toString());
		if(uList.size()==1){
			return uList.get(0).get(0).get("role").toString();
		}else{
			return "";
		}

	}


	/**
	 * @param user
	 * @return
	 * @description
	 * 用户注册
	 */
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
	@Override
	public int registerUser(User user) {
		user.setPassword(MD5.EncoderByMd5(user.getPassword()));
		String userID = Uuid.getUUID().toString();
		user.setId(userID);
		//注册用户默认用户状态正常
		user.setState(1);
		//用户基本信息
		userDao.registerUser(user);
		//角色权限表，新用户默认为游客。
		String roleID = "visitor";
		userDao.insertVisitor(userID,roleID);
		return 2;
	}

	/**
	 * @param user
	 * @return
	 * @description
	 * 查询用户是否已经存在
	 */
	@Override
	public List<User> selectByName(User user) {
		return userDao.selectByName(user);
	}


	/**
	 * @param id
	 * @return
	 * @description
	 * 修改密码
	 */
	@Override
	public String updatePasswordById(String id,String password,String oldPassword) {
		int result=0;
		String pwd=MD5.EncoderByMd5(password);
		String oldPwd=MD5.EncoderByMd5(oldPassword);
		List<User> user=userDao.selectByPasswordId(id, oldPwd);
		if(user.size()>0){
			result=userDao.updatePasswordById(id,pwd);
		}
		//用户信息
		User user_info = userDao.selectById(id);
		return result >= 1 ?Constant.successMessageWithTarget("操作成功！","用户名:"+user_info.getPhone()) : Constant.failureMessageWithTarget("初始密码不正确，请重新输入！","用户名:"+user_info.getPhone());
		//return result >= 1 ? Constant.successMessage() : Constant.updateUserPasswordMessage();
	}

	@Override
	public String resetPasswordById(String id) {
		//生成8位随密码
		String pwd = RandomPwd.getRandomPwd(8);
		//加密
		String en_pwd = MD5.EncoderByMd5(pwd);

		int result = userDao.updatePasswordById(id,en_pwd);
		if(result>0){
			//更新成功
			return Constant.successMessage(pwd);
		}else{
			return Constant.failureMessage("重置失败！");
		}
	}

	@Override
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
	public int setUserRole(String user_ids, String role_ids) {
		int result = 0;
		String[] u_ids = user_ids.split(",");
		String[] r_ids = role_ids.split(",");
		for(String u_id:u_ids){
			//删除原角色信息
			userDao.deleteUserRole(u_id);
			for(String r_id : r_ids){//遍历角色id   添加到用户角色中间表中
				result  = result + userDao.insertUserRole(u_id,r_id);
			}
		}
		return result;
	}

	@Override
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
	public boolean updateProduct() {
		//证书上传后，根据证书信息初始化用户及菜单信息
		//1.获取证书中的信息
		verifyLicense.setParam("/verifyparam.properties");
		LicenseContent licenseContent = verifyLicense.getLicenseContent();
		if(licenseContent!=null){

			//1.删除 系统表、菜单表、按钮表
			userDao.deleteButtonMenuSystem();
			//2.获取新的系统表、菜单表、按钮表、角色表
			List<Menu> menuList = new ArrayList<>();
			List<Button> buttonList = JSONUtil.getBeanListByJSONArrayString(roleAuthorityProperty.getButtons(),Button.class);
			List<Role> roleList = JSONUtil.getBeanListByJSONArrayString(roleAuthorityProperty.getRoles(),Role.class);
			List<System> allSystemList = JSONUtil.getBeanListByJSONArrayString(roleAuthorityProperty.getSystems(),System.class);
			//要插入数据库的模块列表
			List<System> systemList = new ArrayList<>();
			//获取license中的产品信息
			LicenseExtra customInfo = (LicenseExtra) licenseContent.getExtra();
			//获取产品包含的模块信息，模块以逗号隔开
			String product_type = customInfo.getProduct_type();
			String[] product_modules = product_type.split(",");
			//获取所有菜单
			for(String module:product_modules){
				switch (module){
					//日志
					case Constant.LICENSE_TYPE_LOG:
						menuList.addAll(JSONUtil.getBeanListByJSONArrayString(roleAuthorityProperty.getLogMenus(),Menu.class));
						break;
					//资产
					case Constant.LICENSE_TYPE_ASSET:
						menuList.addAll(JSONUtil.getBeanListByJSONArrayString(roleAuthorityProperty.getAssetMenus(),Menu.class));
						break;
					//流量
					case Constant.LICENSE_TYPE_PACKET:
						menuList.addAll(JSONUtil.getBeanListByJSONArrayString(roleAuthorityProperty.getPacketMenus(),Menu.class));
						break;
					//可视化
					case Constant.LICENSE_TYPE_VISUAL:
						menuList.addAll(JSONUtil.getBeanListByJSONArrayString(roleAuthorityProperty.getVisualMenus(),Menu.class));
						break;
					//告警
					case Constant.LICENSE_TYPE_ALERT:
						menuList.addAll(JSONUtil.getBeanListByJSONArrayString(roleAuthorityProperty.getAlertMenus(),Menu.class));
						break;
					//数据资产安全治理
					case Constant.LICENSE_TYPE_DSG:
						menuList.addAll(JSONUtil.getBeanListByJSONArrayString(roleAuthorityProperty.getDsgMenus(),Menu.class));
						break;
					//系统管理
					case Constant.LICENSE_TYPE_SYSTEM:
						menuList.addAll(JSONUtil.getBeanListByJSONArrayString(roleAuthorityProperty.getSystemMenus(),Menu.class));
						break;
				}
			}
			//遍历所有系统，筛选可呈现的
			for(System sys:allSystemList){
				if(product_type.indexOf(sys.getId())>=0){
					systemList.add(sys);
				}
			}


			//3.插入新的系统表、菜单表、按钮表、角色表
			userDao.insertSystemBatch(systemList);
			userDao.insertMenuBatch(menuList);
			userDao.insertButtonBatch(buttonList);

			//4.插入角色信息，通过ignore实现 初始化写入，二次激活时忽略已存在的角色
			userDao.insertIgnoreRoleBatch(roleList);
			//5.删除不存在的系统、菜单、按钮所在的 角色菜单关系表
			userDao.deleteNotExistRoleMenus();
			//6.插入新的关系表 已存在的忽略
			JSONArray roleMenuList = JSONArray.fromObject(roleAuthorityProperty.getRoleMenus());
			for(Object obj:roleMenuList.toArray()){
				JSONObject jsonObject = JSONObject.fromObject(obj);
				userDao.insertIgnoreMenuButton(jsonObject.get(FK_MENU_KEY).toString(),jsonObject.get(FK_ROLE_KEY).toString());
			}
			//7.用户及用户角色表，判定是否需要初始化
			//查询是否存在master用户
			User masterUser = new User();
			masterUser.setPhone("master");
			User masterInfo = userDao.selectByPhone(masterUser);
			//未查到，初始化
			if(masterInfo==null){
				List<User> defaultUserBeans = JSONUtil.getBeanListByJSONArrayString(roleAuthorityProperty.getDefaultUsers(),User.class);
				for(User defaultUser:defaultUserBeans){
					userDao.insert(defaultUser);//插入master用户
					//插入用户角色信息
					userDao.insertUserRole(defaultUser.getId(),"master");
				}
			}
			//configuration配置表要更新的内容列表
			List<Configuration> confList = new ArrayList<>();
			//流量模块需要更新configuration表的状态
			Configuration configuration = new Configuration();
			configuration.setConfiguration_key("has_packet");
			//包含流量模块，设置为true，否则为false
			if(product_type.indexOf(Constant.LICENSE_TYPE_PACKET)>=0){
				configuration.setConfiguration_value("true");
			}else{
				configuration.setConfiguration_value("false");
			}
			confList.add(configuration);
			//logo、首页、版本号信息
			Configuration conf_logo = new Configuration();
			conf_logo.setConfiguration_key(Constant.LOGO_PATH);
			conf_logo.setConfiguration_value(customInfo.getLogo());
			Configuration conf_version = new Configuration();
			conf_version.setConfiguration_key(Constant.VERSION);
			conf_version.setConfiguration_value(customInfo.getVersion());
			Configuration conf_index_page = new Configuration();
			conf_index_page.setConfiguration_key(Constant.INDEX_PAGE);
			conf_index_page.setConfiguration_value(customInfo.getIndex());

			confList.add(conf_logo);
			confList.add(conf_version);
			confList.add(conf_index_page);
			//资产可用报表菜单
			Configuration equipment_chart_json_key = new Configuration();
			equipment_chart_json_key.setConfiguration_key(Constant.EQUIPMENT_CHART_JSON_KEY);
			//产品是否包含告警模块
			if(product_type.indexOf(Constant.LICENSE_TYPE_ALERT)>=0){
				equipment_chart_json_key.setConfiguration_value(Constant.EQUIPMENT_CHART_ALERT);
			}else{
				equipment_chart_json_key.setConfiguration_value(Constant.EQUIPMENT_CHART_DEFAULT);
			}
			confList.add(equipment_chart_json_key);
			configurationDao.update(confList);
			ConfigurationCache.INSTANCE.init(configurationService);//更新缓存
			return true;
		}else{
			//激活失败
			return false;
		}
	}

	@Override
	public User selectByPhonePwd(User user) {
		return userDao.selectByPhonePwd(user);
	}

	@Override
	public String checkPwd_info(String password) {
		//获取密码长度
		String pwd_length = ConfigurationCache.INSTANCE.getConfigurationCache().getIfPresent(Constant.PWD_LENGTH).toString();
		//获取密码复杂度
		String pwd_complex = ConfigurationCache.INSTANCE.getConfigurationCache().getIfPresent(Constant.PWD_COMPLEX).toString();
		//验证密码长度是否符合要求
		boolean lengthResult = PwdCheckUtil.checkPasswordLength(password,pwd_length,null);
		if(lengthResult){
			//密码中包含数字
			if(pwd_complex.indexOf("数字")>=0){
				boolean res = PwdCheckUtil.checkContainDigit(password);
				if(!res){
					return Constant.failureMessage("密码复杂度不符合要求，长度>="+pwd_length+",且必须包含"+pwd_complex);
				}
			}
			//密码中包含字母，不区分大小写
			if(pwd_complex.indexOf("字母")>=0){
				boolean res = PwdCheckUtil.checkContainCase(password);
				if(!res){
					return Constant.failureMessage("密码复杂度不符合要求，长度>="+pwd_length+",且必须包含"+pwd_complex);
				}
			}
			//密码中包含字符
			if(pwd_complex.indexOf("字符")>=0){
				boolean res = PwdCheckUtil.checkContainSpecialChar(password);
				if(!res){
					return Constant.failureMessage("密码复杂度不符合要求，长度>="+pwd_length+",且必须包含"+pwd_complex);
				}
			}
		}else{
			return Constant.failureMessage("密码复杂度不符合要求，长度>="+pwd_length+",且必须包含"+pwd_complex);
		}
		return Constant.successMessage();
	}

	@Override
	public boolean checkPwd_Boolean(String password) {
		//获取密码长度
		String pwd_length = ConfigurationCache.INSTANCE.getConfigurationCache().getIfPresent(Constant.PWD_LENGTH).toString();
		//获取密码复杂度
		String pwd_complex = ConfigurationCache.INSTANCE.getConfigurationCache().getIfPresent(Constant.PWD_COMPLEX).toString();
		//验证密码长度是否符合要求
		boolean lengthResult = PwdCheckUtil.checkPasswordLength(password,pwd_length,null);
		if(lengthResult){
			//密码中包含数字
			if(pwd_complex.indexOf("数字")>=0){
				boolean res = PwdCheckUtil.checkContainDigit(password);
				if(!res){
					return false;
				}
			}
			//密码中包含字母，不区分大小写
			if(pwd_complex.indexOf("字母")>=0){
				boolean res = PwdCheckUtil.checkContainCase(password);
				if(!res){
					return false;
				}
			}
			//密码中包含字符
			if(pwd_complex.indexOf("字符")>=0){
				boolean res = PwdCheckUtil.checkContainSpecialChar(password);
				if(!res){
					return false;
				}
			}
		}else{
			return false;
		}
		return true;
	}
	/**
	 * 获取用户登录ip
	 * @param request
	 * @return
	 */
	private String getIpAddr(HttpServletRequest request){
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if ("0:0:0:0:0:0:0:1".equals(ip)) {
			ip = "127.0.0.1";
		}
		if (ip.split(",").length > 1) {
			ip = ip.split(",")[0];
		}
		return ip;
	}
}

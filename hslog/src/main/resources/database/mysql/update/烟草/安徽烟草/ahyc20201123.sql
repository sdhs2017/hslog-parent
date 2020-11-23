#1.添加表
#资产组
DROP TABLE IF EXISTS `asset_group`;
CREATE TABLE `asset_group` (
  `asset_group_id` varchar(50) NOT NULL COMMENT '资产ID',
  `asset_group_name` varchar(100) NOT NULL COMMENT '资产组名称',
  `asset_group_area` varchar(200) DEFAULT NULL COMMENT '区域',
  `asset_group_person` varchar(100) DEFAULT NULL COMMENT '负责人',
  `asset_group_note` varchar(1000) DEFAULT NULL COMMENT '说明',
  `user_group_id` varchar(50) DEFAULT NULL COMMENT '用户组id',
  `create_time` varchar(50) NOT NULL COMMENT '资产组创建时间',
  `create_user_id` varchar(50) NOT NULL COMMENT '创建资产组的用户id',
  `update_time` varchar(50) DEFAULT NULL COMMENT '资产组更新时间',
  `update_user_id` varchar(50) DEFAULT NULL COMMENT '资产组更新用户id',
  PRIMARY KEY (`asset_group_id`) USING BTREE,
  UNIQUE KEY `UNIQUE_ID` (`asset_group_id`) USING BTREE,
  UNIQUE KEY `UNIQUE_ASSET_GROUP_NAME` (`asset_group_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
#资产组/资产 关系表
DROP TABLE IF EXISTS `asset_group_relations`;
CREATE TABLE `asset_group_relations` (
  `asset_id` varchar(50) NOT NULL COMMENT '资产表id',
  `asset_type` varchar(50) DEFAULT NULL COMMENT '资产类型',
  `asset_name` varchar(100) NOT NULL COMMENT '资产名称',
  `asset_ip` varchar(50) DEFAULT NULL COMMENT '资产IP',
  `asset_group_id` varchar(50) NOT NULL COMMENT '资产组ID',
  `asset_group_name` varchar(100) NOT NULL COMMENT '资产组名称',
  `asset_logType` varchar(50) DEFAULT NULL COMMENT '资产日志类型',
  UNIQUE KEY `UNIQUE_INDEX` (`asset_id`,`asset_group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
#2.添加菜单
INSERT INTO `HS_Menu` VALUES ('1198792f-ee87-4965-9366-a7cdc9e601cc', '复合查询', '10500', 2, 'logsManage/complexSearch.vue', '', '1', '3');
INSERT INTO `HS_Menu` VALUES ('7ef6f59a-d4ef-4a51-9643-95fb120ed670', '文件日志查询', '10500', 3, 'logsManage/fileLogSearch.vue', '', '1', '3');
#3.菜单赋值给管理员
#先尝试删除，防止多次执行时出现异常
DELETE FROM `HS_RoleMenuButton` WHERE fk_menuAndButon_id='1198792f-ee87-4965-9366-a7cdc9e601cc';
DELETE FROM `HS_RoleMenuButton` WHERE fk_menuAndButon_id='7ef6f59a-d4ef-4a51-9643-95fb120ed670';
INSERT INTO `HS_RoleMenuButton` VALUES ('1198792f-ee87-4965-9366-a7cdc9e601cc', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('7ef6f59a-d4ef-4a51-9643-95fb120ed670', 'admin');

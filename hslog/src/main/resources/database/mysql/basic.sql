-- ----------------------------
-- 动作表
-- ----------------------------
DROP TABLE IF EXISTS `action`;
CREATE TABLE `action`  (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
  `userId` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  `state` int(2) NOT NULL COMMENT '状态',
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类型',
  `feature` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `deleteState` int(1) NULL DEFAULT NULL COMMENT '删除状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- 动作-事件表
-- ----------------------------
DROP TABLE IF EXISTS `action_event`;
CREATE TABLE `action_event`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `actionId` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `eventId` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `order` int(5) NULL DEFAULT NULL,
  `number` int(2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- 旧-告警表
-- ----------------------------
DROP TABLE IF EXISTS `alarm`;
CREATE TABLE `alarm`  (
  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `event_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '事件类型',
  `emailState` int(1) NULL DEFAULT NULL COMMENT '是否发送邮件（0：不发送 1：发送）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of alarm
-- ----------------------------
INSERT INTO `alarm` VALUES ('0bc92866061c4877a2561b843b15d329', 'crond', 0);
INSERT INTO `alarm` VALUES ('25dce2ec080b4ad28c30a80fd4604d4d', 'NetworkManager', 0);
INSERT INTO `alarm` VALUES ('34b08d3a60964f8586c9b3f6bf5d3e67', 'login', 0);
INSERT INTO `alarm` VALUES ('38ebcfa549354f03b1af74e524f673f3', 'pci_bus', 0);
INSERT INTO `alarm` VALUES ('57edd69131e248e2bd144e24eeac8b34', 'su', 0);
INSERT INTO `alarm` VALUES ('7222a8c04da641468ae923656c890cd2', 'sshd', 0);
INSERT INTO `alarm` VALUES ('7e5e992492f54cc28d55d3998434cec3', 'rsyslogd', 0);
INSERT INTO `alarm` VALUES ('a41f60a903c8497086ec9c73f2bd3f30', 'poweroff', 0);
INSERT INTO `alarm` VALUES ('bf1640e56b3b4026a2d5f7e64b950873', 'pci', 0);
INSERT INTO `alarm` VALUES ('c2ad9de3a08142c28772d3cb9c10cb19', 'ACPI', 0);
INSERT INTO `alarm` VALUES ('de6c6e68bf29416b8b1239089de2c1a0', 'SRAT', 0);
INSERT INTO `alarm` VALUES ('df35bd79b80b4238be87935f938e4fe8', 'session', 0);
INSERT INTO `alarm` VALUES ('e3e519b3fa3b4878bb40e41c17dac3b3', 'PM', 0);
INSERT INTO `alarm` VALUES ('fef78b41cd224525aae0fbae7ba13351', 'usb', 0);

-- ----------------------------
-- 告警表
-- ----------------------------
DROP TABLE IF EXISTS `alert`;
CREATE TABLE `alert`  (
                          `alert_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '告警ID',
                          `alert_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '告警名称',
                          `alert_note` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '告警说明',
                          `template_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源template',
                          `pre_index_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '索引前缀',
                          `suffix_index_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '索引后缀',
                          `alert_search_filters` varchar(5000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '筛选条件',
                          `alert_search_metric` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '统计条件',
                          `alert_search_bucket` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分组条件',
                          `alert_conditions` varchar(5000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '告警条件',
                          `alert_cron` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '执行周期',
                          `alert_create_time` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建时间',
                          `alert_create_user` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人ID',
                          `alert_update_time` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新时间',
                          `alert_update_user` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人ID',
                          `alert_state` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
                          `alert_structure` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '前端结构',
                          `alert_time` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据查询范围',
                          `alert_time_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据查询范围类型',
                          `alert_exec_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行周期类型',
                          `alert_time_cycle_num` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行周期数值',
                          `alert_time_cycle_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行周期时间类型',
                          `alert_assetGroup_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资产组id',
                          `alert_equipment_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '虚拟资产id',
                          PRIMARY KEY (`alert_id`) USING BTREE,
                          UNIQUE INDEX `UNIQUE_ALERT_NAME`(`alert_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;


-- ----------------------------
-- 旧版 逻辑资产表（已停用）
-- ----------------------------
DROP TABLE IF EXISTS `assets`;
CREATE TABLE `assets`  (
                           `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                           `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                           `hostName` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                           `type` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                           `ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                           `macAdress` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                           `createTime` datetime(0) NULL DEFAULT NULL,
                           `upDateTime` datetime(0) NULL DEFAULT NULL,
                           `endTime` datetime(0) NULL DEFAULT NULL,
                           `startUp` int(1) NULL DEFAULT NULL,
                           `userId` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                           `departmentId` int(8) NULL DEFAULT NULL,
                           `logType` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                           `system` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                           `systemVersion` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                           `assetNum` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                           `serialNum` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                           `describe` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                           `valuation` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                           `log_level` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                           `high_risk` int(4) NULL DEFAULT NULL,
                           `moderate_risk` int(4) NULL DEFAULT NULL,
                           `low_risk` int(4) NULL DEFAULT NULL,
                           `departmentNodeId` int(3) NULL DEFAULT NULL,
                           `ports` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                           `protocol` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                           `state` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                           `responseDate` datetime(0) NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- 逻辑资产表
-- ----------------------------
DROP TABLE IF EXISTS `asset`;
CREATE TABLE `asset`  (
                          `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'id',
                          `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备名称',
                          `hostName` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主机名称',
                          `type` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备类型',
                          `ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'ip地址',
                          `macAdress` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'Mac地址',
                          `createTime` datetime(0) NOT NULL COMMENT '创建时间',
                          `upDateTime` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                          `endTime` datetime(0) NULL DEFAULT NULL COMMENT '截止时间',
                          `startUp` int(1) NOT NULL DEFAULT 0 COMMENT '是否启用（0未启用1启用）',
                          `userId` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
                          `departmentId` int(8) NULL DEFAULT NULL COMMENT '部门id',
                          `logType` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '日志类型',
                          `system` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统',
                          `systemVersion` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统版本号',
                          `assetNum` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资产编号',
                          `serialNum` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '序列号',
                          `describe` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
                          `valuation` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资产价值',
                          `log_level` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '日志级别',
                          `high_risk` int(4) NOT NULL DEFAULT 0 COMMENT '高危',
                          `moderate_risk` int(4) NOT NULL DEFAULT 0 COMMENT '中危',
                          `low_risk` int(4) NOT NULL DEFAULT 0 COMMENT '低危',
                          `departmentNodeId` int(3) NULL DEFAULT NULL,
                          `domain` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '根域名',
                          `port` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '端口',
                          PRIMARY KEY (`id`) USING BTREE,
                          UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Compact;

-- ----------------------------
-- 资产组
-- ----------------------------
DROP TABLE IF EXISTS `asset_group`;
CREATE TABLE `asset_group`  (
                                `asset_group_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '资产ID',
                                `asset_group_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '资产组名称',
                                `asset_group_area` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '区域',
                                `asset_group_person` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '负责人',
                                `asset_group_note` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '说明',
                                `user_group_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户组id',
                                `create_time` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '资产组创建时间',
                                `create_user_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建资产组的用户id',
                                `update_time` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资产组更新时间',
                                `update_user_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资产组更新用户id',
                                PRIMARY KEY (`asset_group_id`) USING BTREE,
                                UNIQUE INDEX `UNIQUE_ID`(`asset_group_id`) USING BTREE,
                                UNIQUE INDEX `UNIQUE_ASSET_GROUP_NAME`(`asset_group_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- 资产（equipment 虚拟资产）-资产组  关系表 N:N
-- ----------------------------
DROP TABLE IF EXISTS `asset_group_relations`;
CREATE TABLE `asset_group_relations`  (
                                          `asset_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '资产表id',
                                          `asset_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资产类型',
                                          `asset_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '资产名称',
                                          `asset_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资产IP',
                                          `asset_group_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '资产组ID',
                                          `asset_group_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '资产组名称',
                                          `asset_logType` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资产日志类型',
                                          UNIQUE INDEX `UNIQUE_INDEX`(`asset_id`, `asset_group_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- 全局配置项
-- ----------------------------
DROP TABLE IF EXISTS `configuration`;
CREATE TABLE `configuration`  (
                                  `configuration_key` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                  `configuration_value` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                  PRIMARY KEY (`configuration_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of configuration
-- ----------------------------
INSERT INTO `configuration` VALUES ('concurrent_requests', '9'); #并发请求书
INSERT INTO `configuration` VALUES ('disk_data_watermark', '85'); #数据盘告警百分比
INSERT INTO `configuration` VALUES ('disk_system_watermark', '70'); #系统盘告警百分比
INSERT INTO `configuration` VALUES ('es_bulk', '2000'); #es批量提交数

-- ----------------------------
-- 部门表（组织机构）
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department`  (
                               `id` int(8) NOT NULL AUTO_INCREMENT COMMENT '部门id',
                               `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '部门名称',
                               `level` int(8) NOT NULL COMMENT '部门层级',
                               `superiorId` int(64) NULL DEFAULT NULL COMMENT '上级id',
                               `subordinate` int(1) NOT NULL COMMENT '是否有下级（1有0没有）',
                               `orderId` int(100) NULL DEFAULT NULL COMMENT '位置排序',
                               `comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '简介',
                               `departmentNodeId` int(3) NULL DEFAULT NULL,
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- 资产表（虚拟资产）
-- ----------------------------
DROP TABLE IF EXISTS `equipment`;
CREATE TABLE `equipment`  (
                              `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'id',
                              `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备名称',
                              `hostName` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主机名称',
                              `type` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备类型',
                              `ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'ip地址',
                              `macAdress` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'Mac地址',
                              `createTime` datetime(0) NOT NULL COMMENT '创建时间',
                              `upDateTime` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
                              `endTime` datetime(0) NULL DEFAULT NULL COMMENT '截止时间',
                              `startUp` int(1) NOT NULL DEFAULT 0 COMMENT '是否启用（0未启用1启用）',
                              `userId` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
                              `departmentId` int(8) NULL DEFAULT NULL COMMENT '部门id',
                              `logType` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '日志类型',
                              `system` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统',
                              `systemVersion` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统版本号',
                              `assetNum` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资产编号',
                              `serialNum` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '序列号',
                              `describe` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
                              `valuation` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资产价值',
                              `log_level` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '日志级别',
                              `high_risk` int(4) NOT NULL DEFAULT 0 COMMENT '高危',
                              `moderate_risk` int(4) NOT NULL DEFAULT 0 COMMENT '中危',
                              `low_risk` int(4) NOT NULL DEFAULT 0 COMMENT '低危',
                              `departmentNodeId` int(3) NULL DEFAULT NULL COMMENT '组织机构ID',
                              `domain` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '根域名',
                              `port` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '端口',
                              `state` int(2) NULL DEFAULT NULL COMMENT '状态(已弃用)',
                              UNIQUE INDEX `nameUnique`(`name`) USING BTREE,
                              UNIQUE INDEX `ipLogTypeUnique`(`ip`, `logType`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = '资产表' ROW_FORMAT = Compact;
-- ----------------------------
-- 资产-事件 关联表（暂未使用）
-- ----------------------------
DROP TABLE IF EXISTS `equipment_event`;
CREATE TABLE `equipment_event`  (
                                    `id` int(11) NOT NULL AUTO_INCREMENT,
                                    `equipmentId` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
                                    `eventId` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Compact;
-- ----------------------------
-- 事件表（暂未使用）
-- ----------------------------
DROP TABLE IF EXISTS `event`;
CREATE TABLE `event`  (
                          `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                          `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                          `userId` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                          `state` int(2) NULL DEFAULT NULL,
                          `message` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                          `enabled` int(2) NOT NULL,
                          `dangerous_level` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                          `time` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                          `safeStrategyName` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                          `time_interval` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                          `event_classify` int(2) NULL DEFAULT NULL,
                          `deleteState` int(1) NULL DEFAULT NULL,
                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- 事件字典表，目前记录linux和windows的常用事件
-- ----------------------------
DROP TABLE IF EXISTS `event_dic`;
CREATE TABLE `event_dic`  (
                              `event_id` int(50) NOT NULL AUTO_INCREMENT COMMENT '事件ID',
                              `event_name_en` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '事件名称（英文）',
                              `event_name_cn` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '事件名称（中文）',
                              `event_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '事件类型',
                              PRIMARY KEY (`event_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1085 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '事件字典表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of event_dic
-- ----------------------------
INSERT INTO `event_dic` VALUES (1, 'poweroff', '主机关机', 'syslog');
INSERT INTO `event_dic` VALUES (2, 'ssh failed', 'ssh登录失败', 'syslog');
INSERT INTO `event_dic` VALUES (3, 'NetworkManager', '网络服务', 'syslog');
INSERT INTO `event_dic` VALUES (4, 'usb', 'usb外接', 'syslog');
INSERT INTO `event_dic` VALUES (5, 'sshd', '通过ssh方式进行操作', 'syslog');
INSERT INTO `event_dic` VALUES (6, 'login', '用户登录', 'syslog');
INSERT INTO `event_dic` VALUES (7, 'su', '通过su方式登录', 'syslog');
INSERT INTO `event_dic` VALUES (8, 'session', '开启新的会话窗口', 'syslog');
INSERT INTO `event_dic` VALUES (9, 'rsyslogd', 'rsyslog自身日志', 'syslog');
INSERT INTO `event_dic` VALUES (10, 'pci', 'pci日志', 'syslog');
INSERT INTO `event_dic` VALUES (11, 'pci_bus', 'pci_bus日志', 'syslog');
INSERT INTO `event_dic` VALUES (12, 'ACPI', 'ACPI日志', 'syslog');
INSERT INTO `event_dic` VALUES (13, 'PM', 'PM日志', 'syslog');
INSERT INTO `event_dic` VALUES (14, 'SRAT', 'SRAT日志', 'syslog');
INSERT INTO `event_dic` VALUES (15, 'crond', '定时任务', 'syslog');
INSERT INTO `event_dic` VALUES (16, 'logged-in', '登录', 'winlogbeat');
INSERT INTO `event_dic` VALUES (17, 'logon-failed', '登录失败', 'winlogbeat');
INSERT INTO `event_dic` VALUES (18, 'logged-out', '登出', 'winlogbeat');
INSERT INTO `event_dic` VALUES (19, 'logged-in-special', '特殊登录', 'winlogbeat');
INSERT INTO `event_dic` VALUES (20, 'created-process', '创建进程', 'winlogbeat');
INSERT INTO `event_dic` VALUES (21, 'exited-process', '存在进程', 'winlogbeat');
INSERT INTO `event_dic` VALUES (22, 'added-user-account', '创建用户', 'winlogbeat');
INSERT INTO `event_dic` VALUES (23, 'enabled-user-account', '启用用户', 'winlogbeat');
INSERT INTO `event_dic` VALUES (24, 'changed-password', '修改密码', 'winlogbeat');
INSERT INTO `event_dic` VALUES (25, 'reset-password', '重置密码', 'winlogbeat');
INSERT INTO `event_dic` VALUES (26, 'disabled-user-account', '禁用用户', 'winlogbeat');
INSERT INTO `event_dic` VALUES (27, 'deleted-user-account', '删除用户', 'winlogbeat');
INSERT INTO `event_dic` VALUES (38, 'modified-user-account', '修改用户', 'winlogbeat');
INSERT INTO `event_dic` VALUES (39, 'locked-out-user-account', '锁定用户', 'winlogbeat');
INSERT INTO `event_dic` VALUES (45, 'type-changed-group-account', '用户组变更', 'winlogbeat');
INSERT INTO `event_dic` VALUES (46, 'unlocked-user-account', '解锁用户', 'winlogbeat');
INSERT INTO `event_dic` VALUES (47, 'renamed-user-account', '重命名用户名', 'winlogbeat');
INSERT INTO `event_dic` VALUES (48, 'group-membership-enumerated', '枚举组成员身份', 'winlogbeat');
INSERT INTO `event_dic` VALUES (49, 'user-member-enumerated', '枚举用户成员', 'winlogbeat');
INSERT INTO `event_dic` VALUES (51, 'Unused message ID', '未使用的信息ID', 'winlogbeat');
INSERT INTO `event_dic` VALUES (75, 'Not used', '未使用', 'winlogbeat');
INSERT INTO `event_dic` VALUES (76, 'Assign Primary Token Privilege', '分配主令牌权限', 'winlogbeat');
INSERT INTO `event_dic` VALUES (77, 'Lock Memory Privilege', '锁定内存权限', 'winlogbeat');
INSERT INTO `event_dic` VALUES (78, 'Increase Memory Quota Privilege', '增加内存配额权限', 'winlogbeat');
INSERT INTO `event_dic` VALUES (79, 'Unsolicited Input Privilege', '主动输入权限', 'winlogbeat');
INSERT INTO `event_dic` VALUES (80, 'Trusted Computer Base Privilege', '受信任的计算机基本权限', 'winlogbeat');
INSERT INTO `event_dic` VALUES (81, 'Security Privilege', '安全权限', 'winlogbeat');
INSERT INTO `event_dic` VALUES (82, 'Take Ownership Privilege', '获得所有权权限', 'winlogbeat');
INSERT INTO `event_dic` VALUES (83, 'Load/Unload Driver Privilege', '加载/卸载驱动程序权限', 'winlogbeat');
INSERT INTO `event_dic` VALUES (84, 'Profile System Privilege', '配置文件系统权限', 'winlogbeat');
INSERT INTO `event_dic` VALUES (85, 'Set System Time Privilege', '设置系统时间权限', 'winlogbeat');
INSERT INTO `event_dic` VALUES (86, 'Profile Single Process Privilege', '配置文件单进程权限', 'winlogbeat');
INSERT INTO `event_dic` VALUES (87, 'Increment Base Priority Privilege', '增量基本优先级特权', 'winlogbeat');
INSERT INTO `event_dic` VALUES (88, 'Create Pagefile Privilege', '页面文件创建权限', 'winlogbeat');
INSERT INTO `event_dic` VALUES (89, 'Create Permanent Object Privilege', '创建永久对象权限', 'winlogbeat');
INSERT INTO `event_dic` VALUES (90, 'Backup Privilege', '备份权限', 'winlogbeat');
INSERT INTO `event_dic` VALUES (91, 'Restore From Backup Privilege', '从备份还原权限', 'winlogbeat');
INSERT INTO `event_dic` VALUES (92, 'Shutdown System Privilege', '关闭系统权限', 'winlogbeat');
INSERT INTO `event_dic` VALUES (93, 'Debug Privilege', '调试权限', 'winlogbeat');
INSERT INTO `event_dic` VALUES (94, 'View or Change Audit Log Privilege', '查看或更改审核日志权限', 'winlogbeat');
INSERT INTO `event_dic` VALUES (95, 'Change Hardware Environment Privilege', '更改硬件环境权限', 'winlogbeat');
INSERT INTO `event_dic` VALUES (96, 'Change Notify (and Traverse) Privilege', '更改通知（和遍历）权限', 'winlogbeat');
INSERT INTO `event_dic` VALUES (97, 'Remotely Shut System Down Privilege', '远程关闭系统权限', 'winlogbeat');
INSERT INTO `event_dic` VALUES (98, '<value changed', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (99, '<value not set>', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (100, '<never>', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (102, 'Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (103, 'All', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (105, 'Audit Policy query/set API Operation', '审核策略查询/设置API操作', 'winlogbeat');
INSERT INTO `event_dic` VALUES (106, '<Value change auditing for this registry type is not supported>', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (107, 'Granted by', '授予人', 'winlogbeat');
INSERT INTO `event_dic` VALUES (108, 'Denied by', '被拒绝', 'winlogbeat');
INSERT INTO `event_dic` VALUES (109, 'Denied by Integrity Policy check', '被完整性策略检查拒绝', 'winlogbeat');
INSERT INTO `event_dic` VALUES (110, 'Granted by Ownership', '所有权授予', 'winlogbeat');
INSERT INTO `event_dic` VALUES (111, 'Not granted', '未获批准', 'winlogbeat');
INSERT INTO `event_dic` VALUES (112, 'Granted by NULL DACL', '由空DACL授予', 'winlogbeat');
INSERT INTO `event_dic` VALUES (113, 'Denied by Empty DACL', '被空DACL拒绝', 'winlogbeat');
INSERT INTO `event_dic` VALUES (114, 'Granted by NULL Security Descriptor', '由空安全描述符授予', 'winlogbeat');
INSERT INTO `event_dic` VALUES (115, 'Unknown or unchecked', '未知或未检查', 'winlogbeat');
INSERT INTO `event_dic` VALUES (116, 'Not granted due to missing', '因失踪未授予', 'winlogbeat');
INSERT INTO `event_dic` VALUES (117, 'Granted by ACE on parent folder', '由ACE在父文件夹上授予', 'winlogbeat');
INSERT INTO `event_dic` VALUES (118, 'Denied by ACE on parent folder', '被父文件夹上的ACE拒绝', 'winlogbeat');
INSERT INTO `event_dic` VALUES (119, 'Granted by Central Access Rule', '由中心访问规则授予', 'winlogbeat');
INSERT INTO `event_dic` VALUES (120, 'NOT Granted by Central Access Rule', '未被中心访问规则授予', 'winlogbeat');
INSERT INTO `event_dic` VALUES (121, 'Granted by parent folder\'s Central Access Rule', '由父文件夹的中心访问规则授予', 'winlogbeat');
INSERT INTO `event_dic` VALUES (122, 'NOT Granted by parent folder\'s Central Access Rule', '未由父文件夹的中心访问规则授予', 'winlogbeat');
INSERT INTO `event_dic` VALUES (123, 'Unknown Type', '未知类型', 'winlogbeat');
INSERT INTO `event_dic` VALUES (124, 'String', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (125, 'Unsigned 64-bit Integer', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (126, '64-bit Integer', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (127, 'FQBN', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (128, 'Blob', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (129, 'Sid', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (130, 'Boolean', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (131, 'TRUE', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (132, 'FALSE', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (133, 'Invalid', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (134, 'an ACE too long to display', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (135, 'a Security Descriptor too long to display', '安全描述符太长，无法显示', 'winlogbeat');
INSERT INTO `event_dic` VALUES (136, 'Not granted to AppContainers', '未授予AppContainers', 'winlogbeat');
INSERT INTO `event_dic` VALUES (137, '...', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (138, 'Identification', '识别', 'winlogbeat');
INSERT INTO `event_dic` VALUES (139, 'Impersonation', '冒充', 'winlogbeat');
INSERT INTO `event_dic` VALUES (140, 'Delegation', '授权', 'winlogbeat');
INSERT INTO `event_dic` VALUES (141, 'Denied by Process Trust Label ACE', '被进程信任标签ACE拒绝', 'winlogbeat');
INSERT INTO `event_dic` VALUES (145, 'Not Available', '无法使用的', 'winlogbeat');
INSERT INTO `event_dic` VALUES (146, 'Default', '默认', 'winlogbeat');
INSERT INTO `event_dic` VALUES (147, 'DisallowMmConfig', '不允许配置', 'winlogbeat');
INSERT INTO `event_dic` VALUES (148, 'Off', '关闭', 'winlogbeat');
INSERT INTO `event_dic` VALUES (149, 'Auto', '自动', 'winlogbeat');
INSERT INTO `event_dic` VALUES (150, 'REG_NONE', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (151, 'REG_SZ', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (152, 'REG_EXPAND_SZ', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (153, 'REG_BINARY', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (154, 'REG_DWORD', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (155, 'REG_DWORD_BIG_ENDIAN', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (156, 'REG_LINK', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (157, 'REG_MULTI_SZ (New lines are replaced with *. A * is replaced with **)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (158, 'REG_RESOURCE_LIST', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (159, 'REG_FULL_RESOURCE_DESCRIPTOR', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (160, 'REG_RESOURCE_REQUIREMENTS_LIST', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (161, 'REG_QWORD', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (162, 'New registry value created', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (163, 'Existing registry value modified', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (164, 'Registry value deleted', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (165, 'Sunday', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (166, 'Monday', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (167, 'Tuesday', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (168, 'Wednesday', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (169, 'Thursday', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (170, 'Friday', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (171, 'Saturday', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (172, 'TokenElevationTypeDefault (1)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (173, 'TokenElevationTypeFull (2)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (174, 'TokenElevationTypeLimited (3)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (175, 'Account Enabled', '帐户已启用', 'winlogbeat');
INSERT INTO `event_dic` VALUES (176, 'Home Directory Required\' - Disabled', '需要主目录-已禁用', 'winlogbeat');
INSERT INTO `event_dic` VALUES (177, 'Password Not Required\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (178, 'Temp Duplicate Account\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (179, 'Normal Account\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (180, 'MNS Logon Account\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (181, 'Interdomain Trust Account\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (182, 'Workstation Trust Account\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (183, 'Server Trust Account\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (184, 'Don\'t Expire Password\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (185, 'Account Unlocked', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (186, 'Encrypted Text Password Allowed\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (187, 'Smartcard Required\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (188, 'Trusted For Delegation\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (189, 'Not Delegated\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (190, 'Use DES Key Only\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (191, 'Don\'t Require Preauth\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (192, 'Password Expired\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (193, 'Trusted To Authenticate For Delegation\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (194, 'Exclude Authorization Information\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (195, 'Undefined UserAccountControl Bit 20\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (196, 'Protect Kerberos Service Tickets with AES Keys\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (197, 'Undefined UserAccountControl Bit 22\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (198, 'Undefined UserAccountControl Bit 23\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (199, 'Undefined UserAccountControl Bit 24\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (200, 'Undefined UserAccountControl Bit 25\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (201, 'Undefined UserAccountControl Bit 26\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (202, 'Undefined UserAccountControl Bit 27\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (203, 'Undefined UserAccountControl Bit 28\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (204, 'Undefined UserAccountControl Bit 29\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (205, 'Undefined UserAccountControl Bit 30\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (206, 'Undefined UserAccountControl Bit 31\' - Disabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (207, 'Account Disabled', '帐户已禁用', 'winlogbeat');
INSERT INTO `event_dic` VALUES (208, 'Home Directory Required\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (209, 'Password Not Required\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (210, 'Temp Duplicate Account\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (211, 'Normal Account\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (212, 'MNS Logon Account\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (213, 'Interdomain Trust Account\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (214, 'Workstation Trust Account\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (215, 'Server Trust Account\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (216, 'Don\'t Expire Password\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (217, 'Account Locked', '帐户已锁定', 'winlogbeat');
INSERT INTO `event_dic` VALUES (218, 'Encrypted Text Password Allowed\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (219, 'Smartcard Required\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (220, 'Trusted For Delegation\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (221, 'Not Delegated\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (222, 'Use DES Key Only\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (223, 'Don\'t Require Preauth\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (224, 'Password Expired\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (225, 'Trusted To Authenticate For Delegation\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (226, 'Exclude Authorization Information\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (227, 'Undefined UserAccountControl Bit 20\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (228, 'Protect Kerberos Service Tickets with AES Keys\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (229, 'Undefined UserAccountControl Bit 22\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (230, 'Undefined UserAccountControl Bit 23\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (231, 'Undefined UserAccountControl Bit 24\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (232, 'Undefined UserAccountControl Bit 25\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (233, 'Undefined UserAccountControl Bit 26\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (234, 'Undefined UserAccountControl Bit 27\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (235, 'Undefined UserAccountControl Bit 28\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (236, 'Undefined UserAccountControl Bit 29\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (237, 'Undefined UserAccountControl Bit 30\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (238, 'Undefined UserAccountControl Bit 31\' - Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (239, 'An Error occured during Logon.', '登录时出错', 'winlogbeat');
INSERT INTO `event_dic` VALUES (240, 'The specified user account has expired.', '指定的用户帐户已过期', 'winlogbeat');
INSERT INTO `event_dic` VALUES (241, 'The NetLogon component is not active.', 'NetLogon组件未处于活动状态', 'winlogbeat');
INSERT INTO `event_dic` VALUES (242, 'Account locked out.', '帐户锁定', 'winlogbeat');
INSERT INTO `event_dic` VALUES (243, 'The user has not been granted the requested logon type at this machine.', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (244, 'The specified account\'s password has expired.', '指定帐户的密码已过期', 'winlogbeat');
INSERT INTO `event_dic` VALUES (245, 'Account currently disabled.', '帐户当前已禁用', 'winlogbeat');
INSERT INTO `event_dic` VALUES (246, 'Account logon time restriction violation.', '违反帐户登录时间限制', 'winlogbeat');
INSERT INTO `event_dic` VALUES (247, 'User not allowed to logon at this computer.', '不允许用户在此计算机上登录', 'winlogbeat');
INSERT INTO `event_dic` VALUES (248, 'Unknown user name or bad password.', '未知用户名或密码错误', 'winlogbeat');
INSERT INTO `event_dic` VALUES (249, 'Domain sid inconsistent.', '域sid不一致', 'winlogbeat');
INSERT INTO `event_dic` VALUES (250, 'Smartcard logon is required and was not used.', '需要智能卡登录，但未使用', 'winlogbeat');
INSERT INTO `event_dic` VALUES (251, 'Not Available.', '无法使用', 'winlogbeat');
INSERT INTO `event_dic` VALUES (252, 'Random number generator failure.', '随机数发生器故障', 'winlogbeat');
INSERT INTO `event_dic` VALUES (253, 'Random number generation failed FIPS-140 pre-hash check.', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (254, 'Failed to zero secret data.', '无法将机密数据归零', 'winlogbeat');
INSERT INTO `event_dic` VALUES (255, 'Key failed pair wise consistency check.', '密钥对一致性检查失败', 'winlogbeat');
INSERT INTO `event_dic` VALUES (256, 'Failed to unprotect persistent cryptographic key.', '无法取消对永久加密密钥的保护', 'winlogbeat');
INSERT INTO `event_dic` VALUES (257, 'Key export checks failed.', '密钥导出检查失败', 'winlogbeat');
INSERT INTO `event_dic` VALUES (258, 'Validation of public key failed.', '公钥验证失败', 'winlogbeat');
INSERT INTO `event_dic` VALUES (259, 'Signature verification failed.', '签名验证失败', 'winlogbeat');
INSERT INTO `event_dic` VALUES (260, 'Open key file.', '打开密钥文件', 'winlogbeat');
INSERT INTO `event_dic` VALUES (261, 'Delete key file.', '删除密钥文件', 'winlogbeat');
INSERT INTO `event_dic` VALUES (262, 'Read persisted key from file.', '从文件中读取持久化密钥', 'winlogbeat');
INSERT INTO `event_dic` VALUES (263, 'Write persisted key to file.', '将保留的密钥写入文件', 'winlogbeat');
INSERT INTO `event_dic` VALUES (264, 'Export of persistent cryptographic key.', '导出永久加密密钥', 'winlogbeat');
INSERT INTO `event_dic` VALUES (265, 'Import of persistent cryptographic key.', '永久加密密钥的导入', 'winlogbeat');
INSERT INTO `event_dic` VALUES (266, 'Open Key.', '打开密钥', 'winlogbeat');
INSERT INTO `event_dic` VALUES (267, 'Create Key.', '创建密钥', 'winlogbeat');
INSERT INTO `event_dic` VALUES (268, 'Delete Key.', '喊出密钥', 'winlogbeat');
INSERT INTO `event_dic` VALUES (269, 'Encrypt.', '加密', 'winlogbeat');
INSERT INTO `event_dic` VALUES (270, 'Decrypt.', '解密', 'winlogbeat');
INSERT INTO `event_dic` VALUES (271, 'Sign hash.', '符号哈希', 'winlogbeat');
INSERT INTO `event_dic` VALUES (272, 'Secret agreement.', '秘密协议', 'winlogbeat');
INSERT INTO `event_dic` VALUES (273, 'Domain settings', '域设置', 'winlogbeat');
INSERT INTO `event_dic` VALUES (274, 'Local settings', '本地设置', 'winlogbeat');
INSERT INTO `event_dic` VALUES (275, 'Add provider.', '添加提供程序', 'winlogbeat');
INSERT INTO `event_dic` VALUES (276, 'Remove provider.', '删除提供程序', 'winlogbeat');
INSERT INTO `event_dic` VALUES (277, 'Add context.', '添加上下文', 'winlogbeat');
INSERT INTO `event_dic` VALUES (278, 'Remove context.', '删除上下文', 'winlogbeat');
INSERT INTO `event_dic` VALUES (279, 'Add function.', '添加函数', 'winlogbeat');
INSERT INTO `event_dic` VALUES (280, 'Remove function.', '移除函数', 'winlogbeat');
INSERT INTO `event_dic` VALUES (281, 'Add function provider.', '提供程序添加函数', 'winlogbeat');
INSERT INTO `event_dic` VALUES (282, 'Remove function provider.', '删除函数提供程序', 'winlogbeat');
INSERT INTO `event_dic` VALUES (283, 'Add function property.', '添加函数属性', 'winlogbeat');
INSERT INTO `event_dic` VALUES (284, 'Remove function property.', '删除函数属性', 'winlogbeat');
INSERT INTO `event_dic` VALUES (285, 'Machine key.', '机器钥匙', 'winlogbeat');
INSERT INTO `event_dic` VALUES (286, 'User key.', '用户钥匙', 'winlogbeat');
INSERT INTO `event_dic` VALUES (287, 'Key Derivation.', '密钥派生', 'winlogbeat');
INSERT INTO `event_dic` VALUES (288, 'Device Access Bit 0', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (289, 'Device Access Bit 1', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (290, 'Device Access Bit 2', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (291, 'Device Access Bit 3', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (292, 'Device Access Bit 4', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (293, 'Device Access Bit 5', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (294, 'Device Access Bit 6', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (295, 'Device Access Bit 7', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (296, 'Device Access Bit 8', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (304, 'Query directory', '查询目录', 'winlogbeat');
INSERT INTO `event_dic` VALUES (305, 'Traverse', '遍历', 'winlogbeat');
INSERT INTO `event_dic` VALUES (306, 'Create object in directory', '在目录中创建对象', 'winlogbeat');
INSERT INTO `event_dic` VALUES (307, 'Create sub-directory', '创建子目录', 'winlogbeat');
INSERT INTO `event_dic` VALUES (320, 'Query event state', '查询事件状态', 'winlogbeat');
INSERT INTO `event_dic` VALUES (321, 'Modify event state', '修改事件状态', 'winlogbeat');
INSERT INTO `event_dic` VALUES (336, 'ReadData (or ListDirectory)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (337, 'WriteData (or AddFile)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (338, 'AppendData (or AddSubdirectory or CreatePipeInstance)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (339, 'ReadEA', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (340, 'WriteEA', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (341, 'Execute/Traverse', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (342, 'DeleteChild', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (343, 'ReadAttributes', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (344, 'WriteAttributes', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (352, 'Query key value', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (353, 'Set key value', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (354, 'Create sub-key', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (355, 'Enumerate sub-keys', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (356, 'Notify about changes to keys', '通知密钥更改', 'winlogbeat');
INSERT INTO `event_dic` VALUES (357, 'Create Link', '创建链接', 'winlogbeat');
INSERT INTO `event_dic` VALUES (360, 'Enable 64(or 32) bit application to open 64 bit key', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (361, 'Enable 64(or 32) bit application to open 32 bit key', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (368, 'Query mutant state', '查询突变状态', 'winlogbeat');
INSERT INTO `event_dic` VALUES (384, 'Communicate using port', '使用端口通信', 'winlogbeat');
INSERT INTO `event_dic` VALUES (400, 'Force process termination', '强制进程终止', 'winlogbeat');
INSERT INTO `event_dic` VALUES (401, 'Create new thread in process', '正在创建新线程', 'winlogbeat');
INSERT INTO `event_dic` VALUES (402, 'Set process session ID', '设置进程会话ID', 'winlogbeat');
INSERT INTO `event_dic` VALUES (403, 'Perform virtual memory operation', '执行虚拟内存操作', 'winlogbeat');
INSERT INTO `event_dic` VALUES (404, 'Read from process memory', '从进程内存读取', 'winlogbeat');
INSERT INTO `event_dic` VALUES (405, 'Write to process memory', '写入进程内存', 'winlogbeat');
INSERT INTO `event_dic` VALUES (406, 'Duplicate handle into or out of process', '将句柄复制到进程中或进程外', 'winlogbeat');
INSERT INTO `event_dic` VALUES (407, 'Create a subprocess of process', '创建流程的子流程', 'winlogbeat');
INSERT INTO `event_dic` VALUES (408, 'Set process quotas', '设置进程配额', 'winlogbeat');
INSERT INTO `event_dic` VALUES (409, 'Set process information', '设置进程信息', 'winlogbeat');
INSERT INTO `event_dic` VALUES (410, 'Query process information', '查询流程信息', 'winlogbeat');
INSERT INTO `event_dic` VALUES (411, 'Set process termination port', '设置进程终止端口', 'winlogbeat');
INSERT INTO `event_dic` VALUES (416, 'Control profile', '控制配置文件', 'winlogbeat');
INSERT INTO `event_dic` VALUES (432, 'Query section state', '查询区域状态', 'winlogbeat');
INSERT INTO `event_dic` VALUES (433, 'Map section for write', '映射写入部分', 'winlogbeat');
INSERT INTO `event_dic` VALUES (434, 'Map section for read', '映射读取部分', 'winlogbeat');
INSERT INTO `event_dic` VALUES (435, 'Map section for execute', '映射执行部分', 'winlogbeat');
INSERT INTO `event_dic` VALUES (436, 'Extend size', '扩展大小', 'winlogbeat');
INSERT INTO `event_dic` VALUES (448, 'Query semaphore state', '查询信号量状态', 'winlogbeat');
INSERT INTO `event_dic` VALUES (449, 'Modify semaphore state', '修改信号量状态', 'winlogbeat');
INSERT INTO `event_dic` VALUES (464, 'Use symbolic link', '使用符号链接', 'winlogbeat');
INSERT INTO `event_dic` VALUES (480, 'Force thread termination', '强制线程终止', 'winlogbeat');
INSERT INTO `event_dic` VALUES (481, 'Suspend or resume thread', '挂起或恢复线程', 'winlogbeat');
INSERT INTO `event_dic` VALUES (482, 'Send an alert to thread', '向线程发送警报', 'winlogbeat');
INSERT INTO `event_dic` VALUES (483, 'Get thread context', '获取线程上下文', 'winlogbeat');
INSERT INTO `event_dic` VALUES (484, 'Set thread context', '设置线程上下文', 'winlogbeat');
INSERT INTO `event_dic` VALUES (485, 'Set thread information', '设置线程信息', 'winlogbeat');
INSERT INTO `event_dic` VALUES (486, 'Query thread information', '查询线程信息', 'winlogbeat');
INSERT INTO `event_dic` VALUES (487, 'Assign a token to the thread', '给线程分配一个令牌', 'winlogbeat');
INSERT INTO `event_dic` VALUES (488, 'Cause thread to directly impersonate another thread', '使线程直接模拟另一个线程', 'winlogbeat');
INSERT INTO `event_dic` VALUES (489, 'Directly impersonate this thread', '直接模拟此线程', 'winlogbeat');
INSERT INTO `event_dic` VALUES (496, 'Query timer state', '查询计时器状态', 'winlogbeat');
INSERT INTO `event_dic` VALUES (497, 'Modify timer state', '修改计时器状态', 'winlogbeat');
INSERT INTO `event_dic` VALUES (511, 'AssignAsPrimary', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (512, 'Duplicate', '复制', 'winlogbeat');
INSERT INTO `event_dic` VALUES (513, 'Impersonate', '冒充', 'winlogbeat');
INSERT INTO `event_dic` VALUES (514, 'Query', '查询', 'winlogbeat');
INSERT INTO `event_dic` VALUES (515, 'QuerySource', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (516, 'AdjustPrivileges', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (517, 'AdjustGroups', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (518, 'AdjustDefaultDacl', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (519, 'AdjustSessionID', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (527, 'Create instance of object type', '创建对象类型的实例', 'winlogbeat');
INSERT INTO `event_dic` VALUES (543, 'Query State', '查询状态', 'winlogbeat');
INSERT INTO `event_dic` VALUES (544, 'Modify State', '修改状态', 'winlogbeat');
INSERT INTO `event_dic` VALUES (545, 'Channel read message', '信道读取消息', 'winlogbeat');
INSERT INTO `event_dic` VALUES (546, 'Channel write message', '信道写入消息', 'winlogbeat');
INSERT INTO `event_dic` VALUES (547, 'Channel query information', '信道查询信息', 'winlogbeat');
INSERT INTO `event_dic` VALUES (548, 'Channel set information', '信道设置信息', 'winlogbeat');
INSERT INTO `event_dic` VALUES (561, 'Assign process', '分配过程', 'winlogbeat');
INSERT INTO `event_dic` VALUES (562, 'Set Attributes', '设置属性', 'winlogbeat');
INSERT INTO `event_dic` VALUES (563, 'Query Attributes', '查询属性', 'winlogbeat');
INSERT INTO `event_dic` VALUES (564, 'Terminate Job', '终止作业', 'winlogbeat');
INSERT INTO `event_dic` VALUES (565, 'Set Security Attributes', '设置安全属性', 'winlogbeat');
INSERT INTO `event_dic` VALUES (577, 'ConnectToServer', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (578, 'ShutdownServer', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (579, 'InitializeServer', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (580, 'CreateDomain', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (581, 'EnumerateDomains', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (582, 'LookupDomain', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (593, 'ReadPasswordParameters', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (594, 'WritePasswordParameters', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (595, 'ReadOtherParameters', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (596, 'WriteOtherParameters', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (597, 'CreateUser', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (598, 'CreateGlobalGroup', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (599, 'CreateLocalGroup', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (600, 'GetLocalGroupMembership', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (601, 'ListAccounts', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (602, 'LookupIDs', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (603, 'AdministerServer', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (641, 'ReadGeneralInformation', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (642, 'ReadPreferences', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (643, 'WritePreferences', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (644, 'ReadLogon', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (645, 'ReadAccount', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (647, 'ChangePassword (with knowledge of old password)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (648, 'SetPassword (without knowledge of old password)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (649, 'ListGroups', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (650, 'ReadGroupMembership', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (651, 'ChangeGroupMembership', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (657, 'View non-sensitive policy information', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (658, 'View system audit requirements', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (659, 'Get sensitive policy information', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (660, 'Modify domain trust relationships', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (661, 'Create special accounts (for assignment of user rights)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (662, 'Create a secret object', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (663, 'Create a privilege', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (664, 'Set default quota limits', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (665, 'Change system audit requirements', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (666, 'Administer audit log attributes', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (667, 'Enable/Disable LSA', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (668, 'Lookup Names/SIDs', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (669, 'Change secret value', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (670, 'Query secret value', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (685, 'Query trusted domain name/SID', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (686, 'Retrieve the controllers in the trusted domain', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (687, 'Change the controllers in the trusted domain', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (688, 'Query the Posix ID offset assigned to the trusted domain', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (689, 'Change the Posix ID offset assigned to the trusted domain', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (701, 'Query account information', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (702, 'Change privileges assigned to account', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (703, 'Change quotas assigned to account', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (704, 'Change logon capabilities assigned to account', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (705, 'Change the Posix ID offset assigned to the accounted domain', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (717, 'KeyedEvent Wait', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (718, 'KeyedEvent Wake', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (733, 'Enumerate desktops', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (734, 'Read attributes', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (735, 'Access Clipboard', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (736, 'Create desktop', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (737, 'Write attributes', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (738, 'Access global atoms', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (739, 'Exit windows', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (740, 'Unused Access Flag', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (741, 'Include this windowstation in enumerations', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (742, 'Read screen', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (743, 'Read Objects', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (744, 'Create window', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (745, 'Create menu', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (746, 'Hook control', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (747, 'Journal (record)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (748, 'Journal (playback)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (749, 'Include this desktop in enumerations', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (750, 'Write objects', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (751, 'Switch to this desktop', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (752, 'Administer print server', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (753, 'Enumerate printers', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (754, 'Full Control', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (755, 'Print', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (756, 'Administer Document', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (757, 'Connect to service controller', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (758, 'Create a new service', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (759, 'Enumerate services', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (760, 'Lock service database for exclusive access', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (761, 'Query service database lock state', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (762, 'Set last-known-good state of service database', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (763, 'Query service configuration information', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (764, 'Set service configuration information', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (765, 'Query status of service', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (766, 'Enumerate dependencies of service', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (767, 'Start the service', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (768, 'Stop the service', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (769, 'Pause or continue the service', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (770, 'Query information from service', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (771, 'Issue service-specific control commands', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (772, 'DDE Share Read', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (773, 'DDE Share Write', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (774, 'DDE Share Initiate Static', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (775, 'DDE Share Initiate Link', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (776, 'DDE Share Request', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (777, 'DDE Share Advise', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (778, 'DDE Share Poke', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (779, 'DDE Share Execute', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (780, 'DDE Share Add Items', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (781, 'DDE Share List Items', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (782, 'Create Child', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (783, 'Delete Child', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (784, 'List Contents', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (785, 'Write Self', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (786, 'Read Property', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (787, 'Write Property', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (788, 'Delete Tree', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (789, 'List Object', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (790, 'Control Access', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (798, 'Audit Set System Policy', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (799, 'Audit Query System Policy', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (800, 'Audit Set Per User Policy', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (801, 'Audit Query Per User Policy', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (802, 'Audit Enumerate Users', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (803, 'Audit Set Options', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (804, 'Audit Query Options', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (805, 'Port sharing (read)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (806, 'Port sharing (write)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (807, 'Default credentials', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (808, 'Credentials manager', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (809, 'Fresh credentials', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (810, 'Kerberos', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (811, 'Preshared key', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (812, 'Unknown authentication', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (816, 'SHA1', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (817, 'Local computer', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (818, 'Remote computer', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (821, 'Sent second (KE) payload', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (822, 'Sent third (ID) payload', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (827, 'Sent final payload', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (828, 'Complete', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (829, 'Unknown', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (832, 'IKE/AuthIP DoS prevention mode started', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (833, 'IKE/AuthIP DoS prevention mode stopped', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (835, 'Not enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (837, 'Sent first (EM attributes) payload', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (838, 'Sent second (SSPI) payload', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (839, 'Sent third (hash) payload', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (840, 'IKEv1', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (841, 'AuthIP', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (842, 'Anonymous', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (843, 'NTLM V2', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (844, 'CGA', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (845, 'Certificate', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (846, 'SSL', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (848, 'DH group 1', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (849, 'DH group 2', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (850, 'DH group 14', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (851, 'DH group ECP 256', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (852, 'DH group ECP 384', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (856, 'Certificate ECDSA P256', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (857, 'Certificate ECDSA P384', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (858, 'SSL ECDSA P256', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (859, 'SSL ECDSA P384', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (860, 'SHA 256', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (861, 'SHA 384', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (862, 'IKEv2', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (863, 'EAP payload sent', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (864, 'Authentication payload sent', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (865, 'EAP', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (866, 'DH group 24', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (868, 'Logon/Logoff', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (869, 'Object Access', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (870, 'Privilege Use', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (871, 'Detailed Tracking', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (872, 'Policy Change', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (873, 'Account Management', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (874, 'DS Access', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (875, 'Account Logon', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (876, 'Success removed', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (877, 'Success Added', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (878, 'Failure removed', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (879, 'Failure added', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (880, 'Success include removed', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (881, 'Success include added', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (882, 'Success exclude removed', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (883, 'Success exclude added', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (884, 'Failure include removed', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (885, 'Failure include added', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (886, 'Failure exclude removed', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (887, 'Failure exclude added', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (888, 'Security State Change', '安全状态更改', 'winlogbeat');
INSERT INTO `event_dic` VALUES (889, 'Security System Extension', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (890, 'System Integrity', '系统完整性', 'winlogbeat');
INSERT INTO `event_dic` VALUES (891, 'IPsec Driver', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (892, 'Other System Events', '其他系统事件', 'winlogbeat');
INSERT INTO `event_dic` VALUES (893, 'Logon', '登录', 'winlogbeat');
INSERT INTO `event_dic` VALUES (894, 'Logoff', '注销', 'winlogbeat');
INSERT INTO `event_dic` VALUES (895, 'Account Lockout', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (896, 'IPsec Main Mode', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (897, 'Special Logon', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (898, 'IPsec Quick Mode', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (899, 'IPsec Extended Mode', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (900, 'Other Logon/Logoff Events', '其他登录/注销事件', 'winlogbeat');
INSERT INTO `event_dic` VALUES (901, 'Network Policy Server', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (902, 'User / Device Claims', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (903, 'Group Membership', '组成员', 'winlogbeat');
INSERT INTO `event_dic` VALUES (904, 'File System', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (905, 'Registry', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (906, 'Kernel Object', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (907, 'SAM', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (908, 'Other Object Access Events', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (909, 'Certification Services', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (910, 'Application Generated', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (911, 'Handle Manipulation', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (912, 'File Share', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (913, 'Filtering Platform Packet Drop', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (914, 'Filtering Platform Connection', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (915, 'Detailed File Share', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (916, 'Removable Storage', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (917, 'Central Policy Staging', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (918, 'Sensitive Privilege Use', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (919, 'Non Sensitive Privilege Use', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (920, 'Other Privilege Use Events', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (921, 'Process Creation', '流程创建', 'winlogbeat');
INSERT INTO `event_dic` VALUES (922, 'Process Termination', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (923, 'DPAPI Activity', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (924, 'RPC Events', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (925, 'Plug and Play Events', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (926, 'Token Right Adjusted Events', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (927, 'Audit Policy Change', '审核策略更改', 'winlogbeat');
INSERT INTO `event_dic` VALUES (928, 'Authentication Policy Change', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (929, 'Authorization Policy Change', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (930, 'MPSSVC Rule-Level Policy Change', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (931, 'Filtering Platform Policy Change', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (932, 'Other Policy Change Events', '其他政策变更事件', 'winlogbeat');
INSERT INTO `event_dic` VALUES (933, 'User Account Management', '用户帐户管理', 'winlogbeat');
INSERT INTO `event_dic` VALUES (934, 'Computer Account Management', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (935, 'Security Group Management', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (936, 'Distribution Group Management', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (937, 'Application Group Management', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (938, 'Other Account Management Events', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (939, 'Directory Service Access', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (940, 'Directory Service Changes', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (941, 'Directory Service Replication', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (942, 'Detailed Directory Service Replication', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (943, 'Credential Validation', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (944, 'Kerberos Service Ticket Operations', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (945, 'Other Account Logon Events', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (946, 'Kerberos Authentication Service', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (947, 'Inbound', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (948, 'Outbound', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (950, 'Bidirectional', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (951, 'IP Packet', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (954, 'Stream', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (955, 'Datagram Data', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (956, 'ICMP Error', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (957, 'MAC 802.3', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (958, 'MAC Native', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (959, 'vSwitch', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (960, 'Resource Assignment', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (961, 'Listen', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (962, 'Receive/Accept', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (963, 'Connect', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (964, 'Flow Established', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (965, 'Resource Release', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (966, 'Endpoint Closure', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (967, 'Connect Redirect', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (968, 'Bind Redirect', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (969, 'Stream Packet', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (970, 'ICMP Echo-Request', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (971, 'vSwitch Ingress', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (972, 'vSwitch Egress', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (973, '<Binary>', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (974, '[NULL]', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (975, 'Value Added', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (976, 'Value Deleted', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (977, 'Active Directory Domain Services', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (978, 'Active Directory Lightweight Directory Services', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (981, 'Value Added With Expiration Time', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (982, 'Value Deleted With Expiration Time', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (983, 'Value Auto Deleted With Expiration Time', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (984, 'Add', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (986, 'Boot-time', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (987, 'Persistent', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (988, 'Not persistent', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (989, 'Block', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (990, 'Permit', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (991, 'Callout', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (993, 'SHA-1', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (994, 'SHA-256', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (995, 'AES-GCM 128', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (996, 'AES-GCM 192', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (997, 'AES-GCM 256', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1007, 'AES-GMAC 128', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1008, 'AES-GMAC 192', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1009, 'AES-GMAC 256', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1010, 'AuthNoEncap Transport', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1011, 'Enable WMI Account', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1012, 'Execute Method', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1013, 'Full Write', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1014, 'Partial Write', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1015, 'Provider Write', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1016, 'Remote Access', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1017, 'Subscribe', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1018, 'Publish', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1019, '3DES', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1020, 'added-group-account', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1021, 'added-group-account-to', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1022, 'AddMember', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1023, 'AES-128', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1024, 'AES-192', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1025, 'AES-256', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1026, 'DELETE', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1027, 'deleted-group-account', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1028, 'deleted-group-account-from', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1029, 'DES', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1030, 'Enabled', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1031, 'Forward', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1032, 'Initiator', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1033, 'ListMembers', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1034, 'MD5', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1035, 'modified-group-account', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1036, 'No', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1037, 'No state', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1038, 'None', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1039, 'ReadInformation', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1040, 'RemoveMember', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1041, 'Responder', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1042, 'Sent first (SA) payload', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1043, 'System', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1044, 'Transport', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1045, 'Tunnel', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1046, 'Undefined Access (no effect) Bit 1', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1047, 'Undefined Access (no effect) Bit 10', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1048, 'Undefined Access (no effect) Bit 11', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1049, 'Undefined Access (no effect) Bit 12', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1050, 'Undefined Access (no effect) Bit 13', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1051, 'Undefined Access (no effect) Bit 14', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1052, 'Undefined Access (no effect) Bit 15', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1053, 'Undefined Access (no effect) Bit 2', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1054, 'Undefined Access (no effect) Bit 3', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1055, 'Undefined Access (no effect) Bit 4', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1056, 'Undefined Access (no effect) Bit 5', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1057, 'Undefined Access (no effect) Bit 6', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1058, 'Undefined Access (no effect) Bit 7', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1059, 'Undefined Access (no effect) Bit 8', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1060, 'Undefined Access (no effect) Bit 9', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1061, 'WriteAccount', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1062, 'Yes', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1063, 'READ_CONTROL', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1064, 'WRITE_DAC', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1065, 'WRITE_OWNER', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1066, 'SYNCHRONIZE', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1067, 'ACCESS_SYS_SEC', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1068, 'MAX_ALLOWED', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1069, 'Unknown specific access (bit 0)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1070, 'Unknown specific access (bit 1)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1071, 'Unknown specific access (bit 2)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1072, 'Unknown specific access (bit 3)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1073, 'Unknown specific access (bit 4)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1074, 'Unknown specific access (bit 5)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1075, 'Unknown specific access (bit 6)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1076, 'Unknown specific access (bit 7)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1077, 'Unknown specific access (bit 8)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1078, 'Unknown specific access (bit 9)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1079, 'Unknown specific access (bit 10)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1080, 'Unknown specific access (bit 11)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1081, 'Unknown specific access (bit 12)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1082, 'Unknown specific access (bit 13)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1083, 'Unknown specific access (bit 14)', NULL, 'winlogbeat');
INSERT INTO `event_dic` VALUES (1084, 'Unknown specific access (bit 15)', NULL, 'winlogbeat');
-- ----------------------------
-- 事件组
-- ----------------------------
DROP TABLE IF EXISTS `event_group`;
CREATE TABLE `event_group`  (
                                `event_group_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '事件关联组ID',
                                `event_group_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '事件关联组名称',
                                `event_group_note` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '事件关联组说明',
                                PRIMARY KEY (`event_group_id`) USING BTREE,
                                UNIQUE INDEX `UNIQUE_EVENT_GROUP_NAME`(`event_group_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '事件关联组' ROW_FORMAT = Compact;
-- ----------------------------
-- 事件组-事件  关系表   1:N
-- ----------------------------
DROP TABLE IF EXISTS `event_group_relations`;
CREATE TABLE `event_group_relations`  (
                                          `event_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '事件ID',
                                          `event_group_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '事件关联组ID',
                                          UNIQUE INDEX `UNIQUE_INDEX`(`event_id`, `event_group_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '事件组与事件关系表' ROW_FORMAT = Compact;

-- ----------------------------
-- 旧版 角色权限表（已停用）
-- ----------------------------
DROP TABLE IF EXISTS `function`;
CREATE TABLE `function`  (
                             `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                             `resource` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '方法路径',
                             `describes` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '功能描述',
                             `role` int(2) NULL DEFAULT NULL,
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- 按钮表，记录按钮的信息，按钮与菜单的关系为 N:1
-- ----------------------------
DROP TABLE IF EXISTS `HS_Button`;
CREATE TABLE `HS_Button`  (
                              `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                              `buttonName` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                              `buttonID` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                              `pk_menu_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- 系统默认的按钮信息
-- ----------------------------
INSERT INTO `HS_Button` VALUES ('1fed4f7e-9366-11ea-8eeb-000c2900a492', '添加资产', 'equipment_addEquipment', '10301');
INSERT INTO `HS_Button` VALUES ('1fed680b-9366-11ea-8eeb-000c2900a492', '删除资产', 'equipment_moredeleteEquipment', '10301');
INSERT INTO `HS_Button` VALUES ('1fed7f91-9366-11ea-8eeb-000c2900a492', '所有资产报表', 'equipment_allCharts', '10301');
INSERT INTO `HS_Button` VALUES ('1fed9672-9366-11ea-8eeb-000c2900a492', '修改资产', 'equipment_reviseEquipment', '10301');
INSERT INTO `HS_Button` VALUES ('1fedaac1-9366-11ea-8eeb-000c2900a492', '查询资产日志', 'equipment_equipmentLogs', '10301');
INSERT INTO `HS_Button` VALUES ('1fedbf68-9366-11ea-8eeb-000c2900a492', '查询资产报表', 'equipment_equipmentCharts', '10301');
INSERT INTO `HS_Button` VALUES ('1fedd32f-9366-11ea-8eeb-000c2900a492', '查询资产事件', 'equipment_equipmentEvents', '10301');
INSERT INTO `HS_Button` VALUES ('1fede73f-9366-11ea-8eeb-000c2900a492', '设置安全策略', 'equipment_equipmentSafe', '10301');
INSERT INTO `HS_Button` VALUES ('1fedfc71-9366-11ea-8eeb-000c2900a492', '潜在威胁分析', 'equipment_equipmentThreat', '10301');
INSERT INTO `HS_Button` VALUES ('469dd7de-17cd-11ea-84c9-000c2900a492', '添加机构', 'userManage_addDepartment', '10101');
INSERT INTO `HS_Button` VALUES ('469e0e61-17cd-11ea-84c9-000c2900a492', '修改机构', 'userManage_reviseDepartment', '10101');
INSERT INTO `HS_Button` VALUES ('469e3dcc-17cd-11ea-84c9-000c2900a492', '删除机构', 'userManage_deleteDepartment', '10101');
INSERT INTO `HS_Button` VALUES ('469e69f5-17cd-11ea-84c9-000c2900a492', '添加用户', 'userManage_addUser', '10101');
INSERT INTO `HS_Button` VALUES ('469e963f-17cd-11ea-84c9-000c2900a492', '删除用户', 'userManage_deleteUser', '10101');
INSERT INTO `HS_Button` VALUES ('469ec459-17cd-11ea-84c9-000c2900a492', '修改用户', 'userManage_reviseUser', '10101');
INSERT INTO `HS_Button` VALUES ('469eef80-17cd-11ea-84c9-000c2900a492', '批量删除用户', 'userManage_moredeleteUser', '10101');
INSERT INTO `HS_Button` VALUES ('a09d243a-e73c-11ea-a0a5-000c2900a492', '资产可视化按钮', 'equipment2_dashboard', '10306');

-- ----------------------------
-- 菜单表
-- ----------------------------
DROP TABLE IF EXISTS `HS_Menu`;
CREATE TABLE `HS_Menu`  (
                            `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                            `menuName` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单名称',
                            `superiorId` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上级id',
                            `childId` int(20) NULL DEFAULT NULL,
                            `url` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '路径',
                            `icon` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图标',
                            `state` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否启用',
                            `fk_system_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- 菜单表中的内容
-- ----------------------------
INSERT INTO `HS_Menu` VALUES ('01563052-fbbc-4b9a-aa19-32c961981bbb', 'User-Agent信息', '11300', 7, 'flowManage/userAgentInfo2.vue', '', '1', '2');
INSERT INTO `HS_Menu` VALUES ('10000', '首页', NULL, 0, '', 'fa fa-desktop', '1', '3');
INSERT INTO `HS_Menu` VALUES ('10100', '系统管理', '', 1, '', 'fa fa-users', '1', '1');
INSERT INTO `HS_Menu` VALUES ('10101', '用户管理 ', '10100', 3, 'userManage/userManage.html', '', '1', '1');
INSERT INTO `HS_Menu` VALUES ('10102', '系统菜单 ', '10100', 1, 'menu/menuManage.html', '', '1', '1');
INSERT INTO `HS_Menu` VALUES ('10103', '角色管理 ', '10100', 2, 'roleManage/roleManage.html', '', '1', '1');
INSERT INTO `HS_Menu` VALUES ('10104', '角色说明', '10100', 4, 'roleManage/roleExplain.html', NULL, '1', '1');
INSERT INTO `HS_Menu` VALUES ('10300', '资产管理', '', 3, '', 'fa fa-laptop', '1', '7eade8fb-ada5-4b84-834a-fe191738a9e3');
INSERT INTO `HS_Menu` VALUES ('10301', '虚拟资产管理', '10300', 1, 'device/device.html', '', '1', '7eade8fb-ada5-4b84-834a-fe191738a9e3');
INSERT INTO `HS_Menu` VALUES ('10303', '资产画像', '10300', 6, 'logPro/rankingList.html', '', '1', '7eade8fb-ada5-4b84-834a-fe191738a9e3');
INSERT INTO `HS_Menu` VALUES ('10304', '资产配置管理', '10300', 8, 'device/deviceScan.html', '', '1', '7eade8fb-ada5-4b84-834a-fe191738a9e3');
INSERT INTO `HS_Menu` VALUES ('10305', '应用资产画像', '10300', 7, 'logPro/urlIpRanking.html', '', '1', '7eade8fb-ada5-4b84-834a-fe191738a9e3');
INSERT INTO `HS_Menu` VALUES ('10306', '虚拟资产概览', '10300', 2, 'device/device2.html', '', '1', '7eade8fb-ada5-4b84-834a-fe191738a9e3');
INSERT INTO `HS_Menu` VALUES ('10307', '资产监控', '10300', 4, 'device/deviceMonitor.html', '', '1', '7eade8fb-ada5-4b84-834a-fe191738a9e3');
INSERT INTO `HS_Menu` VALUES ('10308', '业务系统监控', '10300', 5, 'device/allDeviceMonitor.html', '', '1', '7eade8fb-ada5-4b84-834a-fe191738a9e3');
INSERT INTO `HS_Menu` VALUES ('10400', '数据源管理', NULL, 4, NULL, 'fa fa-database', '1', '3');
INSERT INTO `HS_Menu` VALUES ('10401', '数据源管理', '10400', 0, 'sourceFile/sourceFile.html', NULL, '1', '3');
INSERT INTO `HS_Menu` VALUES ('10500', '日志管理', NULL, 5, NULL, 'fa fa-list-alt', '1', '3');
INSERT INTO `HS_Menu` VALUES ('10503', 'DNS日志', '10500', 4, 'logPro/dnsLogs.html', NULL, '1', '3');
INSERT INTO `HS_Menu` VALUES ('10504', 'DHCP日志', '10500', 5, 'logPro/dhcpLogs.html', NULL, '1', '3');
INSERT INTO `HS_Menu` VALUES ('10600', '动作管理', NULL, 7, '', 'fa fa-credit-card', '1', '3');
INSERT INTO `HS_Menu` VALUES ('10601', '动作列表', '10600', 0, 'actionManage/actionManage.html', NULL, '1', '3');
INSERT INTO `HS_Menu` VALUES ('10700', '事件管理', NULL, 8, NULL, 'fa fa-file-text-o', '1', '3');
INSERT INTO `HS_Menu` VALUES ('10701', '事件组', '10700', 0, 'eventGroup/eventGroup.vue', '', '1', '3');
INSERT INTO `HS_Menu` VALUES ('10703', '事件列表', '10700', 9, 'eventManage/eventList.html', NULL, '1', '3');
INSERT INTO `HS_Menu` VALUES ('10800', '平台管理', NULL, 9, NULL, 'fa fa-th', '1', '3');
INSERT INTO `HS_Menu` VALUES ('10801', '审计中心', '10800', 0, 'auditLog/auditLog.html', NULL, '1', '3');
INSERT INTO `HS_Menu` VALUES ('10803', '流量控制中心', '11200', 2, 'auditLog/flowServiceManage.html', NULL, '1', '2');
INSERT INTO `HS_Menu` VALUES ('10900', '流量管理', NULL, 6, NULL, 'fa fa-code-fork', '1', '2');
INSERT INTO `HS_Menu` VALUES ('10901', '业务流分析', '11500', 10, 'logPro/networkTopology.html', '', '1', '2');
INSERT INTO `HS_Menu` VALUES ('10902', '流量日志\r\n', '10900', 0, 'logPro/flowLogs.html', NULL, '1', '2');
INSERT INTO `HS_Menu` VALUES ('10903', 'HTTP日志', '10900', 2, 'logPro/httpLogs.html\r\n', NULL, '1', '2');
INSERT INTO `HS_Menu` VALUES ('10904', '服务列表 ', '10900', 3, 'device/serviceList.html', NULL, '1', '2');
INSERT INTO `HS_Menu` VALUES ('10905', '应用画像', '10900', 4, 'logPro/urlRanking.html', NULL, '1', '2');
INSERT INTO `HS_Menu` VALUES ('11000', '流量监控', '', 7, '', 'fa fa-clock-o', '1', '2');
INSERT INTO `HS_Menu` VALUES ('11100', '首页', '', 0, '', 'fa fa-desktop', '1', '2');
INSERT INTO `HS_Menu` VALUES ('11101', '首页 ', '11100', 0, 'flowManage/flowIndex', '', '1', '2');
INSERT INTO `HS_Menu` VALUES ('11200', '平台管理', '', 10, '', 'fa fa-th', '1', '2');
INSERT INTO `HS_Menu` VALUES ('11300', '流量统计', '', 8, '', 'fa fa-bar-chart', '1', '2');
INSERT INTO `HS_Menu` VALUES ('11400', '数据可视化', '', 9, '', 'fa fa-lightbulb-o', '1', '404e33f0-f715-4d8c-85a4-86d71ce0e063');
INSERT INTO `HS_Menu` VALUES ('11500', '业务流分析', '', 9, '', 'fa fa-object-ungroup', '1', '2');
INSERT INTO `HS_Menu` VALUES ('11600', '告警管理', '', 5, '', 'fa fa-bell-o', '1', '0af16e0c-cfd3-48f5-bdfa-15a53a54cb36');
INSERT INTO `HS_Menu` VALUES ('1198792f-ee87-4965-9366-a7cdc9e601cc', '复合查询', '10500', 2, 'logsManage/complexSearch.vue', '', '1', '3');
INSERT INTO `HS_Menu` VALUES ('16743c96-6422-4029-bd45-e8340c1addcd', '资产流量', '11300', 5, 'flowManage/equipmentFlow2.vue', '', '1', '2');
INSERT INTO `HS_Menu` VALUES ('1a3fd295-4d1c-4097-95fd-21fac4b4e675', 'SIEM（测试）', '11400', 4, 'common/customChart.vue', '', '1', '404e33f0-f715-4d8c-85a4-86d71ce0e063');
INSERT INTO `HS_Menu` VALUES ('2387997b-83f3-4703-91c3-58a077e2e111', 'IP主机流量', '11300', 1, 'flowManage/IPHostFlow2.vue', '', '1', '2');
INSERT INTO `HS_Menu` VALUES ('3858056b-9baf-44fc-8116-5fbf2a7cb5cf', '告警列表', '11600', 0, 'alarmManage/alarmList.vue', '', '1', '0af16e0c-cfd3-48f5-bdfa-15a53a54cb36');
INSERT INTO `HS_Menu` VALUES ('40b6133c-f455-4fc0-9e80-e8cc9ea9fdee', '广播包/组播包', '11000', 5, 'flowManage/mulAndBro', '', '1', '2');
INSERT INTO `HS_Menu` VALUES ('436d1f4c-c37b-4090-921c-3fbbc0a35808', '数据包类型', '11300', 4, 'flowManage/packetType2.vue', '', '1', '2');
INSERT INTO `HS_Menu` VALUES ('4f80bfab-ce60-4ab3-8b8a-6a3734af37c9', '精确查询', '10500', 1, 'logsManage/accurateSearch2.vue', '', '1', '3');
INSERT INTO `HS_Menu` VALUES ('525a59b7-e794-4d25-9b5a-ac3926498fcc', 'IP主机流量', '11000', 3, 'flowManage/IPHostFlow', '', '1', '2');
INSERT INTO `HS_Menu` VALUES ('5408da1f-324d-4121-8000-59024faceac3', '协议流量', '11300', 2, 'flowManage/protocolFlow2.vue', '', '1', '2');
INSERT INTO `HS_Menu` VALUES ('56d24f8e-b552-4f23-905f-13a267de2e8c', '资产流量', '11000', 7, 'flowManage/equipmentFlow', '', '1', '2');
INSERT INTO `HS_Menu` VALUES ('56d2a887-0506-46b1-bd18-4629258000da', '逻辑资产管理', '10300', 0, 'equipment/logicAssetsList.vue', '', '1', '7eade8fb-ada5-4b84-834a-fe191738a9e3');
INSERT INTO `HS_Menu` VALUES ('59cc182b-b4f3-4a15-ab53-6f61ec43fbc4', '全局端口流量', '11300', 6, 'flowManage/portFlow2.vue', '', '1', '2');
INSERT INTO `HS_Menu` VALUES ('5af2d7e5-5ce4-40d7-8a65-1196b1128873', '首页', '10000', 1, 'index/index_n.vue', '', '1', '3');
INSERT INTO `HS_Menu` VALUES ('636c9ae1-5848-4799-bd89-64a64988b678', '数据包类型', '11000', 6, 'flowManage/packetType', '', '1', '2');
INSERT INTO `HS_Menu` VALUES ('63d60e7e-9c4c-4abf-a5c8-eef73844eabb', '控制中心', '10800', 1, 'platformManage/controlCenter_n.vue', '', '1', '3');
INSERT INTO `HS_Menu` VALUES ('689574a3-8e9a-41fb-9c94-fd17a2c28c8f', '资产组', '10300', 3, 'assetGroup/assetGroup.vue', '', '1', '7eade8fb-ada5-4b84-834a-fe191738a9e3');
INSERT INTO `HS_Menu` VALUES ('6d5eef70-c72f-4a40-b154-fd88db5c0b39', '事件搜索', '10700', 2, 'eventManage/eventSearch2.vue', '', '1', '3');
INSERT INTO `HS_Menu` VALUES ('7bb1e4c7-bf1d-4920-b885-a76b6e6b4835', '应用性能分析', '11000', 10, 'flow/performanceAnalysis.vue', '', '1', '2');
INSERT INTO `HS_Menu` VALUES ('7ef6f59a-d4ef-4a51-9643-95fb120ed670', '文件日志查询', '10500', 3, 'logsManage/fileLogSearch.vue', '', '1', '3');
INSERT INTO `HS_Menu` VALUES ('8f34b263-6606-489d-a880-10493e3d2f79', '全局端口流量', '11000', 8, 'flowManage/portFlow', '', '1', '2');
INSERT INTO `HS_Menu` VALUES ('8ff0b7f4-c2e5-46cc-9f18-14607c12323c', '广播包/组播包', '11300', 3, 'flowManage/mulAndBro2.vue', '', '1', '2');
INSERT INTO `HS_Menu` VALUES ('93a2eaa7-ec05-4bc3-bcdf-6fd39c97e6d5', '元数据', '11400', 3, 'dashboard/property.vue', '', '1', '404e33f0-f715-4d8c-85a4-86d71ce0e063');
INSERT INTO `HS_Menu` VALUES ('a4b36b08-37c4-4a83-b69b-b4631967280c', 'User-Agent信息', '11000', 9, 'flowManage/userAgentInfo', '', '1', '2');
INSERT INTO `HS_Menu` VALUES ('a5b2a483-bfc4-4b99-a347-966f5f68920f', '全文检索', '10500', 0, 'logsManage/fulltextRetrieval.vue', '', '1', '3');
INSERT INTO `HS_Menu` VALUES ('ba9c93d3-2a63-4c34-8de8-b64fa94432cf', '虚拟资产(资产组)', '10300', 2, 'equipment/equipment_group.vue', '', '1', '7eade8fb-ada5-4b84-834a-fe191738a9e3');
INSERT INTO `HS_Menu` VALUES ('c323f476-d17a-4fc3-9fdf-bdc49f879f97', '协议流量', '11000', 4, 'flowManage/protocolFlow', '', '1', '2');
INSERT INTO `HS_Menu` VALUES ('c465f4c6-192d-4c46-b7ad-e700fae0b542', '仪表盘', '11400', 1, 'dashboard/dashboardList.vue', '', '1', '404e33f0-f715-4d8c-85a4-86d71ce0e063');
INSERT INTO `HS_Menu` VALUES ('cbffef63-8f45-4817-9946-1e230f1d16c4', '全局实时流量', '11000', 2, 'flowManage/realTimeFlow', '', '1', '2');
INSERT INTO `HS_Menu` VALUES ('d47006fa-8d2a-4980-81c5-f2aa593253f2', '事件关联查询', '10700', 1, 'eventManage/eventSearch_group.vue', '', '1', '3');
INSERT INTO `HS_Menu` VALUES ('fded6532-6724-4862-b155-7d9dd37a8502', '图表列表', '11400', 2, 'dashboard/chartsList.vue', '', '1', '404e33f0-f715-4d8c-85a4-86d71ce0e063');

-- ----------------------------
-- 角色表
-- ----------------------------
DROP TABLE IF EXISTS `HS_Role`;
CREATE TABLE `HS_Role`  (
                            `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                            `role_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                            `note` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- 角色表中设置的默认角色信息
-- ----------------------------
INSERT INTO `HS_Role` VALUES ('6c481e2a-17e5-4f07-92b9-832f5ae8d1d7', '配置员', '拥有事件组、资产管理和审计中心的权限');
INSERT INTO `HS_Role` VALUES ('74477608-ae69-4b95-8381-a6bd7dd4e199', '审计员', '查看审计数据');
INSERT INTO `HS_Role` VALUES ('7bd3e999-779a-48e1-b162-38a8e851c446', '操作员', '按实际需要配置相关功能给操作人员使用');
INSERT INTO `HS_Role` VALUES ('admin', '管理员', '涵盖大部分功能模块的增删改查功能');
INSERT INTO `HS_Role` VALUES ('master', '超级管理员', '');

-- ----------------------------
-- 菜单-角色关系表（已停用，使用菜单&按钮-角色关系表 表明角色与菜单以及菜单下按钮的关系）
-- ----------------------------
DROP TABLE IF EXISTS `HS_RoleMenu`;
CREATE TABLE `HS_RoleMenu`  (
                                `fk_menuid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                `fk_roleid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                PRIMARY KEY (`fk_menuid`, `fk_roleid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;


-- ----------------------------
-- 菜单&按钮-角色关系表（表明角色与菜单以及菜单下按钮的关系）
-- ----------------------------
DROP TABLE IF EXISTS `HS_RoleMenuButton`;
CREATE TABLE `HS_RoleMenuButton`  (
                                      `fk_menuAndButon_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                      `fk_roleid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                      UNIQUE INDEX `UNIQUE_KEY`(`fk_menuAndButon_id`, `fk_roleid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- 系统默认配置的 菜单&按钮 与 角色之间的关系
-- ----------------------------
INSERT INTO `HS_RoleMenuButton` VALUES ('11100', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('11101', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10900', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10902', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10903', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10904', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10905', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('11000', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('cbffef63-8f45-4817-9946-1e230f1d16c4', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('525a59b7-e794-4d25-9b5a-ac3926498fcc', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('c323f476-d17a-4fc3-9fdf-bdc49f879f97', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('40b6133c-f455-4fc0-9e80-e8cc9ea9fdee', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('636c9ae1-5848-4799-bd89-64a64988b678', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('56d24f8e-b552-4f23-905f-13a267de2e8c', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('8f34b263-6606-489d-a880-10493e3d2f79', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('a4b36b08-37c4-4a83-b69b-b4631967280c', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('11300', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('2387997b-83f3-4703-91c3-58a077e2e111', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('5408da1f-324d-4121-8000-59024faceac3', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('8ff0b7f4-c2e5-46cc-9f18-14607c12323c', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('436d1f4c-c37b-4090-921c-3fbbc0a35808', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('16743c96-6422-4029-bd45-e8340c1addcd', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('59cc182b-b4f3-4a15-ab53-6f61ec43fbc4', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('01563052-fbbc-4b9a-aa19-32c961981bbb', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('7bb1e4c7-bf1d-4920-b885-a76b6e6b4835', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('11500', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10901', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('5af2d7e5-5ce4-40d7-8a65-1196b1128873', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10400', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10401', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('a5b2a483-bfc4-4b99-a347-966f5f68920f', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('4f80bfab-ce60-4ab3-8b8a-6a3734af37c9', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10503', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10504', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10600', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10601', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('6d5eef70-c72f-4a40-b154-fd88db5c0b39', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10703', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('404e33f0-f715-4d8c-85a4-86d71ce0e063', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('11400', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('c465f4c6-192d-4c46-b7ad-e700fae0b542', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('fded6532-6724-4862-b155-7d9dd37a8502', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('93a2eaa7-ec05-4bc3-bcdf-6fd39c97e6d5', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('7eade8fb-ada5-4b84-834a-fe191738a9e3', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10300', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10301', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10306', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10307', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10308', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10303', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10305', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10304', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('2', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('3', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10000', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10500', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('10700', 'visitor');
INSERT INTO `HS_RoleMenuButton` VALUES ('2', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('11100', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('11101', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10900', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10902', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10903', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10904', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10905', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('11000', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('cbffef63-8f45-4817-9946-1e230f1d16c4', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('525a59b7-e794-4d25-9b5a-ac3926498fcc', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('c323f476-d17a-4fc3-9fdf-bdc49f879f97', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('40b6133c-f455-4fc0-9e80-e8cc9ea9fdee', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('636c9ae1-5848-4799-bd89-64a64988b678', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('56d24f8e-b552-4f23-905f-13a267de2e8c', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('8f34b263-6606-489d-a880-10493e3d2f79', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('a4b36b08-37c4-4a83-b69b-b4631967280c', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('11300', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('2387997b-83f3-4703-91c3-58a077e2e111', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('5408da1f-324d-4121-8000-59024faceac3', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('8ff0b7f4-c2e5-46cc-9f18-14607c12323c', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('436d1f4c-c37b-4090-921c-3fbbc0a35808', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('16743c96-6422-4029-bd45-e8340c1addcd', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('59cc182b-b4f3-4a15-ab53-6f61ec43fbc4', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('01563052-fbbc-4b9a-aa19-32c961981bbb', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('7bb1e4c7-bf1d-4920-b885-a76b6e6b4835', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('11500', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10901', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('11200', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10803', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('5af2d7e5-5ce4-40d7-8a65-1196b1128873', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10400', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10401', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('a5b2a483-bfc4-4b99-a347-966f5f68920f', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('4f80bfab-ce60-4ab3-8b8a-6a3734af37c9', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10503', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10504', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10600', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10601', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('6d5eef70-c72f-4a40-b154-fd88db5c0b39', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10703', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10801', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('63d60e7e-9c4c-4abf-a5c8-eef73844eabb', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('404e33f0-f715-4d8c-85a4-86d71ce0e063', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('11400', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('c465f4c6-192d-4c46-b7ad-e700fae0b542', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('fded6532-6724-4862-b155-7d9dd37a8502', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('93a2eaa7-ec05-4bc3-bcdf-6fd39c97e6d5', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('7eade8fb-ada5-4b84-834a-fe191738a9e3', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10300', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('56d2a887-0506-46b1-bd18-4629258000da', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10301', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10306', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10307', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10308', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10303', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10305', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10304', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('3', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10000', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10500', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10700', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('10800', '74477608-ae69-4b95-8381-a6bd7dd4e199');
INSERT INTO `HS_RoleMenuButton` VALUES ('7eade8fb-ada5-4b84-834a-fe191738a9e3', '7bd3e999-779a-48e1-b162-38a8e851c446');
INSERT INTO `HS_RoleMenuButton` VALUES ('10300', '7bd3e999-779a-48e1-b162-38a8e851c446');
INSERT INTO `HS_RoleMenuButton` VALUES ('56d2a887-0506-46b1-bd18-4629258000da', '7bd3e999-779a-48e1-b162-38a8e851c446');
INSERT INTO `HS_RoleMenuButton` VALUES ('10301', '7bd3e999-779a-48e1-b162-38a8e851c446');
INSERT INTO `HS_RoleMenuButton` VALUES ('10306', '7bd3e999-779a-48e1-b162-38a8e851c446');
INSERT INTO `HS_RoleMenuButton` VALUES ('10307', '7bd3e999-779a-48e1-b162-38a8e851c446');
INSERT INTO `HS_RoleMenuButton` VALUES ('10308', '7bd3e999-779a-48e1-b162-38a8e851c446');
INSERT INTO `HS_RoleMenuButton` VALUES ('10303', '7bd3e999-779a-48e1-b162-38a8e851c446');
INSERT INTO `HS_RoleMenuButton` VALUES ('10305', '7bd3e999-779a-48e1-b162-38a8e851c446');
INSERT INTO `HS_RoleMenuButton` VALUES ('10304', '7bd3e999-779a-48e1-b162-38a8e851c446');
INSERT INTO `HS_RoleMenuButton` VALUES ('10701', '6c481e2a-17e5-4f07-92b9-832f5ae8d1d7');
INSERT INTO `HS_RoleMenuButton` VALUES ('10801', '6c481e2a-17e5-4f07-92b9-832f5ae8d1d7');
INSERT INTO `HS_RoleMenuButton` VALUES ('10306', '6c481e2a-17e5-4f07-92b9-832f5ae8d1d7');
INSERT INTO `HS_RoleMenuButton` VALUES ('3', '6c481e2a-17e5-4f07-92b9-832f5ae8d1d7');
INSERT INTO `HS_RoleMenuButton` VALUES ('10700', '6c481e2a-17e5-4f07-92b9-832f5ae8d1d7');
INSERT INTO `HS_RoleMenuButton` VALUES ('10800', '6c481e2a-17e5-4f07-92b9-832f5ae8d1d7');
INSERT INTO `HS_RoleMenuButton` VALUES ('7eade8fb-ada5-4b84-834a-fe191738a9e3', '6c481e2a-17e5-4f07-92b9-832f5ae8d1d7');
INSERT INTO `HS_RoleMenuButton` VALUES ('10300', '6c481e2a-17e5-4f07-92b9-832f5ae8d1d7');
INSERT INTO `HS_RoleMenuButton` VALUES ('a09d243a-e73c-11ea-a0a5-000c2900a492', '6c481e2a-17e5-4f07-92b9-832f5ae8d1d7');
INSERT INTO `HS_RoleMenuButton` VALUES ('0af16e0c-cfd3-48f5-bdfa-15a53a54cb36', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('11600', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('3858056b-9baf-44fc-8116-5fbf2a7cb5cf', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('11100', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('11101', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10900', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10902', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10903', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10904', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10905', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('11000', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('cbffef63-8f45-4817-9946-1e230f1d16c4', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('525a59b7-e794-4d25-9b5a-ac3926498fcc', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('c323f476-d17a-4fc3-9fdf-bdc49f879f97', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('40b6133c-f455-4fc0-9e80-e8cc9ea9fdee', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('636c9ae1-5848-4799-bd89-64a64988b678', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('56d24f8e-b552-4f23-905f-13a267de2e8c', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('8f34b263-6606-489d-a880-10493e3d2f79', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('a4b36b08-37c4-4a83-b69b-b4631967280c', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('7bb1e4c7-bf1d-4920-b885-a76b6e6b4835', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('11500', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10901', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('11200', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10803', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10000', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('5af2d7e5-5ce4-40d7-8a65-1196b1128873', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10400', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10401', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10500', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('a5b2a483-bfc4-4b99-a347-966f5f68920f', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('4f80bfab-ce60-4ab3-8b8a-6a3734af37c9', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('1198792f-ee87-4965-9366-a7cdc9e601cc', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('7ef6f59a-d4ef-4a51-9643-95fb120ed670', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10503', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10504', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10600', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10601', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10701', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('d47006fa-8d2a-4980-81c5-f2aa593253f2', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('6d5eef70-c72f-4a40-b154-fd88db5c0b39', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10800', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10801', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('63d60e7e-9c4c-4abf-a5c8-eef73844eabb', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('404e33f0-f715-4d8c-85a4-86d71ce0e063', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('11400', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('c465f4c6-192d-4c46-b7ad-e700fae0b542', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('fded6532-6724-4862-b155-7d9dd37a8502', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('93a2eaa7-ec05-4bc3-bcdf-6fd39c97e6d5', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('1a3fd295-4d1c-4097-95fd-21fac4b4e675', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('7eade8fb-ada5-4b84-834a-fe191738a9e3', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10300', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('56d2a887-0506-46b1-bd18-4629258000da', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10301', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10306', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('ba9c93d3-2a63-4c34-8de8-b64fa94432cf', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('689574a3-8e9a-41fb-9c94-fd17a2c28c8f', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10307', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10308', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10303', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10305', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10304', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('2', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('3', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('10700', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('1fed4f7e-9366-11ea-8eeb-000c2900a492', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('1fed680b-9366-11ea-8eeb-000c2900a492', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('1fed7f91-9366-11ea-8eeb-000c2900a492', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('1fed9672-9366-11ea-8eeb-000c2900a492', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('1fedaac1-9366-11ea-8eeb-000c2900a492', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('1fedbf68-9366-11ea-8eeb-000c2900a492', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('1fedd32f-9366-11ea-8eeb-000c2900a492', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('1fede73f-9366-11ea-8eeb-000c2900a492', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('1fedfc71-9366-11ea-8eeb-000c2900a492', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('a09d243a-e73c-11ea-a0a5-000c2900a492', 'admin');
INSERT INTO `HS_RoleMenuButton` VALUES ('1', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('10100', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('10102', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('10103', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('10101', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('10104', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('404e33f0-f715-4d8c-85a4-86d71ce0e063', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('11400', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('c465f4c6-192d-4c46-b7ad-e700fae0b542', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('fded6532-6724-4862-b155-7d9dd37a8502', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('93a2eaa7-ec05-4bc3-bcdf-6fd39c97e6d5', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('1a3fd295-4d1c-4097-95fd-21fac4b4e675', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('7eade8fb-ada5-4b84-834a-fe191738a9e3', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('10300', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('56d2a887-0506-46b1-bd18-4629258000da', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('10301', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('10306', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('ba9c93d3-2a63-4c34-8de8-b64fa94432cf', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('689574a3-8e9a-41fb-9c94-fd17a2c28c8f', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('10307', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('10308', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('10303', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('10305', 'master');
INSERT INTO `HS_RoleMenuButton` VALUES ('10304', 'master');

-- ----------------------------
-- 系统表（菜单需要设置所属系统）
-- ----------------------------
DROP TABLE IF EXISTS `HS_System`;
CREATE TABLE `HS_System`  (
                              `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                              `sys_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                              `note` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                              `icon` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                              `order` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- 系统默认的系统信息
-- ----------------------------
INSERT INTO `HS_System` VALUES ('0af16e0c-cfd3-48f5-bdfa-15a53a54cb36', '告警监测', '', 'fa fa-bell-o', '5');
INSERT INTO `HS_System` VALUES ('1', '系统管理', '', NULL, '1');
INSERT INTO `HS_System` VALUES ('2', '流量管理', '', 'fa fa-code-fork', '2');
INSERT INTO `HS_System` VALUES ('3', '日志系统', '', 'fa fa-list-alt', '1');
INSERT INTO `HS_System` VALUES ('404e33f0-f715-4d8c-85a4-86d71ce0e063', '数据可视化', NULL, 'fa fa-lightbulb-o', '4');
INSERT INTO `HS_System` VALUES ('7eade8fb-ada5-4b84-834a-fe191738a9e3', '资产管理', '', 'fa fa-laptop', '3');

-- ----------------------------
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS `HS_User`;
CREATE TABLE `HS_User`  (
                            `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '员工id',
                            `phone` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '手机号',
                            `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
                            `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '姓名',
                            `sex` int(1) NULL DEFAULT NULL COMMENT '性别(1男2女)',
                            `age` int(3) NULL DEFAULT NULL COMMENT '年龄',
                            `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电子邮箱',
                            `departmentId` int(8) NULL DEFAULT NULL COMMENT '部门表id',
                            `state` int(1) NULL DEFAULT NULL COMMENT '是否禁用（0禁用1未禁用）',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- 用户拒-角色 关系表
-- ----------------------------
DROP TABLE IF EXISTS `HS_UserRole`;
CREATE TABLE `HS_UserRole`  (
                                `FK_RoleID` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                `FK_UserID` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                PRIMARY KEY (`FK_RoleID`, `FK_UserID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;
-- ----------------------------
-- 用户操作日志表
-- ----------------------------
DROP TABLE IF EXISTS `note`;
CREATE TABLE `note`  (
                         `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '日志id',
                         `result` int(1) NULL DEFAULT NULL COMMENT '日知操作结果',
                         `describe` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '记录描述',
                         `time` datetime(0) NULL DEFAULT NULL COMMENT '日志生成时间',
                         `userId` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                         `departmentId` int(9) NULL DEFAULT NULL,
                         `account` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户账号',
                         `error` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '异常信息',
                         `ip` varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                         `state` int(1) NULL DEFAULT NULL COMMENT '状态'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;
-- ----------------------------
-- 安全策略表（未使用）
-- ----------------------------
DROP TABLE IF EXISTS `safe_strategy`;
CREATE TABLE `safe_strategy`  (
                                  `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                  `equipmentId` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '资产id',
                                  `event_type` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '事件类型',
                                  `number` int(2) NOT NULL COMMENT '次数',
                                  `time` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '时间（分钟）',
                                  `state` int(1) NOT NULL COMMENT '是否启用（0：未启用 1：启用）',
                                  `safe_strategy_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '安全策略名称',
                                  `time_interval` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- 系统默认的安全策略配置项
-- ----------------------------
INSERT INTO `safe_strategy` VALUES ('05cf7b345f974475b6e7e10ec56f1b90', '42a72bd0d6ca45dead5f3327ffdc5fd8', 'logged-in', 20, '120', 1, '登录次数过多', '00-00-2-00');
INSERT INTO `safe_strategy` VALUES ('2d71125f0fff426a81de0ae71001e3a4', '8f4f6a949e7d46aaa59f428ce1ca862a', 'sshd', 200, '1440', 1, 'ssh登录', '00-1-00-00');
INSERT INTO `safe_strategy` VALUES ('3d4a5456c57c4c408bb8db522ba79113', '8f4f6a949e7d46aaa59f428ce1ca862a', 'rsyslogd', 100, '43200', 1, 'rsyslogd', '1-00-00-00');
INSERT INTO `safe_strategy` VALUES ('64e0bd3e79f54b2881c306c1ea23e29d', '42a72bd0d6ca45dead5f3327ffdc5fd8', 'User Account Management', 1000, '2880', 1, 'User Account Management', '00-2-00-00');
INSERT INTO `safe_strategy` VALUES ('7fce4f6379b64c63835bc58b1d39b188', 'c3e6ed463f684448b0a50b0ccc17bb4e', 'sshd', 1, '60', 1, '登录情况', '00-00-1-00');
INSERT INTO `safe_strategy` VALUES ('84b2a59541504e018f3e5a2825a96bbc', '42a72bd0d6ca45dead5f3327ffdc5fd8', 'Other System Events', 500, '1440', 1, '其他系统事件', '00-1-00-00');
INSERT INTO `safe_strategy` VALUES ('9b7cca51f72a472ab548868a196782b0', '42a72bd0d6ca45dead5f3327ffdc5fd8', '身份验证策略更改', 3, '2880', 1, '身份验证策略更改', '00-2-00-00');
INSERT INTO `safe_strategy` VALUES ('aa9dde7ee289419fb3ee2be7b7183399', '8f4f6a949e7d46aaa59f428ce1ca862a', 'session', 200, '1440', 1, '开启会话', '00-1-00-00');
-- ----------------------------
-- 服务（url）信息表
-- ----------------------------
DROP TABLE IF EXISTS `serviceInfo`;
CREATE TABLE `serviceInfo`  (
                                `id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                `protocol` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                `ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                `port` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                `relativeUrl` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                `equipmentId` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                `complementState` int(1) NOT NULL DEFAULT 0,
                                `createTime` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                `upDateTime` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                `stopTime` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                `state` int(1) NOT NULL DEFAULT 0,
                                `describe` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                PRIMARY KEY (`id`) USING BTREE,
                                UNIQUE INDEX `serviceInfoUrl`(`url`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;
-- ----------------------------
-- 通过SNMP获取资产信息列表（未使用）
-- ----------------------------
DROP TABLE IF EXISTS `snmp_equipment_type`;
CREATE TABLE `snmp_equipment_type`  (
                                        `equipmentId` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                        `sysDesc` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统基本信息',
                                        `sysUptime` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '监控时间',
                                        `sysContact` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统联系人',
                                        `sysName` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '机器名',
                                        `IfNumber` varchar(3) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '网络接口的数目',
                                        `ssCpuUser` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户CPU百分比',
                                        `ssCpuSystem` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统CPU百分比',
                                        `ssCpuIdle` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '空闲CPU百分比',
                                        `hrMemorySize` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '内存大小',
                                        `type` int(1) NULL DEFAULT NULL,
                                        PRIMARY KEY (`equipmentId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;
-- ----------------------------
-- url列表（未使用）
-- ----------------------------
DROP TABLE IF EXISTS `url`;
CREATE TABLE `url`  (
                        `id` int(11) NOT NULL AUTO_INCREMENT,
                        `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'url',
                        `date` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '日期',
                        `equipmentId` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资产id',
                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 102 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Compact;

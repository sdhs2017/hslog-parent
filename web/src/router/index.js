import Vue from 'vue';
import Router from 'vue-router';

/**
 * 处理路由页面切换时，异步组件加载过渡的处理函数
 * @param  {Object} AsyncView 需要加载的组件，如 import('@/components/home/Home.vue')
 * @return {Object} 返回一个promise对象
 */
function lazyLoadView (AsyncView) {
    const AsyncHandler = () => ({
        // 需要加载的组件 (应该是一个 `Promise` 对象)
        component: AsyncView,
        // 异步组件加载时使用的组件
        loading: require('../components/common/RouteLoading.vue').default,
        // 加载失败时使用的组件
        error: require('../components/common/RouteError.vue').default,
        // 展示加载时组件的延时时间。默认值是 200 (毫秒)
        delay: 200,
        // 如果提供了超时时间且组件加载也超时了，
        // 则使用加载失败时使用的组件。默认值是：`Infinity`
        timeout: 10000
    });
    return Promise.resolve({
        functional: true,
        render (h, { data, children }) {
            return h(AsyncHandler, data, children);
        }
    });
}

Vue.use(Router);
/*let indexCompoents  = () =>  import('../components/index/index_n.vue');
console.log(indexCompoents)*/

export default new Router({
    routes: [
        {
            path: '/',
            redirect: '/login'
        },
        {
            path: '/',
            component: resolve => require(['../components/common/Home.vue'], resolve),
            meta: { title: '自述文件' },
            children:[
                {
                    path: '/index_n',
                    name:'index_n',
                    component: resolve => require(['../components/index/index_n.vue'], resolve),
                    // component: () => lazyLoadView(import('../components/index/index_n.vue')),
                    //component:indexCompoents,
                    meta: { title: '日志系统首页' }
                },
                {
                    path: '/flowIndex',
                    name:'flowIndex',
                     component: resolve => require(['../components/flowManage/flowIndex.vue'], resolve),
                    /*component: () => lazyLoadView(import('../components/flowManage/flowIndex.vue')),*/
                    meta: { title: '流量系统首页' }
                },
                {
                    path: '/userManage',
                    name:'userManage',
                    component: resolve => require(['../components/userManage/userManage.vue'], resolve),
                    meta: { title: '用户管理' }
                },
                {
                    path: '/roleManage',
                    name:'roleManage',
                    component: resolve => require(['../components/roleManage/roleManage.vue'], resolve),
                    meta: { title: '角色管理' }
                },
                {
                    path: '/roleExplain',
                    name:'roleExplain',
                    component: resolve => require(['../components/roleManage/roleExplain.vue'], resolve),
                    meta: { title: '角色说明' }
                },
                {
                    path: '/equipment',
                    name:'equipment',
                    component: resolve => require(['../components/equipment/equipment.vue'], resolve),
                    meta: { title: '资产列表' }
                },
                {
                    path: '/equipmentScan',
                    name:'equipmentScan',
                    component: resolve => require(['../components/equipment/equipmentScan.vue'], resolve),
                    meta: { title: '资产配置管理' }
                },
                {
                    path: '/addEquipment',
                    name:'addEquipment',
                    component: resolve => require(['../components/equipment/addEquipment.vue'], resolve),
                    meta: { title: '添加资产' }
                },
                {
                    path: '/reviseEquipment',
                    name:'reviseEquipment',
                    component: resolve => require(['../components/equipment/reviseEquipment.vue'], resolve),
                    meta: { title: '修改资产' }
                },
                {
                    path: '/updateEquipment',
                    name:'updateEquipment',
                    component: resolve => require(['../components/equipment/updateEquipment.vue'], resolve),
                    meta: { title: '更新资产' }
                },
                {
                    path: '/menuManage',
                    name:'menuManage',
                    component: resolve => require(['../components/menu/menuManage.vue'], resolve),
                    meta: { title: '菜单管理' }
                },
                {
                    path: '/equipmentLogs',
                    name:'equipmentLogs',
                    component: resolve => require(['../components/logsManage/equipmentLogs.vue'], resolve),
                    meta: { title: '设备日志' }
                },
                {
                    path: '/equipmentMonitor',
                    name:'equipmentMonitor',
                    component: resolve => require(['../components/equipment/equipmentMonitor.vue'], resolve),
                    meta: { title: '资产监控' }
                },
                {
                    path: '/equipment2',
                    name:'equipment2',
                    component: resolve => require(['../components/equipment/equipment2.vue'], resolve),
                    meta: { title: '资产概览' }
                },
                {
                    path: '/assetOverviewUser',
                    name:'assetOverviewUser',
                    component: resolve => require(['../components/equipment/assetOverviewUser.vue'], resolve),
                    meta: { title: '资产概览(用户)' }
                },
                {
                    path: '/equipment_group',
                    name:'equipment_group',
                    component: resolve => require(['../components/equipment/equipment_group.vue'], resolve),
                    meta: { title: '资产组' }
                },
                {
                    path: '/allEquipmentMonitor',
                    name:'allEquipmentMonitor',
                    component: resolve => require(['../components/equipment/allEquipmentMonitor.vue'], resolve),
                    meta: { title: '业务系统监控' }
                },
                {
                    path: '/serviceList',
                    name:'serviceList',
                    component: resolve => require(['../components/equipment/serviceList.vue'], resolve),
                    meta: { title: '服务列表' }
                },
                {
                    path: '/searchLogs',
                    name:'searchLogs',
                    component: resolve => require(['../components/logsManage/searchLogs.vue'], resolve),
                    meta: { title: '日志检索' }
                },
                {
                    path: '/accurateSearch',
                    name:'accurateSearch',
                    component: resolve => require(['../components/logsManage/accurateSearch.vue'], resolve),
                    meta: { title: '精确检索' }
                },
                {
                    path: '/rankingList',
                    name:'rankingList',
                    component: resolve => require(['../components/logsManage/rankingList.vue'], resolve),
                    meta: { title: '资产画像' }
                },
                {
                    path: '/urlIpRanking',
                    name:'urlIpRanking',
                    component: resolve => require(['../components/equipment/urlIpRanking.vue'], resolve),
                    meta: { title: '应用资产画像' }
                },
                {
                    path: '/rankingListDetail',
                    name:'rankingListDetail',
                    component: resolve => require(['../components/logsManage/rankingListDetail.vue'], resolve),
                    meta: { title: '排行' }
                },
                {
                    path: '/graph',
                    name:'graph',
                    component: resolve => require(['../components/logsManage/graph.vue'], resolve),
                    meta: { title: '关系图' }
                },
                {
                    path: '/funcGraph',
                    name:'funcGraph',
                    component: resolve => require(['../components/logsManage/funcGraph.vue'], resolve),
                    meta: { title: '功能业务流' }
                },
                {
                    path: '/urlGraph',
                    name:'urlGraph',
                    component: resolve => require(['../components/logsManage/urlGraph.vue'], resolve),
                    meta: { title: 'url业务流' }
                },
                {
                    path: '/funcRanking',
                    name:'funcRanking',
                    component: resolve => require(['../components/logsManage/funcGraph.vue'], resolve),
                    meta: { title: '功能排行' }
                },
                {
                    path: '/netflowLogs',
                    name:'netflowLogs',
                    component: resolve => require(['../components/logsManage/netflowLogs.vue'], resolve),
                    meta: { title: '日志' }
                },
                {
                    path: '/networkTopology',
                    name:'networkTopology',
                    component: resolve => require(['../components/logsManage/networkTopology.vue'], resolve),
                    meta: { title: '业务流分析' }
                },
                {
                    path: '/urlRanking',
                    name:'urlRanking',
                    component: resolve => require(['../components/logsManage/urlRanking.vue'], resolve),
                    meta: { title: '应用画像' }
                },
                {
                    path: '/dnsLogs',
                    name:'dnsLogs',
                    component: resolve => require(['../components/logsManage/dnsLogs.vue'], resolve),
                    meta: { title: 'DNS日志' }
                },
                {
                    path: '/dhcpLogs',
                    name:'dhcpLogs',
                    component: resolve => require(['../components/logsManage/dhcpLogs.vue'], resolve),
                    meta: { title: 'DHCP日志' }
                },
                {
                    path: '/httpLogs',
                    name:'httpLogs',
                    component: resolve => require(['../components/logsManage/httpLogs.vue'], resolve),
                    meta: { title: 'HTTP日志' }
                },
                {
                    path: '/flowLogs',
                    name:'flowLogs',
                    component: resolve => require(['../components/logsManage/flowLogs.vue'], resolve),
                    meta: { title: '流量日志' }
                },
                {
                    path: '/sourceFile',
                    name:'sourceFile',
                    component: resolve => require(['../components/sourceFile/sourceFile.vue'], resolve),
                    meta: { title: '数据源管理' }
                },
                {
                    path: '/uploadFile',
                    name:'uploadFile',
                    component: resolve => require(['../components/sourceFile/uploadFile.vue'], resolve),
                    meta: { title: '本地文件上传' }
                },
                {
                    path: '/monitoring',
                    name:'monitoring',
                    component: resolve => require(['../components/sourceFile/monitoring.vue'], resolve),
                    meta: { title: '在线监控' }
                },
                {
                    path: '/actionManage',
                    name:'actionManage',
                    component: resolve => require(['../components/actionManage/actionManage.vue'], resolve),
                    meta: { title: '动作列表' }
                },
                {
                    path: '/eventSearch',
                    name:'eventSearch',
                    component: resolve => require(['../components/eventManage/eventSearch.vue'], resolve),
                    meta: { title: '事件搜索' }
                },
                {
                    path: '/eventSearch_group',
                    name:'eventSearch_group',
                    component: resolve => require(['../components/eventManage/eventSearch_group.vue'], resolve),
                    meta: { title: '事件关联查询' }
                },
                {
                    path: '/eventList',
                    name:'eventList',
                    component: resolve => require(['../components/eventManage/eventList.vue'], resolve),
                    meta: { title: '事件列表' }
                },
                {
                    path: '/auditLog',
                    name:'auditLog',
                    component: resolve => require(['../components/platformManage/auditLog.vue'], resolve),
                    meta: { title: '审计日志' }
                },
                {
                    path: '/controlCenter',
                    name:'controlCenter',
                    component: resolve => require(['../components/platformManage/controlCenter.vue'], resolve),
                    meta: { title: '控制中心' }
                },
                {
                    path: '/controlCenter2',
                    name:'controlCenter2',
                    component: resolve => require(['../components/platformManage/controlCenter2.vue'], resolve),
                    meta: { title: '控制中心' }
                },
                {
                    path: '/flowServiceManage',
                    name:'flowServiceManage',
                    component: resolve => require(['../components/platformManage/flowServiceManage.vue'], resolve),
                    meta: { title: '流量控制中心' }
                },
                {
                    path: '/userAgentInfo',
                    name:'userAgentInfo',
                    component: resolve => require(['../components/flowManage/userAgentInfo.vue'], resolve),
                    meta: { title: 'User-Agent信息' }
                },
                {
                    path: '/realTimeFlow',
                    name:'realTimeFlow',
                    component: resolve => require(['../components/flowManage/realTimeFlow.vue'], resolve),
                    meta: { title: '全局实时流量' }
                },
                {
                    path: '/IPHostFlow',
                    name:'IPHostFlow',
                    component: resolve => require(['../components/flowManage/IPHostFlow.vue'], resolve),
                    meta: { title: 'IP主机流量' }
                },
                {
                    path: '/protocolFlow',
                    name:'protocolFlow',
                    component: resolve => require(['../components/flowManage/protocolFlow.vue'], resolve),
                    meta: { title: '协议流量' }
                },
                {
                    path: '/mulAndBro',
                    name:'mulAndBro',
                    component: resolve => require(['../components/flowManage/mulAndBro.vue'], resolve),
                    meta: { title: '广播包/组播包' }
                },
                {
                    path: '/packetType',
                    name:'packetType',
                    component: resolve => require(['../components/flowManage/packetType.vue'], resolve),
                    meta: { title: '数据包类型' }
                },
                {
                    path: '/equipmentFlow',
                    name:'equipmentFlow',
                    component: resolve => require(['../components/flowManage/equipmentFlow.vue'], resolve),
                    meta: { title: '资产流量' }
                },
                {
                    path: '/portFlow',
                    name:'portFlow',
                    component: resolve => require(['../components/flowManage/portFlow.vue'], resolve),
                    meta: { title: '全局端口流量' }
                },
                {
                    path: '/performanceAnalysis',
                    name:'performanceAnalysis',
                    component: resolve => require(['../components/flowManage/performanceAnalysis.vue'], resolve),
                    meta: { title: '应用性能分析' }
                },
                {
                    path: '/dashboard',
                    name:'dashboard',
                    component: resolve => require(['../components/dashboard/dashboard.vue'], resolve),
                    meta: { title: '新建仪表盘' }
                },
                {
                    path: '/chartsList',
                    name:'chartsList',
                    component: resolve => require(['../components/dashboard/chartsList.vue'], resolve),
                    meta: { title: '图表列表' }
                },
                {
                    path: '/barChart',
                    name:'barChart',
                    component: resolve => require(['../components/dashboard/barChart.vue'], resolve),
                    meta: { title: '创建柱状图' }
                },
                {
                    path: '/lineChart',
                    name:'lineChart',
                    component: resolve => require(['../components/dashboard/lineChart.vue'], resolve),
                    meta: { title: '创建折线图' }
                },
                {
                    path: '/pieChart',
                    name:'pieChart',
                    component: resolve => require(['../components/dashboard/pieChart.vue'], resolve),
                    meta: { title: '创建饼图' }
                },
                {
                    path: '/metricChart',
                    name:'metricChart',
                    component: resolve => require(['../components/dashboard/metricChart.vue'], resolve),
                    meta: { title: '创建指标' }
                },
                {
                    path: '/dynamicTable',
                    name:'dynamicTable',
                    component: resolve => require(['../components/dashboard/dynamicTable.vue'], resolve),
                    meta: { title: '创建表格' }
                },
                /*{
                    path: '/addEvent',
                    name:'addEvent',
                    component: resolve => require(['../components/eventManage/addEvent.vue'], resolve),
                    meta: { title: '添加事件' }
                },*/
                {
                    path: '/property',
                    name:'property',
                    component: resolve => require(['../components/dashboard/property.vue'], resolve),
                    meta: { title: '元数据' }
                },
                {
                    path: '/devTools',
                    name:'devTools',
                    component: resolve => require(['../components/dashboard/devTools.vue'], resolve),
                    meta: { title: '调试工具' }
                },
                {
                    path: '/fulltextRetrieval',
                    name:'fulltextRetrieval',
                    component: resolve => require(['../components/logsManage/fulltextRetrieval.vue'], resolve),
                    meta: { title: '全文检索' }
                },
                {
                    path: '/complexSearch',
                    name:'complexSearch',
                    component: resolve => require(['../components/logsManage/complexSearch.vue'], resolve),
                    meta: { title: '复合查询' }
                },
                {
                    path: '/accurateSearch2',
                    name:'accurateSearch2',
                    component: resolve => require(['../components/logsManage/accurateSearch2.vue'], resolve),
                    meta: { title: '精确查询' }
                },
                {
                    path: '/eventSearch2',
                    name:'eventSearch2',
                    component: resolve => require(['../components/eventManage/eventSearch2.vue'], resolve),
                    meta: { title: '事件检索' }
                },
                {
                    path: '/addEquipment2',
                    name:'addEquipment2',
                    component: resolve => require(['../components/equipment/addEquipment2.vue'], resolve),
                    meta: { title: '添加资产' }
                },
                {
                    path: '/addAssetOverviewUser',
                    name:'addAssetOverviewUser',
                    component: resolve => require(['../components/equipment/addAssetOverviewUser.vue'], resolve),
                    meta: { title: '添加资产' }
                },
                {
                    path: '/addEquipmentWithGroup',
                    name:'addEquipmentWithGroup',
                    component: resolve => require(['../components/equipment/addEquipmentWithGroup.vue'], resolve),
                    meta: { title: '添加资产组' }
                },
                {
                    path: '/dashboardList',
                    name:'dashboardList',
                    component: resolve => require(['../components/dashboard/dashboardList.vue'], resolve),
                    meta: { title: '仪表盘' }
                },
                {
                    path: '/controlCenter_n',
                    name:'controlCenter_n',
                    component: resolve => require(['../components/platformManage/controlCenter_n.vue'], resolve),
                    meta: { title: '控制中心' }
                },
                {
                    path: '/logicAssetsList',
                    name:'logicAssetsList',
                    component: resolve => require(['../components/equipment/logicAssetsList.vue'], resolve),
                    meta: { title: '逻辑资产管理' }
                },
                {
                    path: '/addLogicAssets',
                    name:'addLogicAssets',
                    component: resolve => require(['../components/equipment/addLogicAssets.vue'], resolve),
                    meta: { title: '添加逻辑资产' }
                },
                {
                    path: '/customChart2',
                    name:'customChart2',
                    component: resolve => require(['../components/common/customChart2.vue'], resolve),
                    meta: { title: 'SIEM(演示)' }
                },
                {
                    path: '/assetGroup',
                    name:'assetGroup',
                    component: resolve => require(['../components/assetGroup/assetGroup.vue'], resolve),
                    meta: { title: '资产组' }
                },
                {
                    path: '/addAssetGroup',
                    name:'addAssetGroup',
                    component: resolve => require(['../components/assetGroup/addAssetGroup.vue'], resolve),
                    meta: { title: '添加资产组' }
                },
                {
                    path: '/eventGroup',
                    name:'eventGroup',
                    component: resolve => require(['../components/eventGroup/eventGroup.vue'], resolve),
                    meta: { title: '事件组' }
                },
                {
                    path: '/addEventGroup',
                    name:'addEventGroup',
                    component: resolve => require(['../components/eventGroup/addEventGroup.vue'], resolve),
                    meta: { title: '添加事件组' }
                },
                {
                    path: '/alarmList',
                    name:'alarmList',
                    component: resolve => require(['../components/alarmManage/alarmList.vue'], resolve),
                    meta: { title: '告警列表' }
                },
                {
                    path: '/eventAlarmList',
                    name:'eventAlarmList',
                    component: resolve => require(['../components/alarmManage/eventAlarmList.vue'], resolve),
                    meta: { title: '事件告警' }
                },
                {
                    path: '/addAlarm',
                    name:'addAlarm',
                    component: resolve => require(['../components/alarmManage/addAlarm.vue'], resolve),
                    meta: { title: '添加告警' }
                },
                {
                    path: '/alert',
                    name:'alert',
                    component: resolve => require(['../components/alarmManage/alert.vue'], resolve),
                    meta: { title: '告警' }
                },
               {
                    path: '/alarmExecuteDetail',
                    name:'alarmExecuteDetail',
                    component: resolve => require(['../components/alarmManage/alarmExecuteDetail.vue'], resolve),
                    meta: { title: '告警执行详情' }
                },
                {
                    path: '/fileLogSearch',
                    name:'fileLogSearch',
                    component: resolve => require(['../components/logsManage/fileLogSearch.vue'], resolve),
                    meta: { title: '文件日志查询' }
                },
                {
                    path: '/fileLogManage',
                    name:'fileLogManage',
                    component: resolve => require(['../components/logsManage/fileLogManage.vue'], resolve),
                    meta: { title: '文件类日志' }
                },
                {
                    path: '/detectionAnomalies',
                    name:'study',
                    component: resolve => require(['../components/test/detectionAnomalies.vue'], resolve),
                    meta: { title: '异常检测' }
                },
                {
                    path: '/databaseLogSearch',
                    name:'databaseLogSearch',
                    component: resolve => require(['../components/logsManage/databaseLogSearch.vue'], resolve),
                    meta: { title: '数据库日志查询' }
                },
                {
                    path: 'dataSourceIndex',
                    name:'dataSourceIndex',
                    component: resolve => require(['../components/dataSource/dataSourceIndex.vue'], resolve),
                    meta: { title: '数据源管理' }
                },{
                    path: 'metadata',
                    name:'metadata',
                    component: resolve => require(['../components/dataSource/metadata.vue'], resolve),
                    meta: { title: '元数据管理' }
                },{
                    path: 'classification',
                    name:'classification',
                    component: resolve => require(['../components/dataSource/classification.vue'], resolve),
                    meta: { title: '分类分级管理' }
                },{
                    path: 'tabManage',
                    name:'tabManage',
                    component: resolve => require(['../components/dataSource/tabManage.vue'], resolve),
                    meta: { title: '发现规则配置' }
                },
                /* **********产品检测*************** */
                {
                    path: '/controlCenter_n_t',
                    name:'controlCenter_n_t',
                    component: resolve => require(['../components/platformManage/controlCenter_n_t.vue'], resolve),
                    meta: { title: '控制中心' }
                },
                {
                    path: '/eventSearch2_t',
                    name:'eventSearch2_t',
                    component: resolve => require(['../components/eventManage/eventSearch2_t.vue'], resolve),
                    meta: { title: '事件检索' }
                },
                /* **********产品检测*************** */

                /* **********涉密*************** */
                {
                    path: 'systemConfig_SR',
                    name:'systemConfig_SR',
                    component: resolve => require(['../components/platformManage/systemConfig_SR.vue'], resolve),
                    meta: { title: '系统配置' }
                },
                {
                    path: '/auditLog_SR',
                    name:'auditLog_SR',
                    component: resolve => require(['../components/platformManage/auditLog_SR.vue'], resolve),
                    meta: { title: '审计日志' }
                },
                {
                    path: '/userManage_SR',
                    name:'userManage_SR',
                    component: resolve => require(['../components/userManage/userManage_SR.vue'], resolve),
                    meta: { title: '用户管理' }
                },
                /* **********涉密*************** */
                /*{
                    path: '/test',
                    name:'test',
                    component: resolve => require(['../components/test/test.vue'], resolve),
                    meta: { title: 'test' }
                },*/
                /*{
                    path: '/panel',
                    name:'panel',
                    component: resolve => require(['../components/ef/panel.vue'], resolve),
                    meta: { title: 'panel' }
                },*/
               /* {
                    path: '/calendar',
                    name:'calendar',
                    component: resolve => require(['../components/test/calendar.vue'], resolve),
                    meta: { title: 'calendar' }
                },
                {
                    path: '/sunburst',
                    name:'sunburst',
                    component: resolve => require(['../components/test/sunburst.vue'], resolve),
                    meta: { title: 'sunburst' }
                },*/
               /* {
                    path: '/equipmentDashboardy_qMB3IBmkPMjFRE7O-_',
                    name:'equipmentDashboardy_qMB3IBmkPMjFRE7O-_',
                    component: resolve => require(['../components/dashboard/dashboard.vue'], resolve),
                    meta: { title: '编辑' }
                },*/
                /*--------------*/
                /*{
                    path: '/noAccessLog',
                    name:'noAccessLog',
                    component: resolve => require(['../components/test/noAccessLog.vue'], resolve),
                    meta: { title: '未接入日志' }
                },
                {
                    path: '/createREG',
                    name:'createREG',
                    component: resolve => require(['../components/test/createREG.vue'], resolve),
                    meta: { title: '日志' }
                },
                {
                    path: '/fullSearch',
                    name:'fullSearch',
                    component: resolve => require(['../components/test/fullSearch.vue'], resolve),
                    meta: { title: '日志' }
                },
                {
                    path: '/flow_n',
                    name:'flow_n',
                    component: resolve => require(['../components/test/flow_n.vue'], resolve),
                    meta: { title: '流量日志' }
                },
                {
                    path: '/el',
                    name:'el',
                    component: resolve => require(['../components/test/el.vue'], resolve),
                    meta: { title: '事件列表' }
                },
                {
                    path: '/device_graph',
                    name:'device_graph',
                    component: resolve => require(['../components/test/device_graph.vue'], resolve),
                    meta: { title: '资产拓扑' }
                },*/
                /*--------------*/
                /*{
                    // 富文本编辑器组件
                    path: '/editVersion',
                    component: resolve => require(['../components/versionManage/editVersion.vue'], resolve),
                    meta: { title: '富文本编辑器' }
                },*/
                /*
                {
                    path: '/table',
                    component: resolve => require(['../components/page/BaseTable.vue'], resolve),
                    meta: { title: '基础表格' }
                },
                {
                    path: '/tabs',
                    component: resolve => require(['../components/page/Tabs.vue'], resolve),
                    meta: { title: 'tab选项卡' }
                },
                {
                    path: '/form',
                    component: resolve => require(['../components/page/BaseForm.vue'], resolve),
                    meta: { title: '基本表单' }
                },
                {
                    // 富文本编辑器组件
                    path: '/editor',
                    component: resolve => require(['../components/page/VueEditor.vue'], resolve),
                    meta: { title: '富文本编辑器' }
                },
                {
                    // markdown组件
                    path: '/markdown',
                    component: resolve => require(['../components/page/Markdown.vue'], resolve),
                    meta: { title: 'markdown编辑器' }
                },
                {
                    // 图片上传组件
                    path: '/upload',
                    component: resolve => require(['../components/page/Upload.vue'], resolve),
                    meta: { title: '文件上传' }
                },
                {
                    // vue-schart组件
                    path: '/charts',
                    component: resolve => require(['../components/page/BaseCharts.vue'], resolve),
                    meta: { title: 'schart图表' }
                },
                {
                    // 拖拽列表组件
                    path: '/drag',
                    component: resolve => require(['../components/page/DragList.vue'], resolve),
                    meta: { title: '拖拽列表' }
                },
                {
                    // 权限页面
                    path: '/permission',
                    component: resolve => require(['../components/page/Permission.vue'], resolve),
                    meta: { title: '权限测试', permission: true }
                },*/
                {
                    path: '/404',
                    component: resolve => require(['../components/page/404.vue'], resolve),
                    meta: { title: '404' }
                },
                {
                    path: '/403',
                    component: resolve => require(['../components/page/403.vue'], resolve),
                    meta: { title: '403' }
                }
            ]
        },
        /*datasource*/
       /* {
            path:'/dataSource',
            component: resolve => require(['../components/dataSource/home'], resolve),
            children:[
                {
                    path: '/dataSource/index',
                    name:'dataSource',
                    component: resolve => require(['../components/dataSource/index.vue'], resolve)
                },{
                    path: '/dataSource/metadata',
                    name:'metadata',
                    component: resolve => require(['../components/dataSource/metadata.vue'], resolve)
                },{
                    path: '/dataSource/classification',
                    name:'classification',
                    component: resolve => require(['../components/dataSource/classification.vue'], resolve)
                },{
                    path: '/dataSource/tabManage',
                    name:'标签管理',
                    component: resolve => require(['../components/dataSource/tabManage.vue'], resolve)
                },]
        },*/
        {
            path:'/mobile',
            component: resolve => require(['../components/mobile/common/home'], resolve),
            children:[
                {
                    path: '/mobile/index_m',
                    name:'index_m',
                    component: resolve => require(['../components/mobile/index/index_m.vue'], resolve)
                },
                {
                    path: '/mobile/flowIndex_m',
                    name:'flowIndex_m',
                    component: resolve => require(['../components/mobile/index/flowIndex_m.vue'], resolve)
                },
                {
                    path: '/mobile/equipmentList',
                    name:'equipmentList',
                    component: resolve => require(['../components/mobile/equipment/equipmentList.vue'], resolve)
                },
                {
                    path: '/mobile/equipmentFlow_m',
                    name:'equipmentFlow_m',
                    component: resolve => require(['../components/mobile/flowManage/equipmentFlow_m.vue'], resolve)
                },
                {
                    path: '/mobile/IPHostFlow_m',
                    name:'IPHostFlow_m',
                    component: resolve => require(['../components/mobile/flowManage/IPHostFlow_m.vue'], resolve)
                },
                {
                    path: '/mobile/mulAndBro_m',
                    name:'mulAndBro_m',
                    component: resolve => require(['../components/mobile/flowManage/mulAndBro_m.vue'], resolve)
                },
                {
                    path: '/mobile/packetType_m',
                    name:'packetType_m',
                    component: resolve => require(['../components/mobile/flowManage/packetType_m.vue'], resolve)
                },
                {
                    path: '/mobile/portFlow_m',
                    name:'portFlow_m',
                    component: resolve => require(['../components/mobile/flowManage/portFlow_m.vue'], resolve)
                },
                {
                    path: '/mobile/protocolFlow_m',
                    name:'protocolFlow_m',
                    component: resolve => require(['../components/mobile/flowManage/protocolFlow_m.vue'], resolve)
                },
                {
                    path: '/mobile/realTimeFlow_m',
                    name:'realTimeFlow_m',
                    component: resolve => require(['../components/mobile/flowManage/realTimeFlow_m.vue'], resolve)
                },
                {
                    path: '/mobile/userAgentInfo_m',
                    name:'userAgentInfo_m',
                    component: resolve => require(['../components/mobile/flowManage/userAgentInfo_m.vue'], resolve)
                },
                {
                    path: '/mobile/performanceAnalysis_m',
                    name:'performanceAnalysis_m',
                    component: resolve => require(['../components/mobile/flowManage/performanceAnalysis_m.vue'], resolve)
                },
                {
                    path: '/mobile/dashboardList_m',
                    name:'dashboardList_m',
                    component: resolve => require(['../components/mobile/dashboard/dashboardList_m.vue'], resolve)
                },
                {
                    path: '/mobile/chartList_m',
                    name:'chartList_m',
                    component: resolve => require(['../components/mobile/dashboard/chartList_m.vue'], resolve)
                },
            ]
        },
        {
            path: '/login',
            component: resolve => require(['../components/login/Login.vue'], resolve)
        },
        {
            path: '/flowLogin',
            component: resolve => require(['../components/flowManage/flowLogin.vue'], resolve)
        },
        {
            path: '/login_m',
            component: resolve => require(['../components/mobile/login/login_m.vue'], resolve)
        },
        /*{
            path: '/bgCanvas',
            name:'bgCanvas',
            component: resolve => require(['../components/login/bgCanvas.vue'], resolve),
        },*/
        /*{
            path: '/resigter',
            component: resolve => require(['../components/resigter/resigter.vue'], resolve)
        },*/
        /*{
            path: '*',
            redirect: '/404'
        }*/
    ]
})

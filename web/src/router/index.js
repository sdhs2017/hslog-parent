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
                    path: '/index',
                    name:'index',
                   /* component: resolve => require(['../components/index/index.vue'], resolve),*/
                    component: () => lazyLoadView(import('../components/index/index.vue')),
                    meta: { title: '系统首页' }
                },
                {
                    path: '/flowIndex',
                    name:'flowIndex',
                    /* component: resolve => require(['../components/index/index.vue'], resolve),*/
                    component: () => lazyLoadView(import('../components/flowManage/flowIndex.vue')),
                    meta: { title: '系统首页' }
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
                    path: '/allEcharts',
                    name:'allEcharts',
                    component: resolve => require(['../components/equipment/allEcharts.vue'], resolve),
                    meta: { title: '全部资产报表' }
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
                    path: '/flowServiceManage',
                    name:'flowServiceManage',
                    component: resolve => require(['../components/platformManage/flowServiceManage.vue'], resolve),
                    meta: { title: '流量控制中心' }
                },
                {
                    path: '/versionDescribe',
                    name:'versionDescribe',
                    component: resolve => require(['../components/versionManage/versionDescribe.vue'], resolve),
                    meta: { title: '版本信息' }
                },
                {
                    path: '/test',
                    name:'test',
                    component: resolve => require(['../components/common/test.vue'], resolve),
                    meta: { title: '测试用例' }
                },
                {
                    path: '/icon',
                    component: resolve => require(['../components/page/Icon.vue'], resolve),
                    meta: { title: '自定义图标' }
                },
                {
                    path: '/flowEcharts',
                    name:'flowEcharts',
                    component: resolve => require(['../components/flowManage/flowEcharts.vue'], resolve),
                    meta: { title: '流量报表' }
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
                    meta: { title: '端口流量' }
                },
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
        {
            path: '/login',
            component: resolve => require(['../components/login/Login.vue'], resolve)
        },
        {
            path: '/flowLogin',
            component: resolve => require(['../components/flowManage/flowLogin.vue'], resolve)
        },
        {
            path: '/resigter',
            component: resolve => require(['../components/resigter/resigter.vue'], resolve)
        },
        {
            path: '*',
            redirect: '/404'
        }
    ]
})

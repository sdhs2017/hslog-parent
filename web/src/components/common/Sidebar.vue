<template>
    <div class="sidebar">
        <el-menu class="sidebar-el-menu" :default-active="onRoutes" :collapse="collapse" background-color="#1A242F"
            text-color="#bfcbd9" active-text-color="#20a0ff" >
            <template v-for="item in items">
                <template v-if="item.menus">
                    <el-submenu :index="item.id" :key="item.id">
                        <template slot="title">
                            <i :class="item.icon"></i><span slot="title">{{ item.menuName }}</span>
                        </template>
                        <template v-for="subItem in item.menus">
                            <el-submenu v-if="subItem.menus" :index="subItem.url" :key="subItem.url">
                                <template slot="title">{{ subItem.menuName }}</template>
                                <el-menu-item v-for="(threeItem,i) in subItem.menus" :key="i" :index="threeItem.url" >
                                    {{ threeItem.menuName }}
                                </el-menu-item>
                            </el-submenu>
                            <el-menu-item v-else :index="subItem.url" :key="subItem.url" @click.native="goToHtml(subItem)" :class="subItem.url === currentUrl ? 'currentClass' :''">
                                {{ subItem.menuName }}
                            </el-menu-item>
                        </template>
                    </el-submenu>
                </template>
                <!--<template v-else>router
                    <el-menu-item :index="item.index" :key="item.index">
                        <i :class="item.icon"></i><span slot="title">{{ item.title }}</span>
                    </el-menu-item>
                </template>-->
            </template>
        </el-menu>
    </div>
</template>

<script>
    import bus from '../common/bus';
    import {jumpHtml} from "../../../static/js/common";
    export default {
        data() {
            return {
                collapse: false,
                items: [
                    /*{
                        icon: 'el-icon-lx-home',
                        index: 'dashboard',
                        title: '系统首页'
                    },
                    {
                        icon: 'el-icon-lx-cascades',
                        index: 'table',
                        title: '基础表格'
                    },
                    {
                        icon: 'el-icon-lx-copy',
                        index: 'tabs',
                        title: 'tab选项卡'
                    },
                    {
                        icon: 'el-icon-lx-calendar',
                        index: '3',
                        title: '表单相关',
                        subs: [
                            {
                                index: 'form',
                                title: '基本表单'
                            },
                            {
                                index: '3-2',
                                title: '三级菜单',
                                subs: [
                                    {
                                        index: 'editor',
                                        title: '富文本编辑器'
                                    },
                                    {
                                        index: 'markdown',
                                        title: 'markdown编辑器'
                                    },
                                ]
                            },
                            {
                                index: 'upload',
                                title: '文件上传'
                            }
                        ]
                    },
                    {
                        icon: 'el-icon-lx-emoji',
                        index: 'icon',
                        title: '自定义图标'
                    },
                    {
                        icon: 'el-icon-lx-favor',
                        index: 'charts',
                        title: 'schart图表'
                    },
                    {
                        icon: 'el-icon-rank',
                        index: 'drag',
                        title: '拖拽列表'
                    },
                    {
                        icon: 'el-icon-lx-warn',
                        index: '6',
                        title: '错误处理',
                        subs: [
                            {
                                index: 'permission',
                                title: '权限测试'
                            },
                            {
                                index: '404',
                                title: '404页面'
                            }
                        ]
                    }*/
                ],
                systemId:1,
                currentUrl:'',
            }
        },
        computed:{
            onRoutes(){
                //  console.log(this.$route.path)
                return this.$route.path.replace('/','');
            }
        },
        created(){
            // 通过 Event Bus 进行组件间通信，来折叠侧边栏
            bus.$on('collapse', msg => {
                this.collapse = msg;
            })
            bus.$on('systemId',id =>{
                this.systemId = id;
            })
            this.getMenuData(this.systemId)
            // 获取菜单列表jzLog/menu/selectAll.do
            /*this.$nextTick(() => {
                this.$axios.get('../../../static/menu.json',{
                    params:{}
                })
                .then((response) => {
                    this.items = response.data;
                })
                .catch((err) => {
                    console.log(err);
                });
            })*/


        },
        watch:{
            'systemId'(newV){
                this.getMenuData(newV)
            }
        },
        methods:{
            /**/
            goToHtml(item){
                this.currentUrl = item.url
                //判断是否url 存在于路由表中  通过url特殊字符帕努单
                if(item.url.split('path=').length > 1){ //不存在 需要手动添加
                    let obj = JSON.parse(JSON.parse(item.url.split('path=')[1]));
                    let queryObj = {}
                    queryObj.name = item.menuName
                    for(let i in obj.query){
                        queryObj[i] = obj.query[i]
                    }
                    jumpHtml(obj.routeName+obj.query.id,obj.vuePath,queryObj,'');
                }else{ //存在
                    //第一次需要对特殊的url 进行处理  替换URl
                    if(item.url.indexOf('/') !== -1){
                        item.url =  item.url.split('/')[1].split('.')[0];
                        if(item.url === 'logIndex'){
                            item.url = 'index';
                        }
                        if(item.url === 'searchLogs2'){
                            item.url = 'accurateSearch';
                        }
                        if(item.url === 'serviceManage'){
                            item.url = 'controlCenter'
                        }
                        let us = item.url.replace('device','equipment');
                        item.url = us;
                        let us2 = item.url.replace('Device','Equipment');
                        item.url = us2;
                    }

                    this.$router.push(item.url)
                }
                //console.log(item)
                /*if(item.url.split('?').length > 1){
                    jumpHtml(item.url,'dashboard/dashboard.vue',{name:row.title,id:row.id,type:'see'},' 查看');
                }else{
                    this.$route.push(item.url)
                }*/
               /* let obj2 = {routeName:'seeDashboardkhcbOHoBW9kPociCJKTP',vuePath:'dashboard/dashboard.vue',query:{id:'khcbOHoBW9kPociCJKTP',type:'see'}};
                let obj = {
                    url:'',
                    path:'',

                }*/
            },
            /*获取菜单*/
            getMenuData(id){
                this.$nextTick(() => {
                    this.$axios.post(this.$baseUrl+'/menu/selectMenu.do',this.$qs.stringify({systemID:id}))
                        .then((res) => {
                            //
                           /* res.data.forEach(item =>{
                                //处理以前版本 的数据
                                item.id = item.id.toString();
                                item.menus.forEach(subItem =>{
                                    subItem.url =  subItem.url.split('/')[1].split('.')[0];
                                    if(subItem.url === 'logIndex'){
                                        subItem.url = 'index';
                                    }
                                    if(subItem.url === 'searchLogs2'){
                                        subItem.url = 'accurateSearch';
                                    }
                                    if(subItem.url === 'serviceManage'){
                                        subItem.url = 'controlCenter'
                                    }
                                    let us = subItem.url.replace('device','equipment');
                                    subItem.url = us;
                                    let us2 = subItem.url.replace('Device','Equipment');
                                    subItem.url = us2;

                                })
                            })*/
                            this.items = res.data;
                            //console.log(this.items)
                        })
                        .catch((err) => {
                            console.log(err);
                        });
                })
            }
        }
    }
</script>

<style scoped>
    .sidebar{
        display: block;
        position: absolute;
        left: 0;
        top: 50px;
        bottom:0;
        overflow-y: scroll;
        color: #D6DFEB;
    }
    .sidebar::-webkit-scrollbar{
        width: 0;
    }
    .sidebar-el-menu:not(.el-menu--collapse){
        width: 170px;
    }
    .el-menu--collapse{
        width: 65px;
    }
    .sidebar > ul {
        height:100%;
        /*padding-bottom: 100px;*/
    }
    .el-menu-item, .el-submenu__title{
        height: 40px!important;
        line-height: 40px!important;
    }
    .el-submenu>div{
        height: 46px!important;
        line-height: 46px!important;
    }
    .el-menu-item{
        padding-left: 55px!important;
    }
    .el-submenu__title i.fa{
        display: inline-block;
        color: rgb(191, 203, 217);
        margin-right: 6px;
        width: 20px;
    }
    .is-opened{
        background: #fff;
    }
    .currentClass{
        color: rgb(32, 160, 255) !important;
        background-color: rgb(26, 36, 47) !important;
    }
</style>

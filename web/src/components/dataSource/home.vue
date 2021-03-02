<template>
    <div>
        <div class="header-wapper">
            <img src="../../../static/img/login_cx.png" alt="">
        </div>
        <div class="con-wapper">
            <div class="menu-wapper">
                <el-menu class="sidebar-el-menu" :default-active="onRoutes" background-color="#1A242F"
                         text-color="#bfcbd9" active-text-color="#20a0ff" router>
                    <template v-for="(item,i) in menusArr">
                        <el-submenu :index="item.id" :key="item.id">
                            <template slot="title">
                                <i :class="item.icon"></i><span slot="title">{{ item.menuName }}</span>
                            </template>
                            <template v-for="subItem in item.menus">
                               <!-- <el-submenu v-if="subItem.menus" :index="subItem.url.split('/')[1].split('.')[0]" :key="subItem.url">
                                    <template slot="title">{{ subItem.menuName }}</template>
                                    <el-menu-item v-for="(threeItem,i) in subItem.menus" :key="i" :index="threeItem.url">
                                        {{ threeItem.menuName }}
                                    </el-menu-item>
                                </el-submenu>-->
                                <el-menu-item :index="subItem.url.split('/')[1].split('.')[0]" :key="subItem.url.split('/')[1].split('.')[0]">
                                    {{ subItem.menuName }}
                                </el-menu-item>
                            </template>
                        </el-submenu>
                    </template>
                </el-menu>
            </div>
            <div class="view-wapper">
                <router-view></router-view>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: "home",
        data() {
            return {
                menusArr: [{
                    "childId": 3,
                    "icon": "fa fa-database",
                    "id": "10300",
                    "menuName": "数据源管理",
                    "menus": [{
                        "childId": 0,
                        "icon": "",
                        "id": "10306",
                        "menuName": "数据源管理",
                        "state": "1",
                        "superiorId": "10300",
                        "systemName": "数据源管理",
                        "url": "dataSource/index.vue"
                    },{
                        "childId": 1,
                        "icon": "",
                        "id": "103061",
                        "menuName": "元数据管理",
                        "state": "1",
                        "superiorId": "10300",
                        "systemName": "数据源管理",
                        "url": "dataSource/metadata.vue"
                    }]
                }]
            }
        },
        computed:{
            onRoutes(){
                console.log(this.$route.path.split('/'))
                return this.$route.path.split('/')[2]
            }
        },
    }
</script>

<style scoped>
    .header-wapper{
        height: 50px;
        background: rgb(25,28,40);
    }
    .header-wapper img{
        width: 238px;
    }
    .con-wapper{
        display: flex;
        height: calc(100vh - 50px);
        background: rgb(12,15,26);
    }
    .menu-wapper{
        width: 170px;
        height: 100%;
        background-color: rgb(26, 36, 47);
        overflow-x: hidden;
    }
    .sidebar-el-menu:not(.el-menu--collapse){
        width: 170px;
    }
    .el-menu--collapse{
        width: 65px;
    }
    .view-wapper{
        padding: 8px;
        /*flex: 1;*/
        width: calc(100% - 170px);
        height: 100%;
    }
    .view-wapper div{
        height: 100%;
        overflow-y: auto;
        background: rgb(26,36,47);
    }
    .el-submenu__title i.fa{
        display: inline-block;
        color: rgb(191, 203, 217);
        margin-right: 6px;
        width: 20px;
    }
</style>

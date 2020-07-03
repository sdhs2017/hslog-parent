<template>
    <div class="content-bg1 mobile-wapper">
        <div class="header">
            <i class="title-user el-icon-user-solid" @click="userClick"></i>
            <!--  <img src="../../../static/img/login_cx.png" alt="">-->
            <span>{{systemTitle}}</span>
            <i class="title-menu el-icon-menu" @click="menuClick"></i>
        </div>
        <!--菜单页-->
        <div :style="{top:this.menuPosition}" class="slider-menu">
            <div class="menu-wapper">
                <div class="inf-wapper"></div>
                <ul class="menu-ul">
                    <li data-link="/mobile/index_m" @click="menuLiClick($event)">
                        <span>--</span>
                        <b>日志系统</b>
                        <span>--</span>
                    </li>
                    <li data-link="/mobile/flowIndex_m" @click="menuLiClick($event)">
                        <span>--</span>
                        <b>流量系统</b>
                        <span>--</span>
                    </li>
                </ul>
            </div>
            <i class="el-icon-close" @click="sliderClose"></i>
        </div>
        <!--用户页-->
        <div :style="{top:this.userPosition}" class="slider-user" v-loading.fullscreen.lock="loginOutLoading" element-loading-text="登陆中"  element-loading-background="rgba(0, 0, 0, 0.8)">
            <div class="user-top">
                <div class="user-img">
                    <img src="../../../../static/img/1.jpg" alt="">
                </div>
                <div class="user-inf">
                    <div>
                        <p>{{this.userInf.name}}</p>
                        <p>{{this.userInf.phone}}</p>
                    </div>
                </div>
            </div>
            <ul class="user-ul">
                <!--<li>{{this.userInf.roleName}}</li>-->
                <li>
                    <span>角色：</span>
                    <span style="color: #e4956d;">{{this.userRole}}</span>
                </li>
                <li>
                    <span>邮箱：</span>
                    <span>{{this.userInf.email}}</span>
                </li>
            </ul>
            <div class="login-out" @click="loginOut">退出登录</div>
            <i class="el-icon-close" @click="sliderClose"></i>
        </div>
        <!--主视图-->
        <div class="router-wapper">
            <!--<router-view></router-view>-->
            <transition name="move" mode="out-in">
                <keep-alive >
                    <router-view></router-view>
                </keep-alive>
            </transition>
        </div>
        <!--底部菜单-->
        <div class="bottom-menu" v-if="systemName === 'logSystem'">
            <ul class="b-ul log-menu-ul">
                <li class="b-li"><div data-link="/mobile/index_m" @click="menuLiClick($event)">日志首页</div></li>
                <!--<li class="b-sys-name" @click="menuClick">日志</li>-->
                <li class="b-li">
                    <el-dropdown trigger="click" placement="top">
              <span class="el-dropdown-link more-list" >
                统计报表<i class="el-icon-arrow-down el-icon--right"></i>
              </span>
                        <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item><div data-link="/mobile/equipmentList" @click="menuLiClick($event)">资产列表</div></el-dropdown-item>
                            <!--<el-dropdown-item><div data-link="/allEquipmentCharts" @click="menuLiClick($event)">日志汇总报表</div></el-dropdown-item>-->
                        </el-dropdown-menu>
                    </el-dropdown>
                </li>
            </ul>
        </div>
        <div class="bottom-menu" v-else>
            <ul class="b-ul">
                <li class="b-li"><div data-link="/mobile/flowIndex_m" @click="menuLiClick($event)">流量首页</div></li>
                <!--<li class="b-sys-name" @click="menuClick">流量</li>-->
                <li class="b-li">
                    <el-dropdown trigger="click" placement="top">
                  <span class="el-dropdown-link more-list">
                    流量统计<i class="el-icon-arrow-down el-icon--right"></i>
                  </span>
                        <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item><div data-link="/mobile/userAgentInfo_m" @click="menuLiClick($event)">User-Agent信息</div></el-dropdown-item>
                            <el-dropdown-item><div data-link="/mobile/realTimeFlow_m" @click="menuLiClick($event)">全局实时流量</div></el-dropdown-item>
                            <el-dropdown-item><div data-link="/mobile/IPHostFlow_m" @click="menuLiClick($event)">IP主机流量</div></el-dropdown-item>
                            <el-dropdown-item><div data-link="/mobile/protocolFlow_m" @click="menuLiClick($event)">协议流量</div></el-dropdown-item>
                            <el-dropdown-item><div data-link="/mobile/mulAndBro_m" @click="menuLiClick($event)">广播包/组播包</div></el-dropdown-item>
                            <el-dropdown-item><div data-link="/mobile/packetType_m" @click="menuLiClick($event)">数据包类型</div></el-dropdown-item>
                            <el-dropdown-item><div data-link="/mobile/equipmentFlow_m" @click="menuLiClick($event)">资产流量</div></el-dropdown-item>
                            <el-dropdown-item><div data-link="/mobile/portFlow_m" @click="menuLiClick($event)">端口流量</div></el-dropdown-item>
                            <el-dropdown-item><div data-link="/mobile/performanceAnalysis_m" @click="menuLiClick($event)">应用性能分析</div></el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                </li>
                <!--<li class="b-li">
                        <el-dropdown trigger="click" placement="top">
                  <span class="el-dropdown-link more-list">
                    流量统计<i class="el-icon-arrow-down el-icon&#45;&#45;right"></i>
                  </span>
                            <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item><div data-link="/userAgentInfo2" @click="menuLiClick($event)">User-Agent信息</div></el-dropdown-item>
                                <el-dropdown-item><div data-link="/IPHostFlow2" @click="menuLiClick($event)">IP主机流量</div></el-dropdown-item>
                                <el-dropdown-item><div data-link="/protocolFlow2" @click="menuLiClick($event)">协议流量</div></el-dropdown-item>
                                <el-dropdown-item><div data-link="/mulAndBro2" @click="menuLiClick($event)">广播包/组播包</div></el-dropdown-item>
                                <el-dropdown-item><div data-link="/packetType2" @click="menuLiClick($event)">数据包类型</div></el-dropdown-item>
                                <el-dropdown-item><div data-link="/equipmentFlow2" @click="menuLiClick($event)">资产流量</div></el-dropdown-item>
                                <el-dropdown-item><div data-link="/portFlow2" @click="menuLiClick($event)">端口流量</div></el-dropdown-item>
                                <el-dropdown-item><div data-link="/performanceAnalysis" @click="menuLiClick($event)">应用性能分析</div></el-dropdown-item>
                            </el-dropdown-menu>
                        </el-dropdown>
                </li>-->
            </ul>
        </div>
    </div>

</template>

<script>
    export default {
        name: "home",
        data() {
            return {
                // systemTitle:'宸析日志分析系统',
                systemTitle:'安全运维数据平台',
                systemName:'logSystem',
                userRole:'',
                // userInf:{},//用户信息
                loginOutLoading:false,
                menuPosition:'-100vh',//菜单初始位置
                userPosition:'-100vh', //用户页初始位置
                activedLink:'' //当前正在访问的页面
            }
        },
        computed:{
            //用户信息
            userInf(){
                let user = {};
                user = JSON.parse(localStorage.getItem('LoginUser'));
                if(user !== null){
                    return user;
                }else {
                    return {
                        name:'',
                        phone:'',
                        email:''
                    }
                }

            }
        },
        // 使用watch 监听$router的变化,
        watch: {
            '$route': function(to,from){
                document.body.scrollTop = 50
                document.documentElement.scrollTop = 50
                setTimeout(()=>{
                    document.body.scrollTop = 0
                    document.documentElement.scrollTop = 0
                },1000)
            }
        },
        created(){
            //获得存放在本地的用户信息
            //this.userInf = JSON.parse(sessionStorage.getItem('LoginUser'));
            this.getUserImformation();
        },
        methods:{
            /*获取登录角色信息*/
            getUserImformation(){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/user/selectUserRole.do','')
                        .then(res =>{
                            this.userRole =res.data.message;
                        })
                        .catch(res =>{

                        })
                })
            },
            /*菜单按钮点击*/
            menuClick(){
                this.menuPosition = '0';
            },
            /*菜单列表点击*/
            menuLiClick(e){
                //获取点击的地址
                let linkVal = $(e.currentTarget).attr('data-link');
                //关闭菜单
                this.menuPosition = '-100vh';
                if(linkVal === '/mobile/index_m'){
                    this.systemName='logSystem';
                    //this.systemTitle = '宸析日志分析系统'
                }else if (linkVal === '/mobile/flowIndex_m'){
                    this.systemName='flowSystem';
                    // this.systemTitle = '流量分析系统'
                }
                //跳转
                this.$router.push(linkVal);
            },
            /*用户按钮点击*/
            userClick(){
                this.userPosition = '0';
            },
            /*侧边栏关闭*/
            sliderClose(){
                this.menuPosition = '-100vh';
                this.userPosition = '-100vh';
            },
            /*用户登出*/
            loginOut(){
                this.loginOutLoading = true;
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/user/logout.do',{})
                        .then(res =>{
                            this.loginOutLoading = false;
                            if(res.data.success === 'true'){
                                this.$message.success('登出成功')
                                localStorage.removeItem('LoginUser');
                                setTimeout(()=>{this.$router.push('/login_m')},1000)
                            }else{
                                this.$message.error(res.data.message)
                            }
                        })
                        .catch(err =>{
                            this.loginOutLoading = false;
                            this.$message.error('登出失败')
                        })
                })
            }
        },
        beforeRouteEnter(to, from, next){
            next(vm =>{
                if(to.path === '/mobile/index_m'){
                    vm.systemName='logSystem';
                    // vm.systemTitle = '宸析日志分析系统'
                }else if (to.path === '/mobile/flowIndex_m' || to.path === '/mobile/equipmentFlow_m' || to.path === '/mobile/IPHostFlow_m' || to.path === '/mobile/mulAndBro_m' || to.path === '/mobile/packetType_m' || to.path === '/mobile/portFlow_m' || to.path === '/mobile/protocolFlow_m' || to.path === '/mobile/realTimeFlow_m' || to.path === '/mobile/userAgentInfo_m' || to.path === '/mobile/userAgentInfo2' || to.path === '/mobile/IPHostFlow2' || to.path === '/mobile/protocolFlow2' || to.path === '/mobile/mulAndBro2'|| to.path === '/mobile/packetType2' || to.path === '/mobile/equipmentFlow2' || to.path ==='/mobile/portFlow2' || to.path === '/mobile/performanceAnalysis_m'|| to.path === '/mobile/performanceAnalysisUrl_m'){
                    vm.systemName='flowSystem';
                    // vm.systemTitle = '流量分析系统'
                }
            });
        }
    }
</script>

<style scoped>
    /*底部菜单*/
    .bottom-menu{
        width: 100%;
        height: 50px;
        background: rgba(17, 44, 130, 0.8);
        transition: all 0.3s linear;
        border-top: 1px solid #195bff;
        position: fixed;
        bottom: 0;
        font-size: 14px;
        /*font-weight: 600;*/
    }
    .b-ul{
        height: 50px;
        display: flex;
        justify-content: space-between;
        position: relative;
        box-shadow: 0px 0px 11px #2f8acd;
    }
    .b-ul .b-li{
        width: 48%;
        height: 50px;
        line-height: 50px;
        text-align: center;
        font-weight: 600;
        border-right: 1px solid #203da0;
    }
    .log-menu-ul .b-li{
        width: 50%;
    }
    .more-list{
        display: inline-block;
        width: 100%;
        height: 100%;
    }
    .b-ul .b-sys-name{
        width: 60px;
        /* height: 60px; */
        /* line-height: 60px; */
        /* border-radius: 100%; */
        font-size: 12px;
        border: 1px solid #195bff;
        position: absolute;
        left: 50%;
        margin-left: -30px;
        top: -20px;
        background: rgba(17, 44, 130, 1);
        font-size: 13px;
        padding: 3px 0;
        font-weight: 600;
        color: #e4956d;
        -webkit-box-shadow: 0px 0px 11px #2f8acd;
        box-shadow: 0px 0px 11px #2f8acd;
    }
    .content-bg1{
        /* height: 100%;
         background: url("../../../static/img/wrapper-bg.png");
         background-size: 100% 100%;*/
        background: #0b0f34;
    }


    /*头部*/
    .header{
        height: 70px;
        line-height: 60px;
        font-size: 1.4rem;
        color: #e4956d;
        background: url('../../../../static/img/tt.png') no-repeat;
        /* background: rgb(25,28,40); */
        background-position: bottom;
        position: relative;
        text-align: center;
    }
    .header img{
        width: 60%;
        position: absolute;
        left: 50%;
        margin-left: -30%;
    }
    .header i{
        border: 2px solid #185bff;
        padding: 0.08rem;
        font-size: 1.2rem;
        color: #185bff;
        top: 5px;
        background: #6cc6f3;
        border-radius: 100%;
        box-shadow: 0px 0px 15px #2f8acd;
    }
    .title-user{
        position: absolute;
        top: 10px;
        left: 10px;
    }
    .title-menu{
        position: absolute;
        top: 10px;
        right: 10px;
    }
    /*侧边栏*/
    .slider-user,.slider-menu{
        width: 100vw;
        height: 100vh;
        font-size:0.4rem;
        position: fixed;
        transition: all 0.25s linear;
        background: #000;
        z-index: 99999999;
        background: rgba(11,15,52,0.9);
    }
    .el-icon-close{
        position: absolute;
        bottom: 15%;
        font-size: 1.2rem;
        border: 2px solid rgb(228, 149, 109);
        border-radius: 100%;
        padding: 0.05rem;
        left: 50%;
        color: rgb(228, 149, 109);
        margin-left: -0.4rem;
    }
    /*用户*/
    .user-top{
        height: 30vh;
        background: url(../../../../static/img/panel-l-t.png);
        background-size: 100% 100%;
        display: flex;
        background: rgba(24, 91, 255,0.3);
    }
    .user-img{
        width: 40%;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    .user-img img{
        width: 68%;
        border-radius: 100%;
        border: 3px solid #5bc0de;
    }
    .user-inf{
        flex:1;
        display: flex;
        align-items: center;
        padding-left: 5%;
    }
    .user-inf p{
        width: 100%;
        font-size: 1.3rem;
        text-align: start;
        color: #5bc0de;
        margin: 1rem 0;
    }
    .user-ul{
        list-style: none;
        padding: 3px;
    }
    .user-ul li{
        height: 2.5rem;
        line-height: 2.5rem;
        /*background: #0E2268;*/
        background: url(../../../../static/img/title-bg.png);
        background-size: 100% 100%;
        margin: 15px 0;
        display: flex;
        justify-content: space-between;
        padding: 0 7%;
        font-size: 0.8rem;
    }
    .login-out{
        width: 98%;
        height: 2.5rem;
        line-height: 2.5rem;
        text-align: center;
        background: rgba(57,49,161,0.8);
        color: #eee;
        font-size: 0.9rem;
        margin: 0 auto;
    }
    .router-wapper{
        height: calc(100% - 120px);
        padding-bottom: 50px;
        background: #0b0f34;
        overflow: auto;
    }
    /*菜单*/
    ul,li{
        list-style: none;
        padding: 0;
    }
    .inf-wapper{
        height: 80px;
    }

    .menu-ul li{
        height: 3.5rem;
        line-height: 3.5rem;
        font-weight: 600;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    .menu-ul li span{
        margin: 0 3px;
        font-size: 1.2rem;
    }
    .menu-ul li b{
        margin: 0 50px;
        font-size: 1.2rem;
    }
    /deep/ .el-dropdown{
        color: #a3b9ec;
    }
</style>

<template>
    <div class="header" v-loading="loading" element-loading-spinner="el-icon-loading" element-loading-background="rgba(48, 62, 78, 0.2)">
        <!-- 折叠按钮 -->
        <div class="collapse-btn" @click="collapseChage">
            <i class="el-icon-menu"></i>
        </div>
        <div class="logo">
            <img src="../../../static/img/login_cx.png" alt="">
<!--            <img src="../../../static/img/logo_ay.png" alt="">-->
            <span style="position: absolute;top: -7px;left: 320px;font-size: 10px;">V 3.0</span>
<!--            <img src="../../../static/img/logo_ay.png" alt="">-->
        </div>
        <ul class="header-ul">
            <li v-for="(item,index) in this.systemMenu" :class="{ active:index === current}" :key="index" @click="changeSystem(item.id,index)"><i :class="item.icon"></i>{{item.systemName}}</li>
        </ul>
        <div class="header-right">
            <div class="header-user-con">
                <!-- 消息中心 -->
                <div class="btn-bell">
                    <el-popover
                        placement="bottom"
                        width="200"
                        trigger="click">
                        <ul class="bell-ul" v-if="bellArr.length">
                            <li class="bell-li" @click="goToThreat(item)" v-for="(item,index) in bellArr" :key="index" title="点击查看具体信息">
                                <p class="top-p"><b class="name-b">{{item.name}} : </b></p>
                                <p class="bottom-p">
                                    <span v-if="item.high_risk !== 0">高危事件数 <b class="high-b">{{item.high_risk}}</b></span>
                                    <span  v-if="item.moderate_risk !== 0">    中危事件数 <b class="center-b">{{item.moderate_risk}}</b></span>
                                </p>
                            </li>
                        </ul>
                        <ul class="bell-ul" v-else>
                            <li style="text-align: center;">暂无告警事件</li>
                        </ul>
                        <el-button slot="reference" class="bell-wapper">
                            <el-badge :value="bellArr.length" :hidden="bellArr.length === 0" :max="99" class="item">
                                <i class="el-icon-bell"></i>
                            </el-badge>
                        </el-button>
                    </el-popover>
                </div>

                <!-- 用户头像 -->
                <!--<div class="user-avator"><img src="static/img/img.jpg"></div>-->
                <!-- 用户名下拉菜单 -->
                <el-dropdown class="user-name" trigger="click" @command="handleCommand">
                    <span class="el-dropdown-link">
                        {{roleName}} - {{phone}} <i class="el-icon-caret-bottom"></i>
                    </span>
                    <el-dropdown-menu slot="dropdown" class="drop-menu">
                        <el-dropdown-item command="showPasswordForm">修改密码</el-dropdown-item>
                        <el-dropdown-item command="loginout">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
                <!-- 全屏显示 -->
                <div class="btn-fullscreen" @click="handleFullScreen">
                    <el-tooltip effect="dark" :content="fullscreen?`取消全屏`:`全屏`" placement="bottom">
                        <i class="el-icon-rank"></i>
                    </el-tooltip>
                </div>
            </div>
        </div>
        <el-dialog title="修改密码" :visible.sync="passWordForm" width="400px">
            <el-form label-width="100px">
                <el-form-item label="旧的密码:">
                    <el-input v-model="passwordObj.oldPassword" type="password" placeholder="6-18位数字、字母、字符"  maxlength="18"  class="item"></el-input>
                </el-form-item>
                <el-form-item label="新的密码:">
                    <el-input v-model="passwordObj.password" type="password" placeholder="6-18位数字、字母、字符"  maxlength="18"  class="item"></el-input>
                </el-form-item>
                <ul class="pass_set" v-if="passwordObj.password.length > 0">
                    <li id="strength_L" style="background-color: rgb(237, 62, 13);">弱</li>
                    <li id="strength_M" :style="mode >=2 ? {background:'#ffaf56'}:{}">中</li>
                    <li id="strength_H" :style="mode > 2 ? {background:'#6ba001'}:{}">强</li>
                </ul>
                <el-form-item label="确认密码:">
                    <el-input v-model="password2" type="password" placeholder="6-18位数字、字母、字符"  maxlength="18"  class="item"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="changePassWord">确 定</el-button>
                <el-button @click="passWordForm = false">取 消</el-button>
            </div>
        </el-dialog>
    </div>
</template>
<script>
    import bus from '../common/bus';
    import {checkStrong,jumpHtml} from  '../../../static/js/common.js'
    export default {
        data() {
            return {
                bellArr:[],
                loading:false,
                collapse: false,
                fullscreen: false,
                passWordForm:false,
                message: 2,
                phone:'',
                roleName:'',
                passwordObj:{
                    oldPassword:'',
                    password:''
                },
                password2:'',
                mode:0,
                systemMenu:[],
                current:0
            }
        },
        watch:{
            'passwordObj.password'(val){
                this.mode =  checkStrong(val);
            }
        },
        computed:{
            username(){
                let username = localStorage.getItem('ms_username');
                return username ? username : this.name;
            }
        },
        created(){
            //获取系统菜单
            this.getSystem();
            //获取用户信息
            this.getUserImformation();
            //获取告警数
            this.getBellData();
            //每五分钟请求一次数据
            setInterval(()=>{
                this.getBellData()
            },300000)
            let user =  JSON.parse(localStorage.getItem('LoginUser'));
            this.phone = user.phone;
        },
        methods:{
            /*获取登录角色信息*/
            getUserImformation(){
                this.$nextTick(()=>{
                    this.$axios.get(this.$baseUrl+'/user/selectUserRole.do','')
                        .then(res =>{
                            let obj =res.data;
                            if(obj.success == 'true'){
                                this.roleName = obj.message;
                            }else{
                                this.roleName = obj.message;
                            }
                        })
                        .catch(res =>{

                        })
                })
            },
            /*改变密码*/
            changePassWord(){
                if(this.passwordObj.oldPassword === ''){
                    layer.msg('请填写原来密码',{icon: 5});
                }else if(this.passwordObj.password === ''){
                    layer.msg('未填写新的密码',{icon: 5});
                }else if(this.passwordObj.password .length < 6 || this.passwordObj.password .length>18){
                    layer.msg('密码长度不正确(6-18位)',{icon: 5});
                }else if(this.passwordObj.password  !== this.password2 ){
                    layer.msg('两次密码输入不一致',{icon: 5});
                }else{
                    layer.load(1)
                    let userObj = JSON.parse(localStorage.getItem("LoginUser"));
                    this.passwordObj.id = userObj.id;
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/user/updatePasswordById.do',this.$qs.stringify(this.passwordObj))
                            .then(res =>{
                                layer.closeAll();
                                if(res.data.success === "true"){
                                    layer.msg(res.data.message,{icon: 1});
                                    //关闭弹窗
                                    this.passWordForm =false;
                                }else if(res.data.success === "false"){
                                    layer.msg(res.data.message,{icon: 5});
                                }
                            })
                            .catch(res =>{

                            })
                    })
                }
            },
            // 用户名下拉菜单选择事件
            handleCommand(command) {
                if(command === 'loginout'){
                    layer.load(1)
                    this.$nextTick(()=>{
                        this.$axios.get(this.$baseUrl+'/user/logout.do','')
                            .then(res =>{
                                layer.closeAll('loading');
                                if(res.data.success === 'true'){
                                    localStorage.removeItem('ms_username');
                                    this.$router.push('/login');
                                }
                            })
                            .catch(err =>{
                                layer.closeAll('loading');
                            })
                    })

                }else if(command === 'showPasswordForm'){
                    //初始化参数
                    this.passwordObj={
                        oldPassword:'',
                        password:''
                    }
                    this.password2='';
                    //弹窗
                    this.passWordForm = true;
                }
            },
            // 侧边栏折叠
            collapseChage(){
                this.collapse = !this.collapse;
                bus.$emit('collapse', this.collapse);
            },
            // 全屏事件
            handleFullScreen(){
                let element = document.documentElement;
                if (this.fullscreen) {
                    if (document.exitFullscreen) {
                        document.exitFullscreen();
                    } else if (document.webkitCancelFullScreen) {
                        document.webkitCancelFullScreen();
                    } else if (document.mozCancelFullScreen) {
                        document.mozCancelFullScreen();
                    } else if (document.msExitFullscreen) {
                        document.msExitFullscreen();
                    }
                } else {
                    if (element.requestFullscreen) {
                        element.requestFullscreen();
                    } else if (element.webkitRequestFullScreen) {
                        element.webkitRequestFullScreen();
                    } else if (element.mozRequestFullScreen) {
                        element.mozRequestFullScreen();
                    } else if (element.msRequestFullscreen) {
                        // IE11
                        element.msRequestFullscreen();
                    }
                }
                this.fullscreen = !this.fullscreen;
            },
            //获取标题
            getSystem(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/menu/selectSystem.do','')
                        .then(res=>{
                            this.loading = false;
                            this.systemMenu = res.data;
                            bus.$emit('systemId', res.data[0].id);
                        })
                        .catch(err=>{
                            this.loading = false;
                        })
                })
            },
            //切换标题
            changeSystem(id,index){
                this.current = index;
                bus.$emit('systemId', id);
                //判断折叠状态
                if(this.collapse){
                    this.collapse = false;
                    bus.$emit('collapse', this.collapse);
                }else{
                    this.collapseChage();
                    setTimeout(()=>{
                        this.collapseChage()
                    },700)
                }

            },
            //获取告警信息
            getBellData(){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/equipment/selectRisk.do','')
                        .then(res=>{
                            this.bellArr = res.data
                        })
                        .catch(err=>{

                        })
                })
            },
            //点击跳转到潜在威胁分析页面
            goToThreat(item){
                jumpHtml('equipmentThreat'+item.id,'equipment/equipmentThreat.vue',{ name:item.name,id: item.id ,logType:item.logType},'威胁分析')
            }
        },
        mounted(){
            if(document.body.clientWidth < 1500){
                this.collapseChage();
            }
        }
    }
</script>
<style scoped>
    .header {
        position: relative;
        box-sizing: border-box;
        width: 100%;
        height: 50px;
        font-size: 22px;
        color: #D6DFEB;
        background: rgb(25,28,40);
    }
    .collapse-btn{
        float: left;
        padding: 0 21px;
        cursor: pointer;
        line-height: 50px;
        position: relative;
        z-index: 5;
    }
    .header .logo{
        float: left;
        width:238px;
        line-height: 50px;
        margin-left: 10px;
    }
    .header .logo img{
        width: 100%;
    }
    .header-ul{
        height: 50px;
        position: absolute;
        left: 50%;
        display: flex;
        justify-content: center;
        
    }
    .header-ul li{
        position: relative;
        left: -50%;
        height: 50px;
        line-height: 50px;
        font-size: 16px;
        font-weight: 600;
        padding: 0 20px;
        cursor: pointer;
        /*background: #282d40;*/
        background: #333a52;
        color: #9ee0ec;
        border-right: 1px solid #191c28;
    }
    .header-ul li i{
        margin-right: 6px;
    }
    .header-ul li:hover {
        background: #3b425d;
    }
    .header-ul li.active{
        color: #dd916b;
        background: #464f73;
    }
    .header-ul li.active::after{
        content: '';
        position: absolute;
        width: 100%;
        height: 3px;
        background:  #dd916b;
        bottom: 0;
        left: 0;
    }
    .header-ul li.active::before{
        position: absolute;
        width: 0;
        bottom: 3px;
        height: 0;
        left: 50%;
        margin-left: -5px;
        content: " ";
        border-bottom: 5px solid #dd916b;
        border-left: 5px solid transparent;
        border-right: 5px solid transparent;
    }
    .header-right{
        float: right;
        padding-right: 20px;
    }
    .header-user-con{
        display: flex;
        height: 50px;
        line-height: 50px;
        align-items: center;
    }
    .header-user-con>div:hover,.el-dropdown-link:hover,.el-icon-bell:hover{
        color: #56a4ef!important;
    }
    .btn-fullscreen{
        transform: rotate(45deg);
        margin-right: 5px;
        font-size: 24px;
    }
    .btn-bell, .btn-fullscreen{
        position: relative;
        width: 50px;
        text-align: center;
        border-radius: 15px;
        cursor: pointer;
    }
    .btn-bell-badge{
        position: absolute;
        right: 0;
        top: 9px;
        width: 8px;
        height: 8px;
        border-radius: 4px;
        background: #f56c6c;
        color: #fff;
    }
    .btn-bell .el-icon-bell{
        color: #fff;
    }
    .bell-wapper{
        background: 0;
        border: 0;
        padding: 0;
        font-size: 20px;
    }
    .bell-wapper /deep/ .el-badge__content{
        background-color: #F56C6C;
        border-radius: 10px;
        color: #FFF;
        display: inline-block;
        font-size: 10px;
        height: 14px;
        line-height: 14px;
        padding: 0 4px;
        text-align: center;
        white-space: nowrap;
        border: 1px solid #FFF;

    }
    .bell-ul{
        max-height: 300px;
        overflow-y: auto;
    }
    .bell-li{
        border-bottom: 1px solid #29415a;
        padding: 2px 10px;
        padding-bottom: 10px;
        color: #bfcfe0;
        background: #304b67;
    }
    .bell-li p{
        padding: 5px;
        border-bottom: 1px dashed #3e5d7d;
    }
    .bell-li .bottom-p{
        font-size: 10px;
        padding-left: 10px;
        text-align: center;
    }
    .bell-li:hover{
        cursor: pointer;
        background: #406388;
    }
    .bell-li .name-b{
        color: #56a4ef;
    }
    .bell-li .high-b{
        color: #f56c6c;
        font-size: 12px;
    }
    .bell-li .center-b{
        color: #e6a23c;
        font-size: 12px;
    }
    .user-name{
        margin-left: 10px;
    }
    .user-avator{
        margin-left: 20px;
    }
    .user-avator img{
        display: block;
        width:40px;
        height:40px;
        border-radius: 50%;
    }
    .el-dropdown-link{
        color: #fff;
        cursor: pointer;
    }
    .el-dropdown-menu__item{
        text-align: center;
    }
    .drop-menu{
        border: 2px solid #fff;
        background: #48a2e3;
    }
    .pass_set{
        overflow: hidden;
        margin-bottom: 15px;
        margin-left: 100px;
    }
    .pass_set li {
        float: left;
        text-align: center;
        width: 50px;
        border-right: 2px solid #fff;
        background: #667e9a;
        color: #fff;
        list-style-type: none;
    }
</style>

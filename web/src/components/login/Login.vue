<template>
    <div class="login-wrap" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
<!--        <el-button type="primary" @click="changeSystem">切换系统</el-button>-->
        <div class="ms-login">
            <div class="ms-title"><img :src="systemObj.logo" alt=""></div>
<!--            <div class="ms-title"><img src="../../../static/img/logo_qwd.png" alt=""></div>-->
            <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="0px" class="ms-content" @submit.native.prevent>
                <el-form-item prop="username">
                    <el-input v-model="ruleForm.phone" placeholder="账号">
                        <el-button slot="prepend" icon="fa fa-user-o"></el-button>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input type="password" placeholder="密码" v-model="ruleForm.password">
                        <el-button slot="prepend" icon="fa fa-lock" style="font-size: 18px;"></el-button>
                    </el-input>
                </el-form-item>
                <div class="login-btn">
                    <el-button type="primary" native-type="submit" @click="getKey">登录</el-button>
                </div>
                <p style="height: 30px"><!--<router-link class="go-register" to="/resigter">没有账号？点击注册</router-link>--></p>
                <p class="gengxin" @click="uploadCertificate">证书更新</p>
            </el-form>
        </div>
        <el-dialog title="证书更新" :visible.sync="certificate" width="680px" height="550px" class="dialog-wapper">
            <p>通过浏览本地文件或将文件拖到下面指定区域，上传证书文件。</p>
            <p>文件支持的类型：<span class="txtColor">.lic</span></p>
            <div style="padding-right: 30px;">
                <input type="file" multiple id="ssi-upload"/>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button @click="certificate = false">取 消</el-button>
            </div>
        </el-dialog>
        <div style="position:relative;z-index: 0;width: 100vw;height: 100vh;" v-if="!warningState">
            <vue-canvas-nest :config = "{color:'255,255,255',pointColor:'255,255,255',count:50,zIndex:0,opacity:1}" ></vue-canvas-nest>
        </div>
        <div class="warning-wapper" v-if="warningState">
            <p>系统检测到您的浏览器版本过低，无法完美使用本系统，建议您安装最新的谷歌浏览器进行使用。</p>
        </div>
        <el-dialog title="提示" :visible.sync="passWordForm" width="400px" :close-on-click-modal="falseB" :destroy-on-close="trueB" >
            <p class="tipP"><i class="el-icon-warning" style="margin-right: 7px;font-size: 30px;"></i>
                用户 '<span>{{userValue}}</span>' 密码已过期，请重新设置密码后，才能正常使用系统!
            </p>
            <el-form label-width="100px">
                <el-form-item label="旧的密码:">
                    <el-input v-model="passwordObj.oldPassword" type="password" placeholder="旧的密码"  maxlength="18"  class="item"></el-input>
                </el-form-item>
                <el-form-item label="新的密码:">
                    <el-input v-model="passwordObj.password" type="password" placeholder="8-18位数字、字母、字符至少两者组合"  maxlength="18"  class="item"></el-input>
                </el-form-item>
                <ul class="pass_set" v-if="passwordObj.password.length > 0">
                    <li id="strength_L" style="background-color: rgb(237, 62, 13);">弱</li>
                    <li id="strength_M" :style="mode >=2 ? {background:'#ffaf56'}:{}">中</li>
                    <li id="strength_H" :style="mode > 2 ? {background:'#6ba001'}:{}">强</li>
                </ul>
                <el-form-item label="确认密码:">
                    <el-input v-model="password2" type="password" placeholder="8-18位数字、字母、字符至少两者组合"  maxlength="18"  class="item"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="changePassWord" >确 定</el-button>
                <el-button @click="passWordForm = false">取 消</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    //import crypto from 'crypto'
    import vueCanvasNest from 'vue-canvas-nest'
    // let Base64 = require('js-base64').Base64;
    import {checkStrong} from  '../../../static/js/common.js'
    export default {
        data(){
            return {
                loading:false,
                systemObj:{
                   /* logo:'../../../static/img/qywjcpt.png',
                    version:'V3.0',
                    index:'',*/
                },
                warningState:false,
                certificate:false,
                falseB:false,
                trueB:true,
                config : {
                    color: '255,255,255',
                    opacity: 0.7,
                    zIndex: 0,
                    count: 99,
                },
                ruleForm: {
                    phone: '',
                    password: ''
                },
                rules: {
                    phone: [
                        { required: true, message: '请输入用户名', trigger: 'blur' }
                    ],
                    password: [
                        { required: true, message: '请输入密码', trigger: 'blur' }
                    ]
                },
                passWordForm:false,
                userValue:'',
                passwordObj:{
                    oldPassword:'',
                    password:''
                },
                password2:'',
                mode:0,
            }
        },
        watch:{
            'certificate'(){
                if(this.certificate === true){
                    setTimeout(()=>{
                        $('#ssi-upload').ssi_uploader({
                            url:this.$baseUrl+'/upload/licenseUpload.do',//地址
                            maxNumberOfFiles:1,
                            allowed:['lic'],//允许上传文件的类型
                            ajaxOptions: {
                                success: function(data) {
                                    layer.msg(data,{icon:1})
                                },
                                error:function(data){
                                    layer.msg("上传失败",{icon: 5});
                                    $(".ssi-abortUpload").click()
                                }
                            }
                        })
                    },100)

                }
            },
            'passwordObj.password'(val){
                this.mode =  checkStrong(val);
            }
        },
        created(){
            this.getSystemObj();
        },
        mounted(){
            //判断是否为手机端
            var ua = navigator.userAgent;
            var ipad = ua.match(/(iPad).*OS\s([\d_]+)/),
                isIphone = !ipad && ua.match(/(iPhone\sOS)\s([\d_]+)/),
                isAndroid = ua.match(/(Android)\s+([\d.]+)/),
                isMobile = isIphone || isAndroid;
            if(isMobile) {
                //显示左边栏开关
                //window.location="mobile/index.html
                this.$router.push('/login_m');
            }


            var DEFAULT_VERSION = 8.0;
            var ua = navigator.userAgent.toLowerCase();
            var isIE = ua.indexOf("msie")>-1;
            var safariVersion;
            if(isIE){
                safariVersion =  ua.match(/msie ([\d.]+)/)[1];
            }
            if(safariVersion <= DEFAULT_VERSION ){
                this.warningState = true;
            }

        },
        methods: {
            //获取系统信息
            getSystemObj(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/product/getProductInfo.do',this.$qs.stringify())
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.systemObj = obj.message;
                                sessionStorage.setItem('systemObj', JSON.stringify(this.systemObj))
                                this.$store.commit('updateSystemObj',this.systemObj)
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },

            changeSystem(){
                if(this.systemObj.logo === '../../../static/img/qywjcpt.png'){
                    this.systemObj = {
                        logo:'../../../static/img/login_cx.png',
                        version:'V 3.0',
                        index:'/index',
                    }
                }else{
                    this.systemObj = {
                        logo:'../../../static/img/qywjcpt.png',
                        version:'V 1.0',
                        index:'/dataSourceIndex',
                    }
                }
                sessionStorage.setItem('systemObj', JSON.stringify(this.systemObj ))
                this.$store.commit('updateSystemObj',this.systemObj)
            },
            strToUtf8Bytes(str) {
                const utf8 = [];
                for (let ii = 0; ii < str.length; ii++) {
                    let charCode = str.charCodeAt(ii);
                    if (charCode < 0x80) utf8.push(charCode);
                    else if (charCode < 0x800) {
                        utf8.push(0xc0 | (charCode >> 6), 0x80 | (charCode & 0x3f));
                    } else if (charCode < 0xd800 || charCode >= 0xe000) {
                        utf8.push(0xe0 | (charCode >> 12), 0x80 | ((charCode >> 6) & 0x3f), 0x80 | (charCode & 0x3f));
                    } else {
                        ii++;
                        // Surrogate pair:
                        // UTF-16 encodes 0x10000-0x10FFFF by subtracting 0x10000 and
                        // splitting the 20 bits of 0x0-0xFFFFF into two halves
                        charCode = 0x10000 + (((charCode & 0x3ff) << 10) | (str.charCodeAt(ii) & 0x3ff));
                        utf8.push(
                            0xf0 | (charCode >> 18),
                            0x80 | ((charCode >> 12) & 0x3f),
                            0x80 | ((charCode >> 6) & 0x3f),
                            0x80 | (charCode & 0x3f),
                        );
                    }
                }
                //兼容汉字，ASCII码表最大的值为127，大于127的值为特殊字符
                for(let jj=0;jj<utf8.length;jj++){
                    var code = utf8[jj];
                    if(code>127){
                        utf8[jj] = code - 256;
                    }
                }
                return utf8;
            },
            /*获取秘钥*/
            getKey(){
                var index1 = layer.msg('登陆中,请稍等', {
                    icon: 16,
                    shade: 0.1,
                    time:0,
                    offset: '50px'
                });
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/rsa/getRSAPublicKey.do',this.$qs.stringify())
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                let PUBLIC_KEY = obj.message
                                //使用公钥加密
                                var encrypt = new JSEncrypt();
                                encrypt.setPublicKey('-----BEGIN PUBLIC KEY-----' + PUBLIC_KEY + '-----END PUBLIC KEY-----');
                               // console.log(JSON.stringify(this.ruleForm.phone))
                                let phone = encrypt.encrypt(this.ruleForm.phone)
                                let password = encrypt.encrypt(this.ruleForm.password)
                                this.login(phone,password,index1)
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            /*登录*/
            login(phone,password,index1) {
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/user/login.do',this.$qs.stringify({
                        phone: phone,
                        password:  password
                    }))
                        .then((res) => {
                            //关闭进度条
                            layer.close(index1);
                           if (res.data.success === 'true'){
                               if(res.data.state === '1'){//状态正常
                                   localStorage.setItem("LoginUser", JSON.stringify(res.data.user));
                                   localStorage.setItem("homePage", res.data.homepage);
                                   //判断是否存在 字段
                                   if(res.data.custom_name){
                                       localStorage.setItem("customName", res.data.custom_name);
                                   }
                                   if(res.data.license_due_time){
                                       localStorage.setItem("licenseDueTime", res.data.license_due_time);

                                   }
                                   this.getLogConfig();
                                   this.$router.push(this.systemObj.index);
                               }else if(res.data.state === '0'){//密码过期
                                    this.userValue = res.data.user.phone;
                                    this.passWordForm = true;
                               }

                               // this.$router.push('/flowIndex');

                           }else if(res.data.success==="false"){
                               layer.msg(res.data.message,{
                                   icon: 5,
                                   shade: 0.1,
                                   offset: '50px'
                               });
                           }
                        })
                        .catch((err) => {
                            //关闭进度条
                            layer.closeAll(index1);
                            console.log(err)
                        })
                })
            },
            /*重置密码*/
            changePassWord(){
                if(this.passwordObj.oldPassword === ''){
                    layer.msg('请填写原来密码',{icon: 5});
                }else if(this.passwordObj.password === ''){
                    layer.msg('未填写新的密码',{icon: 5});
                }else if(this.mode < 2){
                    layer.msg('密码格式不正确（8-18位数字、字母、字符至少两者组合）',{icon: 5});
                }else if(this.passwordObj.password .length < 8 || this.passwordObj.password .length>18){
                    layer.msg('密码长度不正确(8-18位)',{icon: 5});
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
            /*证书更新*/
            uploadCertificate(){
                this.certificate = true;
            },
            /*获取日志配置信息存放在本地*/
            getLogConfig(){
                this.$nextTick(()=>{
                    this.$axios.get('static/filejson/logconfig.json',{})
                        .then((res)=>{
                            //this.logConfig = res.data;
                            sessionStorage.setItem('logConfig',JSON.stringify(res.data))
                        })
                        .catch((err)=>{
                            console.log(err)
                        })
                })
            }
        },
        components:{
            vueCanvasNest
        }
    }
</script>

<style scoped>
    .login-wrap{
        position: relative;
        color: #fff;
        width:100%;
        height:100%;
        background-image: url(../../../static/img/bg.jpg);
        background-size: 100%;
        filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src='../../../static/img/bg.jpg',sizingMethod='scale');
        -ms-filter: "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='../../../static/img/bg.jpg',sizingMethod='scale')";
    }
    .ms-title{
        width:100%;
        line-height: 50px;
        text-align: center;
        font-size:20px;
        color: #fff;
        border-bottom: 1px solid #7f89a5;
    }
    .ms-title img{
        width: 370px;
    }
    .ms-login{
        position: absolute;
        z-index: 2;
        left:50%;
        top:50%;
        width:440px;
        padding: 22px 0;
        margin:-190px 0 0 -220px;
        border-radius: 5px;
        box-shadow: 0 5px 30px #2c3d4e;
        overflow: hidden;
    }
    .ms-login p{
        text-align: center;
    }
    .ms-content{
        padding: 20px 80px;
    }
    .login-btn{
        text-align: center;
    }
    .login-btn button{
        width:100%;
        height:36px;
        margin-bottom: 10px;
    }
    .go-register{
        font-size:13px;
        line-height:30px;
        color:#fff!important;
        text-align: center;
    }
    .go-register:hover{
        cursor: pointer;
        text-decoration: underline;
    }
    .gengxin{
        position: absolute;
        right: 34px;
        font-size: 13px;
        bottom: 15px;
        cursor: pointer;
        color: #409eff;
    }
    .gengxin:hover{
        color: #fff;
        text-decoration: underline;
    }
    .dialog-wapper p{
        color: #fff;
        line-height: 30px;
    }
    .dialog-wapper p span{
        color: #4da5ff;
    }
    .warning-wapper{
        height: 50px;
        display: flex;
        justify-content: center;
        position: fixed;
        bottom: 0;
        width: 100%;
    }
    .warning-wapper p{
        height: 50px;
        line-height: 50px;
        padding:0 20px;
        color: #e6a23c;
        background: #fdf6ec;
        border-color: #f5dab1;
        -webkit-border-radius: 10px;
        -moz-border-radius: 10px;
        border-radius: 10px;
    }
    .tipP{
        color: #e26262;
        font-size: 18px;
        font-weight: 600;
        margin-bottom: 30px;
        margin-left: 20px;
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

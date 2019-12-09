<template>
    <div class="login-wrap">
        <div class="ms-login">
            <div class="ms-title"><img src="../../../static/img/login_cx.png" alt=""></div>
            <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="0px" class="ms-content">
                <el-form-item prop="username">
                    <el-input v-model="ruleForm.phone" placeholder="账号">
                        <el-button slot="prepend" icon="el-icon-lx-people"></el-button>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input type="password" placeholder="密码" v-model="ruleForm.password">
                        <el-button slot="prepend" icon="el-icon-lx-lock"></el-button>
                    </el-input>
                </el-form-item>
                <div class="login-btn">
                    <el-button type="primary" @click="login">登录</el-button>
                </div>
                <p><router-link class="go-register" to="/resigter">没有账号？点击注册</router-link></p>
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
        <div style="position:relative;z-index: 0;width: 100vw;height: 100vh;">
            <vue-canvas-nest :config = "{color:'255,255,255',pointColor:'255,255,255',count:50,zIndex:0,opacity:1}" ></vue-canvas-nest>
        </div>
        <div class="warning-wapper" v-if="warningState">
            <p>系统检测到您的浏览器版本过低，无法完美使用本系统，建议您安装最新的谷歌浏览器进行使用。</p>
        </div>
    </div>
</template>

<script>
    import vueCanvasNest from 'vue-canvas-nest'

    export default {
        data(){
            return {
                warningState:false,
                certificate:false,
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
                }
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
            }
        },
        mounted(){

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
           /* submitForm(formName) {
                localStorage.setItem('ms_username',this.ruleForm.username);
                this.$router.push('/');
                /!*this.$refs[formName].validate((valid) => {
                    if (valid) {
                        localStorage.setItem('ms_username',this.ruleForm.username);
                        this.$router.push('/');
                    } else {
                        console.log('error submit!!');
                        return false;
                    }
                });*!/
            },*/
            login() {
                var index1 = layer.msg('登陆中,请稍等', {
                    icon: 16,
                    shade: 0.1,
                    time:0,
                    offset: '50px'
                });
                this.$nextTick(()=>{
                    this.$axios.get(this.$baseUrl+'/user/login.do',{
                        params:{
                            phone: this.ruleForm.phone,
                            password:  this.ruleForm.password
                        }
                    })
                        .then((res) => {
                            //关闭进度条
                            layer.close(index1);
                           if (res.data.success === 'true'){
                               localStorage.setItem("LoginUser", JSON.stringify(res.data.user));
                               this.getLogConfig();
                               this.$router.push('/index');
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
                            layer.closeAll('loading');
                            console.log(err)
                        })
                })
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
</style>

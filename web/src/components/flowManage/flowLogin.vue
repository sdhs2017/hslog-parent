<template>
    <div class="login-wapper">
        <div class="login-con">
            <h1>流量分析系统</h1>
            <el-form :model="form" :rules="rules" ref="ruleForm" label-width="0px" class="ms-content">
                <el-form-item prop="username">
                    <el-input v-model="form.phone" placeholder="输入账号" class="input-item" suffix-icon="el-icon-lx-people" >
<!--                        <el-button slot="prepend" icon="el-icon-lx-people"></el-button>-->
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input type="password" placeholder="输入密码" v-model="form.password" class="input-item" suffix-icon="el-icon-lx-lock">
<!--                        <el-button slot="prepend" icon="el-icon-lx-lock"></el-button>-->
                    </el-input>
                </el-form-item>
                <div class="login-btn">
                    <el-button type="primary" @click="login">登录</el-button>
                </div>
                <!--<p><router-link class="go-register" to="/resigter">没有账号？点击注册</router-link></p>
                <p class="gengxin" @click="uploadCertificate">证书更新</p>-->
            </el-form>
        </div>
    </div>
    
</template>

<script>
    export default {
        name: "flowLogin",
        data() {
            return {
                rules: {
                   /* phone: [
                        { required: true, message: '请输入用户名', trigger: 'blur' }
                    ],
                    password: [
                        { required: true, message: '请输入密码', trigger: 'blur' }
                    ]*/
                },
                form:{
                    phone:'',
                    password:''
                }
            }
        },
        created(){

        },
        methods:{
            login(){
                var index1 = layer.msg('登陆中,请稍等', {
                    icon: 16,
                    shade: 0.1,
                    time:0,
                    offset: '50px'
                });
                this.$nextTick(()=>{
                    this.$axios.get(this.$baseUrl+'/user/login.do',{
                        params:{
                            phone: this.form.phone,
                            password:  this.form.password
                        }
                    })
                        .then((res) => {
                            //关闭进度条
                            layer.close(index1);
                            if (res.data.success === 'true'){
                                localStorage.setItem("LoginUser", JSON.stringify(res.data.user));
                               // this.getLogConfig();
                                this.$router.push('/flowIndex');
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
            }
        }
    }
</script>

<style scoped>
    .login-wapper {
        background: url("../../../static/img/fb2.png");
        background-size: 100% 100%;
        position: relative;
        width:100%;
        height:100%;
        filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src='../../../static/img/Earth.jpg',sizingMethod='scale');
        -ms-filter: "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='../../../static/img/Earth.jpg',sizingMethod='scale')";
    }
    .login-con{
        width: 450px;
        height: 382px;
        position: absolute;
        left: 50%;
        top: 50%;
        margin-left: -225px;
        margin-top: -191px;
        /*background: linear-gradient(230deg,rgba(53,57,74,0) 0%,rgb(0,0,0) 100%);
        box-shadow: -15px 15px 15px rgba(6,17,47,.7);*/
        background: url("../../../static/img/bd2.png");
        background-size: 100% 100%;
    }
    .login-con h1{
        text-align: center;
        height: 100px;
        line-height: 100px;
        background-image: linear-gradient(to right, #4facfe 30%, #00f2fe 70%);
        -webkit-background-clip: text;
        color: transparent;
    }
    .login-con .ms-content{
        padding: 0 75px;
        margin: 50px 0;
        margin-top: 30px;
    }
    .login-con .input-item /deep/ .el-input__inner{
        background: none;
        border: 0;
        border-bottom: 1px solid #409EFF;
        border-radius: 0;
        margin-bottom: 10px;
        color: #00F6FF;
        text-align: center;
    }
    .input-item /deep/ .el-input__inner::placeholder {
        color:#37ceff;
    }
    .input-item /deep/ .el-input__icon{
        color: #00F6FF;
        font-size: 20px;
    }
    .login-btn{
        /*display: flex;*/
        /*justify-content: center;*/
        margin-top: 50px;
    }
    .login-btn /deep/ .el-button{
        background: 0;
        width: 100%;
        color: #00F6FF;
    }
    .login-btn /deep/ .el-button:hover{
        background:rgba(3, 213, 248, 0.3);
    }
</style>

<template>
    <div class="mobile-wapper">
        <div class="content-bg"  v-loading.fullscreen.lock="fullscreenLoading" element-loading-text="登陆中"  element-loading-background="rgba(0, 0, 0, 0.8)">
            <div class="content-wapper">
                <!--<img src="../../../static/img/login_cx.png" />-->
                <img src="../../../../static/img/logo_ay.png" />
                <div class="form-wapper">
                    <el-form ref="form" :model="form" label-width="0">
                        <el-form-item >
                            <el-input size="small" v-model="form.phone" placeholder="账号"></el-input>
                        </el-form-item>
                        <el-form-item >
                            <el-input type="password" size="small" v-model="form.password" placeholder="密码"></el-input>
                        </el-form-item>
                    </el-form>
                    <el-button class="btn-submit" size="small" type="primary" @click="login">登录</el-button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: "login_m",
        data() {
            return {
                fullscreenLoading:false,
                form:{
                    phone:'',
                    password:''
                }
            }
        },
        created(){
            //判断是否为手机端
            var ua = navigator.userAgent;
            var ipad = ua.match(/(iPad).*OS\s([\d_]+)/),
                isIphone = !ipad && ua.match(/(iPhone\sOS)\s([\d_]+)/),
                isAndroid = ua.match(/(Android)\s+([\d.]+)/),
                isMobile = isIphone || isAndroid;
            if(isMobile) {

            }else{
                this.$router.push('/Login');
            }
        },
        methods:{
            login(){
                //遮罩
                this.fullscreenLoading = true;
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/user/login.do',this.$qs.stringify({
                        phone: this.form.phone,
                        password: this.form.password
                    }))
                        .then((res) => {
                            //关闭进度条
                            this.fullscreenLoading = false;
                            if (res.data.success == 'true'){
                                localStorage.setItem("LoginUser", JSON.stringify(res.data.user));
                                this.$router.push('/mobile/index_m');
                            }else if(res.data.success == "false"){
                                layer.msg(res.data.message,{icon:5})
                            }
                        })
                        .catch((err) => {
                            //关闭进度条
                            console.log(err)
                        })
                })
            }
        }
    }
</script>

<style scoped>
    .mobile-wapper{
        width: 100vw;
        height: 100Vh;
        /*background: #ccc;*/
        position: fixed;
    }
    .content-bg{
        width: 100%;
        height: 100%;
        /*background: url('../../../static/img/bg.jpg') no-repeat;*/
        background:url("../../../../static/img/bg.jpg") no-repeat;;
        background-size: 100% 100%;
        font-size: 100px;
    }
    .content-wapper{
        height: 50vh;
        padding: 0 50px;
        display: flex;
        justify-content: center;
        flex-wrap: wrap;
        align-items: center;
        position: relative;
        top: 5%;
    }
    .content-wapper>img{
        width: 100%;
    }
    .content-wapper .form-wapper{
        width: 90%;
        margin-top: 100px;
    }
    .btn-submit{
        width: 100%;
    }
</style>

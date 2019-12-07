<template>
    <div class="resigter-wapper">
        <div class="logo-wapper">
            <img src="../../../static/img/login_cx.png" alt="">
        </div>
        <div class="form-wapper">
            <h3>游客注册</h3>
            <el-form class="form-content">
                <el-form-item label="账号(电话):">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input v-model="userParams.phone" placeholder="11位有效数字" maxlength="11"  class="item"></el-input>
                </el-form-item>
                <el-form-item label="密码:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input v-model="userParams.password" type="password" placeholder="6-18位数字、字母、字符"  maxlength="18"  class="item"></el-input>
                </el-form-item>
                <ul class="pass_set" v-if="userParams.password.length > 0">
                    <li id="strength_L" style="background-color: rgb(237, 62, 13);">弱</li>
                    <li id="strength_M" :style="mode >=2 ? {background:'#ffaf56'}:{}">中</li>
                    <li id="strength_H" :style="mode > 2 ? {background:'#6ba001'}:{}">强</li>
                </ul>
                <el-form-item label="确认密码:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input type="password" v-model="password2" placeholder="确认密码"  class="item" maxlength="18"></el-input>
                </el-form-item>
                <el-form-item label="姓名:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input v-model="userParams.name" placeholder="姓名"  class="item"></el-input>
                </el-form-item>
                <el-form-item label="邮箱:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input v-model="userParams.Email" placeholder="邮箱"  class="item"></el-input>
                </el-form-item>
                <el-form-item label="角色:">
                    <el-input placeholder="游客"  disabled class="item"></el-input>
                </el-form-item>
                <el-form-item label="年龄:">
                    <el-input v-model="userParams.age" type="number" placeholder="年龄"  class="item"></el-input>
                </el-form-item>
                <el-form-item label="性别:">
                    <el-radio-group v-model="userParams.sex">
                        <el-radio :label="1">男</el-radio>
                        <el-radio :label="0">女</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-button style="width: 100%;margin: 20px 0;" type="primary" @click="resigterUser">注册</el-button>
                <p><router-link class="go-login" to="/login">已有账号？点击登录</router-link></p>
            </el-form>
        </div>
        <div style="position:absolute;top:0;z-index: 0;width: 100vw;height: 100vh;">
            <vue-canvas-nest :config = "{color:'255,255,255',pointColor:'255,255,255',count:50,zIndex:0,opacity:1}" ></vue-canvas-nest>
        </div>
    </div>
    
</template>

<script>
    import {checkStrong} from  '../../../static/js/common.js'
    import vueCanvasNest from 'vue-canvas-nest'

    export default {
        name: "resigter",
        data() {
            return {
                userParams:{//用户表单参数
                    name:'',
                    sex:1,
                    phone:'',
                    age:'',
                    password:'',
                    Email:''
                },
                password2:'',
                mode:0

            }
        },
        watch:{
            'userParams.password'(val){
                this.mode =  checkStrong(val);
            }
        },
        methods:{
            /*注册*/
            resigterUser(){
                if(this.checkUserParams(this.userParams)){
                    layer.load(1)
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/users/registerUser.do',this.$qs.stringify(this.userParams))
                            .then(res =>{
                                layer.closeAll();
                                if(res.data.success === 'true'){
                                    layer.msg(res.data.message,{icon:1}) ;
                                    this.$router.push('/login');
                                }else{
                                    layer.msg(res.data.message,{icon:5}) ;
                                }
                            })
                            .catch(err =>{
                                layer.closeAll();

                            })
                    })
                }
            },
            /*检测用户参数合法性*/
            checkUserParams(params){
                //邮箱验证 正则表达式
                let reg = /^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/;
                if(params.password.length < 6 || params.password.length >18){
                    layer.msg('密码长度不正确（6-18）',{icon: 5});
                    return false;
                }else if(params.password !== this.password2){
                    layer.msg('两次密码不正确',{icon: 5});
                    return false;
                }else if(params.phone === '' || params.phone.length !== 11 || !$.isNumeric(params.phone)){
                    layer.msg('电话（账号）不能为空或格式不正确',{icon: 5});
                    return false;
                }else if(params.name === ''){
                    layer.msg('姓名不能为空',{icon: 5});
                    return false;
                }else if(!reg.test(params.Email)){
                    layer.msg('邮箱格式不正确',{icon: 5});
                    return false;
                }else{
                    return true;
                }
            }
        },
        components:{
            vueCanvasNest
        }
    }
</script>

<style scoped>

    .resigter-wapper{
        position: relative;
        overflow: auto;
        padding: 10px 0 50px 0;
        color: #fff;
        width:100%;
        height:100%;
        background-image: url(../../../static/img/bg.jpg);
        background-size: 100%;
        filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src='../../../static/img/bg.jpg',sizingMethod='scale');
        -ms-filter: "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='../../../static/img/bg.jpg',sizingMethod='scale')";
    }

    .logo-wapper{
        /*float: left;*/
        margin-left: 30px;
        /*margin-top: 10px;*/
    }
    .form-wapper{
        width: 100%;
        max-width: 700px!important;
        height: auto;
        position: relative;
        z-index: 1000;
        overflow: hidden;
        box-shadow: 0 5px 30px #2c3d4e;
        border-radius: 5px;
        margin: 0 auto;
        margin-bottom: 50px;
        padding: 10px 0 20px;
    }
    .form-wapper h3{
        color: #fff;
        padding-left: 20px;
        font-size: 22px;
    }
    .form-content{
        padding: 10px 100px;
    }
    .form-content>div{
        margin-bottom: 3px;
    }
    .pass_set{
        overflow: hidden;
        margin-top: 5px;
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
    .go-login{
        font-size: 13px;
        color: #ddd;
    }
    .go-login:hover{
        text-decoration: underline;
    }
</style>

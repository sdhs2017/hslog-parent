<template>
    <div class="content-bg" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
        <div class="top-title">系统配置</div>
        <div class="wapper-con">
            <div>
                <el-form class="form-wapper" >
                    <el-form-item label="数据存储周期(不少于90天)">
                        <el-input v-model="config.es_storage_day" size="mini" min="90" type="number" placeholder="日志存储时间不小于90天"  class="item"></el-input>
                    </el-form-item>
                    <el-form-item label="系统盘阈值">
                        <div style="display: flex;margin-top: 32px;">
                            <input type="range" v-model="config.disk_system_watermark"  size="mini" style="width: 100%;" min = "0" step="1" max="100">
                            <span>{{config.disk_system_watermark}}%</span>
                        </div>
                    </el-form-item>
                    <el-form-item label="数据盘阈值（将满)">
                        <div style="display: flex;margin-top: 32px;">
                            <input type="range" v-model="config.disk_data_watermark"  size="mini" style="width: 100%;" min = "0" step="1" max="100">
                            <span>{{config.disk_data_watermark}}%</span>
                        </div>
                    </el-form-item>
                    <el-form-item label="数据盘阈值（已满）">
                        <div style="display: flex;margin-top: 32px;">
                            <input type="range" v-model="config.disk_data_watermark_high"  size="mini" style="width: 100%;" min = "10" step="1" max="100">
                            <span>{{config.disk_data_watermark_high}}%</span>
                        </div>
<!--                        <p style="color: rgb(228, 149, 109);">注：数据盘到达设置的阈值（已满），会对历史数据进行覆盖。</p>-->
                    </el-form-item>

                    <el-form-item label="数据批量采集">
                        <el-input v-model="config.es_bulk" size="mini" type="number" min="1"></el-input>
                    </el-form-item>

                    <el-form-item label="并发采集数">
                        <el-input v-model="config.concurrent_requests" size="mini" type="number" min="1"></el-input>
                    </el-form-item>

                    <el-form-item label="密码过期时间（天）">
                        <el-input v-model="config.pwd_expire_day" min="1" type="number" size="mini" class="item"></el-input>
                    </el-form-item>
                    <el-form-item label="密码长度（不少于8位）">
                        <el-input v-model="config.pwd_length" min="8"  type="number" size="mini"  class="item"></el-input>
                    </el-form-item>
                    <el-form-item label="密码复杂度（至少选择其中两项）">
                        <p style="margin-top: 32px;">
                            <el-checkbox-group v-model="config.pwd_complex">
                                <el-checkbox label="数字"></el-checkbox>
                                <el-checkbox label="字母"></el-checkbox>
                                <el-checkbox label="符号"></el-checkbox>
                            </el-checkbox-group>
                        </p>

                    </el-form-item>
                    <el-form-item label="密码尝试次数（1-5次）">
                        <el-input v-model="config.pwd_try" min="1" max="5"  type="number" size="mini" class="item"></el-input>
                    </el-form-item>
                    <el-form-item label="登录超时时间（分钟）">
                        <el-input v-model="config.session_timeout" min="1"   type="number" size="mini" class="item"></el-input>
                    </el-form-item>
                    <el-form-item label="IP黑名单(多个IP用“,”隔开)">
                        <el-input v-model="config.ip_black"  size="mini" class="item"></el-input>
                    </el-form-item>
                </el-form>
                <div class="btn-wapper"><el-button type="primary" @click="subConfig">确定</el-button></div>
            </div>

        </div>

    </div>
</template>

<script>
    import {setPasswordComplex} from  '../../../static/js/common.js'
    export default {
        name: "systemConfig_SR",
        data(){
            return{
                loading:false,
                config:{
                    disk_system_watermark:'',//系统盘阈值
                    es_storage_day:'90',//日志存储时间  不少于90天
                    disk_data_watermark:'80',//数据盘阈值  将满告警
                    disk_data_watermark_high:'95',//数据盘阈值  已满告警
                    pwd_expire_day:'7',//密码过期时间 > 0
                    pwd_length:'8',//密码长度 大于8位
                    pwd_complex:['数字','字母'],//密码复杂度  字母数字符号 至少两种
                    pwd_try:'5',//密码尝试次数 1-5
                    session_timeout:'10',//登陆超时时间 1-10分钟
                    ip_black:'',//ip黑名单
                    es_bulk:'',//批量采集数
                    concurrent_requests:'',//并发采集数
                }
            }
        },
        created(){
            this.getConfig();
        },
        methods:{
            /*提交数据*/
            subConfig(){
                //判断数据合法性
                if(this.config.es_storage_day < 90){
                    layer.msg("日志存储日志不少于90天",{icon: 5});
                    return false;
                }else if(this.config.pwd_expire_day < 1){
                    layer.msg("密码过期时间需大于0",{icon: 5});
                    return false;
                }else if(this.config.pwd_length < 8){
                    layer.msg("密码长度不小于8位",{icon: 5});
                    return false;
                }else if(this.config.pwd_complex.length < 2){
                    layer.msg("密码复杂度至少选择两种组合",{icon: 5});
                    return false;
                }else if(this.config.pwd_try < 1){
                    layer.msg("密码尝试次数范围1-5",{icon: 5});
                    return false;
                }else if(this.config.pwd_try > 5){
                    layer.msg("密码尝试次数范围1-5",{icon: 5});
                    return false;
                }else if(this.config.session_timeout < 1){
                    layer.msg("登陆超时时间需大于0",{icon: 5});
                    return false;
                }else if(this.config.es_bulk < 1){
                    layer.msg("批量采集数需大于0",{icon: 5});
                    return false;
                }else if(this.config.concurrent_requests < 1){
                    layer.msg("并发采集数需大于0",{icon: 5});
                    return false;
                }else{
                    let obj = JSON.parse(JSON.stringify(this.config))
                    obj.pwd_complex = JSON.stringify(obj.pwd_complex)
                    console.log(obj)
                    this.$nextTick(()=>{
                        this.loading = true;
                        this.$axios.post(this.$baseUrl+'/configuration/update.do',this.$qs.stringify(obj))
                            .then(res=>{
                                this.loading = false;
                                let obj = res.data;
                                if(obj.success == 'true'){
                                    layer.msg(res.data.message,{icon:1})
                                    this.getConfig();
                                }else{
                                    layer.msg(obj.message,{icon:5})
                                }
                            })
                            .catch(err=>{
                                 this.loading = false;
                            })
                    })
                }

            },
            //获取配置数据
            getConfig(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/configuration/selectAll.do',this.$qs.stringify())
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                let arr = obj.data;
                                for (let i in arr){
                                    if(arr[i].configuration_key === 'es_storage_day'){
                                        this.config.es_storage_day = arr[i].configuration_value
                                    }else if(arr[i].configuration_key === 'disk_data_watermark'){
                                        this.config.disk_data_watermark = arr[i].configuration_value
                                    }else if(arr[i].configuration_key === 'disk_data_watermark_high'){
                                        this.config.disk_data_watermark_high = arr[i].configuration_value
                                    }else if(arr[i].configuration_key === 'pwd_expire_day'){
                                        this.config.pwd_expire_day = arr[i].configuration_value
                                    }else if(arr[i].configuration_key === 'pwd_length'){
                                        this.config.pwd_length = arr[i].configuration_value
                                    }else if(arr[i].configuration_key === 'pwd_try'){
                                        this.config.pwd_try = arr[i].configuration_value
                                    }else if(arr[i].configuration_key === 'session_timeout'){
                                        this.config.session_timeout = arr[i].configuration_value
                                    }else if(arr[i].configuration_key === 'ip_black'){
                                        this.config.ip_black = arr[i].configuration_value
                                    }else if(arr[i].configuration_key === 'pwd_complex'){
                                        this.config.pwd_complex = JSON.parse(arr[i].configuration_value)
                                    }else if(arr[i].configuration_key === 'disk_system_watermark'){
                                        this.config.disk_system_watermark = JSON.parse(arr[i].configuration_value)
                                    }else if(arr[i].configuration_key === 'es_bulk'){
                                        this.config.es_bulk = JSON.parse(arr[i].configuration_value)
                                    }else if(arr[i].configuration_key === 'concurrent_requests'){
                                        this.config.concurrent_requests = JSON.parse(arr[i].configuration_value)
                                    }

                                }
                                setPasswordComplex(this.config.pwd_length,this.config.pwd_complex)

                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            }
        }
    }
</script>

<style scoped>
    .content-bg{
        padding-bottom: 20px;
    }
    .form-wapper{
        width: 700px;
        margin: 20px auto;
        background: #343E4D;
        padding:30px 50px;
    }
    .wapper-con{
        height: 100%;
        display: flex;
        justify-content: center;
        flex-wrap: wrap;
    }
    .btn-wapper{
        width: 800px;
        margin: 0 auto;
    }
    .btn-wapper button{
        width: 100%;
    }
</style>

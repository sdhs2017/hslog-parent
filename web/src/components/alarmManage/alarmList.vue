<template>
    <div  class="content-bg" v-loading="loading" element-loading-background="rgba(48, 62, 78, 0.5)">
        <div class="top-title">告警管理
            <div class="btn-wapper">
                <el-button type="primary" size="mini" plain @click="goToAddAlarm">添加</el-button>
                <el-button type="success" size="mini" plain  @click="refresh">刷新</el-button>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: "alarmList",
        data(){
            return{
                loading:false
            }
        },
        methods:{
            /*添加告警*/
            goToAddAlarm(){
                this.$router.push({path:'addAlarm'});
            },
            /*刷新*/
            refresh(){

            }
        }
    }
</script>

<style scoped>

</style>

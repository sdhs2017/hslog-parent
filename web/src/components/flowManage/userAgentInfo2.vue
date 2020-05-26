<template>
    <div class="content-bg">
        <div class="top-title">User-Agent信息
            <v-basedate class="from-wapper" type="datetimerange" busName="userAgentInfoTimeBus2"></v-basedate>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">业务访问用户统计-操作系统</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper system-chart">
                            <agentSystem_bar :params="dateArr"></agentSystem_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper system-chart">
                            <agentSystem_pie :params="dateArr"></agentSystem_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>

            <div class="echarts-item">
                <p class="echarts-tit">业务访问用户统计-浏览器</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper browser-chart">
                            <agentBrowser_bar :params="dateArr"></agentBrowser_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper browser-chart">
                            <agentBrowser_pie :params="dateArr"></agentBrowser_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>
    </div>
    
</template>

<script>
    import vBasedate from '../common/baseDate'
    import bus from '../common/bus';
    import agentBrowser_bar from '../charts/flow/userAgentInfo/agentBrowser_bar'
    import agentBrowser_pie from '../charts/flow/userAgentInfo/agentBrowser_pie'
    import agentSystem_bar from '../charts/flow/userAgentInfo/agentSystem_bar'
    import agentSystem_pie from '../charts/flow/userAgentInfo/agentSystem_pie'
    import {dateFormat} from '../../../static/js/common'
    export default {
        name: "userAgentInfo2",
        data() {
            return {
                dateArr:{},
            }
        },
        created(){
            /*监听日期改变*/
            bus.$on('userAgentInfoTimeBus2',(arr)=>{
                layer.load(1)
                let paramObj = {
                    starttime:arr[0],
                    endtime:arr[1]
                }
                this.dateArr = paramObj;
            })

        },
        mounted(){},
        methods:{
        },
        beforeDestroy(){
            bus.$off('userAgentInfoTimeBus2');
        },
        components:{
            vBasedate,
            agentBrowser_bar,
            agentBrowser_pie,
            agentSystem_bar,
            agentSystem_pie
        }
    }
</script>

<style scoped>
    .from-wapper{
        float: right;
        margin-right: 10px;
        margin-top: 5px;
    }
    .flow-echarts-con{
        padding: 20px;
        /*padding-bottom: 80px;*/
        /*height: calc(100% - 100px);*/
    }
    .echarts-item{
        background: #303e4e;
        margin: 20px 0;

    }
    .echarts-tit{
        height: 80px;
        line-height: 80px;
        text-align: center;
        font-weight: 600;
        font-size: 21px;
        color: #5bc0de;
    }
    .flow-row{
        padding-bottom: 80px;
        margin: 0 20px;
    }
    .chart-wapper{
        height: 60vh;
        min-height: 420px;
        /*padding-top: 50px;*/
        /*background: #303e4e;*/
    }
    .date-wapper{
        margin-bottom: 20px;
        padding-left: 50px;
    }
    .chart-from{
        padding-left: 50px;
    }
</style>

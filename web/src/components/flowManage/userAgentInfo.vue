<template>
    <div class="content-bg">
        <div class="top-title">User-Agent信息
            <v-flowchartfrom class="from-wapper" refreshBus="userAgentInfoRefreshBus" dataBus="userAgentInfoDataBus" switchBus="userAgentInfoSwitchBus" timeBus="userAgentInfoTimeBus"></v-flowchartfrom>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">业务访问用户统计-操作系统</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper system-chart">
                            <agentSystem_bar :params="params" :setIntervalObj="intervalObj"></agentSystem_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper system-chart">
                            <agentSystem_pie :params="params" :setIntervalObj="intervalObj"></agentSystem_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>

            <div class="echarts-item">
                <p class="echarts-tit">业务访问用户统计-浏览器</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper browser-chart">
                            <agentBrowser_bar :params="params" :setIntervalObj="intervalObj"></agentBrowser_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper browser-chart">
                            <agentBrowser_pie :params="params" :setIntervalObj="intervalObj"></agentBrowser_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>
    </div>
    
</template>

<script>
    import vFlowchartfrom from '../common/flowChartsFrom'
    import vEcharts from '../common/echarts'
    import bus from '../common/bus';
    import agentBrowser_bar from '../charts/flow/userAgentInfo/agentBrowser_bar'
    import agentBrowser_pie from '../charts/flow/userAgentInfo/agentBrowser_pie'
    import agentSystem_bar from '../charts/flow/userAgentInfo/agentSystem_bar'
    import agentSystem_pie from '../charts/flow/userAgentInfo/agentSystem_pie'
    import {dateFormat} from '../../../static/js/common'
    export default {
        name: "userAgentInfo",
        data() {
            return {
                params:{
                    timeInterval:3600
                },
                intervalObj:{
                    state:true,
                    interval:'5000'
                },
                dataTime:'3600',
            }
        },
        created(){
            /*监听刷新时间改变*/
            bus.$on('userAgentInfoRefreshBus',(val)=>{
                this.intervalObj.interval = val
            })
            /*监听数据时间改变*/
            bus.$on('userAgentInfoDataBus',(val)=>{
                this.dataTime = val;
                this.params = {
                    timeInterval:val
                }
            })
            /*监听switch改变*/
            bus.$on('userAgentInfoSwitchBus',(obj)=>{
                this.intervalObj.state = obj.switchVal;
                if (obj.switchVal) {
                    this.params = {
                        timeInterval:this.dataTime
                    }
                }
            })
            /*监听日期改变*/
            bus.$on('userAgentInfoTimeBus',(obj)=>{
                this.params = {
                    starttime:obj.dateArr[0],
                    endtime:obj.dateArr[1]
                }
            })

        },
        mounted(){
        },
        methods:{
        },
        beforeDestroy(){
            bus.$off('userAgentInfoRefreshBus');
            bus.$off('userAgentInfoDataBus')
            bus.$off('userAgentInfoSwitchBus')
            bus.$off('userAgentInfoTimeBus')
        },
        components:{
            vFlowchartfrom,
            vEcharts,
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

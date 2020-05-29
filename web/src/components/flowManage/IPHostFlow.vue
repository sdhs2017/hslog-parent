<template>
    <div class="content-bg">
        <div class="top-title">IP主机流量
            <v-flowchartfrom class="from-wapper" refreshBus="IPHostFlowRefreshBus" dataBus="IPHostFlowDataBus" switchBus="IPHostFlowSwitchBus" timeBus="IPHostFlowTimeBus"></v-flowchartfrom>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">源IP地址流量排序</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <srcIp_bar :params="params" :setIntervalObj="intervalObj"></srcIp_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <srcIp_pie :params="params" :setIntervalObj="intervalObj"></srcIp_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">目的IP地址流量排序</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <dstIp_bar :params="params" :setIntervalObj="intervalObj"></dstIp_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <dstIp_pie :params="params" :setIntervalObj="intervalObj"></dstIp_pie>
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
    import dstIp_bar from '../charts/flow/ipHostFlow/dstIp_bar'
    import dstIp_pie from '../charts/flow/ipHostFlow/dstIp_pie'
    import srcIp_bar from '../charts/flow/ipHostFlow/srcIp_bar'
    import srcIp_pie from '../charts/flow/ipHostFlow/srcIp_pie'
    import {dateFormat} from '../../../static/js/common'
    export default {
        name: "IPHostFlow",
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
            bus.$on('IPHostFlowRefreshBus',(val)=>{
                this.intervalObj.interval = val
            })
            /*监听数据时间改变*/
            bus.$on('IPHostFlowDataBus',(val)=>{
                this.dataTime = val;
                this.params = {
                    timeInterval:val
                }
            })
            /*监听switch改变*/
            bus.$on('IPHostFlowSwitchBus',(obj)=>{
                this.intervalObj.state = obj.switchVal;
                if (obj.switchVal) {
                    this.params = {
                        timeInterval:this.dataTime
                    }
                }
            })
            /*监听日期改变*/
            bus.$on('IPHostFlowTimeBus',(obj)=>{
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
            bus.$off('IPHostFlowRefreshBus');
            bus.$off('IPHostFlowDataBus')
            bus.$off('IPHostFlowSwitchBus')
            bus.$off('IPHostFlowTimeBus')
        },
        components:{
            vFlowchartfrom,
            vEcharts,
            dstIp_bar,
            dstIp_pie,
            srcIp_bar,
            srcIp_pie
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

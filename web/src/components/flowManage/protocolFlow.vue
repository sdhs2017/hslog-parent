<template>
    <div class="content-bg">
        <div class="top-title">协议流量
            <v-flowchartfrom class="from-wapper" refreshBus="protocolFlowRefreshBus" dataBus="protocolFlowDataBus" switchBus="protocolFlowSwitchBus" timeBus="protocolFlowTimeBus"></v-flowchartfrom>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">传输层协议长度统计</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <transport_bar :params="params" :setIntervalObj="intervalObj"></transport_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <transport_pie :params="params" :setIntervalObj="intervalObj"></transport_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">应用层协议长度统计</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <application_bar :params="params" :setIntervalObj="intervalObj"></application_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <application_pie :params="params" :setIntervalObj="intervalObj"></application_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">协议长度综合统计</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <multiple_bar :params="params" :setIntervalObj="intervalObj"></multiple_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <multiple_pie :params="params" :setIntervalObj="intervalObj"></multiple_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>
    </div>
    
</template>

<script>
    import vFlowchartfrom from '../common/flowChartsFrom'
    import application_bar from '../charts/flow/protocoFlow/application_bar'
    import application_pie from '../charts/flow/protocoFlow/application_pie'
    import multiple_bar from '../charts/flow/protocoFlow/multiple_bar'
    import multiple_pie from '../charts/flow/protocoFlow/multiple_pie'
    import transport_bar from '../charts/flow/protocoFlow/transport_bar'
    import transport_pie from '../charts/flow/protocoFlow/transport_pie'
    import bus from '../common/bus';
    import {dateFormat} from '../../../static/js/common'
    export default {
        name: "protocolFlow",
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
            bus.$on('protocolFlowRefreshBus',(val)=>{
                this.intervalObj.interval = val
            })
            /*监听数据时间改变*/
            bus.$on('protocolFlowDataBus',(val)=>{
                this.dataTime = val;
                this.params = {
                    timeInterval:val
                }
            })
            /*监听switch改变*/
            bus.$on('protocolFlowSwitchBus',(obj)=>{
                this.intervalObj.state = obj.switchVal;
                if (obj.switchVal) {
                    this.params = {
                        timeInterval:this.dataTime
                    }
                }
            })
            /*监听日期改变*/
            bus.$on('protocolFlowTimeBus',(obj)=>{
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
            bus.$off('protocolFlowRefreshBus');
            bus.$off('protocolFlowDataBus')
            bus.$off('protocolFlowSwitchBus')
            bus.$off('protocolFlowTimeBus')
        },
        components:{
            vFlowchartfrom,
            application_bar,
            application_pie,
            multiple_bar,
            multiple_pie,
            transport_bar,
            transport_pie
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

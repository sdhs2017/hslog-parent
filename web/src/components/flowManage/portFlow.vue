<template>
    <div class="content-bg">
        <div class="top-title">全局端口流量
            <v-flowchartfrom class="from-wapper" refreshBus="portFlowRefreshBus" dataBus="portFlowDataBus" switchBus="portFlowSwitchBus" timeBus="portFlowTimeBus"></v-flowchartfrom>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">目的端口总流量</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <dstPortAll_bar :params="params" :setIntervalObj="intervalObj"></dstPortAll_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <dstPortAll_pie :params="params" :setIntervalObj="intervalObj"></dstPortAll_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">TCP目的端口总流量</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <tcpDstPort_bar :params="params" :setIntervalObj="intervalObj"></tcpDstPort_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <tcpDstPort_pie :params="params" :setIntervalObj="intervalObj"></tcpDstPort_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">UDP目的端口总流量</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <udpDstPort_bar :params="params" :setIntervalObj="intervalObj"></udpDstPort_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <udpDstPort_pie :params="params" :setIntervalObj="intervalObj"></udpDstPort_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>
    </div>
    
</template>

<script>
    import vFlowchartfrom from '../common/flowChartsFrom'
    //import vEcharts from '../common/echarts'
    import bus from '../common/bus';
    import dstPortAll_bar from '../charts/flow/portFlow/dstPortAll_bar'
    import dstPortAll_pie from '../charts/flow/portFlow/dstPortAll_pie'
    import tcpDstPort_bar from '../charts/flow/portFlow/tcpDstPort_bar'
    import tcpDstPort_pie from '../charts/flow/portFlow/tcpDstPort_pie'
    import udpDstPort_bar from '../charts/flow/portFlow/udpDstPort_bar'
    import udpDstPort_pie from '../charts/flow/portFlow/udpDstPort_pie'
    export default {
        name: "portFlow",
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
            bus.$on('portFlowRefreshBus',(val)=>{
                this.intervalObj.interval = val
            })
            /*监听数据时间改变*/
            bus.$on('portFlowDataBus',(val)=>{
                this.dataTime = val;
                this.params = {
                    timeInterval:val
                }
            })
            /*监听switch改变*/
            bus.$on('portFlowSwitchBus',(obj)=>{
                this.intervalObj.state = obj.switchVal;
                if (obj.switchVal) {
                    this.params = {
                        timeInterval:this.dataTime
                    }
                }
            })
            /*监听日期改变*/
            bus.$on('portFlowTimeBus',(obj)=>{
                layer.load(1)
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
            bus.$off('portFlowRefreshBus');
            bus.$off('portFlowTimeBus')
            bus.$off('portFlowSwitchBus')
            bus.$off('portFlowDataBus')
        },
        components:{
            vFlowchartfrom,
            dstPortAll_bar,
            dstPortAll_pie,
            tcpDstPort_bar,
            tcpDstPort_pie,
            udpDstPort_bar,
            udpDstPort_pie
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

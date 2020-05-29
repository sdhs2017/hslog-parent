<template>
    <div class="content-bg">
        <div class="top-title">资产流量
            <v-flowchartfrom class="from-wapper" refreshBus="equipmentFlowRefreshBus_n" dataBus="equipmentFlowDataBus_n" switchBus="equipmentFlowSwitchBus_n" timeBus="equipmentFlowTimeBus_n"></v-flowchartfrom>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">资产（IP）数据包个数</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <eqippacketbar :params="params" :setIntervalObj="intervalObj"></eqippacketbar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <eqippacketpie :params="params" :setIntervalObj="intervalObj"></eqippacketpie>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">资产（服务）数据包个数</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <eqserverpacketbar :params="params" :setIntervalObj="intervalObj"></eqserverpacketbar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <eqserverpacketpie :params="params" :setIntervalObj="intervalObj"></eqserverpacketpie>
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
    import eqippacketbar from '../charts/flow/equipmentFlow/eqIpPacket_bar'
    import eqippacketpie from '../charts/flow/equipmentFlow/eqIpPacket_pie'
    import eqserverpacketbar from '../charts/flow/equipmentFlow/eqServerPacket_bar'
    import eqserverpacketpie from '../charts/flow/equipmentFlow/eqServerPacket_pie'
    import {dateFormat} from '../../../static/js/common'
    export default {
        name: "equipmentFlow",
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
            bus.$on('equipmentFlowRefreshBus_n',(val)=>{
                this.intervalObj.interval = val
            })
            /*监听数据时间改变*/
            bus.$on('equipmentFlowDataBus_n',(val)=>{
                this.dataTime = val;
                this.params = {
                    timeInterval:val
                }
            })
            /*监听switch改变*/
            bus.$on('equipmentFlowSwitchBus_n',(obj)=>{
                this.intervalObj.state = obj.switchVal;
                if (obj.switchVal) {
                    this.params = {
                        timeInterval:this.dataTime
                    }
                }
            })
            /*监听日期改变*/
            bus.$on('equipmentFlowTimeBus_n',(obj)=>{
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
            bus.$off('equipmentFlowRefreshBus_n');
            bus.$off('equipmentFlowDataBus_n')
            bus.$off('equipmentFlowSwitchBus_n')
            bus.$off('equipmentFlowTimeBus_n')
        },
        components:{
            vFlowchartfrom,
            vEcharts,
            eqippacketbar,
            eqippacketpie,
            eqserverpacketbar,
            eqserverpacketpie
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

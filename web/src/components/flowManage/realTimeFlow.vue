<template>
    <div class="content-bg">
        <div class="top-title">全局实时流量
            <v-flowchartfrom class="from-wapper" refreshBus="realTimeFlowRefreshBus" :refreshObj="refreshObj"></v-flowchartfrom>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">全局-利用率百分比</p>
                <div class="chart-from">
                    <span>带宽：</span>
                    <el-select v-model="bandwidth" placeholder="请选择" size="mini" >
                        <el-option
                            v-for="item in bandwidthArr"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </div>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <allUsedPer_timeline :params="param" :setIntervalObj="intervalObj"></allUsedPer_timeline>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <allUsedPer_gauge :params="param" :setIntervalObj="intervalObj"></allUsedPer_gauge>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <el-row :gutter="20" class="flow-row">
                <el-col :span="12">
                    <div class="echarts-item chart-wapper" style="padding-top: 30px">
                        <allPacketCount_timeline :params="param" :setIntervalObj="intervalObj"></allPacketCount_timeline>
                    </div>
                </el-col>
                <el-col :span="12">
                    <div class="echarts-item chart-wapper" style="padding-top: 30px">
                        <allflow_timeline :params="param" :setIntervalObj="intervalObj"></allflow_timeline>
                    </div>
                </el-col>
            </el-row>
        </div>
    </div>
    
</template>

<script>
    import vFlowchartfrom from '../common/flowChartsFrom'
    //import vEcharts from '../common/echarts'
    import allflow_timeline from '../charts/flow/realTime/allflow_timeline'
    import allPacketCount_timeline from '../charts/flow/realTime/allPacketCount_timeline'
    import allUsedPer_gauge from '../charts/flow/realTime/allUsedPer_gauge'
    import allUsedPer_timeline from '../charts/flow/realTime/allUsedPer_timeline'
    import bus from '../common/bus';
    import {dateFormat} from '../../../static/js/common'
    export default {
        name: "realTimeFlow",
        data() {
            return {
                param:{
                    timeInterval:5
                },
                barParam:{},
                intervalObj:{
                    state:true,
                    interval:'5000'
                },
                refreshObj:{
                    defaultVal:'5',
                    opt:[{
                        label:'5s',
                        value:'5'
                    },
                        {
                            label:'10s',
                            value:'10'
                        },
                        {
                            label:'20s',
                            value:'20'
                        }
                    ]
                },
                //刷新时间间隔
                refreshIntTime :'5000',
                chartType:'timeline',
                //全局利用率百分比
                bandwidth:10,
                bandwidthArr:[
                    {
                        label:'10M',
                        value:10
                    },
                    {
                        label:'100M',
                        value:100
                    },
                    {
                        label:'1000M',
                        value:1000
                    }
                ],
            }
        },
        created(){
            /*监听刷新时间改变*/
            bus.$on('realTimeFlowRefreshBus',(val)=>{
                this.intervalObj.interval = val*1000
                this.refreshIntTime = this.intervalObj.interval
            })

        },
        mounted(){
        },
        methods:{
        },
        beforeDestroy(){
            bus.$off('realTimeFlowRefreshBus')
        },
        components:{
            vFlowchartfrom,
            allflow_timeline,
            allPacketCount_timeline,
            allUsedPer_gauge,
            allUsedPer_timeline
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

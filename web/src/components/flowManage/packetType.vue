<template>
    <div class="content-bg">
        <div class="top-title">数据包类型
            <v-flowchartfrom class="from-wapper" refreshBus="packetTypeRefreshBus" :refreshObj="refreshObj" switchBus="packetTypeSwitchBus" timeBus="packetTypeTimeBus"></v-flowchartfrom>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">全局-数据包类型个数</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="24">
                        <div class="chart-wapper ip-chart" v-if="chartType === 'timeline'">
                            <packetType_timeline :params="lineParam" :setIntervalObj="intervalObj"> </packetType_timeline>
                        </div>
                        <div class="chart-wapper ip-chart" v-else>
                            <packetType_bar :params="barParam" :setIntervalObj="intervalObj"> </packetType_bar>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>
    </div>
    
</template>

<script>
    import vFlowchartfrom from '../common/flowChartsFrom'
    import packetType_timeline from '../charts/flow/packetType/packetType_timeline'
    import packetType_bar from '../charts/flow/packetType/packetType_bar'
    import bus from '../common/bus';
    import {dateFormat} from '../../../static/js/common'
    export default {
        name: "packetType",
        data() {
            return {
                lineParam:{
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
            }
        },
        created(){
            /*监听刷新时间改变*/
            bus.$on('packetTypeRefreshBus',(val)=>{
                this.intervalObj.interval = val*1000
                this.refreshIntTime = this.intervalObj.interval
            })
            /*监听switch改变*/
            bus.$on('packetTypeSwitchBus',(obj)=>{
                this.intervalObj.state = obj.switchVal;
                if(obj.switchVal){
                    this.chartType = 'timeline'
                }
            })
            /*监听日期改变*/
            bus.$on('packetTypeTimeBus',(obj)=>{
                this.chartType = 'bar'
                this.barParam = {
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
            bus.$off('packetTypeTimeBus')
            bus.$off('packetTypeSwitchBus')
            bus.$off('packetTypeRefreshBus')
        },
        components:{
            vFlowchartfrom,
            packetType_timeline,
            packetType_bar
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

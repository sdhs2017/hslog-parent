<template>
    <div class="content-bg">
        <div class="top-title">广播包/组播包
            <v-flowchartfrom class="from-wapper" refreshBus="mulAndBroRefreshBus_n" :refreshObj="refreshObj" switchBus="mulAndBroSwitchBus_n" timeBus="mulAndBroTimeBus_n"></v-flowchartfrom>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">全局-广播包、组播包</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="24">
                        <div class="chart-wapper ip-chart" v-if="chartType === 'timeline'">
                            <mulAndBro_timeline :params="lineParam" :setIntervalObj="intervalObj"> </mulAndBro_timeline>
                        </div>
                        <div class="chart-wapper ip-chart" v-else>
                            <mulAndBro_bar :params="barParam" :setIntervalObj="intervalObj"> </mulAndBro_bar>
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
    import mulAndBro_bar from '../charts/flow/mulAndBro/mulAndBro_bar'
    import mulAndBro_timeline from '../charts/flow/mulAndBro/mulAndBro_timeline'
    import {dateFormat} from '../../../static/js/common'
    export default {
        name: "mulAndBro_n",
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
            bus.$on('mulAndBroRefreshBus_n',(val)=>{
                this.intervalObj.interval = val*1000
                this.refreshIntTime = this.intervalObj.interval
            })
            /*监听switch改变*/
            bus.$on('mulAndBroSwitchBus_n',(obj)=>{
                this.intervalObj.state = obj.switchVal;
                if(obj.switchVal){
                    this.chartType = 'timeline'
                }
            })
            /*监听日期改变*/
            bus.$on('mulAndBroTimeBus_n',(obj)=>{
                this.chartType = 'bar'
                layer.load(1)
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
            bus.$off('mulAndBroTimeBus_n');
            bus.$off('mulAndBroSwitchBus_n');
            bus.$off('mulAndBroRefreshBus_n');
        },
        components:{
            vFlowchartfrom,
            vEcharts,
            mulAndBro_timeline,
            mulAndBro_bar
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

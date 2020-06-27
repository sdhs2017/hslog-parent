<template>
    <div class="content-bg">
        <div class="top-title">数据包类型
<!--            <v-flowchartfrom class="from-wapper" refreshBus="packetTypeRefreshBus" :refreshObj="refreshObj" switchBus="packetTypeSwitchBus" timeBus="packetTypeTimeBus"></v-flowchartfrom>-->
            <dateLayout class="from-wapper" busName="packetTypeRefreshBus" :defaultVal = "defaultVal"></dateLayout>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">全局-数据包类型个数</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="24">
                        <div class="chart-wapper ip-chart">
                            <packetType_timeline :params="param" :setIntervalObj="intervalObj"> </packetType_timeline>
                        </div>
                        <!--<div class="chart-wapper ip-chart" v-else>
                            <packetType_bar :params="barParam" :setIntervalObj="intervalObj"> </packetType_bar>
                        </div>-->
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
    import dateLayout from '../common/dateLayout'
    import {dateFormat,setChartParam} from '../../../static/js/common'
    export default {
        name: "packetType",
        data() {
            return {
                param:{
                    //timeInterval:5
                    intervalValue:'5',
                    intervalType:'second',
                    starttime:'',
                    endtime:'',
                    last:'5-min'
                },
                intervalObj:{
                    state:true,
                    interval:'5000'
                },
                defaultVal:{
                    //具体时间参数
                    lastVal:'',
                    //起始时间
                    starttime:'',
                    //结束时间
                    endtime:'',
                    //具体时间 类型状态
                    dateBlock:false,
                    //是否存在轮询框
                    isIntervalBox:true,
                    //轮询状态
                    intervalState:true,
                    //轮询数值间隔
                    intervalVal:'5',
                    //轮询参数类型
                    intervalType:'second',
                    //‘快速选择’功能参数类型
                    dateUnit:'min',
                    //‘快速选择’功能参数数值
                    dateCount:'5',
                    //‘常用’ 时间值
                    commonlyVal:'',
                    changeState:true,
                }
            }
        },
        created(){
            /*监听刷新时间改变*/
            bus.$on('packetTypeRefreshBus',(obj)=>{
                //设置参数对应
                let arr = setChartParam(obj);
                this.param = arr[0];
                this.intervalObj = arr[1]
            })
        },
        mounted(){
        },
        methods:{

        },
        beforeDestroy(){
            bus.$off('packetTypeRefreshBus')
        },
        components:{
            dateLayout,
            packetType_timeline,
            packetType_bar
        }
    }
</script>

<style scoped>
    .from-wapper{
        float: right;
        margin-right: 10px;
        margin-top: 10px;
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

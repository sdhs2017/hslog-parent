<template>
    <div class="content-bg">
        <div class="top-title">全局实时流量
<!--            <v-flowchartfrom class="from-wapper" refreshBus="realTimeFlowRefreshBus" :refreshObj="refreshObj"></v-flowchartfrom>-->
            <dateLayout class="from-wapper" busName="realTimeFlowRefreshBus" :defaultVal = "defaultVal"></dateLayout>
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
                            <allUsedPer_timeline :params="param" :setIntervalObj="intervalObj" :otherParams="{bandwidth:bandwidth}"></allUsedPer_timeline>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <allUsedPer_gauge :params="param" :setIntervalObj="intervalObj"  :otherParams="{bandwidth:bandwidth}"></allUsedPer_gauge>
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
    import dateLayout from '../common/dateLayout'
    import {dateFormat,setChartParam} from '../../../static/js/common'
    import allflow_timeline from '../charts/flow/realTime/allflow_timeline'
    import allPacketCount_timeline from '../charts/flow/realTime/allPacketCount_timeline'
    import allUsedPer_gauge from '../charts/flow/realTime/allUsedPer_gauge'
    import allUsedPer_timeline from '../charts/flow/realTime/allUsedPer_timeline'
    import bus from '../common/bus';
    export default {
        name: "realTimeFlow",
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
                bandwidth:100,
                bandwidthArr:[
                    {
                        label:'1M',
                        value:1
                    },
                    {
                        label:'2M',
                        value:2
                    },
                    {
                        label:'5M',
                        value:5
                    },
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
                    },{
                        label:'10000M',
                        value:10000
                    }
                ],
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
            bus.$on('realTimeFlowRefreshBus',(obj)=>{
                //设置参数对应
                let arr = setChartParam(obj);
                this.param = arr[0];
                this.intervalObj = arr[1]
               /* //赋值参数
                this.param={
                    intervalValue:val.intervalVal,
                    intervalType:val.intervalType,
                    starttime:val.starttime,
                    endtime:val.endtime,
                    last:val.lastVal
                }
                this.intervalObj.state = val.intervalState;
                if(val.intervalState){
                    if(val.intervalType === 'second'){
                        this.intervalObj.interval = val.intervalVal * 1000;
                    }else if(val.intervalType === 'minute'){
                        this.intervalObj.interval = val.intervalVal * 1000 * 60;
                    }else{
                        this.intervalObj.interval = val.intervalVal * 1000 * 60 * 60;
                    }
                }*/
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
            dateLayout,
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

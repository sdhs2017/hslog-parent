<template>
    <div class="content-bg">
        <div class="top-title">应用性能分析
            <v-basedate class="from-wapper" type="datetimerange" busName="performanceAnalysis"></v-basedate>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">
                    {{this.chartTitle}}
<!--                    <el-button type="primary" plain v-if="this.chartDataType !== 'all'" class="goBack" @click="goBack" size="mini">返回</el-button>-->
                </p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="24">
                        <div class="chart-wapper ip-chart">
<!--                            <v-echarts echartType="bar" :echartData = "this.appAvgResTimeData" :busName="this.busName"></v-echarts>-->
                            <performanceAnalysis_bar :params="dateArr"></performanceAnalysis_bar>
                        </div>
                    </el-col>
                   <!-- <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="pie" :echartData = "this.ipPacketsData2" ></v-echarts>
                        </div>
                    </el-col>-->
                </el-row>
            </div>
           <!-- <div class="echarts-item">
                <p class="echarts-tit">资产（服务）数据包个数</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="bar" :echartData = "this.domainPacketsData" ></v-echarts>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="pie" :echartData = "this.domainPacketsData2" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>-->
        </div>
    </div>

</template>

<script>
    import vBasedate from '../common/baseDate'
    import vEcharts from '../common/echarts'
    import performanceAnalysis_bar from '../charts/flow/performance/performanceAnalysis_bar'
    import bus from '../common/bus'
    import {dateFormat} from '../../../static/js/common'
    export default {
        name: "performanceAnalysis",
        data() {
            return {
                dateArr:{},
                interTime:'',
                //刷新时间间隔
                refreshIntTime :'5000',
                //数据日期间隔
                dataTime:3600,
                busName:{
                    exportName:'',
                    clickName:'appAvgResTimeClick'
                },
                //图表类型
                chartDataType:'all',
                //图表标题
                chartTitle:'应用平均响应时间统计',
                allParams:{},
                oneParams:{},
                clickxAxis:'',
                //应用平均响应时间统计数据
                appAvgResTimeData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'应用\n名称',
                        yAxisName:'时间/ms',
                        rotate:'15',
                        dispose:true,
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                appAvgResTimeData2:{
                    baseConfig:{
                        title:'',
                        hoverText:'百分比'
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                }
            }
        },
        created(){
            /*监听日期改变*/
            bus.$on('performanceAnalysis',(arr)=>{
                let paramObj = {
                    starttime:arr[0],
                    endtime:arr[1]
                }
                this.dateArr = paramObj;
            })
            bus.$on('titleChange',(val)=>{
                this.chartTitle = val
            })
        },
        mounted(){},
        methods:{
        },
        beforeDestroy(){
            bus.$off('performanceAnalysis')
            bus.$off('titleChange')
        },
        components:{
            vBasedate,
            performanceAnalysis_bar
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
        text-align: center;
        font-weight: 600;
        font-size: 21px;
        color: #5bc0de;
        display: flex;
        justify-content: center;
        align-items: center;
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
    .goBack{
        float: left;
        margin-left: 20px;
        margin-top: 15px;
        color: #15c5e3;
        cursor: pointer;
    }
    .goBack:hover{
        text-decoration: underline;
        color: #fff;
    }
</style>

<template>
    <div class="content-bg">
        <div class="top-title">User-Agent信息
            <v-flowchartfrom class="from-wapper" refreshBus="userAgentInfoRefreshBus" dataBus="userAgentInfoDataBus"></v-flowchartfrom>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">业务访问用户统计-操作系统</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper system-chart">
                            <v-echarts echartType="bar" :echartData = "this.systemChartData" ></v-echarts>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper system-chart">
                            <v-echarts echartType="pie" :echartData = "this.systemChartData2" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>

            <div class="echarts-item">
                <p class="echarts-tit">业务访问用户统计-浏览器</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper browser-chart">
                            <v-echarts echartType="bar" :echartData = "this.browserChartData" ></v-echarts>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper browser-chart">
                            <v-echarts echartType="pie" :echartData = "this.browserChartData2" ></v-echarts>
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
    import {dateFormat} from '../../../static/js/common'
    export default {
        name: "userAgentInfo",
        data() {
            return {
                interTime:'',
                //刷新时间间隔
                refreshIntTime :'5000',
                //数据日期间隔
                dataTime:3600,
                //操作系统 柱状图数据
                systemChartData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'操作系统',
                        yAxisName:'访问次数/次',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]

                },
                //操作系统 饼图数据
                systemChartData2:{
                    baseConfig:{
                        title:'',
                        hoverText:'百分比'
                    },
                    xAxisArr:[],
                    yAxisArr:[]

                },
                //浏览器
                browserChartData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'浏览器',
                        yAxisName:'访问次数/次',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]

                },
                browserChartData2:{
                    baseConfig:{
                        title:'',
                        hoverText:'百分比'
                    },
                    xAxisArr:[],
                    yAxisArr:[]

                },
            }
        },
        created(){
            /*监听刷新时间改变*/
            bus.$on('userAgentInfoRefreshBus',(val)=>{
                    this.refreshIntTime = val;
                    clearInterval(this.interTime);
                    this.interTime = setInterval(()=>{
                        /*获取业务系统统计- 浏览器*/
                        this.getBrowserData(this.dataTime);
                        /*获取业务系统统计- 系统*/
                        this.getSystemData(this.dataTime);
                    },this.refreshIntTime);
            })
            /*监听数据时间改变*/
            bus.$on('userAgentInfoDataBus',(val)=>{
                this.dataTime = val;
            })

        },
        mounted(){
            /*获取业务系统统计- 浏览器*/
            this.getBrowserData(this.dataTime);
            /*获取业务系统统计- 系统*/
            this.getSystemData(this.dataTime);
            /*循环获取数据*/
            this.interTime = setInterval(()=>{
                /*let endtime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
                let st = new Date(new Date(endtime).valueOf() - this.dataTime * 60 * 60 * 1000);
                let starttime = dateFormat('yyyy-mm-dd HH:MM:SS',st);*/
                /*获取业务系统统计- 浏览器*/
                this.getBrowserData(this.dataTime);
                /*获取业务系统统计- 系统*/
                this.getSystemData(this.dataTime);
            },this.refreshIntTime);
        },
        methods:{
            /*获取业务系统统计- 浏览器*/
            getBrowserData(dataTime){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getUserAgentBrowserGroupByTime.do',this.$qs.stringify({
                        timeInterval:dataTime
                    }))
                        .then(res=>{
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            for(let i in res.data[0]){
                                xns1.push(i);
                                yvs1.push(res.data[0][i]);
                                yvs2.push({
                                    name:i,
                                    value:res.data[0][i]
                                })
                            }
                            this.browserChartData.xAxisArr = xns1;
                            this.browserChartData.yAxisArr = yvs1;
                            this.browserChartData2.yAxisArr = yvs2;
                        })
                        .catch(err=>{

                        })
                })
            },
            /*获取业务系统统计- 系统*/
            getSystemData(dataTime){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getUserAgentOSGroupByTime.do',this.$qs.stringify({
                        timeInterval:dataTime
                    }))
                        .then(res=>{
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            for(let i in res.data[0]){
                                xns1.push(i);
                                yvs1.push(res.data[0][i]);
                                yvs2.push({
                                    name:i,
                                    value:res.data[0][i]
                                })
                            }
                            this.systemChartData.xAxisArr = xns1;
                            this.systemChartData.yAxisArr = yvs1;
                            this.systemChartData2.yAxisArr = yvs2;

                        })
                        .catch(err=>{

                        })
                })
            },
        },
        beforeDestroy(){
            clearInterval(this.interTime);
        },
        components:{
            vFlowchartfrom,
            vEcharts
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

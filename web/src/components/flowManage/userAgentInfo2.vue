<template>
    <div class="content-bg">
        <div class="top-title">User-Agent信息
            <v-basedate class="from-wapper" type="datetimerange" busName="userAgentInfoTimeBus2"></v-basedate>
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
    import vBasedate from '../common/baseDate'
    import vEcharts from '../common/echarts'
    import bus from '../common/bus';
    import {dateFormat} from '../../../static/js/common'
    export default {
        name: "userAgentInfo2",
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
            /*监听日期改变*/
            bus.$on('userAgentInfoTimeBus2',(arr)=>{
                let paramObj = {
                    starttime:arr[0],
                    endtime:arr[1]
                }
                layer.load(1);
                /*获取业务系统统计- 浏览器*/
                this.getBrowserData(paramObj);
                /*获取业务系统统计- 系统*/
                this.getSystemData(paramObj);
            })

        },
        mounted(){},
        methods:{
            /*获取业务系统统计- 浏览器*/
            getBrowserData(paramObj){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getUserAgentBrowserGroupByTime.do',this.$qs.stringify(paramObj))
                        .then(res=>{
                            layer.closeAll('loading');
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
                            layer.closeAll('loading');
                        })
                })
            },
            /*获取业务系统统计- 系统*/
            getSystemData(paramObj){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getUserAgentOSGroupByTime.do',this.$qs.stringify(paramObj))
                        .then(res=>{
                            layer.closeAll('loading');
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
                            layer.closeAll('loading');
                        })
                })
            },
        },
        computed:{
        },
        watch:{
        },
        beforeDestroy(){
           // clearInterval(this.interTime);
        },
        components:{
            vBasedate,
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

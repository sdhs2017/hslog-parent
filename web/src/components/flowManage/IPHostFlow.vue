<template>
    <div class="content-bg">
        <div class="top-title">IP主机流量
            <v-flowchartfrom class="from-wapper" refreshBus="IPHostFlowRefreshBus" dataBus="IPHostFlowDataBus"></v-flowchartfrom>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">源IP地址流量排序</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="bar" :echartData = "this.sourceIpData" ></v-echarts>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="pie" :echartData = "this.sourceIpData2" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">目的IP地址流量排序</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="bar" :echartData = "this.targetIpData" ></v-echarts>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="pie" :echartData = "this.targetIpData2" ></v-echarts>
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
        name: "IPHostFlow",
        data() {
            return {
                interTime:'',
                //刷新时间间隔
                refreshIntTime :'10000',
                //数据日期间隔
                dataTime:1,
                //源ip地址流量排行
                sourceIpData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'源IP',
                        yAxisName:'访问次数/次',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                sourceIpData2:{
                    baseConfig:{
                        title:'',
                            hoverText:'百分比'
                    },
                    xAxisArr:[],
                        yAxisArr:[]
                },
                //目的ip地址流量排行
                targetIpData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'目的IP',
                        yAxisName:'访问次数/次',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                targetIpData2:{
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
            bus.$on('IPHostFlowRefreshBus',(val)=>{
                    this.refreshIntTime = val;
                    clearInterval(this.interTime);
                    this.interTime = setInterval(()=>{
                        let endtime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
                        let st = new Date(new Date(endtime).valueOf() - this.dataTime * 60 * 60 * 1000);
                        let starttime = dateFormat('yyyy-mm-dd HH:MM:SS',st);
                        /*获取全局利用率百分比*/
                        this.getSourceIpData(starttime,endtime);
                        /*获取全局数据包个数*/
                        this.getTargetIpData(starttime,endtime);
                    },this.refreshIntTime);
            })
            /*监听数据时间改变*/
            bus.$on('IPHostFlowDataBus',(val)=>{
                this.dataTime = val;
            })

        },
        mounted(){
            //第一次获取数据
            let endtime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
            let st = new Date(new Date(endtime).valueOf() - this.dataTime * 60 * 60 * 1000);
            let starttime = dateFormat('yyyy-mm-dd HH:MM:SS',st);
            /*获取全局利用率百分比*/
            this.getSourceIpData(starttime,endtime);
            /*获取全局数据包个数*/
            this.getTargetIpData(starttime,endtime);
            /*循环获取数据*/
            this.interTime = setInterval(()=>{
                    let endtime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
                    let st = new Date(new Date(endtime).valueOf() - this.dataTime * 60 * 60 * 1000);
                    let starttime = dateFormat('yyyy-mm-dd HH:MM:SS',st);
                    /*获取全局利用率百分比*/
                    this.getSourceIpData(starttime,endtime);
                    /*获取全局数据包个数*/
                    this.getTargetIpData(starttime,endtime);
            },this.refreshIntTime);
        },
        methods:{
            /*获取源IP地址流量排行*/
            getSourceIpData(starttime,endtime){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getSrcIPFlow.do',this.$qs.stringify({
                        starttime:starttime,
                        endtime:endtime
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
                            this.sourceIpData.xAxisArr = xns1;
                            this.sourceIpData.yAxisArr = yvs1;
                            this.sourceIpData2.yAxisArr = yvs2;
                        })
                        .catch(err=>{

                        })
                })
            },
            /*获取目的IP地址流量排行*/
            getTargetIpData(starttime,endtime){
                this.$nextTick(()=>{

                    this.$axios.post(this.$baseUrl+'/flow/getDstIPFlow.do',this.$qs.stringify({
                        starttime:starttime,
                        endtime:endtime
                    }))
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
                            this.targetIpData.xAxisArr = xns1;
                            this.targetIpData.yAxisArr = yvs1;
                            this.targetIpData2.yAxisArr = yvs2;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

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

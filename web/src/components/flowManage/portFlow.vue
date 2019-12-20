<template>
    <div class="content-bg">
        <div class="top-title">端口流量
            <v-flowchartfrom class="from-wapper" refreshBus="portFlowRefreshBus" dataBus="portFlowDataBus"></v-flowchartfrom>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">目的端口总流量</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="bar" :echartData = "this.targetPortFlowData" ></v-echarts>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="pie" :echartData = "this.targetPortFlowData2" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">TCP目的端口总流量</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="bar" :echartData = "this.TCPPortFlowData" ></v-echarts>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="pie" :echartData = "this.TCPPortFlowData2" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">UDP目的端口总流量</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="bar" :echartData = "this.UDPPortFlowData" ></v-echarts>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="pie" :echartData = "this.UDPPortFlowData2" ></v-echarts>
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
        name: "portFlow",
        data() {
            return {
                interTime:'',
                //刷新时间间隔
                refreshIntTime :'10000',
                //数据日期间隔
                dataTime:1,
                //目的端口总流量
                targetPortFlowData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'端口',
                        yAxisName:'数据包/个',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                targetPortFlowData2:{
                    baseConfig:{
                        title:'',
                        hoverText:'百分比'
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                //TCP端口总流量
                TCPPortFlowData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'端口',
                        yAxisName:'数据包/个',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                TCPPortFlowData2:{
                    baseConfig:{
                        title:'',
                        hoverText:'百分比'
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                //TCP端口总流量
                UDPPortFlowData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'端口',
                        yAxisName:'数据包/个',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                UDPPortFlowData2:{
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
            /*监听刷新时间改变*/
            bus.$on('portFlowRefreshBus',(val)=>{
                    this.refreshIntTime = val;
                    clearInterval(this.interTime);
                    this.interTime = setInterval(()=>{
                        let endtime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
                        let st = new Date(new Date(endtime).valueOf() - this.dataTime * 60 * 60 * 1000);
                        let starttime = dateFormat('yyyy-mm-dd HH:MM:SS',st);
                        /*获取目的端口总流量*/
                        this.getTargetPortFlowData(starttime,endtime);
                        /*获取TCP端口总流量*/
                        this.getTCPPortFlowData(starttime,endtime);
                        /*获取UDP端口总流量*/
                        this.getUDPPortFlowData(starttime,endtime);
                    },this.refreshIntTime);
            })
            /*监听数据时间改变*/
            bus.$on('portFlowDataBus',(val)=>{
                this.dataTime = val;
            })

        },
        mounted(){
            //第一次获取数据
            let endtime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
            let st = new Date(new Date(endtime).valueOf() - this.dataTime * 60 * 60 * 1000);
            let starttime = dateFormat('yyyy-mm-dd HH:MM:SS',st);
            /*获取目的端口总流量*/
            this.getTargetPortFlowData(starttime,endtime);
            /*获取TCP端口总流量*/
            this.getTCPPortFlowData(starttime,endtime);
            /*获取UDP端口总流量*/
            this.getUDPPortFlowData(starttime,endtime);
            /*循环获取数据*/
            this.interTime = setInterval(()=>{
                    let endtime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
                    let st = new Date(new Date(endtime).valueOf() - this.dataTime * 60 * 60 * 1000);
                    let starttime = dateFormat('yyyy-mm-dd HH:MM:SS',st);
                    /*获取目的端口总流量*/
                    this.getTargetPortFlowData(starttime,endtime);
                    /*获取TCP端口总流量*/
                    this.getTCPPortFlowData(starttime,endtime);
                    /*获取UDP端口总流量*/
                    this.getUDPPortFlowData(starttime,endtime);
            },this.refreshIntTime);
        },
        methods:{
            /*获取目的端口总流量*/
            getTargetPortFlowData(starttime,endtime){
                this.$nextTick(()=>{

                    this.$axios.post(this.$baseUrl+'/flow/getDstPortCount.do',this.$qs.stringify({
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
                            this.targetPortFlowData.xAxisArr = xns1;
                            this.targetPortFlowData.yAxisArr = yvs1;
                            this.targetPortFlowData2.yAxisArr = yvs2;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取TCP目的端口总流量*/
            getTCPPortFlowData(starttime,endtime){
                this.$nextTick(()=>{

                    this.$axios.post(this.$baseUrl+'/flow/getTCPDstPortCount.do',this.$qs.stringify({
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
                            this.TCPPortFlowData.xAxisArr = xns1;
                            this.TCPPortFlowData.yAxisArr = yvs1;
                            this.TCPPortFlowData2.yAxisArr = yvs2;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取UDP端口总流量*/
            getUDPPortFlowData(starttime,endtime){
                this.$nextTick(()=>{

                    this.$axios.post(this.$baseUrl+'/flow/getUDPDstPortCount.do',this.$qs.stringify({
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
                            this.UDPPortFlowData.xAxisArr = xns1;
                            this.UDPPortFlowData.yAxisArr = yvs1;
                            this.UDPPortFlowData2.yAxisArr = yvs2;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
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

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
                refreshIntTime :'5000',
                //数据日期间隔
                dataTime:3600,
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
                        /*获取目的端口总流量*/
                        this.getTargetPortFlowData(this.dataTime);
                        /*获取TCP端口总流量*/
                        this.getTCPPortFlowData(this.dataTime);
                        /*获取UDP端口总流量*/
                        this.getUDPPortFlowData(this.dataTime);
                    },this.refreshIntTime);
            })
            /*监听数据时间改变*/
            bus.$on('portFlowDataBus',(val)=>{
                this.dataTime = val;
            })

        },
        mounted(){
            //第一次获取数据
            /*获取目的端口总流量*/
            this.getTargetPortFlowData(this.dataTime);
            /*获取TCP端口总流量*/
            this.getTCPPortFlowData(this.dataTime);
            /*获取UDP端口总流量*/
            this.getUDPPortFlowData(this.dataTime);
            /*循环获取数据*/
            this.interTime = setInterval(()=>{
                    /*获取目的端口总流量*/
                    this.getTargetPortFlowData(this.dataTime);
                    /*获取TCP端口总流量*/
                    this.getTCPPortFlowData(this.dataTime);
                    /*获取UDP端口总流量*/
                    this.getUDPPortFlowData(this.dataTime);
            },this.refreshIntTime);
        },
        methods:{
            /*获取目的端口总流量*/
            getTargetPortFlowData(dataTime){
                this.$nextTick(()=>{

                    this.$axios.post(this.$baseUrl+'/flow/getDstPortCount.do',this.$qs.stringify({
                        timeInterval:dataTime
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            /*for(let i in res.data[0]){
                                xns1.push(i);
                                yvs1.push(res.data[0][i]);
                                yvs2.push({
                                    name:i,
                                    value:res.data[0][i]
                                })
                            }*/
                            for(let i in res.data){
                                let obj = res.data[i];
                                for (let j in obj){
                                    xns1.push(j);
                                    yvs1.push(obj[j]);
                                    yvs2.push({
                                        name:j,
                                        value:obj[j]
                                    })
                                }
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
            getTCPPortFlowData(dataTime){
                this.$nextTick(()=>{

                    this.$axios.post(this.$baseUrl+'/flow/getTCPDstPortCount.do',this.$qs.stringify({
                        timeInterval:dataTime
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            /*for(let i in res.data[0]){
                                xns1.push(i);
                                yvs1.push(res.data[0][i]);
                                yvs2.push({
                                    name:i,
                                    value:res.data[0][i]
                                })
                            }*/
                            for(let i in res.data){
                                let obj = res.data[i];
                                for (let j in obj){
                                    xns1.push(j);
                                    yvs1.push(obj[j]);
                                    yvs2.push({
                                        name:j,
                                        value:obj[j]
                                    })
                                }
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
            getUDPPortFlowData(dataTime){
                this.$nextTick(()=>{

                    this.$axios.post(this.$baseUrl+'/flow/getUDPDstPortCount.do',this.$qs.stringify({
                        timeInterval:dataTime
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            /*for(let i in res.data[0]){
                                xns1.push(i);
                                yvs1.push(res.data[0][i]);
                                yvs2.push({
                                    name:i,
                                    value:res.data[0][i]
                                })
                            }*/
                            for(let i in res.data){
                                let obj = res.data[i];
                                for (let j in obj){
                                    xns1.push(j);
                                    yvs1.push(obj[j]);
                                    yvs2.push({
                                        name:j,
                                        value:obj[j]
                                    })
                                }
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

<template>
    <div class="content-bg">
        <div class="top-title">协议流量
            <v-flowchartfrom class="from-wapper" refreshBus="protocolFlowRefreshBus" dataBus="protocolFlowDataBus"></v-flowchartfrom>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">传输层协议长度统计</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="bar" :echartData = "this.TUData" ></v-echarts>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="pie" :echartData = "this.TUData2" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">应用层协议长度统计</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="bar" :echartData = "this.appData" ></v-echarts>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="pie" :echartData = "this.appData2" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">协议长度综合统计</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="bar" :echartData = "this.agreementData" ></v-echarts>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="pie" :echartData = "this.agreementData2" ></v-echarts>
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
        name: "protocolFlow",
        data() {
            return {
                interTime:'',
                //刷新时间间隔
                refreshIntTime :'5000',
                //数据日期间隔
                dataTime:3600,
                //传输层协议长度统计
                TUData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'传输层\n协议',
                        yAxisName:'数据包长度/KB',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                TUData2:{
                    baseConfig:{
                        title:'',
                        hoverText:'百分比'
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                //应用层协议长度统计
                appData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'应用层\n协议',
                        yAxisName:'数据包长度/KB',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                appData2:{
                    baseConfig:{
                        title:'',
                        hoverText:'百分比'
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                //协议长度综合统计
                agreementData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'协议',
                        yAxisName:'数据包长度/KB',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                agreementData2:{
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
            bus.$on('protocolFlowRefreshBus',(val)=>{
                    this.refreshIntTime = val;
                    clearInterval(this.interTime);
                    this.interTime = setInterval(()=>{
                        /*获取传输层协议长度统计*/
                        this.getTUData(this.dataTime);
                        /*获取应用层协议长度统计*/
                        this.getAppData(this.dataTime);
                        /*获取协议长度综合统计*/
                        this.getAgreementData(this.dataTime);
                    },this.refreshIntTime);
            })
            /*监听数据时间改变*/
            bus.$on('protocolFlowDataBus',(val)=>{
                this.dataTime = val;
            })

        },
        mounted(){
            //第一次获取数据
            /*获取传输层协议长度统计*/
            this.getTUData(this.dataTime);
            /*获取应用层协议长度统计*/
            this.getAppData(this.dataTime);
            /*获取协议长度综合统计*/
            this.getAgreementData(this.dataTime);
            /*循环获取数据*/
            this.interTime = setInterval(()=>{
                /*获取传输层协议长度统计*/
                this.getTUData(this.dataTime);
                /*获取应用层协议长度统计*/
                this.getAppData(this.dataTime);
                /*获取协议长度综合统计*/
                this.getAgreementData(this.dataTime);
            },this.refreshIntTime);
        },
        methods:{
            /*获取传输层协议长度统计*/
            getTUData(dataTime){
                this.$nextTick(()=>{

                    this.$axios.post(this.$baseUrl+'/flow/getTransportLength.do',this.$qs.stringify({
                        timeInterval:dataTime
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
                             this.TUData.xAxisArr = xns1;
                             this.TUData.yAxisArr = yvs1;
                            this.TUData2.yAxisArr = yvs2;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取应用层协议长度统计*/
            getAppData(dataTime){
                this.$nextTick(()=>{

                    this.$axios.post(this.$baseUrl+'/flow/getApplicationLength.do',this.$qs.stringify({
                        timeInterval:dataTime
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
                             this.appData.xAxisArr = xns1;
                             this.appData.yAxisArr = yvs1;
                            this.appData2.yAxisArr = yvs2;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取协议长度综合统计*/
            getAgreementData(dataTime){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getMultipleLength.do',this.$qs.stringify({
                        timeInterval:dataTime
                    }))

                        .then(res=>{
                            layer.closeAll('loading');
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            for(let i in res.data){
                                /*yvs2.push({
                                    name:i,
                                    value:res.data[0][i]
                                })*/
                                for(let j in res.data[i]){
                                    xns1.push(j);
                                    yvs1.push(res.data[i][j]);
                                    yvs2.push({
                                        name:j,
                                        value:res.data[i][j]
                                    })
                                }
                            }
                             this.agreementData.xAxisArr = xns1;
                             this.agreementData.yAxisArr = yvs1;
                             this.agreementData2.yAxisArr = yvs2;
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

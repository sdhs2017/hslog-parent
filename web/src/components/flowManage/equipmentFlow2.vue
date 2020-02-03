<template>
    <div class="content-bg">
        <div class="top-title">资产流量
            <v-basedate class="from-wapper" type="datetimerange" busName="equipmentFlowTimeBus2"></v-basedate>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">资产（IP）数据包个数</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="bar" :echartData = "this.ipPacketsData" ></v-echarts>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="pie" :echartData = "this.ipPacketsData2" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
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
        name: "equipmentFlow2",
        data() {
            return {
                interTime:'',
                //刷新时间间隔
                refreshIntTime :'5000',
                //数据日期间隔
                dataTime:3600,
                //资产（ip）数据包个数
                ipPacketsData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'资产(IP)',
                        yAxisName:'数据包/个',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                ipPacketsData2:{
                    baseConfig:{
                        title:'',
                        hoverText:'百分比'
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                //资产（域名）数据包个数
                domainPacketsData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'资产(服务)',
                        yAxisName:'数据包/个',
                        rotate:'10',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                domainPacketsData2:{
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
            bus.$on('equipmentFlowTimeBus2',(arr)=>{
                let paramObj = {
                    starttime:arr[0],
                    endtime:arr[1]
                }
                layer.load(1);
                /*获取资产（ip）数据包个数*/
                this.getIpPacketsData(paramObj);
                /*获取资产（域名）数据包个数*/
                this.getDomainPacketsData(paramObj);
            })
        },
        mounted(){},
        methods:{
            /*获取资产（ip）数据包个数*/
            getIpPacketsData(paramObj){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getDstIPPacketCount.do',this.$qs.stringify(paramObj))
                        .then(res=>{
                            layer.closeAll('loading');
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            for(let i in res.data){
                                let obj = res.data[i];
                                for (let j in obj){
                                    xns1.push(j);
                                    yvs1.push(obj[j])
                                    yvs2.push({
                                        name:j,
                                        value:obj[j]
                                    })
                                }
                            }
                            this.ipPacketsData.xAxisArr = xns1;
                            this.ipPacketsData.yAxisArr = yvs1;
                            this.ipPacketsData2.yAxisArr = yvs2;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取资产（域名）数据包个数*/
            getDomainPacketsData(paramObj){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getDstUrlPacketCount.do',this.$qs.stringify(paramObj))
                        .then(res=>{
                            layer.closeAll('loading');
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
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
                            this.domainPacketsData.xAxisArr = xns1;
                            this.domainPacketsData.yAxisArr = yvs1;
                            this.domainPacketsData2.yAxisArr = yvs2;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
        },
        beforeDestroy(){
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
